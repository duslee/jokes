package com.common.as.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.Header;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.common.as.base.log.BaseLog;

public class DateUtil {
	private static String TAG = "DateUtil";
	private  static long mServerT = 0L;
	private static long  mServerOfSystemT = 0L;
	private static boolean isUserLocalTimer = false;
//	private static final long mTimeZone = 8*60*60*1000;
	
	public static String  getDate(){
		 Date dt=new Date();
	     SimpleDateFormat matter1=new SimpleDateFormat("yyyy-MM-dd");
	     return matter1.format(dt);
	}
	public static boolean compare_date(String DATE1, String DATE2) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() < dt2.getTime()) {
				System.out.println("dt1 在dt2前");
				return true;
			} 
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}
	public static boolean isAfterCurrent(Date date){
		Date currentDate = new Date(System.currentTimeMillis());
		if (currentDate.before(date)) {
			return false;
		} else {
            if (currentDate.getYear() > date.getYear()) {
            	return true;
			}else if (currentDate.getMonth() > date.getMonth()) {
				return true;
			} else if(currentDate.getDate() > date.getDate()){
				return true;
			}else{
				return false;
			}
		}
	}
	public static boolean isAfterCurrent(int year, int month, int day){
		Date date = new Date(year, month, day);
		return isAfterCurrent(date);

	}
	
	
	//判断系统时间是否在记录的t之后的days期限�?
	public static boolean isAfterCurrent(long t, long days){
		
		long ct = getCurrentMs();
		long times = days * 24 *60*60*1000;
		BaseLog.d("main3", ".isAfterCurrentt="+t+",,,getCurrentMs="+ct+",,times="+times);
		if (ct < t+times) {
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean isAfterCurrentHour(long t, long hour){
		long ct = getCurrentMs();
		long times =hour *60*60*1000;
		if (ct < t+times) {
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean isAfterCurrent(long ct, long t, long days){
		long times = days * 24 *60*60*1000;
		if (ct < t+times) {
			return false;
		}else{
			return true;
		}
	}
	
	public static long getTms(long t, long days){
		return days * 24 *60*60*1000+t;
	}
	
	
	public static CharSequence getDate(long t){
		return DateFormat.format("yyyy/MM/dd", t);
	}
	
	
	public static int getCurrentYear(){
		Date currentDate = new Date(System.currentTimeMillis());
		return currentDate.getYear();
	}
	public static int getCurrentMonth(){
		Date currentDate = new Date(System.currentTimeMillis());
		return currentDate.getMonth();	
	}
	public static int getCurrentDay(){
		Date currentDate = new Date(System.currentTimeMillis());
		return currentDate.getDate();	
	}
	public static int getCurrentHour(){
		Date currentDate = new Date(getCurrentMs());
//		int hour = currentDate.getHours()-(currentDate.getTimezoneOffset())/60;	
		return currentDate.getHours();	
	}
	public static int getCurrentMinute(){
		Date currentDate = new Date(System.currentTimeMillis());
		return currentDate.getMinutes();
	}
	public static String getBirth(Context context, int year, int month, int day) {
		return String.format("%4d-%02d-%02d", year, month, day);
	}
	
	public static String getDateSub(Context context, long dateMs){
//		long sub = getCurrentMs() - dateMs;
//		if (sub <= 60000 && sub >= 0) {
//			return context.getString(R.string.chat_time_ms);
//		}else if (sub < 0) {
//			return null;
//		}
//		
//		long minute = sub/60000;
//		long hour = minute/60;
//		
//		if (hour > 0) {
//			return String.format(context.getString(R.string.chat_time_hour), hour);
//		} else {
//			return String.format(context.getString(R.string.chat_time_minute), minute);
//		}
		return "";
	}
	
	public static long getCurrentMs(){
		long t = System.currentTimeMillis();
		if (!isUsedServerT()) {
			return t;
		}else{
			return t - mServerOfSystemT+mServerT;
		}
	}
	
	
	
	public static void setUserLocalTimer(boolean isUserLocalTimer) {
		DateUtil.isUserLocalTimer = isUserLocalTimer;
	}
	public static boolean isUsedServerT(){
		if (mServerT <= 0L || isUserLocalTimer) {
			return false;
		}else{
			return true;//使用服务器时�?
		}
	}
	
	/*
	    * 将GMT时间转换成当前时区时�?
	    */
	    public static Date transform(String from){
	        SimpleDateFormat simple = new SimpleDateFormat();
	        //本地时区
	        Calendar nowCal = Calendar.getInstance();
	        TimeZone localZone = nowCal.getTimeZone();
	        //设定SDF的时区为本地
	        simple.setTimeZone(localZone);
//Date: Tue, 20 May 2014 02:49:42 GMT

//	        SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	        //设置 DateFormat的时间区域为GMT
//	        simple1.setTimeZone(TimeZone.getTimeZone("GMT"));


	        //把字符串转化为Date对象，然后格式化输出这个Date
//	        Date fromDate = new Date(from);
//	            //时间string解析成GMT时间
//	            fromDate = simple1.parse(from);
	            //GMT时间转成当前时区的时�?
	        Date to = null;
			try {
				to = simple.parse(from);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         
	        return to;
	    }
	
	public static void setServerT(Header dateHeader){
		if (null != dateHeader) {
			try {
				String strT = dateHeader.getValue();
				Date currentDate = new Date(strT);//gmt
				BaseLog.d("main1", "currentData="+currentDate);
		     //   Calendar nowCal = Calendar.getInstance();
		     //   TimeZone localZone = nowCal.getTimeZone();
             //  BaseLog.d(TAG, "setServerT="+currentDate);
				BaseLog.d("main1", "currentDate.getDate()="+currentDate.getDate());
				BaseLog.d("main1", "currentDate.getMonth()="+currentDate.getMonth());
				BaseLog.d("main1", "currentDate.getYear()="+currentDate.getYear()+1900);
//				currentDate.();getDate
				long t = currentDate.getTime();//+localZone.getRawOffset();//local
				if (t > mServerT) {
					mServerT = t;
					mServerOfSystemT = System.currentTimeMillis();
				}
				
				//BaseLog.d(TAG, "setServerT="+mServerT+"");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mServerT = System.currentTimeMillis();
			}

		}
	}
	
	
	public static boolean isInSameDayWithServerT(long t){
		Date dateinput = new Date(t);
		Date current = new Date(getCurrentMs());
		
		if (current.after(dateinput) && (dateinput.getDate() != current.getDate() || dateinput.getMonth() != current.getMonth())) {
			return false;
		}
		
		return true;
	}
	

	
	public static boolean isInSameDayWithServerT(String t)
	{
		if(t == null)
		{
			return false;
		}
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置 DateFormat的时间区域为GMT
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date dateinput = null;
		try {
			dateinput = format.parse(t);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dateinput == null)
		{
			return false;
		}
//		Date dateinput = new Date(t);
		Date current = new Date(getCurrentMs());


		if (current.after(dateinput) && (dateinput.getDate() != current.getDate() || dateinput.getMonth() != current.getMonth())) {
			return false;
		}
		
		return true;
	}
}
