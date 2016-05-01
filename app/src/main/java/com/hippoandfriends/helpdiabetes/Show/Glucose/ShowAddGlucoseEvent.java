// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Glucose;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupGlucose;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;
import com.hippoandfriends.helpdiabetes.Show.ShowHomeTab;
import com.hippoandfriends.helpdiabetes.slider.DateSlider;
import com.hippoandfriends.helpdiabetes.slider.DateTimeSlider;

public class ShowAddGlucoseEvent extends Activity {
	private Button btUpdateDateAndHour, btAdd;

	private Calendar mCalendar;
	private DbAdapter dbHelper;
	private Functions functions;
	private long glucoseUnitID;

	private EditText etAmount;
	private TextView tvUnit;
	private Button btUp, btDown;

	// need to hold the amount
	private float amount;
	private final int MINIMUM = 0;
	private final int MAXIMUM = 700;

	// stuff needed to increment or decrement the amount
	private final long REPEAT_DELAY = 50;
	private Handler repeateUpdateHandler = new Handler();
	private boolean autoIncrement = false;
	private boolean autoDecrement = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_glucose_event, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupGlucose.group.parent
				.trackPageView(TrackingValues.pageShowGlucoseTab);

		dbHelper = new DbAdapter(this);
		functions = new Functions();

		etAmount = (EditText) findViewById(R.id.editTextAmount);
		tvUnit = (TextView) findViewById(R.id.textViewUnit);
		btUp = (Button) findViewById(R.id.increment);
		btDown = (Button) findViewById(R.id.decrement);
		btUpdateDateAndHour = (Button) contentView
				.findViewById(R.id.buttonUpdateDateAndHour);
		btAdd = (Button) findViewById(R.id.buttonAdd);

		glucoseUnitID = -1;

		btUpdateDateAndHour.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(0);
			}
		});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// track we come here
				ActivityGroupGlucose.group.parent.trackEvent(
						TrackingValues.eventCategoryGlucose,
						TrackingValues.eventCategoryGlucoseAddToTracking);

				onClickAdd();
			}
		});

		// add listeners to the increment and decrement buttons
		// Increment once for a click
		btUp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				increment();
			}
		});

		// Auto increment for a long click
		btUp.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View arg0) {
				autoIncrement = true;
				repeateUpdateHandler.post(new RepetetiveUpdater());
				return false;
			}
		});

		// When the button is released, if we're auto incrementing, stop
		btUp.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
					autoIncrement = false;
				}
				return false;
			}
		});

		// Decrement once for a click
		btDown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				decrement();
			}
		});

		// Auto Decrement for a long click
		btDown.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View arg0) {
				autoDecrement = true;
				repeateUpdateHandler.post(new RepetetiveUpdater());
				return false;
			}
		});

		// When the button is released, if we're auto decrementing, stop
		btDown.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP && autoDecrement) {
					autoDecrement = false;
				}
				return false;
			}
		});

		etAmount.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we do the add code
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						onClickAdd();
					}
				}
				// if we dont return false our text wont get in the
				// edittext
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCalendar = Calendar.getInstance();
		updateTimeAndTimeTextView(mCalendar);

		dbHelper.open();
		fillTextViewGlucoseUnit();
	}

	private void fillTextViewGlucoseUnit() {
		dbHelper.open();
		Cursor cSetting = dbHelper
				.fetchSettingByName(DbSettings.setting_glucose_unit);
		cSetting.moveToFirst();

		glucoseUnitID = cSetting.getLong(cSetting
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		cSetting.close();

		Cursor cGlucoseUnit = dbHelper
				.fetchBloodGlucoseUnitsByID(glucoseUnitID);

		cGlucoseUnit.moveToFirst();

		tvUnit.setText(cGlucoseUnit.getString(cGlucoseUnit
				.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_UNIT)));

		cGlucoseUnit.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	private void updateTimeAndTimeTextView(Calendar selectedDate) {
		// set the local variable = the given variable
		mCalendar = selectedDate;

		btUpdateDateAndHour.setText(android.text.format.DateFormat
				.getDateFormat(this).format(mCalendar.getTime())
				+ " "
				+ functions.getTimeFromDate(mCalendar.getTime()));
	}

	private DateSlider.OnDateSetListener mDateTimeSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			// track we come here
			ActivityGroupGlucose.group.parent.trackEvent(
					TrackingValues.eventCategoryGlucose,
					TrackingValues.eventCategoryGlucoseUpdateTime);

			updateTimeAndTimeTextView(selectedDate);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		// this method is called after invoking 'showDialog' for the first time
		// here we initiate the corresponding DateSlideSelector and return the
		// dialog to its caller
		final Calendar c = Calendar.getInstance();
		return new DateTimeSlider(ActivityGroupGlucose.group,
				mDateTimeSetListener, c);
	}

	private void onClickAdd() {
		try {
			amount = Float.parseFloat(etAmount.getText().toString());
		} catch (Exception e) {
			amount = 0;
		}

		if (amount > 0) {
			dbHelper.open();

			dbHelper.createBloodGlucoseEvent(amount,
					functions.getDateAsStringFromCalendar(mCalendar),
					glucoseUnitID);

			ActivityGroupTracking.group.restartThisActivity();

			// / Go to tracking tab when clicked on add
			ShowHomeTab parentActivity;
			parentActivity = (ShowHomeTab) this.getParent().getParent();
			parentActivity.goToTab(DataParser.activityIDTracking);

			amount = 0;
			etAmount.setText("");
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.amountCantBeZero),
					Toast.LENGTH_LONG).show();
			amount = 0;
			etAmount.setText("");
		}
	}

	/**
	 * This little guy handles the auto part of the auto incrementing feature.
	 * In doing so it instantiates itself. There has to be a pattern name for
	 * that...
	 * 
	 * @author Jeffrey F. Cole
	 * 
	 */
	class RepetetiveUpdater implements Runnable {
		public void run() {
			if (autoIncrement) {
				increment();
				repeateUpdateHandler.postDelayed(new RepetetiveUpdater(),
						REPEAT_DELAY);
			} else if (autoDecrement) {
				decrement();
				repeateUpdateHandler.postDelayed(new RepetetiveUpdater(),
						REPEAT_DELAY);
			}
		}
	}

	public void increment() {
		if (amount == 0) {
			// set standard == 100 for mg/dl
			if (tvUnit.getText().equals("mg/dL"))
				amount = 100;
			else
				amount = 5;
		} else if (amount < MAXIMUM) {
			amount = amount + 1;
		} else {
			amount = MINIMUM;
		}
		etAmount.setText("" + amount);
	}

	public void decrement() {
		if (amount > MINIMUM) {
			amount = amount - 1;
		} else {
			amount = MAXIMUM;
		}
		etAmount.setText("" + amount);
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
					ActivityGroupGlucose.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupGlucose.group);
		builder.setMessage(getResources().getString(R.string.sureToExit))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}
}
