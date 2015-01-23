package com.common.activelog.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.os.StatFs;

public class FileUtils {
	private String SDCardRoot;

	public FileUtils() {
		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
	}

	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + File.separator + dir + File.separator
				+ fileName);
		file.createNewFile();
		return file;
	}

	public File getFileInSDCard(String fileName, String dir) {
		File file = new File(SDCardRoot + File.separator + dir + File.separator
				+ fileName);
		return file;
	}

	// ��SD���ϴ���Ŀ¼
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + File.separator + dir
				+ File.separator);
		System.out.println(dirFile.mkdir());
		return dirFile;
	}

	public boolean isDirExist(String dir) {
		File dirFile = new File(SDCardRoot + File.separator + dir
				+ File.separator);
		if (dirFile.exists() && dirFile.isDirectory()) {
			return true;
		}
		return false;
	}

	public File getDir(String dir) {
		File dirFile = new File(SDCardRoot + File.separator + dir
				+ File.separator);
		return dirFile;
	}

	// �ж�SD���ϵ��ļ��Ƿ����?
	public boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + File.separator + path
				+ File.separator + fileName);
		return file.exists();
	}

	// �Ƴ��ļ�
	public boolean removeFile(String fileName, String path) {
		File file = new File(SDCardRoot + File.separator + path
				+ File.separator + fileName);
		return file.delete();
	}

	public String getFileFullName(String fileName, String path) {
		return SDCardRoot + File.separator + path + File.separator + fileName;
	}

	// ��һ��InputStream��������д�뵽SD����
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			while ((temp = input.read(buffer)) != -1) {
				output.write(buffer, 0, temp);
			}
			// ��ջ���?
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != output) {
					output.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/*
	 * //��ȡĿ¼�е�Mp3�ļ������ֺʹ�С
	 * 
	 * public List<Mp3Info> getMp3Infos(String path){ List<Mp3Info> mp3Infos =
	 * new ArrayList<Mp3Info>(); File file = new File(SDCardRoot +
	 * File.separator + path); File [] files = file.listFiles(); for (int i = 0;
	 * i < files.length; i++) { if (files[i].getName().endsWith("mp3")) {
	 * Mp3Info mp3Info = new Mp3Info(); mp3Info.setMp3Name(files[i].getName());
	 * mp3Info.setMp3Size(files[i].length() + ""); mp3Infos.add(mp3Info); }
	 * 
	 * } return mp3Infos;
	 * 
	 * }
	 */

	// public static File copyToTmpImageFile(Context context,Bitmap bitmap, int
	// dstWidth, int dstHeight){
	//
	// File file = new
	// File(context.getFilesDir()+File.separator+AccountInfo.PIC_NAME);
	// FileOutputStream fos = null;
	// Bitmap b = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
	// if (file.exists() && file.isFile()) {
	// file.delete();
	// }
	//
	// try {
	// fos = new FileOutputStream(file);
	// b.compress(Bitmap.CompressFormat.JPEG, 80, fos);
	// fos.flush();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }finally{
	// if (null != fos) {
	// try {
	// fos.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// return file;
	// }

	public static File getExternalStorageDirectory() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdCardDir = Environment.getExternalStorageDirectory();
			return sdCardDir;
		}
		return null;
	}

	public static boolean checkPhoneHavEnghStorage(long size) {
		return FileUtils.checkStorageRom(Environment.getDataDirectory(), size);
	}

	public static boolean checkSdcardHavEnghStorage(long size) {
		return FileUtils.checkStorageRom(getExternalStorageDirectory(), size);
	}

	static boolean checkStorageRom(File file, long size) {
		if (file == null) {
			return false;
		}

		StatFs mStat = new StatFs(file.getAbsolutePath());
		long blockSize = mStat.getBlockSize();
		long avaleCout = mStat.getAvailableBlocks();
		long val = avaleCout * blockSize;
		if (size + 10 * 1024 * 1024 <= val) {
			return true;
		} else {
			return false;
		}
	}

}
