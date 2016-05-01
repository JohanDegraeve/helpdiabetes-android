// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Objects;

/*
 * This class is used in showSettingsValueOrder
 * This class will hold the value order number and the name of that setting
 */
public class DBValueOrder {
	private int order;
	private String settingName;
	private String valueName;
	
	public DBValueOrder(int order, String settingName, String valueName) {
		super();
		this.order = order;
		this.settingName = settingName;
		this.valueName = valueName;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getSettingName() {
		return settingName;
	}

	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

}
