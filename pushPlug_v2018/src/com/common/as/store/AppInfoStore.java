package com.common.as.store;

import android.content.Context;

public class AppInfoStore {

	//push 开关
	//imsi 用户信息
	
	private static AppInfoStore instance;
	private Context ctx;
	private boolean isPushOpen = false;
	
	public static synchronized AppInfoStore getInstance(){
		if (null == instance) {
			instance = new AppInfoStore();
		}
         return instance;
	}
	
	
	private AppInfoStore(){
		
	}


	public boolean isPushOpen() {
		return isPushOpen;
	}


	public void setPushOpen(boolean isPushOpen) {
		this.isPushOpen = isPushOpen;
	}


	public Context getCtx() {
		return ctx;
	}


	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	
	
	
}
