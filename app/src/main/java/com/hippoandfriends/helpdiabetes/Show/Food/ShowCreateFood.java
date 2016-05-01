// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterCharSequenceShowCreateFood;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowCreateFood extends Activity {
	private EditText editTextfoodName;
	private EditText editTextUnitStandardAmound;
	private EditText editTextUnitName;
	private Button btAdd, btBack;

	private Spinner spinnerUnit;
	private TableRow tableRowSpecialFoodUnit;

	private DbAdapter dbHelper;

	private EditText etCarb, etProt, etFat, etKcal;

	// used to hide the ones we dont need to show
	private TableRow trCarb, trProt, trFat, trKcal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_create_food, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupMeal.group.parent
				.trackPageView(TrackingValues.pageShowCreateFood);

		dbHelper = new DbAdapter(this);

		spinnerUnit = (Spinner) findViewById(R.id.spinnerShowCreateFoodUnit);
		tableRowSpecialFoodUnit = (TableRow) findViewById(R.id.tableRowSpecialFoodUnit);
		editTextfoodName = (EditText) findViewById(R.id.editTextFoodName);
		editTextUnitStandardAmound = (EditText) findViewById(R.id.editTextFoodUnitStandardAmound);
		editTextUnitName = (EditText) findViewById(R.id.editTextFoodUnitName);
		btAdd = (Button) findViewById(R.id.buttonAdd);

		etCarb = (EditText) findViewById(R.id.editTextShowCreateFood1);
		etProt = (EditText) findViewById(R.id.editTextShowCreateFood2);
		etFat = (EditText) findViewById(R.id.editTextShowCreateFood3);
		etKcal = (EditText) findViewById(R.id.editTextShowCreateFood4);

		trCarb = (TableRow) findViewById(R.id.tableRowCarb);
		trProt = (TableRow) findViewById(R.id.tableRowProt);
		trFat = (TableRow) findViewById(R.id.tableRowFat);
		trKcal = (TableRow) findViewById(R.id.tableRowKcal);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupMeal.group.back();
			}
		});

		spinnerUnit.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// hide or show the tableRowSpecialFoodUnit
				hideOrShowSpecialFoodUnitTableRow();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupMeal.group.parent.trackEvent(TrackingValues.eventCategoryMeal, TrackingValues.eventCategoryMealCreateFood);
				onClickAdd(v);
			}
		});

		etCarb.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we go to the next
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						etProt.requestFocus();
					}
				}
				// if we dont return false our numbers wont get in the
				// edittext
				return false;
			}
		});

		etProt.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we go to the next
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						etFat.requestFocus();
					}
				}
				// if we dont return false our numbers wont get in the
				// edittext
				return false;
			}
		});

		etFat.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we go to the next
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						etKcal.requestFocus();
					}
				}
				// if we dont return false our numbers wont get in the
				// edittext
				return false;
			}
		});

		etKcal.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we go to the next
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						onClickAdd(null);
					}
				}
				// if we dont return false our numbers wont get in the
				// edittext
				return false;
			}
		});

	}

	// This method will show the special food unit table row when the last
	// spinner item is selected.
	// This method will also hide the special food unit table row when any other
	// spinner item is selected.
	private void hideOrShowSpecialFoodUnitTableRow() {
		if (spinnerUnit.getCount() == spinnerUnit.getSelectedItemPosition() + 1) {
			// Show the row
			tableRowSpecialFoodUnit.setVisibility(View.VISIBLE);
		} else {
			// Hide the row
			tableRowSpecialFoodUnit.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillTextViewFoodName();
		fillSpinner();
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

	private void fillTextViewFoodName() {
		editTextfoodName.setText(getIntent().getExtras().getString(
				DataParser.foodSearchValue));
	}

	private void fillSpinner() {
		CustomArrayAdapterCharSequenceShowCreateFood adapter = new CustomArrayAdapterCharSequenceShowCreateFood(
				this,
				R.layout.custom_spinner_array_adapter_charsequence_show_create_food,
				getArrayList());
		spinnerUnit.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	private List<CharSequence> getArrayList() {
		List<CharSequence> value = new ArrayList<CharSequence>();

		value.add("100 "
				+ ActivityGroupMeal.group.getFoodData().dbTopOneCommonFoodUnit);
		value.add("100 "
				+ ActivityGroupMeal.group.getFoodData().dbTopTwoCommonFoodUnit);
		value.add(getResources().getString(R.string.define_own));

		return value;
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	public void onClickAdd(View view) {
		dbHelper.open();
		if (checkAllFieldsGotSomeValue()) {
			
			// track we come here
			ActivityGroupMeal.group.parent
					.trackEvent(TrackingValues.eventCategoryMeal, TrackingValues.eventCategoryMealCreateFood);
			
			float standardAmound = 0f;
			String unitName = "";
			float carbs = 0f;
			float kcal = 0f;
			float prot = 0f;
			float fat = 0f;

			long foodId = dbHelper.createFood(editTextfoodName.getText()
					.toString(),
					ActivityGroupMeal.group.getFoodData().foodLanguageID);

			// see what option we selected
			if (spinnerUnit.getSelectedItemPosition() == 0) {
				// if we selected '100 gram'
				standardAmound = 100f;
				unitName = ActivityGroupMeal.group.getFoodData().dbTopOneCommonFoodUnit;
			} else if (spinnerUnit.getSelectedItemPosition() == 1) {
				// if we selected '100 ml'
				standardAmound = 100f;
				unitName = ActivityGroupMeal.group.getFoodData().dbTopTwoCommonFoodUnit;
			} else {
				// else get from the editText boxes
				standardAmound = Float.parseFloat(editTextUnitStandardAmound
						.getText().toString());
				unitName = editTextUnitName.getText().toString();
			}

			try {
				carbs = Float.parseFloat(etCarb.getText().toString());
			} catch (Exception e) {
				carbs = 0f;
			}

			try {
				prot = Float.parseFloat(etProt.getText().toString());
			} catch (Exception e) {
				prot = 0f;
			}

			try {
				fat = Float.parseFloat(etFat.getText().toString());
			} catch (Exception e) {
				fat = 0f;
			}

			try {
				kcal = Float.parseFloat(etKcal.getText().toString());
			} catch (Exception e) {
				kcal = 0f;
			}

			dbHelper.createFoodUnit(foodId, unitName, standardAmound, carbs,
					prot, fat, kcal);

			// set the foodID in the activitygroup so the showFoodList know we
			// have to update the list
			ActivityGroupMeal.group.newFoodID = foodId;

			// Go back to the previous screen
			ActivityGroupMeal.group.back();
		}
	}

	private boolean checkAllFieldsGotSomeValue() {
		if (editTextfoodName.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.food_name_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (spinnerUnit.getCount() == spinnerUnit
				.getSelectedItemPosition() + 1) {
			// if we selected the last spinner option we have to check if
			// standardamount and unitName are filled in
			if (editTextUnitStandardAmound.getText().length() <= 0) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.standardAmountCantBeEmpty),
						Toast.LENGTH_LONG).show();
				return false;
			}
			if (editTextUnitName.getText().length() <= 0) {
				Toast.makeText(this,
						getResources().getString(R.string.unit_cant_be_empty),
						Toast.LENGTH_LONG).show();
				return false;
			}
			

			// and check if standardamount != 0
			float checkStandardAmount = 0;
			try {
				checkStandardAmount = Float
						.parseFloat(editTextUnitStandardAmound.getText()
								.toString());
			} catch (Exception e) {
				checkStandardAmount = 0;
			}
			if (checkStandardAmount == 0) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.unit_amount_cant_be_zero),
						Toast.LENGTH_LONG).show();
				return false;
			}
		}
		// if everything went OK we return true
		return true;
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
