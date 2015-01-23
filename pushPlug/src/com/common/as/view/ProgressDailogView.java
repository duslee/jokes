package com.common.as.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
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


public class ProgressDailogView extends Dialog{
	private Context mContext;
	private View mTableTip;
	private WindowManager mWManager; // WindowManager
	private WindowManager.LayoutParams mWMParams; // WindowManager����
	private TextView txtContent;
	public ProgressDailogView(Context context ) {
		// TODO Auto-generated constructor stub
		super(context,R.style.MyDialogStyle);
		mContext = context;
	}

	public void fun() {
		mWManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		startContent();
		mTableTip = LayoutInflater.from(mContext).inflate(R.layout.progress_dlg,
				null);
		mTableTip.setBackgroundColor(Color.TRANSPARENT);
		txtContent=(TextView)mTableTip.findViewById(R.id.txt_content);
		WindowManager wm = mWManager;
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWMParams = wmParams;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;// 2003; //
		DisplayMetrics Metrics = new DisplayMetrics();
		mWManager.getDefaultDisplay().getMetrics(Metrics);
		getWindow().setAttributes(wmParams);
		getWindow().setGravity(Gravity.CENTER);
		wmParams.format = -3; // ͸��
		wm.addView(mTableTip, wmParams);// ����
	}

	public void removeTipView() {
		try {
			if(null!=mTableTip){
				mWManager.removeView(mTableTip);
			}
			if(null!=timer){
				timer.cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	CountDownTimer timer;
	private void startContent() {
		if(null==timer){
			timer = new CountDownTimer(50*1000,10*1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub
					int index = (int) (Math.random()*ProgressUtils.strs_content.length);
					txtContent.setText(ProgressUtils.strs_content[index]);
				}
				
				@Override
				public void onFinish() {
//					removeTipView();
				}
			};
		}
		timer.start();
	}

}
