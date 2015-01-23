package com.common.as.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.common.as.base.log.BaseLog;

public class Utils {
	private static String TAG = "Utils";
	private static String KEY_CPUV_FIRST = "CPU architecture";
	private static String KEY_CPUV_SECOND = "Processor";
	public static int CPU_VERSION_DEFAULT = 4; 
	public static int CPU_VERSION_SUPPORT = 5;
	
	public enum SIM_TYPE{
		MOBILE,//移动
		LINTONE,//联通
		TELECON,//电信
		UN_KNOW
	}
	
	

	/**
	 * the pattern eg: CPU architecture: 5TE if exists, return the version ,else
	 * return -1;
	 * 
	 * @return
	 */
	private static int getCpuVersionFirst(String ver_str) {
		if (ver_str.trim().equals("")) {
			return -1;
		}
		String[] spilt_str = ver_str.split(":");
		if (spilt_str.length <= 1) {
			return -1;
		}
		String v = spilt_str[1].trim();
		int start_index = -1;
		for (int i = 0; i < v.length(); i++) {
			if (Character.isDigit(v.charAt(i))) {
				start_index = i;
				break;
			}
		}
		if (start_index == -1) {
			return -1;
		}
		String version_str = v.substring(start_index, start_index + 1);
		int version = Integer.valueOf(version_str);
		return version;
	}

	/***
	 * the pattern eg: Processor : Marvell 88SV331x rev 0 (v5l) if exists,
	 * return the version ,else return -1;
	 * 
	 * @param ver_str
	 * @return
	 */
	private static int getCputVersionSecond(String ver_str) {
		if (ver_str.trim().equals("")) {
		//	BaseLog.d("cpuversion", "second-----ver_str equals \"\" ,return  -1");
			return -1;
		}
		String[] spilt_str = ver_str.split(":");
		if (spilt_str.length <= 1) {
		//	BaseLog.d("cpuversion", "second-----the spilt_str <-1 ,return  -1");
			return -1;
		}
		String v = spilt_str[1].trim();
		int start_index = v.lastIndexOf("(");
		int end_index = v.lastIndexOf(")");
		if (start_index == -1 || end_index == -1 || start_index > end_index) {
			BaseLog.d("cpuversion", "second---the (***) is not exist,return -1");
			return -1;
		}
		v = v.substring(start_index + 1, end_index);
		start_index = -1;
		for (int i = 0; i < v.length(); i++) {
			if (Character.isDigit(v.charAt(i))) {
				start_index = i;
				BaseLog.d("cpuversion", "second-----get the start index:"
						+ start_index);
				break;
			}
		}
		if (start_index == -1) {
			BaseLog.d("cpuversion", "second-----the start_index==-1,return -1");
			return -1;
		}

		String version_str = v.substring(start_index, start_index + 1);
		int version = Integer.valueOf(version_str);
		BaseLog.d("cpuversion", "second-----return version:" + version);
		return version;
	}

	public static int getCpuVersion() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" };
		BufferedReader localBufferedReader = null;
		boolean bGoto = false;
		try {
			FileReader fr = new FileReader(str1);
			localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			while (str2 != null) {
				if (str2.startsWith(KEY_CPUV_FIRST)) {
					cpuInfo[0] = str2;
					BaseLog.d(TAG, "cpuInfo[0] = " + cpuInfo[0]);
				} else if (str2.startsWith(KEY_CPUV_SECOND)) {
					cpuInfo[1] = str2;
					BaseLog.d(TAG, "cpuInfo[1] = " + cpuInfo[1]);
				}
				str2 = localBufferedReader.readLine();
			}
		} catch (IOException e) {
			BaseLog.d(TAG, "return the default cpu version 5");
			bGoto = true;
			
		} finally {
			if (localBufferedReader != null) {
				try {
					localBufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (bGoto) {
			return CPU_VERSION_DEFAULT;
		}

		int v = getCputVersionSecond(cpuInfo[1]);
		if (v == -1) {
			v = getCpuVersionFirst(cpuInfo[0]);
		}
		if (v == -1) {
			return CPU_VERSION_DEFAULT;
		}
		return v;
	}
	
	public static boolean isWifiActive(Context context){   
		ConnectivityManager connectivity = (ConnectivityManager)     
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) {    
			NetworkInfo[] info = connectivity.getAllNetworkInfo();    
			if (info != null) {    
				for (int i = 0; i < info.length; i++) {    
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {    
						return true;    
					}    
				}    
			}    
		}    
		
		return false;   
	}
	
	public static int getAndroidOSVersion()  
    {  
         int osVersion;  
         try  
         {  
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);  
         }  
         catch (NumberFormatException e)  
         {  
            osVersion = 0;  
         }  
           
         return osVersion;  
   } 
	
	public static String getImsi(Context context){
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId(); 
		return imsi;
	}
	
	/**
	 * 获取网络类型 -1 没有网络 0: 2G 1: 3G 2: WIFI
	 * 
	 * @return
	 */
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 0;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 1;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 2;

	public static int getNetType(Context context) {
		int mNetWorkType = -1;
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();

			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				String proxyHost = android.net.Proxy.getDefaultHost();

				mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G
						: NETWORKTYPE_2G)
						: -1;
			}
		} else {
			mNetWorkType = -1;
		}

		return mNetWorkType;
	}
	//判断是否是FastMobileNetWork，将3G或者3G以上的网络称为快速网络
	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true; // ~ 10+ Mbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}
	public static int getScreenWidth(WindowManager manager){
		//WindowManager manager = getWindowManager();
		if(null == manager){
			return 0;
		}else{
			Display mDisplay = manager.getDefaultDisplay();
			int W = mDisplay.getWidth();
			return W;
		}
	}
	public static int getScreenHeight(WindowManager manager){
		if(null == manager){
			return 0;
		}else{
			Display mDisplay = manager.getDefaultDisplay();
			int H = mDisplay.getHeight();
			return H;
		}
	} 
	public static SIM_TYPE getSimType(Context context){
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId(); 
		if (null == imsi) {
			return SIM_TYPE.UN_KNOW;
		}
		 if(imsi.startsWith("46000") ||imsi.startsWith("46002") || imsi.startsWith("46007")){
				//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号    
				//中国移动   
				 return SIM_TYPE.MOBILE;
			}else if(imsi.startsWith("46001")|| imsi.startsWith("46006")){
			    //中国联通    
			   return SIM_TYPE.LINTONE;
			}else if(imsi.startsWith("46003") ||imsi.startsWith("46005")){
			//中国电信    
				return SIM_TYPE.TELECON;
			}
	   return SIM_TYPE.MOBILE;
	}
	public static int getProvideType(Context context){
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId(); 
		if (null == imsi) {
			return getProviders(context);
		}
		 if(imsi.startsWith("46000") ||imsi.startsWith("46002") || imsi.startsWith("46007")){
				//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号    
				//中国移动   
				 return 0;
			}else if(imsi.startsWith("46001")|| imsi.startsWith("46006")){
			    //中国联通    
			   return 1;
			}else if(imsi.startsWith("46003") ||imsi.startsWith("46005")){
			//中国电信    
				return 2;
			}
	   return getProviders(context);
	}
	private static int getProviders(Context context) {  
        NetWorkUtil nwu = new NetWorkUtil(context);  
        String net = nwu.getNetWork();  
        List<String> infos = nwu.getNetWorkList();
        int provide = 0;
        if (net == null || net.equals("WIFI")) {  
            if (infos.size() > 1) {  
                infos.remove("WIFI");  
                net = infos.get(0);  
                if (net.equals("3gwap") || net.equals("uniwap")  
                        || net.equals("3gnet") || net.equals("uninet")) {  
                	provide = 1;  //联通
                } else if (net.equals("cmnet") || net.equals("cmwap")) {  
                	provide = 0;  //移动
                } else if (net.equals("ctnet") || net.equals("ctwap")) {  
                	provide = 2;  //电信
                }else{
                	provide = 3; 
                }
            } else {  
            	provide = 3; 
            }  
        } else {  
            if (net.equals("3gwap") || net.equals("uniwap")  
                    || net.equals("3gnet") || net.equals("uninet")) {  
            	provide = 1;  //联通
            } else if (net.equals("cmnet") || net.equals("cmwap")) {  
            	provide = 0;  //移动  
            } else if (net.equals("ctnet") || net.equals("ctwap")) {  
            	provide = 2;  //电信 
            }else{
            	provide = 3;
            }
        }
        return provide;
    }  
	public static String getTelNum(Context context){
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String tel = mTelephonyMgr.getLine1Number();
		return tel == null?"":tel;
	}
	
	private static String mSmsNumber = null; 
	public static String getSms(Context context){
		
		if (null != mSmsNumber) {
			return mSmsNumber;
		}
        SmsUtil sms=SmsUtil.getInstance(context);
        mSmsNumber=sms.getSmsCenter();
        if(mSmsNumber==null || mSmsNumber.length()<1){
        	mSmsNumber = null;
        	return "";
        }       	
        else
        	return mSmsNumber;

	}
	
	public static String getIMEI(Context context){
		TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI=null;
		if(telMgr!=null){
			IMEI = telMgr.getDeviceId();
		}
		return IMEI;
	}
	
	/**
	 * @author spring sky
	 * Email vipa1888@163.com
	 * QQ:840950105   My name :石明政
	 * 获取当前的网络状态  -1：没有网络  1：WIFI网络 2：wap网络 3：net网络
	 * @param context
	 * @return
	 */
	
	public static int CMNET = 3;
	public static int CMWAP = 2;
	public static int WIFI = 1;
	public static int getAPNType(Context context){
    	int netType = -1; 
    	ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	
    	if(networkInfo==null){
    		return netType;
   	    }
    	int nType = networkInfo.getType();
    	if(nType==ConnectivityManager.TYPE_MOBILE){
    		BaseLog.d("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is "+networkInfo.getExtraInfo());
    		if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet") ||
    				networkInfo.getExtraInfo().toLowerCase().equals("ctnet")||
    				networkInfo.getExtraInfo().toLowerCase().equals("3gnet")||
    				networkInfo.getExtraInfo().toLowerCase().equals("uninet")){
    			netType = CMNET;
    		}
    		else{
    			netType = CMWAP;
    		}
    	}
    	else if(nType==ConnectivityManager.TYPE_WIFI){
    		netType = WIFI;
    	}
	    return netType;
    }
	
	public static DisplayMetrics getDisplayMetrics(Context context){
	      DisplayMetrics metric = new DisplayMetrics();
	      WindowManager windowManager = (WindowManager)context.getSystemService(Service.WINDOW_SERVICE);
	      windowManager.getDefaultDisplay().getMetrics(metric);
	      return metric;
	}
	
	//获取泰酷自己渠道号
	public static String TAIKUCHANNELDEFAULT = "8_tkkf0001_";
	public static String TAIKUCHANNELFILE="TK_ChannelID";
	public static String readTaiKuChannelId(Context context){
		InputStream is = null;
		byte[] asset = null;
		String channel = TAIKUCHANNELDEFAULT;
		try {
			is = context.getAssets().open(TAIKUCHANNELFILE);
			if (is.available() == 0) {
				return null;
			}
			asset = new byte[is.available()];
			is.read(asset);
			channel = new String(asset);
		} catch (Exception e) {
			e.printStackTrace();
			channel = TAIKUCHANNELDEFAULT;
		}

		return channel;
	}
	
	//获取渠道号
	public static String readChannelId(Context context){
		InputStream is = null;
		byte[] asset = null;
		String skymobi_a_str = null;
		try {
			is = context.getAssets().open("skymobi_a");
			if (is.available() == 0) {
				return null;
			}
			asset = new byte[is.available()];
			is.read(asset);
			skymobi_a_str = new String(asset);
			String skymobi_a_type = skymobi_a_str.subSequence(0,
					skymobi_a_str.indexOf("_")).toString();
			if (skymobi_a_type.equals("1")) {
				String skymobi_a = getSkyMobiA();
				if (null == skymobi_a || skymobi_a.equals("")) {
					return null;
				}
				String mobi_project = getSkyMobiProject();
				if (null == mobi_project || mobi_project.equals("")) {
					skymobi_a_str = "1_" + skymobi_a + "_";
				} else {
					skymobi_a_str = "1_" + skymobi_a + "_!" + mobi_project + "_";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "undefined";
		}

		return skymobi_a_str;
	}
	
	private static String getSkyMobiA() {
		try {
			Field field = Build.class.getField("SKYMOBI_A");
			String skyMobiA = (String) field.get(null);
			return skyMobiA;
		} catch (Exception e) {
			return null;
		}
	}

	private  static String getSkyMobiProject() {
		try {
			Field field = Build.class.getField("SKYMOBI_PROJECT");
			String skyMobiProject = (String) field.get(null);
			return skyMobiProject;
		} catch (Exception e) {
			return null;
		}
	}
	
//	public static String GetHostIp() {
//	    try {
//	        for (Enumeration<NetworkInterface> en = NetworkInterface
//	                .getNetworkInterfaces(); en.hasMoreElements();) {
//	            NetworkInterface intf = en.nextElement();
//	            for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
//	                    .hasMoreElements();) {
//	                InetAddress inetAddress = ipAddr.nextElement();
//	                if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) {
//	                    return inetAddress.getHostAddress();
//	                }
//	            }
//	        }
//	    } catch (SocketException ex) {
//	    } catch (Exception e) {
//	    }
//	    return null;
//	}
//
//
//	//获取本地IP
//    public static String getLocalIpAddress() {  
//           try {  
//               for (Enumeration<NetworkInterface> en = NetworkInterface  
//                               .getNetworkInterfaces(); en.hasMoreElements();) {  
//                           NetworkInterface intf = en.nextElement();  
//                          for (Enumeration<InetAddress> enumIpAddr = intf  
//                                   .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
//                               InetAddress inetAddress = enumIpAddr.nextElement();  
//                               if (!inetAddress.isLoopbackAddress()) {  
//                               return inetAddress.getHostAddress().toString();  
//                               }  
//                          }  
//                       }  
//                   } catch (SocketException ex) {  
//                       BaseLog.d("WifiPreference IpAddress", ex.toString());  
//                   }  
//           
//           
//                return null;  
//   }
    
    
	public static String getFromAssets(Context context, String fileName) {
		String result = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String getFromAssets1(Context context, String fileName) {
		String channel = null;
			
			try {
				AssetManager assetManager = context.getAssets();
				InputStream in = assetManager.open("ZYF_ChannelID");

				int length= in.available();
				byte[] buffer = new byte[length];
				in.read(buffer);
				channel = EncodingUtils.getString(buffer, "UTF-8");
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return channel;
		}
	/**
	 * 判断当前界面是否是桌面
	 */
	public static boolean isHome(Context context,ActivityManager mActivityManager)
	{
		boolean ret = !isOpen(mActivityManager,context.getPackageName(),context);
		//BaseLog.d(TAG, "isHome="+ret);
		return ret;
	}
	
	
	public static boolean isOpen(ActivityManager mActivityManager,String packageName,Context context) {
		if (packageName.equals("") | packageName == null)
		return false;
		
		if (mActivityManager == null)
		{
			mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
	public static boolean isApplicationBroughtToBackground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
//			BaseLog.d("main4", "task.get(0).topActivity0="+topActivity.getPackageName());
//			BaseLog.d("main4", "task.get(0).topActivity1="+topActivity.getClassName());
			if (!topActivity.getPackageName().equals(context.getPackageName())
					||topActivity.getClassName().equals("com.common.as.activity.TPActivity")) {
				
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInstalerActivity(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (topActivity.getPackageName().equals("com.android.packageinstaller")) {
				return true;
			}
		}
		return false;
	}
	public static String getPackageName(Context context){
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = info.packageName;
		return s;
	}
}
