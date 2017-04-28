package cn.test.gudong.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.test.gudong.BasicFragment;
import cn.test.gudong.R;

/**
 * Created by jiahaodong on 2017/4/28-23:29.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class MyF extends BasicFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.f_my,container,false);
        return view;
    }
}
