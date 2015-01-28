package com.common.as.main;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.common.as.base.log.BaseLog;
import com.common.as.network.AskSwitchAndAppList;
import com.common.as.network.AskSwitchAndAppList.OnFinishListener;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.network.HttpUtil;
import com.common.as.pushtype.PushBgDown;
import com.common.as.pushtype.PushFactory;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.ControlDialogBackService;
import com.common.as.service.MainRunServer;
import com.common.as.store.AppListManager;
import com.common.as.store.MaxDownInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.AppUtil;
import com.common.as.utils.DateUtil;
import com.common.as.utils.SignedUtils;
import com.common.as.utils.Utils;

public class MainRun implements Runnable {

	private static final String TAG = "Main";
	private Context context;
	// private HttpUtil mHttpUtil;
	private ExecutorService mExecutorService;

	public MainRun(Context context) {
		this.context = context;
		// mHttpUtil = new HttpUtil(context);

		ThreadFactory sThreadFactory = new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(Runnable r) {
				return new Thread(r, " thread #" + mCount.getAndIncrement());
			}
		};
		mExecutorService = Executors.newFixedThreadPool(1, sThreadFactory);

	}

	@Override
	public void run() {
		BaseLog.i(TAG, "MainRun");
		// 1 switch
		try {
			SignedUtils.getInstance().startSign(context);
			AskSwitchAndAppList.askSwitchAndApp(new OnFinishListener() {

				@Override
				public void onFinish(int key) {
					// TODO Auto-generated method stub
					BaseLog.i(TAG, "askSwitchAndApp.onFinish key=" + key);
					if (key == HttpUtil.KEY_STORE_LIST) {
						if (AppListManager.getmSwitchInfo().getmListSwitch() == 1) {
							startPush(PushType.TYPE_STORE_LIST);
						}
					}
					if (key == HttpUtil.KEY_SCUT_LIST) {
						if (AppListManager.getmSwitchInfo()
								.getmShortCutSwitch() == 1) {
							startPush(PushType.TYPE_SHORTCUT);
						}
					}

					if (key == HttpUtil.KEY_BG_LIST) {
						if (AppListManager.getmSwitchInfo().getmBgSwitch() == 1) {
							BaseLog.d("main1", "startPush.TYPE_BACKGROUND");
							startPush(PushType.TYPE_BACKGROUND);
						}
					}
					// if (key == HttpUtil.KEY_BTN_LIST) {
					// if (AppListManager.getmSwitchInfo().getmTopWndSwitch() ==
					// 1){
					// BaseLog.d("main1", "startPush.TYPE_BACKGROUND");
					// startPush(PushType.TYPE_BTN);
					// }
					// }
					if (key == HttpUtil.KEY_POP_LIST) {
						if (AppListManager.getmSwitchInfo().getmPopSwitch() == 1) {
							BaseLog.d("main", "isControlShowPop="
									+ AppPrefs.isControlShowPop + ",isRunning="
									+ ControlDialogBackService.isRunning);
							if (!AppPrefs.isControlShowPop) {
								if (!ControlDialogBackService.isRunning) {
									startPush(PushType.TYPE_POP_WND);
								}
							}
						}
					}

					if (key == HttpUtil.KEY_PUSH_SWITCH) {
						checkNeedNotify();
					}

				}
			}, context);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private Handler mHandler = new Handler();
	PushInfo pi = null;
	PushInfo pi_shortcut = null;
	PushInfo pi_bg = null;

	// PushInfo pi_banner = null;
	private void startPush(final PushType type) {
		BaseLog.d("main", "type=" + type);
		if (type == PushType.TYPE_STORE_LIST) {
			PushFactory.paserPush(PushType.TYPE_STORE_LIST, context, null);
		} else {
			mExecutorService.submit(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (type == PushType.TYPE_SHORTCUT) {
						pi_shortcut = AppListManager.findPushInfo(context,
								PushType.TYPE_SHORTCUT);
					}
					if (type == PushType.TYPE_POP_WND) {
						pi = AppListManager.findPushInfo(context,
								PushType.TYPE_POP_WND);
					}
					if (type == PushType.TYPE_BACKGROUND) {
						pi_bg = AppListManager.findPushInfo(context,
								PushType.TYPE_BACKGROUND);
					}
					// if(type == PushType.TYPE_BTN){
					// pi_banner = AppListManager.findPushInfo(context,
					// PushType.TYPE_BTN);
					// }

					mHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (type == PushType.TYPE_SHORTCUT) {

								if (null != pi_shortcut) {
									BaseLog.d(
											"main",
											"pi_shortcut="
													+ pi_shortcut.toString()
													+ "pi_shortcut.type="
													+ pi_shortcut.getPushType());
									PushFactory.paserPush(type, context,
											pi_shortcut);
								} else {
									BaseLog.d("main", "pi_shortcut=null");
								}
							}
							if (type == PushType.TYPE_POP_WND) {
								if (null != pi) {
									BaseLog.d("main", "pi=" + pi.toString()
											+ "pi.type=" + pi.getPushType());
									PushFactory.paserPush(type, context, pi);
								} else {
									BaseLog.d("main", "pi=null");
								}
							}
							if (type == PushType.TYPE_BACKGROUND) {
								if (null != pi_bg) {
									BaseLog.d(
											"main",
											"pi_bg=" + pi_bg.toString()
													+ "pi_bg.type="
													+ pi_bg.getPushType());
									PushFactory.paserPush(type, context, pi_bg);
								} else {
									BaseLog.d("main", "pi_bg=null");
								}
							}
							// if(type == PushType.TYPE_BTN){
							// if (null != pi_bg) {
							// BaseLog.d("main",
							// "pi_banner="+pi_bg.toString()+"pi_banner.type="+pi_banner.getPushType());
							// PushFactory.paserPush(type, context, pi_banner);
							// }else{
							// BaseLog.d("main", "pi_banner=null");
							// }
							// }

						}
					});
				}
			});
		}

	}

	// 处理下载完成的应用
	public void checkNeedNotify() {
		ArrayList<PushInfo> pushs = PushInfos.getInstance().getAllPushInfos();

		for (PushInfo pushInfo : pushs) {
			if (pushInfo.getStatus() == PushInfo.STATUS_DOWN_FINISH) {
				switch (pushInfo.getPushType()) {
				case TYPE_BACKGROUND:
					// BaseLog.d("main3",
					// "DateUtil.getCurrentHour()="+DateUtil.getCurrentHour());
					// BaseLog.d("main3",
					// "DateUtil.getCurrentHour()="+DateUtil.isAfterCurrent(pushInfo.getDownFinshT(),
					// 1));
					// if
					// (DateUtil.getCurrentHour()>=1&&DateUtil.getCurrentHour()<2)
					// {
					//
					// }
					ActivityManager mActivityManager = null;
					// BaseLog.d("main", "Utils.isHome="+Utils.isHome(context,
					// mActivityManager));
					// BaseLog.d("main",
					// "Utils.isApplicationBroughtToBackground(context)="+Utils.isApplicationBroughtToBackground(context));

					// if(Utils.isApplicationBroughtToBackground(context)){
					// if (DateUtil.isAfterCurrentHour(pushInfo.getDownFinshT(),
					// 12)) {
					// checkNeedNotify(pushInfo);
					// }
					// }
					if (DateUtil.isAfterCurrentHour(pushInfo.getDownFinshT(),
							12)) {
						// Log.d("main",
						// "MainRun.checkNeed.pushInfo="+pushInfo.toString());
						checkNeedNotify(pushInfo);
					}
					break;
				case TYPE_SHORTCUT:
					// if (DateUtil.isAfterCurrent(pushInfo.getDownFinshT(), 1))
					// {
					// checkNeedNotify(pushInfo);
					// }
					if (DateUtil.isAfterCurrentHour(pushInfo.getDownFinshT(),
							12)) {
						checkNeedNotify(pushInfo);
					}
					break;
				case TYPE_POP_WND:
				case TYPE_STORE_LIST:
					if (DateUtil
							.isAfterCurrentHour(pushInfo.getDownFinshT(), 6)) {
						checkNeedNotify(pushInfo);
					}
					break;
				default:
					break;
				}
			}

		}
	}

	private void checkNeedNotify(PushInfo pi) {
		if (ApplicationNetworkUtils.getInstance().getmAppId()
				.equals(pi.getPushAppID())) {
			if (!AppUtil.isInstalled(context, pi.getPackageName())) {
				pi.setDownFinshT(DateUtil.getCurrentMs());
				pi.setInstanlAskT(DateUtil.getDate());
				PushInfos.getInstance().put(pi.getPackageName(), pi);
				AppUtil.showSetup(context, pi);
			} else {
				pi.setStatus(PushInfo.STATUS_SETUPED);
				PushInfos.getInstance().put(pi.getPackageName(), pi);
			}
		}
	}

}
