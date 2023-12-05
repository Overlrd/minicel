import java.util.HashMap;
import java.util.Map;

public class Minicell {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: java Minicell <input_file> <output_file>");
            System.exit(1);
        }

        String inputFilePath = args[0];
        String outputFilePath = args[1];

        Helper helper = new Helper();

        String delimiter = "\\,";
        String fallbackValue = "0";

        String[][] tableData = helper.csv_to_2d_array(helper.read_file(inputFilePath), delimiter, fallbackValue);

        Map<String, Integer> operatorPrecedence = new HashMap<>();
        operatorPrecedence.put("+", 1);
        operatorPrecedence.put("-", 1);
        operatorPrecedence.put("*", 2);
        operatorPrecedence.put("/", 2);

        Table table = new Table(tableData, operatorPrecedence);
        table.evaluate();
        table.output(outputFilePath);
    }
}

