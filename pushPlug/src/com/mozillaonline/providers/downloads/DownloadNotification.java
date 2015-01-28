/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mozillaonline.providers.downloads;

import java.util.Collection;
import java.util.HashMap;

import com.common.as.base.log.BaseLog;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.BitmapLoder;
import com.common.as.utils.BitmapLoder.OnLoadBmp;
import com.example.pushplug.R;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.RemoteViews;

/**
 * This class handles the updating of the Notification Manager for the
 * cases where there is an ongoing download. Once the download is complete
 * (be it successful or unsuccessful) it is no longer the responsibility
 * of this component to show the download in the notification manager.
 *
 */
class DownloadNotification {

    Context mContext;
    HashMap <String, NotificationItem> mNotifications;
    private SystemFacade mSystemFacade;

    static final String LOGTAG = "DownloadNotification";
    static final String WHERE_RUNNING =
        "(" + Downloads.COLUMN_STATUS + " >= '100') AND (" +
        Downloads.COLUMN_STATUS + " <= '199') AND (" +
        Downloads.COLUMN_VISIBILITY + " IS NULL OR " +
        Downloads.COLUMN_VISIBILITY + " == '" + Downloads.VISIBILITY_VISIBLE + "' OR " +
        Downloads.COLUMN_VISIBILITY +
            " == '" + Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "')";
    static final String WHERE_COMPLETED =
        Downloads.COLUMN_STATUS + " >= '200' AND " +
        Downloads.COLUMN_VISIBILITY +
            " == '" + Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "'";


    /**
     * This inner class is used to collate downloads that are owned by
     * the same application. This is so that only one notification line
     * item is used for all downloads of a given application.
     *
     */
    static class NotificationItem {
        int mId;  // This first db _id for the download for the app
        long mTotalCurrent = 0;
        long mTotalTotal = 0;
        int mTitleCount = 0;
        String mPackageName;  // App package name
        String mDescription;
        String[] mTitles = new String[2]; // download titles.
        String mPausedText = null;
        String mPicUrl = null;
        /*
         * Add a second download to this notification item.
         */
        void addItem(String title, long currentBytes, long totalBytes) {
            mTotalCurrent += currentBytes;
            if (totalBytes <= 0 || mTotalTotal == -1) {
                mTotalTotal = -1;
            } else {
                mTotalTotal += totalBytes;
            }
            if (mTitleCount < 2) {
                mTitles[mTitleCount] = title;
            }
            mTitleCount++;
        }
    }


    /**
     * Constructor
     * @param ctx The context to use to obtain access to the
     *            Notification Service
     */
    DownloadNotification(Context ctx, SystemFacade systemFacade) {
        mContext = ctx;
        mSystemFacade = systemFacade;
        mNotifications = new HashMap<String, NotificationItem>();
    }

    /*
     * Update the notification ui.
     */
    public void updateNotification(Collection<DownloadInfo> downloads) {
        updateActiveNotification(downloads);
        updateCompletedNotification(downloads);
    }
    RemoteViews expandedView = null;
    private void updateActiveNotification(Collection<DownloadInfo> downloads) {
        // Collate the notifications
        mNotifications.clear();
        for (DownloadInfo download : downloads) {
            if (!isActiveAndVisible(download)) {
                continue;
            }
            String packageName = download.mPackage;
            long max = download.mTotalBytes;
            long progress = download.mCurrentBytes;
            long id = download.mId;
            String title = download.mTitle;
//            BaseLog
            if (title == null || title.length() == 0) {
                title = "download_unknown_title";
            }

            NotificationItem item;
            if (mNotifications.containsKey(packageName)) {
                item = mNotifications.get(packageName);
                item.addItem(title, progress, max);
            } else {
                item = new NotificationItem();
                item.mId = (int) id;
                item.mPackageName = packageName;
                item.mDescription = download.mDescription;
                item.mPicUrl = download.mPicUrl;
                item.addItem(title, progress, max);
                mNotifications.put(packageName, item);
            }
            if (download.mStatus == Downloads.STATUS_QUEUED_FOR_WIFI
                    && item.mPausedText == null) {
                item.mPausedText = "Download size requires Wi-Fi";
            }
        }

        // Add the notifications
        for (NotificationItem item : mNotifications.values()) {
            // Build the notification object
            Notification n = new Notification();

            boolean hasPausedText = (item.mPausedText != null);
//            int iconResource = android.R.drawable.stat_sys_download;
            int iconResource = R.drawable.icon_small;
            if (hasPausedText) {
                iconResource = android.R.drawable.stat_sys_warning;
            }
            StringBuilder title = new StringBuilder(item.mTitles[0]);
            n.icon = iconResource;
            n.tickerText = title+"-正在下载... "+item.mDescription;
            n.flags |= Notification.FLAG_ONGOING_EVENT;
            // Build the RemoteView object
            expandedView = new RemoteViews(mContext.getPackageName(),
                    R.layout.status_bar_ongoing_event_progress_bar);
            BaseLog.d("main3", "DownloadNoti.item="+item.mDescription);
            if (item.mTitleCount > 1) {
                title.append(", ");
                title.append(item.mTitles[1]);
                n.number = item.mTitleCount;
                if (item.mTitleCount > 2) {
                    title.append(String.format("and <xliff:g id=\"number\" example=\"27\">%d</xliff:g> more",
                            new Object[] { Integer.valueOf(item.mTitleCount - 2) }));
                }
            } else {
                expandedView.setTextViewText(R.id.description,
                        item.mDescription);
            }
            expandedView.setTextViewText(R.id.title, title+"-正在下载");

            if (hasPausedText) {
               // expandedView.setViewVisibility(R.id.progress_bar, View.GONE);
                expandedView.setTextViewText(R.id.paused_text, item.mPausedText);
            } else {
                expandedView.setViewVisibility(R.id.paused_text, View.GONE);
//                expandedView.setProgressBar(R.id.progress_bar,
//                        (int) item.mTotalTotal,
//                        (int) item.mTotalCurrent,
//                        item.mTotalTotal == -1);
            }
            expandedView.setTextViewText(R.id.progress_text,
                    getDownloadingText(item.mTotalTotal, item.mTotalCurrent));
            BitmapLoder lbiv = new BitmapLoder(mContext);
			lbiv.startLoad(new OnLoadBmp() {
				@Override
				public void onBitmapLoaded( Bitmap bmp) {
					
					expandedView.setImageViewBitmap(R.id.appIcon, bmp);
					
				}
			}, item.mPicUrl);
           // expandedView.setImageViewResource(R.id.appIcon, iconResource);
            n.contentView = expandedView;

            Intent intent = new Intent(Constants.ACTION_LIST);
            intent.setClassName(mContext.getPackageName(),
                    DownloadReceiver.class.getName());
            intent.setData(
                    ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, item.mId));
            intent.putExtra("multiple", item.mTitleCount > 1);

            n.contentIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            mSystemFacade.postNotification(item.mId, n);

        }
    }

    private void updateCompletedNotification(Collection<DownloadInfo> downloads) {
        for (DownloadInfo download : downloads) {
            if (!isCompleteAndVisible(download)) {
                continue;
            }
            // Add the notifications
            Notification n = new Notification();
            n.icon = android.R.drawable.stat_sys_download_done;

            long id = download.mId;
            String title = download.mTitle;
            if (title == null || title.length() == 0) {
                title = "&lt;Untitled&gt;";
            }
            Uri contentUri =
                ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, id);
            String caption;
            Intent intent;
            if (Downloads.isStatusError(download.mStatus)) {
                caption = "Download unsuccessful";
                intent = new Intent(Constants.ACTION_LIST);
            } else {
                caption = "Download complete";
                if (download.mDestination == Downloads.DESTINATION_EXTERNAL) {
                    intent = new Intent(Constants.ACTION_OPEN);
                } else {
                    intent = new Intent(Constants.ACTION_LIST);
                }
            }
            intent.setClassName(mContext.getPackageName(),
                    DownloadReceiver.class.getName());
            intent.setData(contentUri);

            n.when = download.mLastMod;
            n.setLatestEventInfo(mContext, title, caption,
                    PendingIntent.getBroadcast(mContext, 0, intent, 0));

            intent = new Intent(Constants.ACTION_HIDE);
            intent.setClassName(mContext.getPackageName(),
                    DownloadReceiver.class.getName());
            intent.setData(contentUri);
            n.deleteIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

            mSystemFacade.postNotification(download.mId, n);
        }
    }

    private boolean isActiveAndVisible(DownloadInfo download) {
        return 100 <= download.mStatus && download.mStatus < 200
                && download.mVisibility != Downloads.VISIBILITY_HIDDEN;
    }

    private boolean isCompleteAndVisible(DownloadInfo download) {
        return download.mStatus >= 200
                && download.mVisibility == Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
    }

    /*
     * Helper function to build the downloading text.
     */
    private String getDownloadingText(long totalBytes, long currentBytes) {
        if (totalBytes <= 0) {
            return "";
        }
        long progress = currentBytes * 100 / totalBytes;
        StringBuilder sb = new StringBuilder();
        sb.append(progress);
        sb.append('%');
        return sb.toString();
    }

}
