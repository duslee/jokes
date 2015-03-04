package com.ly.duan.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="savedStatus")
public class SavedStatus extends EntityBase {
	
	@Column(column="tabName")
	private String tabName;
	
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof SavedStatus) {
			SavedStatus status = (SavedStatus) o;
			return ((tabName == null) ? (tabName == status.getTabName()) : (tabName.equals(status.getTabName())));
		}
		return false;
	}

	@Override
	public String toString() {
		return "SavedStatus={" + 
				"\'id\'=" + getId() + 
				", \'tabName\'=\'" + tabName + "\'" +
				'}';
	}

}
