package com.common.as.utils.http;

import android.content.Context;

import com.common.as.network.httpclient.MPHttpClient;
import com.common.as.network.httpclient.MPHttpClientData;
import com.example.pushplug.R;

public class HttpRespPaser {

	protected MPHttpClientData mRespData = null;
	public MPHttpClientData getmRespData() {
		return mRespData;
	}

	private String str;
	private int errId;
	//protected int statusId;
	protected Context mContext;
	
	public int getErrId(){
		return errId;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	//statusId 无用，接口保留
	public HttpRespPaser(Context context, MPHttpClientData mRespData, int errId, int statusId) {
		this.mRespData = mRespData;
		this.errId = errId;
	//	this.statusId = statusId;
		mContext = context;
	}
	
	public boolean isRespondSuccess(){
		boolean isSuccess = true;
		if (errId != MPHttpClient.MPHTTP_RESULT_SUCCESS){			
//			switch (errId){
//			case MPHttpClient.MPHTTP_RESULT_ERR_SERVER:
//				str = mContext.getString(R.string.net_err);//+"statusId= "+statusId;
//				break;
//			case MPHttpClient.MPHTTP_RESULT_ERR_NET:
//				str = mContext.getString(R.string.net_un_connect);//+"statusId= "+statusId;
//				break;
//			default:
//				// network err
//				str = mContext.getString(R.string.net_err);//+"errId = " + errId;
//				
//				break;
//			}	
			isSuccess = false;
		}
		return isSuccess;
	}

	
}
