import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
//import java.awt.*;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Add description later
 */

public class ExpressionImpl implements Expression {

    protected CompoundExpression _parent;
    protected String _representation;
    protected Pane _node;
    protected Color _textColor;

    protected ExpressionImpl(String representation) {
        _parent = null;
        _representation = representation;
        _node = null;
    }

    protected ExpressionImpl(String representation, Pane nodeRepresentation){
        _parent = null;
        _representation = representation;
        _node = nodeRepresentation;
    }

    /**
     * Returns the expression's parent.
     * @return the expression's parent
     */
    public CompoundExpression getParent() {
        return _parent;
    }

    /**
     * Sets the parent be the specified expression.
     * @param parent the CompoundExpression that should be the parent of the target object
     */
    public void setParent(CompoundExpression parent) {
        _parent = parent;
    }

    /**
     * Creates and returns a deep copy of the expression and its JavaFX node if it has one
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * Should be overridden for compound expressions so as to also deep copy children.
     * @return the deep copy
     */
    public Expression deepCopy() {

        if(_node == null) {
            return new ExpressionImpl(new String(_representation));
        }

        return new ExpressionImpl(new String(_representation), copyNode());
    }

    /**
     * Creates and returns a deep copy of the JavaFX node representing the expression
     * Should be overridden for compound expressions so as to also deep copy the children of the node
     * @return a Pane which is the deep copy of the expression's JavaFX node
     */
    protected Pane copyNode() {
        Pane copy = new HBox();
        Labeled oldLabel = (Labeled) _node.getChildren().get(0);
        Labeled toAdd = new Label((oldLabel).getText());
        copy.getChildren().add(toAdd);
        return copy;
    }

    /**
     * Returns the JavaFX node associated with this expression.
     * Should be overridden for subclasses of compound expression so as to also include the nodes of the children.
     * @return the JavaFX node associated with this expression.
     */
    public Node getNode() {
        return _node;
    }

    /**
     * Sets the JavaFX node associated with this expression.
     * @param newNode the Pane to set the JavaFX node to
     */
    protected void setNode(Pane newNode) {
        _node = newNode;
    }

    /**
     * Flattens the expression as much as possible, including the JavaFX nodes
     * throughout the entire tree. This method modifies the expression itself.
     * Should be overridden for compound expressions so as to also recursively flatten children.
     */
    public void flatten () {
    }

    /**
     * Creates a String representation by printing out the
     * tree represented by this expression, starting at the specified indentation level.
     * Should be overridden for compound expressions so as to also recursively convert children to string.
     * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
     * @return a String representation of the expression tree.
     */
    public String convertToString (int indentLevel) {
        final StringBuffer sb = new StringBuffer();
        Expression.indent(sb, indentLevel);
        sb.append(_representation + "\n");
        return sb.toString();
    }

    /**
     * Returns a string representing the type of expression.
     * In the case of compound expression this will be a an operator or parentheses.
     * In the case of a literal it will be the number or letter.
     * @return the type or string representation
     */
    protected String getType() {
        return _representation;
    }

    /**
	 * Switches placement in parent expression with the sibling whose JavaFX node is at the given x coordinate
	 * @param x the x coordinate
	 */
    @Override
    public void swap(double x) {
        if (_parent != null && _node != null) {
            final Pane p = (Pane) _node.getParent();
            // makes a copy of the node's parent's children
            List<Node> currentCase = FXCollections.observableArrayList(p.getChildren());

            final int currentIndex = currentCase.indexOf(_node);
            // +- 2 so as to skip over operation labels
            final int leftIndex = currentIndex - 2;
            final int rightIndex = currentIndex + 2;

            // finding index in expression's parent's children
            final int expressionIndex = (int) currentIndex / 2;
            final int leftExpressionIndex = expressionIndex - 1;
            final int rightExpressionIndex = expressionIndex + 1;

            // determining coordinates in scene
            Bounds currentBoundsInScene = _node.localToScene(_node.getBoundsInLocal());
            final double currentX = currentBoundsInScene.getMinX();
            // if there is no sibling to the left, then the farthest leftwards x coordinate would be that of this expression's JavaFX node
            double leftX = currentX;
            double leftWidth = 0;
            double operatorWidth = 0;

            // determining width of labels representing operations
            if (currentCase.size() > 0) {
                if (currentIndex == 0) {
                    operatorWidth = ((Region)currentCase.get(1)).getWidth();
                } else {
                    operatorWidth = ((Region)currentCase.get(currentCase.size() - 2)).getWidth();
                }
            }
            // checking if this expression and its JavaFX node should be swapped with its sibling to the left
            // first make sure there is a sibling to the left
            if (leftIndex >= 0) {
                Bounds leftBoundsInScene = p.getChildren().get(leftIndex).localToScene(p.getChildren().get(leftIndex).getBoundsInLocal());
                // if the node of this expression was to be in the left position,
                // then its x coordinate would be that of the leftwards sibling
                leftX = leftBoundsInScene.getMinX();
                leftWidth = leftBoundsInScene.getWidth();

                if (Math.abs(x - leftX) < Math.abs(x - currentX)) {
                    Collections.swap(currentCase, currentIndex, leftIndex);
                    p.getChildren().setAll(currentCase);
                    //also swaps the expression itself, not just its JavaFX node
                    swapSubexpressions(expressionIndex, leftExpressionIndex);
                    return;
                }
            }
            // checking if this expression and its JavaFX node should be swapped with its sibling to the right
            // first make sure there is a sibling to the right
            if (rightIndex < currentCase.size()) {
                Bounds rightBoundsInScene = p.getChildren().get(rightIndex).localToScene(p.getChildren().get(rightIndex).getBoundsInLocal());
                // if the node of this expression was to be in the right position,
                // then its x coordinate would be the coordinate of the left sibling, the width of the left sibling, the width of this expression's node, and the width of any labels in place for operator symbols
                final double rightX = leftX + leftWidth + operatorWidth + rightBoundsInScene.getWidth() + operatorWidth;

                if (Math.abs(x - rightX) < Math.abs(x - currentX)) {
                    Collections.swap(currentCase, currentIndex, rightIndex);
                    p.getChildren().setAll(currentCase);
                    // also swaps the expression itself, not just its JavaFX node
                    swapSubexpressions(expressionIndex, rightExpressionIndex);
                    return;
                }
            }
        }
    }

    /**
     * Switches placement in parent expression with the sibling at the given index of swapIndex
     * @param currentIndex index of this expression in its parent's list of children
     * @param swapIndex index of the sibling to switch with in the parent's list of children
     */
    private void swapSubexpressions(int currentIndex, int swapIndex) {
        Collections.swap(_parent.getSubexpressions(), currentIndex, swapIndex);
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
        return null;
    }

    /**
     * Changes color of the text in the expression's JavaFX node to given color
     * @param c the given color
     */
    @Override
    public void setColor(Color c) {
        ((Labeled)_node.getChildren().get(0)).setTextFill(c);
    }
}