package com.hippoandfriends.helpdiabetes.Objects;

public class DBDoubleAndDouble {
	private Double valueX;
	private Double valueY;

	public Double getValueX() {
		return valueX;
	}

	public void setValueX(Double valueX) {
		this.valueX = valueX;
	}

	public Double getValueY() {
		return valueY;
	}

	public void setValueY(Double valueY) {
		this.valueY = valueY;
	}

	public DBDoubleAndDouble(Double valueX, Double valueY) {
		super();
		this.valueX = valueX;
		this.valueY = valueY;
	}

}
