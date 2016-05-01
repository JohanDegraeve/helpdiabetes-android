// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

/*
 * This class is used to create a new unit for a food.
 * This class is also used to update a unit.
 */

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
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
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowCreateUnit extends Activity {
	private long foodId, unitId;

	private EditText editTextStandardAmound;
	private EditText editTextName;
	private Button btAdd;
	private Button btDelete, btBack;

	private Spinner spinnerUnit;
	private TableRow tableRowSpecialUnit;

	private EditText etCarb, etProt, etFat, etKcal;

	// used to hide the ones we dont need to show
	private TableRow trCarb, trProt, trFat, trKcal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_create_unit, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupMeal.group.parent
				.trackPageView(TrackingValues.pageShowCreateUnit);

		editTextStandardAmound = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitStandardAmound);
		editTextName = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitName);
		btAdd = (Button) findViewById(R.id.buttonShowCreateUnitAddOrUpdate);
		btDelete = (Button) findViewById(R.id.buttonShowCreateUnitDelete);
		spinnerUnit = (Spinner) findViewById(R.id.spinnerShowCreateUnit);
		tableRowSpecialUnit = (TableRow) findViewById(R.id.tableRowShowCreateUnitSpecialFoodUnit);

		etCarb = (EditText) findViewById(R.id.editTextShowCreateUnit1);
		etProt = (EditText) findViewById(R.id.editTextShowCreateUnit2);
		etFat = (EditText) findViewById(R.id.editTextShowCreateUnit3);
		etKcal = (EditText) findViewById(R.id.editTextShowCreateUnit4);

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

		// hide button delete
		btDelete.setVisibility(View.GONE);

		spinnerUnit.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				hideOrShowSpecialFoodUnitTableRow();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAdd();
			}
		});

		btDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickDelete();
			}
		});

		// handle the enter keys
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
						onClickAdd();
					}
				}
				// if we dont return false our numbers wont get in the
				// edittext
				return false;
			}
		});
	}

	// when we click on the button delete
	private void onClickDelete() {
		if (weCanDelete()) {

			// track we come here
			ActivityGroupMeal.group.parent.trackEvent(
					TrackingValues.eventCategoryMeal,
					TrackingValues.eventCategoryMealDeleteFoodUnit);

			// delete the unit
			DbAdapter db = new DbAdapter(this);
			db.open();
			// delete the unit
			db.deleteFoodUnit(unitId);
			// mark the food as "own food"
			db.updateFoodToOwnCreated(foodId);

			db.close();

			// refresh the list
			ActivityGroupMeal.group.getShowUpdateFood().refresh();
		}

		// go back
		ActivityGroupMeal.group.back();

	}

	// when we click on the button add / update
	private void onClickAdd() {
		if (checkValues()) {
			float standardamount = 0f;
			String unitName = "";
			float carb = 0f;
			float prot = 0f;
			float fat = 0f;
			float kcal = 0f;

			if (spinnerUnit.getSelectedItemPosition() == 0) {
				standardamount = 100;
				unitName = ActivityGroupMeal.group.getFoodData().dbTopOneCommonFoodUnit;
			} else if (spinnerUnit.getSelectedItemPosition() == 1) {
				standardamount = 100;
				unitName = ActivityGroupMeal.group.getFoodData().dbTopTwoCommonFoodUnit;
			} else {
				try {
					standardamount = Float.parseFloat(editTextStandardAmound
							.getText().toString());
				} catch (Exception e) {
					standardamount = 0f;
				}
				unitName = editTextName.getText().toString();
			}

			try {
				carb = Float.parseFloat(etCarb.getText().toString());
			} catch (Exception e) {
				carb = 0f;
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

			// round values
			Functions functions = new Functions();
			standardamount = functions.roundFloats(standardamount, 1);
			carb = functions.roundFloats(carb, 1);
			prot = functions.roundFloats(prot, 1);
			fat = functions.roundFloats(fat, 1);
			kcal = functions.roundFloats(kcal, 1);

			DbAdapter db = new DbAdapter(this);
			db.open();

			if (unitId <= 0) {
				// add the unit to the database

				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryMeal,
						TrackingValues.eventCategoryMealCreateFoodUnit);

				db.createFoodUnit(foodId, unitName, standardamount, carb, prot,
						fat, kcal);
			} else {

				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryMeal,
						TrackingValues.eventCategoryMealUpdateFoodUnit);

				// update existing unit
				db.updateFoodUnit(unitId, unitName, standardamount, carb, prot,
						fat, kcal);
			}

			// set the food platform to "android"
			db.updateFoodToOwnCreated(foodId);

			db.close();

			// refresh the list
			ActivityGroupMeal.group.getShowUpdateFood().refresh();

			// update the values in the activitygroup foodlist from platform to
			// "android"
			// so we see a green star
			ActivityGroupMeal.group.getFoodData()
					.updateFoodToOwnCreated(foodId);

			// go back
			ActivityGroupMeal.group.back();
		}
	}

	private boolean checkValues() {
		if (spinnerUnit.getCount() == spinnerUnit.getSelectedItemPosition() + 1) {
			// if we selected the last spinner option we have to check if
			// standardamount and unitname are filled in
			if (editTextStandardAmound.getText().length() <= 0) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.standardAmountCantBeEmpty),
						Toast.LENGTH_LONG).show();
				return false;
			} else if (editTextName.getText().length() <= 0) {
				Toast.makeText(this,
						getResources().getString(R.string.name_cant_be_empty),
						Toast.LENGTH_LONG).show();
				return false;
			}
		} else {
			// if there is a value in everything ( check if the value aint '0'
			// or '0.'
			try {
				float unitStandardAmound = Float
						.parseFloat(editTextStandardAmound.getText().toString());
				if (unitStandardAmound <= 0) {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.name_cant_be_empty),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.name_cant_be_empty),
						Toast.LENGTH_LONG).show();
				return false;
			}
		}
		// if everything went OK we return true
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// set foodId
		foodId = getIntent().getExtras().getLong(DataParser.idFood);

		// fill the spinner
		fillSpinner();

		try {
			unitId = getIntent().getExtras().getLong(DataParser.idUnit);

			if (unitId > 0) {
				fillExistingValues();
			}
		} catch (Exception e) {
			unitId = -1;
		}

		hideTheRowsWeDontNeed();

		setButtonDelete();
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

	private void fillExistingValues() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		Cursor cUnit = db.fetchFoodUnit(unitId);
		if (cUnit.getCount() > 0) {
			cUnit.moveToFirst();

			// set the right spinner item
			if (cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) == 100) {
				if (cUnit
						.getString(
								cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						.equals(ActivityGroupMeal.group.getFoodData().dbTopOneCommonFoodUnit))
					spinnerUnit.setSelection(0);
				else if (cUnit
						.getString(
								cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						.equals(ActivityGroupMeal.group.getFoodData().dbTopTwoCommonFoodUnit))
					spinnerUnit.setSelection(1);
				else
					spinnerUnit.setSelection(spinnerUnit.getCount() - 1);
			} else {
				spinnerUnit.setSelection(spinnerUnit.getCount() - 1);
			}

			editTextStandardAmound
					.setText(cUnit.getString(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
			editTextName.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)));

			// set the values in the editTextBoxes
			etCarb.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)));

			etProt.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)));

			etFat.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)));
			etKcal.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));

		}
		cUnit.close();
		db.close();

		setButtonDelete();
	}

	private void setButtonDelete() {
		if (weCanDelete()) {
			btDelete.setVisibility(View.VISIBLE);
		}
	}

	private boolean weCanDelete() {
		DbAdapter dbHelper = new DbAdapter(this);
		dbHelper.open();

		// if the unit isnt the last one from the food
		// and the unit is not in use in template food
		// and the unit is not in use in meal food
		// update: and the unit is not used in selected food!!!
		if (dbHelper.fetchFoodUnitByFoodId(foodId).getCount() > 1
				&& dbHelper.fetchTemplateFoodsByUnitID(unitId).getCount() == 0
				&& dbHelper.fetchMealFoodByFoodUnitID(unitId).getCount() == 0
				&& dbHelper.fetchSelectedFoodByFoodUnitId(unitId).getCount() == 0) {
			return true;
		}

		dbHelper.close();
		return false;
	}

	// This method will show the special food unit table row when the last
	// spinenr item is selected.
	// This method will also hide the special food unit table row when any other
	// spinner item is selected
	private void hideOrShowSpecialFoodUnitTableRow() {
		if (spinnerUnit.getCount() == spinnerUnit.getSelectedItemPosition() + 1) {
			tableRowSpecialUnit.setVisibility(View.VISIBLE);
		} else {
			tableRowSpecialUnit.setVisibility(View.GONE);
		}
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

	private void fillSpinner() {
		CustomArrayAdapterCharSequenceShowCreateFood adapter = new CustomArrayAdapterCharSequenceShowCreateFood(
				this,
				R.layout.custom_spinner_array_adapter_charsequence_show_create_food,
				getArrayList());

		spinnerUnit.setAdapter(adapter);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if we press the back key
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
}
