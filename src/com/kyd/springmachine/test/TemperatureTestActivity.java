package com.kyd.springmachine.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.hardware.Response;
import com.kyd.library.hardware.UartResponse;
import com.kyd.library.hardware.Request.RequestFunctionBit;
import com.kyd.library.hardware.Response.Listener;
import com.kyd.library.hardware.Response.ResponseFunctionBit;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.springmachine.ControlApp;
import com.kyd.springmachine.EfficientActivity;
import com.kyd.springmachine.MainActivity;
import com.kyd.springmachine.R;
import com.kyd.springmachine.uart.Uart1Request;
import com.kyd.springmachine.widget.MyDialog;
import com.kyd.springmachine.widget.MyDialog.onDialogMsgListener;

public class TemperatureTestActivity extends AbActivity implements
		OnClickListener,Listener{

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	// 界面
	private RelativeLayout rl_temp_set;
	private TextView tv_current_temp;
	private TextView tv_temp_set;
	private Button bt_temp_start;
	private Button bt_temp_end;
	private int[] temp;
	private int temp_status=0;
	private int temp_set=0;
	private MyDialog myDialog;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0x123:// 倒计时
				int machineType = AbSharedUtil.getInt(TemperatureTestActivity.this, "machine");
				if(machineType==1){
					tv_current_temp.setText(temp[0]);
				}else{
					tv_current_temp.setText(temp[1]+"℃     "+temp[0]+"℃");
				}
				
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
		setContentView(R.layout.activity_test_temp);
		ControlApp.uartHand1.addListener(this);
		
		initViews();
		initData();
		setListeners();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ControlApp.uartHand1.removeListener(this);
	}
	
	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		// title
		bt_title_left = (Button) findViewById(R.id.bt_title_left);
		tv_title_name = (TextView) findViewById(R.id.tv_title_name);
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		// 界面
		rl_temp_set = (RelativeLayout) findViewById(R.id.rl_temp_set);
		tv_current_temp = (TextView) findViewById(R.id.tv_current_temp);
		tv_temp_set = (TextView) findViewById(R.id.tv_temp_set);
		bt_temp_start = (Button) findViewById(R.id.bt_temp_start);
		bt_temp_end = (Button) findViewById(R.id.bt_temp_end);

		tv_title_name.setText("温度测试");
		tv_title_right.setVisibility(View.INVISIBLE);
	}

	@Override
	public void initData() {
		myDialog = new MyDialog();
	}

	// 温度设置dialog
	private void dialogTemp() {
		myDialog.setInputDialog(this, "温度设置", new String[] { "温度设置:" }, 1,
				new onDialogMsgListener() {

					@Override
					public void Msg(String start, String end) {
						if (AbStrUtil.isNatural(start)) {
							temp_set = Integer.parseInt(start);
							if (temp_set>-50&&temp_set<50) {
								AbToastUtil.showToast(TemperatureTestActivity.this,"输入格式正确");
								tv_temp_set.setText(start);
							}
						} else {
							AbToastUtil.showToast(TemperatureTestActivity.this,
									"输入格式不正确");
						}
					}
				});
	}

	@Override
	public void setListeners() {
		bt_temp_start.setOnClickListener(this);
		bt_temp_end.setOnClickListener(this);
		bt_title_left.setOnClickListener(this);
		rl_temp_set.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_temp_start:// 开始
		{
			if(temp!=null){
				bt_temp_start.setEnabled(false);
				if(temp_set>temp[0]|temp_set>temp[1]){//开加热,下面的顺序不能改变，不然会出事
					ctrlTemp((byte)0,(byte)1);//关制冷
					ctrlTemp((byte)0,(byte)2);//关冷风风扇
					ctrlTemp((byte)1,(byte)4);//开加热
				}else if(temp_set<temp[0]|temp_set<temp[1]){//开制冷,下面的顺序不能改变，不然会出事
					ctrlTemp((byte)0,(byte)4);//关加热		
					ctrlTemp((byte)1,(byte)1);//开制冷
					ctrlTemp((byte)1,(byte)2);//开冷风风扇	
					
				}	
			}
					
		
		}
			break;
		case R.id.bt_temp_end:// 结束
			bt_temp_start.setEnabled(true);			
			ctrlTemp((byte)0,(byte)1);//关制冷
			ctrlTemp((byte)0,(byte)2);//关冷风风扇
			ctrlTemp((byte)0,(byte)4);//关加热
			break;
		case R.id.bt_title_left:
			ctrlTemp((byte)0,(byte)1);//关制冷
			ctrlTemp((byte)0,(byte)2);//关冷风风扇
			ctrlTemp((byte)0,(byte)4);//关加热
			finish();
			break;
		case R.id.rl_temp_set:
			dialogTemp();
			break;
		default:
			break;
		}
	}

	@Override
	public void onResponse(Response res) {
		// TODO Auto-generated method stub
		if(res.getFuncBit()==ResponseFunctionBit.RES_IS_TEMP){
			temp = ((UartResponse) res).getTemp();//温度
			mHandler.sendEmptyMessage(0x123);
		}
	}
	
	private void ctrlTemp(byte sw,byte type){
		Uart1Request req = new Uart1Request();	
		byte[] data = {0,0,0,0,0};
		req.setMachineType((byte) (1+AbSharedUtil.getInt(TemperatureTestActivity.this, "machine")));
		req.setFuncBit((byte) RequestFunctionBit.CMD_CTRL);//
		req.setCommBit(sw);//打开
		data[0]=type;
		req.setData(data);	
		ControlApp.uartHand1.execute(req);
	}
	
	
}
