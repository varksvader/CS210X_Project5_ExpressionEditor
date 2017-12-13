import java.util.List;

interface CompoundExpression extends Expression {
	/**
	 * Adds the specified expression as a child.
	 * @param subexpression the child expression to add
	 */
	void addSubexpression (Expression subexpression);
	
	/**
	 * Returns the list of the specified expression's children.
	 * @return the list of the specified expression's children
	 */
	List<Expression> getSubexpressions();
}
