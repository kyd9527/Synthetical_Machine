package com.kyd.springmachine;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.zxing.WriterException;
import com.kyd.library.AbActivity;
import com.kyd.library.hardware.Request;
import com.kyd.library.hardware.Request.HeartbeatPacket;
import com.kyd.library.hardware.Request.RequestFunctionBit;
import com.kyd.library.hardware.Response;
import com.kyd.library.hardware.Response.Listener;
import com.kyd.library.hardware.Response.ResponseFunctionBit;
import com.kyd.library.hardware.UartResponse;
import com.kyd.library.util.ABQrCodeUtil;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbDateUtil;
import com.kyd.library.util.AbMathUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.AbViewHolder;
import com.kyd.springmachine.AppConfig.GoodsDisable;
import com.kyd.springmachine.AppConfig.GoodsStatus;
import com.kyd.springmachine.AppConfig.OrderStatus;
import com.kyd.springmachine.AppConfig.PayType;
import com.kyd.springmachine.AppConfig.SyncStatuc;
import com.kyd.springmachine.bean.GoodsComInfo;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.msg.RootAliPay;
import com.kyd.springmachine.msg.RootAliPayRefund;
import com.kyd.springmachine.server.BuyTimeListener;
import com.kyd.springmachine.server.ReadSerialPortServer;
import com.kyd.springmachine.server.ReadSerialPortServer.MyBinder;
import com.kyd.springmachine.shopping.OrderActivity;
import com.kyd.springmachine.uart.Uart1Request;
import com.kyd.springmachine.util.NetWorkUtil.OnHttpListener;
import com.kyd.springmachine.util.NetWorkUtil.OnPayStateListenner;
import com.kyd.springmachine.widget.MyGridView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.drm.DrmStore.RightsStatus;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.greenrobot.greendao.OrdersInfo;
import de.greenrobot.greendao.SetOpenDoor;

/**
 * 购物列表
 * 
 * @author 8015
 * 
 */
public class MainActivity extends AbActivity implements OnClickListener,
		Listener, BuyTimeListener {

	private ListView lv;
	private MyGridView gv_main;

	private TextView tv_price;
	private TextView tv_main_balance;
	private TextView tv_order_cash;
	private TextView tv_pay_second;// 交易详情
	private TextView tv_order_second;// 订单详情
	private TextView tv_machine_phone;// 电话
	private TextView tv_main_return;// 返回

	private TextView tv_machine_number;
	private TextView tv_temp_left;
	private TextView tv_temp_right;
	private TextView tv_machine_time;

	private ScrollView sv;

	private ImageView iv_main_shopcar;// 购物车iv
	private ImageView iv_main_closing;// 暂停营业图片
	private RelativeLayout ll_main_closing;// ll_暂停营业图片
	private LinearLayout ll_main_return;// ll_返回

	private RelativeLayout rl_main;

	private AbAdapter<String> lvAdapter;

	private AbAdapter<GoodsComInfo> mainAdapter;

	private AbAdapter<GoodsComInfo> dialogAdapter;

	private List<String> lvList;

	private List<GoodsComInfo> mainList;

	private List<GoodsComInfo> listCar;

	private DataBaseManager dbManager;
	private OrdersInfo orderInfo;

	private int totalPirce = 0;// 总价
	private int totalNumber = 0;// 总个数
	private long orderId;// 订单信息的ID
	private KeyBroadcastReceiver keyBroadcastReceiver;

	private Dialog dialogOrder;//订单dialog

	private int totalMoney = 0;// 总投币
	private String TAG = "uart";

	private Dialog dialogMessage;//交易信息
	private Dialog dialogShipment;//出货中
	private int outCoin=0;//退币金额
	private Timer time;
	private MessageTimer mTimer;
	private Timer shipmentTime;

	private int ad_number = 120;// 广告计时
	private int timeNumber = 10; // 开头商品展示时间
	private boolean ad_flag = true;// 广告开关
	private boolean ad_direction = true;// 商品展示方向,true是往下,false是往上
	private int activity_flag;// 是否在测试跳转
	private MyBinder binder;

	@SuppressLint({ "NewApi", "HandlerLeak" })
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {// 一秒
			case 1:
				tv_machine_time.setText(AbDateUtil.getCurrentDate(AbDateUtil.dateFormatYMDHMS));
				if (dialogOrder != null && tv_order_second != null) {
					if (dialogOrder.isShowing()) {
						int second = Integer.parseInt(tv_order_second.getText().toString());
						if (second > 0) {
							tv_order_second.setText(second - 1 + "");
						} else {
							dialogOrder.dismiss();
						}
					}
				}
				if (dialogMessage != null && tv_pay_second != null) {
					if (dialogMessage.isShowing()) {
						int second = Integer.parseInt(tv_pay_second.getText().toString());
						if (second > 0) {
							tv_pay_second.setText(second - 1 + "");
						} else {
							dialogMessage.dismiss();
						}
					}
				}
				if (ad_number > 0 && ad_flag == true) {// 广告
					ad_number--;
					if (ad_number <= 0) {
						// ad_number==0 进入广告
						startActivityForResult(new Intent(MainActivity.this,AdVerActivity.class), 1);
					}
				}
				if (timeNumber > 0) {// 商品展示
					timeNumber--;
				} else {
					timeNumber = 10;
					int off = gv_main.getMeasuredHeight() - sv.getHeight();// 判断高度
					// System.out.println(gv_main.getMinimumHeight()+"   "+gv_main.getMeasuredHeight());
					if (off > 0 && ad_direction == true) {
						sv.scrollBy(0, 345);
						if (sv.getScrollY() == off) {
							ad_direction = false;
						}
					} else {
						sv.scrollBy(0, -345);
						if (sv.getScrollY() == 0) {
							ad_direction = true;
						}
					}
				}
				break;
			case 0x123:
				tv_order_cash.setText("已付" + totalMoney * 0.01f + "元");
				tv_order_cash.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.radius_gridview));

				if (totalMoney >= orderInfo.getTotalPrice()) {
					dialogOrder.dismiss();
					dialogShipment();//
					ReadSerialPortServer.netWork.closePayTimer();
					outCoin = totalMoney - orderInfo.getTotalPrice();// 交易成功的退币金额
					totalMoney = 0;
					dbManager.replaceOrdersInfoByPayType(orderId,PayType.CASH_PAY.ordinal());
					shipment(orderInfo.getGoodsID(),(byte) 0xff,true);
				}
				break;
			case 0x124:
				tv_temp_left.setText(((int[]) msg.obj)[1] + "℃");
				tv_temp_right.setText(((int[]) msg.obj)[0] + "℃");
				break;
			case 0x125:// 出货结果
				dialogShipment.dismiss();
				dialogPayMessage(orderInfo, orderInfo.getGoodsName());
				Intent intent = new Intent("KEY_ACTION");
				intent.putExtra("action", "UPDATE");
				sendBroadcast(intent);
				break;
			case 0x126:// 如果在购买页面则取消购买
				if (dialogOrder != null && dialogOrder.isShowing()) {
					ReadSerialPortServer.netWork.sendAliPayCancel(orderInfo.getOrderNum());
					ReadSerialPortServer.netWork.closePayTimer();
					dbManager.replaceOrdersInfoByID(orderId,OrderStatus.Unpay.ordinal());
					dialogOrder.dismiss();
				}
				break;
			case 0x127:
				if ((Boolean) msg.obj) {
					ad_flag = true;
					ll_main_closing.setVisibility(View.GONE);
				} else {
					ad_flag = false;
					ll_main_closing.setVisibility(View.VISIBLE);
				}

				break;
			default:
				break;
			}
		};
	};

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			binder = ((MyBinder) arg1);
			binder.setBuyTimeListener(MainActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View view = getLayoutInflater().inflate(R.layout.activity_main, null);
		setContentView(view);
		initViews();
		initData();
		setListeners();
		Intent intent = new Intent(this, ReadSerialPortServer.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		// AbToastUtil.showToast(this, "onCreate");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		time = new Timer();
		mTimer=new MessageTimer();
		time.schedule(mTimer, 0, 1000);
		ControlApp.uartHand1.addListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mTimer.cancel();
		time.cancel();
		ControlApp.uartHand1.removeListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
		unregisterReceiver(keyBroadcastReceiver);
		ControlApp.uartHand1.removeListener(this);
	}

	@Override
	public void initViews() {
		lv = (ListView) findViewById(R.id.lv);

		gv_main = (MyGridView) findViewById(R.id.gv_main);
		tv_price = (TextView) findViewById(R.id.tv_main_price);
		tv_main_balance = (TextView) findViewById(R.id.tv_main_balance);

		iv_main_shopcar = (ImageView) findViewById(R.id.iv_main_shopcar);
		iv_main_closing = (ImageView) findViewById(R.id.iv_main_closing);
		InputStream is = getResources().openRawResource(R.raw.ic_closing);
		iv_main_closing.setImageBitmap(BitmapFactory.decodeStream(is));
		ll_main_closing = (RelativeLayout) findViewById(R.id.ll_main_closing);
		ll_main_closing.setVisibility(View.GONE);

		rl_main = (RelativeLayout) findViewById(R.id.rl_main);

		tv_machine_number = (TextView) findViewById(R.id.tv_machine_number);
		tv_temp_left = (TextView) findViewById(R.id.tv_temp_left);
		tv_temp_right = (TextView) findViewById(R.id.tv_temp_right);
		tv_machine_time = (TextView) findViewById(R.id.tv_machine_time);
		tv_machine_phone = (TextView) findViewById(R.id.tv_machine_phone);
		sv = (ScrollView) findViewById(R.id.sv);
		tv_main_return = (TextView) findViewById(R.id.tv_main_return);
		ll_main_return = (LinearLayout) findViewById(R.id.ll_main_return);

		lv.setVisibility(View.GONE);
	//	iv_main_shopcar.setVisibility(View.GONE);
		rl_main.setVisibility(View.GONE);

		dialogOrder = new Dialog(this);
		dialogOrder.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogMessage = new Dialog(this);
		dialogShipment = new Dialog(this);
	}

	@Override
	public void initData() {
		dbManager = DataBaseManager.getInstance(this);
		activity_flag = getIntent().getIntExtra("flag", 0);
		if (activity_flag == 0) {
			ll_main_return.setVisibility(View.INVISIBLE);
		}else {
			ad_flag=false;
		}
		lvList = new ArrayList<String>();
		lvList.add("全部");
		lvAdapter = new AbAdapter<String>(this, lvList,android.R.layout.simple_expandable_list_item_1) {

			@Override
			public void convert(AbViewHolder viewHolder, String item,
					int position) {
				TextView text = viewHolder.getView(android.R.id.text1);
				text.setText(item);
			}
		};
		lv.setAdapter(lvAdapter);

		mainList = dbManager.queryAllGoodsComInfoByKey();

		mainAdapter = new AbAdapter<GoodsComInfo>(this, mainList,
				R.layout.item_main_buy) {

			@Override
			public void convert(AbViewHolder viewHolder,
					final GoodsComInfo item, int position) {
				LinearLayout ll_item = viewHolder.getView(R.id.ll_item);
				ImageView iv_buy_icon = viewHolder.getView(R.id.iv_buy_icon);
				TextView tv_buy_stock = viewHolder.getView(R.id.tv_buy_stock);
				TextView tv_buy_price = viewHolder.getView(R.id.tv_buy_price);
				ImageView iv_buy_out = viewHolder.getView(R.id.iv_buy_out);
				TextView tv_buy_key=viewHolder.getView(R.id.tv_buy_key);
				
				iv_buy_out.setVisibility(View.GONE);
				iv_buy_icon.setImageBitmap(BitmapFactory.decodeFile(item.getGoodsPic()));
				tv_buy_price.setText("￥" + (item.getGoodsPrice() * 0.01f));

				tv_buy_stock.setText(item.getGoodsCapacity()+"");
				tv_buy_key.setText(item.getGoodsKey()+"");
			//	tv_buy_key.setText("K:"+item.getGoodsKey()+"H:"+item.getGoodsNumber()+"C:"+item.getGoodsCapacity());
				if (item.getGoodsDisable() == GoodsDisable.Disable.ordinal()|| item.getGoodsStatus() != GoodsStatus.In_Stock.ordinal()) {
					iv_buy_out.setVisibility(View.VISIBLE);
					tv_buy_stock.setText("0");
				}
				
				ll_item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent("KEY_ACTION");
						intent.putExtra("action", "KEY");
						intent.putExtra("key_value", item.getGoodsKey());
						MainActivity.this.sendOrderedBroadcast(intent, null);
					}
				});

			}
		};
		gv_main.setAdapter(mainAdapter);

		String machine_number = AbSharedUtil.getString(this, "machine_number");
		if (!AbStrUtil.isEmpty(machine_number)) {
			tv_machine_number.setText(machine_number);
		}
		String machine_phone = AbSharedUtil.getString(this, "phone");
		if (!AbStrUtil.isEmpty(machine_phone)) {
			tv_machine_phone.setText(machine_phone);
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("KEY_ACTION");
		if (activity_flag == 1) {
			intentFilter.setPriority(3);
		} else {
			intentFilter.setPriority(1);
		}
		// 创建NetWorkChangeReceiver的实例，并调用registerReceiver()方法进行注册
		keyBroadcastReceiver = new KeyBroadcastReceiver();
		registerReceiver(keyBroadcastReceiver, intentFilter);
	}

	// 购物车的dialog
	public void dialogCar() {
		final Dialog dialog = new Dialog(this, R.style.MyDialogStyleBottom);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = this.getLayoutInflater().inflate( R.layout.activity_shop_car, null);
		dialog.addContentView(view, new LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = android.widget.LinearLayout.LayoutParams.MATCH_PARENT;// 让dialog的宽占满屏幕的宽
		lp.gravity = Gravity.BOTTOM;// 出现在底部
		window.setAttributes(lp);
		//
		TextView tv_main_balance = (TextView) view
				.findViewById(R.id.tv_main_balance);
		final TextView tv_main_price = (TextView) view
				.findViewById(R.id.tv_main_price);
		ListView lv = (ListView) view.findViewById(R.id.lv);
		updataShopCar();
		tv_price.setText("￥" + totalPirce * 0.01f);
		tv_main_price.setText("￥" + totalPirce * 0.01f);

		dialogAdapter = new AbAdapter<GoodsComInfo>(this, listCar,R.layout.item_shop_car) {

			@Override
			public void convert(AbViewHolder viewHolder,
					final GoodsComInfo item, int position) {
				RelativeLayout rl_item = viewHolder.getView(R.id.rl_item);

				LinearLayout ll_item = viewHolder.getView(R.id.ll_item);
				TextView tv_shopcar_name = viewHolder
						.getView(R.id.tv_shopcar_name);
				TextView tv_shopcar_price = viewHolder
						.getView(R.id.tv_shopcar_price);

				ImageView iv_main_add = viewHolder.getView(R.id.iv_main_add);
				ImageView iv_main_subtract = viewHolder
						.getView(R.id.iv_main_subtract);
				final TextView tv_main_number = viewHolder
						.getView(R.id.tv_main_number);

				tv_shopcar_name.setText(item.getGoodsName());
				tv_shopcar_price.setText("￥" + (item.getGoodsPrice() * 0.01f));

				final int number = item.getCarNumber();
				tv_main_number.setText(number + "");

				iv_main_add.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						item.setCarNumber(number + 1);
						updataShopCar();
						totalPirce += item.getGoodsPrice();
						tv_main_price.setText("￥" + totalPirce * 0.01f);
						tv_price.setText("￥" + totalPirce * 0.01f);
						dialogAdapter.notifyDataSetChanged();
						mainAdapter.notifyDataSetChanged();
					}
				});

				iv_main_subtract.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						item.setCarNumber(number - 1);
						if (item.getCarNumber() == 0) {
							updataShopCar();

							if (listCar.size() == 0) {
								dialog.dismiss();
							}
							dialogAdapter.updateView(listCar);
						}
						totalPirce -= item.getGoodsPrice();
						tv_main_price.setText("￥" + totalPirce * 0.01f);
						tv_price.setText("￥" + totalPirce * 0.01f);
						dialogAdapter.notifyDataSetChanged();
						mainAdapter.notifyDataSetChanged();
					}
				});
			}
		};

		tv_main_balance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (totalPirce != 0) {
					Intent intent = new Intent(MainActivity.this,
							OrderActivity.class);
					intent.putExtra("list", (Serializable) listCar);
					startActivity(intent);
				} else {
					AbToastUtil.showToast(MainActivity.this, "请选择商品");
				}
			}
		});

		lv.setAdapter(dialogAdapter);
		dialog.show();
	}

	private void updataShopCar() {
		listCar = new ArrayList<GoodsComInfo>();
		for (GoodsComInfo info : mainList) {
			if (info.getCarNumber() > 0) {
				listCar.add(info);
			}
		}
	}

	// 加入购物车的动画
	private void startAnimation(View imgPhoto) {
		// 动画
		int[] start_location = new int[2];
		final ImageView buyImg = new ImageView(this);
		buyImg.setImageResource(R.drawable.ic_circle);
		imgPhoto.getLocationInWindow(start_location);

		addContentView(buyImg, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		buyImg.setX(start_location[0]);
		buyImg.setY(start_location[1]);

		int[] end_location = new int[2];
		iv_main_shopcar.getLocationInWindow(end_location);
		int endY = end_location[1] - start_location[1];// 动画位移的y坐标
		int endX = 0 - start_location[0] + (iv_main_shopcar.getWidth() / 2);// 动画位移的X坐标

		final AnimationSet set = new AnimationSet(false);
		TranslateAnimation translateAnimationX = new TranslateAnimation(0,
				endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);
		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
				0, endY);
		translateAnimationY.setInterpolator(new AccelerateInterpolator());
		translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		set.setFillAfter(false);
		set.addAnimation(translateAnimationX);
		set.addAnimation(translateAnimationY);
		set.setFillAfter(false);
		set.setDuration(500);
		buyImg.startAnimation(set);

		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {// 开始
				// TODO Auto-generated method stub
				buyImg.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {// 重复
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {// 结束
				// TODO Auto-generated method stub
				buyImg.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void setListeners() {
		iv_main_shopcar.setOnClickListener(this);
		tv_main_balance.setOnClickListener(this);
		tv_price.setOnClickListener(this);
		ll_main_return.setOnClickListener(this);
		ll_main_closing.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}

	/**
	 * 订单dialog
	 * 
	 * @param key_value
	 *            按键值
	 */
	@SuppressLint("NewApi") 
	public void dialogOrder(int key_value) {
	//	dialogOrder = new Dialog(this);
		
		dialogOrder.setCancelable(false);
		View view = this.getLayoutInflater().inflate(R.layout.activity_order,null);
		dialogOrder.setContentView(view, new LayoutParams(new LayoutParams(700,700)));
		dialogOrder.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				setCoin(RequestFunctionBit.CMD_CLOSE_COIN, 0);// 关投币器
			}
		});
		TextView tv_order_number = (TextView) view.findViewById(R.id.tv_order_number);
		final TextView tv_mian_order_price = (TextView) view.findViewById(R.id.tv_order_price);
		tv_order_cash = (TextView) view.findViewById(R.id.tv_order_cash);
		tv_order_second = (TextView) view.findViewById(R.id.tv_order_second);
		
		if (totalMoney != 0) {
			tv_order_cash.setText("已付" + totalMoney * 0.01f + "元");
			tv_order_cash.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.radius_gridview));
		}


		ListView lv = (ListView) view.findViewById(R.id.lv);

		final ImageView iv_order_alipay = (ImageView) view.findViewById(R.id.iv_order_alipay);
		final ImageView iv_order_wxpay = (ImageView) view.findViewById(R.id.iv_order_wxpay);
		final ImageView iv_order_alipay_small = (ImageView) view.findViewById(R.id.iv_order_alipay_small);
		final ImageView iv_order_wxpay_small = (ImageView) view.findViewById(R.id.iv_order_wxpay_small);
		final ImageView iv_order_cash_small = (ImageView) view.findViewById(R.id.iv_order_cash_small);

		final GoodsComInfo g = dbManager.queryGoodsComInfoByKey(key_value);
		List<GoodsComInfo> gList = new ArrayList<GoodsComInfo>();
		gList.add(g);

		AbAdapter<GoodsComInfo> adapter = new AbAdapter<GoodsComInfo>(this,gList, R.layout.item_order) {

			@Override
			public void convert(AbViewHolder viewHolder, GoodsComInfo item,
					int position) {
				// TODO Auto-generated method stub
				TextView tv_order_name = viewHolder.getView(R.id.tv_order_name);
				TextView tv_order_price = viewHolder.getView(R.id.tv_order_price);
				TextView tv_order_number = viewHolder.getView(R.id.tv_order_number);

				tv_order_name.setText(item.getGoodsName());
				tv_order_number.setText("x1");
				tv_order_price.setText("￥" + item.getGoodsPrice() * 0.01f);
				tv_mian_order_price.setText("￥" + item.getGoodsPrice() * 0.01f);
			}
		};
		lv.setAdapter(adapter);
		// 网络
		Date date = new Date();
		String dataStr = AbDateUtil.getStringByFormat(date,AbDateUtil.dateFormatYMDHMS);
		RootAliPay root = new RootAliPay();
		List<RootAliPay.DetailDataBean> detail_data = new ArrayList<RootAliPay.DetailDataBean>();

		RootAliPay.DetailDataBean bean = new RootAliPay.DetailDataBean();
		bean.setLineno(g.getGoodsNumber());
		bean.setGoods_name(g.getGoodsName());
		bean.setBarcode(g.getGoodsCode());
		bean.setPrice(g.getGoodsPrice());
		bean.setSalenum(1);
		bean.setSubtotal(g.getGoodsPrice());
		detail_data.add(bean);

		root.setDetail_data(detail_data);
		root.setSaletime(dataStr);
		root.setTotal_amount(g.getGoodsPrice());
		String orderNum = AbSharedUtil.getString(this, "machine_number")+ AbDateUtil.getStringByFormat(date, "yyyyMMddHHmmss")+ AbMathUtil.getRandomInt(2);
		while (dbManager.queryOrderNumber(orderNum)) {
			orderNum = AbSharedUtil.getString(this, "machine_number")+ AbDateUtil.getStringByFormat(date, "yyyyMMddHHmmss")+ AbMathUtil.getRandomInt(2);
		}

		root.setSaleno(orderNum);

		// 数据库
		orderInfo = new OrdersInfo();

		orderInfo.setGoodsCode(g.getGoodsCode());
		orderInfo.setGoodsName(g.getGoodsName());
		orderInfo.setOrderStatus(OrderStatus.Unfinished.ordinal());
		orderInfo.setPayType(PayType.NO_PAY.ordinal());
		orderInfo.setTime(dataStr);
		orderInfo.setSyncStatuc(SyncStatuc.Unfinished.ordinal());
		orderInfo.setGoodsID(g.getGoodsNumber());
		orderInfo.setOrderNum(orderNum);
		orderInfo.setTotalNum(1);
		orderInfo.setTotalPrice(g.getGoodsPrice());

		orderId = dbManager.saveOrdersInfo(orderInfo);

		tv_order_number.setText(orderNum);
		if (activity_flag == 0) {
			ReadSerialPortServer.netWork.sendAliPay(root, new OnHttpListener() {
				@Override
				public void Message(int result, String msg) {
					// TODO Auto-generated method stub
					try {
						iv_order_alipay.setImageBitmap(ABQrCodeUtil
								.createQRCode(msg, 200));
						iv_order_alipay_small.setVisibility(View.VISIBLE);
					} catch (WriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			ReadSerialPortServer.netWork.sendWeiXinPay(root,new OnHttpListener() {

						@Override
						public void Message(int result, String msg) {
							// TODO Auto-generated method stub
							try {
								iv_order_wxpay.setImageBitmap(ABQrCodeUtil
										.createQRCode(msg, 200));
								iv_order_wxpay_small
										.setVisibility(View.VISIBLE);
							} catch (WriterException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
		} else {
			ReadSerialPortServer.netWork.sendAliPayTest(root,new OnHttpListener() {
						@Override
						public void Message(int result, String msg) {
							// TODO Auto-generated method stub
							try {
								Log.d("testali", msg);
								iv_order_alipay.setImageBitmap(ABQrCodeUtil.createQRCode(msg, 200));
								iv_order_alipay_small.setVisibility(View.VISIBLE);
							} catch (WriterException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
			
			ReadSerialPortServer.netWork.sendWeiXinPayTest(root, new OnHttpListener() {
				
				@Override
				public void Message(int result, String msg) {
					// TODO Auto-generated method stub
					try {
						Log.d("testwx", msg);
						iv_order_wxpay.setImageBitmap(ABQrCodeUtil.createQRCode(msg, 200));
						iv_order_wxpay_small.setVisibility(View.VISIBLE);
					} catch (WriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});			

		}
		
		ReadSerialPortServer.netWork.openPayTimer(orderNum, new OnPayStateListenner() {
			
			@Override
			public void PayMessage(int channel, int state) {
				// TODO Auto-generated method stub
				if (state==0) {//支付成功
					dialogOrder.dismiss();
					dialogShipment();//
					if (channel==1) {//支付宝
						dbManager.replaceOrdersInfoByPayType(orderId,PayType.ALI_PAY.ordinal());
					}else if (channel==2) {//微信
						dbManager.replaceOrdersInfoByPayType(orderId,PayType.WX_PAY.ordinal());
					}
					shipment(orderInfo.getGoodsID(),(byte) 0xff,true);// 出货
				}
			}
		});
		dialogOrder.show();
	}

	/**
	 * 交易详情
	 * 
	 * @param orderInfo
	 *            the orderInfo
	 * @param msg
	 *            说明,没有就null
	 */
	private void dialogPayMessage(OrdersInfo orderInfo, String msg) {
	//	dialogMessage = new Dialog(this);
		dialogMessage.setTitle("交易详情");
		dialogMessage.setCancelable(false);
		dialogMessage.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
			//	AbToastUtil.showToast(MainActivity.this, "dismiss");
			}
		});
		View view = getLayoutInflater().inflate(R.layout.dialog_pay_message,null);
		dialogMessage.setContentView(view, new LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,android.widget.RelativeLayout.LayoutParams.MATCH_PARENT));
		TextView tv_pay_number = (TextView) view.findViewById(R.id.tv_pay_number);
		TextView tv_pay_state = (TextView) view.findViewById(R.id.tv_pay_state);
		TextView tv_pay_time = (TextView) view.findViewById(R.id.tv_pay_time);
		TextView tv_pay_way = (TextView) view.findViewById(R.id.tv_pay_way);
		TextView tv_pay_pirce = (TextView) view.findViewById(R.id.tv_pay_pirce);
		TextView tv_pay_phone = (TextView) view.findViewById(R.id.tv_pay_phone);
		TextView tv_pay_explain = (TextView) view.findViewById(R.id.tv_pay_explain);
		tv_pay_second = (TextView) view.findViewById(R.id.tv_pay_second);

		tv_pay_number.setText(orderInfo.getOrderNum());
		if (!AbStrUtil.isEmpty(AbSharedUtil.getString(this, "phone"))) {
			tv_pay_phone.setText(AbSharedUtil.getString(this, "phone"));
		}
		tv_pay_pirce.setText(orderInfo.getTotalPrice() * 0.01f + "");
		tv_pay_time.setText(orderInfo.getTime());
		switch (orderInfo.getOrderStatus()) {
		// 订单状态 未完成，已完成，成功，失败，状态未知,未支付，未退款
		// public enum OrderStatus {
		// Unfinished, Finished, Success, Fail, Unknown,Unpay,Refund
		// }
		case 0:
			tv_pay_state.setText("未完成");
			break;
		case 1:
			tv_pay_state.setText("已完成");
			tv_pay_state.setTextColor(Color.GREEN);
			break;
		case 2:
			tv_pay_state.setText("成功");
			tv_pay_state.setTextColor(Color.GREEN);
			break;
		case 3:
			tv_pay_state.setText("失败");
			break;
		case 4:
			tv_pay_state.setText("状态未知");
			break;
		case 5:
			tv_pay_state.setText("未支付");
			break;
		case 6:
			tv_pay_state.setText("未退款");
			break;
		case 7:
			tv_pay_state.setText("测试成功");
			tv_pay_state.setTextColor(Color.GREEN);
			break;
		case 8:
			tv_pay_state.setText("测试失败");
			break;
		default:
			break;
		}

		switch (orderInfo.getPayType()) {
		// 支付类型,支付宝，微信，现金,还没确认
		// public enum PayType {
		// ALI_PAY,WX_PAY, CASH_PAY,NO_PAY
		// }
		case 0:
			tv_pay_way.setText("支付宝");
			break;
		case 1:
			tv_pay_way.setText("微信支付");
			break;
		case 2:
			tv_pay_way.setText("现金");
			break;
		default:
			tv_pay_way.setText("未知支付类型");
			break;
		}
		if (msg != null) {
			tv_pay_explain.setText(msg);
		} else {
			tv_pay_explain.setVisibility(View.GONE);
		}
		dialogMessage.show();
	}

	/**
	 * 出货中dialog
	 */
	public void dialogShipment() {
		// if (dialogShipment == null) {
	//	dialogShipment = new Dialog(this);
		dialogShipment.setCancelable(false);
		dialogShipment.setTitle("出货中");
		View view = getLayoutInflater().inflate(R.layout.dialog_shipment, null);
		dialogShipment.setContentView(view, new LayoutParams(new LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT)));
		// }
		dialogShipment.show();
	}

	class MessageTimer extends TimerTask {

		@Override
		public void run() {
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_main_shopcar:// 购物车
		{
			Intent intent = new Intent(this, SystemMenuActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.tv_main_balance: {// 结算
			updataShopCar();

			if (totalPirce != 0) {
				Intent intent = new Intent(this, OrderActivity.class);
				intent.putExtra("list", (Serializable) listCar);
				startActivity(intent);
			} else {

				if (totalPirce != 0) {
					Intent intent = new Intent(this, OrderActivity.class);
					intent.putExtra("list", (Serializable) listCar);
					startActivity(intent);
				} else {

					AbToastUtil.showToast(MainActivity.this, "请选择商品");
				}
			}
		}
			break;
		case R.id.tv_main_price: {
			Intent intent = new Intent(this, SystemMenuActivity.class);
			startActivity(intent);
		}
		case R.id.ll_main_return: {
			finish();
		}
			break;
		default:
			break;
		}
	}

	class KeyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getStringExtra("action");
			if (action.equals("UPDATE")) {
				mainList = dbManager.queryAllGoodsComInfoByKey();
				mainAdapter.updateView(mainList);
				binder.updateKeyStaus();
				String machine_number = AbSharedUtil.getString(
						MainActivity.this, "machine_number");
				if (!AbStrUtil.isEmpty(machine_number)) {
					tv_machine_number.setText(machine_number);
				}
				String machine_phone = AbSharedUtil.getString(
						MainActivity.this, "phone");
				if (!AbStrUtil.isEmpty(machine_phone)) {
					tv_machine_phone.setText(machine_phone);
				}

			} else if (action.equals("KEY")) {

				if (ll_main_closing.getVisibility() == View.GONE) {
					ad_number = 120;
					int key = intent.getIntExtra("key_value", 1);
					boolean flag_key = dbManager.queryGoodsStatuByKey(key);
					if (!dialogOrder.isShowing() && !dialogMessage.isShowing()&& !dialogShipment.isShowing()) {
						dialogOrder.dismiss();
						dialogShipment.dismiss();
						dialogMessage.dismiss();
						if (flag_key) {
							setCoin(RequestFunctionBit.CMD_OPEN_COIN, 0);// 打开投币器
							dialogOrder(key);
						}
					}
				}
			}

		}
	}

	@Override
	public void onResponse(Response res) {
		// TODO Auto-generated method stub
		int funcBit = res.getFuncBit();

		switch (funcBit) {
		case ResponseFunctionBit.RES_IS_TEMP:
			int[] temp = ((UartResponse) res).getTemp();// 获取温度
			if (temp[0] > 100 || temp[1] > 100) {
				ReadSerialPortServer.netWork.sendGoodsFault("温度异常", null);
			}
			Message msg = new Message();
			msg.what = 0x124;
			msg.obj = temp;
			mHandler.sendMessage(msg);
			break;
		case ResponseFunctionBit.RES_KEY_NUM:// 按键按下
		{
			int key = ((UartResponse) res).getKeyNum();
			Intent intent = new Intent("KEY_ACTION");
			intent.putExtra("action", "KEY");
			intent.putExtra("key_value", key);
			sendOrderedBroadcast(intent, null);
			Log.d(TAG, "-------2------->" + key);

		}
			break;
		case ResponseFunctionBit.RES_RECEIVE_COIN:// 收到的硬币
			// totalPirce
			totalMoney += ((UartResponse) res).getInCoinNum();
			mHandler.sendEmptyMessage(0x123);
			// Log.d(TAG, "-------3------->" + ((UartResponse)
			// res).getInCoinNum());
			break;
		case ResponseFunctionBit.RES_RECEIVE_PAPER:// 收到的纸币
			// Log.d(TAG, "-------4------->" + ((UartResponse)
			// res).getInPaperNum());
			totalMoney += ((UartResponse) res).getInPaperNum();
			mHandler.sendEmptyMessage(0x123);
			break;
		case ResponseFunctionBit.RES_COM_COMPLETE:// 出货状态
			shipmentResult(orderInfo.getGoodsID(),res.data[0]);
			break;
		case ResponseFunctionBit.RES_COM_CORRECT:// 出货命令正确

			break;
		case ResponseFunctionBit.RES_SHIPMENT_TIMEOUT:// 出货超时
			setCoin(RequestFunctionBit.CMD_CLOSE_COIN,outCoin + orderInfo.getTotalPrice());// 退币
			if(outCoin!=0){
				setCoin(RequestFunctionBit.CMD_CLOSE_COIN,outCoin + orderInfo.getTotalPrice());// 退币
				outCoin = 0;
			}else{
				setCoin(RequestFunctionBit.CMD_CLOSE_COIN,0);// 退币
			}			
			mHandler.sendEmptyMessage(0x125);
			break;
		case ResponseFunctionBit.RES_DOOR_KEY:
			if (((UartResponse) res).doorIsOpen()) {
				Integer orderMaxId = dbManager.getOrdersInfoMaxId();
				// if (orderMaxId!=null) {
				SetOpenDoor openDoor = new SetOpenDoor();
				openDoor.setOpenTime(AbDateUtil.getCurrentDate(AbDateUtil.dateFormatYMDHMS));
				
				openDoor.setOrderId(orderMaxId);
				openDoor.setSyncStatuc(AppConfig.SyncStatuc.Unfinished.ordinal());
				dbManager.saveSetOpenDoor(openDoor);
				// }
				 ReadSerialPortServer.netWork.sendOpenDoor();				
				startActivity(new Intent(this, SystemMenuActivity.class));
			
			}
			if (((UartResponse) res).clickLeft()) {
				Log.d(TAG, "-------7------->");
			} else if (((UartResponse) res).clickRight()) {
				Log.d(TAG, "-------9------->");
			} else if (((UartResponse) res).clickConfirm()) {
				Log.d(TAG, "-------8------->");
				mHandler.sendEmptyMessage(0x126);// 如果在购买页面则取消购买
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 退币命令,退完币之后会自动关闭投币器
	 * 
	 * @param coinStatus
	 *            打开或者关闭
	 * @param coin
	 *            退币金额分为单位
	 * */
	private void setCoin(int coinStatus, int coin) {
		Uart1Request req = new Uart1Request();
		byte[] data = { 0, 0, 0, 0, 0 };
		req.setMachineType((byte) (1 + AbSharedUtil.getInt(MainActivity.this,"machine")));
		req.setFuncBit((byte) coinStatus);//
		req.setCommBit((byte) (coin / 10));
		req.setData(data);
		ControlApp.uartHand1.execute(req);
	}
	
	
	/**出货结果处理
	 * @param res 出货结果，0，出货中，1，出货成功，2出货失败
	 * */
	
	private void shipmentResult(int key,byte res) {
	//	Log.d("temp", "-------出货------>"+res);
		switch (res) {
		case 0:// 出货中
	//		Log.d("temp", "-------出货中------>");
			break;
		case 1:// 出货成功
			shipmentTime.cancel();//
			shipment(orderInfo.getGoodsID(),(byte) 1,false);//出货结果反馈
			if (activity_flag == 1) {
				dbManager.replaceOrdersInfoByID(orderId,
						OrderStatus.TestSuccess.ordinal());// 测试出货成功
			} else {
				dbManager.replaceOrdersInfoByID(orderId,
						OrderStatus.Success.ordinal());// 出货成功
			}
			ReadSerialPortServer.netWork.sendsale();
			setCoin(RequestFunctionBit.CMD_CLOSE_COIN, outCoin);
			outCoin = 0;//退币数复位

			dbManager.updateGoodsCapacity(orderInfo.getGoodsID());
			mHandler.sendEmptyMessage(0x125);
		//	Log.d(TAG, "-------5------->");
			break;
		case 2:// 出货失败
			shipmentTime.cancel();//
			shipment(orderInfo.getGoodsID(),(byte) 1,false);//出货结果反馈
			if (activity_flag == 1) {
				dbManager.replaceOrdersInfoByID(orderId,
						OrderStatus.TestFail.ordinal());// 测试出货失败
			} else {
				dbManager.replaceOrdersInfoByID(orderId,
						OrderStatus.Fail.ordinal());// 出货失败
			}
			ReadSerialPortServer.netWork.sendsale();
			if(outCoin==0){//outCoin为0表示投币数量小于商品价格
				setCoin(RequestFunctionBit.CMD_CLOSE_COIN,outCoin);// 退币
			}else{
				setCoin(RequestFunctionBit.CMD_CLOSE_COIN,
						outCoin + orderInfo.getTotalPrice());// 退币
				outCoin= 0;
			}			

			mHandler.sendEmptyMessage(0x125);
			break;
		default:
			break;
		}
	}


	/**
	 * 出货方法
	 * 
	 * @param layer
	 *            层数
	 * @param goodsNum
	 *            layer 层的第goodsNum个货道
	 *  定时器，每3秒发送一次出货命令，直到返回出货成功或者失败
	 * */

	private void shipment(int key,byte arg,boolean flag) {
		final Uart1Request req = new Uart1Request();
		byte[] data = { 0, 0, 0, 0, 0 };
		req.setMachineType((byte) (AbSharedUtil.getInt(MainActivity.this,"machine") + 1));
		req.setFuncBit((byte) RequestFunctionBit.CMD_SHIPMENT);// 出货命令
		req.setCommBit((byte) arg);//出货参数
		data[0] = (byte) (key/100);//第几层
		data[1] = (byte) (key%100);//第几个
		req.setData(data);
		if(flag){
			shipmentTime =new Timer();
			shipmentTime.schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ControlApp.uartHand1.execute(req);
				}
			}, 0, 3000);
		}else{
			ControlApp.uartHand1.execute(req);
		}
		
		
	}


	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		ad_number = 120;
	}

	@Override
	public void onListener(boolean flag) {// 营业控制
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = 0x127;
		msg.obj = flag;
		
		mHandler.sendMessage(msg);
	}
}
