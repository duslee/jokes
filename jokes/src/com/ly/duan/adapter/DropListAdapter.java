package com.ly.duan.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.bean.BannerBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.FileUtils;
import com.ly.duan.utils.GifUtils;
import com.ly.duan.utils.ImageUtils;
import com.ly.duan.utils.ScreenUtils;
import com.ly.duan.utils.StringUtils;
import com.ly.duan.view.ProgressWheel;
import com.sjm.gxdz.R;

@SuppressLint("NewApi")
public class DropListAdapter extends BaseAdapter {

	private final static int IV_HEIGHT = 400;
	private final static int GIF_HEIGHT = 340;

	private Context mContext;
	private LayoutInflater mInflater;
	private List<DuanBean> duanList;
	private boolean insertAds;
	private boolean isAds = false;
	private int currentIndex = 0;
	private int toIndex = 0;

	private List<BannerBean> apkList;
	private List<BannerBean> bannerList;
	private BitmapDisplayConfig config1 = null;
	private BitmapUtils bitmapUtils = null;
	private GifUtils gifUtils = null;

	private Map<String, Integer> map = new HashMap<String, Integer>();

	public DropListAdapter(Context context, boolean insertAds) {
		super();
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		duanList = new ArrayList<DuanBean>();
		this.insertAds = insertAds;
		if (insertAds) {
			apkList = new ArrayList<BannerBean>();
			bannerList = new ArrayList<BannerBean>();
		}
		bitmapUtils = BitmapHelp.getInstance(context);
		gifUtils = new GifUtils(context);

		config1 = new BitmapDisplayConfig();
		config1.setBitmapMaxSize(new BitmapSize(ScreenUtils.pxToDpCeilInt(
				context, 100), ScreenUtils.pxToDpCeilInt(context, 100)));
	}

	public void clear() {
		duanList.clear();
		notifyDataSetChanged();
	}
	
	/** 显示当前显示内容的条目个数，不包含banner */
	public int getDuansSize() {
		return duanList.size();
	}
	
	public List<DuanBean> getDuansList() {
		return duanList;
	}

	public void addDuans(List<DuanBean> _duanList) {
		duanList.addAll(_duanList);
	}

	public void addApks(List<BannerBean> _apkList) {
		this.apkList.addAll(_apkList);
	}
	
	public int getBannerSize() {
		return bannerList.size();
	}
	
	public List<BannerBean> getBannerList() {
		return bannerList;
	}

	public void addBanners(List<BannerBean> bannerList) {
		this.bannerList.addAll(bannerList);
	}

	@Override
	public int getCount() {
		if (!insertAds) {
			return duanList.size();
		}
		return duanList.size() + currentIndex;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		LogUtils.e("11111 position=" + position + ", size=" + duanList.size()
//				+ ", (convertView == null)=" + (convertView == null));
		if (!insertAds) {
			DuanBean bean = duanList.get(position);
			return getDuanView(bean, convertView, parent, false, position);
		}

		// LogUtils.e("22222 position=" + position + ", currentIndex="
		// + currentIndex + ", toIndex=" + toIndex);
		isAds = isAds(position);
//		LogUtils.e("33333 isAds=" + isAds + ", currentIndex=" + currentIndex
//				+ ", toIndex=" + toIndex);
		if (!isAds) {
			int pos = position - currentIndex;
			DuanBean bean = duanList.get(pos);
			return getDuanView(bean, convertView, parent, isAds, position);
		} else {
			int pos = toIndex - 1;
			BannerBean bean = bannerList.get(pos);
//			LogUtils.e(bean.toString());
			return getBannerView(bean, convertView, parent, isAds, position);
		}
	}

	private boolean isAds(int position) {
		int divider = position / 10;
		int residue = position % 10;
		int size = bannerList.size();
		// LogUtils.e("size=" + size);
		if (size == 0) { /* no apk list */
			currentIndex = 0;
			toIndex = 0;
			return false;
		} else { /* has apk list */
			// LogUtils.e("residue=" + residue + ", divider=" + divider
			// + ", size=" + size);

			/* 处理广告条目 处的相关参数 */
			if (residue == 0 && divider == 0) {
				currentIndex = divider + 1;
				toIndex = divider + 1;
				return true;
			} else if (residue == 0 && divider < size && toIndex <= size) {
				if (currentIndex < divider + 1) { /* 上拉 */
					currentIndex = divider + 1;
					toIndex = divider + 1;
				} else { /* 下滚 */
					currentIndex = divider;
					toIndex = divider + 1;
				}
				return true;
			}

			/* 处理非广告条目（段子条目）处上下滑动的相关参数 */
			if (divider + 1 <= size) {
				currentIndex = divider + 1;
				toIndex = divider + 1;
			} else {
				currentIndex = size;
				toIndex = size;
			}
		}
		return false;
	}
	
	private View getBannerView(final BannerBean bean, View convertView,
			ViewGroup parent, boolean isAds, int pos) {
		/* get holder */
		DropItemHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.drop_item, null);
			holder = new DropItemHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (DropItemHolder) convertView.getTag();
		}

		/* set duanzi item not visible */
		holder.duan_ll.setVisibility(View.GONE);
		holder.banner_ll.setVisibility(View.VISIBLE);

		/* fill banner TextView */
		if (!StringUtils.isBlank(bean.getBannerTitle())
				&& !StringUtils.isBlank(bean.getBannerDesc())) {
			holder.banner_content_ll.setVisibility(View.VISIBLE);
			String str = String.format(
					mContext.getResources().getString(R.string.banner_text),
					bean.getBannerTitle(), bean.getBannerDesc());
			holder.banner_tv.setText(str);
		} else if (!StringUtils.isBlank(bean.getBannerTitle())) {
			holder.banner_content_ll.setVisibility(View.VISIBLE);
			holder.banner_tv.setText(bean.getBannerTitle());
		} else if (!StringUtils.isBlank(bean.getBannerDesc())) {
			holder.banner_content_ll.setVisibility(View.VISIBLE);
			holder.banner_tv.setText(bean.getBannerDesc());
		} else {
			holder.banner_content_ll.setVisibility(View.GONE);
		}

		/* display iv */
		String url = bean.getBannerImgUrl();
		if (!StringUtils.isBlank(url)) {
			/* set visibility */
			holder.banner_rl.setVisibility(View.VISIBLE);
			holder.banner_iv.setVisibility(View.VISIBLE);
			holder.banner_pw.setVisibility(View.VISIBLE);

			/* set IV height */
			LogUtils.e("map.containsKey(url)=" + map.containsKey(url));
			if (map.containsKey(url)) {
				setIVHeight(holder.banner_iv, map.get(url));
			} else {
				setIVHeight(holder.banner_iv, IV_HEIGHT);
			}
//			LogUtils.e("holder.banner_iv.getWidth="
//					+ holder.banner_iv.getWidth()
//					+ ", holder.banner_iv.getHeight="
//					+ holder.banner_iv.getHeight());

			// set tag in iv
			String urlTag = (String) holder.banner_iv.getTag();
			if (!StringUtils.isBlank(urlTag) && urlTag.equalsIgnoreCase(url)) {
			} else {
				holder.banner_iv.setTag(url);
//				LogUtils.e("(holder.iv == null)=" + (holder.banner_iv == null));
				bitmapUtils.display(holder.banner_iv, url,
						new CustomBitmapLoadCallBack(holder, isAds));
			}
		} else {
			holder.banner_rl.setVisibility(View.GONE);
		}

		if (bean.getContentType() == 4) {
			holder.down_btn.setText(R.string.download_now);
		} else {
			holder.down_btn.setText(R.string.open_now);
		}

		/* set download listener */
		holder.down_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.startDownloadOrOpen(bean);
				}
//				LogUtils.e("down button clicked");
			}
		});
		return convertView;
	}

	private View getDuanView(final DuanBean bean, View convertView, ViewGroup parent,
			final boolean isAds, final int pos) {
		/* get holder */
		DropItemHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.drop_item, null);
			holder = new DropItemHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
//			LogUtils.e("33333 (convertView == null)=" + (convertView == null));
		} else {
//			LogUtils.e("44444 (convertView == null)=" + (convertView == null));
			holder = (DropItemHolder) convertView.getTag();
		}

		/* set banner item not visible */
		holder.duan_ll.setVisibility(View.VISIBLE);
		holder.banner_ll.setVisibility(View.GONE);

		/* fill item TextView */
		holder.duan_nick.setText(bean.getNick());
		if (!StringUtils.isBlank(bean.getContent())) {
			holder.duan_content.setVisibility(View.VISIBLE);
			holder.duan_content.setText(bean.getContent());
		} else {
			holder.duan_content.setVisibility(View.GONE);
		}
		
		/* display nums of good & bad */
		LogUtils.e("good=" + bean.getGood() + ", bad=" + bean.getBad() + ", approve=" 
				+ bean.getApprove() + ", stamp=" + bean.getStamp());
		if (bean.getGood() > 0) {
			holder.up_tv.setText("" + bean.getGood());
		} else {
			holder.up_tv.setText("");
		}
		if (bean.getBad() > 0) {
			holder.down_tv.setText("" + bean.getBad());
		} else {
			holder.down_tv.setText("");
		}
		
		/* set status the item has good & bad
		 * 1) good=0, bad=0		>>		unfocus
		 * 2) good=1, bad=0 || good=0, bad=1	>>		focus(only one that value is 1) */
		if ((bean.getApprove() == 0) && (bean.getStamp() == 0)) {
			holder.up_ll.setBackgroundResource(R.drawable.approve_bg_selector);
			holder.up_iv.setImageResource(R.drawable.up_selector);
			holder.up_tv.setTextColor(mContext.getResources()
					.getColor(R.color.num_tv_selector));
			
			holder.down_ll.setBackgroundResource(R.drawable.disapprove_bg_selector);
			holder.down_iv.setImageResource(R.drawable.down_selector);
			holder.down_tv.setTextColor(mContext.getResources()
					.getColor(R.color.num_tv_selector));
		} else {
			holder.up_ll.setBackgroundResource(R.drawable.approve_press_bg);
			holder.up_iv.setImageResource(R.drawable.up_focus);
			holder.up_tv.setTextColor(mContext.getResources()
					.getColor(R.color.up_down_focus_color));
			
			holder.down_ll.setBackgroundResource(R.drawable.disapprove_press_bg);
			holder.down_iv.setImageResource(R.drawable.down_focus);
			holder.down_tv.setTextColor(mContext.getResources()
					.getColor(R.color.up_down_focus_color));
		}
		
		/* set click Listener about good & bad */
		holder.up_ll.setOnClickListener(new DropItemClickListener(
				Constants.TYPE_APPROVE, bean, holder));
		holder.down_ll.setOnClickListener(new DropItemClickListener(
				Constants.TYPE_STAMP, bean, holder));

		/* handle comment */
		holder.comment_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (itemListener != null) {
					LogUtils.e("contentType=" + bean.getContentType() + ", isAds=" + isAds + ", pos=" + pos);
					itemListener.startComment(bean.getContentType(), isAds, pos);
				}
			}
		});
		
		String avatarUrl = bean.getAvatarUrl();
		if (!StringUtils.isBlank(avatarUrl)) {
			String urlTag = (String) holder.duan_avatar.getTag();
			if (!StringUtils.isBlank(urlTag) && urlTag.equalsIgnoreCase(avatarUrl)) {
			} else {
				holder.duan_avatar.setTag(avatarUrl);
				bitmapUtils.display(holder.duan_avatar, avatarUrl, config1,
						new CustomAvatarLoadCallback());
			}
		} else {
			holder.duan_avatar.setTag(avatarUrl);
			holder.duan_avatar.setImageDrawable(null);
		}

		/* display iv & gif */
		String url = bean.getImgUrl();
		if (!StringUtils.isBlank(url)) {
			holder.duan_rl.setVisibility(View.VISIBLE);
			int index = url.lastIndexOf(".");
			if (url.substring(index + 1).equalsIgnoreCase("gif")) {
				holder.pictureIV.setVisibility(View.GONE);
				holder.gifView.setVisibility(View.VISIBLE);
				holder.duan_pw.setVisibility(View.VISIBLE);

				/* set GifImageView height */
//				LogUtils.e("map.containsKey(url)=" + map.containsKey(url));
				if (map.containsKey(url)) {
					setGifViewHeight(holder.gifView, map.get(url));
				} else {
					setGifViewHeight(holder.gifView, GIF_HEIGHT);
				}

//				LogUtils.e("55555 (holder.gifView == null)=" + (holder.gifView == null));
				// holder.gifView.setGifImageType(GifImageType.COVER);
				displayGif(url, holder);
			} else {
				holder.gifView.setVisibility(View.GONE);
				holder.pictureIV.setVisibility(View.VISIBLE);

				/* set IV height */
//				LogUtils.e("map.containsKey(url)=" + map.containsKey(url));
				if (map.containsKey(url)) {
//					LogUtils.e("map.get(url)=" + map.get(url));
					setIVHeight(holder.pictureIV, map.get(url));
				} else {
					setIVHeight(holder.pictureIV, IV_HEIGHT);
				}
//				LogUtils.e("66666 holder.pictureIV.getWidth="
//						+ holder.pictureIV.getWidth()
//						+ ", holder.pictureIV.getHeight="
//						+ holder.pictureIV.getHeight());

				String urlTag = (String) holder.pictureIV.getTag();
				if (!StringUtils.isBlank(urlTag)
						&& urlTag.equalsIgnoreCase(url)) {
//					LogUtils.e("77777 (holder.pictureIV == null)="
//							+ (holder.pictureIV == null)
//							+ ", urlTag.equalsIgnoreCase(url)="
//							+ urlTag.equalsIgnoreCase(url));
				} else {
					holder.duan_pw.setVisibility(View.VISIBLE);
					holder.pictureIV.setTag(url);
					bitmapUtils.display(holder.pictureIV, bean.getImgUrl(),
							new CustomBitmapLoadCallBack(holder, isAds));
				}
			}
		} else {
			holder.duan_rl.setVisibility(View.GONE);
		}
		return convertView;
	}

	private void setIVHeight(ImageView iv, int height) {
		// LogUtils.e("iv.width=" + iv.getWidth() + ", iv.height=" +
		// iv.getHeight());
		// LogUtils.e("iv.isInEditMode()=" + iv.isInEditMode() +
		// ", iv.getAdjustViewBounds()=" + iv.getAdjustViewBounds());
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) iv
				.getLayoutParams();
		params.width = mContext.getResources().getDisplayMetrics().widthPixels;
		params.height = height;
		// LogUtils.e("iv.isInEditMode()=" + iv.isInEditMode() +
		// ", iv.getAdjustViewBounds()=" + iv.getAdjustViewBounds());
		// LogUtils.e("------- setIV width=" + params.width + ", height="
		// + params.height);
		iv.setLayoutParams(params);
	}

	private void setGifViewHeight(GifImageView gifView, int height) {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) gifView
				.getLayoutParams();
		params.width = mContext.getResources().getDisplayMetrics().widthPixels;
		params.height = height;
		gifView.setLayoutParams(params);
	}

	private void displayGif(String url, DropItemHolder holder) {
		String path = gifUtils.getDiskCachePath();
		String fileName = gifUtils.getFileName(url);
		try {
			FileUtils.createDir(path);
			File file = new File(path + File.separator + fileName);
//			LogUtils.e("path=" + path + ", fileName=" + fileName
//					+ ", AbsolutePath=" + file.getAbsolutePath());
			if (file.exists()) {
//				LogUtils.e("111111 file.exists=" + file.exists());
				holder.duan_pw.setVisibility(View.GONE);
				// holder.gifView.setGifImage(new FileInputStream(file));
				GifDrawable dr = new GifDrawable(file);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) holder.gifView
						.getLayoutParams();
				int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
				params.width = screenWidth;
				params.height = (int) (dr.getIntrinsicHeight() * screenWidth / (float) dr
						.getIntrinsicWidth());
				holder.gifView.setLayoutParams(params);
				map.put(url, params.height);
				holder.gifView.setImageDrawable(dr);
			} else {
//				LogUtils.e("222222 file.exists=" + file.exists());
				holder.gifView.setImageDrawable(null);
				holder.duan_pw.setVisibility(View.VISIBLE);

				file.createNewFile();
				startDownFile(url, file.getAbsolutePath(), holder);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void startDownFile(final String url, String filePath,
			final DropItemHolder holder) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.download(url, filePath, new RequestCallBack<File>() {

			@Override
			public void onStart() {
				holder.duan_pw.resetCount();
				holder.duan_pw.setProgress(0);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				float rate = current / (float) total;
				holder.duan_pw.setProgress((int) (360 * rate));
				holder.duan_pw.setText(((int) (100 * rate)) + "%");
			}

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				holder.duan_pw.setProgress(360);
				holder.duan_pw.setVisibility(View.GONE);
				String filePath = responseInfo.result.getAbsolutePath();
//				LogUtils.e(filePath);
				try {
					// holder.gifView.setGifImage(new
					// FileInputStream(filePath));
					GifDrawable dr = new GifDrawable(filePath);
					RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) holder.gifView
							.getLayoutParams();
					int screenWidth = mContext.getResources()
							.getDisplayMetrics().widthPixels;
					params.width = screenWidth;
					params.height = (int) (dr.getIntrinsicHeight()
							* screenWidth / (float) dr.getIntrinsicWidth());
					holder.gifView.setLayoutParams(params);
					map.put(url, params.height);
					holder.gifView.setImageDrawable(dr);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
			}
		});
	}

	public class DropItemHolder {
		/* duanzi item */
		@ViewInject(R.id.duan_ll)
		private LinearLayout duan_ll;

		@ViewInject(R.id.duan_avatar)
		private ImageView duan_avatar;

		@ViewInject(R.id.duan_nick)
		private TextView duan_nick;

		@ViewInject(R.id.duan_content)
		private TextView duan_content;

		@ViewInject(R.id.duan_rl)
		private RelativeLayout duan_rl;

		@ViewInject(R.id.duan_picture)
		private ImageView pictureIV;

		@ViewInject(R.id.duan_gif)
		private GifImageView gifView;

		@ViewInject(R.id.duan_pw)
		private ProgressWheel duan_pw;
		
		/* approve */
		@ViewInject(R.id.up_ll)
		public LinearLayout up_ll;
		
		@ViewInject(R.id.up_iv)
		public ImageView up_iv;
		
		@ViewInject(R.id.up_tv)
		public TextView up_tv;
		
		@ViewInject(R.id.add_tv1)
		public TextView add_tv1;

		/* stamp */
		@ViewInject(R.id.down_ll)
		public LinearLayout down_ll;
		
		@ViewInject(R.id.down_iv)
		public ImageView down_iv;
		
		@ViewInject(R.id.down_tv)
		public TextView down_tv;
		
		@ViewInject(R.id.add_tv2)
		public TextView add_tv2;
		
		/* comment */
		@ViewInject(R.id.comment_ll)
		public LinearLayout comment_ll;
		
		@ViewInject(R.id.comment_iv)
		public ImageView comment_iv;
		
		@ViewInject(R.id.comment_tv)
		public TextView comment_tv;
		
		/* banner item */
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

		@ViewInject(R.id.banner_item_pw)
		private ProgressWheel banner_pw;

	}

	public class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {

		private DropItemHolder holder;
		private boolean _isAds;

		public CustomBitmapLoadCallBack(DropItemHolder holder) {
			this.holder = holder;
		}

		public CustomBitmapLoadCallBack(DropItemHolder holder, boolean _isAds) {
			this.holder = holder;
			this._isAds = _isAds;
		}

		@Override
		public void onLoadStarted(ImageView container, String uri,
				BitmapDisplayConfig config) {
			if (!_isAds) {
				holder.duan_pw.resetCount();
				holder.duan_pw.setProgress(0);
			} else {
				holder.banner_pw.resetCount();
				holder.banner_pw.setProgress(0);
			}
		}

		@Override
		public void onLoading(ImageView container, String uri,
				BitmapDisplayConfig config, long total, long current) {
			float rate = current / (float) total;
			if (!_isAds) {
				holder.duan_pw.setProgress((int) (360 * rate));
				holder.duan_pw.setText(((int) (100 * rate)) + "%");
			} else {
				holder.banner_pw.setProgress((int) (360 * current / total));
				holder.banner_pw.setText(((int) (100 * rate)) + "%");
			}
		}

		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

//			LogUtils.e("bitmap.width=" + width + ", bitmap.height=" + height);

			float scaleWidth = mContext.getResources().getDisplayMetrics().widthPixels
					/ (float) width;
			if (!_isAds) {
				holder.duan_pw.setProgress(360);
				holder.duan_pw.setVisibility(View.GONE);
				if (!map.containsKey(uri)) {
//					LogUtils.e("holder.pictureIV.isInEditMode()="
//							+ holder.pictureIV.isInEditMode());
					RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) holder.pictureIV
							.getLayoutParams();
					params.width = mContext.getResources().getDisplayMetrics().widthPixels;
					params.height = (int) (height * scaleWidth);
					holder.pictureIV.setLayoutParams(params);
					LogUtils.e("load bitmap params.height=" + params.height
							+ ", params.width=" + params.width);
					map.put(uri, params.height);
				}
				// LogUtils.e("holder.pictureIV.getWidth="
				// + holder.pictureIV.getWidth()
				// + ", holder.pictureIV.getHeight="
				// + holder.pictureIV.getHeight());
				// LogUtils.e("holder.pictureIV.isInEditMode()="
				// + holder.pictureIV.isInEditMode());
				holder.pictureIV.setImageBitmap(bitmap);
			} else {
				holder.banner_pw.setProgress(360);
				holder.banner_pw.setVisibility(View.GONE);
				if (!map.containsKey(uri)) {
					RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) holder.banner_iv
							.getLayoutParams();
					params.width = mContext.getResources().getDisplayMetrics().widthPixels;
					params.height = (int) (height * scaleWidth);
					holder.banner_iv.setLayoutParams(params);
					map.put(uri, params.height);
				}
				// LogUtils.e("holder.banner_iv.getWidth="
				// + holder.banner_iv.getWidth()
				// + ", holder.banner_iv.getHeight="
				// + holder.banner_iv.getHeight());
				holder.banner_iv.setImageBitmap(bitmap);
			}
		}

	}

	public void setDownloadListener(OnDownloadOrOpenListener listener) {
		this.listener = listener;
	}

	private OnDownloadOrOpenListener listener;
	public interface OnDownloadOrOpenListener {
		public void startDownloadOrOpen(BannerBean bean);
	}

	private class CustomAvatarLoadCallback extends DefaultBitmapLoadCallBack<ImageView> {

		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			Bitmap displayBitmap = ImageUtils.toRoundCorner(bitmap, 10);
			container.setImageBitmap(displayBitmap);
		}

	}
	
	private OnDropItemOperListerner itemListener;
	public interface OnDropItemOperListerner {
		public void dropItemOper(int operType, DuanBean bean, DropItemHolder holder);
		public void startComment(int contentType, boolean isAds, int pos);
	}
	
	public void setDropItemListener(OnDropItemOperListerner itemListener) {
		this.itemListener = itemListener;
	}
	
	private class DropItemClickListener implements OnClickListener {
		
		private int operType;
		private DuanBean bean;
		private DropItemHolder holder;

		public DropItemClickListener(int operType, DuanBean bean,
				DropItemHolder holder) {
			this.operType = operType;
			this.bean = bean;
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
//			LogUtils.e("operType=" + operType + ", (null != itemListerner)=" + (null != itemListener));
			if (null != itemListener) {
				if ((bean.getApprove() == 0) && (bean.getStamp() == 0)) {
					itemListener.dropItemOper(operType, bean, holder);
				}
//				if ((operType == Constants.TYPE_APPROVE) && (bean.getApprove() == 0) && (bean.getStamp() == 0)) {
//					itemListener.dropItemOper(operType, bean, holder);
//				} else if ((operType == Constants.TYPE_STAMP) && (bean.getApprove() == 0) && (bean.getStamp() == 0)) {
//					itemListener.dropItemOper(operType, bean, holder);
//				}
			}
		}
		
	}

	public void changeDuansList(DuanBean bean) {
		LogUtils.e(bean.toString());
		for (int i = 0; i < duanList.size(); i++) {
			DuanBean duanBean = duanList.get(i);
			if ((duanBean.getDuanId() == bean.getDuanId()) && 
					(duanBean.getNick().equalsIgnoreCase(bean.getNick()))) {
				duanList.get(i).setApprove(bean.getApprove());
				duanList.get(i).setStamp(bean.getStamp());
				duanList.get(i).setGood(bean.getGood());
				duanList.get(i).setBad(bean.getBad());
			}
		}
	} 

}
