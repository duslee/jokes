package com.common.as.network;

import android.content.Context;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.AppListManager;
import com.common.as.struct.ScaleSwitchInfo;
import com.common.as.struct.SwitchInfo;

public class AskSwitchAndAppList {

	public static interface OnFinishListener{
		public void onFinish(int key);
	}
	
	
	public static void askSwitchAndApp(final OnFinishListener listenr,final Context ctx){
		HttpUtil mHttpUtil = new HttpUtil(ctx);
		//1 switch
		HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(HttpUtil.KEY_PUSH_SWITCH) {
			
			@Override
			public void onSuccess(int what, Object obj) {
				// 开关打开时，去请求列表，然后检查本地是否有已下载成功的应用需要push
				SwitchInfo switchInfo = (SwitchInfo)obj;
				
				BaseLog.d("main", "switch="+switchInfo.toString());
				
				if (switchInfo.getmBgSwitch() == 1
						|| switchInfo.getmPopSwitch() == 1
					||	switchInfo.getmListSwitch()== 1
						|| switchInfo.getmShortCutSwitch() == 1
						|| switchInfo.getmTopWndSwitch() ==1) {
					if (switchInfo.getmBgSwitch() == 1){
							requestList(ctx,AppListManager.FLAG_APP_BG, listenr);
						}
					if (switchInfo.getmListSwitch()== 1) {
						requestList(ctx,AppListManager.FLAG_APP_LIST, listenr);
					}
					if (switchInfo.getmPopSwitch() == 1){
							requestList(ctx,AppListManager.FLAG_APP_POP, listenr);
						}
					if (switchInfo.getmShortCutSwitch() == 1) {
						requestList(ctx,AppListManager.FLAG_APP_SHORTCUT, listenr);
					}
					if (switchInfo.getmTopWndSwitch() == 1) {
						requestList(ctx,AppListManager.FLAG_APP_BANNER, listenr);
					}
					listenr.onFinish(what);
				} else {
					listenr.onFinish(0);
				}
				
			}
			
			@Override
			public void onFailed(int what, Object obj) {
				// TODO Auto-generated method stub
				Log.d("main", "onFailed");
				SwitchInfo switchInfo = AppListManager.getmSwitchInfo();
				if (null != switchInfo) {
					if (switchInfo.getmBgSwitch() == 1
							|| switchInfo.getmPopSwitch() == 1
						||	switchInfo.getmListSwitch()== 1
							|| switchInfo.getmShortCutSwitch() == 1
							|| switchInfo.getmTopWndSwitch() ==1) {
					if (switchInfo.getmBgSwitch() == 1){
							requestList(ctx,AppListManager.FLAG_APP_BG, listenr);
						}
					if (switchInfo.getmListSwitch()== 1) {
						requestList(ctx,AppListManager.FLAG_APP_LIST, listenr);
					}
					if (switchInfo.getmPopSwitch() == 1){
							requestList(ctx,AppListManager.FLAG_APP_POP, listenr);
						}
					if (switchInfo.getmShortCutSwitch() == 1) {
						requestList(ctx,AppListManager.FLAG_APP_SHORTCUT, listenr);
					}
					if (switchInfo.getmTopWndSwitch() == 1) {
						requestList(ctx,AppListManager.FLAG_APP_BANNER, listenr);
					}
					listenr.onFinish(what);
				} else {
						listenr.onFinish(0);
					}
				}else{
					listenr.onFinish(0);
				}

			}
			
		};
		mHttpUtil.startRequest(mRequestData);
	}
	public static void askScaleSwitch(final OnFinishListener listenr,final Context ctx){
		HttpUtil mHttpUtil = new HttpUtil(ctx);
		//1 switch
		HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(HttpUtil.KEY_APP_SWITCH) {
			
			@Override
			public void onSuccess(int what, Object obj) {
				// 开关打开时，去请求列表，然后检查本地是否有已下载成功的应用需要push
//				SwitchInfo switchInfo = (SwitchInfo)obj;
				ScaleSwitchInfo switchInfo = (ScaleSwitchInfo)obj;
				if (null != switchInfo) {
					if (switchInfo.getmScaleSwitch()==1) {
					listenr.onFinish(what);
				} else {
						listenr.onFinish(0);
					}
				}else{
					listenr.onFinish(0);
				}
			}
			
			@Override
			public void onFailed(int what, Object obj) {
				// TODO Auto-generated method stub
				listenr.onFinish(100);
			}
			
		};
		mHttpUtil.startRequest(mRequestData);
	}
	
	
	private static void  requestList(Context context,int listType, final OnFinishListener listenr){
		HttpUtil mHttpUtil = new HttpUtil(context);
		int key = HttpUtil.KEY_STORE_LIST;
		if (listType == AppListManager.FLAG_APP_BG) {
			key = HttpUtil.KEY_BG_LIST;
		}else if (listType == AppListManager.FLAG_APP_POP) {
			key = HttpUtil.KEY_POP_LIST;
		}else if (listType == AppListManager.FLAG_APP_SHORTCUT) {
			key = HttpUtil.KEY_SCUT_LIST;
		}else if (listType == AppListManager.FLAG_APP_BANNER) {
			key = HttpUtil.KEY_BTN_LIST;
		}
		BaseLog.d("main", "AppListManager.isOutDay(context, listType)="+AppListManager.isOutDay(context, listType));
		BaseLog.d("main2", "key="+key);
		if (AppListManager.isOutDay(context, listType)) {

			HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(key) {
				
				@Override
				public void onSuccess(int what, Object obj) {
					// TODO Auto-generated method stub
					//wait rewrite
					listenr.onFinish(what);
				}
				
				@Override
				public void onFailed(int what, Object obj) {
					// TODO Auto-generated method stub
					listenr.onFinish(what);
				}
			};
			mHttpUtil.startRequest(mRequestData);
		}else{
			listenr.onFinish(key);
		}
	}
}
