package cn.test.gudong.main.fragment.main_game;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import cn.test.gudong.main.BasicActivity;
import cn.test.gudong.Config;
import cn.test.gudong.R;
import cn.test.gudong.db.entity.MyGame;
import cn.test.gudong.db.entity.User;

@ContentView(R.layout.activity_my_game)
public class MyGameActivity extends BasicActivity {

    List<MyGame> datas = new ArrayList<MyGame>();

    @ViewInject(R.id.recycler)
    RecyclerView recyclerView;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 0:
                    getDatas();
                    break;
                case 1:
                    if (datas.size() == 0) {
                        Toast.makeText(MyGameActivity.this, "您还未报名任何比赛", Toast.LENGTH_SHORT).show();

                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyGameActivity.this));
                        MyAdapter adapter = new MyAdapter();
                        recyclerView.setAdapter(adapter);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取数据
        handler.sendEmptyMessage(0);
    }

    public void getDatas() {
        String username = User.getInstace().getUsername();
        if (username == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams(Config.IP_GET_GAME);
        params.addQueryStringParameter("action", "" + Config.GET_MY_GAME);
        params.addQueryStringParameter("username", "" + username);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Gson g = new Gson();
                datas = g.fromJson(result, new TypeToken<List<MyGame>>() {
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

    public class MyAdapter extends RecyclerView.Adapter<MyHodler> {

        @Override
        public MyHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHodler hodler = new MyHodler(LayoutInflater.from(MyGameActivity.this).inflate(R.layout.list_my_game, parent, false));
            return hodler;
        }

        @Override
        public void onBindViewHolder(MyHodler holder, int position) {
            MyGame game = datas.get(position);
            holder.title.setText(game.getTitle());
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    public class MyHodler extends RecyclerView.ViewHolder {

        private TextView title;

        public MyHodler(View v) {
            super(v);


            title = (TextView) v.findViewById(R.id.title);

        }

    }
}
