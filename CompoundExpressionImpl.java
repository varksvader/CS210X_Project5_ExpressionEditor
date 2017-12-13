//import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class CompoundExpressionImpl extends ExpressionImpl implements CompoundExpression {

    protected List<Expression> _children;

    /**
     * Implementation of the CompoundExpression Interface. Superclass for all types of expressions with children.
     * @param representation a string representing the type of expression (as in "()", "+", or "·")
     */
    protected CompoundExpressionImpl(String representation) {
        super(representation);
        _children = new ArrayList<Expression>();
    }

    /**
     * Implementation of the CompoundExpression Interface in the case of having a non-null JavaFX node.
     * Superclass for all types of expressions with children.
     * @param representation a string representing the type of expression (as in "()", "+", or "·")
     * @param nodeRepresentation a Pane that is the JavaFX representation of the expression
     */
    protected CompoundExpressionImpl(String representation, Pane nodeRepresentation) {
        super(representation, nodeRepresentation);
        _children = new ArrayList<Expression>();
    }

    /**
     * Adds the specified expression as a child and sets the child's parent to be this CompoundExpression.
     * @param subexpression the child expression to add
     */
    public void addSubexpression(Expression subexpression) {
        _children.add(subexpression);
        // sets this CompoundExpression as the parent since Expressions only have one parent and so the child's parent must be this CompoundExpression
        subexpression.setParent(this);
    }

    /**
     * Returns the list of the CompoundExpression's children.
     * @return list of children
     */
    public List<Expression> getSubexpressions() {
        return _children;
    }

    /**
     * Creates and returns a deep copy of the expression and its JavaFX node if it has one
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    @Override
    public Expression deepCopy() {
        CompoundExpression copy = new CompoundExpressionImpl(new String(_representation));

        if (_node != null) {
            copy = new CompoundExpressionImpl(new String(_representation), copyNode());
        }

        for (Expression child : _children) {
            copy.addSubexpression(child.deepCopy());
        }

        return copy;
    }

    /**
     * Creates and returns a deep copy of the JavaFX node representing the expression
     * @return a Pane which is the deep copy of the expression's JavaFX node
     */
    @Override
    public Pane copyNode() {
        List<Node> newChildren = new ArrayList<Node>();

        int index = 0;
        for(Node child : _node.getChildren()) {
            if (child instanceof Label) {
                //if its a label, create a copy and add to list of children to be added
                Labeled toAdd = new Label(new String(((Label) child).getText()));
                newChildren.add(toAdd);
            } else {
                //otherwise its an HBox representing an expression, so call this method on the corresponding expression
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
         flattenChildren();
        if (_representation.equals("+") || _representation.equals("·")) {
        	flattenSelf();
        }
    }
    
    /**
     * Helper method for applying the flatten method to the compound expression itself.
     * If any of the expression's children are the same type as itself,
     * the child's children will be added to the list of children of this expression and the child removed.
     * The same happens with the JavaFX node as well if the expression has one
     */
    private void flattenSelf() {
        final List<Expression> newChildren = new ArrayList<>();

        if (_node != null) {
            _node.getChildren().clear();
        }

        for (Expression existingChild: _children) {
            // first cast as an ExpressionImpl to use the getType method since it could be a literal
            if (((ExpressionImpl) existingChild).getType() == _representation) {
                //----stuff for expression
                // Since the child has the same type as this object we know the child is an OperationExpression
                final List<Expression> childrenToAdd = ((CompoundExpressionImpl) existingChild).getSubexpressions();
                // update the new children to have this as their parent
                for (Expression child: childrenToAdd) {
                    child.setParent(this);
                }
                // add all the children to the newChildren list
                newChildren.addAll(childrenToAdd);

                //----stuff for nodes
                if (_node != null) {
                    final List<Node> nodesToAdd = ((Pane) existingChild.getNode()).getChildren();
                    _node.getChildren().addAll(nodesToAdd);
                    Labeled toAdd = new Label(_representation);
                    _node.getChildren().add(toAdd);
                }
            }
            else{
                //----stuff for expression
                // if it's not the same type, keep the existing child and add to the new list to preserve order
                newChildren.add(existingChild);

                //----stuff for nodes
                if (_node != null) {
                    _node.getChildren().add(existingChild.getNode());
                    Labeled toAdd = new Label(_representation);
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
     *
     * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
     * @return a String representation of the expression tree.
     */
    @Override
    public String convertToString(int indentLevel) {
        final StringBuffer sb = new StringBuffer();
        Expression.indent(sb, indentLevel);
        sb.append(_representation + "\n");

        for (Expression child : _children) {
            sb.append(child.convertToString(indentLevel + 1));
        }

        return sb.toString();
    }

    /**
     * Helper method for applying the flatten method to all children of this CompoundExpression
     */
    protected void flattenChildren() {
        for (Expression child : _children) {
            child.flatten();
        }
    }

    /**
     * Focuses on the subexpression at scene coordinates (x,y)
     * If no subexpression is clicked, lor no subexpression exists, returns null
     * @param x scene x coordinate
     * @param y scene y coordinate
     * @return the new focused Expression
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
     * Also recursively changes the color of all children's JavaFX nodes
     * @param c the given color
     */
    @Override
    public void setColor(Color c) {
        int index = 0;
        for(Node child : _node.getChildren()) {
            if (child instanceof Label) {
                ((Label) child).setTextFill(c);
            } else {
                _children.get(index).setColor(c);
                index++;
            }
        }
    }
}
