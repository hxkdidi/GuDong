package cn.test.gudong;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.test.gudong.fragment.SportF;

public class MainActivity extends BasicActivity {

    LinearLayout content;
    Fragment sportF;

    TextView game;
    TextView sport;
    TextView my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, sportF);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    protected void initView() {
        super.initView();
        game = (TextView) findViewById(R.id.game);
        sport = (TextView) findViewById(R.id.sport);
        my = (TextView) findViewById(R.id.my);

        content = (LinearLayout) findViewById(R.id.content);
        sportF = new SportF();

    }

    @Override
    protected void initListener() {
        super.initListener();
    }
}
