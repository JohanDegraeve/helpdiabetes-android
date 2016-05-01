// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBMedicineType {
	private long id;
	private String medicineType;
	private String medicineName;
	private String medicineUnit;
	private int visible;

	public DBMedicineType(long id, String medicineType, String medicineName,
			String medicineUnit, int visible) {
		super();
		this.id = id;
		this.medicineType = medicineType;
		this.medicineName = medicineName;
		this.medicineUnit = medicineUnit;
		this.visible = visible;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMedicineType() {
		return medicineType;
	}

	public void setMedicineType(String medicineType) {
		this.medicineType = medicineType;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}

	public String getMedicineUnit() {
		return medicineUnit;
	}

	public void setMedicineUnit(String medicineUnit) {
		this.medicineUnit = medicineUnit;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

}
