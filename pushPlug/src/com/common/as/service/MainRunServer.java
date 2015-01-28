package com.common.as.service;

import java.util.ArrayList;

import com.common.as.base.log.BaseLog;
import com.common.as.main.MainRun;
import com.common.as.pushtype.PushInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppUtil;
import com.common.as.utils.CommonUtils;
import com.common.as.utils.DateUtil;
import com.common.as.utils.Utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class MainRunServer extends Service{

	private static final String TAG = "MainRunServer";
	private static int DELAY_TIME = 30*60*1000;
	private Handler mHandler;
	private MainRun mMainRun;
	private Handler bg_handler = new Handler(){
		public void handleMessage(Message msg) {
//			BaseLog.d("main4", "mainrunserver.handlmessage");
			if(msg.what == 1){
//				BaseLog.d("main4", "Utils.isApplicationBroughtToBackground(MainRunServer.this)="+Utils.isApplicationBroughtToBackground(MainRunServer.this));
//				BaseLog.d("main4", "Utils.isHome(MainRunServer.this,mActivityManager)="+Utils.isHome(MainRunServer.this,mActivityManager));
				if(Utils.isHome(MainRunServer.this,mActivityManager) && Utils.isApplicationBroughtToBackground(MainRunServer.this)){
					if(!Utils.isInstalerActivity(MainRunServer.this)){
						if(null!=mMainRun){
							mMainRun.checkNeedNotify();
						}
					}
				}
				bg_handler.sendEmptyMessageDelayed(1, 1000);
			}
		};
	};
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static void setDELAY_TIME(int dELAY_TIME) {
		DELAY_TIME = dELAY_TIME;
	}


ActivityManager mActivityManager = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		BaseLog.i("main", "MainRunServer.onCreate");
		mHandler = new Handler();
//		bg_handler.sendEmptyMessage(1);
		mMainRun = new MainRun(this);
	}

	
	private void startRun(int delay){
		BaseLog.d(TAG, "startRun");
		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(mMainRun, delay);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startRun(0);
			}
		},DELAY_TIME+delay);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		BaseLog.d(TAG, "MainRunServer.onStart");
		startRun(0);
	}


	@Override
    public void onDestroy() {
        super.onDestroy();
      //  mLoadAction.removeNotifer(this);
        // 进程被关闭后能够自动启动
        BaseLog.i("main", "MainRunServer.onDestroy");
        Intent serviceIntent = new Intent(this, MainRunServer.class);
        serviceIntent.putExtra("caller", "LifeService");
        this.startService(serviceIntent);
    }
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //	MyLog.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        flags = START_STICKY;        
        return super.onStartCommand(intent, flags, startId);
    }
}
