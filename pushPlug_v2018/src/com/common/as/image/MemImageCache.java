package com.common.as.image;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.graphics.Bitmap;

public class MemImageCache extends ImageCache {

	private final HashMap<String, SoftReference<Bitmap>> sSoftBitmapCache;
				

	public MemImageCache() {
		super();
		sSoftBitmapCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	@Override
	public Bitmap getBitmap(String url) {
		if (null == url) {
			return null;
		}
		String key = generateKey(url);
		
		synchronized (sSoftBitmapCache) {
			final SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(key);
			if (bitmapReference != null) {
				final Bitmap bitmap = bitmapReference.get();
				if (bitmap != null) {
					return bitmap;
				} else {
					sSoftBitmapCache.remove(key);
				}
			}
		}
		return null;
	}

	@Override
	public boolean putBitmap(String url, Bitmap bitmap) {
		String key = generateKey(url);
		
		if(bitmap != null){
			synchronized(sSoftBitmapCache){
				sSoftBitmapCache.put(key, new SoftReference<Bitmap>(bitmap));
			}
			return true;
		}		
		return false;
	}
	
	public void cleanBitmapCache(String url){
		String key = generateKey(url);		
		synchronized (sSoftBitmapCache) {
			SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(key);
			if (bitmapReference != null) {
				sSoftBitmapCache.remove(key);
	    	}
    	}
	}
	
	public void flush(){
		sSoftBitmapCache.clear();
	}

}
