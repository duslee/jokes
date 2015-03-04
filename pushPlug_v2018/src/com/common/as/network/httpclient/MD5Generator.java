package com.common.as.network.httpclient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Generator {
	private static MessageDigest sMd5MessageDigest;

	static {
		try {
			sMd5MessageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String generateKey(String text) {
		byte[] byteMD5 = null;
		
		synchronized(sMd5MessageDigest){
			sMd5MessageDigest.reset();
			sMd5MessageDigest.update(text.getBytes());
			byteMD5 = sMd5MessageDigest.digest();
		}

		return toHexString(byteMD5);
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
}
