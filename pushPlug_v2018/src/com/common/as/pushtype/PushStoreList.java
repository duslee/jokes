package com.common.as.pushtype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.common.as.base.log.BaseLog;
import com.common.as.service.AppListBackService;
import com.common.as.service.BackService;
import com.common.as.utils.AppPrefs;

public class PushStoreList extends PushBaseUtil{
	

	@Override
	public PushType getPushType() {
		// TODO Auto-generated method stub
		return PushType.TYPE_STORE_LIST;
	}

	
	
	
	@Override
	public void doPush(Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
		mContex = ctx;
	    pushPaser(ctx, pi, iconBmp);
		
	}




	@Override
	protected void pushPaser(Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
//		super.pushPaser(ctx, pi, iconBmp);
//		PushInfoActionPaser.doClick(ctx, getPushType(), pi.packageName);
		SharedPreferences sp = ctx.getSharedPreferences(AppPrefs.APP_INFO, ctx.MODE_PRIVATE);
		BaseLog.d("main", "PushStoreList.pushPaser.startBackService");
		if(sp.getBoolean(AppPrefs.CONTROL_SWITCH, true)){
			if(!BackService.isRunning){
				Intent intent = new Intent(ctx, BackService.class);
				intent.putExtra(BackService.TAG_TOP_WND_TYPE,BackService.TYPE_BG_STORELIST);
				intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
				ctx.startService(intent); 
			}
		}else{
			if(!AppListBackService.isRunning){
				Intent intent = new Intent(ctx, AppListBackService.class);
				intent.putExtra(BackService.TAG_TOP_WND_TYPE,AppListBackService.TYPE_BG_STORELIST);
				intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
				ctx.startService(intent); 
			}
		}
	}
}
