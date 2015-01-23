package com.common.activelog;

import android.content.Context;

import com.common.activelog.http.MPHttpClientRequestGet;
import com.common.activelog.http.MPHttpClientUtils;
import com.common.as.utils.ThreeDes;
import com.ly.duan.utils.DeviceUtils;

public class ActiveLogReq extends MPHttpClientRequestGet {

	public ActiveLogReq(int startType, Context context, String appid,
			String channelid) {
		super(getFavHttpUrl(startType, context), true, context, appid,
				channelid);
	}

	public static String getFavHttpUrl(int startType, Context context) {

		String url = MPHttpClientUtils.ROOM_SERVER_URL
				+ String.format("active!commitActive?sec=%s",
						getSec(startType, context));

		return url;
	}

	public static String getSec(int startType, Context context) {
		String sec = "imsi=" + DeviceUtils.getImsi(context) + "&" + "imei="
				+ DeviceUtils.getIMEI(context) + "&" + "startType=" + startType;
		final byte[] keyBytes = "rth9at!0x$pw@0v&d7uow9rt".getBytes();
		byte[] encoded = ThreeDes.encryptMode(keyBytes, sec.getBytes());
		String out = ThreeDes.byte2HexStr(encoded);//
		return out;
	}
}
