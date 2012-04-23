package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.goossens.oracle.R;

public class CustomArrayAdapterCharSequenceShowCreateFood extends
		ArrayAdapter<CharSequence> {

	private Context ctx;
	private List<CharSequence> items;

	public CustomArrayAdapterCharSequenceShowCreateFood(Context context,
			int textViewResourceId, List<CharSequence> objects) {
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
			v = vi.inflate(
					R.layout.custom_spinner_array_adapter_charsequence_show_create_food,
					null);
		}
		TextView tt = (TextView) v.findViewById(R.id.text1);
		tt.setText(items.get(position).toString());
		return v;
	}
}
