// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBMedicineEvent {

	private long id;
	private float amount;
	private String timeStamp;
	private long medicineTypeId;

	// medicine type stuff
	private String medicineTypeName;
	private String medicineTypeUnit;

	public DBMedicineEvent(long id, float amount, String timeStamp,
			long medicineTypeId, String medicineTypeName,
			String medicineTypeUnit) {
		super();
		this.id = id;
		this.amount = amount;
		this.timeStamp = timeStamp;
		this.medicineTypeId = medicineTypeId;
		this.medicineTypeName = medicineTypeName;
		this.medicineTypeUnit = medicineTypeUnit;
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

	public long getMedicineTypeId() {
		return medicineTypeId;
	}

	public void setMedicineTypeId(long medicineTypeId) {
		this.medicineTypeId = medicineTypeId;
	}

	public String getMedicineTypeName() {
		return medicineTypeName;
	}

	public void setMedicineTypeName(String medicineTypeName) {
		this.medicineTypeName = medicineTypeName;
	}

	public String getMedicineTypeUnit() {
		return medicineTypeUnit;
	}

	public void setMedicineTypeUnit(String medicineTypeUnit) {
		this.medicineTypeUnit = medicineTypeUnit;
	}
}
