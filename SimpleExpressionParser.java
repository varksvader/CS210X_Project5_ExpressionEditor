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
	public Expression parse(String str, boolean withJavaFXControls) throws ExpressionParseException {
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
	
	/**
	 * Returns the expression parsed into a tree using the grammar rules at
	 * the top of the file
	 * @param str the expression
	 * @return the expression parsed into a tree
	 */
	private Expression parseExpression(String str) {
		return parseE(str);
	}

	/**
	 * Returns the expression parsed into a tree using the rule:
	 * E := A | X
	 * @param str the expression
	 * @return the expression parsed into a tree
	 */
	private Expression parseE(String str) {
		if (parseA(str) != null) {
			return parseA(str);
		} else if (parseX(str) != null) {
			return parseX(str);
		}
		return null;
	}

	/**
	 * Returns the additive expression parsed into a tree using the rule:
	 * A := A+M | M
	 * @param str the additive expression
	 * @return the additive expression parsed into a tree
	 */
	private Expression parseA(String str) {
		// Check A+M
		int idxOfPlus = str.indexOf('+');
		while (idxOfPlus > 0) { // try each +
			if (parseA(str.substring(0, idxOfPlus)) != null && parseM(
					str.substring(idxOfPlus + 1)) != null) {
				Expression result = new CompoundExpressionImpl("+");
				((CompoundExpressionImpl) result).addSubexpression(
						parseA(str.substring(0, idxOfPlus)));
				((CompoundExpressionImpl) result).addSubexpression(
						parseM(str.substring(idxOfPlus + 1)));
				return result;
			}
			idxOfPlus = str.indexOf('+', idxOfPlus + 1);
		}
		// Check M
		if (parseM(str) != null) {
			return parseM(str);
		}
		return null;
	}

	/**
	 * Returns the multiplicative expression parsed into a tree 
	 * using the rule: M := M*M | X
	 * @param str the multiplicative expression
	 * @return the multiplicative expression parsed into a tree
	 */
	private Expression parseM(String str) {
		// Check M*M
		int idxOfTimes = str.indexOf('*');
		while (idxOfTimes > 0) { // try each *
			if (parseM(str.substring(0, idxOfTimes)) != null && parseM(
					str.substring(idxOfTimes + 1)) != null) {
				Expression result = new CompoundExpressionImpl("*");
				((CompoundExpressionImpl) result).addSubexpression(
						parseM(str.substring(0, idxOfTimes)));
				((CompoundExpressionImpl) result).addSubexpression(
						parseM(str.substring(idxOfTimes + 1)));
				return result;
			}
			idxOfTimes = str.indexOf('+', idxOfTimes + 1);
		}
		// Check X
		if (parseX(str) != null) {
			return parseX(str);
		}
		return null;
	}

	/**
	 * Returns the parenthetical or literal expression parsed into a tree 
	 * using the rule: X := (E) | L
	 * @param str the parenthetical or literal expression
	 * @return the parenthetical or literal expression parsed into a tree
	 */
	private Expression parseX(String str) {
		// Check (E)
		if (str.startsWith("(") && str.endsWith(")") && 
				parseE(str.substring(1, str.length() - 1)) != null) {
			Expression result = new CompoundExpressionImpl("()");
			((CompoundExpressionImpl) result).addSubexpression(
					parseE(str.substring(1, str.length() - 1)));
			return result;
		}
		// Check L
		if (parseL(str) != null) {
			return parseL(str);
		}
		return null;
	}

	/**
	 * Returns the literal expression parsed into a tree using the rule:
	 * L := [0-9]+ | [a-z]
	 * @param str the literal expression
	 * @return the literal expression parsed into a tree
	 */
	private Expression parseL(String str) {
		// Check [0-9]+
		if (str.matches("[0-9]+")) {
			return new LiteralExpression(str);
		}
		// Check [a-z]
		if (str.length() == 1 && str.matches("[a-z]")) {
			return new LiteralExpression(str);
		}
		return null;
	}
}