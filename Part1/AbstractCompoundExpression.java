package Part1;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompoundExpression implements CompoundExpression {

    private CompoundExpression parent;
    String data;
    List<Expression> children = new LinkedList<>();

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
        String str = data + "\n";
        for (Expression child : children) {
            str += buf.toString() + child.convertToString(indentLevel + 1);
        }
        return str;
    }

    @Override
    public void addSubexpression(Expression subexpression) {
        children.add(subexpression);
    }



}
