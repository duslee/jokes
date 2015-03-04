package com.ly.duan.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ly.duan.bean.ArticleBean;
import com.ly.duan.bean.DuanBean;
import com.ly.duan.utils.Constants;
import com.ly.duan.utils.DeviceUtils;
import com.ly.duan.utils.StringUtils;
import com.sjm.gxdz.R;

@ContentView(R.layout.activity_send_comment)
public class SendCommentActivity extends BaseActivity {
	
	private static final int SEND_COMMENT_FAILED = 83;
	private static final int SEND_COMMENT_SUCCESS = 85;
	private static final int BACK_FRONT_VIEW = 87;
	
	private static final int MAX_COMMENT_NUMS = 1000;
	
	@ViewInject(R.id.back)
	private ImageView back;
	
	@ViewInject(R.id.send)
	private Button send;
	
	@ViewInject(R.id.comment_et)
	private EditText comment_et;
	
	@ViewInject(R.id.comment_extra_nums)
	private TextView comment_extra_nums;
	
	private long appid;
	private int contentType;
	private long contentId;
	
	private boolean startSend = false;
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SEND_COMMENT_FAILED:
				startSend = false;
				break;
				
			case SEND_COMMENT_SUCCESS:
				startSend = false;
				backToFrontView(true);
				break;
				
			case BACK_FRONT_VIEW:
				backToFrontView(false);
				break;

			default:
				break;
			}
		};
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* fill view */
		ViewUtils.inject(this);
		
		/* init data */
		initData();
		
		/* init Operaton */
		initOperation();
	}

	private void initOperation() {
		/* handle ET */
		comment_et.addTextChangedListener(textWatcher);
		
		/* handel send */
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (startSend) {
					return;
				}
				startSend = true;
				sendCommentRequest();
			}
		});
	}
	
	private TextWatcher textWatcher = new TextWatcher() {
		
		private CharSequence temp;
		private int editStart, editEnd;
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			temp = s;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			editStart = comment_et.getSelectionStart();
			editEnd = comment_et.getSelectionEnd();
			if (temp.length() > MAX_COMMENT_NUMS) {
				showToast(R.string.max_comment_nums);
				s.delete(editStart - 1, editEnd);
				int tempSelection = editStart;
				comment_et.setText(s);
				comment_et.setSelection(tempSelection);
				comment_extra_nums.setText("" + 0);
			} else {
				comment_extra_nums.setText("" + (MAX_COMMENT_NUMS - temp.length()));
			}
		}
	};

	private void sendCommentRequest() {
		/* handle ET */
		String comment = comment_et.getText().toString();
		if ((StringUtils.isBlank(comment))
				|| ((!StringUtils.isBlank(comment))
						&& (StringUtils.isBlank(comment.trim())))) {
			LogUtils.d("sendCommentRequest no comment");
			startSend = false;
			showToast(R.string.no_comment);
			return;
		}
		
		LogUtils.d("sendCommentRequest comment=" + comment);
		
		/* send request */
		String imei = DeviceUtils.getIMEI(SendCommentActivity.this);
		String nick = DeviceUtils.getName();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(Constants.KEY_APPID, appid + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_USER_ID, imei));
		nvps.add(new BasicNameValuePair(Constants.KEY_CONTENT_TYPE, contentType + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_CONTENTID, contentId + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_COMMENT, comment + ""));
		nvps.add(new BasicNameValuePair(Constants.KEY_USER_NICK, nick));
		nvps.add(new BasicNameValuePair(Constants.KEY_USER_TYPE, 0 + ""));
		RequestParams params = new RequestParams();
		params.addBodyParameter(nvps);
		//params.addQueryStringParameter(nvps);
		new HttpUtils().send(HttpRequest.HttpMethod.POST, Constants.ACTION_SEND_COMMENTS, params,
				new CommentRequestCallback());
	}

	private void initData() {
		if (null != getIntent()) {
			contentType = getIntent().getIntExtra("contentType", 0);
			if (contentType == 3) {
				ArticleBean bean = (ArticleBean) getIntent().getSerializableExtra("bean");
				appid = bean.getAppid();
				contentId = bean.getArticleId();
			} else if (contentType == 8) {
				DuanBean bean = (DuanBean) getIntent().getSerializableExtra("bean");
				appid = bean.getAppid();
				contentId = bean.getDuanId();
			}
		}
		
		LogUtils.d("contentType=" + contentType + ", appid=" + appid + ", contentId=" + contentId);
	}

	@OnClick(R.id.back)
	public void backClicked(View view) {
		mHandler.sendEmptyMessage(BACK_FRONT_VIEW);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
			mHandler.sendEmptyMessage(BACK_FRONT_VIEW);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void backToFrontView(boolean backStatus) {
		Intent intent = new Intent(SendCommentActivity.this, CommentDetailActivity.class);
		intent.putExtra("backStatus", backStatus);
		setResult(Activity.RESULT_OK, intent);
		SendCommentActivity.this.finish();
	}
	
	private class CommentRequestCallback extends RequestCallBack<String> {

		@Override
		public void onFailure(HttpException error, String msg) {
			showToast(R.string.no_service);
			mHandler.sendEmptyMessage(SEND_COMMENT_FAILED);
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parseResult(responseInfo.result);
		}

		private void parseResult(String result) {
			LogUtils.e(result);
			/* 1. 从服务器响应中获取相应数据，也可保存在本地数据库中 */
			JSONObject jsonObject = null;
			try {
				jsonObject = JSON.parseObject(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (jsonObject == null) {
				LogUtils.e("jsonObject==null");
				return;
			}

			int code = jsonObject.getIntValue("code");
			if (code == 0) {
				showToast(jsonObject.getString("dsc"));
				mHandler.sendEmptyMessage(SEND_COMMENT_SUCCESS);
			} else {
				showToast(jsonObject.getString("dsc"));
				mHandler.sendEmptyMessage(SEND_COMMENT_FAILED);
			}
		}

	}
	
}
