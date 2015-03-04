package com.common.as.view;

import com.common.as.base.log.BaseLog;
import com.common.as.utils.AppPrefs;
import com.example.pushplug.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class TableView extends View implements Parcelable {
	private Context mContext;
	private static WindowManager mWManager; // WindowManager
	private static WindowManager.LayoutParams mWMParams = new WindowManager.LayoutParams();; // WindowManager����
	private static View mTableTip;
	private PopupWindow mPopuWin;
	private OnTopViewClick mOnTopViewClick;
	private View mShowView;
	private int mTag = 0;
	private static float midX;
	private static float midY;
	private int mOldOffsetX;
	private int mOldOffsetY;
	private float minX = 0;
	private float maxX = 0;
	private float minY = 0;
	private float maxY = 0;
	private float width = 0;
	private float  height = 0;
	private float maxDistanceX = 0;
	private float maxDiatanceY = 0;
	private float maxOffset = 20;
	private int[] startLocation = new int[2];
	private int[] endLocation = new int[2];
	private float maxlocationX;
	private float maxlocationY;
	private Object userData;
	/**
	 * 鼠标触摸开始位置
	 */
	private static float mTouchStartX = 0;
	/**
	 * 鼠标触摸结束位置
	 */
	private static float mTouchStartY = 0;
	private Bitmap icBmp;
	SharedPreferences sp;
	
	public static interface OnTopViewClick {
		public void onClick(Object userData);
	}

	public TableView(Context context, OnTopViewClick listener, Object userData) {
		// TODO Auto-generated constructor stub
		super(context);
		this.userData = userData;
		mContext = context;
		mOnTopViewClick = listener;
	}

	public void setIcBmp(Bitmap icBmp) {
		this.icBmp = icBmp;
	}
	public void fun() {
		
		sp = mContext.getSharedPreferences(AppPrefs.APP_INFO,
				mContext.MODE_PRIVATE);
		mWManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		mTableTip = LayoutInflater.from(mContext).inflate(R.layout.ctrl_window,
				null);
		mTableTip.setOnTouchListener(mTouchListener);
//		WindowManager.LayoutParams.FLAG_FULLSCREEN;
		mWMParams.type = LayoutParams.TYPE_PHONE;// 2003; //
													// type�ǹؼ������2002��ʾϵͳ�����ڣ���Ҳ��������2003��
		mWMParams.flags = 40| WindowManager.LayoutParams.FLAG_FULLSCREEN;// �����������ɿ�
		if (null != icBmp) {
			ImageView iv = (ImageView) mTableTip.findViewById(R.id.topIcon);
			if (sp.getInt(AppPrefs.APPLIST_ICON, 0) != 0) {
				icBmp = BitmapFactory.decodeResource(getResources(), sp.getInt(
						AppPrefs.APPLIST_ICON, R.drawable.push_list_icon));
			}
			iv.setImageBitmap(icBmp);
			if (sp.getInt(AppPrefs.APPLIST_ICON_WIDTH, 0) != 0) {
				mWMParams.width = sp.getInt(AppPrefs.APPLIST_ICON_WIDTH, 0);
			} else {
				mWMParams.width = icBmp.getWidth();
			}
			if (sp.getInt(AppPrefs.APPLIST_ICON_HEIGHTH, 0) != 0) {
				mWMParams.height = sp.getInt(AppPrefs.APPLIST_ICON_HEIGHTH, 0);
			} else {
				mWMParams.height = icBmp.getHeight();
			}
			width = icBmp.getWidth();
			height = icBmp.getHeight();
			BaseLog.d("main4", "wmParams.width=" + mWMParams.width
					+ ",,wmParams.height=" + mWMParams.height);
			// midX = mWManager.getDefaultDisplay().getWidth();
			// midY = mWManager.getDefaultDisplay().getHeight();
			// midY = -100;
		} else {
			mWMParams.width = 50;
			mWMParams.height = 50;
			midX = mWManager.getDefaultDisplay().getWidth() / 2 - 25;
			midY = mWManager.getDefaultDisplay().getHeight() / 2 - 44;

		}
		// 设置悬浮窗口长宽数据
//		mWMParams.width = WindowManager.LayoutParams.WRAP_CONTENT-100;
//		mWMParams.height = WindowManager.LayoutParams.WRAP_CONTENT-100;
		BaseLog.d("main5", "mWMParams.width="+mWMParams.width+",,mWMParams.height="+mWMParams.height);
		 mWMParams.gravity = Gravity.LEFT | Gravity.TOP;
		midX = mWManager.getDefaultDisplay().getWidth() - icBmp.getWidth();
		midY = mWManager.getDefaultDisplay().getHeight() - icBmp.getHeight() - 25;
		BaseLog.d("main5", "midX="+midX+",,midY="+midY);
		
		if (!sp.getBoolean(AppPrefs.APPLIST_CAN_MOVE, true)) {
			mWMParams.x = sp.getInt(AppPrefs.APPLIST_POSITION_X, (int) midX)+mWManager.getDefaultDisplay().getWidth()/2;
			mWMParams.y = mWManager.getDefaultDisplay().getHeight()-(sp.getInt(AppPrefs.APPLIST_POSITION_Y, (int) midY)+mWManager.getDefaultDisplay().getHeight()/2);
		}else{
			mWMParams.x = (int) midX;
			mWMParams.y = (int) midY;
		}

		mWMParams.format = -3; // ͸��
		mWManager.addView(mTableTip, mWMParams);// 
	}

	public void setmShowView(View mShowView) {
		this.mShowView = mShowView;
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {
		// float lastX, lastY;

		public boolean onTouch(View v, MotionEvent event) {
			// final int action = event.getAction();
			// 获取相对屏幕的坐标，即以屏幕左上角为原点
			//midX=876.0,,midY=431.0
//			int view_j =mContext.getWindow().getAttributes().flags; 
//	        // 全屏 66816 - 非全屏 65792  
//	        if(view_j != 66816){//非全屏  
//	        	
//	        }else{
//	        	midX = event.getRawX();
//	        	midY = event.getRawY() ; 
//	        } 
	        midX = event.getRawX();
	        /**
	         * midY,如何应用是全屏的话，就不用减25，如果应用不是全屏就需要减去25
	         */
        	midY = event.getRawY()-25; // 25是系统状态栏的高度
        	
			BaseLog.d("main5", "onTouch.midX="+midX+",,onTouch.midY="+midY);
			// float x = event.getX();
			// float y = event.getY();
//			Log.d("main5", "x=" + x + ",,y=" + y);
			if (mTag == 0) {
				mOldOffsetX = mWMParams.x; // ƫ����
				mOldOffsetY = mWMParams.y; // ƫ����

			}

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (!sp.getBoolean(AppPrefs.APPLIST_CAN_MOVE, true)) {

					mTouchStartX = sp.getInt(AppPrefs.APPLIST_POSITION_X, (int) midX);
					mTouchStartY = sp.getInt(AppPrefs.APPLIST_POSITION_Y, (int) midY);
					mTableTip.getLocationOnScreen(startLocation);
//					BaseLog.d("main5", "location[0]="+location[0]+",,lcation[1]="+location[1]);
					minX = startLocation[0] - width/2;
					maxX = startLocation[0] + width/2;
					minY = startLocation[1] - height/2;
					maxY = startLocation[1] + height/2;
					return true;
				}
				mTouchStartX = event.getX();
				mTouchStartY = event.getY();
//				int[] location = new int[2];
				
				mTableTip.getLocationOnScreen(startLocation);
//				BaseLog.d("main5", "location[0]="+location[0]+",,lcation[1]="+location[1]);
				minX = startLocation[0] - width/2;
				maxX = startLocation[0] + width/2;
				minY = startLocation[1] - height/2;
				maxY = startLocation[1] + height/2;
				BaseLog.d("main5", "mTouchStartX="+mTouchStartX+",,mTouchStartY="+mTouchStartY);

			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				
//				if((event.getX()- mTouchStartX)<maxOffset){
//					
//				}
//				if((event.getY() - mTouchStartY)<maxOffset){
//					
//				}
				
				maxDistanceX = event.getX()- mTouchStartX;
				maxDiatanceY = event.getY() - mTouchStartY;
				mTableTip.getLocationOnScreen(endLocation);
				if(endLocation[0]>=maxlocationX){
					maxlocationX = endLocation[0];
				}
				if(endLocation[1]>=maxlocationY){
					maxlocationY = endLocation[1];
				}
//				mWMParams.x = (int) (x - mTouchStartX); // 
//				mWMParams.y = (int) (y - mTouchStartY); // 
				BaseLog.d("main5", "event.getX()="+event.getX()+",,event.getY()="+event.getY());
				BaseLog.d("main5", "maxDistanceX="+maxDistanceX+",,maxDiatanceY="+maxDiatanceY);
				mTag = 1;
				if (!sp.getBoolean(AppPrefs.APPLIST_CAN_MOVE, true)) {
					return true;
				}
				try {
					updateViewPosition(v);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
//				if(Math.abs(maxDistanceX)>5&&Math.abs(maxDiatanceY)>5){
//					
//				}
				 

			}

			else if (event.getAction() == MotionEvent.ACTION_UP) {
				int newOffsetX = mWMParams.x;
				int newOffsetY = mWMParams.y;
//				BaseLog.d("main5", "newOffsetX="+newOffsetX+",,newOffsetY="+newOffsetY);
//				BaseLog.d("main5", "minX="+minX+",,maxX="+maxX);
//				BaseLog.d("main5", "minY="+minY+",,maxY="+maxY);
				BaseLog.d("main5", "Math.abs(maxlocationX-startLocation[0])="+Math.abs(maxlocationX-startLocation[0])+",,Math.abs(maxlocationY-startLocation[1])="+Math.abs(maxlocationY-startLocation[1]));
				if(newOffsetX>minX&&newOffsetX<maxX&&newOffsetY>minY&&newOffsetY<maxY&&Math.abs(maxlocationX-startLocation[0])<=maxOffset&&Math.abs(maxlocationY-startLocation[1])<=maxOffset){
//				if(newOffsetX>minX&&newOffsetX<maxX&&newOffsetY>minY&&newOffsetY<maxY&&Math.abs(maxDistanceX)<=maxOffset&&Math.abs(maxDiatanceY)<=maxOffset){
//				if(Math.abs(mOldOffsetX-newOffsetX)<20&&Math.abs(mOldOffsetY-newOffsetY)<20){
//				if (mOldOffsetX == newOffsetX && mOldOffsetY == newOffsetY) {

					if (null != mShowView) {
						// Log.d("main", "mShowView !=null");
						mPopuWin = new PopupWindow(mShowView,
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						mPopuWin.setTouchInterceptor(new OnTouchListener() {

							public boolean onTouch(View v, MotionEvent event) {
								// TODO Auto-generated method stub
								if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
									disPopu();
									return true;
								}
								return false;
							}
						});
						mPopuWin.setBackgroundDrawable(new BitmapDrawable());
						mPopuWin.setTouchable(true);
						mPopuWin.setFocusable(true);
						mPopuWin.setOutsideTouchable(true);
						mPopuWin.setContentView(mShowView);
						if (Math.abs(mOldOffsetX) > midX) {
							if (mOldOffsetX > 0) {
								mOldOffsetX = (int) midX;
							} else {
								mOldOffsetX = (int) -midX;
							}
						}
						if (Math.abs(mOldOffsetY) > midY) {
							if (mOldOffsetY > 0) {
								mOldOffsetY = (int) midY;
							} else {
								mOldOffsetY = (int) -midY;
							}
						}
						// mPopuWin.setAnimationStyle(R.style.AnimationPreview);
						mPopuWin.setFocusable(true);
						mPopuWin.update();
						mPopuWin.showAtLocation(mTableTip, Gravity.CENTER,
								-mOldOffsetX, -mOldOffsetY);

					} else {
						// Log.d("main", "mShowView ==null");
						mOnTopViewClick.onClick(userData);

					}
				} else {
					mTag = 0;
//					updateViewPosition(v);
					mTouchStartX = mTouchStartY = 0;
					maxlocationX = maxlocationY = 0;
				}
			}
			return true;
		}
	};

	public void removeTipView() {
		if (null != mTableTip) {
			mWManager.removeView(mTableTip);
		}
	}

	private void disPopu() {
		if (null != mPopuWin) {
			mPopuWin.dismiss();
		}
	}

	/**
	 * 更新弹出窗口位置
	 */
	private static void updateViewPosition(View view) {
		// 更新浮动窗口位置参数
		mWMParams.x = (int) (midX - mTouchStartX);
		mWMParams.y = (int) (midY - mTouchStartY);
		BaseLog.d("main5", "updateViewPosition.mWMParams.x="+mWMParams.x+",,mWMParams.y="+mWMParams.y);
		mWManager.updateViewLayout(mTableTip, mWMParams);
	}

	public interface ServiceListener {
		public void OnCloseService(boolean isClose);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}
}
