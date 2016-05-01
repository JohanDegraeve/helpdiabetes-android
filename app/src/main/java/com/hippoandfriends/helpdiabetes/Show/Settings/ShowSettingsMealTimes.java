// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Settings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterSettingsMealTimes;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;
import com.hippoandfriends.helpdiabetes.slider.DateSlider;
import com.hippoandfriends.helpdiabetes.slider.TimeSlider;

public class ShowSettingsMealTimes extends ListActivity {
	private DbAdapter dbHelper;
	private int selectedMeal;
	private Functions functions;
	private Button btBack, btNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_meal_times);

		try {
			// track we come here
			ActivityGroupSettings.group.parent
					.trackPageView(TrackingValues.pageShowSettingMealTimes);
		} catch (RuntimeException e) {
		}

		dbHelper = new DbAdapter(this);
		functions = new Functions();
		selectedMeal = 0;

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

		btNext = (Button) findViewById(R.id.buttonNext);
		btNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// call finish on click button next so we go back to showStart
				finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doFirstTime)) {

				btNext.setVisibility(View.VISIBLE);
				// hide the button back
				btBack.setVisibility(View.GONE);
			}
		} catch (NullPointerException e) {
		}

		dbHelper.open();
		fillData();

	}

	// This function will return a arraylist with the times
	// This arraylist is used in our listview
	private ArrayList<String> createArrayList() {
		ArrayList<String> list = new ArrayList<String>();

		// get the times out the database
		// breakfast
		Cursor cBreakfastTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_breakfast);
		cBreakfastTime.moveToFirst();
		// lunch
		Cursor cLunchTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_lunch);
		cLunchTime.moveToFirst();
		// snack
		Cursor cSnackTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_snack);
		cSnackTime.moveToFirst();
		// dinner
		Cursor cDinnerTime = dbHelper
				.fetchSettingByName(DbSettings.setting_meal_time_dinner);
		cDinnerTime.moveToFirst();

		// Fill the list with the times
		list.add(getResources().getString(R.string.pref_breakfastratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cBreakfastTime.getString(cBreakfastTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		list.add(getResources().getString(R.string.pref_lunchratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cLunchTime.getString(cLunchTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		list.add(getResources().getString(R.string.pref_snackratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cSnackTime.getString(cSnackTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		list.add(getResources().getString(R.string.pref_dinnerratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cDinnerTime.getString(cDinnerTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		// Close all the cursors
		cDinnerTime.close();
		cSnackTime.close();
		cLunchTime.close();
		cBreakfastTime.close();

		return list;
	}

	private DateSlider.OnDateSetListener mTimeSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			try {// track we come here
				ActivityGroupSettings.group.parent.trackEvent(
						TrackingValues.eventCategorySettings,
						TrackingValues.eventCategorySettingsChangeMealTime);
			} catch (NullPointerException e) {
			} 
			
			String time = functions.getTimeFromDate(selectedDate.getTime());
			String settingName = "";

			// update the selectedMeal with the corresponding date
			switch (selectedMeal) {
			case 0:
				// update breakfast in db
				settingName = DbSettings.setting_meal_time_breakfast;
				break;
			case 1:
				// update lunch in db
				settingName = DbSettings.setting_meal_time_lunch;
				break;
			case 2:
				// update snack in db
				settingName = DbSettings.setting_meal_time_snack;
				break;
			case 3:
				// update dinner in db
				settingName = DbSettings.setting_meal_time_dinner;
				break;
			}

			if (!settingName.equals("")) {
				dbHelper.updateSettingsByName(settingName, time);
				// refresh list
				setListAdapter(null);
				fillData();
			}
		}
	};

	protected android.app.Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		Date date = new Date();

		Cursor cSettingTime = null;

		switch (id) {
		case 0:
			cSettingTime = dbHelper
					.fetchSettingByName(DbSettings.setting_meal_time_breakfast);
			break;
		case 1:
			cSettingTime = dbHelper
					.fetchSettingByName(DbSettings.setting_meal_time_lunch);
			break;
		case 2:
			cSettingTime = dbHelper
					.fetchSettingByName(DbSettings.setting_meal_time_snack);
			break;
		case 3:
			cSettingTime = dbHelper
					.fetchSettingByName(DbSettings.setting_meal_time_dinner);
			break;
		}

		cSettingTime.moveToFirst();

		date.setHours(functions.getHourFromString(cSettingTime.getString(cSettingTime
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE))));
		date.setMinutes(functions.getMinutesFromString(cSettingTime.getString(cSettingTime
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE))));

		cSettingTime.close();

		c.setTime(date);
		try {
			return new TimeSlider(ActivityGroupSettings.group,
					mTimeSetListener, c, 15);
		} catch (NullPointerException e) {
			return new TimeSlider(this, mTimeSetListener, c, 15);
		}
	};

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		selectedMeal = (int) id;
		showDialog((int) id);
	}

	private void fillData() {
		CustomArrayAdapterSettingsMealTimes adapter = new CustomArrayAdapterSettingsMealTimes(
				this, R.layout.row_custom_array_adapter_setting_meal_times,
				createArrayList());
		setListAdapter(adapter);
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
