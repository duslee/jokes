package com.ly.duan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * 自定义可以滑动的RelativeLayout, 类似于IOS的滑动删除页面效果，当我们要使用
 * 此功能的时候，需要将该Activity的顶层布局设置为SildingFinishLayout，
 * 然后需要调用setTouchView()方法来设置需要滑动的View
 * 
 * @author xiaanming
 * 
 * @blog http://blog.csdn.net/xiaanming
 * 
 */
public class SildingFinishLayout extends RelativeLayout implements
		OnTouchListener {
	/**
	 * SildingFinishLayout布局的父布局
	 */
	private ViewGroup mParentView;
	/**
	 * 处理滑动逻辑的View
	 */
	private View touchView;
	/**
	 * 滑动的最小距离
	 */
	private int mTouchSlop;
	/**
	 * 按下点的X坐标
	 */
	private int downX;
	/**
	 * 按下点的Y坐标
	 */
	private int downY;
	/**
	 * 临时存储X坐标
	 */
	private int tempX;
	/**
	 * 滑动类
	 */
	private Scroller mScroller;
	/**
	 * SildingFinishLayout的宽度
	 */
	private int viewWidth;
	/**
	 * 记录是否正在滑动
	 */
	private boolean isSilding;
	
	private OnSildingFinishListener onSildingFinishListener;
	private boolean isFinish;
	

	public SildingFinishLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SildingFinishLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// 获取SildingFinishLayout所在布局的父布局
			mParentView = (ViewGroup) this.getParent();
			viewWidth = this.getWidth();
		}
	}

	/**
	 * 设置OnSildingFinishListener, 在onSildingFinish()方法中finish Activity
	 * 
	 * @param onSildingFinishListener
	 */
	public void setOnSildingFinishListener(
			OnSildingFinishListener onSildingFinishListener) {
		this.onSildingFinishListener = onSildingFinishListener;
	}

	/**
	 * 设置Touch的View
	 * 
	 * @param touchView
	 */
	public void setTouchView(View touchView) {
		this.touchView = touchView;
		touchView.setOnTouchListener(this);
	}

	public View getTouchView() {
		return touchView;
	}

	/**
	 * 滚动出界面
	 */
	private void scrollRight() {
		final int delta = (viewWidth + mParentView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0,
				Math.abs(delta));
		postInvalidate();
	}

	/**
	 * 滚动到起始位置
	 */
	private void scrollOrigin() {
		int delta = mParentView.getScrollX();
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate();
	}

	/**
	 * touch的View是否是AbsListView， 例如ListView, GridView等其子类
	 * 
	 * @return
	 */
	private boolean isTouchOnAbsListView() {
		return touchView instanceof AbsListView ? true : false;
	}

	/**
	 * touch的view是否是ScrollView或者其子类
	 * 
	 * @return
	 */
	private boolean isTouchOnScrollView() {
		return touchView instanceof ScrollView ? true : false;
	}
	
	private boolean isTouchOnWebView() {
		return touchView instanceof WebView ? true : false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = tempX = (int) event.getRawX();
			downY = (int) event.getRawY();
//			Log.d("main", "downX=" + downX + ", downY=" + downY);
//			Log.d("main", "-----------------");
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) event.getRawX();
			int deltaX = tempX - moveX;
			tempX = moveX;
//			Log.d("main", "moveX=" + moveX + ", deltaX=" + deltaX + ", tempX=" + tempX);
//			Log.d("main", "mTouchSlop=" + mTouchSlop + ", (Math.abs(moveX - downX))=" + (Math.abs(moveX - downX)) + ", (Math.abs((int) event.getRawY() - downY))=" + (Math.abs((int) event.getRawY() - downY)));
			if (Math.abs(moveX - downX) > mTouchSlop
					&& Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
				isSilding = true;

				// 若touchView是AbsListView，
				// 则当手指滑动，取消item的点击事件，不然我们滑动也伴随着item点击事件的发生
//				if (isTouchOnAbsListView()) {
				if (isTouchOnAbsListView() || isTouchOnWebView()) {
					MotionEvent cancelEvent = MotionEvent.obtain(event);
					cancelEvent
							.setAction(MotionEvent.ACTION_CANCEL
									| (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					v.onTouchEvent(cancelEvent);
				}

			}

//			Log.d("main", "++++++++++++++++");
//			Log.d("main", "isSilding=" + isSilding);
			if (moveX - downX >= 0 && isSilding) {
				mParentView.scrollBy(deltaX, 0);
//				Log.d("main", "mParentView.getScrollX()=" + mParentView.getScrollX());

//				Log.d("main", "isTouchOnScrollView()=" + isTouchOnScrollView() + ", isTouchOnAbsListView()=" + isTouchOnAbsListView() + ", isTouchOnWebView()=" + isTouchOnWebView());
				// 屏蔽在滑动过程中ListView ScrollView等自己的滑动事件
//				if (isTouchOnScrollView() || isTouchOnAbsListView()) {
				if (isTouchOnScrollView() || isTouchOnAbsListView() || isTouchOnWebView()) {
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			isSilding = false;
//			Log.d("main", "mParentView.getScrollX()=" + mParentView.getScrollX() + ", -viewWidth / 2=" + -viewWidth / 2);
			if (mParentView.getScrollX() <= -viewWidth / 2) {
				isFinish = true;
				scrollRight();
			} else {
				scrollOrigin();
				isFinish = false;
			}
			break;
		}

		Log.d("main", "isTouchOnScrollView()=" + isTouchOnScrollView() + ", isTouchOnAbsListView()=" + isTouchOnAbsListView() + ", isTouchOnWebView()=" + isTouchOnWebView());
		// 假如touch的view是AbsListView或者ScrollView 我们处理完上面自己的逻辑之后
		// 再交给AbsListView, ScrollView自己处理其自己的逻辑
//		if (isTouchOnScrollView() || isTouchOnAbsListView()) {
		if (isTouchOnScrollView() || isTouchOnAbsListView() || isTouchOnWebView()) {
			return v.onTouchEvent(event);
		}

		// 其他的情况直接返回true
		return true;
	}

	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
//		Log.d("main", "computeScroll mScroller.computeScrollOffset()=" + mScroller.computeScrollOffset());
		if (mScroller.computeScrollOffset()) {
			mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

//			Log.d("main", "mScroller.isFinished()=" + mScroller.isFinished() + ", isFinish=" + isFinish);
//			if (mScroller.isFinished()) {

//				if (onSildingFinishListener != null && isFinish) {
//					onSildingFinishListener.onSildingFinish();
				if (onSildingFinishListener != null) {
					if (isFinish) {
						onSildingFinishListener.onSildingFinish();
					}
				}else{
					//如果没有设置回调接口, 直接划到起始位置
					scrollOrigin();
				}
//			}
		}
	}
	

	public interface OnSildingFinishListener {
		public void onSildingFinish();
	}

}
