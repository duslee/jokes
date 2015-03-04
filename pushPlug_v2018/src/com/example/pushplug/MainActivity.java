package com.example.pushplug;

import java.util.ArrayList;

import com.common.as.activity.ItemListActivity;
import com.common.as.base.log.BaseLog;
import com.common.as.network.HttpUtil;
import com.common.as.pushtype.PushFactory;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.AppListManager;
import com.common.as.utils.PopupUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	private Button btnTestFlyWnd;
	private Button btnTestShortCut;
	private Button btnTestPopWnd;
	private Button buttonTestBg;
	private Button buttonPushSwitch;
	private Button buttonAskStore1;
	private Button buttonAskStore2;
	private Button buttonStore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		//Ppinterface.getInstance().startRun(this);
//		ImageFetcherCreater.getImageFetcher(this);
//		
//		btnTestFlyWnd = (Button)findViewById(R.id.buttonTestFlyWnd);
//		btnTestFlyWnd.setOnClickListener(this);
//		
//		btnTestShortCut = (Button)findViewById(R.id.buttonTestShortCut);
//		btnTestShortCut.setOnClickListener(this);
//		
//		btnTestPopWnd = (Button)findViewById(R.id.buttonTestPopWnd);
//		btnTestPopWnd.setOnClickListener(this);
//		
//		buttonTestBg = (Button)findViewById(R.id.buttonTestBg);
//		buttonTestBg.setOnClickListener(this);
//		
//		buttonPushSwitch = (Button)findViewById(R.id.buttonPushSwitch);
//		buttonPushSwitch.setOnClickListener(this);
//		
//		
//		buttonAskStore1 = (Button)findViewById(R.id.buttonAskStore1);
//		buttonAskStore1.setOnClickListener(this);
//		buttonAskStore2 = (Button)findViewById(R.id.buttonAskStore2);
//		buttonAskStore2.setOnClickListener(this);
//		
//		buttonStore = (Button)findViewById(R.id.buttonStore);
//		buttonStore.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int errid = -1;
		PushInfo pi = null;
		if (v.equals(btnTestShortCut)) {
			pi = createPushInfo(PushType.TYPE_SHORTCUT);
			errid = PushFactory.paserPush(PushType.TYPE_SHORTCUT, this, pi);
		}else if (v.equals(btnTestFlyWnd)) {
			pi = createPushInfo(PushType.TYPE_TOP_WND);
			errid = PushFactory.paserPush(PushType.TYPE_TOP_WND, this, pi);
		}else if (v.equals(btnTestPopWnd)) {
			pi = createPushInfo(PushType.TYPE_POP_WND);
			errid = PushFactory.paserPush(PushType.TYPE_POP_WND, this, pi);
		}else if (v.equals(buttonTestBg)) {
			pi = createPushInfo(PushType.TYPE_BACKGROUND);
			errid = PushFactory.paserPush(PushType.TYPE_BACKGROUND, this, pi);
		}else if (v.equals(buttonPushSwitch)) {
			HttpUtil mHttpUtil = new HttpUtil(this);
			HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(HttpUtil.KEY_PUSH_SWITCH) {
				
				@Override
				public void onSuccess(int what, Object obj) {
					// TODO Auto-generated method stub
					PopupUtils.showShortToast(getApplicationContext(), obj.toString());
				}
				
				@Override
				public void onFailed(int what, Object obj) {
					// TODO Auto-generated method stub
					PopupUtils.showShortToast(getApplicationContext(), "test failed");
				}
			};
			mHttpUtil.startRequest(mRequestData);
		}else if (v.equals(buttonAskStore1)) {
			HttpUtil mHttpUtil = new HttpUtil(this);
			HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(HttpUtil.KEY_STORE_LIST) {
				
				@Override
				public void onSuccess(int what, Object obj) {
					// TODO Auto-generated method stub
					if (obj != null) {
						PopupUtils.showShortToast(getApplicationContext(), obj.toString());

					}else{
						PopupUtils.showShortToast(getApplicationContext(), "no data");

					}
				}
				
				@Override
				public void onFailed(int what, Object obj) {
					// TODO Auto-generated method stub
					PopupUtils.showShortToast(getApplicationContext(), "test failed");
				}
			};
			mHttpUtil.startRequest(mRequestData);
		}else if (v.equals(buttonAskStore2)) {
			HttpUtil mHttpUtil = new HttpUtil(this);
			HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(HttpUtil.KEY_POP_LIST) {
				
				@Override
				public void onSuccess(int what, Object obj) {
					// TODO Auto-generated method stub
					if (obj != null) {
						PopupUtils.showShortToast(getApplicationContext(), obj.toString());

					}else{
						PopupUtils.showShortToast(getApplicationContext(), "no data");

					}				}
				
				@Override
				public void onFailed(int what, Object obj) {
					// TODO Auto-generated method stub
					PopupUtils.showShortToast(getApplicationContext(), "test failed");
				}
			};
			mHttpUtil.startRequest(mRequestData);
		}else if (v.equals(buttonStore)) {
			startActivity(new Intent(this, ItemListActivity.class));
		}
		
		if (errid == PushFactory.ERR_ID_INSTALLED) {
			PopupUtils.showShortToast(this, pi.getAppName()+"已经安装过");
		}else if (errid == PushFactory.ERR_ID_PUSHED) {
			PopupUtils.showShortToast(this, pi.getAppName()+"已经push过");
		}
	}
	
	
	private PushInfo createPushInfo(PushType pushType){
		int listtype = AppListManager.getListType(pushType);
		ArrayList<PushInfo> lists = AppListManager.getApplists(listtype);
		PushInfo pi = null;
		if (null == lists || lists.size() <= 0) {
			PopupUtils.showShortToast(this, "请先下载列表");
		}else{
			int count = (int)(Math.random() * (lists.size()-1));
			pi = lists.get(count);
		}
		// = new PushInfo("cn.com.sina.sports", "123", "2");
//		pi.setAppName("sina");
//		pi.setImageUrl("http://i2.sinaimg.cn/ty/deco/2013/0729/images/0729_zyc_sports_logo.jpg");
//		pi.setmDownUrl("http://i3.sinaimg.cn/lf/mobile/sports/SinaSports_v2.5.0.12_3027_0001.apk");
		return pi;
	}

}
