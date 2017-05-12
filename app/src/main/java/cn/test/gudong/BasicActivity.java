package cn.test.gudong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.xutils.x;

public class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initView();
        initListener();
    }

    //初始化都在这里做
    protected void initView(){

    }
    //注册监听都在这里
    protected  void initListener(){}
}
