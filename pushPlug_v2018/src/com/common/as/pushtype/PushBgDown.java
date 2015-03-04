package com.common.as.pushtype;

import com.common.as.base.log.BaseLog;
import com.common.as.store.AppListManager;
import com.common.as.store.MaxDownInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.DateUtil;
import com.common.as.utils.PointUtil;
import com.common.as.utils.Preferences;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.DeviceUtil;

public class PushBgDown extends PushBaseUtil{

	public static final int MAX_PUSH_SIZE_PER_DAY = 2;

	@Override
	public PushType getPushType() {
		// TODO Auto-generated method stub
		return PushType.TYPE_BACKGROUND;
	}
	
//	@Override
//	public void doPush(Context ctx, PushInfo pi, Bitmap iconBmp) {
//		// TODO Auto-generated method stub
//		//静默下载特殊处理，不要上传埋点1，由埋点开始下载来判断开始push
//		mContex = ctx;
//		if (isCanPush(pi)) {
//
//			
//			PushInfo piold = PushInfos.getInstance().get(pi.packageName);
//			if (null == piold || !piold.getPushType().equals(getPushType())) {
//				 pi.setPushType(getPushType());
//				 PushInfos.getInstance().put(pi.getPackageName(), pi);
//			}
//
//			 pushPaser(ctx, pi, iconBmp);
//		}
//	}

	@Override
	protected void pushPaser(Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
		super.pushPaser(ctx, pi, iconBmp);
		BaseLog.d("main1", "PushBgDown.pushPaser"+",,,pi.packageName="+pi.packageName+",type="+pi.getPushType());
		
		PushInfoActionPaser.doClick(ctx, getPushType(), pi.packageName);
		saveCount();
	}
	
	private void saveCount(){
		MaxDownInfo mdi = MaxDownInfo.get(getTagName());		
		if (mdi == null ){
			mdi = new MaxDownInfo();
		}
		mdi.addCount(DateUtil.getCurrentYear(), DateUtil.getCurrentMonth(), DateUtil.getCurrentDay());
		mdi.save(getTagName());
	}
	
	
	public static String getTagName(){
		return Preferences.BG_DOWN_SIZE_DAY;
		//return DateUtil.getCurrentYear()+DateUtil.getCurrentMonth()+DateUtil.getCurrentDay()+Preferences.BG_DOWN_SIZE_DAY;
	}

	@Override
	public boolean isCanPush(PushInfo pi) {
		// TODO Auto-generated method stub

		if (AppListManager.getmSwitchInfo().getmBgSwitch() == 1) {
			
			MaxDownInfo mdi = MaxDownInfo.get(getTagName());
			if (null != mdi) {
				BaseLog.d("main1", "mdi.pushedCount="+mdi.getmCurDownNum());
			}
			if (mdi == null 
					|| !mdi.isSuperMax(MAX_PUSH_SIZE_PER_DAY
							, DateUtil.getCurrentYear(), DateUtil.getCurrentMonth(), DateUtil.getCurrentDay())) {
				
				if (null != mdi) {
					BaseLog.d("main1", "pushedCount="+mdi.getmCurDownNum()+";"+mdi.getmCurTime());
				}
				if (super.isCanPush(pi)) {
					if (DeviceUtil.isWifiActive(mContex)) {
						return true;
					}
				}
			}
//			int pushedCount = Preferences.getInt(mContex, getTagName(), 0);
//			BaseLog.d(TAG, "pushedCount="+pushedCount);
//			if (pushedCount < MAX_PUSH_SIZE_PER_DAY) {
//				if (super.isCanPush(pi)) {
//					if (DeviceUtil.isWifiActive(mContex)) {
//						return true;
//					}
//				}
//			}

			
		}
		return false;
	}

	
	
}
