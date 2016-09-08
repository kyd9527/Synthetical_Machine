package com.kyd.springmachine;

import net.micode.fileexplorer.IntentBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.springmachine.goodset.GoodsInfoActivity;
import com.kyd.springmachine.goodset.RoadInfoActivity;
import com.kyd.springmachine.goodset.SetRoadActivity;

/**
 * 货道管理界面
 * 
 * @author 8015
 * 
 */
public class GoodsSetActivity extends AbActivity implements OnClickListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	// 界面
	private Switch sw_key;
	private RelativeLayout rl_goods;
	private RelativeLayout rl_key;
	private RelativeLayout rl_goods_set;
	private RelativeLayout rl_goods_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_set);
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

		rl_goods_set = (RelativeLayout) findViewById(R.id.rl_goods_set);
		rl_goods_info = (RelativeLayout) findViewById(R.id.rl_goods_info);
		
		sw_key=(Switch) findViewById(R.id.sw_key);

		tv_title_name.setText("货道管理");
		tv_title_right.setVisibility(View.INVISIBLE);
	}

	@Override
	public void initData() {
		sw_key.setChecked(AbSharedUtil.getBoolean(this, "sw_key", true));
	}

	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
		rl_goods_set.setOnClickListener(this);
		rl_goods_info.setOnClickListener(this);
		sw_key.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				AbSharedUtil.putBoolean(GoodsSetActivity.this, "sw_key", arg1);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.rl_goods_set: {//货道设置
			Intent intent = new Intent(this, RoadInfoActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.rl_goods_info:{//商品信息
			Intent intent = new Intent(this, GoodsInfoActivity.class);
			startActivity(intent); 
		}
			break;
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
}
