package com.common.as.pushtype;

import com.common.as.main.Main;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.AppListBackService;
import com.common.as.service.BackService;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.view.TableView;
import com.common.as.view.TableView.OnTopViewClick;
import com.example.pushplug.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class AppStoreList extends PushBaseUtil{
	
	TableView mtTableView = null;
	
	public AppStoreList(TableView mTableView){
		this.mtTableView = mTableView;
	}
	
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
	protected void pushPaser(final Context ctx, PushInfo pi, Bitmap iconBmp) {
		// TODO Auto-generated method stub
//		super.pushPaser(ctx, pi, iconBmp);
//		PushInfoActionPaser.doClick(ctx, getPushType(), pi.packageName);
//		SharedPreferences sp = ctx.getSharedPreferences(AppPrefs.APP_INFO, ctx.MODE_PRIVATE);
//			if(!sp.getBoolean(AppPrefs.APPLIST_ISSHOW, false)){
//				sp.edit().putBoolean(AppPrefs.APPLIST_ISSHOW, true).commit();
//			}
//			mtTableView.setIcBmp(BitmapFactory.decodeResource(ctx.getResources(),
//					R.drawable.push_list_icon));
//			mtTableView.fun();
			Intent intent = new Intent(ctx, AppListBackService.class);
			intent.putExtra("tableview", mtTableView);
			intent.putExtra(AppListBackService.TAG_TOP_WND_TYPE,AppListBackService.TYPE_BG_STORELIST);
			intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			ctx.startService(intent); 
//		}
	}
}
