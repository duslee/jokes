package com.common.activelog.error;

import org.json.JSONObject;

import com.common.activelog.http.HttpRespErrorCode;

public class ErrorLogResp extends HttpRespErrorCode {

	public boolean isRespSucc() {
		return this.getErrCode().equals("true");
	}

	@Override
	public void phraseJason(JSONObject jsonObj) {
		super.phraseJason(jsonObj);
	}
}
