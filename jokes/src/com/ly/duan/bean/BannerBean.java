package com.ly.duan.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="banners")
public class BannerBean extends EntityBase implements Serializable {
	
	private static final long serialVersionUID = -3345102002110715142L;

	@Column(column="bannerId")
	private long bannerId;

	@Column(column="appid")
	private long appid;

	@Column(column="ver")
	private int ver;
	
	@Column(column="bannerTitle")
	private String bannerTitle;
	
	@Column(column="bannerDesc")
	private String bannerDesc;
	
	/** 1-文章，2-帖子,3-网页，4-APK */
	@Column(column="contentType")
	private int contentType;
	
	@Column(column="bannerImgUrl")
	private String bannerImgUrl;
	
	/** 文章或帖子ID */
	@Column(column="contentId")
	private long contentId;
	
	/** 网页或APK的地址 */
	@Column(column="contentUrl")
	private String contentUrl;
	
	/** APK包名 */
	@Column(column="contentPackage")
	private String contentPackage;

	@Column(column="userNick")
	private String userNick;
	
	@Column(column="userVarUrl")
	private String userVarUrl;

	public BannerBean() {
		super();
	}

	public long getBannerId() {
		return bannerId;
	}

	public void setBannerId(long bannerId) {
		this.bannerId = bannerId;
	}

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public String getBannerTitle() {
		return bannerTitle == null ? "" : bannerTitle;
	}

	public void setBannerTitle(String bannerTitle) {
		this.bannerTitle = bannerTitle;
	}

	public String getBannerDesc() {
		return bannerDesc == null ? "" : bannerDesc;
	}

	public void setBannerDesc(String bannerDesc) {
		this.bannerDesc = bannerDesc;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getBannerImgUrl() {
		return bannerImgUrl == null ? "" : bannerImgUrl;
	}

	public void setBannerImgUrl(String bannerImgUrl) {
		this.bannerImgUrl = bannerImgUrl;
	}

	public long getContentId() {
		return contentId;
	}

	public void setContentId(long contentId) {
		this.contentId = contentId;
	}

	public String getContentUrl() {
		return contentUrl == null ? "" : contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getContentPackage() {
		return contentPackage == null ? "" : contentPackage;
	}

	public void setContentPackage(String contentPackage) {
		this.contentPackage = contentPackage;
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
		if (o instanceof BannerBean) {
			BannerBean bean = (BannerBean) o;
			return 	(getId() == bean.getId()) &&
					(bannerId == bean.getBannerId()) &&
					(ver == bean.getVer()) &&
					((bannerTitle == null) ? (bannerTitle == bean.getBannerTitle()) : (bannerTitle.equals(bean.getBannerTitle()))) &&
					((bannerDesc == null) ? (bannerDesc == bean.getBannerDesc()) : (bannerDesc.equals(bean.getBannerDesc()))) &&
					(contentType == bean.getContentType()) &&
					((bannerImgUrl == null) ? (bannerImgUrl == bean.getBannerImgUrl()) : (bannerImgUrl.equals(bean.getBannerImgUrl()))) &&
					(contentId == bean.getContentId()) &&
					((contentUrl == null) ? (contentUrl == bean.getContentUrl()) : (contentUrl.equals(bean.getContentUrl()))) &&
					((contentPackage == null) ? (contentPackage == bean.getContentPackage()) : (contentPackage.equals(bean.getContentPackage()))) &&
					((userNick == null) ? (userNick == bean.getUserNick()) : (userNick.equals(bean.getUserNick()))) &&
					((userVarUrl == null) ? (userVarUrl == bean.getUserVarUrl()) : (userVarUrl.equals(bean.getUserVarUrl())))
					;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "BannerBean={" + 
				"\'id\'=" + getId() + 
				", \'bannerId\'=" + bannerId + 
				", \'appid\'=" + appid + 
				", \'ver\'=" + ver + 
				", \'bannerTitle\'=\'" + bannerTitle + "\'" + 
				", \'bannerDesc\'=\'" + bannerDesc + "\'"+ 
				", \'contentType\'=" + contentType + 
				", \'bannerImgUrl\'=\'" + bannerImgUrl + "\'" + 
				", \'contentId\'=" + contentId + 
				", \'contentUrl\'=\'" + contentUrl + "\'"+ 
				", \'contentPackage\'='" + contentPackage + "\'"+ 
				", \'userNick\'=\'" + userNick + "\'" + 
				", \'userVarUrl\'=\'" + userVarUrl + "\'" + 
				'}';
	}

}
