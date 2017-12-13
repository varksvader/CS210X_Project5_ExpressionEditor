import javafx.application.Application;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

		private Pane pane;
		private CompoundExpression root;
		private double lastX, lastY;
		private Region focus;
		private final Region hbox;
		private Expression focusedExpression;
		private int focusedExpressionIndex;
		private double[] possibleLocations;
		private Region deepCopy;

		MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
			this.pane = pane_;
			root = rootExpression_;
			focus = (Pane) pane.getChildren().get(0);
			hbox = focus;
			focusedExpressionIndex = 0;

			// pane's and the root's children mirror each other
		}

		private void clearFocus() {
			focus.setBorder(Expression.NO_BORDER);
			focus = hbox;
			focus.setBorder(Expression.NO_BORDER);
			focusedExpression = root;
		}

		private void makeGhosted(Node node) {
			if (node instanceof Label) {
				((Label) node).setTextFill(Expression.GHOST_COLOR);
			} else {
				for (Node child : ((Pane) node).getChildren()) {
					if (child instanceof Label) {
						((Label) child).setTextFill(Expression.GHOST_COLOR);
					} else {
						makeGhosted(child);
					}
				}
			}
		}

		private int getIndexofClosestValue(double x, double[] coords) {
			double distance = Math.abs(coords[0] - x);
			int idx = 0;
			for(int i = 1; i < coords.length; i++){
				double cdistance = Math.abs(coords[i] - x);
				if(cdistance < distance){
					idx = i;
					distance = cdistance;
				}
			}
			return idx;
		}

//		private double[] possibleLocationsOfFocus(List<CompoundExpression> possibilities, Expression focusedExpression) {
//			double[] coords = new double[possibilities.size()];
//
//			int counter = 0;
//			double coordsSum = 0;
//			for (CompoundExpression parent : possibilities) {
//				for (Expression child : ((CompoundExpressionImpl) parent)._children) {
//					if (child.equals(focusedExpression)) {
//						coords[counter] = coordsSum;
//						coordsSum = 0;
//						break;
//					} else {
//						coordsSum += child.getNode().getLayoutBounds().getWidth();
//					}
//				}
//				counter++;
//			}
//			return coords;
//		}

//		private double[] possibleLocationsOfFocus(List<CompoundExpression> possibilities, Node focus, Expression focusedExpression) {
//
//			List<HBox> configs = new ArrayList<>();
//
//			for (CompoundExpression expr : possibilities) {
//				configs.add((HBox) expr.getNode());
//			}
//
//			double[] coords = new double[configs.size()];
//
//			for (int i = 0; i < configs.size(); i++) {
//				for (Node child : configs.get(i).getChildren()) {
//
//				}
//			}
//			return coords;
//		}
//
//		private List<CompoundExpression> generatePossibleExpressions(Expression focusedExpression) {
//			final List<Expression> siblings = ((CompoundExpressionImpl) focusedExpression.getParent())._children;
//			final List<CompoundExpression> possibleResults = new ArrayList<>();
//
//			siblings.remove(focusedExpression);
//
//			for (int i = 0; i < siblings.size() + 1; i++) {
//				siblings.add(i, focusedExpression);
//				final CompoundExpression result = new CompoundExpressionImpl(((CompoundExpressionImpl) focusedExpression.getParent())._operator);
//				for (Expression expr : siblings) {
//					result.addSubexpression(expr);
//				}
//				possibleResults.add(result);
//				siblings.remove(focusedExpression);
//			}
//			return possibleResults;
//		}

		public void handle (MouseEvent event) {
			final double sceneX = event.getSceneX();
			final double sceneY = event.getSceneY();

			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

				boolean childContainsClick = false;

				final List<Node> children = ((Pane) focus).getChildren();


				for (int i = 0; i < children.size(); i++) {
					Node child = children.get(i);

					if (child.contains(child.sceneToLocal(sceneX, sceneY))) {
						childContainsClick = true;
						// A literal expression or a  * or +
						if (child instanceof Label) {
							if (!((Label) child).getText().equals("*") && !((Label) child).getText().equals("+")) {
								if (focus != null) {
									clearFocus();
								}
								focus = (Region) child;
								focusedExpression = ((CompoundExpressionImpl) focusedExpression)._children.get(i/2); // focus is a literal expression
								focusedExpressionIndex = i;
								focus.setBorder(Expression.RED_BORDER);
							} else {
								clearFocus();
							}
						} else if (child instanceof Text) { // if a literal expression is clicked on
							clearFocus();
						} else {
							if (focus != null) {
								clearFocus();
							}
							focus = (Region) child;
							focusedExpression = ((CompoundExpressionImpl) focusedExpression)._children.get(i/2);
							focusedExpressionIndex = i;
							focus.setBorder(Expression.RED_BORDER);
						}
					}
				}

				if (!childContainsClick) {
					clearFocus();
				}

				if (!focus.equals(hbox)) {
					deepCopy = (Region) focusedExpression.deepCopy().getNode();
					deepCopy.setLayoutX(focus.getLayoutX());
					deepCopy.setLayoutY(focus.getLayoutY());
					makeGhosted(focus);
					pane.getChildren().add(deepCopy);

				}


			} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				if (deepCopy != null) {
					deepCopy.setTranslateX(deepCopy.getTranslateX() + sceneX - lastX);
					deepCopy.setTranslateY(deepCopy.getTranslateY() + sceneY - lastY);
				}

				if (!focus.equals(hbox)) {
					possibleLocations = new double[((CompoundExpressionImpl) focusedExpression.getParent())._children.size()];
					int counter = 0;
					for (int i = 0; i < focus.getParent().getChildrenUnmodifiable().size(); i = i + 2) {
						possibleLocations[counter] = ((HBox) focus.getParent()).getChildren().get(i).getLayoutX();
						counter++;
					}
//
//					List<CompoundExpression> possibleConfigurations = generatePossibleExpressions(focusedExpression);
//
//					possibleLocations = possibleLocationsOfFocus(possibleConfigurations, focus, focusedExpression);

					final int index = getIndexofClosestValue(sceneX, possibleLocations);
					System.out.println(Arrays.toString(possibleLocations));
					focus.setLayoutX(possibleLocations[index]);
					focus.setLayoutY(hbox.getLayoutY());


					if (focusedExpressionIndex != index) {
						System.out.println("Focused index: " + focusedExpressionIndex);
						System.out.println("Index: " + index * 2);
						// modify expression tree based on dragging
						final List<Expression> siblingsOfFocus = ((CompoundExpressionImpl) focusedExpression.getParent())._children;
						siblingsOfFocus.remove(focusedExpression);
						siblingsOfFocus.add(index, focusedExpression);
						System.out.println(siblingsOfFocus.toString());
						System.out.println(focusedExpression.getParent().convertToString(0));

						ObservableList<Node> workingCollection = FXCollections.observableArrayList(((Pane) focus.getParent()).getChildren());
						Collections.swap(workingCollection,index * 2, focusedExpressionIndex);
						((Pane) focus.getParent()).getChildren().setAll(workingCollection);

						focusedExpressionIndex = index * 2;
						System.out.println("Focused index 2: " + focusedExpressionIndex);
						System.out.println("Index 2: " + index * 2);
					}

				}


			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				focus.setLayoutX(focus.getLayoutX() + focus.getTranslateX());
				focus.setLayoutY(focus.getLayoutY() + focus.getTranslateY());

				focus.setTranslateX(0);
				focus.setTranslateY(0);

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
