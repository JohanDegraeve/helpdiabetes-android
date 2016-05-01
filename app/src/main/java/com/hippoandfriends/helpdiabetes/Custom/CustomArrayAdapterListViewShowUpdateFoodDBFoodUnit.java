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
import com.hippoandfriends.helpdiabetes.Objects.DBFoodUnit;

public class CustomArrayAdapterListViewShowUpdateFoodDBFoodUnit extends ArrayAdapter<DBFoodUnit> {
	
	private Context context;
	private int fontSize;
	private List<DBFoodUnit> objects;
	private int layout;
	
	public CustomArrayAdapterListViewShowUpdateFoodDBFoodUnit(Context context, int textViewResourceId,
			List<DBFoodUnit> objects, int fontSize) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.fontSize = fontSize;
		this.objects = objects;
		this.layout = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layout, null);
		} 
		TextView tt = (TextView) v.findViewById(R.id.text1);
		TextView tt2 = (TextView)v.findViewById(R.id.text2);
		
		tt.setText(objects.get(position).getName());
		tt.setTextSize(fontSize);
		
		if (position % 2 == 0) {
			tt.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewOne));
			tt2.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewOne));
		} else {
			tt.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewTwo));
			tt2.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewTwo));
		}
		
		return v;
	}

}
