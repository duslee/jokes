package com.ly.duan.utils;

import java.util.List;

import com.lidroid.xutils.util.LogUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * AppUtils
 * <ul>
 * <li>{@link AppUtils#isNamedProcess(Context, String)}</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-5-07
 */
public class AppUtils {

	private AppUtils() {
		throw new AssertionError();
	}

	/**
	 * whether this process is named with processName
	 * 
	 * @param context
	 * @param processName
	 * @return <ul>
	 *         return whether this process is named with processName
	 *         <li>if context is null, return false</li>
	 *         <li>if {@link ActivityManager#getRunningAppProcesses()} is null,
	 *         return false</li>
	 *         <li>if one process of
	 *         {@link ActivityManager#getRunningAppProcesses()} is equal to
	 *         processName, return true, otherwise return false</li>
	 *         </ul>
	 */
	public static boolean isNamedProcess(Context context, String processName) {
		if (context == null) {
			return false;
		}

		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfoList = manager
				.getRunningAppProcesses();
		if (processInfoList == null) {
			return true;
		}

		for (RunningAppProcessInfo processInfo : manager
				.getRunningAppProcesses()) {
			if (processInfo.pid == pid
					&& ObjectUtils.isEquals(processName,
							processInfo.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * whether application is in background
	 * <ul>
	 * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
	 * </ul>
	 * 
	 * @param context
	 * @return if application is in background return true, otherwise return
	 *         false
	 */
	public static boolean isApplicationInBackground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty()) {
			ComponentName topActivity = taskList.get(0).topActivity;
			if (topActivity != null
					&& !topActivity.getPackageName().equals(
							context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public static int getSystemVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static boolean compareToVersion(int comVersion) {
		LogUtils.e("SDK_INT=" + android.os.Build.VERSION.SDK_INT
				+ ", comVersion=" + comVersion);
		return (android.os.Build.VERSION.SDK_INT >= comVersion) ? true : false;
	}
	
	public static int getAppVer(Context context){
		PackageManager pm = context.getPackageManager();  
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return info.versionCode;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		} 
	}

}
