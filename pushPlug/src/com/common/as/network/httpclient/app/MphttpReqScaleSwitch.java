package com.common.as.network.httpclient.app;


import android.content.Context;

import com.common.as.network.httpclient.MPHttpClientRequestGet;
import com.common.as.network.httpclient.MPHttpClientUtils;

public class MphttpReqScaleSwitch extends MPHttpClientRequestGet{

	public MphttpReqScaleSwitch(Context ctx) {
		super(ctx,getUrl(), true);
		// TODO Auto-generated constructor stub
	}

	public static String getUrl(){
		String s = MPHttpClientUtils.ROOM_SERVER_URL + 
				"switch!getScaleSwitches?";
		return s;
	}
}
