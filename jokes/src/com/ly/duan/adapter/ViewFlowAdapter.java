package com.ly.duan.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.ly.duan.bean.BannerBean;
import com.ly.duan.help.BitmapHelp;
import com.sjm.gxdz.R;

public class ViewFlowAdapter extends BaseAdapter {

	private Context context;
	private List<BannerBean> list;
	private BitmapUtils bitmapUtils;

	private BitmapDisplayConfig config = null;

	public ViewFlowAdapter(Context context, List<BannerBean> list, int width,
			int height) {
		this.context = context;
		this.list = list;
		bitmapUtils = BitmapHelp.getInstance(context);

		config = new BitmapDisplayConfig();
		config.setBitmapMaxSize(new BitmapSize(width, height));
	}

	public ViewFlowAdapter(Context context, List<BannerBean> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		// 返回很大的值使得getView中的position不断增大来实现循环
		return Integer.MAX_VALUE;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = ((LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.item_vf, null, false);
			holder.item_iv = (ImageView) convertView.findViewById(R.id.item_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		/* 填充数据 */
//		LogUtils.e("position=" + position + ", list.size=" + list.size());
		BannerBean bean = list.get(position % list.size());
//		LogUtils.e("imgUrl=" + bean.getBannerImgUrl());
		if (TextUtils.isEmpty(bean.getBannerImgUrl())) {
		} else {
			// bitmapUtils.display(holder.item_iv, bean.getBannerImgUrl());
			bitmapUtils.display(holder.item_iv, bean.getBannerImgUrl(), config);
		}
		return convertView;
	}

	class ViewHolder {
		private ImageView item_iv;
	}

}
