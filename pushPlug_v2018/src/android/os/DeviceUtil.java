package android.os;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class DeviceUtil {

	
	public static boolean isWifiActive(Context context){   
		ConnectivityManager connectivity = (ConnectivityManager)     
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) {    
			NetworkInfo[] info = connectivity.getAllNetworkInfo();    
			if (info != null) {    
				for (int i = 0; i < info.length; i++) {    
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {    
						return true;    
					}   
					
					if (info[i].getType()==TelephonyManager.NETWORK_TYPE_EVDO_A && info[i].isConnected()) {    
						return true;    
					}   
					
					
				}    
			}    
		}    
		
		return false;   
	}
	
	public static String getFactory()
	{
		return android.os.Build.MANUFACTURER;
	}
	
	public static String getType()
	{
		return android.os.Build.MODEL;
	}
	public static int getVersion()
	{
		return android.os.Build.VERSION.SDK_INT;
	}
	public static String getPhoneVersion()
	{
		return android.os.Build.VERSION.RELEASE;
	}
}
