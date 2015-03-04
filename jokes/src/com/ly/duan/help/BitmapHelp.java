package com.ly.duan.help;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;

public class BitmapHelp {

	private static BitmapUtils bitmapUtils;
	private static final String CACHE_DIR = "duans_cache";

	public static BitmapUtils getInstance(Context context) {
		if (null == bitmapUtils) {
			syncInit(context);
		}
		return bitmapUtils;
	}
	
	private synchronized static void syncInit(Context context) {
		if (null == bitmapUtils) {
			bitmapUtils = new BitmapUtils(context, CACHE_DIR);
		}
	}

	public static void clearAllCache(Context context) {
		if (null == bitmapUtils) {
			getInstance(context);
		}
		bitmapUtils.clearCache();
		bitmapUtils.clearMemoryCache();
		bitmapUtils.clearDiskCache();
	}

}
