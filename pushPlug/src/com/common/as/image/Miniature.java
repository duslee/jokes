package com.common.as.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Miniature {
	private int mScaleWidth;
	private int mScaleHeight;
	private boolean mIsCutting;	// �Ƿ���к����ţ�������������ʱ����
	private boolean mIsUniform;	// �Ƿ�ȱ�����
	
	public Miniature(int width, int height){
		mScaleWidth = width;
		mScaleHeight = height;
	}
	
	public void setCutting(boolean bCutting){
		mIsCutting = bCutting;
	}
	
	public void setUniformScale(boolean b){
		mIsUniform = b;
	}
	
	private Bitmap createCuttingBitmap(Bitmap source){
		Bitmap b = source;
		int x, y, w, h;
		
		w = mScaleWidth * b.getHeight() / mScaleHeight;
		
		if (w == b.getWidth()){
			return b;
		}
		
		if (w < b.getWidth()){
			x = (b.getWidth() - w) / 2;
			y = 0;
			h = b.getHeight();
		}else{
			h = mScaleHeight * b.getWidth() / mScaleWidth;
			x = 0;
			y = (b.getHeight() - h) / 2;
			w = b.getWidth();
		}
		
		b = Bitmap.createBitmap(source, x, y, w, h);
		
		return b;
	}
	
	private Bitmap createScaledBitmap(Bitmap source){
		Bitmap b = source;
		
		if (mIsCutting){
			b = createCuttingBitmap(source);
		}
		
		int w = mScaleWidth;
		int h = mScaleHeight;
		
		// Uniform Scaled.
		if (mIsUniform){
			w = b.getWidth() * mScaleHeight / h;
			if (w > mScaleWidth){
				h = b.getHeight() * mScaleWidth / w;
				w = mScaleWidth;
			}
		}
		
		b = Bitmap.createScaledBitmap(b, w, h, false);
		
		return b;
	}
	
	public Bitmap GetBitmap(String pathName){
		Bitmap b = BitmapFactory.decodeFile(pathName);
		
		if (b != null){
			b = createScaledBitmap(b);
		}
		
		return b;
	}
	
	public Bitmap GetBitmap(InputStream is){
		Bitmap b = BitmapFactory.decodeStream(is);
		
		if (b != null){
			b = createScaledBitmap(b);
		}
		
		return b;
	}
	
	public Bitmap GetBitmap(Resources res, int id){
		Bitmap b = BitmapFactory.decodeResource(res, id);
		
		if (b != null){
			b = createScaledBitmap(b);
		}
		return b;
	}
	
	public Bitmap GetBitmap(ZipFile zf, String fileName){
		Bitmap b = null;
		ZipEntry ze = zf.getEntry(fileName);
		
		if (ze != null){
			try {
				b = GetBitmap(zf.getInputStream(ze));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return b;
	}
	
	public Bitmap GetBitmap(String zipFileName, String fileName){
		Bitmap b = null;
		
		try {
			ZipFile zf = new ZipFile(zipFileName);
			
			b = GetBitmap(zf, fileName);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return b;
	}
	
	public Bitmap GetBitmap(Bitmap b){
		if (b != null){
			b = createScaledBitmap(b);
		}
		return b;
	}
	
	public int getWidth(){
		return mScaleWidth;
	}
	
	public int getHeight(){
		return mScaleHeight;
	}

}
