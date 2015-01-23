package com.common.as.pushtype;


import android.content.Context;
import android.graphics.Bitmap;

import com.common.as.store.AppListManager;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.view.BannerDailogView;
import com.common.as.view.BannerDailogView.OnCancelBtnClick;
import com.common.as.view.BannerDailogView.OnDownLoadBtnClick;

public class PushTopWnd extends PushBaseUtil{


	Context mContext;

	private BannerDailogView dialogView;
	
	
	

	@Override
	protected void pushPaser(Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
		super.pushPaser(ctx, pi, iconBmp);
		mContext = ctx;
//		PushInfos.getInstance().put(pi.getPackageName(), pi);
//		Log.d("main", "PushTopWnd.startBackService");
//		Intent intent = new Intent(ctx, BackService.class);
//		intent.putExtra(INTENT_PACKAGE_NAME, pi.getPackageName());
//		intent.putExtra(INTENT_ICON_BMP, iconBmp);
//		ctx.startService(intent);  
		showBannerView(ctx, pi, iconBmp);
		
	}


	private void showBannerView(final Context ctx,final PushInfo pi,Bitmap iconBmp){
		new BitmapLoder(ctx).startLoad(new OnLoadBmp() {
			
			@Override
			public void onBitmapLoaded(Bitmap bmp) {
				// TODO Auto-generated method stub
				if(null == dialogView){
					dialogView = new BannerDailogView(ctx, new OnDownLoadBtnClick() {
						
						@Override
						public void onClick1() {
							PushInfoActionPaser.doClick(ctx, PushType.TYPE_BTN, pi.getPackageName());
							dialogView.removeTipView();
						}
					}, new OnCancelBtnClick() {
						
						@Override
						public void onClick2() {
							dialogView.removeTipView();
						}
					});
				}
				dialogView.setPushInfo(pi);
				dialogView.setIcBmp(bmp);
				dialogView.fun();
				
			}
		}, pi.getPicUrl());
	}



	@Override
	public PushType getPushType() {
		// TODO Auto-generated method stub
		return PushType.TYPE_BTN;
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
