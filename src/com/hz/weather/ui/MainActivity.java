package com.hz.weather.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hz.weather.R;
import com.hz.weather.bean.City;
import com.hz.weather.bean.Province;
import com.hz.weather.db.WeatherDBService;
import com.hz.weather.util.HttpCallbackListener;
import com.hz.weather.util.HttpUtil;
import com.hz.weather.util.XMLDataUtil;

public class MainActivity extends Activity {
	private static final int PROVINCE_LEVEL = 0;
	private static final int CITY_LEVEL = 1;
	private static final String TAG = "MainActivity";
	private boolean isFromWeatherActivity;
	private ProgressDialog progressDialog;
	private TextView title;
	private ListView listView;
	private List<String> dataList = new ArrayList<String>();
	private WeatherDBService weatherDBService;
	/**
	 * 选中级别 省级 、市级
	 */
	private int currentLevel;
	
	private List<Province> provinceList;
	private List<City> cityList;
	/**
	 * 选中的省
	 */
	private Province selectProvince;
	private City selectCity;
	private ArrayAdapter<String> adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        Log.i(TAG, "isFromWeatherActivity: " + isFromWeatherActivity);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //是否选择城市，默认城市天气显示
        if (sharedPreferences.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        title = (TextView) findViewById(R.id.tv_title_name);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, dataList);
        listView.setAdapter(adapter);
        weatherDBService = WeatherDBService.getInstance(this);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//选择省、市
				if (currentLevel == PROVINCE_LEVEL) {
					selectProvince = provinceList.get(position);
					//查询这个省的下面市
					Log.i("hz", "选择省：" + selectProvince.getProvinceName() + "开始查询queryCitys()");
					queryCitys();
				} else if(currentLevel == CITY_LEVEL){
					String selectCityCode = cityList.get(position).getCityCode();
					Log.i(TAG, "selectCityCode" + selectCityCode);
					//查询该城市的天气 ,跳转WeatherActivity显示
					Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
					intent.putExtra("selectCityCode", selectCityCode);
					startActivity(intent);
					finish();
				}
				
			}
		});
        
        //从服务器加载省级数据
        queryProvinces();
    }


	protected void queryCitys() {
		cityList = weatherDBService.loadCity(selectProvince.getId());   //???
		Log.i("hz", "选择省：" + selectProvince.getProvinceName() + "有多少市"+ cityList.size());
		if (cityList.size() > 0) {
			Log.i("hz", "queryCitys优先本地数据库显示");
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName()); //把从数据得到的城市名称加入到数据集合提供显示
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);  //默认选中为第1个城市
			title.setText(selectProvince.getProvinceName()); //设置标题显示省名
			currentLevel = CITY_LEVEL;  //设置为选中市级
		} else {
			Log.i("hz", "queryCitys 从服务器查询~");
			queryFromServer(selectProvince.getProvinceCode(),"city");
		}
	}



	private void queryProvinces() {
		provinceList = weatherDBService.loadProvince();
		//优先从数据库查询，没有到服务器加载
		if (provinceList.size() > 0) {
			//清除listView dataList显示数据
			dataList.clear();
			//得到的省级列表数据，装进dataList
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			//适配器数据改变唤醒
			adapter.notifyDataSetChanged();
			//设置listView默认选中第一个 0
			listView.setSelection(0);
			//设置标题栏名称
			title.setText("中国");
			//设置选中的级别等级
			currentLevel = PROVINCE_LEVEL;
		} else {
			//去下载数据
			queryFromServer(null,"province");
		}
	}
	/**
	 * 从服务器加载数据
	 * @param code  查询的省级；市级 code
	 * @param typeName  类型名称 省级名称 province  市级 名称 city
	 */
	private void queryFromServer(final String code, final String typeName) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getSupportCityString?theRegionCode="
					+ code;
		} else {
			address = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getRegionProvince";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(typeName)){
					result = XMLDataUtil.handProvince(weatherDBService, response);
				}else if("city".equals(typeName)){
					result = XMLDataUtil.handCity(weatherDBService, response,selectProvince.getId());
					Log.i("hz", "handCity 保存成功~" + result );
				}
				
				if (result) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//关闭加载....
							closeProgressDialog();
							if("province".equals(typeName)){
								queryProvinces();
							}else if("city".equals(typeName)){
								queryCitys();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(MainActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
						
					}
				});
				
			}
			
		});
	}



	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("加载....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
    
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}


	@Override
	public void onBackPressed() {
		if (currentLevel == CITY_LEVEL) {
			Log.i(TAG, "CITY_LEVEL");
			queryProvinces();
		} else if(currentLevel == PROVINCE_LEVEL){
			//
			Log.i(TAG, "PROVINCE_LEVEL");
			finish();
		}else{
			Log.i(TAG, "OTHER_LEVEL");
		}
	}
	
	
}
