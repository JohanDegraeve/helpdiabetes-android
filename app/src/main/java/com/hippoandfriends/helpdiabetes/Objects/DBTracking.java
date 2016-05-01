// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

import java.util.Date;

public class DBTracking {
	private DBExerciseEvent exerciseEvent;
	private DBMealEvent mealEvent;
	private DBBloodGlucoseEvent bloodGlucoseEvent;
	private DBMedicineEvent medicineEvent;
	private Date timestamp;
	
	// This String is used when there are no records
	// This String will be displayed
	private String noRecors;
	
	// we need this record to see if we really need to show the timestamp or
	// just need it to order the objects by date
	private boolean showTimeStamp;

	public DBTracking(DBExerciseEvent exerciseEvent, DBMealEvent mealEvent,
			DBBloodGlucoseEvent bloodGlucoseEvent,
			DBMedicineEvent medicineEvent, Date timestamp,
			boolean showTimeStamp, String noRecors) {
		super();
		this.exerciseEvent = exerciseEvent;
		this.mealEvent = mealEvent;
		this.bloodGlucoseEvent = bloodGlucoseEvent;
		this.medicineEvent = medicineEvent;
		this.timestamp = timestamp;
		this.showTimeStamp = showTimeStamp;
		this.noRecors = noRecors;
	}

	public boolean getShowTimeStamp() {
		return showTimeStamp;
	}

	public void setShowTimeStamp(boolean showTimeStamp) {
		this.showTimeStamp = showTimeStamp;
	}

	public DBMedicineEvent getMedicineEvent() {
		return medicineEvent;
	}

	public void setMedicineEvent(DBMedicineEvent medicineEvent) {
		this.medicineEvent = medicineEvent;
	}

	public DBExerciseEvent getExerciseEvent() {
		return exerciseEvent;
	}

	public void setExerciseEvent(DBExerciseEvent exerciseEvent) {
		this.exerciseEvent = exerciseEvent;
	}

	public DBMealEvent getMealEvent() {
		return mealEvent;
	}

	public void setMealEvent(DBMealEvent mealEvent) {
		this.mealEvent = mealEvent;
	}

	public DBBloodGlucoseEvent getBloodGlucoseEvent() {
		return bloodGlucoseEvent;
	}

	public void setBloodGlucoseEvent(DBBloodGlucoseEvent bloodGlucoseEvent) {
		this.bloodGlucoseEvent = bloodGlucoseEvent;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getNoRecords() {
		return noRecors;
	}

	public void setNoRecors(String noRecors) {
		this.noRecors = noRecors;
	}

}
