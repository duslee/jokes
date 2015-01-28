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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;
/**
 * An ImageLoader asynchronously loads image from a given url. Client may be
 * notified from the current image loading state using the
 * {@link ImageLoaderCallback}.
 * <p>
 * <em><strong>Note: </strong>You normally don't need to use the {@link ImageLoader}
 * class directly in your application. You'll generally prefer using an
 * {@link ImageRequest} that takes care of the entire loading process.</em>
 * </p>
 * 
 * @author Cyril Mottier
 */
public class ImageLoader {

    private static final String LOG_TAG = "ImageLoader";
    private static final String ASSERT_BMP_URI = "file:///android_asset/";

    /**
     * @author Cyril Mottier
     */
    public static interface ImageLoaderCallback {

        void onImageLoadingStarted(ImageLoader loader);

        void onImageLoadingEnded(ImageLoader loader, Bitmap bitmap, String etag, String lastModifed, String catchControl);

        void onImageLoadingFailed(ImageLoader loader, Throwable exception);
        
        void onImageNoNeedDown(ImageLoader loader);
    }

    private static final int ON_START = 0x100;
    private static final int ON_FAIL = 0x101;
    private static final int ON_END = 0x102;
    private static final int ON_NO_DOWN = 0x103; //未修改
    private static final int CORE_LOW_POOL_SIZE = 2;

    private final ExecutorService mExecutorService;
    private static BitmapFactory.Options sDefaultOptions;
    private static AssetManager sAssetManager;
    
    private static class LoadEndInfo{
    	Bitmap bmp;
    	String etag;
    	String modified;
    	String catchControl = "0";
    }

    public ImageLoader(Context context) {
    	
		ThreadFactory sThreadFactory = new ThreadFactory() {
	        private final AtomicInteger mCount = new AtomicInteger(1);

	        public Thread newThread(Runnable r) {
	            return new Thread(r, "high thread #" + mCount.getAndIncrement());
	        }
	    };
	    mExecutorService = Executors.newFixedThreadPool(CORE_LOW_POOL_SIZE, sThreadFactory);
	    
	    
        if (sDefaultOptions == null) {
        	sDefaultOptions = new BitmapFactory.Options();
        	sDefaultOptions.inDither = true;
        	sDefaultOptions.inScaled = true;
        	sDefaultOptions.inDensity = DisplayMetrics.DENSITY_MEDIUM;
        	sDefaultOptions.inTargetDensity = context.getResources().getDisplayMetrics().densityDpi;
        }
        sAssetManager = context.getAssets();
    }

    public Future<?> loadImage(String url,String modifed, String etag, ImageLoaderCallback callback) {
        return loadImage(url,modifed, etag, callback, null);
    }

    public Future<?> loadImage(String url,String modifed, String etag, ImageLoaderCallback callback, ImageDecorator bitmapProcessor) {
        return loadImage(url,modifed, etag, callback, bitmapProcessor, null);
    }
    
    public Future<?> loadImage(String url,String modifed, String etag,  ImageLoaderCallback callback, ImageDecorator bitmapProcessor, BitmapFactory.Options options) {
        return mExecutorService.submit(new ImageFetcher(url,modifed, etag,callback, bitmapProcessor, options),false);
    }

    private class ImageFetcher implements Runnable {

        private String mUrl;
        private ImageHandler mHandler;
        private ImageDecorator mBitmapProcessor;
        private BitmapFactory.Options mOptions;
        private String mModified_since;
        private String mEtag;

        public ImageFetcher(String url, String modifed, String etag, ImageLoaderCallback callback, ImageDecorator bitmapProcessor, BitmapFactory.Options options) {
            mUrl = url;
            mHandler = new ImageHandler(url, callback);
            mBitmapProcessor = bitmapProcessor;
            mOptions = options;
            mModified_since = modifed;
            mEtag = etag;
        }

        public void run() {

            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            final Handler h = mHandler;
            Bitmap bitmap = null;
            Throwable throwable = null;

            h.sendMessage(Message.obtain(h, ON_START));
            String etag = null;
            String modified = null;
            String cacheControl = null;
            InputStream inputStream = null;
            HttpURLConnection uc = null;
            try {

                if (TextUtils.isEmpty(mUrl)) {
                    throw new Exception("The given URL cannot be null or empty");
                }
                if (mUrl.startsWith(ASSERT_BMP_URI)) {
                    inputStream = sAssetManager.open(mUrl.replaceFirst(ASSERT_BMP_URI, ""));
                } else {
                	URL url = new URL(mUrl);
                	uc = (HttpURLConnection)url.openConnection();
                	uc.setRequestProperty("If-Modified-Since", mModified_since==null?"":mModified_since);
                	uc.setRequestProperty("If-None-Match", mEtag==null?"":mEtag);
                	uc.setRequestProperty("Cache-Control", "max-age=0");
                	uc.connect();
                	
                	int res = uc.getResponseCode();
                	if (res == 200) {
                		inputStream = url.openStream();
                		modified = uc.getHeaderField("Last-Modified");
                		etag = uc.getHeaderField("Etag");
                		cacheControl = uc.getHeaderField("Cache-Control");
					}else if (res == 304) {
						 h.sendMessage(Message.obtain(h, ON_NO_DOWN, null));
						 return;
					}
                	
                    
                }

                // TODO Cyril: Use a AndroidHttpClient?
                if (null == mOptions) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                  			
				} else {
	                  bitmap = BitmapFactory.decodeStream(inputStream, null, (mOptions == null) ? sDefaultOptions : mOptions);
	                  
				}
                


                
                if (mBitmapProcessor != null && bitmap != null) {
                    final Bitmap processedBitmap = mBitmapProcessor.decorateImage(bitmap);
                    if (processedBitmap != null) {
                        bitmap = processedBitmap;
                    }
                }

            } catch (Exception e) {
                // An error occured while retrieving the image
               // if (Config.GD_ERROR_LOGS_ENABLED) {
                 //   MyLog.d(LOG_TAG, "Error while fetching image");
               // }
                e.printStackTrace();
                throwable = e;
            }finally{
            	if(uc != null){
            		uc.disconnect();
            	}
            	if (inputStream != null) {
            		try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
            }

            if (bitmap == null) {
                if (throwable == null) {
                    // Skia returned a null bitmap ... that's usually because
                    // the given url wasn't pointing to a valid image
                    throwable = new Exception("Skia image decoding failed");
                }
                h.sendMessage(Message.obtain(h, ON_FAIL, throwable));
            } else {
            	LoadEndInfo lei = new LoadEndInfo();
            	lei.bmp = bitmap;
            	lei.modified = modified;
            	lei.etag = etag;
            	if (null != cacheControl) {
            		int index = cacheControl.indexOf("=");
            		if (-1 != index) {
            			lei.catchControl = cacheControl.substring(index+1);
					}
            		
				}
            	
                h.sendMessage(Message.obtain(h, ON_END, lei));
            }
        }
    }

    private class ImageHandler extends Handler {

        private String mUrl;
        private ImageLoaderCallback mCallback;

        private ImageHandler(String url, ImageLoaderCallback callback) {
        	// TODO: add
//        	super(Looper.getMainLooper());
        	super();
            mUrl = url;
            mCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case ON_START:
                    if (mCallback != null) {
                        mCallback.onImageLoadingStarted(ImageLoader.this);
                    }
                    break;

                case ON_FAIL:
                    if (mCallback != null) {
                        mCallback.onImageLoadingFailed(ImageLoader.this, (Throwable) msg.obj);
                    }
                    break;

                case ON_END:

                    final LoadEndInfo lei = (LoadEndInfo) msg.obj;


                    if (mCallback != null) {
                        mCallback.onImageLoadingEnded(ImageLoader.this, lei.bmp,lei.etag,lei.modified, lei.catchControl);
                    }
                    break;
                case ON_NO_DOWN:
                    if (mCallback != null) {
                        mCallback.onImageNoNeedDown(ImageLoader.this);
                    }
                	break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        };
    }

}
