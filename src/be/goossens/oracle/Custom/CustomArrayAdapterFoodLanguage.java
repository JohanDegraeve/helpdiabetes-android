package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBFoodLanguage;

public class CustomArrayAdapterFoodLanguage extends
		ArrayAdapter<DBFoodLanguage> {

	private Context ctx;
	private List<DBFoodLanguage> items;

	public CustomArrayAdapterFoodLanguage(Context context,
			int textViewResourceId, List<DBFoodLanguage> objects) {
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
			v = vi.inflate(R.layout.row_custom_array_adapter_food_language,
					null);
		}

		TextView tt = (TextView) v.findViewById(R.id.text1);
		TextView ttTwo = (TextView) v.findViewById(R.id.text2);

		tt.setText(items.get(position).getLanguage());

		if (position % 2 == 0) {
			tt.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewOne));
			ttTwo.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewOne));
		} else {
			tt.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewTwo));
			ttTwo.setBackgroundColor(ctx.getResources().getColor(
					R.color.ColorListViewTwo));
		}

		tt.setTextColor(ctx.getResources().getColor(
				R.color.ColorListViewTextViewTextColor));
		ttTwo.setTextColor(ctx.getResources().getColor(
				R.color.ColorListViewTextViewTextColor));
		return v;
	}

}
