import java.util.*;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Expressions that have more than one term (also know as expressions other than literal expressions)
 */
public class CompoundExpressionImpl implements CompoundExpression {

	private CompoundExpression _parent;
	private final String _operator;
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
	 * Returns the expression's children.
	 * @return the expression's children
	 */
	protected List<Expression> getChildren() {
		return _children;
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
		final Pane hbox = new HBox();
		for (int i = 0; i < _children.size(); i++) {
			// Starts parentheses
			if (_operator.equals("()")) {
				hbox.getChildren().add(new Label("("));
			}
			// Use recursion to get subexpressions
			hbox.getChildren().add(_children.get(i).getNode());
			// Adds operators * or +
			if (i != _children.size() - 1) {
				hbox.getChildren().add(new Label(_operator));
			}
			// end parentheses
			if (_operator.equals("()")) {
				hbox.getChildren().add(new Label(")"));
			}
		}
		return hbox;
	}
	
	/**
	 * Returns the ghost version of the JavaFX node associated with this expression.
	 * @return the ghost version of the JavaFX node associated with this expression.
	 */
	@Override
	public Node getGhostNode() {
		final Pane hbox = new HBox();
		for (int i = 0; i < _children.size(); i++) {
			// Starts parentheses
			if (_operator.equals("()")) {
				Label startParen = new Label("(");
				startParen.setTextFill(GHOST_COLOR);
				hbox.getChildren().add(startParen);
			}
			// Use recursion to get subexpressions
			hbox.getChildren().add(_children.get(i).getNode());
			// Adds operators * or +
			if (i != _children.size() - 1) {
				Label operator = new Label(_operator);
				operator.setTextFill(GHOST_COLOR);
				hbox.getChildren().add(new Label(_operator));
			}
			// end parentheses
			if (_operator.equals("()")) {
				Label closeParen = new Label(")");
				closeParen.setTextFill(GHOST_COLOR);
				hbox.getChildren().add(closeParen);
			}
		}
		return hbox;
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
				// Check if children is a CompoundExpressionImpl
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
	private void clearSubexpression() {
		_children = new ArrayList<Expression>();
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

        for(Expression child : _children) {

            Bounds boundsInScene = child.getNode().localToScene(child.getNode().getBoundsInLocal());

            final double xMin = boundsInScene.getMinX();
            final double xMax = boundsInScene.getMaxX();
            final double yMin = boundsInScene.getMinY();
            final double yMax = boundsInScene.getMaxY();

            if (((x <= xMax) && (x >= xMin)) && ((y <= yMax) && (y >= yMin))) {
                ((HBox)child.getNode()).setBorder(RED_BORDER);
                return child;
            }
        }
        return null;
    }
}