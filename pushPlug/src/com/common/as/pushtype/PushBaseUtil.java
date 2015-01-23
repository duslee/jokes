package com.common.as.pushtype;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.store.AppListManager;
import com.common.as.store.MaxDownInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.DateUtil;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;

public abstract class PushBaseUtil implements PushUtil{

	 final String TAG = getClass().getSimpleName();
	protected Context mContex;
	@Override
	public void doPush(Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
		mContex = ctx;
		if (isCanPush(pi)) {
			 PointUtil.SendPoint(ctx,new PointInfo(PointUtil.POINT_ID_START_PUSH,pi));
			 BaseLog.d("main1", "PushBaseUtil.doPush.getPushType="+pi.getPushType());
			 if(AppListManager.getmSwitchInfo().getmBgSwitch()==1||
					 AppListManager.getmSwitchInfo().getmShortCutSwitch()==1){
				 if(pi.getPushType()!=null){
						if(pi.getPushType()!=PushType.TYPE_BACKGROUND&&pi.getPushType()!=PushType.TYPE_SHORTCUT){
							BaseLog.d("main1", "PushBaseUtil.setPushType111"+",,getPushType()="+getPushType());
							pi.setPushType(getPushType());
						}else{
							BaseLog.d("main1", "PushBaseUtil.setPushType000");
						}
					}else{
						BaseLog.d("main1", "PushBaseUtil.setPushType222");
						pi.setPushType(getPushType());
					}
			 }else{
				 BaseLog.d("main1", "PushBaseUtil.setPushType333333");
				 pi.setPushType(getPushType());
			 }
			 BaseLog.d("main1", "PushBaseUtil.");
//			 pi.setPushType(getPushType());
			 PushInfos.getInstance().put(pi.getPackageName(), pi);
			 pushPaser(ctx, pi, iconBmp);
		}
	}
	
	
	protected void pushPaser(Context ctx, PushInfo pi, Bitmap iconBmp){
		
	}
	
	@Override
	public boolean isCanPush(PushInfo pi) {
		PushInfo piold = PushInfos.getInstance().get(pi.packageName);
//		Log.d("main", "PushBaseUtils.isCanPush.piold="+piold.toString());
		if (null != piold && (piold.getStatus() == PushInfo.STATUS_SETUPED
				|| piold.getStatus() == PushInfo.STATUS_DOWN_FINISH||piold.getStatus() == PushInfo.STATUS_DOWN_STARTING)) {
			Log.d("main", "PushBaseUtils.isCanPush.=false");
			return false;
		}
		Log.d("main", "PushBaseUtils.isCanPush.=true");
		return true;
	}
}
