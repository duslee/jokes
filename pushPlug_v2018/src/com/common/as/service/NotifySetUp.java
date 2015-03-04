package com.common.as.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import com.common.as.activity.TPActivity;
import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushInfo;
import com.common.as.utils.AppUtil;
import com.example.pushplug.R;
import com.mozillaonline.providers.downloads.Constants;
import com.mozillaonline.providers.downloads.DownloadReceiver;
import com.mozillaonline.providers.downloads.Downloads;
import com.mozillaonline.providers.downloads.RealSystemFacade;

public class NotifySetUp {

	RealSystemFacade mSystemFacade;
	Context mContext;
	
	
	public NotifySetUp(Context mContext) {
		super();
		// TODO Auto-generated constructor stub
		mSystemFacade = new RealSystemFacade(mContext);
		this.mContext = mContext;
	}
	
	public void postSetupNotify(PushInfo pid, Bitmap bmp, String str){
		BaseLog.d("main2", "AppUtil.postSetupNotify");
		Intent intent = AppUtil.getSetUpIntentForPackage(mContext,pid.getPackageName(),pid);
		
		if (null == intent) {
			return;
		}
		postNotify(pid, bmp, str, PendingIntent.getActivity(mContext, 0, intent, 0));
	}

	public void postStartUpNotify(PushInfo pid, Bitmap bmp, String str){
//		Intent intent = new Intent(mContext, TPActivity.class);
//		intent.putExtra(TPActivity.TAG_TYPE, TPActivity.TYPE_NOTIFY_START_APP);
//		intent.putExtra(TPActivity.TAG_PACKAGE_NAME, pid.getPackageName());
		Intent intent=	AppUtil.getLaunchIntentForPackage(mContext,pid.getPackageName());
		
		BaseLog.d("postStartUpNotify", "added2="+pid);
		postNotify(pid, bmp, str, PendingIntent.getActivity(mContext, 0, intent, 0));
	}

	public void postNotify(PushInfo pid, Bitmap bmp, String str, PendingIntent pIntent){
		Notification n = new Notification();
		BaseLog.d("main", "NotifySetUp.postNotity");
		n.tickerText = pid.getAppName()+"下载完成，安装体验超爽刺激";
        n.icon = R.drawable.icon_small;
//        n.flags |= Notification.FLAG_ONGOING_EVENT;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        // Build the RemoteView object
        BaseLog.d("main3", "mContext.getPackageName()="+mContext.getPackageName());
        RemoteViews expandedView = new RemoteViews(mContext.getPackageName(),
                R.layout.status_bar_startup);
        if(pid.getmBrief().equals(str)){
        	expandedView.setTextViewText(R.id.textTitle, pid.getAppName()+"-请安装");
        }else{
        	
        	expandedView.setTextViewText(R.id.textTitle, pid.getAppName());
        }
        expandedView.setTextViewText(R.id.textBrief, str);
        if (bmp != null) {
        	expandedView.setImageViewBitmap(R.id.appIcon, bmp);
		}
        
        n.contentView = expandedView;

        n.contentIntent = pIntent;

    //    n.setLatestEventInfo(mContext, pid.getAppName(), str, pIntent);
        try {
        	BaseLog.d("main3", "NotifySetUp.pid.getAppid="+Integer.valueOf(pid.getAppid()));
        	mSystemFacade.postNotification(Integer.valueOf(pid.getAppid()), n);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        
	}
	
	
	public void cancelNotification(int id){
        try {
        	mSystemFacade.cancelNotification(id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
