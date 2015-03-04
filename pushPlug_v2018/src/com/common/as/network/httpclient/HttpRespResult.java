package com.common.as.network.httpclient;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpRespResult extends HttpRespDefault{

	
	private int result = 0;
	@Override
	public boolean isSuccess() {
		// TODO Auto-generated method stub
		if (super.isSuccess()) {
			if (200 == result) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void phraseJason(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		super.phraseJason(jsonObj);
		try {
			result = jsonObj.getInt("result");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
