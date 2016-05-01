// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show.Food;

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

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodComparable;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.DbSettings;
import com.hippoandfriends.helpdiabetes.Rest.ExcelCharacter;
import com.hippoandfriends.helpdiabetes.Rest.FoodComparator;
import com.hippoandfriends.helpdiabetes.Rest.SpecialCharactersToNormal;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

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

		// track we come here
		ActivityGroupMeal.group.parent
				.trackPageView(TrackingValues.pageShowFoodList);

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

				// store the search string in a variable to set it back when we
				// press cancel on addFoodToSelection
				ActivityGroupMeal.group.lastSearchString = editTextSearch
						.getText().toString();

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
				// track we come here
				ActivityGroupMeal.group.parent.trackEvent(
						TrackingValues.eventCategoryMeal,
						TrackingValues.eventCategoryMealSearch);

				onClickSearch();
			}
		});
	}

	// This method will show the gray or the highlighted search button
	private void checkSearchButton() {
		// if we are searching with a listfilter we show the cross
		if (listWithFilter) {
			btSearch.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_menu_close_clear_cancel));
		} else {
			btSearch.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_menu_search));
		}
	}

	private void onClickSearch() {
		if (listWithFilter) {
			listWithFilter = false;
			checkSearchButton();
			setListAdapter(null);
			updateListAdapter();

			// set right selected item
			if (customArrayAdapterFoodList != null
					&& ActivityGroupMeal.group.getFoodData().listFood.size() > 0
					&& !listWithFilter)
				setSelection(customArrayAdapterFoodList
						.getFirstMatchingItem(editTextSearch.getText()
								.toString()));

		} else if (!listWithFilter) {
			if (editTextSearch.length() > 0) {
				listWithFilter = true;

				// hide the keyboard
				ActivityGroupMeal.group.hideKeyboard();

				btSearch.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_menu_close_clear_cancel));
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
		super.onResume();

		if (!ActivityGroupMeal.group.goToSelectedFood) {
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
				// when we come here our asynctask is donne with fetching data
				// from
				// the database
				tvLoading.setVisibility(View.VISIBLE);
				// so we update the listadapter
				updateListAdapter();
			}

			// when deleteFoodIdFromList != 0 we have to delete that object from
			// the
			// list
			if (ActivityGroupMeal.group.deleteFoodIDFromList != 0) {
				tvLoading.setVisibility(View.VISIBLE);
				setListAdapter(null);

				long deleteFoodId = ActivityGroupMeal.group.deleteFoodIDFromList;
				ActivityGroupMeal.group.deleteFoodIDFromList = 0;

				// start asynctask to delete the items from the list , recreate
				// the
				// list and update the listadapter
				new AsyncDeleteItemFromList().execute(deleteFoodId);
			}

			// if we added a food item to the selectedFood list this boolean =
			// true
			if (ActivityGroupMeal.group.addedFoodItemToList) {
				ActivityGroupMeal.group.addedFoodItemToList = false;
				// do selectedFood + 1;
				ActivityGroupMeal.group.getFoodData().countSelectedFood++;
				// and let it blink
				animateButton();
			}

			// if we updated a food name we have to reorder the list
			if (ActivityGroupMeal.group.recreatelist) {
				ActivityGroupMeal.group.recreatelist = false;
				setListAdapter(null);
				tvLoading.setVisibility(View.VISIBLE);
				new AsyncRecreateTotalList().execute();
			}

			// update button with ic
			updateButton();

			btSearch.requestFocus();

			// becaus edit text search has a on text change listeren we will
			// automaticly go to the right item.
			editTextSearch.setText(ActivityGroupMeal.group.lastSearchString);

			/*
			 * //check if we are startup
			 * if(ActivityGroupMeal.group.getFoodData().startUp){
			 * Toast.makeText(this,
			 * getResources().getString(R.string.selections_aint_empty),
			 * Toast.LENGTH_LONG).show();
			 * ActivityGroupMeal.group.getFoodData().startUp = false; }
			 */

		} else {
			ActivityGroupMeal.group.goToSelectedFood = false;
			goToPageSelectedFood();
		}
	};

	private class AsyncRecreateTotalList extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// sort the 2 lists again and recreate our total list
			ActivityGroupMeal.group.getFoodData().recreateTotalList();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// update the list adapter
			updateListAdapter();
			super.onPostExecute(result);
		}

	}

	private void animateButton() {
		btSelections.setAnimation(animation);
	}

	private class AsyncDeleteItemFromList extends AsyncTask<Long, Void, Long> {

		@Override
		protected Long doInBackground(Long... params) {
			// delete item from the lists
			ActivityGroupMeal.group.getFoodData().deleteFoodFromList(params[0]);
			// recreate the main list
			ActivityGroupMeal.group.getFoodData().recreateTotalList();
			return params[0];
		}

		@Override
		protected void onPostExecute(Long result) {
			// update list adapter
			updateListAdapter();
		}
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
				ActivityGroupMeal.group.getFoodData().listAllFood.add(newFood);

				// sort the list and recreate the total list
				ActivityGroupMeal.group.getFoodData().recreateTotalList();
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
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
		ActivityGroupMeal.group.setContentView(v);
	}

	public void updateListAdapterWithFilter() {
		dbHelper.open();
		Cursor cSettings = dbHelper
				.fetchSettingByName(DbSettings.setting_font_size);

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
		// get all the right items with the search.
		listDBFoodComparableWithFilter = new ArrayList<DBFoodComparable>();
		new SpecialCharactersToNormal();

		// split with spaces
		String[] searchWorths = editTextSearch.getText().toString().split(" ");
		
		// do for every object in the normal list
		for (DBFoodComparable obj : ActivityGroupMeal.group.getFoodData().listFood) {
			String tempName = SpecialCharactersToNormal.removeAccents(obj
					.getName());
			// if tempname.indexof != -1 then we found the search string in the
			// obj
			for (String worth : searchWorths) {
				if (tempName.toLowerCase().indexOf(worth.toLowerCase()) != -1 && worth.trim().length() > 0) {
					listDBFoodComparableWithFilter.add(obj);
				}
			}
		}

		// sort the list
		sortObjectsWithFilter();
	}

	private void sortObjectsWithFilter() {
		// sort the list
		FoodComparator comparator = new FoodComparator();
		Collections.sort(listDBFoodComparableWithFilter, comparator);
	}

	public void updateButton() {
		/*
		 * if (ActivityGroupMeal.group.getFoodData().countSelectedFood == 0) {
		 * btSelections.setBackgroundDrawable(getResources().getDrawable(
		 * R.drawable.ic_selection_no)); } else {
		 * btSelections.setBackgroundDrawable(getResources().getDrawable(
		 * R.drawable.ic_selection_yes)); }
		 */
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

		.startActivity(DataParser.activityIDMeal, i).getDecorView();
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

	public void onClickShowSelectedFood(View view) {
		// go to page selected food
		goToPageSelectedFood();
	}

	public void goToPageSelectedFood() {
		Intent i = new Intent(this, ShowSelectedFood.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
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
		// set list adapter = null and show loading so we cant click on a other
		// until this is donne
		tvLoading.setVisibility(View.VISIBLE);
		setListAdapter(null);

		DbAdapter db = new DbAdapter(this);
		db.open();

		// to hold the foodID we are changing
		DBFoodComparable food = ActivityGroupMeal.group.getFoodData().listFood
				.get(position);

		// when the favorite == 0
		// we have to add the favorite to the listFavoriteFood
		// sort the 2 listviews and recreate the total listview
		if (food.getIsfavorite() == 0) {
			// update the value in database
			db.updateFoodIsFavorite(food.getId(), 1);

			// set the foodobjects favorite = 1
			food.setIsfavorite(1);
			// add food to the list of favorites
			ActivityGroupMeal.group.getFoodData().listFavoriteFood.add(food);

			// set favorite = 1 in the listviews with all the food ( otherwise
			// we dont see a gold star )
			ActivityGroupMeal.group.getFoodData().setIsFavoriteFromFoodListAll(
					food.getId(), 1);
		} else {
			// else we set isfavorite = 0 in the database
			db.updateFoodIsFavorite(food.getId(), 0);
			// we delete the favorite food from the favorite list
			ActivityGroupMeal.group.getFoodData().deleteFoodFromFavoriteList(
					food.getId());
			// and we set the favorite = 0 in the listviews ( so we dont see a
			// golden star anymore )
			ActivityGroupMeal.group.getFoodData().setIsFavoriteFromFoodListAll(
					food.getId(), 0);
		}

		// close db connection
		db.close();

		// start a asyntask to recreate the total list and update the
		// listadapter
		new AsyncRecreateTotalListAndGoToSelectedFoodId().execute(food.getId());

	}

	private class AsyncRecreateTotalListAndGoToSelectedFoodId extends
			AsyncTask<Long, Void, Long> {

		@Override
		protected Long doInBackground(Long... params) {
			// sort the 2 lists again and recreate our total list
			ActivityGroupMeal.group.getFoodData().recreateTotalList();
			return params[0];
		}

		@Override
		protected void onPostExecute(Long result) {
			// hide the loading
			tvLoading.setVisibility(View.GONE);

			// update the list adapter
			updateListAdapter();

			// go to the selection
			goToFoodID(result);

			super.onPostExecute(result);
		}
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

	// This dialog will be shown when we long click on a item in the list
	private void showDialogLongClickListItem(final int position) {
		// create a list to show options
		CharSequence[] items = new CharSequence[2];
		items[0] = getResources().getString(R.string.update);
		items[1] = getResources().getString(R.string.hide);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMeal.group);
		builder.setTitle(getResources().getString(R.string.options));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				// when we click on update
				case 0:
					Intent i = new Intent(ActivityGroupMeal.group,
							ShowUpdateFood.class).addFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(
							DataParser.idFood,
							ActivityGroupMeal.group.getFoodData().listFood.get(
									position).getId());
					View view = ActivityGroupMeal.group
							.getLocalActivityManager()
							.startActivity(DataParser.activityIDMeal, i)
							.getDecorView();
					ActivityGroupMeal.group.setContentView(view);
					break;
				// when we click on hide
				case 1:
					dbHelper.open();
					// set visible = 0 for food item in database
					dbHelper.updateFoodSetInVisible(ActivityGroupMeal.group
							.getFoodData().listFood.get(position).getId());

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
			try {
				lastIndex[0] = foodItemList.size() - 1;
			} catch (Exception e) {
				lastIndex[0] = 0;
			}
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

			// only set text and image when foodID != -1
			// it will be -1 for the "item" between favorite and all
			if (foodItemList.get(position).getId() != -1) {
				tt.setText(foodItemList.get(position).getName());
				ttTwo.setText(getResources().getString(R.string.endOfListView));

				tt.setTextSize(fontSize);
				ttTwo.setTextSize(fontSize);

				// first see if the food is favorite
				if (foodItemList.get(position).getIsfavorite() != 0) {
					iv.setImageDrawable(ctx.getResources().getDrawable(
							R.drawable.ic_star_yellow));
					// else see if the food is no standard
				} else if (!foodItemList.get(position).getPlatform()
						.equals("s")) {
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
						// track we come here
						ActivityGroupMeal.group.parent.trackEvent(
								TrackingValues.eventCategoryMeal,
								TrackingValues.eventCategoryMealHideFood);

						showDialogLongClickListItem(position);
						return false;
					}
				});

				ttTwo.setOnLongClickListener(new OnLongClickListener() {
					public boolean onLongClick(View v) {
						// track we come here
						ActivityGroupMeal.group.parent.trackEvent(
								TrackingValues.eventCategoryMeal,
								TrackingValues.eventCategoryMealHideFood);
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
			} else {
				// when we are on the item that is between favorite and all
				tt.setText("");
				ttTwo.setText("");
				iv.setImageDrawable(null);

				tt.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorTransparant));
				ttTwo.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorTransparant));
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

	// This method will refresh the listview with the right fontSize
	// This method is caled when the user presses on a listitem in setting font
	// size
	public void setNewFontSize() {
		// show the loading
		tvLoading.setVisibility(View.VISIBLE);
		setListAdapter(null);
		updateListAdapter();
	}
}