package com.common.as.pushtype;

import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.DeviceUtil;
import android.util.Log;

import com.common.as.activity.TPActivity;
import com.common.as.base.log.BaseLog;
import com.common.as.store.AppListManager;
import com.common.as.store.MaxDownInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.common.as.utils.DateUtil;
import com.common.as.utils.Preferences;
import com.common.as.utils.Utils;

public class PushShortCut extends PushBaseUtil{
	public static final String ACTION_INTENT_DEFAULT = "com.common.as.Action.sec_";
	
	private static final int MAX_PUSH_SIZE_PER_DAY = 2;

	@Override
	public PushType getPushType() {
		// TODO Auto-generated method stub
		return PushType.TYPE_SHORTCUT;
	}
	
	private static String getActionIntent(String packageName){
		//String ret = ACTION_INTENT_DEFAULT;
		if (null == packageName) {
			return null;
		}
		
		String[] part = packageName.split("\\.");
		if (null != part && part.length > 0) {
			return ACTION_INTENT_DEFAULT+part[part.length-1];
		}
		
		return null;
	}
	
	private static String getTagName(){
		return Preferences.SHORT_DOWN_SIZE_DAY;
		//return DateUtil.getCurrentYear()+DateUtil.getCurrentMonth()+DateUtil.getCurrentDay()+Preferences.SHORT_DOWN_SIZE_DAY;
	}
	
    protected void pushPaser(Context ctx,PushInfo downloadInfo, Bitmap bmp) {
    	if (null == downloadInfo) {
			return;
		}
    	
    	
    		createShortCut(ctx, downloadInfo, bmp);  
    		Log.d("main", "DeviceUtil.isWifiActive(ctx)="+DeviceUtil.isWifiActive(ctx));
//            if (DeviceUtil.isWifiActive(ctx)) {
//				PushInfoActionPaser.doClick(ctx, downloadInfo.pushType, downloadInfo.packageName);
//			}else{
//				
//			}


    }
    private static Bitmap small(Context context,Bitmap bmp){
        //获得Bitmap的高和宽
        int bmpWidth=bmp.getWidth();
        int bmpHeight=bmp.getHeight();
        //设置缩小比例
        double scale=0.7;
        //计算出这次要缩小的比例
        float disWidth =(float) ((240/480.0)*Utils.getDisplayMetrics(context).scaledDensity);
         BaseLog.d("main", "scaleWidth.width="+disWidth);
        //产生resize后的Bitmap对象
        Matrix matrix=new Matrix();
        matrix.postScale(disWidth,disWidth);
        Bitmap resizeBmp=Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
        return resizeBmp;
    }
    public static void createShortCut(Context ctx,PushInfo downloadInfo, Bitmap bmp){
    	BaseLog.d("main", "bmp.width="+bmp.getWidth()+",,bmp.height="+bmp.getHeight());
    	bmp = small(ctx,bmp);
    	BaseLog.d("main", "bmp1.width="+bmp.getWidth()+",,bmp1.height="+bmp.getHeight());
    	BaseLog.d("main1", "PushShortCut.createShortCut");
    	if(!AppPrefs.isControlShowPop){
			AppPrefs.mBitmap = null;
			PushInfo mPushInfo = AppListManager.findPushInfo(ctx, PushType.TYPE_SHORTCUT);
			BaseLog.d("main", "PushShortCut.startloadBmp111");
			if(null != mPushInfo){
				BaseLog.d("main", "PushShortCut.startloadBmp222");
				BitmapLoder lbiv = new BitmapLoder(ctx);
				lbiv.startLoad(new OnLoadBmp() {
					@Override
					public void onBitmapLoaded( Bitmap bmp) {
						AppPrefs.mBitmap = bmp;
						AppPrefs.bmpUpdate = 1;
					}
				}, mPushInfo.imageUrl);
			}
		}
    	try {
	    	String actionStr = getActionIntent(ctx.getPackageName());
	        if (null != actionStr) {
	        	 BaseLog.d("main", "PushShortCut.actionStr="+actionStr);
	        	PushInfo pi =PushInfos.getInstance().get(downloadInfo.getPackageName());
	        	if(null!=pi){
	        		BaseLog.d("main", "PushShortCut.pi="+pi.toString());
	        		if(!pi.isCreatedShortCut()){
	        			 BaseLog.d("main", "downloadInfo.isCreatedShortCut="+downloadInfo.isCreatedShortCut());
	     	        	if(!downloadInfo.isCreatedShortCut()){
	     		        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT"); // ��ݷ�ʽ�����
	     		        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, downloadInfo.getAppName()); //
	     		        shortcut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     		        shortcut.putExtra("duplicate", false); //不允许重复创建
	     		        Intent intent = new Intent();
	     		        intent.putExtra(TPActivity.TAG_URL_TYPE, downloadInfo.getUrlType());
	     		        intent.putExtra(TPActivity.TAG_PACKAGE_NAME, downloadInfo.getPackageName());
	     		        intent.setAction(actionStr);
	     		        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent); 
	     		        // ��ݷ�ʽ��ͼ��
	     		        if (null != bmp && !bmp.isRecycled()) {
	     		        	shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bmp);
	     				} else {
	     			        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(ctx,
	     			        		com.example.pushplug.R.drawable.default_icon);
	     			        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
	     				}
	     		        BaseLog.d("main", "downloadInfo.isCreatedShortCut11="+downloadInfo.isCreatedShortCut());
	     		        if(!downloadInfo.isCreatedShortCut()){
	     		        	ctx.sendBroadcast(shortcut);
	     		        }
	     		        downloadInfo.setCreatedShortCut(true);
	     		        BaseLog.d("main", "createshortcur.downloadinfo="+downloadInfo.toString());
	     		        PushInfos.getInstance().put(downloadInfo.getPackageName(), downloadInfo);
	     		        saveCount();
	     	        	}
	        		}
	        	}else{
	        		BaseLog.d("main", "PushShortCut.pi=null");
	        	}
	        	
	        }else{
	        	BaseLog.d("main", "PushShortCut.actionStr=null");
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
	private static void saveCount(){
		MaxDownInfo mdi = MaxDownInfo.get(getTagName());		
		if (mdi == null ){
			mdi = new MaxDownInfo();
		}
		mdi.addCount(DateUtil.getCurrentYear(), DateUtil.getCurrentMonth(), DateUtil.getCurrentDay());
		mdi.save(getTagName());
	}
	
	@Override
	public boolean isCanPush(PushInfo pi) {
		// TODO Auto-generated method stub

		if (AppListManager.getmSwitchInfo().getmShortCutSwitch() == 1) {
			MaxDownInfo mdi = MaxDownInfo.get(getTagName());
			//int pushedCount = Preferences.getInt(mContex, getTagName(), 0);
			if (mdi == null 
					|| !mdi.isSuperMax(MAX_PUSH_SIZE_PER_DAY
							, DateUtil.getCurrentYear(), DateUtil.getCurrentMonth(), DateUtil.getCurrentDay())) {
				
				if (null != mdi) {
					BaseLog.d(TAG, "pushedCount="+mdi.getmCurDownNum()+";"+mdi.getmCurTime());
				}
//				if (DateUtil.getCurrentHour() >=22 && DateUtil.getCurrentHour() <=24) {
//					if (super.isCanPush(pi)) {
//						return true;
//					}
//				}
				BaseLog.d("main", "PushShortCut.isCanPush="+super.isCanPush(pi));
				if (super.isCanPush(pi)) {
					return true;
				}
			}
			
//			if (pushedCount < MAX_PUSH_SIZE_PER_DAY) {
//				//一天push两个
//				if (DateUtil.getCurrentHour() >=22 && DateUtil.getCurrentHour() <=24) {
//					if (super.isCanPush(pi)) {
//						return true;
//					}
//				}
//
//						
//			}

			
		}
		return false;
	}
}
