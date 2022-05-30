import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	String reservedCharacters = "λ\\().; =";

	/*
	 * A lexer (or "tokenizer") converts an input into tokens that
	 * eventually need to be interpreted.
	 * 
	 * Given the input
	 * (\bat .bat flies)cat λg.joy! )
	 * you should output the ArrayList of strings
	 * [(, \, bat, ., bat, flies, ), cat, \, g, ., joy!, )]
	 *
	 */

	private HashMap<String, String> dictionary = new HashMap<>();

	public boolean inDictionary(String s) {
		return dictionary.containsKey(s);
	}

	public String getDictValue(String s) {
		return dictionary.get(s);
	}

	
	public void addToDictionary(String key, Expression e) {
		if (e instanceof Function) {
			Function f = (Function) e;
		
			dictionary.put(key, String.format("λ%s.%s", f.parameter, f.expression));
		} else {
			dictionary.put(key, e.toString());
		}
	}

	public ArrayList<String> tokenize(String input) {
		ArrayList<String> tokens = new ArrayList<String>();

		if (input.length() == 0) {
			return tokens;
		}
		int index = 0;
		char current = input.charAt(0);
		int wordLength = 0;

		while (!(current == ';') && index < input.length()) {
			current = input.charAt(index);
			// if input is not a reserved character
			if (reservedCharacters.indexOf(current) == -1) {
				wordLength++;
			} else {
				if (wordLength > 0) {
					String word = input.substring(index - wordLength, index);
					if (dictionary.containsKey(word) && index > input.indexOf("=")) {
						tokens.addAll(tokenize(dictionary.get(word)));

					} else {
						tokens.add(input.substring(index - wordLength, index));
					}
					wordLength = 0;

				}
				if (current != ' ') {
					tokens.add(Character.toString(current));

				}
			}
			index++;
		}

		if (wordLength > 0) {
			String word = input.substring(index - wordLength, index);
			if (dictionary.containsKey(word)&& index > input.indexOf("=")) {
				tokens.addAll(tokenize(dictionary.get(word)));

			} else {
				tokens.add(input.substring(index - wordLength, index));
			}			wordLength = 0;
		}

		return tokens;
	}

}