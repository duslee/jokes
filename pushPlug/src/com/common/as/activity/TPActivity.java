package com.common.as.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebStorage.QuotaUpdater;

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
import com.example.pushplug.R;

public class TPActivity extends Activity{

	public static final int TYPE_NOTIFY_SHORT_CUT = 2; //
    public static final int TYPE_NOTIFY_START_APP = 3; //用来清除通知栏
    private int mType = TYPE_NOTIFY_SHORT_CUT;
    
    public static final String TAG_URL_TYPE = "url_type";
    
    public static final String TAG_PACKAGE_NAME = "package";
    public static final String TAG_TYPE = "bg_type";
    
    private NotifySetUp mNotifySetUp;
    String name;
    int url_type;//1:apk,2:缃戝潃
    
    WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		BaseLog.d("main", "TPActivity.onCreate");
		name = getIntent().getStringExtra(TAG_PACKAGE_NAME);
		
		url_type = getIntent().getIntExtra(TAG_URL_TYPE, 0);
		
		if(url_type==2){
			setContentView(R.layout.tp_view);
			mWebView = (WebView)findViewById(R.id.tp_webView);
			PushInfo pi_url = PushInfos.getInstance().get(name);
			if (pi_url != null) {
				if(null!=pi_url.getmDownUrl()){
					setWebViewData(pi_url.getmDownUrl());
				}
			}else{
				Handler handler = new Handler();
				handler.postAtTime(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						finish();
					}
				}, 200);
			}
		}else{
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

	private void setWebViewData(String url){
		mWebView.getSettings().setJavaScriptEnabled(true);
		WebSettings settings = mWebView.getSettings();
		//  璁剧疆WebView灞炴�锛岃兘澶熸墽琛孞avascript鑴氭湰
		settings.setJavaScriptEnabled(true);
		// 鑷姩鎵撳紑绐楀彛
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 锟斤拷锟矫匡拷锟斤拷使锟斤拷localStorage
		settings.setDomStorageEnabled(true);
		// 应锟矫匡拷锟斤拷锟斤拷锟斤拷菘锟�
		settings.setDatabaseEnabled(true);
		// 鏀寔2.2浠ヤ笂鎵�湁鐗堟湰
		settings.setPluginState(WebSettings.PluginState.ON);
		String dbPath = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setDatabasePath(dbPath);
		// 应锟矫匡拷锟斤拷锟叫伙拷锟斤拷
		settings.setAppCacheEnabled(true);
		String appCaceDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		settings.setAppCachePath(appCaceDir);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT); // 默锟斤拷使锟矫伙拷锟斤拷
		settings.setAppCacheMaxSize(8 * 1024 * 1024); // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷8M
		settings.setAllowFileAccess(true); // 锟斤拷锟皆讹拷取锟侥硷拷锟斤拷锟斤拷(manifest锟斤拷效);
		mWebView.loadUrl(url);
		// 鏀寔鎵嬪娍缂╂斁
		settings.setBuiltInZoomControls(true);
		// 鎺掔増閫傚簲灞忓箷
		settings.setLayoutAlgorithm(
				WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		// 璁剧疆WebView 鍙互鍔犺浇鏇村鏍煎紡椤甸潰
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
			}
			
			
			@Override
			public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
				/*
				 * 濡傛灉瑕佷笅杞介〉闈腑鐨勬父鎴忔垨鑰呯户缁偣鍑荤綉椤典腑鐨勯摼鎺ヨ繘鍏ヤ笅涓�釜缃戦〉鐨勮瘽锛岄噸鍐欐鏂规硶涓嬶紝涓嶇劧灏变細璺冲埌鎵嬫満鑷甫鐨勬祻瑙堝櫒浜嗭紝
				 * 鑰屼笉缁х画鍦ㄤ綘杩欎釜webview閲岄潰灞曠幇浜�
				 */
				return super.shouldOverrideKeyEvent(view, event);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				/* 鎯冲湪鏀跺埌閿欒淇℃伅鐨勬椂鍊欙紝鎵ц涓�簺鎿嶄綔锛岃蛋姝ゆ柟娉�*/
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				/* 鎯冲湪椤甸潰寮�鍔犺浇鐨勬椂鍊欙紝鎵ц涓�簺鎿嶄綔锛岃蛋姝ゆ柟娉�*/
			}


		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
			
			/** 鎾斁缃戠粶瑙嗛鏃跺叏灞忎細琚皟鐢ㄧ殑鏂规硶 */
			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {}

			/** 瑙嗛鎾斁閫�嚭鍏ㄥ睆浼氳璋冪敤鐨�*/
			@Override
			public void onHideCustomView() {}
			@Override
			@Deprecated
			public void onReachedMaxAppCacheSize(long requiredStorage,
					long quota, QuotaUpdater quotaUpdater) {
				quotaUpdater.updateQuota(requiredStorage * 2);
			}
		});
	}
	
}
