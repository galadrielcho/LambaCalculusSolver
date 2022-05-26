
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Parser {
	/*
	 * Turns a set of tokens into an expression.
	 */

	private HashMap<String, Expression> dictionary = new HashMap<>();
	private Stack<String> functionParameters = new Stack<>();

	public boolean inDictionary(String s) {
		return dictionary.containsKey(s);
	}

	public Expression addToDictionary(ArrayList<String> tokens) throws ParseException {
		Expression exp = parse(subArrayList(tokens, 2, tokens.size()));
		dictionary.put(tokens.get(0), exp);

		return exp;
	}

	public void addToDictionary(String key, Expression value) {
		dictionary.put(key, value);
	}

	public Expression getDictValue(String s) throws ParseException {
		return dictionary.get(s);
	}

	public ArrayList<String> subArrayList(ArrayList<String> arr, int start, int end) {
		ArrayList<String> subset = new ArrayList<String>();
		for (int i = start; i < end; i++) {
			subset.add(arr.get(i));
		}
		return subset;
	}

	private int findFirstLambdaIndex(ArrayList<String> tokens) {
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).equals("(")) {
				return -1;
			} else if (tokens.get(i).equals("λ")) {
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
			// System.out.println("Test");
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

			if (inDictionary(last)) {

				return dictionary.get(last);
			} else if (last.charAt(0) >= 'a' && last.charAt(0) <= 'z'
					|| last.charAt(0) >= 'A' && last.charAt(0) <= 'Z') {
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
