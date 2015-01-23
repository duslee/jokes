package com.ly.duan.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.ly.duan.bean.BannerBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.bean.MultiReqStatus;
import com.ly.duan.help.DBHelp;
import com.ly.duan.help.GlobalHelp;
import com.ly.duan.help.HttpHelp.MyRequestCallback;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.ToastUtils;

public class InitDataService extends Service {

	public static final String ACTION = "com.sjm.gxdz.STATUS_BROADCAST";
	
	/* send BroadcastReceiver type */
	public static final int SEND_BC_CONTENT1 = 100;
	public static final int SEND_BC_CONTENT2 = 101;
	public static final int SEND_BC_BANNER = 102;

	/* status of clm1 request */
	public static final int CONTENT1_REQUESTING = 41;
	public static final int CONTENT1_REQUEST_FINISH = 42;
	public static final int CONTENT1_REQUEST_FAILED = 43;
	public static final int CONTENT1_RESPONSE_ERROR = 44;
	private static final int RE_REQUEST_CONTENT1 = 45;

	/* status of clm2 request */
	public static final int CONTENT2_REQUESTING = 51;
	public static final int CONTENT2_REQUEST_FINISH = 52;
	public static final int CONTENT2_REQUEST_FAILED = 53;
	public static final int CONTENT2_RESPONSE_ERROR = 54;
	private static final int RE_REQUEST_CONTENT2 = 55;

	/* status of banner request */
	public static final int BANNER_REQUESTING = 21;
	public static final int BANNER_REQUEST_FINISH = 22;
	public static final int BANNER_REQUEST_FAILED = 23;
	public static final int BANNER_RESPONSE_ERROR = 24;
	private static final int RE_REQUEST_BANNER = 25;

	private long appid;

	/* relative ver in DB(if no data in DB, ver is 0) */
	private int content1Ver;
	private int content2Ver;
	private int bannerVer;

	/* label status */
	private int currentContent1Status;
	private int currentContent2Status;
	private int currentBannerStatus;

	/* label reRequest status */
	private boolean reRequestContent1 = false;
	private boolean reRequestContent2 = false;
	private boolean reRequestBanner = false;

	private DbUtils db;
	private MyHandler mHandler;
	private HandlerThread handlerThread;

	private MultiReqStatus status;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		db = DBHelp.getInstance(InitDataService.this);
		status = new MultiReqStatus();
		handlerThread = new HandlerThread("back service");
		handlerThread.start();
		mHandler = new MyHandler(handlerThread.getLooper());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			appid = intent.getLongExtra("appid", 0L);
		}
		LogUtils.e("(intent != null)=" + (intent != null) + ", appid=" + appid);

		mHandler.post(content1Runnable);
		// TODO: modify
//		mHandler.post(content2Runnable);
//		mHandler.post(bannerRunnable);
		return START_NOT_STICKY;
	}

	private void reRequest(int what, int columnId, MultiReqStatus reqStatus) {
		if (what == RE_REQUEST_CONTENT1 && reRequestContent1
				&& reqStatus.getContent1Status() == CONTENT1_REQUEST_FAILED) {
			sendContentRequest(columnId, content1Ver);
		}
		if (what == RE_REQUEST_CONTENT2 && reRequestContent2
				&& reqStatus.getContent2Status() == CONTENT2_REQUEST_FAILED) {
			sendContentRequest(columnId, content2Ver);
		}
		if (what == RE_REQUEST_BANNER && reRequestBanner
				&& reqStatus.getBannerStatus() == BANNER_REQUEST_FAILED) {
			sendBannerRequest();
		}
	}

	private class MyHandler extends Handler {

		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SEND_BC_CONTENT1:
			case SEND_BC_CONTENT2:
			case SEND_BC_BANNER:
				handleBroadcastAndNextStep(msg.what, (MultiReqStatus) msg.obj);
				break;

			case RE_REQUEST_CONTENT1:
			case RE_REQUEST_CONTENT2:
			case RE_REQUEST_BANNER:
				reRequest(msg.what, msg.arg1, (MultiReqStatus) msg.obj);
				break;

			default:
				break;
			}
		}

	}

	private Runnable content1Runnable = new Runnable() {

		@Override
		public void run() {
//			getContentFromHttp(83);
			getContentFromHttp(4);
		}
	};

	private Runnable content2Runnable = new Runnable() {

		@Override
		public void run() {
//			getContentFromHttp(84);
			getContentFromHttp(4);
		}
	};

	private Runnable bannerRunnable = new Runnable() {

		@Override
		public void run() {
			getBannerFromHttp();
		}
	};

	private void getBannerFromHttp() {
		/* 1. get ver from DB */
		try {
			BannerBean bean = db.findFirst(Selector.from(BannerBean.class)
					.where("appid", "=", appid));
			if (bean != null) {
				bannerVer = bean.getVer();
			} else {
				bannerVer = 0;
			}
		} catch (DbException e) {
			e.printStackTrace();
			return;
		}

		/* 2. send request */
		sendBannerRequest();
	}

	private void sendBannerRequest() {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.KEY_APPID, appid + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_VER, bannerVer + ""));
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(nvps);
		new HttpUtils().send(HttpRequest.HttpMethod.POST,
				Constants.ACTION_GET_BANNER_LIST, params, bannerCallback);
	}

	private MyRequestCallback bannerCallback = new MyRequestCallback() {

		public void onStart() {
			currentBannerStatus = BANNER_REQUESTING;
			Message msg = new Message();
			msg.what = SEND_BC_BANNER;
			status.setContent1Status(currentContent1Status);
			status.setContent2Status(currentContent2Status);
			status.setBannerStatus(BANNER_REQUESTING);
			msg.obj = status;
			mHandler.sendMessage(msg);
		};

		public void onSuccess(
				com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			parseBannerResult(responseInfo.result);
		};

		public void onFailure(com.lidroid.xutils.exception.HttpException error,
				String msg) {
			currentBannerStatus = BANNER_REQUEST_FAILED;
			Message message = new Message();
			status.setContent1Status(currentContent1Status);
			status.setContent2Status(currentContent2Status);
			status.setBannerStatus(BANNER_REQUEST_FAILED);
			if (!reRequestBanner) {
				reRequestBanner = true;
				message.what = RE_REQUEST_BANNER;
			} else {
				message.what = SEND_BC_BANNER;
			}
			message.arg1 = 0;
			message.obj = status;
			mHandler.sendMessage(message);
		};

	};

	private void parseBannerResult(String result) {
		LogUtils.e("result: " + result);
		/* 从服务器响应中获取相应数据 */
		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (jsonObject == null) {
			LogUtils.e("(jsonObject == null)=" + (jsonObject == null));
			return;
		}

		int code = jsonObject.getIntValue("code");
		if (code == 0) {/* 表示可以正常读取数据 */
			boolean hasNew = jsonObject.getBooleanValue("hasNew");
			if (hasNew) {
				/* 开始解析数据 */
				int _ver = jsonObject.getIntValue("ver");
				List<BannerBean> bannerList = new ArrayList<BannerBean>();
				JSONArray jsonArray = jsonObject.getJSONArray("banners");
				for (int i = 0; i < jsonArray.size(); i++) {
					BannerBean bean = new BannerBean();
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					int type = jsonObject2.getIntValue("contentType");
					LogUtils.e("type=" + type);
					String pkgName = jsonObject2.getString("contentPackage");
					/* 添加内容到Banner列表 */
					// int id = jsonObject2.getIntValue("id");
					// bean.setId(id);
					long id = jsonObject2.getLongValue("id");
					bean.setBannerId(id);
					bean.setAppid(appid);
					bean.setVer(_ver);
					bean.setContentType(type);
					bean.setContentId(jsonObject2.getLongValue("contentId"));
					bean.setContentPackage(pkgName);
					bean.setBannerTitle(jsonObject2.getString("bannerTitle"));
					bean.setBannerDesc(jsonObject2.getString("bannerDesc"));
					bean.setBannerImgUrl(jsonObject2.getString("bannerImgUrl"));
					bean.setContentUrl(jsonObject2.getString("contentUrl"));
					bannerList.add(bean);
				}
				/* 保存到本地数据库，先删除掉本地数据 */
				try {
					db.deleteAll(BannerBean.class);
					db.saveAll(bannerList);
				} catch (DbException e) {
					e.printStackTrace();
					return;
				}
			}

			currentBannerStatus = BANNER_REQUEST_FINISH;
			Message msg = new Message();
			msg.what = SEND_BC_BANNER;
			status.setContent1Status(currentContent1Status);
			status.setContent2Status(currentContent2Status);
			status.setBannerStatus(BANNER_REQUEST_FINISH);
			msg.obj = status;
			mHandler.sendMessage(msg);

			// mHandler.sendEmptyMessage(SEND_BC);
		} else {
			/* 出现其它异常 */
			ToastUtils.show(InitDataService.this, jsonObject.getString("dsc")
					+ "");
			currentBannerStatus = BANNER_RESPONSE_ERROR;
			Message msg = new Message();
			msg.what = SEND_BC_BANNER;
			status.setContent1Status(currentContent1Status);
			status.setContent2Status(currentContent2Status);
			status.setBannerStatus(BANNER_RESPONSE_ERROR);
			msg.obj = status;
			mHandler.sendMessage(msg);
		}
	}

	private void getContentFromHttp(int columnId) {
		/* 1. get ver from DB */
		try {
			DuanBean bean = db.findFirst(Selector.from(DuanBean.class)
					.where("appid", "=", appid).and("columnId", "=", columnId)
					.and("curPage", "=", 0));
			if (bean != null) {
				// TODO: modify
				content1Ver = bean.getVer();
//				if (columnId == 83) {
//					content1Ver = bean.getVer();
//				} else if (columnId == 84) {
//					content2Ver = bean.getVer();
//				}
			} else {
				content1Ver = 0;
				// TODO: modify
//				content2Ver = 0;
			}
		} catch (DbException e) {
			e.printStackTrace();
			return;
		}
		LogUtils.e("appid=" + appid + ", columnId=" + columnId
				+ ", content1Ver=" + content1Ver + ", content2Ver="
				+ content2Ver);

		/* 2. send request */
		// TODO: modify
		sendContentRequest(4, content1Ver);
//		if (columnId == 83) {
//			sendContentRequest(columnId, content1Ver);
//		} else if (columnId == 84) {
//			sendContentRequest(columnId, content2Ver);
//		}
	}

	private void sendContentRequest(int columnId, int contentVer) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.KEY_APPID, appid + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_CLMID, columnId + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_FIRST, 0 + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_MAX, 12 + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_VER, contentVer + ""));
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(nvps);
		new HttpUtils().send(HttpRequest.HttpMethod.POST,
				Constants.ACTION_GET_COLUMN_CONTENT, params,
				new ContentRequestCallBack(columnId));
	}

	private class ContentRequestCallBack extends RequestCallBack<String> {

		private int columnId;

		public ContentRequestCallBack(int columnId) {
			this.columnId = columnId;
		}

		@Override
		public void onStart() {
			super.onStart();
			Message msg = new Message();
			// TODO: modify
			currentContent1Status = CONTENT1_REQUESTING;
			msg.what = SEND_BC_CONTENT1;
			status.setContent1Status(CONTENT1_REQUESTING);
			status.setContent2Status(currentContent2Status);
			status.setBannerStatus(currentBannerStatus);
//			if (columnId == 83) {
//				currentContent1Status = CONTENT1_REQUESTING;
//				msg.what = SEND_BC_CONTENT1;
//				status.setContent1Status(CONTENT1_REQUESTING);
//				status.setContent2Status(currentContent2Status);
//				status.setBannerStatus(currentBannerStatus);
//			} else if (columnId == 84) {
//				currentContent2Status = CONTENT2_REQUESTING;
//				msg.what = SEND_BC_CONTENT2;
//				status.setContent1Status(currentContent1Status);
//				status.setContent2Status(CONTENT2_REQUESTING);
//				status.setBannerStatus(currentBannerStatus);
//			}
			msg.obj = status;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parseContentResult(responseInfo.result);
		}

		private void parseContentResult(String result) {
			LogUtils.e("result=" + result);
			/* 从服务器响应中获取相应数据，也可保存在本地数据库中 */
			JSONObject jsonObject = null;
			try {
				jsonObject = JSON.parseObject(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (jsonObject == null) {
				LogUtils.e("(jsonObject==null)" + (jsonObject == null));
				return;
			}

			int code = jsonObject.getIntValue("code");
			if (code == 0) {/* 表示可以正常读取数据 */
				boolean hasNew = jsonObject.getBooleanValue("hasNew");
				if (hasNew) {
					/* 开始解析数据 */
					int _ver = jsonObject.getIntValue("ver");
					int type = jsonObject.getIntValue("type");
					JSONArray jsonArray = jsonObject.getJSONArray("jokes");
					List<DuanBean> list = new ArrayList<DuanBean>();
					for (int i = 0; i < jsonArray.size(); i++) {
						DuanBean bean = new DuanBean();
						JSONObject jsonObject2 = jsonArray.getJSONObject(i);
						int id = jsonObject2.getIntValue("id");
//						bean.setId(id);
						bean.setDuanId(id);
						bean.setNick(jsonObject2.getString("userName"));
						bean.setAvatarUrl(jsonObject2.getString("userVar"));
						bean.setContent(jsonObject2.getString("jokeDesc"));
						bean.setImgUrl(jsonObject2.getString("imgUrl"));
						bean.setVip(jsonObject2.getBoolean("vip"));
						bean.setGood(jsonObject2.getIntValue("good"));
						bean.setBad(jsonObject2.getIntValue("bad"));
						bean.setVer(_ver);
						bean.setContentType(type);
						bean.setAppid(appid);
						bean.setColumnId(columnId);
						bean.setCurPage(0);
						list.add(bean);
					}

					// TODO: 将数据保存到数据库中
					try {
						db.saveAll(list);
					} catch (DbException e) {
						e.printStackTrace();
						return;
					}
				}

				/* send message */
				Message msg = new Message();
				// TODO: modify
				currentContent1Status = CONTENT1_REQUEST_FINISH;
				msg.what = SEND_BC_CONTENT1;
				status.setContent1Status(CONTENT1_REQUEST_FINISH);
				status.setContent2Status(currentContent2Status);
				status.setBannerStatus(currentBannerStatus);
//				if (columnId == 83) {
//					currentContent1Status = CONTENT1_REQUEST_FINISH;
//					msg.what = SEND_BC_CONTENT1;
//					status.setContent1Status(CONTENT1_REQUEST_FINISH);
//					status.setContent2Status(currentContent2Status);
//					status.setBannerStatus(currentBannerStatus);
//				} else if (columnId == 84) {
//					currentContent2Status = CONTENT2_REQUEST_FINISH;
//					msg.what = SEND_BC_CONTENT2;
//					status.setContent1Status(currentContent1Status);
//					status.setContent2Status(CONTENT2_REQUEST_FINISH);
//					status.setBannerStatus(currentBannerStatus);
//				}

				msg.obj = status;
				mHandler.sendMessage(msg);
			} else { /* 出现其它异常 */
				ToastUtils.show(InitDataService.this,
						jsonObject.getString("dsc") + "");
				Message msg = new Message();
				// TODO: modify
				currentContent1Status = CONTENT1_RESPONSE_ERROR;
				msg.what = SEND_BC_CONTENT1;
				status.setContent1Status(CONTENT1_RESPONSE_ERROR);
				status.setContent2Status(currentContent2Status);
				status.setBannerStatus(currentBannerStatus);
//				if (columnId == 83) {
//					currentContent1Status = CONTENT1_RESPONSE_ERROR;
//					msg.what = SEND_BC_CONTENT1;
//					status.setContent1Status(CONTENT1_RESPONSE_ERROR);
//					status.setContent2Status(currentContent2Status);
//					status.setBannerStatus(currentBannerStatus);
//				} else if (columnId == 84) {
//					currentContent2Status = CONTENT2_RESPONSE_ERROR;
//					msg.what = SEND_BC_CONTENT2;
//					status.setContent1Status(currentContent1Status);
//					status.setContent2Status(CONTENT2_RESPONSE_ERROR);
//					status.setBannerStatus(currentBannerStatus);
//				}

				msg.obj = status;
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			Message message = new Message();
			// TODO: modify
			currentContent1Status = CONTENT1_REQUEST_FAILED;
			status.setContent1Status(CONTENT1_REQUEST_FAILED);
			status.setContent2Status(currentContent2Status);
			status.setBannerStatus(currentBannerStatus);
			if (!reRequestContent1) {
				reRequestContent1 = true;
				message.what = RE_REQUEST_CONTENT1;
				message.arg1 = columnId;
			} else {
				message.what = SEND_BC_CONTENT1;
			}
//			if (columnId == 83) {
//				currentContent1Status = CONTENT1_REQUEST_FAILED;
//				status.setContent1Status(CONTENT1_REQUEST_FAILED);
//				status.setContent2Status(currentContent2Status);
//				status.setBannerStatus(currentBannerStatus);
//				if (!reRequestContent1) {
//					reRequestContent1 = true;
//					message.what = RE_REQUEST_CONTENT1;
//					message.arg1 = columnId;
//				} else {
//					message.what = SEND_BC_CONTENT1;
//				}
//			} else if (columnId == 84) {
//				currentContent2Status = CONTENT2_REQUEST_FAILED;
//				status.setContent1Status(currentContent1Status);
//				status.setContent2Status(CONTENT2_REQUEST_FAILED);
//				status.setBannerStatus(currentBannerStatus);
//				if (!reRequestContent2) {
//					reRequestContent2 = true;
//					message.what = RE_REQUEST_CONTENT2;
//					message.arg1 = columnId;
//				} else {
//					message.what = SEND_BC_CONTENT2;
//				}
//			}

			message.obj = status;
			mHandler.sendMessage(message);
		}

	}

	private void handleBroadcastAndNextStep(int what, MultiReqStatus reqStatus) {
		/* 1. save status */
		// TODO: add
		reqStatus.setBannerStatus(BANNER_REQUEST_FINISH);
		reqStatus.setContent2Status(CONTENT2_REQUEST_FINISH);
		
		GlobalHelp.getInstance().setMultiReqStatus(reqStatus);

		int content1Status = reqStatus.getContent1Status();
		int content2Status = reqStatus.getContent2Status();
		int bannerStatus = reqStatus.getBannerStatus();

		/* 2. send BroadcastReceiver */
		Intent intent = new Intent(ACTION);
		intent.putExtra("content1Status", content1Status);
		intent.putExtra("content2Status", content2Status);
		intent.putExtra("bannerStatus", bannerStatus);
		intent.putExtra("bc_type", what);
		sendBroadcast(intent);

		/* 3. handle next step */
		boolean content1ReqEnd = ((content1Status == CONTENT1_REQUEST_FAILED)
				|| (content1Status == CONTENT1_REQUEST_FINISH) || (content1Status == CONTENT1_RESPONSE_ERROR));
		boolean content2ReqEnd = ((content2Status == CONTENT2_REQUEST_FAILED)
				|| (content2Status == CONTENT2_REQUEST_FINISH) || (content2Status == CONTENT2_RESPONSE_ERROR));
		boolean bannerReqEnd = ((bannerStatus == BANNER_REQUEST_FAILED)
				|| (bannerStatus == BANNER_REQUEST_FINISH) || (bannerStatus == BANNER_RESPONSE_ERROR));
		/* remove thread */
		if (content1ReqEnd) {
			mHandler.removeCallbacks(content1Runnable);
		}
		if (content2ReqEnd) {
			mHandler.removeCallbacks(content2Runnable);
		}
		if (bannerReqEnd) {
			mHandler.removeCallbacks(bannerRunnable);
		}
		/* stop service */
		LogUtils.e("content1ReqEnd=" + content1ReqEnd + ", content2ReqEnd=" + content2ReqEnd + ", bannerReqEnd=" + bannerReqEnd);
		if (content1ReqEnd && content2ReqEnd && bannerReqEnd) {
			LogUtils.e("thread.name=" + Thread.currentThread().getName());
			stopSelf();
			handlerThread.quit();
		}
	}

}
