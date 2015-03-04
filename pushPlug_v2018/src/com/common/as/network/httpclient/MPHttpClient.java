package com.common.as.network.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.common.as.base.log.BaseLog;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientRequest;
import com.common.as.network.httpclient.MPHttpClientInterface.MPHttpClientResponse;
import com.common.as.network.utils.ApplicationNetworkUtils;
import com.common.as.utils.DateUtil;
import com.common.as.utils.Preferences;
import com.common.as.utils.SignedUtils;
import com.common.as.utils.ThreadPoolsInterface;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MPHttpClient{
	public static interface MPHttpClientResponseListenerInner{
		public void onResponseInner(MPHttpClient client, int errId, int statusId, MPHttpClientData obj);
	}

	private static final String TAG = "MPHttpClient";
	
	private static final String CHARSET = HTTP.UTF_8;
	
	public static final int MPHTTP_RESULT_SUCCESS = 0;
	public static final int MPHTTP_RESULT_ERR_PROTOCOL_E = -1;
	public static final int MPHTTP_RESULT_ERR_IO_E = -2;
	public static final int MPHTTP_RESULT_ERR_SERVER = -3;
	public static final int MPHTTP_RESULT_ERR_OTHER = -100;
	public static final int MPHTTP_RESULT_ERR_NET = -101;
	
	private static final int MPHTTP_MSG_RESPONSE = 10001;
	
	private static HttpClient mHttpClient = null;
	
	private MPHttpClientRequest mHttpGenerator;
	private MPHttpClientResponse mHttpParser;
	private MPHttpClientResponseListenerInner mRespListenerInner;
	private ThreadPoolsInterface mThreadManager;
	public void setHttpRequest(MPHttpClientRequest gen){
		mHttpGenerator = gen;
	}
	
	public void setHttpParser(MPHttpClientResponse parser){
		mHttpParser = parser;
	}
	
	public void setHttpListenerInner(MPHttpClientResponseListenerInner listenerInner){
		mRespListenerInner = listenerInner;
	}
	
	
	static class MPHttpHandler extends Handler{
		WeakReference<MPHttpClient> mClient;
		
		public MPHttpHandler(MPHttpClient client){
			mClient = new WeakReference<MPHttpClient>(client);
		}
		
		@Override
		public void handleMessage(Message msg){
			MPHttpClient httpClient = mClient.get();
			switch(msg.what){
			case MPHTTP_MSG_RESPONSE:
				if (null != httpClient && httpClient.mRespListenerInner != null){
					int errId = msg.arg1;
					int statusId = msg.arg2;
					MPHttpClientData clientData = (MPHttpClientData)msg.obj;
					httpClient.mRespListenerInner
							.onResponseInner(httpClient, errId, statusId, clientData);
				}
				break;
			}
		}
	}
	
	
	private void saveFirstServerT(Context ctx){
		long firstT = Preferences.getLong(ctx, Preferences.FirstGetServerT, 0);
		if (0l == firstT) {
			Preferences.setLong(ctx, Preferences.FirstGetServerT, DateUtil.getCurrentMs());
		}

	}
	
	private final MPHttpHandler mHandler = new MPHttpHandler(this);
	
//	private Thread mHttpThread;
	private final Runnable mHttpRunable = new Runnable(){
		
		public boolean executeOneTime(HttpUriRequest request){
			int errId = MPHTTP_RESULT_ERR_OTHER;
			int statusId = 0;
			MPHttpClientData clientData = null; 
			HttpEntity entity  = null;
			InputStream is = null;
			try{
				HttpResponse response = mHttpClient.execute(request);
				BaseLog.d("main1", "MPHttpClient.response.getFirstHeader(Date)="+response.getFirstHeader("Date"));
				SignedUtils.serverdate = response.getFirstHeader("Date").getValue();
				DateUtil.setServerT(response.getFirstHeader("Date"));
				saveFirstServerT(ApplicationNetworkUtils.getInstance().getAppCtx());
				Header  tokenH = response.getFirstHeader("Content-Length");
				statusId = response.getStatusLine().getStatusCode();
				BaseLog.d("main1", "statusId="+statusId);
				
				 if (statusId == HttpStatus.SC_OK){
					 errId = MPHTTP_RESULT_SUCCESS;
					 if (null != tokenH) {
							//BaseLog.d("getKeyString", "r set length="+tokenH.getValue());

						 mHttpParser.setKey(tokenH.getValue());
					}
					 
					  entity = response.getEntity();
					  if (entity != null) {
						  entity = new BufferedHttpEntity(entity);
					}
					  
					  is = entity.getContent();
					 clientData = mHttpParser.phraseData(entity, errId, statusId);
					// clientData = mHttpParser.phraseJason(jason, errId, statusId);
				
				 
				 }else{
					  entity = response.getEntity();
					  if (entity != null) {
						  is = entity.getContent();
					}
					  
					  
					 errId = MPHTTP_RESULT_ERR_SERVER;
				 }
			}catch (NoHttpResponseException e){
				e.printStackTrace();	
				return false;
			}catch (ClientProtocolException e) {
				e.printStackTrace();	
				errId = MPHTTP_RESULT_ERR_PROTOCOL_E;
			} catch (IOException e) {
				e.printStackTrace();
				errId = MPHTTP_RESULT_ERR_IO_E;
				return false;
			}catch (Exception e) {
				e.printStackTrace();
				errId = MPHTTP_RESULT_ERR_IO_E;
				return false;
			}finally{
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			dipatchResponse(errId, statusId, clientData);
			return true;
		}
		
		@Override
		public void run() {
			int retryTimes = 2;	
			HttpUriRequest request = null;
			try {
				request = mHttpGenerator.getHttpRequest();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dipatchResponse(MPHTTP_RESULT_ERR_IO_E, 0, null);
				BaseLog.d("main1", "run.e="+e.toString());
				BaseLog.d("main1", "finish run1");
				return;
			}
			
			if (mHttpClient == null){
				intHttpClient();
			}
			 
			
			
			setCookies(request);
			
			while (retryTimes > 0){
				 BaseLog.d("login man", "want request:"+retryTimes+";"+request.getRequestLine().toString());

				if (executeOneTime(request)){
					break;
				}
				
				retryTimes--;
			}
			
			if (retryTimes == 0){
				dipatchResponse(MPHTTP_RESULT_ERR_IO_E, 0, null);
			}
			BaseLog.d("login man", "finish run "+request.getRequestLine().toString());
		}
		
	};
	
	public void dipatchResponse(int errId, int statusId, MPHttpClientData clientData){
		Message msg = 
			mHandler.obtainMessage(MPHTTP_MSG_RESPONSE, errId, statusId, (Object)clientData);
		mHandler.sendMessage(msg);
	}
	
	private void intHttpClient(){
		 HttpParams params = new BasicHttpParams();
         // 设置一些基本参数
         HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
         HttpProtocolParams.setContentCharset(params,
                 CHARSET);
         HttpProtocolParams.setUseExpectContinue(params, true);
         HttpProtocolParams
                 .setUserAgent(
                         params,
                         "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                 + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
         // 超时设置
         /* 从连接池中取连接的超时时间 */
         ConnManagerParams.setTimeout(params, 20*1000);//180 Allan 20131025
         /* 连接超时 */
         HttpConnectionParams.setConnectionTimeout(params, 20*1000);
         /* 请求超时 */
         HttpConnectionParams.setSoTimeout(params, 8*1000);
      // 增加最大连接到100
      ConnManagerParams.setMaxTotalConnections(params, 100);
      // 增加每个路由的默认最大连接到20
      ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
      // 对localhost:80增加最大连接到50
      ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
       
         // 设置我们的HttpClient支持HTTP和HTTPS两种模式
         SchemeRegistry schReg = new SchemeRegistry();
         schReg.register(new Scheme("http", PlainSocketFactory
                 .getSocketFactory(), 80));
         schReg.register(new Scheme("https", 
        		 SSLSocketFactory.getSocketFactory(), 443)); 

         // 使用线程安全的连接管理来创建HttpClient
         ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                 params, schReg);
         
         mHttpClient = new DefaultHttpClient(conMgr, params);

	}
	
	public void doRequest(boolean isHigh){
//		mHttpThread = new Thread(mHttpRunable, "doRequest#");
//		mHttpThread.start();
		if (null == mThreadManager) {
	         mThreadManager = ApplicationNetworkUtils.getInstance().getmThreadManager();
	         if (null == mThreadManager) {
	        	 mThreadManager = ExecuterServerManager.getInstance();
			}
	        		 
		}
		mThreadManager.submit(mHttpRunable,isHigh);
	}
	
	public static MPHttpClient createMPHttpClient(MPHttpClientRequest gen, MPHttpClientResponse parser,
			MPHttpClientResponseListenerInner listenerInner){
		
		MPHttpClient httpClient = new MPHttpClient();
		
		httpClient.setHttpRequest(gen);
		httpClient.setHttpParser(parser);
		httpClient.setHttpListenerInner(listenerInner);
		
		return httpClient;
	}
	
	protected void setCookies(HttpUriRequest request){
		//String value = LoginHttpResp.getSessionId();
		
//	BaseLog.d(TAG, "Cookie:" + value);
		
//		if (value != null){
//			request.addHeader("Cookie", value);
//		}
	}
	
	
	
	// Warning: 
	// 这里只对Cookie做了简单的处理，可能会出现Cookie信息导致服务器无法正确的业务处理。
	public static String getCookiesAttrbute(){
		String s = null;
		List<Cookie> cookies = ((DefaultHttpClient)mHttpClient)
				.getCookieStore().getCookies();
		
		
	     String domain ="";
		for (int i = 0; i < cookies.size(); i++){
			Cookie cookie = cookies.get(i);
			
			
			domain = cookie.getDomain();
			if (domain != null && !MPHttpClientUtils.ROOM_SERVER_URL.contains(domain)) {
				continue;
			}
			if (s != null){
				s += "; " + cookie.getName() + "=" + cookie.getValue();
			}else{
				s = cookie.getName() + "=" + cookie.getValue();
			}	
		}
		
		return s;
	}
	
	public static String getSessionId(){
		List<Cookie> cookies = ((DefaultHttpClient)mHttpClient)
				.getCookieStore().getCookies();
		String name;
		
		for (int i = 0; i < cookies.size(); i++){
			Cookie cookie = cookies.get(i);
			name = cookie.getName().toLowerCase();
			if (name.contains("session")){
				return cookie.getValue();
			}	
		}
		return "";
	
	}
	
	public static void terminateHttpClient(){
		if (mHttpClient != null){
			mHttpClient.getConnectionManager().shutdown();
		}
	}
	
}
