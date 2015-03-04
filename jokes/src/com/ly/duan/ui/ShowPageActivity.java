package com.ly.duan.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ly.duan.utils.StringUtils;
import com.ly.duan.view.SildingFinishLayout;
import com.ly.duan.view.SildingFinishLayout.OnSildingFinishListener;
import com.sjm.gxdz.R;

@ContentView(R.layout.view_show_page)
public class ShowPageActivity extends BaseActivity {

	@ViewInject(R.id.webView)
	WebView mWebView;

	@ViewInject(R.id.back)
	private ImageView back;

	@ViewInject(R.id.title)
	private TextView title;

//	@ViewInject(R.id.ll)
//	private LinearLayout ll;
	@ViewInject(R.id.sfl)
	private SildingFinishLayout sfl;

	@ViewInject(R.id.rl)
	private RelativeLayout rl;
	
	@ViewInject(R.id.ll_head)
	private LinearLayout ll_head;
	
	@ViewInject(R.id.iv)
	private ImageView iv;
	
	@ViewInject(R.id.tv)
	private TextView tv;

	private View mView;

	private CustomViewCallback mCallback;
	private MyWebChromeClient client = null;

	String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initData();

		if (checkNetWork()) {
			setWebViewData();
		} else {
			getAlertDialog(ShowPageActivity.this);
		}

		if (savedInstanceState != null) {
			mWebView.restoreState(savedInstanceState);
		}
		
		initUI();
	}

	private void initUI() {
		sfl.setOnSildingFinishListener(new OnSildingFinishListener() {
			
			@Override
			public void onSildingFinish() {
				ShowPageActivity.this.finish();
			}
		});
//		sfl.setTouchView(sfl);
		sfl.setTouchView(mWebView);
	}

	@OnClick(R.id.back)
	public void backClicked(View view) {
		LogUtils.d("backClicked >>>>>>>>>>>");
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			ShowPageActivity.this.finish();
		}
	}

	private void initData() {
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		if (TextUtils.isEmpty(url)) {
			showToast("抱歉，该内容地址不存在！");
			ShowPageActivity.this.finish();
			return;
		}

		String titleString = getIntent().getStringExtra("title");
		if (!StringUtils.isBlank(titleString)) {
			title.setText(titleString);
		}
		
		if (getIntent().getBooleanExtra("isPostBar", false)) {
			tv.setText(getIntent().getStringExtra("userNick"));
			String imgUrl = getIntent().getStringExtra("imgUrl");
			if (!StringUtils.isBlank(imgUrl)) {
				getBitmapUtils().display(iv, imgUrl);
			} else {
				iv.setImageResource(R.drawable.icon);
			}
		}
	}

	private void getAlertDialog(Context context) {
		final Builder bd = new Builder(context);
		bd.setTitle("温馨提示");
		bd.setMessage("请检查网络设置是否正常!");
		bd.create().setButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				bd.create().cancel();
				ShowPageActivity.this.finish();
			}
		});
		bd.setCancelable(false);
		bd.create().show();
	}

	private void setWebViewData() {
		mWebView.getSettings().setJavaScriptEnabled(true);
		WebSettings settings = mWebView.getSettings();
		// 设置WebView属性，能够执行Javascript脚本
		settings.setJavaScriptEnabled(true);
		// 自动打开窗口
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		// ���ÿ���ʹ��localStorage
		settings.setDomStorageEnabled(true);
		// Ӧ�ÿ�������ݿ�
		settings.setDatabaseEnabled(true);
		// 支持2.2以上所有版本
		settings.setPluginState(WebSettings.PluginState.ON);
		String dbPath = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setDatabasePath(dbPath);
		// Ӧ�ÿ����л���
		settings.setAppCacheEnabled(true);
		String appCaceDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		settings.setAppCachePath(appCaceDir);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT); // Ĭ��ʹ�û���
		settings.setAppCacheMaxSize(8 * 1024 * 1024); // ������������8M
		settings.setAllowFileAccess(true); // ���Զ�ȡ�ļ�����(manifest��Ч);
		mWebView.loadUrl(url);
		// mWebView.loadDataWithBaseURL(url, null, "text/html", "UTF-8", null);
		// 支持手势缩放
		settings.setBuiltInZoomControls(true);
		// 排版适应屏幕
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		// 设置WebView 可以加载更多格式页面
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebChromeClient(client);
	}

	/** 退出全屏 */
	public void quitFullScreen() {
		// 声明当前屏幕状态的参数并获取
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(attrs);
		getWindow()
				.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/** 进入全屏 */
	public void setFullScreen() {
		// 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mWebView.saveState(outState);
	}
	
	@Override
	public void onBackPressed() {
		LogUtils.d("onBackPressed >>>>>>>>>>>");
		if (null != mView) {
			client.onHideCustomView();
		} else {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				ShowPageActivity.this.finish();
				super.onBackPressed();
			}
		}
		
	}

//	private void hideView() {
//		if (null == mView) {
//			return;
//		}
//		/* 处理屏幕播放视频的方式:竖屏显示 */
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		if (null != mCallback) {
//			mCallback.onCustomViewHidden();
//			mCallback = null;
//		}
//		video_ll.removeView(mView);
//		mView = null;
//		video_ll.setBackgroundColor(Color.WHITE);
//		video_ll.addView(title_rl);
//		video_ll.addView(mWebView);
//		// 退出全屏参数设置
//		quitFullScreen();
//	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (event.getAction() == KeyEvent.ACTION_DOWN) {
//			if (keyCode == KeyEvent.KEYCODE_BACK) {
//				LogUtils.e("mWebView.canGoBack: " + mWebView.canGoBack());
//				if (mWebView.canGoBack()) {
//					mWebView.goBack();
//					return false;
//				}
//				if (mView != null) {
//					LogUtils.e("mView != null");
//					hideView();
//					return false;
//				} else {
//					LogUtils.e("finish");
//					finish();
//				}
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mWebView) {
			((ViewGroup) mWebView.getParent()).removeView(mWebView);
			/* 释放WebView的资源 */
			mWebView.removeAllViews();
			mWebView.destroy();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWebView.onPause();
	}
	
	public class MyWebChromeClient extends WebChromeClient {

		/** 播放网络视频时全屏会被调用的方法 */
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (null != mView) {
				callback.onCustomViewHidden();
				return;
			}
			/* 处理屏幕播放视频的方式:横屏显示 */
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			ll.removeView(rl);
//			if (null != ll_head) {
//				ll.removeView(ll_head);
//			}
//			ll.removeView(mWebView);
//			ll.setBackgroundColor(Color.BLACK);
//			ll.addView(view);
			sfl.removeView(rl);
			if (null != ll_head) {
				sfl.removeView(ll_head);
			}
			sfl.removeView(mWebView);
			sfl.setBackgroundColor(Color.BLACK);
			sfl.addView(view);
			
			mView = view;
			mCallback = callback;
			// 进入全屏参数设置
			setFullScreen();
		}

		/** 视频播放退出全屏会被调用的 */
		@Override
		public void onHideCustomView() {
			if (null == mView) {
				return;
			}
			/* 处理屏幕播放视频的方式:竖屏显示 */
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			if (null != mCallback) {
				mCallback.onCustomViewHidden();
				mCallback = null;
			}
//			ll.removeView(mView);
//			mView = null;
//			ll.setBackgroundColor(Color.WHITE);
//			ll.addView(rl);
//			if (null != ll_head) {
//				ll.addView(ll_head);
//			}
//			ll.addView(mWebView);
			sfl.removeView(mView);
			mView = null;
			sfl.setBackgroundColor(Color.WHITE);
			sfl.addView(rl);
			if (null != ll_head) {
				sfl.addView(ll_head);
			}
			sfl.addView(mWebView);
			
			// 退出全屏参数设置
			quitFullScreen();
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}

		@Override
		@Deprecated
		public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
				QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(requiredStorage * 2);
		}

	}
	
}
