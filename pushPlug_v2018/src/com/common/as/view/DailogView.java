package com.common.as.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.as.pushtype.PushInfo;
import com.example.pushplug.R;


public class DailogView extends Dialog{
	private Context mContext;
	private View mTableTip;
	private WindowManager mWManager; // WindowManager
	private WindowManager.LayoutParams mWMParams; // WindowManager����
	private OnDownLoadBtnClick mOnTopViewClick;
	private OnCancelBtnClick mCancelBtnClick;
	private int midX;
	private int midY;
	private View mShowView;
	View btnyes;
	View btnno;
	private Bitmap icBmp;
	PushInfo pi;
	ImageView dg_title_iv;
	public static interface OnDownLoadBtnClick {
		public void onClick1();
	}
	public static interface OnCancelBtnClick {
		public void onClick2();
	}
	public DailogView(Context context, OnDownLoadBtnClick listener,OnCancelBtnClick cancelBtnClick ) {
		// TODO Auto-generated constructor stub
		super(context,R.style.MyDialogStyle);
		mContext = context;
		mOnTopViewClick = listener;
		mCancelBtnClick = cancelBtnClick;
	}

	public void setIcBmp(Bitmap icBmp) {
		this.icBmp = icBmp;
	}
	public void setPushInfo(PushInfo piInfo){
		this.pi = piInfo;
	}
	public void fun() {
		// ��������view WindowManager����
		mWManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		midX = mWManager.getDefaultDisplay().getWidth() / 2 - 25;
		midY = mWManager.getDefaultDisplay().getHeight() / 2 - 44;

		mTableTip = LayoutInflater.from(mContext).inflate(R.layout.common_dlg,
				null);
		mTableTip.setBackgroundColor(Color.TRANSPARENT);
		WindowManager wm = mWManager;
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWMParams = wmParams;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;// 2003; //
												// type�ǹؼ������2002��ʾϵͳ�����ڣ���Ҳ��������2003��
	//	wmParams.flags = 40;// �����������ɿ�
		ImageView iv = (ImageView) mTableTip.findViewById(R.id.icon);
		if (null != icBmp) {
				iv.setImageBitmap(icBmp);
		}
		dg_title_iv = (ImageView) mTableTip.findViewById(R.id.dg_title_iv);
		btnyes = (ImageView) mTableTip.findViewById(R.id.btnYes);
		btnno = (ImageView) mTableTip.findViewById(R.id.btnNo);
		
		btnyes.setOnClickListener(mOnClickListener1);
		btnno.setOnClickListener(mOnClickListener2);
		TextView tv = (TextView)mTableTip.findViewById(R.id.txtTitle);
		tv.setText(pi.getAppName());
		TextView tvBrief = (TextView)mTableTip.findViewById(R.id.txtBrief);
		tvBrief.setText(pi.getmBrief());
		tvBrief.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		
		DisplayMetrics Metrics = new DisplayMetrics();
		mWManager.getDefaultDisplay().getMetrics(Metrics);
		getWindow().setAttributes(wmParams);
		getWindow().setGravity(Gravity.CENTER);
		wmParams.format = -3; // ͸��
		wm.addView(mTableTip, wmParams);// ����
	}
	private android.view.View.OnClickListener mOnClickListener1 = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mOnTopViewClick.onClick1();
		}
	};
private android.view.View.OnClickListener mOnClickListener2 = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mCancelBtnClick.onClick2();
		}
	};
	public void setmShowView(View mShowView) {
		this.mShowView = mShowView;
	}


	public void removeTipView() {
		if(null!=mTableTip){
			mWManager.removeView(mTableTip);
		}
	}

	private void disPopu() {
	}

	public interface ServiceListener {
		public void OnCloseService(boolean isClose);
	}
}
