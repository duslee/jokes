/**
 * 
 */
package com.ly.duan.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 功能：<br>
 * 日期：2014-11-3
 * 
 * @author zfwu
 * @time 上午10:34:14
 */
@Table(name = "pushes")
public class PushBean extends EntityBase {

	@Column(column = "appid")
	private long appid;
	
	@Column(column="pushId")
	private long pushId;

	@Column(column = "ver")
	private int ver;
	
	@Column(column = "pushTitle")
	private String pushTitle;
	
	@Column(column = "pushDesc")
	private String pushDesc;
	
	@Column(column = "pushImgUrl")
	private String pushImgUrl;
	
	@Column(column = "sourceType")
	private int sourceType;
	
	@Column(column = "sourceId")
	private long sourceId;
	
	@Column(column="contentUrl")
	private String contentUrl;
	
	@Column(column="userNick")
	private String userNick;
	
	@Column(column="userVarUrl")
	private String userVarUrl;

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}

	public long getPushId() {
		return pushId;
	}

	public void setPushId(long pushId) {
		this.pushId = pushId;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public String getPushTitle() {
		return pushTitle == null ? "" : pushTitle;
	}

	public void setPushTitle(String pushTitle) {
		this.pushTitle = pushTitle;
	}

	public String getPushDesc() {
		return pushDesc == null ? "" : pushDesc;
	}

	public void setPushDesc(String pushDesc) {
		this.pushDesc = pushDesc;
	}

	public String getPushImgUrl() {
		return pushImgUrl == null ? "" : pushImgUrl;
	}

	public void setPushImgUrl(String pushImgUrl) {
		this.pushImgUrl = pushImgUrl;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	
	public String getContentUrl() {
		return contentUrl == null ? "" : contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getUserNick() {
		return userNick == null ? "" : userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getUserVarUrl() {
		return userVarUrl == null ? "" : userVarUrl;
	}

	public void setUserVarUrl(String userVarUrl) {
		this.userVarUrl = userVarUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof PushBean) {
			PushBean bean = (PushBean) o;
			return (getId() == bean.getId()) &&
					(pushId == bean.getPushId()) &&
					(ver == bean.getVer()) &&
					((pushTitle == null) ? (pushTitle == bean.getPushTitle()) : (pushTitle.equals(bean.getPushTitle()))) &&
					((pushDesc == null) ? (pushDesc == bean.getPushDesc()) : (pushDesc.equals(bean.getPushDesc()))) &&
					((pushImgUrl == null) ? (pushImgUrl == bean.getPushImgUrl()) : (pushImgUrl.equals(bean.getPushImgUrl()))) &&
					(sourceType == bean.getSourceType()) &&
					(sourceId == bean.getSourceId()) &&
					((contentUrl == null) ? (contentUrl == bean.getContentUrl()) : (contentUrl.equals(bean.getContentUrl()))) &&
					((userNick == null) ? (userNick == bean.getUserNick()) : (userNick.equals(bean.getUserNick()))) &&
					((userVarUrl == null) ? (userVarUrl == bean.getUserVarUrl()) : (userVarUrl.equals(bean.getUserVarUrl())))
					;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PushBean={" + 
				"\'id\'=" + getId() + 
				", \'appid\'=" + appid + 
				", \'pushId\'=" + pushId + 
				", \'ver\'=" + ver + 
				", \'pushTitle\'=\'" + pushTitle + "\'" + 
				", \'pushDesc\'=\'" + pushDesc + "\'" + 
				", \'pushImgUrl\'=\'" + pushImgUrl + "\'" + 
				", \'sourceType\'=\'" + sourceType + "\'" + 
				", \'sourceId\'=\'" + sourceId + "\'" + 
				", \'contentUrl\'=\'" + contentUrl + "\'" + 
				", \'userNick\'=\'" + userNick + "\'" + 
				", \'userVarUrl\'=\'" + userVarUrl + "\'" + 
				'}';
	}

}
