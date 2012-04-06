package be.goossens.oracle.Custom;

import java.util.List;

import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBExerciseEvent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapterDBExerciseEvent extends ArrayAdapter<DBExerciseEvent> {

	private Context context;
	private int fontSize;
	private List<DBExerciseEvent> objects;
	
	public CustomArrayAdapterDBExerciseEvent(Context context,
			int textViewResourceId, List<DBExerciseEvent> objects, int fontSize) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.fontSize = fontSize;
		this.objects = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_custom_array_adapter, null);
		}
		TextView tt = (TextView) v.findViewById(R.id.text1);
		String description = objects.get(position).getDescription();
		if(description.length()>10){
			description = description.substring(0,9) + "...";
		}
		tt.setText(description);
		tt.setTextSize(fontSize);
		return v;
	}

}
