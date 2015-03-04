package com.ly.duan.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="duans")
public class DuanBean extends EntityBase implements Serializable {
	
	private static final long serialVersionUID = 4277199663386083690L;

	@Column(column="duanId")
	private long duanId; 
	
	@Column(column="appid")
	private long appid;
	
	@Column(column="columnId")
	private long columnId;
	
	@Column(column="ver")
	private int ver;
	
	@Column(column="curPage")
	private int curPage;

	@Column(column="avatarUrl")
	private String avatarUrl;
	
	@Column(column="nick")
	private String nick;
	
	@Column(column="content")
	private String content;
	
	@Column(column="imgUrl")
	private String imgUrl;
	
	@Column(column="imgType")
	private int imgType;
	
	@Column(column="vip")
	private boolean vip;
	
	@Column(column="good")
	private int good;
	
	@Column(column="bad")
	private int bad;
	
	@Column(column="approve")
	private int approve;
	
	@Column(column="stamp")
	private int stamp;
	
	@Column(column="contentType")
	private int contentType;

	public long getDuanId() {
		return duanId;
	}

	public void setDuanId(long duanId) {
		this.duanId = duanId;
	}

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}

	public long getColumnId() {
		return columnId;
	}

	public void setColumnId(long columnId) {
		this.columnId = columnId;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public String getAvatarUrl() {
		return avatarUrl == null ? "" : avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getNick() {
		return nick == null ? "" : nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getContent() {
		return content == null ? "" : content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImgUrl() {
		return imgUrl == null ? "" : imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getImgType() {
		return imgType;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}
	
	public boolean getVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
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

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return "DuanBean={"	+ 
				"\'id\'=" + getId() + 
				", \'duanId\'=" + duanId + 
				", \'appid\'=" + appid + 
				", \'columnId\'=" + columnId + 
				", \'ver\'=" + ver + 
				", \'curPage\'=" + curPage + 
				", \'avatarUrl\'=\'" + avatarUrl + '\'' + 
				", \'nick\'=\'" + nick + '\'' + 
				", \'content\'=\'" + content + '\'' + 
				", \'imgUrl\'=\'" + imgUrl + '\'' + 
				", \'imgType\'=" + imgType + 
				", \'vip\'=" + vip + 
				", \'good\'=" + good + 
				", \'bad\'=" + bad + 
				", \'approve\'=" + approve + 
				", \'stamp\'=" + stamp + 
				", \'contentType\'=" + contentType + 
				'}';
	}
	
}
