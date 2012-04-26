package be.goossens.oracle.ActivityGroup;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Show.Settings.ShowSettings;
import be.goossens.oracle.Show.Settings.ShowSettingsMealTimes;
 
public class ActivityGroupSettings extends ActivityGroup {
	// keep this in a static variable to make it accessible
	// activities, let them manipulate the view
	public static ActivityGroupSettings group;

	// Need to keep track of the history so the back button works properly
	private ArrayList<View> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.history = new ArrayList<View>();
		group = this;
		
		// make a root activity when the history size = 0
		if (history.size() == 0) {
			// Start the root activity within the group and get its view
			View view = getLocalActivityManager().startActivity(DataParser.activityIDSettings,
					new Intent(this, 
							ShowSettings.class)	.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					.getDecorView();
			
			replaceView(view);
		} 
		
	}
	
	// let the keyboard dissapear
	private void keyboardDissapear() {
		try {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
		}
	}

	@Override
	public void onContentChanged() {
		keyboardDissapear();
		super.onContentChanged();
	}

	@Override
	public void setContentView(View view) {
		replaceView(view);
	}

	private void replaceView(View view) {
		// Adds the old one to history
		history.add(view);
		// changes this group view to the new view
		super.setContentView(view);
	}

	public void back() {
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

	// Make for every class a refresh method
	// Show Mealtimes
	public void refreshShowSettingsMealTimes() {
		try {
			// the first history is the setting page with the list of settings
			// the second one is the show settings meal times page
			View v = history.get(1);
			ShowSettingsMealTimes currentActivity = (ShowSettingsMealTimes) v
					.getContext();
			currentActivity.onResume();
		} catch (Exception e) {
		}
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
