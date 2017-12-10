import javafx.application.Application;

import java.awt.*;
import java.util.*;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {

		private Pane pane;
		private CompoundExpression root;
		double lastX, lastY;
		private Region focus;

		MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
			this.pane = pane_;
			root = rootExpression_;
			focus = null;

			// pane's and the root's children mirror each other
		}

		public void handle (MouseEvent event) {
			final double sceneX = event.getSceneX();
			final double sceneY = event.getSceneY();

			final Pane hbox = (Pane) pane.getChildren().get(0);

			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

				for (Node child : hbox.getChildrenUnmodifiable()) {
					if (child.contains(child.sceneToLocal(sceneX, sceneY))) {
						// A literal expression or a  * or +
						if (child instanceof Label) {
							if (!((Label) child).getText().equals("*") && !((Label) child).getText().equals("+")) {
								if (focus != null) {
									focus.setBorder(Expression.NO_BORDER);
								}
								focus = (Region) child;
								focus.setBorder(Expression.RED_BORDER);
							} else {
								focus.setBorder(Expression.NO_BORDER);
								focus = hbox;
								focus.setBorder(Expression.NO_BORDER);
							}
						} else {
							if (focus != null) {
								focus.setBorder(Expression.NO_BORDER);
							}
							focus = (Region) child;
							focus.setBorder(Expression.RED_BORDER);
						}
					} else {
//						if (focus != null) {
//							focus.setBorder(Expression.NO_BORDER);
//							focus = null;
//						}
					}
				}

			} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				pane.setTranslateX(pane.getTranslateX() + sceneX - lastX);
				pane.setTranslateY(pane.getTranslateY() + sceneY - lastY);
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				pane.setLayoutX(pane.getLayoutX() + pane.getTranslateX());
				pane.setLayoutY(pane.getLayoutY() + pane.getTranslateY());

				pane.setTranslateX(0);
				pane.setTranslateY(0);
			}

			lastX = sceneX;
			lastY = sceneY;
		}
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed	
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle (MouseEvent e) {
				// Try to parse the expression
				try {
					// Success! Add the expression's Node to the expressionPane
					final Expression expression = expressionParser.parse(textField.getText(), true);
					System.out.println(expression.convertToString(0));
					expressionPane.getChildren().clear();
					expressionPane.getChildren().add(expression.getNode());
					expression.getNode().setLayoutX(WINDOW_WIDTH/4);
					expression.getNode().setLayoutY(WINDOW_HEIGHT/2);

					// If the parsed expression is a CompoundExpression, then register some callbacks
					if (expression instanceof CompoundExpression) {
						((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
						final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (CompoundExpression) expression);
						expressionPane.setOnMousePressed(eventHandler);
						expressionPane.setOnMouseDragged(eventHandler);
						expressionPane.setOnMouseReleased(eventHandler);
					}
				} catch (ExpressionParseException epe) {
					// If we can't parse the expression, then mark it in red
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
}
