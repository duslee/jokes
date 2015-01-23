package com.common.activelog.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

public class MPHttpClientRequestGet extends
		MPHttpClientInterface.MPHttpClientRequest {
	private boolean mUsedCookie = true;

	private HttpGet mHttpGet;

	public MPHttpClientRequestGet() {

	}

	public MPHttpClientRequestGet(String url, boolean usedCookie,
			Context cotnext, String payChannelid, String nodeId,
			String contentId, String downTime, String downLength) {
		setErrorUrl(url, cotnext, payChannelid, nodeId, contentId, downTime,
				downLength);
		mUsedCookie = usedCookie;
	}

	public MPHttpClientRequestGet(String url, boolean usedCookie,
			Context cotnext, String appid, String channelid) {
		setNetUrl(url, cotnext, appid, channelid);
		mUsedCookie = usedCookie;
	}

	public MPHttpClientRequestGet(String url, boolean usedCookie,
			Context cotnext) {
		setUrl(url, cotnext);
		mUsedCookie = usedCookie;
	}

	public MPHttpClientRequestGet(String url, boolean usedCookie,
			Context cotnext, String channelid) {
		setVPayUrl(url, cotnext, channelid);
		mUsedCookie = usedCookie;
	}

	public void setUsedCookie(boolean bCookie) {
		mUsedCookie = bCookie;
	}

	@Override
	public HttpUriRequest getHttpRequest() {
		// TODO Auto-generated method stub
		mHttpGet = new HttpGet(mUrl);

		if (mUsedCookie) {
			MPHttpClientUtils.addDefaultCookie(mHttpGet);
		}

		return mHttpGet;
	}

}
