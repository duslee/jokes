/**
 * 
 */
package com.ly.duan.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.as.image.ImageRequest;
import com.common.as.image.ImageRequest.ImageRequestCallback;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.ly.duan.bean.PushBean;
import com.ly.duan.help.DBHelp;
import com.ly.duan.ui.ShowPageActivity;
import com.ly.duan.utils.AppUtils;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.ResourceUtils;
import com.ly.duan.utils.StringUtils;
import com.ly.duan.utils.ToastUtils;
import com.sjm.gxdz.R;

public class PushService extends Service {
	
	private static final int CLEAR_PUSH_REQUEST = 26;
	private static final int POST_PUSH_NOTIFICATION = 27;

	/** 对应appid */
	private long appid = 0L;
	/** 上传服务器进行匹配的版本号 */
	private int ver = 0;
	private PushBean pushBean = null;

	private DbUtils db;
	private MyHandler mHandler;
	private HandlerThread handlerThread;
	
	/** 通知管理器 */
	private NotificationManager nm = null;
	/** 通知的构造器 */
	private NotificationCompat.Builder builder = null;

	private boolean isVersionBig = false;
	
	private boolean requestPush = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		initData();
	}

	private void initData() {
		/* 1. init data */
		db = DBHelp.getInstance(PushService.this);
		appid = Long.parseLong(ResourceUtils.getFileFromAssets(PushService.this, "appid.txt"));
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		isVersionBig = AppUtils.compareToVersion(android.os.Build.VERSION_CODES.JELLY_BEAN);
		
		/* 2. init Handler */
		handlerThread = new HandlerThread("push service");
		handlerThread.start();
		mHandler = new MyHandler(handlerThread.getLooper());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/* 从服务器获取push列表，并保存在数据库中；若版本没有更新，则直接从数据库中获取数据 */
		mHandler.post(pushRunnable);
		return START_NOT_STICKY;
	}
	
	private class MyHandler extends Handler {

		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CLEAR_PUSH_REQUEST:
				mHandler.removeCallbacks(pushRunnable);
				break;
				
			case POST_PUSH_NOTIFICATION:
				postNotification((PushBean) msg.obj);
				break;

			default:
				break;
			}
		}
		
	}
	
	private Runnable pushRunnable = new Runnable() {
		
		@Override
		public void run() {
			LogUtils.e("thread.name=" + Thread.currentThread().getName());
			getPushesFromHttp();
		}
		
	};
	
	private void getPushesFromHttp() {
		/* 1. get ver */
		try {
			PushBean bean = db.findFirst(
					Selector.from(PushBean.class)
							.where("appid", "=", appid));
			if (bean != null) {
				ver = bean.getVer();
			} else {
				ver = 0;
			}
		} catch (DbException e) {
			e.printStackTrace();
			ver = 0;
			return;
		}
		
		/* 2. send Request */
		sendPushRequest();
	}

	private void sendPushRequest() {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.KEY_APPID, appid + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_VER, ver + ""));
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(nvps);
		new HttpUtils().send(HttpRequest.HttpMethod.POST, Constants.ACTION_GET_PUSH_LIST,
				params, new PushRequestCallBack());
	}
	
	private class PushRequestCallBack extends RequestCallBack<String> {

		@Override
		public void onFailure(HttpException error, String msg) {
			if (!requestPush) {
				requestPush = true;
				sendPushRequest();
			} else {
				mHandler.sendEmptyMessage(CLEAR_PUSH_REQUEST);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parsePushResult(responseInfo.result);
		}

		private void parsePushResult(String result) {
			LogUtils.e(result);
			/* 从服务器响应中获取相应数据，也可保存在本地数据库中 */
			JSONObject jsonObject = null;
			try {
				jsonObject = JSON.parseObject(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null == jsonObject) {
				LogUtils.e("PushService.parseResult (jsonObject==null)");
				return;
			}
			
			/* parse data */
			int code = jsonObject.getIntValue("code");
			if (code == 0) {/* 表示可以正常读取数据 */
				boolean hasNew = jsonObject.getBooleanValue("hasNew");
				if (!hasNew) {
					LogUtils.e("get data from db");
					/* 直接从数据库中获取数据 */
					List<PushBean> _list = new ArrayList<PushBean>();
					try {
						_list = db.findAll(Selector.from(PushBean.class)
								.where("appid", "=", appid)
								.and(WhereBuilder.b("ver", "=", ver)));
					} catch (DbException e) {
						e.printStackTrace();
						return;
					}
					if ((null != _list) && (_list.size() > 0)) {
						pushBean = _list.get(0);
					}
				} else {
					LogUtils.e("get data from Server");
					/* 本地保存版本号（注意：这里对应栏目有多少，需要保存多少，所以键值后缀添加了columnId） */
					JSONArray jsonArray = jsonObject.getJSONArray("pushs");
					
					/* clear data in DB */
					if (jsonArray.size() > 0) {
						clearDataInDb();
					} else {
						Message msg = mHandler.obtainMessage(POST_PUSH_NOTIFICATION);
						msg.obj = pushBean;
						mHandler.sendMessage(msg);
						
						mHandler.removeCallbacks(pushRunnable);
						return;
					}
					
					ver = jsonObject.getIntValue("ver");
					List<PushBean> _list = new ArrayList<PushBean>();
					for (int i = 0; i < jsonArray.size(); i++) {
						PushBean bean = new PushBean();
						JSONObject jsonObject2 = jsonArray.getJSONObject(i);
						bean.setVer(ver);
						bean.setAppid(appid);
						bean.setPushTitle(jsonObject2.getString("pushTitle"));
						bean.setPushDesc(jsonObject2.getString("pushDesc"));
						bean.setPushImgUrl(jsonObject2.getString("pushImgUrl"));
						bean.setSourceType(jsonObject2.getIntValue("sourceType"));
						bean.setSourceId(jsonObject2.getLongValue("sourceId"));
						bean.setContentUrl(jsonObject2.getString("contentUrl"));
						bean.setUserNick(jsonObject2.getString("userNick"));
						bean.setUserVarUrl(jsonObject2.getString("userVarUrl"));
						if (0 == i) {
							pushBean = bean;
						}
						// 将数据保存到数据库中
						_list.add(bean);
					}

					// 将数据保存到数据库中
					try {
						db.saveAll(_list);
					} catch (DbException e) {
						e.printStackTrace();
						return;
					}
				}

				/* handle result */
				Message msg = mHandler.obtainMessage(POST_PUSH_NOTIFICATION);
				msg.obj = pushBean;
				mHandler.sendMessage(msg);
			} else {/* 出现其它异常 */
				ToastUtils.show(PushService.this, jsonObject.getString("dsc") + "");
			}
			
			mHandler.removeCallbacks(pushRunnable);
		}

		private void clearDataInDb() {
			try {
				db.delete(PushBean.class,
						WhereBuilder.b("appid", "=", appid)
									.and("ver", "=", ver));
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void postNotification(PushBean pushBean) {
		if (pushBean == null) {
//			pushBean = getPushBean();
			return;
		}
		LogUtils.d(pushBean.toString());
		
		/* 1. create RemoteViews */
		RemoteViews remoteViews = null;
		if (isVersionBig) {
			remoteViews = new RemoteViews(getPackageName(), R.layout.view_noti);
		} else {
			remoteViews = new RemoteViews(getPackageName(), R.layout.view_noti2);
		}
		
		remoteViews.setViewVisibility(R.id.noti_pb, View.GONE);
		remoteViews.setTextViewText(R.id.noti_title, pushBean.getPushTitle());
		remoteViews.setTextViewText(R.id.noti_subTitle, pushBean.getPushDesc());
		String url = pushBean.getPushImgUrl();
		if (!StringUtils.isBlank(url) && !StringUtils.isBlank(url.trim())) {
			displayNotiIV(remoteViews, url, pushBean);
		} else {
			remoteViews.setImageViewResource(R.id.noti_iv, R.drawable.icon);
			createNoti(remoteViews, pushBean);
		}
	}
	
	/** create Notification */
	private void createNoti(RemoteViews remoteViews, PushBean pushBean) {
		if (isVersionBig) {
			createNotiBuilder(remoteViews, pushBean);
		} else {
			createNotification(remoteViews, pushBean);
		}
	}

	private PushBean getPushBean() {
		PushBean bean = new PushBean();
		bean.setPushTitle("喜欢老女人的年轻人");
		bean.setPushDesc("push1");
		bean.setSourceType(1);
		bean.setAppid(appid);
		bean.setPushImgUrl("http://7q5dcu.com2.z0.glb.qiniucdn.com/article_7926.jpg");
		bean.setContentUrl("http://7q5dcu.com2.z0.glb.qiniucdn.com/article_7926.htm");
		bean.setPushId(2009L);
		bean.setVer(2);
		LogUtils.d(bean.toString());
		return bean;
	}

	private void createNotiBuilder(RemoteViews remoteViews, PushBean pushBean) {
		Intent intent = new Intent();
		intent.setClass(PushService.this, ShowPageActivity.class);
		intent.putExtra("title", pushBean.getPushTitle());
		intent.putExtra("url", pushBean.getContentUrl());
		int type = pushBean.getSourceType();
		if (type == 1) {	/* Article */
			intent.putExtra("isPostBar", false);
		} else if (type == 2) {	/* Post */
			intent.putExtra("isPostBar", true);
			intent.putExtra("userNick", pushBean.getUserNick());
			intent.putExtra("imgUrl", pushBean.getUserVarUrl());
		}
		PendingIntent pi = PendingIntent.getActivity(PushService.this, (int) pushBean.getPushId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder = new NotificationCompat.Builder(PushService.this);
		builder.setSmallIcon(R.drawable.icon);
		builder.setContentTitle(pushBean.getPushTitle());
		builder.setTicker(pushBean.getPushTitle());
		builder.setContent(remoteViews);
		builder.setContentIntent(pi);
		Notification no = builder.build();
		no.flags = Notification.FLAG_AUTO_CANCEL;
		no.defaults = Notification.DEFAULT_SOUND;
		no.defaults |= Notification.DEFAULT_LIGHTS;
		no.defaults |= Notification.DEFAULT_VIBRATE;
		nm.notify((int) pushBean.getPushId(), no);
		
		stopSelf();
	}

	private void createNotification(RemoteViews remoteViews, PushBean pushBean) {
		Notification no = new Notification(R.drawable.icon, pushBean.getPushTitle(), 0);
		Intent intent = new Intent();
		intent.setClass(PushService.this, ShowPageActivity.class);
		intent.putExtra("title", pushBean.getPushTitle());
		intent.putExtra("url", pushBean.getContentUrl());
		int type = pushBean.getSourceType();
		if (type == 1) {	/* Article */
			intent.putExtra("url", pushBean.getSourceId());
		} else if (type == 2) {	/* Post */
			intent.putExtra("isPostBar", true);
			intent.putExtra("userNick", pushBean.getUserNick());
			intent.putExtra("imgUrl", pushBean.getUserVarUrl());
		}
		PendingIntent pi = PendingIntent.getActivity(PushService.this, (int) pushBean.getPushId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
	
		no.contentIntent = pi;
		no.contentView = remoteViews;
		
		no.flags = Notification.FLAG_AUTO_CANCEL;
		no.defaults = Notification.DEFAULT_SOUND;
		no.defaults |= Notification.DEFAULT_LIGHTS;
		no.defaults |= Notification.DEFAULT_VIBRATE;
		nm.notify((int) pushBean.getPushId(), no);
		
		stopSelf();
	}

	private void displayNotiIV(final RemoteViews remoteViews, String url, final PushBean pushBean) {
		ImageRequest request = new ImageRequest(PushService.this, url, new ImageRequestCallback() {
			
			@Override
			public void onImageRequestStarted(ImageRequest request) {
			}
			
			@Override
			public void onImageRequestStart(ImageRequest request, Bitmap image) {
			}
			
			@Override
			public void onImageRequestFailed(ImageRequest request, Throwable throwable) {
				LogUtils.d("displayNotiIV onImageRequestFailed");
				remoteViews.setImageViewResource(R.id.noti_iv, R.drawable.icon);
				createNoti(remoteViews, pushBean);
			}
			
			@Override
			public void onImageRequestEnded(ImageRequest request, Bitmap image) {
				LogUtils.d("displayNotiIV onImageRequestEnded (null == image)=" + (null == image));
				remoteViews.setImageViewBitmap(R.id.noti_iv, image);
				createNoti(remoteViews, pushBean);
			}
			
			@Override
			public void onImageRequestCancelled(ImageRequest request) {
			}
			
		});
		request.load(PushService.this, true);
	}

}
