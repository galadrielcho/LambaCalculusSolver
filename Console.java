import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {
	private static Scanner in;

	private static HashMap<String, String> dictionary = new HashMap<>();

	private static boolean isStoringValue(ArrayList<String> tokens) {
		return tokens.size() > 2 && tokens.get(1).equals("=");

	}

	public static Expression alphaReduce(Expression e, Variable v) {
		if (e instanceof Application) {
			return new Application(alphaReduce(((Application) e).left, v), alphaReduce(((Application) e).right, v));
		} else if (e instanceof Function) {
			if (((Function) e).parameter.nameEquals(v)) {
				return new Function(new Variable(v.toString() + "1", "parameter"),
						replace(((Function) e).expression, v, new Variable(v.toString() + "1", "bound")));
			}
			return new Function(((Function) e).parameter, alphaReduce(((Function) e).expression, v));
		} else if (e instanceof Variable) {
			if (((Variable) e).nameEquals(v) && ((Variable) e).type().equals("bound")) {
				return new Variable(v.toString() + "1", "bound");
			}
			return e;
		}

		return e;

	}

	public static ArrayList<Variable> getFreeVariables(Expression e) {
		ArrayList<Variable> variableNames = new ArrayList<>();

		if (e instanceof Variable && ((Variable) e).type().equals("free")) {
			variableNames.add((Variable) e);
		} else if (e instanceof Application) {
			variableNames.addAll(getFreeVariables(((Application) e).left));
			variableNames.addAll(getFreeVariables(((Application) e).right));
		}

		return variableNames;
	}

	// also known as "eat"
	public static Expression replace(Expression e, Variable swapOut, Expression input) {
		if (e instanceof Function) {
			if ((((Function) e).parameter).nameEquals(swapOut)) {

				return e;
			} else if (input instanceof Variable && ((((Function) e).parameter).nameEquals((Variable) input))) {
				return new Function(new Variable(((Function) e).parameter.toString() + "1",
						"parameter"), replace(((Function) e).expression, swapOut, input));
			} else {
				return new Function(((Function) e).parameter, replace(((Function) e).expression, swapOut, input));
			}
		} else if (e instanceof Application) {
			// System.out.println("In replace, exp is " + e);
			// System.out.println("In replace, exp is this before the aciton " + ((Application)e).left);
			// System.out.println("In replace, the left is " + (new Application(replace(((Application) e).left, swapOut, input), replace(((Application) e).right, swapOut, input))).left);
			return new Application(replace(((Application) e).left, swapOut, input),
					replace(((Application) e).right, swapOut, input));
		} else if (e instanceof Variable) {
			if (swapOut.nameEquals((Variable) e)) {
				return input;
			} else {
				return e;
			}
		} else {
			return null;
		}

	}

	public static Expression runNextRedex(Expression current) {
		if (current instanceof Application) {
			Application app = (Application) current;

			if (app.left instanceof Function) {
				// System.out.println("REDEX! " + current);
				ArrayList<Variable> freeVariables = getFreeVariables(current);
				if (freeVariables.size() > 0) {
					for (Variable v : freeVariables) {
						current = alphaReduce(current, v);
					}
				}
				Function func = (Function) app.left;
				return replace(func.expression, func.parameter, app.right);
				
			} else {
				Expression runLeft = runNextRedex(((Application)current).left);
				if (!((((Application)current).left).toString()).equals(runLeft.toString())) {
					// System.out.println("LEFT!");
					return new Application(runLeft, ((Application)current).right);
				} else {
					// System.out.println("RIGHT!!");

					Expression runRight = runNextRedex(((Application)current).right);
					if (!((((Application)current).right).toString()).equals(runRight.toString())) {
						return new Application(((Application)current).left, runRight);
					}
				}
			}
		}
		else if (current instanceof Function) {
			Expression expRun = runNextRedex(((Function) current).expression);
			if (!(((Function)current).expression).equals(expRun)) {
				return new Function(((Function)current).parameter, expRun);
			}
		}
		return current;

	}


	public static Expression run(Expression exp) {
		Expression current = exp;
		Expression run = runNextRedex(exp);
		while (!current.equals(run)) {
			// System.out.println("Current: " + current);
			current = run;
			// System.out.println("Run: " + run);

			run = runNextRedex(current);

		}



		return run;
	}

	public static Expression getDictionaryNames(Expression e) {
		if (dictionary.containsKey(e.toString())) {
			return new Variable(dictionary.get(e.toString()), "");
		} else if (e instanceof Application) {
			return new Application(getDictionaryNames(((Application) e).left),
					getDictionaryNames(((Application) e).right));
		} else if (e instanceof Function) {
			return new Function(new Variable(getDictionaryNames(((Function) e).parameter).toString(), "parameter"),
					getDictionaryNames(((Function) e).expression));
		} else {
			return e;
		}
	}

	public static void main(String[] args) {
		in = new Scanner(System.in);

		Lexer lexer = new Lexer();
		Parser parser = new Parser();

		String input = cleanConsoleInput();

		while (!input.equalsIgnoreCase("exit")) {

			ArrayList<String> tokens = lexer.tokenize(input);
			String output = "";
			if (tokens.size() != 0) {
				try {
					String first = tokens.get(0);
					Expression exp;
					if (isStoringValue(tokens)) {
						if (!parser.inDictionary(first)) {
							if (tokens.get(2).equals("run")) {
								exp = run(parser.parse(parser.subArrayList(tokens, 3, tokens.size())));
								dictionary.put(exp.toString(), first);
								parser.addToDictionary(first, exp);
							} else {
								exp = parser.addToDictionary(tokens);
								dictionary.put(exp.toString(), first);
							}
							System.out.printf("Added %s as %s.", parser.getDictValue(first).toString(), first);

						} else {
							System.out.printf("%s is already defined.", parser.getDictValue(first).toString());

						}
					} else {
						if (tokens.get(0).equals("run")) {
							// SAFE
							exp = run(parser.parse(parser.subArrayList(tokens, 1, tokens.size())));
						} else {
							exp = parser.parse(tokens);

						}
						// System.out.println("BEfore dict " + exp);
						output = exp.toString();

					}
				} catch (Exception e) {
					System.out.println("Unparsable expression, input was: \"" + input + "\"");
					input = cleanConsoleInput();
					continue;
				}

			}

			System.out.println(output);

			input = cleanConsoleInput();
		}
		System.out.println("Goodbye!");

	}

	/*
	 * Collects user input, and ...
	 * ... does a bit of raw string processing to (1) strip away comments,
	 * (2) remove the BOM character that appears in unicode strings in Windows,
	 * (3) turn all weird whitespace characters into spaces,
	 * and (4) replace all backslashes with λ.
	 */

	private static String cleanConsoleInput() {
		System.out.print("> ");
		String raw = in.nextLine();
		String deBOMified = raw.replaceAll("\uFEFF", ""); // remove Byte Order Marker from UTF

		String clean = removeWeirdWhitespace(deBOMified);

		if (deBOMified.contains(";")) {
			clean = deBOMified.substring(0, deBOMified.indexOf(";"));
		}

		return clean.replaceAll("\\\\", "λ");
	}

	public static String removeWeirdWhitespace(String input) {
		String whitespace_chars = "" /* dummy empty string for homogeneity */
				+ "\\u0009" // CHARACTER TABULATION
				+ "\\u000A" // LINE FEED (LF)
				+ "\\u000B" // LINE TABULATION
				+ "\\u000C" // FORM FEED (FF)
				+ "\\u000D" // CARRIAGE RETURN (CR)
				+ "\\u0020" // SPACE
				+ "\\u0085" // NEXT LINE (NEL)
				+ "\\u00A0" // NO-BREAK SPACE
				+ "\\u1680" // OGHAM SPACE MARK
				+ "\\u180E" // MONGOLIAN VOWEL SEPARATOR
				+ "\\u2000" // EN QUAD
				+ "\\u2001" // EM QUAD
				+ "\\u2002" // EN SPACE
				+ "\\u2003" // EM SPACE
				+ "\\u2004" // THREE-PER-EM SPACE
				+ "\\u2005" // FOUR-PER-EM SPACE
				+ "\\u2006" // SIX-PER-EM SPACE
				+ "\\u2007" // FIGURE SPACE
				+ "\\u2008" // PUNCTUATION SPACE
				+ "\\u2009" // THIN SPACE
				+ "\\u200A" // HAIR SPACE
				+ "\\u2028" // LINE SEPARATOR
				+ "\\u2029" // PARAGRAPH SEPARATOR
				+ "\\u202F" // NARROW NO-BREAK SPACE
				+ "\\u205F" // MEDIUM MATHEMATICAL SPACE
				+ "\\u3000"; // IDEOGRAPHIC SPACE
		Pattern whitespace = Pattern.compile(whitespace_chars);
		Matcher matcher = whitespace.matcher(input);
		String result = input;
		if (matcher.find()) {
			result = matcher.replaceAll(" ");
		}

		return result;
	}

}