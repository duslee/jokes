package com.common.as.network.httpclient;

import com.common.as.network.httpclient.HttpRespErrorCode;
import com.common.as.network.httpclient.MPHttpClientData;

import android.content.Context;

public class HttpRespResultPaser extends HttpRespPaser {

	public HttpRespResultPaser(Context context, MPHttpClientData mRespData,
			int errId, int statusId) {
		super(context, mRespData, errId, statusId);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isRespondSuccess() {
		// TODO Auto-generated method stub
		boolean isSuccess = super.isRespondSuccess();
		if (isSuccess) {
			HttpRespErrorCode lResp = (HttpRespErrorCode)getmRespData();			
			if (!lResp.isSuccess()){
				isSuccess = false;
				setStr(lResp.getErrMessage());
			}
		} 
		return isSuccess;
	}

	@Override
	public int getErrId() {
		if (super.isRespondSuccess()) {
			HttpRespErrorCode lResp = (HttpRespErrorCode)getmRespData();
			return Integer.valueOf(lResp.getErrCode());
		} else {
			return super.getErrId();
		}
		
	}
	
	

}
