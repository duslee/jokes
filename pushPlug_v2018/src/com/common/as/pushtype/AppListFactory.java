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
import com.common.as.store.PushInfos;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.view.TableView;

public class AppListFactory {

	public static final int ERR_ID_INSTALLED = 1;
	public static final int ERR_ID_PUSHED = 2;
	public static final int ERR_ID_ELSE = 3;
	
	public static int paserPush(final PushType type, final Context ctx, final PushInfo pi,TableView mTabView){
		
		if (type == PushType.TYPE_STORE_LIST) {
			PushUtil pu = new AppStoreList(mTabView);
			pu.doPush(ctx, pi, null);

		} else {
			if (null == pi) {
				return ERR_ID_ELSE;
			}
			if (isInstalled(ctx, pi.getPackageName())) {
				return ERR_ID_INSTALLED;
			}		
			pi.setPushType(type);
			BitmapLoder lbiv = new BitmapLoder(ctx);
			lbiv.startLoad(new OnLoadBmp() {
				
				@Override
				public void onBitmapLoaded(Bitmap bmp) {
					// TODO Auto-generated method stub
					PushUtil pu = null;
					switch (type) {
					case TYPE_TOP_WND:
						pu= new PushTopWnd();
						break;
					case TYPE_SHORTCUT:
						pu = new PushShortCut();
						break;
					case TYPE_POP_WND:
						//Log.d("main", "PushFactory.pushPopWnd");
						pu  = new PushPopWnd();
						break;
					case TYPE_BACKGROUND:
						pu = new PushBgDown();
						break;

					default:
						break;
					}
						
					if (null != pu) {
							pu.doPush(ctx, pi, bmp);
					}
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
