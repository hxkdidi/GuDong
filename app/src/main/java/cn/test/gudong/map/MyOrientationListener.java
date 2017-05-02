package cn.test.gudong.map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by jiahaodong on 2017/4/30-21:19.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class MyOrientationListener implements SensorEventListener
{  //代码来自http://blog.csdn.net/lmj623565791/article/details/37730469

    private Context context;
    private SensorManager sensorManager;
    private Sensor sensor;

    private float lastX ;

    private OnOrientationListener onOrientationListener ;

    public MyOrientationListener(Context context)
    {
        this.context = context;
    }

    // 开始
    public void start()
    {
        // 获得传感器管理器
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null)
        {
            // 获得方向传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        // 注册
        if (sensor != null)
        {//SensorManager.SENSOR_DELAY_UI
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

    }

    // 停止检测
    public void stop()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // 接受方向感应器的类型
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
        {
            // 这里我们可以得到数据，然后根据需要来处理
            float x = event.values[SensorManager.DATA_X];

            if( Math.abs(x- lastX) > 1.0 )
            {
                onOrientationListener.onOrientationChanged(x);
            }
//            Log.e("DATA_X", x+"");
            lastX = x ;

        }
    }

    public void setOnOrientationListener(OnOrientationListener onOrientationListener)
    {
        this.onOrientationListener = onOrientationListener ;
    }


    public interface OnOrientationListener
    {
        void onOrientationChanged(float x);
    }

}