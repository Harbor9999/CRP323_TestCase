# 天气APP #

包名：com.hz.weather


- ui	 界面相关代码
 
- db		  数据库相关代码
 
- bean		  实体类 省级，市级，县级	
 
- receive	 所有广播接受相关代码

- service	 所有服务相关代码
 
- util		  工具相关代码


**Step 1**：
从服务器获取的数据存储到本地数据库，创建数据库和表。

*创建*三张表：

**Province**

----------

create tabel Province
( id integer primary key antoincrement,
province_name text,
 province_code text)
**id 主键
province_name 省名
province_code 省级代号**

**City**

----------
create table City
(id integer primary key autoincrement,
city_name text,
city_code text,
province_id integer)

**id自增长主键
city_name 城市名
city_code 市级代号
province_id 关联Province表的外键**

**County**

----------

create table County(
id integer primary key antoincrement,
county_name text,
county_code text,
city_id integer)

**id主键
county_name 县名
county_code 县级代号
city_id 关联City表外键**

*创建*表类继承 SQLiteOpenHelper

*创建*每张表写个对应的实体类

*创建*DB类封装 对数据库操作的方法
保存省、市、县 三个方法
获取全国的省信息
获取某个省下的市的信息
获取某个市下的县的信息


**Step 2** :
从服务器获取的数据，HttpUtil类  

返回的数据 使用一个工具类解析 XMLDataUtil 存储到数据库


**Step 3:**

UI界面:
MainActivity 选择省 城市界面
WeatherActivity 天气显示界面 

Endpoint： http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx

使用一下三个web接口 
getRegionProvince
getSupportCityString
getWeather
