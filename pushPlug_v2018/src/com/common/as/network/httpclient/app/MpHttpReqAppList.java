package com.common.as.network.httpclient.app;

import android.content.Context;

import com.common.as.network.httpclient.MPHttpClientRequestGet;
import com.common.as.network.httpclient.MPHttpClientUtils;

public class MpHttpReqAppList extends MPHttpClientRequestGet {
	
	public static class ListReq{
		final int listType;
		public static final int TYPE_LIST_BTN = 4;
		public static final int TYPE_LIST_STORE_LIST = 5;
		public static final int TYPE_LIST_POP = 1;
		public static final int TYPE_LIST_S_CUT = 2;
		public static final int TYPE_LIST_BG = 3;
		public ListReq(int listType) {
			super();
			this.listType = listType;
		}
		
	}
	public MpHttpReqAppList(ListReq listReq,Context ctx) {
		super(ctx,getUrl(listReq), true);
		// TODO Auto-generated constructor stub
	}

	public static String getUrl(ListReq listReq){
		String s = MPHttpClientUtils.ROOM_SERVER_URL + 
				"push!getPushList?listType="+listReq.listType;
		return s;
	}
}
