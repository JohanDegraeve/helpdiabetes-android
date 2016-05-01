// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Tracking;

import java.util.ArrayList;
import java.util.Calendar;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterDBTracking;
import com.hippoandfriends.helpdiabetes.Objects.DBBloodGlucoseEvent;
import com.hippoandfriends.helpdiabetes.Objects.DBExerciseEvent;
import com.hippoandfriends.helpdiabetes.Objects.DBMealFood;
import com.hippoandfriends.helpdiabetes.Objects.DBMedicineEvent;
import com.hippoandfriends.helpdiabetes.Objects.DBTracking;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;
import com.hippoandfriends.helpdiabetes.Show.ShowHomeTab;
import com.hippoandfriends.helpdiabetes.slider.DateSlider;
import com.hippoandfriends.helpdiabetes.slider.DateTimeSlider;

public class ShowTracking extends ListActivity {
	private CustomArrayAdapterDBTracking adapter;
	private Button btMore, btForward;

	// search function
	private Button btSearch, btSearchNext;
	private EditText et;
	private LinearLayout llSearch, llButtons;

	private int searchPosition;

	private Calendar mCalendar;
	// to save selected position so we can update the time
	private int selectedPosition;
	private Context ctx;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_tracking);
		ctx = this;
		mCalendar = Calendar.getInstance();
		selectedPosition = 0;

		// track we come here
		ActivityGroupTracking.group.parent
				.trackPageView(TrackingValues.pageShowTrackingTab);

		pd = new ProgressDialog(ActivityGroupTracking.group);

		// search function
		btSearch = (Button) findViewById(R.id.buttonSearch);
		btSearchNext = (Button) findViewById(R.id.buttonSearchNext);
		btMore = (Button) findViewById(R.id.buttonMore);
		btForward = (Button) findViewById(R.id.ButtonForward);
		llSearch = (LinearLayout) findViewById(R.id.LinearLayoutSearch);
		llButtons = (LinearLayout) findViewById(R.id.LinearLayoutButtons);
		et = (EditText) findViewById(R.id.editText1);

		// hide the search part
		llSearch.setVisibility(View.GONE);

		// standard hide the button
		llButtons.setVisibility(View.GONE);

		adapter = null;

		searchPosition = 0;

		btForward.setVisibility(View.INVISIBLE);
		
		btForward.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// start asynctask to make excel file
				new AsyncWriteExcel().execute();
			}
		});

		btMore.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryTracking,
						TrackingValues.eventCategoryTrackingSeeMore);

				setListAdapter(null);

				// start a background thread to add the next month (where are
				// values in)
				// so if the current month is 07 and in the month 06 are no date
				// we go on searching in 05 for data , etc ...
				new AsyncGetNextMonthOfData().execute();
			}
		});

		btSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (llSearch.getVisibility() == View.VISIBLE) {
					llSearch.setVisibility(View.GONE);
					// set the search mark
					btSearch.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.ic_menu_search));
				} else {
					llSearch.setVisibility(View.VISIBLE);
					// set the "X" mark
					btSearch.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.ic_menu_close_clear_cancel));
				}
			}
		});

		btSearchNext.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryTracking,
						TrackingValues.eventCategoryTrackingFindNext);

				onClickFindNext();
				setNext();
			}

		});

		et.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// reset search posotion;
				searchPosition = 0;
			}

			public void afterTextChanged(Editable s) {

			}
		});
	}

	private class AsyncWriteExcel extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			createExcelFile();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(ActivityGroupTracking.group,
					"Finished creating excel", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}

	private void createExcelFile() {

		File sd = Environment.getExternalStorageDirectory();
		if (sd.canWrite()) {
			try{
			WritableWorkbook workbook = Workbook.createWorkbook(new File(sd, "HelpDiabetesExcel.xls"));
			WritableSheet sheet = workbook.createSheet("HelpDiabetes", 0);
 
			int column = 0;
			int row = 0;

			Label label = new Label(column, row, "A label record");
			sheet.addCell(label);
			
			for (DBTracking obj : ActivityGroupTracking.group.getTrackingData().listTracking) {
				try {

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			row++;
			
			Label labelTwo = new Label(column, row, "A label record again");
			sheet.addCell(labelTwo);

			workbook.write();
			workbook.close();
			} catch (Exception e){
				
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			return new DateTimeSlider(ActivityGroupTracking.group,
					mDateTimeSetListener, mCalendar);
		}
		return super.onCreateDialog(id);
	}

	private DateSlider.OnDateSetListener mDateTimeSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			// every time we clicked on the date slider
			mCalendar = selectedDate;
			updateDate();
		}

		private void updateDate() {
			// update time for selected position
			DbAdapter db = new DbAdapter(ctx);
			db.open();

			if (ActivityGroupTracking.group.getTrackingData().listTracking.get(
					selectedPosition).getMedicineEvent() != null) {
				// update medicine event
				db.updateMedicineEventDate(
						new Functions().getDateAsStringFromCalendar(mCalendar),
						ActivityGroupTracking.group.getTrackingData().listTracking
								.get(selectedPosition).getMedicineEvent()
								.getId());
			} else if (ActivityGroupTracking.group.getTrackingData().listTracking
					.get(selectedPosition).getExerciseEvent() != null) {
				// update exercise event
				db.updateExerciseEventDate(
						new Functions().getDateAsStringFromCalendar(mCalendar),
						ActivityGroupTracking.group.getTrackingData().listTracking
								.get(selectedPosition).getExerciseEvent()
								.getId());
			} else if (ActivityGroupTracking.group.getTrackingData().listTracking
					.get(selectedPosition).getBloodGlucoseEvent() != null) {
				// update glucose event
				db.updateGlucoseEventDate(
						new Functions().getDateAsStringFromCalendar(mCalendar),
						ActivityGroupTracking.group.getTrackingData().listTracking
								.get(selectedPosition).getBloodGlucoseEvent()
								.getId());
			}

			db.close();

			// refresh the list
			ActivityGroupTracking.group.restartThisActivity();
		}
	};

	private void setNext() {
		setSelection(searchPosition);
	}

	private void onClickFindNext() {
		try {
			int lastSearch = searchPosition;
			for (int i = searchPosition + 1; i < ActivityGroupTracking.group
					.getTrackingData().listTracking.size(); i++) {
				if (ActivityGroupTracking.group.getTrackingData().listTracking
						.get(i).getMealEvent() != null) {
					for (DBMealFood obj : ActivityGroupTracking.group
							.getTrackingData().listTracking.get(i)
							.getMealEvent().getMealFood()) {
						String foodName = obj.getFoodName().toString()
								.toLowerCase();
						if (foodName.indexOf(et.getText().toString()
								.toLowerCase()) != -1) {
							searchPosition = i;
							return;
						} else {
							// we get here if we are on the last search item (
							// go back to top )
							searchPosition = 0;
						}
					}
				}
			}

			// restart the searchposition when we didnt find a new item and
			// search for the first one again
			if (lastSearch == searchPosition) {
				searchPosition = 0;
				for (int i = searchPosition + 1; i < ActivityGroupTracking.group
						.getTrackingData().listTracking.size(); i++) {
					if (ActivityGroupTracking.group.getTrackingData().listTracking
							.get(i).getMealEvent() != null) {
						for (DBMealFood obj : ActivityGroupTracking.group
								.getTrackingData().listTracking.get(i)
								.getMealEvent().getMealFood()) {
							String foodName = obj.getFoodName().toString()
									.toLowerCase();
							if (foodName.indexOf(et.getText().toString()
									.toLowerCase()) != -1) {
								searchPosition = i;
								return;
							} else {
								// we get here if we are on the last search item
								// (
								// go back to top )
								searchPosition = 0;
							}
						}
					}
				}
			}

		} catch (Exception e) {
			// we get here if we selected our final item in the list
			// then serachposition + 1 will crash :(
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();

		checkHideButtonLoadMore();
	}

	private void checkHideButtonLoadMore() {
		// check if we need to show the button see more
		if (ActivityGroupTracking.group.getTrackingData().thereAreMoreItems) {
			llButtons.setVisibility(View.VISIBLE);
		} else {
			llButtons.setVisibility(View.GONE);
			// show the search stuff
			llSearch.setVisibility(View.VISIBLE);
		}
	}

	private void fillListView() {
		setListAdapter(null);

		if (ActivityGroupTracking.group.getTrackingData().listTracking.size() <= 0) {
			// add a no data part to the list
			// create the list
			ActivityGroupTracking.group.getTrackingData().listTracking = new ArrayList<DBTracking>();
			// Fill the list with 1 item "no records found"
			ActivityGroupTracking.group.getTrackingData().listTracking
					.add(new DBTracking(null, null, null, null, null, false,
							getResources().getString(R.string.noTrackingValues)));
		}

		adapter = new CustomArrayAdapterDBTracking(this, 0,
				ActivityGroupTracking.group.getTrackingData().listTracking,
				ActivityGroupMeal.group.getFoodData().dbFontSize,
				ActivityGroupMeal.group.getFoodData().defaultValue);

		setListAdapter(adapter);
	}

	private class AsyncGetNextMonthOfData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			ActivityGroupTracking.group.getTrackingData().calendarDate.add(
					Calendar.MONTH, -1);

			ActivityGroupTracking.group.getTrackingData().loopTrueGethingData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// set listtracking as listview
			fillListView();

			// check if we have to hide the button " see more "
			checkHideButtonLoadMore();

			// go to last listview item
			setSelection(ActivityGroupTracking.group.getTrackingData().listTracking
					.size());

			super.onPostExecute(result);
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// save clicked position so we can update the time
		selectedPosition = position;

		// click on medine event
		if (ActivityGroupTracking.group.getTrackingData().listTracking.get(
				position).getMedicineEvent() != null) {
			showPopUpMedicineEvent(
					ActivityGroupTracking.group.getTrackingData().listTracking
							.get(position).getMedicineEvent(), position);
		} else if (ActivityGroupTracking.group.getTrackingData().listTracking
				.get(position).getExerciseEvent() != null) {
			showPopUpExerciseEvent(
					ActivityGroupTracking.group.getTrackingData().listTracking
							.get(position).getExerciseEvent(), position);
		} else if (ActivityGroupTracking.group.getTrackingData().listTracking
				.get(position).getBloodGlucoseEvent() != null) {
			showPopUpGlucoseEvent(
					ActivityGroupTracking.group.getTrackingData().listTracking
							.get(position).getBloodGlucoseEvent(), position);
		} else if (ActivityGroupTracking.group.getTrackingData().listTracking
				.get(position).getMealEvent() != null) {

			float totalValue = 0;
			String text = " \n";
			String defaultValueText = "";
			String calculatedInsuline = "";

			for (DBMealFood mealFood : ActivityGroupTracking.group
					.getTrackingData().listTracking.get(position)
					.getMealEvent().getMealFood()) {
				float calculatedValue = 0f;

				switch (ActivityGroupMeal.group.getFoodData().defaultValue) {
				case 1:
					calculatedValue = ((mealFood.getUnit().getCarbs() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.short_carbs);
					break;
				case 2:
					calculatedValue = ((mealFood.getUnit().getProtein() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.amound_of_protein);
					break;
				case 3:
					calculatedValue = ((mealFood.getUnit().getFat() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.amound_of_fat);
					break;
				case 4:
					calculatedValue = ((mealFood.getUnit().getKcal() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.short_kcal);
					break;
				}

				totalValue += calculatedValue;

				// round calculatedValue
				calculatedValue = new Functions().roundFloats(calculatedValue,
						1);

				text += "" + mealFood.getAmount() + " "
						+ mealFood.getUnit().getName() + " "
						+ mealFood.getFoodName() + " (" + calculatedValue + " "
						+ defaultValueText + ") \n";
			}

			if (ActivityGroupTracking.group.getTrackingData().listTracking
					.get(position).getMealEvent().getInsulineRatio() != 0) {
				calculatedInsuline = "\n"
						+ getResources().getString(R.string.calculated)
						+ " "
						+ ActivityGroupTracking.group.getTrackingData().listTracking
								.get(position).getMealEvent()
								.getCalculatedInsulineAmount()
						+ " "
						+ getResources().getString(R.string.insulineUnit)
						+ " \n"
						+ ActivityGroupTracking.group.getTrackingData().listTracking
								.get(position).getMealEvent()
								.getInsulineRatio() + " "
						+ getResources().getString(R.string.insulineRatio);
			} else {
				calculatedInsuline = "";
			}

			// Round total value
			totalValue = new Functions().roundFloats(totalValue, 1);

			showPopUpWhatToDoWithMealEvent(
					ActivityGroupTracking.group.getTrackingData().listTracking
							.get(position).getMealEvent().getId(),
					getResources().getString(R.string.realTotal) + ": "
							+ totalValue + " " + defaultValueText + " \n"
							+ text + " \n" + calculatedInsuline, position);
		}
	}

	private void showPopUpGlucoseEvent(final DBBloodGlucoseEvent event,
			final int location) {
		// Show a dialog
		new AlertDialog.Builder(ActivityGroupTracking.group)
				.setPositiveButton(
						getResources().getString(R.string.changeTime),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// update time
								mCalendar.setTime(new Functions()
										.getYearMonthDayHourMinutesAsDateFromString(event
												.getTimeStamp()));

								// show dialog to update time
								showDialog(0);
							}
						})
				.setNeutralButton(getResources().getString(R.string.cancel),
						null)
				.setNegativeButton(getResources().getString(R.string.delete),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Delete from database
								deleteGlucoseEventFromDatabase(event.getId());
								// refresh list
								// delete the item from the list
								ActivityGroupTracking.group.getTrackingData().listTracking
										.remove(location);

								adapter.notifyDataSetChanged();
							}
						})
				.setMessage(
						""
								+ new Functions().getTimeFromString(event
										.getTimeStamp()) + " \n"
								+ event.getAmount() + " " + event.getUnit())
				.show();
	}

	private void showPopUpExerciseEvent(final DBExerciseEvent event,
			final int location) {
		// Show a dialog
		new AlertDialog.Builder(ActivityGroupTracking.group)
				.setPositiveButton(
						getResources().getString(R.string.changeTime),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// update time
								mCalendar.setTime(new Functions()
										.getYearMonthDayHourMinutesAsDateFromString(event
												.getTimeStamp()));

								// show dialog to update time
								showDialog(0);
							}
						})
				.setNeutralButton(getResources().getString(R.string.cancel),
						null)
				.setNegativeButton(getResources().getString(R.string.delete),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Delete from database
								deleteExerciseEventFromDatabase(event.getId());
								// refresh list
								// delete the item from the list
								ActivityGroupTracking.group.getTrackingData().listTracking
										.remove(location);

								adapter.notifyDataSetChanged();
							}
						})
				.setMessage(
						""
								+ new Functions().getTimeFromString(event
										.getTimeStamp())
								+ " - "
								+ new Functions().getTimeFromDuration(
										event.getTimeStamp(),
										event.getEndTime()) + " \n"
								+ event.getDescription()).show();
	}

	private void showPopUpMedicineEvent(final DBMedicineEvent medicineEvent,
			final int location) {
		// Show a dialog
		new AlertDialog.Builder(ActivityGroupTracking.group)
				.setPositiveButton(
						getResources().getString(R.string.changeTime),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// update time
								mCalendar.setTime(new Functions()
										.getYearMonthDayHourMinutesAsDateFromString(medicineEvent
												.getTimeStamp()));

								// show dialog to update time
								showDialog(0);
							}
						})
				.setNegativeButton(getResources().getString(R.string.delete),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click positive button
								// Delete from database
								deleteMedicineEventFromDatabase(medicineEvent
										.getId());

								// delete the item from the list
								ActivityGroupTracking.group.getTrackingData().listTracking
										.remove(location);

								adapter.notifyDataSetChanged();
							}
						})
				.setNeutralButton(getResources().getString(R.string.cancel),
						null)
				.setMessage(
						""
								+ new Functions()
										.getTimeFromString(medicineEvent
												.getTimeStamp()) + " \n"
								+ medicineEvent.getAmount() + " "
								+ medicineEvent.getMedicineTypeUnit() + " "
								+ medicineEvent.getMedicineTypeName()).show();
	}

	private void deleteGlucoseEventFromDatabase(long id) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.deleteBloodGlucoseEventByID(id);
		db.close();
	}

	private void deleteExerciseEventFromDatabase(long id) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.deleteExerciseEventByID(id);
		db.close();
	}

	private void deleteMedicineEventFromDatabase(long id) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.deleteMedicineEventByID(id);
		db.close();
	}

	private void showPopUpWhatToDoWithMealEvent(final long mealID, String text,
			final int location) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupTracking.group);
		builder.setMessage(text)
				.setPositiveButton(
						getResources().getString(R.string.addToSelection),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								addMealEventToSelections(mealID);
								// go to selected food tab ?
								goToFoodTab();
							}
						})
				.setNeutralButton(getResources().getString(R.string.cancel),
						null)
				.setNegativeButton(getResources().getString(R.string.delete),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// delete from database
								deleteMealEventFromDatabase(mealID);

								// delete the item from the list
								ActivityGroupTracking.group.getTrackingData().listTracking
										.remove(location);

								adapter.notifyDataSetChanged();
							}
						}).show();
	}

	private void deleteMealEventFromDatabase(long mealID) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cMealFoods = db.fetchMealFoodByMealEventID(mealID);
		if (cMealFoods.getCount() > 0) {
			cMealFoods.moveToFirst();
			do {
				db.deleteMealFoodByID(cMealFoods.getLong(cMealFoods
						.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_ID)));
			} while (cMealFoods.moveToNext());
		}
		cMealFoods.close();
		db.deleteMealEventByID(mealID);
		db.close();
	}

	private void addMealEventToSelections(long mealID) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cMealFoods = db.fetchMealFoodByMealEventID(mealID);
		cMealFoods.moveToFirst();

		do {
			db.createSelectedFood(
					cMealFoods
							.getFloat(cMealFoods
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_AMOUNT)),
					cMealFoods.getLong(cMealFoods
							.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_FOODUNITID)),
					new Functions().getDateAsStringFromCalendar(Calendar
							.getInstance()));

			ActivityGroupMeal.group.getFoodData().countSelectedFood++;
		} while (cMealFoods.moveToNext());

		cMealFoods.close();
		db.close();
	}

	private void goToFoodTab() {
		try {
			// flag to start show selected food
			// ActivityGroupMeal.group.goToSeletedFood();

			// Go to tracking tab when clicked on add
			ShowHomeTab parentActivity;
			parentActivity = (ShowHomeTab) this.getParent().getParent();
			parentActivity.goToTab(DataParser.activityIDMeal);

			// show sucess message
			Toast.makeText(this,
					getResources().getString(R.string.sucesfull_added),
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			showPopUp(e.toString());
		}
	}

	private void showPopUp(String string) {
		new AlertDialog.Builder(ActivityGroupTracking.group).setTitle("Error")
				.setMessage(string)
				.setNeutralButton(getResources().getString(R.string.oke), null)
				.show();
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
					ActivityGroupTracking.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupTracking.group);
		builder.setMessage(getResources().getString(R.string.sureToExit))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}

}
