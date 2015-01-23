package com.common.as.pushtype;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.AppListManager;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.AppUtil;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.PointUtil;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.PointUtil.PointInfo;
import com.example.pushplug.R;
import com.mozillaonline.providers.DownloadManager;
import com.mozillaonline.providers.DownloadManager.Request;

public class PushInfoActionPaser {
	private static final String TAG = "main";

	private static DownloadManager mDownloadManager;
	private static Handler mHandler = new Handler();
	
	public static void doClick(final Context ctx,final PushType type,final String name){	
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if (null == mDownloadManager) {
					mDownloadManager = new DownloadManager(ctx.getContentResolver(), 
							ctx.getPackageName());
				}
				BaseLog.d("main1", "doClick.name="+name+",,doClick.type="+type);
				PushInfo pid = PushInfos.getInstance().get(name);
				//BaseLog.d("main1", "pid.type="+pid.getPushType());
				if (null != pid) {
					
					if (AppUtil.isInstalled(ctx, pid.packageName)) {
						PointUtil.SendPoint(ctx, new PointInfo(PointUtil.POINT_ID_START_UP, pid));
						AppUtil.startApp(ctx, pid.packageName);
					}else{
						BaseLog.d("main1", "doClick.type="+type);
						if(type.equals(PushType.TYPE_STORE_LIST)){
							pid.setPushType(type);
							PushInfos.getInstance().put(name, pid);
						}
						startDown(ctx,pid);
					}
				}else{
					BaseLog.d("main1", "null == pid");
				}
			}
		});

		
	}
	
	
	
	private static void startDown(Context ctx, PushInfo pi){
		long id = isInDownDatabase(mDownloadManager, pi.getmDownUrl());
		BaseLog.d("main", "startDown.id="+id);
		if (id > 0) {
			PointUtil.SendPoint(ctx, new PointInfo(PointUtil.POINT_ID_SETUP, pi));
			mDownloadManager.resumeDownload(ctx,pi,id);
		}else{
			PointUtil.SendPoint(ctx,new PointInfo(PointUtil.POINT_ID_START_DOWN,pi));
			addDownItem(mDownloadManager, pi,ctx);
		}
	}

	
	public static void addDownItem(DownloadManager mDownloadManager, PushInfo item,Context ctx){
		item.setStatus(PushInfo.STATUS_DOWN_STARTING);
		PushInfos.getInstance().put(item.getPackageName(), item);
		if(item.getPushType()!=PushType.TYPE_SHORTCUT){
			if(!AppPrefs.isControlShowPop){
				AppPrefs.mBitmap = null;
				PushInfo mPushInfo = AppListManager.findPushInfo(ctx, item.getPushType());
				BaseLog.d("main", "PushInfoActionPaser.startloadBmp111");
				if(null != mPushInfo){
					BaseLog.d("main", "PushInfoActionPaser.startloadBmp222");
					BitmapLoder lbiv = new BitmapLoder(ctx);
					lbiv.startLoad(new OnLoadBmp() {
						@Override
						public void onBitmapLoaded( Bitmap bmp) {
							AppPrefs.mBitmap = bmp;
							AppPrefs.bmpUpdate = 1;
						}
					}, mPushInfo.imageUrl);
				}
			}
		}
		String url = item.getmDownUrl();
		Uri srcUri = Uri.parse(url);
		DownloadManager.Request request = new Request(srcUri);
		request.setDestinationInExternalPublicDir(
				DownloadManager.DEFAULT_DIR, "/");
		//request.setDescription(item.getmDescripion());
		request.setTitle(item.getAppName());
		request.setDescription(item.getmBrief());
//		request.setMimeType(PushInfo.MIME_APP);
		request.setMimeType(PushInfo.getMIME_APP());
		request.setPicUrl(item.getImageUrl());
		request.setPackageName(item.packageName);
		
		BaseLog.d("main1", "addDownItem pushtype="+item.getPushType() + ";package="+item.getPackageName()+";url="+item.getmDownUrl());
		
//		switch (item.getPushType()) {
//		case TYPE_BACKGROUND:
//		case TYPE_SHORTCUT:
//			request.setShowRunningNotification(false);		
//			break;
//
//		default:
//			break;
//		}
		mDownloadManager.enqueue(request);

	}
	
	
	public static long isInDownDatabase(DownloadManager mDownloadManager,String uri){
		long id = -1;
		DownloadManager.Query query = new DownloadManager.Query().setOnlyIncludeVisibleInDownloadsUi(true);
		query.setFilterByUri(uri);
		Cursor cursor = mDownloadManager.query(query);
		if (null != cursor) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));

			} 
			cursor.close();
		}
		
		return id;

	}
	
	
	
	

}
