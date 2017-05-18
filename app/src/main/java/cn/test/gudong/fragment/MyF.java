package cn.test.gudong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.test.gudong.BasicFragment;
import cn.test.gudong.R;
import cn.test.gudong.db.entity.DBTestActivity;
import cn.test.gudong.sign.LoginA;
import cn.test.gudong.user.BDTraceA;

/**
 * Created by jiahaodong on 2017/4/28-23:29.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

@ContentView(R.layout.f_my)
public class MyF extends BasicFragment {

    @ViewInject(R.id.login)
    TextView login;

    @ViewInject(R.id.test)
    Button test;

    //@ViewInject(R.id.db_test)
    //Button db_test;


    @Event(value = R.id.login, type = View.OnClickListener.class)
    private void login(View view) {
        Log.e("jhd", "click login on my");
        startActivity(new Intent(getActivity(), LoginA.class));
    }

   /* @Event(value = R.id.db_test, type = View.OnClickListener.class)
    private void db_test(View view) {
        Log.e("jhd", "click dbTest on my");
    }*/

   @Event(R.id.test)
    private void test(View v){
       startActivity(new Intent(getActivity(), BDTraceA.class));
   }
}
