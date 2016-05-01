// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Show.Settings.ShowSettingsDBLanguage;
import com.hippoandfriends.helpdiabetes.Show.Settings.ShowSettingsDefaultMedicineType;
import com.hippoandfriends.helpdiabetes.Show.Settings.ShowSettingsInsulineRatio;
import com.hippoandfriends.helpdiabetes.Show.Settings.ShowSettingsMealTimes;

public class ShowStart extends Activity {

	private DbAdapter dbHelper;
	private TextView tv2;

	private final int requestCodeSelectedDBFood = 1;
	private final int requestCodeMealTimes = 2;
	private final int requestCodeKHRatio = 3;
	private final int requestCodeDefaultMedicineType = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_start);

		tv2 = (TextView) findViewById(R.id.textView2);
		tv2.setText(getResources().getString(R.string.checkingDatabase));

		dbHelper = new DbAdapter(this);
		new checkDatabase().execute();
	}

	private class checkDatabase extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				dbHelper.createDataBase();
			} catch (IOException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			goToRightPage();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case requestCodeSelectedDBFood:
			showPopUpDiabetesPatient();
			break;
		case requestCodeMealTimes:
			startIntentInsulineRatios();
			break;
		case requestCodeKHRatio:
			showActivityDefaultMedicineType();
			break;
		case requestCodeDefaultMedicineType:
			startHomeActivityAndStopThisActivity();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showActivityDefaultMedicineType() {
		Intent k = new Intent(this, ShowSettingsDefaultMedicineType.class);
		// mark the intent as first time
		k.putExtra(DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(k, requestCodeDefaultMedicineType);
	}

	private void goToRightPage() {
		// check to see if it is the first time application starts
		dbHelper.open();

		doUpdates(dbHelper);
		
		Cursor cSetting = dbHelper
				.fetchSettingByName(DbSettings.setting_language);
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();

			if (cSetting.getLong(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 0) {

				// first time application starts!
				showPopUpAbout();

			} else {
				// mark app as startup ( to show dialog when we have already
				// food selected )
				dbHelper.updateSettingsByName(DbSettings.setting_startUp, "1");
				startHomeActivityAndStopThisActivity();
			}
		} else {
			// mark app as startup ( to show dialog when we have already food
			// selected )
			dbHelper.updateSettingsByName(DbSettings.setting_startUp, "1");
			startHomeActivityAndStopThisActivity();
		}
		cSetting.close();
		dbHelper.close();
	}
 
	private void doUpdates(DbAdapter dbHelper) {
		Cursor cDatabaseVersion = dbHelper.fetchSettingByName(DbSettings.setting_database_version);
		if(cDatabaseVersion.getCount() > 0){
			cDatabaseVersion.moveToFirst();
	 		
			if(cDatabaseVersion.getString(cDatabaseVersion.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)).equals("1")){
				//from version 1 to version 2
				dbHelper.updateSettingsByName(DbSettings.setting_database_version, "2");
				
				//rename "Canada Francaise" to "Canada Francais" = ID 6
				dbHelper.updateFoodLanguageLanguage(6, "Français - Switserland");

				//rename "USDA National Nitrution Datbase to "English - USDA National Nitrution Database" = ID 16
				dbHelper.updateFoodLanguageLanguage(16, "English - USDA National Nutrition Database");
				
				
			}
		}
		cDatabaseVersion.close();
	}

	private void startActivityDBLanguage() {
		// start the set database language page
		Intent m = new Intent(this, ShowSettingsDBLanguage.class).putExtra(
				DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(m, requestCodeSelectedDBFood);
	}

	private void showPopUpDiabetesPatient() {
		new AlertDialog.Builder(this)
				.setMessage(
						getResources().getString(
								R.string.are_you_diabetes_patient))
				.setPositiveButton(getResources().getString(R.string.yes),
						new OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								startIntentMealTimes();
							}
						})
				.setNegativeButton(getResources().getString(R.string.no),
						new OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								startHomeActivityAndStopThisActivity();
							}
						}).show();
	}
 
	// first start up
	private void showPopUpAbout() {
		String versionName = "";
		
		try {
			versionName = "Versie " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Show a dialog with info
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.pref_about))
				.setPositiveButton(getResources().getString(R.string.oke),
						new OnClickListener() { 
							public void onClick(DialogInterface dialog,
									int which) {
								startActivityDBLanguage();
							}
						})
				.setMessage(
						versionName
								+ " \n\n"
								+ getResources().getString(
										R.string.about_text_copyright)
								+ " \n\n"
								+ getResources().getString(R.string.about_text))
				.setNegativeButton(getResources().getString(R.string.cancel),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).show();
	}

	private void startIntentMealTimes() {
		Intent k = new Intent(this, ShowSettingsMealTimes.class);
		// mark the intent as first time
		k.putExtra(DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(k, requestCodeMealTimes);
	}

	private void startIntentInsulineRatios() {
		Intent k = new Intent(this, ShowSettingsInsulineRatio.class);
		// mark the intent as first time
		k.putExtra(DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(k, requestCodeKHRatio);
	}

	private void startHomeActivityAndStopThisActivity() {
		// start home activity
		Intent i = new Intent(this, ShowHomeTab.class);
		startActivity(i);

		// stop this activity
		finish();
	}
}
