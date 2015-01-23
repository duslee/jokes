package com.ly.duan.ui.fragment;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ly.duan.help.BitmapHelp;
import com.ly.duan.utils.FileUtils;
import com.sjm.gxdz.R;

public class Fragment4 extends BaseFragment {

	@ViewInject(R.id.cache_tv)
	private TextView cacheTV;

	@ViewInject(R.id.cache_rl)
	private RelativeLayout rl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		displayCacheSize();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_frag4, container, false);
		ViewUtils.inject(this, view);

		/* display cache size */
		displayCacheSize();

		/* set RelativeLayout listener */
		setListener();

		return view;
	}

	private void displayCacheSize() {
		String cachePath = FileUtils.getCachePath(getActivity());
		long dirSize = FileUtils.getFileSize2(cachePath);
		LogUtils.e("cachePath=" + cachePath + ", dirSize=" + dirSize);
		String cacheText = "";
		if (dirSize < 1024) {
			float size = dirSize / (float) 1024;
			cacheText = String.format(
					getActivity().getResources().getString(
							R.string.clean_cache_text), size, "K");
		} else if (dirSize < 1024 * 1024) {
			float size = dirSize / (float) 1024;
			cacheText = String.format(
					getActivity().getResources().getString(
							R.string.clean_cache_text), size, "K");
		} else if (dirSize < 1024 * 1024 * 1024) {
			float size = dirSize / (float) (1024 * 1024);
			cacheText = String.format(
					getActivity().getResources().getString(
							R.string.clean_cache_text), size, "M");
		}
		LogUtils.e("cachePath=" + cachePath + ", cacheText=" + cacheText);

		cacheTV.setText(cacheText);
	}

	private void setListener() {
		rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
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
		BitmapHelp.clearAllCache(getActivity());
		deleteAllGif(getActivity());
		// deleteAllFile(getActivity());
		/* display cache size */
		displayCacheSize();
	}

	private void deleteAllFile(Context context) {
		try {
			FileUtils.delAllFile(FileUtils.getCachePath(context));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void deleteAllGif(Context context) {
		try {
			FileUtils.delAllFile(FileUtils.getCachePath(context, "gif"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
