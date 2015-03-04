package com.common.as.store;

public class AppData {

	final public String appid;
	final public String appVersion;
	public int    appVersionCode = 0;
	public String packageName = "";
	public String appName     = "";
	public String channel = "";
	public String pushid = "";
	public int    dexVer = 0;
	public AppData(String appid, String appVersion) {
		super();
		this.appid = appid;
		this.appVersion = appVersion;
	}
	
	
}
