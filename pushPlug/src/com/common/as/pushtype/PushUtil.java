package com.common.as.pushtype;

import com.common.as.view.TableView;

import android.content.Context;
import android.graphics.Bitmap;

public interface PushUtil {
     public static enum PushType{
    	 TYPE_BTN,
    	 TYPE_POP_WND,
    	 TYPE_SHORTCUT,   	 
    	 TYPE_BACKGROUND,   	 
    	 TYPE_TOP_WND,
    	 TYPE_STORE_LIST
     }
	
 	public static final String INTENT_PACKAGE_NAME = "storename";
 	public static final String INTENT_ICON_BMP = "bmp";
	public abstract void doPush(Context ctx,PushInfo pi, Bitmap iconBmp);
	public abstract PushType getPushType();
	public abstract boolean isCanPush(PushInfo pi);
	
}
