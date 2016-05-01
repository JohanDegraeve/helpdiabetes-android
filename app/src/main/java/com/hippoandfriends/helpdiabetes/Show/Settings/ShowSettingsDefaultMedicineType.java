// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterDBMedicineType;
import com.hippoandfriends.helpdiabetes.Objects.DBMedicineType;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;

public class ShowSettingsDefaultMedicineType extends ListActivity {

	private List<DBMedicineType> listMedicineTypes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_default_medicine_type);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();
	}

	private void fillListView() {
		setListAdapter(null);

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
								0));
			} while (cMedicineTypes.moveToNext());
		}
		cMedicineTypes.close();
		db.close();

		CustomArrayAdapterDBMedicineType adapter = new CustomArrayAdapterDBMedicineType(
				this, R.layout.row_custom_array_adapter_with_arrow,
				listMedicineTypes, 21, 0);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// set default medicine type
		// update default in db
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.updateSettingsByName(DbSettings.setting_default_medicine_type_ID, ""
				+ listMedicineTypes.get(position).getId());
		db.close();

		// kill this activity
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
