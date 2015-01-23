package com.common.activelog;

import org.json.JSONObject;

import com.common.activelog.http.HttpRespErrorCode;

public class ActiveLogResp extends HttpRespErrorCode {

	public boolean isRespSucc() {
		// Log.d("main",
		// "this.getErrCode().equals("true")="+this.getErrCode().equals("true"));
		return Boolean.getBoolean(this.getErrCode());
	}

	@Override
	public void phraseJason(JSONObject jsonObj) {
		super.phraseJason(jsonObj);
	}
}
