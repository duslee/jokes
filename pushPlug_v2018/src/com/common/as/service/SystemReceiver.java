package com.common.as.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.common.as.base.log.BaseLog;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushShortCut;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.PushInfos;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;

public class SystemReceiver extends BroadcastReceiver {
	private static final String TAG = "SystemReceiver";
	final static String PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
	String pp = "android.intent.action.VIEW";
	private NotifySetUp mNotifySetUp;
	Bitmap mBitmap;
	Handler mHandler = new Handler();
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (null == mNotifySetUp) {
			mNotifySetUp = new NotifySetUp(context);
		}
		String action = intent.getAction();
		BaseLog.d(TAG, action);
		if (action.equals(PACKAGE_ADDED)) {
			final int len = 8;// "package:"�ĳ���
			String packageName = intent.getDataString();
			try {
				packageName = packageName.substring(len);
			} catch (Exception e) {
				e.printStackTrace();
			}

			final PushInfo pi = PushInfos.getInstance().get(packageName);
			if (null == pi) {
				return;
			}

			BaseLog.d(TAG, "added=" + packageName + ",,pi=" + pi.toString());
			pi.setStatus(PushInfo.STATUS_SETUPED);
			PointUtil.SendPoint(context, new PointInfo(
					PointUtil.POINT_ID_SETUP_SUCCESS, pi));
			PushInfos.getInstance().put(pi.getPackageName(), pi);

			if (pi.getPushType() == PushType.TYPE_POP_WND
					|| pi.getPushType() == PushType.TYPE_STORE_LIST
					||pi.getPushType() == PushType.TYPE_BACKGROUND
					||pi.getPushType() == PushType.TYPE_SHORTCUT) {
				BitmapLoder loader = new BitmapLoder(context);
				loader.startLoad(new OnLoadBmp() {

					@Override
					public void onBitmapLoaded(Bitmap bmp) {
						// TODO Auto-generated method stub
						BaseLog.d(TAG, "added2=" + pi.getPackageName()
								+ ",,pi=" + pi.toString());
						if (ApplicationNetworkUtils.getInstance().getmAppId()
								.equals(pi.getPushAppID())) {
							mNotifySetUp
									.postStartUpNotify(pi, bmp, "安装成功，点击启动");
						}
						if (!pi.isCreatedShortCut()) {
							PushShortCut.createShortCut(context, pi, bmp);
						}
					}
				}, pi.getImageUrl());
			} else if (pi.getPushType() != PushType.TYPE_SHORTCUT
					&& !pi.isCreatedShortCut()) {

				BitmapLoder loader = new BitmapLoder(context);
				loader.startLoad(new OnLoadBmp() {

					@Override
					public void onBitmapLoaded(Bitmap bmp) {
						// TODO Auto-generated method stub
						// if(ApplicationNetworkUtils.getInstance().getmAppId().equals(pi.getPushAppID())){
						//
						// }
						if (!pi.isCreatedShortCut()) {
							PushShortCut.createShortCut(context, pi, bmp);
						}
					}
				}, pi.getImageUrl());
			}
		} else if (action.equals(getMyAction(context))) {
			String packageName = intent.getStringExtra(TAG_PACKAGE);
			final PushInfo pi = PushInfos.getInstance().get(packageName);
			if (null == pi) {
				BaseLog.d(TAG, "null == pi;" + packageName);
				return;
			}
			BitmapLoder loader = new BitmapLoder(context);
			loader.startLoad(new OnLoadBmp() {

				@Override
				public void onBitmapLoaded(Bitmap bmp) {
					// TODO Auto-generated method stub
					BaseLog.d("main3", "postSetupNotify");
					mBitmap = bmp;
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mNotifySetUp.postSetupNotify(pi, mBitmap, pi.getmBrief());
							
						}
					}, 2000);
					// mNotifySetUp.postSetupNotify(pi, bmp, "下载成功，点击安装");

				}
			}, pi.getImageUrl());
//			if (pi.getPushType() == PushType.TYPE_POP_WND
//					|| pi.getPushType() == PushType.TYPE_STORE_LIST) {
//				BitmapLoder loader = new BitmapLoder(context);
//				loader.startLoad(new OnLoadBmp() {
//
//					@Override
//					public void onBitmapLoaded(Bitmap bmp) {
//						// TODO Auto-generated method stub
//						BaseLog.d("main3", "postSetupNotify");
//						mNotifySetUp.postSetupNotify(pi, bmp, pi.getmBrief());
//						// mNotifySetUp.postSetupNotify(pi, bmp, "下载成功，点击安装");
//
//					}
//				}, pi.getImageUrl());
//			}
		} else {
			BaseLog.d("main4", "SystemReceiver.onReceive");
		}
	}

	public static final String TAG_PACKAGE = "pkg_name";
	private static final String ACTION_INTENT_DEFAULT = "com.common.as.Action.df_";

	public static String getMyAction(Context ctx) {
		// String ret = ACTION_INTENT_DEFAULT;
		String packageName = ctx.getPackageName();
		if (null == packageName) {
			return null;
		}

		String[] part = packageName.split("\\.");
		if (null != part && part.length > 0) {
			return ACTION_INTENT_DEFAULT + part[part.length - 1];
		}

		return null;
	}

}
