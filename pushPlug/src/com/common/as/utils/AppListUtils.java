package com.common.as.utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.common.as.base.log.BaseLog;
import com.common.as.network.AskSwitchAndAppList;
import com.common.as.network.AskSwitchAndAppList.OnFinishListener;
import com.common.as.network.HttpUtil;
import com.common.as.pushtype.AppListFactory;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.AppListBackService;
import com.common.as.service.ControlDialogBackService;
import com.common.as.store.AppListManager;
import com.common.as.view.TableView;

public class AppListUtils {

	private static AppListUtils utils;

	private static TableView mTableView;

	private static Context context;
	static SharedPreferences sp;

	// private static boolean isShow = false;
	public static AppListUtils getInstance(final Context context) {
		if (utils == null) {
			utils = new AppListUtils(context);
		}
		return utils;
	}

	public AppListUtils(final Context context) {
		BaseLog.d("main", "AppListUtil"+",,,Config.DEBUG="+com.common.as.base.log.Config.Debug);
		this.context = context;
		sp = context.getSharedPreferences(AppPrefs.APP_INFO,
				Context.MODE_PRIVATE);
	}

	public static void SetApplistControlSwitch(boolean bAuto) {
		sp.edit().putBoolean(AppPrefs.CONTROL_SWITCH, bAuto).commit();
	}

	public static void getSwitchAndAppList() {
		BaseLog.d("main", "ShowAppList.AppPref.isEnable="+AppPrefs.isEnable);
		Handler handler = new Handler();
		//if(!AppListBackService.isRunning){
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ArrayList<PushInfo> list = AppListManager
							.getApplists(AppListManager.FLAG_APP_LIST);
					BaseLog.i("main", "getmListSwitch()="+AppListManager.getmSwitchInfo().getmListSwitch());
					if (null == list
							|| list.size() <= 0
							|| AppListManager.getmSwitchInfo().getmListSwitch() == 0) {
						AskSwitchAndAppList.askSwitchAndApp(new OnFinishListener() {
							
							@Override
							public void onFinish(int type) {
								// TODO Auto-generated method stub
								if (type == HttpUtil.KEY_STORE_LIST) {
									doPushList(context, mTableView);
								}
								
							}
						}, context);
					} else if (AppListManager.getmSwitchInfo().getmListSwitch() == 1) {
						doPushList(context, mTableView);
					}
				}
			});
	//	}

	}

	private static Handler handler = new Handler();

	private static void doPushList(final Context ctx, final TableView mTableView) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final PushInfo pi = AppListManager.findPushInfo(ctx,
						PushType.TYPE_STORE_LIST);
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						AppListFactory.paserPush(PushType.TYPE_STORE_LIST, ctx,
								pi, mTableView);
					}
				});
			}
		});
		thread.start();
	}

	public static void HideApplist() {
		if(AppPrefs.listIsShow){
			AppListBackService.removeTopView();
		}
	}

	public static boolean ShowAppList() {
		BaseLog.d("main", "ShowView.AppPref.isEnable="+AppPrefs.isEnable);
		if(AppPrefs.isEnable&&!AppPrefs.listIsShow){
			AppListBackService.showTabView();
		}
		return AppPrefs.listIsShow;
	}
	public static boolean ShowPop() {
		if(AppPrefs.popIsCanShow&&!AppPrefs.popIsShow){
			
			ControlDialogBackService.showDialogView();
		}
		return AppPrefs.popIsShow;
	}
	public static void HidePop() {
		if(AppPrefs.popIsShow){
			ControlDialogBackService.removeTopView();
		}
	}
	public static void SetApplistEntryPosition(int x, int y) {
		BaseLog.d("main", "x=" + x + ",,y=" + y);
		sp.edit().putInt(AppPrefs.APPLIST_POSITION_X, x).commit();
		sp.edit().putInt(AppPrefs.APPLIST_POSITION_Y, y).commit();
	}
	
	public static void SetApplistEntryPosition(int x, int y,int Width,int Heighth) {
		BaseLog.d("main", "x=" + x + ",,y=" + y);
		sp.edit().putInt(AppPrefs.APPLIST_POSITION_X, x).commit();
		sp.edit().putInt(AppPrefs.APPLIST_POSITION_Y, y).commit();
		sp.edit().putInt(AppPrefs.APPLIST_ICON_WIDTH, Width).commit();
		sp.edit().putInt(AppPrefs.APPLIST_ICON_HEIGHTH, Heighth).commit();
	}

	public static void SetApplistEntryCanBeMoved(boolean isCanMove) {
		// SharedPreferences sp =
		// context.getSharedPreferences(AppPrefs.APP_INFO,
		// Context.MODE_PRIVATE);
		sp.edit().putBoolean(AppPrefs.APPLIST_CAN_MOVE, isCanMove).commit();
	}

	public static void SetApplistEntryIcon(int id) {
		// SharedPreferences sp =
		// context.getSharedPreferences(AppPrefs.APP_INFO,
		// Context.MODE_PRIVATE);
		sp.edit().putInt(AppPrefs.APPLIST_ICON, id).commit();
	}
}
