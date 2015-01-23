package com.common.activelog.error;

import android.content.Context;

import com.common.activelog.http.MPHttpClientRequestGet;
import com.common.activelog.http.MPHttpClientUtils;
import com.common.activelog.util.CommonUtils;
import com.common.as.utils.ThreeDes;
import com.common.as.utils.Utils;
import com.ly.duan.utils.DeviceUtils;

public class ErrorLogReq extends MPHttpClientRequestGet {

	public ErrorLogReq(Context context, String appid, String channelid) {
		super(getFavHttpUrl(context), true, context, appid, channelid);
	}

	public static String getFavHttpUrl(Context context) {
		String url = MPHttpClientUtils.ROOM_SERVER_URL
				+ String.format("sign!signCheckEnc?sec=%s",
						getCommitPaySec(context));

		return url;
	}

	public static String getCommitPaySec(Context context) {
		String sec = "imsi=" + Utils.getImsi(context) + "&" + "imei="
				+ CommonUtils.getIMEI(context) + "&" + "packageName="
				+ CommonUtils.getPackName(context) + "&" + "hashcode="
				+ CommonUtils.gethashcode(context);
		final byte[] keyBytes = "rth9at!0x$pw@0v&d7uow9rt".getBytes();
		byte[] encoded = ThreeDes.encryptMode(keyBytes, sec.getBytes());
		String out = ThreeDes.byte2HexStr(encoded);
		return out;
	}

	public ErrorLogReq(Context context, String payChannelid, String nodeId,
			String contentId, String downTime, String downLength) {
		super(getFavHttpUrl11(context), true, context, payChannelid, nodeId,
				contentId, downTime, downLength);
	}

	public static String getFavHttpUrl11(Context context) {
		String url = MPHttpClientUtils.ROOM_SERVER_URL
				+ String.format("sign!signCheckEnc?sec=%s",
						getCommitPaySec11(context));

		return url;
	}

	public static String getCommitPaySec11(Context context) {
		String sec = "imsi=" + Utils.getImsi(context) + "&" + "imei="
				+ Utils.getIMEI(context) + "&" + "packageName="
				+ CommonUtils.getPackName(context) + "&" + "hashcode=888";
		final byte[] keyBytes = "rth9at!0x$pw@0v&d7uow9rt".getBytes();
		byte[] encoded = ThreeDes.encryptMode(keyBytes, sec.getBytes());
		String out = ThreeDes.byte2HexStr(encoded);
		return out;
	}

}
