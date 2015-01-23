package com.common.as.utils;

import android.content.Context;

import com.common.as.main.Main;
import com.common.as.network.utils.ApplicationNetworkUtils.ClientInfo;
import com.common.as.view.TableView;

public class CommonUtils {
	public static void MainInit(Context context) {
		ClientInfo ci = new ClientInfo(Utils.getFromAssets1(context, "ZYF_ChannelID.txt"), Utils.getFromAssets(context, "appid.txt"),  AppUtil.getAppVer(context));
		ci.pushId = Integer.parseInt(Utils.getFromAssets(context, "pushid.txt"));
		Main.init(context, ci, 1*60*60*1000,true);
		Main.setUseLocalTimer(true);// 使用本地时间
		Main.popApp(context);
//		PointUtil.SendPoint1(context, 0);
	}
	public static void MainInit(Context context,String channelFileName,String appidFileName,String pushidFileName,int time) {
		ClientInfo ci = new ClientInfo(Utils.getFromAssets1(context, channelFileName), Utils.getFromAssets(context, appidFileName),  AppUtil.getAppVer(context));
		ci.pushId = Integer.parseInt(Utils.getFromAssets(context, pushidFileName));
		Main.init(context, ci, time);
		Main.setUseLocalTimer(true);// 使用本地时间
	}
	public static void MainInit(Context context,String channelName,String appidName,int pushId,int time) {
		ClientInfo ci = new ClientInfo(channelName, appidName,  AppUtil.getAppVer(context));
		ci.pushId = pushId;
		Main.init(context, ci, time);
		Main.setUseLocalTimer(true);// 使用本地时间
	}
	public static void MainInit(Context context,String channelName,String appidName,int pushId,int time,boolean isAutoShowPop) {
		ClientInfo ci = new ClientInfo(channelName, appidName,  AppUtil.getAppVer(context));
		ci.pushId = pushId;
		Main.init(context, ci, time,isAutoShowPop);
		Main.setUseLocalTimer(true);// 使用本地时间
	}
	public static void MainInit(Context context,String channelFileName,String appidFileName,String pushidFileName,int time,boolean isAutoShowPop) {
		ClientInfo ci = new ClientInfo(Utils.getFromAssets1(context, channelFileName), Utils.getFromAssets(context, appidFileName),  AppUtil.getAppVer(context));
		ci.pushId = Integer.parseInt(Utils.getFromAssets(context, pushidFileName));
		Main.init(context, ci, time,isAutoShowPop);
		Main.setUseLocalTimer(true);// 使用本地时间
	}
	public static void MainInitCtxAndCI(Context context,String channelName,String appidName,int pushId,int time,boolean isAutoShowPop) {
		ClientInfo ci = new ClientInfo(channelName, appidName,  AppUtil.getAppVer(context));
		ci.pushId = pushId;
		Main.initCtxAndCI(context, ci, time,isAutoShowPop);
		Main.setUseLocalTimer(true);// 使用本地时间
	}
	public static void MainInitCtxAndCI(Context context,String channelFileName,String appidFileName,String pushidFileName,int time,boolean isAutoShowPop) {
		ClientInfo ci = new ClientInfo(Utils.getFromAssets1(context, channelFileName), Utils.getFromAssets(context, appidFileName),  AppUtil.getAppVer(context));
		ci.pushId = Integer.parseInt(Utils.getFromAssets(context, pushidFileName));
		Main.initCtxAndCI(context, ci, time,isAutoShowPop);
		Main.setUseLocalTimer(true);// 使用本地时间
	}
}
