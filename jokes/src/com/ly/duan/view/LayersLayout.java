package com.ly.duan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class LayersLayout extends LinearLayout {
	/**
	 * 自定义图层
	 */
	private ViewFlow viewFlow;
	private String TAG = "MyViewFlow";
	boolean onHorizontal = false;
	float x = 0.0f;
	float y = 0.0f;

	public LayersLayout(Context context) {
		super(context);
	}

	public LayersLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setView(ViewFlow viewFlow) {
		this.viewFlow = viewFlow;
	}

	// 对触屏事件进行重定向
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onHorizontal = false;
			x = ev.getX();
			y = ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			onHorizontal = false;
			break;
		default:
			break;
		}

		if (ViewFlow.onTouch) {
			float dx = Math.abs(ev.getX() - x);
			Log.i("MyViewFlow", "dx:=" + dx);
			if (dx > 20.0) {
				onHorizontal = true;
			}
			if (onHorizontal) {
				Log.i(TAG, "viewFlow处理");
				return true;
			} else {
				Log.i(TAG, "listview处理");
				return super.onInterceptTouchEvent(ev);
			}
		} else {
			return super.onInterceptTouchEvent(ev);
		}

	}

	// 对触屏事件进行处理
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "viewFlow是否被点击：" + ViewFlow.onTouch);
		if (ViewFlow.onTouch) {
			return viewFlow.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}
}
