package com.hz.weather.receive;

import com.hz.weather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

public class AutoUpdateReceive extends BroadcastReceiver {

	private static final String TAG = "AutoUpdateReceive";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive");
		Intent intentReceive = new Intent(context, AutoUpdateService.class);
		context.startService(intentReceive);
	}

}
