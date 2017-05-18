package cn.test.gudong;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatus.Builder;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.juli.vehicle.bean.Orbit;
import com.qsydw_android.R;
import com.umeng.analytics.MobclickAgent;


/**
 * 
 * 说明：轨迹回放
 * @author 陈琳亮
 * 2015-11-16
 */
public class TrackPlayBackActivity  extends Activity implements OnMapDrawFrameCallback, OnSeekBarChangeListener, OnClickListener{
	IntBuffer b;

	//播放进度条
	private MapView mv_vehicle_orbit_map;
	//播放进度条
	private SeekBar sb_vehicle_orbit_play;
	//播放/停止按钮
	private Button bt_vehicle_orbit_paly;
	//用于显示当前播放点经纬度上传时间的TextView
	private TextView tv_vehicle_orbit_time;
	////播放速度进度条的父控件
	private LinearLayout ll_vehicle_orbit_speed;
	//控制播放速度的进度条
	private SeekBar sb_vehicle_orbit_speed;
	//返回按钮
	private ImageView iv_vehicle_orbit_rollback;
	
	//------------------------地图画线相关--------------------------
	private List<LatLng> latLngPolygon = new ArrayList<LatLng>();
	private float[] vertexs;
	private FloatBuffer vertexBuffer;
	
	private BaiduMap mBaiduMap;
	//页面传递获取的数据
	private List<Orbit> playbackList;
	//	自动播放
	private static final int AUTO_PLAY = 0x01;
	//	拖动播放
	private static final int DRAG_PLAY = 0x02;
	//处理任务的Handler
	private PlayHandler playHandler;
	//播放线程
	private PlayThread playThread;
	//播放的点
	private int playPoint = 0;
	//播放轨迹线程的标志位
	private boolean play = true;
	//间隔时间
	private long interval = 1 * 1000;
	//车辆标记
	private Marker carMarker;

	
	

	//拖动播放传递内容的消息
	private Message msg;

	
	OnInfoWindowClickListener onInfoWindowClickListener = null;

	InfoWindow infoWindow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
		setContentView(R.layout.vehicle_orbit_paly_activity);
		InitView();//初始化控件
		InitEvent();//初始化事件
	}

	//view初始化
	private void InitView() {
		//百度地图
		mv_vehicle_orbit_map = (MapView) findViewById(R.id.mv_vehicle_orbit_map);
		//播放进度条
		sb_vehicle_orbit_play = (SeekBar) findViewById(R.id.sb_vehicle_orbit_play);
		////播放/停止按钮
		bt_vehicle_orbit_paly = (Button) findViewById(R.id.bt_vehicle_orbit_paly);
		//用于显示当前播放点经纬度上传时间的TextView
		tv_vehicle_orbit_time = (TextView) findViewById(R.id.tv_vehicle_orbit_time);
		//播放速度进度条的父控件
		ll_vehicle_orbit_speed = (LinearLayout) findViewById(R.id.ll_vehicle_orbit_speed);
		//控制播放速度的进度条
		sb_vehicle_orbit_speed = (SeekBar) findViewById(R.id.sb_vehicle_orbit_speed);
		sb_vehicle_orbit_play.setOnSeekBarChangeListener(this);
		bt_vehicle_orbit_paly.setOnClickListener(this);
		sb_vehicle_orbit_speed.setOnSeekBarChangeListener(this);
		sb_vehicle_orbit_speed.setMax(8);
		sb_vehicle_orbit_speed.setProgress(4);
		iv_vehicle_orbit_rollback = (ImageView) findViewById(R.id.iv_vehicle_orbit_rollback);
		iv_vehicle_orbit_rollback.setOnClickListener(this);
	}
	
	public void InitEvent(){
		//初始化数据
		dataInit();
		//初始化地图
		mapInit();
		//点击地图上图标
		OnMarkerClick();
	}
	
	
	

	//数据初始化
	private void dataInit() {
		playbackList = (List<Orbit>) this.getIntent().getSerializableExtra("orbitList");
		if (null != playbackList) {
			for(Orbit orbit : playbackList){
				// 将GPS设备采集的原始GPS坐标转换成百度坐标  
				CoordinateConverter converter  = new CoordinateConverter();  
				converter.from(CoordType.GPS);
				LatLng latLng = new LatLng(orbit.getLatitude(),orbit.getLongitude());
				// sourceLatLng待转换坐标  
				converter.coord(latLng);  
				LatLng desLatLng = converter.convert(); 
				latLngPolygon.add(desLatLng);
			}
		}
		playHandler = new PlayHandler();
		sb_vehicle_orbit_play.setMax(latLngPolygon.size() - 1);
		startPlayThread();
	}
	
	public void OnMarkerClick(){
		// 点击地图图标
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if(marker==carMarker){  
					mBaiduMap.showInfoWindow(infoWindow);
				}
				mBaiduMap.showInfoWindow(infoWindow);
				Button infoimg = new Button(getApplicationContext());
				infoimg.setBackgroundColor(Color.parseColor("#FFFFFF"));
				infoimg.setText(marker.getTitle());
				infoimg.setTextSize(14);
				infoimg.setTextColor(Color.parseColor("#000000"));
				infoimg.setGravity(Gravity.LEFT);
				infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(infoimg), marker.getPosition(), -62, onInfoWindowClickListener);
				mBaiduMap.showInfoWindow(infoWindow);
				return false;
			}
		});

		onInfoWindowClickListener = new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick() {
				mBaiduMap.hideInfoWindow();

			}
		};
	}

	//地图初始化
	private void mapInit() {
		
		mBaiduMap = mv_vehicle_orbit_map.getMap();
		mBaiduMap.setOnMapDrawFrameCallback(this);
		Builder mMapStatusBuilder = new Builder();
		mMapStatusBuilder.zoom(20);
		mMapStatusBuilder.target(latLngPolygon.get(0));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatusBuilder.build()));
		addMarkByLatLng(latLngPolygon.get(0), R.drawable.nav_route_result_start_point);
		addMarkByLatLng(latLngPolygon.get(latLngPolygon.size() - 1), R.drawable.nav_route_result_end_point);
		carMarker = addMarkByLatLng(latLngPolygon.get(0), getResIdByDir(playbackList.get(0).getDirection()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_vehicle_orbit_paly:
			if (play) {
				playThread.cancel();
			} else {
				//重新开始播放
				if(playPoint==0){
					//清空地图
					mBaiduMap.clear();
					addMarkByLatLng(latLngPolygon.get(0), R.drawable.nav_route_result_start_point);
					addMarkByLatLng(latLngPolygon.get(latLngPolygon.size() - 1), R.drawable.nav_route_result_end_point);
				}
				startPlayThread();
			}
			break;
		case R.id.iv_vehicle_orbit_rollback:
			iv_vehicle_orbit_rollback.setImageResource(R.drawable.backebtnpressed);
			finish();
			break;

		default:
			break;
		}
	}

	//开始播放
	private void startPlayThread() {
		play = true;
		bt_vehicle_orbit_paly.setText("停止");
		tv_vehicle_orbit_time.setVisibility(View.VISIBLE);
		ll_vehicle_orbit_speed.setVisibility(View.VISIBLE);
		playThread = new PlayThread();
		playThread.start();
	}

	//处理拖动进度条事件的方法
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.sb_vehicle_orbit_play:
			onPlayChanged(progress);
			break;
		case R.id.sb_vehicle_orbit_speed:
			onSpeedChanged(progress);
			break;

		default:
			break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}

	private void onPlayChanged(int progress) {
		msg = playHandler.obtainMessage(DRAG_PLAY, progress);
		playHandler.sendMessage(msg);
	}

	//处理速度改变的方法
	private void onSpeedChanged(int progress) {
		interval = 1800 - (200 * progress);
	}

	//处理轨迹回放Handler
	class PlayHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AUTO_PLAY:
				if (play) {
					if (playPoint < latLngPolygon.size()) {
//						if (null != carMarker) {
//							carMarker.remove();
//							carMarker = null;
//						}
						carMarker = addMarkByLatLng(latLngPolygon.get(playPoint), getResIdByDir(playbackList.get(playPoint).getDirection()));
						mapMoveTo(latLngPolygon.get(playPoint));
						sb_vehicle_orbit_play.setProgress(playPoint);
						tv_vehicle_orbit_time.setText(playbackList.get(playPoint).getGpsDateTime());
						playPoint ++;
					} else {
						playThread.cancel();
						playPoint = 0;
					}
				}
				break;
			case DRAG_PLAY:
				playPoint = (Integer) msg.obj;
//				if (null != carMarker) {
//					carMarker.remove();
//					carMarker = null;
//				}
				carMarker = addMarkByLatLng(latLngPolygon.get(playPoint), getResIdByDir(playbackList.get(playPoint).getDirection()));
				mapMoveTo(latLngPolygon.get(playPoint));
				tv_vehicle_orbit_time.setText(playbackList.get(playPoint).getGpsDateTime());
				break;

			default:
				break;
			}
		}

	}

	//播放轨迹线程
	class PlayThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (play) {
				playHandler.sendEmptyMessage(AUTO_PLAY);
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void cancel() {
			play = false;
			bt_vehicle_orbit_paly.setText("回放");
			playThread = null;
			tv_vehicle_orbit_time.setVisibility(View.GONE);
			ll_vehicle_orbit_speed.setVisibility(View.INVISIBLE);
		}

	}

	//将地图中心点移动到指定坐标
	private void mapMoveTo(LatLng point) {
		MapStatusUpdate latLng = MapStatusUpdateFactory.newLatLng(point);
		mBaiduMap.animateMapStatus(latLng);
	}

	//根据给定坐标和给定图片在地图上添加覆盖物
	private Marker addMarkByLatLng(LatLng point, int iconId) {
		StringBuilder builder = new StringBuilder();
		int speed = playbackList.get(playPoint).getSpeed(); 
		double totaldistance = playbackList.get(playPoint).getTotalDistance();
		double totaloilconsume = playbackList.get(playPoint).getTotalOilConsume();
		String gpsdatetime = playbackList.get(playPoint).getGpsDateTime();
		String accStatus = playbackList.get(playPoint).getAccStatus();
		builder.append("当前车速:").append(speed).append("(小时/公里)\n");
		builder.append("启动状态:").append(accStatus).append("\n");
		builder.append("总耗油量:").append(totaloilconsume).append("(升)\n");
		builder.append("总行驶里程:").append(totaldistance).append("(公里)\n");
		builder.append("上传时间:").append(gpsdatetime).append("");
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(iconId);
		MarkerOptions option = new MarkerOptions().position(point).icon(bitmap).title(builder.toString());
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(point);
		mBaiduMap.animateMapStatus(mapStatusUpdate);
		return (Marker) (mBaiduMap.addOverlay(option));
	}

	//根据方向返回图片资源ID
	public int getResIdByDir(int direction) {
		int d=(direction-(-5))/10;
		if(d>35){
			d=0;
		}
		switch (d) {
		case 0:
			return R.drawable.e0;
		case 1:
			return R.drawable.e1;
		case 2:
			return R.drawable.e2;
		case 3:
			return R.drawable.e3;
		case 4:
			return R.drawable.e4;
		case 5:
			return R.drawable.e5;
		case 6:
			return R.drawable.e6;
		case 7:
			return R.drawable.e7;
		case 8:
			return R.drawable.e8;
		case 9:
			return R.drawable.e9;
		case 10:
			return R.drawable.e10;
		case 11:
			return R.drawable.e11;
		case 12:
			return R.drawable.e12;
		case 13:
			return R.drawable.e13;
		case 14:
			return R.drawable.e14;
		case 15:
			return R.drawable.e15;
		case 16:
			return R.drawable.e16;
		case 17:
			return R.drawable.e17;
		case 18:
			return R.drawable.e18;
		case 19:
			return R.drawable.e19;	
		case 20:
			return R.drawable.e20;	
		case 21:
			return R.drawable.e21;
		case 22:
			return R.drawable.e22;
		case 23:
			return R.drawable.e23;
		case 24:
			return R.drawable.e24;
		case 25:
			return R.drawable.e25;
		case 26:
			return R.drawable.e26;
		case 27:
			return R.drawable.e27;
		case 28:
			return R.drawable.e28;
		case 29:
			return R.drawable.e29;
		case 30:
			return R.drawable.e30;
		case 31:
			return R.drawable.e31;
		case 32:
			return R.drawable.e32;
		case 33:
			return R.drawable.e33;
		case 34:
			return R.drawable.e34;
		case 35:
			return R.drawable.e35;	
		}
		return R.drawable.e0;
	}

	//---------------------------画线相关方法---------------------------
	public void onMapDrawFrame(GL10 gl, MapStatus drawingMapStatus) {
		if (mBaiduMap.getProjection() != null) {
			calPolylinePoint(drawingMapStatus);
			drawPolyline(gl, Color.argb(255, 255, 0, 0), vertexBuffer, 10, latLngPolygon.size(), drawingMapStatus);
		}
	}

	//---------------------------画线相关方法---------------------------
	public void calPolylinePoint(MapStatus mspStatus) {
		PointF[] polyPoints = new PointF[latLngPolygon.size()];
		vertexs = new float[3 * latLngPolygon.size()];
		int i = 0;
		for (LatLng xy : latLngPolygon) {
			polyPoints[i] = mBaiduMap.getProjection().toOpenGLLocation(xy,
					mspStatus);
			vertexs[i * 3] = polyPoints[i].x;
			vertexs[i * 3 + 1] = polyPoints[i].y;
			vertexs[i * 3 + 2] = 0.0f;
			i++;
		}
		vertexBuffer = makeFloatBuffer(vertexs);
	}

	//---------------------------画线相关方法---------------------------
	private FloatBuffer makeFloatBuffer(float[] fs) {
		ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(fs);
		fb.position(0);
		return fb;
	}

	//---------------------------画线相关方法---------------------------
	private void drawPolyline(GL10 gl, int color, FloatBuffer lineVertexBuffer,
			float lineWidth, int pointSize, MapStatus drawingMapStatus) {

		gl.glEnable(GL10.GL_BLEND);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		float colorA = Color.alpha(color) / 255f;
		float colorR = Color.red(color) / 255f;
		float colorG = Color.green(color) / 255f;
		float colorB = Color.blue(color) / 255f;

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVertexBuffer);
		gl.glColor4f(colorR, colorG, colorB, colorA);
		gl.glLineWidth(lineWidth);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointSize);

		gl.glDisable(GL10.GL_BLEND);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	

	@Override
	protected void onResume() {
		mv_vehicle_orbit_map.onResume();
		super.onResume();
		//友盟Session统计
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		mv_vehicle_orbit_map.onPause();
		super.onPause();
		//友盟Session统计
        MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		onMyDestroy();
		mv_vehicle_orbit_map.onDestroy();
		super.onDestroy();
	}

	//结束之前停止线程
	private void onMyDestroy() {
		if (null != playThread) {
			playThread.cancel();
		}
	}

}
