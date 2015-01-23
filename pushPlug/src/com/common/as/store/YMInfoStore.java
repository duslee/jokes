package com.common.as.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.common.as.base.log.BaseLog;


public class YMInfoStore  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CACHE_DIR =  ".ym.str";
	
	private String mYmStr="";
	
	private ArrayList<String> unUsered_ym = new ArrayList<String>();
	public YMInfoStore(String s) {
		
		FileUtils mFileUtils = new FileUtils();		
		if (!mFileUtils.isDirExist(s+CACHE_DIR)) {
			mFileUtils.creatSDDir(s+CACHE_DIR);
		}
	}
	






	public ArrayList<String> getUnUsered_ym() {
		return unUsered_ym;
	}







	public void setUnUsered_ym(ArrayList<String> unUsered_ym) {
		this.unUsered_ym = unUsered_ym;
	}







	public String getmYmStr() {
		return mYmStr;
	}




	public void setmYmStr(String mYmStr) {
		this.mYmStr = mYmStr;
	}




	public void addYM(String ym) {
		this.mYmStr = ym;
	}
	
	
	public synchronized void save(String name){
		FileUtils mFileUtils = new FileUtils();	
		mFileUtils.removeFile(name, name+CACHE_DIR);
		FileOutputStream fo = null;
		
		try {
			File file = mFileUtils.createFileInSDCard(name, name+CACHE_DIR);
			BaseLog.d("main", "StartTimes.file.path="+file.getAbsolutePath());
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
	
	
	public static YMInfoStore get(String name){
		FileUtils mFileUtils = new FileUtils();	
		File f = new File(mFileUtils.getDir(name+CACHE_DIR), name);
		YMInfoStore hci = null;
		FileInputStream fis = null;
		if (f.exists() && f.isFile()) {
			try {
				fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				hci = (YMInfoStore)ois.readObject();
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
	
	
	public boolean isExistYM(){
		String ym = getmYmStr();
		//Log.d("main", "times="+times);
		if (!TextUtils.isEmpty(ym)) {
			return true; 
		}else{
			return false;
		}
	}
}
