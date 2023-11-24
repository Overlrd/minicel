/*
 * Table class
 * Instance variables:
 *  - rawData -> the 2D array passed to instanciate the Table
 *  - evaluatedData -> the 2D array representing the evaluated data
 *  - numRows
 *  - numCols
 * Methods
 */

public class Table {

    private final Cell[] rawData;
    private Cell[] evaluatedData;
    private final int numRows;
    private final int numCols;

    public Table(String[][] data) {
        this.numRows = data.length;
        this.numCols = data[0].length;
        this.rawData = new Cell[this.numRows * this.numCols];

        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                this.rawData[i * this.numCols + j] = new Cell(data[i][j]);
            }
        }
    }
}

