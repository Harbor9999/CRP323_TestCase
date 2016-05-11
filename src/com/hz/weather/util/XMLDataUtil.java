package com.hz.weather.util;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.hz.weather.bean.City;
import com.hz.weather.bean.Province;
import com.hz.weather.db.WeatherDBService;

public class XMLDataUtil {
	


	public synchronized static boolean handProvince(WeatherDBService weatherDBService,String responseString) {
		//<string>黑龙江,3113</string>
		if (!TextUtils.isEmpty(responseString)) {
			try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(new StringReader(responseString));

				for (int eventType = parser.getEventType(); eventType != parser.END_DOCUMENT; eventType = parser
						.next()) {
					if ("string".equals(parser.getName())) {
						String[] array = parser.nextText().split(",");
						Province province = new Province();
						province.setProvinceName(array[0]);
						province.setProvinceCode(array[1]);
						weatherDBService.saveProvince(province); //把返回数据省保存数据库
					}
				}
				return true;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return false;
	}

	public static boolean handCity(WeatherDBService weatherDBService, String responseString, int provinceId) {
		if (!TextUtils.isEmpty(responseString)) {
			try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(new StringReader(responseString));

				for (int eventType = parser.getEventType(); eventType != parser.END_DOCUMENT; eventType = parser
						.next()) {
					if ("string".equals(parser.getName())) {
						String[] array = parser.nextText().split(",");
						City city = new City();
						city.setCityName(array[0]);
						city.setCityCode(array[1]);
						city.setProvinceId(provinceId);
						weatherDBService.saveCity(city);//把返回数据市保存数据库
						Log.i("hz", "保存市到数据库");
					}
				}
				
				return true;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return false;
	}

	

	public static void handWeather(Context context,
			String response) {
		Log.i("hz", "handWeather:  "  + response);
		ArrayList<String> weatherInfo = new ArrayList<String>();
		if (!TextUtils.isEmpty(response)) {
			try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(new StringReader(response));
				for (int eventType = parser.getEventType(); eventType != parser.END_DOCUMENT; eventType = parser.next()) {
					if ("string".equals(parser.getName())) {
						// parser.nextText()
						weatherInfo.add(parser.nextText());
					}
				}
				Log.i("hz", "handWeather: weatherInfo.size()  "  + weatherInfo.size());
				saveWeatherInfo(context,weatherInfo);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

	private static void saveWeatherInfo(Context context,
			ArrayList<String> weatherInfo) {
		String cityName = weatherInfo.get(0);
		String countyName = weatherInfo.get(1);
		String cityId = weatherInfo.get(2);
		String time = weatherInfo.get(3);
		String todayTemperature = weatherInfo.get(4);
		String UV_radiation_intensity = weatherInfo.get(5);
		String dressIndex= weatherInfo.get(6);
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("cityName", cityName);
		editor.putString("countyName", countyName);
		editor.putString("cityId", cityId);
		editor.putString("time", time);
		editor.putString("todayTemperature", todayTemperature);
		editor.putString("UV_radiation_intensity", UV_radiation_intensity);
		editor.putString("dressIndex", dressIndex);
		editor.commit();
		
	}

}
