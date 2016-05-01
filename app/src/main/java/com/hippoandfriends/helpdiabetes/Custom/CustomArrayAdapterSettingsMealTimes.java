// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;

public class CustomArrayAdapterSettingsMealTimes extends ArrayAdapter<String> {
	private Context ctx;
	private List<String> items;

	public CustomArrayAdapterSettingsMealTimes(Context context,
			int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		this.ctx = context;
		this.items = objects;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_custom_array_adapter_setting_meal_times, null);
		}
		
		TextView tt = (TextView) v.findViewById(R.id.text1);
		TextView tt2 = (TextView)v.findViewById(R.id.text2); 
		
		tt.setText(items.get(position).toString());
		
		if (position % 2 == 0) {
			tt.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewOne));
			tt2.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewOne));
		} else {
			tt.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewTwo));
			tt2.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewTwo));
		}

		return v;
	}

}
