// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterCharSequenceSettings;
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
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;
import com.hippoandfriends.helpdiabetes.Show.Exercise.ShowExerciseTypes;
import com.hippoandfriends.helpdiabetes.Show.Medicine.ShowMedicineTypes;

public class ShowSettings extends ListActivity {

	String versionName = "";
	private Context context;
	private List<DBTracking> listTracking;
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// track we come here
		ActivityGroupSettings.group.parent
				.trackPageView(TrackingValues.pageShowSettingTab);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_settings, null);
		setContentView(contentView);

		context = this;
		listTracking = new ArrayList<DBTracking>();
		pd = new ProgressDialog(this);
		
		try {
			versionName = "Versie "
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();
	}

	public void fillListView() {
		setListAdapter(null);

		CustomArrayAdapterCharSequenceSettings adapter = new CustomArrayAdapterCharSequenceSettings(
				this, R.layout.row_custom_array_adapter_charsequence_settings,
				getCharSequenceList());

		setListAdapter(adapter);
	}

	private List<CharSequence> getCharSequenceList() {
		List<CharSequence> value = new ArrayList<CharSequence>();

		value.add(getResources().getString(R.string.pref_meal_times));
		value.add(getResources().getString(R.string.pref_insuline_ratio));
		value.add(getResources().getString(R.string.pref_glucose_unit));
		value.add(getResources().getString(R.string.pref_value_order));
		value.add(getResources().getString(R.string.pref_text_size));
		value.add(getResources().getString(R.string.pref_exercise_types));
		value.add(getResources().getString(R.string.pref_medicine_types));
		value.add(getResources().getString(R.string.pref_db_language));
		value.add(getResources().getString(R.string.pref_backup));
		value.add(getResources().getString(R.string.pref_log_to_excel));
		value.add(getResources().getString(R.string.unhide_hidden_food));
		value.add(getResources().getString(R.string.pref_about) + " ("
				+ versionName + ")");

		return value;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = null;
		switch (position) {
		case 0:
			i = new Intent(getApplicationContext(), ShowSettingsMealTimes.class);
			break;
		case 1:
			i = new Intent(getApplicationContext(),
					ShowSettingsInsulineRatio.class);
			break;
		case 2:
			i = new Intent(getApplicationContext(),
					ShowSettingsGlucoseUnit.class);
			break;
		case 3:
			i = new Intent(getApplicationContext(), ShowSettingsValue.class);
			break;
		case 4:
			i = new Intent(getApplicationContext(),
					ShowSettingsFontSizeLists.class);
			break;
		case 5:
			i = new Intent(getApplicationContext(), ShowExerciseTypes.class);
			break;
		case 6:
			i = new Intent(getApplicationContext(), ShowMedicineTypes.class);
			break;
		case 7:
			i = new Intent(getApplicationContext(),
					ShowSettingsDBLanguage.class);
			break;
		case 8:
			i = new Intent(getApplicationContext(), ShowSettingsBackup.class);
			break;
		case 9:
			showProgressDialog(getResources().getString(R.string.loading));
			new AsyncCreateExcelFile().execute();
			break;
		case 10:
			showPopUpToUnhideFood();
			break;
		case 11:
			showPopUpAbout();
			break;
		}

		if (i != null) {
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			View view = ActivityGroupSettings.group.getLocalActivityManager()
					.startActivity("ShowSetting", i).getDecorView();

			ActivityGroupSettings.group.setContentView(view);
		}

	}

	// when we click on the "about" in the listview
	private void showPopUpAbout() {
		try {// track we come here
			ActivityGroupSettings.group.parent.trackEvent(
					TrackingValues.eventCategorySettings,
					TrackingValues.eventCategorySettingsAbout);
		} catch (NullPointerException e) {
		}

		// Show a dialog with info
		new AlertDialog.Builder(ActivityGroupSettings.group)
				.setTitle(getResources().getString(R.string.pref_about))
				.setNeutralButton(getResources().getString(R.string.oke), null)
				.setMessage(
						versionName
								+ " \n\n"
								+ getResources().getString(
										R.string.about_text_copyright)
								+ " \n\n"
								+ getResources().getString(R.string.about_text))
				.show();
	}

	//if we press the unhide food list item
	private void showPopUpToUnhideFood(){
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					//trigger to unhide all the hidden food
					showProgressDialog(getResources().getString(R.string.loading));
					new AsyncUnhideHiddenFood().execute();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupSettings.group);
		builder.setMessage(getResources().getString(R.string.sure_to_unhide_hidden_food))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}
	
	// if we press the back button on this activity we have to show a popup to
	// exit
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showPopUpToExitApplication();
			// when we return true here we wont call the onkeydown from
			// activitygroupmeal
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
	
	private void showPopUpToExitApplication() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// exit application on click button positive
					ActivityGroupSettings.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupSettings.group);
		builder.setMessage(getResources().getString(R.string.sureToExit))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}

	// method used to unhide all hidden food
	private class AsyncUnhideHiddenFood extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... arg0) {
			//trigger sql to unhide hidden food
			DbAdapter dbHelper = new DbAdapter(context);
			dbHelper.open();
			dbHelper.updateAllFoodSetVisible();
			return null;
		}
		
		protected void onPostExecute(String result) {
			pd.dismiss();
			showPopUpToExit();
	};
		
	}
	
	private void showPopUpToExit() {
		new AlertDialog.Builder(ActivityGroupMeal.group)
				.setTitle(getResources().getString(R.string.warning))
				.setMessage(getResources().getString(R.string.app_will_exit))
				.setPositiveButton(getResources().getString(R.string.oke),
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// exit application
								ActivityGroupMeal.group.killApplication();
							}
						}).show();
	}
	
	// //////////////For log to excel
	// method used to get all history data and create excel file.
	private class AsyncCreateExcelFile extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			listTracking = new ArrayList<DBTracking>();

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

					listDates.add(tempDate);

				} while (cDates.moveToNext());
			}
			cDates.close();

			for (Date date : listDates) {
				// check if that date is already in the list
				if (checkIfDateIsAlreadyInList(date)) {
					// For each date object add it to the listview
					listTracking.add(new DBTracking(null, null, null, null,
							date, true, ""));

					// create a temporary list with all the events from the date
					// so
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
													cExerciseEvents
															.getInt(cExerciseEvents
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STARTTIME)),
													cExerciseEvents
															.getInt(cExerciseEvents
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

									// to do so we first get the unit that
									// belongs
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
													cMealEvents
															.getFloat(cMealEvents
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_INSULINERATIO)),
													cMealEvents
															.getFloat(cMealEvents
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CORRECTIONFACTOR)),
													cMealEvents
															.getFloat(cMealEvents
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT)),
													cMealEvents
															.getString(cMealEvents
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME)),
													cMealEvents
															.getLong(cMealEvents
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_USERID)),
													listDBMealFood),
											null,
											null,
											new Functions()
													.getYearMonthDayHourMinutesAsDateFromString(cMealEvents
															.getString(cMealEvents
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
													cGlucoseEvent
															.getFloat(cGlucoseEvent
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_AMOUNT)),
													cGlucoseEvent
															.getString(cGlucoseEvent
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME)),
													cGlucoseEvent
															.getLong(cGlucoseEvent
																	.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_BGUNITID)),
													cGlucoseEvent
															.getLong(cGlucoseEvent
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
													cMedicineEvent
															.getLong(cMedicineEvent
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
					listTracking
							.addAll(listTracking.size(), listTempDBTracking);
				}
			}
			// end of getting data

			// write excel file
			File sd = Environment.getExternalStorageDirectory();
			if (sd.canWrite()) {
				try {
					WritableWorkbook workbook = Workbook
							.createWorkbook(new File(sd,
									"HelpDiabetesExcel.xls"));
					WritableSheet sheet = workbook.createSheet("HelpDiabetes",
							0);
					
					//set standard stuff				
					sheet.addCell(new Label(0, 0, "Date"));
					sheet.addCell(new Label(1, 0, "Time"));
					sheet.addCell(new Label(2, 0, "Event Type"));
					sheet.addCell(new Label(3, 0, "Event Description"));
					sheet.addCell(new Label(4, 0, "Param 1"));
					sheet.addCell(new Label(5, 0, "Param 1 unit"));
					sheet.addCell(new Label(6, 0, "Param 2"));
					sheet.addCell(new Label(7, 0, "Param 2 unit"));
					sheet.addCell(new Label(8, 0, "Param 3"));
					sheet.addCell(new Label(9, 0, "Param 3 unit"));
					sheet.addCell(new Label(10, 0, "Param 4"));
					sheet.addCell(new Label(11, 0, "Param 4 unit"));
					
					//which row we write on
					int row = 1; 
					
					WritableCellFormat wrapFormat = new WritableCellFormat();
					wrapFormat.setWrap(true);
					
					for (DBTracking obj : listTracking) {
						try {
							//get string in right format as date
							String sDate = android.text.format.DateFormat
									.getDateFormat(context).format(obj.getTimestamp());
							
							if(obj.getMealEvent() != null){
								//if we have a mealevent
								
								//get everything we need
								//get a string with list of food seperated with a "\n"
								String listOfFood = "";
								
								//get total carbs, fat, prot, kcal
								float totalKcal = 0;
								float totalCarbs = 0;
								float totalProt = 0;
								float totalFat = 0;							
								
								for(DBMealFood mealFood : obj.getMealEvent().getMealFood()){
									listOfFood += mealFood.getFoodName() + " \n";
									 
									totalKcal += ((mealFood.getAmount() / mealFood.getUnit().getStandardamound()) * mealFood.getUnit().getKcal());
									totalCarbs += ((mealFood.getAmount() / mealFood.getUnit().getStandardamound()) * mealFood.getUnit().getCarbs());
									totalProt += ((mealFood.getAmount() / mealFood.getUnit().getStandardamound()) * mealFood.getUnit().getProtein());
									totalFat += ((mealFood.getAmount() / mealFood.getUnit().getStandardamound()) * mealFood.getUnit().getFat());
									 
								}
								
								//remove last "\n" from the string
								listOfFood =  listOfFood.substring(0,listOfFood.length() - 1);
								
								//add everything to the excel file
								//add date on first column
								sheet.addCell(new Label(0, row, sDate));
								
								//add time on column 2
								sheet.addCell(new Label(1, row, new Functions().getTimeFromString(obj.getMealEvent().getEventDateTime())));
								
								//add event type "meal" on column 3
								sheet.addCell(new Label(2, row, "meal"));
								
								//add the food
								sheet.addCell(new Label(3, row, listOfFood, wrapFormat));								
								
								//add total kh
								sheet.addCell(new Label(4, row, "" + totalCarbs));
								sheet.addCell(new Label(5, row, getResources().getString(R.string.amound_of_carbs)));
								 
								//add total kh
								sheet.addCell(new Label(6, row, "" + totalProt));
								sheet.addCell(new Label(7, row, getResources().getString(R.string.amound_of_protein)));
								
								//add total kh
								sheet.addCell(new Label(8, row, "" + totalFat));
								sheet.addCell(new Label(9, row, getResources().getString(R.string.amound_of_fat)));
								
								//add total kh
								sheet.addCell(new Label(10, row, "" + totalKcal));
								sheet.addCell(new Label(11, row, getResources().getString(R.string.amound_of_kcal)));
								
								row++;
							} else if(obj.getMedicineEvent() != null){
								//if we have a medicine event
								
								//add everything to the excel file
								//add date on first column
								sheet.addCell(new Label(0, row, sDate));
								
								//add time on column 2
								sheet.addCell(new Label(1, row, new Functions().getTimeFromString(obj.getMedicineEvent().getTimeStamp())));
								
								//add event type "medicine" on column 3
								sheet.addCell(new Label(2, row, "medicine"));
								
								//add the medicine
								sheet.addCell(new Label(3, row, obj.getMedicineEvent().getMedicineTypeName()));
								
								//add the amount 
								sheet.addCell(new Label(4, row, "" + obj.getMedicineEvent().getAmount()));
								
								//add the unit
								sheet.addCell(new Label(5, row, obj.getMedicineEvent().getMedicineTypeUnit()));
								
								row++;
							} else if(obj.getBloodGlucoseEvent() != null){
								//if we have a bloodglucose event
								
								//add everything to the excel file
								//add date on first column
								sheet.addCell(new Label(0, row, sDate));
								
								//add time on column 2
								sheet.addCell(new Label(1, row, new Functions().getTimeFromString(obj.getBloodGlucoseEvent().getTimeStamp())));
								
								//add event type "medicine" on column 3
								sheet.addCell(new Label(2, row, "glucose"));

								//leave column 3 empty
								
								//add the amount 
								sheet.addCell(new Label(4, row, "" + obj.getBloodGlucoseEvent().getAmount()));
								
								//add the unit
								sheet.addCell(new Label(5, row, obj.getBloodGlucoseEvent().getUnit()));
								
								row ++;
							} else if (obj.getExerciseEvent() != null){
							 	//if we have a exercise event
								
								//add everything to the excel file
								//add date on first column
								sheet.addCell(new Label(0, row, sDate));
								
								//add time on column 2
								sheet.addCell(new Label(1, row, new Functions().getTimeFromString(obj.getExerciseEvent().getTimeStamp())));
								
								//add event type "medicine" on column 3
								sheet.addCell(new Label(2, row, "exercise"));
								
								//add the medicine
								sheet.addCell(new Label(3, row, obj.getExerciseEvent().getDescription()));
								
								//add the amount 
								sheet.addCell(new Label(4, row, "" + "" + (obj.getExerciseEvent().getEndTime() / 60)));
								
								//add the unit
								sheet.addCell(new Label(5, row, getResources().getString(R.string.minutes)));
								
								row ++;
							}
						} catch (Exception e) {
							sheet.addCell(new Label(0, row, e.toString()));
							row++;
						}
					}
					
					//set column widhts
					sheet.setColumnView(0, 10);
					sheet.setColumnView(1, 6);
					sheet.setColumnView(2, 10);
					sheet.setColumnView(3, 25);
					sheet.setColumnView(4, 15);
					sheet.setColumnView(5, 15);
					sheet.setColumnView(6, 15);
					sheet.setColumnView(7, 15);
					sheet.setColumnView(8, 15);
					sheet.setColumnView(9, 15);
					sheet.setColumnView(10, 15);
					sheet.setColumnView(11, 15);
					
					workbook.write();
					workbook.close();
					
					return "HelpDiabetesExcel.xls " + getResources().getString(R.string.created);
				} catch (Exception e) {
					return getResources().getString(R.string.could_not_write); 
				}
			} else {
				return getResources().getString(R.string.could_not_write);
			}
		}

		protected void onPostExecute(String result) {
				pd.dismiss();
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		};

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
	
	private void showProgressDialog(String text) {
		pd = ProgressDialog.show(ActivityGroupSettings.group, "", text, true);
	}
}

