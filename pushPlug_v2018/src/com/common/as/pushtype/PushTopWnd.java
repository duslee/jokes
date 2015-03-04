package com.common.as.pushtype;


import com.common.as.service.BackService;
import com.common.as.store.AppListManager;
import com.common.as.store.PushInfos;
import com.common.as.view.TableView;
import com.common.as.view.TableView.OnTopViewClick;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

public class PushTopWnd extends PushBaseUtil{


	Context mContext;

	
	
	
	

	@Override
	protected void pushPaser(Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
		super.pushPaser(ctx, pi, iconBmp);
		mContext = ctx;
		PushInfos.getInstance().put(pi.getPackageName(), pi);
		Log.d("main", "PushTopWnd.startBackService");
		Intent intent = new Intent(ctx, BackService.class);
		intent.putExtra(INTENT_PACKAGE_NAME, pi.getPackageName());
		intent.putExtra(INTENT_ICON_BMP, iconBmp);
		ctx.startService(intent);  
	}






	@Override
	public PushType getPushType() {
		// TODO Auto-generated method stub
		return PushType.TYPE_TOP_WND;
	}

	
	@Override
	public boolean isCanPush(PushInfo pi) {
		// TODO Auto-generated method stub

		if (AppListManager.getmSwitchInfo().getmTopWndSwitch() == 1) {
				if (super.isCanPush(pi)) {
					return true;
				}
		}
		return false;
	}
}
