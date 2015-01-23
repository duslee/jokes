package com.common.as.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Log;

import com.common.as.pushtype.PushInfo;

public class MaxDownInfo  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CACHE_DIR = "com.s360.maxt";
	
	private String mCurTime = "";
	private int mCurDownNum = 0;
	
	
	
	public MaxDownInfo() {
		
		FileUtils mFileUtils = new FileUtils();		
		if (!mFileUtils.isDirExist(CACHE_DIR)) {
			mFileUtils.creatSDDir(CACHE_DIR);
		}
	}
	public String getmCurTime() {
		return mCurTime;
	}
	public void setmCurTime(int year, int month, int day) {
		this.mCurTime = getTimes(year, month, day);
	}
	public int getmCurDownNum() {
		return mCurDownNum;
	}
	public void addCount(int year, int month, int day) {
		if (mCurTime.equals(getTimes(year, month, day))) {
			mCurDownNum++;
		}else{
			mCurDownNum = 1;
			setmCurTime(year, month, day);
		}
		Log.d("main", "mCurDownNum="+mCurDownNum);
	}
	
	private String getTimes(int year, int month, int day){
		return year+"|"+month+"|"+day;
	}
	
	
	public synchronized void save(String name){
		FileUtils mFileUtils = new FileUtils();	
		mFileUtils.removeFile(name, CACHE_DIR);
		FileOutputStream fo = null;
		
		try {
			File file = mFileUtils.createFileInSDCard(name, CACHE_DIR);
	         fo = new FileOutputStream(file);
	         ObjectOutputStream oos = new ObjectOutputStream(fo);
	         oos.writeObject(this);
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
	
	
	public static MaxDownInfo get(String name){
		FileUtils mFileUtils = new FileUtils();	
		File f = new File(mFileUtils.getDir(CACHE_DIR), name);
		MaxDownInfo hci = null;
		FileInputStream fis = null;
		if (f.exists() && f.isFile()) {
			try {
				fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				hci = (MaxDownInfo)ois.readObject();
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
	
	
	public boolean isSuperMax(int max, int year, int month, int day){
		final String times = getTimes(year, month, day);
		if (times.equals(mCurTime)) {
			return mCurDownNum >= max? true:false; 
		}else{
			mCurTime = times;
			return false;
		}
	}
}
