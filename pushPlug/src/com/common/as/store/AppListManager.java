package com.common.as.store;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.struct.PaySwitchInfo;
import com.common.as.struct.SwitchInfo;
import com.common.as.utils.AppUtil;
import com.common.as.utils.DateUtil;
import com.common.as.utils.Preferences;

public class AppListManager {

//	public static final int FLAG_APP_STORE = 2; //shortcut, list
//	public static final int FLAG_APP_PUSH = 1;//bg, pop
	public static final int FLAG_APP_POP = 1; //shortcut, list
	public static final int FLAG_APP_BG = 3;//
	public static final int FLAG_APP_SHORTCUT = 2; //
	public static final int FLAG_APP_BANNER = 4;//
	public static final int FLAG_APP_LIST = 5;//
	public static final long OUT_T = 4*60*60*1000;
	
//	private static ArrayList<PushInfo>  mAppStorelists;
//	private static ArrayList<PushInfo>  mAppPushlists;
	
	private static ArrayList<PushInfo>  mApplists;
	private static ArrayList<PushInfo>  mAppBglists;
	private static ArrayList<PushInfo>  mAppSClists;
	private static ArrayList<PushInfo>  mAppPoplists;
	private static ArrayList<PushInfo>  mAppBannerlists;
	private static SwitchInfo mSwitchInfo = new SwitchInfo();
	private static PaySwitchInfo mPaySwitchInfo = new PaySwitchInfo();
	
	public static interface OnListChangeListener{
		public void onDataChange(Object obj);
	}
	private static ArrayList<OnListChangeListener> mOnListeners = new ArrayList<AppListManager.OnListChangeListener>();
	
	
	public static int getListType(PushType pt){
		switch (pt) {
		case TYPE_STORE_LIST:
			return FLAG_APP_LIST;
		case TYPE_BACKGROUND:
			return FLAG_APP_BG;
		case TYPE_POP_WND:
			return FLAG_APP_POP;
		case TYPE_SHORTCUT:
			return FLAG_APP_SHORTCUT;
		case TYPE_BTN:
			return FLAG_APP_BANNER;
		default:
			return FLAG_APP_LIST;
		}
	}
	public static ArrayList<PushInfo> getApplists(int flag) {
		BaseLog.d("main", "flag="+flag);
		switch (flag) {
		case FLAG_APP_LIST:			
			return getmApplists();
		case FLAG_APP_BG:
			return getmAppBglists();
		case FLAG_APP_POP:			
			return getmAppPoplists();
		case FLAG_APP_SHORTCUT:
			return getmAppSCutlists();
		case FLAG_APP_BANNER:
			return getmAppBTNlists();
		default:
			break;
		}
		if (null == mApplists) {
			mApplists = AppListInfos.getInstance().get(Preferences.APP_List_TAG);
		}
		return mApplists;
	}
	private static ArrayList<PushInfo> getmApplists() {
		if (null == mApplists) {
			mApplists = AppListInfos.getInstance().get(Preferences.APP_List_TAG);
		}
		return mApplists;
	}
	public static synchronized void setmAppStorelists(Context ctx,ArrayList<PushInfo> appStorelists) {
		
		if (null != appStorelists && appStorelists.size() > 0) {
			mApplists = new ArrayList<PushInfo>(appStorelists);
			AppListInfos.getInstance().put(Preferences.APP_List_TAG, mApplists);
			Preferences.setLong(ctx, Preferences.APP_List_TAG, System.currentTimeMillis());
			notifyDataChanged(appStorelists);
		}

	}
	private static  ArrayList<PushInfo> getmAppBTNlists() {
		if (null == mAppBannerlists ) {
			mAppBannerlists = AppListInfos.getInstance().get(Preferences.APP_BTN_TAG);
		}
		return mAppBannerlists;
	}
	private static  ArrayList<PushInfo> getmAppBglists() {
		if (null == mAppBglists ) {
			mAppBglists = AppListInfos.getInstance().get(Preferences.APP_BG_TAG);
		}
		return mAppBglists;
	}
	private static  ArrayList<PushInfo> getmAppPoplists() {
		if (null == mAppPoplists ) {
			mAppPoplists = AppListInfos.getInstance().get(Preferences.APP_POP_TAG);
		}
		return mAppPoplists;
	}
	private static  ArrayList<PushInfo> getmAppSCutlists() {
		if (null == mAppSClists ) {
			mAppSClists = AppListInfos.getInstance().get(Preferences.APP_SHORT_CUT_TAG);
		}
		return mAppSClists;
	}
	public static synchronized void setmAppBannerlists(Context ctx,ArrayList<PushInfo> appPushlists) {
		if (null != appPushlists && appPushlists.size() > 0) {
			mAppBannerlists = new ArrayList<PushInfo>(appPushlists);
			AppListInfos.getInstance().put(Preferences.APP_BTN_TAG, mAppBannerlists);
			Preferences.setLong(ctx, Preferences.APP_BTN_TAG, System.currentTimeMillis());

		}
	}
	public static synchronized void setmAppBglists(Context ctx,ArrayList<PushInfo> appPushlists) {
		if (null != appPushlists && appPushlists.size() > 0) {
			mAppBglists = new ArrayList<PushInfo>(appPushlists);
			AppListInfos.getInstance().put(Preferences.APP_BG_TAG, mAppBglists);
			Preferences.setLong(ctx, Preferences.APP_BG_TAG, System.currentTimeMillis());

		}
	}
	public static synchronized void setmAppPoplists(Context ctx,ArrayList<PushInfo> appPushlists) {
		if (null != appPushlists && appPushlists.size() > 0) {
			mAppPoplists = new ArrayList<PushInfo>(appPushlists);
			AppListInfos.getInstance().put(Preferences.APP_POP_TAG, mAppPoplists);
			Preferences.setLong(ctx, Preferences.APP_POP_TAG, System.currentTimeMillis());

		}
	}
	public static synchronized void setmAppSCutlists(Context ctx,ArrayList<PushInfo> appPushlists) {
		if (null != appPushlists && appPushlists.size() > 0) {
			mAppSClists = new ArrayList<PushInfo>(appPushlists);
			AppListInfos.getInstance().put(Preferences.APP_SHORT_CUT_TAG, mAppSClists);
			Preferences.setLong(ctx, Preferences.APP_SHORT_CUT_TAG, System.currentTimeMillis());

		}
	}
	public static boolean isOutDay(Context context,int flag){
		long lastT = 0l;
		ArrayList<PushInfo> list = null;
		switch (flag) {
		case FLAG_APP_LIST:
			lastT = Preferences.getLong(context, Preferences.APP_List_TAG, 0);
			list = mApplists;
			break;
		case FLAG_APP_BG:
			lastT = Preferences.getLong(context, Preferences.APP_BG_TAG, 0);
			list = mAppBglists;
			break;
		case FLAG_APP_SHORTCUT:
			lastT = Preferences.getLong(context, Preferences.APP_SHORT_CUT_TAG, 0);
			list = mAppSClists;
			break;
		case FLAG_APP_POP:
			lastT = Preferences.getLong(context, Preferences.APP_POP_TAG, 0);
			list = mAppPoplists;
			break;
		case FLAG_APP_BANNER:
			lastT = Preferences.getLong(context, Preferences.APP_BTN_TAG, 0);
			list = mAppBannerlists;
			break;
		default:
			break;
		}

        if (DateUtil.isAfterCurrent(System.currentTimeMillis(),OUT_T+lastT, 0)) {
			return true;
		}else{
			if (null == list) {
				list = getApplists(flag);				
			}
			
			if (null == list) {
				return true;
			} else {
                return false;
			}
		}
		
	}
	
	public static synchronized PushInfo findPushInfo(Context ctx,PushType type){
		ArrayList<PushInfo> lists = getApplists(getListType(type));
		
		if (null != lists && lists.size() > 0) {
			BaseLog.d("main", "lists="+lists.toString());
			BaseLog.d("main", "lists="+lists.size());
			for (PushInfo pushInfo : lists) {
				if (!AppUtil.isInstalled(ctx, pushInfo.getPackageName())) {
					PushInfo temp = PushInfos.getInstance().get(pushInfo.getPackageName());
					if(null!=temp){
						BaseLog.d("main", "AppListManager.get.temp="+temp.toString());
					}
					if(type==PushType.TYPE_BTN){
						if (null == temp) {
							if(TextUtils.isEmpty(pushInfo.getPicUrl())){
								return null;
							}else{
								pushInfo.setPushType(type);
								PushInfos.getInstance().put(pushInfo.packageName, pushInfo);
								BaseLog.d("main", "findPushInfo.temp=null"+",,pushInfo="+pushInfo.toString());
								return pushInfo;
							}
						}else if(temp.getStatus() != PushInfo.STATUS_DOWN_FINISH
								&& temp.getStatus() != PushInfo.STATUS_SETUPED
								&& temp.getPushType().equals(type)
								&& temp.getPushType() != PushType.TYPE_SHORTCUT
								&&temp.getStatus()!=PushInfo.STATUS_DOWN_STARTING){
							if(TextUtils.isEmpty(temp.getPicUrl())){
								return null;
							}else{
								pushInfo.setPushType(type);
								PushInfos.getInstance().put(temp.packageName, temp);
								return temp;
							}
							
						}
					}else{
						if (null == temp) {
							pushInfo.setPushType(type);
							PushInfos.getInstance().put(pushInfo.packageName, pushInfo);
							BaseLog.d("main", "findPushInfo.temp=null"+",,pushInfo="+pushInfo.toString());
							return pushInfo;
						}else if(temp.getStatus() != PushInfo.STATUS_DOWN_FINISH
								&& temp.getStatus() != PushInfo.STATUS_SETUPED
								&& temp.getPushType().equals(type)
								&& temp.getPushType() != PushType.TYPE_SHORTCUT
								&&temp.getStatus()!=PushInfo.STATUS_DOWN_STARTING){
							pushInfo.setPushType(type);
							PushInfos.getInstance().put(temp.packageName, temp);
							return temp;
							
						}
					}
					
					
					
//					if(type == PushType.TYPE_SHORTCUT){
//						if (null == temp) {
//							BaseLog.d("main", "TYPE_SHORTCUT.temp=null"+",,pushInfo="+pushInfo.toString());
//							return pushInfo;
//						}
//						else if (temp.getStatus() != PushInfo.STATUS_DOWN_FINISH
//								&& temp.getStatus() != PushInfo.STATUS_SETUPED
//								&& !temp.getPushType().equals(type)
//								&& temp.getPushType() != PushType.TYPE_SHORTCUT
//								&& temp.getPushType() != PushType.TYPE_TOP_WND
//								&&temp.getStatus()!=PushInfo.STATUS_DOWN_STARTING ) {
//							BaseLog.d("main", "pushInfo="+pushInfo.toString());
//							return pushInfo;
//						}
//					}else{
//						if (null == temp) {
//							BaseLog.d("main", "type+"+type+".temp=null"+",,pushInfo="+pushInfo.toString());
//							return pushInfo;
//						}
//						else if (temp.getStatus() != PushInfo.STATUS_DOWN_FINISH
//								&& temp.getStatus() != PushInfo.STATUS_SETUPED
//								&& temp.getPushType().equals(type)
//								&& temp.getPushType() != PushType.TYPE_SHORTCUT 
//								&&temp.getStatus()!=PushInfo.STATUS_DOWN_STARTING ) {
//							BaseLog.d("main", "type+"+type+".temp!=null"+"temp="+temp.toString()+",,pushInfo="+pushInfo.toString());
//							return pushInfo;
//						}
//					}
				}

			}
		}else{
			BaseLog.d("main", "lists=null");
		}
		return null;
	}
	public static SwitchInfo getmSwitchInfo() {
		return mSwitchInfo;
	}
	public static void setmSwitchInfo(SwitchInfo mSwitchInfo) {
		AppListManager.mSwitchInfo = mSwitchInfo;
	}
	public static PaySwitchInfo getmPaySwitchInfo() {
		return mPaySwitchInfo;
	}
	public static void setmPaySwitchInfo(PaySwitchInfo mPaySwitchInfo) {
		AppListManager.mPaySwitchInfo = mPaySwitchInfo;
	}
	
	public static void notifyDataChanged(Object obj){
		for (OnListChangeListener listener : mOnListeners) {
			listener.onDataChange(obj);
		}
	}
	
	
	public static void addListener(OnListChangeListener mOnListChangeListener){
		if (!mOnListeners.contains(mOnListChangeListener)) {
			mOnListeners.add(mOnListChangeListener);
		}
		
	}
	public static void removeListener(OnListChangeListener mOnListChangeListener){
		if (mOnListeners.contains(mOnListChangeListener)) {
			mOnListeners.remove(mOnListChangeListener);
		}

	}
	
}
