package com.common.as.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.common.as.pushtype.PushInfo;

public class AppListInfos {
	FileUtils mFileUtils;
	private static final String CACHE_DIR = "com.import.plis";
	
	
	private static AppListInfos instance;
	
	
	public static synchronized AppListInfos  getInstance(){
		if (null == instance) {
			instance = new AppListInfos();
		}
		
		return instance;
	}
	
	private void init(){
		if (null == mFileUtils) {
			mFileUtils = new FileUtils();
		}
		
		
		if (!mFileUtils.isDirExist(CACHE_DIR)) {
			mFileUtils.creatSDDir(CACHE_DIR);
		}		
	}
	
	
	
	public ArrayList<PushInfo> get(String url){
		init();
		ArrayList<PushInfo>  hci = getFromFile(url);
		return hci;
	}
	
	public synchronized void put(String url,ArrayList<PushInfo> hi){
		if (null != hi) {
			init();
			createFile(url, hi);
		}

	}
	
	private void createFile(String key,ArrayList<PushInfo> hi){	
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
	
	
	private synchronized ArrayList<PushInfo> getFromFile(String key){
		FileInputStream fis = null;
		ArrayList<PushInfo> hci = null;
		
		File f = new File(mFileUtils.getDir(CACHE_DIR), key);
		if (f.exists() && f.isFile()) {
			try {
				fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				hci = (ArrayList<PushInfo>)ois.readObject();
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
}
