import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * CS 210X 2017 B-term (Sinha, Backe) 
 * GUI where the user can interact with expression
 */
public class ExpressionEditor extends Application {

	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
		final private Pane _pane;
		final private CompoundExpression _rootExpression;
		private Expression _focusedExpression;
		private Expression _copyExpression;
		private double _lastX;
		private double _lastY;

		MouseEventHandler (Pane pane, CompoundExpression rootExpression) {
			_pane = pane;
			_rootExpression = rootExpression;
			_focusedExpression = null;
			_copyExpression = null;
		}

		public void handle (MouseEvent event) {
			final double x = event.getSceneX();
			final double y = event.getSceneY();
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				// so long as an expression is currently in focus...
				if (_focusedExpression != null) {
					//if a copy does not exist, built it
					if (_copyExpression == null) {
						buildCopy();
					}
					//drags around copy
					_copyExpression.getNode().setTranslateX(_copyExpression.getNode().getTranslateX() + (x - _lastX));
					_copyExpression.getNode().setTranslateY(_copyExpression.getNode().getTranslateY() + (y - _lastY));
					//swaps focused expression accordingly
					((ExpressionImpl) _focusedExpression).swap(x);
				}
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				// print out root expression after mouse is released
				System.out.println(_rootExpression.convertToString(0));
				//if there is currently no copy, then change focus
				if (_copyExpression == null) {
					if (_focusedExpression == null) {
						_focusedExpression = ((ExpressionImpl) _rootExpression).focus(x, y);
					} else {
						((HBox) _focusedExpression.getNode()).setBorder(Expression.NO_BORDER);
						_focusedExpression = ((ExpressionImpl) _focusedExpression).focus(x, y);
					}
				} else {
					//if there is a copy, set it down (aka set it to null and remove from pane)
					((ExpressionImpl) _focusedExpression).setColor(Color.BLACK);
					_pane.getChildren().remove(_copyExpression.getNode());
					_copyExpression = null;
				}
			}

			_lastX = x;
			_lastY = y;
		}

		/**
		 * Copies the focused expression and puts the copy in the same location as the original
		 */
		private void buildCopy() {
			_copyExpression = _focusedExpression.deepCopy();
			//ghosts the focused expression
			((ExpressionImpl) _focusedExpression).setColor(Expression.GHOST_COLOR);
			_pane.getChildren().add(_copyExpression.getNode());

			final Bounds originalBounds = _focusedExpression.getNode().localToScene(_focusedExpression.getNode().getBoundsInLocal());
			final Bounds copyBounds = _copyExpression.getNode().localToScene(_copyExpression.getNode().getBoundsInLocal());

			_copyExpression.getNode().setLayoutX(originalBounds.getMinX() - copyBounds.getMinX());
			_copyExpression.getNode().setLayoutY(originalBounds.getMinY()- copyBounds.getMinY());
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
