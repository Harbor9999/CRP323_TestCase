package com.hz.weather.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hz.weather.R;
import com.hz.weather.service.AutoUpdateService;
import com.hz.weather.util.HttpCallbackListener;
import com.hz.weather.util.HttpUtil;
import com.hz.weather.util.XMLDataUtil;

public class WeatherActivity extends Activity implements OnClickListener{

    private static final String TAG = "WeatherActivity";
	private Button bt_change_city;
	private TextView tv_city_name;
	private Button bt_refresh_weather;
	private TextView tv_display_weather;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        
        bt_change_city = (Button) findViewById(R.id.change_city);
        bt_change_city.setOnClickListener(this);
        tv_city_name = (TextView) findViewById(R.id.tv_city_name);
        bt_refresh_weather = (Button) findViewById(R.id.refresh_weather);
        bt_refresh_weather.setOnClickListener(this);
        tv_display_weather = (TextView) findViewById(R.id.tv_display_weather);
        String selectCityCode = getIntent().getStringExtra("selectCityCode");
        Log.i(TAG, "selectCityCode" + selectCityCode);
        if (!TextUtils.isEmpty(selectCityCode)) {
        	tv_display_weather.setText("同步中......");
			//从服务器查询天气
        	queryWeather(selectCityCode);
		} else {
			//显示本地的天气信息
			showWeather();
		}
    }

	private void queryWeather(String selectCityCode) {
		String userID = "";
		// 	多了一个【/】/WebServices/WeatherWS.asmx/getWeather?theCityCode=string&theUserID=string
		String get = "/WebServices/WeatherWS.asmx/getWeather?theCityCode=" + selectCityCode +"&theUserID=" + userID;
		String host = "ws.webxml.com.cn";
		
		String address = "http://"+ host + get;
//		String address = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/WebServices/WeatherWS.asmx/getWeather?theCityCode="
//				+ selectCityCode + "&theUserID=" + userID ; 自己组合的地址容易出错 重复
		Log.i(TAG, address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// 解析天气，保存到本地SharedPreferences
				XMLDataUtil.handWeather(WeatherActivity.this, response);
				//显示天气
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showWeather();
					}
				});
			}
			@Override
			public void onError(Exception e) {
				Log.i(TAG, "onError: " + e.getMessage());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_display_weather.setText("同步失败");
					}
				});
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_city:
			//回到主UI选择省
			Intent intent  = new Intent(WeatherActivity.this, MainActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			Log.i(TAG, "refresh_weather....." );
			tv_display_weather.setText("更新......");
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			String cityCode = preferences.getString("cityId", "");
			queryWeather(cityCode);
			break;

		default:
			break;
		}
		
	}

	private void showWeather() {
		Log.i(TAG, "showWeather....." );
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		tv_city_name.setText(preferences.getString("countyName", ""));
		tv_display_weather.setText(preferences.getString("cityName", "")
				+ " " + preferences.getString("countyName", "")
				+ "\n" + preferences.getString("time", "")
				+ "\n" + preferences.getString("todayTemperature", "")
				+ "\n" + preferences.getString("UV_radiation_intensity", "")
				+ "\n" + preferences.getString("dressIndex", ""));
		
		//更新服务,即更新天气显示
		Intent i = new Intent(this, AutoUpdateService.class);
		startService(i);
	}

    
}
