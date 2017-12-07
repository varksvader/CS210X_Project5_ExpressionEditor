package Part1;
import java.util.*;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Expressions that have operators such as "+" or "*"
 */
public class SimpleCompoundExpression extends AbstractCompoundExpression {

	//Constructor
	public SimpleCompoundExpression(String operator) {
		super();
		super._operator = operator;
	}

	/**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
	@Override
	public Expression deepCopy() {
		final Expression copy = new SimpleCompoundExpression(_operator);
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
	@Override
	public void flatten() {
		final ArrayList<Expression> toAdd = new ArrayList<Expression>();
		for (Expression e : this._children) {
			e.flatten(); // recursively call flatten on children
			if (e.getClass() == this.getClass()) { // Check if children is a SimpleCompoundExpression
				if (this._operator.equals(((SimpleCompoundExpression) e)._operator)) { // Check if operation of
					// children is the same.
					for (Expression c : ((SimpleCompoundExpression) e)._children) {
						toAdd.add(c); // adds children of children with the same operation to toAdd.
					}
				} else {
					toAdd.add(e); // adds the child to toAdd if the operation is different
				}
			} else {
				toAdd.add(e); // adds the child to toAdd if it is of type literal or parenthetical
			}
		}
		this.clearSubexpression(); // clears subexpressions so that the order will stay the same
		for (Expression a : toAdd) {
			this.addSubexpression(a); // adds all Expressions in toAdd as children of this
		}
	}
}
