package com.kyd.springmachine;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kyd.library.AbActivity;
import com.kyd.library.util.AbDateUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.AbWifiUtil;
import com.kyd.library.util.SunTimes;
import com.kyd.springmachine.server.ReadSerialPortServer;
import com.kyd.springmachine.server.ReadSerialPortServer.EfficientReceiver;
import com.kyd.springmachine.widget.MyDialog;
import com.kyd.springmachine.widget.MyDialog.onDialogListener;
import com.kyd.springmachine.widget.MyDialog.onDialogMsgListener;

/**
 * 系统设置
 * 
 * @author 8015
 *
 */
public class SystemSetActivity extends AbActivity implements OnClickListener {
	public static final String ACTION_LIGHT_AUTO = "time light auto";
	public static final String ACTION_LIGHT_STOP = "time light stop";
	public static final String LIGHT_OPEN = "light open";
	public static final String LIGHT_CLOSE = "light close";
	public static final String BUY_OPEN = "buy open";
	public static final String BUY_CLOSE = "buy close";
	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	// 界面
	private EditText et_machine_number;
	private EditText et_phone;
	private EditText et_password;

	private RelativeLayout rl_machine;
	private RelativeLayout rl_business_time;
	private RelativeLayout rl_wifi_set;
	private RelativeLayout rl_coin_machine;
	private RelativeLayout rl_light;
	private RelativeLayout rl_machine_height;

	private TextView tv_machine;
	private TextView tv_business_time;
	private TextView tv_wifi_set;
	private TextView tv_coin_machine;
	private TextView tv_light;
	private TextView tv_machine_height;

	private Switch sw_wifi;
	private Switch sw_screen_model;
	private Switch sw_machine_adver;

	private String[] machine = { "饮料机", "杂货机", "弹簧机" };
	private String[] coin_machine = {"松下", "MEI" };
	private String[] light = {"常关", "自动",  "常开" };
	
	private String[] strSSID;

	private WifiManager wifiManager;

	private MyDialog dialog;
	
	private int falg;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 连接成功
				String m = (String) msg.obj;
				AbToastUtil.showToast(SystemSetActivity.this, "连接成功");
				tv_wifi_set.setText(m);
				break;
			case 2:// 连接失败
				AbToastUtil.showToast(SystemSetActivity.this, "连接超时");
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_set);
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
		rl_machine = (RelativeLayout) findViewById(R.id.rl_machine);
		rl_business_time = (RelativeLayout) findViewById(R.id.rl_business_time);
		rl_wifi_set = (RelativeLayout) findViewById(R.id.rl_wifi_set);
		rl_coin_machine = (RelativeLayout) findViewById(R.id.rl_coin_machine);
		rl_light = (RelativeLayout) findViewById(R.id.rl_light);
		rl_machine_height = (RelativeLayout) findViewById(R.id.rl_machine_height);

		tv_machine = (TextView) findViewById(R.id.tv_machine);
		tv_business_time = (TextView) findViewById(R.id.tv_business_time);
		tv_wifi_set = (TextView) findViewById(R.id.tv_wifi_set);
		tv_coin_machine = (TextView) findViewById(R.id.tv_coin_machine);
		tv_light = (TextView) findViewById(R.id.tv_light);
		tv_machine_height = (TextView) findViewById(R.id.tv_machine_height);

		et_machine_number = (EditText) findViewById(R.id.et_machine_number);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_password = (EditText) findViewById(R.id.et_password);

		sw_wifi = (Switch) findViewById(R.id.sw_wifi);
		sw_screen_model = (Switch) findViewById(R.id.sw_screen_model);
		sw_machine_adver=(Switch) findViewById(R.id.sw_machine_adver);

		tv_title_right.setText("保存");
		tv_title_name.setText("系统设置");

	}

	@Override
	public void initData() {
		falg = getIntent().getIntExtra("flag", 0);

		WifiInfo wifiInfo = AbWifiUtil.getConnectionInfo(this);
		dialog = new MyDialog();
		wifiManager = (WifiManager) SystemSetActivity.this.getSystemService(SystemSetActivity.this.WIFI_SERVICE);
		tv_wifi_set.setText(AbStrUtil.parseEmpty(wifiInfo.getSSID()));

		int machine_model = AbSharedUtil.getInt(this, "machine");
		tv_machine.setText(machine[machine_model]);
		if (machine_model == 2) {// 弹簧机
			rl_machine_height.setVisibility(View.VISIBLE);
		} else {
			rl_machine_height.setVisibility(View.GONE);
		}

		tv_coin_machine.setText(coin_machine[AbSharedUtil.getInt(this, "coin_machine")]);
		tv_light.setText(light[AbSharedUtil.getInt(this, "light")]);
		int machine_height = AbSharedUtil.getInt(this, "machine_height");
		tv_machine_height.setText(machine_height + "cm");

		tv_title_right.setText("保存");
		tv_title_name.setText("系统设置");

		boolean wifi_state = AbWifiUtil.isWifiConnectivity(this);
		if (wifi_state == true) {
			sw_wifi.setChecked(true);
		} else {
			sw_wifi.setChecked(false);
			rl_wifi_set.setVisibility(View.GONE);
		}

		boolean screen_model = AbSharedUtil.getBoolean(this, "screen_model", false);//true触摸屏,false非触摸屏
		sw_screen_model.setChecked(screen_model);
		
		boolean machine_adver=AbSharedUtil.getBoolean(this, "machine_adver", true);//true后台,falseSD卡
		sw_machine_adver.setChecked(machine_adver);

		String machine_number = AbSharedUtil.getString(this, "machine_number");
		String phone = AbSharedUtil.getString(this, "phone");
		String password = AbSharedUtil.getString(this, "password");
		
		String buy_time_start= AbSharedUtil.getString(SystemSetActivity.this, "buy_time_start","0:00");// 营业开始时间
		String buy_time_end= AbSharedUtil.getString(SystemSetActivity.this, "buy_time_end","24:00");// 营业结束时间
		tv_business_time.setText(buy_time_start+"-"+buy_time_end);
		et_machine_number.setText(machine_number);
		et_phone.setText(phone);
		et_password.setText(password);
	}

	@Override
	public void setListeners() {
		rl_machine.setOnClickListener(this);
		rl_business_time.setOnClickListener(this);
		rl_wifi_set.setOnClickListener(this);
		rl_coin_machine.setOnClickListener(this);
		rl_light.setOnClickListener(this);
		rl_machine_height.setOnClickListener(this);

		bt_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);

		sw_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean flag) {
				if (flag) {// wifi开关
					AbWifiUtil.setWifiEnabled(SystemSetActivity.this, true);
					rl_wifi_set.setVisibility(View.VISIBLE);
					new Thread(new wifiState()).start();
				} else {
					AbWifiUtil.setWifiEnabled(SystemSetActivity.this, false);
					rl_wifi_set.setVisibility(View.GONE);
				}
			}
		});

		sw_screen_model.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// 设置触摸屏
				AbSharedUtil.putBoolean(SystemSetActivity.this, "screen_model", arg1);
			}
		});
		
		sw_machine_adver.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				//设置广告
				AbSharedUtil.putBoolean(SystemSetActivity.this, "machine_adver", arg1);
			}
		});
	}

	/**
	 * wifi列表dialog
	 */
	private void dialogWifi() {
		Builder builder = new Builder(this);
		builder.setTitle("Wifi");
		strSSID = AbWifiUtil.getListSSID(this);
		builder.setItems(strSSID, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				joinWifi(which);
			}
		});
		builder.show();
	}

	/**
	 * wifi输入密码dialog
	 * 
	 * @param number
	 */
	private void joinWifi(final int number) {

		dialog.setInputDialog(this, "wifi", new String[] { "名称:" + strSSID[number], "密码:" }, 2,
				new onDialogMsgListener() {

					@Override
					public void Msg(String start, String end) {
						WifiConfiguration con = AbWifiUtil.createWifiInfo(strSSID[number], end, 3);

						int networkId = wifiManager.addNetwork(con);

						wifiManager.enableNetwork(networkId, true);
						new Thread(new wifiState()).start();
					}
				});
	}

	/**
	 * 营业时间dialog
	 */
	private void dialogBusiness() {
		dialog.setTimeDialog(this, "营业时间设置", "注:0：00-24:00为24小时营业", new onDialogMsgListener() {

			@Override
			public void Msg(String start, String end) {
				// TODO Auto-generated method stub
				AbSharedUtil.putString(SystemSetActivity.this, "buy_time_start",start);// 营业开始时间
				AbSharedUtil.putString(SystemSetActivity.this, "buy_time_end",end);// 营业结束时间
				tv_business_time.setText(start+"-"+end);
				if(start.equals("24:00")&&end.equals("00:00")){
					
				}else{
					setAlarm(end, 10001,BUY_CLOSE);// 设置营业范围
					setAlarm(start,10000,BUY_OPEN);					
				}
			}
			
		});
		final Dialog dialog = new Dialog(this);
		dialog.setTitle("营业时间设置");
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = this.getLayoutInflater().inflate(R.layout.dialog_business_time, null);
		dialog.addContentView(view, new LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		final EditText et_start_time_hour = (EditText) view.findViewById(R.id.et_start_time_hour);
		final EditText et_start_time_second = (EditText) view.findViewById(R.id.et_start_time_second);
		final EditText et_end_time_hour = (EditText) view.findViewById(R.id.et_end_time_hour);
		final EditText et_end_time_second = (EditText) view.findViewById(R.id.et_end_time_second);

		Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String start_time_hour = et_start_time_hour.getText().toString();
				String string_time_second = et_start_time_second.getText().toString();
				String end_time_hour = et_end_time_hour.getText().toString();
				String end_time_second = et_end_time_second.getText().toString();

				if (AbDateUtil.isDataHour(start_time_hour) && AbDateUtil.isDataSecond(string_time_second)
						&& AbDateUtil.isDataHour(end_time_hour) && AbDateUtil.isDataSecond(end_time_second)) {
					int int_start_time_hour = Integer.parseInt(start_time_hour);
					int int_string_time_second = Integer.parseInt(string_time_second);
					int int_end_time_hour = Integer.parseInt(end_time_hour);
					int int_end_time_second = Integer.parseInt(end_time_second);
					
					tv_business_time.setText(String.format("%02d", int_start_time_hour) + ":"
							+ String.format("%02d", int_string_time_second) + "-"
							+ String.format("%02d", int_end_time_hour) + ":"
							+ String.format("%02d", int_end_time_second));

					AbSharedUtil.putString(SystemSetActivity.this, "buy_time_start",
							String.format("%02d", int_start_time_hour) + String.format("%02d", int_string_time_second));// 营业开始时间
					AbSharedUtil.putString(SystemSetActivity.this, "buy_time_end",  String.format("%02d", int_end_time_hour) + String.format("%02d", int_end_time_second));// 营业结束时间
//					if(buy_time_end==2400&&buy_time_start==0){
//						
//					}else{
//						setAlarm(start_time_hour + string_time_second,10000,BUY_OPEN);
//						setAlarm(end_time_hour + end_time_second, 10001,BUY_CLOSE);// 设置营业范围
//					}
					
					dialog.dismiss();
				} else {
					AbToastUtil.showToast(SystemSetActivity.this, "请输入正确的时间");
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.setCanceledOnTouchOutside(true);
	//	dialog.show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_machine: {// 机器类型
			dialog.setRadioDialog(this, machine, AbSharedUtil.getInt(this, "machine"), new onDialogListener() {

				@Override
				public void confirm(int number) {
					AbSharedUtil.putInt(SystemSetActivity.this, "machine", number);
					tv_machine.setText(machine[number]);
					if (number == 2) {// 弹簧机
						rl_machine_height.setVisibility(View.VISIBLE);
					} else {
						rl_machine_height.setVisibility(View.GONE);
					}
				}
			});
		}
			break;
		case R.id.rl_business_time:// 营业时间
			dialogBusiness();
			break;
		case R.id.rl_wifi_set:// wifi设置
			dialogWifi();
			break;
		case R.id.rl_coin_machine: {// 投币器模式
			dialog.setRadioDialog(this, coin_machine, AbSharedUtil.getInt(this, "coin_machine"),
					new onDialogListener() {

						@Override
						public void confirm(int number) {
							AbSharedUtil.putInt(SystemSetActivity.this, "coin_machine", number);
							tv_coin_machine.setText(coin_machine[number]);
						}
					});
		}
			break;
		case R.id.rl_light: {// 灯
			dialog.setRadioDialog(this, light, AbSharedUtil.getInt(this, "light"), new onDialogListener() {

				@Override
				public void confirm(int number) {
					if (number == 1) {
						String latitude = AbSharedUtil.getString(SystemSetActivity.this, "latitude");
						String longitude = AbSharedUtil.getString(SystemSetActivity.this, "longitude");
						if (latitude == null) {
							SunTimes.GetSunTime(AppConfig.longitude, AppConfig.latitude);// 如果没有自动定位，则经纬度默认为广州
						} else {
							SunTimes.GetSunTime(Double.parseDouble(longitude), Double.parseDouble(latitude));
						}
						String sunset = SunTimes.getSunSet();
						String suntimes = SunTimes.getSunrise();
						
						AbSharedUtil.putString(SystemSetActivity.this, "auto_liht_start", sunset.substring(0, 2)+sunset.substring(3,5));//自动开灯时间
						AbSharedUtil.putString(SystemSetActivity.this, "auto_liht_close", suntimes.substring(0, 2)+suntimes.substring(3,5));// 自动关灯时间
					//	System.out.println(SunTimes.getSunSet()+"------------>"+SunTimes.getSunrise());
						setAlarm(SunTimes.getSunSet(),20000,LIGHT_OPEN);
						setAlarm(SunTimes.getSunrise(), 20001,LIGHT_CLOSE);
					}else if(number==0){
						Intent intent = new Intent(SystemSetActivity.this, EfficientReceiver.class);
						intent.setAction(ACTION_LIGHT_STOP);
						sendBroadcast(intent);
					}else{
						Intent intent = new Intent(SystemSetActivity.this, EfficientReceiver.class);
						intent.setAction(ACTION_LIGHT_AUTO);
						sendBroadcast(intent);
					}
					AbSharedUtil.putInt(SystemSetActivity.this, "light", number);
					tv_light.setText(light[number]);
				}
			});
		}
			break;
		case R.id.rl_machine_height: {
			dialog.setInputDialog(this, "机器高度", new String[] { "高度" }, 1, new onDialogMsgListener() {

				@Override
				public void Msg(String start, String end) {
					if (AbStrUtil.isNumber(start)) {
						int height = Integer.parseInt(start);
						AbSharedUtil.putInt(SystemSetActivity.this, "machine_height", height);
						tv_machine_height.setText(height + "cm");

					} else {
						AbToastUtil.showToast(SystemSetActivity.this, "请输入整数");
					}
				}
			});
		}
			break;
		case R.id.tv_title_right: {// 保存

			String machine_number = et_machine_number.getText().toString();
			String phone = et_phone.getText().toString();
			String password = et_password.getText().toString();
			if (!AbStrUtil.isNumber(machine_number) && machine_number.length() == 8) {
				AbToastUtil.showToast(this, "机器码格式不对");
				// dialog.setPromptDialog(this, "提示","机器码格式不对");
			} else if (!AbStrUtil.isMobileNo(phone)) {
				AbToastUtil.showToast(this, "电话格式不对");
				// dialog.setPromptDialog(this, "提示","电话格式不对");
			} else {
				String mNumber=AbSharedUtil.getString(this, "machine_number","");
				String mPhone=AbSharedUtil.getString(this, "phone","");
				if (!mNumber.equals(machine_number)||!mPhone.equals(phone)) {					
					AbSharedUtil.putString(this, "machine_number", machine_number);
					AbSharedUtil.putString(this, "phone", phone);
					ReadSerialPortServer.netWork.sendLogin("修改");
				}
				AbSharedUtil.putString(this, "password", password);
				if (falg == 1) {
					startActivity(new Intent(SystemSetActivity.this, MainActivity.class));
					ReadSerialPortServer.netWork.sendLogin("初次登录");
				} else {
					Intent intent = new Intent("KEY_ACTION");
					intent.putExtra("action", "UPDATE");
					this.sendBroadcast(intent);
					finish();
				}
			}
		}
			break;
		case R.id.bt_title_left: {// 返回
			finish();
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
	
	class wifiState implements Runnable {

		private int flag = 20;

		@Override
		public void run() {

			while (flag > 0) {
				WifiInfo wifiInfo = AbWifiUtil.getConnectionInfo(SystemSetActivity.this);
				if (null != wifiInfo.getSSID() && 0 != wifiInfo.getIpAddress()) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = wifiInfo.getSSID();
					handler.sendMessage(msg);
					break;
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				flag--;
			}
			if (flag <= 0) {
				handler.sendEmptyMessage(2);
			}
		}

	}

	// 设置定时

	private void setAlarm(String time,int id,String action) {
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(SystemSetActivity.this, EfficientReceiver.class);
	//	intent.setAction(LIGHT_OPEN);
		intent.setAction(action);
		PendingIntent sender = PendingIntent.getBroadcast(SystemSetActivity.this, 2 * id, intent, 0);
		alarm.setRepeating(AlarmManager.RTC, AbDateUtil.getTimeMillis(time), AbDateUtil.DAY, sender);
		intent = new Intent(SystemSetActivity.this, EfficientReceiver.class);
	}

}
