package com.kyd.springmachine;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.library.hardware.Response;
import com.kyd.library.hardware.Response.Listener;
import com.kyd.library.hardware.Response.ResponseFunctionBit;
import com.kyd.library.util.AbAdapter;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.AbViewHolder;
import com.kyd.springmachine.server.ReadSerialPortServer;

/**
 * π ’œ–≈œ¢
 * 
 * @author 8015
 * 
 */
public class FaultInfoActivity extends AbActivity implements OnClickListener,Listener{

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;

	private ListView lv;
	private AbAdapter<String> adapter;
	private List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fault_info);
		initViews();
		initData();
		setListeners();
		ControlApp.uartHand1.addListener(this);
	}

	@Override
	public void initViews() {
		// title
		bt_title_left = (Button) findViewById(R.id.bt_title_left);
		tv_title_name = (TextView) findViewById(R.id.tv_title_name);
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		// ΩÁ√Ê
		lv = (ListView) findViewById(R.id.lv);

		tv_title_name.setText("π ’œ–≈œ¢");
		tv_title_right.setText("π ’œ»´«Â");
	}

	@Override
	public void initData() {
		list = new ArrayList<String>();	
		String err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_COIN_NOT_CONNECT");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_COIN_PAPER_ERROR");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_LACK_COIN");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_COIN_PIPING_NOT_EXIST");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_COIN_POLL_ERROR");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_PAPER_NOT_CONNECT");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_PAPER_PIPING_NOT_EXIST");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_PAPER_POLL_ERROR");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_TEMP_WARING");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_TEMP_NOT_EXIST");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}
		err = AbSharedUtil.getString(FaultInfoActivity.this, "RES_UART_NOT_CONNECT");
		if(err!=null&&!err.equals("")){
			list.add(err);
		}

		adapter = new AbAdapter<String>(this, list,
				android.R.layout.simple_list_item_1) {

			@Override
			public void convert(AbViewHolder viewHolder, String item,
					int position) {
				TextView text1 = viewHolder.getView(android.R.id.text1);
				text1.setTextColor(Color.RED);
				text1.setText(item);
			}
		};
		lv.setAdapter(adapter);
	}

	@Override
	public void setListeners() {
		bt_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_title_left:
			finish();
			break;
		case R.id.tv_title_right://π ’œ»´«Â
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_NOT_CONNECT", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_PAPER_ERROR", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_LACK_COIN", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_PIPING_NOT_EXIST", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_POLL_ERROR", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_PAPER_NOT_CONNECT", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_PAPER_PIPING_NOT_EXIST", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_PAPER_POLL_ERROR", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_TEMP_WARING", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_TEMP_NOT_EXIST", "");
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_UART_NOT_CONNECT", "");//
			AbToastUtil.showToast(this, "π ’œ»´«Â");
			list.clear();
			adapter.notifyDataSetChanged();
			ReadSerialPortServer.netWork.sendFaultClean();
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
	
	@Override
	public void onResponse(Response res) {
		// TODO Auto-generated method stub
		String msg = null;
		switch (res.getFuncBit()) {
		case ResponseFunctionBit.RES_COIN_NOT_CONNECT:	
			msg = "”≤±“∆˜Œﬁ¡¨Ω”";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_NOT_CONNECT", msg);
			break;		
		case ResponseFunctionBit.RES_COIN_PAPER_ERROR:
			msg = "÷Ω±“∆˜”≤±“∆˜¥ÌŒÛ";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_PAPER_ERROR", msg);
			break;
		case ResponseFunctionBit.RES_LACK_COIN:
			msg = "”≤±“ ˝¡ø≤ª◊„";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_LACK_COIN", msg);
			break;
		case ResponseFunctionBit.RES_COIN_PIPING_NOT_EXIST:
			msg = "”≤±“∆˜«Æπ‹≤ª¥Ê‘⁄";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_PIPING_NOT_EXIST", msg);
			break;
		case ResponseFunctionBit.RES_COIN_POLL_ERROR:
			msg = "”≤±“∆˜¬÷—Ø¥ÌŒÛ";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_COIN_POLL_ERROR", msg);
			break;
		case ResponseFunctionBit.RES_PAPER_NOT_CONNECT:
			msg = "÷Ω±“∆˜Œﬁ¡¨Ω”";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_PAPER_NOT_CONNECT", msg);
			break;
		case ResponseFunctionBit.RES_PAPER_PIPING_NOT_EXIST:
			msg = "÷Ω±“∆˜«Æπ‹±ª»°◊ﬂ";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_PAPER_PIPING_NOT_EXIST", msg);
			break;
		case ResponseFunctionBit.RES_PAPER_POLL_ERROR:
			msg = "÷Ω±“∆˜¬÷—Ø¥ÌŒÛ";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_PAPER_POLL_ERROR", msg);
			break;	
		case ResponseFunctionBit.RES_TEMP_NOT_EXIST:
			msg = "Œ¬∂»¥´∏–∆˜≤ª¥Ê‘⁄";
			AbSharedUtil.putString(FaultInfoActivity.this, "RES_TEMP_NOT_EXIST", msg);
			//Log.d(TAG, "-------1------->");
			break;
		default:
			break;
		}	
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ControlApp.uartHand1.removeListener(this);
	}
	
}
