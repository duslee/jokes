package com.common.as.image;


import com.common.as.image.FileImageCache;
import com.common.as.image.MemImageCache;

import android.content.Context;
import android.graphics.Bitmap;

public class CacheManager{

	private static CacheManager instance = null;
	private FileImageCache mFileCache = null;
	private MemImageCache mMemCache = null;
	public static final String TEST_URL = "http://pic.52le.com:18081/image/30124_square";

	public  static synchronized CacheManager getInstance(Context context) {
		if (instance == null) {
			instance = new CacheManager(context);
		}
		return instance;
	}
	
	private CacheManager(Context context) {
		mMemCache = new MemImageCache();
		mFileCache = new FileImageCache(context);
	}
	
	public void flush(){
		mMemCache.flush();
	}
	
	public void cleanFileCache(){
		mFileCache.cleanCacheDir();
	}
	
	public void cleanCache(String url){
		mFileCache.cleanCacheFile(url);
		mMemCache.cleanBitmapCache(url);
	}
	
	public Bitmap get(String url){
		if (null == url) {
			return null;
		}
		Bitmap bmp = mMemCache.getBitmap(url);
		if (null == bmp) {
			bmp = mFileCache.getBitmap(url);
			if (null != bmp) {
				mMemCache.putBitmap(url, bmp);
			}
		}
		return bmp;
	}
	
	public Bitmap get(String url, boolean isOral){
		return get(url, isOral, true);
	}
	
	
	public boolean isInMem(String url){
		if (null == url) {
			return false;
		}
		Bitmap bmp = mMemCache.getBitmap(url);
        if (null != bmp) {
			return true;
		}
        return false;
	}
	
	/*
	 * bCheckFromFile,�Ƿ�����ⲿ�洢�ļ���ture����飬false�����ԡ�
	 * */
	public Bitmap get(String url, boolean isOral, boolean bCheckFromFile){
		if (null == url) {
			return null;
		}
		
		
		Bitmap bmp = mMemCache.getBitmap(url);
	//	MyLog.d("bitmap", url+":"+bmp);
        if(!bCheckFromFile  && null != bmp){
        	return bmp;
        }
        
		if (null == bmp) {
			bmp = mFileCache.getBitmap(url, isOral);
			if (null != bmp) {
		//		MyLog.d("putbitmap1", url+":"+bmp.getHeight()+":"+bmp.getWidth());
				mMemCache.putBitmap(url, bmp);
			}
		}
		return bmp;
	}
	
	public Bitmap getFromFile(String url){
		if (null == url) {
			return null;
		}
		Bitmap bmp = mMemCache.getBitmap(url);
        
		if (null == bmp) {
			bmp = mFileCache.getBitmap(url, true);
			if (null != bmp) {
				mMemCache.putBitmap(url, bmp);
			}
		}
		return bmp;
	}
	
	public synchronized void putBitmap(String url, Bitmap bmp){	
		cleanCache(url);
	//	MyLog.d("putbitmap", url+":"+bmp.getHeight()+":"+bmp.getWidth());
		mFileCache.putBitmap(url, bmp);
		mMemCache.putBitmap(url, bmp);
	}

	
	
	
	private Object mObject; //for list bmp cache
    /**
     * Store a single object in this Fragment.
     *
     * @param object The object to store
     */
    public void setObject(Object object) {
        mObject = object;
    }

    /**
     * Get the stored object.
     *
     * @return The stored object
     */
    public Object getObject() {
        return mObject;
    }
	
	

}
