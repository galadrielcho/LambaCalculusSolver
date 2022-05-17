
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

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
			// if input is a letter
			if (current >= 'a' && current <= 'z' || current >= 'A' && current <= 'Z') {
				wordLength++;
			} else {
				if (wordLength > 0) {
					tokens.add(input.substring(index - wordLength, index));
					wordLength = 0;
					// Not adding the parens..

				} else if (current != ' ') {
					tokens.add(Character.toString(current));

				}
			}
			index++;
		}

		if (wordLength > 0) {
			tokens.add(input.substring(index - wordLength, index));
			wordLength = 0;
		} else if (current != ' ') {
			tokens.add(Character.toString(current));

		}

		return tokens;
	}

}
