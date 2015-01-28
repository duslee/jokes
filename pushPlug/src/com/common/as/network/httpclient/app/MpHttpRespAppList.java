package com.common.as.network.httpclient.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.as.network.httpclient.HttpRespArray;
import com.common.as.pushtype.PushInfo;

public class MpHttpRespAppList extends HttpRespArray{

	private ArrayList<PushInfo> mPushinfos  = null;
	@Override
	public void phraseJasonArray(JSONArray jArray) {
		// TODO Auto-generated method stub
		super.phraseJasonArray(jArray);
		JSONObject job;
		PushAppInfo pushApp;
		PushInfo pushInfo;
		mPushinfos = new ArrayList<PushInfo>();
		for (int i = 0; i < jArray.length(); i++) {
			try {
				job = jArray.getJSONObject(i);
				pushApp = new PushAppInfo();
				pushApp.phraseJasonData(job);
				pushInfo = new PushInfo(pushApp.packageName, pushApp.appid+"", "0");
				pushInfo.setAppName(pushApp.title);
				pushInfo.setmDownUrl(pushApp.appUrl);
				pushInfo.setImageUrl(pushApp.imgUrl);
				pushInfo.setmBrief(pushApp.brief);
				mPushinfos.add(pushInfo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public ArrayList<PushInfo> getmPushinfos() {
		return mPushinfos;
	}

	private static class PushAppInfo{
		
		public String appUrl;
		public String brief;
		public String imgUrl;
		public int appid;
		public String packageName;
		public String title;
		public void phraseJasonData(JSONObject obj) throws JSONException{
			appUrl = obj.getString("appUrl");
			brief = obj.getString("brief");
			imgUrl = obj.getString("iconUrl");
			appid = obj.getInt("id");
			packageName = obj.getString("packageName");
			title = obj.getString("title");
		}
	}
	
}
