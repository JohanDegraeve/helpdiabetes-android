// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.ActivityGroup;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Show.ShowHomeTab;
import com.hippoandfriends.helpdiabetes.Show.Food.ShowFoodList;
import com.hippoandfriends.helpdiabetes.Show.Food.ShowLoadingFoodData;
import com.hippoandfriends.helpdiabetes.Show.Food.ShowSelectedFood;
import com.hippoandfriends.helpdiabetes.Show.Food.ShowUpdateFood;

public class ActivityGroupMeal extends ActivityGroup {

	public ShowHomeTab parent;
	
	// keep this in a static variable to make it accessible
	// activities, let them manipulate the view
	public static ActivityGroupMeal group;

	// Need to keep track of the history so the back button works properly
	public ArrayList<View> history;

	// if newFoodID != 0 the showFoodList will add the id to the list, order the
	// objects, go to the ID and set it back to 0
	// This long != 0 when showCreateFood created a new foodItem
	public long newFoodID;

	// this boolean is used to check if we added a fooditem to the selectedFood
	// the addFoodToSelection page will set this boolean on true, the
	// showFoodList page will animate the button and set it back on false
	public boolean addedFoodItemToList;

	// if this long != 0 we have to delete the fooditem with that id from the
	// list ( becaus it is not in the datase anymore )
	// this id gets set by showupdateownfood and gets set back to 0 in
	// showFoodList
	public long deleteFoodIDFromList;

	// If this boolean is true we recreate the list before we updatelistadapter
	// in showfoodlist
	public boolean recreatelist;

	// when we pressed on add tracking meal event to selected food we flag this
	// boolean true
	// showfoodlist wll start the activity
	public boolean goToSelectedFood;

	// when we click on a item in the list we store the search string in here
	// when we come back to show food list and this string != "" we store this
	// string in the search box
	// and go to the right item in the list
	public String lastSearchString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.history = new ArrayList<View>();
		group = this;

		// Initialize stuff
		lastSearchString = "";
		initializeTracker();
		
		// make a root activity when the history size = 0
		if (history.size() == 0) {
			restartThisActivity();
		}
	}
	
	private void initializeTracker() {
		parent = (ShowHomeTab) this.getParent();
	}

	public void hideKeyboard() {
		try {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(getParent().getCurrentFocus()
					.getWindowToken(), 0);
		} catch (NullPointerException e) {
		}
	}

	// This will hide the keyboard on tab change
	@Override
	protected void onPause() {
		hideKeyboard();
		super.onPause();
	}

	public void restartThisActivity() {
		// clear history
		history = null;

		// inialize list
		history = new ArrayList<View>();

		// Start the root activity within the group and get its view
		// This activity is the activity that hold our objects for showFoodList
		View view = getLocalActivityManager().startActivity(
				DataParser.activityIDMeal,
				new Intent(this, ShowLoadingFoodData.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		replaceView(view);
	}

	// this method is called from showfoodlist when we click on the back button
	// this method will kill the application
	public void killApplication() {
		// finish the tab activity so everything will close
		this.getParent().finish();
	}

	// The foodData will always be in history place 0
	public ShowLoadingFoodData getFoodData() {
		return ShowLoadingFoodData.INSTANCE;
	}

	// The showFoodList will always be in history place 1
	// It is not sure that this activity is running! becaus when we go back to
	// the activity we destroy it and create a new one
	// This means that history only will have 1 object in its list and that is
	// on position 0 so position 1 is empty
	public ShowFoodList getShowFoodList() {
		return ShowFoodList.INSTANCE;
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
	}

	@Override
	public void setContentView(View view) {
		hideKeyboard();
		replaceView(view);
	}

	private void replaceView(View view) {
		// Adds the old one to history
		history.add(view);

		// changes this group view to the new view
		super.setContentView(view);

		// Animation hyperSpaceJump = AnimationUtils.loadAnimation(this,
		// R.anim.slide_in_left);
		// view.startAnimation(hyperSpaceJump);
	}

	public void back() {
		hideKeyboard();
		// if the size > 2 then we are in a activity that is not the home
		// activity ( show food list )

		if (history.size() > 2) {
			// remove the view from the history list
			history.remove(history.size() - 1);

			// if history.size == 2 we are in our home activity ( show food list
			// ) and we have to recreate it
			// otherwise we cant handle the keyboard events.
			if (history.size() == 2) {
				// remove the view from the list
				history.remove(history.size() - 1);
				// add it again
				View view = getLocalActivityManager().startActivity(
						DataParser.activityIDMeal,
						new Intent(this, ShowFoodList.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();

				history.add(view);
			} else if (history.size() == 3) {
				try {
					getShowSelectedFood().refreshData();
				} catch (Exception e) {

				}
			}

			View view = history.get(history.size() - 1);

			// dont put animation on the home screen ( show food list )
			// if (history.size() != 2) {
			// Animation slideOut = AnimationUtils.loadAnimation(this,
			// R.anim.slide_out_right);
			// view.startAnimation(slideOut);
			// }

			// call the super.setContent view! so set the real view
			super.setContentView(view);

		}
	}

	// This is called when we updated the foodname and pressed the back key from
	// showupdatefood
	// This will restart the showfoodlist activity to show the new ordered
	// foodlist
	public void restartShowFoodList() {
		history.remove(history.size() - 1);
		// add it again
		View view = getLocalActivityManager().startActivity(
				DataParser.activityIDMeal,
				new Intent(this, ShowFoodList.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		replaceView(view);

		// call the super.setContent view! so set the real view
		super.setContentView(history.get(history.size() - 1));
	}

	// when we didnt overide the onkeydown method in the activity this onkeydown
	// will be triggered
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// destroy current activity and go back to the last one in the
			// history
			back();
			// return true so other onkeydowns wont get called
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	// show update food
	public ShowUpdateFood getShowUpdateFood() {
		return ShowUpdateFood.INSTANCE;
	}

	public ShowSelectedFood getShowSelectedFood() {
		return ShowSelectedFood.INSTANCE;
	}

	public void goToSeletedFood() {
		// clear the history until we are back at our showFoodList
		if (history.size() > 2) {
			while (history.size() > 2) {
				back();
			}
		}
		// flag a boolean here to start show selected food when we go to show
		// food list
		goToSelectedFood = true;
	}
}
