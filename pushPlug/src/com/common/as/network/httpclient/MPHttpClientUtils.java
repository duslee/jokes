package com.common.as.network.httpclient;

import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

import com.common.as.base.log.Config;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientRespListener;
import com.common.as.network.httpclient.app.MpHttpReqAppList;
import com.common.as.network.httpclient.app.MpHttpReqAppList.ListReq;
import com.common.as.network.httpclient.app.MpHttpReqPostData;
import com.common.as.network.httpclient.app.MpHttpReqPostData.LogData;
import com.common.as.network.httpclient.app.MpHttpRespAppList;
import com.common.as.network.httpclient.app.MpHttpRespPostData;
import com.common.as.network.httpclient.app.MphttpReqAppSwitch;
import com.common.as.network.httpclient.app.MphttpReqPaySwitch;
import com.common.as.network.httpclient.app.MphttpRespAppSwitch;
import com.common.as.network.httpclient.app.MphttpRespPaySwitch;
import com.common.as.network.utils.ApplicationNetworkUtils;


public class MPHttpClientUtils {	

	
	public static final String ROOM_SERVER_URL_RELEASE = "http://appnew.tortodream.com/mvideo/";
	public static  String ROOM_SERVER_URL = ROOM_SERVER_URL_RELEASE;
	
		
	public static void addDefaultCookie(HttpUriRequest request){
		// Nothing.
	}
	
	public static boolean isSuccResponseCode(String errCode){
		return errCode.equals("200");
	}
	
	public static void cancel(int id,MPHttpClientRespListener listener){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ApplicationNetworkUtils.getInstance().getAppCtx());
		hM.removeListener(listener, id);
	}
	
	public static void cancel(MPHttpClientRespListener listener){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ApplicationNetworkUtils.getInstance().getAppCtx());
		hM.removeListener(listener);
	}
	
	/**
	 * 开关
	 * @param usrName eg: winger.zhou@hotmail.com
	 * @param pwd eg: 123456
	 * @param id eg: 0
	 * @param listener 返回值监听接口
	 * @return 事件回调obj = LoginHttpResp
	 * Note: 如果用户名为空时表示以游客身份登录
	 */
	public static void getSwitch(int id, MPHttpClientRespListener listener,Context ctx){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ctx);
		MphttpReqAppSwitch request = new MphttpReqAppSwitch(ctx);
		MphttpRespAppSwitch response = new MphttpRespAppSwitch();
		
		hM.doRequest(listener, id, request, response);
	}
	public static void getPaySwitch(int id, MPHttpClientRespListener listener,Context ctx){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ctx);
		MphttpReqPaySwitch request = new MphttpReqPaySwitch(ctx);
		MphttpRespPaySwitch response = new MphttpRespPaySwitch();
		hM.doRequest(listener, id, request, response);
	}
	public static void getAppList(int id, MPHttpClientRespListener listener,ListReq listReq,Context ctx){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ctx);
		MpHttpReqAppList request = new MpHttpReqAppList(listReq,ctx);
		MpHttpRespAppList response = new MpHttpRespAppList();
		
		hM.doRequest(listener, id, request, response);
	}
	
	
	public static void postLog(int id, MPHttpClientRespListener listener,LogData logData,Context ctx){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ctx);
		MpHttpReqPostData request = new MpHttpReqPostData(logData,ctx);
		MpHttpRespPostData response = new MpHttpRespPostData();
		
		hM.doRequest(listener, id, request, response);
	}
	public static void postLog(int id, MPHttpClientRespListener listener,int startType,Context ctx){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ctx);
		MpHttpReqPostData request = new MpHttpReqPostData(ctx,startType);
		MpHttpRespPostData response = new MpHttpRespPostData();
		hM.doRequest(listener, id, request, response);
	}
	public static void signedLog(int id, MPHttpClientRespListener listener,Context ctx){
		MPHttpClientManager hM = MPHttpClientManager.getInstance(ctx);
		MpHttpReqPostData request = new MpHttpReqPostData(ctx);
		MpHttpRespPostData response = new MpHttpRespPostData();
		hM.doRequest(listener, id, request, response);
	}
}
