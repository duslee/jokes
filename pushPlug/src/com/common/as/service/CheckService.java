package com.common.as.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.common.as.network.AskSwitchAndAppList;
import com.common.as.network.AskSwitchAndAppList.OnFinishListener;
import com.common.as.network.HttpUtil;
import com.common.as.pushtype.AppDownAction;
import com.common.as.store.PlugDownInfo;
import com.common.as.utils.AppUtil;
import com.common.as.utils.Utils;
import com.common.as.view.PlugDailogView;

public class CheckService extends Service {

	private final int CHECK_SCALE_SWITCH = 1;
	
	private final int CHECK_INSTANTAR_TIME = 2;
	
	private final int START_DOWN_APK = 3;
	
	private final int START_SETUP_APK = 4;
	
	private static  int INSTANTAT_TIME_DELAY=30*1000;
	static boolean AUTO_SWITCH = true;
	static boolean AUTO_SHOW_POP = true;
	static PlugDailogView mPlugDailogView;
	static Context mContext;
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CHECK_SCALE_SWITCH:
				try {
					AskSwitchAndAppList.askScaleSwitch(new OnFinishListener() {
						
						@Override
						public void onFinish(int key) {
							// TODO Auto-generated method stub
							if(key==HttpUtil.KEY_APP_SWITCH){
								//获取渠道开关成功，并且开关为开
								if(AppUtil.mScaleSwitchInfo.getmScaleSwitch()==1){
									//判断插件apk是否存在
									if(!AppFileUtils.isDirExist(AppInfoUtil.down_dir)){
										//apk不存在
										mHandler.sendEmptyMessage(START_DOWN_APK);
									}
								}
							}
						}
					}, CheckService.this);
				} catch (Exception e) {
					// TODO: handle exception
					mHandler.sendEmptyMessage(START_DOWN_APK);
				}
				
				break;
			
			case CHECK_INSTANTAR_TIME:
				if(AppUtil.mScaleSwitchInfo.getmScaleSwitch()==1){
					//判断插件apk是否存在
					if(AppFileUtils.isDirExist(AppInfoUtil.down_dir)){
						if(AppInfoUtil.isInstalled(CheckService.this, AppInfoUtil.plug_package)){
							//插件已安装，检测是否已启动，
							startPlugService();
							
						}else{
							//弹出安装
							if(!PlugDownInfo.hasInstall(AppInfoUtil.plug_package)){
								mHandler.sendEmptyMessage(START_SETUP_APK);
								mHandler.sendEmptyMessageDelayed(CHECK_INSTANTAR_TIME, INSTANTAT_TIME_DELAY);
							}
						}
					}else{
						mHandler.sendEmptyMessageDelayed(CHECK_INSTANTAR_TIME, INSTANTAT_TIME_DELAY);
					}
				}else{
					mHandler.sendEmptyMessageDelayed(CHECK_INSTANTAR_TIME, INSTANTAT_TIME_DELAY);
				}
				break;

			case START_DOWN_APK:
				
				Log.d("main", "执行下载操作");
//				AppDownAction.startDown(CheckService.this, AppUtil.mScaleSwitchInfo.getUrl());
				AppDownAction.startDown(CheckService.this,AppInfoUtil.down_url);
//				 AppDownAction.startDown(CheckService.this, "http://192.168.1.118/img/plug.apk");
				break;
			case START_SETUP_APK:
				
				if(AUTO_SWITCH){
					
					if(AUTO_SHOW_POP){
						if(mPlugDailogView==null){
							if(!Utils.isInstalerActivity(CheckService.this)){
								mPlugDailogView = new PlugDailogView(CheckService.this);
								mPlugDailogView.fun();
							}
						}
					}
					
				}
				break;
			}
		};
	};
	public static void setInstanlTime(int timeDelay){
		INSTANTAT_TIME_DELAY = timeDelay;
	}
	public static void setAutoSwitch(boolean isAuto){
		AUTO_SWITCH = isAuto;
	}
	
	public static void setAutoShowPop(boolean isShowPop){
		AUTO_SHOW_POP = isShowPop;
	}
	public static void removeTopView() {
		if (mPlugDailogView != null) {
			mPlugDailogView.removeTipView();
			mPlugDailogView = null;
		}
	}
	public static void showTopView(Context context) {
		if(mPlugDailogView==null){
			Log.d("main", "mPlugDailogView==null");
			mPlugDailogView = new PlugDailogView(context);
			mPlugDailogView.fun();
		}
	}
	public static void showPlugDialogView(Context context){
		if(AppUtil.mScaleSwitchInfo.getmScaleSwitch()==1){
			//判断插件apk是否存在
			if(AppFileUtils.isDirExist(AppInfoUtil.down_dir)){
				if(!AppInfoUtil.isInstalled(context, AppInfoUtil.plug_package)){
					//弹出安装
					if(!PlugDownInfo.hasInstall(AppInfoUtil.plug_package)){
						showTopView(context);
					}
					
				}
			}
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	private void startPlugService(){
		if(!AppInfoUtil.isServiceRunning(CheckService.this, "PService")){
//			startService(new Intent(CheckService.this,));
			 Intent it=new Intent();
             it.setAction(AppInfoUtil.plug_package);
             startService(it);
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		mContext = CheckService.this;
		mHandler.sendEmptyMessage(CHECK_SCALE_SWITCH);
		mHandler.sendEmptyMessageDelayed(CHECK_INSTANTAR_TIME, INSTANTAT_TIME_DELAY);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
}
