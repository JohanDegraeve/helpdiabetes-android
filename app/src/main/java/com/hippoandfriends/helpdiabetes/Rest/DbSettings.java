// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Rest;

/*
 * This class is used to hold all the database setting names
 */

public class DbSettings {
	//<!-- Database version -->
	public static final String setting_database_version = "databaseVersion";
	
    //<!-- Insuline ratio setting names -->
    public static final String setting_insuline_ratio_breakfast = "insulineRatioBreakfast";
    public static final String setting_insuline_ratio_lunch = "insulineRatioLunch";
    public static final String setting_insuline_ratio_dinner = "insulineRatioDinner";
    public static final String setting_insuline_ratio_snack = "insulineRatioSnack";

    //<!-- Meal time setting names -->
    public static final String setting_meal_time_breakfast = "mealTimeBreakfast";
    public static final String setting_meal_time_lunch = "mealTimeLunch";
    public static final String setting_meal_time_snack = "mealTimeSnack";
    public static final String setting_meal_time_dinner = "mealTimeDinner";

    //<!-- Font size settings -->
    public static final String setting_font_size = "fontSize";

    //<!-- Language -->
    public static final String setting_language = "language";

    //<!-- meal unit -->
    public static final String setting_glucose_unit = "glucoseUnit";
    
    //<!-- value on/off settings -->
    public static final String setting_value_carb_onoff = "valueCarbOnOff";
    public static final String setting_value_prot_onoff = "valueProtOnOff";
    public static final String setting_value_fat_onoff = "valueFatOnOff";
    public static final String setting_value_kcal_onoff = "valueKcalOnOff";
    
    //<!-- value default -->
    //<!-- 1 = carb, 2 = prot, 3 = fat, 4 = kcal -->
    public static final String setting_value_default = "valueDefault";
    
    //<!-- Default medicine type (used to add selected food to tracking page) -->
    public static final String setting_default_medicine_type_ID = "defaultMedicineTypeID";
    
    //<!-- To check if application just started -->
    public static final String setting_startUp = "startUp";
    
    //<!-- Default exercise type -->
    public static final String setting_default_exercise_type_ID = "exerciseUnit";
	
    //<!-- Expand listview - selected food -->
    public static final String setting_expand_listview = "expandListView";
}
