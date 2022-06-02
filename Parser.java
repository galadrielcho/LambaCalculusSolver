// Written by Galadriel Cho and Annie Li

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Parser {

	String reservedCharacters = "λ\\().; ";

	/*
	 * Turns a set of tokens into an expression.
	 */

	private Stack<String> functionParameters = new Stack<>();

	public ArrayList<String> subArrayList(ArrayList<String> arr, int start, int end) {
		ArrayList<String> subset = new ArrayList<String>();
		for (int i = start; i < end; i++) {
			subset.add(arr.get(i));
		}
		return subset;
	}

	private int findFirstLambdaIndex(ArrayList<String> tokens) {
		int withinParens = 0;
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).equals("(")) {
				withinParens++;
			} else if (tokens.get(i).equals(")")) {
				withinParens--;
			}
			else if (tokens.get(i).equals("λ") && withinParens == 0) {
				return i;
			}
		}
		return -1;
	}

	public Expression parse(ArrayList<String> tokens) throws ParseException {
		if (tokens.get(0).equals("run")) {
			return new Application(new Variable("run", "free"), parse(subArrayList(tokens, 1, tokens.size())));
		}

		String last = tokens.get(tokens.size() - 1);
		int lambdaIndex = findFirstLambdaIndex(tokens);

		if (lambdaIndex == 0) {
			functionParameters.push(tokens.get(1));
			Expression e = parse(subArrayList(tokens, 3, tokens.size()));
			Function f = new Function(new Variable(functionParameters.pop(), "parameter"), e);
			return f;
		} else if (lambdaIndex > 0) {
			return new Application(parse(subArrayList(tokens, 0, lambdaIndex)),
					parse(subArrayList(tokens, lambdaIndex, tokens.size())));
		} else if (last.equals(")")) {
			int closedParensInWay = -1;
			for (int i = tokens.size() - 1; i >= 0; i--) {
				if (tokens.get(i).equals(")")) {
					closedParensInWay++;
				} else if (tokens.get(i).equals("(")) {
					if (closedParensInWay == 0) {
						if (i == 0) {
							return parse(subArrayList(tokens, 1, tokens.size() - 1));
						} else {
							return new Application(parse(subArrayList(tokens, 0, i)),
									parse(subArrayList(tokens, i + 1, tokens.size() - 1)));
						}
					} else {
						closedParensInWay--;
					}
				}
			}

			throw new ParseException("Parentheses not balanced! :()", 0);

		} else if (tokens.size() == 1) {

			if (last.charAt(0) >= '!' && last.charAt(0) <= '~') {
				if (functionParameters.contains(last)) {
					return new Variable(last, "bound");

				} else {
					return new Variable(last, "free");

				}
			}
		}

		return new Application(parse(subArrayList(tokens, 0, tokens.size() - 1)),
				parse(subArrayList(tokens, tokens.size() - 1, tokens.size())));

	}

}
