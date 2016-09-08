package com.kyd.springmachine.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.hardware.Response;
import com.kyd.library.hardware.UartResponse;
import com.kyd.library.hardware.Response.Listener;
import com.kyd.library.hardware.Response.ResponseFunctionBit;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbLogUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbViewHolder;
import com.kyd.springmachine.ControlApp;
import com.kyd.springmachine.R;
import com.kyd.springmachine.bean.KeyTestInfo;
import com.kyd.springmachine.server.ReadSerialPortServer;
import com.kyd.springmachine.uart.Uart1Request;

public class KeyTestActivity extends AbActivity implements OnClickListener,Listener{

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	//
	private TextView number;
	private TextView value;
	// 界面
	private ListView lv;
	
	private List<String> list; 
	private AbAdapter<String> adapter;
	private KeyTestBroadcastReceiver keyTestBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_key);
		initViews();
		initData();
		setListeners();
	}

	@Override
	public void initViews() {
		// title
		bt_title_left = (Button) findViewById(R.id.bt_title_left);
		tv_title_name = (TextView) findViewById(R.id.tv_title_name);
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		//
		number=(TextView) findViewById(R.id.item_key_number);
		value=(TextView) findViewById(R.id.item_key_value);
		// 界面
		lv = (ListView) findViewById(R.id.lv);
		
		tv_title_name.setText("按键测试");
		tv_title_right.setVisibility(View.INVISIBLE);
		
		number.setText("次数");
		value.setText("按键值");
	}

	@Override
	public void initData() {
		list=new ArrayList<String>();
		adapter=new AbAdapter<String>(this,list,R.layout.item_test_key) {
			
			@Override
			public void convert(AbViewHolder viewHolder, String item, int position) {
				TextView number=viewHolder.getView(R.id.item_key_number);
				TextView value=viewHolder.getView(R.id.item_key_value);
				number.setText(position+"");
				value.setText(item+"");
			}
		};
		lv.setAdapter(adapter);
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("KEY_ACTION");
		intentFilter.setPriority(3);
		// 创建NetWorkChangeReceiver的实例，并调用registerReceiver()方法进行注册
		keyTestBroadcastReceiver = new KeyTestBroadcastReceiver();
		registerReceiver(keyTestBroadcastReceiver, intentFilter);
	}

	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(keyTestBroadcastReceiver);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_title_left:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode==4) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onResponse(Response res) {
		// TODO Auto-generated method stub
		if(res.getFuncBit()==ResponseFunctionBit.RES_KEY_NUM){
			((UartResponse) res).getKeyNum();//按键

			Log.d("有没有货", "-------rr------->"+res.data.length);			

		}
	}

	
	class KeyTestBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getStringExtra("action");
			 if (action.equals("KEY")) {
				int key = intent.getIntExtra("key_value", 1);
				list.add(key+"");
				adapter.updateView(list);
				abortBroadcast();
			}
			
		}
	}
}
