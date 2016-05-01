package com.hippoandfriends.helpdiabetes.Show.Tracking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Objects.DBBloodGlucoseEvent;
import com.hippoandfriends.helpdiabetes.Objects.DBExerciseEvent;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodUnit;
import com.hippoandfriends.helpdiabetes.Objects.DBMealEvent;
import com.hippoandfriends.helpdiabetes.Objects.DBMealFood;
import com.hippoandfriends.helpdiabetes.Objects.DBMedicineEvent;
import com.hippoandfriends.helpdiabetes.Objects.DBTracking;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TimeComparator;

public class ShowLoadingTrackingData extends Activity {
	// This boolean is used to see if we are finisht with gething our data from
	// the database
	public boolean finishedGethingData;

	// This context is used in the async tasks to create a dbadapter
	private Context context;

	// a list to hold the tracking data
	public List<DBTracking> listTracking;
	public List<DBBloodGlucoseEvent> listBloodGlucose;

	// used to hold the date where we got items from
	public Calendar calendarDate;

	// used to know if there are more items
	public boolean thereAreMoreItems;

	public Date currentDateMinutOneWeek;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View convertView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_start, null);
		setContentView(convertView);

		calendarDate = Calendar.getInstance();
		// do -1 month
		calendarDate.add(Calendar.MONTH, -1);

		listTracking = new ArrayList<DBTracking>();
		listBloodGlucose = new ArrayList<DBBloodGlucoseEvent>();

		context = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		currentDateMinutOneWeek = new Date();
		currentDateMinutOneWeek.setDate(currentDateMinutOneWeek.getDate() - 7);
		new AsyncGetTrackingList().execute();
	}

	private class AsyncGetTrackingList extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			loopTrueGethingData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// only do this the first time
			if (!finishedGethingData) {
				finishedGethingData = true;

				// create the tracking page
				Intent i = new Intent(context, ShowTracking.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				View v = ActivityGroupTracking.group.getLocalActivityManager()
						.startActivity(DataParser.activityIDTracking, i)
						.getDecorView();
				ActivityGroupTracking.group.setContentView(v);
			}
			super.onPostExecute(result);
		}
	}

	public void loopTrueGethingData() {
		// get current size of list tracking
		int currentSize = listTracking.size();

		// we try to get the data from current date - 1 month
		getData();

		// if for the current month is no data
		// AND there IS data in the history
		// we loop - 1 month until we found data
		while (listTracking.size() == currentSize && thereAreMoreItems) {
			calendarDate.add(Calendar.MONTH, -1);
			getData();
		}
	}

	private void getData() {
		DbAdapter dbHelper = new DbAdapter(context);
		dbHelper.open();
		List<Date> listDates = new ArrayList<Date>();
		Cursor cDates = dbHelper.fetchAllTimestamps();

		// fill listdates with all the dates
		if (cDates.getCount() > 0) {
			cDates.moveToFirst();
			do {
				Date tempDate = new Functions()
						.getYearMonthDayAsDateFromString(cDates.getString(cDates
								.getColumnIndexOrThrow(new DataParser().timestamp)));

				if ((tempDate.after(calendarDate.getTime()) || tempDate
						.equals(calendarDate.getTime()))) {
					thereAreMoreItems = false;
					listDates.add(tempDate);
				} else {
					// stop looping
					cDates.moveToLast();
					// if we get here there are more items so we flag the
					// boolean to show button " see more "
					thereAreMoreItems = true;
				}

			} while (cDates.moveToNext());
		}
		cDates.close();

		for (Date date : listDates) {
			// check if that date is already in the list
			if (checkIfDateIsAlreadyInList(date)) {
				// For each date object add it to the listview
				listTracking.add(new DBTracking(null, null, null, null, date,
						true, ""));

				// create a temporary list with all the events from the date so
				// we
				// can sort it by time
				List<DBTracking> listTempDBTracking = new ArrayList<DBTracking>();

				// For each date get the exercise events
				Cursor cExerciseEvents = dbHelper
						.fetchExerciseEventByDate(new Functions()
								.getYearMonthDayAsStringFromDate(date));
				if (cExerciseEvents.getCount() > 0) {
					cExerciseEvents.moveToFirst();
					do {
						Cursor cExerciseType = dbHelper
								.fetchExerciseTypeByID(cExerciseEvents.getLong(cExerciseEvents
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)));
						cExerciseType.moveToFirst();
						// new DBTracking(exerciseEvent, mealEvent,
						// bloodGlucoseEvent, medicineEvent, timestamp,
						// noRecors)
						listTempDBTracking
								.add(new DBTracking(
										new DBExerciseEvent(
												cExerciseEvents
														.getLong(cExerciseEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_ID)),
												cExerciseEvents
														.getString(cExerciseEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_DESCRIPTION)),
												cExerciseEvents.getInt(cExerciseEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STARTTIME)),
												cExerciseEvents.getInt(cExerciseEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STOPTIME)),
												cExerciseEvents
														.getString(cExerciseEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EVENTDATETIME)),
												cExerciseEvents
														.getLong(cExerciseEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)),
												cExerciseEvents
														.getLong(cExerciseEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_USERID)),
												cExerciseType
														.getString(cExerciseType
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME))),
										null,
										null,
										null,
										new Functions()
												.getYearMonthDayHourMinutesAsDateFromString(cExerciseEvents
														.getString(cExerciseEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EVENTDATETIME))),
										false, ""));

						cExerciseType.close();
					} while (cExerciseEvents.moveToNext());
				}
				cExerciseEvents.close();

				// For each date get the meal event
				Cursor cMealEvents = dbHelper
						.fetchMealEventsByDate(new Functions()
								.getYearMonthDayAsStringFromDate(date));

				if (cMealEvents.getCount() > 0) {
					cMealEvents.moveToFirst();
					do {
						List<DBMealFood> listDBMealFood = new ArrayList<DBMealFood>();
						Cursor cMealFood = dbHelper
								.fetchMealFoodByMealEventID(cMealEvents.getLong(cMealEvents
										.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_ID)));
						if (cMealFood.getCount() > 0) {
							cMealFood.moveToFirst();
							do {
								// fill the list with meal foods

								// to do so we first get the unit that belongs
								// to
								// this mealfood
								Cursor cUnit = dbHelper
										.fetchFoodUnit(cMealFood.getLong(cMealFood
												.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_FOODUNITID)));
								cUnit.moveToFirst();
								Cursor cFood = dbHelper
										.fetchFood(cUnit.getLong(cUnit
												.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
								cFood.moveToFirst();

								// fill cmealfood
								listDBMealFood
										.add(new DBMealFood(
												cMealFood
														.getLong(cMealFood
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_ID)),
												cFood.getString(cFood
														.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
												cMealFood.getFloat(cMealFood
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_AMOUNT)),
												new DBFoodUnit(
														cUnit.getLong(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)),
														cUnit.getString(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
														cUnit.getString(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_DESCRIPTION)),
														cUnit.getFloat(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)),
														cUnit.getFloat(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)),
														cUnit.getFloat(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)),
														cUnit.getFloat(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)),
														cUnit.getFloat(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)),
														cUnit.getFloat(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_VISIBLE)),
														cUnit.getInt(cUnit
																.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)))));

								cFood.close();
								cUnit.close();

							} while (cMealFood.moveToNext());
						}
						cMealFood.close();

						// new DBTracking(exerciseEvent, mealEvent,
						// bloodGlucoseEvent, medicineEvent, timestamp,
						// noRecors)
						listTempDBTracking
								.add(new DBTracking(
										null,
										new DBMealEvent(
												cMealEvents
														.getLong(cMealEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_ID)),
												cMealEvents.getFloat(cMealEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_INSULINERATIO)),
												cMealEvents.getFloat(cMealEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CORRECTIONFACTOR)),
												cMealEvents.getFloat(cMealEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT)),
												cMealEvents.getString(cMealEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME)),
												cMealEvents.getLong(cMealEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_USERID)),
												listDBMealFood),
										null,
										null,
										new Functions()
												.getYearMonthDayHourMinutesAsDateFromString(cMealEvents.getString(cMealEvents
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME))),
										false, ""));
					} while (cMealEvents.moveToNext());
				}
				cMealEvents.close();

				// For each date get the glucose event
				Cursor cGlucoseEvent = dbHelper
						.fetchBloodGlucoseEventByDate(new Functions()
								.getYearMonthDayAsStringFromDate(date));

				if (cGlucoseEvent.getCount() > 0) {
					cGlucoseEvent.moveToFirst();
					do {
						// get the unit
						Cursor cBGUnit = dbHelper
								.fetchBloodGlucoseUnitsByID(cGlucoseEvent.getLong(cGlucoseEvent
										.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_BGUNITID)));
						cBGUnit.moveToFirst();
						// new DBTracking(exerciseEvent, mealEvent,
						// bloodGlucoseEvent, medicineEvent, timestamp,
						// noRecors)
						listTempDBTracking
								.add(new DBTracking(
										null,
										null,
										new DBBloodGlucoseEvent(
												cGlucoseEvent
														.getLong(cGlucoseEvent
																.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_ID)),
												cGlucoseEvent.getFloat(cGlucoseEvent
														.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_AMOUNT)),
												cGlucoseEvent
														.getString(cGlucoseEvent
																.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME)),
												cGlucoseEvent.getLong(cGlucoseEvent
														.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_BGUNITID)),
												cGlucoseEvent.getLong(cGlucoseEvent
														.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_USERID)),
												cBGUnit.getString(cBGUnit
														.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_UNIT))),
										null,
										new Functions()
												.getYearMonthDayHourMinutesAsDateFromString(cGlucoseEvent
														.getString(cGlucoseEvent
																.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME))),
										false, ""));

						cBGUnit.close();
					} while (cGlucoseEvent.moveToNext());
				}

				// store the glucose in a seperated list so we can show it in
				// the chart
				// store only the last 7 days in the list
				if (cGlucoseEvent.getCount() > 0) {
					cGlucoseEvent.moveToFirst();

					Date glucoseDate = new Functions()
							.getYearMonthDayHourMinutesAsDateFromString(cGlucoseEvent.getString(cGlucoseEvent
									.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME)));

					if (glucoseDate.after(currentDateMinutOneWeek)) {
						// add to list
						listBloodGlucose
								.add(new DBBloodGlucoseEvent(
										0,
										cGlucoseEvent.getFloat(cGlucoseEvent
												.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_AMOUNT)),
										cGlucoseEvent.getString(cGlucoseEvent
												.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME)),
										0, 0, null));
					}

				}

				cGlucoseEvent.close();

				// For each date get the medicine event
				Cursor cMedicineEvent = dbHelper
						.fetchMedicineEventByDate(new Functions()
								.getYearMonthDayAsStringFromDate(date));
				if (cMedicineEvent.getCount() > 0) {
					cMedicineEvent.moveToFirst();

					// loop true all medicine events
					do {
						// get the medicine type
						Cursor cMedicineType = dbHelper
								.fetchMedicineTypesByID(cMedicineEvent.getLong(cMedicineEvent
										.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_MEDICINETYPEID)));
						cMedicineType.moveToFirst();
						// new DBTracking(exerciseEvent, mealEvent,
						// bloodGlucoseEvent,
						// medicineEvent, timestamp, noRecors)
						listTempDBTracking
								.add(new DBTracking(
										null,
										null,
										null,
										new DBMedicineEvent(
												cMedicineEvent
														.getLong(cMedicineEvent
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_ID)),
												cMedicineEvent
														.getFloat(cMedicineEvent
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_AMOUNT)),
												cMedicineEvent
														.getString(cMedicineEvent
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_TIMESTAMP)),
												cMedicineEvent.getLong(cMedicineEvent
														.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_MEDICINETYPEID)),
												cMedicineType
														.getString(cMedicineType
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME)),
												cMedicineType
														.getString(cMedicineType
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINEUNIT))),
										new Functions()
												.getYearMonthDayHourMinutesAsDateFromString(cMedicineEvent
														.getString(cMedicineEvent
																.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_TIMESTAMP))),
										false, ""));
						cMedicineType.close();
					} while (cMedicineEvent.moveToNext());
				}
				cMedicineEvent.close();

				// order the temp list on timestamp
				Collections.sort(listTempDBTracking, new TimeComparator());

				// add the temp list to the real list
				listTracking.addAll(listTracking.size(), listTempDBTracking);
			}
		}

		dbHelper.close();
	}

	private boolean checkIfDateIsAlreadyInList(Date date) {
		for (DBTracking dbTracking : listTracking) {
			if (dbTracking.getTimestamp() != null) {
				if (dbTracking.getTimestamp().equals(date))
					// returns false wont add the date to the list
					return false;
			}
		}

		// returns true will add the date to the list
		return true;
	}

}
