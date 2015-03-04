package com.ly.duan.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 功能：精品文章的封装类；也可以用于对应某一栏目中文章的数据封装类<br>
 * 日期：2014-10-20
 * 
 * @author zfwu
 * @time 上午11:49:13
 */
@Table(name = "articles")
public class ArticleBean extends EntityBase implements Serializable {
	
	private static final long serialVersionUID = -6528433584966371277L;

	@Column(column="articleId")
	private long articleId;

	@Column(column="appid")
	private long appid;
	
	@Column(column="columnId")
	private long columnId;
	
	@Column(column="ver")
	private int ver;
	
	@Column(column="articleName")
	private String articleName;
	
	@Column(column="articleDesc")
	private String articleDesc;
	
	@Column(column="hasAudio")
	private boolean hasAudio;
	
	@Column(column="hasVideo")
	private boolean hasVideo;
	
	@Column(column="imgUrl")
	private String imgUrl;
	
	@Column(column="hasPush")
	private boolean hasPush;
	
	@Column(column="url")
	private String url;
	
	@Column(column="hasVip")
	private boolean hasVip;
	
	@Column(column="curPage")
	private int curPage;
	
	@Column(column="urlType")
	private int urlType;
	
	@Column(column="type")
	private int type;
	
	@Column(column="good")
	private int good;
	
	@Column(column="bad")
	private int bad;
	
	@Column(column="approve")
	private int approve;
	
	@Column(column="stamp")
	private int stamp;

	public ArticleBean() {
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public String getArticleName() {
		return articleName == null ? "" : articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	public String getImgUrl() {
		return imgUrl == null ? "" : imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getUrl() {
		return url == null ? "" : url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getArticleDesc() {
		return articleDesc == null ? "" : articleDesc;
	}

	public void setArticleDesc(String articleDesc) {
		this.articleDesc = articleDesc;
	}

	public long getColumnId() {
		return columnId;
	}

	public void setColumnId(long columnId) {
		this.columnId = columnId;
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

	public boolean isHasPush() {
		return hasPush;
	}

	public void setHasPush(boolean push) {
		this.hasPush = push;
	}

	public boolean isHasAudio() {
		return hasAudio;
	}

	public void setHasAudio(boolean audio) {
		this.hasAudio = audio;
	}

	public boolean isHasVideo() {
		return hasVideo;
	}

	public void setHasVideo(boolean video) {
		this.hasVideo = video;
	}

	public boolean isHasVip() {
		return hasVip;
	}

	public void setHasVip(boolean hasVip) {
		this.hasVip = hasVip;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getUrlType() {
		return urlType;
	}

	public void setUrlType(int urlType) {
		this.urlType = urlType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public int getBad() {
		return bad;
	}

	public void setBad(int bad) {
		this.bad = bad;
	}

	public int getApprove() {
		return approve;
	}

	public void setApprove(int approve) {
		this.approve = approve;
	}

	public int getStamp() {
		return stamp;
	}

	public void setStamp(int stamp) {
		this.stamp = stamp;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof ArticleBean) {
			ArticleBean bean = (ArticleBean) o;
			return 	(articleId == bean.getArticleId()) &&
					(ver == bean.getVer()) &&
					(columnId == bean.getColumnId()) &&
					((articleName == null) ? (articleName == bean.getArticleName()) : (articleName.equals(bean.getArticleName()))) &&
					((articleDesc == null) ? (articleDesc == bean.getArticleDesc()) : (articleDesc.equals(bean.getArticleDesc()))) &&
					(hasAudio == bean.isHasAudio()) &&
					(hasVideo == bean.isHasVideo()) &&
					((imgUrl == null) ? (imgUrl == bean.getImgUrl()) : (imgUrl.equals(bean.getImgUrl()))) &&
					(hasPush == bean.isHasPush()) &&
					((url == null) ? (url == bean.getUrl()) : (bean.equals(bean.getUrl()))) &&
					(hasVip == bean.isHasVip()) &&
					(curPage == bean.getCurPage()) &&
					(urlType == bean.getUrlType()) &&
					(type == bean.getType()) &&
					(good == bean.getGood()) &&
					(bad == bean.getBad()) &&
					(approve == bean.getApprove()) &&
					(stamp == bean.getStamp())
					;
		}
		return false;
	}

	@Override
	public String toString() {
		return "ArticleBean={" + 
				"\'id\'=" + getId() + 
				", \'articleId\'=" + articleId + 
				", \'appid\'=" + appid + 
				", \'ver\'=" + ver + 
				", \'columnId\'=" + columnId + 
				", \'articleName\'=\'" + articleName + "\'" + 
				", \'articleDesc\'=\'" + articleDesc + "\'"+ 
				", \'hasAudio\'=" + hasAudio + 
				", \'hasVideo\'=" + hasVideo + 
				", \'imgUrl\'=\'" + imgUrl + "\'" + 
				", \'hasPush\'=" + hasPush + 
				", \'url\'=\'" + url + "\'"+ 
				", \'hasVip\'=" + hasVip + 
				", \'curPage\'=" + curPage + 
				", \'urlType\'=" + urlType + 
				", \'type\'=" + type + 
				", \'good\'=" + good + 
				", \'bad\'=" + bad + 
				", \'approve\'=" + approve + 
				", \'stamp\'=" + stamp + 
				'}';
	}

}
