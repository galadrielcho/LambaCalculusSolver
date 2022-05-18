
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		// Variable var = new Variable(tokens.get(0));

		// // This is nonsense code, just to show you how to thrown an Exception.
		// // To throw it, type "error" at the console.
		// if (var.toString().equals("error")) {
		// throw new ParseException("User typed \"Error\" as the input!", 0);
		// }
		// System.out.println("Parsing " + tokens);
		String last = tokens.get(tokens.size() - 1);

		if (last.compareTo(")") == 0) {
			int closedParensInWay = -1;
			for (int i = tokens.size() - 1; i >= 0; i--) {
				if (tokens.get(i).compareTo(")") == 0) {
					closedParensInWay++;
				} else if (tokens.get(i).compareTo("(") == 0) {
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
