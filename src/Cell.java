/*
 * Cell class
 * Instance variables:
 *  - type -> the type of the Cell <text, number, formula>
 *  - content -> the raw content of the cell
 *  - value -> the value of the cell (if it's a formula)
 * Methods
 *  - getType() -> returns the type of the cell
 *  - getContent() -> returns the raw content of the cell
 *  - getValue() -> returns the value of the cell (if it's a formula)
 *  - setValue(double)
 *  - setType() -> private method so the cell can evaluate it content an assign a type to itself
 */
public class Cell {
    private String type;
    private final String content;
    private double value;

    public Cell(String content) {
        this.content = content;
        this.setType();
    }

    private void setType() {
        if (this.content.startsWith("==")) {
            this.type = "formula";
        } else if (this.content.matches("-?\\d+")) {
            this.type = "number";
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
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
