import java.util.*;

enum ExprTermTypes {
    ADD_OPERATOR,
    SUB_OPERATOR,
    MUL_OPERATOR,
    DIV_OPERATOR,
    LEFT_PARENTHESES,
    RIGHT_PARENTHESES,
    CELL,
    NUMBER,
    NULL // i needed a default one for the ExprOperator so it can override it
         // i think this is a good idea. TODO - FIND A BETTER IMPLEMENTATION
}

class ExprTerm {
    protected ExprTermTypes type;
    protected String value;
    protected int precedence;

    public ExprTerm(String value, ExprTermTypes type) {
        this.value = value;
        this.type = type;
    }
}

class ExprOperator extends ExprTerm {
    public ExprOperator(String value, ExprTermTypes type) {
        super(value, type);
        setPrecedenceAndType();
    }

    private void setPrecedenceAndType() {
        if (value.equals("+")) {
            this.precedence = 1;
            this.type = ExprTermTypes.ADD_OPERATOR;
        } else if (value.equals("-")) {
            this.precedence = 1;
            this.type = ExprTermTypes.SUB_OPERATOR;
        } else if (value.equals("*")) {
            this.precedence = 2;
            this.type = ExprTermTypes.MUL_OPERATOR;
        } else if (value.equals("/")) {
            this.precedence = 2;
            this.type = ExprTermTypes.DIV_OPERATOR;
        } else if (value.equals("(")) {
            this.precedence = 0;
            this.type = ExprTermTypes.LEFT_PARENTHESES;
        } else if (value.equals(")")) {
            this.precedence = 0;
            this.type = ExprTermTypes.RIGHT_PARENTHESES;
        }
    }
}

class ExpressionEvaluator {
    // outputQueue fro the shuting yard algorythm
    Queue<ExprTerm> outputQueue = new LinkedList<ExprTerm>();
    // operator stack for the shuting yard algorythm
    Stack<ExprTerm> operatorStack = new Stack<ExprTerm>();
    // Table
    Table table;

    public ExpressionEvaluator(Table table) {
        this.table = table;
    }

    public Queue<ExprTerm> parse(String expression) {
        // will be used when extracting a Cell name
        String tmpCellName = "";

        // iterate on all the charachters in the expression and
        // enque or stack them
        for (int cursor = 0; cursor < expression.length(); cursor++) {
            String c = String.valueOf(expression.charAt(cursor));

            if (c.equals("(")) {
                ExprTerm leftParentheses = new ExprTerm(c, ExprTermTypes.LEFT_PARENTHESES);
                operatorStack.push(leftParentheses);
                continue;

            } else if (c.equals(")")) {
                while (!operatorStack.isEmpty()) {
                    ExprTerm lastTerm = operatorStack.pop();
                    if (lastTerm.type == ExprTermTypes.LEFT_PARENTHESES) {
                        // We found the matching left parenthesis, so we don't add it to the output
                        break;
                    }
                    outputQueue.add(lastTerm);
                }
                continue;
            } else if (c.matches("^[A-Z]+$")) {
                // Cell ex 'A12', '12'(the integer after the column name representing the row)
                tmpCellName = tmpCellName.concat(c);
                // try to parse a number following this letter
                String cellRow = numberUntilSomethingElse(expression, cursor + 1);
                if (Integer.parseInt(cellRow) != -1) {
                    tmpCellName = tmpCellName.concat(String.valueOf(cellRow));
                    ExprTerm Cell = new ExprTerm(tmpCellName, ExprTermTypes.CELL);
                    String val = this.table.getCellbyId(Cell.value).getContent();
                    System.out.println("Cell:" + tmpCellName + " has value " + val);
                    Cell.value = val;
                    outputQueue.add(Cell);
                    tmpCellName = "";
                }
                cursor = cursor + tmpCellName.length();
                continue;
            } else if (c.matches("^\\d+$")) {
                ExprTerm number = new ExprTerm(c, ExprTermTypes.NUMBER);
                outputQueue.add(number);
                continue;
            } else if (c.matches("[+\\-*/]")) {
                ExprOperator operator = new ExprOperator(c, ExprTermTypes.NULL);

                // Check if the operator stack is not empty before accessing its elements
                if (!operatorStack.isEmpty()) {
                    ExprTerm lastOperator = operatorStack.peek();
                    System.out.println("Current Op: " + operator.value);
                    System.out.println("Last Op: " + lastOperator.value);
                    if (operator.precedence < lastOperator.precedence) {
                        ExprTerm lastOp = operatorStack.pop();
                        outputQueue.add(lastOp);
                    }
                }
                operatorStack.push(operator);
                continue;
            }
        }

        while (!operatorStack.isEmpty()) {
            outputQueue.add(operatorStack.pop());
        }

        System.out.print("outputQueue: ");
        for (ExprTerm el : outputQueue) {
            System.out.print(el.value + " ");
        }
        System.out.println("");

        return outputQueue;
    }

    public double evaluate(Queue<ExprTerm> parsedQueue) {
        Stack<ExprTerm> evalStack = new Stack<ExprTerm>();
        for (ExprTerm Term : parsedQueue) {
            if (Term.type == ExprTermTypes.NUMBER) {
                evalStack.push(Term);
            } else if (Term.type == ExprTermTypes.CELL) {
                System.out.println("Value of Cell:" + Term.value);
                // Term.value = String.valueOf(this.table.getCellbyId(Term.value).getValue());
                evalStack.push(Term);
            } else if (Term instanceof ExprOperator) {
                ExprTerm right = evalStack.pop();
                ExprTerm left = evalStack.pop();
                System.out.println("left: " + left.value);
                System.out.println("right: " + right.value);
                double value = doCalc(left.value, right.value, Term.value);
                ExprTerm val = new ExprTerm(String.valueOf(value), ExprTermTypes.NUMBER);
                evalStack.push(val);
            }
        }
        double result = Double.parseDouble(evalStack.pop().value);
        System.out.println("Result: " + result);
        return result;
    }

    public String leftUntilOperator(String expression) {
        String left = expression.split("[+\\-*/]")[0];
        return left;
    }

    public String numberUntilSomethingElse(String expression, int startIndex) {
        String tmp = "";
        expression = expression.substring(startIndex);

        for (int i = 0; i < expression.length(); i++) {
            String c = String.valueOf(expression.charAt(i));
            if (c.matches("^\\d+$")) {
                tmp = tmp.concat(c); // Corrected to assign the result back to tmp
            } else {
                break; // Break the loop if a non-digit character is encountered
            }
        }

        // TODO - Find a better way to handle this
        if (!tmp.isEmpty() && tmp.matches("^\\d+$")) {
            return tmp;
        } else {
            return "-1";
        }
    }

    public double doCalc(String left, String right, String operator) {
        double value = 0;
        double numLeft = Double.parseDouble(left);
        double numRight = Double.parseDouble(right);
        if (operator.equals("+")) {
            value = numLeft + numRight;
        } else if (operator.equals("-")) {
            value = numLeft - numRight;
        } else if (operator.equals("*")) {
            value = numLeft * numRight;
        } else if (operator.equals("/")) {
            value = numLeft / numRight;
        }
        return value;
    }

}
