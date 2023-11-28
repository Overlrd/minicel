import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.*;

enum CellType {
    NUMBER, TEXT, EXPRESSION
}

enum ExpressionType {
    ADD_OPERATOR,
    SUB_OPERATOR,
    DIV_OPERATOR,
    MUL_OPERATOR,
    LEFT_PARENTHESES,
    RIGHT_PARENTHESES,
    NUMBER,
    NULL
}

class ExprTerm {
    protected ExpressionType type;
    protected String value;
    protected int precedence;

    public ExprTerm(String value, ExpressionType type) {
        this.value = value;
        this.type = type;
    }

    public int getNumericValue() {
        if (this.type != ExpressionType.NUMBER) {
            throw new Error("Value " + this.value + " is not a Numeric value.");
        }
        return Integer.parseInt(this.value);
    }
}

class ExprOperator extends ExprTerm {

    public ExprOperator(String value, ExpressionType type, int precedence) {
        super(value, type);
    }
}

class Table {

    protected final int numRows;
    protected final int numCols;
    protected final String[] headers;
    private final String[][] data;
    protected Cell[] Cells;
    Map<String, Integer> operatorPrecedenceMap = new HashMap<>();


    public Table(String[][] data, Map<String, Integer> operatorPrecedenceMapMap) {
        this.data = data;
        this.headers = data[0];
        this.numRows = data.length;
        this.numCols = data[0].length;
        this.operatorPrecedenceMap = operatorPrecedenceMapMap;
    }

    public void build() {
        this.Cells = new Cell[this.numRows * this.numCols];
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                String content = data[i][j].trim();
                CellType type = getCellType(content);
                this.Cells[i * this.numCols + j] = new Cell(content, type);
            }
        }
    }

    public void evaluate() {
        for (Cell cell: this.Cells) {
            if (cell.type == CellType.EXPRESSION) {
                int value = evaluateParsedExpression(parseExpression(cell.content));
                System.out.println(cell.content + " -> " + value);
            }
        }
    }
    public int getOperatorPrecedence(String operator) {
        return this.operatorPrecedenceMap.get(operator);
    }

    private CellType getCellType(String content) {
        CellType cellType;
        if (Helper.isDigit(content)) {
            cellType = CellType.NUMBER;
        } else if (Helper.isExpression(content)) {
            cellType = CellType.EXPRESSION;
        } else {
            cellType = CellType.TEXT;
        }
        return cellType;
    }

    // TODO - Correct this implementation (use a Map maybe)
    public ExpressionType getOperatorType(String operator) {
        ExpressionType type;
        switch (operator) {
            case "+":
                type = ExpressionType.ADD_OPERATOR;
                break;
            case "-":
                type = ExpressionType.SUB_OPERATOR;
                break;
            case "*":
                type = ExpressionType.MUL_OPERATOR;
                break;
            case "/":
                type = ExpressionType.DIV_OPERATOR;
                break;
            default:
                type = ExpressionType.NULL;
                break;
        };
        return type;
    }

    public Cell getCell(int row, int col) {
        return this.Cells[row * this.numCols + col];
    }

    public Cell getCellById(String Coords) {
        Cell cell;
        String column = String.valueOf(Coords.charAt(0));
        int intColumn = Helper.arrayContainsValue(headers, column);

        if (intColumn == -1) {
            throw new java.lang.Error("Column " + Coords.charAt(0) + " not in the Table");
        }

        String row = Coords.substring(1);
        if (!row.matches("\\d+")) {
            throw new java.lang.Error("Invalid row value in Coords " + Coords);
        }

        int intRow = Integer.parseInt(row);

        if (intRow < 0 || intRow >= this.numRows) {
            throw new java.lang.Error("Row " + row + " out of index for the Table");
        }

        cell = this.Cells[intRow * this.numCols + intColumn];
        return cell;
    }

    // Parses a Math expression using the Shunting yard algorithm
    // https://en.wikipedia.org/wiki/Shunting_yard_algorithm#w
    private Queue<ExprTerm> parseExpression(String CellContent) {

        Queue<ExprTerm> outputQueue = new LinkedList<>();
        Stack<ExprTerm> operatorStack = new Stack<>();
        for (int cursor = 0; cursor < CellContent.length(); cursor++) {
            String token = String.valueOf(CellContent.charAt(cursor));

            if (Helper.isLeftParenthesis(token)) {
                operatorStack.push(new ExprTerm(token, ExpressionType.LEFT_PARENTHESES));
                continue;
            } else if (Helper.isRightParenthesis(token)) {
                while (!operatorStack.isEmpty()) {
                    ExprTerm previousTerm = operatorStack.pop();
                    if (previousTerm.type == ExpressionType.LEFT_PARENTHESES) {
                        break;
                    }
                    outputQueue.add(previousTerm);
                }
                continue;
            } else if (Helper.isDigit(token)) {
                outputQueue.add(new ExprTerm(token, ExpressionType.NUMBER));
                continue;
            } else if (Helper.isOperator(token)) {
                ExpressionType type = getOperatorType(token);
                int precedence = getOperatorPrecedence(token);
                if (!operatorStack.isEmpty()) {
                    ExprTerm stackHead = operatorStack.peek();
                    if ((stackHead instanceof ExprOperator && precedence < stackHead.precedence)) {
                        outputQueue.add(operatorStack.pop());
                    }
                }
                operatorStack.push(new ExprOperator(token, type, precedence));
                continue;
            } else if (token.matches("^[A-Z]$")) {
                // Based on the next token check if this one is the start of a Cell ID
                String cellId = Helper.isCellId(CellContent, cursor);
                if (cellId != null) {
                    String content = getCellById(cellId).content;
                    ExprTerm Term;
                    if (Helper.isDigit(content)) {
                        Term = new ExprTerm(content, ExpressionType.NUMBER);
                        outputQueue.add(Term);
                    } else if (Helper.isExpression(content)) {
                        if (content.contains(cellId)) {
                            throw new Error("Circular reference for cell "+ cellId);
                        }
                        String evaluated = String.valueOf(evaluateParsedExpression(parseExpression(content)));
                        Term = new ExprTerm(evaluated, ExpressionType.NUMBER);
                        outputQueue.add(Term);
                    }
                    // TODO - handle when the content is an Expression
                    cursor += cellId.length();
                    continue;
                } else {
                    throw new Error("Unknown Expression type " + token);
                }
            }
        }

        while (!operatorStack.isEmpty()) {
            outputQueue.add(operatorStack.pop());
        }
        return outputQueue;
    }
    private int evaluateParsedExpression(Queue<ExprTerm> parsedQueue) {
        Stack<ExprTerm> evalStack = new Stack<>();
        for (ExprTerm term : parsedQueue) {
            if (term.type == ExpressionType.NUMBER) {
                evalStack.push(term);
                continue;
            } else if (term instanceof ExprOperator) {
                ExprTerm right = evalStack.pop();
                ExprTerm left = evalStack.pop();
                int rightValue = Integer.parseInt(right.value);
                int leftValue = Integer.parseInt(left.value);
                int termValue = Helper.doCalc(leftValue, term.value, rightValue);
                evalStack.push(new ExprTerm(String.valueOf(termValue), ExpressionType.NUMBER));
            }
        }
        return Integer.parseInt(evalStack.pop().value);
    }
}

class Cell {
    protected final String content;
    protected CellType type;
    protected int value;

    public Cell(String content, CellType type) {
        this.content = content;
        this.type = type;
    }

    public void setValue(int value) {
        this.value = value;
    }
}