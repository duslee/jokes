package com.ly.duan.utils;

public class Constants {
	
	/** 标记此应用退出时界面处于哪一个tab */
	public static final String TAB_NAME = "tab_name";

//	public static final String ACTION_GET_COLUMN_CONTENT = "http://upload.tortodream.com/upload/api/getColumnContent.do";
	public static final String ACTION_GET_COLUMN_CONTENT = "http://192.168.100.22/upload/api/getColumnContent.do";
//	public static final String ACTION_GET_BANNER_LIST = "http://upload.tortodream.com/upload/api/getBanner.do";
	public static final String ACTION_GET_BANNER_LIST = "http://192.168.100.22/upload/api/getBanner.do";
	public static final String ACTION_APPROVE_STAMP = "http://192.168.100.22/upload/api/appreciate.do";
	
	public static final String KEY_APPID = "appid";
	public static final String KEY_CLMID = "columnId";
	public static final String KEY_FIRST = "first";
	public static final String KEY_MAX = "max";
	public static final String KEY_VER = "ver";
	
	/* Banner条目类型 */
	/** 文章 */
	public static final int BANNER_TYPE_ARTICLE = 1;
	/** 帖子 */
	public static final int BANNER_TYPE_POSTBAR = 2;
	/** 网页 */
	public static final int BANNER_TYPE_PAGE = 3;
	/** APK */
	public static final int BANNER_TYPE_APK = 4;
	
	/* 条目赞&踩 */
	/** approve */
	public static final int TYPE_APPROVE = 1;
	/** stamp */
	public static final int TYPE_STAMP = 0;
	
}
