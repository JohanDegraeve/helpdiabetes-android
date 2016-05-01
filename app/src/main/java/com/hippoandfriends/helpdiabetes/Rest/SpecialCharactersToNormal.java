package com.hippoandfriends.helpdiabetes.Rest;

/*
 * This class will take a input from a string and returns the string without special characters
 */

public class SpecialCharactersToNormal {
	/*
	 * Special thanks to a guy posted on stackoverflow
	 * http://stackoverflow.com/questions
	 * /3211974/transforming-some-special-caracters-e-e-into-e
	 * 
	 * I used his code for removing special characters
	 */
	private static final String PLAIN_ASCII = "AaEeIiOoUu" // grave
			+ "AaEeIiOoUuYy" // acute
			+ "AaEeIiOoUuYy" // circumflex
			+ "AaOoNn" // tilde
			+ "AaEeIiOoUuYy" // umlaut
			+ "Aa" // ring
			+ "Cc" // cedilla
			+ "OoUu" // double acute
			+ "A" // for replacing Æ with A
			+ "O" // for replacing Ø  with O
	;

	private static final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9" // grave
			+ "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" // acute
			+ "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" // circumflex
			+ "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1" // tilde
			+ "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" // umlaut
			+ "\u00C5\u00E5" // ring
			+ "\u00C7\u00E7" // cedilla
			+ "\u0150\u0151\u0170\u0171" // double acute
			+ "\u00C6" // for replacing Æ
			+ "\u00D8" // for replacing Ø  with O
	;

	/**
	 * remove accented from a string and replace with ascii equivalent
	 */
	public static String removeAccents(String s) {
		if (s == null)
			return null;
		StringBuilder sb = new StringBuilder(s.length());
		int n = s.length();
		int pos = -1;
		char c;
		boolean found = false;
		for (int i = 0; i < n; i++) {
			pos = -1;
			c = s.charAt(i);
			pos = (c <= 126) ? -1 : UNICODE.indexOf(c);
			if (pos > -1) {
				found = true;
				sb.append(PLAIN_ASCII.charAt(pos));
			} else {
				sb.append(c);
			}
		}
		if (!found) {
			return s;
		} else {
			return sb.toString();
		}
	}
}
