package com.hippoandfriends.helpdiabetes.Objects;

public class DBTotalCalculated {
	private float calculatedValue;
	// use to know if we have carb, prot, fat or kcal
	// carb = 1, prot = 2, fat = 3, kcal = 4
	private int valueNumber;
	private String valueText;

	public DBTotalCalculated(float calculatedValue, int valueNumber,
			String valueText) {
		super();
		this.calculatedValue = calculatedValue;
		this.valueNumber = valueNumber;
		this.valueText = valueText;
	}

	public float getCalculatedValue() {
		return calculatedValue;
	}

	public void setCalculatedValue(float calculatedValue) {
		this.calculatedValue = calculatedValue;
	}

	public int getValueNumber() {
		return valueNumber;
	}

	public void setValueNumber(int valueNumber) {
		this.valueNumber = valueNumber;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

}
