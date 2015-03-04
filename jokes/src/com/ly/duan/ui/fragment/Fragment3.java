package com.ly.duan.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.charon.pulltorefreshlistview.PullRefreshAndLoadMoreListView;
import com.charon.pulltorefreshlistview.PullRefreshAndLoadMoreListView.OnLoadMoreListener;
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
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
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ly.duan.adapter.ArticleAdapter;
import com.ly.duan.adapter.ArticleAdapter.OnFrag3ItemClickListener;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.bean.ColumnBean;
import com.ly.duan.bean.MultiReqStatus;
import com.ly.duan.help.GlobalHelp;
import com.ly.duan.service.InitDataService;
import com.ly.duan.ui.ShowPageActivity;
import com.ly.duan.utils.ActivityAnimator;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.ScreenUtils;
import com.sjm.gxdz.R;

public class Fragment3 extends BaseFragment {

	private static final int FIRST_IN = 1;
	private static final int DROP_DOWN = 2;
	private static final int PULL_UP = 3;
	
	private static final int FRESH_LISTVIEW = 28;
	private static final int MOVE_UP = 29;
	private static final int CLEAR_CONTENT_REQUEST = 30;
	
	@ViewInject(R.id.dropDownListView)
	private PullRefreshAndLoadMoreListView listView;

	@ViewInject(R.id.fresh)
	private ImageView fresh;
	
	private ArticleAdapter adapter = null;
	
	private long appid = 30;
	private long columnId;
	private int ver;
	private int first = 0;
	private int max = 0;
	private int curPage = 0;
	private int pageSize = 12;
	
	private int currentStatus = FIRST_IN;

	private boolean freshEnabled = false;

	private boolean isFirst = false;

	private Frag3Handler myHandler;
	private HandlerThread myThread;

	public static Fragment3 newInstance(boolean first, ColumnBean bean) {
		Fragment3 fragment = new Fragment3();
		Bundle args = new Bundle();
		args.putBoolean("first", first);
		args.putLong("appid", bean.getAppid());
		args.putLong("columnId", bean.getColumnId());
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
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
			appid = getArguments().getLong("appid");
			columnId = getArguments().getLong("columnId");
			isFirst = getArguments().getBoolean("first");
		}

		/* 2. start HandlerThread */
		if (null == myHandler) {
			initHandler();
		}
	}
	
	private void initHandler() {
		myThread = new HandlerThread("My Thread " + columnId);
		myThread.start();
		myHandler = new Frag3Handler(myThread.getLooper());		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_frag3, container, false);
		ViewUtils.inject(this, view);
		
		setListener();

		setEmptyTv();

		baseInit();
		
		return view;
	}

	private void setListener() {
		/* init adapter */
		adapter = new ArticleAdapter(getActivity());
		listView.setAdapter(adapter);
		
		/* set item click listener */
		adapter.setFrag3ItemClicked(itemListener);
		
		// Drop down
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				currentStatus = DROP_DOWN;
				first = 0;
				max = pageSize;
				myHandler.post(contentRunnable);
			}
		});

		// Pull up
		listView.setOnLoadMoreListener(new OnLoadMoreListener() {

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

	private void setEmptyTv() {
		TextView tv = new TextView(getActivity());
		tv.setGravity(Gravity.CENTER);
		tv.setText(R.string.fresh2);
		listView.setEmptyView(tv);
	}

	private void baseInit() {
		/* 1 get current request status */
		MultiReqStatus reqStatus = GlobalHelp.getInstance().getMultiReqStatus(getActivity());
		int currentStatus = InitDataService.CONTENT_REQUESTING;
		if (null != reqStatus) {
			currentStatus = reqStatus.getCurrentStatus();
		}
		
		/* 2. handle data */
		if (currentStatus == InitDataService.CONTENT_REQUEST_FINISH) {
			LogUtils.e("(null == adapter)=" + (null == adapter));
			if ((null == adapter) || (adapter.getCount() == 0)) {
				myHandler.post(contentRunnable);
			}
		}
	}
	
	@OnClick(R.id.fresh)
	public void freshClicked(View view) {
		if (freshEnabled) {
			return;
		}

		LogUtils.e("start fresh");
		freshEnabled = true;
		fresh.clearAnimation();
		fresh.startAnimation(AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_clockwise));

		currentStatus = DROP_DOWN;
		myHandler.post(contentRunnable);
	}

	public void acceptNotify(int which) {
		/* strategy of protect */
		if (null == myHandler) {
			initHandler();
		}
		
		switch (which) {
		case InitDataService.CONTENT_REQUEST_FINISH:
			currentStatus = FIRST_IN;
			myHandler.post(contentRunnable);
			break;

		default:
			break;
		}
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
	
	private void getVer() {
		try {
			int _curPage = curPage;
			if (currentStatus == DROP_DOWN) {
				_curPage = 0;
			} else if (currentStatus == FIRST_IN) {
				_curPage = 0;
			}
			ArticleBean bean = getDb().findFirst(Selector.from(ArticleBean.class)
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

	private void getListsFromDb() {
		/* 1. get list from db */
		LogUtils.e("isFirst=" + isFirst + ", currentStatus=" + currentStatus);

		try {
			List<ArticleBean> _list = getDb().findAll(
					Selector.from(ArticleBean.class)
							.where("appid", "=", appid)
							.and("columnId", "=", columnId)
							.and("ver", "=", ver)
							.and("curPage", "=", curPage));
			LogUtils.e("(_list == null)=" + (_list == null) + ", curPage="
					+ curPage + ", currentStatus=" + currentStatus);

			/* 注意发送请求获取数据 */
			if ((_list == null) || _list.size() == 0) {
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
					adapter.addArticles(_list);
					if ((currentStatus == FIRST_IN) && (adapter != null)) {
						mHandler.sendEmptyMessage(FRESH_LISTVIEW);
					}
				}
				LogUtils.e("_list.size=" + _list.size() + ", adapter.size="
						+ adapter.getCount());
			}
		} catch (DbException e) {
			e.printStackTrace();
			myHandler.sendEmptyMessage(CLEAR_CONTENT_REQUEST);
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
	
	private void checkVersionUpdate() {
		initParamsAndSendRequest(true);
	}

	private void initParamsAndSendRequest(boolean checkUpdate) {
		/* 1. 初始化参数 */
		/* 修改分页请求时传递参数的起始值，因请求分页时已经是最后一页，若该页总数小于pageSize时，并修改下一页请求的起始值 */
		first = curPage * pageSize;
		LogUtils.e("first=" + first + ", adapter.getDuansSize()=" + adapter.getCount());
		if (adapter.getCount() < first) {
			first = adapter.getCount();
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
		new HttpUtils().send(HttpRequest.HttpMethod.POST, Constants.ACTION_GET_COLUMN_CONTENT,
				params, new ContentRequestCallback(checkUpdate));
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
		public void onFailure(HttpException error, String msg) {
			showToast(R.string.no_service);
			if (currentStatus == DROP_DOWN) {
				changeDropDownFinishStatus();
			} else if (currentStatus == PULL_UP) {
				listView.onLoadMoreComplete();
			}

			myHandler.sendEmptyMessage(CLEAR_CONTENT_REQUEST);
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parseResult(responseInfo.result, checkUpdate);
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
					/* change listView status */
					LogUtils.e("thread.name=" + Thread.currentThread().getName());
					showToast(R.string.no_fresh);
					changeDropDownFinishStatus();
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
				}
			} else { /* 有版本更新 */
				LogUtils.e("get data from Server");

				int _ver = jsonObject.getIntValue("ver");
				int type = jsonObject.getIntValue("type");
				JSONArray jsonArray = jsonObject.getJSONArray("articles");
				/* 处理上拉加载没有获取到内容时 */
				if (jsonArray.size() == 0) {
					if (currentStatus == PULL_UP) {
						/* 当执行上拉操作时，自动将curPage--，设置成先前的状态，保证准确性 */
						curPage--;
						listView.onLoadMoreComplete();
						showToast(R.string.no_more);

						myHandler.sendEmptyMessage(CLEAR_CONTENT_REQUEST);
						return;
					} else if (currentStatus == DROP_DOWN) {
						showToast(R.string.no_fresh);
						changeDropDownFinishStatus();

						myHandler.sendEmptyMessage(CLEAR_CONTENT_REQUEST);
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
				List<ArticleBean> list = new ArrayList<ArticleBean>();
				for (int i = 0; i < jsonArray.size(); i++) {
					ArticleBean bean = new ArticleBean();
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					bean.setArticleName(jsonObject2.getString("articleName"));
					bean.setArticleDesc(jsonObject2.getString("articleDesc"));
					bean.setHasAudio(jsonObject2.getBooleanValue("hasAudio"));
					bean.setHasVideo(jsonObject2.getBooleanValue("hasVideo"));
					long id = jsonObject2.getLongValue("id");
					bean.setArticleId(id);
					bean.setImgUrl(jsonObject2.getString("imgUrl"));
					bean.setUrl(jsonObject2.getString("url"));
					bean.setHasPush(jsonObject2.getBooleanValue("push"));
					bean.setHasVip(jsonObject2.getBooleanValue("vip"));
					bean.setUrlType(jsonObject2.getIntValue("contentType"));
					bean.setGood(jsonObject2.getIntValue("good"));
					bean.setBad(jsonObject2.getIntValue("bad"));
					bean.setVer(_ver);
					bean.setType(type);
					bean.setAppid(appid);
					bean.setColumnId(columnId);
					bean.setCurPage(curPage);
					list.add(bean);
				}

				// 将数据保存到数据库中
				try {
					getDb().saveAll(list);
				} catch (DbException e) {
					e.printStackTrace();
					return;
				}

				/* 同步适配器 */
				LogUtils.e("parseResult list.size=" + list.size());
				adapter.addArticles(list);

				mHandler.sendEmptyMessage(FRESH_LISTVIEW);
				changeFinishState();
			}

			myHandler.sendEmptyMessage(CLEAR_CONTENT_REQUEST);
		} else {/* 出现其它异常 */
			showToast(jsonObject.getString("dsc") + "");
			if (currentStatus == DROP_DOWN) {
				changeDropDownFinishStatus();
			} else if (currentStatus == PULL_UP) {
				mHandler.sendEmptyMessage(FRESH_LISTVIEW);
				listView.onLoadMoreComplete();
			}

			myHandler.sendEmptyMessage(CLEAR_CONTENT_REQUEST);
		}
	}
	
	private void clearListAndDb() {
		curPage = 0;
		adapter.clear();
		try {
			getDb().delete(ArticleBean.class, WhereBuilder.b("appid", "=", appid)
					.and("columnId", "=", columnId));
		} catch (DbException e) {
			e.printStackTrace();
			return;
		}
	}
	
	protected void setMoveUp() {
		mHandler.sendEmptyMessage(MOVE_UP);
	}
	
	private void changeFinishState() {
		LogUtils.e("freshEnabled=" + freshEnabled);
		switch (currentStatus) {
		case PULL_UP:
			listView.onLoadMoreComplete();
			setMoveUp();
			break;

		case DROP_DOWN:
			changeDropDownFinishStatus();
			break;

		case FIRST_IN:
			break;
		}
	}
	
	private void changeDropDownFinishStatus() {
		if (freshEnabled) {
			freshEnabled = false;
			fresh.clearAnimation();
		}
		listView.onRefreshComplete();
	}

	private class Frag3Handler extends Handler {

		public Frag3Handler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CLEAR_CONTENT_REQUEST:
				myHandler.removeCallbacks(contentRunnable);
				break;

			default:
				break;
			}
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

			case MOVE_UP:
				listView.smoothScrollBy(ScreenUtils.dpToPxInt(getActivity(), Constants.SCROLL_DISTANCE), Constants.SCROLL_DURATION);
				break;
				
			default:
				break;
			}
		}

	};
	
	private OnFrag3ItemClickListener itemListener = new OnFrag3ItemClickListener() {

		@Override
		public void itemClicked(ArticleBean bean) {
			Intent intent = new Intent(getActivity(), ShowPageActivity.class);
			intent.putExtra("title", bean.getArticleName());
			intent.putExtra("url", bean.getUrl());
			startActivity(intent);
			new ActivityAnimator().pushLeftAnimation(getActivity());
		}
		
	};
	
}
