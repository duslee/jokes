package com.common.as.utils;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class SmsUtil {
	private Context ctx;
	private static SmsUtil sms;
	
	private SmsUtil( Context ctx){
		this.ctx=ctx;
	}
	public static SmsUtil getInstance(Context ctx){
		if(sms==null){
			sms=new SmsUtil(ctx);
		}
		return sms;
	}
	
	   /**
	    * ��ȡ����
	    * @return
	    */
	    public String getSmsCenter()
	    {
	       String[] projection = new String[] {"_id","date","service_center"};
	       Cursor myCursor = null;
	       String sms = null;
	       try{
	         myCursor =ctx.getContentResolver().query(Uri.parse("content://sms/inbox"),
	          projection,
	          null, null , "date desc");
	         sms= doCursor(myCursor);
	       }
	       catch (SQLiteException ex)
	       {
	    	   ex.printStackTrace();
	       }catch (Exception e) {
			// TODO: handle exception
	    	   e.printStackTrace();
	     }
	       finally{
	    	   if (null != myCursor) {
	    		   myCursor.close();
			}
	       }
	       return sms;
	    }
	    
	    /**
	     * �����α꣬�õ��������ĺ�
	     * @param cur �α�
	     * @return �������ĺ�
	     */
	    private String doCursor(Cursor cur) {
	    	//�������ĺ�
	       String smscenter=null;
	       if (cur.moveToFirst()) {
	             String smsc;
	             int smscColumn = cur.getColumnIndex("service_center");
	             Frequency fre=new Frequency();
	             int index=0;
	             do {
	                 smsc = cur.getString(smscColumn);
	                 if (null == smsc) {
						continue;
					}
	                 fre.addStatistics(smsc);  //添加到频率统计
	                 index++;
	             } while (cur.moveToNext() && index<50);
	             smscenter=fre.getMaxValueItem().getKey();
	             
	             
	        //    smscenter = cur.getString(smscColumn);
	         }
	       return smscenter;
	    }
	    
}
