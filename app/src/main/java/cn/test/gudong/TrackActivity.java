package cn.test.gudong;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

import cn.test.gudong.map.MyOrientationListener;

public class TrackActivity extends BasicActivity {
    String tag = "TrackActivity";

    private TextView locationStatus;
    private TextView end;

    //地图模式
    private int mode = 1;//1.普通，2，跟随，3罗盘。默认是1；

    private long startTime;// start and end time
    private long endTime;

    private double sum_distance = 0.0;


    MapView mapView;
    BaiduMap baiduMap;
    //默认缩放级别
    int ZOOM_LEVEL = 20;//地图等级为3-21级
    int SPAN = 1000;//定位间隔 ms 至少一秒

    LocationClient locationClient = null;
    BDLocationListener locationListener = new MyLocationListener();
    private BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration config;

    private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode mCurrentMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());//要在setcontent之前使用并传入ApplicationContext
        setContentView(R.layout.activity_track);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Toast.makeText(this, "android版本:" + Build.VERSION.RELEASE + " 需动态请求定位权限", Toast.LENGTH_SHORT).show();
            // TODO 6.0以上要先动态申请权限
            checkMyPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 200);
        } else {
            Toast.makeText(this, "android版本:" + Build.VERSION.RELEASE, Toast.LENGTH_SHORT).show();
            begain_location();
        }


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /*
            TypeNone = 0;
    int TypeGpsLocation = 61;
    int TypeCriteriaException = 62;
    int TypeNetWorkException = 63;
    int TypeOffLineLocation = 66;
    int TypeOffLineLocationFail = 67;
    int TypeOffLineLocationNetworkFail = 68;
    int TypeNetWorkLocation = 161;
    int TypeCacheLocation = 65;
    int TypeServerError = 167;
    TypeServerDecryptError = 162;
    TypeServerCheckKeyError = 505;

             */
            switch (msg.what) {

                case BDLocation.TypeNone:
                    locationStatus.setText("None");
                    break;
                case BDLocation.TypeGpsLocation:
                    locationStatus.setText("GPS定位中");
                    break;
                case BDLocation.TypeOffLineLocation:
                    locationStatus.setText("离线定位中");
                    break;
                case BDLocation.TypeOffLineLocationFail:
                    locationStatus.setText("离线定位失败");
                    break;
                case BDLocation.TypeNetWorkLocation:
                    locationStatus.setText("网络定位中");
                    break;
                case BDLocation.TypeCacheLocation:
                    locationStatus.setText("缓存定位中");
                    break;
                case BDLocation.TypeServerError:
                    locationStatus.setText("服务器错误");
                    break;
                case BDLocation.TypeServerCheckKeyError:
                    locationStatus.setText("Key错误");
                    break;
                default:
                    locationStatus.setText("定位失败" + msg.what);
                    break;
            }
        }
    };

    private void begain_location() {
        locateCurrent();

        MapStatusUpdate zoomTo = MapStatusUpdateFactory.zoomTo(ZOOM_LEVEL);//设置缩放级别
        baiduMap.animateMapStatus(zoomTo);
        baiduMap.hideInfoWindow();

        baiduMap.setMyLocationEnabled(true);
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_top);
        mCurrentMode = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL;
        config = new MyLocationConfiguration(mCurrentMode, true, null); //最后一个参数填mCurrentMaker，null则位系统默认

        baiduMap.setMyLocationConfiguration(config);
        initOritationListener();//得到localpositon后初始化方向监听
        //设置点击marker监听
        /*baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                //	Log.e("jhd", "ccccccccccccccccccccc");
                //marker.getPosition();
                startNavi(localposition, marker.getPosition());
                return false;
            }
        });*/
    }

    //每次都要检查定位权限
    //在6.0以上 都需要动态获取权限
    private void checkMyPermission(String need_this, int requestCode) {
        // Assume thisActivity is the current activity
      /*  int wifi_dingwei = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int GPS_dingwei = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(wifi_dingwei == PackageManager.PERMISSION_DENIED){
            //被拒绝后要动态申请

        }*/

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, need_this) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    need_this)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "android6.0以上需要您的定位权限，您可以去系统设置中允许本应用的定位权限", Toast.LENGTH_LONG).show();

            } else {

                // No explanation needed, we can request the permission.

                Toast.makeText(this, "没有权限，正在申请", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{need_this},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            //有权限
            Toast.makeText(this, "已获得定位权限", Toast.LENGTH_SHORT).show();
            begain_location();
        }

    }

    /**
     * 初始化方向传感器
     */
    //方向监听
    MyOrientationListener myOrientationListener;

    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(
                getApplicationContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

                    public void onOrientationChanged(float x) {
                        mXDirection = x;
                        Log.e("jhd", "方向：" + x);
                        if (last_localposition != null) {

                            // 构造定位数据
                            MyLocationData locData = new MyLocationData.Builder()
                                    .accuracy(10)
                                    // 此处设置开发者获取到的方向信息，顺时针0-360
                                    .direction(x)
                                    .latitude(last_localposition.latitude)
                                    .longitude(last_localposition.longitude).build();
                            // 设置定位数据
                            baiduMap.setMyLocationData(locData);
                            // 设置自定义图标
                            //				BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                            //						.fromResource(R.drawable.navi_map_gps_locked);
                            //				MyLocationConfigeration config = new MyLocationConfigeration(
                            //						mCurrentMode, true, null);
                            //				mBaiduMap.setMyLocationConfigeration(config);
                        } else {
                            Log.e("jhd", "localposition为空--方向：" + x);
                        }

                    }
                });
    }

    //定位
    private void locateCurrent() {
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(locationListener);

        LocationClientOption option = new LocationClientOption();
        // LocationClientOption.MIN_SCAN_SPAN
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        // http://bbs.lbsyun.baidu.com/forum.php?mod=viewthread&tid=2920
        /*
        wgs84 --GPS系统直接通过卫星定位获得的坐标.(最基础的坐标.)
        gcj02 --兲朝已安全原因为由,要求在中国使用的地图产品使用的都必须是加密后的坐标.这套加密后的坐标就是gcj02 google的中国地图.高德地图. 他们为中国市场的产品都是用这套坐标.
        bd09ll 百度又在gcj02的技术上将坐标加密就成了 bd09ll坐标.
        总而言之  用这些地图厂家提供的产品和api得到的坐标都不是真正的坐标(不是wgs84坐标).
        wgs84转gcj02的算法在网上可以找到, 但是完善的逆转算法目前还没有.
        bd09ll坐标能再百度地图上正确显示, 如果你用百度地图的api搞开发, 就设置此参数.
         */
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系bd09ll

        int span = 5000;//5秒定位一次 详见 SPAN
        option.setScanSpan(SPAN);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        // mLocationClient.setLocOption(option);

        /*

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
*/
        locationClient.setLocOption(option);

        locationClient.start();

        //  baiduMap.setMyLocationEnabled(true);
        // MyLocationData locationData=new MyLocationData.Builder().accuracy(loa)
    }

    @Override
    protected void initView() {
        super.initView();
        mapView = (MapView) findViewById(R.id.map);
        baiduMap = mapView.getMap();
        locationStatus = (TextView) findViewById(R.id.location_status);
        end = (TextView) findViewById(R.id.end);

    }

    @Override
    protected void initListener() {
        super.initListener();
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (!locationClient.isStarted()) {//开启定位
            locationClient.start();
        }
        // 开启方向传感器
        myOrientationListener.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        locationClient.stop();
        // 关闭方向传感器
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
    }

    private boolean isFirstLoc = true;
    private LatLng last_localposition;//记录当前位置
    private float mXDirection = 0;//方向角度,一开始归为0
    //轨迹点
    List<LatLng> pts = new ArrayList<LatLng>();

    public class MyLocationListener implements BDLocationListener {
        MyLocationData locData;

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.e(tag, "onReceiveLocation locType:" + location.getLocType());
            // Toast.makeText(TrackActivity.this, "type"+location.getLocType()+"la:"+location.getLatitude()+" lo:"+location.getLongitude(), Toast.LENGTH_SHORT).show();
            //获取定位结果

            //更改定位状态
            handler.sendEmptyMessage(location.getLocType());
            // TODO 这里调试用与输出位置的相关信息
            outputResult(location);
            // Toast.makeText(TrackActivity.this,"locType:"+location.getLocType(),Toast.LENGTH_SHORT).show();
            //167表示错误
            //Receive Location
            if (location == null || mapView == null) {
                return;
            }
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(u);//第一次显示地图上的当前位置
                last_localposition = ll;
                //pts.add(ll);
                //handler.sendEmptyMessage(1);
            }
            locData = new MyLocationData.Builder()
                    .accuracy(10)//location.getRadius()
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mXDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            Log.e("jhd", "mXDirection:" + mXDirection);
            Log.e("jhd", "AddrStr:" + location.getAddrStr());


            //位置有变化就增加一个点
            if (last_localposition.latitude != ll.latitude || last_localposition.longitude != ll.longitude) {

                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // Toast.makeText(TrackActivity.this, "GPS add", Toast.LENGTH_SHORT).show();
                    pts.add(ll);
                    if (pts.size() >= 2) {
                        //单位 米
                        sum_distance += DistanceUtil.getDistance(pts.get(pts.size() - 1), pts.get(pts.size() - 1));
                    }
                } else {
                    // Toast.makeText(TrackActivity.this, "add", Toast.LENGTH_SHORT).show();
                }

            }
            last_localposition = ll;


            if (pts.size() > 1) {
                //points count can not less than 2
                OverlayOptions polylineOpt = new PolylineOptions().points(pts);
                //mapView.getOverlay().clear();
                baiduMap.clear();
                baiduMap.addOverlay(polylineOpt);
            }


        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void outputResult(BDLocation location) {
        //获取定位结果
        StringBuffer sb = new StringBuffer(256);

        sb.append("time : ");
        sb.append(location.getTime());    //获取定位时间

        sb.append("\nerror code : ");
        sb.append(location.getLocType());    //获取类型类型

        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());    //获取纬度信息

        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());    //获取经度信息

        sb.append("\nradius : ");
        sb.append(location.getRadius());    //获取定位精准度

        if (location.getLocType() == BDLocation.TypeGpsLocation) {

            // locationStatus.setText("GPS定位中 卫星数:"+location.getSatelliteNumber());
            // GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());    // 单位：公里每小时

            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());    //获取卫星数

            sb.append("\nheight : ");
            sb.append(location.getAltitude());    //获取海拔高度信息，单位米

            sb.append("\ndirection : ");
            sb.append(location.getDirection());    //获取方向信息，单位度

            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
            //locationStatus.setText("网络定位中");

            // 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\noperationers : ");
            sb.append(location.getOperators());    //获取运营商信息

            sb.append("\ndescribe : ");
            sb.append("网络定位成功");

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
            //locationStatus.setText("离线定位中");

            // 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");

        } else if (location.getLocType() == BDLocation.TypeServerError) {
            // locationStatus.setText("服务端网络定位失败");
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            //  locationStatus.setText("网络定位失败，请检查网络");
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            //  locationStatus.setText("多种原因定位失败");
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        } else {
            // locationStatus.setText("努力定位中");
        }

        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());    //位置语义化信息

        List<Poi> list = location.getPoiList();    // POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }

        Log.i("baidu", sb.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //TODO 定位开始
                    Toast.makeText(this, "定位权限已被同意", Toast.LENGTH_SHORT).show();
                    begain_location();

                } else {
                    finish();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
