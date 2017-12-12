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
	 * Returns the ghost version of the JavaFX node associated with this expression.
	 * @return the ghost version of the JavaFX node associated with this expression.
	 */
	@Override
	public Node getGhostNode() {
		Label literal = new Label(_value);
		literal.setTextFill(GHOST_COLOR);
		return literal;
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
	
	public void swap(double x) {
		Node node = this.getNode();
        if (_parent != null && node != null) {
            final HBox p = (HBox) node.getParent();
            List<Node> currentCase = FXCollections.observableArrayList(p.getChildren());

            final int currentIndex = currentCase.indexOf(node);
            // 2 so as to skip over operation labels
            final int leftIndex = currentIndex - 2;
            final int rightIndex = currentIndex + 2;

            final int expressionIndex = (int) currentIndex / 2;
            final int leftExpressionIndex = expressionIndex - 1;
            final int rightExpressionIndex = expressionIndex + 1;

            Bounds currentBoundsInScene = node.localToScene(node.getBoundsInLocal());
            final double currentX = currentBoundsInScene.getMinX();
            double leftX = currentX;
            double leftWidth = 0;
            double operatorWidth = 0;

            if (currentCase.size() > 0) {
                if (currentIndex == 0) {
                    operatorWidth = ((Region)currentCase.get(1)).getWidth();
                } else {
                    operatorWidth = ((Region)currentCase.get(currentCase.size() - 2)).getWidth();
                }
            }

            List<Node> leftCase = FXCollections.observableArrayList(p.getChildren());
            if (leftIndex >= 0) {
                Collections.swap(leftCase, currentIndex, leftIndex);

                Bounds leftBoundsInScene = ((HBox)p).getChildren().get(leftIndex).localToScene(((HBox)p).getChildren().get(leftIndex).getBoundsInLocal());
                leftX = leftBoundsInScene.getMinX();
                leftWidth = leftBoundsInScene.getWidth();

                if (Math.abs(x - leftX) < Math.abs(x - currentX)) {
                    p.getChildren().setAll(leftCase);
                    swapSubexpressions(expressionIndex, leftExpressionIndex);
                    return;
                }
            }

            List<Node> rightCase = FXCollections.observableArrayList(p.getChildren());
            if (rightIndex < rightCase.size()) {
                Collections.swap(rightCase, currentIndex, rightIndex);

                Bounds rightBoundsInScene = ((HBox)p).getChildren().get(rightIndex).localToScene(((HBox)p).getChildren().get(rightIndex).getBoundsInLocal());

                final double rightX = leftX + leftWidth + operatorWidth + rightBoundsInScene.getWidth() + operatorWidth;

                if (Math.abs(x - rightX) < Math.abs(x - currentX)) {
                    p.getChildren().setAll(rightCase);
                    swapSubexpressions(expressionIndex, rightExpressionIndex);
                    return;
                }
            }
        }
    }

    private void swapSubexpressions(int currentIndex, int swapIndex) {
        Collections.swap(((CompoundExpressionImpl) _parent).getChildren(), currentIndex, swapIndex);
    }
    
    public Expression focus(double x, double y) {
        return null;
    }
}
