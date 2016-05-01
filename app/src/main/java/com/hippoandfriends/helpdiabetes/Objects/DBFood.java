// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;


public class DBFood {
	private long id;
	private String name;
	private String amount;
	
	//stores the selected unit for the tracking page
	private DBFoodUnit unit;

	public DBFood(long id, String name, String amount, DBFoodUnit unit) {
		super();
		this.id = id;
		this.name = name;
		this.amount = amount;
		this.unit = unit;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public DBFoodUnit getUnit() {
		return unit;
	}

	public void setUnit(DBFoodUnit unit) {
		this.unit = unit;
	}

}
