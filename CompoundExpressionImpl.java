import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Expressions that have more than one term (also know as expressions other than literal expressions)
 */
public class CompoundExpressionImpl extends ExpressionImpl implements CompoundExpression {

    private List<Expression> _children;

    // Constructor without JavaFX node
    public CompoundExpressionImpl(String representation) {
        super(representation);
        _children = new ArrayList<Expression>();
    }

    // Constructor with JavaFX node
    public CompoundExpressionImpl(String representation, Pane nodeRepresentation) {
        super(representation, nodeRepresentation);
        _children = new ArrayList<Expression>();
    }

    /**
	 * Adds the specified expression as a child.
	 * @param subexpression the child expression to add
	 */
    public void addSubexpression(Expression subexpression) {
        _children.add(subexpression);
        subexpression.setParent(this);
    }

    /**
	 * Returns the list of the specified expression's children.
	 * @return the list of the specified expression's children
	 */
    @Override
    public List<Expression> getSubexpressions() {
        return _children;
    }

    /**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
    @Override
    public Expression deepCopy() {
        CompoundExpression copy = new CompoundExpressionImpl(new String(this.getOperator()));
        if (this.getNode() != null) {
            copy = new CompoundExpressionImpl(new String(this.getOperator()), copyNode());
        }
        for (Expression child : _children) {
            copy.addSubexpression(child.deepCopy());
        }
        return copy;
    }

    /**
     * Returns a deep copy of the JavaFX node representing the expression
     * @return a deep copy of the JavaFX node representing the expression
     */
    @Override
    protected Pane copyNode() {
        List<Node> newChildren = new ArrayList<Node>();
        int index = 0;
        for(Node child : _node.getChildren()) {
        	// if it is a label, create a copy and add to list of children to be added
        	if (child instanceof Label) {
                Labeled toAdd = new Label(new String(((Label) child).getText()));
                newChildren.add(toAdd);
            } else { // else it is an HBox representing an expression
                newChildren.add(((ExpressionImpl)_children.get(index)).copyNode());
                index++;
            }
        }
        Pane copyNode = new HBox();
        copyNode.getChildren().addAll(newChildren);
        return copyNode;
    }

    /**
     * Recursively flattens the expression as much as possible throughout the entire tree, including the JavaFX nodes
     * Should be overridden for operation expressions so that in every multiplicative
     * or additive expression x whose first or last
     * child c is of the same type as x, the children of c will be added to x, and
     * c itself will be removed. This method modifies the expression itself.
     */
    @Override
    public void flatten() {
        // flattens parentheses
        for (Expression child : _children) {
            child.flatten();
        }
        if (this.getOperator().equals("+") || this.getOperator().equals("·")) {
        	flattenSelf();
        }
    }
    
    /**
     * Helper method for flatten
     * Flattens the children of additive and multiplicative expressions
     */
    private void flattenSelf() {
        final List<Expression> newChildren = new ArrayList<>();
        // node does not exist, its children shouldn't either
        if (_node != null) {
            _node.getChildren().clear();
        }
        for (Expression existingChild: _children) {
        	// checks if the parent and child have the same operator
            if (((ExpressionImpl) existingChild).getOperator() == this.getOperator()) {
                final List<Expression> childrenToAdd = ((CompoundExpressionImpl) existingChild).getSubexpressions();
                // update the new children to have this as their parent
                for (Expression child: childrenToAdd) {
                    child.setParent(this);
                }
                // add all the children to the newChildren list
                newChildren.addAll(childrenToAdd);
                // same for nodes
                if (_node != null) {
                    final List<Node> nodesToAdd = ((Pane) existingChild.getNode()).getChildren();
                    _node.getChildren().addAll(nodesToAdd);
                    Labeled toAdd = new Label(this.getOperator());
                    _node.getChildren().add(toAdd);
                }
            } else{ // keep the existing child and add to the new list to preserve order
                newChildren.add(existingChild);

                // same for nodes
                if (_node != null) {
                    _node.getChildren().add(existingChild.getNode());
                    Labeled toAdd = new Label(this.getOperator());
                    _node.getChildren().add(toAdd);
                }
            }
        }
        // replace the children list with the new list
        _children = newChildren;
        if (_node != null) {
            //removes last additional operation sign
            _node.getChildren().remove( _node.getChildren().size()-1, _node.getChildren().size());
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
        final StringBuffer sb = new StringBuffer();
        Expression.indent(sb, indentLevel);
        sb.append(this.getOperator() + "\n");
        for (Expression child : _children) {
            sb.append(child.convertToString(indentLevel + 1));
        }
        return sb.toString();
    }

    /**
	 * Returns focus from the expression to its subexpression whose JavaFX node contains the point (x,y)
	 * if point (x,y) is on operator or outside the expression, the focus disappears
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return focus from the expression to its subexpression whose JavaFX node contains the point (x,y)
	 */
    @Override
    public Expression focus(double x, double y) {
        for(Expression child : _children) {
            final Bounds boundsInScene = child.getNode().localToScene(child.getNode().getBoundsInLocal());
            final double xMin = boundsInScene.getMinX();
            final double xMax = boundsInScene.getMaxX();
            final double yMin = boundsInScene.getMinY();
            final double yMax = boundsInScene.getMaxY();
            if (((x <= xMax) && (x >= xMin)) && ((y <= yMax) && (y >= yMin))) {
                ((Pane)child.getNode()).setBorder(RED_BORDER);
                return child;
            }
        }
        return null;
    }

    /**
     * Changes color of the text in the expression's JavaFX node to given color
     * @param c the given color
     */
    @Override
    public void setColor(Color c) {
        int index = 0;
        for(Node child : _node.getChildren()) {
            if (child instanceof Label) {
                ((Label) child).setTextFill(c);
            } else {
                ((ExpressionImpl) _children.get(index)).setColor(c);
                index++;
            }
        }
    }
}
