package com.ly.duan.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "columns")
public class ColumnBean extends EntityBase {

	@Column(column="appid")
	private long appid;
	
	@Column(column="columnId")
	private long columnId;
	
	@Column(column="columnName")
	private String columnName;
	
	@Column(column="columnDesc")
	private String columnDesc;
	
	@Column(column="imgUrl")
	private String imgUrl;
	
	@Column(column="type")
	private int type;
	
	@Column(column="hasVip")
	private boolean hasVip;
	
	@Column(column="ver")
	private int ver;

	public long getColumnId() {
		return columnId;
	}

	public void setColumnId(long columnId) {
		this.columnId = columnId;
	}

	public String getColumnName() {
		return columnName == null ? "" : columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnDesc() {
		return columnDesc == null ? "" : columnDesc;
	}

	public void setColumnDesc(String columnDesc) {
		this.columnDesc = columnDesc;
	}

	public String getImgUrl() {
		return imgUrl == null ? "" : imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isHasVip() {
		return hasVip;
	}

	public void setHasVip(boolean hasVip) {
		this.hasVip = hasVip;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof ColumnBean) {
			ColumnBean bean = (ColumnBean) o;
			return 	(columnId == bean.getColumnId()) &&
					((columnName == null) ? (columnName == bean.getColumnName()) :(columnName.equals(bean.getColumnName()))) &&
					((columnDesc == null) ? (columnDesc == bean.getColumnDesc()) : (columnDesc.equals(bean.getColumnDesc()))) &&
					((imgUrl == null) ? (imgUrl == bean.getImgUrl()) : (imgUrl.equals(bean.getImgUrl()))) &&
					(type == bean.getType()) &&
					(hasVip == bean.isHasVip()) &&
					(ver == bean.getVer());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "ColumnBean={" + 
				"id=" + getId() + 
				", appid=" + appid + 
				", columnId=" + columnId + 
				", ver=" + ver + 
				", columnName='" + columnName + "\'" + 
				", columnDesc='" + columnDesc + "\'"+ 
				", imgUrl='" + imgUrl  + "\'" + 
				", type=" + type + 
				", hasVip=" + hasVip +  
				'}';
	}

}
