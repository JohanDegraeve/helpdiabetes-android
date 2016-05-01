// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.Objects.DBTotalCalculated;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ArrayList<ArrayList<DBTotalCalculated>>> children;
	private String calculatedInsuline = "";
	private String defaultCalculated = "";
	private String insulineRatio = "";
	private String defaultCalculatedText = "";
	private int fontSize;
	private int defaultValue;
	private int displayMetrics;

	public void clear() {
		calculatedInsuline = "";
		defaultCalculated = "";
		insulineRatio = "";
		defaultCalculatedText = "";

		defaultValue = 1;
		children = new ArrayList<ArrayList<ArrayList<DBTotalCalculated>>>();
		children.add(new ArrayList<ArrayList<DBTotalCalculated>>());
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getInsulineRatio() {
		return insulineRatio;
	}

	public void setInsulineRatio(String insulineRatio) {
		this.insulineRatio = insulineRatio;
	}

	public String getDefaultCalculatedText() {
		return defaultCalculatedText;
	}

	public void setDefaultCalculatedText(String defaultCalculatedText) {
		this.defaultCalculatedText = defaultCalculatedText;
	}

	public CustomExpandableListAdapter(Context context, int fontSize,
									   int displayMetrics) {
		this.context = context;
		this.fontSize = fontSize;
		this.displayMetrics = displayMetrics;
	}

	public String getCalculatedInsuline() {
		return calculatedInsuline;
	}

	public void setCalculatedInsuline(String calculatedInsuline) {
		this.calculatedInsuline = calculatedInsuline;
	}

	public String getDefaultCalculated() {
		return defaultCalculated;
	}

	public void setDefaultCalculated(String defaultCalculated) {
		this.defaultCalculated = defaultCalculated;
	}

	/*
	 * This method allows you to add a string to the list
	 */
	public void addItem(ArrayList<DBTotalCalculated> item) {
		if (children == null) {
			children = new ArrayList<ArrayList<ArrayList<DBTotalCalculated>>>();
			children.add(new ArrayList<ArrayList<DBTotalCalculated>>());
		}
		children.get(0).add(item);
	}

	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// return a child view. with a custom layout
	public View getChildView(int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {

		@SuppressWarnings("unchecked")
		ArrayList<DBTotalCalculated> items = (ArrayList<DBTotalCalculated>) getChild(
				groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infl = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infl
					.inflate(
							R.layout.row_custom_expandable_listview_adapter_child_layout,
							null);
		}

		TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
		TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
		TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
		TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);
		TextView tv5 = (TextView) convertView.findViewById(R.id.tv5);
		TextView tv6 = (TextView) convertView.findViewById(R.id.tv6);

		LinearLayout ll1 = (LinearLayout) convertView
				.findViewById(R.id.linearLayout1);
		LinearLayout ll2 = (LinearLayout) convertView
				.findViewById(R.id.linearLayout2);
		LinearLayout ll3 = (LinearLayout) convertView
				.findViewById(R.id.linearLayout3);

		// set right height
		LayoutParams params = ll1.getLayoutParams();

		switch (displayMetrics) {
			case DisplayMetrics.DENSITY_HIGH:
				params.height = (5 * fontSize);
				break;
			default:
				params.height = (4 * fontSize);
				break;
		}

		ll1.setLayoutParams(params);
		ll2.setLayoutParams(params);
		ll3.setLayoutParams(params);

		// set right font sizes
		tv1.setTextSize(fontSize);
		tv3.setTextSize(fontSize);
		tv5.setTextSize(fontSize);

		tv2.setTextSize(fontSize - 7);
		tv4.setTextSize(fontSize - 7);
		tv6.setTextSize(fontSize - 7);

		for (int i = 0; i < items.size(); i++) {
			// 0 = first item
			// first item uses ll1, tv1 and tv2
			if (i == 0) {
				tv1.setText("" + items.get(i).getCalculatedValue());
				tv2.setText(items.get(i).getValueText());

				// set the right color
				tv1.setBackgroundColor(getRightColor(items.get(i)
						.getValueNumber()));
			}

			// 1 = second item
			// second item uses ll2, tv3 and tv4
			if (i == 1) {
				tv3.setText("" + items.get(i).getCalculatedValue());
				tv4.setText(items.get(i).getValueText());

				// set the right color
				tv3.setBackgroundColor(getRightColor(items.get(i)
						.getValueNumber()));
			}

			// 3 = third item
			// third item uses ll3, tv5 and tv6
			if (i == 2) {
				tv5.setText("" + items.get(i).getCalculatedValue());
				tv6.setText(items.get(i).getValueText());

				// set the right color
				tv5.setBackgroundColor(getRightColor(items.get(i)
						.getValueNumber()));
			}
		}

		// set the right visibiliy on the listview so we dont see just black
		// boxes
		if (items.size() == 0) {
			// hide everything
			ll1.setVisibility(View.GONE);
			ll2.setVisibility(View.GONE);
			ll3.setVisibility(View.GONE);
		} else if (items.size() == 1) {
			ll1.setVisibility(View.VISIBLE);
			ll2.setVisibility(View.GONE);
			ll3.setVisibility(View.GONE);
		} else if (items.size() == 2) {
			ll1.setVisibility(View.VISIBLE);
			ll2.setVisibility(View.VISIBLE);
			ll3.setVisibility(View.GONE);
		}

		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	public Object getGroup(int groupPosition) {
		return "";
	}

	public int getGroupCount() {
		return 1;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	private int getRightColor(int valueNumber) {
		switch (valueNumber) {
			case 1:
				return (context.getResources()
						.getColor(R.color.ColorBackgroundTrackingDate));
			case 2:
				return (context.getResources().getColor(R.color.colorSport));
			case 3:
				return (context.getResources().getColor(R.color.colorGlucose));
			case 4:
				return (context.getResources().getColor(R.color.colorInsuline));
			default:
				return context.getResources().getColor(
						R.color.ColorBackgroundTrackingDate);
		}
	}

	// return a group view with a custom layout
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater infl = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infl
					.inflate(
							R.layout.row_custom_expandable_listview_adapter_group_layout,
							null);
		}

		TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
		TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
		TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
		TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);

		// used to set the right hight for the fontsize so the text wont go off
		// screen
		TextView tvHeigth = (TextView) convertView
				.findViewById(R.id.textViewHeight);

		LinearLayout ll = (LinearLayout) convertView
				.findViewById(R.id.LinearLayoutInsuline);

		tv1.setTextSize(fontSize);
		tv2.setTextSize(fontSize - 7);

		tv3.setTextSize(fontSize);
		tv4.setTextSize(fontSize - 7);

		LayoutParams params = tvHeigth.getLayoutParams();
		switch (displayMetrics) {
			case DisplayMetrics.DENSITY_HIGH:
				params.height = (5 * fontSize);
				break;
			default:
				params.height = (4 * fontSize);
				break;
		}
		tvHeigth.setLayoutParams(params);

		if (calculatedInsuline.equals("")) {
			ll.setVisibility(View.GONE);
		} else {
			ll.setVisibility(View.VISIBLE);
			tv1.setText(calculatedInsuline);
			tv2.setText(insulineRatio);
		}

		tv3.setText(defaultCalculated);
		tv4.setText(defaultCalculatedText);

		tv3.setBackgroundColor(getRightColor(defaultValue));

		return convertView;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}