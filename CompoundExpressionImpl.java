import java.util.*;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Expressions that have more than one term (also know as expressions other than literal expressions)
 */
public class CompoundExpressionImpl implements CompoundExpression {

	private CompoundExpression _parent;
	private String _operator;
	private List<Expression> _children;

	// Constructor
	public CompoundExpressionImpl(String operator) {
		_parent = null;
		_operator = operator;
		_children = new LinkedList<Expression>();
	}

	/**
	 * Returns the expression's parent.
	 * @return the expression's parent
	 */
	@Override
	public CompoundExpression getParent() {
		return _parent;
	}

	/**
	 * Sets the parent be the specified expression.
	 * @param parent the CompoundExpression that should be the parent of the target object
	 */
	@Override
	public void setParent(CompoundExpression parent) {
		_parent = parent;
	}

	/**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
	@Override
	public Expression deepCopy() {
		final Expression copy = new CompoundExpressionImpl(_operator);
		for (Expression e : this._children) {
			((CompoundExpressionImpl) copy).addSubexpression(e.deepCopy());
		}
		return copy;
	}
    
    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
    @Override
    public Node getNode() {
        // TODO Auto-generated method stub
        return null;
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
		// When operator is ()
		if (_operator.equals("()")) {
			for (Expression e : this._children) {
				e.flatten(); // recursively call flatten on children
			}
		} else { // When operator is * or +
			final ArrayList<Expression> toAdd = new ArrayList<Expression>();
			for (Expression e : this._children) {
				e.flatten(); // recursively call flatten on children
				// Check if children is a SimpleCompoundExpression
				if (e.getClass() == this.getClass()) {
					// Check if operation of children is the same.
					if (this._operator.equals(((CompoundExpressionImpl) e)._operator)) {
						for (Expression c : ((CompoundExpressionImpl) e)._children) {
							// adds children of children with the same operation to toAdd
							toAdd.add(c);
						}
					} else { // adds the child to toAdd if the operation is different
						toAdd.add(e);
					}
				} else { // adds the child to toAdd if it is of type literal or parenthetical
					toAdd.add(e);
				}
			} 
			this.clearSubexpression(); // clears subexpressions so that the order will stay the same
			for (Expression a : toAdd) { // adds all Expressions in toAdd as children of this
				this.addSubexpression(a);
			} 
		}
	}

	/**
	 * Creates a String representation by recursively printing out (using indentation) the
	 * tree represented by this expression, starting at the specified indentation level.
	 * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
	 * @return a String representation of the expression tree.
	 */
	@Override
	public String convertToString(int indentLevel) {
		final StringBuffer buf = new StringBuffer("");
		Expression.indent(buf, indentLevel);
		String str = buf.toString() + _operator + "\n";
		for (Expression child : _children) {
			str += child.convertToString(indentLevel + 1);
		}
		return str;
	}

	/**
	 * Adds the specified expression as a child.
	 * @param subexpression the child expression to add
	 */
	@Override
	public void addSubexpression(Expression subexpression) {
		_children.add(subexpression);
		subexpression.setParent(this);
	}

	/**
	 * Helper method for flatten
	 * Resets the children of the specified expression to be empty
	 */
	protected void clearSubexpression() {
		_children = new ArrayList<Expression>();
	}
}
