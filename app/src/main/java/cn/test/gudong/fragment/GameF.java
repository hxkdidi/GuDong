package cn.test.gudong.fragment;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.test.gudong.BasicFragment;
import cn.test.gudong.Config;
import cn.test.gudong.R;

/**
 * Created by jiahaodong on 2017/4/28-23:29.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

@ContentView(R.layout.f_game)
public class GameF extends BasicFragment {

    @ViewInject(R.id.recycler)
    RecyclerView recyclerView;

    List<Game> datas = new ArrayList<Game>();

    public GameF() {
        super();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 0:
                    getDatas();
                    break;
                case 1:
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    MyAdapter adapter=new MyAdapter();
                    adapter.setOnItemClickListener(new MyOnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int postion) {
                            Toast.makeText(getActivity(),"p:"+postion,Toast.LENGTH_SHORT).show();
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    break;
            }
        }
    };

    public void getDatas() {
        RequestParams params = new RequestParams(Config.IP_GET_GAME);
        params.addQueryStringParameter("action", "" + Config.GET_GAME);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Gson g = new Gson();
                datas = g.fromJson(result, new TypeToken<List<Game>>() {
                }.getType());
                handler.sendEmptyMessage(1);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        //  recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // recyclerView.setAdapter(new MyAdapter());
        handler.sendEmptyMessage(0);
        return v;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyHodler> {

        MyOnItemClickListener clickListener=null;
        public void setOnItemClickListener(MyOnItemClickListener clickListener){
            this.clickListener=clickListener;

        }

        @Override
        public MyHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHodler hodler = new MyHodler(LayoutInflater.from(getActivity()).inflate(R.layout.list_game, parent, false),clickListener);
            return hodler;
        }

        @Override
        public void onBindViewHolder(MyHodler holder, int position) {
            Game game = datas.get(position);
            holder.title.setText(game.getTitle());
            holder.countDown.setText(game.getDate());
            holder.tags.setText(game.getTag());
            holder.itemView.setTag(position);


        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    public class MyHodler extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView title;
        private TextView countDown;
        private TextView num;
        private TextView tags;

        MyOnItemClickListener clickListener;

        public MyHodler(View v, MyOnItemClickListener listener) {
            super(v);
            clickListener = listener;

            imageView = (ImageView) v.findViewById(R.id.image);
            title = (TextView) v.findViewById(R.id.title);
            countDown = (TextView) v.findViewById(R.id.count_down);
            num = (TextView) v.findViewById(R.id.num);
            tags = (TextView) v.findViewById(R.id.tags);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(v, (int) v.getTag());
            }
        }
    }

    public interface MyOnItemClickListener {
        public void onItemClick(View v, int postion);
    }


}
