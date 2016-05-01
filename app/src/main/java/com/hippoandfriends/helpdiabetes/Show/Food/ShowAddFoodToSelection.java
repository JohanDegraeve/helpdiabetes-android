// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

/*
 * This class is uses to add food to the selected food list or update a selected food.
 * This class gets a parameter from its intent with the foodID
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.Custom.CustomSimpleArrayAdapterForASpinner;
import com.hippoandfriends.helpdiabetes.Objects.DBNameAndID;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowAddFoodToSelection extends Activity {
	private DbAdapter dbHelper;

	private TextView textViewSelectedFood;
	private EditText editTextFoodAmound;
	private Spinner spinnerFoodUnits;
	private Button btAdd, btBack, btDelete;

	// The table textview
	private TextView tvRowOneFieldTwo, tvRowOneFieldThree, tvOneItemInSpinner,
			tvRowTwoFieldTwo, tvRowTwoFieldThree, tvRowThreeFieldTwo,
			tvRowThreeFieldThree, tvRowFourFieldTwo, tvRowFourFieldThree,
			tvRowFiveFieldTwo, tvRowFiveFieldThree;

	// To store the selected food in
	private Cursor foodCursor;
	// private CustomSimpleCursorAdapterAddFoodToSelectionSpinner adapter;

	// this boolean is needed to not set "" or standardamount in the
	// editTextFoodAmound on start if we come from showSelectedFood
	private boolean setStandardAmount;

	private Functions functions;

	// used to hide the ones we dont need to show
	private TableRow trCarb, trProt, trFat, trKcal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_food_to_selection, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupMeal.group.parent
				.trackPageView(TrackingValues.pageShowAddFoodToSelection);

		dbHelper = new DbAdapter(this);
		functions = new Functions();

		setStandardAmount = true;

		textViewSelectedFood = (TextView) findViewById(R.id.textViewSelectedFood);
		editTextFoodAmound = (EditText) findViewById(R.id.editTextFoodAmount);
		tvOneItemInSpinner = (TextView) findViewById(R.id.textViewOneValue);

		spinnerFoodUnits = (Spinner) findViewById(R.id.spinnerFoodUnit);
		btDelete = (Button) findViewById(R.id.buttonShowAddFoodDelete);

		tvRowTwoFieldTwo = (TextView) findViewById(R.id.textViewShowAddFoodRowTwoFieldTwo);
		tvRowThreeFieldTwo = (TextView) findViewById(R.id.textViewShowAddFoodRowThreeFieldTwo);
		tvRowFourFieldTwo = (TextView) findViewById(R.id.textViewShowAddFoodRowFourFieldTwo);
		tvRowFiveFieldTwo = (TextView) findViewById(R.id.textViewShowAddFoodRowFiveFieldTwo);

		// fill the listTextViewColumn THREE
		tvRowTwoFieldThree = (TextView) findViewById(R.id.textViewShowAddFoodRowTwoFieldThree);
		tvRowThreeFieldThree = (TextView) findViewById(R.id.textViewShowAddFoodRowThreeFieldThree);
		tvRowFourFieldThree = (TextView) findViewById(R.id.textViewShowAddFoodRowFourFieldThree);
		tvRowFiveFieldThree = (TextView) findViewById(R.id.textViewShowAddFoodRowFiveFieldThree);

		// gething the table textViews
		tvRowOneFieldTwo = (TextView) findViewById(R.id.textViewShowAddFoodRowOneFieldTwo);
		tvRowOneFieldThree = (TextView) findViewById(R.id.textViewShowAddFoodRowOneFieldThree);

		// to hide what we dont need
		trCarb = (TableRow) findViewById(R.id.tableRowCarb);
		trProt = (TableRow) findViewById(R.id.tableRowProt);
		trFat = (TableRow) findViewById(R.id.tableRowFat);
		trKcal = (TableRow) findViewById(R.id.tableRowKcal);

		// new buttons for bether navigation
		btAdd = (Button) findViewById(R.id.buttonShowAddFoodAddAndAddAnother);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupMeal.group.back();
			}
		});

		btDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryMeal,
						TrackingValues.eventCategoryMealDeleteFoodFromSelection);
				
				onClickDeleteFoodFromSelection(v);
			}
		});

		// add a edittext handler afther we did the on resume
		// otherwise the boolean firstKey will already be changed
		editTextFoodAmound.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				// fill the table with the right value
				fillTextViewCalculated();
			}
		});

		editTextFoodAmound.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we save the food to selection
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						// update food
						if (getIntent().getExtras()
								.getString(DataParser.fromWhereWeCome)
								.equals(DataParser.weComeFRomShowSelectedFood)) {
							onClickButtonUpdate();
						} else {
							// add food
							addFood();
						}

						// go back
						ActivityGroupMeal.group.back();
					}
				} // if we dont return false our numbers wont get in the
					// edittext
				return false;
			}
		});

		// when the spinner selected item changes
		spinnerFoodUnits
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (setStandardAmount) {
							// check the standard amount
							checkStandardAmound();
							// fill the textview selected food
							fillTextViewSelectedFood();
							// fill the calculated column
							fillTextViewCalculated();
							// set focus on the edittext and let the keyboard
							// come out
							// setFocusOnEditText();
						}

						// switch standardamount if its false
						if (!setStandardAmount)
							setStandardAmount = true;
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// if we come from show selected food we have to update
				if (getIntent().getExtras()
						.getString(DataParser.fromWhereWeCome)
						.equals(DataParser.weComeFRomShowSelectedFood)) {
					
					// track we come here
					ActivityGroupMeal.group.parent.trackEvent(
							TrackingValues.eventCategoryMeal,
							TrackingValues.eventCategoryMealUpdateFoodFromSelection);
					
					onClickButtonUpdate();
				} else {
					// track we come here
					ActivityGroupMeal.group.parent.trackEvent(
							TrackingValues.eventCategoryMeal,
							TrackingValues.eventCategoryMealAddFoodToSelection);
					
					// else we add a new food item to the selected food list
					addFood();
				}
 
				// go back
				ActivityGroupMeal.group.back();
			}
		});
	}

	private float getInsertedAmund() {
		float returnValue = 0f;
		String stringFoodAmound = "";

		try {
			// replace "," by "."
			stringFoodAmound = editTextFoodAmound.getText().toString();
			stringFoodAmound = stringFoodAmound.replace(",", ".");
		} catch (Exception f) {
		}

		try {
			returnValue = Float.parseFloat(stringFoodAmound);
			returnValue = functions.roundFloats(returnValue, 1);
		} catch (Exception e) {
			returnValue = 0f;
		}

		return returnValue;
	}

	@Override
	protected void onResume() {
		super.onResume();

		dbHelper.open();

		// Get the selected food
		foodCursor = dbHelper.fetchFood(getIntent().getExtras().getLong(
				DataParser.idFood));
		startManagingCursor(foodCursor);

		fillData();

		checkStandardAmound();

		/*
		 * If we come from showSelectedFood we have to: 1. fill in the spinner
		 * with the right unit 2. set the right amount 3. Show the button to
		 * delete the food
		 */

		if (getIntent().getExtras().getString(DataParser.fromWhereWeCome)
				.equals(DataParser.weComeFRomShowSelectedFood)) {
			Cursor cSelectedFood = dbHelper.fetchSelectedFood(getIntent()
					.getExtras().getLong(DataParser.idSelectedFood));
			startManagingCursor(cSelectedFood);
			// 1. fill in the spinner with the right unit
			setSelectedFoodUnitItemInSpinnerSelected(cSelectedFood
					.getInt(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
			// 2. set the right amount
			editTextFoodAmound
					.setText(""
							+ cSelectedFood.getFloat(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)));
			// 3. show the button to delete the food
			btDelete.setVisibility(View.VISIBLE);
			// 5. setStandardAmount = false so that we see our food amount and
			// not "" or standardamount
			setStandardAmount = false;
			cSelectedFood.close();
		} else {
			// if we dont come from showSelectedFood or showFoodTemplates
			// hide the button delete
			btDelete.setVisibility(View.GONE);

			// we have to see if we have a "gram" value , if yes we have to set
			// the gram as default
			setSelectedItemOnSpinnerToGramOrMl();
		}

		// if we only have 1 item , we have to hide the spinner and show a
		// textview with the unitName
		Cursor cFoodUnit = dbHelper.fetchFoodUnitByFoodId(getIntent()
				.getExtras().getLong(DataParser.idFood));
		if (cFoodUnit.getCount() == 1) {
			spinnerFoodUnits.setVisibility(View.GONE);
			tvOneItemInSpinner.setVisibility(View.VISIBLE);
			cFoodUnit.moveToFirst();
			tvOneItemInSpinner
					.setText(functions.getShorterString(
							cFoodUnit
									.getString(cFoodUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
							6));

			// if we only have 1 unit we show the keyboard
			// setFocusOnEditText();
		} else {
			// if we have more then 1 item we have to hide the keyboard becaus
			// the spinner needs to be selected first
			// hideKeyboard();
			// set focus to the spinner
			// spinnerFoodUnits.requestFocus();
		}
		cFoodUnit.close();

		fillTextViewSelectedFood();
		fillTextViewCalculated();

		// hide the ones we dont need
		hideTheRowsWeDontNeed();
	}

	private void hideTheRowsWeDontNeed() {
		// carb
		if (ActivityGroupMeal.group.getFoodData().showCarb) {
			trCarb.setVisibility(View.VISIBLE);
		} else {
			trCarb.setVisibility(View.GONE);
		}

		// prot
		if (ActivityGroupMeal.group.getFoodData().showProt) {
			trProt.setVisibility(View.VISIBLE);
		} else {
			trProt.setVisibility(View.GONE);
		}

		// fat
		if (ActivityGroupMeal.group.getFoodData().showFat) {
			trFat.setVisibility(View.VISIBLE);
		} else {
			trFat.setVisibility(View.GONE);
		}

		// kcal
		if (ActivityGroupMeal.group.getFoodData().showKcal) {
			trKcal.setVisibility(View.VISIBLE);
		} else {
			trKcal.setVisibility(View.GONE);
		}

	}

	// When we click on the button delete
	public void onClickDeleteFoodFromSelection(View view) {
		dbHelper.open();
		dbHelper.deleteSelectedFood(getIntent().getExtras().getLong(
				DataParser.idSelectedFood));

		// try to refresh the foodlist on the selectedFood page
		ActivityGroupMeal.group.getShowSelectedFood().refreshData();

		// do the selectedFoodCounter--
		ActivityGroupMeal.group.getFoodData().countSelectedFood--;

		// go back
		ActivityGroupMeal.group.back();
	}

	/*
	 * This function wil set the amount = standardamount when standardamount !=
	 * 100 Else it wil set "" as amount
	 */
	private void checkStandardAmound() {
		dbHelper.open();
		Cursor cUnit = dbHelper.fetchFoodUnit(spinnerFoodUnits
				.getSelectedItemId());
		if (cUnit.getCount() > 0) {
			cUnit.moveToFirst();
			if (cUnit
					.getInt(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) != 100) {
				editTextFoodAmound
						.setText(cUnit.getString(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
			} else {
				editTextFoodAmound.setText("");
			}
		}
		cUnit.close();
	}

	private void setSelectedItemOnSpinnerToGramOrMl() {
		dbHelper.open();
		Cursor cUnits = dbHelper.fetchFoodUnitByFoodId(getIntent().getExtras()
				.getLong(DataParser.idFood));

		if (cUnits.getCount() > 0) {
			cUnits.moveToFirst();
			do {
				if (cUnits
						.getString(
								cUnits.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						.equals(ActivityGroupMeal.group.getFoodData().dbTopOneCommonFoodUnit)
						|| cUnits
								.getString(
										cUnits.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
								.equals(ActivityGroupMeal.group.getFoodData().dbTopTwoCommonFoodUnit)) {
					try {
						spinnerFoodUnits.setSelection(cUnits.getPosition());
						// move to last so we stop looping
						cUnits.moveToLast();
					} catch (Exception e) {
					}
				}
			} while (cUnits.moveToNext());
		}

		cUnits.close();
	}

	private void setSelectedFoodUnitItemInSpinnerSelected(int unitId) {
		dbHelper.open();
		int position = 0;
		Cursor cursorTemp = dbHelper.fetchFoodUnitByFoodId(getIntent()
				.getExtras().getLong(DataParser.idFood));
		startManagingCursor(cursorTemp);
		cursorTemp.moveToFirst();
		startManagingCursor(cursorTemp);

		// check the first one ( if the id is the same of the units )
		if (cursorTemp.getInt(cursorTemp
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)) == unitId) {
			position = cursorTemp.getPosition();
		}

		// move next and keep checking if the id is the same
		while (cursorTemp.moveToNext()) {
			if (cursorTemp.getInt(cursorTemp
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)) == unitId) {
				position = cursorTemp.getPosition();
			}
		}

		// This will set the selected item = position that is given
		spinnerFoodUnits.setSelection(position);
		cursorTemp.close();
	}

	// This method will fill the calculated textView ( its under the spinner )
	// This method is called when the spinner selected item changes or when the
	// amount in editText changes
	private void fillTextViewCalculated() {
		dbHelper.open();
		// First get the amount inserted
		float amount = getInsertedAmund();

		float calcCarbs = 0f;
		float calcKcal = 0f;
		float calcFat = 0f;
		float calcProtein = 0f;

		// without this try the app will crash when the device is turned.
		// reason: ( not 100% sure ) when the device turns this method is called
		// without data in the spinner so it wil crash at gething the unit.
		try {
			Cursor cUnit = dbHelper.fetchFoodUnit(spinnerFoodUnits
					.getSelectedItemId());
			cUnit.moveToFirst();

			// new formule: ((amound / standardamound) * foodcompositionValue)
			calcCarbs = ((amount / cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)));

			calcKcal = ((amount / cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));
			calcFat = ((amount / cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)));
			calcProtein = ((amount / cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))) * cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)));

			// Round the calculated floats
			calcCarbs = functions.roundFloats(calcCarbs, 1);
			calcKcal = functions.roundFloats(calcKcal, 1);
			calcFat = functions.roundFloats(calcFat, 1);
			calcProtein = functions.roundFloats(calcProtein, 1);

			// Fill the table with the data
			// 1. Fill column 2 with the standard stuff
			String unitName = cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME));

			// make the unitName shorter if its to long
			unitName = functions.getShorterString(unitName, 6);
 
			tvRowOneFieldTwo
					.setText(cUnit.getString(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))
							+ " " + unitName);

			tvRowOneFieldThree.setText("" + amount + " " + unitName);

			// fill the rest of the table
   
			// carbs 
			tvRowTwoFieldTwo
	 				.setText(""
							+ functions.roundFloats(
									cUnit.getFloat(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)),
									1));
			tvRowTwoFieldThree.setText("" + calcCarbs);
					
			// prot 
			tvRowThreeFieldTwo
					.setText(""
							+ functions.roundFloats(
									cUnit.getFloat(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)),
									1));
			tvRowThreeFieldThree.setText("" + calcProtein);
					 
			// fat
			tvRowFourFieldTwo
					.setText(""
							+ functions.roundFloats(
									cUnit.getFloat(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)),
									1));
			tvRowFourFieldThree.setText("" + calcFat);
			 
		 	
			// kcal
			tvRowFiveFieldTwo
					.setText("" 
							+ functions.roundFloats(
									cUnit.getFloat(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)),
									1));
			tvRowFiveFieldThree.setText("" + calcKcal); 
					
			cUnit.close();
		} catch (Exception e) {
		}
	}

	// This method will fill the textView selectedFood
	// ( its on top of the page )
	private void fillTextViewSelectedFood() {
		textViewSelectedFood.setText(foodCursor.getString(foodCursor
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
	}

	private void fillData() {
		dbHelper.open();
		List<DBNameAndID> objects = new ArrayList<DBNameAndID>();

		Cursor cFoodUnits = dbHelper.fetchFoodUnitByFoodId(getIntent()
				.getExtras().getLong(DataParser.idFood));

		if (cFoodUnits.getCount() > 0) {
			cFoodUnits.moveToFirst();
			do {
				objects.add(new DBNameAndID(
						cFoodUnits
								.getLong(cFoodUnits
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)),
						cFoodUnits.getString(cFoodUnits
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
						""));
			} while (cFoodUnits.moveToNext());
		}
		cFoodUnits.close();

		CustomSimpleArrayAdapterForASpinner adapter = new CustomSimpleArrayAdapterForASpinner(
				this, android.R.layout.simple_spinner_item, objects, 6);

		spinnerFoodUnits.setAdapter(adapter);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	// when pressed on button update
	private void onClickButtonUpdate() {
		//track it
		ActivityGroupMeal.group.parent.trackEvent(TrackingValues.eventCategoryMeal, TrackingValues.eventCategoryMealUpdateFoodFromSelection);
		
		
		float amount = getInsertedAmund();
		// round the input to 1 decimal behind ,
		amount = functions.roundFloats(amount, 1);

		dbHelper.open();
		// update a selectedFood
		dbHelper.updateSelectedFood(
				getIntent().getExtras().getLong(DataParser.idSelectedFood),
				amount, spinnerFoodUnits.getSelectedItemId());

		// and refresh the list in showSelectedFood
		ActivityGroupMeal.group.getShowSelectedFood().refreshData();
	}

	private void addFood() {
		// set search string back to ""
		ActivityGroupMeal.group.lastSearchString = "";
		
		float amount = getInsertedAmund();
		// round the input to 1 decimal behind ,
		amount = functions.roundFloats(amount, 1);

		dbHelper.open();
		// create a new selectedFood
		dbHelper.createSelectedFood(amount, spinnerFoodUnits
				.getSelectedItemId(), new Functions()
				.getDateAsStringFromCalendar(Calendar.getInstance()));

		// set the boolean in activitygroup on true
		ActivityGroupMeal.group.addedFoodItemToList = true;
	}

	@Override
	protected void onStop() {
		foodCursor.close();
		dbHelper.close();
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if we press the back key
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			// return false so the keydown event from activitygroupmeal will get
			// called
			return false;
		else
			return super.onKeyDown(keyCode, event);
	}
}
