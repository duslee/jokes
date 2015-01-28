package com.ly.duan.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.charon.pulltorefreshlistview.PullRefreshAndLoadMoreListView;
import com.charon.pulltorefreshlistview.PullRefreshAndLoadMoreListView.OnLoadMoreListener;
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ly.duan.adapter.DropListAdapter;
import com.ly.duan.adapter.DropListAdapter.DropItemHolder;
import com.ly.duan.adapter.DropListAdapter.OnDownloadOrOpenListener;
import com.ly.duan.adapter.DropListAdapter.OnDropItemOperListerner;
import com.ly.duan.bean.BannerBean;
import com.ly.duan.bean.ColumnBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.bean.MultiReqStatus;
import com.ly.duan.help.GlobalHelp;
import com.ly.duan.service.InitDataService;
import com.ly.duan.ui.ShowPageActivity;
import com.ly.duan.user_inter.DownloadUtils;
import com.ly.duan.user_inter.IDownload;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.DeviceUtils;
import com.ly.duan.utils.PackageUtils;
import com.ly.duan.utils.ScreenUtils;
import com.sjm.gxdz.R;

public class Fragment1 extends BaseFragment {

	private static final int FIRST_IN = 1;
	private static final int DROP_DOWN = 2;
	private static final int PULL_UP = 3;

	private static final int FRESH_LISTVIEW = 28;
	private static final int CLEAR_BANNER_REQUEST = 29;
	private static final int CLEAR_DUANS_REQUEST = 30;
	private static final int ADD_BANNER_LIST = 31;
	private static final int MODIFY_DUANS_LIST = 32;
	private static final int UPDATE_DROP_ITEM = 33;
	private static final int MOVE_UP = 34;

	@ViewInject(R.id.dropDownListView)
	private PullRefreshAndLoadMoreListView dropDownListView;

	@ViewInject(R.id.fresh)
	private ImageView fresh;

	private DropListAdapter adapter = null;

	private long appid = 30;
	private long columnId;
	private int ver;
	private int first = 0;
	private int max = 0;
	private int curPage = 0;
	private int pageSize = 12;

	private boolean insertAds = false;
	private List<BannerBean> bannerList = new ArrayList<BannerBean>();

	private int currentStatus = FIRST_IN;

	private boolean freshEnabled = false;

	private boolean isFirst = false;

	private FragHandler myHandler;
	private HandlerThread myThread;

	public static Fragment1 newInstance(boolean insertAds) {
		Fragment1 fragment = new Fragment1();
		Bundle args = new Bundle();
		args.putBoolean("insertAds", insertAds);
		fragment.setArguments(args);
		return fragment;
	}

	public static Fragment1 newInstance(boolean insertAds, boolean first) {
		LogUtils.e("11111 newInstance");
		Fragment1 fragment = new Fragment1();
		Bundle args = new Bundle();
		args.putBoolean("insertAds", insertAds);
		args.putBoolean("first", first);
		fragment.setArguments(args);
		return fragment;
	}
	
	public static Fragment1 newInstance(boolean insertAds, boolean first, 
			ColumnBean bean) {
		LogUtils.e("11111 newInstance");
		Fragment1 fragment = new Fragment1();
		Bundle args = new Bundle();
		args.putBoolean("insertAds", insertAds);
		args.putBoolean("first", first);
		args.putLong("appid", bean.getAppid());
		args.putLong("columnId", bean.getColumnId());
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogUtils.e("22222 onCreate");
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	public void onDestroy() {
		if (null != myThread) {
			myThread.quit();
		}
		super.onDestroy();
	}

	private void initData() {
		/* 1. get data */
		if (getArguments() != null) {
			insertAds = getArguments().getBoolean("insertAds");
//			if (insertAds) {
//				// TODO: modify
////				columnId = 83;
//				columnId = 4;
//			} else {
//				// TODO: modify
////				columnId = 84;
//				columnId = 4;
//			}
			appid = getArguments().getLong("appid");
			columnId = getArguments().getLong("columnId");
			isFirst = getArguments().getBoolean("first");
		}
//		appid = Long.parseLong(ResourceUtils.getFileFromAssets(getActivity(),
//				"appid.txt"));

		/* 2. start HandlerThread */
		myThread = new HandlerThread("My Thread " + columnId);
		myThread.start();
		myHandler = new FragHandler(myThread.getLooper());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		LogUtils.e("33333 onCreateView");
		View view = inflater.inflate(R.layout.view_frag1, container, false);
		ViewUtils.inject(this, view);

		setListener();

		setEmptyTv();

		baseInit();

		return view;
	}

	private void baseInit() {
		/* 1 get current request status */
		MultiReqStatus reqStatus = GlobalHelp.getInstance().getMultiReqStatus();
		int content1Status = InitDataService.CONTENT1_REQUESTING;
		int content2Status = InitDataService.CONTENT2_REQUESTING;
		int currentStatus = InitDataService.CONTENT_REQUESTING;
		int bannerStatus = InitDataService.BANNER_REQUESTING;
		if (null != reqStatus) {
			currentStatus = reqStatus.getCurrentStatus();
			content1Status = reqStatus.getContent1Status();
			content2Status = reqStatus.getContent2Status();
			bannerStatus = reqStatus.getBannerStatus();
		}

		/* 2. handle data */
		if (insertAds) { /* in tab1 */
			if (currentStatus == InitDataService.CONTENT_REQUEST_FINISH) {
				LogUtils.e("(null == adapter)=" + (null == adapter));
				if ((null == adapter) || (adapter.getCount() == 0)) {
					myHandler.post(contentRunnable);
				}
			}
			
			if (content1Status == InitDataService.CONTENT1_REQUEST_FINISH) {
				LogUtils.e("(null == adapter)=" + (null == adapter));
				if ((null == adapter) || (adapter.getCount() == 0)) {
					myHandler.post(contentRunnable);
				}
			}

			if (bannerStatus == InitDataService.BANNER_REQUEST_FINISH) {
				LogUtils.e("(null == bannerList)=" + (null == bannerList));
				if ((null == bannerList) || (bannerList.size() == 0)) {
					myHandler.post(bannerRunnable);
				}
			}
		} else { /* in tab2 */
			if (currentStatus == InitDataService.CONTENT_REQUEST_FINISH) {
				if ((null == adapter) || (adapter.getCount() == 0)) {
					myHandler.post(contentRunnable);
				}
			}
			
			if (content2Status == InitDataService.CONTENT2_REQUEST_FINISH) {
				if ((null == adapter) || (adapter.getCount() == 0)) {
					myHandler.post(contentRunnable);
				}
			}
		}
	}

	private void setEmptyTv() {
		TextView tv = new TextView(getActivity());
		tv.setGravity(Gravity.CENTER);
		tv.setText(R.string.fresh2);
		dropDownListView.setEmptyView(tv);
	}

	@OnClick(R.id.fresh)
	public void freshClicked(View view) {
		if (freshEnabled) {
			return;
		}

		LogUtils.e("start fresh");
		freshEnabled = true;
		/* set rotate animation */
		// if (getRotateAnimation() != null) {
		// fresh.startAnimation(getRotateAnimation());
		// }
		fresh.clearAnimation();
		fresh.startAnimation(AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_clockwise));

		// first = 0;
		// max = pageSize;
		// getContentFromHttpOrDb(DROP_DOWN);
		currentStatus = DROP_DOWN;
		myHandler.post(contentRunnable);
	}

	private void setListener() {
		adapter = new DropListAdapter(getActivity(), insertAds);
		dropDownListView.setAdapter(adapter);

		if (insertAds) {
			adapter.setDownloadListener(listener);
		}
		
		/* set OnDropItemOperListerner */
		adapter.setDropItemListener(itemListener);

		// dropDownListView.setXListViewListener(this);
		// dropDownListView.setPullLoadEnable(true);
		// dropDownListView.setPullRefreshEnable(true);
		//
		// setRefreshTime();

		// Drop down
		dropDownListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				currentStatus = DROP_DOWN;
				first = 0;
				max = pageSize;
				myHandler.post(contentRunnable);
			}
		});

		// Pull up
		dropDownListView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				currentStatus = PULL_UP;
				curPage++;
				first = curPage * pageSize;
				max = pageSize;
				myHandler.post(contentRunnable);
			}
		});

	}

	public void parseResult(int status, String result) {
		LogUtils.e(result);

		/* 从服务器响应中获取相应数据，也可保存在本地数据库中 */
		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(result);
			int code = jsonObject.getIntValue("code");
			if (code == 0) {/* 表示可以正常读取数据 */
				/* 使用hasNew的标记来处理上拉加载和下拉刷新两种情况 */
				boolean hasNew = jsonObject.getBooleanValue("hasNew");
				if (!hasNew) {
					switch (status) {
					/* 处理下拉刷新情况（没有数据可以刷新） */
					case DROP_DOWN: {
						showToast(R.string.no_fresh);
						changeDropDownFinishStatus();
						return;
					}

					/* 第一次进来时的加载数据操作情况 */
					case FIRST_IN:
						/* 处理上拉加载情况 */
					case PULL_UP:
						/* 直接从数据库中获取数据 */
						LogUtils.e("get data from DB");
						getListFromDb();
						break;
					}
				} else {
					LogUtils.e("get data from Server");
					switch (status) {
					/* 处理下拉刷新情况 */
					case DROP_DOWN:
						/* 处理第一次进来时的加载数据操作情况 */
					case FIRST_IN:
						/* 有版本更新，列表清空，同时数据库也清空，解析数据并绑定到适配器中 */
						/* 特别注意curPage要重置为0，以便下次可以保证上拉加载获取数据的准确性 */
						clearListAndDb();
						break;

					/* 处理上拉加载情况 */
					case PULL_UP: {
						/*
						 * 根据版本号来判断（注意这里的ver有0和非0（严格来说大于0）两种情况，0
						 * 表示从数据库中未搜索到对应页的内容时返回的版本号
						 * ，也可以理解为当前要加载的页还没有从服务器获取；非0表示本地数据库有备份）
						 */
						if (ver == 0) {/* 可以直接往list添加从服务器获取的列表内容 */
							/* 解析数据的操作方法体在外面 */
						} else {
							/* 有版本更新，列表清空，同时数据库也清空，重新解析数据并绑定到适配器中 */
							/* 特别注意curPage要重置为0，以便下次可以保证上拉加载获取数据的准确性 */
							clearListAndDb();
						}
					}
						break;
					}

					/* 开始解析数据 */
					int _ver = jsonObject.getIntValue("ver");
					JSONArray jsonArray = jsonObject.getJSONArray("jokes");
					/* 处理上拉加载没有获取到内容时 */
					if (jsonArray.size() == 0) {
						if (status == PULL_UP) {
							/* 当执行上拉操作时，自动将curPage--，设置成先前的状态，保证准确性 */
							curPage--;
							// stopLoad();
							// dropDownListView.stopLoadMore();
							dropDownListView.onLoadMoreComplete();
							showToast(R.string.no_more);
							return;
						}
					}
					List<DuanBean> list = new ArrayList<DuanBean>();
					for (int i = 0; i < jsonArray.size(); i++) {
						DuanBean bean = new DuanBean();
						JSONObject jsonObject2 = jsonArray.getJSONObject(i);
						int id = jsonObject2.getIntValue("id");
						bean.setId(id);
						bean.setNick(jsonObject2.getString("userName"));
						bean.setAvatarUrl(jsonObject2.getString("userVar"));
						bean.setContent(jsonObject2.getString("jokeDesc"));
						bean.setImgUrl(jsonObject2.getString("imgUrl"));
						bean.setVip(jsonObject2.getBoolean("vip"));
						bean.setVer(_ver);
						bean.setAppid(appid);
						bean.setColumnId(columnId);
						bean.setCurPage(curPage);
						list.add(bean);
					}

					// TODO: 将数据保存到数据库中
					try {
						getDb().saveAll(list);
					} catch (DbException e) {
						e.printStackTrace();
						return;
					}

					/* 同步适配器 */
					LogUtils.e("list.size=" + list.size());
					adapter.addDuans(list);
				}
				// adapter.notifyDataSetChanged();
				mHandler.sendEmptyMessage(FRESH_LISTVIEW);
				// changeFinishState(status);
			} else {/* 出现其它异常 */
				showToast(jsonObject.getString("dsc") + "");
			}
			changeFinishState(status);
		} catch (Exception e) {
			e.printStackTrace();
			changeFinishState(status);
			return;
		}
	}

	private void changeDropDownFinishStatus() {
		if (freshEnabled) {
			freshEnabled = false;
			fresh.clearAnimation();
		} else {
			// setRefreshTime();
		}
		// dropDownListView.stopRefresh();
		dropDownListView.onRefreshComplete();
	}

	// private void setRefreshTime() {
	// SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	// dropDownListView.setRefreshTime(dateFormat.format(new Date()));
	// }

	private void changeFinishState(int status) {
		LogUtils.e("freshEnabled=" + freshEnabled);
		switch (status) {
		case PULL_UP:
			// setMoveUp();
			// dropDownListView.onBottomComplete();
			// dropDownListView.stopLoadMore();
			dropDownListView.onLoadMoreComplete();
			break;

		case DROP_DOWN:
			changeDropDownFinishStatus();
			break;

		case FIRST_IN:
			// dropDownListView.setDropDownStyle(true);
			// dropDownListView.setOnBottomStyle(true);
			// dropDownListView.setAutoLoadOnBottom(true);
			break;
		}
	}

	protected void setMoveUp() {
		mHandler.sendEmptyMessage(MOVE_UP);
	}

	private void clearListAndDb() {
		curPage = 0;
		adapter.clear();
		// duanList.clear();
		try {
			getDb().delete(
					DuanBean.class,
					WhereBuilder.b("appid", "=", appid).and("columnId", "=",
							columnId));
			// long count =
			// getDb().count(Selector.from(DuanBean.class).where(WhereBuilder.b("appid",
			// "=", appid)
			// .and("columnId", "=", columnId)));
			// LogUtils.e("count=" + count);
		} catch (DbException e) {
			e.printStackTrace();
			return;
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FRESH_LISTVIEW:
				adapter.notifyDataSetChanged();
				break;

			case ADD_BANNER_LIST:
				if ((null != adapter) && (adapter.getCount() > 0)) {
					adapter.addBanners(bannerList);
					adapter.notifyDataSetChanged();
				} else {
					adapter.addBanners(bannerList);
					adapter.notifyDataSetChanged();
				}
				break;
				
			case MOVE_UP:
				dropDownListView.smoothScrollBy(ScreenUtils.dpToPxInt(getActivity(), 30), 400);
				break;

			case MODIFY_DUANS_LIST:
				if ((null != adapter) && (adapter.getCount() > 0)) {
					adapter.changeDuansList((DuanBean) msg.obj);
				}
				break;
				
			case UPDATE_DROP_ITEM:
				final DropItemHolder holder = (DropItemHolder) msg.obj;
				if (msg.arg1 == Constants.TYPE_APPROVE) {
					/* change status */
					holder.up_ll.setBackgroundResource(R.drawable.approve_press_bg);
					holder.up_iv.setImageResource(R.drawable.up_focus);
					holder.up_tv.setTextColor(getActivity().getResources()
							.getColor(R.color.up_down_focus_color));
					
					holder.down_ll.setBackgroundResource(R.drawable.disapprove_press_bg);
					holder.down_iv.setImageResource(R.drawable.down_focus);
					holder.down_tv.setTextColor(getActivity().getResources()
							.getColor(R.color.up_down_focus_color));
					
					/* change content */
					holder.up_tv.setText(msg.arg2 + "");
					/* animation */
					holder.add_tv1.setVisibility(View.VISIBLE);
					holder.add_tv1.clearAnimation();
					Animation animation = AnimationUtils.
							loadAnimation(getActivity(), R.anim.fade_in);
					animation.setAnimationListener(new MyAnimationListener(getActivity(), holder.add_tv1, false));
					holder.add_tv1.startAnimation(animation);
				} else if (msg.arg1 == Constants.TYPE_STAMP) {
					/* change status */
					holder.up_ll.setBackgroundResource(R.drawable.approve_press_bg);
					holder.up_iv.setImageResource(R.drawable.up_focus);
					holder.up_tv.setTextColor(getActivity().getResources()
							.getColor(R.color.up_down_focus_color));
					
					holder.down_ll.setBackgroundResource(R.drawable.disapprove_press_bg);
					holder.down_iv.setImageResource(R.drawable.down_focus);
					holder.down_tv.setTextColor(getActivity().getResources()
							.getColor(R.color.up_down_focus_color));
					
					/* change content */
					holder.down_tv.setText(msg.arg2 + "");
					/* animation */
					holder.add_tv2.setVisibility(View.VISIBLE);
					holder.add_tv2.clearAnimation();
					Animation animation = AnimationUtils.
							loadAnimation(getActivity(), R.anim.fade_in);
					animation.setAnimationListener(new MyAnimationListener(getActivity(), holder.add_tv2, false));
					holder.add_tv2.startAnimation(animation);
				}
				break;
				
			default:
				break;
			}
		}

	};
	
	public static class MyAnimationListener implements AnimationListener {
		
		private Context mContext;
		private TextView tv;
		private boolean exit;

		public MyAnimationListener(Context context, TextView tv, boolean exit) {
			this.mContext = context;
			this.tv = tv;
			this.exit = exit;
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			tv.clearAnimation();
			if (!exit) {
				Animation animation2 = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
				animation2.setAnimationListener(new MyAnimationListener(mContext, tv, true));
				tv.startAnimation(animation2);
			} else {
				tv.setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}

	private OnDownloadOrOpenListener listener = new OnDownloadOrOpenListener() {

		@Override
		public void startDownloadOrOpen(final BannerBean bean) {

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
				pi.setPushAppID(appid + "");
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
	};

	protected void insertBanners() {
		LogUtils.e("insert banners, size=" + bannerList.size());
		adapter.addBanners(bannerList);
	}

	protected void sendIntent(BannerBean bean) {
		Intent intent = new Intent(getActivity(), ShowPageActivity.class);
		intent.putExtra("title", bean.getBannerTitle());
		intent.putExtra("url", bean.getContentUrl());
		startActivity(intent);
	}

	public void acceptNotify(int which) {
		switch (which) {
		case InitDataService.CONTENT_REQUEST_FINISH:
			currentStatus = FIRST_IN;
			myHandler.post(contentRunnable);
			break;
		
		case InitDataService.CONTENT1_REQUEST_FINISH:
			currentStatus = FIRST_IN;
			myHandler.post(contentRunnable);
			break;

		case InitDataService.CONTENT2_REQUEST_FINISH:
			currentStatus = FIRST_IN;
			myHandler.post(contentRunnable);
			break;

		case InitDataService.BANNER_REQUEST_FINISH:
			myHandler.post(bannerRunnable);
			break;

		default:
			break;
		}
	}

	private Runnable bannerRunnable = new Runnable() {

		@Override
		public void run() {
			LogUtils.e("thread.name=" + Thread.currentThread().getName());
			getBannerFromDb();
		}
	};

	protected void getBannerFromDb() {
		/* 1. get banner list from db */
		bannerList.clear();
		List<BannerBean> _list;
		try {
			_list = getDb().findAll(BannerBean.class);
		} catch (DbException e) {
			e.printStackTrace();
			/* 发送清除线程操作 */
			myHandler.sendEmptyMessage(CLEAR_BANNER_REQUEST);
			return;
		}
		LogUtils.e("getBannerFromDb (null != _list)=" + (null != _list));
		if ((null != _list) && (_list.size() > 0)) {
			bannerList.addAll(_list);
		} else {
			/* 发送清除线程操作 */
			// setBannerData();
			// mHandler.sendEmptyMessage(ADD_BANNER_LIST);

			myHandler.sendEmptyMessage(CLEAR_BANNER_REQUEST);
			return;
		}

		/* 2. 筛选未安装的APK列表 */
		List<BannerBean> installApkList = new ArrayList<BannerBean>();
		for (BannerBean bean : bannerList) {
			/* apk */
			LogUtils.e("bean.getContentType()=" + bean.getContentType());
			if (bean.getContentType() == 4) {
				String packageName = bean.getContentPackage();
				boolean isInstall = PackageUtils.isInstall(getActivity(),
						packageName);
				if (isInstall) {
					installApkList.add(bean);
				}
			}
		}

		/* remove installed apk list */
		if (installApkList.size() > 0) {
			bannerList.removeAll(installApkList);
		}

		LogUtils.e("bannerList.size=" + bannerList.size());

		/* 3. 初始化ViewFlow */
		if (bannerList.size() > 0 && (null != adapter)) {
			if (adapter.getCount() > 0) {
				adapter.addBanners(bannerList);
				// adapter.notifyDataSetChanged();
				mHandler.sendEmptyMessage(FRESH_LISTVIEW);
			} else {
				adapter.addBanners(bannerList);
			}
		}

		/* 4. 发送清除线程操作 */
		myHandler.sendEmptyMessage(CLEAR_BANNER_REQUEST);
	}

	private Runnable contentRunnable = new Runnable() {

		@Override
		public void run() {
			LogUtils.e("thread.name=" + Thread.currentThread().getName());
			getContentFromDb();
		}
	};

	private void getContentFromDb() {
		/* 1. 从数据库中获取对应版本号 */
		getVer();
		/* 2. 从数据库获取数据 */
		getListsFromDb();
	}

	/** 从数据库中获取相应列表数据 */
	private void getListsFromDb() {
		/* 1. get list from db */
		LogUtils.e("isFirst=" + isFirst + ", currentStatus=" + currentStatus);

		try {
			List<DuanBean> _list = getDb().findAll(
					Selector.from(DuanBean.class)
							.where("appid", "=", appid)
							.and("columnId", "=", columnId)
							.and("ver", "=", ver)
							.and("curPage", "=", curPage));
			LogUtils.e("(_list == null)=" + (_list == null) + ", curPage="
					+ curPage + ", currentStatus=" + currentStatus);

			/* 注意发送请求获取数据 */
			if ((_list == null) || _list.size() == 0) {
				LogUtils.e("_list.size=" + _list.size());
				if (currentStatus == FIRST_IN) {
					if (!isFirst) {
						initParamsAndSendRequest(false);
					}
				} else {
					initParamsAndSendRequest(false);
				}
				return;
			} else {
				if (currentStatus == DROP_DOWN) {
					/* do not add list, but can check version update */
				} else {
					adapter.addDuans(_list);
					if ((currentStatus == FIRST_IN) && (adapter != null)) {
						mHandler.sendEmptyMessage(FRESH_LISTVIEW);
					}
				}
				LogUtils.e("_list.size=" + _list.size() + ", adapter.size="
						+ adapter.getCount());
			}
		} catch (DbException e) {
			e.printStackTrace();
			myHandler.sendEmptyMessage(CLEAR_DUANS_REQUEST);
			return;
		}

		/* 2. check update || pop up dialog */
		if (currentStatus == FIRST_IN) {
			if (isFirst) {
			} else {
				checkVersionUpdate();
			}
		} else {
			checkVersionUpdate();
		}
	}

	private void getVer() {
		try {
			int _curPage = curPage;
			if (currentStatus == DROP_DOWN) {
				_curPage = 0;
			} else if (currentStatus == FIRST_IN) {
				_curPage = 0;
			}
			DuanBean bean = getDb().findFirst(Selector.from(DuanBean.class)
							.where("appid", "=", appid)
							.and("columnId", "=", columnId)
							.and("curPage", "=", _curPage));
			if (bean != null) {
				ver = bean.getVer();
			} else {
				ver = 0;
			}
		} catch (DbException e) {
			e.printStackTrace();
			changeDropDownFinishStatus();
			return;
		}
	}

	private void checkVersionUpdate() {
		initParamsAndSendRequest(true);
	}

	private void initParamsAndSendRequest(boolean checkUpdate) {
		/* 1. 初始化参数 */
		/* 修改分页请求时传递参数的起始值，因请求分页时已经是最后一页，若该页总数小于pageSize时，并修改下一页请求的起始值 */
		first = curPage * pageSize;
		LogUtils.e("first=" + first + ", adapter.getDuansSize()=" + adapter.getDuansSize());
		if (adapter.getDuansSize() < first) {
			first = adapter.getDuansSize();
		}
		max = pageSize;

		/* 2. 发送请求 */
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.KEY_APPID, appid + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_CLMID, columnId + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_VER, ver + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_FIRST, first + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_MAX, max + ""));
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(nvps);
		new HttpUtils().send(HttpRequest.HttpMethod.POST,
				Constants.ACTION_GET_COLUMN_CONTENT, params,
				new ContentRequestCallback(checkUpdate));
	}

	private class ContentRequestCallback extends RequestCallBack<String> {

		boolean checkUpdate;

		public ContentRequestCallback(boolean checkUpdate) {
			this.checkUpdate = checkUpdate;
		}

		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parseResult(responseInfo.result, checkUpdate);
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			showToast(R.string.no_service);
			if (currentStatus == DROP_DOWN) {
				changeDropDownFinishStatus();
			} else if (currentStatus == PULL_UP) {
				// dropDownListView.stopLoadMore();
				dropDownListView.onLoadMoreComplete();
			}

			myHandler.sendEmptyMessage(CLEAR_DUANS_REQUEST);
		}

	}

	protected void parseResult(String result, boolean checkUpdate) {
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
		if (code == 0) {/* 表示可以正常读取数据 */
			/* 读取“hasNew”字段，判断是否有版本更新 */
			boolean hasNew = jsonObject.getBooleanValue("hasNew");
			LogUtils.e("hasNew=" + hasNew + ", checkUpdate=" + checkUpdate);
			if (!hasNew) {
				/* 没有版本更新 */
				switch (currentStatus) {
				case FIRST_IN:
				default:
					break;

				case DROP_DOWN:
					// if (checkUpdate) {
					/* change listView status */
					LogUtils.e("thread.name="
							+ Thread.currentThread().getName());
					showToast(R.string.no_fresh);
					changeDropDownFinishStatus();
					// }
					break;

				case PULL_UP:
					LogUtils.e("thread.name="
							+ Thread.currentThread().getName());
					// adapter.notifyDataSetChanged();
					mHandler.sendEmptyMessage(FRESH_LISTVIEW);
					if (checkUpdate) {
						/* change listView status */
						setMoveUp();
						// dropDownListView.stopLoadMore();
						dropDownListView.onLoadMoreComplete();
					}
					break;
				}
			} else { /* 有版本更新 */
				LogUtils.e("get data from Server");

				int _ver = jsonObject.getIntValue("ver");
				int type = jsonObject.getIntValue("type");
				JSONArray jsonArray = jsonObject.getJSONArray("jokes");
				/* 处理上拉加载没有获取到内容时 */
				if (jsonArray.size() == 0) {
					if (currentStatus == PULL_UP) {
						/* 当执行上拉操作时，自动将curPage--，设置成先前的状态，保证准确性 */
						curPage--;
						// dropDownListView.stopLoadMore();
						dropDownListView.onLoadMoreComplete();
						showToast(R.string.no_more);

						myHandler.sendEmptyMessage(CLEAR_DUANS_REQUEST);
						return;
					} else if (currentStatus == DROP_DOWN) {
						showToast(R.string.no_fresh);
						changeDropDownFinishStatus();

						myHandler.sendEmptyMessage(CLEAR_DUANS_REQUEST);
						return;
					}
				}

				/* 处理获取到内容时列表或数据库清空 */
				switch (currentStatus) {
				case PULL_UP:
					if (checkUpdate) {
						clearListAndDb();
					}
					break;

				case FIRST_IN:
				case DROP_DOWN:
				default:
					clearListAndDb();
					break;
				}

				/* 开始解析数据 */
				List<DuanBean> list = new ArrayList<DuanBean>();
				for (int i = 0; i < jsonArray.size(); i++) {
					DuanBean bean = new DuanBean();
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					int id = jsonObject2.getIntValue("id");
//					bean.setId(id);
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
					bean.setCurPage(curPage);
					list.add(bean);
				}

				// TODO: 将数据保存到数据库中
				try {
					getDb().saveAll(list);
				} catch (DbException e) {
					e.printStackTrace();
					return;
				}

				/* 同步适配器 */
				LogUtils.e("parseResult list.size=" + list.size());
				adapter.addDuans(list);

				// adapter.notifyDataSetChanged();
				mHandler.sendEmptyMessage(FRESH_LISTVIEW);
				changeFinishState();
			}

			myHandler.sendEmptyMessage(CLEAR_DUANS_REQUEST);
		} else {/* 出现其它异常 */
			showToast(jsonObject.getString("dsc") + "");
			if (currentStatus == DROP_DOWN) {
				changeDropDownFinishStatus();
			} else if (currentStatus == PULL_UP) {
				// adapter.notifyDataSetChanged();
				mHandler.sendEmptyMessage(FRESH_LISTVIEW);
				// dropDownListView.stopLoadMore();
				dropDownListView.onLoadMoreComplete();
			}

			myHandler.sendEmptyMessage(CLEAR_DUANS_REQUEST);
		}
	}

	private void changeFinishState() {
		LogUtils.e("freshEnabled=" + freshEnabled);
		switch (currentStatus) {
		case PULL_UP:
			// setMoveUp();
			// dropDownListView.stopLoadMore();
			dropDownListView.onLoadMoreComplete();
			break;

		case DROP_DOWN:
			changeDropDownFinishStatus();
			break;

		case FIRST_IN:
			break;
		}
	}

	private void getListFromDb() {
		try {
			List<DuanBean> _list = getDb().findAll(
					Selector.from(DuanBean.class).where("appid", "=", appid)
							.and("columnId", "=", columnId)
							.and("ver", "=", ver).and("curPage", "=", curPage));
			LogUtils.e("_list.size=" + _list.size() + ", curPage=" + curPage);
			if ((null != _list) && (_list.size() > 0)) {
				adapter.addDuans(_list);
			}
		} catch (DbException e) {
			e.printStackTrace();
			return;
		}
	}

	private class FragHandler extends Handler {

		public FragHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FRESH_LISTVIEW:
				LogUtils.e("thread.name=" + Thread.currentThread().getName());
				break;

			case CLEAR_DUANS_REQUEST:
				myHandler.removeCallbacks(contentRunnable);
				break;

			case CLEAR_BANNER_REQUEST:
				myHandler.removeCallbacks(bannerRunnable);
				break;

			default:
				break;
			}
		}

	}
	
	private OnDropItemOperListerner itemListener = new OnDropItemOperListerner() {
		
		@Override
		public void dropItemOper(int operType, DuanBean bean, DropItemHolder holder) {
			/* 1. init params */
			String imei = DeviceUtils.getIMEI(getActivity());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("appid", bean.getAppid() + ""));
			nvps.add(new BasicNameValuePair("contentType", bean.getContentType() + ""));
			nvps.add(new BasicNameValuePair("contentId", bean.getDuanId() + ""));
			nvps.add(new BasicNameValuePair("type", operType + ""));
			nvps.add(new BasicNameValuePair("userId", imei));
			RequestParams params = new RequestParams();
			params.addQueryStringParameter(nvps);
			
			/* 2. send request */
			new HttpUtils().send(HttpMethod.POST, Constants.ACTION_APPROVE_STAMP, params, 
					new DropItemCallBack(operType, bean, holder));
		}
	};
	
	private class DropItemCallBack extends RequestCallBack<String> {
		
		private int operType;
		private DuanBean bean;
		private DropItemHolder holder;

		public DropItemCallBack(int operType, DuanBean bean,
				DropItemHolder holder) {
			this.operType = operType;
			this.bean = bean;
			this.holder = holder;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parseResult(responseInfo.result);
		}

		private void parseResult(String result) {
			LogUtils.e("result=" + result);
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
			if (code == 0) {/* 表示操作成功 */
				/* save status about good & bad */
				if (operType == Constants.TYPE_APPROVE) {
					bean.setApprove(1);
					bean.setGood(bean.getGood() + 1);
				} else if (operType == Constants.TYPE_STAMP) {
					bean.setStamp(1);
					bean.setBad(bean.getBad() + 1);
				}
				try {
					getDb().saveOrUpdate(bean);
				} catch (DbException e) {
					e.printStackTrace();
					return;
				}
				
				/* modify list in adapter */
				Message msg1 = new Message();
				msg1.what = MODIFY_DUANS_LIST;
				msg1.obj = bean;
				mHandler.sendMessage(msg1);
				
				/* update UI */
				Message msg2 = new Message();
				msg2.what = UPDATE_DROP_ITEM;
				msg2.arg1 = operType;
				int nums = 0;
				if (operType == Constants.TYPE_APPROVE) {
					nums = bean.getGood();
				} else if (operType == Constants.TYPE_STAMP) {
					nums = bean.getBad();
				}
				msg2.arg2 = nums;
				msg2.obj = holder;
				mHandler.sendMessage(msg2);
			} else { /* 出现其它异常 */
				showToast(jsonObject.getString("dsc") + "");
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			showToast(R.string.no_service);
		}
		
	}

	private void setBannerData() {
		BannerBean bean1 = new BannerBean();
		bean1.setBannerTitle("桃桃斗地主");
		bean1.setBannerImgUrl("http://ubanner.qiniudn.com/banner_28.jpg");
		bean1.setBannerDesc("怀抱四大美女斗地主，让你的夜晚不再寂寞");
		bean1.setContentPackage("com.dianfengjingji.dfddzdj");
		bean1.setContentType(4);
		bean1.setContentUrl("http://apk.boya1993.com/sdmrddz_141210.apk");
		bean1.setBannerId(28);
		bannerList.add(bean1);

		BannerBean bean2 = new BannerBean();
		bean2.setBannerTitle("超级战舰");
		bean2.setBannerImgUrl("http://7qnch9.com2.z0.glb.qiniucdn.com/banner_35.jpg");
		bean2.setBannerDesc("美女陪你打飞机，冲关有好礼，疯狂射吧");
		bean2.setContentPackage("com.ly.shiprush");
		bean2.setContentType(4);
		bean2.setContentUrl("http://apk.boya1993.com/cjzj__banner001.apk");
		bean2.setBannerId(35);
		bannerList.add(bean2);

		BannerBean bean3 = new BannerBean();
		bean3.setBannerTitle("把妹圣经");
		bean3.setBannerImgUrl("http://7qnch9.com2.z0.glb.qiniucdn.com/banner_24.jpg");
		bean3.setBannerDesc("男女夜话，让你更大更强！");
		bean3.setContentPackage("com.ly.bmsj");
		bean3.setContentType(4);
		bean3.setContentUrl("http://push-apk.qiniudn.com/bm_0001_1012.apk");
		bean3.setBannerId(24);
		bannerList.add(bean3);

		LogUtils.e("bannerList.size=" + bannerList.size());
	}

}
