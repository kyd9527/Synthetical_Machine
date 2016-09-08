package com.kyd.springmachine;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kyd.library.AbActivity;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.springmachine.server.ReadSerialPortServer;

public class WelcomeActivity extends AbActivity implements SurfaceHolder.Callback, OnCompletionListener{

	private SurfaceView sv_login;
	private SurfaceHolder holder;
	private MediaPlayer player;
	private boolean isFinished = false;

	private ServiceConnection conn=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
		//	System.out.println("------------");
			if(player!=null&&!player.isPlaying()){
				Intent startIntent;
				if (AbStrUtil.isEmpty(AbSharedUtil.getString(WelcomeActivity.this, "machine_number"))) {
					startIntent = new Intent(WelcomeActivity.this, SystemSetActivity.class);
				}else
				{
					startIntent = new Intent(WelcomeActivity.this, MainActivity.class);
				}
				startActivity(startIntent);
				finish();
			}else{
				isFinished=true;
			}	
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initViews();
		initData();
		setListeners();
	}
	
	@Override
	public void initViews() {
		Intent intent = new Intent(this, ReadSerialPortServer.class);  
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		sv_login=(SurfaceView) findViewById(R.id.sv_login);
		holder = sv_login.getHolder();
		holder.addCallback(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (isFinished) { // 判断资源是否加载完
			Intent startIntent;
			if (AbStrUtil.isEmpty(AbSharedUtil.getString(WelcomeActivity.this, "machine_number"))) {
				startIntent = new Intent(WelcomeActivity.this, SystemSetActivity.class);
				startIntent.putExtra("flag", 1);
			}else 
			{
				startIntent = new Intent(WelcomeActivity.this, MainActivity.class);
			}
			startActivity(startIntent);
			finish();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(holder);
		player.setOnCompletionListener(this);
		// 设置显示视频显示在SurfaceView上
		try {
			player.setDataSource(this, Uri.parse("android.resource://com.kyd.springmachine/" + R.raw.startlogo));// 开机动画
			player.prepare();
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (player.isPlaying()) {
			player.stop();
		}
		player.release();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
	}
}
