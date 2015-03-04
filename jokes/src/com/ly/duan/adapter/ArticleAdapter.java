package com.ly.duan.adapter;

import java.util.ArrayList;
import java.util.List;

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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.utils.StringUtils;
import com.sjm.gxdz.R;

public class ArticleAdapter extends BaseAdapter {
	
	private final static int IV_HEIGHT = 120;
	private final static int IV_WIDTH = 160;

	private Context mContext;
	private List<ArticleBean> list;
	private int width;
	private int height;
	private BitmapUtils bitmapUtils;
	
	public ArticleAdapter(Context context) {
		super();
		this.mContext = context;
		bitmapUtils = BitmapHelp.getInstance(context);
		list = new ArrayList<ArticleBean>();
		/* init width & height */
		float scale = mContext.getResources().getDisplayMetrics().widthPixels
				/ (float) 480;
		width = (int) (IV_WIDTH * scale);
		height = (int) (IV_HEIGHT * scale);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Frag3ViewHolder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_frag3, null);
			holder = new Frag3ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (Frag3ViewHolder) convertView.getTag();
		}

		/* 填充数据 */
		final ArticleBean bean = list.get(position);
		/* 1. set article name */
		holder.frag3_tv.setText(bean.getArticleName());
//		String articleName = bean.getArticleName();
//		if (!StringUtils.isBlank(articleName)) {
//			holder.frag3_tv.setText(articleName);
//		} else {
//			holder.frag3_tv.setText("");
//		}
		
		/* set item click listener */
		holder.frag3_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != itemListener) {
					itemListener.itemClicked(bean);
				}
			}
		});
		
		/* set IV width & height */
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) 
				holder.frag3_iv.getLayoutParams();
		params.width = width;
		params.height = height;
		holder.frag3_iv.setLayoutParams(params);
		
		/* 从缓存中获取图片，若无，直接根据url去网络下载，并保存在缓存中 */
		String imgUrl = bean.getImgUrl();
		if (!StringUtils.isBlank(imgUrl)) {
			String urlTag = (String) holder.frag3_iv.getTag();
			if (!StringUtils.isBlank(urlTag) && urlTag.equalsIgnoreCase(imgUrl)) {
			} else {
				holder.frag3_iv.setTag(imgUrl);
				bitmapUtils.display(holder.frag3_iv, imgUrl, new CustomBitmapLoadCallback());
			}
		} else {
			holder.frag3_iv.setTag(imgUrl);
			holder.frag3_iv.setImageDrawable(null);
		}
		
		return convertView;
	}

	public void addArticles(List<ArticleBean> _list) {
		list.addAll(_list);
	}

	public void clear() {
		list.clear();
		notifyDataSetChanged();
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
	
	public class Frag3ViewHolder {
		@ViewInject(R.id.frag3_rl)
		private RelativeLayout frag3_rl;
		
		@ViewInject(R.id.frag3_iv)
		private ImageView frag3_iv;
		
		@ViewInject(R.id.frag3_tv)
		private TextView frag3_tv;
	}
	
	public class CustomBitmapLoadCallback extends DefaultBitmapLoadCallBack<ImageView> {

		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			container.setImageBitmap(bitmap);
		}

	}
	
	private OnFrag3ItemClickListener itemListener;
	public interface OnFrag3ItemClickListener {
		public void itemClicked(ArticleBean bean);
	}
	
	public void setFrag3ItemClicked(OnFrag3ItemClickListener itemListener) {
		this.itemListener = itemListener;
	}

}
