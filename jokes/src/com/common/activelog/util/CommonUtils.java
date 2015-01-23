package com.common.activelog.util;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.utils.ThreeDes;

public class CommonUtils {

	public static String getFromAssets(Context context, String fileName) {
		String result = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组�?
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

			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			channel = EncodingUtils.getString(buffer, "UTF-8");

			BaseLog.e("main", "channel:" + channel);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return channel;
	}

	public static String getChannelid(Context context, String fileName) {
		String channelid = getFromAssets(context, fileName);
		String[] s = channelid.split("[\\n]");
		s[0] = s[0].substring(9, s[0].length() - 2);
		return s[0];
	}

	public static int getApplid(Context context, String fileName) {
		String channelid = getFromAssets(context, fileName);
		String[] s = channelid.split("[\\n]");
		s[1] = s[1].substring(6, s[1].length() - 2);
		return Integer.parseInt(s[1]);
	}

	public static String getPushlid(Context context, String fileName) {
		String channelid = getFromAssets(context, fileName);
		String[] s = channelid.split("[\\n]");
		s[2] = s[2].substring(7, s[2].length() - 2);
		return s[2];
	}

	public static String getPushVer(Context context, String fileName) {
		String channelid = getFromAssets(context, fileName);
		String[] s = channelid.split("[\\n]");
		s[3] = s[3].substring(8, s[3].length() - 1);
		return s[3];
	}

	public static int getAppVer(Context context, String fileName) {
		String channelid = getFromAssets(context, fileName);
		String[] s = channelid.split("[\\n]");
		Log.d("main", "s[4]=" + s[4]);
		s[4] = s[4].substring(7, s[4].length() - 1);
		Log.d("main", "s[4]=" + s[4]);
		return Integer.parseInt(s[4]);
	}

	// public static SIM_TYPE getSimType(Context context){
	// TelephonyManager mTelephonyMgr =
	// (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	// String imsi = mTelephonyMgr.getSubscriberId();
	// if (null == imsi) {
	// return SIM_TYPE.UN_KNOW;
	// }
	// if(imsi.startsWith("46000") ||imsi.startsWith("46002") ||
	// imsi.startsWith("46007")){
	// //因为移动网络编号46000下的IMSI已经用完，所以虚拟了�?��46002编号�?34/159号段使用了此编号
	// //中国移动
	// return SIM_TYPE.MOBILE;
	// }else if(imsi.startsWith("46001")|| imsi.startsWith("46006")){
	// //中国联�?
	// return SIM_TYPE.LINTONE;
	// }else if(imsi.startsWith("46003") ||imsi.startsWith("46005")){
	// //中国电信
	// return SIM_TYPE.TELECON;
	// }
	// return SIM_TYPE.MOBILE;
	// }
	public static String getPackName(Context context) {

		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = info.packageName;

		return s;

	}

	public static String gethash(Context context, PackageInfo packageInfo) {

		Signature[] signs = packageInfo.signatures;
		Signature sign = signs[0];

		int hash = sign.hashCode();
		// Log.d("main", "hash="+hash);
		final byte[] keyBytes = "rth9at!0x$pw@0v&d7uow9rt".getBytes();// 密钥
		byte[] encoded = ThreeDes.encryptMode(keyBytes, String.valueOf(hash)
				.getBytes());
		String out = ThreeDes.byte2HexStr(encoded);
		// Log.d("main", "加密后的hash="+out);
		return out;
	}

	public static int gethashcode(Context context) {

		String packName = context.getPackageName();

		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packName,
					PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Signature[] signs = packageInfo.signatures;
		Signature sign = signs[0];

		int hash = sign.hashCode();
		return hash;
	}

	public static int getAppVer(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			return info.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getImsi(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		return imsi;
	}

	public static String getIMEI(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI = null;
		if (telMgr != null) {
			IMEI = telMgr.getDeviceId();
		}
		return IMEI;
	}

	public static void init_data(Activity activity) {
		// PayUtils.init_point(activity, true,
		// "1","2","3","4","5","6","7","8","9");
		// HfbUtil.getInstance().setHFBPayInfo(activity,"商户号","商品号");
		// UPayUtil.getInstance().setUPayData(activity, "商品扩展信息", "商品描述信息");
	}

	public static void pay(Activity activity, Handler mHandler) {
		// PayUtils.startPay(activity, mHandler,true, 100, 1,"001", "我的商品",
		// PayPrama.PAY_TYPE, "1","test_01",1,2,true,null);
	}
}
