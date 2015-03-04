package com.common.as.pushtype;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.common.as.base.log.BaseLog;
import com.common.as.service.ControlDialogBackService;
import com.common.as.service.DialogBackService;
import com.common.as.store.AppListManager;
import com.common.as.utils.AppPrefs;
import com.common.as.view.DailogView;

public class PushPopWnd extends PushBaseUtil {

	ActivityManager mActivityManager = null;
	DailogView view = null;
	String packageName = "";

	@Override
	protected void pushPaser(final Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
		super.pushPaser(ctx, pi, iconBmp);
		BaseLog.i("main", "PushPopWnd.pushPaser");
		BaseLog.i("main", "AppPrefs.isControlShowPop="+AppPrefs.isControlShowPop);
		BaseLog.i("main", "ControlDialogBackService.isRunning="+ControlDialogBackService.isRunning);
		if (!AppPrefs.isControlShowPop) {
			if (!ControlDialogBackService.isRunning) {
				Intent intent = new Intent(ctx, ControlDialogBackService.class);
				intent.putExtra("pi", pi);
				// intent.putExtra(BackService.TAG_TOP_WND_TYPE,BackService.TYPE_BG_STORELIST);
				intent.putExtra("iconBmp", iconBmp);
				intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
				ctx.startService(intent);
			}
		} else {
			if (!DialogBackService.isRunning) {
				Intent intent = new Intent(ctx, DialogBackService.class);
				intent.putExtra("pi", pi);
				// intent.putExtra(BackService.TAG_TOP_WND_TYPE,BackService.TYPE_BG_STORELIST);
				intent.putExtra("iconBmp", iconBmp);
				intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
				ctx.startService(intent);
			}
		}
		// mActivityManager = (ActivityManager)
		// ctx.getSystemService(Context.ACTIVITY_SERVICE);
		// if(!Utils.isHome(ctx,
		// mActivityManager)&&!Utils.isApplicationBroughtToBackground(ctx)){
		// if(null == PushInfos.getInstance().get(pi.packageName)){
		// return;
		// }else{
		// packageName = pi.getPackageName();
		// if(null == view){
		// Log.d("main", "DialogView");
		// view = new DailogView(ctx, new OnDownLoadBtnClick() {
		//
		// @Override
		// public void onClick1() {
		// PushInfoActionPaser.doClick(ctx, PushType.TYPE_POP_WND, packageName);
		// view.removeTipView();
		// }
		// }, new OnCancelBtnClick() {
		//
		// @Override
		// public void onClick2() {
		// view.removeTipView();
		// }
		// });
		// view.setPushInfo(pi);
		// view.setIcBmp(iconBmp);
		// view.fun();
		// }
		// }
		// // Intent intent = new Intent(ctx, DlgActivity.class);
		// // intent.putExtra(INTENT_PACKAGE_NAME, pi.getPackageName());
		// // intent.putExtra(INTENT_ICON_BMP, iconBmp);
		// // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// // ctx.startActivity(intent);
		// }
	}

	@Override
	public PushType getPushType() {
		// TODO Auto-generated method stub
		return PushType.TYPE_POP_WND;
	}

	@Override
	public boolean isCanPush(PushInfo pi) {

		if (AppListManager.getmSwitchInfo().getmPopSwitch() == 1) {
			if (super.isCanPush(pi)) {
				return true;
			}
		}
		return false;
	}

}
