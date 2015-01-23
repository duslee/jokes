/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
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
package com.common.as.image;

import com.common.as.image.ImageLoader.ImageLoaderCallback;

import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * An {@link ImageRequest} may be used to request an image from the network. The
 * process of requesting for an image is done in three steps:
 * <ul>
 * <li>Instantiate a new {@link ImageRequest}</li>
 * <li>Call {@link #load(Context)} to start loading the image</li>
 * <li>Listen to loading state changes using a {@link ImageRequestCallback}</li>
 * </ul>
 * 
 * @author Cyril Mottier
 */

//加载图片。 1 先从本地缓冲中查找图片
//2. 判断缓冲时间是否过期
//3. http 请求图片
public class ImageRequest {

    /**
     * @author Cyril Mottier
     */
    public static interface ImageRequestCallback {

        /**
         * Callback to be invoked when the request processing started.
         * 
         * @param request The ImageRequest that started
         */
        void onImageRequestStarted(ImageRequest request);

        /**
         * Callback to be invoked when the request processing failed.
         * 
         * @param request ImageRequest that failed
         * @param throwable The Throwable that occurs
         */
        void onImageRequestFailed(ImageRequest request, Throwable throwable);

        /**
         * Callback to be invoked when the request processing ended.
         * 
         * @param request ImageRequest that ended
         * @param image The resulting Bitmap
         */
        void onImageRequestEnded(ImageRequest request, Bitmap image);
        
        //set default bmp
        void onImageRequestStart(ImageRequest request, Bitmap image);

        /**
         * Callback to be invoked when the request processing has been
         * cancelled.
         * 
         * @param request ImageRequest that has been cancelled
         */
        void onImageRequestCancelled(ImageRequest request);
    }

    private static ImageLoader sImageLoader;

    private Future<?> mFuture;
    private String mUrl;
    private ImageRequestCallback mCallback;
    private ImageDecorator mBitmapProcessor;
    private BitmapFactory.Options mOptions;
    private final CacheManager sImageCacheManager;
    private Bitmap mTempBmp;

    public ImageRequest(Context ctx,String url, ImageRequestCallback callback) {
        this(ctx,url, callback, null);
    }

    public ImageRequest(Context ctx,String url, ImageRequestCallback callback, ImageDecorator bitmapProcessor) {
        this(ctx,url, callback, bitmapProcessor, null);
    }

    public ImageRequest(Context ctx,String url, ImageRequestCallback callback, ImageDecorator bitmapProcessor, BitmapFactory.Options options) {
        mUrl = url;
        mCallback = callback;
        mBitmapProcessor = bitmapProcessor;
        mOptions = options;
        sImageCacheManager = CacheManager.getInstance(ctx);
    }

    public void setImageRequestCallback(ImageRequestCallback callback) {
        mCallback = callback;
    }

    public String getUrl() {
        return mUrl;
    }

    public void load(Context context) {
    	load(context, true);
    }
    
    public void load(Context context, boolean bCheckFromFile) {
        if (mFuture == null) {
            Bitmap mBitmap = sImageCacheManager.get(mUrl, true, bCheckFromFile);
            if (mBitmap != null) {

			    if (mCallback != null) {
			            mCallback.onImageRequestEnded(ImageRequest.this, mBitmap);
			     }
			     return;
            }else{
                if (sImageLoader == null) {
                    sImageLoader = new ImageLoader(context);
                }
                mFuture = sImageLoader.loadImage(mUrl, null, null, new InnerCallback(), mBitmapProcessor, mOptions);

            }
        }
    }

    public void cancel() {
        if (!isCancelled()) {
            // Here we do not want to force the task to be interrupted. Indeed,
            // it may be useful to keep the result in a cache for a further use
            mFuture.cancel(false);
            if (mCallback != null) {
                mCallback.onImageRequestCancelled(this);
            }
        }
    }

    public final boolean isCancelled() {
        return mFuture.isCancelled();
    }

    private class InnerCallback implements ImageLoaderCallback {

        public void onImageLoadingStarted(ImageLoader loader) {
            if (mCallback != null) {
                mCallback.onImageRequestStarted(ImageRequest.this);
            }
        }


        public void onImageLoadingFailed(ImageLoader loader, Throwable exception) {
            if (mCallback != null && !isCancelled()) {
                mCallback.onImageRequestFailed(ImageRequest.this, exception);
            }
            mFuture = null;
        }

		@Override
		public void onImageNoNeedDown(ImageLoader loader) {
			// TODO Auto-generated method stub
            if (mCallback != null && !isCancelled()) {
            	if (null != mTempBmp) {
            		 mCallback.onImageRequestEnded(ImageRequest.this, mTempBmp);
				}else{
		            Bitmap mBitmap = sImageCacheManager.get(mUrl, true);
		            if (mBitmap != null) {
		            	mCallback.onImageRequestEnded(ImageRequest.this, mBitmap);
		            }
				}
               
                
            }
            mTempBmp = null;
            mFuture = null;
		}

		@Override
		public void onImageLoadingEnded(ImageLoader loader, Bitmap bitmap,
				String etag, String lastModifed, String cacheControl) {
			// TODO Auto-generated method stub
            sImageCacheManager.putBitmap(mUrl, bitmap);
            if (mCallback != null && !isCancelled()) {
                mCallback.onImageRequestEnded(ImageRequest.this, bitmap);
            }
            mFuture = null;
		}
    }

}
