package com.common.activelog.http;

import android.content.Context;
import android.util.Log;


public class HttpRespPaser {

	protected MPHttpClientData mRespData = null;
	public MPHttpClientData getmRespData() {
		return mRespData;
	}

	private String str;
	private int errId;
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

	public HttpRespPaser(Context context, MPHttpClientData mRespData, int errId, int statusId) {
		this.mRespData = mRespData;
		this.errId = errId;
		mContext = context;
	}
	
	public boolean isRespondSuccess(){
		boolean isSuccess = true;
		//Log.d("main", "isRespondSuccess.errId="+errId);
		if (errId != MPHttpClient.MPHTTP_RESULT_SUCCESS){			
//			switch (errId){
//			case MPHttpClient.MPHTTP_RESULT_ERR_SERVER:
//				str = "网络错误";//+"statusId= "+statusId;
//				break;
//			case MPHttpClient.MPHTTP_RESULT_ERR_NET:
//				str = "网络未连接,请检查网络设置";//+"statusId= "+statusId;
//				break;
//			default:
//				// network err
//				str = "网络错误";//+"errId = " + errId;
//				
//				break;
//			}	
			isSuccess = false;
		}
		return isSuccess;
	}

	
}
