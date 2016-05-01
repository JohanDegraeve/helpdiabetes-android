// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Settings;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.Custom.CustomArrayAdapterCharSequenceSettings;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowSettingsBackup extends ListActivity {
	private ProgressDialog pd;
	private ProgressDialog pdProcents;
	private Button btBack;
	private DbAdapter db;

	private int counter, max;
	private long foodLanguageID;
	private String filename = "";

	// for letting the user choose a file
	private String[] mFileList;
	private File mPath;
	private String mChosenFile;
	private static final String FTYPE = ".csv";
	private static final int DIALOG_LOAD_FILE = 1000;
	private boolean noFilesFound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_backup);

		// track we come here
		ActivityGroupSettings.group.parent
				.trackPageView(TrackingValues.pageShowSettingBackup);

		db = new DbAdapter(ActivityGroupSettings.group);
		foodLanguageID = ActivityGroupMeal.group.getFoodData().foodLanguageID;
		checkFoodLanguageID();

		pd = new ProgressDialog(this);
		pdProcents = new ProgressDialog(this);

		counter = 0;
		max = 0;

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

	}

	private void checkFoodLanguageID() {
		// first check what foodlanguageID is
		// if its 0 we have to get the right foodlanguageID first!
		// it is 0 when the users changes to setting tab before food is loaded
		if (foodLanguageID == 0) {
			db.open();
			// First get the food language id
			Cursor cSetting = db
					.fetchSettingByName(DbSettings.setting_language);
			// there will always be 1 record
			if (cSetting.getCount() > 0) {
				// move the cursor to the first record
				cSetting.moveToFirst();
				// put the foodLanguageID in the variable
				foodLanguageID = cSetting
						.getLong(cSetting
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
			} else {
				// if we dont have the record ( something went wrong )
				// and we set foodLanguage to 1 so the app wont crash
				foodLanguageID = 1;
			}

			// close the cursor
			cSetting.close();
			db.close();
			ActivityGroupMeal.group.getFoodData().foodLanguageID = foodLanguageID;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();
	}

	private void fillListView() {
		CustomArrayAdapterCharSequenceSettings adapter = new CustomArrayAdapterCharSequenceSettings(
				this, R.layout.row_custom_array_adapter_charsequence_settings,
				getCharSequenceList());
		setListAdapter(adapter);
	}

	private List<CharSequence> getCharSequenceList() {
		List<CharSequence> value = new ArrayList<CharSequence>();
		value.add(getResources().getString(R.string.copyDatabaseToSD));
		value.add(getResources().getString(R.string.databaseFromSDToPhone));
		value.add("");
		value.add(getResources().getString(R.string.foodToExcel));
		value.add(getResources().getString(R.string.excelToDB));

		return value;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		counter = 0;
		max = 0;
		switch (position) {
		case 0:
			try {// track we come here
				ActivityGroupSettings.group.parent.trackEvent(
						TrackingValues.eventCategorySettings,
						TrackingValues.eventCategorySettingsBackupDB);
			} catch (NullPointerException e) {
			}

			showProgressDialog(getResources().getString(
					R.string.copyDatabaseToSD));
			new AsyncCopyDBToSD().execute();
			break;
		case 1:
			showPopUpWarning();
			break;
		case 3:
			try {// track we come here
				ActivityGroupSettings.group.parent.trackEvent(
						TrackingValues.eventCategorySettings,
						TrackingValues.eventCategorySettingsExportCSV);
			} catch (NullPointerException e) {
			}
			// show popup to ask filename
			writeFile();
			break;
		case 4:
			// reloade file list on root of sd
			loadFileList();
			// select file name
			showDialog(DIALOG_LOAD_FILE);
			break;
		}
	}

	private void showPopUpWarning() {
		new AlertDialog.Builder(ActivityGroupSettings.group)
				.setTitle(getResources().getString(R.string.warning))
				.setMessage(
						getResources()
								.getString(R.string.deleteCurrentDatabase))
				.setPositiveButton(getResources().getString(R.string.oke),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								try {// track we come here
									ActivityGroupSettings.group.parent
											.trackEvent(
													TrackingValues.eventCategorySettings,
													TrackingValues.eventCategorySettingsRestoreDB);
								} catch (NullPointerException e) {
								}

								// if we click the ok button
								// start loading stuff
								showProgressDialog(getResources().getString(
										R.string.loading));
								new AsyncCopyDBFromSDToPhone().execute();
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						null).show();
	}

	private void writeFile() {
		filename = "helpDiabetesExported";

		db.open();
		max = db.fetchFoodByLanguageIDAndPlatformNotStandard(foodLanguageID)
				.getCount();
		db.close();

		if (max > 0) {
			showProgressDialogWithProcents(getResources().getString(
					R.string.foodToExcel));
		}

		new AsyncCopyDBToCSV().execute();
	}

	private void selectedFileToImport() {
		try {// track we come here
			ActivityGroupSettings.group.parent.trackEvent(
					TrackingValues.eventCategorySettings,
					TrackingValues.eventCategorySettingsImportCSV);
		} catch (NullPointerException e) {
		}

		max = getRowsFromCSV(mChosenFile);

		if (max > 0) {
			showProgressDialogWithProcents(getResources().getString(
					R.string.excelToDB));
		}

		new AsyncCopySCVToDB().execute(mChosenFile);

	}

	private int getRowsFromCSV(String filename) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File inputFile = new File(sd, filename);

			InputStream is = new BufferedInputStream(new FileInputStream(
					inputFile));

			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
			}
			return count;
		} catch (Exception e) {
		}
		return 0;
	}

	private void showProgressDialogWithProcents(String text) {
		pdProcents = new ProgressDialog(ActivityGroupSettings.group);
		pdProcents.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pdProcents.setCancelable(false);
		pdProcents.setProgress(0);
		pdProcents.setMax(max);
		pdProcents.show();
	}

	private void showProgressDialog(String text) {
		pd = ProgressDialog.show(ActivityGroupSettings.group, "", text, true);
	}

	// asynctask to copy database from sd to phone
	private class AsyncCopyDBFromSDToPhone extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				File sd = Environment.getExternalStorageDirectory();
				File data = Environment.getDataDirectory();

				String currentdbPath = DbAdapter.DB_PATH_PART_TWO
						+ DbAdapter.DB_NAME;

				File currentDB = new File(data, currentdbPath);
				File backupDB = new File(sd, DbAdapter.DB_NAME);

				FileChannel src = new FileInputStream(backupDB).getChannel();
				FileChannel dst = new FileOutputStream(currentDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

				return null;
			} catch (Exception e) {
				return e.toString();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();

			if (result == null) {
				// show popup to exit application
				showPopUpToExit();
			} else {
				makeToast(getResources().getString(
						R.string.something_went_wrong)
						+ "\n" + result);
			}
			super.onPostExecute(result);
		}
	}

	private void showPopUpToExit() {
		new AlertDialog.Builder(ActivityGroupMeal.group)
				.setTitle(getResources().getString(R.string.warning))
				.setMessage(getResources().getString(R.string.app_will_exit))
				.setPositiveButton(getResources().getString(R.string.oke),
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// exit application
								ActivityGroupMeal.group.killApplication();
							}
						}).show();
	}

	private void makeToast(String result) {
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	// asynctask to import food & food units into the database
	private class AsyncCopySCVToDB extends AsyncTask<String, Void, String> {

		@Override
		protected void onProgressUpdate(Void... values) {
			pdProcents.setProgress(counter);
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {
			db.open();

			File sd = Environment.getExternalStorageDirectory();
			String seperator = ";";
			if (sd.canRead()) {
				File inputFile = new File(sd, params[0]);

				try {
					BufferedReader in = new BufferedReader(new FileReader(
							inputFile));
					String str;

					while ((str = in.readLine()) != null) {
						// for every line
						String foodName = "";
						String unitName1 = "";
						Float standardAmount1 = 0F, prot1 = 0F, kcal1 = 0F, fat1 = 0F, carb1 = 0F;

						// Food name end position
						int positionOfEndFoodName = str.indexOf(seperator);
						// unit name end position
						int positionOfFirstUnitName = str.indexOf(seperator,
								positionOfEndFoodName + 1);
						// standard amount end position
						int positionOfFirstStandardAmount = str.indexOf(
								seperator, positionOfFirstUnitName + 1);
						// carb end position
						int positionOfFirstCarb = str.indexOf(seperator,
								positionOfFirstStandardAmount + 1);
						// prot end position
						int positionOfFirstprot = str.indexOf(seperator,
								positionOfFirstCarb + 1);
						// fat end position
						int positionOfFirstFat = str.indexOf(seperator,
								positionOfFirstprot + 1);
						// kcal end position
						int positionOfFirstKcal = str.indexOf(seperator,
								positionOfFirstFat + 1);
						if (positionOfEndFoodName != -1) {
							try {
								foodName = str.substring(0,
										positionOfEndFoodName);
							} catch (Exception e) {
							}
						}
						if (positionOfFirstUnitName != -1) {
							try {
								unitName1 = str.substring(
										positionOfEndFoodName + 1,
										positionOfFirstUnitName);
							} catch (Exception e) {
							}
						}

						try {
							standardAmount1 = Float.parseFloat(str.substring(
									positionOfFirstUnitName + 1,
									positionOfFirstStandardAmount).replace(",",
									"."));
						} catch (Exception e) {
						}

						try {
							carb1 = Float.parseFloat(str.substring(
									positionOfFirstStandardAmount + 1,
									positionOfFirstCarb).replace(",", "."));
						} catch (Exception e) {
						}

						try {
							prot1 = Float.parseFloat(str.substring(
									positionOfFirstCarb + 1,
									positionOfFirstprot).replace(",", "."));
						} catch (Exception e) {
						}

						try {
							fat1 = Float.parseFloat(str
									.substring(positionOfFirstprot + 1,
											positionOfFirstFat).replace(",",
											"."));
						} catch (Exception e) {
						}

						try {
							// this will be -1 when we didnt at a seperator on
							// the
							// end of the line!
							if (positionOfFirstKcal != -1) {
								kcal1 = Float.parseFloat(str.substring(
										positionOfFirstFat + 1,
										positionOfFirstKcal).replace(",", "."));
							} else {
								// if we dont have a seperator we just put
								// everything till the end in kcal

								kcal1 = Float.parseFloat(str.substring(
										positionOfFirstFat + 1).replace(",",
										"."));

							}
						} catch (Exception e) {
						}

						// trim the stuff
						foodName = foodName.trim();
						unitName1 = unitName1.trim();

						// do stuff for adding food and first unit to the
						// database
  
						// if foodname aint empty and unitname aint empty and
						// standardamount not 0
						if (!foodName.equals("") && !unitName1.equals("")
								&& standardAmount1 != 0) {
							Long foodID = db.createFood(foodName,
									foodLanguageID);

							db.createFoodUnit(foodID, unitName1,
									standardAmount1, carb1, prot1, fat1, kcal1);

							// if we had a seperator behind the first kcal we go
							// further
							if (positionOfFirstKcal != -1 && foodID != 0) {
								int positionOfUnitName = str.indexOf(seperator,
										positionOfFirstKcal + 1);
								// if the seperator behind the first kcal wasnt
								// the
								// last one we go further
								if (positionOfUnitName != -1) {
									int lastPosition = positionOfFirstKcal;
									int positionOfSeperator = str.indexOf(
											seperator, lastPosition + 1);

									while (positionOfSeperator != -1) {

										String unitName;
										Float standardAmount = 0F, prot = 0F, kcal = 0F, fat = 0F, carb = 0F;

										unitName = str.substring(
												lastPosition + 1,
												positionOfSeperator);
										lastPosition = positionOfSeperator;
										positionOfSeperator = str.indexOf(
												seperator, lastPosition + 1);

										try {
											standardAmount = Float
													.parseFloat(str
															.substring(
																	lastPosition + 1,
																	positionOfSeperator)
															.replace(",", "."));
											lastPosition = positionOfSeperator;
											positionOfSeperator = str
													.indexOf(seperator,
															lastPosition + 1);
										} catch (Exception e) {
										}

										try {
											carb = Float
													.parseFloat(str
															.substring(
																	lastPosition + 1,
																	positionOfSeperator)
															.replace(",", "."));
											lastPosition = positionOfSeperator;
											positionOfSeperator = str
													.indexOf(seperator,
															lastPosition + 1);
										} catch (Exception e) {
										}

										try {
											prot = Float
													.parseFloat(str
															.substring(
																	lastPosition + 1,
																	positionOfSeperator)
															.replace(",", "."));
											lastPosition = positionOfSeperator;
											positionOfSeperator = str
													.indexOf(seperator,
															lastPosition + 1);
										} catch (Exception e) {
										}

										try {
											fat = Float
													.parseFloat(str
															.substring(
																	lastPosition + 1,
																	positionOfSeperator)
															.replace(",", "."));
											lastPosition = positionOfSeperator;
											positionOfSeperator = str
													.indexOf(seperator,
															lastPosition + 1);
										} catch (Exception e) {
										}

										try {
											if (positionOfSeperator != -1) {
												kcal = Float
														.parseFloat(str
																.substring(
																		lastPosition + 1,
																		positionOfSeperator)
																.replace(",",
																		"."));
												lastPosition = positionOfSeperator;
												positionOfSeperator = str
														.indexOf(
																seperator,
																lastPosition + 1);
											} else {
												// if we are at the last kcal
												// and we
												// didnt at a seperator on the
												// end
												// of the line!
												kcal = Float
														.parseFloat(str
																.substring(
																		lastPosition + 1)
																.replace(",",
																		"."));
											}
										} catch (Exception e) {
										}

										if (!unitName.equals("")
												&& standardAmount != 0) {
											// trim unitName
											unitName = unitName.trim();

											// do stuff for adding unit to the
											// database
											db.createFoodUnit(foodID, unitName,
													standardAmount, carb, prot,
													fat, kcal);
										}
									}
								}
							}
						}
						counter++;
						publishProgress(null);
						// end of the while loop! ( dont go past here with code
					}
					return getResources().getString(
							R.string.sucessfull_copied_records_to_database);
				} catch (FileNotFoundException e) {
					return e.toString();
				} catch (IOException e) {
					return e.toString();
				}
			} else {
				return getResources()
						.getString(R.string.could_not_read_from_sd);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			db.close();

			// refresh the food data on showLoadingFoodData
			ActivityGroupMeal.group.restartThisActivity();

			pdProcents.dismiss();

			Toast.makeText(ActivityGroupSettings.group, result,
					Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}

	// asynctask to export food & food units to excel
	private class AsyncCopyDBToCSV extends AsyncTask<Void, Void, String> {

		@Override
		protected void onProgressUpdate(Void... values) {
			pdProcents.setProgress(counter);
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(Void... params) {
			db.open();
			Cursor cFood = db
					.fetchFoodByLanguageIDAndPlatformNotStandard(foodLanguageID);
			if (cFood.getCount() > 0) {
				cFood.moveToFirst();
				File sd = Environment.getExternalStorageDirectory();
				if (sd.canWrite()) {
					File outputFile = new File(sd, filename + ".csv");

					int fileNumber = 1;

					// update outputFile until we have a not existing number
					while (outputFile.exists()) {
						outputFile = new File(sd, filename + "-" + fileNumber
								+ ".csv");
						fileNumber++;
					}

					try {
						FileWriter writer = new FileWriter(outputFile);
						String seperator = ";";

						// write first line with example
						// writer.append("Example: FOODNAME ; UNITNAME1 ; standardamount1 ; carbs1 ; prot1 ; fat1 ; kcal1 ; UNITNAME2 ; standardamount2 ; carb2 ; prot2 ; fat2 ; kcal2 ; UNITNAME3 ; standardamount3 ; carbs3 ; prot3 ; fat3 ; kcal3");
						// writer.append("\n");

						// writer order:
						// FOODNAME ; UNITNAME ; standardamount ; carbs ; prot ;
						// fat ; kcal ; UNITNAME ; standrdamount ; carbs ; prot
						// ; kcal

						do {
							Cursor cUnit = db
									.fetchFoodUnitByFoodId(cFood.getLong(cFood
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)));
							if (cUnit.getCount() > 0) {
								cUnit.moveToFirst();
								// write the food name
								writer.append(cFood.getString(cFood
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
								writer.append(seperator);

								do {
									// write the unit name
									writer.append(cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)));
									writer.append(seperator);

									// write the standardamount
									writer.append(cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
									writer.append(seperator);

									// write the carbs
									writer.append(cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)));
									writer.append(seperator);

									// write the prot
									writer.append(cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)));
									writer.append(seperator);

									// write the fat
									writer.append(cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)));
									writer.append(seperator);

									// write the kcal
									writer.append(cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));
									writer.append(seperator);

								} while (cUnit.moveToNext());
								writer.append("\n");
							}
							cUnit.close();

							counter++;
							publishProgress(null);

						} while (cFood.moveToNext());
						cFood.close();

						writer.flush();
						writer.close();
						return getResources().getString(
								R.string.succesfull_wrote_csv_file)
								+ " (" + outputFile.getName() + ")";
					} catch (IOException e) {
						return e.toString();
					}
				} else {
					cFood.close();
					return getResources().getString(R.string.could_not_write);
				}
			} else {
				cFood.close();
				return getResources().getString(R.string.no_own_food);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			db.close();
			pdProcents.dismiss();
			Toast.makeText(ActivityGroupSettings.group, result,
					Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}

	// asynctask to copy database to sd
	private class AsyncCopyDBToSD extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			if (db.copyDatabaseToSD())
				return getResources()
						.getString(R.string.succesfull_wrote_to_sd);
			else
				return getResources().getString(R.string.could_not_write);

		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			Toast.makeText(ActivityGroupSettings.group, result,
					Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if we press the back key
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			// return false so the keydown event from activitygroup will get
			// called
			return false;
		else
			return super.onKeyDown(keyCode, event);
	}

	private void loadFileList() {
		mPath = new File(Environment.getExternalStorageDirectory(), "");

		try {
			mPath.mkdirs();
		} catch (SecurityException e) {

		}

		if (mPath.exists()) {

			noFilesFound = false;

			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					return filename.contains(FTYPE);
				}
			};

			mFileList = mPath.list(filter);

			if (mFileList.length == 0) {
				// add the no files found thing
				noFilesFound = true;
				mFileList = new String[1];
				mFileList[0] = ""
						+ ActivityGroupSettings.group.getResources().getString(
								R.string.noFilesFound);
			}
		} else {
			noFilesFound = true;
			mFileList = new String[1];
			mFileList[0] = ""
					+ ActivityGroupSettings.group.getResources().getString(
							R.string.noFilesFound);
		}

	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(ActivityGroupSettings.group);

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle(getResources().getString(R.string.chooseFile));

			builder.setItems(mFileList, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (!noFilesFound) {
						mChosenFile = mFileList[which];
						selectedFileToImport();
					}
				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}
}
