package com.ly.duan.help;

import android.content.Context;

import com.lidroid.xutils.DbUtils;

public class DBHelp {

	private static DbUtils dbUtils = null;
	private static final String DB_NAME = "duans.db";

	public static DbUtils getInstance(Context context) {
		synchronized (DBHelp.class) {
			if (null == dbUtils) {
				dbUtils = DbUtils.create(context, DB_NAME);
				dbUtils.configAllowTransaction(true);
				dbUtils.configDebug(true);
			}
		}
		return dbUtils;
	}

}
