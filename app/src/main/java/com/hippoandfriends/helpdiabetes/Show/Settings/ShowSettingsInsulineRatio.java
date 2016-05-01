
package com.hippoandfriends.helpdiabetes.Show.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowSettingsInsulineRatio extends Activity {
	private DbAdapter dbHelper;
	private EditText insulineRatioBreakfast, insulineRatioLunch,
			insulineRatioSnack, insulineRatioDinner;
	private boolean firstKeyPressBreakfast, firstKeyPressLunch,
			firstKeyPressSnack, firstKeyPressDinner;
	private Button btNext, btBack;

	// create a edittext list to run true the array and set a onkey listener on
	// every edittext
	private List<EditText> listEditTexts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_insuline_ratio);

		try {
			// track we come here
			ActivityGroupSettings.group.parent
					.trackPageView(TrackingValues.pageShowSettingInsulineRatio);
		} catch (RuntimeException e) {
		}

		dbHelper = new DbAdapter(this);
		dbHelper.open();

		insulineRatioBreakfast = (EditText) findViewById(R.id.editTextShowPreferencesBreakfastRatio);
		insulineRatioLunch = (EditText) findViewById(R.id.editTextShowPreferencesLunchRatio);
		insulineRatioSnack = (EditText) findViewById(R.id.editTextShowPreferencesSnackRatio);
		insulineRatioDinner = (EditText) findViewById(R.id.editTextShowPreferencesDinnerRatio);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

		firstKeyPressBreakfast = true;
		firstKeyPressLunch = true;
		firstKeyPressSnack = true;
		firstKeyPressDinner = true;

		// create a edittext list and add al edittexts to it
		listEditTexts = new ArrayList<EditText>();
		listEditTexts.add(insulineRatioBreakfast);
		listEditTexts.add(insulineRatioLunch);
		listEditTexts.add(insulineRatioSnack);
		listEditTexts.add(insulineRatioDinner);

		// create for every edittext in the list a onkeylistener
		for (int i = 0; i < listEditTexts.size(); i++) {
			listEditTexts.get(i).setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// filter so we only get the onkey up actions
					if (event.getAction() != KeyEvent.ACTION_DOWN) {
						// if the pressed key = enter we go to the next
						if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
							goToNextEditText();
						}
					}
					// if we dont return false our numbers wont get in the
					// edittext
					return false;
				}
			});
		}

		fillData();

		btNext = (Button) findViewById(R.id.buttonNext);
		btNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					// see if we are here from startup page
					if (getIntent().getExtras().getString(DataParser.whatToDo)
							.equals(DataParser.doFirstTime)) {
						updateValues();
						finish();
					} else {
						try {// track we come here
							ActivityGroupSettings.group.parent
									.trackEvent(
											TrackingValues.eventCategorySettings,
											TrackingValues.eventCategorySettingsChangeInsulineRatios);
						} catch (NullPointerException e) {
						}
						onClickUpdate();
					}
				} catch (Exception e) {
					try {// track we come here
						ActivityGroupSettings.group.parent
								.trackEvent(
										TrackingValues.eventCategorySettings,
										TrackingValues.eventCategorySettingsChangeInsulineRatios);
					} catch (NullPointerException l) {
					}
					onClickUpdate();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// check if we need to show the button next
		// only need to show first time application starts
		try {
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doFirstTime)) {
				// hide the button back
				btBack.setVisibility(View.GONE);
			}
		} catch (Exception e) {
		}
	}

	public void goToNextEditText() {
		// change focus to next
		// but do size-1 so we dont try to change from edittext so one outside
		// the list that doesnt exists
		for (int i = 0; i < listEditTexts.size() - 1; i++) {
			if (this.getCurrentFocus() == listEditTexts.get(i)) {
				// set focus
				listEditTexts.get(i + 1).requestFocus();
				// stop this method
				return;
			}
		}
		// if we get here we clicked enter on the last edit text
		// if we do that we trigger the onclick Update button
		onClickUpdate();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// only check if we need to clear the box if we didnt press the enter
		// button
		// becaus enter button will go to the next edittext
		if (event.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
			if (insulineRatioBreakfast.isFocused()) {
				if (firstKeyPressBreakfast) {
					firstKeyPressBreakfast = false;
					insulineRatioBreakfast.setText("");
				}
			} else if (insulineRatioLunch.isFocused()) {
				if (firstKeyPressLunch) {
					firstKeyPressLunch = false;
					insulineRatioLunch.setText("");
				}
			} else if (insulineRatioDinner.isFocused()) {
				if (firstKeyPressDinner) {
					firstKeyPressDinner = false;
					insulineRatioDinner.setText("");
				}
			} else if (insulineRatioSnack.isFocused()) {
				if (firstKeyPressSnack) {
					firstKeyPressSnack = false;
					insulineRatioSnack.setText("");
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public void fillData() {

		float breakfastRatio = 0;
		float lunchRatio = 0;
		float snackRatio = 0;
		float dinnerRatio = 0;

		Cursor cSettingInsulineRatioBreakfast = dbHelper
				.fetchSettingByName(DbSettings.setting_insuline_ratio_breakfast);
		cSettingInsulineRatioBreakfast.moveToFirst();
		breakfastRatio = cSettingInsulineRatioBreakfast
				.getFloat(cSettingInsulineRatioBreakfast
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		Cursor cSettinginsulineratioLunch = dbHelper
				.fetchSettingByName(DbSettings.setting_insuline_ratio_lunch);
		cSettinginsulineratioLunch.moveToFirst();
		lunchRatio = cSettinginsulineratioLunch
				.getFloat(cSettinginsulineratioLunch
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		Cursor cSettingInsulineRatioSnack = dbHelper
				.fetchSettingByName(DbSettings.setting_insuline_ratio_snack);
		cSettingInsulineRatioSnack.moveToFirst();
		snackRatio = cSettingInsulineRatioSnack
				.getFloat(cSettingInsulineRatioSnack
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		Cursor cSettingInsulineRatioDinner = dbHelper
				.fetchSettingByName(DbSettings.setting_insuline_ratio_dinner);
		cSettingInsulineRatioDinner.moveToFirst();
		dinnerRatio = cSettingInsulineRatioDinner
				.getFloat(cSettingInsulineRatioDinner
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		insulineRatioBreakfast.setText("" + breakfastRatio);
		insulineRatioLunch.setText("" + lunchRatio);
		insulineRatioSnack.setText("" + snackRatio);
		insulineRatioDinner.setText("" + dinnerRatio);

		cSettingInsulineRatioDinner.close();
		cSettingInsulineRatioSnack.close();
		cSettinginsulineratioLunch.close();
		cSettingInsulineRatioBreakfast.close();
	}

	// on click update
	public void onClickUpdate() {
		updateValues();
		// go back
		try {
			ActivityGroupSettings.group.back();
		} catch (Exception e){
		}
	}

	private void updateValues() {
		float breakfastRatio = 0;
		float lunchRatio = 0;
		float snackRatio = 0;
		float dinnerRatio = 0;

		try {
			breakfastRatio = Float.parseFloat(insulineRatioBreakfast.getText()
					.toString());
		} catch (Exception e) {
			breakfastRatio = 0;
		}
		try {
			lunchRatio = Float.parseFloat(insulineRatioLunch.getText()
					.toString());
		} catch (Exception e) {
			lunchRatio = 0;
		}
		try {
			snackRatio = Float.parseFloat(insulineRatioSnack.getText()
					.toString());
		} catch (Exception e) {
			snackRatio = 0;
		}
		try {
			dinnerRatio = Float.parseFloat(insulineRatioDinner.getText()
					.toString());
		} catch (Exception e) {
			dinnerRatio = 0;
		}

		// Round the calculated floats
		float p = (float) Math.pow(10, 2);
		breakfastRatio = Math.round(breakfastRatio * p) / p;
		lunchRatio = Math.round(lunchRatio * p) / p;
		snackRatio = Math.round(snackRatio * p) / p;
		dinnerRatio = Math.round(dinnerRatio * p) / p;

		// make sure dbHelper is open
		dbHelper.open();

		dbHelper.updateSettingsByName(
				DbSettings.setting_insuline_ratio_breakfast, ""
						+ breakfastRatio);
		dbHelper.updateSettingsByName(DbSettings.setting_insuline_ratio_lunch,
				"" + lunchRatio);
		dbHelper.updateSettingsByName(DbSettings.setting_insuline_ratio_snack,
				"" + snackRatio);
		dbHelper.updateSettingsByName(DbSettings.setting_insuline_ratio_dinner,
				"" + dinnerRatio);
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
