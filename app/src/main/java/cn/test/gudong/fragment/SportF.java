package cn.test.gudong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import cn.test.gudong.BasicFragment;
import cn.test.gudong.R;
import cn.test.gudong.TrackActivity;

/**
 * Created by jiahaodong on 2017/4/27-14:05.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class SportF extends BasicFragment{
    public SportF() {
        super();
    }

    private ViewPager vp;

    private List<View> viewList;//view数组
    private View yundong;
    private View jibu;

    Button begain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_sport, container, false);
        vp= (ViewPager) view.findViewById(R.id.vp);
        //LayoutInflater inflater=getLayoutInflater();
        yundong = inflater.inflate(R.layout.f_sport_yundong, null);
        begain = (Button) yundong.findViewById(R.id.begin);
        begain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TrackActivity.class));
            }
        });
        jibu = inflater.inflate(R.layout.f_sport_jibu,null);
        viewList =new ArrayList<>();
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

        return view;
       // return super.onCreateView(inflater, container, savedInstanceState);
    }
}
