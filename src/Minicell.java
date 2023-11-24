import java.util.Arrays;

public class Minicell {
    public static void main(String[] args) {

        if (args.length!= 1) {
            System.err.println("Usage: java Minicell <path_to_file>");
            System.exit(1);
        }

        Helper helper = new Helper();
        String fileContent = helper.read_csv_file(args[0]);
        String[][] data = helper.string_to_2d_array(fileContent);

        for (String[] row: data){
            System.out.println(Arrays.toString(row));
        }

        Table table = new Table(data);
    }
}
