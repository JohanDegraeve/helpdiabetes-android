// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import com.hippoandfriends.helpdiabetes.Rest.Functions;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomSimpleCursorAdapterAddFoodToSelectionSpinner extends
		SimpleCursorAdapter {

	private Context context;
	private String[] from;
	private int[] to;
	private int layout;

	public CustomSimpleCursorAdapterAddFoodToSelectionSpinner(Context context,
			int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.from = from;
		this.to = to;
		this.layout = layout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Cursor cursor = getCursor();
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layout, null);
		}
		try {
			TextView tt = (TextView) v.findViewById(to[0]);

			// set text
			tt.setText(new Functions().getShorterString(
					cursor.getString(cursor.getColumnIndexOrThrow(from[0])), 6));

			// set layout params to fill parent
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			tt.setLayoutParams(params);
			// and set gravity to center
			tt.setGravity(Gravity.CENTER);

			cursor.moveToNext();
		} catch (Exception e) {
		}
		return v;
	}
}
