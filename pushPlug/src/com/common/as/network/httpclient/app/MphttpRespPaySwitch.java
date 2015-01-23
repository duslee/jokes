package com.common.as.network.httpclient.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.as.network.httpclient.HttpRespArray;
import com.common.as.struct.PaySwitchInfo;
import com.common.as.struct.SwitchInfo;

public class MphttpRespPaySwitch extends HttpRespArray{

	private PaySwitchInfo mPaySwitchInfo = new PaySwitchInfo();
	
	
	
	@Override
	public void phraseJasonArray(JSONArray jArray) {
		// TODO Auto-generated method stub
		super.phraseJasonArray(jArray);
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jsonObj;
			try {
				jsonObj = jArray.getJSONObject(i);
				try {
					int paySwitch1 = jsonObj.getInt("switch1");
					mPaySwitchInfo.setmPaySwitch(paySwitch1);
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
//	@Override
//	public void phraseJason(JSONObject jsonObj) {
//		// TODO Auto-generated method stub
//		try {
//			int switch1 = jsonObj.getInt("switch1");
//			mSwitchInfo.setmPopSwitch(switch1);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			int switch1 = jsonObj.getInt("switch2");
//			mSwitchInfo.setmShortCutSwitch(switch1);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			int switch1 = jsonObj.getInt("switch3");
//			mSwitchInfo.setmBgSwitch(switch1);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			int switch1 = jsonObj.getInt("switch4");
//			mSwitchInfo.setmTopWndSwitch(switch1);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			int switch1 = jsonObj.getInt("switch5");
//			mSwitchInfo.setmListSwitch(switch1);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	public PaySwitchInfo getmPaySwitchInfo() {
		return mPaySwitchInfo;
	}

	
}
