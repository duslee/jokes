package com.common.as.network.httpclient.app;

import android.content.Context;
import android.os.Handler;
import android.view.WindowManager;

import com.common.as.base.log.BaseLog;
import com.common.as.network.httpclient.MPHttpClientRequestGet;
import com.common.as.network.httpclient.MPHttpClientUtils;
import com.common.as.utils.AppUtil;
import com.common.as.utils.ThreeDes;
import com.common.as.utils.Utils;

public class MpHttpReqPostData extends MPHttpClientRequestGet {

	public static class LogData {
		final int listType;
		final String appid;
		final int pushType;
		final int logCode;
		int logsubcode;
		int startType;

		public int getStartType() {
			return startType;
		}

		public void setStartType(int startType) {
			this.startType = startType;
		}

		public int getLogsubcode() {
			return logsubcode;
		}

		public void setLogsubcode(int logsubcode) {
			this.logsubcode = logsubcode;
		}

		public LogData(int listType, String appid, int pushType, int logCode) {
			super();
			this.listType = listType;
			this.appid = appid;
			this.pushType = pushType;
			this.logCode = logCode;
		}

		public LogData(int listType, String appid, int pushType, int logCode,
				int startType) {
			super();
			this.listType = listType;
			this.appid = appid;
			this.pushType = pushType;
			this.logCode = logCode;
			this.startType = startType;
		}

	}

	public MpHttpReqPostData(LogData logData,Context ctx) {
		super(ctx,getUrl(logData), true);
	}

	public MpHttpReqPostData(Context ctx,int startType) {
		super(getActiveUrl(startType,ctx), true, ctx);
	}

	public MpHttpReqPostData(Context ctx) {
		super(getSignedUrl(ctx), true, ctx);
	}

	public static String getUrl(LogData logData) {
		String s = MPHttpClientUtils.ROOM_SERVER_URL + "log!upLoad?listType="
				+ logData.listType + "&pushAppId=" + logData.appid
				+ "&pushType=" + logData.pushType + "&logCode="
				+ logData.logCode + "&logParam=" + logData.logsubcode;
		return s;
	}

	// // 应用激活
	// public static String getActiveUrl(int startType) {
	// String s = MPHttpClientUtils.ROOM_SERVER_URL
	// + "active!active?startType=" + startType;
	// return s;
	// }
	// 应用激活
	public static String getActiveUrl(int startType,Context context) {
//		MPHttpClientUtils.ROOM_SERVER_URL = AppUtil.getYMRadom(context);
//		BaseLog.d("main", "ROOM_SERVER_URL="+MPHttpClientUtils.ROOM_SERVER_URL);
		String url = MPHttpClientUtils.ROOM_SERVER_URL
				+ String.format("active!commitActive?sec=%s",
						getActiveSec(startType, context));

		return url;
	}

	public static String getActiveSec(int startType, Context context) {
		String sec = "imsi=" + Utils.getImsi(context) + "&" + "imei="
				+ Utils.getIMEI(context) + "&" + "startType=" + startType;
		final byte[] keyBytes = "rth9at!0x$pw@0v&d7uow9rt".getBytes();
		byte[] encoded = ThreeDes.encryptMode(keyBytes, sec.getBytes());
		String out = ThreeDes.byte2HexStr(encoded);//
		return out;
	}

	// 每日签到
	public static String getSignedUrl(Context ctx) {
		String url = MPHttpClientUtils.ROOM_SERVER_URL
				+ String.format("sign!signEnc?sec=%s", getSignedSec(ctx));
		return url;
	}

	public static String getSignedSec(Context ctx) {
		String sec = "imsi=" + Utils.getImsi(ctx) + "&" + "imei="
				+ Utils.getIMEI(ctx);
		final byte[] keyBytes = "rth9at!0x$pw@0v&d7uow9rt".getBytes();
		byte[] encoded = ThreeDes.encryptMode(keyBytes, sec.getBytes());
		String out = ThreeDes.byte2HexStr(encoded);
		return out;
	}
	// public static String getFavHttpUrl(Context context){
	// String url = MPHttpClientUtils.ROOM_SERVER_URL +
	// String.format("sign!SignCheck2?sec=%s", getCommitPaySec(context));
	//
	// return url;
	// }
	// public static String getCommitPaySec(Context context){
	// String sec =
	// "imsi="+Utils.getImsi(context)+"&"+"imei="+Utils.getImei(context)+"&"+"packageName="+CommonUtils.getPackName(context)
	// +"&"+"hashcode="+CommonUtils.gethashcode(context);
	// final byte[] keyBytes ="gt911t0x$pw0v&3f5zt%vub9".getBytes();
	// byte[] encoded = ThreeDes.encryptMode(keyBytes, sec.getBytes());
	// String out=ThreeDes.byte2HexStr(encoded);
	// return out ;
	// }
}
