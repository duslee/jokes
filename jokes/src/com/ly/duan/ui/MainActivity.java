package com.ly.duan.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.bean.MultiReqStatus;
import com.ly.duan.help.GlobalHelp;
import com.ly.duan.service.InitDataService;
import com.ly.duan.ui.fragment.DummyTabContent;
import com.ly.duan.ui.fragment.Fragment1;
import com.ly.duan.ui.fragment.Fragment3;
import com.ly.duan.ui.fragment.Fragment4;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.DialogUtils;
import com.ly.duan.utils.PreferencesUtils;
import com.ly.duan.utils.ResourceUtils;
import com.ly.duan.utils.StringUtils;
import com.ly.duan.utils.ToastUtils;
import com.sjm.gxdz.R;

@ContentView(R.layout.view_main)
public class MainActivity extends FragmentActivity {

	private static final int RE_OPEN_APP = 30;
	private static final int GET_CONTENT1_SUCCESS = 31;
	private static final int GET_CONTENT2_SUCCESS = 32;
	private static final int GET_BANNER_SUCCESS = 33;

	@ViewInject(android.R.id.tabhost)
	private TabHost mTabHost;

	private FragmentManager fm;
	private String tabName;
	private String savedTabName;

	private int iconArray[] = { R.drawable.tab1_item_selector,
			R.drawable.tab2_item_selector, R.drawable.tab3_item_selector,
			R.drawable.tab4_item_selector };
	private String tabArray[] = { "tab1", "tab2", "tab3", "tab4" };

	private Fragment1 fragment1;
	private Fragment1 fragment2;
	private Fragment3 fragment3;
	private Fragment4 fragment4;

	private ProgressDialog freshDialog;

	private MainHandler mHandler = new MainHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 1. Dependency inject */
		ViewUtils.inject(this);

		/* 2. registe BroadcastReceiver */
		IntentFilter filter = new IntentFilter(InitDataService.ACTION);
		registerReceiver(receiver, filter);

		/* 3. init Operation */
		initOperation();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void initOperation() {
		/* 1. set default/saved tab name */
		tabName = tabArray[0];
		savedTabName = PreferencesUtils.getConfigSharedPreferences(
				MainActivity.this).getString(Constants.TAB_NAME, tabName);

		/* 2. get data */
		/* 2.1 get current status */
		MultiReqStatus status = GlobalHelp.getInstance().getMultiReqStatus();
		int content1Status = InitDataService.CONTENT1_REQUESTING;
		int content2Status = InitDataService.CONTENT2_REQUESTING;
		if (null != status) {
			content1Status = status.getContent1Status();
			content2Status = status.getContent2Status();
		}
		LogUtils.e("content1Status=" + content1Status + ", content2Status="
				+ content2Status);

		/* 2.2 根据content1Status & content2Status & savedTabName判断是否弹出刷新框提示等待 */
		if (savedTabName.equalsIgnoreCase(tabArray[0])) {
			switch (content1Status) {
			case InitDataService.CONTENT1_REQUESTING:
				openDialog();
				break;

			default:
				break;
			}
		} else if (savedTabName.equalsIgnoreCase(tabArray[1])) {
			switch (content2Status) {
			case InitDataService.CONTENT2_REQUESTING:
				openDialog();
				break;

			default:
				break;
			}
		}

		/* 3. get Fragment */
		getFragment();

		/* 4. set up tab */
		setupTabView();
	}

	protected void openDialog() {
		if (freshDialog == null) {
			freshDialog = DialogUtils.getFreshDialog(MainActivity.this);
			freshDialog.show();
		}
	}

	protected void closeDialog() {
		if (null != freshDialog) {
			freshDialog.dismiss();
			freshDialog = null;
		}
	}

	private void hideFragment(FragmentTransaction ft) {
		LogUtils.e("hide all fragment");
		/* 若对应的Fragment被添加进容器中了，隐藏之 */
		if (null != fragment1) {
			ft.hide(fragment1);
		}
		if (null != fragment2) {
			ft.hide(fragment2);
		}
		if (null != fragment3) {
			ft.hide(fragment3);
		}
		if (null != fragment4) {
			ft.hide(fragment4);
		}
	}

	private void getFragment() {
		fm = getSupportFragmentManager();
		/* 获取各个标签的Fragment */
		fragment1 = (Fragment1) fm.findFragmentByTag(tabArray[0]);
		fragment2 = (Fragment1) fm.findFragmentByTag(tabArray[1]);
		fragment3 = (Fragment3) fm.findFragmentByTag(tabArray[2]);
		fragment4 = (Fragment4) fm.findFragmentByTag(tabArray[3]);
	}

	@Override
	protected void onStop() {
		saveTabInSP();
		super.onStop();
	}

	private void setupTabView() {
		/* set listener */
		mTabHost.setOnTabChangedListener(listener);
		mTabHost.setup();

		for (int i = 0; i < tabArray.length; i++) {
			mTabHost.addTab(mTabHost.newTabSpec(tabArray[i])
					.setIndicator(getTabItemView(i))
					.setContent(new DummyTabContent(getBaseContext())));
		}

		// saved tabName != tabArray[0], then set saved tab as current tab; or
		// do nothing
		if (!savedTabName.equalsIgnoreCase(tabArray[0])) {
			mTabHost.setCurrentTabByTag(savedTabName);
		}
	}

	private View getTabItemView(int index) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.tab_bottom_nav, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
		imageView.setImageResource(iconArray[index]);
		return view;
	}

	private OnTabChangeListener listener = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {
			FragmentTransaction ft = fm.beginTransaction();
			hideFragment(ft);

			/* 处理各个标签对应的事件 */
			if (tabId.equalsIgnoreCase(tabArray[0])) {
				tabName = tabArray[0];
				LogUtils.e("(fragment1==null)=" + (fragment1 == null)
						+ ", tabName=" + tabName);
				/* 将Fragment添加进容器中 */
				if (null == fragment1) {
					fragment1 = Fragment1.newInstance(true,
							isSame(tabName, savedTabName));
					ft.add(R.id.content, fragment1, tabArray[0]);
				} else {/* 直接显示该Fragment */
					ft.show(fragment1);
				}
			} else if (tabId.equalsIgnoreCase(tabArray[1])) {
				tabName = tabArray[1];
				LogUtils.e("(fragment2==null)=" + (fragment2 == null)
						+ ", tabName=" + tabName);
				if (null == fragment2) {
					fragment2 = Fragment1.newInstance(false,
							isSame(tabName, savedTabName));
					ft.add(R.id.content, fragment2, tabArray[1]);
				} else {
					ft.show(fragment2);
				}
			} else if (tabId.equalsIgnoreCase(tabArray[2])) {
				tabName = tabArray[2];
				LogUtils.e("(fragment3==null)=" + (fragment3 == null)
						+ ", tabName=" + tabName);
				if (null == fragment3) {
					fragment3 = new Fragment3();
					ft.add(R.id.content, fragment3, tabArray[2]);
				} else {
					ft.show(fragment3);
				}
			} else if (tabId.equalsIgnoreCase(tabArray[3])) {
				tabName = tabArray[3];
				LogUtils.e("(fragment4==null)=" + (fragment4 == null)
						+ ", tabName=" + tabName);
				if (null == fragment4) {
					fragment4 = new Fragment4();
					ft.add(R.id.content, fragment4, tabArray[3]);
				} else {
					ft.show(fragment4);
					fragment4.onStart();
				}
			}
			/* 使之生效 */
			ft.commit();
			// ft.commitAllowingStateLoss();
		}
	};

	private long mExitTime;

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					ToastUtils.show(MainActivity.this, "再按一次退出程序");
					mExitTime = System.currentTimeMillis();
				} else {
					saveTabInSP();
					this.finish();
					System.exit(0);
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};

	private void saveTabInSP() {
		LogUtils.e("save tabName=" + tabName);
		PreferencesUtils.getConfigSharedPreferences(this).edit()
				.putString(Constants.TAB_NAME, tabName).commit();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogUtils.e("action=" + action);

			if (!StringUtils.isBlank(action)
					&& (action.equalsIgnoreCase(InitDataService.ACTION))) {
				int bc_type = intent.getIntExtra("bc_type", 0);
				int content1Status = intent.getIntExtra("content1Status", 0);
				int content2Status = intent.getIntExtra("content2Status", 0);
				int bannerStatus = intent.getIntExtra("bannerStatus", 0);

				LogUtils.e("MainActivity receiver bc_type=" + bc_type
						+ ", content1Status=" + content1Status
						+ ", content2Status=" + content2Status
						+ ", bannerStatus=" + bannerStatus);

				switch (bc_type) {
				/* handle content1 request */
				case InitDataService.SEND_BC_CONTENT1: {
					switch (content1Status) {
					case InitDataService.CONTENT1_REQUEST_FAILED:
						mHandler.sendEmptyMessage(RE_OPEN_APP);
						ToastUtils.show(MainActivity.this, R.string.no_service);
						break;

					case InitDataService.CONTENT1_RESPONSE_ERROR:
						ToastUtils
								.show(MainActivity.this, R.string.error_appid);
						break;

					case InitDataService.CONTENT1_REQUEST_FINISH:
						Message msg = new Message();
						msg.what = GET_CONTENT1_SUCCESS;
						msg.arg1 = bc_type;
						msg.arg2 = content1Status;
						mHandler.sendMessage(msg);

						// mHandler.sendEmptyMessage(GET_CONTENT1_SUCCESS);
						break;

					case InitDataService.CONTENT1_REQUESTING:
					default:
						break;
					}

					break;
				}

				/* handle content2 request */
				case InitDataService.SEND_BC_CONTENT2: {
					switch (content2Status) {
					case InitDataService.CONTENT2_REQUEST_FAILED:
						mHandler.sendEmptyMessage(RE_OPEN_APP);
						ToastUtils.show(MainActivity.this, R.string.no_service);
						break;

					case InitDataService.CONTENT2_RESPONSE_ERROR:
						ToastUtils
								.show(MainActivity.this, R.string.error_appid);
						break;

					case InitDataService.CONTENT2_REQUEST_FINISH:
						Message msg = new Message();
						msg.what = GET_CONTENT2_SUCCESS;
						msg.arg1 = bc_type;
						msg.arg2 = content2Status;
						mHandler.sendMessage(msg);

						// mHandler.sendEmptyMessage(GET_CONTENT2_SUCCESS);
						break;

					case InitDataService.CONTENT2_REQUESTING:
					default:
						break;
					}

					break;
				}

				/* handle banner request */
				case InitDataService.SEND_BC_BANNER: {
					switch (bannerStatus) {
					case InitDataService.BANNER_REQUEST_FAILED:
						ToastUtils.show(MainActivity.this, R.string.no_service);
						break;

					case InitDataService.BANNER_RESPONSE_ERROR:
						ToastUtils
								.show(MainActivity.this, R.string.error_appid);
						break;

					case InitDataService.BANNER_REQUEST_FINISH:
						Message msg = new Message();
						msg.what = GET_BANNER_SUCCESS;
						msg.arg1 = bc_type;
						msg.arg2 = bannerStatus;
						mHandler.sendMessage(msg);

						// mHandler.sendEmptyMessage(GET_BANNER_SUCCESS);
						break;

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

	private class MainHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case RE_OPEN_APP:
				handleExitApp();
				break;

			case GET_CONTENT1_SUCCESS:
			case GET_CONTENT2_SUCCESS:
				closeDialog();

				LogUtils.e("thread.name=" + Thread.currentThread().getName()
						+ ", msg.arg1=" + msg.arg1 + ", msg.arg2=" + msg.arg2);
				notifyFragment(msg.arg2);
				break;

			case GET_BANNER_SUCCESS:
				LogUtils.e("thread.name=" + Thread.currentThread().getName()
						+ ", msg.arg1=" + msg.arg1 + ", msg.arg2=" + msg.arg2);
				notifyFragment(msg.arg2);
				break;

			default:
				break;
			}
		}

	}

	private boolean hasNotifyClm1 = false;
	private boolean hasNotifyClm2 = false;
	private boolean hasNotifyBanner1 = false;
	private boolean hasNotifyBanner2 = false;

	/* TODO: Question */
	private void notifyFragment(int which) {
		LogUtils.e("which=" + which + ", savedTabName=" + savedTabName
				+ ", tabArray[0]=" + tabArray[0] + ", hasNotifyClm1="
				+ hasNotifyClm1 + ", hasNotifyClm2=" + hasNotifyClm2);
		// if ((which == InitDataService.CONTENT1_REQUEST_FINISH)
		// && savedTabName.equalsIgnoreCase(tabArray[0])) {
		if ((which == InitDataService.CONTENT1_REQUEST_FINISH)
				&& !hasNotifyClm1) {
			LogUtils.e("(fragment1 != null)=" + (fragment1 != null));
			if (fragment1 != null) {
				hasNotifyClm1 = true;
				fragment1.acceptNotify(which);
			}
		} else if ((which == InitDataService.CONTENT2_REQUEST_FINISH)
				&& savedTabName.equalsIgnoreCase(tabArray[1]) && !hasNotifyClm2) {
			LogUtils.e("(fragment2 != null)=" + (fragment2 != null));
			if (fragment2 != null) {
				hasNotifyClm2 = true;
				fragment2.acceptNotify(which);
			}
			// } else if ((which == InitDataService.BANNER_REQUEST_FINISH)
			// && savedTabName.equalsIgnoreCase(tabArray[2])) {
		} else if ((which == InitDataService.BANNER_REQUEST_FINISH)) {
			LogUtils.e("(fragment3 != null)=" + (fragment3 != null)
					+ ", hasNotifyBanner1=" + hasNotifyBanner1
					+ ", hasNotifyBanner2=" + hasNotifyBanner2);
			if (savedTabName.equalsIgnoreCase(tabArray[2])
					&& (fragment3 != null) && !hasNotifyBanner2) {
				hasNotifyBanner2 = true;
				fragment3.acceptNotify(which);
			}
			if (!hasNotifyBanner1) {
				hasNotifyBanner1 = true;
				fragment1.acceptNotify(which);
			}
		}
	}

	private void handleExitApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
				.setMessage(R.string.exit_app).setPositiveButton(R.string.exit,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								MainActivity.this.finish();
								System.exit(0);
							}
						});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private boolean isSame(String str1, String str2) {
		if (StringUtils.isBlank(str1) || StringUtils.isBlank(str2)) {
			return false;
		}

		if (str1.equalsIgnoreCase(str2)) {
			return true;
		}
		return false;
	}

}
