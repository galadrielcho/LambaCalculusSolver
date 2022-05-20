
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
	/*
	 * Turns a set of tokens into an expression.
	 */

	private HashMap<String, Expression> dictionary = new HashMap<>();
	
	public boolean inDictionary(String s) {
		return dictionary.containsKey(s);
	}

	public void addToDictionary(ArrayList<String> tokens) throws ParseException {
		dictionary.put(tokens.get(0), parse(subArrayList(tokens, 2, tokens.size())));
	}

	public Expression getDictValue(String s) throws ParseException {
		return dictionary.get(s);
	}

	private ArrayList<String> subArrayList(ArrayList<String> arr, int start, int end) {
		ArrayList<String> subset = new ArrayList<String>();
		for (int i = start; i < end; i++) {
			subset.add(arr.get(i));
		}
		return subset;
	}

	private int findFirstLambdaIndex(ArrayList<String> tokens) {
		for (int i = 0; i < tokens.size(); i++) {
			String current = tokens.get(i);
			if (tokens.get(i).equals("(")) {
				return -1;
			}
			else if (tokens.get(i).equals("λ")) {
				return i;
			}
		}
		return -1;
	}

	public boolean isStoringValue(ArrayList<String> tokens) {
		return tokens.size() > 2 && tokens.get(1).equals("=");

	} 

	public Expression parse(ArrayList<String> tokens) throws ParseException {
		String last = tokens.get(tokens.size() - 1);
		// System.out.println("Parsng" + tokens);

		int lambdaIndex = findFirstLambdaIndex(tokens);

		if (lambdaIndex == 0) {
			// System.out.println(subArrayList(tokens, 3, tokens.size()));
			return new Function(new Variable(tokens.get(1)), parse(subArrayList(tokens, 3, tokens.size())));
		} else if (lambdaIndex > 0) {
			return new Application(parse(subArrayList(tokens, 0, lambdaIndex)), parse(subArrayList(tokens, lambdaIndex, tokens.size())));
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
			if (inDictionary(last)) {
				return dictionary.get(last);
			}
			else if (last.charAt(0) >= 'a' && last.charAt(0) <= 'z' || last.charAt(0) >= 'A' && last.charAt(0) <= 'Z') {
				return new Variable(last);
			}
		}

		return new Application(parse(subArrayList(tokens, 0, tokens.size() - 1)),
				parse(subArrayList(tokens, tokens.size() - 1, tokens.size())));

	}

}
