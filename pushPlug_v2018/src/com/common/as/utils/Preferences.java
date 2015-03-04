package com.common.as.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	public static final String Filename = "push";
	
	
//	public static final String APP_STORE_TAG = "appstore";
//	public static final String APP_PUSH_TAG = "apppush";
	public static final String APP_List_TAG = "appstore";
	public static final String APP_BG_TAG = "appbg";
	public static final String APP_SHORT_CUT_TAG = "appsc";
	public static final String APP_POP_TAG = "apppop";
	public static final String APP_BTN_TAG = "appbanner";
	public static final String FirstGetServerT = "first_server_t";
	
	public static final String BG_DOWN_SIZE_DAY = "_bg";
	public static final String SHORT_DOWN_SIZE_DAY = "_cut";
	
	public static boolean getBoolean(Context context, String tag, boolean defaultValue){
		SharedPreferences sp = context.getSharedPreferences(Preferences.Filename, 0);
		return sp.getBoolean(tag, defaultValue);
	}
	
	public static void setBoolean(Context context, String tag, boolean value){
		SharedPreferences sp = context.getSharedPreferences(Preferences.Filename, 0);
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean(tag, value);			    	
		edit.commit();
	}
	
	public static int getInt(Context context, String tag, int defaultValue){
		SharedPreferences sp = context.getSharedPreferences(Preferences.Filename, 0);
		return sp.getInt(tag, defaultValue);
	}
	
	public static void setInt(Context context, String tag, int value){
		SharedPreferences sp = context.getSharedPreferences(Preferences.Filename, 0);
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt(tag, value);			    	
		edit.commit();
	}
	
	
	public static long getLong(Context context, String tag, long defaultValue){
		SharedPreferences sp = context.getSharedPreferences(Preferences.Filename, 0);
		return sp.getLong(tag, defaultValue);
	}
	
	public static void setLong(Context context, String tag, long value){
		SharedPreferences sp = context.getSharedPreferences(Preferences.Filename, 0);
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong(tag, value);			    	
		edit.commit();
	}
}
