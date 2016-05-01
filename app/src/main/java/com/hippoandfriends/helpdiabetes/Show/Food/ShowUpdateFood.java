// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterListViewShowUpdateFoodDBFoodUnit;
import com.hippoandfriends.helpdiabetes.Objects.DBFood;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodUnit;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowUpdateFood extends ListActivity {

	private Button btName, btAdd, btDelete, btBack;
	private DBFood food;
	private List<DBFoodUnit> listFoodUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_update_food, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupMeal.group.parent
				.trackPageView(TrackingValues.pageShowUpdateFood);

		btName = (Button) findViewById(R.id.buttonName);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupMeal.group.back();
			}
		});

		btName.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickButtonName();
			}
		});

		btDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickButtonDelete();
			}
		});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickButtonAdd();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			DbAdapter db = new DbAdapter(this);
			db.open();
			// get the food object out the database
			Cursor cFood = db.fetchFood(getIntent().getExtras().getLong(
					DataParser.idFood));
			// move the cursor to the first
			cFood.moveToFirst();
			// fill the food object with the id and name
			food = new DBFood(
					cFood.getLong(cFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
					cFood.getString(cFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
					null, null);
			// close the cursor
			cFood.close();
			// close the database
			db.close();
		} catch (Exception e) {
			// when something goes wrong we go back ( this will normal never be
			// executed )
			ActivityGroupMeal.group.back();
		}

		// set the right foodName on the button
		btName.setText(food.getName());

		// fill the listFoodUnit
		fillListFoodUnit();

		// set list adapter with the listFoodUnit
		fillListView();

		// check to show button delete
		showButtonDelete();
	}

	private void showButtonDelete() {
		if (weCanDelete()) {
			btDelete.setVisibility(View.VISIBLE);
		}
	}

	// if the food is not in use we can show the delete button
	private boolean weCanDelete() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		// check for every foodUnit in the list if it appears in the database
		for (DBFoodUnit obj : listFoodUnit) {
			// if we found the foodunit in the database we return false
			if (db.fetchTemplateFoodsByUnitID(obj.getId()).getCount() > 0
					|| db.fetchMealFoodByFoodUnitID(obj.getId()).getCount() > 0
					|| db.fetchSelectedFoodByFoodUnitId(obj.getId()).getCount() > 0)
				return false;
		}

		db.close();
		return true;
	}

	// this method is called when we update a unit
	public void refresh() {
		fillListFoodUnit();
		fillListView();
	}

	private void fillListView() {
		CustomArrayAdapterListViewShowUpdateFoodDBFoodUnit adapter = new CustomArrayAdapterListViewShowUpdateFoodDBFoodUnit(
				this, R.layout.row_custom_array_adapter_with_arrow,
				listFoodUnit, ActivityGroupMeal.group.getFoodData().dbFontSize);
		setListAdapter(adapter);
	}

	private void fillListFoodUnit() {
		listFoodUnit = new ArrayList<DBFoodUnit>();
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cFoodUnit = db.fetchFoodUnitByFoodId(food.getId());
		if (cFoodUnit.getCount() > 0) {
			cFoodUnit.moveToFirst();
			do {
				listFoodUnit
						.add(new DBFoodUnit(
								cFoodUnit
										.getLong(cFoodUnit
												.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)),
								cFoodUnit.getString(cFoodUnit
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
								"", 0F, 0F, 0F, 0F, 0F, 1F, 0));
			} while (cFoodUnit.moveToNext());
		}
		cFoodUnit.close();
		db.close();
	}

	// on click on the button with the food name
	private void onClickButtonName() {
		// show a dialog to update the name
		showDialogUpdateName();
	}

	private void showDialogUpdateName() {
		final EditText input = new EditText(this);
		input.setText(btName.getText().toString());

		// Show a dialog with a inputbox to insert the template name
		new AlertDialog.Builder(ActivityGroupMeal.group)
				.setTitle(getResources().getString(R.string.update_food_name))
				.setView(input)
				.setPositiveButton(getResources().getString(R.string.save),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// track we come here
								ActivityGroupMeal.group.parent.trackEvent(
										TrackingValues.eventCategoryMeal,
										TrackingValues.eventCategoryMealUpdateFoodName);
								
								
								// on click positive button
								// if inputbox text is longer then ""
								if (input.getText().length() > 0) {
									updateFoodName(input.getText().toString());
								} else {
									Toast.makeText(
											ActivityGroupMeal.group,
											getResources()
													.getString(
															R.string.name_cant_be_empty),
											Toast.LENGTH_LONG).show();
								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click negative button do nothing
							}
						}).show();
	}

	// we get here from the dialog to update the foodname
	private void updateFoodName(String foodName) {
		// update the button
		btName.setText(foodName);

		// update the object
		food.setName(foodName);

		// update the database
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.updateFoodName(food.getId(), foodName);
		db.close();

		// update the foodname in showloadingfooddata
		ActivityGroupMeal.group.getFoodData().updateFoodName(food.getId(),
				foodName);

		// mark a value in activitygroupmeal to recreate the list when we go
		// back to showfoodlist
		ActivityGroupMeal.group.recreatelist = true;
	}

	// on click button delete
	private void onClickButtonDelete() {
		// if we can delete the food
		if (weCanDelete()) {

			// track we come here
			ActivityGroupMeal.group.parent.trackEvent(
					TrackingValues.eventCategoryMeal,
					TrackingValues.eventCategoryMealDeleteFood);

			// delete all the units
			DbAdapter db = new DbAdapter(this);
			db.open();
			Cursor cUnits = db.fetchFoodUnitByFoodId(food.getId());
			if (cUnits.getCount() > 0) {
				cUnits.moveToFirst();
				do {
					db.deleteFoodUnit(cUnits.getLong(cUnits
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
				} while (cUnits.moveToNext());
			}
			cUnits.close();

			// delete the food itself
			db.deleteFood(food.getId());

			db.close();

			// mark the deleteFoodID in activitygroupmeal so that showfoodlist
			// will delete the item from the list.
			ActivityGroupMeal.group.deleteFoodIDFromList = food.getId();
		}

		// go back
		ActivityGroupMeal.group.back();
	}

	// when we add a unit to the food
	private void onClickButtonAdd() {
		Intent i = new Intent(ActivityGroupMeal.group, ShowCreateUnit.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(
						DataParser.idFood, food.getId());
		View view = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
		ActivityGroupMeal.group.setContentView(view);
	}

	// when we click on a unit
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(ActivityGroupMeal.group, ShowCreateUnit.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra(DataParser.idFood, food.getId())
				.putExtra(DataParser.idUnit, listFoodUnit.get(position).getId());
		View view = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
		ActivityGroupMeal.group.setContentView(view);
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
