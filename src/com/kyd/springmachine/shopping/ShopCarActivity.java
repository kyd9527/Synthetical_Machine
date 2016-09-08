package com.kyd.springmachine.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kyd.library.AbActivity;
import com.kyd.springmachine.R;


/**
 *暂时没用到
 *
 * @author 8015
 *
 */
public class ShopCarActivity extends AbActivity implements OnClickListener {

	private TextView tv_main_balance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_car);
		initViews();
		initData();
		setListeners();
	}

	@Override
	public void initViews() {
		initValues();
		tv_main_balance=(TextView) findViewById(R.id.tv_main_balance);
	}

	private void initValues(){
        // 不能写在init()中  
        Window window = getWindow();  
        WindowManager.LayoutParams lp = window.getAttributes();  
        DisplayMetrics dm = this.getResources().getDisplayMetrics();  
        lp.width = android.widget.LinearLayout.LayoutParams.MATCH_PARENT;//让dialog的宽占满屏幕的宽 
        lp.gravity = Gravity.BOTTOM;//出现在底部
        window.setAttributes(lp);
  
    }  
	
	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		tv_main_balance.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_main_balance:
		{
			Intent intent=new Intent(this, OrderActivity.class);
			startActivity(intent);
			finish();
		}
			break;

		default:
			break;
		}
	}
}
