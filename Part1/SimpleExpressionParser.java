package Part1;
/**
 * Starter code to implement an ExpressionParser. Your parser methods should use the following grammar:
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
		Expression expression = parseExpression(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		return expression;
	}
	
	protected Expression parseExpression (String str) {
		Expression expression;
		
		// TODO implement me
		return null;
	}

	private boolean parseE(String str) {
		if (parseA(str) || parseX(str)) {
			return true;
		}
		return false;
	}

	private boolean parseA(String str) {
		// Check A+M
		int indexOfPlus = str.indexOf("+");
		while (indexOfPlus >= 0) {
			if (parseA(str.substring(0, indexOfPlus)) &&
					parseM(str.substring(indexOfPlus + 1))) {
				return true;
			}
			indexOfPlus = str.indexOf("+", indexOfPlus + 1);
		}

		// Check M
		if (parseM(str)) {
			return true;
		}

		return false;
	}

	private boolean parseM(String str) {
		// Check M*M
		int indexOfMult = str.indexOf("*");
		while (indexOfMult >= 0) {
			if (parseA(str.substring(0, indexOfMult)) &&
					parseM(str.substring(indexOfMult + 1))) {
				return true;
			}
			indexOfMult = str.indexOf("*", indexOfMult + 1);
		}

		// Check X
		if (parseX(str)) {
			return true;
		}

		return false;
	}

	private boolean parseX(String str) {
		// Check (E)


		// Check L
		if (parseL(str)) {
			return true;
		}

		return false;
	}

	private boolean parseL(String str) {
		// Is letter
		if (str.length() == 1 &&
				(str.charAt(0) >= 'a' && str.charAt(0) <= 'z')) {
			return true;
		}

		// Is number
		if (str.matches("[0-9]+")) {
			return true;
		}

		// Is mult or addition
		if (str.equals("+") || str.equals("*")) {
			return true;
		}

		return false;
	}
}
