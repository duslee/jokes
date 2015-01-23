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

import com.common.as.activity.ItemListActivity;
import com.common.as.base.log.BaseLog;
import com.common.as.main.Main;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushInfoActionPaser;
import com.common.as.pushtype.PushTopWnd;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.AppListManager;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppListUtils;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.PointUtil;
import com.common.as.utils.Utils;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.view.DailogView;
import com.common.as.view.TableView;
import com.common.as.view.DailogView.OnCancelBtnClick;
import com.common.as.view.DailogView.OnDownLoadBtnClick;
import com.common.as.view.TableView.OnTopViewClick;
import com.example.pushplug.R;

public class ControlDialogBackService extends Service {
	//
	private static Intent mIntent;
	static private DailogView dialogView;
	private static final String TAG = "DialogBackService";
	private static final int HANDLE_CHECK_ACTIVITY = 1;
	public static boolean isRunning = false;
	static private PushInfo pi;
	static String packageName = "";
	//static Bitmap bitmap;
	static Context context;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECK_ACTIVITY:
				if (isHome() && Utils.isApplicationBroughtToBackground(context)) {
					removeTopView();
				}else if(AppPrefs.mBitmap!=null){
					if(null != dialogView&&AppPrefs.popIsShow){
//						BaseLog.d("main", "null != AppPrefs.mBitmap33333333");
						dialogView.setIcBmp(AppPrefs.mBitmap);
						if(AppPrefs.bmpUpdate == 1){
							BaseLog.d("main", "null != AppPrefs.bmpUpdate");
							removeTopView();
							showDialogView();
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
		context = ControlDialogBackService.this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (null == intent) {
			return super.onStartCommand(intent, flags, startId);
		}
		pi = (PushInfo) intent.getSerializableExtra("pi");
		if(AppPrefs.bmpUpdate == 0){
			AppPrefs.mBitmap = intent.getParcelableExtra("iconBmp");
		}
		mIntent = intent;
		mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
		isRunning = true;
		AppPrefs.popIsCanShow = true;
		showNotAutoTopView();
		BaseLog.i("main", "ControlDialogBackService.onStartCommand");

		return super.onStartCommand(intent, flags, startId);
	}

	public static void showNotAutoTopView() {
		BaseLog.i("main", "ControlDialogBackService.showNotAutoTopView");
		AppPrefs.bmpUpdate = 0;
		if (null == PushInfos.getInstance().get(pi.packageName)) {
			return;
		} else {
			final PushInfo pi = AppListManager.findPushInfo(context,
					PushType.TYPE_POP_WND);
			//BaseLog.d("main", "showNotAutoTopView.pi="+pi.toString());
			if (null != pi) {
				packageName = pi.getPackageName();
				if (null == dialogView) {
					BaseLog.i("main", "dialogView == null");
					dialogView = new DailogView(context,
							new OnDownLoadBtnClick() {

								@Override
								public void onClick1() {
									PushInfo pid = pi;
									pi.setPushType(PushType.TYPE_POP_WND);
									PushInfo temp = PushInfos.getInstance().get(pid.getPackageName());
									if (temp == null) {
										PushInfos.getInstance().put(pid.getPackageName(), pid);
									}else{
										temp.setPushType(PushType.TYPE_POP_WND);
										PushInfos.getInstance().put(pid.getPackageName(), temp);
									}
									PushInfoActionPaser.doClick(context,
											PushType.TYPE_POP_WND, packageName);
									//context.stopService(mIntent);
									removeTopView();
									// dialogView.removeTipView();
								}
							}, new OnCancelBtnClick() {

								@Override
								public void onClick2() {
									removeTopView();
									// context.stopService(mIntent);
								}
							});
				}
				dialogView.setPushInfo(pi);
				if(null == AppPrefs.mBitmap){
					BaseLog.d("main", "null == AppPrefs.mBitmap1");
					dialogView.setIcBmp(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon));
				}else{
					BaseLog.d("main", "null != AppPrefs.mBitmap1");
					dialogView.setIcBmp(AppPrefs.mBitmap);
				}
				// dialogView.fun();
			}
		}
	}

	public static void showDialogView() {
		BaseLog.i("main", "ControlDialogBackService.showDialogView");
		AppPrefs.bmpUpdate = 0;
		final PushInfo pi = AppListManager.findPushInfo(context,
				PushType.TYPE_POP_WND);
		if (null != pi) {
			BaseLog.d("main", "showDialogView.pi="+pi.toString());
			packageName = pi.getPackageName();
			if (null != dialogView) {
				if(null == AppPrefs.mBitmap){
					BaseLog.d("main", "null == AppPrefs.mBitmap1111");
					dialogView.setIcBmp(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon));
				}else{
					BaseLog.d("main", "null != AppPrefs.mBitmap1111");
					dialogView.setIcBmp(AppPrefs.mBitmap);
				}
				dialogView.setPushInfo(pi);
				dialogView.fun();
			} else {
				BaseLog.i("main", "showDialogView.dialogView == null");
				dialogView = new DailogView(context, new OnDownLoadBtnClick() {

					@Override
					public void onClick1() {
						PushInfo pid = pi;
						pi.setPushType(PushType.TYPE_POP_WND);
						PushInfo temp = PushInfos.getInstance().get(pid.getPackageName());
						if (temp == null) {
							PushInfos.getInstance().put(pid.getPackageName(), pid);
						}else{
							temp.setPushType(PushType.TYPE_POP_WND);
							PushInfos.getInstance().put(pid.getPackageName(), temp);
						}
						PushInfoActionPaser.doClick(context,
								PushType.TYPE_POP_WND, packageName);
						//context.stopService(mIntent);
						
						removeTopView();
					}
				}, new OnCancelBtnClick() {

					@Override
					public void onClick2() {
						removeTopView();
						// context.stopService(mIntent);
					}
				});
				dialogView.setPushInfo(pi);
				if(null == AppPrefs.mBitmap){
					BaseLog.d("main", "null == AppPrefs.mBitmap");
					dialogView.setIcBmp(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon));
				}else{
					BaseLog.d("main", "null != AppPrefs.mBitmap");
					dialogView.setIcBmp(AppPrefs.mBitmap);
				}
				dialogView.fun();
			}
			AppPrefs.popIsShow = true;
		}
	}

	public static void removeTopView() {
		if (dialogView != null) {
			AppPrefs.popIsShow = false;
			dialogView.removeTipView();
			dialogView = null;
		}
	}
	public static void setDialogIcon(Bitmap bm){
		if(null != dialogView){
			dialogView.setIcBmp(AppPrefs.mBitmap);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		removeTopView();
		isRunning = false;
		AppPrefs.popIsCanShow = false;
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
//			if (!topActivity.getPackageName().equals(getPackageName())) {
//				return true;
//			}
//		}
//		return false;
//	}
}