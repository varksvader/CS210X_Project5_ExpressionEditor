import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * CS 210X 2017 B-term (Sinha, Backe)
 * Parser for expressions
 * Parser methods use the following grammar:
 * E := A | X
 * A := A+M | M
 * M := M*M | X
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
        Expression expression = parseExpression(str, withJavaFXControls);
        if (expression == null) {
            // If we couldn't parse the string, then raise an error
            throw new ExpressionParseException("Cannot parse expression: " + str);
        }

        // Flatten the expression before returning
        expression.flatten();

        return expression;
    }
    
    /**
	 * Returns the expression parsed into a tree using the grammar rules at
	 * the top of the file
	 * @param str the expression
	 * @param withJavaFXControls the boolean that activates the GUI
	 * @return the expression parsed into a tree
	 */
	private Expression parseExpression(String str, boolean withJavaFXControls) {
		return parseE(str, withJavaFXControls);
	}
	
	/**
	 * Returns the expression parsed into a tree using the rule:
	 * E := A | X
	 * @param str the expression
	 * @param withJavaFXControls the boolean that activates the GUI
	 * @return the expression parsed into a tree
	 */
	private Expression parseE(String str, boolean withJavaFXControls) {
		// if it can be parsed as an A
		if (parseA(str, withJavaFXControls) != null) {
			return parseA(str, withJavaFXControls);
		// if it can be parsed as an X
		} else if (parseX(str, withJavaFXControls) != null) {
			return parseX(str, withJavaFXControls);
		}
		return null;
	}
	
	/**
	 * Returns the additive expression parsed into a tree using the rule:
	 * A := A+M | M
	 * @param str the additive expression
	 * @param withJavaFXControls the boolean that activates the GUI
	 * @return the additive expression parsed into a tree
	 */
	private Expression parseA(String str, boolean withJavaFXControls) {
		// try A+M
		int idxOfPlus = str.indexOf('+');
		while (idxOfPlus > 0) { // try each +
			final Expression subExpression1 = parseA(str.substring(0, idxOfPlus), withJavaFXControls);
			final Expression subExpression2 = parseM(str.substring(idxOfPlus + 1), withJavaFXControls);
			if (subExpression1 != null && subExpression2 != null) {
				CompoundExpression addExpression = new CompoundExpressionImpl("+");
				// also parses into node
				if (withJavaFXControls) {
					addExpression = new CompoundExpressionImpl("+", new HBox());
					((HBox) addExpression.getNode()).getChildren().add(subExpression1.getNode());
					((HBox) addExpression.getNode()).getChildren().add(new Label("+"));
					((HBox) addExpression.getNode()).getChildren().add(subExpression2.getNode());
				}
				addExpression.addSubexpression(subExpression1);
                addExpression.addSubexpression(subExpression2);
                return addExpression;
			}
			idxOfPlus = str.indexOf('+', idxOfPlus + 1);
		}
		// try M
		if (parseM(str, withJavaFXControls) != null) {
			return parseM(str, withJavaFXControls);
		}
		return null;
	}

	/**
	 * Returns the additive expression parsed into a tree using the rule:
	 * M := M*M | X
	 * @param str the additive expression
	 * @param withJavaFXControls the boolean that activates the GUI
	 * @return the additive expression parsed into a tree
	 */
	private Expression parseM(String str, boolean withJavaFXControls) {
		// try M*M
		int idxOfTimes = str.indexOf('*');
		while (idxOfTimes > 0) { // try each *
			final Expression subExpression1 = parseM(str.substring(0, idxOfTimes), withJavaFXControls);
			final Expression subExpression2 = parseM(str.substring(idxOfTimes + 1), withJavaFXControls);
			if (subExpression1 != null && subExpression2 != null) {
				CompoundExpression multExpression = new CompoundExpressionImpl("·");
				// also parses into node
				if (withJavaFXControls) {
					multExpression = new CompoundExpressionImpl("·", new HBox());
					((HBox) multExpression.getNode()).getChildren().add(subExpression1.getNode());
					((HBox) multExpression.getNode()).getChildren().add(new Label("·"));
					((HBox) multExpression.getNode()).getChildren().add(subExpression2.getNode());
				}
				multExpression.addSubexpression(subExpression1);
                multExpression.addSubexpression(subExpression2);
                return multExpression;
			}
			idxOfTimes = str.indexOf('*', idxOfTimes + 1);
		}
		// try X
		if (parseX(str, withJavaFXControls) != null) {
			return parseX(str, withJavaFXControls);
		}
		return null;
	}

	/**
	 * Returns the parenthetical or literal expression parsed into a tree 
	 * using the rule: X := (E) | L
	 * @param str the parenthetical or literal expression
	 * @param withJavaFXControls the boolean that activates the GUI
	 * @return the parenthetical or literal expression parsed into a tree
	 */
	private Expression parseX(String str, boolean withJavaFXControls) {
		// try (E)
		final int indexLeftParen = str.indexOf("(");
        final int indexRightParen = str.lastIndexOf(")");
		if (indexLeftParen == 0 && indexRightParen == str.length() - 1
				&& parseE(str.substring(indexLeftParen + 1, indexRightParen), withJavaFXControls) != null) {
			final Expression subExpression = parseE(str.substring(indexLeftParen + 1, indexRightParen), withJavaFXControls);
			CompoundExpression parenExpression = new CompoundExpressionImpl("()");
			// also parses into node
			if (withJavaFXControls) {
				parenExpression = new CompoundExpressionImpl("()", new HBox());
				((HBox)parenExpression.getNode()).getChildren().add(new Label("("));
                ((HBox)parenExpression.getNode()).getChildren().add(subExpression.getNode());
                ((HBox)parenExpression.getNode()).getChildren().add(new Label(")"));
			}
			parenExpression.addSubexpression(subExpression);
            return parenExpression;
		}
		// try L
		if (parseL(str, withJavaFXControls) != null) {
			return parseL(str, withJavaFXControls);
		}
		return null;
	}

    /**
	 * Returns the literal expression parsed into a tree using the rule:
	 * L := [0-9]+ | [a-z]
	 * @param str the literal expression
	 * @param withJavaFXControls the boolean that activates the GUI
	 * @return the literal expression parsed into a tree
	 */
    private Expression parseL(String str, boolean withJavaFXControls) {
        Expression literal = null;
        // check [0-9]+ and [a-z]
        if (str.matches("[0-9]+|[a-z]")) {
            literal = new ExpressionImpl(str);
            //also parses into node
            if (withJavaFXControls) {
                literal = new ExpressionImpl(str, new HBox(new Label(str)));
            }
        }
        // returns null if string could not be parsed
        return literal;
    }
}

