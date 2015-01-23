package com.common.activelog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.common.activelog.http.HttpRespPaser;
import com.common.activelog.http.MPHttpClientData;
import com.common.activelog.http.MPHttpClientInterface.MPHttpClientRespListener;
import com.common.activelog.http.MPHttpClientUtils;
import com.common.activelog.util.StartTimesInfo;
import com.lidroid.xutils.util.LogUtils;
import com.ly.duan.utils.NetUtils;

public class ActiveLogUtil {
	public static String ActiveLogUtil_TAG = "ActiveLogUtil";
	public static boolean ACTIVELOGDEBUG = false;

	private static Context context;
	private int provider = -1;

	private static ActiveLogUtil instance;
	public static int NETWORKTYPE_DEFAULT = -1;
	public static int WIFI = 1;
	public static int CMWAP = 2;
	public static int CMNET = 3;

	public static synchronized ActiveLogUtil getInstance() {
		if (instance == null) {
			instance = new ActiveLogUtil(context);
		}
		return instance;
	}

	public ActiveLogUtil(Context context) {
		this.context = context;
	}

	public static void sendActiveLog(int startType, Context context,
			String appid, String channelid) {
		ActiveLogUtil activeLog = ActiveLogUtil.getInstance();
		activeLog.req(startType, context, appid, channelid);
	}

	public void req(int startType, final Context context, String appid,
			String channelid) {
		provider = NetUtils.getAPNType(context);
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String s = info.packageName;
		LogUtils.e(info.packageName);
		StartTimesInfo times = StartTimesInfo.get(s);
		if (null == times) {
			times = new StartTimesInfo(s);
		}
		times.addCount();
		times.save(s);
		if (times.isFirstStart()) {
			startType = 0;
		} else {
			startType = 1;
		}
		MPHttpClientUtils.sendActivieLog(10, new MPHttpClientRespListener() {

			@Override
			public void onMPHttpClientResponse(int id, int errId, int statusId,
					MPHttpClientData obj) {
				HttpRespPaser paser = new HttpRespPaser(context, obj, errId,
						statusId);
				// BaseLog.d("main", "sendActiveLog=" +
				// paser.isRespondSuccess());
				LogUtils.e("sendActiveLog=" + paser.isRespondSuccess());
				// if (paser.isRespondSuccess())
				// {
				// ActiveLogResp response = (ActiveLogResp)obj;
				// }
			}
		}, startType, context, appid, channelid);
	}
}