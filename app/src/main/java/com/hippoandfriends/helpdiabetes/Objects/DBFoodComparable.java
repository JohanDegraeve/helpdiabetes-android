// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBFoodComparable {
	private long id;
	private String platform;
	private long languageid;
	private int visible;
	private long categoryid;
	private long userid;
	private int isfavorite;
	private String name;
	
	public DBFoodComparable(long id, String platform, long languageid, int visible,
			long categoryid, long userid, int isfavorite, String name) {
		super();
		this.id = id;
		this.platform = platform;
		this.languageid = languageid;
		this.visible = visible;
		this.categoryid = categoryid;
		this.userid = userid;
		this.isfavorite = isfavorite;
		this.name = name;
	}

	public DBFoodComparable(DBFoodComparable newFood) {
		super();
		this.id = newFood.getId();
		this.platform = newFood.getPlatform();
		this.languageid = newFood.getLanguageid();
		this.visible = newFood.getVisible();
		this.categoryid = newFood.getCategoryid();
		this.userid = newFood.getUserid();
		this.isfavorite = newFood.getIsfavorite();
		this.name = newFood.getName();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public long getLanguageid() {
		return languageid;
	}

	public void setLanguageid(long languageid) {
		this.languageid = languageid;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public long getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(long categoryid) {
		this.categoryid = categoryid;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public int getIsfavorite() {
		return isfavorite;
	}

	public void setIsfavorite(int isfavorite) {
		this.isfavorite = isfavorite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString(){
		return name;
	}
	
	

}
