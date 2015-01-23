package com.common.as.network.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientResponse;

public class HttpRespArray  extends MPHttpClientData implements MPHttpClientResponse {

	@Override
	public MPHttpClientData phraseData(HttpEntity entity, int errId,
			int statusCode) throws ParseException, IOException {
		// TODO Auto-generated method stub
        setErrId(errId);
        setStatusCode(statusCode);
		if (errId == MPHttpClient.MPHTTP_RESULT_SUCCESS)
		{
			try {
				JSONArray jArray = new JSONArray(EntityUtils.toString(entity));
				phraseJasonArray(jArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}  
		}

		return this;
	}
	
	
	public void phraseJasonArray(JSONArray jArray){
		
	}

	@Override
	public void setKey(String key) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isSuccess() {
		// TODO Auto-generated method stub
		return getErrId() == 0 ? true:false;
	}

}
