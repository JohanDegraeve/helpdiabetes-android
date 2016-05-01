// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBMealFood {
	private long id;
	private String foodName;
	private float amount;
	private DBFoodUnit unit;
	
	public DBMealFood(long id, String food, float amount, DBFoodUnit unit) {
		super();
		this.id = id;
		this.foodName = food;
		this.amount = amount;
		this.unit = unit;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
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

	
	
}
