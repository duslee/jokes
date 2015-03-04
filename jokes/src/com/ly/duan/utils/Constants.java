package com.ly.duan.utils;

public class Constants {
	
	/** 标记此应用退出时界面处于哪一个tab */
	public static final String TAB_NAME = "tab_name";
	
	/** scheme */
	public static final String HTTP_SCHEME = "http://";
	/** domain */
	public static final String HTTP_DOMAIN = "upload.tortodream.com";
//	public static final String HTTP_DOMAIN = "192.168.31.100:80";
	public static final String HTTP_DOMAIN_COMMENT = "comment.tortodream.com";

	public static final String ACTION_GET_COLUMNS = HTTP_SCHEME + HTTP_DOMAIN + "/upload/api/getRootColumns.do";
	public static final String ACTION_GET_COLUMN_CONTENT = HTTP_SCHEME + HTTP_DOMAIN + "/upload/api/getColumnContent.do";
	public static final String ACTION_GET_BANNER_LIST = HTTP_SCHEME + HTTP_DOMAIN + "/upload/api/getBanner.do";
	public static final String ACTION_APPROVE_STAMP = HTTP_SCHEME + HTTP_DOMAIN + "/upload/api/appreciate.do";
	public static final String ACTION_GET_COMMENTS = HTTP_SCHEME + HTTP_DOMAIN + "/upload/api/getComments.do";
	public static final String ACTION_SEND_COMMENTS = HTTP_SCHEME + HTTP_DOMAIN_COMMENT + "/comment/api/sendComment.do";
//	public static final String ACTION_SEND_COMMENTS = HTTP_SCHEME + HTTP_DOMAIN + "/comment/api/sendComment.do";
	public static final String ACTION_GET_PUSH_LIST = HTTP_SCHEME + HTTP_DOMAIN + "/upload/api/getPush.do";
	
	/* http request that need keys of params */
	/* 1. Paging request need keys of params */
	public static final String KEY_APPID = "appid";
	public static final String KEY_CLMID = "columnId";
	public static final String KEY_FIRST = "first";
	public static final String KEY_MAX = "max";
	public static final String KEY_VER = "ver";
	
	/* 2. Comment request that need keys of params */
	/** 文章ID */
	public static final String KEY_CONTENTID = "contentId";
	/** 3-文章，4-帖子，8-段子 */
	public static final String KEY_CONTENT_TYPE = "contentType";
	/* 2.1 Getting comment request that need extra keys of params */
	/** 默认为第一页 */
	public static final String KEY_PAGE = "page";
	/* 2.2 Sending comment request that need extra keys of params */
	/** 服务端分配，没有的情况下暂时可以用IMEI号代替 */
	public static final String KEY_USER_ID = "userId";
	/** 不能超过1000字 */
	public static final String KEY_COMMENT = "comment";
	/** 在用户为游客的时候必须传 */
	public static final String KEY_USER_NICK = "userNick";
	/** 0-游客，1-注册用户 */
	public static final String KEY_USER_TYPE = "userType";
	
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
	
	/** key of MultiReqStatus */
	public static final String KEY_STATUS = "key_status";
	
	/** listview scroll distance */
	public static final int SCROLL_DISTANCE = 40;
	/** listview scroll duration */
	public static final int SCROLL_DURATION = 500;
	
}
