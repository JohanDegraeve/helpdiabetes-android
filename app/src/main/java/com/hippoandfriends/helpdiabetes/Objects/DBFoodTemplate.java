// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

import java.util.ArrayList;

public class DBFoodTemplate {
	private int id;
	private int mealTypeID;
	private int userID;
	private int visible;
	private String foodTemplateName;
	private ArrayList<DBFood> foods;

	public DBFoodTemplate(int id, int mealTypeID, int userID, int visible,
			String foodTemplateName, ArrayList<DBFood> foods) {
		super();
		this.id = id;
		this.mealTypeID = mealTypeID;
		this.userID = userID;
		this.visible = visible;
		this.foodTemplateName = foodTemplateName;
		this.foods = foods;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<DBFood> getFoods() {
		return foods;
	}

	public void setFoods(ArrayList<DBFood> foods) {
		this.foods = foods;
	}

	public int getMealTypeID() {
		return mealTypeID;
	}

	public void setMealTypeID(int mealTypeID) {
		this.mealTypeID = mealTypeID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public String getFoodTemplateName() {
		return foodTemplateName;
	}

	public void setFoodTemplateName(String foodTemplateName) {
		this.foodTemplateName = foodTemplateName;
	}

}
