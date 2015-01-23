package com.ly.duan.ui;

import android.app.Application;

import com.common.as.utils.AppListUtils;
import com.common.as.utils.CommonUtils;
import com.lidroid.xutils.util.LogUtils;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		LogUtils.allowE = true;
		LogUtils.allowD = true;

		/* 初始化并启动push */
		AppListUtils.getInstance(this).SetApplistControlSwitch(false);
		CommonUtils.MainInit(this);
	}

}
