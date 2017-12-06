package Part1;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Expressions that have parentheses enclosing them
 */
public class ParentheticalExpression extends AbstractCompoundExpression {

	//private final String _operator;

	// Constructor
	public ParentheticalExpression() {
		super();
		super._operator = "()";
	}

	/**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
	public Expression deepCopy() {
		final Expression copy = new ParentheticalExpression();
		for (Expression e : this._children) {
			((AbstractCompoundExpression) copy).addSubexpression(e.deepCopy());
		}
		return copy;
	}

	/**
	 * Recursively flattens the expression as much as possible
	 * throughout the entire tree. Specifically, in every multiplicative
	 * or additive expression x whose first or last
	 * child c is of the same type as x, the children of c will be added to x, and
	 * c itself will be removed. This method modifies the expression itself.
	 */
	public void flatten() {
		for (Expression e : this._children) {
			e.flatten(); // recursively call flatten on children
		}
	}
}
