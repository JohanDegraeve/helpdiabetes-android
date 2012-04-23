package be.goossens.oracle.Rest;

/*
 * This class is used to compare the food names with each other
 * it will first store the foodName in a temp String and remove the accent from that String. Then compare the String with another food name
 * and return the compared names
 * */

import java.util.Comparator;

import be.goossens.oracle.Objects.DBFoodComparable;

public class FoodComparator implements Comparator<DBFoodComparable> {

	public int compare(DBFoodComparable foodOne, DBFoodComparable foodTwo) {

		if (foodOne.getIsfavorite() > 0 && foodTwo.getIsfavorite() <= 0)
			return -1;
		else if (foodOne.getIsfavorite() <= 0 && foodTwo.getIsfavorite() > 0)
			return 1;
		else {
			String foodNameOne = removeAccents(foodOne.getName());
			String foodNameTwo = removeAccents(foodTwo.getName());
			 
			return foodNameOne.toLowerCase().compareTo(
					foodNameTwo.toLowerCase());
		}
	}

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
	;

	private static final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
			+ "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
			+ "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
			+ "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
			+ "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
			+ "\u00C5\u00E5" + "\u00C7\u00E7" + "\u0150\u0151\u0170\u0171";

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
