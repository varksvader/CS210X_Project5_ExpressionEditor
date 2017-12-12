import javafx.application.Application;
import java.awt.*;
import java.util.*;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {

		private Pane _pane;
		private final CompoundExpression _root;
		private double _lastX, _lastY;
		// private Region _focus;
		// private final Region _hbox;
		private Expression _focusedExpression;
        private Expression _ghostExpression;

		MouseEventHandler (Pane pane, CompoundExpression rootExpression) {
			// pane's and the root's children mirror each other
			_pane = pane;
			_root = rootExpression;
			//_focus = (Pane) pane.getChildren().get(0);
			//_hbox = (Pane) pane.getChildren().get(0);
			_focusedExpression = null;
			_ghostExpression = null;
		}

		/**
		private void clearFocus() {
			_focus.setBorder(Expression.NO_BORDER);
			_focus = _hbox;
			_focus.setBorder(Expression.NO_BORDER);
		} */

		public void handle (MouseEvent event) {
			final double sceneX = event.getSceneX();
			final double sceneY = event.getSceneY();

			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				/**
				boolean childContainsClick = false;

				for (Node child : _focus.getChildrenUnmodifiable()) {
					if (child.contains(child.sceneToLocal(sceneX, sceneY))) {
						childContainsClick = true;
						// A literal expression or a  * or +
						if (child instanceof Label) {
							if (!((Label) child).getText().equals("*") && !((Label) child).getText().equals("+")) {
								if (_focus != null) {
									clearFocus();
								}
								_focus = (Region) child;
								_focus.setBorder(Expression.RED_BORDER);
							} else {
								clearFocus();
							}
						} else if (child instanceof Text) { // if a literal expression is clicked on
							clearFocus();
						} else {
							if (_focus != null) {
								_focus.setBorder(Expression.NO_BORDER);
							}
							_focus = (Region) child;
							_focus.setBorder(Expression.RED_BORDER);
						}
					}
				}

				if (!childContainsClick) {
					clearFocus();
				} */

			} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				/**
				_focus.setTranslateX(_focus.getTranslateX() + sceneX - _lastX);
				_focus.setTranslateY(_focus.getTranslateY() + sceneY - _lastY); */
				
				//Node _ghostNode = _ghostExpression.getGhostNode();
				
				if (_focusedExpression != null) {
                    if (_ghostExpression == null) {
                        Bounds bounds = _focusedExpression.getNode().localToScene(_focusedExpression.getNode().getBoundsInLocal());
                        _ghostExpression = _focusedExpression.deepCopy();
                        _pane.getChildren().add(_ghostExpression.getGhostNode());
                        _ghostExpression.getGhostNode().setLayoutX(bounds.getMinX());
                        _ghostExpression.getGhostNode().setLayoutY(bounds.getMinY());
                    }
                    _ghostExpression.getGhostNode().setTranslateX(_ghostExpression.getGhostNode().getTranslateX() + (sceneX - _lastX));
                    _ghostExpression.getGhostNode().setTranslateY(_ghostExpression.getGhostNode().getTranslateY() + (sceneY - _lastY));
                    ((Expression) _focusedExpression).swap(sceneX);
                }
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				/**
				_focus.setLayoutX(_focus.getLayoutX() + _focus.getTranslateX());
				_focus.setLayoutY(_focus.getLayoutY() + _focus.getTranslateY());
				//_focus.setTranslateX(0);
				//_focus.setTranslateY(0); */
				
				if (_ghostExpression == null) {
                    if (_focusedExpression == null) {
                        _focusedExpression = ((Expression) _root).focus(sceneX, sceneY);
                    } else {
                        ((HBox) _focusedExpression.getNode()).setBorder(Expression.NO_BORDER);
                        _focusedExpression = ((Expression) _focusedExpression).focus(sceneX, sceneY);
                    }
                } else {
                    _pane.getChildren().remove(_ghostExpression.getNode());
                    _ghostExpression = null;
                }
			}
			_lastX = sceneX;
			_lastY = sceneY;
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