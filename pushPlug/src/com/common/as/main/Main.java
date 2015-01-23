package com.common.as.main;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.common.as.activity.ItemListActivity;
import com.common.as.base.log.BaseLog;
import com.common.as.network.AskSwitchAndAppList;
import com.common.as.network.AskSwitchAndAppList.OnFinishListener;
import com.common.as.network.HttpUtil;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.network.utils.ApplicationNetworkUtils.ClientInfo;
import com.common.as.pushtype.PushFactory;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.BackService;
import com.common.as.service.CheckService;
import com.common.as.service.MainRunServer;
import com.common.as.store.AppListManager;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.DateUtil;
import com.mozillaonline.providers.downloads.Downloads;

public class Main {


	private static boolean isInited = false;
	private static Handler mUiHandler;
	public static void init(Context appContex,ClientInfo ci){

		if (!isInited) {
			BaseLog.v("main", "Main.init");
			ApplicationNetworkUtils.getInstance().setData(appContex, ci, null);
			isInited = true;
			mUiHandler = new Handler();
			Downloads.setAuthority(appContex);
			appContex.startService(new Intent(appContex, MainRunServer.class));
			appContex.startService(new Intent(appContex, CheckService.class));
		}		
	}
	
	public static void initCtxAndCI(Context appContex,ClientInfo ci){
		ApplicationNetworkUtils.getInstance().setData(appContex, ci, null);
		mUiHandler = new Handler();
		Downloads.setAuthority(appContex);
	}
	
	public static void startMainRun(Context context){
		BaseLog.i("main", "startMainRun.startMainRunService");
		if (!isInited) {
			BaseLog.i("main", "startMainRun.startMainRunService22");
			isInited = true;
			context.startService(new Intent(context, MainRunServer.class));
		}
	}
	
	public static void setUseLocalTimer(boolean isUse){
		DateUtil.setUserLocalTimer(isUse);
	}
	
	public static void init(Context appContex,ClientInfo ci, int timer){

		init(appContex, ci);
		MainRunServer.setDELAY_TIME(timer);	
	}
	public static void init(Context appContex,ClientInfo ci, int timer,boolean isAutoShowpop){

		init(appContex, ci);
		MainRunServer.setDELAY_TIME(timer);	
		AppPrefs.isControlShowPop = isAutoShowpop;
	}
	public static void initCtxAndCI(Context appContex,ClientInfo ci, int timer,boolean isAutoShowpop){

		initCtxAndCI(appContex, ci);
		MainRunServer.setDELAY_TIME(timer);	
		AppPrefs.isControlShowPop = isAutoShowpop;
	}
	public static synchronized Handler getUiHandler(){
		return mUiHandler;
	}
	
	public static void stopTopAppStoreBtn(Context ctx){
		Intent inten = new Intent(ctx, BackService.class);
		ctx.stopService(inten);
	}
	
	
	public static void showStoreList(final Context ctx){
		Intent inten = new Intent(ctx, ItemListActivity.class);
		inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(inten);
	}
	
	
	public static void popApp(final Context ctx){
		Handler handler = new Handler();
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<PushInfo> list = AppListManager.getApplists(AppListManager.FLAG_APP_POP);
				//Log.d("main", "Main.list.size="+list.size()+",,,list.tostring="+list.toString());
				BaseLog.i("main", "AppListManager.getmSwitchInfo().getmPopSwitch()="+AppListManager.getmSwitchInfo().getmPopSwitch());
				if (null == list || list.size() <= 0 || AppListManager.getmSwitchInfo().getmPopSwitch() == 0) {
					AskSwitchAndAppList.askSwitchAndApp(new OnFinishListener() {
						
						@Override
						public void onFinish(int type) {
							// TODO Auto-generated method stub
							if (type == HttpUtil.KEY_POP_LIST) {
								doPushPop(ctx);
							}

						}
					}, ctx);
				}else if(AppListManager.getmSwitchInfo().getmPopSwitch() == 1){
					
					doPushPop(ctx);
				}
			}
		});

	}
	
	private static Handler handler = new Handler();
	//private static final int MSG_FIND_PUSHINFO = 1;
	static PushInfo pi = null;
	private static void doPushPop(final Context ctx){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pi = AppListManager.findPushInfo(ctx,PushType.TYPE_POP_WND);
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//Log.d("main", "Main.dopushPop");
						if(null != pi){
							BaseLog.d("main", "Main.dopushPop.pi="+pi.toString());
							PushFactory.paserPush(PushType.TYPE_POP_WND, ctx, pi);
						}
					}
				});
			}
		}) ;
		thread.start();
	}
	
}
