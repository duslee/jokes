package com.ly.duan.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.as.base.log.BaseLog;
import com.common.as.network.HttpUtil;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushInfoActionPaser;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.AppListManager;
import com.common.as.store.AppListManager.OnListChangeListener;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppUtil;
import com.common.as.utils.PopupUtils;
import com.common.as.view.AsyncImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ly.duan.adapter.ViewFlowAdapter;
import com.ly.duan.bean.BannerBean;
import com.ly.duan.bean.MultiReqStatus;
import com.ly.duan.help.GlobalHelp;
import com.ly.duan.service.InitDataService;
import com.ly.duan.ui.ShowPageActivity;
import com.ly.duan.user_inter.DownloadUtils;
import com.ly.duan.user_inter.IDownload;
import com.ly.duan.utils.ActivityAnimator;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.PackageUtils;
import com.ly.duan.utils.StringUtils;
import com.ly.duan.view.CircleFlowIndicator;
import com.ly.duan.view.LayersLayout;
import com.ly.duan.view.ViewFlow;
import com.sjm.gxdz.R;

public class Fragment3 extends BaseFragment {

	private static final int CLEAR_BANNER_REQUEST = 83;
	private static final int CLEAR_APPLIST_REQUEST = 84;
	private static final int INIT_VIEWFLOW = 85;

	@ViewInject(R.id.listView)
	private ListView listView;

	@ViewInject(R.id.layerslayout)
	private LayersLayout layersLayout;

	private View item_vf;
	private RelativeLayout rl;
	private ViewFlow viewFlow;

	private CircleFlowIndicator indicator;

	private AdapterList mAdapterList;
	private OnListChangeListener mOnListChangeListener;
	private ArrayList<PushInfo> infos = new ArrayList<PushInfo>();
	private ArrayList<PushInfo> nouse_infos = new ArrayList<PushInfo>();
	private ProgressDialog pd;

	@ViewInject(R.id.fresh)
	private ImageView fresh;

	private List<BannerBean> bannerList = new ArrayList<BannerBean>();
	private ViewFlowAdapter vfAdapter;
	private int item;

	private boolean freshEnabled = false;

	private HandlerThread thread;
	private MyHandler myHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* init Data */
		thread = new HandlerThread("frag3 thread");
		thread.start();
		myHandler = new MyHandler(thread.getLooper());

		// /* get Banner list */
		// getBannerList();
	}

	@Override
	public void onDestroy() {
		if (thread != null) {
			thread.quit();
		}
		super.onDestroy();
	}

	private void getBannerList() {
		/* 1. get status */
		MultiReqStatus status = GlobalHelp.getInstance().getMultiReqStatus();
		int bannerStatus = InitDataService.BANNER_REQUESTING;
		if (status != null) {
			bannerStatus = status.getBannerStatus();
		}

		/* 2. get banner */
		LogUtils.e("getBannerList bannerStatus=" + bannerStatus);
		if ((bannerStatus == InitDataService.BANNER_REQUEST_FINISH)
				&& (bannerList != null) && (bannerList.size() == 0)) {
			myHandler.post(bannerRunnable);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (null != viewFlow && (bannerList.size() > 0)) {
			item = viewFlow.getSelectedItemPosition();
			if (bannerList.size() > 1) {
				viewFlow.stopAutoFlowTimer();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if ((null != viewFlow) && (bannerList.size() > 0) && item != 0) {
			LogUtils.e("onStart item=" + item);
			viewFlow.setSelection(item);
			viewFlow.startAutoFlowTimer();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_frag3, container, false);
		ViewUtils.inject(this, view);

		/* 1. init Controller */
		// item_vf = inflater.inflate(R.layout.item_viewflow, null);
		// rl = (RelativeLayout) item_vf.findViewById(R.id.tab_rl);
		// viewFlow = (ViewFlow) item_vf.findViewById(R.id.viewflow);
		// indicator = (CircleFlowIndicator)
		// item_vf.findViewById(R.id.indicator);
		// viewFlow.setFlowIndicator(indicator);
		//
		// listView.addHeaderView(item_vf);
		// layersLayout.setView(viewFlow); // 将viewFlow对象传递给自定义图层，用于对事件进行重定向
		// item_vf.setVisibility(View.GONE);

		/* 2. display Banner */
		baseInit();

		/* 3. get APP list */
		displayList();

		return view;
	}

	private void baseInit() {
		/* 1. get status */
		MultiReqStatus status = GlobalHelp.getInstance().getMultiReqStatus();
		int bannerStatus = InitDataService.BANNER_REQUESTING;
		if (status != null) {
			bannerStatus = status.getBannerStatus();
		}

		/* 2. display banner */
		LogUtils.e("baseInit (bannerList != null)=" + (bannerList != null));
		if ((bannerStatus == InitDataService.BANNER_REQUEST_FINISH)
				&& (bannerList != null) && (bannerList.size() > 0)
				&& (null == viewFlow)) {
			LogUtils.e("baseInit bannerList.size=" + bannerList.size());
			initViewFlow();
			// mHandler.sendEmptyMessage(INIT_VIEWFLOW);
		}

		// if (bannerList.size() == 0) {
		// setBannerData();
		// initViewFlow();
		// }
	}

	private void displayList() {
		infos = AppListManager.getApplists(AppListManager.FLAG_APP_LIST);
		LogUtils.e("(null != infos)=" + (null != infos));
		if (null != infos) {
			LogUtils.e("infos.size()=" + infos.size());
			if (infos.size() != 0) {
				for (PushInfo pushInfo : infos) {
					if (pushInfo.getPackageName().equals(
							getActivity().getPackageName())) {
						nouse_infos.add(pushInfo);
						// break;
					}
				}
				infos.removeAll(nouse_infos);
			}
			if (infos.size() == 0) {
				createProgressDialog(getActivity());
			}
		} else {
			createProgressDialog(getActivity());
		}

		mAdapterList = new AdapterList(infos);
		listView.setAdapter(mAdapterList);
		mOnListChangeListener = new OnListChangeListener() {

			@Override
			public void onDataChange(Object obj) {
				mAdapterList.notifyDataChanged((ArrayList<PushInfo>) obj);
				if (pd != null) {
					pd.cancel();
				}
			}
		};
		AppListManager.addListener(mOnListChangeListener);

		HttpUtil mHttpUtil = new HttpUtil(getActivity());
		HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(
				HttpUtil.KEY_STORE_LIST) {

			@Override
			public void onSuccess(int what, Object obj) {
				changeFreshFinishStatus();

				if (pd != null) {
					pd.cancel();
				}
				if (obj != null) {
					ArrayList<PushInfo> _list = (ArrayList<PushInfo>) obj;
					if (_list.size() == 0) {
						LogUtils.e("obj != null, _list.size=" + _list.size());
						mAdapterList.notifyDataSetChanged();
						return;
					}
					mAdapterList.notifyDataChanged(_list);

				} else {
					PopupUtils.showShortToast(getActivity()
							.getApplicationContext(), "no data");
				}
			}

			@Override
			public void onFailed(int what, Object obj) {
				PopupUtils.showShortToast(
						getActivity().getApplicationContext(),
						"connected failed");
				if (pd != null) {
					pd.cancel();
				}

				changeFreshFinishStatus();
			}

		};
		mHttpUtil.startRequest(mRequestData);
	}

	protected void changeFreshFinishStatus() {
		LogUtils.e("fresh finished... freshEnabled=" + freshEnabled);

		if (freshEnabled) {
			freshEnabled = false;
			fresh.clearAnimation();
		}
	}

	private void createProgressDialog(Context context) {
		BaseLog.v("main", "ItemListActivity.createProgressDialog");
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("正在获取数据...");
		pd.show();
	}

	@OnClick(R.id.fresh)
	public void freshClicked(View v) {
		if (freshEnabled) {
			return;
		}

		// LogUtils.e("fresh clicked... (rotateAnimation!=null)="
		// + (null != getRotateAnimation()));

		freshEnabled = true;
		/* set rotate animation */
		// if (getRotateAnimation() != null) {
		fresh.clearAnimation();
		fresh.startAnimation(AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_clockwise));
		// }

		displayList();
	}

	private class AdapterList extends BaseAdapter implements OnClickListener {

		private ArrayList<PushInfo> pushInfos;
		private final String packageName;

		public class ViewHolder {
			public AsyncImageView photoAsynImg;
			public TextView title;
			public TextView brief;
			public Button btn;
		}

		public AdapterList(ArrayList<PushInfo> pushInfos) {
			super();
			packageName = getActivity().getPackageName();
			if (null != pushInfos) {
				this.pushInfos = pushInfos;
			} else {
				this.pushInfos = new ArrayList<PushInfo>();
			}

		}

		public void notifyDataChanged(ArrayList<PushInfo> temps) {
			pushInfos.clear();
			pushInfos.addAll(temps);

			for (PushInfo pushInfo : pushInfos) {
				if (pushInfo.getPackageName().equals(packageName)) {
					pushInfos.remove(pushInfo);
					break;
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return pushInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return pushInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (null == convertView) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.list_down_item, null);
				viewHolder = new ViewHolder();
				viewHolder.photoAsynImg = (AsyncImageView) convertView
						.findViewById(R.id.icon);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.title);
				viewHolder.brief = (TextView) convertView
						.findViewById(R.id.brief);
				viewHolder.btn = (Button) convertView.findViewById(R.id.btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			PushInfo pi = (PushInfo) getItem(position);
			viewHolder.title.setText(pi.getAppName());
			viewHolder.photoAsynImg.setUrl(pi.getImageUrl());
			viewHolder.brief.setText(pi.getmBrief());
			viewHolder.brief.setMovementMethod(ScrollingMovementMethod
					.getInstance());
			setBtnStatus(viewHolder.btn, pi);
			return convertView;
		}

		private void setBtnStatus(Button btn, PushInfo pi) {
			if (AppUtil.isInstalled(getActivity(), pi.getPackageName())) {
				btn.setText("启动");
			} else {
				PushInfo tempPi = PushInfos.getInstance().get(
						pi.getPackageName());
				if (null != tempPi
						&& tempPi.getStatus() == PushInfo.STATUS_DOWN_FINISH
						&& tempPi.isFileExist()) {
					btn.setText("安装");
				} else {
					btn.setText("下载");
				}

			}
			btn.setTag(pi);
			btn.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			/* 首先判断APK是否正在下载或已下载完成 */
			if (!checkSdcard()) {
				return;
			}

			PushInfo pi = (PushInfo) v.getTag();
			pi.setPushType(PushType.TYPE_STORE_LIST);
			BaseLog.d("main",
					"ItemListActivity.onClick.pi=" + pi.getPackageName());
			PushInfo temp = PushInfos.getInstance().get(pi.getPackageName());

			if (temp == null) {
				PushInfos.getInstance().put(pi.getPackageName(), pi);
			} else {
				temp.setPushType(PushType.TYPE_STORE_LIST);
				PushInfos.getInstance().put(pi.getPackageName(), temp);
			}

			PushInfoActionPaser.doClick(getActivity(),
					PushType.TYPE_STORE_LIST, pi.getPackageName());
		}

	}

	/** 修改ViewFlow的布局的宽高，并添加内容 */
	protected void initViewFlow() {
		/* 1. init Controller */
		item_vf = getActivity().getLayoutInflater().inflate(
				R.layout.item_viewflow, null);
		rl = (RelativeLayout) item_vf.findViewById(R.id.tab_rl);
		viewFlow = (ViewFlow) item_vf.findViewById(R.id.viewflow);
		indicator = (CircleFlowIndicator) item_vf.findViewById(R.id.indicator);
		viewFlow.setFlowIndicator(indicator);

		listView.addHeaderView(item_vf);
		layersLayout.setView(viewFlow); // 将viewFlow对象传递给自定义图层，用于对事件进行重定向

		// item_vf.setVisibility(View.VISIBLE);

		/* 2. set ViewFlow attr */
		LogUtils.e("bannerList.size()=" + bannerList.size());
		int vfWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
		int vfHeight = (int) ((200 * getActivity().getResources()
				.getDisplayMetrics().heightPixels) / (float) 800);
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) rl
				.getLayoutParams();
		params.width = vfWidth;
		params.height = vfHeight;
		rl.setLayoutParams(params);

		/* 3. set adapter */
		vfAdapter = new ViewFlowAdapter(getActivity(), bannerList, vfWidth,
				vfHeight);
		viewFlow.setAdapter(vfAdapter);
		viewFlow.setTimeSpan(5000);
		viewFlow.setmSideBuffer(bannerList.size());
		if (bannerList.size() > 1) {
			viewFlow.startAutoFlowTimer();
		}

		/* 4. 设置ViewFlow点击事件 */
		viewFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LogUtils.e("viewflow.setOnItemClickListener: position="
						+ position + ", id=" + id);
				final BannerBean bean = bannerList.get(position);
				LogUtils.e("bean.getContentType()=" + bean.getContentType());
				switch (bean.getContentType()) {
				/* 具体内容 */
				case Constants.BANNER_TYPE_ARTICLE:
				case Constants.BANNER_TYPE_POSTBAR:
					sendIntent2(bean);

					/* 网页 */
				case Constants.BANNER_TYPE_PAGE:
					sendIntent2(bean);
					break;

				/* APK */
				case Constants.BANNER_TYPE_APK: {
					/* 首先判断APK是否正在下载或已下载完成 */
					if (!checkSdcard()) {
						return;
					}
					final String pkg = bean.getContentPackage();
					PushInfo pi = PushInfos.getInstance().get(pkg);
					if (null == pi) {
						pi = new PushInfo(pkg, (10000 + bean.getBannerId())
								+ "", "");
						pi.setAppName(bean.getBannerTitle());
						pi.setImageUrl(bean.getBannerImgUrl());
						pi.setmDownUrl(bean.getContentUrl());
						pi.setmBrief(bean.getBannerDesc());
						pi.setPushAppID(bean.getBannerId() + "");
						pi.setPushType(PushType.TYPE_STORE_LIST);
						pi.setStatus(PushInfo.STATUS_DOWN_STARTING);
						PushInfos.getInstance().put(pkg, pi);

						LogUtils.e("viewflow.onItemClick download apk!");
						/* 启动后台服务开始下载 */
						startDown(bean.getBannerTitle(), pi);

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
					break;
				}
			}
		});
	}

	protected void startDown(final String bannerTitle, PushInfo pi) {
		LogUtils.e("viewflow.onItemClick download apk!");
		/* 启动后台服务开始下载 */
		showToast("《" + bannerTitle + "》开始下载");
		DownloadUtils.getInstance().startDownload(getActivity(), pi,
				new IDownload() {

					@Override
					public void onDlSuccess() {
						showToast("《" + bannerTitle + "》下载完成");
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
	}

	protected void sendIntent2(BannerBean bean) {
		String url = bean.getContentUrl();
		LogUtils.e("url=" + url);
		if (StringUtils.isBlank(url)) {
			showToast("该内容链接地址不存在");
			return;
		}
		String subStr = url.substring(url.lastIndexOf(".") + 1);
		LogUtils.e("subStr=" + subStr);
		if (subStr.equalsIgnoreCase("html") || subStr.equalsIgnoreCase("htm")
				|| subStr.equalsIgnoreCase("com")) {
			Intent intent = new Intent(getActivity(), ShowPageActivity.class);
			intent.putExtra("title", bean.getBannerTitle());
			intent.putExtra("url", bean.getContentUrl());
			startActivity(intent);
		} else {
		}
		new ActivityAnimator().pushLeftAnimation(getActivity());
	}

	public void acceptNotify(int which) {
		if (which == InitDataService.BANNER_REQUEST_FINISH) {
			myHandler.post(bannerRunnable);
		}
	}

	private Runnable appListRunnable = new Runnable() {

		@Override
		public void run() {
			displayList();
		}
	};

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

		LogUtils.e("(null != _list)=" + (null != _list));
		if ((null != _list) && (_list.size() > 0)) {
			bannerList.addAll(_list);
		} else {
			/* 发送清除线程操作 */
			// if (bannerList.size() == 0) {
			// setBannerData();
			// }
			// if (bannerList.size() > 0 && (null == viewFlow)) {
			// initViewFlow();
			// }

			myHandler.sendEmptyMessage(CLEAR_BANNER_REQUEST);
			return;
		}

		/* 2. 筛选出未安装的APK列表 */
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

		LogUtils.e("bannerList.size=" + bannerList.size()
				+ ", (null == viewFlow)=" + (null == viewFlow));

		/* 3. 初始化ViewFlow */
		if (bannerList.size() > 0 && (null == viewFlow)) {
			initViewFlow();
			// mHandler.sendEmptyMessage(INIT_VIEWFLOW);
		}

		/* 4. 发送清除线程操作 */
		myHandler.sendEmptyMessage(CLEAR_BANNER_REQUEST);
	}

	private class MyHandler extends Handler {

		public MyHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CLEAR_BANNER_REQUEST:
				myHandler.removeCallbacks(bannerRunnable);
				break;

			case CLEAR_APPLIST_REQUEST:
				myHandler.removeCallbacks(appListRunnable);
				break;

			default:
				break;
			}
		};

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

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INIT_VIEWFLOW:
				LogUtils.e("thread.name=" + Thread.currentThread().getName());
				initViewFlow();
				break;

			default:
				break;
			}
		};

	};

}
