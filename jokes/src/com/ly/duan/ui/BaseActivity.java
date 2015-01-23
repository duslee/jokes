package com.ly.duan.ui;

import android.app.Activity;
import android.graphics.Bitmap;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.help.DBHelp;
import com.ly.duan.utils.FileUtils;
import com.ly.duan.utils.NetUtils;
import com.ly.duan.utils.ToastUtils;
import com.sjm.gxdz.R;

public class BaseActivity extends Activity {
	
	private DbUtils dbUtils = null;
	private BitmapUtils bitmapUtils = null;
	
	public DbUtils getDb() {
		if (null == dbUtils) {
			dbUtils = DBHelp.getInstance(getApplicationContext());
		}
		return dbUtils;
	}

	public BitmapUtils getBitmapUtils() {
		if (null == bitmapUtils) {
			bitmapUtils = BitmapHelp.getInstance(getApplicationContext());
			bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		}
		return bitmapUtils;
	}
	
	public boolean checkNetWork() {
		if (!NetUtils.isnetWorkAvilable(getBaseContext())) {
			showToast(getResources().getString(R.string.toast_network));
			return false;
		} else {
			return true;
		}
	}
	
	public boolean checkNetWorkOrSdcard() {
		if (!FileUtils.isMounted()) {
			showToast(getResources().getString(R.string.toast_sdcard_mounted));
			return false;
		} else if (!FileUtils.isSDCardAvailable()) {
			showToast(getResources().getString(R.string.toast_sdcard_available));
			return false;
		} else {
			return checkNetWork();
		}
	}

	public boolean checkSdcard() {
		if (!FileUtils.isMounted()) {
			showToast(getResources().getString(R.string.toast_sdcard_mounted));
			return false;
		} else if (!FileUtils.isSDCardAvailable()) {
			showToast(getResources().getString(R.string.toast_sdcard_available));
			return false;
		} else {
			return true;
		}
	}

	public void showToast(String str) {
		ToastUtils.show(getBaseContext(), str);
	}

	public void showToast(int str) {
		ToastUtils.show(getBaseContext(), str);
	}

}
