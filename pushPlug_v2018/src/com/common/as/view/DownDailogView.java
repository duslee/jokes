package com.common.as.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.common.as.utils.AppUtil;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.Utils;
import com.example.pushplug.R;


public class DownDailogView extends Dialog implements com.common.as.view.DailogView.OnDownLoadBtnClick,com.common.as.view.DailogView.OnCancelBtnClick{
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
	Button btnno;
	ImageView iv_btnno;
	ImageView iv;
	ImageView down_dialog_girl;
	private Bitmap icBmp;
	PushInfo pi;
	TextView tv;
	TextView tvBrief;
	LinearLayout down_dialog_bg_ll;
	BitmapLoder bmpLoader;
	private final String url = "http://apk.boya1993.com/";
	private final String dialog_girl = "down_dialog_girl.png";
	private final String dialog_bg = "down_dialog_bg.png";
	private final String dialog_install = "down_dialog_install.png";
	public static interface OnDownLoadBtnClick {
		public void onClick1();
	}
	public static interface OnCancelBtnClick {
		public void onClick2();
	}
	public DownDailogView(Context context,PushInfo pushInfo) {
		// TODO Auto-generated constructor stub
		super(context,R.style.MyDialogStyle);
		mContext = context;
		this.pi = pushInfo;
	}
	public void setIcBmp(Bitmap icBmp) {
		this.icBmp = icBmp;
	}
	public void setPushInfo(PushInfo pushInfo){
		this.pi = pushInfo;
	}
	public void fun() {
		// ��������view WindowManager����
		mWManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		midX = mWManager.getDefaultDisplay().getWidth() / 2 - 25;
		midY = mWManager.getDefaultDisplay().getHeight() / 2 - 44;

		
//		LayoutParams ivpar = (LayoutParams) push_image.getLayoutParams();
//		ivpar.height = (120*Utils.getDisplayMetrics(this).heightPixels)/800;
//		ivpar.width = (480*Utils.getDisplayMetrics(this).widthPixels)/480;
//		push_image.setLayoutParams(ivpar);
		
//		mTableTip = LayoutInflater.from(mContext).inflate(R.layout.down_common_dlg,
//				null);
		if(Utils.isApplicationBroughtToBackground(mContext)){
			if(null==bmpLoader){
				bmpLoader=new BitmapLoder(mContext);
			}
			mTableTip = LayoutInflater.from(mContext).inflate(R.layout.down_common_dlg_other,
					null);
			iv_btnno = (ImageView)mTableTip.findViewById(R.id.btnNo);
			iv_btnno.setOnClickListener(mOnClickListener2);
			down_dialog_girl = (ImageView)mTableTip.findViewById(R.id.down_dialog_girl);
			down_dialog_bg_ll = (LinearLayout)mTableTip.findViewById(R.id.down_dialog_bg_ll);
			btnyes = (ImageView) mTableTip.findViewById(R.id.btnYes);
			btnyes.setOnClickListener(mOnClickListener1);
			android.widget.RelativeLayout.LayoutParams ivpar = (android.widget.RelativeLayout.LayoutParams)down_dialog_girl.getLayoutParams();
			ivpar.bottomMargin=100;
//			ivpar.height = (120*Utils.getDisplayMetrics(this).heightPixels)/800;
//			ivpar.width = (480*Utils.getDisplayMetrics(this).widthPixels)/480;
//			push_image.setLayoutParams(ivpar);
			bmpLoader.startLoad(new OnLoadBmp() {
				
				@Override
				public void onBitmapLoaded(Bitmap bmp) {
					// TODO Auto-generated method stub
					down_dialog_girl.setImageBitmap(bmp);
					bmpLoader.startLoad(new OnLoadBmp() {
						
						@Override
						public void onBitmapLoaded(Bitmap bmp1) {
							// TODO Auto-generated method stub
							down_dialog_bg_ll.setBackgroundDrawable(new BitmapDrawable(bmp1));
							
							bmpLoader.startLoad(new OnLoadBmp() {
								
								@Override
								public void onBitmapLoaded(Bitmap bmp2) {
									// TODO Auto-generated method stub
									((ImageView) btnyes).setImageBitmap(bmp2);
									
								}
							}, url+dialog_install);
							
							
						}
					}, url+dialog_bg);
				}
			}, url+dialog_girl);
			
		}else{
			mTableTip = LayoutInflater.from(mContext).inflate(R.layout.down_common_dlg,
					null);
			btnno = (Button) mTableTip.findViewById(R.id.btnNo);
			btnno.setOnClickListener(mOnClickListener2);
			btnyes = (Button) mTableTip.findViewById(R.id.btnYes);
			btnyes.setOnClickListener(mOnClickListener1);
		}
		mTableTip.setBackgroundColor(Color.TRANSPARENT);
		WindowManager wm = mWManager;
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWMParams = wmParams;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;// 2003; //
												// type�ǹؼ������2002��ʾϵͳ�����ڣ���Ҳ��������2003��
	//	wmParams.flags = 40;// �����������ɿ�
		iv = (ImageView) mTableTip.findViewById(R.id.icon);
		new BitmapLoder(mContext).startLoad(new OnLoadBmp() {
			
			@Override
			public void onBitmapLoaded(Bitmap bmp) {
				// TODO Auto-generated method stub
				if (null != bmp) {
					iv.setImageBitmap(bmp);
				}
			}
		}, pi.getImageUrl()); 
		
//		if (null != icBmp) {
//				iv.setImageBitmap(icBmp);
//		}
	
		
		tv = (TextView)mTableTip.findViewById(R.id.txtTitle);
		tvBrief = (TextView)mTableTip.findViewById(R.id.txtBrief);
		tv.setText(pi.getAppName());
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
			try {
				mWManager.removeView(mTableTip);
			} catch (Exception e) {
				
				// TODO: handle exception
			}
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
		AppUtil.showSetup(mContext, pi);
		removeTipView();
	}
}
