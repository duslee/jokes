package com.common.as.network.httpclient;

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
	
	public abstract boolean isSuccess();
	
}
