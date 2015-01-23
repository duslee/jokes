package com.common.as.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushInfo;
import com.common.as.service.AppFileUtils;
import com.common.as.service.AppInfoUtil;
import com.common.as.store.YMInfoStore;
import com.common.as.struct.ScaleSwitchInfo;
import com.common.as.utils.PointUtil.PointInfo;

public class AppUtil {
	
	public static ScaleSwitchInfo mScaleSwitchInfo = new ScaleSwitchInfo();
	/**
	 * 判断是否存在快捷方式
	 * */
	public static boolean hasShortcut(Activity activity, String shortcutName) {
		String url = "";
		int systemversion = Integer.parseInt(android.os.Build.VERSION.SDK);
		/* 大于8的时候在com.android.launcher2.settings 里查询（未测试） */
		if (systemversion < 8) {
			url = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			url = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		ContentResolver resolver = activity.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(url), null, "title=?",
				new String[] { shortcutName }, null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		return false;
	}
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
		return 2022;
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
    
    public static void showSetup(Context ctx,String url){
    	BaseLog.d("main2", "AppUtil.showSetup,弹出安装");
//    	PointUtil.SendPoint(ctx, new PointInfo(PointUtil.POINT_ID_SETUP, pi));
    	Intent intent = new Intent(Intent.ACTION_VIEW);
//    	String mime = PushInfo.MIME_APP;
    	String mime = PushInfo.getMIME_APP();
//    	String uri = pi.getFileUrl();
    	File file = new File(url);
    	Log.d("main", "url="+url+",,AppFileUtils.isDirExist(AppInfoUtil.down_dir)="+AppFileUtils.isDirExist(AppInfoUtil.down_dir));
    	if (AppFileUtils.isDirExist(AppInfoUtil.down_dir)) {
    		
        	intent.setDataAndType(Uri.parse("file://"+url), mime);
//    		intent.setDataAndType(Uri.fromFile(file), mime);
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
	
	public static String[] YMStrs={"http://appnew.tortodream.com/mvideo/","http://sjm.hzt88.com:8080/mvideo/","http://115.28.105.166:8080/mvideo/","http://192.168.1.118:8089/video/","http://appnew.hzt88.com/mvideo/"};
	
	public static String getYMRadom(Context context){
		ArrayList<String> strList = new ArrayList<String>();
		int index = 0;
		String ym_str="";
		YMInfoStore stores = YMInfoStore.get(Utils.getPackageName(context));
		if(stores!=null){
			if(TextUtils.isEmpty(stores.getmYmStr())){
			
			ArrayList<String> ymLists = stores.getUnUsered_ym();
			for (int i = 0; i < YMStrs.length; i++) {
				if(!ymLists.contains(YMStrs[i])){
					strList.add(YMStrs[i]);
				}
			}
			BaseLog.d("main", "strList.size()=="+strList.size());
			index	= (int)(Math.random()*strList.size());
			
			BaseLog.d("main", "random.index=="+index);
			ym_str = strList.get(index);
			
			}else{
				ym_str=stores.getmYmStr();
			}
		}else{
			index	= (int)(Math.random()*YMStrs.length);
			ym_str = YMStrs[index];
		}
		BaseLog.d("main", "getYMRadom.getYMRadom="+ym_str);
		return ym_str;
	}
	public static void saveYMRadom(Context context,String ymStr){
		YMInfoStore stores = YMInfoStore.get(Utils.getPackageName(context));
		if(stores==null){
			stores = new YMInfoStore(Utils.getPackageName(context));
		}
		stores.addYM(ymStr);
		stores.save(Utils.getPackageName(context));
	}
	
	public static void saveUnUserYM(Context context,String ymStr){
		YMInfoStore stores = YMInfoStore.get(Utils.getPackageName(context));
		if(stores==null){
			stores = new YMInfoStore(Utils.getPackageName(context));
		}
		if(!stores.getUnUsered_ym().contains(ymStr)){
			stores.getUnUsered_ym().add(ymStr);
		}
		stores.save(Utils.getPackageName(context));
	}
	public static String getResultYM(Context context){
		String ym_str="";
		YMInfoStore stores = YMInfoStore.get(Utils.getPackageName(context));
		if(stores!=null){
			ym_str = stores.getmYmStr();
		}
		BaseLog.d("main", "getResultYM.ym_str="+ym_str);
		return ym_str;
	}
}
