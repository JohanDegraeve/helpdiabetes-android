// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBFoodLanguage {
	private long id;
	private String language;
	private String resource;

	public DBFoodLanguage(long id, String language, String resource) {
		super();
		this.id = id;
		this.language = language;
		this.resource = resource;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	
	
}
