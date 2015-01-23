package com.ly.duan.user_inter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.common.as.pushtype.PushInfo;
import com.common.as.store.PushInfos;
import com.common.as.utils.DateUtil;
import com.common.as.utils.PointUtil;
import com.common.as.utils.PointUtil.PointInfo;
import com.lidroid.xutils.util.LogUtils;
import com.ly.duan.service.PkgSetupReceiver;
import com.ly.duan.utils.AppUtils;
import com.ly.duan.utils.FileUtils;
import com.ly.duan.utils.ResourceUtils;
import com.ly.duan.utils.StringUtils;
import com.sjm.gxdz.R;

public class DownloadUtils {

	/* APK下载状态 */
	/** 开始下载 */
	public static final int DOWNLOAD_START = 51;
	/** 下载完成 */
	public static final int DOWNLOAD_END = 52;
	/** 下载失败 */
	public static final int DOWNLOAD_FAILED = 53;
	/** 下载出现异常 */
	public static final int DOWNLOAD_ERROR = 54;
	public static final int SHOW_PROGRESS = 55;

	private static DownloadUtils instance = null;

	private Map<String, IDownload> downMaps = new HashMap<String, IDownload>();

	/** 超时 */
	private static final int TIMEOUT = 10 * 1000;

	/** 通知管理器 */
	private NotificationManager nm = null;
	/** 通知的构造器 */
	private NotificationCompat.Builder builder = null;

	private Activity mActivity;

	private boolean isVersionBig = false;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			/* TODO: add */
			PushInfo pushInfo = (PushInfo) msg.obj;
			IDownload iDownload = null;
			if (downMaps.containsKey(pushInfo.getAppid())) {
				iDownload = downMaps.get(pushInfo.getAppid());
			}

			switch (msg.what) {
			case DOWNLOAD_START: {
				/* 上传开始下载日志 */
				// LogUtils.e(pi.toString());
				PointUtil.SendPoint(mActivity, new PointInfo(
						PointUtil.POINT_ID_START_DOWN, pushInfo));
				break;
			}

			case DOWNLOAD_END: {
				// TODO: modify
				pushInfo.setStatus(PushInfo.STATUS_DOWN_FINISH);
				pushInfo.setDownFinshT(DateUtil.getCurrentMs());
				PushInfos.getInstance()
						.put(pushInfo.getPackageName(), pushInfo);

				/* 上传下载完成日志 */
				// LogUtils.e(pi.toString());
				PointUtil.SendPoint(mActivity, new PointInfo(
						PointUtil.POINT_ID_FINISH_DOWN, pushInfo));

				if (iDownload != null) {
					iDownload.onDlSuccess();
				}

				/* pop setup dialog */
				popSetupDialog(pushInfo);
				break;
			}

			case DOWNLOAD_FAILED: {
				pushInfo.setStatus(PushInfo.STATUS_PUSHED);
				PushInfos.getInstance()
						.put(pushInfo.getPackageName(), pushInfo);
				if (iDownload != null) {
					iDownload.onDlFailed();
				}
				break;
			}

			case DOWNLOAD_ERROR: {
				pushInfo.setStatus(PushInfo.STATUS_PUSHED);
				PushInfos.getInstance()
						.put(pushInfo.getPackageName(), pushInfo);
				if (iDownload != null) {
					iDownload.onDlError();
				}
				break;
			}

			default:
				break;
			}

		};

	};

	private DownloadUtils() {
	}

	protected void popSetupDialog(final PushInfo pushInfo) {
		/* handle noti */
		// handleNoti();
		new AlertDialog.Builder(mActivity)
				.setMessage("《" + pushInfo.getAppName() + "》下载完成，立即安装？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startSetup(pushInfo);
						nm.cancel(Integer.parseInt(pushInfo.getAppid()));
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	protected void startSetup(PushInfo pushInfo) {
		/* 1. 上传APK安装日志 */
		PointUtil.SendPoint(mActivity, new PointInfo(PointUtil.POINT_ID_SETUP,
				pushInfo));

		/* 2. 打开安装界面 */
		Intent i = new Intent(Intent.ACTION_VIEW);
//		i.setDataAndType(Uri.parse("file://" + pushInfo.getFileUrl()),
//				"application/vnd.android.package-archive");
		i.setDataAndType(Uri.parse("file://" + pushInfo.getFileUrl()),
				PushInfo.getMIME_APP());
		LogUtils.e("fileUrl" + pushInfo.getFileUrl());
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivity.startActivity(i);
	}

	public static DownloadUtils getInstance() {
		if (null == instance) {
			synchronized (DownloadUtils.class) {
				instance = new DownloadUtils();
			}
		}
		return instance;
	}

	public void startDownload(Activity activity, PushInfo pushInfo,
			IDownload iDownload) {
		/* config params */
		this.mActivity = activity;
		downMaps.put(pushInfo.getAppid(), iDownload);

		nm = (NotificationManager) mActivity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		isVersionBig = AppUtils
				.compareToVersion(android.os.Build.VERSION_CODES.JELLY_BEAN);

		String url = pushInfo.getmDownUrl();
		LogUtils.e("StringUtils.isBlank(url)==" + StringUtils.isBlank(url));
		if (!StringUtils.isBlank(url)) {
			new DownloadApkTask(pushInfo).execute(url);
		}
	}

	/**
	 * 功能：完成apk文件的异步下载，并能显示下载进度<br>
	 * 日期：2014-10-28
	 * 
	 * @author zfwu
	 * @time 上午11:44:24
	 */
	private class DownloadApkTask extends AsyncTask<String, Integer, File> {

		private int pro;
		private PushInfo pushInfo;
		private RemoteViews remoteViews;
		private Notification notiInLowVer = null;

		public DownloadApkTask(PushInfo pushInfo) {
			this.pushInfo = pushInfo;
			if (isVersionBig) {
				remoteViews = new RemoteViews(mActivity.getPackageName(),
						R.layout.view_noti);
			} else {
				remoteViews = new RemoteViews(mActivity.getPackageName(),
						R.layout.view_noti2);
			}
		}

		@Override
		protected void onPreExecute() {
			/* 1. config RemoteViews */
			LogUtils.e("onPreExecute1111: start download");
			remoteViews.setImageViewResource(R.id.noti_iv, R.drawable.icon);
			remoteViews.setTextViewText(R.id.noti_title, pushInfo.getAppName()
					+ "下载");
			remoteViews.setProgressBar(R.id.noti_pb, 100, 0, false);
			// remoteViews.setViewVisibility(R.id.noti_subTitle, View.GONE);
			if (isVersionBig) {
				createNotiBuilder();
			} else {
				createNotification();
			}
			LogUtils.e("onPreExecute1111: after notify");

			/* 2. send message(start download) */
			Message msg = new Message();
			msg.what = DOWNLOAD_START;
			msg.obj = this.pushInfo;
			mHandler.sendMessage(msg);
		}

		private void createNotification() {
			notiInLowVer = new Notification(R.drawable.icon, "《"
					+ pushInfo.getAppName() + "》开始下载...", 0);
			Intent intent = new Intent();
			PendingIntent pi = PendingIntent.getActivity(mActivity, 0, intent,
					0);
			notiInLowVer.contentIntent = pi;
			notiInLowVer.contentView = remoteViews;
			nm.notify(Integer.parseInt(pushInfo.getAppid()), notiInLowVer);
		}

		private void createNotiBuilder() {
			builder = new NotificationCompat.Builder(mActivity);
			builder.setSmallIcon(R.drawable.icon);
			builder.setContentTitle("《" + pushInfo.getAppName() + "》开始下载...");
			builder.setTicker("《" + pushInfo.getAppName() + "》开始下载...");
			// builder.setProgress(100, 0, false);
			builder.setContent(remoteViews);
			PendingIntent contentIntent = PendingIntent.getActivity(mActivity,
					0, new Intent(), 0);
			builder.setContentIntent(contentIntent);
			LogUtils.e("onPreExecute1111: start notify");
			Notification notification = builder.build();
			nm.notify(Integer.parseInt(pushInfo.getAppid()), notification);
		}

		@Override
		protected File doInBackground(String... params) {
			LogUtils.e("doInBackground2222: downloading");
			File file = null;
			try {
				/* 1. 创建对应的apk文件 */
				String filePath = FileUtils.getCurrentDataPath(mActivity,
						"360apk");
				LogUtils.e("filePath: " + filePath);
				String fileName = pushInfo.getAppName() + ".apk";
				file = new File(filePath, fileName);
				if (!file.exists()) {
					file.createNewFile();
				}
				LogUtils.e("create file: " + file.exists());
				/* 2. 实现下载操作，同时显示进度 */
				URL url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(TIMEOUT);
				conn.setReadTimeout(TIMEOUT);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 404) {
					/* send message(download failed) */
					Message msg = new Message();
					msg.what = DOWNLOAD_ERROR;
					msg.obj = pushInfo;
					mHandler.sendMessage(msg);
					throw new Exception("download fail!");
				}
				/* 3. 获取下载文件的总长度 */
				int totalLen = conn.getContentLength();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file, false);
				byte buffer[] = new byte[1024];
				int readSize = 0;
				int downCount = 0;
				int updateCount = 0;
				int down_step = 5;
				int process = 0;
				pro = 0;
				while ((readSize = is.read(buffer)) != -1) {
					fos.write(buffer, 0, readSize);
					downCount += readSize;
					process = (int) (downCount * 100 / (float) totalLen);
					if (updateCount == 0
							|| (process - down_step) >= updateCount) {
						updateCount += down_step;
						publishProgress(process);
					}
				}
				/* 清理操作 */
				if (null != conn) {
					conn.disconnect();
				}
				if (null != is) {
					is.close();
					is = null;
				}
				if (null != fos) {
					fos.close();
					fos = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return file;
		}

		@Override
		protected void onProgressUpdate(final Integer... values) {
			if (pro < values[0]) {
				pro = values[0];
				remoteViews.setViewVisibility(R.id.noti_subTitle, View.VISIBLE);
				remoteViews.setTextViewText(R.id.noti_subTitle, "下载进度："
						+ values[0] + "%");
				remoteViews.setProgressBar(R.id.noti_pb, 100, values[0], false);
				if (isVersionBig) {
					createNotiBuilder2();
				} else {
					createNotification();
				}
			}
		}

		private void createNotiBuilder2() {
			builder.setContent(remoteViews);
			PendingIntent contentIntent = PendingIntent.getActivity(mActivity,
					0, new Intent(), 0);
			builder.setContentIntent(contentIntent);
			Notification no = builder.build();
			no.flags = Notification.FLAG_NO_CLEAR;
			nm.notify(Integer.parseInt(pushInfo.getAppid()), no);
			no = null;
		}

		@Override
		protected void onPostExecute(File file) {
			remoteViews.setProgressBar(R.id.noti_pb, 0, 0, true);
			if (file != null) {
				remoteViews.setViewVisibility(R.id.noti_subTitle, View.VISIBLE);
				remoteViews.setTextViewText(R.id.noti_subTitle,
						pushInfo.getAppName() + "下载完成，点击安装...");
				remoteViews.setProgressBar(R.id.noti_pb, 0, 0, false);
				remoteViews.setViewVisibility(R.id.noti_pb, View.GONE);

				if (isVersionBig) {
					createNotiBuilder3(file.getAbsolutePath());
				} else {
					createNotification2(file.getAbsolutePath());
				}

				Message msg = new Message();
				msg.what = DOWNLOAD_END;
				pushInfo.setFileUrl(file.getAbsolutePath());
				msg.obj = pushInfo;
				mHandler.sendMessage(msg);
			} else {
				remoteViews.setViewVisibility(R.id.noti_pb, View.GONE);
				remoteViews.setTextViewText(R.id.noti_subTitle,
						pushInfo.getAppName() + "下载失败...");

				if (isVersionBig) {
					createNotiBuilder4();
				} else {
					createNotification();
				}

				Message msg = new Message();
				msg.what = DOWNLOAD_FAILED;
				msg.obj = pushInfo;
				mHandler.sendMessage(msg);
			}
		}

		private void createNotiBuilder4() {
			builder.setContent(remoteViews);
			PendingIntent contentIntent = PendingIntent.getActivity(mActivity,
					0, new Intent(), 0);
			builder.setContentIntent(contentIntent);
			Notification no = builder.build();
			no.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(Integer.parseInt(pushInfo.getAppid()), no);
			nm.cancel(Integer.parseInt(pushInfo.getAppid()));
		}

		private void createNotiBuilder3(String filePath) {
			builder.setContent(remoteViews);

			String appid = ResourceUtils.getFileFromAssets(mActivity,
					"appid.txt");
			LogUtils.e("appid=" + appid);
			Intent intent = new Intent(mActivity, PkgSetupReceiver.class);
			intent.setAction(PkgSetupReceiver.PACKAGE_START_SETUP);
			intent.putExtra("pkg", pushInfo.getPackageName());
			intent.putExtra("appid", appid);
			PendingIntent contentIntent = PendingIntent.getBroadcast(mActivity,
					Integer.parseInt(pushInfo.getAppid()), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			builder.setContentIntent(contentIntent);
			Notification no = builder.build();
			no.flags = Notification.FLAG_AUTO_CANCEL;
			no.defaults = Notification.DEFAULT_SOUND;
			no.defaults |= Notification.DEFAULT_LIGHTS;
			no.defaults |= Notification.DEFAULT_VIBRATE;
			nm.notify(Integer.parseInt(pushInfo.getAppid()), no);
		}

		private void createNotification2(String filePath) {
			String appid = ResourceUtils.getFileFromAssets(mActivity,
					"appid.txt");
			LogUtils.e("appid=" + appid);
			Intent intent = new Intent(mActivity, PkgSetupReceiver.class);
			intent.setAction(PkgSetupReceiver.PACKAGE_START_SETUP);
			intent.putExtra("pkg", pushInfo.getPackageName());
			intent.putExtra("appid", appid);
			PendingIntent contentIntent = PendingIntent.getBroadcast(mActivity,
					Integer.parseInt(pushInfo.getAppid()), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			notiInLowVer.contentIntent = contentIntent;
			notiInLowVer.contentView = remoteViews;
			
			notiInLowVer.flags = Notification.FLAG_AUTO_CANCEL;
			notiInLowVer.defaults = Notification.DEFAULT_SOUND;
			notiInLowVer.defaults |= Notification.DEFAULT_LIGHTS;
			notiInLowVer.defaults |= Notification.DEFAULT_VIBRATE;
			nm.notify(Integer.parseInt(pushInfo.getAppid()), notiInLowVer);
		}

	}

}
