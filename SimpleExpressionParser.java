import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Parser following the grammar :
 * E := M+E | M
 * M := X*M | X
 * X := (E) | L
 * L := [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {

    /*
     * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
     * Throws a ExpressionParseException if the specified string cannot be parsed.
     * @param str the string to parse into an expression tree
     * @param withJavaFXControls you can just ignore this variable for R1
     * @return the Expression object representing the parsed expression tree
     */
    public Expression parse (String str, boolean withJavaFXControls) throws ExpressionParseException {
        // Remove spaces -- this simplifies the parsing logic
        str = str.replaceAll(" ", "");
        Expression expression = parseE(str, withJavaFXControls);
        if (expression == null) {
            // If we couldn't parse the string, then raise an error
            throw new ExpressionParseException("Cannot parse expression: " + str);
        }

        // Flatten the expression before returning
        expression.flatten();

        return expression;
    }

    /**
     * E := M+E | M
     * Attempts to parse string into an expression tree
     * with a root node that is the first instance of "+" which yields two valid subexpressions.
     * If unable to, calls parseM on str
     * @param str the string to parse into an expression tree
     * @param withJavaFXControls boolean to determine if corresponding JavaFX node should also be built when parsed
     * @return the Expression object representing the parsed but unflattened expression tree
     */
    private Expression parseE(String str, boolean withJavaFXControls) {
        int indexPlus = str.indexOf("+");
        while (indexPlus > -1) {

            // divides string based on location of "+"
            final String subString1 = str.substring(0, indexPlus);
            final String subString2 = str.substring(indexPlus + 1, str.length());

            // parses first subexpression
            final Expression subExpression1 = parseM(subString1, withJavaFXControls);
            // if first subexpression was able to be parsed, continue
            if (subExpression1 != null) {
                // parses second subexpression
                final Expression subExpression2 = parseE(subString2, withJavaFXControls);
                //if both subexpressions are able to be parsed, create new compound expression and add the two subexpressions to it
                if (subExpression2 != null) {
                    CompoundExpression addExpression = new CompoundExpressionImpl("+");

                    if (withJavaFXControls)
                        addExpression = new CompoundExpressionImpl("+", new HBox());

                    addExpression.addSubexpression(subExpression1);
                    addExpression.addSubexpression(subExpression2);

                    //also parses into node
                    if (withJavaFXControls) {
                        ((HBox)addExpression.getNode()).getChildren().add(subExpression1.getNode());
                        ((HBox)addExpression.getNode()).getChildren().add(new Label("+"));
                        ((HBox)addExpression.getNode()).getChildren().add(subExpression2.getNode());
                    }

                    return addExpression;
                }
            }

            // if previous instance of "+" did not yield valid subexpressions, check the next one
            indexPlus = str.indexOf("+", (indexPlus + 1));
        }

        // if there are no valid instances of "+", check multiplication
        return parseM(str, withJavaFXControls);
    }

    /**
     * M := X*M | X
     * Attempts to parse string into an expression tree
     * with a root node that is the first instance of "*" which yields two valid subexpressions.
     * If unable to, calls parseX on str
     * @param str the string to parse into an expression tree
     * @param withJavaFXControls boolean to determine if corresponding JavaFX node should also be built when parsed
     * @return the Expression object representing the parsed but unflattened expression tree
     */
    private Expression parseM(String str, boolean withJavaFXControls) {
        int indexTimes = str.indexOf("*");
        while (indexTimes > -1) {

            // divides string based on location of "*"
            final String subString1 = str.substring(0, indexTimes);
            final String subString2 = str.substring(indexTimes + 1, str.length());

            // parses first subexpression
            final Expression subExpression1 = parseX(subString1, withJavaFXControls);
            // if first subexpression was able to be parsed, continue
            if (subExpression1 != null) {
                // parses second subexpression
                final Expression subExpression2 = parseM(subString2, withJavaFXControls);
                //if both subexpressions are able to be parsed, create new compound expression and add the two subexpressions to it
                if (subExpression2 != null) {
                    CompoundExpression multExpression = new CompoundExpressionImpl("*");

                    if (withJavaFXControls)
                        multExpression = new CompoundExpressionImpl("*", new HBox());

                    multExpression.addSubexpression(subExpression1);
                    multExpression.addSubexpression(subExpression2);

                    //also parses into node
                    if (withJavaFXControls) {
                        ((HBox)multExpression.getNode()).getChildren().add(subExpression1.getNode());
                        ((HBox)multExpression.getNode()).getChildren().add(new Label("·"));
                        ((HBox)multExpression.getNode()).getChildren().add(subExpression2.getNode());
                    }

                    return multExpression;
                }
            }

            // if previous instance of "*" did not yield valid subexpressions, check the next one
            indexTimes = str.indexOf("*", (indexTimes + 1));
        }

        // if there are no valid instances of "*", check parentheses
        return parseX(str, withJavaFXControls);
    }

    /**
     * X := (E) | L
     * Attempts to parse string into an expression tree
     * with a root node that is the first occurrence of closed parentheses which yields a valid subexpressions.
     * If unable to, calls parseL on str
     * @param str the string to parse into an expression tree
     * @param withJavaFXControls boolean to determine if corresponding JavaFX node should also be built when parsed
     * @return the Expression object representing the parsed but unflattened expression tree
     */
    private Expression parseX(String str, boolean withJavaFXControls) {
        final int indexLeftParen = str.indexOf("(");
        final int indexRightParen = str.lastIndexOf(")");
        // if the left parentheses is the first character in the string
        if (indexLeftParen == 0) {
            // if the right parentheses is the last character in the string
            if (indexRightParen == str.length() - 1) {
                final String subString = str.substring(indexLeftParen + 1, indexRightParen);

                // parses subexpression within parentheses
                final Expression subExpression = parseE(subString, withJavaFXControls);
                //if subexpression is able to be parsed, create new compound expression and add the subexpression to it
                if (subExpression != null) {
                    CompoundExpression parenExpression = new CompoundExpressionImpl("()");

                    if (withJavaFXControls)
                        parenExpression = new CompoundExpressionImpl("()", new HBox());

                    parenExpression.addSubexpression(subExpression);

                    //also parses into node
                    if (withJavaFXControls) {
                        ((HBox)parenExpression.getNode()).getChildren().add(new Label("("));
                        ((HBox)parenExpression.getNode()).getChildren().add(subExpression.getNode());
                        ((HBox)parenExpression.getNode()).getChildren().add(new Label(")"));
                    }

                    return parenExpression;
                }
            }
        }

        // if there are no valid instances of parentheses check numbers and letters
        return parseL(str, withJavaFXControls);
    }

    /**
     * L := [0-9]+ | [a-z]
     * Attempts to parse string into an expression tree
     * with a root node that is a number or letter
     * If unable to, returns null.
     * @param str the string to parse into an expression tree
     * @param withJavaFXControls boolean to determine if corresponding JavaFX node should also be built when parsed
     * @return the Expression object representing the parsed but unflattened expression tree
     */
    private Expression parseL(String str, boolean withJavaFXControls) {

        Expression literal = null;

        // checks numbers and letters
        if (str.matches("[0-9]+|[a-z]")) {
            literal = new LiteralExpression(str);

            //also parses into node
            if (withJavaFXControls) {
                literal = new LiteralExpression(str, new HBox(new Label(str)));
            }
        }

        // returns null if string could not be parsed
        return literal;
    }
}

