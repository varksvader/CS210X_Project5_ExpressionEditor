package Part1;

import java.util.LinkedList;

public class ParentheticalExpression extends AbstractCompoundExpression {

    public ParentheticalExpression() {
        operation = "()";
        children = new LinkedList<>();
    }

	@Override
	public Expression deepCopy() {
		final Expression copy = new ParentheticalExpression();
		for (Expression e : this.children) {
			((AbstractCompoundExpression) copy).addSubexpression(e.deepCopy());
		}
		return copy;
	}

}
