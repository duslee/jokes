package com.ly.duan.ui;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.utils.StringUtils;
import com.sjm.gxdz.R;

@ContentView(R.layout.view_vv)
public class VVActivity extends BaseActivity implements
		MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnPreparedListener {

	@ViewInject(R.id.pb)
	private ProgressBar pb;
	
	@ViewInject(R.id.vv)
	private VideoView mVideoView;
	
	private Uri mUri;
	private int mPositionWhenPaused = -1;
	private SurfaceHolder surfaceHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initData();
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		initUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mVideoView.setVideoURI(mUri);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mPositionWhenPaused >= 0) {
			mVideoView.seekTo(mPositionWhenPaused);
			mPositionWhenPaused = -1;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPositionWhenPaused = mVideoView.getCurrentPosition();
		mVideoView.pause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVideoView.stopPlayback();
	}

	private void initData() {
		if (getIntent() != null && !StringUtils.isBlank(getIntent().getStringExtra("url"))) {
			mUri = Uri.parse(getIntent().getStringExtra("url"));
		} else {
			showToast(R.string.no_url_addr);
			VVActivity.this.finish();
			return;
		}
	}

	private void initUI() {
		surfaceHolder = mVideoView.getHolder();

		mVideoView.setZOrderOnTop(true);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnPreparedListener(this);
		MediaController mc = new MediaController(this);
		mc.setAnchorView(mVideoView);
		mVideoView.setMediaController(mc);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		setVideoLayout(mp);
		pb.setVisibility(View.GONE);
		mVideoView.requestFocus();
		mVideoView.start();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			Log.v("this is error", "Media Error,Server Died===>" + extra);
		} else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
			Log.v("this is error", "Media Error,Error Unknown===>" + extra);
		}
		showToast(R.string.play_video_failed);
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mVideoView.pause();
		mVideoView.seekTo(0);
//		mVideoView.stopPlayback();
	}

	private void setVideoLayout(MediaPlayer mp) {
		LayoutParams lp = mVideoView.getLayoutParams();
		int windowWidth = getResources().getDisplayMetrics().widthPixels;
		int windowHeight = getResources().getDisplayMetrics().heightPixels;
		Log.e("main", "setVideoLayout windowWidth*windowHeight=" + windowWidth
				+ "*" + windowHeight);
		float windowRatio = windowWidth / (float) windowHeight;
		int videoWidth = mp.getVideoWidth();
		int videoHeight = mp.getVideoHeight();
		Log.e("main", "setVideoLayout videoWidth*videoHeight=" + videoWidth
				+ "*" + videoHeight);
		float videoRatio = videoWidth / (float) videoHeight;
		lp.width = (windowRatio > videoRatio) ? (int) (windowHeight * videoRatio)
				: windowWidth;
		lp.height = (windowRatio > videoRatio) ? windowHeight
				: (int) (windowWidth / videoRatio);
		Log.e("main", "setVideoLayout lp.width*lp.height=" + lp.width + "*"
				+ lp.height);
		mVideoView.setLayoutParams(lp);
		surfaceHolder.setSizeFromLayout();
		surfaceHolder.setFixedSize(mp.getVideoWidth(), mp.getVideoHeight());
	}
	
}
