package com.common.as.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {
	
	Context mContext;
	
	public NetWorkUtil(Context context){
		this.mContext = context;
	}
	
	public List<String> getNetWorkList() {  
        ConnectivityManager cm = (ConnectivityManager) mContext  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo[] infos = cm.getAllNetworkInfo();  
        List<String> list = new ArrayList<String>();  
        if (infos != null) {  
            for (int i = 0; i < infos.length; i++) {  
                NetworkInfo info = infos[i];  
                String name = null;  
                if (info.getTypeName().equals("WIFI")) {  
                    name = info.getTypeName();  
                } else {  
                    name = info.getExtraInfo();  
                }  
                if (name != null && list.contains(name) == false) {  
                    list.add(name);  
                    // System.out.println(name);  
                }  
            }  
        }  
        return list;  
    }  
  
    public String getNetWork() {  
        String NOWNET = null;  
        ConnectivityManager cm = (ConnectivityManager) mContext  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo info = cm.getActiveNetworkInfo();  
        if (info != null && info.isAvailable()) {  
            if (info.getTypeName().equals("WIFI")) {  
                NOWNET = info.getTypeName();  
            } else {  
                NOWNET = info.getExtraInfo();// cmwap/cmnet/wifi/uniwap/uninet  
            }  
        }  
        return NOWNET;  
    }  
}
