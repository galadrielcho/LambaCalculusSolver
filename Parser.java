
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {

	/*
	 * Turns a set of tokens into an expression. Comment this back in when you're
	 * ready.
	 */

	private ArrayList<String> subArrayList(ArrayList<String> arr, int start, int end) {
		ArrayList<String> subset = new ArrayList<String>();
		for (int i = start; i < end; i++) {
			subset.add(arr.get(i));
		}
		return subset;
	}

	private Expression findExpressionInsideParens(ArrayList<String> arr, int closeParenIndex) throws ParseException {
		Stack<Integer> innerCloseParens = new Stack<Integer>();

		for (int i = closeParenIndex - 1; i >= 0; i--) {
			if (arr.get(i).equals(")")) {
				innerCloseParens.push(i);
			} else if (arr.get(i).equals("(")) {
				if (innerCloseParens.size() == 0) {
					return parse(subArrayList(arr, i + 1, closeParenIndex));
				}
				else if (innerCloseParens.size() > 0) {
					ArrayList<String> insideParen = subArrayList(arr, i + 1, innerCloseParens.pop());
					ArrayList<String> outsideParens = subArrayList(arr, 0, i);
					outsideParens.add(")");
					return new Application(parse(outsideParens), parse(insideParen));
				}
			}

		}
		throw new ParseException("Parentheses not balanced! :()", 0);
	}

	public Expression parse(ArrayList<String> tokens) throws ParseException {
		// Variable var = new Variable(tokens.get(0));

		// // This is nonsense code, just to show you how to thrown an Exception.
		// // To throw it, type "error" at the console.
		// if (var.toString().equals("error")) {
		// throw new ParseException("User typed \"Error\" as the input!", 0);
		// }

		String last = tokens.get(tokens.size() - 1);
		System.out.println("In parse:" + tokens);
		if (last.equals(")")) {

			return findExpressionInsideParens(tokens, tokens.size() - 1);
		}

		if (tokens.size() == 1) {
			// last remaining is letter
			if (last.charAt(0) >= 'a' && last.charAt(0) <= 'z' || last.charAt(0) >= 'A' && last.charAt(0) <= 'Z') {
				return new Variable(last);
			}
		}

		return new Application(parse(subArrayList(tokens, 0, tokens.size() - 1)),
				parse(subArrayList(tokens, tokens.size() - 1, tokens.size())));

	}

}
