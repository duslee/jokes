package com.ly.duan.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lidroid.xutils.util.LogUtils;
import com.sjm.gxdz.R;

public class TabMenu extends PopupWindow {
	
	private View mView;
	private Button setButton;

	public TabMenu(Context context) {
		super(context);
		init(context);
	}

	public TabMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TabMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mView = inflater.inflate(R.layout.menu_setting, null);
		setButton = (Button) mView.findViewById(R.id.setButton);
		
		/* set view */
		setContentView(mView);
//		setBackgroundDrawable(null);
		setBackgroundDrawable(new BitmapDrawable());  
		// 设置弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
		
		setAnimationStyle(R.style.PopupAnimation);
		update();
		
		/* 设置相关属性 */
		/*设置触摸外面时消失*/
		setOutsideTouchable(true);
		setTouchable(true);
		/*设置点击menu以外其他地方以及返回键退出*/
		setFocusable(true);
		
		mView.setFocusableInTouchMode(true);
		/* dismiss PopupWindow when press MENU down */
		mView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				LogUtils.e("onKey event.getAction=" + event.getAction() + ", keyCode=" + keyCode + ", isShowing()=" + isShowing());
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (isShowing() && (event.getAction() == KeyEvent.ACTION_DOWN))) {  
	                dismiss();// 这里写明模拟menu的PopupWindow退出就行  
	                return true;  
	            } 
				return false;
			}
		});
		// mView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				LogUtils.e("onTouch event.getAction=" + event.getAction());
				int height = mView.findViewById(R.id.btn_ll).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
                
        });
		
	}
	
	public Button getSetButton() {
		return setButton;
	}
	
}
