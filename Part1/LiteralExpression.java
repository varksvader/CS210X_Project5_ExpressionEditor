package Part1;

public class LiteralExpression implements Expression {

    private String data;
    private CompoundExpression parent;

    public LiteralExpression(String data) {
        this.data = data;
    }

    @Override
    public CompoundExpression getParent() {
        return parent;
    }

    @Override
    public void setParent(CompoundExpression parent) {
        this.parent = parent;
    }

    @Override
    public Expression deepCopy() {
        return null;
    }

    @Override
    public void flatten() {

    }

    @Override
    public String convertToString(int indentLevel) {
        StringBuffer buf = new StringBuffer("");
        Expression.indent(buf, indentLevel);
        buf.append(data + "\n");
        return buf.toString();
    }
}
