import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * Collection of methods to be used with expressions
 */
public class ExpressionImpl implements Expression {

	private CompoundExpression _parent;
    private final String _operator;
    protected final Pane _node;

    // Constructor without JavaFX node
    public ExpressionImpl(String operator) {
        _parent = null;
        _operator = operator;
        _node = null;
    }

    // Constructor with JavaFX node
    public ExpressionImpl(String operator, Pane node){
        _parent = null;
        _operator = operator;
        _node = node;
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
     * Returns the operator
     * @return the type or string representation
     */
    protected String getOperator() {
        return _operator;
    }

    /**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
    @Override
    public Expression deepCopy() {
        if(_node == null) {
            return new ExpressionImpl(new String(_operator));
        }
        return new ExpressionImpl(new String(_operator), copyNode());
    }

    /**
     * Returns a deep copy of the JavaFX node representing the expression
     * @return a deep copy of the JavaFX node representing the expression
     */
    protected Pane copyNode() {
        final Pane copy = new HBox();
        final Labeled oldLabel = (Labeled) _node.getChildren().get(0);
        final Labeled toAdd = new Label((oldLabel).getText());
        copy.getChildren().add(toAdd);
        return copy;
    }

    /**
	 * Returns the JavaFX node associated with this expression.
	 * @return the JavaFX node associated with this expression.
	 */
    @Override
    public Node getNode() {
        return _node;
    }

    /**
	 * Recursively flattens the expression as much as possible
	 * throughout the entire tree. Specifically, in every multiplicative
	 * or additive expression x whose first or last
	 * child c is of the same type as x, the children of c will be added to x, and
	 * c itself will be removed. This method modifies the expression itself.
	 */
    @Override
    public void flatten () { /** Implemented in child class **/ }

    /**
	 * Creates a String representation by recursively printing out (using indentation) the
	 * tree represented by this expression, starting at the specified indentation level.
	 * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
	 * @return a String representation of the expression tree.
	 */	
    @Override
    public String convertToString (int indentLevel) {
        final StringBuffer sb = new StringBuffer();
        Expression.indent(sb, indentLevel);
        sb.append(_operator + "\n");
        return sb.toString();
    }

    // Is there a way we can make it look much nicer?
    /**
	 * The specified expression switches places with its sibling whose JavaFX node is at the given x coordinate
	 * @param x the x coordinate
	 */
    public void swap(double x) {
        if (_parent != null && _node != null) {
            final Pane pane = (Pane) _node.getParent();
            // makes a copy of the node's parent's children
            List<Node> currentCase = FXCollections.observableArrayList(pane.getChildren());
            final int currentIndex = currentCase.indexOf(_node);
            // to skip over operation labels
            final int leftIndex = currentIndex - 2;
            final int rightIndex = currentIndex + 2;
            // finding index in expression's parent's children
            final int expressionIndex = (int) currentIndex / 2;
            final int leftExpressionIndex = expressionIndex - 1;
            final int rightExpressionIndex = expressionIndex + 1;
            // determining coordinates in scene
            Bounds currentBoundsInScene = _node.localToScene(_node.getBoundsInLocal());
            final double currentX = currentBoundsInScene.getMinX();
            // if there is no sibling to the left, then the farthest left x coordinate
            double leftX = currentX;
            double leftWidth = 0;
            double operatorWidth = getLabelWidth(currentCase, currentIndex);
            // checks for sibling to the left
            if (leftIndex >= 0) {
                Bounds leftBoundsInScene = pane.getChildren().get(leftIndex).localToScene(pane.getChildren().get(leftIndex).getBoundsInLocal());
                leftX = leftBoundsInScene.getMinX();
                leftWidth = leftBoundsInScene.getWidth();
               // if x coordinate is over another sibling, switches nodes
                if (Math.abs(x - leftX) < Math.abs(x - currentX)) {
                    Collections.swap(currentCase, currentIndex, leftIndex);
                    pane.getChildren().setAll(currentCase);
                    // swaps the expression itself, not just its JavaFX node
                    swapSubexpressions(expressionIndex, leftExpressionIndex);
                }
            }
            // checks for sibling to the right
            if (rightIndex < currentCase.size()) {
                Bounds rightBoundsInScene = pane.getChildren().get(rightIndex).localToScene(pane.getChildren().get(rightIndex).getBoundsInLocal());
                final double rightX = leftX + leftWidth + operatorWidth + rightBoundsInScene.getWidth() + operatorWidth;
                // if x coordinate is over another sibling, switches nodes
                if (Math.abs(x - rightX) < Math.abs(x - currentX)) {
                    Collections.swap(currentCase, currentIndex, rightIndex);
                    pane.getChildren().setAll(currentCase);
                    // swaps the expression itself, not just its JavaFX node
                    swapSubexpressions(expressionIndex, rightExpressionIndex);
                    return;
                }
            }
        }
    }
    
    /**
     * Helper method for swap
     * Returns width of labels
     * @param nodes the subexpressions of the current expression
     * @param currentIdx the current index of the expression
     * @return width of labels
     */
    private double getLabelWidth(List<Node> nodes, int currentIdx){
        double labelWidth = 0;
        if (nodes.size() > 0) {
            if (currentIdx == 0) {
                labelWidth = ((Region) nodes.get(1)).getWidth();
            } else {
                labelWidth = ((Region) nodes.get(nodes.size() - 2)).getWidth();
            }
        }
        return labelWidth;
    }

    /**
     * Helper method for swap
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
    protected Expression focus(double x, double y) {
        return null;
    }

    /**
     * Changes color of the text in the expression's JavaFX node to given color
     * @param c the given color
     */
    protected void setColor(Color c) {
        ((Labeled) _node.getChildren().get(0)).setTextFill(c);
    }
}