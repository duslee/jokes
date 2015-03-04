package com.ly.duan.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import pl.droidsonroids.gif.GifImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.charon.pulltorefreshlistview.LoadMoreListView;
import com.charon.pulltorefreshlistview.LoadMoreListView.OnLoadMoreListener;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.adapter.CommentAdapter;
import com.ly.duan.adapter.CommentAdapter.OnPlayVideoListener;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.bean.BannerBean;
import com.ly.duan.bean.CommentBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.ui.ShowPageActivity;
import com.ly.duan.ui.VVActivity;
import com.ly.duan.user_inter.DownloadUtils;
import com.ly.duan.user_inter.IDownload;
import com.ly.duan.utils.ActivityAnimator;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.GifUtils;
import com.ly.duan.utils.ImageUtils;
import com.ly.duan.utils.ScreenUtils;
import com.ly.duan.utils.StringUtils;
import com.sjm.gxdz.R;

public class CommentFragment2 extends BaseFragment {

	private static final int FIRST_IN = 1;
	private static final int PULL_UP = 2;
	private static final int MANUAL_FRESH = 3;

	private final static int FRAG4_IV_HEIGHT = 200;
	private final static int DUAN_IV_HEIGHT = 400;
	private final static int BANNER_IV_HEIGHT = 200;
	private final static int GIF_HEIGHT = 340;

	private static final int FRESH_LISTVIEW = 131;
	private final static int CLEAR_COMMENT_REQUEST = 132;
	private static final int MODIFY_COMMENT_LIST = 133;
	private static final int UPDATE_COMMENT_ITEM = 134;
	private static final int MOVE_UP = 135;

	/* display banner */
	@ViewInject(R.id.banner_ll)
	private LinearLayout banner_ll;

	@ViewInject(R.id.banner_content_ll)
	private LinearLayout banner_content_ll;

	@ViewInject(R.id.banner_tv)
	private TextView banner_tv;

	@ViewInject(R.id.banner_rl)
	private RelativeLayout banner_rl;

	@ViewInject(R.id.banner_iv)
	private ImageView banner_iv;

	@ViewInject(R.id.down_btn)
	private Button down_btn;

	/* display no banner */
	@ViewInject(R.id.no_banner_rl)
	private RelativeLayout no_banner_rl;

//	/* display duans */
//	@ViewInject(R.id.duan_ll)
//	private LinearLayout duan_ll;
//	
//	@ViewInject(R.id.scrollView)
//	private ScrollView scrollView;
//
//	@ViewInject(R.id.duan_avatar)
//	private ImageView duan_avatar;
//
//	@ViewInject(R.id.duan_nick)
//	private TextView duan_nick;
//
//	@ViewInject(R.id.duan_content)
//	private TextView duan_content;
//
//	@ViewInject(R.id.duan_rl)
//	private RelativeLayout duan_rl;
//
//	@ViewInject(R.id.duan_pw)
//	private ProgressWheel duan_pw;
//
//	@ViewInject(R.id.duan_picture)
//	private ImageView duan_picture;
//
//	@ViewInject(R.id.duan_gif)
//	private GifImageView duan_gif;
//
//	/* play video */
//	@ViewInject(R.id.frag4_ll)
//	private LinearLayout frag4_ll;
//
//	@ViewInject(R.id.frag4_content)
//	private TextView frag4_content;
//
//	@ViewInject(R.id.frag4_rl)
//	private RelativeLayout frag4_rl;
//
//	@ViewInject(R.id.frag4_picture)
//	private ImageView frag4_picture;
//
//	/* display comment */
//	@ViewInject(R.id.comment_tv)
//	private TextView comment_tv;

	@ViewInject(R.id.listView)
	private LoadMoreListView listView;

	/* send comment */
//	@ViewInject(R.id.comment_et)
//	private EditText comment_et;

	private ArticleBean articleBean;
	private DuanBean duanBean;
	private BannerBean bannerBean;

	private CommentAdapter adapter;

	private long appid;
	private int page = 1;
	private int contentType;
	private long contentId;

	private int currentStatus = FIRST_IN;

	private GifUtils gifUtils = null;

	private boolean insertAds;
	private boolean isAds;

	private FragHandler myHandler;
	private HandlerThread myThread;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FRESH_LISTVIEW:
				if (null == adapter) {
					initAdapter();
				}
				adapter.notifyDataSetChanged();
//				BasicUiUtils.setLVHeight(listView);
				break;

			case MOVE_UP:
				listView.smoothScrollBy(ScreenUtils.dpToPxInt(getActivity(),
						Constants.SCROLL_DISTANCE), Constants.SCROLL_DURATION);
				break;

			case UPDATE_COMMENT_ITEM:
				break;

			case MODIFY_COMMENT_LIST:
				break;

			default:
				break;
			}
		};

	};

	public static CommentFragment2 newInstance(int contentType, ArticleBean articleBean) {
		LogUtils.d("newInstance 11111");
		CommentFragment2 fragment = new CommentFragment2();
		Bundle args = new Bundle();
		args.putInt("contentType", contentType);
		args.putSerializable("bean", articleBean);
		fragment.setArguments(args);
		return fragment;
	}

	public static CommentFragment2 newInstance(int contentType, DuanBean duanBean, boolean insertAds) {
		LogUtils.d("newInstance 11111");
		return newInstance(contentType, duanBean, insertAds, false);
	}

	public static CommentFragment2 newInstance(int contentType, DuanBean duanBean, boolean insertAds, boolean isAds) {
		LogUtils.d("newInstance 11111");
		CommentFragment2 fragment = new CommentFragment2();
		Bundle args = new Bundle();
		args.putInt("contentType", contentType);
		args.putBoolean("insertAds", insertAds);
		args.putBoolean("isAds", isAds);
		args.putSerializable("bean", duanBean);
		fragment.setArguments(args);
		return fragment;
	}

	public static CommentFragment2 newInstance(int contentType,
			BannerBean bannerBean, boolean insertAds, boolean isAds) {
		LogUtils.d("newInstance 11111");
		CommentFragment2 fragment = new CommentFragment2();
		Bundle args = new Bundle();
		args.putInt("contentType", contentType);
		args.putBoolean("insertAds", insertAds);
		args.putBoolean("isAds", isAds);
		args.putSerializable("bean", bannerBean);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			visibility = (IBottomVisibility) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IBottomVisibility ");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.d("onCreate 22222");
		/* init data */
		initData();
	}

	private void initData() {
		/* 1. get data */
		if (null != getArguments()) {
			contentType = getArguments().getInt("contentType");
			if (contentType == 3) {
				articleBean = (ArticleBean) getArguments().getSerializable("bean");
				appid = articleBean.getAppid();
				contentId = articleBean.getArticleId();
			} else if (contentType == 8) {
				insertAds = getArguments().getBoolean("insertAds");
				if (!insertAds) {
					duanBean = (DuanBean) getArguments().getSerializable("bean");
					appid = duanBean.getAppid();
					contentId = duanBean.getDuanId();
				} else {
					isAds = getArguments().getBoolean("isAds");
					if (!isAds) {
						duanBean = (DuanBean) getArguments().getSerializable("bean");
						appid = duanBean.getAppid();
						contentId = duanBean.getDuanId();
					} else {
						bannerBean = (BannerBean) getArguments().getSerializable("bean");
					}
				}
			}
		}

		gifUtils = new GifUtils(getActivity());

		/* 2. start HandlerThread */
		if (null == myHandler) {
			initHandler();
		}
	}

	private void initHandler() {
		myThread = new HandlerThread("Comment Thread " + contentType);
		myThread.start();
		myHandler = new FragHandler(myThread.getLooper());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		LogUtils.d("onCreateView 33333");
		View view = inflater.inflate(R.layout.view_comment2, container, false);
		ViewUtils.inject(this, view);

		/* init UI */
		initUI();

		/* get data list to be displayed */
		if (null == adapter) {
			initAdapter();
		}

		return view;
	}
	
	private boolean getContentSuccess = false;
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		LogUtils.d("setUserVisibleHint isVisibleToUser=" + isVisibleToUser + ", getContentSuccess=" + getContentSuccess);
		if (isVisibleToUser) {
			// 相当于Fragment的onResume
			if (!getContentSuccess) {
				getContentSuccess = true;
				myHandler.post(commentRunnable);
			}
			if (null != adapter && adapter.getCount() > 0) {
				LogUtils.d("count=" + adapter.getCount());
			}
			if (null != visibility) {
				visibility.setBottomVisibility();
			}
//			else {
//				myHandler.post(commentRunnable);
//			}
		} else {
			// 相当于Fragment的onPause
		}
	}

	private void initAdapter() {
		adapter = new CommentAdapter(getActivity());
		listView.setAdapter(adapter);
//		scrollView.smoothScrollTo(0, 0);
		
		// TODO: modify
		adapter.setContentType(contentType);
		if (contentType == 3) {
			adapter.setArticleBean(articleBean);
			adapter.setVideoListener(videoListener);
		} else if (contentType == 8) {
			if (!insertAds || !isAds) {
				adapter.setDuanBean(duanBean);
			}
		}
	}

	private void initUI() {
		LogUtils.e("contentType=" + contentType + ", appid=" + appid + ", insertAds=" + insertAds + ", isAds=" + isAds + ", contentId=" + contentId);
		if (contentType == 3) {
			/* set visibility */
			banner_ll.setVisibility(View.GONE);
			no_banner_rl.setVisibility(View.VISIBLE);
//			frag4_ll.setVisibility(View.VISIBLE);
//			duan_ll.setVisibility(View.GONE);

			/* set title & picture */
//			displayArticleView();
		} else if (contentType == 8) {
			/* set visibility */
//			frag4_ll.setVisibility(View.GONE);
//			duan_ll.setVisibility(View.VISIBLE);
			if (!insertAds) {
				banner_ll.setVisibility(View.GONE);
				no_banner_rl.setVisibility(View.VISIBLE);
//				displayDuansView();
			} else {
				if (!isAds) {
					banner_ll.setVisibility(View.GONE);
					no_banner_rl.setVisibility(View.VISIBLE);
//					displayDuansView();
				} else {
					banner_ll.setVisibility(View.VISIBLE);
					no_banner_rl.setVisibility(View.GONE);
					displayBannerView();
				}
			}
		}
		
		/* handle listView */
		listView.setOnLoadMoreListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				currentStatus = PULL_UP;
				page++;
				myHandler.post(commentRunnable);
			}
			
		});
		
	}

	private void displayBannerView() {
		/* handle banner title & content */
		String bannerTitle = bannerBean.getBannerTitle();
		String bannerContent = bannerBean.getBannerDesc();
		if (!StringUtils.isBlank(bannerTitle)
				&& !StringUtils.isBlank(bannerContent)
				&& !StringUtils.isBlank(bannerTitle.trim())
				&& !StringUtils.isBlank(bannerContent.trim())) {
			banner_content_ll.setVisibility(View.VISIBLE);
			String str = String.format(
					getResources().getString(R.string.banner_text),
					bannerTitle.trim(), bannerContent.trim());
			banner_tv.setText(str);
		} else if (!StringUtils.isBlank(bannerTitle)
				&& !StringUtils.isBlank(bannerTitle.trim())) {
			banner_content_ll.setVisibility(View.VISIBLE);
			banner_tv.setText(bannerTitle.trim());
		} else if (!StringUtils.isBlank(bannerContent)
				&& !StringUtils.isBlank(bannerContent.trim())) {
			banner_content_ll.setVisibility(View.VISIBLE);
			banner_tv.setText(bannerContent.trim());
		} else {
			banner_content_ll.setVisibility(View.GONE);
		}

		/* display iv */
		String url = bannerBean.getBannerImgUrl();
		if (!StringUtils.isBlank(url)) {
			/* set visibility */
			banner_rl.setVisibility(View.VISIBLE);

			setIVHeight(banner_iv, BANNER_IV_HEIGHT);

			getBitmapUtils().display(banner_iv, url, new CustomBitmapLoadCallBack(getActivity()));
		} else {
			banner_rl.setVisibility(View.GONE);
		}

		/* set button listener */
		down_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startDownloadOrOpen(bannerBean);
			}
		});
	}

	private void startDownloadOrOpen(final BannerBean bean) {

		if (bean.getContentType() != 4) {
			sendIntent(bean);
			return;
		}

		LogUtils.e("start downloading or open...");

		/* 首先判断APK是否正在下载或已下载完成 */
		if (!checkSdcard()) {
			return;
		}
		final String pkg = bean.getContentPackage();
		PushInfo pi = PushInfos.getInstance().get(pkg);
		if (null == pi) {
			pi = new PushInfo(pkg, (10000 + bean.getBannerId()) + "", "");
			pi.setAppName(bean.getBannerTitle());
			pi.setImageUrl(bean.getBannerImgUrl());
			pi.setmDownUrl(bean.getContentUrl());
			pi.setmBrief(bean.getBannerDesc());
			pi.setPushAppID(bean.getAppid() + "");
			pi.setPushType(PushType.TYPE_STORE_LIST);
			pi.setStatus(PushInfo.STATUS_DOWN_STARTING);
			PushInfos.getInstance().put(pkg, pi);

			LogUtils.e("btn download apk!");
			/* 启动后台服务开始下载 */
			showToast("《" + bean.getBannerTitle() + "》开始下载");
			DownloadUtils.getInstance().startDownload(getActivity(), pi,
					new IDownload() {

						@Override
						public void onDlSuccess() {
							showToast("《" + bean.getBannerTitle() + "》下载完成");
						}

						@Override
						public void onDlFailed() {
							showToast(R.string.dl_failed);
						}

						@Override
						public void onDlError() {
							showToast(R.string.dl_error);
						}
					});
		} else {
			switch (pi.getStatus()) {
			/* 正在下载 */
			case PushInfo.STATUS_DOWN_STARTING:
				showToast(R.string.downloading);
				break;

			/* 下载完成 */
			case PushInfo.STATUS_DOWN_FINISH:
				showToast(R.string.download_finished);
				/* 自动弹出安装 */
				AppUtil.showSetup(getActivity(), pi);
				break;

			case PushInfo.STATUS_SETUPED:
				showToast(R.string.app_install_end);
				break;

			default:
				break;
			}
		}

	}

	private void sendIntent(BannerBean bean) {
		Intent intent = new Intent(getActivity(), ShowPageActivity.class);
		intent.putExtra("title", bean.getBannerTitle());
		intent.putExtra("url", bean.getContentUrl());
		startActivity(intent);
	}

//	private void displayDuansView() {
//		/* display nick & content */
//		duan_nick.setText(duanBean.getNick());
//		String content = duanBean.getContent();
//		if (!StringUtils.isBlank(content)) {
//			duan_content.setText(content);
//		} else {
//			duan_content.setVisibility(View.GONE);
//		}
//
//		/* display avatar */
//		String avatarUrl = duanBean.getAvatarUrl();
//		if (!StringUtils.isBlank(avatarUrl)) {
//			getBitmapUtils().display(duan_avatar, avatarUrl,
//					new CustomAvatarLoadCallback());
//		} else {
//			duan_avatar.setVisibility(View.GONE);
//		}
//
//		/* display iv & gif */
//		String imgUrl = duanBean.getImgUrl();
//		if (!StringUtils.isBlank(imgUrl)) {
//			int index = imgUrl.lastIndexOf(".");
//			if (imgUrl.substring(index + 1).equalsIgnoreCase("gif")) {
//				duan_picture.setVisibility(View.GONE);
//				duan_gif.setVisibility(View.VISIBLE);
//				duan_pw.setVisibility(View.VISIBLE);
//
//				setGifViewHeight(duan_gif, GIF_HEIGHT);
//
//				displayGif(imgUrl, duan_gif);
//			} else {
//				duan_picture.setVisibility(View.VISIBLE);
//				duan_gif.setVisibility(View.GONE);
//
//				setIVHeight(duan_picture, DUAN_IV_HEIGHT);
//
//				getBitmapUtils().display(duan_picture, imgUrl, new CustomBitmapLoadCallBack(getActivity()));
//			}
//		} else {
//			duan_rl.setVisibility(View.GONE);
//		}
//	}
//
//	private void displayGif(String url, GifImageView gifView) {
//		String path = gifUtils.getDiskCachePath();
//		String fileName = gifUtils.getFileName(url);
//		try {
//			FileUtils.createDir(path);
//			File file = new File(path + File.separator + fileName);
//			// LogUtils.e("path=" + path + ", fileName=" + fileName
//			// + ", AbsolutePath=" + file.getAbsolutePath());
//			if (file.exists()) {
//				// LogUtils.e("111111 file.exists=" + file.exists());
//				duan_pw.setVisibility(View.GONE);
//				GifDrawable dr = new GifDrawable(file);
//				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) gifView
//						.getLayoutParams();
//				int screenWidth = getResources().getDisplayMetrics().widthPixels;
//				params.width = screenWidth;
//				params.height = (int) (dr.getIntrinsicHeight() * screenWidth / (float) dr
//						.getIntrinsicWidth());
//				gifView.setLayoutParams(params);
//				gifView.setImageDrawable(dr);
//			} else {
//				// LogUtils.e("222222 file.exists=" + file.exists());
//				gifView.setImageDrawable(null);
//
//				file.createNewFile();
//				startDownFile(url, file.getAbsolutePath(), gifView);
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	private void startDownFile(final String url, String filePath,
//			final GifImageView gifView) {
//		HttpUtils httpUtils = new HttpUtils();
//		httpUtils.download(url, filePath, new RequestCallBack<File>() {
//
//			@Override
//			public void onStart() {
//				duan_pw.resetCount();
//				duan_pw.setProgress(0);
//			}
//
//			@Override
//			public void onLoading(long total, long current, boolean isUploading) {
//				float rate = current / (float) total;
//				duan_pw.setProgress((int) (360 * rate));
//				duan_pw.setText(((int) (100 * rate)) + "%");
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<File> responseInfo) {
//				duan_pw.setProgress(360);
//				duan_pw.setVisibility(View.GONE);
//				String filePath = responseInfo.result.getAbsolutePath();
//				try {
//					GifDrawable dr = new GifDrawable(filePath);
//					RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) gifView
//							.getLayoutParams();
//					int screenWidth = getResources().getDisplayMetrics().widthPixels;
//					params.width = screenWidth;
//					params.height = (int) (dr.getIntrinsicHeight() * screenWidth / (float) dr.getIntrinsicWidth());
//					gifView.setLayoutParams(params);
//					gifView.setImageDrawable(dr);
//				} catch (IOException e) {
//					e.printStackTrace();
//					return;
//				}
//			}
//
//			@Override
//			public void onFailure(HttpException error, String msg) {
//			}
//		});
//	}

//	private void displayArticleView() {
//		/* display name */
//		String articleName = articleBean.getArticleName();
//		if (!StringUtils.isBlank(articleName)) {
//			frag4_content.setText(articleName);
//		} else {
//			frag4_content.setVisibility(View.GONE);
//		}
//
//		/* display picture */
//		String imgUrl = articleBean.getImgUrl();
//		if (!StringUtils.isBlank(imgUrl)) {
//			getBitmapUtils().display(frag4_picture, imgUrl, new CustomBitmapLoadCallBack(getActivity()));
//		} else {
//			setIVHeight(frag4_picture, FRAG4_IV_HEIGHT);
//			frag4_picture.setImageDrawable(null);
//		}
//
//		/* set play listener */
//		frag4_rl.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				playVideo();
//			}
//		});
//	}
	
	private OnPlayVideoListener videoListener = new OnPlayVideoListener() {
		
		@Override
		public void playVideo() {
			startPlayVideo();
		}
	};

	private void startPlayVideo() {
		if (articleBean.getUrlType() == 1) {
			Intent intent = new Intent(getActivity(), VVActivity.class);
			intent.putExtra("title", articleBean.getArticleName());
			intent.putExtra("url", articleBean.getUrl());
			startActivity(intent);
			new ActivityAnimator().pushLeftAnimation(getActivity());
		}
	}

	private void setGifViewHeight(GifImageView gifView, int height) {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) gifView
				.getLayoutParams();
		params.width = getResources().getDisplayMetrics().widthPixels;
		params.height = height;
		gifView.setLayoutParams(params);
	}

	private void setIVHeight(ImageView iv, int height) {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) iv
				.getLayoutParams();
		params.width = getResources().getDisplayMetrics().widthPixels;
		params.height = height;
		iv.setLayoutParams(params);
	}

	private class FragHandler extends Handler {

		public FragHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CLEAR_COMMENT_REQUEST:
				myHandler.removeCallbacks(commentRunnable);
				break;

			default:
				break;
			}
		}

	}

	private Runnable commentRunnable = new Runnable() {

		@Override
		public void run() {
			// getCommentFromDb();
			getCommentFromHttp();
		}

	};
	
	private void getCommentFromDb() {
		LogUtils.e("thread.name=" + Thread.currentThread().getName());
		/* 1. get list from db */
		LogUtils.d("appid=" + appid + ", page=" + page + ", contentType="
				+ contentType + ", contentId=" + contentId);
		try {
			List<CommentBean> _list = getDb().findAll(
					Selector.from(CommentBean.class)
							.where("appid", "=", appid)
							.and("contentId", "=", contentId)
							.and("contentType", "=", contentType)
							.and("page", "=", page));

			/* 注意发送请求获取数据 */
			if ((_list == null) || _list.size() == 0) {
				if (currentStatus != FIRST_IN) {
					initParamsAndSendRequest(false);
				}
				return;
			} else {
				adapter.addComments(_list);
				if ((currentStatus == FIRST_IN) && (adapter != null)) {
					mHandler.sendEmptyMessage(FRESH_LISTVIEW);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
			myHandler.sendEmptyMessage(CLEAR_COMMENT_REQUEST);
			return;
		}

		/* 2. check update */
		checkUpdate(true);
	}

	private void checkUpdate(boolean b) {
		initParamsAndSendRequest(true);
	}

	private void getCommentFromHttp() {
		initParamsAndSendRequest(false);
	}

	private void initParamsAndSendRequest(boolean checkUpdate) {
		LogUtils.e("thread.name=" + Thread.currentThread().getName());
		int _page = page;
		if (currentStatus == MANUAL_FRESH) {
			_page = 1;
		}
		/* 发送请求 */
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.KEY_APPID, appid + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_CONTENT_TYPE, contentType + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_CONTENTID, contentId + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_PAGE, _page + ""));
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(nvps);
		new HttpUtils().send(HttpRequest.HttpMethod.POST, Constants.ACTION_GET_COMMENTS, params,
				new CommentRequestCallback(checkUpdate));
	}

	private class CommentRequestCallback extends RequestCallBack<String> {

		private boolean checkUpdate;

		public CommentRequestCallback(boolean checkUpdate) {
			this.checkUpdate = checkUpdate;
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			showToast(R.string.no_service);
			if (currentStatus == PULL_UP) {
				listView.onLoadMoreComplete();
			} else if (currentStatus == MANUAL_FRESH) {
				visibility.freshFinish();
			}

			myHandler.removeCallbacks(commentRunnable);
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parseResult(responseInfo.result, checkUpdate);
		}

		private void parseResult(String result, boolean checkUpdate) {
			LogUtils.e(result);
			/* 1. 从服务器响应中获取相应数据，也可保存在本地数据库中 */
			JSONObject jsonObject = null;
			try {
				jsonObject = JSON.parseObject(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (jsonObject == null) {
				LogUtils.e("jsonObject==null");
				return;
			}

			int code = jsonObject.getIntValue("code");
			if (code == 0) {	/* 表示可以正常读取数据 */
				/* 读取“hasNew”字段，判断是否有版本更新 */
				boolean hasNew = jsonObject.getBooleanValue("hasNew");
				LogUtils.e("hasNew=" + hasNew + ", checkUpdate=" + checkUpdate);
				if (!hasNew) {	/* 没有版本更新 */
					switch (currentStatus) {
					case FIRST_IN:
					default:
						break;

					case PULL_UP:
						LogUtils.e("thread.name=" + Thread.currentThread().getName());
						mHandler.sendEmptyMessage(FRESH_LISTVIEW);
						if (checkUpdate) {
							/* change listView status */
							listView.onLoadMoreComplete();
							setMoveUp();
						}
						break;
						
					case MANUAL_FRESH:
						visibility.freshFinish();
						break;
					}
				} else {
					LogUtils.e("get data from Server");

					JSONArray jsonArray = jsonObject.getJSONArray("comments");
					/* 处理上拉加载没有获取到内容时 */
					if (jsonArray.size() == 0) {
						if (currentStatus == PULL_UP) {
							/* 当执行上拉操作时，自动将curPage--，设置成先前的状态，保证准确性 */
							LogUtils.e("parseResult PULL_UP");
							page--;
							listView.onLoadMoreComplete();
							showToast(R.string.no_more);

							myHandler.sendEmptyMessage(CLEAR_COMMENT_REQUEST);
							return;
						} else if (currentStatus == MANUAL_FRESH) {
							visibility.freshFinish();
							
							myHandler.sendEmptyMessage(CLEAR_COMMENT_REQUEST);
							return;
						} else if (currentStatus == FIRST_IN) {
							page--;
							addComments();
							
							myHandler.sendEmptyMessage(CLEAR_COMMENT_REQUEST);
							return;
						}
					}

					/* 开始解析数据 */
					List<CommentBean> list = new ArrayList<CommentBean>();
					for (int i = 0; i < jsonArray.size(); i++) {
						CommentBean bean = new CommentBean();
						JSONObject jsonObject2 = jsonArray.getJSONObject(i);
						bean.setCommentId(jsonObject2.getString("id"));
						bean.setUserNick(jsonObject2.getString("userNick"));
						bean.setUserType(jsonObject2.getIntValue("userType"));
						bean.setUserVar(jsonObject2.getString("userVar"));
						bean.setIp(jsonObject2.getString("ip"));
						bean.setCreateTime(jsonObject2.getString("createTime"));
						bean.setComment(jsonObject2.getString("comment"));
						bean.setGoodCount(jsonObject2.getIntValue("goodCount"));
						bean.setBadCount(jsonObject2.getIntValue("badCount"));
						bean.setContentType(contentType);
						bean.setAppid(appid);
						bean.setPage(page);
						bean.setContentId(contentId);
						list.add(bean);
					}

					/* 同步适配器 */
					LogUtils.e("parseResult list.size=" + list.size());
					adapter.addComments(list);
					mHandler.sendEmptyMessage(FRESH_LISTVIEW);
					changeFinishState();
				}
			} else {/* 出现其它异常 */
				showToast(jsonObject.getString("dsc") + "");
				if (currentStatus == PULL_UP) {
					listView.onLoadMoreComplete();
				} else if (currentStatus == MANUAL_FRESH) {
					visibility.freshFinish();
				}
			}

			myHandler.sendEmptyMessage(CLEAR_COMMENT_REQUEST);
		}

	}

	public void addComments() {
		List<CommentBean> list = new ArrayList<CommentBean>();
		for (int i = 0; i < 10; i++) {
			CommentBean bean = new CommentBean();
			bean.setCommentId(String.valueOf(i + 1));
			bean.setUserNick("joke" + (i + 1));
			bean.setUserType(0);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			bean.setCreateTime(df.format(new Date(System.currentTimeMillis())));
			bean.setContentType(3);
			bean.setAppid(appid);
			list.add(bean);
		}
		adapter.addComments(list);
		mHandler.sendEmptyMessage(FRESH_LISTVIEW);
	}

	private void changeFinishState() {
		switch (currentStatus) {
		case PULL_UP:
			listView.onLoadMoreComplete();
			setMoveUp();
			break;
			
		case MANUAL_FRESH:
			visibility.freshFinish();
			break;

		case FIRST_IN:
		default:
			break;
		}
	}

	protected void setMoveUp() {
		mHandler.sendEmptyMessage(MOVE_UP);
	}

	private class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {
		
		private Context context;

		public CustomBitmapLoadCallBack(Context context) {
			super();
			this.context = context;
		}

		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			float scaleWidth = context.getResources().getDisplayMetrics().widthPixels / (float) width;
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) container
					.getLayoutParams();
			params.width = getResources().getDisplayMetrics().widthPixels;
			params.height = (int) (height * scaleWidth);
			container.setLayoutParams(params);
			container.setImageBitmap(bitmap);
		}

	}

	private class CustomAvatarLoadCallback extends DefaultBitmapLoadCallBack<ImageView> {

		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			Bitmap displayBitmap = ImageUtils.toRoundCorner(bitmap, 10);
			container.setImageBitmap(displayBitmap);
		}

	}
	
	private IBottomVisibility visibility;
	public interface IBottomVisibility {
		public void setBottomVisibility();
		public void freshFinish();
	}
	
	public void setIBottomVisibility(IBottomVisibility visibility) {
		this.visibility = visibility;
	}

	public void refreshList() {
		LogUtils.d("refreshList ==========");
		if (null != adapter) {
			adapter.clear();
			currentStatus = MANUAL_FRESH;
			myHandler.post(commentRunnable);
		}
	}

}
