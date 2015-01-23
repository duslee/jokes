package com.ly.duan.ui.fragment;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.help.DBHelp;
import com.ly.duan.utils.FileUtils;
import com.ly.duan.utils.NetUtils;
import com.ly.duan.utils.ToastUtils;
import com.sjm.gxdz.R;

public class BaseFragment extends Fragment {

	private DbUtils dbUtils = null;
	private BitmapUtils bitmapUtils = null;

	private Animation rotateAnimation = null;

	public Animation getRotateAnimation() {
		rotateAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_clockwise);
		return rotateAnimation;
	}

	public DbUtils getDb() {
		if (null == dbUtils) {
			dbUtils = DBHelp.getInstance(getActivity());
		}
		return dbUtils;
	}

	public BitmapUtils getBitmapUtils() {
		if (null == bitmapUtils) {
			bitmapUtils = BitmapHelp.getInstance(getActivity());
			bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		}
		return bitmapUtils;
	}

	public boolean checkNetWork() {
		if (!NetUtils.isnetWorkAvilable(getActivity())) {
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
		ToastUtils.show(getActivity(), str);
	}

	public void showToast(int str) {
		ToastUtils.show(getActivity(), str);
	}

}
