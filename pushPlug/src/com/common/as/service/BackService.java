package com.common.as.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.common.as.base.log.BaseLog;
import com.common.as.main.Main;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushInfoActionPaser;
import com.common.as.pushtype.PushTopWnd;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.utils.Utils;
import com.common.as.view.TableView;
import com.common.as.view.TableView.OnTopViewClick;
import com.example.pushplug.R;

public class BackService extends Service {
	//
	private Intent mIntent;
	private static TableView tabView;
	private static final String TAG = "BackService";
	public static final String TAG_TOP_WND_TYPE = "bg_type";
	public static final String TAG_PACKAGE_NAME = "package";
	public static final int TYPE_BG_PUSH = 1;
	public static final int TYPE_BG_STORELIST = 2;
	private int mType = TYPE_BG_STORELIST;
	private static final int HANDLE_CHECK_ACTIVITY = 1;
	public static boolean isRunning = false;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECK_ACTIVITY:
				// Log.d("main", "isHome=" +
				// isHome()+",isBacgroung="+isApplicationBroughtToBackground());
				if (!isHome() && !Utils.isApplicationBroughtToBackground(BackService.this)&&!AppPrefs.isListActivity) {
					showTopView();
				} else {
					removeTopView();
				}
				mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
		homeList = getHomes();
		BaseLog.d("main", "BackService.onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (null == intent) {
			return super.onStartCommand(intent, flags, startId);
		}

		mType = intent.getIntExtra(TAG_TOP_WND_TYPE, TYPE_BG_PUSH);

		mIntent = intent;
		BaseLog.d("main", "BackService.onStartCommand");
		if (mType == TYPE_BG_PUSH) {
			String name = intent.getStringExtra(PushTopWnd.INTENT_PACKAGE_NAME);
			Bitmap bmp = intent.getParcelableExtra(PushTopWnd.INTENT_ICON_BMP);
			tabView = new TableView(this, new OnTopViewClick() {

				@Override
				public void onClick(Object userData) {
					// TODO Auto-generated method stub
					String name = (String) userData;
					PushInfoActionPaser.doClick(BackService.this,
							PushType.TYPE_TOP_WND, name);
					stopService(mIntent);
				}
			}, name);
			tabView.setIcBmp(bmp);
			tabView.fun();
			isRunning = true;
		} else {
			// if (!isHome())
			// {
			// showTopView();
			// }
			mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
			isRunning = true;
			return START_REDELIVER_INTENT;
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void sendStartPushPoint() {
		PushInfo pitemp = new PushInfo("", "0", "0");
		pitemp.setPushType(PushType.TYPE_STORE_LIST);
		PointUtil.SendPoint(this, new PointInfo(PointUtil.POINT_ID_START_PUSH,
				pitemp));

	}

	private void showTopView() {
		// Log.d("main", "BackService.showTopView");
		if (null == tabView) {
			if (isRunning) {
				sendStartPushPoint();
			}
			SharedPreferences sp = getSharedPreferences(AppPrefs.APP_INFO, BackService.this.MODE_PRIVATE);
				tabView = new TableView(this, new OnTopViewClick() {
					
					@Override
					public void onClick(Object userData) {
						// TODO Auto-generated method stub
						Main.showStoreList(BackService.this);
					}
				}, "");
				tabView.setIcBmp(BitmapFactory.decodeResource(getResources(),
						R.drawable.push_list_icon));
				tabView.fun();
				//sp.edit().putBoolean(AppPrefs.APPLIST_ISSHOW, true).commit();
		}

	}

	public static void removeTopView() {
		if (tabView != null) {
			tabView.removeTipView();
			tabView = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// // mLoadAction.removeNotifer(this);
		// // 进程被关闭后能够自动启动
		// Intent serviceIntent = new Intent(this, BackService.class);
		// serviceIntent.putExtra(TAG_TOP_WND_TYPE, mType);
		// serviceIntent.putExtra("caller", "LifeService");
		// this.startService(serviceIntent);
		removeTopView();
		isRunning = false;

	}

	/**
	 * 获得属于桌面的应用的应用包名称
	 * 
	 * @return 返回包含所有包名的字符串列表
	 */
	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		// 属性
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

	private List<String> homeList; // 桌面应用程序包名列表
	ActivityManager mActivityManager;

	/**
	 * 判断当前界面是否是桌面
	 */
	public boolean isHome() {
		boolean ret = !isOpen(getPackageName());
		// BaseLog.d(TAG, "isHome="+ret);
		return ret;
		// if (mActivityManager == null)
		// {
		// mActivityManager = (ActivityManager)
		// getSystemService(Context.ACTIVITY_SERVICE);
		// }
		// List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		// return homeList.contains(rti.get(0).topActivity.getPackageName());
	}

	public boolean isOpen(String packageName) {
		if (packageName.equals("") | packageName == null)
			return false;
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		}
		List<RunningAppProcessInfo> runningAppProcesses = mActivityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runinfo : runningAppProcesses) {
			String pn = runinfo.processName;
			if (pn.equals(packageName)
					&& runinfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
				return true;
		}
		return false;
	}

	/**
	 * 判断当前应用程序处于前台还是后台
	 * 
	 * @param context
	 * 
	 * @return
	 */
//	public boolean isApplicationBroughtToBackground() {
//		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
//		if (!tasks.isEmpty()) {
//			ComponentName topActivity = tasks.get(0).topActivity;
//			if (!topActivity.getPackageName().equals(getPackageName())||topActivity.getClassName().equals("com.common.as.activity.TPActivity")) {
//				return true;
//			}
//		}
//		return false;
//	}
}