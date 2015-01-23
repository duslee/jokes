package com.common.activelog.error;

import android.content.Context;

import com.common.activelog.http.HttpRespPaser;
import com.common.activelog.http.MPHttpClientData;
import com.common.activelog.http.MPHttpClientInterface.MPHttpClientRespListener;
import com.common.activelog.http.MPHttpClientUtils;
import com.common.as.base.log.BaseLog;

public class ErrorUtils {

	private static ErrorUtils mCommitPayUtils;
	private static Context context;

	public static synchronized ErrorUtils getInstance() {

		if (mCommitPayUtils == null) {
			mCommitPayUtils = new ErrorUtils(context);
		}
		return mCommitPayUtils;
	}

	public ErrorUtils(Context context) {
		ErrorUtils.context = context;
	}

	public static void sendErrorLog(Context context, String appid,
			String channelid) {
		ErrorUtils activeLog = ErrorUtils.getInstance();
		activeLog.req(context, appid, channelid);
	}

	public void req(final Context context, String appid, String channelid) {
		MPHttpClientUtils.sendErrorLogReq(10, new MPHttpClientRespListener() {

			@Override
			public void onMPHttpClientResponse(int id, int errId, int statusId,
					MPHttpClientData obj) {
				HttpRespPaser paser = new HttpRespPaser(context, obj, errId,
						statusId);
				BaseLog.d("main", "sendErrorLog=" + paser.isRespondSuccess());
				// if(paser.isRespondSuccess()){
				// CommitPayLogResp response = (CommitPayLogResp)obj;
				// }
			}
		}, context, appid, channelid);
	}

	public static void sendErrorLog11(Context context, String payChannelid,
			String nodeId, String contentId, String downTime, String downLength) {
		ErrorUtils activeLog = ErrorUtils.getInstance();
		activeLog.req11(context, payChannelid, nodeId, contentId, downTime,
				downLength);
	}

	public void req11(final Context context, String payChannelid,
			String nodeId, String contentId, String downTime, String downLength) {
		MPHttpClientUtils.sendErrorLogReq11(10, new MPHttpClientRespListener() {

			@Override
			public void onMPHttpClientResponse(int id, int errId, int statusId,
					MPHttpClientData obj) {
				HttpRespPaser paser = new HttpRespPaser(context, obj, errId,
						statusId);
				BaseLog.d("main", "sendErrorLog=" + paser.isRespondSuccess());
				// if(paser.isRespondSuccess()){
				// CommitPayLogResp response = (CommitPayLogResp)obj;
				// }
			}
		}, context, payChannelid, nodeId, contentId, downTime, downLength);
	}
}
