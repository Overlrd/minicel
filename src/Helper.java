import java.nio.file.Files;
import java.nio.file.Paths;

public class Helper {
    private static final String IN_FILE_COL_DELIMITER = "|";
    public final String EMPTY_CELL_VALUE = "null_v";
    public final String EMPTY_CELL_TYPE = "null_t";

    /*
     * Read file and return String
     */
    public String read_csv_file(String filePath) {
        String fileContent = "";
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            System.err.println("Can't read " + filePath + " make sure file exists");
            System.exit(1);
        }
        return fileContent;
    }

    /*
     * String to 2D array
     */
    public String[][] string_to_2d_array(String fileContent) {
        String[] rows = fileContent.split("\n");
        int numRows = rows.length;
        String[] header = rows[0].split("\\" + IN_FILE_COL_DELIMITER);
        int numCols = header.length;

        String[][] data = new String[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            String[] cells = rows[i].split("\\" + IN_FILE_COL_DELIMITER);
            for (int j = 0; j < numCols; j++) {
                if (j < cells.length) {
                    data[i][j] = cells[j].trim();
                } else {
                    data[i][j] = EMPTY_CELL_VALUE;
                }
            }
        }
        return data;
    }
}
