package com.common.as.image;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;

public abstract class ImageCache {
	private static MessageDigest sMd5MessageDigest;

	static {
		try {
			sMd5MessageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized static String generateKey(String url) {
		sMd5MessageDigest.reset();
		sMd5MessageDigest.update(url.getBytes());

		return toHexString(sMd5MessageDigest.digest());
	}

	private static String toHexString(byte[] digest) {
		StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			final int b = digest[i] & 255;
			if (b < 16) {
				hexString.append('0');
			}
			hexString.append(Integer.toHexString(b));
		}
		return hexString.toString();
	}

	public abstract Bitmap getBitmap(String url);
	public abstract boolean putBitmap(String url, Bitmap bitmap);
}
