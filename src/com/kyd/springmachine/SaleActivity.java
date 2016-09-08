package com.kyd.springmachine;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.pullview.AbPullToRefreshView;
import com.kyd.library.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.kyd.library.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbLogUtil;
import com.kyd.library.util.AbViewHolder;
import com.kyd.springmachine.database.DataBaseManager;

import de.greenrobot.greendao.OrdersInfo;

/**
 * ����ͳ��
 * 
 * @author 8015
 * 
 */
public class SaleActivity extends AbActivity implements OnClickListener,
		OnHeaderRefreshListener, OnFooterLoadListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;

	// ����
	private AbPullToRefreshView mAbPullToRefreshView;
	private ListView lv;

	private AbAdapter<OrdersInfo> adapter;
	private DataBaseManager dbManager;
	
	private int page=0;
	private List<OrdersInfo> listOrders;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale);
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
		// ����
		mAbPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
		lv = (ListView) findViewById(R.id.lv);

		tv_title_name.setText("����ͳ��");
		tv_title_right.setVisibility(View.INVISIBLE);

		// ���ý���������ʽ
		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
		mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		dbManager = DataBaseManager.getInstance(this);
		listOrders = dbManager.queryOrderPage(page);
		AbLogUtil.d(getClass(), listOrders.size()+" +");
		adapter = new AbAdapter<OrdersInfo>(this, listOrders,R.layout.item_sale) {
			@Override
			public void convert(AbViewHolder viewHolder, OrdersInfo item,int position) {
				LinearLayout ll_item=viewHolder.getView(R.id.ll_item);
				TextView item_order_number=viewHolder.getView(R.id.item_order_number);
				TextView item_goods_number=viewHolder.getView(R.id.item_goods_number);
				TextView item_goods_code=viewHolder.getView(R.id.item_goods_code);
				TextView item_goods_name=viewHolder.getView(R.id.item_goods_name);
				TextView item_order_price=viewHolder.getView(R.id.item_order_price);
				TextView item_total_number=viewHolder.getView(R.id.item_total_number);
				TextView item_order_type=viewHolder.getView(R.id.item_order_type);
				TextView item_order_status=viewHolder.getView(R.id.item_order_status);
				TextView item_order_time=viewHolder.getView(R.id.item_order_time);
				
				item_order_number.setText(item.getOrderNum());
				item_goods_number.setText(item.getGoodsID()+"");
				item_goods_code.setText(item.getGoodsCode());
				item_goods_name.setText(item.getGoodsName());
				item_order_price.setText(item.getTotalPrice()*0.01f+"");
				item_total_number.setText(item.getTotalNum()+"");
				item_order_time.setText(item.getTime());
				
				switch (item.getOrderStatus()) {
				// ����״̬ δ��ɣ�����ɣ��ɹ���ʧ�ܣ�״̬δ֪,δ֧����δ�˿�
				// public enum OrderStatus {
				// Unfinished, Finished, Success, Fail, Unknown,Unpay,Refund
				// }
				case 0:
					item_order_status.setText("δ���");
					break;
				case 1:
					item_order_status.setText("�����");
					break;
				case 2:
					item_order_status.setText("�ɹ�");
					break;
				case 3:
					item_order_status.setText("ʧ��");
					break;
				case 4:
					item_order_status.setText("״̬δ֪");
					break;
				case 5:
					item_order_status.setText("δ֧��");
					break;
				case 6:
					item_order_status.setText("δ�˿�");
					break;
				case 7:
					item_order_status.setText("���Գɹ�");
					break;
				case 8:
					item_order_status.setText("����ʧ��");
					break;
				default:
					break;
				}

				switch (item.getPayType()) {
				// ֧������,֧������΢�ţ��ֽ�,��ûȷ��
				// public enum PayType {
				// ALI_PAY,WX_PAY, CASH_PAY,NO_PAY
				// }
				case 0:
					item_order_type.setText("֧����");
					break;
				case 1:
					item_order_type.setText("΢��֧��");
					break;
				case 2:
					item_order_type.setText("�ֽ�");
					break;
				default:
					item_order_type.setText("δ֪֧������");
					break;
				}
			}
		};
		lv.setAdapter(adapter);
	}

	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);
		// ���ü�����
		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setOnFooterLoadListener(this);
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
	public void onFooterLoad(AbPullToRefreshView view) {
	//	mAbPullToRefreshView.onFooterLoadFinish();
		page++;
		List<OrdersInfo> listOrders = dbManager.queryOrderPage(page);
		if (listOrders.size()>0) {
			this.listOrders.addAll(listOrders);
			adapter.updateView(this.listOrders);
			mAbPullToRefreshView.onFooterLoadFinish();
			mAbPullToRefreshView.setLoadMoreEnable(true);
		}else {
			mAbPullToRefreshView.onFooterLoadFinish();
			mAbPullToRefreshView.setLoadMoreEnable(false);
		}
	}

	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
		mAbPullToRefreshView.onHeaderRefreshFinish();
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
