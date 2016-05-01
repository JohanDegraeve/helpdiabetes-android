// Please read info.txt for license and legal information

/*  
 *  Copyright (C) 2009  Johan Degraeve
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 *    
 *  Please contact Johan Degraeve at johan.degraeve@johandegraeve.net if you need
 *  additional information or have any questions.
 */
package com.hippoandfriends.helpdiabetes.Rest;

/**
 * This class defines the static method compareToAsInExcel, which sorts
 * characters in the same way as Excel does.
 * 
 * @version 1.0
 * @author Johan Degraeve
 * 
 */
public class ExcelCharacter {

	/**
	 * Array used for comparing char values according to Excel rules. Element at
	 * index x, has the same value as in column 'Considered Equal to' in the
	 * table above, for the row with Ascii code x.<br>
	 * The last element has value '256', this is the value that will be used for
	 * any character which char-value out of range
	 */
	static private final char[] CHARORDER = {
			0,
			1,
			2,
			3,
			4,
			5,
			6,
			7,
			8,
			40,
			41,
			42,
			43,
			44,
			9,
			10,
			11,
			12,
			13,
			14,
			15,
			16,
			17,
			18,
			19,
			20,
			21,
			22,
			23,
			24,
			25,
			26,
			38,
			45,
			46,
			47,
			48,
			49,
			50,
			33,
			51,
			52,
			53,
			88,
			0,
			34,
			55,
			56,
			115,
			120,// 44 which is ascii code for , is being mapped to 0, this is
				// because , will never be shown in list, should not appear in
				// text, and only appears in byte range as delimiter between the
				// different fields
			122, 124, 125, 126, 127, 128, 129, 130, 57, 58, 89, 90, 91, 59, 60,
			147, 149, 153, 157, 167, 170, 172, 174, 184, 186, 188, 190, 192,
			196, 213, 215, 217, 219, 224, 229, 239, 241, 243, 245, 251, 255,
			61, 62, 63, 64, 66, 67, 147, 149, 153, 157, 167, 170, 172, 174,
			184, 186, 188, 190, 192, 196, 213, 215, 217, 219, 224, 229, 239,
			241, 243, 245, 251, 255, 68, 69, 70, 71, 27, 114, 28, 82, 170, 85,
			112, 109, 110, 65, 113, 224, 86, 213, 29, 255, 30, 31, 80, 81, 83,
			84, 111, 36, 37, 79, 229, 224, 87, 213, 32, 255, 251, 39, 72, 97,
			98, 99, 100, 73, 101, 74, 102, 147, 93, 103, 35, 104, 75, 105, 92,
			122, 124, 76, 106, 107, 108, 77, 120, 213, 94, 116, 117, 118, 78,
			147, 147, 147, 147, 147, 147, 147, 153, 167, 167, 167, 167, 184,
			184, 184, 184, 157, 196, 213, 213, 213, 213, 213, 95, 213, 239,
			239, 239, 239, 251, 229, 224, 147, 147, 147, 147, 147, 147, 147,
			153, 167, 167, 167, 167, 184, 184, 184, 184, 157, 196, 213, 213,
			213, 213, 213, 96, 213, 239, 239, 239, 239, 251, 229, 251, 256 };

	/**
	 * Compares two characters using Excel sorting rules.
	 * 
	 * @param characterA
	 *            the first character to be compared to
	 * @param characterB
	 *            the second character
	 * @return the value 0 if characterA is equal to characterB; ﻿ a value less
	 *         than 0 if characterA is numerically less than characterB; ﻿ and a
	 *         value greater than 0 if characterB is numerically greater than
	 *         characterA. ﻿ Note that this is a comparison of the value in
	 *         column 3 (considered equal to) in the table above.
	 * 
	 */
	static public int compareToAsInExcel(char characterA, char characterB) {
		if (characterA > (CHARORDER.length - 1)) // -1 becaus the very last
													// element is not really
													// considered to be a
													// character value
			characterA = CHARORDER[CHARORDER.length - 1];
		if (characterB > (CHARORDER.length - 1))
			characterB = CHARORDER[CHARORDER.length - 1];
		if (CHARORDER[characterA] < CHARORDER[characterB])
			return -1;
		if (CHARORDER[characterA] > CHARORDER[characterB])
			return 1;
		return 0;
	}
}
