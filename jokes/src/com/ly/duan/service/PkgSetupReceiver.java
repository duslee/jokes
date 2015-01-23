package com.ly.duan.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.common.as.pushtype.PushInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppUtil;
import com.lidroid.xutils.util.LogUtils;
import com.ly.duan.utils.ResourceUtils;
import com.ly.duan.utils.StringUtils;

public class PkgSetupReceiver extends BroadcastReceiver {

	public final static String PACKAGE_START_SETUP = "android.intent.action.PACKAGE_SETUP";

	@Override
	public void onReceive(Context context, Intent intent) {
		String appid = ResourceUtils.getFileFromAssets(context, "appid.txt");

		String action = intent.getAction();
		LogUtils.e("action=" + action + ", appid=" + appid);

		if (!StringUtils.isBlank(action)
				&& action.equalsIgnoreCase(PACKAGE_START_SETUP)) {
			String pkg = intent.getStringExtra("pkg");
			String _appid = intent.getStringExtra("appid");
			LogUtils.e("pkg=" + pkg + ", _appid=" + _appid);

			if (!StringUtils.isBlank(_appid) && !StringUtils.isBlank(appid)
					&& appid.equalsIgnoreCase(_appid)) {
				PushInfo pi = PushInfos.getInstance().get(pkg);
				if (pi != null) {
					LogUtils.e("pi=" + pi.toString());
					AppUtil.showSetup(context, pi);
				}
			}

		}
	}

}
