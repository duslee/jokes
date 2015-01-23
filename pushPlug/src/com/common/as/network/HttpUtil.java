package com.common.as.network;

import android.content.Context;

import com.common.as.base.log.BaseLog;
import com.common.as.network.httpclient.MPHttpClientData;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientRespListener;
import com.common.as.network.httpclient.MPHttpClientUtils;
import com.common.as.network.httpclient.app.MpHttpReqAppList.ListReq;
import com.common.as.network.httpclient.app.MpHttpReqPostData.LogData;
import com.common.as.network.httpclient.app.MpHttpRespAppList;
import com.common.as.network.httpclient.app.MphttpRespAppSwitch;
import com.common.as.network.httpclient.app.MphttpRespScaleSwitch;
import com.common.as.store.AppListManager;
import com.common.as.utils.AppUtil;
import com.common.as.utils.http.HttpRespPaser;

public class HttpUtil implements MPHttpClientRespListener{
	private String TAG = "main";
	
	public static final int KEY_APP_SWITCH = 6;
	
	public static final int KEY_PUSH_SWITCH = 1;
	
	public static final int KEY_BG_LIST = 2;
	public static final int KEY_STORE_LIST = 3;
	public static final int KEY_POP_LIST = 21;
	public static final int KEY_SCUT_LIST = 31;
	public static final int KEY_BTN_LIST = 41;
	
	public static final int KEY_POST_DATA = 4;
	public static final int KEY_POST_NET_DATA = 5;
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
	
	
	
	
	public HttpUtil(Context mContext) {
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
			case KEY_PUSH_SWITCH:
				MPHttpClientUtils.getSwitch(req.key, this,mContext);
				break;
			case KEY_BG_LIST:
				MPHttpClientUtils.getAppList(req.key, this, new ListReq(ListReq.TYPE_LIST_BG),mContext);
				break;
			case KEY_STORE_LIST:
				MPHttpClientUtils.getAppList(req.key, this, new ListReq(ListReq.TYPE_LIST_STORE_LIST),mContext);
				break;
			case KEY_POP_LIST:
				MPHttpClientUtils.getAppList(req.key, this, new ListReq(ListReq.TYPE_LIST_POP),mContext);
				break;
			case KEY_SCUT_LIST:
				MPHttpClientUtils.getAppList(req.key, this, new ListReq(ListReq.TYPE_LIST_S_CUT),mContext);
			case KEY_BTN_LIST:
				MPHttpClientUtils.getAppList(req.key, this, new ListReq(ListReq.TYPE_LIST_BTN),mContext);
				break;
			case KEY_POST_DATA:
				MPHttpClientUtils.postLog(req.key, this, (LogData)req.input,mContext);
				break;
			case KEY_POST_NET_DATA:
				MPHttpClientUtils.postLog(req.key, this,0,mContext);
			case KEY_APP_SWITCH:
				MPHttpClientUtils.getScaleSwitches(req.key, this, mContext);
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
			if (mRequestData.key == KEY_PUSH_SWITCH) {
				MphttpRespAppSwitch switchinfo = (MphttpRespAppSwitch)obj;
                AppListManager.setmSwitchInfo(switchinfo.getmSwitchInfo());
               // BaseLog.d(TAG, switchinfo.getmSwitchInfo());
				mRequestData.onSuccess(mRequestData.key, switchinfo.getmSwitchInfo());
			}else if (mRequestData.key == KEY_BG_LIST) {
				MpHttpRespAppList respApplist = (MpHttpRespAppList)obj;
				AppListManager.setmAppBglists(mContext, respApplist.getmPushinfos());
				if (null != respApplist.getmPushinfos()) {
					//BaseLog.d(TAG, respApplist.getmPushinfos());
				}
				
				mRequestData.onSuccess(mRequestData.key, respApplist.getmPushinfos());
			}else if (mRequestData.key == KEY_STORE_LIST) {
				MpHttpRespAppList respApplist = (MpHttpRespAppList)obj;
				AppListManager.setmAppStorelists(mContext, respApplist.getmPushinfos());
				if (null != respApplist.getmPushinfos()) {
					BaseLog.d(TAG, "HttpUtils.getmPushinfos="+respApplist.getmPushinfos());
				}
				mRequestData.onSuccess(mRequestData.key, respApplist.getmPushinfos());
			}else if (mRequestData.key == KEY_SCUT_LIST) {
				MpHttpRespAppList respApplist = (MpHttpRespAppList)obj;
				AppListManager.setmAppSCutlists(mContext, respApplist.getmPushinfos());
				if (null != respApplist.getmPushinfos()) {
					BaseLog.d(TAG, "HttpUtils.getmPushinfos="+respApplist.getmPushinfos());
				}
				mRequestData.onSuccess(mRequestData.key, respApplist.getmPushinfos());
			}else if (mRequestData.key == KEY_POP_LIST) {
				MpHttpRespAppList respApplist = (MpHttpRespAppList)obj;
				AppListManager.setmAppPoplists(mContext, respApplist.getmPushinfos());
				if (null != respApplist.getmPushinfos()) {
					BaseLog.d(TAG, "HttpUtils.getmPushinfos="+respApplist.getmPushinfos());
				}
				mRequestData.onSuccess(mRequestData.key, respApplist.getmPushinfos());
			}else if (mRequestData.key == KEY_BTN_LIST) {
				MpHttpRespAppList respApplist = (MpHttpRespAppList)obj;
				AppListManager.setmAppBannerlists(mContext, respApplist.getmPushinfos());
				if (null != respApplist.getmPushinfos()) {
					BaseLog.d(TAG, "HttpUtils.getmPushinfos="+respApplist.getmPushinfos());
				}
				mRequestData.onSuccess(mRequestData.key, respApplist.getmPushinfos());
			}else if(mRequestData.key == KEY_APP_SWITCH){
				MphttpRespScaleSwitch mScaleSwitch = (MphttpRespScaleSwitch)obj;
				if (null != mScaleSwitch.getmPaySwitchInfo()) {
					AppUtil.mScaleSwitchInfo = mScaleSwitch.getmPaySwitchInfo();
				}
				mRequestData.onSuccess(mRequestData.key, mScaleSwitch.getmPaySwitchInfo());
			}
			
		} else {
			mRequestData.onFailed(paser.getErrId(), paser.getStr());
		}
		isReqing = false;
	}
}
