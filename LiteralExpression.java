import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Expressions that are literals (also known as numbers [0-9]+ and variables [a-z])
 */
public class LiteralExpression implements Expression {

	protected final String _value;
	private CompoundExpression _parent;

	// Constructor
	public LiteralExpression(String value) {
		_value = value;
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
		return new LiteralExpression(_value);
	}

	/**
	 * Returns the JavaFX node associated with this expression.
	 * @return the JavaFX node associated with this expression.
	 */
	@Override
	public Node getNode() {
		return new Label(_value);
	}

	/**
	 * Recursively flattens the expression as much as possible
	 * throughout the entire tree. Specifically, in every multiplicative
	 * or additive expression x whose first or last
	 * child c is of the same type as x, the children of c will be added to x, and
	 * c itself will be removed. This method modifies the expression itself.
	 */
	@Override
	public void flatten() { /** Literal expressions have no children to flatten **/ }

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
		buf.append(_value + "\n");
		return buf.toString();
	}
}
