// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

/*
 * This class shows the selected food
 * */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Custom.CustomBaseAdapterSelectedFood;
import com.hippoandfriends.helpdiabetes.Custom.CustomExpandableListAdapter;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodUnit;
import com.hippoandfriends.helpdiabetes.Objects.DBSelectedFood;
import com.hippoandfriends.helpdiabetes.Objects.DBTotalCalculated;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;
import com.hippoandfriends.helpdiabetes.Show.ShowHomeTab;
import com.hippoandfriends.helpdiabetes.slider.DateSlider;
import com.hippoandfriends.helpdiabetes.slider.DateTimeSlider;

public class ShowSelectedFood extends ListActivity {
	private DbAdapter dbHelper;

	private List<DBSelectedFood> listOfSelectedFood;

	private boolean saveFoodAmount;
	private String templateName;

	private float fInsulineRatio;
	private float fCorrectionFactor;
	private float fCalculatedInsulineAmount;

	private Button btDelete, btSaveTemplate, btLoadTemplate,
			btAddSelectedFoodToTracking, btBack;

	private CustomExpandableListAdapter adapter;
	private ExpandableListView expandableListview;

	private Functions functions;

	private Calendar mCalendar;
	// if we pressed the change date button
	private boolean changeDate;

	// to write to the database when listview expand or collaps
	private DbAdapter expandOrNotDBAdapater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_selected_food, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupMeal.group.parent
				.trackPageView(TrackingValues.pageShowSelectedFood);

		expandOrNotDBAdapater = new DbAdapter(this);
		expandOrNotDBAdapater.open();

		functions = new Functions();

		mCalendar = Calendar.getInstance();

		btDelete = (Button) findViewById(R.id.buttonDelete);
		btSaveTemplate = (Button) findViewById(R.id.buttonSaveAsTemplate);
		btLoadTemplate = (Button) findViewById(R.id.buttonLoadTemplate);
		btAddSelectedFoodToTracking = (Button) findViewById(R.id.buttonAddSelectedFoodToTracking);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupMeal.group.back();
			}
		});

		expandableListview = (ExpandableListView) findViewById(R.id.expandableListview);

		saveFoodAmount = false;

		fInsulineRatio = 0f;
		fCorrectionFactor = 0f;
		fCalculatedInsulineAmount = 0f;

		dbHelper = new DbAdapter(this);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		adapter = new CustomExpandableListAdapter(this,
				ActivityGroupMeal.group.getFoodData().dbFontSize,
				metrics.densityDpi);

		listOfSelectedFood = new ArrayList<DBSelectedFood>();
		registerForContextMenu(getListView());

		btAddSelectedFoodToTracking.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAddSelectedFoodToTracking();
			}
		});

		btDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryMeal,
						TrackingValues.eventCategoryMealDeleteSelectedFood);

				onClickDeleteAll(v);
			}
		});

		btSaveTemplate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryMeal,
						TrackingValues.eventCategoryMealSaveTemplate);

				onClickSaveAsTemplate(v);
			}
		});

		btLoadTemplate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryMeal,
						TrackingValues.eventCategoryMealLoadTemplate);

				onClickLoadTemplate(v);
			}
		});

		expandableListview
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
						setExpand(1);
					}
				});

		expandableListview
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					public void onGroupCollapse(int groupPosition) {
						setExpand(0);
					}
				});
	}

	private void setExpand(int value) {
		expandOrNotDBAdapater.open();
		expandOrNotDBAdapater.updateSettingsByName(
				DbSettings.setting_expand_listview, "" + value);
	}

	@Override
	protected void onDestroy() {
		expandOrNotDBAdapater.close();
		super.onDestroy();
	}

	// This method is called from the save button in the popup when we try to
	// add selected food to tracking list
	private void createMedicineEvent() {
		dbHelper.open();

		// if changeDate = true we take mCalendar as time
		if (changeDate) {
			// do calendar time -1 second so it comes below the meal food!
			mCalendar.add(Calendar.SECOND, +1);

			// medicine id = the medicine id that is default from settings!
			dbHelper.createMedicineEvent(fCalculatedInsulineAmount,
					new Functions().getDateAsStringFromCalendar(mCalendar),
					ActivityGroupMeal.group.getFoodData().defaultMedicineTypeID);
		} else {
			// otherwise we take the current time
			// medicine id = the medicine id that is default from settings!
			dbHelper.createMedicineEvent(fCalculatedInsulineAmount,
					new Functions().getDateAsStringFromCalendar(Calendar
							.getInstance()), ActivityGroupMeal.group
							.getFoodData().defaultMedicineTypeID);
		}

	}

	private void addSelectedFoodToTracking() {
		dbHelper.open();
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();

		// only create objects if we have something in the selectedFood list
		if (cSelectedFood.getCount() > 0) {
			// create a meal event
			long lMealEventID = dbHelper.createMealEvent(fInsulineRatio,
					fCorrectionFactor, fCalculatedInsulineAmount,
					new Functions().getDateAsStringFromCalendar(mCalendar));

			// create for every food in the selectedFood a mealFood object
			cSelectedFood.moveToFirst();
			do {
				// get the foodUnit
				Cursor cUnit = dbHelper
						.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
				// move the foodUnit to the first object in the cursor
				cUnit.moveToFirst();

				// create the mealFood
				dbHelper.createMealFood(
						lMealEventID,
						cUnit.getLong(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)),
						cSelectedFood.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)));

				// close the foodUnit cursor
				cUnit.close();
			} while (cSelectedFood.moveToNext());
		}

		cSelectedFood.close();

		// delete selected food from selectedFood table in database
		deleteSelectedFood();

		// setlist adapter = null
		setListAdapter(null);

		// refresh data
		refreshData();
	}

	private void goToTrackingWhenFoodAddedToTracking() {
		// kill this activity
		ActivityGroupMeal.group.back();

		// set selectedFoodCount = 0
		ActivityGroupMeal.group.getFoodData().countSelectedFood = 0;

		// reset the tracking activity
		ActivityGroupTracking.group.restartThisActivity();

		// Go to tracking activity
		ShowHomeTab parentActivity;
		parentActivity = (ShowHomeTab) this.getParent().getParent();
		parentActivity.goToTab(DataParser.activityIDTracking);
	}

	// Click on button add selected food to tracking
	private void onClickAddSelectedFoodToTracking() {
		// if we have at leat 1 food item in the list
		if (listOfSelectedFood.size() > 0) {
			showPopUpToAddFoodToTrackingList();
		} else {
			makeToastSelectionsAreEmpty();
		}
	}

	private void showPopUpToAddFoodToTrackingList() {
		TextView tv = new TextView(this);
		tv.setText(""
				+ android.text.format.DateFormat.getDateFormat(this).format(
						mCalendar.getTime()) + " "
				+ functions.getTimeFromDate(mCalendar.getTime()));

		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setTextColor(getResources().getColor(R.color.ColorWhite));

		// Show a dialog
		new AlertDialog.Builder(ActivityGroupMeal.group)
				.setTitle(
						getResources().getString(
								R.string.addSelectionToTracking))
				.setView(tv)
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click positive button
								if (fInsulineRatio != 0) {
									// track we come here
									ActivityGroupMeal.group.parent
											.trackEvent(
													TrackingValues.eventCategoryMeal,
													TrackingValues.eventCategoryMealAddToTracking);

									// to hold the calculated insuline amount
									// addseletedfoodtotracking will set it back
									// to 0!
									Float tempCalculatedInsulineAmount = fCalculatedInsulineAmount;

									// add food to tracking
									addSelectedFoodToTracking();

									// mark tracking page for refresh
									// ActivityGroupTracking.group.getShowTracking().refresh
									// = true;

									// show medicine event
									showPopUpToCreateMedicineEvent(tempCalculatedInsulineAmount);
								} else {
									// else just add the food without
									// creating medicine event or a
									// popup
									addSelectedFoodToTracking();
									// then go to the tracking page
									goToTrackingWhenFoodAddedToTracking();
								}

							}
						})
				.setNeutralButton(
						getResources().getString(R.string.changeTime),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								showDialog(0);
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click negative button do nothing
							}
						}).show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new DateTimeSlider(ActivityGroupMeal.group,
				mDateTimeSetListener, mCalendar);
	}

	private DateSlider.OnDateSetListener mDateTimeSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			// track we come here
						ActivityGroupMeal.group.parent.trackEvent(
								TrackingValues.eventCategoryMeal,
								TrackingValues.eventCategoryMealchangeTimeInSelectedFoodPopUp);
			
			// flag changeDate = true so the medicine event takes the mCalendar
			// date
			changeDate = true;
			// every time we clicked on the date slider
			mCalendar = selectedDate;
			onClickAddSelectedFoodToTracking();
		}
	};

	private void makeToastSelectionsAreEmpty() {
		// show toast we cant add a empty list
		Toast.makeText(this,
				getResources().getString(R.string.selections_are_empty),
				Toast.LENGTH_LONG).show();
	}

	private void showPopUpToCreateMedicineEvent(Float insulineAmount) {
		// this will be the titel of the popup
		String medicineTitle = "";

		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cSettingDefaultMedicineID = db
				.fetchSettingByName(DbSettings.setting_default_medicine_type_ID);
		cSettingDefaultMedicineID.moveToFirst();
		Cursor cMedicineType = db
				.fetchMedicineTypesByID(cSettingDefaultMedicineID.getLong(cSettingDefaultMedicineID
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		cMedicineType.moveToFirst();
		medicineTitle = getResources().getString(R.string.howMuch)
				+ " "
				+ cMedicineType
						.getString(cMedicineType
								.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINEUNIT))
				+ " "
				+ cMedicineType
						.getString(cMedicineType
								.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME))
				+ " " + getResources().getString(R.string.didUUse);
		cMedicineType.close();
		cSettingDefaultMedicineID.close();
		db.close();

		final EditText input = new EditText(this);

		// set edittext to the calculated insuline amount
		input.setText("" + insulineAmount);

		input.setInputType(InputType.TYPE_CLASS_PHONE);

		// Show a dialog with a inputbox to insert the template name
		new AlertDialog.Builder(ActivityGroupMeal.group)
				.setTitle(medicineTitle)
				.setView(input)
				.setPositiveButton(getResources().getString(R.string.add),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// track we come here
								ActivityGroupMeal.group.parent.trackEvent(
										TrackingValues.eventCategoryMeal,
										TrackingValues.eventCategoryMealAddMedicineToTrackingFromSelectedFood);
								
								// on click positive button
								try {
									// set calculated insuline amount
									fCalculatedInsulineAmount = Float
											.parseFloat(input.getText()
													.toString());
								} catch (Exception e) {
									fCalculatedInsulineAmount = 0f;
								}
								createMedicineEvent();
								goToTrackingWhenFoodAddedToTracking();
							}
						})
				.setNegativeButton(
						getResources().getString(R.string.dont_add_medicine),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click negative button
								goToTrackingWhenFoodAddedToTracking();
							}
						}).show();

	}

	// converts the cursor with all selected food to a arrayList<DBSelectedFood>
	// and returns the arraylist
	private ArrayList<DBSelectedFood> getSelectedFood() {
		ArrayList<DBSelectedFood> list = new ArrayList<DBSelectedFood>();
		dbHelper.open();
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		if (cSelectedFood.getCount() > 0) {
			cSelectedFood.moveToFirst();

			do {
				Cursor cUnit = dbHelper
						.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
				cUnit.moveToFirst();

				Cursor cFood = dbHelper
						.fetchFood(cUnit.getLong(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
				cFood.moveToFirst();

				list.add(new DBSelectedFood(
						cSelectedFood
								.getLong(cSelectedFood
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)),
						cSelectedFood.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),

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
			} while (cSelectedFood.moveToNext());

			cSelectedFood.close();
		}
		return list;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		startActivityUpdateSelectedFood(id);
	}

	// This will set the total calculated values on top of the page
	private void calculateValues() {

		// clear the adapter
		adapter.clear();

		ArrayList<DBTotalCalculated> items = new ArrayList<DBTotalCalculated>();

		// calculate amound of kilocalories
		float totalKcal = 0;
		float totalCarbs = 0;
		float totalProtein = 0;
		float totalFat = 0;

		fCalculatedInsulineAmount = 0;

		dbHelper.open();

		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		if (cSelectedFood.getCount() > 0) {
			startManagingCursor(cSelectedFood);
			cSelectedFood.moveToFirst();

			// store the first date in mCalendar
			mCalendar
					.setTime(functions
							.getYearMonthDayHourMinutesAsDateFromString(cSelectedFood.getString(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_EVENTDATETIME))));
			do {
				float subKcal, subCarbs, subProtein, subFat;
				Cursor cUnit = dbHelper
						.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
				cUnit.moveToFirst();

				// add the calculated kcal to the total
				subKcal = ((cSelectedFood
						.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)) / cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));

				// add the calculated carbs to the total
				subCarbs = ((cSelectedFood
						.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)) / cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)));

				// add the calculated protein to the total
				subProtein = ((cSelectedFood
						.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)) / cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)));
				// add the calculated fat to the total
				subFat = ((cSelectedFood
						.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)) / cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)));

				totalKcal += subKcal;
				totalCarbs += subCarbs;
				totalProtein += subProtein;
				totalFat += subFat;

				cUnit.close();
			} while (cSelectedFood.moveToNext());
		}
		cSelectedFood.close();

		// insuline ratio
		// Get all the needed settings
		Cursor cSettingsBreakfastTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_breakfast);
		cSettingsBreakfastTime.moveToFirst();
		Cursor cSettingsLunchTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_lunch);
		cSettingsLunchTime.moveToFirst();
		Cursor cSettingsSnackTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_snack);
		cSettingsSnackTime.moveToFirst();
		Cursor cSettingsDinnerTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_dinner);
		cSettingsDinnerTime.moveToFirst();

		// Make all the needed variables
		Date currentTime = new Date();
		Date dateBreakfastTime = new Date();
		Date dateLunchTime = new Date();
		Date dateSnackTime = new Date();
		Date dateDinnerTime = new Date();

		// Set all the needed variables
		dateBreakfastTime = transformStringToDate(cSettingsBreakfastTime
				.getString(cSettingsBreakfastTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		dateLunchTime = transformStringToDate(cSettingsLunchTime
				.getString(cSettingsLunchTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		dateSnackTime = transformStringToDate(cSettingsSnackTime
				.getString(cSettingsSnackTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		dateDinnerTime = transformStringToDate(cSettingsDinnerTime
				.getString(cSettingsDinnerTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		Cursor cInsulineRatio = null;

		// see if it is breakfast time
		// If current time is after breakfast time but before lunch time,
		// then its breakfast time!
		if (currentTime.after(dateBreakfastTime)
				&& currentTime.before(dateLunchTime)) {
			cInsulineRatio = dbHelper
					.fetchSettingByName(DbSettings.setting_insuline_ratio_breakfast);
		} else if (currentTime.after(dateLunchTime)
				&& currentTime.before(dateSnackTime)) {
			cInsulineRatio = dbHelper
					.fetchSettingByName(DbSettings.setting_insuline_ratio_lunch);
		} else if (currentTime.after(dateSnackTime)
				&& currentTime.before(dateDinnerTime)) {
			cInsulineRatio = dbHelper
					.fetchSettingByName(DbSettings.setting_insuline_ratio_snack);
		} else {
			cInsulineRatio = dbHelper
					.fetchSettingByName(DbSettings.setting_insuline_ratio_dinner);
		}

		cInsulineRatio.moveToFirst();
		fInsulineRatio = cInsulineRatio.getFloat(cInsulineRatio
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		// close all cursors
		cSettingsDinnerTime.close();
		cSettingsSnackTime.close();
		cSettingsLunchTime.close();
		cSettingsBreakfastTime.close();
		cInsulineRatio.close();

		// set the right insuline if fInsulineRatio != 0
		if (fInsulineRatio != 0)
			fCalculatedInsulineAmount = totalCarbs / fInsulineRatio;

		// Round the calculated floats
		totalCarbs = functions.roundFloats(totalCarbs, 1);
		totalProtein = functions.roundFloats(totalProtein, 1);
		totalFat = functions.roundFloats(totalFat, 1);
		totalKcal = functions.roundFloats(totalKcal, 1);
		fCalculatedInsulineAmount = functions.roundFloats(
				fCalculatedInsulineAmount, 1);

		// if we didnt togle the carbs off
		// update: AND the carbs are not the default
		// we add them to the list
		if (ActivityGroupMeal.group.getFoodData().showCarb) {
			// add the carbs to the total
			if (expandableListview != null
					&& ActivityGroupMeal.group.getFoodData().defaultValue != 1)

				items.add(new DBTotalCalculated(totalCarbs, 1, getResources()
						.getString(R.string.short_carbs)));
			// adapter.addItem(totalCarbs + " "
			// + getResources().getString(R.string.amound_of_carbs));

		}

		if (ActivityGroupMeal.group.getFoodData().showProt) {
			// add the prot to the total
			if (expandableListview != null
					&& ActivityGroupMeal.group.getFoodData().defaultValue != 2)
				// adapter.addItem(totalProtein + " "
				// + getResources().getString(R.string.amound_of_protein));
				items.add(new DBTotalCalculated(totalProtein, 2, getResources()
						.getString(R.string.amound_of_protein)));

		}

		if (ActivityGroupMeal.group.getFoodData().showFat) {
			// add the fat to the total
			if (expandableListview != null
					&& ActivityGroupMeal.group.getFoodData().defaultValue != 3)
				// adapter.addItem(totalFat + " "
				// + getResources().getString(R.string.amound_of_fat));
				items.add(new DBTotalCalculated(totalFat, 3, getResources()
						.getString(R.string.amound_of_fat)));

		}

		if (ActivityGroupMeal.group.getFoodData().showKcal) {
			// add the kcal to the total
			if (expandableListview != null
					&& ActivityGroupMeal.group.getFoodData().defaultValue != 4)
				// adapter.addItem(totalKcal + " "
				// + getResources().getString(R.string.amound_of_kcal));
				items.add(new DBTotalCalculated(totalKcal, 4, getResources()
						.getString(R.string.short_kcal)));

		}

		adapter.addItem(items);

		// add insuline ratio to the string when the radio != 0
		if (fInsulineRatio != 0) {
			if (expandableListview != null) {
				adapter.setCalculatedInsuline("" + fCalculatedInsulineAmount
						+ " " + getResources().getString(R.string.insulineUnit));

				adapter.setInsulineRatio(functions.roundFloats(fInsulineRatio,
						1)
						+ " "
						+ getResources().getString(R.string.insulineRatioShort));

			}
		}

		// set calculated default
		switch (ActivityGroupMeal.group.getFoodData().defaultValue) {
		case 1:
			if (expandableListview != null)
				adapter.setDefaultCalculated("" + totalCarbs);
			adapter.setDefaultCalculatedText(getResources().getString(
					R.string.short_carbs));
			break;
		case 2:
			if (expandableListview != null)
				adapter.setDefaultCalculated("" + totalProtein);
			adapter.setDefaultCalculatedText(getResources().getString(
					R.string.amound_of_protein));
			break;
		case 3:
			if (expandableListview != null)
				adapter.setDefaultCalculated("" + totalFat);
			adapter.setDefaultCalculatedText(getResources().getString(
					R.string.amound_of_fat));
			break;
		case 4:
			if (expandableListview != null)
				adapter.setDefaultCalculated("" + totalKcal);
			adapter.setDefaultCalculatedText(getResources().getString(
					R.string.short_kcal));
			break;
		}

		adapter.setDefaultValue(ActivityGroupMeal.group.getFoodData().defaultValue);

		expandableListview.setAdapter(adapter);

	}

	// This function will transform a string to a date object
	// Example "06:00" to a date object with time 6hours 0minutes
	private Date transformStringToDate(String time) {
		Date value = new Date();
		value.setHours(Integer.parseInt(time.substring(0, time.indexOf(":"))));
		value.setMinutes(Integer.parseInt(time.substring(time.indexOf(":") + 1)));
		return value;
	}

	private void startActivityUpdateSelectedFood(long selectedFoodId) {
		dbHelper.open();
		Cursor cSelectedFood = dbHelper.fetchSelectedFood(selectedFoodId);
		cSelectedFood.moveToFirst();

		// get the food
		Cursor cUnit = dbHelper
				.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
		cUnit.moveToFirst();
		Intent i = new Intent(this, ShowAddFoodToSelection.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra(DataParser.fromWhereWeCome,
						DataParser.weComeFRomShowSelectedFood)
				.putExtra(
						DataParser.idFood,
						cUnit.getLong(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)))
				.putExtra(DataParser.idSelectedFood, selectedFoodId);

		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
		ActivityGroupMeal.group.setContentView(v);

		cUnit.close();
		cSelectedFood.close();
	}

	// to fill the listview with data
	private void fillData() {
		listOfSelectedFood = getSelectedFood();
		if (listOfSelectedFood.size() > 0) {
			CustomBaseAdapterSelectedFood adapter = new CustomBaseAdapterSelectedFood(
					this, listOfSelectedFood,
					ActivityGroupMeal.group.getFoodData().dbFontSize,
					ActivityGroupMeal.group.getFoodData().defaultValue);
			setListAdapter(adapter);

		} else {
			// if we delete all items
			// we need to clear the listview
			setListAdapter(null);
		}
	}

	// when we click on the button delete all
	public void onClickDeleteAll(View view) {
		// show a popup to ask if the user is sure he wants to delete his
		// selected food

		// only show this popup when we have food selected
		if (ActivityGroupMeal.group.getFoodData().countSelectedFood > 0) {
			showPopUpToDeleteAllSelectedFood();
		}
	}

	// this method is called when the user press the button delete
	private void showPopUpToDeleteAllSelectedFood() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// delete all selected food
					deleteSelectedFood();
					// set counter == 0
					ActivityGroupMeal.group.getFoodData().countSelectedFood = 0;
					// refresh the data
					refreshData();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMeal.group);
		builder.setMessage(
				getResources().getString(R.string.sureToDeleteSelectedFood))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}

	private void deleteSelectedFood() {
		dbHelper.open();
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		if (cSelectedFood.getCount() > 0) {
			cSelectedFood.moveToFirst();
			do {
				dbHelper.deleteSelectedFood(cSelectedFood.getInt(cSelectedFood
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
			} while (cSelectedFood.moveToNext());
		}
		cSelectedFood.close();
	}

	// this method will refresh all the data on the screen
	public void refreshData() {
		setListAdapter(null);
		expandableListview.setAdapter(adapter);
		calculateValues();
		fillData();
		calculateTemplates();
	}

	private void calculateTemplates() {
		dbHelper.open();
		Cursor cFoodTemplates = dbHelper.fetchAllFoodTemplates();
		if (cFoodTemplates.getCount() == 0) {
			btLoadTemplate.setVisibility(View.INVISIBLE);
		} else {
			btLoadTemplate.setVisibility(View.VISIBLE);
		}
		// btLoadTemplate.setText(" (" + cFoodTemplates.getCount() + ")");
		cFoodTemplates.close();
	}

	// this method is called when the user press on save as template
	public void onClickSaveAsTemplate(View view) {
		dbHelper.open();
		/*
		 * First we check if we have more then 1 selectedFood in the table It
		 * would be stupid to add only 1 selected food to a template
		 */
		if (dbHelper.fetchAllSelectedFood().getCount() > 1) {
			final EditText input = new EditText(this);
			// Show a dialog with a inputbox to insert the template name
			new AlertDialog.Builder(ActivityGroupMeal.group)
					.setTitle(getResources().getString(R.string.template_name))
					.setView(input)
					.setPositiveButton(getResources().getString(R.string.save),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// on click positive button
									// if inputbox text is longer then ""
									if (input.getText().length() > 0) {
										// Show dialog to ask if we need to save
										// food amount
										templateName = input.getText()
												.toString();
										showDialogSaveFoodAmount();
									} else {
										showToast(getResources()
												.getString(
														R.string.template_name_cant_be_empty));
									}
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// on click negative button do nothing
								}
							}).show();
		} else {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.add_food_to_template_error),
					Toast.LENGTH_LONG).show();
		}
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	private void showDialogSaveFoodAmount() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					saveFoodAmount = true;
					break;
				default:
					saveFoodAmount = false;
					break;
				}
				createNewTemplate();
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMeal.group);
		builder.setMessage(
				getResources().getString(
						R.string.showSelectedFoodPopupAddAmountToTemplate))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}

	private void createNewTemplate() {
		dbHelper.open();
		// Add the selected food to a template
		// We create a new mealType
		Long mealTypeID = dbHelper.createMealType("testOneMealType");
		// Then we add a FoodTemplate with the MealTypeID
		Long foodTemplateID = dbHelper.createFoodTemplate(mealTypeID,
				templateName);
		// then for every selected food row we create a template_food
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		cSelectedFood.moveToFirst();

		do {
			float amount = 0f;
			if (saveFoodAmount)
				amount = cSelectedFood
						.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT));

			dbHelper.createTemplateFood(
					foodTemplateID,
					cSelectedFood.getLong(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)),
					amount);

		} while (cSelectedFood.moveToNext());

		cSelectedFood.close();

		refreshData();
	}

	public void onClickLoadTemplate(View view) {
		dbHelper.open();
		if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
			Intent i = new Intent(this, ShowFoodTemplates.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			View v = ActivityGroupMeal.group.getLocalActivityManager()
					.startActivity(DataParser.activityIDMeal, i).getDecorView();
			ActivityGroupMeal.group.setContentView(v);
		} else {
			showToast(getResources().getString(R.string.template_load_empty));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		dbHelper.open();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		adapter = new CustomExpandableListAdapter(ActivityGroupMeal.group,
				ActivityGroupMeal.group.getFoodData().dbFontSize,
				metrics.densityDpi);

		refreshData();

		checkToOpenExpandableListView();
	}

	private void checkToOpenExpandableListView() {
		// if setting expandListview = 1 we expand the listview
		DbAdapter db = new DbAdapter(this);
		db.open();

		Cursor c = db.fetchSettingByName(DbSettings.setting_expand_listview);
		if (c.getCount() > 0) {
			c.moveToFirst();
			if (c.getInt(c
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 1) {
				expandableListview.expandGroup(0);
			}
		} else {
			// if we didnt find the expand listview object, it is the first time
			// the user comes here
			// so we create it.
			// with value = 0 so we dont expand the listview
			db.createSettings(DbSettings.setting_expand_listview, "0");
		}

		c.close();
		db.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if we press the back key
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			// return false so the keydown event from activitygroupmeal will get
			// called
			return false;
		else
			return super.onKeyDown(keyCode, event);
	}
}
