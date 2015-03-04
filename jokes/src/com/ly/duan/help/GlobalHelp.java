package com.ly.duan.help;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.ly.duan.bean.MultiReqStatus;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.PreferencesUtils;

public class GlobalHelp {
	
	private static GlobalHelp instance;
	
	private static Map<Integer, MultiReqStatus> maps = new HashMap<Integer, MultiReqStatus>();
	
	public static GlobalHelp getInstance() {
		synchronized (GlobalHelp.class) {
			if (instance == null) {
				instance = new GlobalHelp();
			}
		}
		return instance;
	}
	
	public synchronized void setMultiReqStatus(Context context, MultiReqStatus reqStatus) {
		maps.put(0, reqStatus);
		/* save as Json String */
		String jsonString = JSON.toJSONString(reqStatus);
		PreferencesUtils.getConfigSharedPreferences(context.getApplicationContext())
			.edit().putString(Constants.KEY_STATUS, jsonString).commit();
	}
	
	public MultiReqStatus getMultiReqStatus(Context context) {
		MultiReqStatus status = null;
		if (maps.containsKey(0)) {
			status = maps.get(0);
		}
		if (null == status) {
			String jsonString = PreferencesUtils.getConfigSharedPreferences(context.getApplicationContext())
					.getString(Constants.KEY_STATUS, null);
			status = JSON.toJavaObject(JSON.parseObject(jsonString), MultiReqStatus.class);
		}
		return status;
	}

}
