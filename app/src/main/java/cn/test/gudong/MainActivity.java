package cn.test.gudong;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;

import cn.test.gudong.fragment.GameF;
import cn.test.gudong.fragment.MyF;
import cn.test.gudong.fragment.SportF;

public class MainActivity extends BasicActivity implements View.OnClickListener {
    String tag = "MainActivity";

    LinearLayout content;
    Fragment sportF;
    Fragment gameF;
    Fragment myF;
    //当前正在显示的fragment
    Fragment mCurrent;

    @ViewInject(R.id.game)
    RadioButton game;
    @ViewInject(R.id.sport)
    RadioButton sport;
    @ViewInject(R.id.my)
    RadioButton my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        switchF(sportF);
        /*FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, sportF);
        ft.addToBackStack(null);
        ft.commit();*/
    }


    @Override
    protected void initView() {
        super.initView();

        content = (LinearLayout) findViewById(R.id.content);
        sportF = new SportF();
        gameF = new GameF();
        myF = new MyF();

    }

    @Override
    protected void initListener() {
        super.initListener();
        game.setOnClickListener(this);
        sport.setOnClickListener(this);
        my.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game:
                switchF(gameF);

                break;
            case R.id.sport:
                switchF(sportF);
                break;
            case R.id.my:
                switchF(myF);
                break;
        }
    }

  /*  private void switchF(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }*/

    private void switchF(Fragment to) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mCurrent == null) {
            mCurrent = to;
        }
        if (mCurrent == to) {
            if (!to.isAdded()) {
                ft.add(R.id.content, to);
                ft.commit();
                Log.d(tag, "初始化" + to.getClass().getSimpleName());

            } else {
                Log.d(tag, "没有任何变化");
            }

        } else {
            if (!to.isAdded()) {
                ft.hide(mCurrent);
                ft.add(R.id.content, to);
                ft.commit();
                Log.d(tag, "第一次从" + mCurrent.getClass().getSimpleName() + "切换到" + to.getClass().getSimpleName());
            } else {
                ft.hide(mCurrent);
                ft.show(to);
                ft.commit();
                Log.d(tag, "从" + mCurrent.getClass().getSimpleName() + "切换到" + to.getClass().getSimpleName());
                ;
            }
        }
        //最终切换到的fragment就是当前fragment
        mCurrent = to;
    }


}
