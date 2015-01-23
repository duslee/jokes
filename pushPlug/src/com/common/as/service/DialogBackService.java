package com.common.as.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
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
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.main.Main;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushInfoActionPaser;
import com.common.as.pushtype.PushTopWnd;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppListUtils;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.utils.Utils;
import com.common.as.view.DailogView;
import com.common.as.view.TableView;
import com.common.as.view.DailogView.OnCancelBtnClick;
import com.common.as.view.DailogView.OnDownLoadBtnClick;
import com.common.as.view.TableView.OnTopViewClick;
import com.example.pushplug.R;

public class DialogBackService extends Service {
	//
	private static Intent mIntent;
	static private DailogView dialogView;
	private static final String TAG = "DialogBackService";
	private static final int HANDLE_CHECK_ACTIVITY = 1;
	public static boolean isRunning = false;
	static private PushInfo pi;
	static String packageName = "";
	static Bitmap bitmap;
	static Context context;
	private static boolean isCanShow = true;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECK_ACTIVITY:
//				 BaseLog.d("main4", "isHome=" +
//				 isHome()+",isBacgroung="+Utils.isApplicationBroughtToBackground(context));
				if (Utils.isApplicationBroughtToBackground(context)) {
					removeTopView();
				} else{
					if(isCanShow){
						if(!Utils.isApplicationBroughtToBackground(context)){
							showAutoTopView();
						}
					}
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
		context = DialogBackService.this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (null == intent) {
			return super.onStartCommand(intent, flags, startId);
		}
			pi = (PushInfo) intent.getSerializableExtra("pi");
			bitmap = intent.getParcelableExtra("iconBmp");
			mIntent = intent;
			mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
			isRunning = true;
			if (!Utils.isApplicationBroughtToBackground(context)) {
				showAutoTopView();
			}
			BaseLog.i("main", "DialogBackService.onStartCommand");
			
		return super.onStartCommand(intent, flags, startId);
	}

	public static void showAutoTopView() {
		BaseLog.i("main", "DialogBackService.showTopView");
		if(null == PushInfos.getInstance().get(pi.packageName)){
			return;
		}else{
			packageName = pi.getPackageName();
			if(null == dialogView){
				dialogView = new DailogView(context, new OnDownLoadBtnClick() {
					
					@Override
					public void onClick1() {
						pi.setPushType(PushType.TYPE_POP_WND);
//						BaseLog.d("SystemReceiver", "DialogBackService.pi="+pi.toString());
						PushInfos.getInstance().put(pi.getPackageName(), pi);
						PushInfoActionPaser.doClick(context, PushType.TYPE_POP_WND, packageName);
						//dialogView.removeTipView();
						context.stopService(mIntent);
					}
				}, new OnCancelBtnClick() {
					
					@Override
					public void onClick2() {
						//dialogView.removeTipView();
						context.stopService(mIntent);
					}
				});
			}
			dialogView.setPushInfo(pi);
			dialogView.setIcBmp(bitmap);
			dialogView.fun();
			isCanShow = false;
		}
		

	}
	private void removeTopView() {
		if (dialogView != null) {
			dialogView.removeTipView();
			dialogView = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		return ret;
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
//			BaseLog.d("main4", "topActivity="+topActivity.getClassName());
//			if (!topActivity.getPackageName().equals(getPackageName())||topActivity.getClassName().equals("com.common.as.activity.TPActivity")) {
//				return true;
//			}
//		}
//		return false;
//	}
}