package com.common.activelog.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StartTimesInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CACHE_DIR = ".start.times";

	private int mStartTimes = -1;

	public StartTimesInfo(String s) {

		FileUtils mFileUtils = new FileUtils();
		if (!mFileUtils.isDirExist(s + CACHE_DIR)) {
			mFileUtils.creatSDDir(s + CACHE_DIR);
		}
	}

	public int getmStartTimes() {
		return mStartTimes;
	}

	public void setmStartTimes(int mStartTimes) {
		this.mStartTimes = mStartTimes;
	}

	public void addCount() {
		mStartTimes++;
	}

	public synchronized void save(String name) {
		FileUtils mFileUtils = new FileUtils();
		mFileUtils.removeFile(name, name + CACHE_DIR);
		FileOutputStream fo = null;

		try {
			File file = mFileUtils.createFileInSDCard(name, name + CACHE_DIR);
			fo = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fo);
			oos.writeObject(this);
			oos.flush();
			oos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (fo != null) {
				try {
					fo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static StartTimesInfo get(String name) {
		FileUtils mFileUtils = new FileUtils();
		File f = new File(mFileUtils.getDir(name + CACHE_DIR), name);
		StartTimesInfo hci = null;
		FileInputStream fis = null;
		if (f.exists() && f.isFile()) {
			try {
				fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				hci = (StartTimesInfo) ois.readObject();
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				hci = null;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				hci = null;
			} finally {
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

	public boolean isFirstStart() {
		int times = getmStartTimes();
		// Log.d("main", "times="+times);
		if (times == 0) {
			return true;
		} else {
			return false;
		}
	}
}
