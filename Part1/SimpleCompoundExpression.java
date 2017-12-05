package Part1;

import java.util.LinkedList;

public abstract class SimpleCompoundExpression extends AbstractCompoundExpression {

    // Need to fix bc cannot instantiate abstract class
    @Override
    public Expression deepCopy() {
        final SimpleCompoundExpression copy = this; // need to find better solution

        for (Expression child : children) {
            copy.children.add(child.deepCopy());
        }
        return copy;
    }

}
