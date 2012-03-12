package be.goossens.oracle;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class CustomAdapterUnit extends SimpleCursorAdapter {

	private Context context;
	
	public CustomAdapterUnit(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
	}
		
	//Override the view to set a differend text on the spinner
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Cursor cursor = getCursor();
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_spinner_item, null);
		}
		TextView tvText = (TextView) convertView.findViewById(android.R.id.text1);
		//set the text on the spinner unitStandardamount + " " + unitName
		tvText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) + " " + cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)));
		
		return convertView;
	}
	
}
