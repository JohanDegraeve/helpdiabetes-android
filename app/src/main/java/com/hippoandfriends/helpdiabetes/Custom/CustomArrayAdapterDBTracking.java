// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.Objects.DBMealFood;
import com.hippoandfriends.helpdiabetes.Objects.DBTracking;
import com.hippoandfriends.helpdiabetes.Rest.Functions;

public class CustomArrayAdapterDBTracking extends ArrayAdapter<DBTracking> {
	private Context context;
	private int fontSize;
	private List<DBTracking> objects;
	private int defaultValue;

	public CustomArrayAdapterDBTracking(Context context,
			int textViewResourceId, List<DBTracking> objects, int fontSize,
			int defaultValue) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.fontSize = fontSize;
		this.objects = objects;
		this.defaultValue = defaultValue;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		v = vi.inflate(R.layout.row_custom_array_adapter_tracking_date, null);

		// if norecords != "" then
		if (!objects.get(position).getNoRecords().equals("")) {
			v = vi.inflate(R.layout.row_custom_array_adapter_tracking_date,
					null);
			TextView tv = (TextView) v.findViewById(R.id.textViewTracking1);
			tv.setTextSize(fontSize);
			tv.setTextColor(context.getResources().getColor(R.color.ColorText));
			tv.setText(objects.get(position).getNoRecords());
		} else {
			// if the timestamp != 0 and boolean showtimestamp == true then we
			// have to display the date in the
			// record
			if (objects.get(position).getTimestamp() != null
					&& objects.get(position).getShowTimeStamp()) {
				v = vi.inflate(R.layout.row_custom_array_adapter_tracking_date,
						null);
				TextView tv = (TextView) v.findViewById(R.id.textViewTracking1);
				tv.setTextSize(fontSize);
				tv.setBackgroundColor(context.getResources().getColor(
						R.color.ColorBackgroundTrackingDate));
				tv.setTextColor(context.getResources().getColor(
						R.color.ColorTextTrackingDate));
				tv.setGravity(Gravity.CENTER);
				tv.setText(android.text.format.DateFormat
						.getDateFormat(context).format(
								objects.get(position).getTimestamp()));
			} else {
				// if the exercise event != null we have to display the exercise
				// event
				if (objects.get(position).getExerciseEvent() != null) {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_exercise,
							null);

					ImageView iv = (ImageView) v
							.findViewById(R.id.imageViewTracking);
					TextView tv1 = (TextView) v
							.findViewById(R.id.textViewTracking1);
					TextView tv2 = (TextView) v
							.findViewById(R.id.textViewTracking2);

					TextView tv4 = (TextView) v
							.findViewById(R.id.textViewTracking4);

					iv.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_tab_exercise_unselected));

					// show timestamp - endtime
					tv1.setText(new Functions().getTimeFromString(objects
							.get(position).getExerciseEvent().getTimeStamp())
							+ " - "
							+ new Functions().getTimeFromDuration(
									objects.get(position).getExerciseEvent()
											.getTimeStamp(),
									objects.get(position).getExerciseEvent()
											.getEndTime()));

					tv2.setText(objects.get(position).getExerciseEvent()
							.getDescription());

					tv4.setText(objects.get(position).getExerciseEvent()
							.getType());

					tv4.setTextColor(context.getResources().getColor(
							R.color.colorSport));

					tv1.setTextSize(fontSize);
					tv2.setTextSize(fontSize - 3);
					tv4.setTextSize(fontSize);

					tv4.setGravity(Gravity.RIGHT);

					// add background
					LinearLayout ll = (LinearLayout) v
							.findViewById(R.id.LinearLayoutForColor);
					if (position % 2 == 0) {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewOne));
					} else {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewTwo));
					}

				} else if (objects.get(position).getMealEvent() != null) {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_meal,
							null);

					ImageView iv = (ImageView) v
							.findViewById(R.id.imageViewTracking);
					TextView tv1 = (TextView) v
							.findViewById(R.id.textViewTracking1);
					TextView tv2 = (TextView) v
							.findViewById(R.id.textViewTracking2);

					TextView tv4 = (TextView) v
							.findViewById(R.id.textViewTracking4);

					iv.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_tab_meal_unselected));

					// show timestamp
					tv1.setText(new Functions().getTimeFromString(objects
							.get(position).getMealEvent().getEventDateTime()));

					float totalCalc = 0f;
					String text = "";

					// for each mealfood get calculated value
					for (DBMealFood mealfood : objects.get(position)
							.getMealEvent().getMealFood()) {
						// do (getUnit().getValue / getUnit().getStandardAmound)
						// * mealFood.getAmount()

						// default value = 1 = carb , 2 = prot , 3 = fat, 4 =
						// kcal
						switch (defaultValue) {
						case 1:
							totalCalc += ((mealfood.getUnit().getCarbs() / mealfood
									.getUnit().getStandardamound()) * mealfood
									.getAmount());
							break;
						case 2:
							totalCalc += ((mealfood.getUnit().getProtein() / mealfood
									.getUnit().getStandardamound()) * mealfood
									.getAmount());
							break;
						case 3:
							totalCalc += ((mealfood.getUnit().getFat() / mealfood
									.getUnit().getStandardamound()) * mealfood
									.getAmount());
							break;
						case 4:
							totalCalc += ((mealfood.getUnit().getKcal() / mealfood
									.getUnit().getStandardamound()) * mealfood
									.getAmount());
							break;
						}

						// add the list of food
						// max 10 chars
						// text += "" + new
						// Functions().getShorterString(mealfood.getFoodName(),
						// 10) + " \n ";
						text += "" + mealfood.getFoodName() + " \n";
					}

					// Round the totalCalc
					totalCalc = new Functions().roundFloats(totalCalc, 1);

					// remove last \n from the text so we dont lose space in the
					// list
					try {
						text = text.substring(0, text.length() - 2);
					} catch (StringIndexOutOfBoundsException e) {
					}

					String value = "";

					// get right value
					switch (defaultValue) {
					case 1:
						value = context.getResources().getString(
								R.string.short_carbs);
						break;
					case 2:
						value = context.getResources().getString(
								R.string.amound_of_protein);
						break;
					case 3:
						value = context.getResources().getString(
								R.string.amound_of_fat);
						break;
					case 4:
						value = context.getResources().getString(
								R.string.short_kcal);
						break;
					}

					// tv2 is for the food list
					tv2.setText(text);

					// tv4 is for the default total value
					tv4.setText(totalCalc + " " + value);

					tv1.setTextSize(fontSize);
					tv2.setTextSize(fontSize - 3);
					tv4.setTextSize(fontSize);

					tv4.setTextColor(context.getResources().getColor(
							R.color.colorFood));

					tv4.setGravity(Gravity.RIGHT);

					// add background
					LinearLayout ll = (LinearLayout) v
							.findViewById(R.id.LinearLayoutForColor);
					if (position % 2 == 0) {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewOne));
					} else {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewTwo));
					}

				} else if (objects.get(position).getBloodGlucoseEvent() != null) {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_glucose,
							null);

					ImageView iv = (ImageView) v
							.findViewById(R.id.imageViewTracking);
					TextView tv1 = (TextView) v
							.findViewById(R.id.textViewTracking1);
					TextView tv2 = (TextView) v
							.findViewById(R.id.textViewTracking2);

					iv.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_tab_glucose_unselected));

					tv1.setText(new Functions().getTimeFromString(objects
							.get(position).getBloodGlucoseEvent()
							.getTimeStamp()));

					tv2.setText(""
							+ objects.get(position).getBloodGlucoseEvent()
									.getAmount()
							+ " "
							+ objects.get(position).getBloodGlucoseEvent()
									.getUnit());

					tv2.setTextColor(context.getResources().getColor(
							R.color.colorGlucose));

					tv2.setGravity(Gravity.RIGHT);
					tv1.setTextSize(fontSize);
					tv2.setTextSize(fontSize);

					// add background
					LinearLayout ll = (LinearLayout) v
							.findViewById(R.id.LinearLayoutForColor);
					if (position % 2 == 0) {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewOne));
					} else {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewTwo));
					}

				} else if (objects.get(position).getMedicineEvent() != null) {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_medicine,
							null);

					ImageView iv = (ImageView) v
							.findViewById(R.id.imageViewTracking);
					TextView tv1 = (TextView) v
							.findViewById(R.id.textViewTracking1);
					TextView tv2 = (TextView) v
							.findViewById(R.id.textViewTracking2);

					TextView tv3 = (TextView) v
							.findViewById(R.id.textViewTracking3);

					iv.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_tab_medicine_unselected));

					tv1.setText(new Functions().getTimeFromString(objects
							.get(position).getMedicineEvent().getTimeStamp()));

					tv2.setText(""
							+ objects.get(position).getMedicineEvent()
									.getMedicineTypeName());

					tv3.setText(""
							+ objects.get(position).getMedicineEvent()
									.getAmount()
							+ " "
							+ objects.get(position).getMedicineEvent()
									.getMedicineTypeUnit());

					tv1.setTextSize(fontSize);
					tv2.setTextSize(fontSize - 3);
					tv3.setTextSize(fontSize);

					tv3.setTextColor(context.getResources().getColor(
							R.color.colorMedicine));

					tv3.setGravity(Gravity.RIGHT);

					// add background
					LinearLayout ll = (LinearLayout) v
							.findViewById(R.id.LinearLayoutForColor);
					if (position % 2 == 0) {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewOne));
					} else {
						ll.setBackgroundColor(context.getResources().getColor(
								R.color.ColorListViewTwo));
					}

				} else {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_date,
							null);
				}
			}

		}

		return v;
	}
}
