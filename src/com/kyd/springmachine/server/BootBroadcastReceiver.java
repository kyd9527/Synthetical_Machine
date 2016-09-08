package com.kyd.springmachine.server;

import com.kyd.springmachine.WelcomeActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String action_boot = "android.intent.action.BOOT_COMPLETED";
	private String action;

	@Override
	public void onReceive(Context context, Intent intent) {

		// TODO Auto-generated method stub
		action = intent.getAction();

		if (action.equals(action_boot)) {
			Intent ootStartIntent = new Intent(context, WelcomeActivity.class);
			ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(ootStartIntent);
		} 
	}

}
