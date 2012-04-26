package be.goossens.oracle.Show.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Objects.DBFoodComparable;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.ExcelCharacter;
import be.goossens.oracle.Rest.FoodComparator;

public class ShowFoodList extends ListActivity {
	// dbHelper to get the food list out the database
	private DbAdapter dbHelper;

	// we need this context for the asynctask to add one food item to the list
	private Context context;

	// editTextSearch is the search box above the listview
	private EditText editTextSearch;

	private CustomArrayAdapterFoodList customArrayAdapterFoodList;

	private List<DBFoodComparable> listDBFoodComparableWithFilter;
	private boolean listWithFilter;

	private Button btCreateFood, btSelections, btSearch;

	// The textview with text = loading...
	private TextView tvLoading;

	// Animation for the button selections
	// this animation is used to let the button flash when we have selections
	private Animation animation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_food_list, null);
		setContentView(contentView);

		customArrayAdapterFoodList = null;

		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		btCreateFood = (Button) findViewById(R.id.buttonShowFoodListShowCreateFood);
		btSelections = (Button) findViewById(R.id.buttonShowFoodListShowSelectedFood);
		btSearch = (Button) findViewById(R.id.buttonShowFoodListSearch);
		tvLoading = (TextView) findViewById(R.id.textViewLoading);

		dbHelper = new DbAdapter(this);
		context = this;

		// set animation
		animation = new AlphaAnimation(1, 0); // Change alpha from fully visible
												// to invisible
		animation.setDuration(800); // duration ( 500 = half a second )
		animation.setInterpolator(new LinearInterpolator()); // do not alter
																// animation
																// rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation
														// infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
													// end so the button will
													// fade back in

		editTextSearch.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (customArrayAdapterFoodList != null
						&& ActivityGroupMeal.group.getFoodData().listFood
								.size() > 0 && !listWithFilter)
					setSelection(customArrayAdapterFoodList
							.getFirstMatchingItem(s));
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				// when the edittext length > 1 we highlight the search button
				// else we show the gray one
				// update: we only do this method when !listWithFilter becaus
				// else we show the red cross
				checkSearchButton();
			}
		});

		btCreateFood.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickCreateNewFood(v);
			}
		});

		btSelections.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickShowSelectedFood(v);
			}
		});

		btSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickSearch();
			}
		});
	} 

	// This method will show the gray or the highlighted search button
	private void checkSearchButton() {
		if (editTextSearch.length() > 0) {
			btSearch.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_search_yes));
		} else {
			btSearch.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_search_no));
		}
	}

	private void onClickSearch() {
		if (listWithFilter) {
			listWithFilter = false;
			checkSearchButton();
			setListAdapter(null);
			updateListAdapter();
		} else if (!listWithFilter) {
			if (editTextSearch.length() > 0) {
				listWithFilter = true;
				btSearch.setBackgroundDrawable(getResources().getDrawable(
						android.R.drawable.ic_delete));
				tvLoading.setVisibility(View.VISIBLE);
				setListAdapter(null);
				// start asynctask to get food
				new AsyncGetSearch().execute();
			} 
		}
	}

	private class AsyncGetSearch extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			dbHelper.open();
			fillObjectsWithFilter();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateListAdapterWithFilter();
			super.onPostExecute(result);
		}

	}

	protected void onResume() {
		if (ActivityGroupMeal.group.newFoodID != 0) {
			// when we come here we just created a new fooditem from
			// showCreateFood
			// set the listadapter = null
			setListAdapter(null);

			// set the loading textview
			tvLoading.setVisibility(View.VISIBLE);

			// run a asynctask to add the foodItem to the list and order the
			// objects
			new AsyncAddOneFoodItemToList()
					.execute(ActivityGroupMeal.group.newFoodID);

			// set the newFoodID = 0
			ActivityGroupMeal.group.newFoodID = 0;
		} else {
			// when we come here our asynctask is donne with fetching data from
			// the database
			tvLoading.setVisibility(View.VISIBLE);
			// so we update the listadapter
			updateListAdapter();
		}

		// when deleteFoodIdFromList != 0 we have to delete that object from the
		// list
		if (ActivityGroupMeal.group.deleteFoodIDFromList != 0) {
			ActivityGroupMeal.group.getFoodData().listFood
					.remove(getPositionOfFOODID(ActivityGroupMeal.group.deleteFoodIDFromList));
			ActivityGroupMeal.group.deleteFoodIDFromList = 0;
			// update list adapter
			updateListAdapter();
		}

		// if we added a food item to the selectedFood list this boolean = true
		if (ActivityGroupMeal.group.addedFoodItemToList) {
			ActivityGroupMeal.group.addedFoodItemToList = false;
			// do selectedFood + 1;
			ActivityGroupMeal.group.getFoodData().countSelectedFood++;
			// and let it blink
			animateButton();
		}

		// update button with ic
		updateButton();

		btSearch.requestFocus();

		super.onResume();
	};

	private void animateButton() {
		btSelections.setAnimation(animation);
	}

	private class AsyncAddOneFoodItemToList extends AsyncTask<Long, Void, Long> {

		@Override
		protected Long doInBackground(Long... params) {
			DbAdapter db = new DbAdapter(context);
			db.open();

			// get the foodItem
			Cursor cFood = db.fetchFood(params[0]);
			if (cFood.getCount() > 0) {
				cFood.moveToFirst();

				// create a DBFoodComparable object
				DBFoodComparable newFood = new DBFoodComparable(
						cFood.getLong(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
						cFood.getLong(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_FOODLANGUAGEID)),
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_VISIBLE)),
						cFood.getLong(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_CATEGORYID)),
						cFood.getLong(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_USERID)),
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
				// add new food item to list
				ActivityGroupMeal.group.getFoodData().listFood.add(newFood);

				// sort the list
				ActivityGroupMeal.group.getFoodData().sortObjects();
			}
			cFood.close();
			db.close();
			return params[0];
		}

		@Override
		protected void onPostExecute(Long result) {
			// update the list adapter
			updateListAdapter();

			// go to the new created food item
			goToFoodID(result);

			super.onPostExecute(result);
		}

	}

	public void onClickCreateNewFood(View view) {
		// Go to new page to create new food
		Intent i = new Intent(this, ShowCreateFood.class)
				.putExtra(DataParser.foodSearchValue,
						editTextSearch.getText().toString()).addFlags(
						Intent.FLAG_ACTIVITY_CLEAR_TOP);
		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDShowFoodList, i)
				.getDecorView();
		ActivityGroupMeal.group.setContentView(v);
	}

	public void updateListAdapterWithFilter() {
		dbHelper.open();
		Cursor cSettings = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.setting_font_size));

		cSettings.moveToFirst();

		customArrayAdapterFoodList = new CustomArrayAdapterFoodList(
				this,
				R.layout.row_food,
				20,
				cSettings.getInt(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
				listDBFoodComparableWithFilter);

		cSettings.close();
		setListAdapter(customArrayAdapterFoodList);
		tvLoading.setVisibility(View.GONE);
	}

	public void updateListAdapter() {
		customArrayAdapterFoodList = new CustomArrayAdapterFoodList(this,
				R.layout.row_food, 20,
				ActivityGroupMeal.group.getFoodData().dbFontSize,
				ActivityGroupMeal.group.getFoodData().listFood);

		setListAdapter(customArrayAdapterFoodList);

		tvLoading.setVisibility(View.GONE);
	}

	private void fillObjectsWithFilter() {
		listDBFoodComparableWithFilter = new ArrayList<DBFoodComparable>();
		dbHelper.open();
		Cursor cSettings = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.setting_language));
		cSettings.moveToFirst();

		// get all the food items
		Cursor cFood = dbHelper.fetchFoodWithFilterByName(editTextSearch
				.getText().toString(), cSettings.getLong(cSettings
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		if (cFood.getCount() > 0) {
			cFood.moveToFirst();
			do {
				// new DBFoodComparable(id, platform, languageid, visible,
				// categoryid, userid, isfavorite, name)
				DBFoodComparable newFood = new DBFoodComparable(
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
						0,
						0,
						0,
						0,
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
				listDBFoodComparableWithFilter.add(newFood);
			} while (cFood.moveToNext());
		}

		cSettings.close();
		cFood.close();

		// sort the list
		sortObjectsWithFilter();
	}

	private void sortObjectsWithFilter() {
		// sort the list
		FoodComparator comparator = new FoodComparator();
		Collections.sort(listDBFoodComparableWithFilter, comparator);
	}

	public void updateButton() {
		if (ActivityGroupMeal.group.getFoodData().countSelectedFood == 0) {
			btSelections.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_selection_no));
		} else {
			btSelections.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_selection_yes));
		}
	}

	public void goToPageAddFoodToSelection(int positionOfFood) {
		Intent i = null;

		if (!listWithFilter) {
			i = new Intent(this, ShowAddFoodToSelection.class)
					.putExtra(DataParser.fromWhereWeCome,
							DataParser.weComeFromShowFoodList)
					.putExtra(
							DataParser.idFood,
							Long.parseLong(""
									+ ActivityGroupMeal.group.getFoodData().listFood
											.get(positionOfFood).getId()))
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		} else {
			i = new Intent(this, ShowAddFoodToSelection.class)
					.putExtra(DataParser.fromWhereWeCome,
							DataParser.weComeFromShowFoodList)
					.putExtra(
							DataParser.idFood,
							Long.parseLong(""
									+ listDBFoodComparableWithFilter.get(
											positionOfFood).getId()))
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		View view = ActivityGroupMeal.group.getLocalActivityManager()

		.startActivity(DataParser.activityIDShowAddFoodToSelection, i)
				.getDecorView();
		ActivityGroupMeal.group.setContentView(view);
	}

	// if we press the back button on this activity we have to show a popup to
	// exit
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showPopUpToExitApplication();
			// when we return true here we wont call the onkeydown from
			// activitygroupmeal
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
					ActivityGroupMeal.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMeal.group);
		builder.setMessage(
				context.getResources().getString(R.string.sureToExit))
				.setPositiveButton(
						context.getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(
						context.getResources().getString(R.string.no),
						dialogClickListener).show();
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.food_event_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		// if we press in the menu on update own food
		case R.id.menuManageOwnFood:
			i = new Intent(this, ShowManageOwnFood.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			View v = ActivityGroupMeal.group.getLocalActivityManager()
					.startActivity(DataParser.activityIDShowFoodList, i)
					.getDecorView();
			ActivityGroupMeal.group.setContentView(v);
			break;
		}
		return true;
	}

	public void onClickShowSelectedFood(View view) {
		goToPageSelectedFood();
	}

	public void goToPageSelectedFood() {
		Intent i = new Intent(this, ShowSelectedFood.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDShowFoodList, i)
				.getDecorView();
		ActivityGroupMeal.group.setContentView(v);
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void changeFavorite(int position) {
		DbAdapter db = new DbAdapter(this);
		db.open();

		// to hold the foodID we are changing
		long foodID = ActivityGroupMeal.group.getFoodData().listFood.get(
				position).getId();

		// when the favorite == 0
		if (ActivityGroupMeal.group.getFoodData().listFood.get(position)
				.getIsfavorite() == 0) {
			db.updateFoodIsFavorite(
					ActivityGroupMeal.group.getFoodData().listFood
							.get(position).getId(), 1);
			ActivityGroupMeal.group.getFoodData().listFood.get(position)
					.setIsfavorite(1);
		} else {
			// else we set isfavorite = 0
			db.updateFoodIsFavorite(
					ActivityGroupMeal.group.getFoodData().listFood
							.get(position).getId(), 0);
			ActivityGroupMeal.group.getFoodData().listFood.get(position)
					.setIsfavorite(0);
		}

		// sort the objects
		ActivityGroupMeal.group.getFoodData().sortObjects();

		// notify the change to the listview
		customArrayAdapterFoodList.notifyDataSetChanged();

		// go to the selection
		goToFoodID(foodID);

		// close db connection
		db.close();
	}

	// get the position of a given foodID
	private int getPositionOfFOODID(long foodID) {
		int position = -1;
		for (int i = 0; i < ActivityGroupMeal.group.getFoodData().listFood
				.size(); i++) {
			if (foodID == ActivityGroupMeal.group.getFoodData().listFood.get(i)
					.getId()) {
				position = i;
				i = ActivityGroupMeal.group.getFoodData().listFood.size();
			}
		}
		return position;
	}

	// this method will check what position the given foodID is at in the
	// listview
	// and set the selected item on that foodID
	private void goToFoodID(long foodID) {
		setSelection(getPositionOfFOODID(foodID));
	}

	// This dialog shows the hide option for a food item
	private void showDialogLongClickListItem(final int position) {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMeal.group);
		builder.setTitle(getResources().getString(R.string.options));
		builder.setItems(
				getResources().getStringArray(
						R.array.showFoodListResourceOptions),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							dbHelper.open();
							// set visible = 0 for food item in database
							dbHelper.updateFoodSetInVisible(ActivityGroupMeal.group
									.getFoodData().listFood.get(position)
									.getId());

							// delete selected fooditem from list
							ActivityGroupMeal.group.getFoodData().listFood
									.remove(position);

							// update list
							customArrayAdapterFoodList.notifyDataSetChanged();
							break;
						}
					}
				});

		builder.create().show();
	}

	public class CustomArrayAdapterFoodList extends
			ArrayAdapter<DBFoodComparable> {
		private Context ctx;
		private int fontSize;
		private List<DBFoodComparable> foodItemList;
		private String previousSearchString;
		private int[] firstIndex;
		private int[] lastIndex;

		public CustomArrayAdapterFoodList(Context context,
				int textViewResourceId, int maximuSearchStringLength,
				int fontSize, List<DBFoodComparable> foodItemList) {
			super(context, textViewResourceId, foodItemList);
			this.ctx = context;
			this.foodItemList = foodItemList;
			this.fontSize = fontSize;

			previousSearchString = null;

			firstIndex = new int[maximuSearchStringLength + 1];
			lastIndex = new int[maximuSearchStringLength + 1];

			firstIndex[0] = 0;
			lastIndex[0] = foodItemList.size() - 1;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_food, null);
			}

			TextView tt = (TextView) v.findViewById(R.id.row_food_text);
			TextView ttTwo = (TextView) v.findViewById(R.id.text2);
			ImageView iv = (ImageView) v.findViewById(R.id.imageViewFavorite);

			tt.setText(foodItemList.get(position).getName());

			tt.setTextSize(fontSize);
			ttTwo.setTextSize(fontSize);

			// first see if the food is favorite
			if (foodItemList.get(position).getIsfavorite() != 0) {
				iv.setImageDrawable(ctx.getResources().getDrawable(
						R.drawable.ic_star_yellow));
				// else see if the food is no standard
			} else if (!foodItemList.get(position).getPlatform().equals("s")) {
				iv.setImageDrawable(ctx.getResources().getDrawable(
						R.drawable.ic_star_green));
			} else {
				// else mark food as normal
				iv.setImageDrawable(ctx.getResources().getDrawable(
						R.drawable.ic_star_bw));
			}

			// when we click on a star
			iv.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					changeFavorite(position);
				}
			});

			// when we click on a textview!
			tt.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					goToPageAddFoodToSelection(position);
				}
			});

			ttTwo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					goToPageAddFoodToSelection(position);
				}
			});

			// when we long click on a textview!
			tt.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					showDialogLongClickListItem(position);
					return false;
				}
			});

			ttTwo.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					showDialogLongClickListItem(position);
					return false;
				}
			});

			if (position % 2 == 0) {
				tt.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewOne));
				ttTwo.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewOne));
			} else {
				tt.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewTwo));
				ttTwo.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewTwo));
			}

			return v;
		}

		// For searching in the list
		public int getFirstMatchingItem(CharSequence s) {
			int index = 0;
			int[] result = new int[2];

			if (previousSearchString != null) {
				while ((index < s.length())
						&& (index < previousSearchString.length())
						&& (ExcelCharacter.compareToAsInExcel(s.charAt(index),
								previousSearchString.charAt(index)) == 0)) {
					index++;
				}
			}

			if (index != s.length()) {
				while ((index < s.length())
						&& (index < (firstIndex.length - 1))) {
					result = searchFirst(firstIndex[index], lastIndex[index],
							s.charAt(index), index);
					if (result[0] > -1) {
						firstIndex[index + 1] = result[0];
						lastIndex[index + 1] = searchLast(result[0], result[1],
								s.charAt(index), index);
					} else {
						if (index < (firstIndex.length - 1)) {
							firstIndex[index + 1] = firstIndex[index];
							lastIndex[index + 1] = lastIndex[index];
						}
					}
					index++;
				}
			}

			previousSearchString = s.toString();
			return firstIndex[index];
		}

		private int[] searchFirst(int low, int high, char value, int index) {
			int temp = 0;
			int temp2 = 0;
			int mid = 0;
			int belenth;
			low++;
			high++;
			int[] returnValue = { -1, high };
			char[] be;
			temp = high + 1;
			while (low < temp) {
				mid = (low + temp) / 2;
				be = this.foodItemList.get(mid - 1).getName().toCharArray();
				belenth = be.length;
				if (!(belenth > index)) {
					low = mid + 1;
				} else {
					if (ExcelCharacter.compareToAsInExcel(be[index], value) < 0) {
						low = mid + 1;
					} else {
						if (temp2 > value) {
							returnValue[1] = mid;
						}
						temp = mid;
					}
				}
			}
			if (low > high) {
				;
			} else {
				be = this.foodItemList.get(low - 1).getName().toCharArray();
				belenth = be.length;
				if (belenth > index) {
					if ((low < (high + 1))
							&& (ExcelCharacter.compareToAsInExcel(be[index],
									value) == 0))
						returnValue[0] = low;
				} else {
					;
				}
			}
			returnValue[0] = returnValue[0] - 1;
			returnValue[1] = returnValue[1] - 1;
			return returnValue;
		}

		private int searchLast(int low, int high, char value, int index) {
			int temp = 0;
			int mid = 0;
			int returnvalue = -1;
			char[] be;
			int belength;
			low++;
			high++;
			temp = low - 1;
			while (high > temp) {
				if ((high + temp) % 2 > 0) {
					mid = (high + temp) / 2 + 1;
				} else {
					mid = (high + temp) / 2;
				}
				be = this.foodItemList.get(mid - 1).getName().toCharArray();
				belength = be.length;
				if (!(belength > index)) {
					temp = mid;
				} else {
					if (ExcelCharacter.compareToAsInExcel(be[index], value) > 0)
						high = mid - 1;
					else
						temp = mid;
				}
			}
			if (high < low) {
				;
			} else {
				be = this.foodItemList.get(high - 1).getName().toCharArray();
				belength = be.length;
				if (belength > index) {
					if (((low - 1) < high)
							& (ExcelCharacter.compareToAsInExcel(be[index],
									value) == 0))
						returnvalue = high;
				} else {
					;
				}
			}
			returnvalue = returnvalue - 1;
			return returnvalue;
		}
	}
}