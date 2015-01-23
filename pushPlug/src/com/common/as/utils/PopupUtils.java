package com.common.as.utils;

import java.util.Timer;
import java.util.TimerTask;

import com.example.pushplug.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class PopupUtils {

	private ProgressDialog waitingDialog;
	private ProgressDialog waitingDialogCanCancel;
	private AlertDialog mMessageDlg;
	private static String lastStr;
	private static Handler mTimerHandler = new Handler();
	private static final int DELAY_TIME = 3000;
	public static void showLongToast(Context ctx, String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context ctx, String str) {
		if (null != lastStr && lastStr.equals(str)) {
			return;
		}
		lastStr = str;
		Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
		mTimerHandler.removeCallbacksAndMessages(null);
		mTimerHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				lastStr = null;
			}
		}, DELAY_TIME);
	}

	public static void showLongToast(Context ctx, int strId) {
		showLongToast(ctx, ctx.getString(strId));
	}

	public static void showShortToast(Context ctx, int strId) {
		showShortToast(ctx, ctx.getString(strId));
	}
	
	public void showMessageBox(Context ctx, String title, String message){
//		if (mMessageDlg != null) {
//			mMessageDlg.dismiss();
//		}
//		mMessageDlg = new AlertDialog.Builder(ctx)
//			.setTitle(title)
//			.setMessage(message)
//			.setNegativeButton(R.string.back, null).create();
//		
//		mMessageDlg.show();
	}
	
	public static class DlgInfo{
		public String title;
		public String message;
		public int positiveBtnStr;
		public int negativeBtnStr;
		public View v;
		public AlertDialog.OnClickListener positiveListener;
		public AlertDialog.OnClickListener negativeListener;
	}
	
	public void showMessageBox(Context ctx, DlgInfo info){
		if (mMessageDlg != null) {
			mMessageDlg.dismiss();
		}
		
		AlertDialog.Builder build = new AlertDialog.Builder(ctx); 
		if (null != info.message) {
			build.setMessage(info.message);
		} else if(null != info.v){
            build.setView(info.v);
		}
		
		if (null != info.title) {
			build.setTitle(info.title);
		}
		
		
		build.setPositiveButton(info.positiveBtnStr, info.positiveListener);
		build.setNegativeButton(info.negativeBtnStr, info.negativeListener);
		mMessageDlg = build.create();
		mMessageDlg.show();
		mMessageDlg.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mMessageDlg = null;
				
			}
		});
	}
	
	
	public void showMessageBox(Context ctx, int title, int message){
		showMessageBox(ctx, ctx.getString(title), ctx.getString(message));
	}
	
	public void hideWaitingDialog() {
		if (waitingDialog != null && waitingDialog.isShowing()) {
			waitingDialog.dismiss();
		}
		waitingDialog = null;
	}

	public void showWaitingDialog(Context ctx, int id) {
		if (waitingDialog != null && waitingDialog.isShowing()) {
			waitingDialog.setMessage(ctx.getString(id));
		} else {	
			waitingDialog = ProgressDialog.show(ctx, "", ctx.getString(id), true,false);

		    waitingDialog.setCancelable(false);
		    waitingDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					//waitingDialog = null;
				}
			});
		}
	}	
	
	public void hideWaitingDialogCanCancel() {
		if (waitingDialogCanCancel != null) {
			waitingDialogCanCancel.dismiss();
		}
		waitingDialogCanCancel = null;
	}
	
	public void showWaitingDialogCanCancel(Context ctx, int id, OnCancelListener listener) {
		if (waitingDialogCanCancel != null) {
			waitingDialogCanCancel.setMessage(ctx.getString(id));
		} else {
			waitingDialogCanCancel = ProgressDialog.show(ctx, "", ctx.getString(id), true, true, listener);
			waitingDialogCanCancel.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					waitingDialogCanCancel = null;
				}
			});
		}
	}	
	
	public void hideMessageDialog(){
		if (mMessageDlg != null) {
			mMessageDlg.dismiss();
		}
		mMessageDlg = null;
	}
}
