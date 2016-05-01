// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Exercise;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupExercise;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterCharSequenceForASpinner;
import com.hippoandfriends.helpdiabetes.Custom.CustomSimpleArrayAdapterForASpinner;
import com.hippoandfriends.helpdiabetes.Objects.DBNameAndID;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;
import com.hippoandfriends.helpdiabetes.Show.ShowHomeTab;
import com.hippoandfriends.helpdiabetes.slider.DateSlider;
import com.hippoandfriends.helpdiabetes.slider.DateTimeSlider;

public class ShowAddExerciseEvent extends Activity {
	private Spinner spinnerExerciseTypes, spinnerDuration;
	private EditText etDescription;
	private Button btAdd, btDelete, btUpdateDateAndHour;
	private DbAdapter dbHelper;
	private static final int requestCodeAddExerciseType = 1;
	private Calendar mCalendar;
	private Functions functions;
	private List<DBNameAndID> objects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_exercise_event, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupExercise.group.parent
				.trackPageView(TrackingValues.pageShowExerciseTab);

		dbHelper = new DbAdapter(this);
		functions = new Functions();
		spinnerExerciseTypes = (Spinner) findViewById(R.id.spinnerSportType);
		spinnerDuration = (Spinner) findViewById(R.id.spinnerDuration);
		etDescription = (EditText) findViewById(R.id.editText1);

		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);
		btUpdateDateAndHour = (Button) findViewById(R.id.buttonUpdateDateAndHour);

		btDelete.setVisibility(View.GONE);

		spinnerExerciseTypes
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// track we come here
						ActivityGroupExercise.group.parent.trackEvent(
								TrackingValues.eventCategoryExercise,
								TrackingValues.eventCategoryExerciseChangeType);
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		spinnerDuration.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// track we come here
				ActivityGroupExercise.group.parent.trackEvent(
						TrackingValues.eventCategoryExercise,
						TrackingValues.eventCategoryExerciseChangeDuration);
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// track we come here
				ActivityGroupExercise.group.parent.trackEvent(
						TrackingValues.eventCategoryExercise,
						TrackingValues.eventCategoryExerciseAddToTracking);

				onClickAdd(v);
			}
		});

		btUpdateDateAndHour.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(0);
			}
		});

		etDescription.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we do the add code
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						onClickAdd(null);
					}
				}
				// if we dont return false our text wont get in the
				// edittext
				return false;
			}
		});
	}

	private void updateTimeAndTimeTextView(Calendar selectedDate) {
		// set the local variable = the given variable
		mCalendar = selectedDate;

		// update the dateText view with the corresponding date
		btUpdateDateAndHour.setText(android.text.format.DateFormat
				.getDateFormat(this).format(mCalendar.getTime())
				+ " "
				+ functions.getTimeFromDate(mCalendar.getTime()));
	}

	private DateSlider.OnDateSetListener mDateTimeSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			// track we come here
			ActivityGroupExercise.group.parent.trackEvent(
					TrackingValues.eventCategoryExercise,
					TrackingValues.eventCategoryExerciseUpdateTime);

			updateTimeAndTimeTextView(selectedDate);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		// this method is called after invoking 'showDialog' for the first time
		// here we initiate the corresponding DateSlideSelector and return the
		// dialog to its caller
		return new DateTimeSlider(ActivityGroupExercise.group,
				mDateTimeSetListener, mCalendar);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// create a new date with current date and hour
		mCalendar = Calendar.getInstance();
		updateTimeAndTimeTextView(Calendar.getInstance());
		
		dbHelper.open();
		fillSpinnerSportType();
		fillSpinnerDuration();

		if (getIntent().getExtras().getString(DataParser.whatToDo)
				.equals(DataParser.doUpdateExerciseEvent))
			setExistingValues();

		setDefaultSpinner();
	}

	private void setDefaultSpinner() {
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).getId() == ActivityGroupMeal.group.getFoodData().defaultExerciseTypeID) {
				spinnerExerciseTypes.setSelection(i);
			}
		}
	}

	private void setExistingValues() {
		dbHelper.open();
		Cursor cExerciseEvent = dbHelper.fetchExerciseEventByID(getIntent()
				.getExtras().getLong(DataParser.idExerciseEvent));
		cExerciseEvent.moveToFirst();

		etDescription
				.setText(cExerciseEvent.getString(cExerciseEvent
						.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_DESCRIPTION)));

		setRightItemInSpinner(cExerciseEvent
				.getLong(cExerciseEvent
						.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)));

		cExerciseEvent.close();

		btAdd.setText(getResources().getString(R.string.update));
		btDelete.setVisibility(View.VISIBLE);
	}

	private void setRightItemInSpinner(long exerciseTypeID) {
		dbHelper.open();
		int position = 0;
		Cursor cTemp = dbHelper.fetchAllExerciseTypes();
		cTemp.moveToFirst();
		do {
			if (cTemp.getLong(cTemp
					.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_ID)) == exerciseTypeID) {
				position = cTemp.getPosition();
			}
		} while (cTemp.moveToNext());
		cTemp.close();

		spinnerExerciseTypes.setSelection(position);
	}

	private void fillSpinnerSportType() {
		DbAdapter dbHelper = new DbAdapter(this);
		dbHelper.open();
		objects = new ArrayList<DBNameAndID>();
		Cursor cExerciseTypes = dbHelper.fetchAllExerciseTypes();
		if (cExerciseTypes.getCount() > 0) {
			cExerciseTypes.moveToFirst();
			do {
				objects.add(new DBNameAndID(
						cExerciseTypes
								.getLong(cExerciseTypes
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_ID)),
						cExerciseTypes.getString(cExerciseTypes
								.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME)),
						""));
			} while (cExerciseTypes.moveToNext());
		}
		cExerciseTypes.close();
		dbHelper.close();

		CustomSimpleArrayAdapterForASpinner adapter = new CustomSimpleArrayAdapterForASpinner(
				this, android.R.layout.simple_spinner_item, objects, 25);

		spinnerExerciseTypes.setAdapter(adapter);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	private void fillSpinnerDuration() {
		CustomArrayAdapterCharSequenceForASpinner adapter = new CustomArrayAdapterCharSequenceForASpinner(
				this, android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.standard_duration));

		spinnerDuration.setAdapter(adapter);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	public void onClickAdd(View view) {
		dbHelper.open();

		// create new exercise event
		int startTime = 0;
		int endTime = 0;

		// Fill startTime with seconds of time
		startTime = (functions.getHour(mCalendar) * 3600)
				+ (functions.getMinutes(mCalendar) * 60);

		// Fill endTime
		// The * 1800 comes from 30 minutes * position + 1
		endTime = ((spinnerDuration.getSelectedItemPosition() + 1) * 1800);

		// create exerciseEvent
		dbHelper.createExerciseEvent(etDescription.getText().toString(),
				startTime, endTime, spinnerExerciseTypes.getSelectedItemId(),
				functions.getDateAsStringFromCalendar(mCalendar));

		ActivityGroupTracking.group.restartThisActivity();

		etDescription.setText("");

		// Go to tracking tab when clicked on add
		ShowHomeTab parentActivity;
		parentActivity = (ShowHomeTab) this.getParent().getParent();
		parentActivity.goToTab(DataParser.activityIDTracking);

	}

	public void onClickDelete(View view) {
		dbHelper.open();
		dbHelper.deleteExerciseEventByID(getIntent().getExtras().getLong(
				DataParser.idExerciseEvent));
		setResult(RESULT_OK);
		finish();
	}

	// if we come back from addExerciseType we have to update the spinner
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case requestCodeAddExerciseType:
			if (resultCode == RESULT_OK)
				fillSpinnerSportType();
			break;
		}
	}

	// if we press the back button on this activity we have to show a popup to
	// exit
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showPopUpToExitApplication();
			// when we return true here we wont call the onkeydown from
			// activitygroup
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
					ActivityGroupExercise.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupExercise.group);
		builder.setMessage(getResources().getString(R.string.sureToExit))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}
}
