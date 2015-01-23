package com.common.as.network.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.os.DeviceUtil;
import android.util.Log;
import android.view.WindowManager;

import com.common.as.base.log.BaseLog;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.network.utils.ApplicationNetworkUtils.ClientInfo;
import com.common.as.utils.AppUtil;
import com.common.as.utils.Utils;


public class MPHttpClientInterface {	
	public static abstract class MPHttpClientRequest{	

		public abstract HttpUriRequest getHttpRequest();

		public String mUrl;
//		public void setUrl(String url){
//			mUrl = url;
//			String str = null;
//			if (mUrl.contains("?")) {
//				str = "&"+getFormatUrl();//+getEt();
//			} else {
//				str = "?"+getFormatUrl();//+getEt();
//			}
//
//			mUrl += str;
//			mUrl = mUrl.replace(" ", "_");
//		}
		
		public void setNetUrl(String url,Context ctx ){
			mUrl = url;
			String str = null;
			if (mUrl.contains("?")) {
				str = "&"+getNetFormatUrl(ctx);//+getEt();
			} else {
				str = "?"+getNetFormatUrl(ctx);//+getEt();
			}

			mUrl += str;
			mUrl = mUrl.replace(" ", "_");
			//Log.d("main", "mUrl="+mUrl);
			BaseLog.d("main", "mUrl="+mUrl);
		}
		public void setSignedUrl(String url,Context ctx){
			mUrl = url;
			String str = null;
			if (mUrl.contains("?")) {
				str = "&"+getSignedFormatUrl(ctx);//+getEt();
			} else {
				str = "?"+getSignedFormatUrl(ctx);//+getEt();
			}

			mUrl += str;
			mUrl = mUrl.replace(" ", "_");
			//Log.d("main", "mUrl="+mUrl);
			BaseLog.d("main", "setSignedUrl.mUrl="+mUrl);
		}
		public void setPostUrl(String url){
			mUrl = url;
		}
		
		
		String getFormatUrl(Context ctx){
//			final Context ctx = ApplicationNetworkUtils.getInstance().getAppCtx();
//			final ClientInfo ci = ApplicationNetworkUtils.getInstance().getmClientInfo();
			return String.format(
			"imsi=%s&smsc=%s&imei=%s&pushId=%d&appid=%s&appVer=%d&channel=%s&pushVer=%d&manu=%s&type=%s&plat=%d&provider=%d&phoneNumber=%s&phoneVersion=%s"
			, Utils.getImsi(ctx),Utils.getSms(ctx),Utils.getIMEI(ctx)
			,Integer.parseInt(Utils.getFromAssets(ctx, "pushid.txt")),Utils.getFromAssets(ctx, "appid.txt"),AppUtil.getAppVer(ctx),Utils.getFromAssets1(ctx, "ZYF_ChannelID.txt"),AppUtil.getPushVer()
			,DeviceUtil.getFactory(),DeviceUtil.getType(),DeviceUtil.getVersion(),Utils.getProvideType(ctx),Utils.getTelNum(ctx),DeviceUtil.getPhoneVersion());
		}
		String getNetFormatUrl(Context ctx){
			BaseLog.v("main", "getNetFormatUrl");
//			final Context ctx = ApplicationNetworkUtils.getInstance().getAppCtx();
//			final ClientInfo ci = ApplicationNetworkUtils.getInstance().getmClientInfo();
			return String.format(
			"netType=%d&screenW=%d&screenH=%d&imsi=%s&smsc=%s&imei=%s&pushId=%d&appid=%s&appVer=%d&channel=%s&pushVer=%d&manu=%s&type=%s&plat=%d&provider=%d&phoneNumber=%s&phoneVersion=%s"
			,Utils.getNetType(ctx),Utils.getDisplayMetrics(ctx).widthPixels,Utils.getDisplayMetrics(ctx).heightPixels, Utils.getImsi(ctx),Utils.getSms(ctx),Utils.getIMEI(ctx)
			,Integer.parseInt(Utils.getFromAssets(ctx, "pushid.txt")),Utils.getFromAssets(ctx, "appid.txt"),AppUtil.getAppVer(ctx),Utils.getFromAssets1(ctx, "ZYF_ChannelID.txt"),AppUtil.getPushVer()
			,DeviceUtil.getFactory(),DeviceUtil.getType(),DeviceUtil.getVersion(),Utils.getProvideType(ctx),Utils.getTelNum(ctx),DeviceUtil.getPhoneVersion());
		}
		
		String getSignedFormatUrl(Context ctx){
			BaseLog.v("main", "getSignedFormatUrl");
			//final Context ctx = ApplicationNetworkUtils.getInstance().getAppCtx();
//			final ClientInfo ci = ApplicationNetworkUtils.getInstance().getmClientInfo();
			return String.format(
			"netType=%d&screenW=%d&screenH=%d&smsc=%s&pushId=%d&appid=%s&appVer=%d&channel=%s&pushVer=%d&manu=%s&type=%s&plat=%d&provider=%d&phoneNumber=%s&phoneVersion=%s"
			,Utils.getNetType(ctx),Utils.getDisplayMetrics(ctx).widthPixels,Utils.getDisplayMetrics(ctx).heightPixels,Utils.getSms(ctx)
			,Integer.parseInt(Utils.getFromAssets(ctx, "pushid.txt")),Utils.getFromAssets(ctx, "appid.txt"),AppUtil.getAppVer(ctx),Utils.getFromAssets1(ctx, "ZYF_ChannelID.txt"),AppUtil.getPushVer()
			,DeviceUtil.getFactory(),DeviceUtil.getType(),DeviceUtil.getVersion(),Utils.getProvideType(ctx),Utils.getTelNum(ctx),DeviceUtil.getPhoneVersion());
		}		
		
	}
	

	
//	public static String getEt(){
//		return ApplicationNetworkUtils.mClientInfo.appId
//		+"-"+1
//		+"-"+ApplicationNetworkUtils.mClientInfo.appVer
//		+"-"+ApplicationNetworkUtils.mClientInfo.channelId
//		+"-"+Utils.getAPNType(ApplicationNetworkUtils.getAppCtx());
//	}
	
	public interface MPHttpClientResponse{
//		public MPHttpClientData phraseJason(String jason, int errId, int statusCode); 
		public MPHttpClientData phraseData(HttpEntity entity, int errId, int statusCode) throws ParseException, IOException; 
		public void setKey(String key);
	}
	
	public interface MPHttpClientRespListener{
		public void onMPHttpClientResponse(int id, int errId, int statusId, MPHttpClientData obj);
	}

}
