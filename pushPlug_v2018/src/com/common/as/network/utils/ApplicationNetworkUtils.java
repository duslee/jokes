package com.common.as.network.utils;


import com.common.as.base.log.BaseLog;
import com.common.as.store.AppData;
import com.common.as.utils.ThreadPoolsInterface;

import android.content.Context;


public class ApplicationNetworkUtils 
{
	private  Context appCtx;
	private  ThreadPoolsInterface mThreadManager = null;
	private  ClientInfo mClientInfo;
	
	public static class ClientInfo{
		final public String channelId;
		final public String appId;
		public int pushId;
		public int pushVer = 2018;
		final public int appVer;
		public ClientInfo(String channelId, String appId, int pushId,
				int pushVer, int appVer) {
			super();
			this.channelId = channelId;
			this.appId = appId;
			this.pushId = pushId;
			this.pushVer = pushVer;
			this.appVer = appVer;
		}
		public ClientInfo(String channelId, String appId, int appVer) {
			super();
			this.channelId = channelId;
			this.appId = appId;
			this.appVer = appVer;
		}
		
		
		//public int apnType = -1; //����״̬  -1��û������  1��WIFI���� 2��wap���� 3��net����
	}
	
	private static ApplicationNetworkUtils instance;
	public static ApplicationNetworkUtils getInstance(){

		if (null == instance) {
			return instance = new ApplicationNetworkUtils();
		}
		
		return instance;
	}
	
	public void setData(Context ctx, ClientInfo ci,ThreadPoolsInterface pool){
		BaseLog.v("main", "ApplicationNetworkUtils.setData");
		mClientInfo = ci;
		appCtx = ctx;
		mThreadManager = pool;
	}
		
	

	
	
	
	public ThreadPoolsInterface getmThreadManager() {
		return mThreadManager;
	}





	
	public Context getAppCtx()
	{
		return appCtx;
	}
	

	
	public  String getmAppId(){
		if(mClientInfo != null)
		{
			return mClientInfo.appId;
		}
		return null;
	}

	
	


	public ClientInfo getmClientInfo() {
		return mClientInfo;
	}
	

	

}
