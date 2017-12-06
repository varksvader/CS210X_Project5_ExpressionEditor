package Part1;

import java.util.*;

public abstract class AbstractCompoundExpression implements CompoundExpression {

    private CompoundExpression parent;
    String operation;
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
    public abstract Expression deepCopy();

    @Override
    public void flatten() {
    	final ArrayList<Expression> toAdd = new ArrayList<Expression>();
		for (Expression e : this.children) {
			e.flatten(); // recursively call flatten on children
			if (e.getClass() == this.getClass()) { // Check if children is a SimpleCompoundExpression
				if (this.operation.equals(((SimpleCompoundExpression) e).operation)) { // Check if operation of
																							// children is the same.
					for (Expression c : ((SimpleCompoundExpression) e).children) {
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
    
    public void clearSubexpression() {
		children = new ArrayList<Expression>();
	}

    @Override
    public String convertToString(int indentLevel) {
        System.out.println(children.size());
        StringBuffer buf = new StringBuffer("");
        Expression.indent(buf, indentLevel);

        String str = buf.toString() + operation + "\n";
        for (Expression child : children) {
            str += child.convertToString(indentLevel + 1);
        }
        return str;
    }

    @Override
    public void addSubexpression(Expression subexpression) {
        children.add(subexpression);
    }

}
