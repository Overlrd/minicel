import java.util.ArrayList;
import java.util.Arrays;

/*
 * Table class
 * Instance variables:
 *  - Cells -> the 2D array passed to instanciate the Table
 *  - evaluatedData -> the 2D array representing the evaluated data
 *  - numRows
 *  - numCols
 * Methods
 */
class Table {

    protected final Cell[] Cells;
    protected final int numRows;
    protected final int numCols;
    protected final ArrayList<String> headers;

    public Table(String[][] data) {
        this.headers = new ArrayList<>(Arrays.asList(data[0]));
        this.numRows = data.length;
        this.numCols = data[0].length;
        this.Cells = new Cell[this.numRows * this.numCols];

        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                this.Cells[i * this.numCols + j] = new Cell(data[i][j]);
            }
        }
    }

    public Cell getItem(int row, int col) {
        return this.Cells[row * this.numCols + col];
    }

    // Coords -> Col-Row of the cell ex: A12, B2, C5

    public Cell getCellbyId(String Coords) {
        Cell c = null;

        // Extract column index
        String colIndex = String.valueOf(Coords.charAt(0));
        System.out.println("Coords are: " + Coords);
        int col = headers.indexOf(colIndex);

        // Check if the column index is valid
        if (col == -1 || col >= this.numCols) {
            throw new java.lang.Error("Column " + Coords.charAt(0) + " not in the Table");
        }

        // Extract and validate row value
        String strRow = Coords.substring(1);
        if (!strRow.matches("\\d+")) {
            throw new java.lang.Error("Invalid row value in Coords " + Coords);
        }

        int row = Integer.parseInt(strRow);

        // Check if the row index is valid
        if (row < 0 || row >= this.numRows) {
            throw new java.lang.Error("Row " + row + " out of index for the Table");
        }

        // Calculate cell index and retrieve the cell
        c = this.Cells[row * this.numCols + col];

        return c;
    }
}

/*
 * Cell class
 * Instance variables:
 * - type -> the type of the Cell <text, number, formula>
 * - content -> the raw content of the cell
 * - value -> the value of the cell (if it's a formula)
 * Methods
 * - getType() -> returns the type of the cell
 * - getContent() -> returns the raw content of the cell
 * - getValue() -> returns the value of the cell (if it's a formula)
 * - setValue(double)
 * - setType() -> private method so the cell can evaluate it content an assign a
 * type to itself
 */
class Cell {
    Helper helper = new Helper();
    private String type;
    private final String content;
    private double value;

    public Cell(String content) {
        this.content = content;
        this.setType();
    }

    private void setType() {
        if (this.content.startsWith("=")) {
            this.type = "formula";
        } else if (this.content.matches("-?\\d+")) {
            this.type = "number";
        } else if (this.content == helper.EMPTY_CELL_VALUE) {
            this.type = helper.EMPTY_CELL_TYPE;
        } else {
            this.type = "text";
        }
    }

    public String getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public double getValue() {
        return Integer.parseInt(this.content);
    }

    public void setValue(double value) {
        this.value = value;
    }
}
