// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Settings;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterFontSize;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowSettingsFontSizeLists extends ListActivity {

	private DbAdapter dbHelper;
	private ArrayList<String> list;
	private Button btBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_font_size_list);

		// track we come here
		ActivityGroupSettings.group.parent
				.trackPageView(TrackingValues.pageShowSettingFontSize);

		dbHelper = new DbAdapter(this);

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

		list = getList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillList();
	}

	private void saveSize(int fontSize) {
		dbHelper.updateSettingsByName(DbSettings.setting_font_size, ""
				+ fontSize);
	}

	private void fillList() {
		CustomArrayAdapterFontSize adapter = new CustomArrayAdapterFontSize(
				this, R.layout.row_custom_array_adapter, list);

		setListAdapter(adapter);
	}

	private ArrayList<String> getList() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (int i = 0; i < 16; i++) {
			returnList.add(getResources().getString(R.string.fontSize));
		}
		return returnList;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// save font size
		saveSize((position + 15));
		// refresh the list of food
		ActivityGroupMeal.group.getFoodData().setNewFontSize();

		// refresh the history page
		ActivityGroupTracking.group.restartThisActivity();

		// go back
		ActivityGroupSettings.group.back();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
