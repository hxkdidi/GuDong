package cn.test.gudong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.test.gudong.BasicFragment;
import cn.test.gudong.R;
import cn.test.gudong.TrackActivity;
import cn.test.gudong.db.entity.DBHelper;
import cn.test.gudong.db.entity.Track;
import cn.test.gudong.sign.LoginA;

/**
 * Created by jiahaodong on 2017/4/27-14:05.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class SportF extends BasicFragment {
    public SportF() {
        super();
    }

    private ViewPager vp;

    private List<View> viewList;//view数组
    private View yundong;
    private View jibu;

    private TextView allM;

    Button begain;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //启动登录页面
                startActivity(new Intent(getActivity(), LoginA.class));
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_sport, container, false);
        vp = (ViewPager) view.findViewById(R.id.vp);
        //LayoutInflater inflater=getLayoutInflater();
        yundong = inflater.inflate(R.layout.f_sport_yundong, null);
        allM = (TextView) yundong.findViewById(R.id.zlc_num);
        begain = (Button) yundong.findViewById(R.id.begin);
        begain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TrackActivity.class));
            }
        });
        jibu = inflater.inflate(R.layout.f_sport_jibu, null);
        viewList = new ArrayList<>();
        viewList.add(yundong);
        viewList.add(jibu);
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));


                return viewList.get(position);
            }
        };
        vp.setAdapter(pagerAdapter);

        //启动登录页面
        handler.sendEmptyMessage(1);

        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            double all = 0.0;
            List<Track> tracks = DBHelper.seleteAllTrack();
            for (Track temp : tracks) {
                all += Double.parseDouble(temp.getDistance());
            }
            allM.setText((int) all+"");

        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
