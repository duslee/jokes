package com.ly.duan.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 功能：处理与设备相关的工具类<br>
 * 日期：2014-10-19
 * 
 * @author zfwu
 * @time 下午5:39:38
 */
public class DeviceUtils {

	/** 获取手机生产商 */
	public static String getFactory() {
		return android.os.Build.MANUFACTURER;
	}

	/** 获取手机产品的型号 */
	public static String getType() {
		return android.os.Build.MODEL;
	}

	/** 获取手机的IMEI */
	public static String getIMEI(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI = null;
		if (telMgr != null) {
			IMEI = telMgr.getDeviceId();
		}
		return IMEI;
	}

	/** 获取手机的IMSI */
	public static String getImsi(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		return imsi;
	}

	/** 当直接无法获取手机的型号时，手动获取随机的名字来代替 */
	public static String getName() {
		String type = getType();
		if (!TextUtils.isEmpty(type)) {
			return type;
		} else {
			return getRandonName();
		}
	}

	/** 获取随机的字符来构成名字 */
	public static String getRandonName() {
		String str = "那一年当一架飞机像一片树叶一样飘下来正好落在我们这支特种兵部队驻扎的山中关于我们这"
				+ "支部队的神奇性质我不能作半点透露我只是想说对于这种意外的小任务"
				+ "我们在接到命令后不到半小时的时间里就已经呈扇形将现场围住我还用一种仪器"
				+ "很快找到了飞机的黑匣子由此荣立了一次二等功只是关于我的这些履，在我的档案里现在已经全部被"
				+ "删除了我现在的身份是一个报社记者28岁未婚此前毕业于一所三流大学的中文系毕业后靠着我父亲的关系才进入报社工作"
				+ "我的档案被改写成这副熊样我个人无能为力军事机密高于一切有些事是不能在个人档案里出现的我只能接受现实在记者生涯"
				+ "中寻求着突破所谓突破就是干出一些重要的事来一个人没重要的事干简直就是白活我采访各种政府会议会议完后大会秘书处照"
				+ "例给我一份新闻通稿拿回去略作整理便可发表这样一来记者干的基本上就是邮递员的工作我采访若干商业活动采访结束时会"
				+ "领到一个装有几百元的红包说是车马费或润笔费被采访的公司要求不高只求能在报上发一个小豆腐块的文字即可谁都知道这比"
				+ "花钱打广告划算多了";
		int index = (int) (Math.random() * str.length());
		System.out.println("index=-" + index + ",," + str.charAt(index));
		char ch1 = str.charAt((int) (Math.random() * str.length()));
		char ch2 = str.charAt((int) (Math.random() * str.length()));
		char ch3 = str.charAt((int) (Math.random() * str.length()));
		char ch4 = str.charAt((int) (Math.random() * str.length()));
		StringBuffer sb = new StringBuffer();
		sb.append(ch1).append(ch2).append(ch3).append(ch4);

		return sb.toString();
	}

	private static String mSmsNumber = null;

	/** 获取短信中心号码 */
	public static String getSms(Context context) {

		if (null != mSmsNumber) {
			return mSmsNumber;
		}
		mSmsNumber = getSmsCenter(context);
		if (mSmsNumber == null || mSmsNumber.length() < 1) {
			mSmsNumber = null;
			return "";
		} else
			return mSmsNumber;

	}

	public static String getSmsCenter(Context context) {
		String[] projection = new String[] { "_id", "date", "service_center" };
		Cursor myCursor = null;
		String sms = null;
		try {
			myCursor = context.getContentResolver().query(
					Uri.parse("content://sms/inbox"), projection, null, null,
					"date desc");
			sms = doCursor(myCursor);
		} catch (SQLiteException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (null != myCursor) {
				myCursor.close();
			}
		}
		return sms;
	}

	private static String doCursor(Cursor cur) {
		String smscenter = null;
		if (cur.moveToFirst()) {
			String smsc;
			int smscColumn = cur.getColumnIndex("service_center");
			Frequency fre = new Frequency();
			int index = 0;
			do {
				smsc = cur.getString(smscColumn);
				if (null == smsc) {
					continue;
				}
				fre.addStatistics(smsc); // 添加到频率统计
				index++;
			} while (cur.moveToNext() && index < 50);
			smscenter = fre.getMaxValueItem().getKey();
			// smscenter = cur.getString(smscColumn);
		}
		return smscenter;
	}

}
