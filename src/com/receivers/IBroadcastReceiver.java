package com.receivers;

import android.content.Context;
import android.content.Intent;

public interface IBroadcastReceiver {
	public void onReceive(Context context, Intent intent);

}
