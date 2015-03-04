package com.common.as.network.httpclient;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.common.as.base.log.BaseLog;

import android.content.Context;
import android.view.WindowManager;


public class MPHttpClientRequestGet extends MPHttpClientInterface.MPHttpClientRequest{
	private boolean mUsedCookie = true;
	
	private HttpGet mHttpGet;
	

	
	public MPHttpClientRequestGet() {

	}


	public MPHttpClientRequestGet(Context ctx,String url, boolean usedCookie){
		setNetUrl(url,ctx);
		mUsedCookie = usedCookie;
	}
	public MPHttpClientRequestGet(String url, boolean usedCookie,Context context){
		setSignedUrl(url,context);
		mUsedCookie = usedCookie;
	}
	
	public void setUsedCookie(boolean bCookie){
		mUsedCookie = bCookie;
	}
	
	@Override
	public HttpUriRequest getHttpRequest() {
		// TODO Auto-generated method stub
		mHttpGet = new HttpGet(mUrl);
		
		if (mUsedCookie){
			MPHttpClientUtils.addDefaultCookie(mHttpGet);
		}
		
		return mHttpGet;
	}
	

}
