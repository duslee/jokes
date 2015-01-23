package com.common.as.utils;

import android.graphics.Bitmap;

public class AppPrefs {
	public static final String APP_INFO = "app_info";
	public static final String CONTROL_SWITCH = "control_switch";
	public static final String SHOW_APPLIST = "show_applist";
	public static final String HIDE_APPLIST = "hide_applist";
	public static final String APPLIST_POSITION_X = "applist_position_x";
	public static final String APPLIST_POSITION_Y = "applist_position_y";
	public static final String APPLIST_ICON_WIDTH = "applist_position_width";
	public static final String APPLIST_ICON_HEIGHTH= "applist_position_heighth";
	public static final String APPLIST_CAN_MOVE = "applist_can_move";
	public static final String APPLIST_ICON = "applist_icon";
	//public static final String APPLIST_ISSHOW = "applist_isshow";
	public static final String SYNC_DATA = "sync_data";
	public static final String DISTANCE = "distance";
	public static boolean isEnable = false;
	public static boolean listIsShow = false;
	public static boolean popIsCanShow = false;
	public static boolean popIsShow = false;
	public static boolean isControlShowPop = false;
	public static boolean isListActivity = false;
	public static boolean isBGfirst = true;
	public static Bitmap mBitmap;
	public static int bmpUpdate = 0;
}
