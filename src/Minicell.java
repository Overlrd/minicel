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
    EXPR_KIND_OP
}

class Expression {
    ExpressionKind kind = ExpressionKind.EXPR_KIND_NUMBER;
}

interface CellValue {
    String getString();
    double getNumber();
    Expression getExpression();
}

class Cell implements CellValue{
   CellKind kind = CellKind.CELL_KIND_TEXT;
   String value = "";

   public Cell(String value){
    this.value = value;
   }

   @Override
   public String getString() {
       // TODO Auto-generated method stub
       return null;
   }

   @Override
   public double getNumber() {
       // TODO Auto-generated method stub
       return 0;
   }

   @Override
   public Expression getExpression() {
       // TODO Auto-generated method stub
       return null;
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
                Cell c = new Cell(cells[j].trim());
                this.cells[i][j] = c;
            }
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
        System.out.println(String.format("the dims of the tables: (%dx%d)", dims[0], dims[1]));
        Table table = new Table(dims[0], dims[1]);
        table.parseTable(fileContent);
        for (int i = 0; i < dims[0]; i++) {
            for (int j = 0; j < dims[1]; j++) {
                System.out.println(table.cellAt(i, j).value);
            }
        }

    }

    public static int[] estimateTableDimensions(String fileContent) {
        String[] lines = fileContent.split("\n");
        int rows = lines.length;
        int columns;
        if (rows > 0) {
            int numCols = lines[0].split("\\|").length;
            columns = numCols;
        } else {
            columns = 0;
        }
        int[] dims = {rows, columns};

        return dims;
    }
}
