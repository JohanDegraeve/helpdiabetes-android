// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

/*
 * This class will be the first class that starts when we open the activitygroupmeal.
 * This class will hold our foodList and other values we cant lose when we restart the ShowFoodList activity
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodComparable;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.FoodComparator;

public class ShowLoadingFoodData extends Activity {
	// This boolean is used to see if we are finish with gething our data from
	// the database
	// The first time the application starts showFoodList will check this
	// boolean
	// sees that we are still bussy and displayes the loading screen
	// when we finish with gething our data the method will trigger showFoodList
	// to update its listview
	// the next time we display showFoodList ( when we come back from a child
	// activity )
	// ShowFoodList will show the foodlist becaus this boolean says we are donne
	// gething our data
	public boolean finishedGethingData;

	// This context is used in the async task to create a dbadapter
	private Context context;

	// all the food with visible = 1 and foodlanguageID = setting ID
	public List<DBFoodComparable> listAllFood;
	// all the food with visible = 1 and foodlanguageID = setting ID and
	// favorite = 1
	public List<DBFoodComparable> listFavoriteFood;

	// stuff we need to hold for the showFoodList
	public List<DBFoodComparable> listFood;
	public long foodLanguageID;
	public int countSelectedFood;
	public int dbFontSize;

	// default value to show
	public int defaultValue;
	// booleans so see if we need to show the value
	public boolean showCarb, showProt, showFat, showKcal;
	// used to hold the default medicine
	public long defaultMedicineTypeID;
	// used to hold the default exerciseTypeID;
	public long defaultExerciseTypeID;

	// used to hold the startup
	public boolean startUp;

	public String dbTopOneCommonFoodUnit;
	public String dbTopTwoCommonFoodUnit;

	public static ShowLoadingFoodData INSTANCE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// show the loading screen
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_start, null);
		setContentView(contentView);

		context = this;
		INSTANCE = this;
	}

	@Override
	protected void onResume() {
		// check to see if we have already food items in selectedFood
		// if yes we have to show popup to delete them
		// In the end of this method we will start the asynctask to get our
		// food
		checkToShowPopUpDeleteSelectedFoodItems();

		super.onResume();
	}

	private void checkToShowPopUpDeleteSelectedFoodItems() {
		DbAdapter db = new DbAdapter(context);
		db.open();

		boolean startUp = false;

		Cursor cSetting = db.fetchSettingByName(DbSettings.setting_startUp);
		cSetting.moveToFirst();

		if (cSetting.getInt(cSetting
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 1) {
			startUp = true;
		}

		// if selectedFood > 0 && we are starting up!
		if (db.fetchAllSelectedFood().getCount() > 0 && startUp) {
			// mark startup as false
			db.updateSettingsByName(DbSettings.setting_startUp, "0");

			// create popup to show
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						DbAdapter database = new DbAdapter(context);
						database.open();

						Cursor cSelectedFood = database.fetchAllSelectedFood();
						cSelectedFood.moveToFirst();

						do {
							// delete all selected food
							database.deleteSelectedFood(cSelectedFood.getLong(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
						} while (cSelectedFood.moveToNext());

						cSelectedFood.close();
						database.close();
						countSelectedFood = 0;
						// start asynctask after we deleted all
						new AsyncGetFoodListOutDatabase().execute();
						break;
					default:
						// start asynctask when we press the no button
						new AsyncGetFoodListOutDatabase().execute();
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ActivityGroupMeal.group);
			builder.setMessage(
					context.getResources().getString(
							R.string.do_you_want_to_delete_the_selections))
					.setPositiveButton(
							context.getResources().getString(R.string.yes),
							dialogClickListener)
					.setNegativeButton(
							context.getResources().getString(R.string.no),
							dialogClickListener)
					.setOnKeyListener(new OnKeyListener() {

						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							// when we press the back button to close the dialog
							// we have to start the async task to!
							if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
								new AsyncGetFoodListOutDatabase().execute();
							return false;
						}
					}).show();
		} else {
			// run a async task when we dont have selectedFood
			new AsyncGetFoodListOutDatabase().execute();
		}

		db.close();
	}

	private void setValues() {
		// this method will set the count selected food
		// the foodlanguageid and the fontsize

		countSelectedFood = 0;

		// we put these 3 methods in 1 super method so we can use 1 database
		// connection
		DbAdapter db = new DbAdapter(context);
		db.open();
		setCountSelectedFood(db);
		setFoodLanguageID(db);
		setFontSize(db);
		setDefaultValue(db);
		setShowValues(db);
		setDefaultMedicineTypeID(db);
		setDefaultExerciseTypeID(db);
		setCommonFoodUnitTypes(db);
		db.close();
	}
 
	public void setCommonFoodUnitTypes(DbAdapter db) {
		dbTopOneCommonFoodUnit = "gram";
		dbTopTwoCommonFoodUnit = "ml";

		/*Cursor cTemp = db.fetchAllUnitsFromCurrentDBLangauge(foodLanguageID);
		if (cTemp.getCount() > 1) {
			cTemp.moveToFirst();
			dbTopOneCommonFoodUnit = cTemp.getString(1);
			cTemp.moveToNext();
			dbTopTwoCommonFoodUnit = cTemp.getString(1);
		} else { 
			dbTopOneCommonFoodUnit = "gram";
			dbTopTwoCommonFoodUnit = "ml";
		}
		cTemp.close();*/

	}
 
	private void setDefaultExerciseTypeID(DbAdapter db) {
		Cursor cSetting = db
				.fetchSettingByName(DbSettings.setting_default_exercise_type_ID);
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();
			defaultExerciseTypeID = cSetting.getLong(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			defaultExerciseTypeID = 1;
		}
		cSetting.close();
	}

	private void setDefaultMedicineTypeID(DbAdapter db) {
		Cursor cSetting = db
				.fetchSettingByName(DbSettings.setting_default_medicine_type_ID);
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();
			defaultMedicineTypeID = cSetting.getLong(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			defaultMedicineTypeID = 1;
		}
		cSetting.close();
	}

	private void setShowValues(DbAdapter db) {

		// carb togle button
		Cursor cSettingCarb = db
				.fetchSettingByName(DbSettings.setting_value_carb_onoff);
		if (cSettingCarb.getCount() > 0) {
			cSettingCarb.moveToFirst();
			if (cSettingCarb.getInt(cSettingCarb
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 1) {
				showCarb = true;
			} else {
				showCarb = false;
			}
		}
		cSettingCarb.close();

		// prot togle button
		Cursor cSettingProt = db
				.fetchSettingByName(DbSettings.setting_value_prot_onoff);
		if (cSettingProt.getCount() > 0) {
			cSettingProt.moveToFirst();
			if (cSettingProt.getInt(cSettingProt
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 1) {
				showProt = true;
			} else {
				showProt = false;
			}
		}
		cSettingProt.close();

		// fat togle button
		Cursor cSettingFat = db
				.fetchSettingByName(DbSettings.setting_value_fat_onoff);
		if (cSettingFat.getCount() > 0) {
			cSettingFat.moveToFirst();
			if (cSettingFat.getInt(cSettingFat
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 1) {
				showFat = true;
			} else {
				showFat = false;
			}
		}
		cSettingFat.close();

		// kcal togle button
		Cursor cSettingKcal = db
				.fetchSettingByName(DbSettings.setting_value_kcal_onoff);
		if (cSettingKcal.getCount() > 0) {
			cSettingKcal.moveToFirst();
			if (cSettingKcal.getInt(cSettingKcal
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 1) {
				showKcal = true;
			} else {
				showKcal = false;
			}
		}
		cSettingKcal.close();

	}

	private void setDefaultValue(DbAdapter db) {
		Cursor cSetting = db
				.fetchSettingByName(DbSettings.setting_value_default);
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();
			defaultValue = cSetting.getInt(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			defaultValue = 1;
		}
		cSetting.close();
	}

	private void setCountSelectedFood(DbAdapter db) {
		countSelectedFood = db.fetchAllSelectedFood().getCount();
	}

	private void setFontSize(DbAdapter db) {
		Cursor cSetting = db.fetchSettingByName(DbSettings.setting_font_size);
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();
			dbFontSize = cSetting.getInt(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			dbFontSize = 20;
		}
		cSetting.close();
	}

	// This method is called from show settings font size
	// When the user presses on a list item to set new font size
	// This method will set the new font size on this page
	// and reload the showFoodList
	public void setNewFontSize() {
		DbAdapter db = new DbAdapter(context);
		db.open();
		// get the new font size
		setFontSize(db);
		db.close();
		// refresh the foodlist
		ActivityGroupMeal.group.getShowFoodList().setNewFontSize();
	}

	private void setFoodLanguageID(DbAdapter db) {

		// First get the food language id
		Cursor cSetting = db.fetchSettingByName(DbSettings.setting_language);
		// there will always be 1 record
		if (cSetting.getCount() > 0) {
			// move the cursor to the first record
			cSetting.moveToFirst();
			// put the foodLanguageID in the variable
			foodLanguageID = cSetting.getLong(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			// if we dont have the record ( something went wrong )
			// and we set foodLanguage to 1 so the app wont crash
			foodLanguageID = 1;
		}

		// close the cursor
		cSetting.close();

	}

	private class AsyncGetFoodListOutDatabase extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// we are not finished gething our data
			finishedGethingData = false;

			// set the values fontsize, languageid and countSelectedFood
			setValues();

			// create new dbHelper object
			DbAdapter dbHelper = new DbAdapter(context);

			// initialise the lists
			listAllFood = new ArrayList<DBFoodComparable>();
			listFavoriteFood = new ArrayList<DBFoodComparable>();

			// open database helper object
			dbHelper.open();

			// get all the food items with the right languageID and visible = 1
			// Cursor cAllFood = dbHelper.fetchFoodByLanguageID(foodLanguageID);
			Cursor cAllFood = dbHelper
					.fetchFoodByLanguageIDInQuery(foodLanguageID);
			// if we have records
			if (cAllFood.getCount() > 0) {
				// move to first record
				cAllFood.moveToFirst();
				// do this loop for every record
				do {
					// create a DBFoodComparable object
					DBFoodComparable newFood = new DBFoodComparable(
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
							cAllFood.getString(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_FOODLANGUAGEID)),
							cAllFood.getInt(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_VISIBLE)),
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_CATEGORYID)),
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_USERID)),
							cAllFood.getInt(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
							cAllFood.getString(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
					// add the new food to the list
					listAllFood.add(newFood);
				} while (cAllFood.moveToNext());
			}
			// close the cursor
			cAllFood.close();

			// get all the food items with the right languageID and visible = 1
			// and favorite = 1
			Cursor cFavoriteFood = dbHelper
					.fetchFoodByLanguageIDAndFavorite(foodLanguageID);
			// if we have records
			if (cFavoriteFood.getCount() > 0) {
				// move to first record
				cFavoriteFood.moveToFirst();
				// do this loop for every record
				do {
					// create a DBFoodComparable object
					DBFoodComparable newFood = new DBFoodComparable(
							cFavoriteFood
									.getLong(cFavoriteFood
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
							cFavoriteFood.getString(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
							cFavoriteFood.getLong(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_FOODLANGUAGEID)),
							cFavoriteFood.getInt(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_VISIBLE)),
							cFavoriteFood.getLong(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_CATEGORYID)),
							cFavoriteFood.getLong(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_USERID)),
							cFavoriteFood.getInt(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
							cFavoriteFood.getString(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
					// add the new food to the list
					listFavoriteFood.add(newFood);
				} while (cFavoriteFood.moveToNext());
			}
			// close the cursor
			cFavoriteFood.close();

			// close dbHelper object
			dbHelper.close();

			// sort the 2 lists and create a total list
			recreateTotalList();

			return null;
		}

		// when we finished getting the food list out the database and sorting
		// it
		@Override
		protected void onPostExecute(Void result) {
			// only do this the first time
			if (!finishedGethingData) {
				// we are finished gething our data
				finishedGethingData = true;

				// create the showfoodlist page
				Intent i = new Intent(context, ShowFoodList.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				View v = ActivityGroupMeal.group.getLocalActivityManager()
						.startActivity(DataParser.activityIDMeal, i)
						.getDecorView();
				ActivityGroupMeal.group.setContentView(v);

			}
			super.onPostExecute(result);
		}
	}

	// this method will sort the 2 lists and recreate the total list
	public void recreateTotalList() {
		try {
			FoodComparator comparator = new FoodComparator();

			// sort all the favorite food items
			Collections.sort(listFavoriteFood, comparator);

			// sort all the food items
			Collections.sort(listAllFood, comparator);

			// set list to empty
			listFood = null;
			// Initialise the total list of food
			listFood = new ArrayList<DBFoodComparable>();
			// put the favorites and the list with all food in this list
			listFood.addAll(listFood.size(), listFavoriteFood);

			// add one item to the list to make the line between favorite and
			// normal
			// bigger when the favorite list count > 0!
			if (listFavoriteFood.size() > 0)
				listFood.add(new DBFoodComparable(-1, "", 0L, 0, 0L, 0L, 0, ""));

			listFood.addAll(listFood.size(), listAllFood);
		} catch (Exception e) {
		}
	}

	// This method is called from showfoodlist when we change favorite
	public void setIsFavoriteFromFoodListAll(long foodID, int isFavorite) {
		for (DBFoodComparable obj : listAllFood) {
			if (obj.getId() == foodID) {
				listAllFood.get(listAllFood.indexOf(obj)).setIsfavorite(
						isFavorite);
				return;
			}
		}
	}

	// This method is called from showupdatefood when we delete food out the
	// database
	public void deleteFoodFromList(long foodID) {
		deleteFoodFromFavoriteList(foodID);
		deleteFoodFromAllList(foodID);
	}

	public void deleteFoodFromAllList(long foodID) {
		for (DBFoodComparable obj : listAllFood) {
			if (obj.getId() == foodID) {
				listAllFood.remove(obj);
				return;
			}
		}
	}

	// This method is called from showfoodlist when we unmark a fooditem as
	// favorite
	public void deleteFoodFromFavoriteList(long foodID) {
		for (DBFoodComparable obj : listFavoriteFood) {
			if (obj.getId() == foodID) {
				listFavoriteFood.remove(obj);
				return;
			}
		}
	}

	// This method is called from showfoodlist when we changed a food name in
	// showupdatefood
	// in this method we change the foodname to the given name with the given ID
	// update: we also mark the food as "own created" -> to do so we set
	// platform on "android"
	public void updateFoodName(long foodID, String foodName) {
		// check for every object in the favorites
		for (DBFoodComparable obj : listFavoriteFood) {
			if (obj.getId() == foodID) {

				listFavoriteFood.get(listFavoriteFood.indexOf(obj)).setName(
						foodName);

				listFavoriteFood.get(listFavoriteFood.indexOf(obj))
						.setPlatform(DataParser.foodPlatform);
			}
		}
		// check for every object in the normal list
		for (DBFoodComparable obj : listAllFood) {
			if (obj.getId() == foodID) {

				listAllFood.get(listAllFood.indexOf(obj)).setName(foodName);

				listAllFood.get(listAllFood.indexOf(obj)).setPlatform(
						DataParser.foodPlatform);
			}
		}
	}

	// for every food object in the normal list we set the platform to "android"
	// so we get a green star
	public void updateFoodToOwnCreated(long foodID) {
		// check for every object in the normal list
		for (DBFoodComparable obj : listAllFood) {
			if (obj.getId() == foodID) {
				listAllFood.get(listAllFood.indexOf(obj)).setPlatform(
						DataParser.foodPlatform);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// return true so the application wont exit
		return true;
	}
}
