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
 
public class CustomArrayAdapter extends ArrayAdapter<String> {
	private Context ctx;
	private List<String> items;
	private float fontSize;
	
	public CustomArrayAdapter(Context context, int textViewResourceId,
			List<String> objects, float fontSize) {
		super(context, textViewResourceId, objects);
		this.ctx = context;
		this.items = objects;
		this.fontSize = fontSize;
	}
	
	public void setFontSize(float size){
		this.fontSize = size;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_custom_array_adapter, null);
		}
		TextView tt = (TextView) v.findViewById(R.id.text1);
		tt.setText(items.get(position).toString());
		tt.setTextSize(fontSize);
		return v;
	}

}
