package com.common.activelog.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.net.ParseException;
import android.os.DeviceUtil;

import com.common.as.base.log.BaseLog;
import com.common.as.utils.Utils;
import com.ly.duan.utils.AppUtils;

public class MPHttpClientInterface {

	public static abstract class MPHttpClientRequest {

		public abstract HttpUriRequest getHttpRequest();

		public String mUrl;

		public void setErrorUrl(String url, Context context,
				String payChannelid, String nodeId, String contentId,
				String downTime, String downLength) {
			mUrl = url;
			String str = null;
			if (mUrl.contains("?")) {
				str = "&"
						+ getErrorFormatUrl(context, payChannelid, nodeId,
								contentId, downTime, downLength);// +getEt();
			} else {
				str = "?"
						+ getErrorFormatUrl(context, payChannelid, nodeId,
								contentId, downTime, downLength);// +getEt();
			}

			mUrl += str;
			mUrl = mUrl.replace(" ", "_");
			BaseLog.d("main", "mUrl=" + mUrl);
		}

		String getErrorFormatUrl(Context context, String payChannelid,
				String nodeId, String contentId, String downTime,
				String downLength) {
			return String
					.format("netType=%d&screenW=%d&screenH=%d&smsc=%s&appid=%s&appVer=%d&channel=%s&manu=%s&type=%s&plat=1&provider=%d&phoneNumber=%s",
							Utils.getNetType(context),
							Utils.getDisplayMetrics(context).widthPixels,
							Utils.getDisplayMetrics(context).heightPixels,
							payChannelid, nodeId, AppUtils.getAppVer(context),
							contentId, downTime, downLength,
							Utils.getProvideType(context),
							Utils.getTelNum(context));
		}

		public void setNetUrl(String url, Context context, String appid,
				String channelid) {
			mUrl = url;
			String str = null;
			if (mUrl.contains("?")) {
				str = "&" + getNetFormatUrl(context, appid, channelid);// +getEt();
			} else {
				str = "?" + getNetFormatUrl(context, appid, channelid);// +getEt();
			}

			mUrl += str;
			mUrl = mUrl.replace(" ", "_");
			BaseLog.d("main", "mUrl=" + mUrl);
		}

		String getNetFormatUrl(Context context, String appid, String channelid) {
			return String
					.format("netType=%d&screenW=%d&screenH=%d&smsc=%s&appid=%s&appVer=%d&channel=%s&manu=%s&type=%s&plat=1&provider=%d&phoneNumber=%s",
							Utils.getNetType(context),
							Utils.getDisplayMetrics(context).widthPixels,
							Utils.getDisplayMetrics(context).heightPixels,
							Utils.getSms(context), appid,
							AppUtils.getAppVer(context), channelid,
							DeviceUtil.getFactory(), DeviceUtil.getType(),
							Utils.getProvideType(context),
							Utils.getTelNum(context));
		}

		public void setUrl(String url, Context context) {
			mUrl = url;
			String str = null;
			if (mUrl.contains("?")) {
				str = "&" + getFormatUrl(context);// +getEt();
			} else {
				str = "?" + getFormatUrl(context);// +getEt();
			}

			mUrl += str;
			mUrl = mUrl.replace(" ", "_");
			BaseLog.d("main", "mUrl=" + mUrl);
		}

		String getFormatUrl(Context context) {
			return String
					.format("netType=%d&screenW=%d&screenH=%d&smsc=%s&appVer=%d&manu=%s&type=%s&plat=1&provider=%d&phoneNumber=%s",
							Utils.getNetType(context),
							Utils.getDisplayMetrics(context).widthPixels,
							Utils.getDisplayMetrics(context).heightPixels,
							Utils.getSms(context), AppUtils.getAppVer(context),
							DeviceUtil.getFactory(), DeviceUtil.getType(),
							Utils.getProvideType(context),
							Utils.getTelNum(context));
		}

		public void setVPayUrl(String url, Context context, String channelid) {
			mUrl = url;
			String str = null;
			if (mUrl.contains("?")) {
				str = "&" + getVPayFormatUrl(context, channelid);// +getEt();
			} else {
				str = "?" + getVPayFormatUrl(context, channelid);// +getEt();
			}

			mUrl += str;
			mUrl = mUrl.replace(" ", "_");
			BaseLog.d("main", "mUrl=" + mUrl);
		}

		String getVPayFormatUrl(Context context, String channelid) {
			return String
					.format("netType=%d&screenW=%d&screenH=%d&appVer=%d&channel=%s&manu=%s&type=%s&plat=1&provider=%d&phoneNumber=%s",
							Utils.getNetType(context),
							Utils.getDisplayMetrics(context).widthPixels,
							Utils.getDisplayMetrics(context).heightPixels,
							AppUtils.getAppVer(context), channelid,
							DeviceUtil.getFactory(), DeviceUtil.getType(),
							Utils.getProvideType(context),
							Utils.getTelNum(context));
		}

	}

	public interface MPHttpClientResponse {
		public MPHttpClientData phraseData(HttpEntity entity, int errId,
				int statusCode) throws ParseException, IOException;

		// public MPHttpClientData phraseJason(String jason, int errId, int
		// statusCode);
		public void setKey(String key);
	}

	public interface MPHttpClientRespListener {
		public void onMPHttpClientResponse(int id, int errId, int statusId,
				MPHttpClientData obj);
	}

}
