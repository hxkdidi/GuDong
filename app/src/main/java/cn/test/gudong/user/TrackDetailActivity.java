package cn.test.gudong.user;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.test.gudong.BasicActivity;
import cn.test.gudong.R;
import cn.test.gudong.db.entity.DBHelper;
import cn.test.gudong.db.entity.Point;
import cn.test.gudong.db.entity.Track;
import cn.test.gudong.map.MyOrientationListener;

public class TrackDetailActivity extends BasicActivity {
    String tag = "TrackDetailActivity";

    //地图模式
    private int mode = 1;//1.普通，2，跟随，3罗盘。默认是1；


    List<LatLng> pts = new ArrayList<LatLng>();
    MapView mapView;
    BaiduMap baiduMap;
    //默认缩放级别
    int ZOOM_LEVEL = 20;//地图等级为3-21级

    private BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration config;

    private MyLocationConfiguration.LocationMode mCurrentMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());//要在setcontent之前使用并传入ApplicationContext
        setContentView(R.layout.activity_track_detail);
        super.onCreate(savedInstanceState);

        String pointsStr = getIntent().getStringExtra("points");
        Gson g = new Gson();
        Type t = new TypeToken<List<Point>>() {
        }.getType();
        List<Point> points = g.fromJson(pointsStr, t);
        Point point = points.get(0);
        LatLng latLng = new LatLng(Double.parseDouble(point.getLa()), Double.parseDouble(point.getLo()));
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(u);//第一次显示地图上的当前位置

        MapStatusUpdate zoomTo = MapStatusUpdateFactory.zoomTo(ZOOM_LEVEL);//设置缩放级别
        baiduMap.animateMapStatus(zoomTo);
        baiduMap.hideInfoWindow();

        for (int i = 0; i < points.size(); i++) {
            pts.add(new LatLng(Double.parseDouble(points.get(i).getLa()), Double.parseDouble(points.get(i).getLo())));
        }

        //guiji
        if (pts.size() > 1) {
            //points count can not less than 2
            OverlayOptions polylineOpt = new PolylineOptions().points(pts);
            //mapView.getOverlay().clear();
            baiduMap.clear();
            baiduMap.addOverlay(polylineOpt);
        }
    }


    @Override
    protected void initView() {
        super.initView();
        mapView = (MapView) findViewById(R.id.map);
        baiduMap = mapView.getMap();

    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
    }
}
