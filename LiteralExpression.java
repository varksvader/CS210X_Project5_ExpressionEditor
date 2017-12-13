import javafx.scene.layout.Pane;

public class LiteralExpression extends ExpressionImpl {

    /**
     * An expression which is a number or a letter
     * @param representation the number or letter
     */
    public LiteralExpression(String representation) {
        super(representation);
    }

    /**
     * An expression which is a number or a letter and who has a JavaFX node
     * @param representation the number or letter
     * @param nodeRepresentation a Pane that is the JavaFX representation of the expression
     */
    public LiteralExpression(String representation, Pane nodeRepresentation) {
        super(representation, nodeRepresentation);
    }
}