package com.ly.duan.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.StringUtils;
import com.sjm.gxdz.R;

public class Frag4Adapter extends BaseAdapter {
	
	private final static int IV_HEIGHT = 200;
	
	private Context mContext;
	private List<ArticleBean> list;
	private BitmapUtils bitmapUtils;
	
	private Map<String, Integer> map = new HashMap<String, Integer>();
	
	public Frag4Adapter(Context context) {
		super();
		this.mContext = context;
		bitmapUtils = BitmapHelp.getInstance(context);
		list = new ArrayList<ArticleBean>();
	}
	
	public void addArticles(List<ArticleBean> _list) {
		list.addAll(_list);
	}

	public void clear() {
		list.clear();
		notifyDataSetChanged();
	}

	public List<ArticleBean> getArticleList() {
		return list;
	} 
	
	@Override
	public int getCount() {
		return list.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		Frag4ViewHolder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_frag4, null);
			holder = new Frag4ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (Frag4ViewHolder) convertView.getTag();
		}
		
		final ArticleBean bean = list.get(position);
		
		/* set tv about content */
		if (!StringUtils.isBlank(bean.getArticleName())) {
			holder.frag4_content.setVisibility(View.VISIBLE);
			holder.frag4_content.setText(bean.getArticleName());
		} else {
			holder.frag4_content.setVisibility(View.GONE);
		}
		
		/* set video played listener */
		holder.frag4_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != playListener) {
					playListener.playVideo(bean);
				}
			}
		});
		
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
					LogUtils.e("contentType=" + bean.getType());
					itemListener.startComment(bean.getType(), false, position);
				}
			}
		});
		
		/* display picture */
		String url = bean.getImgUrl();
//		LogUtils.e("img url=" + url);
		if (!StringUtils.isBlank(url)) {
			/* set IV height */
			if (map.containsKey(url)) {
				setIVHeight(holder.frag4_picture, map.get(url));
			} else {
				setIVHeight(holder.frag4_picture, IV_HEIGHT);
			}
			
			String urlTag = (String) holder.frag4_picture.getTag();
//			LogUtils.e("img urlTag=" + urlTag);
			if (!StringUtils.isBlank(urlTag) && urlTag.equalsIgnoreCase(url)) {
			} else {
				holder.frag4_picture.setTag(url);
				bitmapUtils.display(holder.frag4_picture, url, new CustomBitmapLoadCallBack(holder));
			}
		} else {
			holder.frag4_picture.setTag(url);
			/* set IV height 200 */
			setIVHeight(holder.frag4_picture, IV_HEIGHT);
			holder.frag4_picture.setImageDrawable(null);
		}
		
		return convertView;
	}
	
	private void setIVHeight(ImageView iv, int height) {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) iv
				.getLayoutParams();
		params.width = mContext.getResources().getDisplayMetrics().widthPixels;
		params.height = height;
		iv.setLayoutParams(params);
	}
	
	public class Frag4ViewHolder {
		@ViewInject(R.id.frag4_ll)
		private LinearLayout frag4_ll;

		@ViewInject(R.id.frag4_content)
		private TextView frag4_content;

		@ViewInject(R.id.frag4_rl)
		private RelativeLayout frag4_rl;

		@ViewInject(R.id.frag4_picture)
		private ImageView frag4_picture;

		@ViewInject(R.id.frag4_play)
		private ImageView frag4_play;
		
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
	}
	
	public class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {
		
		private Frag4ViewHolder holder;

		public CustomBitmapLoadCallBack(Frag4ViewHolder holder) {
			this.holder = holder;
		}
		
		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			
			float scaleWidth = mContext.getResources().getDisplayMetrics().widthPixels
					/ (float) width;
			if (!map.containsKey(uri)) {
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams)
						holder.frag4_picture.getLayoutParams();
				params.width = mContext.getResources().getDisplayMetrics().widthPixels;
				params.height = (int) (height * scaleWidth);
				holder.frag4_picture.setLayoutParams(params);
				LogUtils.e("load bitmap params.height=" + params.height + ", params.width=" + params.width);
				map.put(uri, params.height);
			}
//			LogUtils.e("(bitmap==null)=" + (bitmap == null) + ", width*height=" + width + "*" + height);
			holder.frag4_picture.setImageBitmap(bitmap);
		}
		
	}
	
	private OnVideoPlayListener playListener;
	public interface OnVideoPlayListener {
		public void playVideo(ArticleBean bean);
	}
	
	public void setVideoPlayListener(OnVideoPlayListener playListener) {
		this.playListener = playListener;
	}
	
	private OnFrag4ItemOperListerner itemListener;
	public interface OnFrag4ItemOperListerner {
		public void dropItemOper(int operType, ArticleBean bean, Frag4ViewHolder holder);
		public void startComment(int contentType, boolean isAds, int pos);
	}
	
	public void setFrag4ItemListener(OnFrag4ItemOperListerner itemListener) {
		this.itemListener = itemListener;
	}
	
	private class DropItemClickListener implements OnClickListener {
		
		private int operType;
		private ArticleBean bean;
		private Frag4ViewHolder holder;

		public DropItemClickListener(int operType, ArticleBean bean, Frag4ViewHolder holder) {
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
			}
		}
		
	}

	public void changeArticlesList(ArticleBean bean) {
		LogUtils.e(bean.toString());
		for (int i = 0; i < list.size(); i++) {
			ArticleBean articleBean = list.get(i);
			if ((articleBean.getArticleId() == bean.getArticleId()) && 
					(articleBean.getArticleName().equalsIgnoreCase(bean.getArticleName()))) {
				list.get(i).setApprove(bean.getApprove());
				list.get(i).setStamp(bean.getStamp());
				list.get(i).setGood(bean.getGood());
				list.get(i).setBad(bean.getBad());
			}
		}
	}

}
