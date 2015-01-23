package com.common.as.network.httpclient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.view.WindowManager;

public class MPHttpClientRequestPost extends MPHttpClientInterface.MPHttpClientRequest{
	private final ArrayList<BasicNameValuePair> mParams = 
		new ArrayList<BasicNameValuePair>();

	
	public void resetPostParams(){
		mParams.clear();
//		addPostParams(new BasicNameValuePair("channelId", ApplicationNetworkUtils.mClientInfo.channelId));
//		addPostParams(new BasicNameValuePair("imsi", Utils.getImsi(ApplicationNetworkUtils.getAppCtx())));
//		addPostParams(new BasicNameValuePair("v", ApplicationNetworkUtils.mClientInfo.appVer+""));
//		addPostParams(new BasicNameValuePair("smsc", Utils.getSms(ApplicationNetworkUtils.getAppCtx())));
//		addPostParams(new BasicNameValuePair("appId", ApplicationNetworkUtils.mClientInfo.appId));

	}
	
	public void addPostParams(BasicNameValuePair param){
		mParams.add(param);
	}
	
	@Override
	public HttpUriRequest getHttpRequest(){
		
		try {
			HttpPost httpRequest = new HttpPost(mUrl); 
			HttpEntity httpentity = new UrlEncodedFormEntity(mParams, HTTP.UTF_8);
			httpRequest.setEntity(httpentity); 
			return httpRequest;
		} catch (UnsupportedEncodingException e) {
			//throw new RuntimeException(e);
			return null;
		}  
		
		
	}


	@Override
	public void setNetUrl(String url,Context ctx) {
		mUrl = url;
	}
	
}
