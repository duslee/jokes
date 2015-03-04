package com.common.as.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushInfo;
import com.common.as.utils.PointUtil.PointInfo;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

public class AppUtil {
	public static boolean isInstalled(Context ctx,String name){
		PackageInfo packageInfo;
		BaseLog.d("main", "isInstalled.name="+name);
	//	List<PackageInfo> list = getPackageManager().getInstalledPackages(0);

	    try {
	        packageInfo = ctx.getPackageManager().getPackageInfo(
	        		name.trim(), 0);

	    } catch (NameNotFoundException e) {
	        packageInfo = null;
	        e.printStackTrace();
	    }
	    if(packageInfo ==null){
	    	BaseLog.d("main", "isInstalled.packageInfo==null");
	        return false;
	    }else{
	    	BaseLog.d("main", "isInstalled.packageInfo="+packageInfo.toString());
	        return true;
	    }
	}
	
    public static Intent getLaunchIntentForPackage(Context ctx,String pkg)
    {
        try
        {
            return ctx.getPackageManager()
                .getLaunchIntentForPackage(pkg);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    public static Intent getSetUpIntentForPackage(Context ctx,String pkg,PushInfo pi)
    {
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW);
//    	String mime = PushInfo.MIME_APP;
    	String mime = PushInfo.getMIME_APP();
    	String uri = pi.getFileUrl();
    	BaseLog.d("main2","AppUtil.getSetUpIntentForPackage="+ pi);
    	BaseLog.d("main2", "AppUtil.uri="+uri);
    	if(null == uri){
    		return null;
    	}else{
    		File file = new File(uri);
    		if (file.exists()) {
    			intent.setDataAndType(Uri.parse("file://"+uri), mime);
    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
    					| Intent.FLAG_GRANT_READ_URI_PERMISSION);
    			return intent;
    		}
    		
    		return null;
    	}
        
    }
    public static int getPushVer() {
		return 2018;
	}
    public static boolean startApp(Context ctx,String pkg)
    {
        Intent intent = getLaunchIntentForPackage(ctx,pkg);
        if (null == intent)
        {
            return false;
        }
        // intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
        	ctx.startActivity(intent);
        }
        catch (Exception e)
        {
            List<ResolveInfo> tApps = new ArrayList<ResolveInfo>();
            Intent temp = new Intent(Intent.ACTION_MAIN);
            temp.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pManager =
            		ctx.getPackageManager();
            tApps =
                pManager.queryIntentActivities(temp,
                    PackageManager.GET_ACTIVITIES);
            for (ResolveInfo resoInfo : tApps)
            {
                if (resoInfo.activityInfo.packageName.equals(pkg))
                {
                    ComponentName cn =
                        new ComponentName(resoInfo.activityInfo.packageName,
                            resoInfo.activityInfo.name);
                    Intent start = new Intent();
                    start.setComponent(cn);
                    start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try
                    {
                    	ctx.startActivity(start);
                    }
                    catch (Exception e1)
                    {
                      //  ToastUtil.showToast(R.string.start_failure);
                        return false;
                    }

                    return true;
                }

            }
            return false;
        }
        return true;
    }
    
    
    public static void showSetup(Context ctx,PushInfo pi){
    	BaseLog.d("main2", "AppUtil.showSetup,弹出安装");
    	PointUtil.SendPoint(ctx, new PointInfo(PointUtil.POINT_ID_SETUP, pi));
    	Intent intent = new Intent(Intent.ACTION_VIEW);
//    	String mime = PushInfo.MIME_APP;
    	String mime = PushInfo.getMIME_APP();
    	String uri = pi.getFileUrl();
    	
    	File file = new File(uri);
    	if (file.exists()) {
        	intent.setDataAndType(Uri.parse("file://"+uri), mime);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        		| Intent.FLAG_GRANT_READ_URI_PERMISSION);
        	try {
        		ctx.startActivity(intent);
        	} catch (ActivityNotFoundException ex) {
//        	    Toast.makeText(ctx, R.string.download_no_application_title,
//        		    Toast.LENGTH_LONG).show();
        		ex.printStackTrace();
        	}
		}

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
