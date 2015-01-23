package com.ly.duan.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import com.common.activelog.ActiveLogUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.ly.duan.service.InitDataService;
import com.ly.duan.utils.ActivityAnimator;
import com.ly.duan.utils.NetUtils;
import com.ly.duan.utils.ResourceUtils;
import com.sjm.gxdz.R;

@ContentView(R.layout.view_welcome)
public class WelcomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 1. Dependency inject */
		ViewUtils.inject(this);

		/* 2. check internet */
		checkInternet();

		// LogUtils.e("imei=" + DeviceUtils.getIMEI(this));

		/* 3. init Operation */
		initOperation();

		/* 4. go to MainActivity */
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(WelcomeActivity.this,
						MainActivity.class));
				new ActivityAnimator().pushLeftAnimation(WelcomeActivity.this);
				WelcomeActivity.this.finish();
			}
		}, 3 * 1000);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void initOperation() {
		/* 1. get data from assets */
		String appid = ResourceUtils.getFileFromAssets(WelcomeActivity.this,
				"appid.txt");
		String channelId = ResourceUtils.getFileFromAssets(
				WelcomeActivity.this, "ZYF_ChannelID");

		LogUtils.e("appid=" + appid + ", channelId=" + channelId);

		/* 2. registe BroadcastReceiver & start service to init data from server */
		IntentFilter filter = new IntentFilter(InitDataService.ACTION);
		registerReceiver(receiver, filter);

		Intent service = new Intent(WelcomeActivity.this, InitDataService.class);
		service.putExtra("appid", Long.parseLong(appid));
		startService(service);

		/* 3. active log */
		ActiveLogUtil.sendActiveLog(0, WelcomeActivity.this, appid, channelId);
	}

	private void checkInternet() {
		// if (!NetUtils.isConnected(this)) {
		// if (!NetUtils.haveInternet(this)) {
		if (!NetUtils.isnetWorkAvilable(this)) {
			showToast(R.string.toast_network);
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equalsIgnoreCase(InitDataService.ACTION)) {
				int bc_type = intent.getIntExtra("bc_type", 0);
				int content1Status = intent.getIntExtra("content1Status", 0);
				int content2Status = intent.getIntExtra("content2Status", 0);
				int bannerStatus = intent.getIntExtra("bannerStatus", 0);

				switch (bc_type) {
				/* handle content1 request */
				case InitDataService.SEND_BC_CONTENT1: {
					switch (content1Status) {
					case InitDataService.CONTENT1_REQUEST_FAILED:
						showToast(R.string.no_service);
						break;

					case InitDataService.CONTENT1_RESPONSE_ERROR:
						showToast(R.string.error_appid);
						break;

					case InitDataService.CONTENT1_REQUESTING:
					case InitDataService.CONTENT1_REQUEST_FINISH:
					default:
						break;
					}

					break;
				}

				/* handle content2 request */
				case InitDataService.SEND_BC_CONTENT2: {
					switch (content2Status) {
					case InitDataService.CONTENT2_REQUEST_FAILED:
						showToast(R.string.no_service);
						break;

					case InitDataService.CONTENT2_RESPONSE_ERROR:
						showToast(R.string.error_appid);
						break;

					case InitDataService.CONTENT2_REQUESTING:
					case InitDataService.CONTENT2_REQUEST_FINISH:
					default:
						break;
					}

					break;
				}

				/* handle banner request */
				case InitDataService.SEND_BC_BANNER: {
					switch (bannerStatus) {
					case InitDataService.BANNER_REQUEST_FAILED:
						showToast(R.string.no_service);
						break;

					case InitDataService.BANNER_RESPONSE_ERROR:
						showToast(R.string.error_appid);
						break;

					case InitDataService.BANNER_REQUEST_FINISH:
					case InitDataService.BANNER_REQUESTING:
					default:
						break;
					}

					break;
				}

				default:
					break;
				}

			}
		}

	};

}
