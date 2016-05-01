// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Exercise;

/*
 * This class is used to add a new exercise type
 * This class is also used to update a existing exercise type
 * This class is also used to delete a existing exercise type when it is not in use and it is not the last existing exercise type
 */

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

public class ShowAddExerciseType extends Activity {
	private EditText etName;
	private Button btAdd, btDelete, btBack, btStandard;
	private long excistingExerciseTypeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_exercise_type, null);
		setContentView(contentView);

		// track we come here
				ActivityGroupSettings.group.parent
						.trackPageView(TrackingValues.pageShowSettingActivityTypesCreateType);
		
		etName = (EditText) findViewById(R.id.editTextShowAddExerciseTypeName);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);
		btStandard = (Button) findViewById(R.id.buttonStandard);
		btBack = (Button) findViewById(R.id.buttonBack);

		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
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
							TrackingValues.eventCategorySettingsDeleteExerciseType);
				} catch (NullPointerException e) {
				} 
				
				onClickDelete();
			}
		});

		btStandard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {// track we come here
					ActivityGroupSettings.group.parent.trackEvent(
							TrackingValues.eventCategorySettings,
							TrackingValues.eventCategorySettingsDefaultExerciseType);
				} catch (NullPointerException e) {
				} 
				onClickStandard();
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
		db.updateSettingsByName(DbSettings.setting_default_exercise_type_ID, ""
				+ excistingExerciseTypeID);
		db.close();

		// update default medicine type ID in food data
		ActivityGroupMeal.group.getFoodData().defaultExerciseTypeID = excistingExerciseTypeID;

		// update show exercise types
		ActivityGroupSettings.group.getExerciseTypes().refresh();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// we put a try around this code becaus when we come here to create a
		// NEW exercise type this will return a nullpointerexception
		try {
			excistingExerciseTypeID = getIntent().getExtras().getLong(
					DataParser.idExerciseType);

			// fill the textview with the current exercise type we selected
			fillEditText();

			// check if we can show the delete button
			showDeleteButton();

			// check if we can show the button mark as default
			showDefaultButton();

		} catch (NullPointerException e) {
			excistingExerciseTypeID = -1; 

			// hide button set standard when we create a new one
			btStandard.setVisibility(View.GONE);
		}
	}

	private void showDefaultButton() {
		if (excistingExerciseTypeID == ActivityGroupMeal.group.getFoodData().defaultExerciseTypeID) {
			// hide when the item is already default
			btStandard.setVisibility(View.GONE);
		}
	}

	// This method is called when we clicked on a already existing exercise type
	// This method will check if we can show the delete button for this exercise
	// type ( if it is not in use and not the last one )
	private void showDeleteButton() {
		// if the exercise type is not in use in exercise events
		// and the exercise type is not the last one
		if (canWeDeleteExerciseType()) {
			btDelete.setVisibility(View.VISIBLE);
		}
	}

	// This method is called when this activity starts to check if we can show
	// the delete button
	// This method is also called when we press on the button delete to check if
	// we can still delete the exercise type
	private boolean canWeDeleteExerciseType() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		// if the exercise type is not in use in exercise events
		// and the exercise type is not the last one
		if (db.fetchExerciseEventByExerciseTypeID(excistingExerciseTypeID)
				.getCount() == 0
				&& db.fetchAllExerciseTypes().getCount() > 1
				&& excistingExerciseTypeID != ActivityGroupMeal.group
						.getFoodData().defaultExerciseTypeID) {
			// we can return true to delete the exercise type
			return true;
		}
		db.close();

		// else we return false
		return false;
	}

	private void fillEditText() {
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cExerciseType = db
				.fetchExerciseTypeByID(excistingExerciseTypeID);
		// if we found the item in the database
		if (cExerciseType.getCount() > 0) {
			cExerciseType.moveToFirst();
			etName.setText(cExerciseType.getString(cExerciseType
					.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME)));
		} else {
			// when the item didnt exists in the datase
			excistingExerciseTypeID = -1;
		}
		cExerciseType.close();
		db.close();
	}

	// on click button add
	private void onClickAdd() {
		if (etName.length() > 0) {
			DbAdapter db = new DbAdapter(this);
			db.open();

			// create new one
			if (excistingExerciseTypeID == -1) {
				try {// track we come here
					ActivityGroupSettings.group.parent.trackEvent(
							TrackingValues.eventCategorySettings,
							TrackingValues.eventCategorySettingsAddExerciseType);
				} catch (NullPointerException e) {
				} 
				
				
				// create the exercise type
				long exerciseType = db.createExerciseType(etName.getText()
						.toString(), "");

				db.close();

				// add the created item to the list of exercise types on the
				// exercise
				// TYPES
				// activity
				ActivityGroupSettings.group.getExerciseTypes()
						.addExerciseTypeToList(exerciseType);

				// go back to the list
				ActivityGroupSettings.group.back();
			} else {
				try {// track we come here
					ActivityGroupSettings.group.parent.trackEvent(
							TrackingValues.eventCategorySettings,
							TrackingValues.eventCategorySettingsUpdateExerciseType);
				} catch (NullPointerException e) {
				} 
				
				// update one
				db.updateExerciseTypeByID(excistingExerciseTypeID, etName
						.getText().toString(), "");

				db.close();

				// refresh the list
				ActivityGroupSettings.group.getExerciseTypes().refreshObject(
						excistingExerciseTypeID, etName.getText().toString());

				// go back
				ActivityGroupSettings.group.back();
			}
		} else {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.exercise_type_cant_be_empty),
					Toast.LENGTH_LONG).show();
		}
	}

	// on click button delete
	private void onClickDelete() {
		// first check if the exercise type is in use ( when the user opens this
		// screen , then adds a exercise event and comes back
		// the button delete will be visible while we cant delete the exercise
		// type! )
		if (canWeDeleteExerciseType()) {
			// delete it from the database
			DbAdapter db = new DbAdapter(this);
			db.open();
			db.deleteExerciseTypeByID(excistingExerciseTypeID);
			db.close();

			// delete it from the list
			ActivityGroupSettings.group.getExerciseTypes().deleteFromList(
					excistingExerciseTypeID);
		}

		// Go back
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
