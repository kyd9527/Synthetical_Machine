package com.kyd.springmachine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.kyd.library.AbActivity;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbDateUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.AbViewHolder;
import com.kyd.library.widget.LinearListView;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.server.ReadSerialPortServer;
import com.kyd.springmachine.server.ReadSerialPortServer.EfficientReceiver;
import com.kyd.springmachine.widget.MyDialog;
import com.kyd.springmachine.widget.MyDialog.onDialogListener;
import com.kyd.springmachine.widget.MyDialog.onDialogMsgListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import de.greenrobot.greendao.SetTimeInfo;

public class EfficientActivity extends AbActivity implements OnClickListener {
	//action
	public static final String ACTION_START = "time start";
	public static final String ACTION_END = "time end";
	public static final String ACTION_AUTO = "time auto";
	public static final String ACTION_STOP = "time stop";
	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;
	
	// ����
	private Switch sw_model;
	private RelativeLayout rl_temp_set;
	private RelativeLayout rl_time;
	private LinearListView lv;
	private TextView tv_temp_set;
	private Button bt_efficient_confirm;
	private Button bt_efficient_cancel;

	private MyDialog myDialog;

	private DataBaseManager dbManger;
	
	private AbAdapter<SetTimeInfo> adpter;
	private AlarmManager alarm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_efficient);
		alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
		sw_model = (Switch) findViewById(R.id.sw_model);
		rl_temp_set = (RelativeLayout) findViewById(R.id.rl_temp_set);
		rl_time = (RelativeLayout) findViewById(R.id.rl_time);
		tv_temp_set = (TextView) findViewById(R.id.tv_temp_set);
		lv = (LinearListView) findViewById(R.id.lv);
		bt_efficient_confirm = (Button) findViewById(R.id.bt_efficient_confirm);
		bt_efficient_cancel = (Button) findViewById(R.id.bt_efficient_cancel);

		tv_title_name.setText("��������");
		tv_title_right.setText("���");
	}

	@Override
	public void initData() {
		boolean flag = AbSharedUtil.getBoolean(this, "sw_model", true);
		tv_temp_set.setText(AbSharedUtil.getInt(this, "temp") + "��");
		sw_model.setChecked(flag);
		setView(flag);
		boolean flag1=AbSharedUtil.getBoolean(this, "efficient", false);//
		setView1(flag1);
		sw_model.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				AbSharedUtil.putBoolean(EfficientActivity.this, "sw_model",arg1);
				setView(arg1);
				
			}
		});
		myDialog = new MyDialog();
		dbManger = DataBaseManager.getInstance(this);
		getTimeInfo();
	}

	// ��ȡ���ݿ�ʱ���趨
	private void getTimeInfo() {
		List<SetTimeInfo> list = dbManger.queryAllSetTimeInfo();
		adpter = new AbAdapter<SetTimeInfo>(this, list, R.layout.item_temp_set) {

			@Override
			public void convert(AbViewHolder viewHolder, final SetTimeInfo item,
					final int position) {
				// TODO Auto-generated method stub
				RelativeLayout rl_item=viewHolder.getView(R.id.rl_item);
				TextView item_start_tiem = viewHolder.getView(R.id.item_start_tiem);
				TextView item_end_tiem = viewHolder.getView(R.id.item_end_tiem);

				item_start_tiem.setText(item.getStartTime());
				item_end_tiem.setText(item.getEndTime());
				rl_item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						myDialog.setPromptDialog(EfficientActivity.this, "ɾ��", "ɾ����ʱ��", 2, new onDialogListener() {
							
							@Override
							public void confirm(int number) {
								// TODO Auto-generated method stub
				     		dbManger.dropSetTimeInfo(item);	
				     		cancelAlarm(position+1);
				     		getTimeInfo();
							}
						});
					}
				});
			}
		};
		lv.setAdapter(adpter);
	}

	// ���ʱ��dialog
	private void dialogTime() {
		myDialog.setTimeDialog(this, "ʱ���趨", null, new onDialogMsgListener() {

			@Override
			public void Msg(String start, String end) {
				SetTimeInfo s = new SetTimeInfo();
				SimpleDateFormat sdf=new SimpleDateFormat(AbDateUtil.dateFormatHM);
				Date dateStart = null;
				Date dateEnd = null;
				long id =0;
				try {
					dateStart=sdf.parse(start);
					dateEnd=sdf.parse(end);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dateStart.compareTo(dateEnd) < 0) {
					s.setStartTime(start);
					s.setEndTime(end);				
					if ((id=dbManger.saveSetTimeInfo(s))!=0) {
						AbToastUtil.showToast(EfficientActivity.this, "��ӳɹ�");														
					}else {
						AbToastUtil.showToast(EfficientActivity.this, "���ʧ��");
					}
					getTimeInfo();
				}else {
					AbToastUtil.showToast(EfficientActivity.this, "����ʱ�䳬����ʼʱ��");
				}
			}
		});
	}

	// �¶�����dialog
	private void dialogTemp() {
		myDialog.setInputDialog(this, "�¶�����", new String[] { "�¶�����:" }, 1,
				new onDialogMsgListener() {

					@Override
					public void Msg(String start, String end) {
						if (AbStrUtil.isNatural(start)) {
							AbToastUtil.showToast(EfficientActivity.this,"�����ʽ��ȷ");
							AbSharedUtil.putInt(EfficientActivity.this, "temp", Integer.parseInt(start));
							tv_temp_set.setText(AbSharedUtil.getInt(EfficientActivity.this, "temp") + "��");
						} else {
							AbToastUtil.showToast(EfficientActivity.this,"�����ʽ����ȷ");
						}
					}
				});
	}

	// ������ʾ
	public void setView(boolean flag) {
		if (flag) {
			rl_time.setVisibility(View.GONE);
			lv.setVisibility(View.GONE);
			tv_title_right.setVisibility(View.INVISIBLE);

		} else {
			rl_time.setVisibility(View.VISIBLE);
			lv.setVisibility(View.VISIBLE);
			tv_title_right.setVisibility(View.VISIBLE);	

		}
	}
	
	//����view
	public void setView1(boolean flag){
		if (flag) {
			sw_model.setEnabled(false);
			bt_efficient_confirm.setEnabled(false);
			tv_title_right.setEnabled(false);
		}else {
			sw_model.setEnabled(true);
			bt_efficient_confirm.setEnabled(true);
			tv_title_right.setEnabled(true);
		}
	}
	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);
		rl_temp_set.setOnClickListener(this);
		bt_efficient_confirm.setOnClickListener(this);
		bt_efficient_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_temp_set://�¶�����
			dialogTemp();
			break;
		case R.id.bt_title_left:
			finish();
			break;
		case R.id.tv_title_right://���ʱ��
			dialogTime();
			break;
		case R.id.bt_efficient_confirm://��ʼ
			AbSharedUtil.putBoolean(this, "efficient", true);
			setView1(true);
			if(AbSharedUtil.getBoolean(EfficientActivity.this, "sw_model", true)){
				Intent intent = new Intent(EfficientActivity.this, EfficientReceiver.class);
				intent.setAction(ACTION_AUTO);
				sendBroadcast(intent);
			}else{
			List<SetTimeInfo> list=dbManger.queryAllSetTimeInfo();
			int i = 0,size = 0;
			size = list.size();
			for(i=0;i<size;i++){
			SetTimeInfo	 info = list.get(i);
			setAlarm(info.getStartTime(),info.getEndTime(),i+1);//��������	
			}
			}		
			break;
		case R.id.bt_efficient_cancel://����
			AbSharedUtil.putBoolean(this, "efficient", false);
			setView1(false);
			if(!AbSharedUtil.getBoolean(EfficientActivity.this, "sw_model", true)){
				int count = (int) dbManger.getSetTimeInfoCount();
				for(int i=0;i<count;i++){
					cancelAlarm(i+1);//ȡ��
				}	
			}
			Intent intent = new Intent(EfficientActivity.this, EfficientReceiver.class);		
			intent.setAction(ACTION_STOP);
			sendBroadcast(intent);
			break;
		default:
			break;
		}
	}
	
	
	private void cancelAlarm(int requestCode) {
		Intent intent = new Intent(EfficientActivity.this, EfficientReceiver.class);
		intent.setAction(ACTION_START);
		PendingIntent sender = PendingIntent.getBroadcast(EfficientActivity.this, 2 * requestCode, intent, 0);
		alarm.cancel(sender);
		intent = new Intent(EfficientActivity.this, EfficientReceiver.class);
		intent.setAction(ACTION_END);
		sender = PendingIntent.getBroadcast(EfficientActivity.this, 2 * requestCode + 1, intent, 0);
		alarm.cancel(sender);
	}
	// ���ö�ʱ

	private void setAlarm(String str_start_time, String str_end_time, int id) {

		Intent intent = new Intent(EfficientActivity.this, EfficientReceiver.class);
		intent.setAction(ACTION_START);
		PendingIntent sender = PendingIntent.getBroadcast(EfficientActivity.this, 2 * id, intent, 0);
		alarm.setRepeating(AlarmManager.RTC, AbDateUtil.getTimeMillis(str_start_time), AbDateUtil.DAY, sender);
		intent = new Intent(EfficientActivity.this, EfficientReceiver.class);
		intent.setAction(ACTION_END);
		sender = PendingIntent.getBroadcast(EfficientActivity.this, 2 * id + 1, intent, 0);
		alarm.setRepeating(AlarmManager.RTC, AbDateUtil.getTimeMillis(str_end_time), AbDateUtil.DAY, sender);
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
