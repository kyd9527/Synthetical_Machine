package com.kyd.springmachine.widget;


import java.util.ArrayList;
import java.util.List;

import com.kyd.library.util.AbDateUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.springmachine.EfficientActivity;
import com.kyd.springmachine.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyDialog {
	public MyDialog(){
		
	}
	/**
	 * 描述:单选按钮提示框,最多3个
	 * @param activity the activity
	 * @param content String数组类型   选择的内容  最多3个
	 * @param flag    已选中 选中第几个
	 * @param listener 监听器
	 */
	public void setRadioDialog(Activity activity,String[] content,int flag,final onDialogListener listener){
		final Dialog dialog=new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		RadioGroup rg_dialog;
		RadioButton rb_dialog_one;
		RadioButton rb_dialog_two;
		RadioButton rb_dialog_three;
		
		View view;
		
		view=activity.getLayoutInflater().inflate(R.layout.dialog_radio,null);
		
		rg_dialog=(RadioGroup) view.findViewById(R.id.rg_dialog);
		
		rb_dialog_one=(RadioButton) view.findViewById(R.id.rb_dialog_one);
		rb_dialog_two=(RadioButton) view.findViewById(R.id.rb_dialog_two);
		rb_dialog_three=(RadioButton) view.findViewById(R.id.rb_dialog_three);
		
		List<RadioButton> list=new ArrayList<RadioButton>();
		list.add(rb_dialog_one);
		list.add(rb_dialog_two);
		list.add(rb_dialog_three);
		
		for (int i = 0; i < content.length; i++) {
			RadioButton rb=list.get(i);
			rb.setVisibility(View.VISIBLE);
			rb.setText(content[i]);
			if (flag==i) {
				list.get(i).setChecked(true);
			}
		}
		
		rg_dialog.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.rb_dialog_one){
					if(listener!=null)
					listener.confirm(0);
				}else if (checkedId==R.id.rb_dialog_two) {
					if(listener!=null)
					listener.confirm(1);
				}else if (checkedId==R.id.rb_dialog_three) {
					if(listener!=null)
					listener.confirm(2);
				}
				dialog.dismiss();
			}
		});
		
		dialog.setContentView(view, new LayoutParams(400, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	/**
	 * 描述:确定和返回按钮的dialog
	 * @param activity the activity
	 * @param title    dialog标题
	 * @param content  dialog内容
	 * @param flag     1是返回按钮,2是确定+返回按钮
	 * @param listener 监听器
	 */
	public void setPromptDialog(Activity activity,String title,String content,int flag,final onDialogListener listener){
		final AlertDialog.Builder builder = new Builder(activity);
		if(!AbStrUtil.isEmpty(title)){
			builder.setTitle(title);
		}
		if (!AbStrUtil.isEmpty(content)) {
			builder.setMessage(content);
		}
		if (flag==2) {
			builder.setNegativeButton("返回", null);
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (listener!=null) {
						System.out.println("onclick");
						listener.confirm(1);
					}
				}
			});
		}
		if (flag==1) {
			builder.setNegativeButton("返回", null);
		}
		builder.show();
	}
	
	/**
	 * 描述:时间设定的dialog
	 * @param activity the activity
	 * @param title    dialog标题
	 * @param note     dialog注释,null是不显示
	 * @param listener 监听器
	 */
	public void setTimeDialog(final Activity activity,String title,String note,final onDialogMsgListener listener){
		final Dialog dialog = new Dialog(activity);
		dialog.setTitle(title);
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = activity.getLayoutInflater().inflate(R.layout.dialog_business_time, null);
		dialog.addContentView(view, new LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		final EditText et_start_time_hour = (EditText) view.findViewById(R.id.et_start_time_hour);
		final EditText et_start_time_second = (EditText) view.findViewById(R.id.et_start_time_second);
		final EditText et_end_time_hour = (EditText) view.findViewById(R.id.et_end_time_hour);
		final EditText et_end_time_second = (EditText) view.findViewById(R.id.et_end_time_second);
		TextView tv_note= (TextView) view.findViewById(R.id.tv_note);
		
		if (note==null) {
			tv_note.setVisibility(View.GONE);
		}else {
			tv_note.setText(note);
		}
		
		Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String start_time_hour = et_start_time_hour.getText().toString();
				String string_time_second = et_start_time_second.getText().toString();
				String end_time_hour = et_end_time_hour.getText().toString();
				String end_time_second = et_end_time_second.getText().toString();

				if (AbDateUtil.isDataHour(start_time_hour)&& AbDateUtil.isDataSecond(string_time_second)&& AbDateUtil.isDataHour(end_time_hour)&& AbDateUtil.isDataSecond(end_time_second)) {
					if(listener!=null){
						listener.Msg(String.format("%02d", Integer.parseInt(start_time_hour)) + ":"+ String.format("%02d", Integer.parseInt(string_time_second)), String.format("%02d",Integer.parseInt(end_time_hour) ) + ":"+ String.format("%02d", Integer.parseInt(end_time_second)));
						dialog.dismiss();
					}
				} else {
					AbToastUtil.showToast(activity.getApplicationContext(), "请输入正确的时间");
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	/**
	 * 描述:有输入框的dialog,只要两个
	 * @param activity the activity
	 * @param title    dialog标题
	 * @param content  String数组提示,只能2个
	 * @param flag     1为只有第一行输入框,2是第一行没有输入框
	 * @param listener 监听器
	 */
	public void setInputDialog(Activity activity,String title,String[] content,int flag,final onDialogMsgListener listener){
		final Dialog dialog = new Dialog(activity);
		dialog.setTitle(title);
		
		View view = activity.getLayoutInflater().inflate(R.layout.dialog_wifi_login, null);
		dialog.addContentView(view, new LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

		LinearLayout ll_dialog_user=(LinearLayout) view.findViewById(R.id.ll_dialog_user);
		LinearLayout ll_dialog_password=(LinearLayout) view.findViewById(R.id.ll_dialog_password);
		
		
		TextView tv_dialog_user = (TextView) view.findViewById(R.id.tv_dialog_user);
		TextView tv_dialog_password = (TextView) view.findViewById(R.id.tv_dialog_password);
		
		final EditText et_dialog_user=(EditText) view.findViewById(R.id.et_dialog_user);
		final EditText et_dialog_password=(EditText) view.findViewById(R.id.et_dialog_password);
		
		TextView dialog_confirm = (TextView) view.findViewById(R.id.dialog_confirm);
		TextView dialog_cancel = (TextView) view.findViewById(R.id.dialog_cancel);
		
		if (flag==1) {
			ll_dialog_password.setVisibility(View.GONE);
			tv_dialog_user.setText(content[0]);
		}else if (flag==2) {
			et_dialog_user.setVisibility(View.GONE);
			tv_dialog_user.setText(content[0]);
			tv_dialog_password.setText(content[1]);
		}
		
		dialog_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (listener!=null) {
					listener.Msg(et_dialog_user.getText().toString(), et_dialog_password.getText().toString());
				}
				dialog.dismiss();
			}
		});
		
		dialog_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public interface onDialogListener{
		/**
		 * 描述:在setRadioDialog和setPromptDialog使用
		 * @param number 在setRadioDialog中点击radio的第几个,在setPromptDialog中1是确定
		 */
		void confirm(int number);
	}
	public interface onDialogMsgListener{
		/**
		 * 描述:在时间dialog和输入dialog使用
		 * @param start 时间dialog的开始时间,输入dialog的第一行的输入信息
		 * @param end   时间dialog的结束时间,输入dialog的第二行的输入信息
		 */
		void Msg(String start,String end);
	}
	
}
