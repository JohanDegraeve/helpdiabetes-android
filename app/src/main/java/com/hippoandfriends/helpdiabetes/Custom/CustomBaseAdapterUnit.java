// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

/*
 * This class is used to show a popup if we click on a spinner with the unit standardamount + the unit name
 */

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.Objects.DBFoodUnit;

public class CustomBaseAdapterUnit extends BaseAdapter {
	private Context context;
	private List<DBFoodUnit> list;
	private int fontSize;
	
	public CustomBaseAdapterUnit(Context context, List<DBFoodUnit> list, int fontSize) {
		this.context = context;
		this.list = list;
		this.fontSize = fontSize;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	public long getItemId(int arg0) {
		return Long.parseLong("" + list.get(arg0).getId());
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		DBFoodUnit entry = list.get(arg0);
		if(arg1 == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.row_listview_unit, null);
		}
		TextView tvUnit = (TextView) arg1.findViewById(R.id.textViewRowListviewUnit);
		tvUnit.setText("" + entry.getStandardamound() + " " + entry.getName());
		tvUnit.setTextSize(fontSize); 
		return arg1;
	}

}
