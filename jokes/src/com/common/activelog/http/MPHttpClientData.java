package com.common.activelog.http;

public abstract class MPHttpClientData {
	private int mErrId;
	private int mStatusCode;
	
	public void setErrId(int errId){
		mErrId = errId;
	}
	
	public void setStatusCode(int statusCode){
		mStatusCode = statusCode;
	}
	
	public int getErrId(){
		return mErrId;
	}
	
	public int getStatusCode(){
		return mStatusCode;
	}
	public abstract String getErrMessage();
	
	public abstract boolean isSuccess();
}
