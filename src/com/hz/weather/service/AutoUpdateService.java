package com.hz.weather.service;

import com.hz.weather.receive.AutoUpdateReceive;
import com.hz.weather.util.HttpCallbackListener;
import com.hz.weather.util.HttpUtil;
import com.hz.weather.util.XMLDataUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoUpdateService extends Service {

	protected static final String TAG = "AutoUpdateService";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//开启新的线性更新
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//执行更新
				Log.i(TAG, "onStartCommand + run"  );
				updateWeather();
				
			}
		}).start();
		
		//间隔多长时间更新一次的通知，广播
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); //进行精准定时
		
		Intent intentUpdate = new Intent(this, AutoUpdateReceive.class);
		
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, intentUpdate, 0);
		long triggerAtMillis = SystemClock.elapsedRealtime() + 60 * 1000; 
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, operation);
		
		return super.onStartCommand(intent, flags, startId);
	}

	protected void updateWeather() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String cityCode = preferences.getString("cityId", "");
		String address = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather?theCityCode=" + cityCode + "&theUserID=";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Log.i(TAG, "onFinish" + response );
				XMLDataUtil.handWeather(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				Log.i(TAG, "onError" + e.getMessage() );
				e.printStackTrace();
				
			}
		});
	}
	
}
