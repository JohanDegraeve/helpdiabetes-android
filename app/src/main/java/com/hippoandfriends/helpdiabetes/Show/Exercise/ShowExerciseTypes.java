// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Exercise;

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
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterDBExerciseType;
import com.hippoandfriends.helpdiabetes.Objects.DBExerciseType;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowExerciseTypes extends ListActivity {
	private List<DBExerciseType> listExerciseTypes;
	private Button btAdd, btBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_exercise_types, null);
		setContentView(contentView);

		// track we come here
				ActivityGroupSettings.group.parent
						.trackPageView(TrackingValues.pageShowSettingActivityTypes);
		
		btAdd = (Button) findViewById(R.id.buttonAdd);

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAddExerciseType();
			}
		});

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});
		
	}

	//refresh the name of a object
	//this is called from showaddexercisetype when we update a exercise type
	public void refreshObject(long exerciseID,String exerciseName){
		setListAdapter(null);
		for(DBExerciseType obj: listExerciseTypes){
			if(obj.getId() == exerciseID){
				//set name
				listExerciseTypes.get(listExerciseTypes.indexOf(obj)).setName(exerciseName);
				//update listadapter
				fillListView();
				//stop looping
				return;
			}
		}
	}
	
	//This method will delete a object from the list where exercise type id = exerciseID
	//This method is called from showaddexercisetype when we click on delete button
	public void deleteFromList(long exerciseID){
		setListAdapter(null);
		for(DBExerciseType obj: listExerciseTypes){
			if(obj.getId() == exerciseID){
				//delete object
				listExerciseTypes.remove(obj);
				//update listadapter
				fillListView();
				//stop looping
				return; 
			}
		}
	}
	
	// on click on a existing exercise type
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getApplicationContext(),
				ShowAddExerciseType.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(DataParser.idExerciseType, listExerciseTypes.get(position).getId());

		View view = ActivityGroupSettings.group.getLocalActivityManager()
				.startActivity("ShowSetting", i).getDecorView();

		ActivityGroupSettings.group.setContentView(view);
	}

	// on click button add exercise type
	public void onClickAddExerciseType() {
		Intent i = new Intent(getApplicationContext(),
				ShowAddExerciseType.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		View v = ActivityGroupSettings.group.getLocalActivityManager()
				.startActivity("ShowSetting", i).getDecorView();

		ActivityGroupSettings.group.setContentView(v);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		fillListExerciseTypes();
		fillListView();
	}

	private void fillListView() {
		CustomArrayAdapterDBExerciseType adapter = new CustomArrayAdapterDBExerciseType(
				this, R.layout.row_custom_array_adapter_with_arrow,
				listExerciseTypes,
				ActivityGroupMeal.group.getFoodData().dbFontSize,
				ActivityGroupMeal.group.getFoodData().defaultExerciseTypeID);
 
		setListAdapter(adapter);
	}

	// This method will fill the list object with the right items
	private void fillListExerciseTypes() {
		listExerciseTypes = new ArrayList<DBExerciseType>();
		DbAdapter dbHelper = new DbAdapter(this);
		dbHelper.open();

		Cursor cExerciseType = dbHelper.fetchAllExerciseTypes();

		if (cExerciseType.getCount() > 0) {
			cExerciseType.moveToFirst();
			do {
				listExerciseTypes
						.add(new DBExerciseType(
								cExerciseType
										.getLong(cExerciseType
												.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_ID)),
								cExerciseType.getString(cExerciseType
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME)),
								cExerciseType.getString(cExerciseType
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_DESCRIPTION))));
			} while (cExerciseType.moveToNext());
		}

		cExerciseType.close();
		dbHelper.close();
	}

	// this method is called from the addexercisetype view
	public void addExerciseTypeToList(long exerciseType) {
		setListAdapter(null);
		
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cExerciseType = db.fetchExerciseTypeByID(exerciseType);
		if (cExerciseType.getCount() > 0) {
			cExerciseType.moveToFirst();
			listExerciseTypes
					.add(new DBExerciseType(
							cExerciseType
									.getLong(cExerciseType
											.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_ID)),
							cExerciseType.getString(cExerciseType
									.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME)),
							cExerciseType.getString(cExerciseType
									.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_DESCRIPTION))));
		}
		cExerciseType.close();
		db.close();
		
		//set the listadapter back to the list of exercise types
		fillListView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void refresh() {
		fillListExerciseTypes();
		fillListView();
	}
}
