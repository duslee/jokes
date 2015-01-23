package com.ly.duan.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="banners")
public class BannerBean extends EntityBase {
	
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
				'}';
	}

}
