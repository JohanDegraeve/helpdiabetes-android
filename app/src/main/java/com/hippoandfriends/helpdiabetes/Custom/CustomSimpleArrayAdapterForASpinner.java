// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import java.util.List;

import com.hippoandfriends.helpdiabetes.Objects.DBNameAndID;
import com.hippoandfriends.helpdiabetes.Rest.Functions;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSimpleArrayAdapterForASpinner extends
		ArrayAdapter<DBNameAndID> {

	private Context context;
	private int layout;
	private List<DBNameAndID> objects;
	private int maxLengthString;

	public CustomSimpleArrayAdapterForASpinner(Context context,
			int textViewResourceId, List<DBNameAndID> objects,
			int maxLengthString) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layout = textViewResourceId;
		this.objects = objects;
		this.maxLengthString = maxLengthString;
	}

	@Override
	public long getItemId(int position) {
		return objects.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layout, null);

			TextView tt = (TextView) v.findViewById(android.R.id.text1);

			if (tt != null) {
				tt.setText(new Functions().getShorterString(
						objects.get(position).getName(), maxLengthString));

				// set layout params to fill parent
				LayoutParams params = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				tt.setLayoutParams(params);
				// and set gravity to center
				tt.setGravity(Gravity.CENTER);
			}
		}
		return v;
	}

}
