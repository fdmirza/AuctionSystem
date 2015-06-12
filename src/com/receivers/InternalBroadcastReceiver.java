package com.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InternalBroadcastReceiver extends BroadcastReceiver {

	IBroadcastReceiver iBroadcastReceiver;

	public InternalBroadcastReceiver(IBroadcastReceiver aReceiver) {
		iBroadcastReceiver = aReceiver;

	}

	@Override
	public void onReceive(Context context, Intent intent) {

		iBroadcastReceiver.onReceive(context, intent);
	}

}
