package Part1;

public class CompoundExpressionImpl implements CompoundExpression {

    CompoundExpression parent;

    public CompoundExpressionImpl() {

    }

    @Override
    public void addSubexpression(Expression subexpression) {

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
        return null;
    }
}
