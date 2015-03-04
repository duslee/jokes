package com.common.as.pushtype;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.BackService;
import com.common.as.store.AppListManager;
import com.common.as.store.MaxDownInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.DateUtil;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;

public class PushFactory {

	public static final int ERR_ID_INSTALLED = 1;
	public static final int ERR_ID_PUSHED = 2;
	public static final int ERR_ID_ELSE = 3;
	private  static PushInfo pi = null;
	private  static PushInfo pi_pop = null;
	private  static PushInfo pi_bg = null;
	private  static PushInfo pi_short_cut = null;
	public static int paserPush(final PushType type, final Context ctx, PushInfo mPushInfo){
		 pi = mPushInfo;
		if (type == PushType.TYPE_STORE_LIST) {
			PushUtil pu = new PushStoreList();
			pu.doPush(ctx, pi, null);

		} else {
			if (null == pi) {
				return ERR_ID_ELSE;
			}
			if (isInstalled(ctx, pi.getPackageName())) {
				Log.d("main", "isInstalled");
				return ERR_ID_INSTALLED;
			}
			//Log.d("main", "pi.type="+type+",,pi.url="+pi.imageUrl);
//			PushInfo temp = PushInfos.getInstance().get(pi.getPackageName());
//			if(type == PushType.TYPE_POP_WND){
//				if(null != temp ){
//					PushInfo pip = AppListManager.findPushInfo(ctx, PushType.TYPE_POP_WND);
//					if(null!=pip){
//						pi = pip;
//					}
//				}else{
//					BaseLog.d("main", "type="+type+",,temp==null");
//				}
//			}else if(type == PushType.TYPE_BACKGROUND){
//				if(null != temp ){
//					PushInfo pip = AppListManager.findPushInfo(ctx, PushType.TYPE_BACKGROUND);
//					if(null!=pip){
//						pi = pip;
//					}
//				}else{
//					BaseLog.d("main", "type="+type+",,temp==null");
//				}
//			}else if(type == PushType.TYPE_SHORTCUT){
//				if(null != temp ){
//					PushInfo pip = AppListManager.findPushInfo(ctx, PushType.TYPE_SHORTCUT);
//					if(null!=pip){
//						pi = pip;
//					}
//				}else{
//					BaseLog.d("main", "type="+type+",,temp==null");
//				}
//			}
			PushInfo temp = PushInfos.getInstance().get(pi.getPackageName());
			if(type == PushType.TYPE_POP_WND){
				pi_pop = mPushInfo;
			}else if(type == PushType.TYPE_BACKGROUND){
				pi_bg = mPushInfo;
			}else if(type == PushType.TYPE_SHORTCUT){
				pi_short_cut = mPushInfo;
			}
			BaseLog.d("main", "pi.imageUrl="+pi.imageUrl);
			BitmapLoder lbiv = new BitmapLoder(ctx);
			lbiv.startLoad(new OnLoadBmp() {
				
				@Override
				public void onBitmapLoaded(Bitmap bmp) {
					// TODO Auto-generated method stub
					PushUtil pu = null;
					switch (type) {
					case TYPE_TOP_WND:
						pu= new PushTopWnd();
						if (null != pu) {
							BaseLog.d("main", "pu!=null");
							pu.doPush(ctx, pi, bmp);
						}
						break;
					case TYPE_SHORTCUT:
						BaseLog.d("main", "PushFactory.PushShortCut");
						pu = new PushShortCut();
						if (null != pu) {
							BaseLog.d("main", "pu!=null");
							pu.doPush(ctx, pi_short_cut, bmp);
						}
						break;
					case TYPE_POP_WND:
						BaseLog.d("main", "PushFactory.pushPopWnd");
						pu  = new PushPopWnd();
						if (null != pu) {
							BaseLog.d("main1", "PushPopWnd.doPush");
							pu.doPush(ctx, pi_pop, bmp);
						}
						break;
					case TYPE_BACKGROUND:
						BaseLog.d("main1", "PushFactory.PushBgDown");
						pu = new PushBgDown();
						if (null != pu) {
							BaseLog.d("main1", "PushBgDown.doPush");
							pu.doPush(ctx, pi_bg, bmp);
						}
						break;

					default:
						break;
					}
						
//					if (null != pu) {
//						Log.d("main", "pu!=null");
//						pu.doPush(ctx, pi, bmp);
//					}
				}
			}, pi.imageUrl);
		}
		



		return ERR_ID_ELSE;
	}
	
	
	private static boolean isPushed(String packagename){
		PushInfo pi = PushInfos.getInstance().get(packagename);
		if (pi == null) {
			return false;
		}
		return true;
	}
	
	
	public static boolean isInstalled(Context ctx,String name){
		PackageInfo packageInfo;
		
	//	List<PackageInfo> list = getPackageManager().getInstalledPackages(0);

	    try {
	        packageInfo = ctx.getPackageManager().getPackageInfo(
	        		name.trim(), 0);

	    } catch (NameNotFoundException e) {
	        packageInfo = null;
	       // e.printStackTrace();
	    }
	    if(packageInfo ==null){
	        return false;
	    }else{
	        return true;
	    }
	}
}
