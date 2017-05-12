package cn.test.gudong;

import android.app.Application;

import org.xutils.x;

/**
 * Created by jiahaodong on 2017/5/5-13:58.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

    }
}
