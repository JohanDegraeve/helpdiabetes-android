// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Medicine;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterDBMedicineType;
import com.hippoandfriends.helpdiabetes.Objects.DBMedicineType;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowMedicineTypes extends ListActivity {
	private List<DBMedicineType> listMedicineTypes;
	private Button btAdd, btBack;

	public static ShowMedicineTypes INSTANCE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_medicine_types, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupSettings.group.parent
				.trackPageView(TrackingValues.pageShowSettingMedicineTypes);

		btAdd = (Button) findViewById(R.id.buttonAdd);

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAddMedicineType();
			}
		});

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

		INSTANCE = this;
	}

	// refresh the name of a object
	// this is called from showaddmedicinetype when we update a existing type
	public void refreshObject(long medicineTypeID, String name) {
		setListAdapter(null);
		for (DBMedicineType obj : listMedicineTypes) {
			if (obj.getId() == medicineTypeID) {
				// set name
				listMedicineTypes.get(listMedicineTypes.indexOf(obj))
						.setMedicineName(name);
				// update listadapter
				fillListView();
				// stop looping
				return;
			}
		}
	}

	// This method will delete a object from the list where medicine type id =
	// id
	// This method is called from showaddmedicinetype when we click on delete
	// button
	public void deleteFromList(long id) {
		setListAdapter(null);
		for (DBMedicineType obj : listMedicineTypes) {
			if (obj.getId() == id) {
				// delete object
				listMedicineTypes.remove(obj);
				// update listadapter
				fillListView();
				// stop looping
				return;
			}
		}
	}

	// on click n a exissting medicine type
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getApplicationContext(),
				ShowAddMedicineType.class).addFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(
				DataParser.idMedicineType,
				listMedicineTypes.get(position).getId());

		View view = ActivityGroupSettings.group.getLocalActivityManager()
				.startActivity("ShowSetting", i).getDecorView();

		ActivityGroupSettings.group.setContentView(view);
	}

	private void fillListView() {
		setListAdapter(null);

		CustomArrayAdapterDBMedicineType adapter = new CustomArrayAdapterDBMedicineType(
				this, R.layout.row_custom_array_adapter_with_arrow,
				listMedicineTypes,
				ActivityGroupMeal.group.getFoodData().dbFontSize,
				ActivityGroupMeal.group.getFoodData().defaultMedicineTypeID);

		setListAdapter(adapter);
	}

	private void onClickAddMedicineType() {
		Intent i = new Intent(getApplicationContext(),
				ShowAddMedicineType.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		View v = ActivityGroupSettings.group.getLocalActivityManager()
				.startActivity("ShowSetting", i).getDecorView();

		ActivityGroupSettings.group.setContentView(v);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh() {
		fillListMedicineTypes();
		fillListView();
	}

	private void fillListMedicineTypes() {
		listMedicineTypes = new ArrayList<DBMedicineType>();
		DbAdapter db = new DbAdapter(this);
		db.open();

		Cursor cMedicineTypes = db.fetchAllMedicineTypes();
		if (cMedicineTypes.getCount() > 0) {
			cMedicineTypes.moveToFirst();
			do {
				listMedicineTypes
						.add(new DBMedicineType(
								cMedicineTypes
										.getLong(cMedicineTypes
												.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_ID)),
								cMedicineTypes.getString(cMedicineTypes
										.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINETYPE)),
								cMedicineTypes.getString(cMedicineTypes
										.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME)),
								cMedicineTypes.getString(cMedicineTypes
										.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINEUNIT)),
								cMedicineTypes.getInt(cMedicineTypes
										.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_VISIBLE))));
			} while (cMedicineTypes.moveToNext());
		}
		cMedicineTypes.close();
		db.close();
	}

	// This method is called from the addmedicinetype
	public void addMedicineTypeToList(long medicineTypeID) {
		setListAdapter(null);
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cMedicineType = db.fetchMedicineTypesByID(medicineTypeID);
		if (cMedicineType.getCount() > 0) {
			cMedicineType.moveToFirst();

			listMedicineTypes
					.add(new DBMedicineType(
							cMedicineType
									.getLong(cMedicineType
											.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_ID)),
							cMedicineType.getString(cMedicineType
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINETYPE)),
							cMedicineType.getString(cMedicineType
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME)),
							cMedicineType.getString(cMedicineType
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINEUNIT)),
							cMedicineType.getInt(cMedicineType
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_VISIBLE))));
		}
		cMedicineType.close();
		db.close();

		// set the listadapter back to the list of medicine types
		fillListView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
