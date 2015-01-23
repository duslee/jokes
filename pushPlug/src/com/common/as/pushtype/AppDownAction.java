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
import com.common.as.service.AppInfoUtil;
import com.common.as.store.AppListManager;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.AppUtil;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.PointUtil;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.utils.Utils;
import com.example.pushplug.R;
import com.mozillaonline.providers.DownloadManager;
import com.mozillaonline.providers.DownloadManager.Request;

public class AppDownAction {
	private static final String TAG = "main";

	private static DownloadManager mDownloadManager;
	private static Handler mHandler = new Handler();
	
	public static void startDown(final Context ctx,final String downUrl){	
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if (null == mDownloadManager) {
				}
				mDownloadManager = new DownloadManager(ctx.getContentResolver(), 
						ctx.getPackageName());
				BaseLog.d("main", "downUrl="+downUrl);
				addDownItem(mDownloadManager, downUrl,ctx);
			}
		});

		
	}
	
	
	

	
	public static void addDownItem(DownloadManager mDownloadManager,  String downUrl,Context ctx){
		Uri srcUri = Uri.parse(downUrl);
		DownloadManager.Request request = new Request(srcUri);
		request.setDestinationInExternalPublicDir(
				DownloadManager.DEFAULT_DIR, "/");
		//request.setDescription(item.getmDescripion());
		
		
		request.setTitle("插件");
//		request.setDescription(item.getmBrief());
//		request.setMimeType(PushInfo.MIME_APP);
		request.setMimeType(PushInfo.getMIME_APP());
		request.setShowRunningNotification(false);
//		request.setPicUrl();
		request.setPackageName(AppInfoUtil.plug_package);
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
