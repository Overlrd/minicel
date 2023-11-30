import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class Table {
    String[][] data;
    Map<String, Integer> opPrecedence;
    String outputBuffer;

    public Table(String[][] data, Map<String, Integer> opPrecedence) {
        this.data = data;
        this.opPrecedence = opPrecedence;
    }

    public void evaluate() {
        StringBuilder outputBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                String cell = data[i][j].trim();
                if (Helper.isExpression(cell)) {
                    Queue<String> infixEpression = infix(cell, opPrecedence);
                    double value = postFixEvaluator(infixEpression);
                    System.out.println("Evaluating " + cell + " -> " + value);
                    outputBuilder.append(value);
                } else {
                    outputBuilder.append(cell);
                }
                outputBuilder.append(",");
            }
            outputBuilder.append("\n");
        }
        outputBuffer = outputBuilder.toString();
    }

    public void output(String outputFilepath) {
        if (!outputBuffer.isEmpty()) {
            File outputFile = Helper.createFile("output.csv");
            Helper.writeFile(outputFile, outputBuffer);
        } else {
            System.err.println("Trying to write empty buffer to" + outputFilepath);
            System.exit(1);
        }
    }

    public String cellCoordToValue(String coord) {
        // TODO - Add coord lenght checking
        String columnString = coord.substring(0, 1);
        int row = 0;
        String[] tableColumns = data[0];
        String value;

        if (Helper.arrayContainsString(tableColumns, columnString)) {
            row = Integer.parseInt(Helper.getNextNumberString(coord.substring(1)));
        } else {
            return null;
        }

        int column = columnString.codePointAt(0) - 65;
        try {
            value = data[row][column];
        } catch (Exception e) {
            return null;
        }
        return value;
    }

    public Queue<String> infix(String cell, Map<String, Integer> opPrecedence) {
        System.out.println("Evaluating cell " + cell);
        Queue<String> outQueue = new LinkedList<>();
        Stack<String> opStack = new Stack<>();

        for (int cursor = 0; cursor < cell.length(); cursor++) {
            String token = cell.substring(cursor, cursor + 1);

            if (Helper.isLeftParenthesis(token)) {
                opStack.push(token);
                continue;
            } else if (Helper.isRightParenthesis(token)) {
                while ((!opStack.empty() && !Helper.isLeftParenthesis(opStack.peek()))) {
                    outQueue.add(opStack.pop());
                }
                continue;
            } else if (Helper.isOperator(token)) {
                if ((!opStack.empty() && Helper.isOperator(opStack.peek()))) {
                    int current_precedence = opPrecedence.get(token);
                    int prev_precedence = opPrecedence.get(opStack.peek());
                    if (current_precedence < prev_precedence) {
                        outQueue.add(opStack.pop());
                    }
                } else {
                    opStack.add(token);
                }
                continue;
            } else if (Helper.isDigit(token)) {
                outQueue.add(token);
            } else if (token.matches("[A-Z]")) {
                String cellCoord = Helper.isCell(cell.substring(cursor));
                if (cellCoord != null) {
                    String value = cellCoordToValue((cellCoord));
                    if (Helper.isExpression(value)) {
                        // TODO - add a way to determine if cell has already being evaluated and get value
                        // if not evaluate it and store cell-value
                        String subExprValue = String.valueOf(postFixEvaluator(infix(value, opPrecedence)));
                        outQueue.add(subExprValue);
                    } else {
                        outQueue.add(value);
                    }
                }
                cursor += cellCoord.length();
                continue;
            }
        }
        while (!opStack.isEmpty()) {
            outQueue.add(opStack.pop());
        }

        return outQueue;
    }

    public double postFixEvaluator(Queue<String> outQueue) {
        for (String term : outQueue) {
            System.out.print(term);
        }
        System.out.println("\n");
        Stack<String> evalStack = new Stack<>();
        int outValue = 0;

        for (String term : outQueue) {
            if (Helper.isDigit(term)) {
                evalStack.push(term);
            } else if (Helper.isOperator(term)) {
                System.out.println(term);
                String right = evalStack.pop();
                String left = evalStack.pop();
                String value = String.valueOf(Helper.doCalc(left, term, right));
                evalStack.add(value);
            }
        }
        return Double.parseDouble(evalStack.pop());
    }
}
