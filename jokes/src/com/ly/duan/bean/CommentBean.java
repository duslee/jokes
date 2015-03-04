package com.ly.duan.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="comments")
public class CommentBean extends EntityBase {
	
	@Column(column="appid")
	private long appid;
	
	/** 1-文章，2-帖子,3-网页，4-APK */
	@Column(column="contentType")
	private int contentType;
	
	@Column(column="contentId")
	private long contentId;
	
	@Column(column="page")
	private int page;
	
	@Column(column="commentId")
	private String commentId;
	
	@Column(column="comment")
	private String comment;

	@Column(column="createTime")
	private String createTime;
	
	@Column(column="userNick")
	private String userNick;
	
	@Column(column="userVar")
	private String userVar;
	
	@Column(column="userType")
	private int userType;
	
	@Column(column="ip")
	private String ip;
	
	@Column(column="goodCount")
	private int goodCount;
	
	@Column(column="badCount")
	private int badCount;

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public long getContentId() {
		return contentId;
	}

	public void setContentId(long contentId) {
		this.contentId = contentId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getUserVar() {
		return userVar;
	}

	public void setUserVar(String userVar) {
		this.userVar = userVar;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getGoodCount() {
		return goodCount;
	}

	public void setGoodCount(int goodCount) {
		this.goodCount = goodCount;
	}

	public int getBadCount() {
		return badCount;
	}

	public void setBadCount(int badCount) {
		this.badCount = badCount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof CommentBean) {
			CommentBean bean = (CommentBean) o;
			return ((commentId == null) ? (commentId == bean.getCommentId()) : (commentId.equals(bean.getCommentId()))) &&
					((comment == null) ? (comment == bean.getComment()) : (comment.equals(bean.getComment()))) &&
					((createTime == null) ? (createTime == bean.getCreateTime()) : (createTime.equals(bean.getCreateTime()))) &&
					((userNick == null) ? (userNick == bean.getUserNick()) : (userNick.equals(bean.getUserNick()))) &&
					((userVar == null) ? (userVar == bean.getUserVar()) : (userVar.equals(bean.getUserVar()))) &&
					(userType == bean.getUserType()) &&
					((ip == null) ? (ip == bean.getIp()) : (ip.equals(bean.getIp()))) &&
					(contentType == bean.getContentType()) &&
					(page == bean.getPage()) &&
					(contentId == bean.getContentId()) &&
					(goodCount == bean.getGoodCount()) &&
					(badCount == bean.getBadCount());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "CommentBean={" + 
				"\'id\'=" + getId() + 
				"\'appid\'=" + appid +
				"\'contentType\'=" + contentType +
				"\'page\'=" + page +
				"\'contentId\'=" + contentId +
				", \'commentId\'=\'" + commentId + "\'" +
				", \'comment\'=\'" + comment + "\'" +
				", \'createTime\'=\'" + createTime + "\'" +
				", \'userNick\'=\'" + userNick + "\'" +
				", \'userVar\'=\'" + userVar + "\'" +
				", \'userType\'=" + userType +
				", \'commentId\'=\'" + ip + "\'" +
				", \'goodCount\'=" + goodCount +
				", \'badCount\'=" + badCount +
				'}';
	}
	
}
