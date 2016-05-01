// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Rest;

/*
 * This class is used to parse data from activity to activity.
 * The data has to be parsing with the same name and the names are stored in this class
 */

public class DataParser {
	// public static final String
	public static final String fromWhereWeCome = "fromWherWeCome";
	public static final String whatToDo = "whatToDo";
  
	//used first time app starts app
	public static final String doFirstTime = "doFirstTime";
	
	// food.platform name for own created food //
	public static final String foodPlatform = "android";
	
	// Food //
	public static final String weComeFromShowFoodList = "weComeFromShowFoodList";
	public static final String weComeFRomShowSelectedFood = "weComeFRomSelectedFood";
	public static final String weComeFromShowFoodTemplates = "weComeFromShowFoodTemplates";

	// for parsing ID's
	public static final String idFood = "foodID";
	public static final String idSelectedFood = "selectedFoodID";
	public static final String idUnit = "unitID";

	// for parsing the food amount from a template to the
	// showAddFoodToSelectionPage
	public static final String foodAmount = "foodAmount";

	//For parsing the search value to the create food page
	public static final String foodSearchValue = "searchValue";
	
	// Exercise event //
	public static final String doUpdateExerciseEvent = "updateExerciseEvent";
	public static final String doCreateExerciseEvent = "createExerciseEvent";
	public static final String idExerciseEvent = "exerciseEventID";

	// Exercise type //
	public static final String doUpdateExerciseType = "updateExerciseType";
	public static final String doCreateExerciseType = "createExerciseType";
	public static final String idExerciseType = "exerciseTypeID";

	// Medicine type //
	public static final String idMedicineType = "medicineTypeID";
	
	// used to parse the tabHost object //
	public static final String tabhost = "tabhost";

	// used to parse the boolean from showhometab to showfoodlist
	public static final String bool = "booleanFromHomeTabToFoodList";

	// used to get all the timestamps out the database
	public static final String timestamp = "eventDateTimeFromDB";
	
	// Activity IDs //
	public static final String activityIDMeal = "showActivityMeal";	
	public static final String activityIDTracking = "showActivityTracking";
	public static final String activityIDGlucose = "showActivityGlucose";
	public static final String activityIDMedicine = "showActivityMedicine";
	public static final String activityIDExercise = "showActivityExercise";
	public static final String activityIDSettings = "showActivitySettings";
}
