package com.hz.weather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hz.weather.bean.City;
import com.hz.weather.bean.Province;

public class WeatherDBService {

	private static final String DB_NAME = "weather_db";
	private static final int DB_VER = 1;
	private SQLiteDatabase db;
	public static WeatherDBService weatherDBService;

	private  WeatherDBService(Context context) {
		WeatherOpenHelper weatherOpenHelper = new WeatherOpenHelper(context, DB_NAME, null, DB_VER);
		db = weatherOpenHelper.getWritableDatabase();
	}
	/**
	 * 单例模式 实例化
	 * @param context
	 * @return
	 */
	public synchronized static WeatherDBService getInstance(Context context){
		if (weatherDBService == null) {
			weatherDBService = new WeatherDBService(context);
		}
		return weatherDBService;
	}
	
	/**
	 * 保存省 --数据库
	 */
	public void saveProvince(Province province){
		if (province != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Province", null, contentValues);
		}
	}
	/**
	 * 从数据库读取所有省名称
	 */
	public List<Province> loadProvince(){
		 List<Province> list = new ArrayList<Province>();
		 Cursor cursor = db.query("Province", null, null, null, null, null, null);
		 if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
		
	}
	
	/**
	 * 保存市到数据库
	 */
	public void saveCity(City city){
		if (city != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId()); //问题这里未保存province_id
			db.insert("City", null, contentValues);
		}
	}
	/**
	 * 从数据加载所有市名称 
	 */
	public List<City> loadCity(int province_id){
		Log.i("hz", "province_id:" + province_id);
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(province_id)}, null, null, null);
		Log.i("hz", "cursor.moveToFirst()" + cursor.getCount());
		if (cursor.moveToFirst()) {
			
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				Log.i("hz", "city:" + city.toString());
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
}
