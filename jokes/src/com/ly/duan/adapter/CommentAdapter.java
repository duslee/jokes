package com.ly.duan.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.bean.CommentBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.utils.FileUtils;
import com.ly.duan.utils.GifUtils;
import com.ly.duan.utils.ImageUtils;
import com.ly.duan.utils.StringUtils;
import com.ly.duan.view.ProgressWheel;
import com.sjm.gxdz.R;

public class CommentAdapter extends BaseAdapter {
	
	private final static int FRAG4_IV_HEIGHT = 200;
	private final static int DUAN_IV_HEIGHT = 400;
	private final static int BANNER_IV_HEIGHT = 200;
	private final static int GIF_HEIGHT = 340;
	
	private Context mContext;
	private BitmapUtils bitmapUtils;
	private List<CommentBean> list;
	
	private ArticleBean articleBean;
	private DuanBean duanBean;
	private int contentType;
	
	private GifUtils gifUtils = null;
	
	public CommentAdapter(Context context) {
		super();
		this.mContext = context;
		gifUtils = new GifUtils(context);
		bitmapUtils = BitmapHelp.getInstance(context);
		list = new ArrayList<CommentBean>();
	}
	
	public void addComments(List<CommentBean> _list) {
		list.addAll(_list);
	}

	public void clear() {
		list.clear();
		notifyDataSetChanged();
	}
	
	public List<CommentBean> getCommentList() {
		return list;
	}
	
	public void setArticleBean(ArticleBean articleBean) {
		this.articleBean = articleBean;
	}
	
	public void setDuanBean(DuanBean duanBean) {
		this.duanBean = duanBean;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	@Override
	public int getCount() {
//		return list.size();
		return list.size() + 1;
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
		LogUtils.d("getView position=" + position + ", contentType=" + contentType);
		
		CommentViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
			holder = new CommentViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (CommentViewHolder) convertView.getTag();
		}
		
		if (position == 0) {
			holder.ll1.setVisibility(View.VISIBLE);
			holder.ll2.setVisibility(View.GONE);
			
			if (contentType == 3) {
				holder.frag4_ll.setVisibility(View.VISIBLE);
				holder.duan_ll.setVisibility(View.GONE);
				
				displayArticleView(holder);
			} else if (contentType == 8) {
				holder.frag4_ll.setVisibility(View.GONE);
				holder.duan_ll.setVisibility(View.VISIBLE);
				
				displayDuansView(holder);
			}
		} else {
			holder.ll1.setVisibility(View.GONE);
			holder.ll2.setVisibility(View.VISIBLE);
			
			position--;
			CommentBean bean = list.get(position);
		
			/* display avatar */
			String avatarUrl = bean.getUserVar();
			if (!StringUtils.isBlank(avatarUrl)) {
				String urlTag = (String) holder.comment_iv.getTag();
				if (!StringUtils.isBlank(urlTag)
						&& urlTag.equalsIgnoreCase(avatarUrl)) {
				} else {
					holder.comment_iv.setTag(avatarUrl);
					bitmapUtils.display(holder.comment_iv, avatarUrl,
							new CustomAvatarLoadCallback());
				}
			} else {
				holder.comment_iv.setTag(avatarUrl);
				holder.comment_iv.setImageDrawable(null);
			}

			/* display TextView */
			String title = bean.getUserNick();
			if (!StringUtils.isBlank(title)
					&& !StringUtils.isBlank(title.trim())) {
				holder.comment_title.setText(title.trim());
			} else {
				holder.comment_title.setText("");
			}

			String time = bean.getCreateTime();
			if (!StringUtils.isBlank(time) && !StringUtils.isBlank(time.trim())) {
				String replaceTime = time.trim().replace("T", " ")
						.substring(5, 16);
				// LogUtils.d("replaceTime=" + replaceTime);
				holder.comment_time.setText(replaceTime);
			} else {
				holder.comment_time.setText("");
			}

			String content = bean.getComment();
			if (!StringUtils.isBlank(content)
					&& !StringUtils.isBlank(content.trim())) {
				holder.comment_content.setText(content.trim());
			} else {
				holder.comment_content.setText("");
			}

			/* handle approve */
		}
		
		return convertView;
	}
	
	private void displayDuansView(CommentViewHolder holder) {
		/* display nick & content */
		holder.duan_nick.setText(duanBean.getNick());
		String content = duanBean.getContent();
		if (!StringUtils.isBlank(content)) {
			holder.duan_content.setText(content);
		} else {
			holder.duan_content.setVisibility(View.GONE);
		}

		/* display avatar */
		String avatarUrl = duanBean.getAvatarUrl();
		if (!StringUtils.isBlank(avatarUrl)) {
			bitmapUtils.display(holder.duan_avatar, avatarUrl,new CustomAvatarLoadCallback());
		} else {
			holder.duan_avatar.setVisibility(View.GONE);
		}

		/* display iv & gif */
		String imgUrl = duanBean.getImgUrl();
		if (!StringUtils.isBlank(imgUrl)) {
			int index = imgUrl.lastIndexOf(".");
			if (imgUrl.substring(index + 1).equalsIgnoreCase("gif")) {
				holder.duan_picture.setVisibility(View.GONE);
				holder.duan_gif.setVisibility(View.VISIBLE);
				holder.duan_pw.setVisibility(View.VISIBLE);

				setGifViewHeight(holder.duan_gif, GIF_HEIGHT);

				displayGif(imgUrl, holder.duan_gif, holder.duan_pw);
			} else {
				holder.duan_picture.setVisibility(View.VISIBLE);
				holder.duan_gif.setVisibility(View.GONE);

				setIVHeight(holder.duan_picture, DUAN_IV_HEIGHT);

				bitmapUtils.display(holder.duan_picture, imgUrl, new CustomBitmapLoadCallBack(mContext));
			}
		} else {
			holder.duan_rl.setVisibility(View.GONE);
		}
	}

	private void displayGif(String url, GifImageView gifView, ProgressWheel duan_pw) {
		String path = gifUtils.getDiskCachePath();
		String fileName = gifUtils.getFileName(url);
		try {
			FileUtils.createDir(path);
			File file = new File(path + File.separator + fileName);
			// LogUtils.e("path=" + path + ", fileName=" + fileName
			// + ", AbsolutePath=" + file.getAbsolutePath());
			if (file.exists()) {
				// LogUtils.e("111111 file.exists=" + file.exists());
				duan_pw.setVisibility(View.GONE);
				GifDrawable dr = new GifDrawable(file);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) gifView
						.getLayoutParams();
				int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
				params.width = screenWidth;
				params.height = (int) (dr.getIntrinsicHeight() * screenWidth / (float) dr
						.getIntrinsicWidth());
				gifView.setLayoutParams(params);
				gifView.setImageDrawable(dr);
			} else {
				// LogUtils.e("222222 file.exists=" + file.exists());
				gifView.setImageDrawable(null);

				file.createNewFile();
				startDownFile(url, file.getAbsolutePath(), gifView, duan_pw);
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
			final GifImageView gifView, final ProgressWheel duan_pw) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.download(url, filePath, new RequestCallBack<File>() {

			@Override
			public void onStart() {
				duan_pw.resetCount();
				duan_pw.setProgress(0);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				float rate = current / (float) total;
				duan_pw.setProgress((int) (360 * rate));
				duan_pw.setText(((int) (100 * rate)) + "%");
			}

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				duan_pw.setProgress(360);
				duan_pw.setVisibility(View.GONE);
				String filePath = responseInfo.result.getAbsolutePath();
				try {
					GifDrawable dr = new GifDrawable(filePath);
					RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) gifView
							.getLayoutParams();
					int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
					params.width = screenWidth;
					params.height = (int) (dr.getIntrinsicHeight() * screenWidth / (float) dr.getIntrinsicWidth());
					gifView.setLayoutParams(params);
					gifView.setImageDrawable(dr);
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
	
	private void setGifViewHeight(GifImageView gifView, int height) {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) gifView
				.getLayoutParams();
		params.width = mContext.getResources().getDisplayMetrics().widthPixels;
		params.height = height;
		gifView.setLayoutParams(params);
	}

	private void displayArticleView(CommentViewHolder holder) {
		/* display name */
		String articleName = articleBean.getArticleName();
		if (!StringUtils.isBlank(articleName)) {
			holder.frag4_content.setText(articleName);
		} else {
			holder.frag4_content.setVisibility(View.GONE);
		}

		/* display picture */
		String imgUrl = articleBean.getImgUrl();
		if (!StringUtils.isBlank(imgUrl)) {
			bitmapUtils.display(holder.frag4_picture, imgUrl, new CustomAvatarLoadCallback());
		} else {
			setIVHeight(holder.frag4_picture, FRAG4_IV_HEIGHT);
			holder.frag4_picture.setImageDrawable(null);
		}

		/* set play listener */
		holder.frag4_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				playVideo();
				if (null != videoListener) {
					videoListener.playVideo();
				}
			}
		});
	}
	
	private void setIVHeight(ImageView iv, int height) {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) iv
				.getLayoutParams();
		params.width = mContext.getResources().getDisplayMetrics().widthPixels;
		params.height = height;
		iv.setLayoutParams(params);
	}

	public class CommentViewHolder {
		/* ll1 */
		@ViewInject(R.id.ll1)
		private LinearLayout ll1;
		
		@ViewInject(R.id.comment_fl)
		private FrameLayout comment_fl;
		
		/* duans */
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
		
		@ViewInject(R.id.duan_pw)
		private ProgressWheel duan_pw;
		
		@ViewInject(R.id.duan_picture)
		private ImageView duan_picture;
		
		@ViewInject(R.id.duan_gif)
		private GifImageView duan_gif;
		
		/* frag4 ll */
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
		
		/* comment ll2 */
		@ViewInject(R.id.comment_ll2)
		private LinearLayout comment_ll2;
		
		@ViewInject(R.id.comment_tv)
		private TextView comment_tv;
		
		/* ll2 */
		@ViewInject(R.id.ll2)
		private LinearLayout ll2;
		
		@ViewInject(R.id.comment_iv)
		private ImageView comment_iv;
		
		@ViewInject(R.id.comment_title)
		private TextView comment_title;
		
		@ViewInject(R.id.comment_time)
		private TextView comment_time;
		
		@ViewInject(R.id.approve_rl)
		private RelativeLayout approve_rl;
		
		@ViewInject(R.id.up_iv)
		public ImageView up_iv;
		
		@ViewInject(R.id.approve_nums)
		public TextView approve_nums;
		
		@ViewInject(R.id.add_tv)
		public TextView add_tv;
		
		@ViewInject(R.id.comment_content)
		private TextView comment_content;
	}
	
	private class CustomAvatarLoadCallback extends DefaultBitmapLoadCallBack<ImageView> {

		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			Bitmap displayBitmap = ImageUtils.toRoundCorner(bitmap, 10);
			container.setImageBitmap(displayBitmap);
		}

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
			params.width = context.getResources().getDisplayMetrics().widthPixels;
			params.height = (int) (height * scaleWidth);
			container.setLayoutParams(params);
			container.setImageBitmap(bitmap);
		}

	}
	
	private OnPlayVideoListener videoListener;

	public void setVideoListener(OnPlayVideoListener videoListener) {
		this.videoListener = videoListener;
	}

	public interface OnPlayVideoListener {
		public void playVideo();
	}

}
