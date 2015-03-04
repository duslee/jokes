/*
 * Copyright (C) 2013 Charon Chui <charon.chui@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.charon.pulltorefreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.sjm.gxdz.R;

/**
 * Android load more ListView when scroll down.
 * 
 * @author Charon Chui
 */
public class PullRefreshAndLoadMoreListView extends PullToRefreshListView {
	
    protected static final String TAG = "LoadMoreListView";
    private View mFooterView;
    private OnScrollListener mOnScrollListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * If is loading now.
     */
    private boolean mIsLoading;
    
//    private int mFooterheight;

//    private int mCurrentScrollState;

    public PullRefreshAndLoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PullRefreshAndLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullRefreshAndLoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mFooterView = View.inflate(context, R.layout.load_more_footer, null);
        addFooterView(mFooterView);
//        mFooterheight = mFooterView.getHeight();
        hideFooterView();
        /*
         * Must use super.setOnScrollListener() here to avoid override when call this view's setOnScrollListener method
         */
        super.setOnScrollListener(superOnScrollListener);
    }

    /**
     * Hide the load more view(footer view)
     */
    private void hideFooterView() {
//    	Log.e("bmp test", "hide Footer View!!!");
//        mFooterView.setPadding(0, -mFooterView.getHeight(), 0, 0);
    	scrollBy(0, -mFooterView.getHeight());
    	mFooterView.setVisibility(View.GONE);
//    	android.widget.AbsListView.LayoutParams lp = (android.widget.AbsListView.LayoutParams) mFooterView.getLayoutParams();
//    	if (lp == null) {
//			lp = new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
//		}
//    	lp.height = 0;
//    	mFooterView.setLayoutParams(lp);
//    	requestLayout();
    }

    /**
     * Show load more view
     */
    private void showFooterView() {
//    	Log.e("bmp test", "show Footer View!!!");
    	scrollBy(0, 0);
        mFooterView.setVisibility(View.VISIBLE);
//        mFooterView.setPadding(0, 0, 0, 0);
//        scrollBy(0, 0);
//    	android.widget.AbsListView.LayoutParams lp = (android.widget.AbsListView.LayoutParams) mFooterView.getLayoutParams();
//    	lp.height = mFooterheight;
//    	mFooterView.setLayoutParams(lp);
//    	requestLayout();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Set load more listener, usually you should get more data here.
     * 
     * @param listener OnLoadMoreListener
     * @see OnLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    /**
     * When complete load more data, you must use this method to hide the footer view, if not the footer view will be
     * shown all the time.
     */
    public void onLoadMoreComplete() {
//    	Log.e("bmp test", "load more complete!!!");
        mIsLoading = false;
        hideFooterView();
    }
    
    private boolean mLastItemVisible;

    private OnScrollListener superOnScrollListener = new OnScrollListener() {
    	
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        	Log.e("bmp test", "1111 scrollState=" + scrollState);
        	if (scrollState == OnScrollListener.SCROLL_STATE_IDLE 
        			&& null != mOnLoadMoreListener 
        			&& mLastItemVisible) {
        		Log.e("bmp test", "onScrollStateChanged mIsLoading=" + mIsLoading + 
        				", mLastItemVisible=" + mLastItemVisible);
        		if (!mIsLoading) {
        			showFooterView();
            		mIsLoading = true;
            		mOnLoadMoreListener.onLoadMore();
				}
    		}
        	
//        	mCurrentScrollState = scrollState;
            // Avoid override when use setOnScrollListener
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        	
//        	Log.e("bmp test", "onScrollStateChanged mCurrentScrollState=" + mCurrentScrollState 
//					+ ", scrollState=" + scrollState);
        }
        
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        	Log.e("bmp test", "onScroll push onScroll3 first:"+firstVisibleItem+", visi:"+visibleItemCount+", total:"+totalItemCount);
//        	Log.e("bmp test", "onScroll mIsLoading=" + mIsLoading + ", mCurrentScrollState=" + mCurrentScrollState + ", touchUp=" + touchUp);
            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            
            // The count of footer view will be add to visibleItemCount also are
            // added to totalItemCount
//            if (visibleItemCount == totalItemCount) {
//                // If all the item can not fill screen, we should make the
//                // footer view invisible.
//                hideFooterView();
//            } else if (!mIsLoading
//            		&& (firstVisibleItem + visibleItemCount >= totalItemCount)
//                    && mCurrentScrollState != SCROLL_STATE_IDLE
//                    && !touchUp) {
//                showFooterView();
//                mIsLoading = true;
//                if (mOnLoadMoreListener != null) {
////                	Log.e("bmp test", "onScroll mOnLoadMoreListener=" + mOnLoadMoreListener);
//                    mOnLoadMoreListener.onLoadMore();
//                }
//            }
//            if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
//            	mFooterView.setVisibility(View.VISIBLE);
//            	mFooterView.setPadding(0, 0, 0, 0);
//			}
            if (null != mOnLoadMoreListener) {
    			mLastItemVisible = (totalItemCount > 0)
    					&& (firstVisibleItem + visibleItemCount >= totalItemCount);
    		}
        }
    };

    /**
     * Interface for load more
     */
    public interface OnLoadMoreListener {
        /**
         * Load more data.
         */
        void onLoadMore();
    }

}
