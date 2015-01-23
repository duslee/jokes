package com.common.activelog.http;

import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

import com.common.activelog.ActiveLogReq;
import com.common.activelog.ActiveLogResp;
import com.common.activelog.error.ErrorLogReq;
import com.common.activelog.error.ErrorLogResp;
import com.common.activelog.http.MPHttpClientInterface.MPHttpClientRespListener;

public class MPHttpClientUtils {

	public static final String ROOM_SERVER_URL_RELEASE = "http://appnew.tortodream.com/mvideo/";

	public static String ROOM_SERVER_URL = ROOM_SERVER_URL_RELEASE;

	public static final String ROOO_SERVER_VPLAY_URL = "http://192.168.100.121:8081/video/";

	public static void addDefaultCookie(HttpUriRequest request) {
		// Nothing.
	}

	public static boolean isSuccResponseCode(String errCode) {
		return errCode.equals("200");
	}

	// Allan add 20130726 send point
	public static void sendActivieLog(int id,
			MPHttpClientRespListener listener, int startType, Context context,
			String appid, String channelid) {
		MPHttpClientManager hM = MPHttpClientManager.getInstance(context);
		ActiveLogReq request = new ActiveLogReq(startType, context, appid,
				channelid);
		ActiveLogResp response = new ActiveLogResp();
		hM.doRequest(listener, id, request, response);
	}

	public static void sendErrorLogReq11(int id,
			MPHttpClientRespListener listener, Context context,
			String payChannelid, String nodeId, String contentId,
			String downTime, String downLength) {
		MPHttpClientManager hM = MPHttpClientManager.getInstance(context);
		ErrorLogReq request = new ErrorLogReq(context, payChannelid, nodeId,
				contentId, downTime, downLength);
		ErrorLogResp response = new ErrorLogResp();
		hM.doRequest(listener, id, request, response);
	}

	public static void sendErrorLogReq(int id,
			MPHttpClientRespListener listener, Context context, String appid,
			String channelid) {
		MPHttpClientManager hM = MPHttpClientManager.getInstance(context);
		ErrorLogReq request = new ErrorLogReq(context, appid, channelid);
		ErrorLogResp response = new ErrorLogResp();
		hM.doRequest(listener, id, request, response);
	}

}
