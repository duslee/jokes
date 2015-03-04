package com.ly.duan.help;

import android.content.Context;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.ly.duan.bean.SavedStatus;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.PreferencesUtils;
import com.ly.duan.utils.StringUtils;

public class SavedStatusHelp {
	
	private static SavedStatusHelp instance;
	
	public static SavedStatusHelp getInstance() {
		synchronized (SavedStatusHelp.class) {
			if (null == instance) {
				instance = new SavedStatusHelp();
			}
		}
		return instance;
	}
	
	public synchronized void saveTabName(Context context, String tabName) {
		/* 1. saved in SP */
		PreferencesUtils.getConfigSharedPreferences(context.getApplicationContext())
			.edit().putString(Constants.TAB_NAME, tabName).commit();
		
		/* 2. saved in DB */
		try {
			SavedStatus status = DBHelp.getInstance(context.getApplicationContext()).findFirst(SavedStatus.class);
			if (null == status) {
				status = new SavedStatus();
			}
			LogUtils.d("before status=" + status.toString());
			status.setTabName(tabName);
			LogUtils.d("after status=" + status.toString());
			DBHelp.getInstance(context.getApplicationContext()).saveOrUpdate(status);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public String getTabName(Context context) {
		String tabName = PreferencesUtils.getConfigSharedPreferences(context.getApplicationContext())
				.getString(Constants.TAB_NAME, "");
		if (StringUtils.isBlank(tabName)) {
			try {
				SavedStatus status = DBHelp.getInstance(context.getApplicationContext()).findFirst(SavedStatus.class);
				if (null != status) {
					tabName = status.getTabName();
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		return tabName;
	}

}
