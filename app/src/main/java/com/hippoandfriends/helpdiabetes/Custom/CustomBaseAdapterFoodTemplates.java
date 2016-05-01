// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.Objects.DBFood;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodTemplate;

public class CustomBaseAdapterFoodTemplates extends BaseAdapter {
	private Context context;
	private List<DBFoodTemplate> listFoodTemplates;
	private int fontSize;

	public CustomBaseAdapterFoodTemplates(Context context,
			List<DBFoodTemplate> listFoodTemplates, int fontSize) {
		this.context = context;
		this.listFoodTemplates = listFoodTemplates;
		this.fontSize = fontSize;
	}

	public int getCount() {
		return listFoodTemplates.size();
	}

	public Object getItem(int position) {
		return listFoodTemplates.get(position);
	}

	public long getItemId(int position) {
		return listFoodTemplates.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		DBFoodTemplate entry = listFoodTemplates.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.row_custom_base_adapter_food_templates, null);
		}

		TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
		TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
		TextView tv3 = (TextView) convertView.findViewById(R.id.textView3);

		// Get the food from foodTemplate
		String tv2String = "";

		String tv3String = "";

		for (DBFood obj : entry.getFoods()) {
			tv2String += obj.getName() + " \n";
			tv3String += obj.getAmount() + " \n";
		}

		tv1.setText(entry.getFoodTemplateName());
		tv1.setBackgroundColor(context.getResources().getColor(
				R.color.ColorBackgroundTrackingDate));
		tv1.setTextColor(context.getResources().getColor(
				R.color.ColorTextTrackingDate));
		tv1.setGravity(Gravity.CENTER);

		tv2.setText(tv2String);
		tv3.setText(tv3String);

		tv1.setTextSize(fontSize);
		tv2.setTextSize(fontSize);
		tv3.setTextSize(fontSize);

		// add background 
		LinearLayout ll = (LinearLayout) convertView
				.findViewById(R.id.LinearLayoutForColor);
		if (position % 2 == 0) {
			ll.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewOne));
		} else {
			ll.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewTwo));
		}

		return convertView;
	}

}
