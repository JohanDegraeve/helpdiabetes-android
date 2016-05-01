// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapterCharSequenceForASpinner extends
		ArrayAdapter<CharSequence> {

	private Context ctx;
	private CharSequence[] items;
	private int layout;

	public CustomArrayAdapterCharSequenceForASpinner(Context context,
			int textViewResourceId, CharSequence[] objects) {
		super(context, textViewResourceId, objects);
		this.ctx = context;
		this.layout = textViewResourceId;
		items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) { 
			LayoutInflater vi = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layout, null);

			TextView tt = (TextView) v.findViewById(android.R.id.text1);

			if (tt != null) {
				// set layout params to fill parent
				LayoutParams params = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				tt.setLayoutParams(params);
				// and set gravity to center
				tt.setGravity(Gravity.CENTER);

				tt.setText(items[position].toString());
			}
		}
		return v;
	}

}
