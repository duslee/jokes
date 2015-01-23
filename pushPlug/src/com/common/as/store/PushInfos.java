package com.common.as.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Xml;

import com.common.as.base.log.BaseLog;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;


public class PushInfos {
	FileUtils mFileUtils;
	private static final String CACHE_DIR = "com.import.pis";
	
	
	private static PushInfos instance;
	private File mCacheDir;
	
	
	public static synchronized PushInfos  getInstance(){
		if (null == instance) {
			instance = new PushInfos();
		}
		
		return instance;
	}
	
	private void init(){
		if (null == mFileUtils) {
			mFileUtils = new FileUtils();
		}
		
		
		if (!mFileUtils.isDirExist(CACHE_DIR)) {
			mCacheDir = mFileUtils.creatSDDir(CACHE_DIR);
		}else if(null == mCacheDir){
			mCacheDir = mFileUtils.getDir(CACHE_DIR);
		}
	}
	
	
	public ArrayList<PushInfo> getAllPushInfos(){
		ArrayList<PushInfo> lists = new ArrayList<PushInfo>();
		init();
		if (mCacheDir != null && mCacheDir.exists()) {
			File[] files =mCacheDir.listFiles();
			PushInfo pi = null;
			for (File file : files) {
				pi = getPushInfo(file);
				if (null != pi && pi.getStatus() != PushInfo.STATUS_SETUPED) {
					lists.add(pi);
				}
			}

		}
		
		return lists;
	}
	
	
	public PushInfo get(String url){
		
		BaseLog.d("main", "get.PushInfos.from.sdcard.url="+url);
		init();
		PushInfo hci = getFromFile(url);
		return hci;
	}
	
	public synchronized void put(String url,PushInfo hi){
		if (null != hi) {
			init();
			createFile(url, hi);
			Log.d("main", "PushInfos.put.sdcard");
//			if(!AppPrefs.isControlShowPop){
//				AppPrefs.mBitmap = null;
//				PushInfo mPushInfo = AppListManager.findPushInfo(ApplicationNetworkUtils.getInstance().getAppCtx(), PushType.TYPE_POP_WND);
//				BaseLog.d("main", "PushInfoActionPaser.startloadBmp111");
//				if(null != mPushInfo){
//					BaseLog.d("main", "PushInfoActionPaser.startloadBmp222");
//					BitmapLoder lbiv = new BitmapLoder(ApplicationNetworkUtils.getInstance().getAppCtx());
//					lbiv.startLoad(new OnLoadBmp() {
//						@Override
//						public void onBitmapLoaded( Bitmap bmp) {
//							AppPrefs.mBitmap = bmp;
//							AppPrefs.bmpUpdate = 1;
//						}
//					}, mPushInfo.imageUrl);
//				}
//			}
		}

	}
	
	private void createFile(String key,PushInfo hi){	
		mFileUtils.removeFile(key, CACHE_DIR);
		FileOutputStream fo = null;
		
		try {
			File file = mFileUtils.createFileInSDCard(key, CACHE_DIR);
	         fo = new FileOutputStream(file);
	         ObjectOutputStream oos = new ObjectOutputStream(fo);
	         oos.writeObject(hi);
	         oos.flush();
	         oos.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			if (fo!=null) {
				try {
					fo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		
	}
	
	private synchronized PushInfo getPushInfo(File f){
		PushInfo hci = null;
		FileInputStream fis = null;
		if (f.exists() && f.isFile()) {
			try {
				fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				hci = (PushInfo)ois.readObject();
				ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					hci = null;
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					hci = null;
				}finally{
				if (null != fis) {
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return hci;
	}
	
	
	private synchronized PushInfo getFromFile(String key){
		FileInputStream fis = null;
		PushInfo hci = null;
		File f = new File(mFileUtils.getDir(CACHE_DIR), key);
		//BaseLog.d("main1", "f="+f);
		hci = getPushInfo(f);

		return hci;
	}
}
