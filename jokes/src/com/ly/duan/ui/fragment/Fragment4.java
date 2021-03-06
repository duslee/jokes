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
import android.view.animation.Animation;
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
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ly.duan.adapter.Frag4Adapter;
import com.ly.duan.adapter.Frag4Adapter.Frag4ViewHolder;
import com.ly.duan.adapter.Frag4Adapter.OnFrag4ItemOperListerner;
import com.ly.duan.adapter.Frag4Adapter.OnVideoPlayListener;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.bean.ColumnBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.bean.MultiReqStatus;
import com.ly.duan.help.GlobalHelp;
import com.ly.duan.service.InitDataService;
import com.ly.duan.ui.CommentDetailActivity;
import com.ly.duan.ui.VVActivity;
import com.ly.duan.ui.fragment.Fragment1.MyAnimationListener;
import com.ly.duan.utils.ActivityAnimator;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.DeviceUtils;
import com.ly.duan.utils.ScreenUtils;
import com.sjm.gxdz.R;

public class Fragment4 extends BaseFragment {
	
	private static final int FIRST_IN = 1;
	private static final int DROP_DOWN = 2;
	private static final int PULL_UP = 3;

	private static final int FRESH_LISTVIEW = 28;
	private static final int MOVE_UP = 29;
	private static final int CLEAR_CONTENT_REQUEST = 30;
	private static final int MODIFY_VIDEOS_LIST = 31;
	private static final int UPDATE_FRAG4_ITEM = 32;
	private static final int ADD_LIST = 33;
	
	@ViewInject(R.id.dropDownListView)
	private PullRefreshAndLoadMoreListView listView;

	@ViewInject(R.id.fresh)
	private ImageView fresh;
	
	private Frag4Adapter adapter = null;
	
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

	private Frag4Handler myHandler;
	private HandlerThread myThread;
	
	public static Fragment4 newInstance(boolean first, ColumnBean bean) {
		Fragment4 fragment = new Fragment4();
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
		LogUtils.e("initData columnId=" + columnId);

		/* 2. start HandlerThread */
		if (null == myHandler) {
			initHandler();
		}
	}

	private void initHandler() {
		myThread = new HandlerThread("My Thread " + columnId);
		myThread.start();
		myHandler = new Frag4Handler(myThread.getLooper());		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_frag1, container, false);
		ViewUtils.inject(this, view);
		
		setListener();

		setEmptyTv();

		baseInit();
		
		return view;
	}
	
	private void setListener() {
		/* init adapter */
		adapter = new Frag4Adapter(getActivity());
		listView.setAdapter(adapter);
		
		/* set item play listener */
		adapter.setVideoPlayListener(playListener);
		
		/* set OnFrag4ItemOperListerner */
		adapter.setFrag4ItemListener(itemListener);
		
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
//					LogUtils.e("thread.name=" + Thread.currentThread().getName());
//					mHandler.sendEmptyMessage(ADD_LIST);
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
			getDb().delete(ArticleBean.class, WhereBuilder
					.b("appid", "=", appid)
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

	private class Frag4Handler extends Handler {

		public Frag4Handler(Looper looper) {
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
			case ADD_LIST:
				setData();
				adapter.notifyDataSetChanged();
				break;
			
			case FRESH_LISTVIEW:
				adapter.notifyDataSetChanged();
				break;

			case MOVE_UP:
				listView.smoothScrollBy(ScreenUtils.dpToPxInt(getActivity(), Constants.SCROLL_DISTANCE), Constants.SCROLL_DURATION);
				break;
				
			case MODIFY_VIDEOS_LIST:
				if ((null != adapter) && (adapter.getCount() > 0)) {
					adapter.changeArticlesList((ArticleBean) msg.obj);
				}
				break;
				
			case UPDATE_FRAG4_ITEM:
				final Frag4ViewHolder holder = (Frag4ViewHolder) msg.obj;
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
					Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
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
					Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
					animation.setAnimationListener(new MyAnimationListener(getActivity(), holder.add_tv2, false));
					holder.add_tv2.startAnimation(animation);
				}
				break;
				
			default:
				break;
			}
		}

	};
	
	private OnVideoPlayListener playListener = new OnVideoPlayListener() {
		
		@Override
		public void playVideo(ArticleBean bean) {
			if (bean.getUrlType() == 1) {
				Intent intent = new Intent(getActivity(), VVActivity.class);
				intent.putExtra("title", bean.getArticleName());
				intent.putExtra("url", bean.getUrl());
				startActivity(intent);
				new ActivityAnimator().pushLeftAnimation(getActivity());
			}
		}
	};
	
	private OnFrag4ItemOperListerner itemListener = new OnFrag4ItemOperListerner() {
		
		@Override
		public void dropItemOper(int operType, ArticleBean bean, Frag4ViewHolder holder) {
			/* 1. init params */
			String imei = DeviceUtils.getIMEI(getActivity());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("appid", bean.getAppid() + ""));
			nvps.add(new BasicNameValuePair("contentType", bean.getType() + ""));
			nvps.add(new BasicNameValuePair("contentId", bean.getArticleId() + ""));
			nvps.add(new BasicNameValuePair("type", operType + ""));
			nvps.add(new BasicNameValuePair("userId", imei));
			RequestParams params = new RequestParams();
			params.addQueryStringParameter(nvps);
			
			/* 2. send request */
			new HttpUtils().send(HttpMethod.POST, Constants.ACTION_APPROVE_STAMP, params, 
					new Frag4ItemCallBack(operType, bean, holder));
		}

		@Override
		public void startComment(int contentType, boolean isAds, int pos) {
			/* Go To Comment Activity */
			LogUtils.e("contentType=" + contentType + ", isAds=" + isAds + ", pos=" + pos);
			Intent intent = new Intent(getActivity(), CommentDetailActivity.class);
			intent.putExtra("contentType", contentType);
			intent.putExtra("articlePos", pos);
			/* handle Articles */
			int size = adapter.getCount();
			intent.putExtra("size", size);
			if (size > 0) {
				List<ArticleBean> _list = adapter.getArticleList();
				for (int i = 0; i < size; i++) {
					intent.putExtra("" + i, _list.get(i));
				}
			}
			startActivity(intent);
			new ActivityAnimator().pushLeftAnimation(getActivity());
		}
	};
	
	private class Frag4ItemCallBack extends RequestCallBack<String> {
		
		private int operType;
		private ArticleBean bean;
		private Frag4ViewHolder holder;

		public Frag4ItemCallBack(int operType, ArticleBean bean, Frag4ViewHolder holder) {
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
				Message msg1 = mHandler.obtainMessage(MODIFY_VIDEOS_LIST, bean);
				mHandler.sendMessage(msg1);
				
				/* update UI */
				Message msg2 = mHandler.obtainMessage(UPDATE_FRAG4_ITEM, holder); 
				msg2.arg1 = operType;
				int nums = 0;
				if (operType == Constants.TYPE_APPROVE) {
					nums = bean.getGood();
				} else if (operType == Constants.TYPE_STAMP) {
					nums = bean.getBad();
				}
				msg2.arg2 = nums;
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
	
	private void setData() {
		List<ArticleBean> list = new ArrayList<ArticleBean>();
		
		ArticleBean bean1 = new ArticleBean();
		bean1.setArticleName("美女妞妞");
		bean1.setUrlType(1);
		bean1.setArticleId(6783);
		bean1.setImgUrl("http://7q5dcu.com2.z0.glb.qiniucdn.com/article_6783.jpg");
		bean1.setUrl("http://uvideo.qiniudn.com/zp0238.MP4");
		list.add(bean1);
		
		ArticleBean bean2 = new ArticleBean();
		bean2.setArticleName("黑丝靓妹");
		bean2.setUrlType(1);
		bean2.setArticleId(6784);
		bean2.setImgUrl("http://7q5dcu.com2.z0.glb.qiniucdn.com/article_6784.jpg");
		bean2.setUrl("http://uvideo.qiniudn.com/zp0239.MP4");
		list.add(bean2);
		
		ArticleBean bean3 = new ArticleBean();
		bean3.setArticleName("紧身黑衣");
		bean3.setUrlType(1);
		bean3.setArticleId(6783);
		bean3.setImgUrl("http://7q5dcu.com2.z0.glb.qiniucdn.com/article_6785.jpg");
		bean3.setUrl("http://uvideo.qiniudn.com/zp0240.MP4");
		list.add(bean3);
		
		adapter.addArticles(list);
	}

}
