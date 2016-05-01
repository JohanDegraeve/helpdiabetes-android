
package com.hippoandfriends.helpdiabetes.Rest;

import java.util.Comparator;

import com.hippoandfriends.helpdiabetes.Objects.DBTracking;


/*
 * This class is used to compare a dbtacking list on HH:mm
 * It is used on the tracking page
 * */

public class TimeComparator implements Comparator<DBTracking> {

	public int compare(DBTracking arg0, DBTracking arg1) {
		int compareTo = arg0.getTimestamp().compareTo(arg1.getTimestamp());

		// switch the compareTo value if -1 it becomes 1 and visa versa
		if (compareTo == -1)
			compareTo = 1;
		else if (compareTo == 1)
			compareTo = -1;
		else
			compareTo = 0;
 
		return compareTo; 
	}
}
