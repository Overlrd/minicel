import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


enum CellKind {
    CELL_KIND_TEXT,
    CELL_KIND_NUMBER,
    CELL_KIND_EXPRESSION
}

enum ExpressionKind {
    EXPR_KIND_NUMBER,
    EXPR_KIND_CELL,
    EXPR_KIND_PLUS
}

class Expression {
    ExpressionKind kind = ExpressionKind.EXPR_KIND_NUMBER;
}

interface CellValue {
    Object getValue();
}

class Cell implements CellValue{
   CellKind kind = CellKind.CELL_KIND_TEXT;
   Object value;

   public Cell(String value){
    this.value = value;
   }

    @Override
    public Object getValue() {
        return value;
    }
}

class Table {
    Cell[][] cells;
    int rows;
    int columns;

    public Table(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[this.rows][this.columns];
    }

    public void parseTable(String fileContent) {
        String[] lines = fileContent.split("\n");
        for (int i = 0; i < this.rows; i++) {
            String[] cells = lines[i].split("\\|");
            for (int j = 0; j < this.columns; j++) {
                if (j < cells.length) {
                    this.cells[i][j] = new Cell(cells[j].trim());
                    Cell c = this.cells[i][j];

                    // if starts with a "=" value is considered an expression
                    if (((String) c.value).startsWith("=")){
                        c.kind = CellKind.CELL_KIND_EXPRESSION;
                    } else {
                        // if can parse double the value, it's definitely a numeric 
                        try {
                            double numValue = Double.parseDouble(c.value.toString());
                            c.kind = CellKind.CELL_KIND_NUMBER;
                        // if not it's text
                        } catch (NumberFormatException e) {
                            c.kind = CellKind.CELL_KIND_TEXT;
                        }
                    }
                } else {
                    this.cells[i][j] = new Cell("empty");
                }
            }
        }
        // print the table back
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                Cell c = this.cellAt(i, j);
                switch (c.kind) {
                    case CELL_KIND_TEXT:
                        System.out.print("TEXT(" + c.value.toString() + ")|");
                        break;
                    case CELL_KIND_NUMBER:
                        System.out.print("NUMBER(" + Double.parseDouble(c.value.toString())+ ")|");
                        break;
                    case CELL_KIND_EXPRESSION:
                        System.out.print("EXPR(" + c.value.toString()+ ")|");
                        break;
                }
            }
            System.out.println("");
        }
    }

    public Cell cellAt(int row, int col) {
        assert(row < this.rows);
        assert(col < this.columns);
        return this.cells[row][col];
    }
}

public class Minicell {
    public static void main(String[] args) throws IOException {
        // assert an input file is passed
        if (args.length < 1) {
            System.err.println("Usage java Minicell <input.csv>");
            System.exit(1);
        }
        // try to read the file if can't exit the prgram
        String inputFile = args[0];
        String fileContent = "";
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(inputFile)));            
        } catch (Exception e) {
            System.err.println("Can't read " + inputFile + " make sure file exists");
            System.exit(1);
        }

        int[] dims = estimateTableDimensions(fileContent);
        System.out.println(String.format("table dims: (%dx%d)", dims[0], dims[1]));
        Table table = new Table(dims[0], dims[1]);
        table.parseTable(fileContent);
    }

    public static int[] estimateTableDimensions(String fileContent) {
        String[] lines = fileContent.split("\n");
        int rows = lines.length;
        int columns;
        if (rows > 0) {
            // numCols is the number of columns in the header of the file
            int numCols = lines[0].split("\\|").length;
            columns = numCols;
        } else {
            columns = 0;
        }
        int[] dims = {rows, columns};

        return dims;
    }
}
