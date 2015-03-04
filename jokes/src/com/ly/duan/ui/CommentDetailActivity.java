package com.ly.duan.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.util.LogUtils;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.bean.BannerBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.ui.fragment.CommentFragment2;
import com.ly.duan.ui.fragment.CommentFragment2.IBottomVisibility;
import com.ly.duan.utils.ActivityAnimator;
import com.sjm.gxdz.R;

public class CommentDetailActivity extends FragmentActivity implements IBottomVisibility {

	private static final int GO_TO_SEND_COMMENT_VIEW = 27;

	private ImageView fresh;
	private RelativeLayout bottom_rl;
	private ViewPager viewPager;
	private CommentPagerAdapter adapter;
	private List<DuanBean> duansList = new ArrayList<DuanBean>();
	private List<BannerBean> bannerList = new ArrayList<BannerBean>();
	private List<ArticleBean> articleList = new ArrayList<ArticleBean>();

	private boolean insertAds;
	private boolean isAds;
	private int pos;
	private int articlePos;

	private boolean hasClick = false;
	private boolean freshEnabled = false;

	private int contentType;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		/* 1. fill view */
		setContentView(R.layout.activity_comment);
		/* 2. init data */
		initData();
		/* 3. init UI */
		initUI();
		/* 4. init Operation */
		initOperation();
	}

	private void initOperation() {
		/* init adapter */
		adapter = new CommentPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);

		/* display item in which pos */
		if (contentType == 3) {
			viewPager.setCurrentItem(articlePos);
		} else if (contentType == 8) {
			viewPager.setCurrentItem(pos);
		}
	}

	private void initUI() {
		/* 1. Init Controller */
		fresh = (ImageView) findViewById(R.id.fresh);
		bottom_rl = (RelativeLayout) findViewById(R.id.bottom_rl);
		viewPager = (ViewPager) findViewById(R.id.viewPager);

		/* 2. set Listener */
		((ImageView) findViewById(R.id.back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CommentDetailActivity.this.finish();
					}
				});
		
		fresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (freshEnabled) {
					return;
				}

				LogUtils.e("start fresh");
				freshEnabled = true;
				fresh.clearAnimation();
				fresh.startAnimation(AnimationUtils.loadAnimation(CommentDetailActivity.this,
						R.anim.rotate_clockwise));
				
				refreshLists();
			}
		});

		/* 3. handle ET */
		((EditText) findViewById(R.id.comment_et))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (hasClick) {
							return;
						}
						hasClick = true;
						LogUtils.d("initUI onTouch currentItem="
								+ viewPager.getCurrentItem());
						goToSendComment();
					}
				});
	}

	private void goToSendComment() {
		Intent intent = new Intent(CommentDetailActivity.this,
				SendCommentActivity.class);
		/* handle params */
		intent.putExtra("contentType", contentType);
		int currItem = viewPager.getCurrentItem();
		if (contentType == 3) {
			intent.putExtra("bean", articleList.get(currItem));
		} else if (contentType == 8) {
			if (!insertAds) {
				intent.putExtra("bean", duansList.get(currItem));
			} else {
				int bannerSize = bannerList.size();
				if (bannerSize == 0) {
					intent.putExtra("bean", duansList.get(currItem));
				} else {
					/* get banner's position */
					if ((bannerSize > 0) /* no banner */
							&& (currItem / 10 < bannerSize) /*
															 * has banner to be
															 * displayed
															 */
							&& (currItem % 10 == 0) /* This pos is banner */
					) {
					} else {
						currItem = currItem - (currItem / 10 + 1);
						intent.putExtra("bean", duansList.get(currItem));
					}
				}
			}
		}
		startActivityForResult(intent, GO_TO_SEND_COMMENT_VIEW);
		new ActivityAnimator().fadeAnimation(CommentDetailActivity.this);
	}

	private void initData() {
		if (getIntent() != null) {
			contentType = getIntent().getIntExtra("contentType", 0);
			/* content of Duans */
			if (contentType == 8) {
				insertAds = getIntent().getBooleanExtra("insertAds", false);
				if (!insertAds) {
					pos = getIntent().getIntExtra("pos", 0);

					/* handle Duans */
					int size = getIntent().getIntExtra("size", 0);
					if (size > 0) {
						for (int i = 0; i < size; i++) {
							duansList.add((DuanBean) getIntent()
									.getSerializableExtra("" + i));
						}
					}
				} else {
					isAds = getIntent().getBooleanExtra("isAds", false);
					pos = getIntent().getIntExtra("pos", 0);

					/* handle Duans */
					int duansSize = getIntent().getIntExtra("duansSize", 0);
					if (duansSize > 0) {
						for (int i = 0; i < duansSize; i++) {
							duansList.add((DuanBean) getIntent()
									.getSerializableExtra("" + i));
						}
					}

					/* handle Banners */
					int bannerSize = getIntent().getIntExtra("bannerSize", 0);
					if (bannerSize > 0) {
						for (int i = 0; i < bannerSize; i++) {
							bannerList.add((BannerBean) getIntent()
									.getSerializableExtra("" + i + duansSize));
						}
					}
				}
			}
			/* content of article */
			else if (contentType == 3) {
				articlePos = getIntent().getIntExtra("articlePos", 0);
				int size = getIntent().getIntExtra("size", 0);
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						articleList.add((ArticleBean) getIntent()
								.getSerializableExtra("" + i));
					}
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == GO_TO_SEND_COMMENT_VIEW && arg1 == Activity.RESULT_OK) {
			LogUtils.d("onActivityResult");
			/* change var hasClick */
			hasClick = false;
//			if ((arg2 != null)) {
//				Boolean backStatus = arg2.getBooleanExtra("backStatus", false);
//				if (backStatus) {
//					refreshLists();
//				}
//			}
		}
	}

	private void refreshLists() {
		((CommentFragment2) adapter.getFragment(viewPager.getCurrentItem())).refreshList();
	}

	class CommentPagerAdapter extends FragmentStatePagerAdapter {
		
		Map<Integer, Fragment> fragments = new HashMap<Integer, Fragment>();

		public CommentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			CommentFragment2 fragment = null;
			int pos = 0;
			LogUtils.e("contentType=" + contentType + ", isAds=" + isAds);
			if (contentType == 3) {
				pos = arg0 % articleList.size();
				fragment = CommentFragment2.newInstance(contentType, articleList.get(pos));
			} else if (contentType == 8) {
				if (!insertAds) {
					pos = arg0 % duansList.size();
					fragment = CommentFragment2.newInstance(contentType,
							duansList.get(pos), insertAds);
				} else {
					int bannerSize = bannerList.size();
					if (bannerSize == 0) {
						pos = arg0 % duansList.size();
						fragment = CommentFragment2.newInstance(contentType,
								duansList.get(pos), insertAds, false);
					} else {
						/* get banner's position */
						if ((bannerSize > 0) /* no banner */
								&& (arg0 / 10 < bannerSize) /*
															 * has banner to be
															 * displayed
															 */
								&& (arg0 % 10 == 0) /* This pos is banner */
						) {
							pos = arg0 / 10;
							fragment = CommentFragment2.newInstance(contentType,
									bannerList.get(pos), insertAds, true);
						}
						/* get duans's position */
						else {
							pos = arg0 - (arg0 / 10 + 1);
							fragment = CommentFragment2.newInstance(contentType,
									duansList.get(pos), insertAds, false);
						}
					}
				}
			}
			LogUtils.e("arg0=" + arg0 + ", pos=" + pos + ", contentType=" + contentType);
			fragments.put(arg0, fragment);
			return fragment;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (contentType == 3) {
				count = articleList.size();
			} else if (contentType == 8) {
				int size = duansList.size();
				if (size / 10 < bannerList.size() - 1) {
					count = size + size / 10 + 1;
				} else {
					count = size + bannerList.size();
				}
			}
			return count;
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
		
		public Fragment getFragment(int arg0) {
			LogUtils.e("position=" + arg0 + ", fragments.containsKey(arg0)="
					+ fragments.containsKey(arg0));
			if (fragments.containsKey(arg0)) {
				return fragments.get(arg0);
			}
			return null;
		}

	}

	@Override
	public void setBottomVisibility() {
		if (contentType == 8 && insertAds) {
			int currItem = viewPager.getCurrentItem();
			int bannerSize = bannerList.size();
			if (bannerSize == 0) {
				return;
			}
			/* get banner's position */
			if ((bannerSize > 0) /* no banner */
					&& (currItem / 10 < bannerSize) /* has banner to be displayed */
					&& (currItem % 10 == 0) /* This pos is banner */
					) {
				fresh.setVisibility(View.GONE);
				bottom_rl.setVisibility(View.GONE);
			} else {
				fresh.setVisibility(View.VISIBLE);
				bottom_rl.setVisibility(View.VISIBLE);
			}
		}		
	}

	@Override
	public void freshFinish() {
		if (freshEnabled) {
			freshEnabled = false;
			fresh.clearAnimation();
		}
	}

}
