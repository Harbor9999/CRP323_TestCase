package com.hz.weather.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherOpenHelper extends SQLiteOpenHelper {
	/**
	 * 省<(￣3￣)> 表 Province
	 */
	private static final String CREATE_PROVINCE = "create table Province ("
		    + "id integer primary key autoincrement, " 
		    + "province_name text, "
		    + "province_code text)";
	/**
	 * 市表 City
	 */
	private static final String CREATE_CITY = "create table City ("
		    + "id integer primary key autoincrement, " 
		    + "city_name text, " 
		    + "city_code text, " 
		    + "province_id integer)";

	public WeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);//省<(￣3￣)> 表!
		db.execSQL(CREATE_CITY); //市表
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
