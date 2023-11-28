import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    private static final String IN_FILE_COL_DELIMITER = "|";
    public final String EMPTY_CELL_VALUE = "null_v";


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

    public static <T> int arrayContainsValue(final T[] array, final T v) {
        if (v == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null)
                    return i;
            }
        } else {
            for (int i = 0; i < array.length; i++)
                if (array[i].equals(v))
                    return i;
        }

        return -1;
    }

    public static boolean isDigit(String text) {
        return text.matches("^\\d+$");
    }

    public static boolean isExpression(String text) {
        return text.startsWith("=");
    }
    public static boolean isLeftParenthesis(String text) { return text.equals("("); }
    public static boolean isRightParenthesis(String text) { return text.equals(")"); }

    // return null if startIndex is not the first char of a cell coordinate else , returns the cell coordinate
    public static String isCellId(String text, int cursor) {
        String stringPart = String.valueOf(text.charAt(cursor));
        cursor ++; // update the cursor to point to the next token
        String numericPart = "";
        while (text.substring(cursor, cursor+1).matches("^\\d$")) {
            numericPart = numericPart.concat(text.substring(cursor, cursor+1));
            if (cursor <= text.length()) break;
            cursor += 1;
        }
        if (numericPart.isEmpty()) {
            return  null;
        }
        return stringPart.concat(numericPart);
    }
    public static boolean isOperator(String text) { return text.matches("[+\\-*/]"); }

    public static int doCalc(int left, String operator, int right) {
        int value = 0;
        switch (operator){
            case "+":
                value = left + right;
                break;
            case "-":
                value = left - right;
                break;
            case "*":
                value = left * right;
                break;
            case "/":
                System.out.println("dividing " + left + " by " + right);
                value = left / right;
                System.out.println("result: " + value);
                break;
        }
        return value;
    }
}
