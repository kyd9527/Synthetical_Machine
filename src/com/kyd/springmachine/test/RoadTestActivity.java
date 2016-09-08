package com.kyd.springmachine.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.hardware.Response;
import com.kyd.library.hardware.UartResponse;
import com.kyd.library.hardware.Request.RequestFunctionBit;
import com.kyd.library.hardware.Response.Listener;
import com.kyd.library.hardware.Response.ResponseFunctionBit;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbLogUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.AbViewHolder;
import com.kyd.springmachine.ControlApp;
import com.kyd.springmachine.MainActivity;
import com.kyd.springmachine.R;
import com.kyd.springmachine.AppConfig.OrderStatus;
import com.kyd.springmachine.bean.GoodsTestInfo;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.server.ReadSerialPortServer;
import com.kyd.springmachine.uart.Uart1Request;

public class RoadTestActivity extends AbActivity implements OnClickListener,Listener{

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	// ����
	private ListView lv;
	private Button bt_temp_start;
	private Button bt_temp_end;
	private DataBaseManager dataBase;
	private AbAdapter<GoodsTestInfo> adapter;

	private List<GoodsTestInfo> list;
	private int goodscount = 0,shipment_goods=1;
	private Thread thread;
	private boolean loop_flag = false;
	private boolean single_flag=false;
	private boolean shipment_flag = false;
	private Timer shipmentTime;

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x123:
			//	adapter.updateView(list);
				adapter.notifyDataSetChanged();
				lv.setSelectionFromTop((Integer) msg.obj-1, 0);
				break;
			case 0x124:
			//	adapter.updateView(list);
			   adapter.notifyDataSetChanged();
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
		setContentView(R.layout.activity_test_road);
		dataBase = DataBaseManager.getInstance(this);
		goodscount =(int) dataBase.getGoodsInfoCount(); 
		ControlApp.uartHand1.addListener(this);
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
		TextView tv_road_number = (TextView) findViewById(R.id.tv_road_number);
		TextView tv_road_state = (TextView) findViewById(R.id.tv_road_state);
		TextView tv_road_fault = (TextView) findViewById(R.id.tv_road_fault);
		// ����
		lv = (ListView) findViewById(R.id.lv);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		bt_temp_start = (Button) findViewById(R.id.bt_temp_start);
		bt_temp_end = (Button) findViewById(R.id.bt_temp_end);

		tv_title_name.setText("��������");
		tv_title_right.setVisibility(View.INVISIBLE);

		tv_road_number.setText("������");
		tv_road_state.setText("����״̬");
		tv_road_fault.setText("����״̬");
	}

	@Override
	public void initData() {
		list = new ArrayList<GoodsTestInfo>();
		List<Integer> goodsStatus = dataBase.getGoodsStatusList();
		// ��ȡ���ݿ��
		for (int i = 1; i <= goodscount; i++) {
			GoodsTestInfo g = new GoodsTestInfo();
			g.setNumber(i);			
			g.setGoodFault(1);
			g.setGoodState(goodsStatus.get(i-1));
			g.setState(0);//״̬
			list.add(g);
		}

		adapter = new AbAdapter<GoodsTestInfo>(this, list,
				R.layout.item_test_road) {

			@Override
			public void convert(AbViewHolder viewHolder, GoodsTestInfo item,
					int position) {
				LinearLayout ll_item = viewHolder.getView(R.id.ll_item);
				TextView tv_road_number = viewHolder.getView(R.id.tv_road_number);
				TextView tv_road_state = viewHolder.getView(R.id.tv_road_state);
				TextView tv_road_fault = viewHolder.getView(R.id.tv_road_fault);
				final int number = item.getNumber();
				tv_road_number.setText(number + "");
				if (item.getState()==1) {
					tv_road_number.setTextColor(Color.RED);
				}else {
					tv_road_number.setTextColor(Color.BLACK);
				}
				if (item.getGoodState() == 0) {
					tv_road_state.setTextColor(Color.RED);
					tv_road_state.setText("�޻�");
				} else {
					tv_road_state.setTextColor(Color.GREEN);
					tv_road_state.setText("�л�");
				}
				if (item.getGoodFault() == 0) {
					tv_road_fault.setTextColor(Color.RED);
					tv_road_fault.setText("����");
				} else {
					tv_road_fault.setTextColor(Color.GREEN);
					tv_road_fault.setText("����");
				}

				ll_item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						AlertDialog.Builder builder = new Builder(
								RoadTestActivity.this);
						builder.setTitle("����������");
						builder.setMessage("�Ƿ���Ե�" + number + "����");
						builder.setNegativeButton("��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,int arg1) {
										// TODO Auto-generated method stub
										bt_temp_start.setEnabled(false);
										lv.setEnabled(false);
										single_flag=true;
										loop_flag = false;
										for (GoodsTestInfo goodsTestInfo : list) {
											goodsTestInfo.setState(0);
										}
										list.get(number-1).setState(1);
										handler.sendEmptyMessage(0x124);
										new Thread(new singleRoadRunnable(number)).start();
									}
								});
						builder.setPositiveButton("��", null);
						builder.show();
					}
				});

			}
		};
		lv.setAdapter(adapter);
			
	}

	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
		bt_temp_start.setOnClickListener(this);
		bt_temp_end.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (thread != null) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_title_left:			
			finish();
			break;
		case R.id.bt_temp_start: {
			loop_flag = true;
			single_flag=false;
			new Thread(new loopRoadRunnable()).start();
			bt_temp_start.setEnabled(false);
			lv.setEnabled(false);
		}
			break;
		case R.id.bt_temp_end: {
			loop_flag = false;
			single_flag=false;
			bt_temp_start.setEnabled(true);
			lv.setEnabled(true);
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
	
	class loopRoadRunnable implements Runnable {
		
		@Override
		public void run() {
			int num=1;
			while (loop_flag) {				
				shipment((byte) 0xff,num,true);				
				for (GoodsTestInfo goodsTestInfo : list) {
					goodsTestInfo.setState(0);
				}
				list.get(num-1).setState(1);
				handler.sendEmptyMessage(0x124);
				num++;
				if (num>list.size()) {
					num=1;
				}
				while(true){
					if(shipment_flag){
						shipment_flag = false;
						break;
					}
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class singleRoadRunnable implements Runnable{

		int number;
		
		private singleRoadRunnable(int number){
			this.number=number;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (single_flag) {
				
				shipment((byte) 0xff,number,true);
				while(true){
					if(shipment_flag){
						shipment_flag = false;
						break;
					}
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void onResponse(Response res) {
		// TODO Auto-generated method stub

		int funcBit = res.getFuncBit();	
		switch (funcBit) {
		case ResponseFunctionBit.RES_COM_COMPLETE:// �������
			
			
			shipmentResult(res.data[0]);
			
							
			break;
		case ResponseFunctionBit.RES_COM_CORRECT:// ����������ȷ
			
			break;
		case ResponseFunctionBit.RES_SHIPMENT_TIMEOUT:// ������ʱ
			
			break;	
		case ResponseFunctionBit.RES_GOODS_STATE://���޻�
			byte[]goods = ((UartResponse) res).getGoodsState(goodscount);
			for(int i = 0;i<goodscount;i++){
				list.get(i).setGoodState(goods[i]);
			}
			System.out.println("------RES_GOODS_STATE---------->");
			handler.sendEmptyMessage(0x124);			
			break;
		default:
			break;
		}
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		loop_flag = false;
		single_flag=false;
		shipment_flag = false;
	}
	
	
	/**
	 * ��������
	 * 
	 * @param layer
	 *            ����
	 * @param goodsNum
	 *            layer ��ĵ�goodsNum������
	 * */

	private void shipment(byte arg,int number,boolean flag) {
		final Uart1Request req = new Uart1Request();
		byte[] data = { 0, 0, 0, 0, 0 };
		req.setMachineType((byte) (1+ AbSharedUtil.getInt(RoadTestActivity.this, "machine")));
		req.setFuncBit((byte) RequestFunctionBit.CMD_SHIPMENT);// ��������
		req.setCommBit(arg);
		shipment_goods = number;
		data[1] = (byte) number;// ������
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

	
	private void shipmentResult(byte res) {
		switch (res) {
		case 0:// ������
			Log.d("test", "-------������------>");
			break;
		case 1:// �����ɹ�
			shipmentTime.cancel();//
			shipment((byte) 1,shipment_goods,false);//�����������
			shipment_flag=true;
			list.get(shipment_goods-1).setGoodFault(1);
			{
				Message msg = new Message();
				msg.what= 0x123;
				msg.obj = shipment_goods;
				handler.sendMessage(msg);
			}		
		//	Log.d("test", "-------�ɹ�------->");
			break;
		case 2:// ����ʧ��
			shipmentTime.cancel();//
			shipment((byte) 1,shipment_goods,false);//�����������
			shipment_flag=true;
			list.get(shipment_goods-1).setGoodFault(0);
			{
				Message msg = new Message();
				msg.what= 0x123;
				msg.obj = shipment_goods;
				handler.sendMessage(msg);
			}
		//	Log.d("test", "-------ʧ��------->");
			break;
		default:
			break;
		}
	}
}
