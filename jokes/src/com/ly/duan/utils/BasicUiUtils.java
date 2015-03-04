package com.ly.duan.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Some method help do some UI works User: cym Date: 13-9-22
 * 
 */
public class BasicUiUtils {
	public static void hiddenKeyboard(Class className, Context context,
			Activity activity) {
		try {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);

			if (activity.getCurrentFocus() != null) {
				if (activity.getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(activity.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void hiddenKeyBoardByClick(Class className, Context context,
			Activity activity, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			BasicUiUtils.hiddenKeyboard(className, context, activity);
		}
	}

	/**
	 * 根据手机的分辨率从dp的单位转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从px(像素)的单位转成为dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static void expandViews(final View v) {
		v.measure(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		final int targtetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1 ? RelativeLayout.LayoutParams.WRAP_CONTENT
						: (int) (targtetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targtetHeight / v.getContext().getResources()
				.getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapseViews(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / v.getContext().getResources()
				.getDisplayMetrics().density) * 1);
		v.startAnimation(a);
	}

	/** 动态设置ListView组件的高度（因其与ScrollView结合在一起） */
	public static void setLVHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (null == listAdapter) {
			return;
		}
		int totalHeight = listView.getPaddingTop()
				+ listView.getPaddingBottom();
		/* 获取ListView的高度 */
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
//			if (listItem instanceof ViewGroup) {
//				listItem.setLayoutParams(new LayoutParams(
//						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//			}
			if (listItem instanceof AbsListView) {
				listItem.setLayoutParams(new AbsListView.LayoutParams(
						AbsListView.LayoutParams.WRAP_CONTENT, 
						AbsListView.LayoutParams.WRAP_CONTENT));
			}
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		/* 重新设置ListView的高度 */
		RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) listView.getLayoutParams();
		lp.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(lp);
		listView.requestLayout();
	}

	public class DropDownAnim extends Animation {
		private final int targetHeight;
		private final View view;
		private final boolean down;

		public DropDownAnim(View view, int targetHeight, boolean down) {
			this.view = view;
			this.targetHeight = targetHeight;
			this.down = down;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			int newHeight;
			if (down) {
				newHeight = (int) (targetHeight * interpolatedTime);
			} else {
				newHeight = (int) (targetHeight * (1 - interpolatedTime));
			}
			view.getLayoutParams().height = newHeight;
			view.requestLayout();
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	}
}
