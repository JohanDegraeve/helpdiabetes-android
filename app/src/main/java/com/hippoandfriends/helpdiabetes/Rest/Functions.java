// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/*
 * This class got functions that are used in the application.
 */

public class Functions {

	// This function will get a string as input and transrom that string to
	// xxxx... if its longer then maxLength
	public String getShorterString(String input, int maxLength) {
		if (input.length() > maxLength)
			return input.substring(0, (maxLength - 2)) + "...";
		else
			return input;
	}

	/*
	 * Round function
	 */

	public float roundFloats(float input, int numbersBehind) {
		float p = (float) Math.pow(10, numbersBehind);
		return Math.round(input * p) / p;
	}

	/*
	 * Time & Date functions
	 */

	public int getHour(Calendar calendar) {
		return calendar.getTime().getHours();
	}

	public int getMinutes(Calendar calendar) {
		return calendar.getTime().getMinutes();
	}

	// This method will return yyyy-MM-dd from a date object
	public String getYearMonthDayAsStringFromDate(Date date) {
		String year = "" + (date.getYear() + 1900);
		String month = "" + (date.getMonth() + 1);
		String day = "" + date.getDate();

		if ((date.getMonth() + 1) < 10)
			month = "0" + month;

		if (date.getDate() < 10)
			day = "0" + day;

		return year + "-" + month + "-" + day;
	}

	// This method will return yyyy-MM-dd HH:mm:ss from the regular calendar
	// Just like datetime('now') would do but then with the selected time
	public String getDateAsStringFromCalendar(Calendar calendar) {
		String year = "" + calendar.get(Calendar.YEAR);
		String month = "" + (calendar.get(Calendar.MONTH) + 1);
		String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
		String hour = "" + getHour(calendar);
		String minute = "" + getMinutes(calendar);
		String seconds = "" + calendar.get(Calendar.SECOND);

		// if month < 10 we have to add a zero in front
		if ((calendar.get(Calendar.MONTH) + 1) < 10)
			month = "0" + month;

		// if day < 10 we have to add a zero in front
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
			day = "0" + day;

		// if hour < 10 we have to add a zero in front
		if (getHour(calendar) < 10)
			hour = "0" + hour;

		// if minute < 10 we have to add a zero in front
		if (getMinutes(calendar) < 10)
			minute = "0" + minute;

		// if second < 10 we have to add a zero in front;
		if (calendar.get(Calendar.SECOND) < 10)
			seconds = "0" + seconds;

		// return yyyy-MM-dd HH:mm:ss
		return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":"
				+ seconds;
	}

	// This method will return a string object with the right time in the format
	// HH:mm
	public String getTimeFromDate(Date date) {
		String hour = "";
		String minute = "";

		if (date.getHours() < 10)
			hour = "0";
		hour += date.getHours();

		if (date.getMinutes() < 10)
			minute = "0";
		minute += date.getMinutes();

		return hour + ":" + minute;
	}

	// This method will return a date object with the right date and time
	// The input string willb e in the format yyyy-MM-dd HH:mm:ss
	// This method is used to order our dbTracking list on time
	public Date getYearMonthDayHourMinutesAsDateFromString(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date value = format.parse(date);
			return value;
		} catch (ParseException e) {
			return null;
		}
	}

	//This function will return the day / month as string from a long
	//The input long is the amount of days that has to be minused from the current date
	public String getDayAndMonthFromDouble(double value){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, (int) value);
				
		return "" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH);
	}
	
	// This method will return a date object with the right date
	// The input string will be in the format yyyy-MM-dd HH:mm:ss
	public Date getYearMonthDayAsDateFromString(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date value = format.parse(date);
			return value;
		} catch (ParseException e) {
			return null;
		}
	}

	public int getHourAndMinutesFromString(String date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date dValue = format.parse(date);
			return (dValue.getHours() * 60) + dValue.getMinutes();
		} catch(ParseException e){
			return 0;
		}
	}
	
	// This method will return a int with the hour from the input
	// The input date will be in the format HH:mm
	public int getHourFromString(String date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			return format.parse(date).getHours();
		} catch (ParseException e) {
			return -1;
		}
	}

	// This method will return a int with the minutes from the input
	// The input date will be in the format HH:mm
	public int getMinutesFromString(String date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			return format.parse(date).getMinutes();
		} catch (ParseException e) {
			return -1;
		}
	}

	// This method will return a string objct with the time
	// The input string will be in the format yyyy-MM-dd HH:mm:ss
	public String getTimeFromString(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date value = format.parse(date);
			int hour = value.getHours();
			int minutes = value.getMinutes();
			String returnHour = "";
			String returnMinutes = "";

			if (hour < 10)
				returnHour = "0";
			returnHour += hour;

			if (minutes < 10)
				returnMinutes = "0";
			returnMinutes += minutes;

			return returnHour + ":" + returnMinutes;
		} catch (ParseException e) {
			return null;
		}
	}

	// This is used in the tracking activity where we show a exercise type
	// the input is a start time and the duration in seconds
	public String getTimeFromDuration(String startDate, int duration) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date dateStart;

			dateStart = format.parse(startDate);

			int hour = duration / 3600;
			int minutes = (duration - (hour * 3600)) / 60;
 
			dateStart.setHours(dateStart.getHours() + hour);
			dateStart.setMinutes(dateStart.getMinutes() + minutes);

			String returnHour = "";
			String returnMinutes = "";

			if (dateStart.getHours() < 10)
				returnHour = "0";
			returnHour += dateStart.getHours();

			if (dateStart.getMinutes() < 10)
				returnMinutes = "0";
			returnMinutes += dateStart.getMinutes();

			return "" + returnHour + ":" + returnMinutes;

		} catch (ParseException e) {
			return null;
		}
	}

	public String getDurationFromSeconds(int startTime, int endTime) {
		int seconds = endTime - startTime;
		int hour = seconds / 3600;
		int minutes = (seconds - (hour * 3600)) / 60;

		String returnHour = "";
		String returnMinutes = "";

		if (hour < 10)
			returnHour = "0";
		returnHour += hour;

		if (minutes < 10)
			returnMinutes = "0";
		returnMinutes += minutes;

		return "" + returnHour + ":" + returnMinutes;
	}

}
