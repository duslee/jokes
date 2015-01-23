package com.common.as.utils;

import com.common.as.image.ImageRequest;

import android.content.Context;
import android.graphics.Bitmap;

public class BitmapLoder  {

	Context mCtx;
	
	public BitmapLoder(Context ctx) {
		super();
		mCtx = ctx;
		// TODO Auto-generated constructor stub
	}


	public static interface OnLoadBmp{
		void onBitmapLoaded(Bitmap bmp);
	}
	
	
	public void startLoad(final OnLoadBmp onLoad, String url){
		ImageRequest imageRequest = new ImageRequest(mCtx, url, new ImageRequest.ImageRequestCallback() {
			
			@Override
			public void onImageRequestStarted(ImageRequest request) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onImageRequestStart(ImageRequest request, Bitmap image) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onImageRequestFailed(ImageRequest request, Throwable throwable) {
				// TODO Auto-generated method stub
				onLoad.onBitmapLoaded(null);
			}
			
			@Override
			public void onImageRequestEnded(ImageRequest request, Bitmap image) {
				// TODO Auto-generated method stub
				onLoad.onBitmapLoaded(image);
			}
			
			@Override
			public void onImageRequestCancelled(ImageRequest request) {
				// TODO Auto-generated method stub
				onLoad.onBitmapLoaded(null);
			}
		}) ;
		
		imageRequest.load(mCtx, true);
	}
}
