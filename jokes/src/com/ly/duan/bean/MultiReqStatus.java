package com.ly.duan.bean;

public class MultiReqStatus {
	
	private int content1Status;
	private int content2Status;
	private int bannerStatus;
	private int currentStatus;
	
	public MultiReqStatus() {
	}
	
	public MultiReqStatus(int content1Status, int content2Status, int bannerStatus) {
		this.content1Status = content1Status;
		this.content2Status = content2Status;
		this.bannerStatus = bannerStatus;
	}

	public int getContent1Status() {
		return content1Status;
	}

	public void setContent1Status(int content1Status) {
		this.content1Status = content1Status;
	}

	public int getContent2Status() {
		return content2Status;
	}

	public void setContent2Status(int content2Status) {
		this.content2Status = content2Status;
	}

	public int getBannerStatus() {
		return bannerStatus;
	}

	public void setBannerStatus(int bannerStatus) {
		this.bannerStatus = bannerStatus;
	}

	public int getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	@Override
	public String toString() {
		return "MultiReqStatus:{" + 
				"\'content1Status=\'" + content1Status + 
				", \'content2Status=\'" + content2Status + 
				", \'bannerStatus=\'" + bannerStatus + 
				", \'currentStatus=\'" + currentStatus + 
				"}";
	}

}
