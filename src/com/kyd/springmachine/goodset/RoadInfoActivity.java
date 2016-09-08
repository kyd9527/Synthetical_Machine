package com.kyd.springmachine.goodset;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbViewHolder;
import com.kyd.springmachine.MainActivity;
import com.kyd.springmachine.R;
import com.kyd.springmachine.bean.GoodsComInfo;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.server.ReadSerialPortServer;
import com.kyd.springmachine.widget.MyDialog;
import com.kyd.springmachine.widget.MyDialog.onDialogListener;

/**
 * 货道设置
 * 
 * @author 8015
 * 
 */
public class RoadInfoActivity extends AbActivity implements OnClickListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;

	private ListView lv;
	private TextView tv_set_goods_add;

	private DataBaseManager dbManager;
	
	private AbAdapter<GoodsComInfo> adAdapter ;

	private MyDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_road_info);
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
		lv = (ListView) findViewById(R.id.lv);

		tv_title_name.setText("货道设置");
		tv_title_right.setText("添加");
	}

	@Override
	public void initData() {
		dialog=new MyDialog();
		dbManager = DataBaseManager.getInstance(this);
		List<GoodsComInfo> list=dbManager.queryAllGoodsComInfoByGoodsNumber();
		adAdapter=new AbAdapter<GoodsComInfo>(this,list,R.layout.item_set_goods) {
			
			@Override
			public void convert(AbViewHolder viewHolder, final GoodsComInfo item, int position) {
				LinearLayout ll_item=viewHolder.getView(R.id.ll_item);
				TextView item_goods_number=(TextView) viewHolder.getView(R.id.item_goods_number);
				TextView item_goods_code=viewHolder.getView(R.id.item_goods_code);
				TextView item_goods_name=viewHolder.getView(R.id.item_goods_name);
				TextView item_goods_type=viewHolder.getView(R.id.item_goods_type);
				TextView item_goods_price=viewHolder.getView(R.id.item_goods_price);
				
				TextView item_goods_updata=viewHolder.getView(R.id.item_goods_updata);
				TextView item_goods_delete=viewHolder.getView(R.id.item_goods_delete);
				
				final int goods_number=item.getGoodsNumber();
				item_goods_number.setText(goods_number+"");
				item_goods_code.setText(item.getGoodsCode()+"");
				item_goods_name.setText(item.getGoodsName()+"");
				item_goods_type.setText(item.getGoodsKey()+"");
				item_goods_price.setText(item.getGoodsPrice()*0.01f+"元");
				
                item_goods_updata.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(RoadInfoActivity.this, SetRoadActivity.class);
						intent.putExtra("goodsNumber", goods_number);
						startActivityForResult(intent, 1);
					}
				});
				item_goods_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.setPromptDialog(RoadInfoActivity.this, "删除", "是否删除第"+goods_number+"货道号", 2, new onDialogListener() {
							
							@Override
							public void confirm(int number) {
								// TODO Auto-generated method stub
								System.out.println("confirm");
								dbManager.dropGoodsInfoByGoogsNumber(goods_number);
								List<GoodsComInfo> list=dbManager.queryAllGoodsComInfoByGoodsNumber();
								adAdapter.updateView(list);
							}
						});
					}
				});
			}
		};
		lv.setAdapter(adAdapter);
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		bt_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ReadSerialPortServer.netWork.sendGoodsSet();
		Intent intent = new Intent("KEY_ACTION");
		intent.putExtra("action", "UPDATE");
		this.sendBroadcast(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_title_left:
			finish();
			break;
		case R.id.tv_title_right:{
			Intent intent=new Intent(this, SetRoadActivity.class);
			startActivityForResult(intent, 1);
		}
			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==1) {
			dbManager = DataBaseManager.getInstance(this);
			List<GoodsComInfo> list=dbManager.queryAllGoodsComInfoByGoodsNumber();
			adAdapter.updateView(list);
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
