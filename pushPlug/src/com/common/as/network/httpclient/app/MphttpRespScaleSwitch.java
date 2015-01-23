package com.common.as.network.httpclient.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.common.as.network.httpclient.HttpRespArray;
import com.common.as.service.AppInfoUtil;
import com.common.as.struct.ScaleSwitchInfo;

public class MphttpRespScaleSwitch extends HttpRespArray{

	private ScaleSwitchInfo mScaleSwitchInfo = new ScaleSwitchInfo();
	
	
	
	@Override
	public void phraseJasonArray(JSONArray jArray) {
		// TODO Auto-generated method stub
		super.phraseJasonArray(jArray);
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jsonObj;
			try {
				jsonObj = jArray.getJSONObject(i);
				try {
					int scaleSwitch1 = jsonObj.getInt("switch1");
					mScaleSwitchInfo.setmScaleSwitch(scaleSwitch1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					String url = jsonObj.getString("url");
					mScaleSwitchInfo.setUrl(url);
					if(!TextUtils.isEmpty(mScaleSwitchInfo.getUrl())){
						AppInfoUtil.down_url = mScaleSwitchInfo.getUrl(); 
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}
	public ScaleSwitchInfo getmPaySwitchInfo() {
		return mScaleSwitchInfo;
	}

	
}
