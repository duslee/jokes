package com.common.as.network.httpclient;

import java.util.ArrayList;

import android.content.Context;

import com.common.as.base.log.BaseLog;
import com.common.as.network.httpclient.MPHttpClient.MPHttpClientResponseListenerInner;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientRequest;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientRespListener;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientResponse;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.utils.Utils;

public class MPHttpClientManager implements MPHttpClientResponseListenerInner{
	public static class MPRecord {
		public int mId;
		public MPHttpClientRespListener mListener;
		public MPHttpClient mClient;
	}
	
	private final Context ctx;
	
	
	
	private MPHttpClientManager(Context ctx) {
		super();
		this.ctx = ctx;
	}
	private static MPHttpClientManager mManager = null;
	
	private final ArrayList<MPRecord> mListeners = new ArrayList<MPRecord>();
	
	protected void addRecord(
			MPHttpClientRespListener listener, int id, MPHttpClient client){
		MPRecord rec = new MPRecord();
		rec.mId = id;
		rec.mListener = listener;
		rec.mClient = client;
		
		mListeners.add(rec);
	}
	
	public void removeListener(MPHttpClientRespListener listener, int id){
		for (MPRecord rec: mListeners){
			if (rec.mListener == listener && rec.mId == id){
				mListeners.remove(rec);
			}
		}
	}
	
	public void removeListener(MPHttpClientRespListener listener){
		for (MPRecord rec: mListeners){
			if (rec.mListener == listener){
				mListeners.remove(rec);
				break;
			}
		}
	}

	@Override
	public void onResponseInner(
			MPHttpClient client, int errId, int statusId,MPHttpClientData obj) {
		// TODO Auto-generated method stub
		for (MPRecord rec: mListeners){
			if (rec.mClient == client && rec.mListener != null){
				rec.mListener.onMPHttpClientResponse(rec.mId, errId, statusId, obj);
				mListeners.remove(rec);
				return;
			}
		}
	}
	
	
	public static final int HIGH_ID = 1000; //优先级
	public void doRequest(MPHttpClientRespListener listener, int id, 
			MPHttpClientRequest gen, MPHttpClientResponse parser){
		MPHttpClient client = MPHttpClient.createMPHttpClient(gen, parser, this);


		addRecord(listener, id, client);
		if (Utils.getAPNType(ctx) == -1) {
			client.dipatchResponse(MPHttpClient.MPHTTP_RESULT_ERR_NET, 0, null);
			return;
		}
		
		BaseLog.d("login man", "doRequest name" + gen.getClass().getName());
		if (id >= HIGH_ID) {
			client.doRequest(true);
		} else {
			client.doRequest(false);
		}
		
		
		
	}
	
	public static synchronized MPHttpClientManager getInstance(Context ctx){
		if (mManager == null){
			mManager = new MPHttpClientManager(ctx);
		}
		
		return mManager;
	}

}
