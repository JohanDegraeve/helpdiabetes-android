// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Settings;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowSettingsValue extends Activity {

	private RadioButton rbCarb, rbProt, rbFat, rbKcal;
	private ToggleButton tgCarb, tgProt, tgFat, tgKcal;

	// used to hold the default value in
	// 1 = carb, 2 = prot, 3 = fat, 4 = kcal
	private int defaultValue;

	private Button btNext, btBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_value);

		// track we come here
		ActivityGroupSettings.group.parent
				.trackPageView(TrackingValues.pageShowSettingFoodComposition);

		rbCarb = (RadioButton) findViewById(R.id.radioCarbs);
		rbProt = (RadioButton) findViewById(R.id.radioProt);
		rbFat = (RadioButton) findViewById(R.id.radioFat);
		rbKcal = (RadioButton) findViewById(R.id.radioKcal);

		tgCarb = (ToggleButton) findViewById(R.id.toggleButtonCarbs);
		tgProt = (ToggleButton) findViewById(R.id.toggleButtonProt);
		tgFat = (ToggleButton) findViewById(R.id.toggleButtonFat);
		tgKcal = (ToggleButton) findViewById(R.id.toggleButtonKcal);

		btNext = (Button) findViewById(R.id.buttonNext);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

		// set text off
		tgCarb.setTextOff(getResources().getString(R.string.invisible));
		tgProt.setTextOff(getResources().getString(R.string.invisible));
		tgFat.setTextOff(getResources().getString(R.string.invisible));
		tgKcal.setTextOff(getResources().getString(R.string.invisible));

		// set text on
		tgCarb.setTextOn(getResources().getString(R.string.visible));
		tgProt.setTextOn(getResources().getString(R.string.visible));
		tgFat.setTextOn(getResources().getString(R.string.visible));
		tgKcal.setTextOn(getResources().getString(R.string.visible));

		rbCarb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updatedChecked(1);
			}
		});

		rbCarb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updatedChecked(1);
			}
		});

		rbProt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updatedChecked(2);
			}
		});

		rbFat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updatedChecked(3);
			}
		});

		rbKcal.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updatedChecked(4);
			}
		});

		tgCarb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				changeToggleButton(1);
			}
		});

		tgProt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				changeToggleButton(2);
			}
		});

		tgFat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				changeToggleButton(3);
			}
		});

		tgKcal.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				changeToggleButton(4);
			}
		});

		btNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// call finish on click button next so we go back to showStart
				finish();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		setDefaultRadioButton();
		setToggleButtons();

		// check if we need to show the button next
		// only need to show first time application starts
		try {
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doFirstTime)) {
				btNext.setVisibility(View.VISIBLE);
				// hide the button back
				btBack.setVisibility(View.GONE);
			}
		} catch (NullPointerException e) {
		}
 
	}

	private void updateToggleButton(int togleID) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		switch (togleID) {
		case 1:
			int carbValue = 0;

			if (tgCarb.isChecked()) {
				carbValue = 1;
				try {
					ActivityGroupMeal.group.getFoodData().showCarb = true;
				} catch (NullPointerException e) {
				}
			} else {
				try {
					ActivityGroupMeal.group.getFoodData().showCarb = false;
				} catch (NullPointerException e) {
				}
			}

			db.updateSettingsByName(DbSettings.setting_value_carb_onoff, ""
					+ carbValue);
			break;
		case 2:
			int protValue = 0;

			if (tgProt.isChecked()) {
				protValue = 1;
				try {
					ActivityGroupMeal.group.getFoodData().showProt = true;
				} catch (NullPointerException e) {
				}
			} else {
				try {
					ActivityGroupMeal.group.getFoodData().showProt = false;
				} catch (NullPointerException e) {
				}
			}

			db.updateSettingsByName(DbSettings.setting_value_prot_onoff, ""
					+ protValue);

			break;
		case 3:
			int fatValue = 0;

			if (tgFat.isChecked()) {
				fatValue = 1;
				try {
					ActivityGroupMeal.group.getFoodData().showFat = true;
				} catch (NullPointerException e) {
				}
			} else {
				try {
					ActivityGroupMeal.group.getFoodData().showFat = false;
				} catch (NullPointerException e) {
				}
			}

			db.updateSettingsByName(DbSettings.setting_value_fat_onoff, ""
					+ fatValue);
			break;
		case 4:
			int kcalValue = 0;

			if (tgKcal.isChecked()) {
				kcalValue = 1;
				try {
					ActivityGroupMeal.group.getFoodData().showKcal = true;
				} catch (NullPointerException e) {
				}
			} else {
				try {
					ActivityGroupMeal.group.getFoodData().showKcal = false;
				} catch (NullPointerException e) {
				}
			}

			db.updateSettingsByName(DbSettings.setting_value_kcal_onoff, ""
					+ kcalValue);
			break;
		}

		db.close();

	}

	// This method is called when we click on a togle button
	// This method will write the value to the database
	private void changeToggleButton(int togleID) {
		try {// track we come here
			ActivityGroupSettings.group.parent.trackEvent(
					TrackingValues.eventCategorySettings,
					TrackingValues.eventCategorySettingsChangeFoodCompositionVisible);
		} catch (NullPointerException e) {
		} 
		
		if (defaultValue != togleID) {
			updateToggleButton(togleID);
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.standard_cant_be_hidden),
					Toast.LENGTH_SHORT).show();

			// set toglebutton back to on
			switch (defaultValue) {
			case 1:
				tgCarb.toggle();
				break;
			case 2:
				tgProt.toggle();
				break;
			case 3:
				tgFat.toggle();
				break;
			case 4:
				tgKcal.toggle();
				break;
			}
		}
	}

	// This method is called when we select a other radio button
	// Ths method wil uncheck the other radio buttons
	// Write the default with the given one
	// update it in showloading fooddata
	private void updatedChecked(int newDefault) {
		try {// track we come here
			ActivityGroupSettings.group.parent.trackEvent(
					TrackingValues.eventCategorySettings,
					TrackingValues.eventCategorySettingsChangeFoodCompositionDefault);
		} catch (NullPointerException e) {
		} 
		
		// set all radio buttons to unchecked
		rbCarb.setChecked(false);
		rbProt.setChecked(false);
		rbFat.setChecked(false);
		rbKcal.setChecked(false);

		try {
			// set the new default in showfooddata
			ActivityGroupMeal.group.getFoodData().defaultValue = newDefault;
		} catch (NullPointerException e) {
		}

		// set the default value on this page
		defaultValue = newDefault;

		// write to database
		DbAdapter db = new DbAdapter(this);
		db.open();

		// set right radio button checked
		// and visible to true
		// and set the togle button on
		switch (newDefault) {
		case 1:
			tgCarb.setChecked(true);
			rbCarb.setChecked(true);
			break;
		case 2:
			tgProt.setChecked(true);
			rbProt.setChecked(true);
			break;
		case 3:
			tgFat.setChecked(true);
			rbFat.setChecked(true);
			break;
		case 4:
			tgKcal.setChecked(true);
			rbKcal.setChecked(true);
			break;
		}

		db.updateSettingsByName(DbSettings.setting_value_default, ""
				+ newDefault);
		db.close();

		// mark the new default as visible in database and on screen
		// togglebutton
		updateToggleButton(newDefault);
	}

	// This method will uncheck all radio buttons and check the default one
	private void setDefaultRadioButton() {
		rbCarb.setChecked(false);
		rbProt.setChecked(false);
		rbFat.setChecked(false);
		rbKcal.setChecked(false);

		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cSetting = db
				.fetchSettingByName(DbSettings.setting_value_default);
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();
			defaultValue = cSetting.getInt(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			// set default = carb
			defaultValue = 1;
		}
		cSetting.close(); 
		db.close();

		switch (defaultValue) {
		case 1:
			rbCarb.setChecked(true);
			break;
		case 2:
			rbProt.setChecked(true);
			break;
		case 3:
			rbFat.setChecked(true);
			break;
		case 4:
			rbKcal.setChecked(true);
			break;
		}
	}

	// This method will set right value on the togle buttons
	private void setToggleButtons() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		// carb togle button
		Cursor cSettingCarb = db
				.fetchSettingByName(DbSettings.setting_value_carb_onoff);
		if (cSettingCarb.getCount() > 0) {
			cSettingCarb.moveToFirst();
			if (cSettingCarb.getInt(cSettingCarb
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 1) {
				tgCarb.setChecked(true);
			} else {
				tgCarb.setChecked(false);
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
				tgProt.setChecked(true);
			} else {
				tgProt.setChecked(false);
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
				tgFat.setChecked(true);
			} else {
				tgFat.setChecked(false);
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
				tgKcal.setChecked(true);
			} else {
				tgKcal.setChecked(false);
			}
		}

		cSettingKcal.close();

		db.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
