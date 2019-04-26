package com.example.a11322.lifetrace;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.overlayutil.*;

import com.baidu.trace.*;
import com.example.a11322.lifetrace.track.TrackApplication;
//import com.baidu.track.R;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener,OnGetPoiSearchResultListener,OnGetSuggestionResultListener,OnGetRoutePlanResultListener {
    User user = new User();
    private MapView mMapView =null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    private BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;

    /* 搜索关键字输入窗口 */
    private EditText editCity = null;
    private Button search;
    private Button searchNearby;
    private Button searcbound;
    private Button rounte;
    private PlanNode stNode;
    private PlanNode enNode;
    private String start;
    private String terminal;
    private String city;
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;
    private RoutePlanSearch mSearch = null;
    private LatLng center ;
    private int radius = 100;
    private LatLng southwest;
    private LatLng northeast;
    private LatLngBounds searchBound;
    public Context context = null;
    private int searchType = 0;//搜索的类型，在显示时区分
    // UI相关
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        SDKInitializer.initialize(context);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = LocationMode.NORMAL;
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker, accuracyCircleFillColor, accuracyCircleStrokeColor));
        Button popupbutton = (Button) findViewById(R.id.popupbutton);
        popupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });
        ;
        mBaiduMap.setMyLocationEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);

//通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

//设置locationClientOption
        mLocationClient.setLocOption(option);

//开启地图定位图层
        mLocationClient.start();


        //Poi  初始化搜索模块
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        //创建路线规划检索
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);


        editCity = (EditText) findViewById(R.id.city);
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);

        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonProcess(v);
            }
        });
        searchNearby = (Button) findViewById(R.id.searchNearby);
        searchNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNearbyProcess(v);
            }
        });
        searcbound = (Button) findViewById(R.id.searchBound);
        searcbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBoundProcess(v);
            }
        });
        rounte = (Button) findViewById(R.id.routebutton);
        rounte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(intent,2);
            }
        });

        //Sug检索
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        //当关键字变化是动态添加建议列表
        keyWorldsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city(editCity.getText().toString()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Login_item:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.User_Center:
                Intent intent2 = new Intent(MainActivity.this,UserCenterActivity.class);
                startActivity(intent2);
                break;
            case R.id.Use_Trace:
                Intent intent3 = new Intent(MainActivity.this,com.example.a11322.lifetrace.track.activity.MainActivity.class);
                startActivity(intent3);
                break;
                default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    user.setName(data.getStringExtra("UserName"));
                    user.setEmail(data.getStringExtra("UserEmail"));
                    user.setPhonenumber(data.getStringExtra("UserPhone"));
                }
                break;
            case 2:if (resultCode == RESULT_OK) {
                start = data.getStringExtra("start");
                terminal = data.getStringExtra("terminal");
                city = data.getStringExtra("city");
                Log.d("MainActivity",data.getStringExtra("way")+data.getStringExtra("strategy")+city+start+terminal);
                switch (data.getStringExtra("way")) {
                    case "walk":
                        mBaiduMap.clear();
                        stNode = PlanNode.withCityNameAndPlaceName(city, start);
                        enNode = PlanNode.withCityNameAndPlaceName(city,terminal);
                        mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
                        break;
                    case "bike":
                        mBaiduMap.clear();
                        stNode = PlanNode.withCityNameAndPlaceName(city, start);
                        enNode = PlanNode.withCityNameAndPlaceName(city, terminal);
                        mSearch.bikingSearch((new BikingRoutePlanOption()).from(stNode).to(enNode).ridingType(0));
                        break;
                    case "transit":
                        mBaiduMap.clear();
                        stNode = PlanNode.withCityNameAndPlaceName(city, start);
                        enNode = PlanNode.withCityNameAndPlaceName(city, terminal);
                        TransitRoutePlanOption transitRoutePlanOption = new TransitRoutePlanOption();
                        switch (data.getStringExtra("strategy")){
                            case "NoSubway":
                                transitRoutePlanOption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_NO_SUBWAY);
                                break;
                            case "TimeFirst2":
                                transitRoutePlanOption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_TIME_FIRST);
                                break;
                            case "TransferFirst":
                                transitRoutePlanOption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST);
                                break;
                            case "WalkFirst":
                                transitRoutePlanOption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_WALK_FIRST);
                                break;
                            default:
                        }
                        mSearch.transitSearch(transitRoutePlanOption.from(stNode).to(enNode).city(data.getStringExtra("city")));
                        break;
                    case "drive":
                        mBaiduMap.clear();
                        stNode = PlanNode.withCityNameAndPlaceName(city, start);
                        enNode = PlanNode.withCityNameAndPlaceName(city, terminal);
                        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
                        switch (data.getStringExtra("strategy")){
                            case "AvoidCongestion":
                                drivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_AVOID_JAM);
                                break;
                            case "TimeFirst":
                                drivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
                                break;
                            case "ShortestDistance":
                                drivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST);
                                break;
                            case "FewerExpense":
                                drivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_FEE_FIRST);
                                break;
                             default:
                        }
                        mSearch.drivingSearch(drivingRoutePlanOption.from(stNode).to(enNode));
                        break;
                    default:
                }
            }
        }
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        //退出时销毁定位
        mLocationClient.stop();
        //关闭定位层
        mBaiduMap.setMyLocationEnabled(false);
        mSuggestionSearch.destroy();
        mMapView.onDestroy();
        mMapView = null;
        mPoiSearch.destroy();
        mSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Log.d("MainActivity", String.valueOf(location.getLongitude()) + "---" +location.getLocType());
            //Log.d("MainActivity",location.getProvince()+location.getCity()+location.getDistrict()+location.getLocType());
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            center = new LatLng(mCurrentLon,mCurrentLat);
            southwest = new LatLng( mCurrentLon+0.024896, mCurrentLat );
            northeast = new LatLng( mCurrentLon+0.024896, mCurrentLat+0.034639);
            searchBound = new LatLngBounds.Builder().include(southwest).include(northeast).build();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    //弹出窗体
    private void showPopWindow() {
        RadioGroup.OnCheckedChangeListener radioButtonListener;
        RadioGroup.OnCheckedChangeListener radioButtonListener2;
        RadioGroup.OnCheckedChangeListener radioButtonListener3;
        //实例化对象
        PopupWindow popupWindow = new PopupWindow(MainActivity.this);
        //设置属性
        View view = LayoutInflater.from(this).inflate(R.layout.popup, null);
        popupWindow.setContentView(view);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //获得当前窗体属性
        View parent = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(parent, Gravity.RIGHT, 0, 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().setAttributes(layoutParams);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });


        RadioGroup group = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioButtonListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.normalmap){
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                if (checkedId == R.id.satellitemap){
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
            }
        };
        group.setOnCheckedChangeListener(radioButtonListener);


        RadioGroup group2 = (RadioGroup) view.findViewById(R.id.Overridemap);
        radioButtonListener2 = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.trafficcondition){
                    mBaiduMap.setTrafficEnabled(true);
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                }
                if (checkedId == R.id.heatmap){
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                    mBaiduMap.setTrafficEnabled(false);
                }

            }
        };
        group2.setOnCheckedChangeListener(radioButtonListener2);



        RadioGroup group3 = (RadioGroup) view.findViewById(R.id.lacmode);
        radioButtonListener3 = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.following){
                    mCurrentMode = LocationMode.FOLLOWING;
                    mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode,true,mCurrentMarker));
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.overlook(0);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
                if (checkedId == R.id.compass){
                    mCurrentMode = LocationMode.COMPASS;
                    mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
                    MapStatus.Builder builder1 = new MapStatus.Builder();
                    builder1.overlook(0);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                }
                if (checkedId == R.id.normal){
                    mCurrentMode = LocationMode.NORMAL;
                    mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
                    MapStatus.Builder builder1 = new MapStatus.Builder();
                    builder1.overlook(0);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                }
            }
        };
        group3.setOnCheckedChangeListener(radioButtonListener3);
    }


    /**
     * 响应城市内搜索按钮点击事件
     *
     * @param v    检索Button
     */
    public void searchButtonProcess(View v) {
        searchType = 1;
        String citystr = editCity.getText().toString();
        String keystr = keyWorldsView.getText().toString();
        Log.d("MainAcitivity",citystr+":"+keystr);
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(citystr)
                .keyword(keystr)
                .pageNum(loadIndex)
                .scope(1));
    }

    /**
     * 响应周边搜索按钮点击事件
     *
     * @param v    检索Button
     */
    public void  searchNearbyProcess(View v) {
        mBaiduMap.clear();
        searchType = 2;
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(keyWorldsView.getText().toString())
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(center)
                .radius(radius)
                .pageNum(loadIndex)
                .scope(1);

        mPoiSearch.searchNearby(nearbySearchOption);
    }


    /**
     * 响应区域搜索按钮点击事件
     *
     * @param v    检索Button
     */
    public void searchBoundProcess(View v) {
        mBaiduMap.clear();
        searchType = 3;
        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(searchBound)
                .keyword(keyWorldsView.getText().toString())
                .scope(1));

    }


    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param result    Poi检索结果，包括城市检索，周边检索，区域检索
     */
    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            MyPoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();

            switch( searchType ) {
                case 2:
                    showNearbyArea(center, radius);
                    break;
                case 3:
                    showBound(searchBound);
                    break;
                default:
                    break;
            }

            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";

            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }

            strInfo += "找到结果";
            Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     * V5.2.0版本之后，还方法废弃，使用{@link #onGetPoiDetailResult(PoiDetailSearchResult)}代替
     * @param result    POI详情检索结果
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this,
                    result.getName() + ": " + result.getAddress(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        if (poiDetailSearchResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            List<PoiDetailInfo> poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (null == poiDetailInfoList || poiDetailInfoList.isEmpty()) {
                Toast.makeText(MainActivity.this, "抱歉，检索结果为空", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < poiDetailInfoList.size(); i++) {
                PoiDetailInfo poiDetailInfo = poiDetailInfoList.get(i);
                if (null != poiDetailInfo) {
                    Toast.makeText(MainActivity.this,
                            poiDetailInfo.getName() + ": " + poiDetailInfo.getAddress(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
     *
     * @param res    Sug检索结果
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }

        List<String> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }

        sugAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line,
                suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    private class MyPoiOverlay extends PoiOverlay {
        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            // }
            return true;
        }
    }

    /**
     * 对周边检索的范围进行绘制
     *
     * @param center    周边检索中心点坐标
     * @param radius    周边检索半径，单位米
     */
    public void showNearbyArea( LatLng center, int radius) {
        BitmapDescriptor centerBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        MarkerOptions ooMarker = new MarkerOptions().position(center).icon(centerBitmap);
        mBaiduMap.addOverlay(ooMarker);

        OverlayOptions ooCircle = new CircleOptions().fillColor( 0xCCCCCC00 )
                .center(center)
                .stroke(new Stroke(5, 0xFFFF00FF ))
                .radius(radius);

        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 对区域检索的范围进行绘制
     *
     * @param bounds     区域检索指定区域
     */
    public void showBound( LatLngBounds bounds) {
        BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                .image(bdGround)
                .transparency(0.8f)
                .zIndex(1);

        mBaiduMap.addOverlay(ooGround);

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);

        bdGround.recycle();
    }
    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        List<WalkingRouteLine> walkingRouteLines = walkingRouteResult.getRouteLines();
        if (walkingRouteResult ==null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR){
            Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            Toast.makeText(MainActivity.this,"抱歉，输入地址有歧义",Toast.LENGTH_SHORT).show();
            return;
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR){
        //创建WalkingRouteOverlay实例
        WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
        if (walkingRouteLines.size() > 0) {
            //获取路径规划数据,(以返回的第一条数据为例)
            //为WalkingRouteOverlay实例设置路径数据
            Log.d("MainActivity",walkingRouteResult.getRouteLines().size()+"");
            for (int i=0;i<walkingRouteResult.getRouteLines().size();i++){
                overlay.setData(walkingRouteResult.getRouteLines().get(i));
                //在地图上绘制DrivingRouteOverlay
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
        for (WalkingRouteLine walkingRouteLine : walkingRouteLines){
          showPopRouteInfo("步行",start,terminal,walkingRouteLine.getDuration(),walkingRouteLine.getDistance());
        }
        }
    }
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        List<BikingRouteLine> bikingRouteLines = bikingRouteResult.getRouteLines();
        if (bikingRouteResult ==null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR){
            Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            Toast.makeText(MainActivity.this,"抱歉，输入地址有歧义。",Toast.LENGTH_SHORT).show();
            return;
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR){
        //创建BikingRouteOverlay实例
        BikingRouteOverlay overlay = new BikingRouteOverlay(mBaiduMap);
        if (bikingRouteLines.size() > 0) {
            //获取路径规划数据,(以返回的第一条路线为例）
            //为BikingRouteOverlay实例设置数据
            for (int i=0;i<bikingRouteResult.getRouteLines().size();i++){
                overlay.setData(bikingRouteResult.getRouteLines().get(i));
                //在地图上绘制DrivingRouteOverlay
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
            for (BikingRouteLine bikingRouteLine : bikingRouteLines){
                showPopRouteInfo("骑行",start,terminal,bikingRouteLine.getDuration(),bikingRouteLine.getDistance());
            }
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        List<DrivingRouteLine> drivingRouteLines = drivingRouteResult.getRouteLines();
        if (drivingRouteResult ==null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR){
            Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            Toast.makeText(MainActivity.this,"抱歉",Toast.LENGTH_SHORT).show();
            return;
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR){
        //创建DrivingRouteOverlay实例
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
        if (drivingRouteLines.size() > 0) {
            //获取路径规划数据,(以返回的第一条路线为例）
            //为DrivingRouteOverlay实例设置数据
            for (int i=0;i<drivingRouteResult.getRouteLines().size();i++){
                overlay.setData(drivingRouteResult.getRouteLines().get(i));
            //在地图上绘制DrivingRouteOverlay
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
            for (DrivingRouteLine drivingRouteLine : drivingRouteLines){
                showPopRouteInfo("驾车",start,terminal,drivingRouteLine.getDuration(),drivingRouteLine.getDistance());
            }
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        List<TransitRouteLine> transitRouteLines = transitRouteResult.getRouteLines();
        if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR){
            Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            Toast.makeText(MainActivity.this,"抱歉,输入地址有歧义。",Toast.LENGTH_SHORT).show();
            return;
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR){
        //创建TransitRouteOverlay实例
        TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
        //获取路径规划数据,(以返回的第一条数据为例)
        //为TransitRouteOverlay实例设置路径数据
        if (transitRouteLines.size() > 0) {
            overlay.setData(transitRouteResult.getRouteLines().get(0));
            //在地图上绘制TransitRouteOverlay
            overlay.addToMap();
            overlay.zoomToSpan();
        }
            for (TransitRouteLine transitRouteLine : transitRouteLines){
                showPopRouteInfo("公交",start,terminal,transitRouteLine.getDuration(),transitRouteLine.getDistance());
            }
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
        //创建IndoorRouteOverlay实例
        IndoorRouteOverlay overlay = new IndoorRouteOverlay(mBaiduMap);
        if (indoorRouteResult.getRouteLines() != null && indoorRouteResult.getRouteLines().size() > 0) {
            //获取室内路径规划数据（以返回的第一条路线为例）
            //为IndoorRouteOverlay实例设置数据
            overlay.setData(indoorRouteResult.getRouteLines().get(0));
            //在地图上绘制IndoorRouteOverlay
            overlay.addToMap();
        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
        //创建MassTransitRouteOverlay实例
        MassTransitRouteOverlay overlay = new MassTransitRouteOverlay(mBaiduMap);
        if (massTransitRouteResult.getRouteLines() != null && massTransitRouteResult.getRouteLines().size() > 0){
            //获取路线规划数据（以返回的第一条数据为例）
            //为MassTransitRouteOverlay设置数据
            overlay.setData(massTransitRouteResult.getRouteLines().get(0));
            //在地图上绘制Overlay
            overlay.addToMap();
        }
    }

    private void showPopRouteInfo(String way,String start,String terminal,float duration,float distance) {
        //实例化对象
        PopupWindow popupWindow = new PopupWindow(MainActivity.this);
        //设置属性
        View view = LayoutInflater.from(this).inflate(R.layout.poprouteinfo, null);
        EditText startText = (EditText) view.findViewById(R.id.start);
        EditText wayText = (EditText) view.findViewById(R.id.way) ;
        EditText terminalText = (EditText) view.findViewById(R.id.terminal);
        EditText durationText = (EditText) view.findViewById(R.id.duration);
        EditText distanceText = (EditText) view.findViewById(R.id.distance);

        popupWindow.setContentView(view);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        wayText.setText(way);
        startText.setText(start);
        terminalText.setText(terminal);
        durationText.setText(duration/3600+"h");
        distanceText.setText(distance/1000+"km");
        //获得当前窗体属性
        View parent = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().setAttributes(layoutParams);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

}
