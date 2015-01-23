package com.common.as.network;

import android.content.Context;

import com.common.as.network.httpclient.MPHttpClientData;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientRespListener;
import com.common.as.network.httpclient.MPHttpClientUtils;
import com.common.as.network.httpclient.app.MphttpRespAppSwitch;
import com.common.as.network.httpclient.app.MphttpRespPaySwitch;
import com.common.as.store.AppListManager;
import com.common.as.utils.http.HttpRespPaser;

public class HttpPayUtil implements MPHttpClientRespListener{
	private String TAG = "HttpPayUtil";
	public static final int KEY_PAY_SWITCH = 0;
	private final Context mContext;

	public abstract static class RequestData{
		
		public RequestData(int key) {
			super();
			this.key = key;
		}
		private final int key;
		private Object input;
		
		
		
		public Object getInput() {
			return input;
		}
		public void setInput(Object input) {
			this.input = input;
		}
		public abstract void onSuccess(int what, Object obj);
		public abstract void onFailed(int what, Object obj);
	}
	
	
	
	
	public HttpPayUtil(Context mContext) {
		super();
		this.mContext = mContext;
	}


	private boolean isReqing = false;
	private RequestData mRequestData;
	public void startRequest(RequestData req){
		
		if (!isReqing) {
			isReqing = true;
			mRequestData = req;
			switch (req.key) {
			case KEY_PAY_SWITCH:
				MPHttpClientUtils.getPaySwitch(req.key, this,mContext);
				break;
			}
			
		}
		
		
	}


	@Override
	public void onMPHttpClientResponse(int id, int errId, int statusId,
			MPHttpClientData obj) {
		// TODO Auto-generated method stub
		HttpRespPaser paser = new HttpRespPaser(mContext, obj, errId, statusId);
		if (paser.isRespondSuccess()) {
			if (mRequestData.key == KEY_PAY_SWITCH) {
				MphttpRespPaySwitch switchinfo = (MphttpRespPaySwitch)obj;
                AppListManager.setmPaySwitchInfo(switchinfo.getmPaySwitchInfo());
               // BaseLog.d(TAG, switchinfo.getmSwitchInfo());
				mRequestData.onSuccess(mRequestData.key, switchinfo.getmPaySwitchInfo());
			}
		} else {
			mRequestData.onFailed(paser.getErrId(), paser.getStr());
		}
		isReqing = false;
	}
}
