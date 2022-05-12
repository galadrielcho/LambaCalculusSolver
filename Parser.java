
import java.text.ParseException;
import java.util.ArrayList;

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
		int parenIndex = 0;
		for (int i = closeParenIndex; i >= 0; i--) {
			if ("(".equals(arr.get(i))) {
				parenIndex = i;
			}
		}
		return parse(subArrayList(arr, parenIndex, closeParenIndex + 1));
	}

	public Expression parse(ArrayList<String> tokens) throws ParseException {
		// Variable var = new Variable(tokens.get(0));

		// // This is nonsense code, just to show you how to thrown an Exception.
		// // To throw it, type "error" at the console.
		// if (var.toString().equals("error")) {
		// throw new ParseException("User typed \"Error\" as the input!", 0);
		// }

		String last = tokens.get(tokens.size() - 1);

		if (last == ")") {

			return findExpressionInsideParens(tokens, tokens.size() - 1);
		}
		if (tokens.size() == 1) {
			if (last.charAt(0) >= 'a' && last.charAt(0) <= 'z' || last.charAt(0) >= 'A' && last.charAt(0) <= 'Z') {
				return new Variable(last);
			}
		}

		return new Application(parse(subArrayList(tokens, 0, tokens.size() - 1)),
				parse(subArrayList(tokens, tokens.size() - 1, tokens.size())));

	}

}
