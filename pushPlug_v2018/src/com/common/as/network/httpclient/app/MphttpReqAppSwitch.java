package com.common.as.network.httpclient.app;

import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

import com.common.as.network.httpclient.MPHttpClientRequestGet;
import com.common.as.network.httpclient.MPHttpClientUtils;
import com.common.as.network.utils.ApplicationNetworkUtils;

public class MphttpReqAppSwitch extends MPHttpClientRequestGet{

	public MphttpReqAppSwitch(Context ctx) {
		super(ctx,getUrl(), true);
		// TODO Auto-generated constructor stub
	}

	public static String getUrl(){
		String s = MPHttpClientUtils.ROOM_SERVER_URL + 
				"push!getPushSwitches";
		return s;
	}
}
