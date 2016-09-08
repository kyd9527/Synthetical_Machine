package com.kyd.springmachine;

import net.micode.fileexplorer.FileExplorerTabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autolayout.utils.L;
import com.kyd.library.AbActivity;
import com.kyd.library.util.AbLogUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.springmachine.MainActivity.KeyBroadcastReceiver;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.server.ReadSerialPortServer;

public class SystemMenuActivity extends AbActivity implements OnClickListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;

	// 界面
	private TextView tv_system_set;
	private TextView tv_system_test;
	private TextView tv_file_manage;
	private TextView tv_sale_statistic;
	private TextView tv_goods_set;
	private TextView tv_efficient;
	private TextView tv_fault_info;
	
	private LinearLayout ll_system_set;
	private LinearLayout ll_system_test;
	private LinearLayout ll_file_manage;
	private LinearLayout ll_sale_statistic;
	private LinearLayout ll_goods_set;
	private LinearLayout ll_efficient;
	private LinearLayout ll_fault_info;
	
	private MainBroadcastReceiver mainBroadcastReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_menu);
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
		tv_system_set = (TextView) findViewById(R.id.tv_system_set);
		tv_system_test = (TextView) findViewById(R.id.tv_system_test);
		tv_file_manage = (TextView) findViewById(R.id.tv_file_manage);
		tv_sale_statistic = (TextView) findViewById(R.id.tv_sale_statistic);
		tv_goods_set = (TextView) findViewById(R.id.tv_goods_set);
		tv_efficient = (TextView) findViewById(R.id.tv_efficient);
		tv_fault_info = (TextView) findViewById(R.id.tv_fault_info);
		
		ll_system_set=(LinearLayout) findViewById(R.id.ll_system_set);
		ll_system_test=(LinearLayout) findViewById(R.id.ll_system_test);
		ll_file_manage=(LinearLayout) findViewById(R.id.ll_file_manage);
		ll_sale_statistic=(LinearLayout) findViewById(R.id.ll_sale_statistic);
		ll_goods_set=(LinearLayout) findViewById(R.id.ll_goods_set);
		ll_efficient=(LinearLayout) findViewById(R.id.ll_efficient);
		ll_fault_info=(LinearLayout) findViewById(R.id.ll_fault_info);

		tv_title_name.setText("系统菜单");
	//	tv_title_right.setVisibility(View.INVISIBLE);
		tv_title_right.setText("补货完毕");
	}

	@Override
	public void initData() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("KEY_ACTION");
		intentFilter.setPriority(2);
		// 创建NetWorkChangeReceiver的实例，并调用registerReceiver()方法进行注册
		mainBroadcastReceiver = new MainBroadcastReceiver();
		registerReceiver(mainBroadcastReceiver, intentFilter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mainBroadcastReceiver);
		Intent intent = new Intent("KEY_ACTION");
		intent.putExtra("action", "UPDATE");
		sendBroadcast(intent);
	}
	@Override
	public void setListeners() {
		tv_system_set.setOnClickListener(this);
		tv_system_test.setOnClickListener(this);
		tv_file_manage.setOnClickListener(this);
		tv_sale_statistic.setOnClickListener(this);
		tv_goods_set.setOnClickListener(this);
		tv_efficient.setOnClickListener(this);
		tv_fault_info.setOnClickListener(this);
		bt_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_system_set: {// 系统设置
			Intent intent = new Intent(this, SystemSetActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.tv_system_test: {// 系统测试
			Intent intent=new Intent(this,SystemTestActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.tv_file_manage: {// 文件管理
			Intent intent = new Intent(this, FileExplorerTabActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.tv_sale_statistic: {// 销售统计
			Intent intent = new Intent(this, SaleActivity.class);
			startActivity(intent);
		}

			break;
		case R.id.tv_goods_set: {// 货道管理
			Intent intent = new Intent(this, GoodsSetActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.tv_efficient: {// 节能设置
			Intent intent = new Intent(this, EfficientActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.tv_fault_info: {//故障信息
			Intent intent = new Intent(this, FaultInfoActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.bt_title_left:
			finish();
			break;
			case R.id.tv_title_right:
				AbToastUtil.showToast(this, "补货全满");
				DataBaseManager dbManager=DataBaseManager.getInstance(this);
				dbManager.updateGoodsMax();
				ReadSerialPortServer.netWork.sendGoodsReplenish();
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
	class MainBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getStringExtra("action");
		    if (action.equals("KEY")) {
		    	abortBroadcast();
			}
			
		}
		
	}
}
