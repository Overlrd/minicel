import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Helper {
    public static File createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new Error("Can't create file " + filePath);
        }
    }

    public static void writeFile(File file, String Buffer) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(Buffer);
            writer.close();
        } catch (IOException e) {
            throw new Error("Can't write file " + file.getName());
        }
    }

    public static boolean isDigit(String text) {
        return text.matches("^\\d+(\\.\\d+)?$");
    }

    public static boolean isExpression(String text) {
        return text.startsWith("=");
    }

    public static boolean isLeftParenthesis(String text) {
        return text.equals("(");
    }

    public static boolean isRightParenthesis(String text) {
        return text.equals(")");
    }

    public static boolean isOperator(String text) {
        return text.matches("[+\\-*/]");
    }

    public static String isCell(String cellString) {
        if (!cellString.substring(0, 1).matches("^[A-Z]$")) {
            return null;
        }
        StringBuilder cellBuilder = new StringBuilder();
        cellBuilder.append(cellString.charAt(0));
        int cursor = 1;
        while (!cellString.isEmpty()) {
            String token = String.valueOf(cellString.charAt(cursor));
            if (isDigit(token)) {
                cellBuilder.append(token);
            } else {
                break;
            }
            cursor++;
        }
        if (cellBuilder.toString().matches("^[A-Z]+[0-9]+$")) {
            return cellBuilder.toString();
        }
        return null;
    }

    public static boolean arrayContainsString(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static String getNextNumberString(String s) {
        StringBuilder numBuilder = new StringBuilder();
        if (!s.isEmpty()) {
            for (int i = 0; i < s.length(); i++) {
                String c = s.substring(i, i + 1);
                if (isDigit(c)) {
                    numBuilder.append(c);
                }
            }
        }
        String numBuild = numBuilder.toString();
        if (!numBuild.isEmpty()) {
            return numBuild;
        }
        return null;
    }

    public static void Assert(boolean romeo, boolean juliette, String log) {
        if (!(romeo && juliette)) {
            throw new Error(log);
        }
    }

    public static double doCalc(String left, String operator, String right) {
        double leftDouble = Double.parseDouble(left);
        double rightDouble = Double.parseDouble(right);
        double value = 0;
        switch (operator) {
            case "+":
                value = leftDouble + rightDouble;
                break;
            case "-":
                value = leftDouble - rightDouble;
                break;
            case "*":
                value = leftDouble * rightDouble;
                break;
            case "/":
                value = leftDouble / rightDouble;
                break;
        }
        return value;
    }

    public String read_file(String filePath) {
        String fileContent = "";
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println(filePath + " not found. Make sure file exists.");
            System.exit(1);
        }
        return fileContent;
    }

    public String[][] csv_to_2d_array(String fileContent, String delimiter, String fallbackValue) {
        String[] rows = fileContent.split("\n");
        int lenHeader = rows[0].split(delimiter).length;

        String[][] data = new String[rows.length][lenHeader];

        for (int i = 0; i < rows.length; i++) {
            String[] cells = rows[i].split(delimiter);
            for (int j = 0; j < lenHeader; j++) {
                if (j < cells.length) {
                    data[i][j] = cells[j].trim();
                } else {
                    data[i][j] = fallbackValue;
                }
            }
        }
        return data;
    }
}
