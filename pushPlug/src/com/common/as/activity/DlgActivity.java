package com.common.as.activity;

import com.common.as.pushtype.PushInfo;
import com.common.as.pushtype.PushInfoActionPaser;
import com.common.as.pushtype.PushUtil;
import com.common.as.pushtype.PushUtil.PushType;
import com.common.as.store.PushInfos;
import com.example.pushplug.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class DlgActivity extends Activity implements OnClickListener{


	Handler mhandler = new Handler();
	View btnyes;
	View btnno;
	String packageName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		

		getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
		//setTheme(android.R.style.Animation_Dialog);
		
		 packageName = getIntent().getStringExtra(PushUtil.INTENT_PACKAGE_NAME);
		
		PushInfo pi = PushInfos.getInstance().get(packageName);
		if (null == pi) {
			mhandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}else{
			setContentView(R.layout.common_dlg);
		    btnyes = findViewById(R.id.btnYes);	
			btnyes.setOnClickListener(this);

			
		    btnno = findViewById(R.id.btnNo);	
			btnno.setOnClickListener(this);		
			
			Bitmap bmp = getIntent().getParcelableExtra(PushUtil.INTENT_ICON_BMP);
			if (bmp != null) {
				ImageView iv = (ImageView)findViewById(R.id.icon);
				iv.setImageBitmap(bmp);
			}
			
			TextView tv = (TextView)findViewById(R.id.txtTitle);
			tv.setText(pi.getAppName());
			TextView tvBrief = (TextView)findViewById(R.id.txtBrief);
			tvBrief.setText(pi.getmBrief());
			tvBrief.setMovementMethod(ScrollingMovementMethod.getInstance());
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mhandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnyes) {
			PushInfoActionPaser.doClick(this, PushType.TYPE_POP_WND, packageName);
			finish();
		}else if (v == btnno) {
			finish();
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
