package com.kyd.springmachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;

import com.kyd.library.AbActivity;
import com.kyd.springmachine.MainActivity.KeyBroadcastReceiver;
import com.kyd.springmachine.widget.VideoImageView;

public class AdVerActivity extends AbActivity{

	VideoImageView adver_video;
	AdVerBroadcastReceiver adverBroadcastReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adver);
		initViews();
		initData();
		setListeners();
	}
	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		adver_video=(VideoImageView) findViewById(R.id.adver_video);
		adver_video.getVideoList();
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("KEY_ACTION");
		intentFilter.setPriority(2);
		// 创建NetWorkChangeReceiver的实例，并调用registerReceiver()方法进行注册
		adverBroadcastReceiver = new AdVerBroadcastReceiver();
		registerReceiver(adverBroadcastReceiver, intentFilter);
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(adverBroadcastReceiver);
	}
	
	class AdVerBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getStringExtra("action");
			if (action.equals("KEY")) {
				abortBroadcast();
				AdVerActivity.this.finish();
			}
		}
		
	}
	
}
