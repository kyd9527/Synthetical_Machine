package com.kyd.springmachine.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbLogUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.AbViewHolder;
import com.kyd.springmachine.AppConfig;
import com.kyd.springmachine.AppConfig.PayType;
import com.kyd.springmachine.R;
import com.kyd.springmachine.bean.GoodsComInfo;
import com.kyd.springmachine.bean.PayInfo;

public class OrderActivity extends AbActivity implements OnClickListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	// 界面
	private GridView gv_order_pay;
	private ListView lv;
	private TextView tv_order_price;

	private Dialog dialogPay;
	private Dialog dialogMessage;

	private List<GoodsComInfo> list;

	private List<PayInfo> listPay;

	private AbAdapter<GoodsComInfo> adapter;

	private AbAdapter<PayInfo> payAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
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
	//	gv_order_pay = (GridView) findViewById(R.id.gv_order_pay);
		lv = (ListView) findViewById(R.id.lv);
		tv_order_price=(TextView) findViewById(R.id.tv_order_price);
		
		tv_title_name.setText("订单信息");
		tv_title_right.setVisibility(View.INVISIBLE);
	}

	@Override
	public void initData() {
		list = (List<GoodsComInfo>) getIntent().getSerializableExtra("list");
		if (list != null) {
			adapter = new AbAdapter<GoodsComInfo>(this, list,
					R.layout.item_order) {

				@Override
				public void convert(AbViewHolder viewHolder, GoodsComInfo item,
						int position) {
					// TODO Auto-generated method stub
					TextView tv_order_name = viewHolder.getView(R.id.tv_order_name);
					TextView tv_order_price = viewHolder.getView(R.id.tv_order_price);
					TextView tv_order_number = viewHolder.getView(R.id.tv_order_number);
					
					int number=item.getCarNumber();
					int price=item.getGoodsPrice();
					tv_order_name.setText(item.getGoodsName());
					tv_order_number.setText("x"+number);
					tv_order_price.setText((price*number*0.01f)+"");
				}
			};
			lv.setAdapter(adapter);
			int price = 0;//总价
			for (GoodsComInfo goodsComInfo : list) {
				
				int p=goodsComInfo.getGoodsPrice()*goodsComInfo.getCarNumber();
				price=price+p;
			}
			tv_order_price.setText("￥"+price*0.01f);
		}
		listPay = new ArrayList<PayInfo>();
		PayInfo p1 = new PayInfo();
		p1.setPayName(PayType.ALI_PAY.ordinal());
		p1.setPayIcon(R.drawable.alipay_icon);

		PayInfo p2 = new PayInfo();
		p2.setPayName(PayType.WX_PAY.ordinal());
		p2.setPayIcon(R.drawable.wxpay_icon);

		PayInfo p3 = new PayInfo();
		p3.setPayName(PayType.CASH_PAY.ordinal());
		p3.setPayIcon(R.drawable.cashpay_icon_big);

		listPay.add(p1);
		listPay.add(p2);
		listPay.add(p3);

		payAdapter = new AbAdapter<PayInfo>(this, listPay, R.layout.item_pay) {

			@Override
			public void convert(AbViewHolder viewHolder, final PayInfo item,
					int position) {
				// TODO Auto-generated method stub
				LinearLayout ll_item = viewHolder.getView(R.id.ll_item);
				TextView tv_pay_name = viewHolder.getView(R.id.tv_pay_name);
				ImageView iv_pay_icon = viewHolder.getView(R.id.iv_pay_icon);

				iv_pay_icon.setImageResource(item.getPayIcon());

				switch (item.getPayName()) {
				case 0:
					tv_pay_name.setText("支付宝支付");
					break;
				case 1:
					tv_pay_name.setText("微信支付");
					break;
				case 2:
					tv_pay_name.setText("现金支付");
					break;
				default:
					break;
				}

				ll_item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						switch (item.getPayName()) {
						case 0:
							dialogPay("支付宝支付");
						//	tv_pay_name.setText("支付宝支付");
							break;
						case 1:
							dialogPay("微信支付");
						//	tv_pay_name.setText("微信支付");
							break;
						case 2:
							dialogPay("现金支付");
						//	tv_pay_name.setText("现金支付");
							break;
						default:
							break;
						}
					}
				});
			}
		};
		gv_order_pay.setAdapter(payAdapter);
	}

	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
	}

	// 交易60S的dialog
	private void dialogPay(String title) {
		dialogPay = new Dialog(this);
		dialogPay.setTitle(title);
		View view = getLayoutInflater().inflate(R.layout.dialog_pay, null);
		dialogPay.addContentView(view, new LayoutParams(
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT));
		ProgressBar pb_pay = (ProgressBar) view.findViewById(R.id.pb_pay);
		ImageView iv_pay = (ImageView) view.findViewById(R.id.iv_pay);
		TextView tv_pay = (TextView) view.findViewById(R.id.tv_pay);
		tv_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialogPayMessage();
				dialogPay.dismiss();
			}
		});
		dialogPay.show();
	}

	/**
	 * 交易详情
	 */
	private void dialogPayMessage() {
		dialogMessage = new Dialog(this);
		dialogMessage.setTitle("交易详情");
		View view = getLayoutInflater().inflate(R.layout.dialog_pay_message,
				null);
		dialogMessage.addContentView(view, new LayoutParams(
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT));
		TextView tv_pay_number = (TextView) view
				.findViewById(R.id.tv_pay_number);
		TextView tv_pay_state = (TextView) view.findViewById(R.id.tv_pay_state);
		TextView tv_pay_time = (TextView) view.findViewById(R.id.tv_pay_time);
		TextView tv_pay_way = (TextView) view.findViewById(R.id.tv_pay_way);
		TextView tv_pay_pirce = (TextView) view.findViewById(R.id.tv_pay_pirce);
		TextView tv_pay_phone = (TextView) view.findViewById(R.id.tv_pay_phone);
		TextView tv_pay_explain = (TextView) view
				.findViewById(R.id.tv_pay_explain);

		dialogMessage.show();
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
}
