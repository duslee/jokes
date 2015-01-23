package com.common.as.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.AppInfoUtil;
import com.common.as.service.CheckService;
import com.common.as.store.FileUtils;
import com.common.as.utils.AppUtil;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.Utils;
import com.example.pushplug.R;


public class PlugDailogView extends Dialog implements com.common.as.view.DailogView.OnDownLoadBtnClick,com.common.as.view.DailogView.OnCancelBtnClick{
	private Context mContext;
	private View mTableTip;
	private WindowManager mWManager; // WindowManager
	private WindowManager.LayoutParams mWMParams; // WindowManager����
	private OnDownLoadBtnClick mOnTopViewClick;
	private OnCancelBtnClick mCancelBtnClick;
	private int midX;
	private int midY;
	private View mShowView;
	Button btnyes;
	private Bitmap icBmp;
	public static interface OnDownLoadBtnClick {
		public void onClick1();
	}
	public static interface OnCancelBtnClick {
		public void onClick2();
	}
	public PlugDailogView(Context context) {
		// TODO Auto-generated constructor stub
		super(context,R.style.MyDialogStyle);
		mContext = context;
	}
	public void setIcBmp(Bitmap icBmp) {
		this.icBmp = icBmp;
	}
	public void fun() {
		// ��������view WindowManager����
		mWManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		midX = mWManager.getDefaultDisplay().getWidth() / 2 - 25;
		midY = mWManager.getDefaultDisplay().getHeight() / 2 - 44;

		

		mTableTip = LayoutInflater.from(mContext).inflate(R.layout.dlg_an_zhuang,
				null);
		mTableTip.setBackgroundColor(Color.TRANSPARENT);
		WindowManager wm = mWManager;
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWMParams = wmParams;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;// 2003; //
												// type�ǹؼ������2002��ʾϵͳ�����ڣ���Ҳ��������2003��
		
		btnyes = (Button) mTableTip.findViewById(R.id.btnYes);
		
		
		btnyes.setOnClickListener(mOnClickListener1);
		
		
		DisplayMetrics Metrics = new DisplayMetrics();
		mWManager.getDefaultDisplay().getMetrics(Metrics);
		getWindow().setAttributes(wmParams);
		getWindow().setGravity(Gravity.CENTER);
		wmParams.format = -3; // ͸��
		wm.addView(mTableTip, wmParams);// ����
//		if (null != icBmp) {
//				iv.setImageBitmap(icBmp);
//		}
		
		
	
	}
	private android.view.View.OnClickListener mOnClickListener1 = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onClick1();
		}
	};
	private android.view.View.OnClickListener mOnClickListener2 = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onClick2();
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
	@Override
	public void onClick2() {
		// TODO Auto-generated method stub
		removeTipView();
	}

	@Override
	public void onClick1() {
		// TODO Auto-generated method stub
		AppUtil.showSetup(mContext, FileUtils.getExternalStorageDirectory()+"/"+AppInfoUtil.down_dir);
		
//		removeTipView();
		CheckService.removeTopView();
	}
}
