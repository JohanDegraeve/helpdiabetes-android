// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

public class DBNameAndID {
	private long id;
	private String name;
	private String nameTwo;
	
	

	public DBNameAndID(long id, String name, String nameTwo) {
		super();
		this.id = id;
		this.name = name;
		this.nameTwo = nameTwo;
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
	
	public String getNameTwo() {
		return nameTwo;
	}

	public void setNameTwo(String nameTwo) {
		this.nameTwo = nameTwo;
	}

	//to string returns the name so we see the name in the dropdown view
	@Override
	public String toString() {
		return name;
	}

}
