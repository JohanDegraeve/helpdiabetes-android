// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

/*
 * This class is used to display the food,
 * amound and kcal in a listview
 * */

public class DBSelectedFood {
	private long id;
	private float amound;
	private String foodName;
	private DBFoodUnit unit;

	public DBSelectedFood(long id, float amound, String foodName,
			DBFoodUnit unit) {
		super();
		this.id = id;
		this.amound = amound;
		this.foodName = foodName;
		this.unit = unit;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getAmound() {
		return amound;
	}

	public void setAmound(float amound) {
		this.amound = amound;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public DBFoodUnit getUnit() {
		return unit;
	}

	public void setUnit(DBFoodUnit unit) {
		this.unit = unit;
	}

	public String toString() {
		return foodName;
	}

}
