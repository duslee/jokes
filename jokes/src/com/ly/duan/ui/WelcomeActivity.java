package com.ly.duan.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
	
	private static final int EXIT_APP = 14;
	private static final int GO_TO_MAIN = 15;
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case EXIT_APP:
				handleExitApp();
				break;

			case GO_TO_MAIN:
				goToMain();
				break;
				
			default:
				break;
			}
		};
		
	};

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
		mHandler.sendEmptyMessageDelayed(GO_TO_MAIN, 3 * 1000);
	}

	protected void goToMain() {
		startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
		new ActivityAnimator().pushLeftAnimation(WelcomeActivity.this);
		WelcomeActivity.this.finish();
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
	
	private void handleExitApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this)
				.setMessage(R.string.exit_app).setPositiveButton(R.string.exit,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								WelcomeActivity.this.finish();
								System.exit(0);
							}
						});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equalsIgnoreCase(InitDataService.ACTION)) {
				int bc_type = intent.getIntExtra("bc_type", 0);
				
				/* exit app directly */
				if (bc_type == InitDataService.SEND_BC_EXIT) {
					mHandler.sendEmptyMessage(EXIT_APP);
				} else {
					int content1Status = intent.getIntExtra("content1Status", 0);
					int content2Status = intent.getIntExtra("content2Status", 0);
					int bannerStatus = intent.getIntExtra("bannerStatus", 0);
					int currentStatus = intent.getIntExtra("currentStatus", 0);

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
					
					case InitDataService.SEND_BC_CONTENT: {
						switch (currentStatus) {
						case InitDataService.CONTENT_REQUEST_FAILED:
							showToast(R.string.no_service);
							break;

						case InitDataService.CONTENT_RESPONSE_ERROR:
							showToast(R.string.error_appid);
							mHandler.sendEmptyMessage(EXIT_APP);
							break;

						case InitDataService.CONTENT_REQUESTING:
						case InitDataService.CONTENT_REQUEST_FINISH:
						default:
							break;
						}
					}
						break;
						
					case InitDataService.SEND_BC_CLM: {
						switch (currentStatus) {
						case InitDataService.COLUMN_REQUEST_FAILED:
							showToast(R.string.no_service);
							break;

						case InitDataService.COLUMN_RESPONSE_ERROR:
							showToast(R.string.error_appid);
							mHandler.sendEmptyMessage(EXIT_APP);
							break;

						case InitDataService.COLUMN_REQUESTING:
						case InitDataService.COLUMN_REQUEST_FINISH:
						default:
							break;
						}
					}
						break;

					default:
						break;
					}
				}
			}
		}

	};

}
