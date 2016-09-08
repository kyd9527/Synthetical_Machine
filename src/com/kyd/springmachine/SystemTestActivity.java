package com.kyd.springmachine;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.springmachine.test.RoadTestActivity;
import com.kyd.springmachine.test.KeyTestActivity;
import com.kyd.springmachine.test.TemperatureTestActivity;

public class SystemTestActivity extends AbActivity implements OnClickListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	// 界面
	private RelativeLayout rl_test_temp;
	private RelativeLayout rl_test_road;
	private RelativeLayout rl_test_key;
	private RelativeLayout rl_test_buy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_test);
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
		// 界面
		rl_test_temp = (RelativeLayout) findViewById(R.id.rl_test_temp);
		rl_test_road = (RelativeLayout) findViewById(R.id.rl_test_road);
		rl_test_key = (RelativeLayout) findViewById(R.id.rl_test_key);
		rl_test_buy = (RelativeLayout) findViewById(R.id.rl_test_buy);

		tv_title_name.setText("系统测试");
		tv_title_right.setVisibility(View.INVISIBLE);
	}

	@Override
	public void initData() {

	}

	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
		rl_test_temp.setOnClickListener(this);
		rl_test_road.setOnClickListener(this);
		rl_test_key.setOnClickListener(this);
		rl_test_buy.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_title_left:
			finish();
			break;
		case R.id.rl_test_temp:{
			Intent intent=new Intent(this, TemperatureTestActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.rl_test_road:{
			Intent intent=new Intent(this, RoadTestActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.rl_test_key:{
			Intent intent=new Intent(this, KeyTestActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.rl_test_buy:{
			Intent intent =new Intent(this,MainActivity.class);
			intent.putExtra("flag", 1);
			startActivity(intent);
		}
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
}
