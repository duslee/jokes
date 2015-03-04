package com.common.as.network.httpclient;

import com.common.as.base.log.Config;
public class MPHttpClientImage {
	public static String PIC_URL_RELEASE = "http://pic.52le.com:18081/";
	public static String PIC_URL_TEST = "http://chattest.sky-mobi.com:4300/";
	public static String PIC_URL = Config.Debug ? PIC_URL_TEST : PIC_URL_RELEASE;

	
	public static int ROOM_IMAGE_DEFAUT_WIDTH = 208;
	public static int ROOM_IMAGE_DEFAULT_HEIGHT = 156;
	
	public static int ROOM_IMAGE_SQUARE_WIDTH = 136;
	public static int ROOM_IMAGE_SQUARE_HEIGHT = 136;
	public IMAGETYPE mImageType = IMAGETYPE.SQUARE;
	
	public static enum IMAGETYPE{
		SQUARE,
		COMMON
	};
	
	public static int WIDTH_HEIGHT_120 = 120;
	public static int WIDTH_HEIGHT_80 = 80;
	public static int WIDTH_HEIGHT_70 = 70;
	public static int WIDTH_HEIGHT_60 = 60;
	public static int WIDTH_HEIGHT_50 = 50;
	public static int WIDTH_HEIGHT_40 = 40;
	public static int WIDTH_HEIGHT_25 = 25;

	private static String getImageUrl(int id, int w, int h){
		String url = PIC_URL +
				String.format("image/%d_%dx%d.jpg", id, w, h);
		
		return url;
	}
	
	/*
	 * 		roomId_square_136x136
	 * */
	private static String getSquareImageUrl(int id, int w, int h){
		String url = PIC_URL +
				String.format("image/%d_square.jpg", id);

		//String.format("image/%d_square_%dx%d.jpg", id, w, h);
		return url;
	}
	
	public static String getRoomImageUrl(int roomId, int size){
		return getImageUrl(roomId, size, size);
	}
	
	public static String getRoomImageUrl(int roomId, IMAGETYPE type){
		if(type == IMAGETYPE.SQUARE){
			return getRoomImageUrl(roomId);
		}else{
			return getImageUrl(roomId,
					ROOM_IMAGE_DEFAUT_WIDTH, ROOM_IMAGE_DEFAULT_HEIGHT);
//			return getSquareImageUrl(roomId, 
//					ROOM_IMAGE_DEFAUT_WIDTH, ROOM_IMAGE_DEFAULT_HEIGHT);
		}

	}
	
	public static String getRoomImageUrl(int roomId){
		return getSquareImageUrl(roomId, 
				ROOM_IMAGE_SQUARE_WIDTH, ROOM_IMAGE_SQUARE_HEIGHT);
	}
	
	public static String getHeadPicUrl(int skyId, int w, int h){
		String url = PIC_URL +
			String.format("header/%d_%dx%d.jpg", skyId, w, h);
		
		return url;
	}
	
	public static String getHeadPicUrl(int skyId, int size){
		return getHeadPicUrl(skyId, size, size);
	}
	
	public static String getHeaderListPicUrl(String name){
		String url = PIC_URL + String.format("image/%s.jpg", name);
		return url;
	}
	
	public static String getGiftPicUrl(int giftId){
		return getImageUrl(giftId, WIDTH_HEIGHT_50, WIDTH_HEIGHT_50);
	}
	
	public static String getGiftPicUrl(String name){
		String url = PIC_URL + String.format("image/%s.jpg", name);
		return url;
	}
	
	public static String getCarPicUrl(String name){
		String url = PIC_URL + String.format("image/%s.jpg", name);
		return url;
	}
}
