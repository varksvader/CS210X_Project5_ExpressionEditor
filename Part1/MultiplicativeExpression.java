package Part1;

import java.util.LinkedList;

public class MultiplicativeExpression extends SimpleCompoundExpression {

    public MultiplicativeExpression() {
        operation = "*";
        children = new LinkedList<>();
    }

	@Override
	public Expression deepCopy() {
		final Expression copy = new MultiplicativeExpression();
		for (Expression e : this.children) {
			((AbstractCompoundExpression) copy).addSubexpression(e.deepCopy());
		}
		return copy;
	}
    
    

}
