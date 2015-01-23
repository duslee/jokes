package com.ly.duan.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class DummyTabContent implements TabContentFactory {

	private Context mContext;

	public DummyTabContent(Context context) {
		this.mContext = context;
	}

	@Override
	public View createTabContent(String tag) {
		return new View(mContext);
	}
}
