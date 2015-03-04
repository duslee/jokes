package com.common.as.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushInfoActionPaser;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.NotifySetUp;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppUtil;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;
import com.common.as.utils.PopupUtils;

public class TPActivity extends Activity{

	public static final int TYPE_NOTIFY_SHORT_CUT = 2; //
    public static final int TYPE_NOTIFY_START_APP = 3; //用来清除通知栏
    private int mType = TYPE_NOTIFY_SHORT_CUT;
    public static final String TAG_PACKAGE_NAME = "package";
    public static final String TAG_TYPE = "bg_type";
    
    private NotifySetUp mNotifySetUp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		BaseLog.d("main", "TPActivity.onCreate");
		String name = getIntent().getStringExtra(TAG_PACKAGE_NAME);
		mType = getIntent().getIntExtra(TAG_TYPE, TYPE_NOTIFY_SHORT_CUT);
		if (mType == TYPE_NOTIFY_START_APP) {
		//	BaseLog.d(TAG, "start="+name);
			PushInfo pi = PushInfos.getInstance().get(name);
			if (pi != null) {
				if (null == mNotifySetUp) {
					mNotifySetUp = new NotifySetUp(this);
				}
			//	BaseLog.d(TAG, "cancel="+pi.getAppid());
				mNotifySetUp.cancelNotification(Integer.valueOf(pi.getAppid()));
				BaseLog.d("main", "TpActivity.启动app");
				PointUtil.SendPoint(this, new PointInfo(PointUtil.POINT_ID_START_UP, pi));
				AppUtil.startApp(this, name);
			}
			
		} else {
			if (null != name) {
				if (!AppUtil.isInstalled(this, name)) {
						PopupUtils.showShortToast(this, "软件优化中，请稍候");
				}
				PushInfoActionPaser.doClick(this, PushType.TYPE_SHORTCUT, name);
			}
		}

		
		Handler handler = new Handler();
		handler.postAtTime(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
			}
		}, 200);
		
	}

	
	
}
