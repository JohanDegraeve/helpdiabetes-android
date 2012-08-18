// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.ActivityGroup;

import java.util.ArrayList;

import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Show.ShowHomeTab;
import com.hippoandfriends.helpdiabetes.Show.Medicine.ShowAddMedicineEvent;

import com.hippoandfriends.helpdiabetes.R;

import android.app.ActivityGroup;
import com.hippoandfriends.helpdiabetes.R;

import android.content.Context;
import com.hippoandfriends.helpdiabetes.R;

import android.content.Intent;
import com.hippoandfriends.helpdiabetes.R;

import android.os.Bundle;
import com.hippoandfriends.helpdiabetes.R;

import android.view.KeyEvent;
import com.hippoandfriends.helpdiabetes.R;

import android.view.View;
import com.hippoandfriends.helpdiabetes.R;

import android.view.inputmethod.InputMethodManager;

public class ActivityGroupMedicine extends ActivityGroup {
	public ShowHomeTab parent;
	// keep this in a static variable to make it accessible for all the nesten
	// activities, let them manipulate the view
	public static ActivityGroupMedicine group;

	// Need to keep track of the history so the back button works properly
	private ArrayList<View> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.history = new ArrayList<View>();
		group = this;
		initializeTracker();
		// make a root activity when the history size = 0
		if (history.size() == 0) {
			// Start the root activity within the group and get its view
			View view = getLocalActivityManager().startActivity(
					DataParser.activityIDMedicine,
					new Intent(this, ShowAddMedicineEvent.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					.getDecorView();
			replaceView(view);
		}
	}
	private void initializeTracker() {
		parent = (ShowHomeTab) this.getParent();
	}
	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(getParent().getCurrentFocus().getWindowToken(), 0);
	}
	
	//This will hide the keyboard on tab change
	@Override
	protected void onPause() {
		hideKeyboard();
		super.onPause();
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
	}

	public void back() {
		hideKeyboard();
		try {
			// if we set history.size() > 0 and we press back key on home
			// activity
			// and then on another activity we wont get back!
			if (history.size() > 1) {
				history.remove(history.size() - 1);
				// call the super.setContent view! so set the real view
				super.setContentView(history.get(history.size() - 1));
			} else {

			}
		} catch (Exception e) {
			if (history.size() >= 0)
				super.setContentView(history.get(0));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return true;
	}

	@Override
	public void finish() {

	}

	// this method will kill the application
	public void killApplication() {
		// finish the tab activity so everything will close
		this.getParent().finish();
	}
}
