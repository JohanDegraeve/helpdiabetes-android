// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Show;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupExercise;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupGlucose;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMedicine;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupSettings;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.TrackingValues;

public class ShowHomeTab extends TabActivity {
	private TabHost tabHost;
	public static TabActivity context;
	public GoogleAnalyticsTracker tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_home_tab);

		context = this;

		//block orientation change so our activity wont restart when we change orientation!!
		//This will give crashes...
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(TrackingValues.trackingCode, this);
		
		//to upload all data to the cloud
		tracker.setSampleRate(100);
		
		tabHost = getTabHost();

		setupTabHost();
	}

	public void trackPageView(String value){
		tracker.trackPageView(value);
		tracker.dispatch();
	}
	
	public void trackEvent(String categorie, String action){
		tracker.trackEvent(categorie, action, null, 0);
		tracker.dispatch();
	}
	
	public void trackEventWithValue(String categorie, String action, String value){
		tracker.trackEvent(categorie, action, value, 0);
		tracker.dispatch();
	}
	
	@Override
	public void finish() {
		tracker.stopSession();
		super.finish();
	}
	
	// when we press the home button we set startup back to true!
	@Override
	protected void onUserLeaveHint() {
		//mark food data startUp true
		ActivityGroupMeal.group.getFoodData().startUp = true;
		super.onUserLeaveHint();
	}

	private void setupTabHost() {
		Intent in = new Intent(this, ActivityGroupTracking.class);
		setupTab(new TextView(this), DataParser.activityIDTracking,
				R.drawable.ic_tab_tracking, in);
		
		in = new Intent(this, ActivityGroupMeal.class);
		setupTab(new TextView(this), DataParser.activityIDMeal,
				R.drawable.ic_tab_meal, in);
		
		in = new Intent(this, ActivityGroupExercise.class);
		setupTab(new TextView(this), DataParser.activityIDExercise,
				R.drawable.ic_tab_exercise, in);
		
		in = new Intent(this, ActivityGroupGlucose.class);
		setupTab(new TextView(this), DataParser.activityIDGlucose,
				R.drawable.ic_tab_glucose, in);
		
		in = new Intent(this, ActivityGroupMedicine.class);
		setupTab(new TextView(this), DataParser.activityIDMedicine,
				R.drawable.ic_tab_medicine, in);
		
		in = new Intent(this, ActivityGroupSettings.class);
		setupTab(new TextView(this), DataParser.activityIDSettings,
				R.drawable.ic_tab_settings, in);
		
		// set default tab on show food list
		goToTab(DataParser.activityIDMeal);
	}

	private void setupTab(final View view, final String tag, final int image,
			Intent in) {

		View tabview = createTabView(tabHost.getContext(), image);

		TabSpec spec = tabHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(new TabContentFactory() {
					public View createTabContent(String tag) {
						return view;
					}
				}).setContent(in);
		tabHost.addTab(spec);
	}

	public void goToTab(String tag) {
		tabHost.setCurrentTabByTag(tag);
	}

	private static View createTabView(final Context context, final int image) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		ImageView iv = (ImageView) view.findViewById(R.id.imageViewTabs);
		iv.setImageResource(image);
		return view;
	}

}