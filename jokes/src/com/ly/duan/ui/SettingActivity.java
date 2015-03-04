package com.ly.duan.ui;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.utils.FileUtils;
import com.sjm.gxdz.R;

@ContentView(R.layout.view_settings)
public class SettingActivity extends BaseActivity {
	
	@ViewInject(R.id.back)
	private ImageView back;
	
	@ViewInject(R.id.cache_rl)
	private RelativeLayout cache_rl;
	
	@ViewInject(R.id.cache_tv)
	private TextView cache_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 1. Dependency inject */
		ViewUtils.inject(this);
		
		/* 2. display cache size */
		displayCacheSize();
		
		/* 3. set RelativeLayout listener */
		setListener();
	}
	
	@OnClick(R.id.back)
	public void backClicked(View view) {
		SettingActivity.this.finish();
	}
	
	private void displayCacheSize() {
		String cachePath = FileUtils.getCachePath(SettingActivity.this);
		long dirSize = FileUtils.getFileSize2(cachePath);
		LogUtils.e("cachePath=" + cachePath + ", dirSize=" + dirSize);
		String cacheText = "";
		if (dirSize < 1024) {
			float size = dirSize / (float) 1024;
			cacheText = String.format(getResources().getString(
							R.string.clean_cache_text), size, "K");
		} else if (dirSize < 1024 * 1024) {
			float size = dirSize / (float) 1024;
			cacheText = String.format(getResources().getString(
							R.string.clean_cache_text), size, "K");
		} else if (dirSize < 1024 * 1024 * 1024) {
			float size = dirSize / (float) (1024 * 1024);
			cacheText = String.format(getResources().getString(
							R.string.clean_cache_text), size, "M");
		}
		LogUtils.e("cachePath=" + cachePath + ", cacheText=" + cacheText);

		cache_tv.setText(cacheText);
	}

	private void setListener() {
		cache_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SettingActivity.this)
						.setTitle(R.string.clean_cache_title)
						.setMessage(R.string.clean_cache_content)
						.setPositiveButton(R.string.yes_btn,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										cleanCache();
									}
								})
						.setNegativeButton(R.string.no_btn,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();

			}
		});
	}

	private void cleanCache() {
		/* clean cache about img & gif */
		BitmapHelp.clearAllCache(SettingActivity.this);
		deleteAllGif(SettingActivity.this);
		// deleteAllFile(getActivity());
		/* display cache size */
		displayCacheSize();
	}

//	private void deleteAllFile(Context context) {
//		try {
//			FileUtils.delAllFile(FileUtils.getCachePath(context));
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//	}

	private void deleteAllGif(Context context) {
		try {
			FileUtils.delAllFile(FileUtils.getCachePath(context, "gif"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
}
