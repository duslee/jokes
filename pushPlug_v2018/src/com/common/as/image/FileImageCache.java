package com.common.as.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.common.as.base.log.BaseLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class FileImageCache extends ImageCache {
	private final File mCacheDir;
	public Context mContext;
    private static final String FILE_DIR = "img";
    public static final int DEFAULT_MAX_PIXS = 128*128;
	public FileImageCache(Context context) {
		File dir = context.getFilesDir();
		mCacheDir = new File(dir+File.separator+FILE_DIR+ File.separator);
		BaseLog.d("main4", "mCacheDir.getAbsolutePath()="+mCacheDir.getAbsolutePath());
		if (!mCacheDir.exists()) {
			mCacheDir.mkdir();
		}
		mContext = context;
	}
	
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {

	    int initialSize = computeInitialSampleSize(options, minSideLength,

	            maxNumOfPixels);



	    int roundedSize;

	    if (initialSize <= 8) {

	        roundedSize = 1;

	        while (roundedSize < initialSize) {

	            roundedSize <<= 1;

	        }

	    } else {

	        roundedSize = (initialSize + 7) / 8 * 8;

	    }



	    return roundedSize;

	}



	//Android提供了一种动态计算采样率的方法。
	private static int computeInitialSampleSize(BitmapFactory.Options options,

	        int minSideLength, int maxNumOfPixels) {

	    double w = options.outWidth;
	    double h = options.outHeight;


	    int lowerBound = (maxNumOfPixels == -1) ? 1 :

	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

	    int upperBound = (minSideLength == -1) ? 128 :

	            (int) Math.min(Math.floor(w / minSideLength),

	            Math.floor(h / minSideLength));



	    if (upperBound < lowerBound) {

	        // return the larger one when there is no overlapping zone.

	        return lowerBound;

	    }



	    if ((maxNumOfPixels == -1) &&

	            (minSideLength == -1)) {

	        return 1;

	    } else if (minSideLength == -1) {

	        return lowerBound;

	    } else {

	        return upperBound;

	    }

	} 
	
	public Bitmap getBitmap(String url,boolean isOral){
		if (isOral) {
			String key = generateKey(url);
			
			Bitmap bitmap = null;
			File file = new File(mCacheDir, key);
			if (file.exists() && file.isFile()) {
				try {
					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath()); 
				} catch (OutOfMemoryError err) {
				}
			}
			return bitmap;	
		} else {
            return getBitmap(url);
		}
	}

	@Override
	public Bitmap getBitmap(String url) {
		String key = generateKey(url);
		
		Bitmap bitmap = null;
		File file = new File(mCacheDir, key);
		if (file.exists() && file.isFile()) {
			BitmapFactory.Options opts = new BitmapFactory.Options();  
			//设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height。有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。
			opts.inJustDecodeBounds = true;  
			BitmapFactory.decodeFile(file.getAbsolutePath(), opts);  
			opts.inSampleSize = computeSampleSize(opts, -1, DEFAULT_MAX_PIXS);  	
			opts.inJustDecodeBounds = false;
			try {
				bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),opts); 
			} catch (OutOfMemoryError err) {
			}
		}
		return bitmap;
	}

	@Override
	public synchronized boolean putBitmap(String url, Bitmap bitmap) {
		String key = generateKey(url);
		
		if (bitmap != null) {
			File file = new File(mCacheDir, key);
			if (file.exists() && file.isFile()) {
				return true; // file exists
			}

			try {
				FileOutputStream fos = new FileOutputStream(file);
						//new FileOutputStream(file);
				boolean saved = bitmap.compress(CompressFormat.PNG, 80, fos);
				fos.flush();
				fos.close();

				return saved;
			} catch (IOException e) {
				System.out.println("mingo:err");
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void cleanCacheDir(){
		if (mCacheDir.exists()) {
			File[] files =mCacheDir.listFiles();
			for (File file : files) {
				if (file.exists() && file.isFile()) {
					file.delete(); // file exists
				}
			}

		}
	}
	
	public void cleanCacheFile(String url){
		String key = generateKey(url);
		
		File file = new File(mCacheDir, key);
		if (file.exists() && file.isFile()) {
			file.delete();
		}
	}
	
	public Context getContext(){
		return this.mContext;
	}
}
