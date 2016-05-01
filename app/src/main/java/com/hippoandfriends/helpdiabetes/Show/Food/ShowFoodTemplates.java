// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.Custom.CustomBaseAdapterFoodTemplates;
import com.hippoandfriends.helpdiabetes.Objects.DBFood;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodTemplate;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowFoodTemplates extends ListActivity {
	private DbAdapter dbHelper;

	private Button btBack;

	private static final int DELETE_ID = Menu.FIRST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_food_templates, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupMeal.group.parent
				.trackPageView(TrackingValues.pageShowTemplates);

		dbHelper = new DbAdapter(this);
		registerForContextMenu(getListView());

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupMeal.group.back();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillData();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		dbHelper.open();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			// track we come here
			ActivityGroupMeal.group.parent.trackEvent(
					TrackingValues.eventCategoryMeal,
					TrackingValues.eventCategoryMealDeleteTemplate);

			// delete all the template foods
			dbHelper.deleteTemplateFoodByFoodTemplateID(info.id);
			// delete all the food teplates
			dbHelper.deleteFoodTemplate(info.id);
			// if it was the last template we go back to the
			// showSelectedFoodPage
			if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
				// refresh the data
				fillData();
			} else {
				ActivityGroupMeal.group.back();
				// refresh the page selectedFood
				if (ActivityGroupMeal.group.getShowSelectedFood() != null) {
					ActivityGroupMeal.group.getShowSelectedFood().refreshData();
				} else {
					Toast.makeText(this, "error refresh data",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {
		dbHelper.open();
		if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
			Cursor cSettings = dbHelper
					.fetchSettingByName(DbSettings.setting_font_size);
			cSettings.moveToFirst();
			CustomBaseAdapterFoodTemplates adapter = new CustomBaseAdapterFoodTemplates(
					this,
					getFoodTemplates(),
					cSettings.getInt(cSettings
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
			setListAdapter(adapter);
			cSettings.close();
		} else {
			setListAdapter(null);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		dbHelper.open();
		// count how much foodItems we add
		int count = 0;

		// First put all the food in the selectedFood table
		Cursor cTemplateFood = dbHelper.fetchTemplateFoodsByFoodTemplateID(id);
		cTemplateFood.moveToFirst();
		Cursor cUnit = null;
		do {
			count++;
			cUnit = dbHelper
					.fetchFoodUnit(cTemplateFood.getLong(cTemplateFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_UNITID)));
			cUnit.moveToFirst();
			dbHelper.createSelectedFood(
					cTemplateFood
							.getFloat(cTemplateFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_AMOUNT)),
					cUnit.getLong(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)),
					new Functions().getDateAsStringFromCalendar(Calendar
							.getInstance()));
		} while (cTemplateFood.moveToNext());

		cUnit.close();
		cTemplateFood.close();

		// update the list in show selected food
		ActivityGroupMeal.group.getShowSelectedFood().refreshData();

		// update the count selected food
		ActivityGroupMeal.group.getFoodData().countSelectedFood += count;

		// go back
		ActivityGroupMeal.group.back();
	}

	// converts the cursor with all food templates to a arrayList
	// and returns that array list
	private ArrayList<DBFoodTemplate> getFoodTemplates() {
		dbHelper.open();
		ArrayList<DBFoodTemplate> list = new ArrayList<DBFoodTemplate>();
		Cursor cFoodTemplates = dbHelper.fetchAllFoodTemplates();
		cFoodTemplates.moveToFirst();

		do {

			// first create a arrayList with the food
			ArrayList<DBFood> foods = new ArrayList<DBFood>();

			Cursor cTemplateFood = dbHelper
					.fetchTemplateFoodsByFoodTemplateID(cFoodTemplates.getLong(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_ID)));
			cTemplateFood.moveToFirst();
			do {
				Cursor cUnit = dbHelper
						.fetchFoodUnit(cTemplateFood.getLong(cTemplateFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_UNITID)));

				cUnit.moveToFirst();
				Cursor cFood = dbHelper
						.fetchFood(cUnit.getLong(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
				cFood.moveToFirst();
				foods.add(new DBFood(
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
						cTemplateFood.getFloat(cTemplateFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_AMOUNT))
								+ " "
								+ cUnit.getString(cUnit
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
						null));
				cUnit.close();
				cFood.close();
			} while (cTemplateFood.moveToNext());
			cTemplateFood.close();

			list.add(new DBFoodTemplate(
					cFoodTemplates
							.getInt(cFoodTemplates
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_ID)),
					cFoodTemplates.getInt(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_MEALTYPEID)),
					cFoodTemplates.getInt(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_USERID)),
					cFoodTemplates.getInt(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_VISIBLE)),
					cFoodTemplates.getString(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_FOODTEMPLATENAME)),
					foods));
		} while (cFoodTemplates.moveToNext());
		cFoodTemplates.close();
		return list;
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
