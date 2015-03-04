package com.common.as.network.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.as.base.log.BaseLog;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientResponse;

public class HttpRespCode extends MPHttpClientData implements MPHttpClientResponse {

	public static final String LOGIN_RESP_ERRCODE_SUCCESS = "200";
	public static final int PAY_CHANNEL_NOT_CONFIG = 91404; //no config 无法找到可用支付信息
	public static final int PAY_CHANNEL_INVALID_PARAMS=450;//请求参数非法
	public static final int PAY_CHANNEL_SERVER_INNER_ERR=500;//服务器内部错误
	public static final int PAY_CHANNEL_PAYED=92100;//用户已支付
	// mErrCode = "200" 表示服务器返回成功 否则表示服务器处理失败: 如密码错误等。 
	private String mErrCode;
	
	// 返回服务器处理错误原因，当mErrCode != "200";
	private String mErrMessage;
	public String getErrCode(){
		return mErrCode;
	}
	
	public String getErrMessage(){
		return mErrMessage;
	}
	
	public boolean isSuccess(){
		if (!getErrCode().equals(LOGIN_RESP_ERRCODE_SUCCESS)){
			return false;
		}
		return true;
	}
	
	public MPHttpClientData phraseJason(String jason, int errId, int statusCode) {
		BaseLog.d("http","jason:"+jason);
		setErrId(errId);
		setStatusCode(statusCode);	
		try {
			JSONObject jsonObject = new JSONObject(jason);
			phraseJason(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}  
		return this;
	}
	
	public void phraseJason(JSONObject jsonObj){	
		try {
			mErrCode = jsonObj.getString("code");
			mErrMessage = jsonObj.getString("message");		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}

	@Override
	public void setKey(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MPHttpClientData phraseData(HttpEntity entity, int errId,
			int statusCode) throws ParseException, IOException {
		// TODO Auto-generated method stub
		return phraseJason(EntityUtils.toString(entity), errId, statusCode);
	}


}
