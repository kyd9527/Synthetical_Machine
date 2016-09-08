package com.kyd.springmachine.goodset;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
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
import com.kyd.springmachine.R;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.server.ReadSerialPortServer;
import com.kyd.springmachine.util.NetWorkUtil.OnHttpListener;

import de.greenrobot.greendao.CommodityInfo;

/**
 * 商品信息
 * 
 * @author 8015
 * 
 */
public class GoodsInfoActivity extends AbActivity implements OnClickListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	// 界面
	private ListView lv;

	private DataBaseManager dbManager;
	private AbAdapter<CommodityInfo> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_info);
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

		tv_title_right.setText("更新");
		tv_title_name.setText("商品信息");
	}

	@Override
	public void initData() {
		dbManager = DataBaseManager.getInstance(this);
		List<CommodityInfo> list = dbManager.queryAllCommodityInfo();
		adapter = new AbAdapter<CommodityInfo>(this, list,
				R.layout.item_set_goods) {

			@Override
			public void convert(AbViewHolder viewHolder, CommodityInfo item,
					int position) {
				LinearLayout ll_item = viewHolder.getView(R.id.ll_item);
				LinearLayout ll_goods = viewHolder.getView(R.id.ll_goods);
				TextView item_goods_number = (TextView) viewHolder
						.getView(R.id.item_goods_number);
				TextView item_goods_code = viewHolder
						.getView(R.id.item_goods_code);
				TextView item_goods_name = viewHolder
						.getView(R.id.item_goods_name);
				TextView item_goods_type = viewHolder
						.getView(R.id.item_goods_type);
				TextView item_goods_price = viewHolder
						.getView(R.id.item_goods_price);

				ll_goods.setVisibility(View.GONE);
				item_goods_number.setVisibility(View.GONE);
				item_goods_code.setText(item.getGoodsCode() + "");
				item_goods_name.setText(item.getName() + "");
				item_goods_type.setText(item.getCommType() + "");
				item_goods_price.setText(item.getPrice() * 0.01f + "元");
			}
		};
		lv.setAdapter(adapter);
	}

	@Override
	public void setListeners() {
		tv_title_right.setOnClickListener(this);
		bt_title_left.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:// 更新按钮
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("下载中");
			final Dialog dialog = builder.show();
			ReadSerialPortServer.netWork.sendCommodityInfo(new OnHttpListener() {

						@Override
						public void Message(int result, String msg) {
							// TODO Auto-generated method stub
							initData();
							dialog.dismiss();
						}
					});
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
		if (keyCode == 4) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
