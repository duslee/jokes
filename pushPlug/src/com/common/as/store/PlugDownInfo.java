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

public class PlugDownInfo  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CACHE_DIR = "com"+"/"+"plug";
	
	private int installTimes = 0;
	
	
	
	public PlugDownInfo() {
		
		FileUtils mFileUtils = new FileUtils();		
		if (!mFileUtils.isDirExist(CACHE_DIR)) {
			mFileUtils.creatSDDir(CACHE_DIR);
		}
	}
	
	
	public int getInstallTimes() {
		return installTimes;
	}


	public void setInstallTimes(int installTimes) {
		this.installTimes = installTimes;
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
	
	
	public static PlugDownInfo get(String name){
		FileUtils mFileUtils = new FileUtils();	
		File f = new File(mFileUtils.getDir(CACHE_DIR), name);
		PlugDownInfo hci = null;
		FileInputStream fis = null;
		if (f.exists() && f.isFile()) {
			try {
				fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				hci = (PlugDownInfo)ois.readObject();
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
	public static boolean hasInstall(String name){
		PlugDownInfo info = get(name);
		if(info==null){
			return false;
		}
		return true;
	}
	
}
