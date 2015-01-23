package com.ly.duan.help;

import java.util.HashMap;
import java.util.Map;

import com.ly.duan.bean.MultiReqStatus;

public class GlobalHelp {
	
	private static GlobalHelp instance;
	
	private Map<Integer, MultiReqStatus> maps = new HashMap<Integer, MultiReqStatus>();
	
	public static GlobalHelp getInstance() {
		synchronized (GlobalHelp.class) {
			if (instance == null) {
				instance = new GlobalHelp();
			}
		}
		return instance;
	}
	
	public synchronized void setMultiReqStatus(MultiReqStatus reqStatus) {
		maps.put(0, reqStatus);
	}
	
	public MultiReqStatus getMultiReqStatus() {
		if (maps.containsKey(0)) {
			return maps.get(0);
		}
		return null;
	}

}
