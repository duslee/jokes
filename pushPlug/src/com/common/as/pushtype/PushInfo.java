package com.common.as.pushtype;

import java.io.File;
import java.io.Serializable;

import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.utils.DateUtil;

import android.graphics.Bitmap;

public class PushInfo implements Serializable{
	
	public static final String MIME_APP1 = "application";
	public static final String MIME_APP2 = "/";
	public static final String MIME_APP3 = "vnd.android";
	public static final String MIME_APP4 = ".package-archive";
//	public static final String MIME_APP = MIME_APP1+MIME_APP2+MIME_APP3+MIME_APP4;
	
	public static final String MIME_VIDEO = "video/mpeg";
	public static final String MIME_TEXT = "text/plain";
//	public static final String MIME_APP = "application/vnd.android.package-archive";
	public static final String MIME_JPEG = "image/jpeg";
	public static final String MIME_SHORT_TEXT = "text/html";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	public final String packageName;
	final String appid;
	final String ver;
	String appName;
	public String imageUrl;
	String mDownUrl;
	String mBrief;
	String pushAppID;
	String picUrl;
	
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public int getUrlType() {
		return urlType;
	}
	public void setUrlType(int urlType) {
		this.urlType = urlType;
	}
	int urlType;
	public String getPushAppID() {
		return pushAppID;
	}
	public void setPushAppID(String pushAppID) {
		this.pushAppID = pushAppID;
	}
	boolean isCreatedShortCut = false;
	public String getmBrief() {
		return mBrief;
	}
	public void setmBrief(String mBrief) {
		this.mBrief = mBrief;
	}
	PushType pushType;
	long downFinshT;
	String instanlAskT;
	public String getInstanlAskT() {
		return instanlAskT;
	}
	public void setInstanlAskT(String instanlAskT) {
		this.instanlAskT = instanlAskT;
	}
	private String fileUrl;
	
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public static final int STATUS_DOWN_STARTING = 3;
	public static final int STATUS_DOWN_FINISH = 2;// 
	public static final int STATUS_SETUPED = 1;// �Ѱ�װ
	public static final int STATUS_PUSHED = 0;
	int status = STATUS_PUSHED;
	
	
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
		if (status == STATUS_DOWN_FINISH) {
			if(pushType != PushType.TYPE_BACKGROUND){
				setDownFinshT(DateUtil.getCurrentMs());
			}
		}
	}
	public PushInfo(String packageName, String appid, String ver) {
		super();
		this.packageName = packageName;
		this.appid = appid;
		this.ver = ver;
	}
	public String getPackageName() {
		return packageName;
	}
	public String getAppid() {
		return appid;
	}
	public String getVer() {
		return ver;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getmDownUrl() {
		return mDownUrl;
	}
	public void setmDownUrl(String mDownUrl) {
		this.mDownUrl = mDownUrl;
	}
	
	
	public PushType getPushType() {
		return pushType;
	}
	public void setPushType(PushType pushType) {
		this.pushType = pushType;
	}
	
	
	public long getDownFinshT() {
		return downFinshT;
	}
	public void setDownFinshT(long downFinshT) {
		this.downFinshT = downFinshT;
	}
	
	
	
	
	public boolean isCreatedShortCut() {
		return isCreatedShortCut;
	}
	public void setCreatedShortCut(boolean isCreatedShortCut) {
		this.isCreatedShortCut = isCreatedShortCut;
	}
	public boolean isFileExist(){
		if (null != fileUrl) {
			File f = new File(fileUrl);
			if (f.exists()) {
				return true;
			}
		}
		
		return false;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "appid="+appid+";appName="+appName+";package="+packageName+";file="+fileUrl+"\n"
				+",,pushtype="+getPushType()+",,imageUrl="+imageUrl+",,status="+getStatus()+",,isCreatedShortCut="+isCreatedShortCut;
		//return mDownUrl +";"+imageUrl+";"+appid+";"+appName+";"+packageName;
	}

	public static String getMIME_APP(){
		return getMIME_APP1()+getMIME_APP2();
	}
	public static String getMIME_APP1(){
		return MIME_APP1+MIME_APP2;
	}
	public static String getMIME_APP2(){
		return MIME_APP3+MIME_APP4;
	}

	

}
