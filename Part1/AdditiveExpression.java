package Part1;

import java.util.LinkedList;

public class AdditiveExpression extends SimpleCompoundExpression {

    public AdditiveExpression() {
        operation = "+";
        children = new LinkedList<>();
    }

	@Override
	public Expression deepCopy() {
		final Expression copy = new AdditiveExpression();
		for (Expression e : this.children) {
			((AbstractCompoundExpression) copy).addSubexpression(e.deepCopy());
		}
		return copy;
	}

}
