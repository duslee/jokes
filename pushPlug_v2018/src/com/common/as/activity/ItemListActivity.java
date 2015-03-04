package com.common.as.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.common.as.base.log.BaseLog;
import com.common.as.network.HttpUtil;
import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushInfoActionPaser;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.service.BackService;
import com.common.as.store.AppListManager;
import com.common.as.store.AppListManager.OnListChangeListener;
import com.common.as.store.PushInfos;
import com.common.as.utils.AppListUtils;
import com.common.as.utils.AppPrefs;
import com.common.as.utils.AppUtil;
import com.common.as.utils.PopupUtils;
import com.common.as.view.AsyncImageView;
import com.example.pushplug.R;

public class ItemListActivity extends Activity{

	ListView mList;
	AdapterList mAdapterList;
	private View mleftBtn;
	OnListChangeListener mOnListChangeListener;
	ArrayList<PushInfo> infos = new ArrayList<PushInfo>();
	ArrayList<PushInfo> nouse_infos = new ArrayList<PushInfo>();
	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itemlist);
		
		mList = (ListView)findViewById(R.id.listView1);
		mleftBtn = findViewById(R.id.leftBtn);
		mleftBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		infos = AppListManager.getApplists(AppListManager.FLAG_APP_LIST);
		if(null != infos){
			if(infos.size()!=0){
				for (PushInfo pushInfo : infos) {
					if (pushInfo.getPackageName().equals(ItemListActivity.this.getPackageName())) {
						nouse_infos.add(pushInfo);
						//break;
					}
				}
				infos.removeAll(nouse_infos);
			}
			if(infos.size()==0){
				createProgressDialog(ItemListActivity.this);
			}
		}else{
			createProgressDialog(ItemListActivity.this);
		}
		mAdapterList = new AdapterList(infos);
		mList.setAdapter(mAdapterList);
		mOnListChangeListener = new OnListChangeListener() {
			
			@Override
			public void onDataChange(Object obj) {
				// TODO Auto-generated method stub
				mAdapterList.notifyDataChanged((ArrayList<PushInfo>) obj);
				if(pd!=null){
					pd.cancel();
				}
			}
		};
		AppListManager.addListener(mOnListChangeListener);
		
		
		HttpUtil mHttpUtil = new HttpUtil(this);
		HttpUtil.RequestData mRequestData = new HttpUtil.RequestData(HttpUtil.KEY_STORE_LIST) {
			
			@Override
			public void onSuccess(int what, Object obj) {
				// TODO Auto-generated method stub
				if (obj != null) {
					//PopupUtils.showShortToast(getApplicationContext(), obj.toString());

				}else{
					PopupUtils.showShortToast(getApplicationContext(), "no data");

				}
				if(pd!=null){
					pd.cancel();
				}
				
			}
			
			@Override
			public void onFailed(int what, Object obj) {
				// TODO Auto-generated method stub
				PopupUtils.showShortToast(getApplicationContext(), "connected failed");
				if(pd!=null){
					pd.cancel();
				}
			}
			
		};
		mHttpUtil.startRequest(mRequestData);
	}
	@Override
	protected void onResume() {
		super.onResume();
		AppPrefs.isListActivity = true;
		BackService.removeTopView();
		AppListUtils.HideApplist();
		BaseLog.d("main3", "ItemListActivity.onResume");
	}
	private void createProgressDialog(Context context){
		BaseLog.v("main", "ItemListActivity.createProgressDialog");
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("正在获取数据...");
		pd.show();
	}
	@Override
	protected void onPause() {
		super.onPause();
		BaseLog.d("main3", "ItemListActivity.onPause");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		BaseLog.d("main3", "ItemListActivity.onRestart");
		if(null!=mAdapterList){
			mAdapterList.notifyDataSetChanged();
		}

	}
	private class AdapterList extends BaseAdapter implements OnClickListener{

		private ArrayList<PushInfo>  pushInfos;
		private final String packageName;
		
	     public  class ViewHolder {
	         public AsyncImageView photoAsynImg;
	         public TextView title;
	         public TextView brief;
	         public Button btn;
	     }
		
		
		public AdapterList(ArrayList<PushInfo> pushInfos) {
			super();
			packageName = ItemListActivity.this.getPackageName();
			if (null != pushInfos) {
				this.pushInfos = pushInfos;
			} else {
				this.pushInfos = new ArrayList<PushInfo>();
			}
			
		}
		
		public void notifyDataChanged(ArrayList<PushInfo> temps){
			pushInfos.clear();
			pushInfos.addAll(temps);
			
			for (PushInfo pushInfo : pushInfos) {
				if (pushInfo.getPackageName().equals(packageName)) {
					pushInfos.remove(pushInfo);
					break;
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pushInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pushInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if (null == convertView) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_down_item, null);
				viewHolder = new ViewHolder();
				viewHolder.photoAsynImg = (AsyncImageView)convertView.findViewById(R.id.icon);
				viewHolder.title = (TextView)convertView.findViewById(R.id.title);
				viewHolder.brief = (TextView)convertView.findViewById(R.id.brief);
				viewHolder.btn=(Button)convertView.findViewById(R.id.btn);					
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			PushInfo pi = (PushInfo)getItem(position);
			viewHolder.title.setText(pi.getAppName());
			viewHolder.photoAsynImg.setUrl(pi.getImageUrl());
			viewHolder.brief.setText(pi.getmBrief());
			viewHolder.brief.setMovementMethod(ScrollingMovementMethod.getInstance());
			setBtnStatus(viewHolder.btn,pi);
			return convertView;
		}
		
		
		private void setBtnStatus(Button btn ,PushInfo pi){
			if (AppUtil.isInstalled(ItemListActivity.this, pi.getPackageName())) {
				btn.setText("启动");
			}else{
				PushInfo tempPi = PushInfos.getInstance().get(pi.getPackageName());
				if (null != tempPi && tempPi.getStatus() == PushInfo.STATUS_DOWN_FINISH
						&& tempPi.isFileExist()) {
					btn.setText("安装");
				} else {
					btn.setText("下载");
				}
				
			}
			btn.setTag(pi);
			btn.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			PushInfo pi = (PushInfo)v.getTag();
			pi.setPushType(PushType.TYPE_STORE_LIST);
			BaseLog.d("main", "ItemListActivity.onClick.pi="+pi.getPackageName());
			PushInfo temp = PushInfos.getInstance().get(pi.getPackageName());
			
			if (temp == null) {
				PushInfos.getInstance().put(pi.getPackageName(), pi);
			}else{
				temp.setPushType(PushType.TYPE_STORE_LIST);
				PushInfos.getInstance().put(pi.getPackageName(), temp);
			}
			
			PushInfoActionPaser.doClick(ItemListActivity.this, PushType.TYPE_STORE_LIST, pi.getPackageName());
		}
		
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppListManager.removeListener(mOnListChangeListener);
		AppPrefs.isListActivity = false;
	}

	
	
}
