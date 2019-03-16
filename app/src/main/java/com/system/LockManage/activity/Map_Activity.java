package com.system.LockManage.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.system.LockManage.Listener.MyOrientationListener;
import com.system.LockManage.R;

public class Map_Activity extends Activity implements View.OnClickListener {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    //简化代码            用于this.context=this;
    private Context context;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    //是否第一次定位的标志
    private boolean isFirstIn = true;
    //定位经纬度
    private double mLatitude;
    private double mLongtitude;
    //自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyLocationConfiguration.LocationMode mLocationMode;//控制模式
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);
        this.context = this;
        initView();
        //初始化定位
        initLocation();

    }

    private void initView() {
        // TODO Auto-generated method stub
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //设置打开时的显示比列  这里显示500m左右
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
    }
    private void initLocation() {
        // TODO Auto-generated method stub
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);

        mLocationClient.setLocOption(option);

        // 初始化图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.driction);
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener()
        {
            @Override
            public void onOrientationChanged(float x)
            {
                mCurrentX = x;
            }
        });
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        centerToMyLocation();
    }
    //activity销毁时百度地图也销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // 开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
        // 开启方向传感器
        myOrientationListener.start();

    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        // 停止方向传感器
        myOrientationListener.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

//    //菜单按钮的响应事件
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // TODO Auto-generated method stub
//        switch (item.getItemId()) {
//            case R.id.id_map_common:
//                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//                break;
//            case R.id.id_map_site:
//                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
//                break;
//            case R.id.id_map_traffic:
//                if(mBaiduMap.isTrafficEnabled())
//                {
//                    mBaiduMap.setTrafficEnabled(false);
//                    item.setTitle("实时交通(off)");
//                }
//                else
//                {
//                    mBaiduMap.setTrafficEnabled(true);
//                    item.setTitle("实时交通(on)");
//                }
//                break;
//            case R.id.id_map_location:
//                //定位最新自己的位置
//                centerToMyLocation();
//                break;
//
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    //进到自己的位置
    private void centerToMyLocation() {
        LatLng latLng = new LatLng(mLatitude,mLongtitude);

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    @Override
    public void onClick(View view) {

    }

    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();
            mBaiduMap.setMyLocationData(data);
            // 设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode.NORMAL, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);
            //获取最新经纬度
            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();
            //判断是否第一次定位
            if (isFirstIn)
            {
                //设置经纬度
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                //将是否第一次定位的标志 变为不是第一次定位
                isFirstIn = false;
                //显示当前定位的位置
                Toast.makeText(context, location.getAddrStr(),
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

}