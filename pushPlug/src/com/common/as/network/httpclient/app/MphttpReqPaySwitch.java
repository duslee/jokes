package com.common.as.network.httpclient.app;

import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

import com.common.as.network.httpclient.MPHttpClientRequestGet;
import com.common.as.network.httpclient.MPHttpClientUtils;
import com.common.as.network.utils.ApplicationNetworkUtils;

public class MphttpReqPaySwitch extends MPHttpClientRequestGet{

	public MphttpReqPaySwitch(Context ctx) {
		super(ctx,getUrl(), true);
		// TODO Auto-generated constructor stub
	}

	public static String getUrl(){
		String s = MPHttpClientUtils.ROOM_SERVER_URL + 
				"switch!getPaySwitches";
		return s;
	}
}
