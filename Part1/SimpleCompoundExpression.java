package Part1;

import java.util.LinkedList;

public class SimpleCompoundExpression extends AbstractCompoundExpression {

    private LinkedList<Expression> children = new LinkedList<>();

    @Override
    public Expression deepCopy() {
        final SimpleCompoundExpression copy = new SimpleCompoundExpression();

        for (Expression child : children) {
            copy.children.add(child.deepCopy());
        }
        return copy;
    }

    @Override
    public void addSubexpression(Expression subexpression) {
        children.add(subexpression);
    }
}
