package Part1;

public class ExpressionImpl implements Expression {

    private String data;
    private CompoundExpression parent;

    public ExpressionImpl(String data) {
        this.data = data;
    }

    @Override
    public CompoundExpression getParent() {
        return null;
    }

    @Override
    public void setParent(CompoundExpression parent) {

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
        return null;
    }
}
