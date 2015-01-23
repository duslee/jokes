package com.ly.duan.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

import com.sjm.gxdz.R;

/**
 * 功能：操作Dialog的工具类<br>
 * 日期：2014-10-20
 * 
 * @author zfwu
 * @time 上午9:49:02
 */
public class DialogUtils {

	/** 刷新框 */
	public static final ProgressDialog getFreshDialog(Activity activity) {
		ProgressDialog freshDialog = new ProgressDialog(activity);
		freshDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		freshDialog.setMessage(activity.getResources()
				.getString(R.string.fresh));
		freshDialog.setCanceledOnTouchOutside(false);
		return freshDialog;
	}

	/** 刷新框 */
	public static final ProgressDialog getFreshDialog(Activity activity, int res) {
		ProgressDialog freshDialog = new ProgressDialog(activity);
		freshDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		freshDialog.setMessage(activity.getResources().getString(res));
		freshDialog.setCanceledOnTouchOutside(false);
		return freshDialog;
	}

	/** 将对话框的大小按屏幕大小的百分比设置 */
	public static void setDialogAttr(Context context, Dialog dialog) {
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88);
		dialog.getWindow().setAttributes(lp);
	}
}
