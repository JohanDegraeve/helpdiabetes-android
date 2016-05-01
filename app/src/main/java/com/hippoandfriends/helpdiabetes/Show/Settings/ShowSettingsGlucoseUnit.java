// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Settings;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowSettingsGlucoseUnit extends Activity {
	private DbAdapter dbHelper;
	private RadioGroup rgGlucoseUnit;

	private Button btNext, btBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_glucose_unit);

		// track we come here
		ActivityGroupSettings.group.parent
				.trackPageView(TrackingValues.pageShowSettingGlucoseUnit);

		dbHelper = new DbAdapter(this);
		rgGlucoseUnit = (RadioGroup) findViewById(R.id.radioGroupGlucoseUnit);

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
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillRadioGroup();

		// set on checked change listener after we filled the radio group!
		rgGlucoseUnit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				onRadioGroupSelectedChange();
			}
		});

		// check if we need to show the button next
		// only need to show first time application starts
		try {
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doFirstTime)) {
				btNext.setVisibility(View.VISIBLE);
				// hide the button back
				btBack.setVisibility(View.GONE);
			}
		} catch (Exception e) {
		}
	}

	private void fillRadioGroup() {
		Cursor cGlucoseUnits = dbHelper.fetchAllBloodGlucoseUnits();
		Cursor cSettingGlucoseUnit = dbHelper
				.fetchSettingByName(DbSettings.setting_glucose_unit);
		cSettingGlucoseUnit.moveToFirst();

		if (cGlucoseUnits.getCount() > 0) {
			cGlucoseUnits.moveToFirst();
			do {
				RadioButton rb = new RadioButton(this);
				rb.setText(cGlucoseUnits.getString(cGlucoseUnits
						.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_UNIT)));

				rb.setId(cGlucoseUnits.getInt(cGlucoseUnits
						.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_ID)));

				rb.setTextColor(getResources().getColor(R.color.ColorText));

				if (cSettingGlucoseUnit
						.getLong(cSettingGlucoseUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == cGlucoseUnits
						.getLong(cGlucoseUnits
								.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_ID))) {
					rb.setChecked(true);
				}

				rgGlucoseUnit.addView(rb);
			} while (cGlucoseUnits.moveToNext());
		}
		cSettingGlucoseUnit.close();
		cGlucoseUnits.close();
	}

	private void onRadioGroupSelectedChange() {
		try {// track we come here
			ActivityGroupSettings.group.parent.trackEvent(
					TrackingValues.eventCategorySettings,
					TrackingValues.eventCategorySettingsChangeBloodGlucoseUnit);
		} catch (NullPointerException e) {
		} 
		
		// update the setting glucose unit
		dbHelper.updateSettingsByName(DbSettings.setting_glucose_unit, ""
				+ rgGlucoseUnit.getCheckedRadioButtonId());

		try {
			// go back
			ActivityGroupSettings.group.back();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}
}
