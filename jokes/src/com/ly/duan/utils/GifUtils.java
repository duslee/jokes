package com.ly.duan.utils;

import android.content.Context;
import android.text.TextUtils;

import com.lidroid.xutils.util.OtherUtils;

public class GifUtils {

	private String defaultCachePath = "gif";
	private String mDiskCachePath = "";

	public GifUtils(Context context) {
		this(context, null);
	}

	public GifUtils(Context context, String diskCachePath) {
		if (context == null) {
			throw new IllegalArgumentException("context may not be null");
		}

		if (TextUtils.isEmpty(diskCachePath)) {
			mDiskCachePath = OtherUtils.getDiskCacheDir(context,
					defaultCachePath);
		} else {
			mDiskCachePath = OtherUtils.getDiskCacheDir(context, diskCachePath);
		}
	}
	
	public String getDiskCachePath() {
		return this.mDiskCachePath;
	}


	public String getFileName(String url) {
		String fileName = "";
		if (StringUtils.isBlank(url)) {
			return null;
		}
		fileName = url.substring(url.lastIndexOf("/") + 1);
		return fileName;
	}

}
