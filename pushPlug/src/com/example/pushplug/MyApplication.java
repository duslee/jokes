package com.example.pushplug;

import com.common.as.main.Main;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.network.utils.ApplicationNetworkUtils.ClientInfo;
import com.common.as.utils.AppUtil;

import android.app.Application;

public class MyApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ClientInfo ci = new ClientInfo("test_zyf_002", "2",  AppUtil.getAppVer(this));
		ci.pushId = 1;
		ci.pushVer = 1;
		Main.init(this,ci);

	}

	
}
