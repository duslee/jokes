package com.ly.duan.utils;

import android.app.Activity;

import com.sjm.gxdz.R;

public class ActivityAnimator {

	public void pushLeftAnimation(Activity a) {
		a.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	public void pushRightAnimation(Activity a) {
		a.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	public void fadeAnimation(Activity a) {
		a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	public void unzoomAnimation(Activity a) {
		a.overridePendingTransition(R.anim.unzoom_in, R.anim.unzoom_out);
	}

}
