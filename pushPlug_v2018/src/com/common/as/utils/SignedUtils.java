package com.common.as.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.common.as.network.httpclient.MPHttpClientUtils;

public class SignedUtils {
	
	private static SignedUtils mSignedUtils;
	private static DataBaseHelper dbHelper;
	
	public static String serverdate = "";
	public static SignedUtils getInstance(){
		if(null == mSignedUtils){
			mSignedUtils = new SignedUtils();
		}
		return mSignedUtils;
	}
	
	public static void startSign(Context ctx){
		if(null == dbHelper){
			dbHelper = new DataBaseHelper(ctx);
		}
		//Log.d("main", "dbHelper.queryIsExist(parseServeDate(serverdate))="+dbHelper.queryIsExist(parseServeDate(serverdate)));
		if(null!= serverdate&&!"".equals(serverdate)){
			if(!dbHelper.queryIsExist(parseServeDate(serverdate))){
				dbHelper.insert_signed(parseServeDate(serverdate));
				MPHttpClientUtils.signedLog(0, null, ctx);
			}
		}else{
			if(!dbHelper.queryIsExist(parseLocalDate())){
				dbHelper.insert_signed(parseLocalDate());
				MPHttpClientUtils.signedLog(0, null, ctx);
			}
		}
	}
	
	private static String parseServeDate(String serverDate){
		SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(serverDate);
		return formart.format(date);
	}
	private static String parseLocalDate(){
		SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd");
		return formart.format(new Date());
	}
	
}
