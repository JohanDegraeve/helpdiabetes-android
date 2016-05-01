// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBBloodGlucoseEvent {
	private long id;
	private float amount;
	private String timeStamp;
	private long bgUnitID;
	private long userID;
	private String unit;

	public DBBloodGlucoseEvent(long id, float amount, String timeStamp,
			long bgUnitID, long userID, String unit) {
		super();
		this.id = id;
		this.amount = amount;
		this.timeStamp = timeStamp;
		this.bgUnitID = bgUnitID;
		this.userID = userID;
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
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

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getBgUnitID() {
		return bgUnitID;
	}

	public void setBgUnitID(long bgUnitID) {
		this.bgUnitID = bgUnitID;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

}
