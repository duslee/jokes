package com.common.as.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.main.Main;
import com.common.as.network.HttpPayUtil;
import com.common.as.network.HttpUtil;
import com.common.as.network.HttpUtil.RequestData;
import com.common.as.network.httpclient.MPHttpClientData;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientRespListener;
import com.common.as.network.httpclient.MPHttpClientUtils;
import com.common.as.network.httpclient.app.MpHttpReqPostData.LogData;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.AppListManager;
import com.common.as.store.StartTimesInfo;
import com.common.as.struct.PaySwitchInfo;

public class PointUtil {
	public static final String TAG = "PointUtil";
	public static final int POINT_ID_START_PUSH = 1;
	public static final int POINT_ID_START_DOWN = 2;
	public static final int POINT_ID_FINISH_DOWN = 3;
	public static final int POINT_ID_SETUP_SUCCESS = 4;
	public static final int POINT_ID_START_UP = 5;
	public static final int POINT_ID_SETUP = 6;
	public static class PointInfo {
		final int pointid;
		int subPoint;
		final PushInfo pi;

		public int getSubPoint() {
			return subPoint;
		}

		public void setSubPoint(int subPoint) {
			this.subPoint = subPoint;
		}

		public PointInfo(int pointid, PushInfo pi) {
			super();
			this.pointid = pointid;
			this.pi = pi;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			PushType type = pi.getPushType();
			return "point=" + pointid + ",type=" + type + "," + pi;
		}

	}

	public static void SendPoint(final Context ctx, final PointInfo pi) {
		BaseLog.d(TAG, pi + "");
		if (null == pi.pi) {
			return;
		}

		Main.getUiHandler().post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpUtil mHttpUtil = new HttpUtil(ctx);
				RequestData rq = new RequestData(HttpUtil.KEY_POST_DATA) {

					@Override
					public void onSuccess(int what, Object obj) {
						// TODO Auto-generated method stub
						

					}

					@Override
					public void onFailed(int what, Object obj) {
					}
				};
				LogData lgoData = new LogData(AppListManager.getListType(pi.pi
						.getPushType()), pi.pi.getAppid(), pi.pi.getPushType()
						.ordinal(), pi.pointid);
				lgoData.setLogsubcode(pi.subPoint);
				rq.setInput(lgoData);

				mHttpUtil.startRequest(rq);
			}
		});
	}
	public static int getPaySwitch(final Context context){
		Main.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpPayUtil mHttpPayUtil = new HttpPayUtil(context);
				HttpPayUtil.RequestData hrq = new HttpPayUtil.RequestData(HttpPayUtil.KEY_PAY_SWITCH) {
					
					@Override
					public void onSuccess(int what, Object obj) {
						PaySwitchInfo	info = (PaySwitchInfo)obj;
						Log.d("main", "info="+info.toString());
					}
					
					@Override
					public void onFailed(int what, Object obj) {
						PaySwitchInfo  info = AppListManager.getmPaySwitchInfo();
					}
				};
				mHttpPayUtil.startRequest(hrq);
			}
		});
		return AppListManager.getmPaySwitchInfo().getmPaySwitch();
	}
	public static void SendPoint1(final Context ctx,  int startType) {
		PackageInfo info = null;
		try {
			info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = info.packageName;
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
	MPHttpClientUtils.postLog(HttpUtil.KEY_POST_NET_DATA, new MPHttpClientRespListener() {
			
			@Override
			public void onMPHttpClientResponse(int id, int errId, int statusId,
					MPHttpClientData obj) {
				// TODO Auto-generated method stub
			}
		}, startType,ctx);
		
	}
	
	

}
