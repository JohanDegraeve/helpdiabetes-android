// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

import java.util.List;

public class DBMealEvent {
	private long id;
	private float insulineRatio;
	private float corretionFactor;
	private float calculatedInsulineAmount;
	private String eventDateTime;
	private long userID;
	
	//a list of mealfood
	private List<DBMealFood> mealFood;

	public DBMealEvent(long id, float insulineRatio, float corretionFactor,
			float calculatedInsulineAmount, String eventDateTime, long userID,
			List<DBMealFood> mealFood) {
		super();
		this.id = id;
		this.insulineRatio = insulineRatio;
		this.corretionFactor = corretionFactor;
		this.calculatedInsulineAmount = calculatedInsulineAmount;
		this.eventDateTime = eventDateTime;
		this.userID = userID;
		this.mealFood = mealFood;
	}

	public List<DBMealFood> getMealFood() {
		return mealFood;
	}

	public void setMealFood(List<DBMealFood> mealFood) {
		this.mealFood = mealFood;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getInsulineRatio() {
		return insulineRatio;
	}

	public void setInsulineRatio(float insulineRatio) {
		this.insulineRatio = insulineRatio;
	}

	public float getCorretionFactor() {
		return corretionFactor;
	}

	public void setCorretionFactor(float corretionFactor) {
		this.corretionFactor = corretionFactor;
	}

	public float getCalculatedInsulineAmount() {
		return calculatedInsulineAmount;
	}

	public void setCalculatedInsulineAmount(float calculatedInsulineAmount) {
		this.calculatedInsulineAmount = calculatedInsulineAmount;
	}

	public String getEventDateTime() {
		return eventDateTime;
	}

	public void setEventDateTime(String eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

}
