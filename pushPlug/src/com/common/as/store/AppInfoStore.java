package com.common.as.store;

import android.content.Context;

public class AppInfoStore {

	//push ����
	//imsi �û���Ϣ
	
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
