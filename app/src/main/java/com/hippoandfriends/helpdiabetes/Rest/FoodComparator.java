// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Rest;

/*
 * This class is used to compare the food names with each other
 * it will first store the foodName in a temp String and remove the accent from that String. Then compare the String with another food name
 * and return the compared names
 * */

import java.util.Comparator;

import com.hippoandfriends.helpdiabetes.Objects.DBFoodComparable;


public class FoodComparator implements Comparator<DBFoodComparable> {

	public int compare(DBFoodComparable foodOne, DBFoodComparable foodTwo) {

		String foodNameOne = new SpecialCharactersToNormal().removeAccents(foodOne.getName());
		String foodNameTwo = new SpecialCharactersToNormal().removeAccents(foodTwo.getName());
		
		return foodNameOne.toLowerCase().compareTo(foodNameTwo.toLowerCase());
	}
 
	

}
