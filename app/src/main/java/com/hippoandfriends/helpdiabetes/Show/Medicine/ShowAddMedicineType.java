// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Medicine;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowAddMedicineType extends Activity {

	private EditText etName, etUnit;
	private Button btAdd, btDelete, btBack, btStandard;
	private long existingMedicineTypeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_medicine_type, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupSettings.group.parent
				.trackPageView(TrackingValues.pageShowSettingMedicineTypesCreateType);

		etName = (EditText) findViewById(R.id.editText1);
		etUnit = (EditText) findViewById(R.id.editText2);

		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);
		btStandard = (Button) findViewById(R.id.buttonStandard);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

		btStandard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {// track we come here
					ActivityGroupSettings.group.parent.trackEvent(
							TrackingValues.eventCategorySettings,
							TrackingValues.eventCategorySettingsDefaultMedicineType);
				} catch (NullPointerException e) {
				} 
				
				onClickStandard();
			}
		});

		// standard hide the button delete
		btDelete.setVisibility(View.GONE);

		btAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onClickAdd();
			}
		});

		btDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {// track we come here
					ActivityGroupSettings.group.parent.trackEvent(
							TrackingValues.eventCategorySettings,
							TrackingValues.eventCategorySettingsDeleteMedicineType);
				} catch (NullPointerException e) {
				} 
				
				onClickDelete();
			}
		});
	}

	private void onClickStandard() {
		// hide button delete
		btDelete.setVisibility(View.GONE);

		// hide button standard
		btStandard.setVisibility(View.GONE);

		// update default in db
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.updateSettingsByName(DbSettings.setting_default_medicine_type_ID, ""
				+ existingMedicineTypeID);
		db.close();

		// update default medicine type ID in food data
		ActivityGroupMeal.group.getFoodData().defaultMedicineTypeID = existingMedicineTypeID;

		// update show medicine types
		ActivityGroupSettings.group.getMedicineTypes().refresh();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// we put a try around this code becaus when we come here to create a
		// NEW medicine type this will return a nullpointerexception
		try {
			existingMedicineTypeID = getIntent().getExtras().getLong(
					DataParser.idMedicineType);

			// fill the textview with the current medicine type we selected
			fillEditTexts();

			// check if we can show the delete button
			showDeleteButton();

			// check if we can show the button mark as default
			showDefaultButton();

		} catch (NullPointerException e) {
			existingMedicineTypeID = -1;

			// hide the button mark default when we create a new one
			btStandard.setVisibility(View.GONE);
		}
	}

	private void showDefaultButton() {
		if (existingMedicineTypeID == ActivityGroupMeal.group.getFoodData().defaultMedicineTypeID) {
			// hide when the item is already default
			btStandard.setVisibility(View.GONE);
		}
	}

	// This method is called when we clicked on a already existing exercise type
	// This method will check and show the dlete button
	private void showDeleteButton() {
		if (canWeDelete()) {
			btDelete.setVisibility(View.VISIBLE);
		}
	}

	private boolean canWeDelete() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		// if the medicine type is not in use in medicine events
		// and the medicine type is not the last one
		// and the medicine type is not the default one!
		if (db.fetchMedicineEventByMedicineTypeID(existingMedicineTypeID)
				.getCount() == 0
				&& db.fetchAllMedicineTypes().getCount() > 1
				&& existingMedicineTypeID != ActivityGroupMeal.group
						.getFoodData().defaultMedicineTypeID) {
			// we can return true to show button delete
			return true;
		}
		db.close();

		// else we return false
		return false;
	}

	private void fillEditTexts() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		Cursor cMedicineType = db
				.fetchMedicineTypesByID(existingMedicineTypeID);
		if (cMedicineType.getCount() > 0) {
			cMedicineType.moveToFirst();
			etName.setText(cMedicineType.getString(cMedicineType
					.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME)));
			etUnit.setText(cMedicineType.getString(cMedicineType
					.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINEUNIT)));
		} else {
			existingMedicineTypeID = -1;
		}
		cMedicineType.close();

		db.close();
	}

	private void onClickAdd() {
		if (etName.length() > 0) {
			if (etUnit.length() > 0) {
				// if we get here we can add or update medicine type
				DbAdapter db = new DbAdapter(this);
				db.open();

				// create new one
				if (existingMedicineTypeID == -1) {
					try {// track we come here
						ActivityGroupSettings.group.parent.trackEvent(
								TrackingValues.eventCategorySettings,
								TrackingValues.eventCategorySettingsAddMedicineType);
					} catch (NullPointerException e) {
					} 
					
					db.createMedicineType(etName.getText().toString(), etUnit
							.getText().toString());
				} else {
					try {// track we come here
						ActivityGroupSettings.group.parent.trackEvent(
								TrackingValues.eventCategorySettings,
								TrackingValues.eventCategorySettingsUpdateMedicineType);
					} catch (NullPointerException e) {
					} 
					
					// update one
					db.updateMedicineTypeByID(existingMedicineTypeID, etName
							.getText().toString(), etUnit.getText().toString());
				}
				db.close();

				// refresh the list
				ActivityGroupSettings.group.getMedicineTypes().refresh();

				// go back
				ActivityGroupSettings.group.back();
			} else {
				Toast.makeText(this,
						getResources().getString(R.string.unit_cant_be_empty),
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.name_cant_be_empty),
					Toast.LENGTH_LONG).show();
		}
	}

	private void onClickDelete() {
		// first check if the medicine type is in use ( when the user opens this
		// screen, then adds a medicine event and comes back
		// the button delete will be visible while we cant delete the medicine
		// type! )
		if (canWeDelete()) {
			DbAdapter db = new DbAdapter(this);
			db.open();
			db.deleteMedicineTypeByID(existingMedicineTypeID);
			db.close();

			// delete if from the list
			ActivityGroupSettings.group.getMedicineTypes().deleteFromList(
					existingMedicineTypeID);
		}
		// go back
		ActivityGroupSettings.group.back();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
