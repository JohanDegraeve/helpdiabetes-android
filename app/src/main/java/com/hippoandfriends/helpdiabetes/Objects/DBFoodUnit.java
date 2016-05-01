// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBFoodUnit {
	private long id;
	private String name;
	private String description;
	private float standardamound;
	private float kcal;
	private float protein;
	private float carbs;
	private float fat;
	private float visible;
	private int foodid;
	
	public DBFoodUnit(long id, String name, String description,
			float standardamound, float kcal, float protein, float carbs,
			float fat, float visible, int foodid) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.standardamound = standardamound;
		this.kcal = kcal;
		this.protein = protein;
		this.carbs = carbs;
		this.fat = fat;
		this.visible = visible;
		this.foodid = foodid;
	}
	
	public DBFoodUnit() {
		super();
	}


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getStandardamound() {
		return standardamound;
	}
	public void setStandardamound(float standardamound) {
		this.standardamound = standardamound;
	}
	public float getKcal() {
		return kcal;
	}
	public void setKcal(float kcal) {
		this.kcal = kcal;
	}
	public float getProtein() {
		return protein;
	}
	public void setProtein(float protein) {
		this.protein = protein;
	}
	public float getCarbs() {
		return carbs;
	}
	public void setCarbs(float carbs) {
		this.carbs = carbs;
	}
	public float getFat() {
		return fat;
	}
	public void setFat(float fat) {
		this.fat = fat;
	}
	public float getVisible() {
		return visible;
	}
	public void setVisible(float visible) {
		this.visible = visible;
	}
	public int getFoodid() {
		return foodid;
	}
	public void setFoodid(int foodid) {
		this.foodid = foodid;
	}
	
	
	
	
}
