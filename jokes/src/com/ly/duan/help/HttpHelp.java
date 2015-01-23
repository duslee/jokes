package com.ly.duan.help;

import java.util.List;

import org.apache.http.NameValuePair;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class HttpHelp {

	private static HttpUtils httpUtils = null;

	public HttpHelp() {
		createHttpUtils();
	}

	private void createHttpUtils() {
		httpUtils = new HttpUtils();
	}

	public void handleHttp(String url, List<NameValuePair> queryStringparams,
			MyRequestCallback callback) {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(queryStringparams);
		httpUtils.send(HttpRequest.HttpMethod.POST, url, params, callback);
	}

	public static class MyRequestCallback extends RequestCallBack<String> {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			closeHttpClient();
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			closeHttpClient();
			error.printStackTrace();
		}

	}

	public static void closeHttpClient() {
		if (null != httpUtils) {
			httpUtils.getHttpClient().getConnectionManager()
					.closeExpiredConnections();
			httpUtils = null;
		}
	}

}
