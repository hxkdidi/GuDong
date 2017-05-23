package cn.test.gudong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.test.gudong.BasicFragment;
import cn.test.gudong.Config;
import cn.test.gudong.R;
import cn.test.gudong.db.entity.DBHelper;
import cn.test.gudong.db.entity.DBTestActivity;
import cn.test.gudong.db.entity.Track;
import cn.test.gudong.sign.LoginA;
import cn.test.gudong.user.BDTraceA;
import cn.test.gudong.user.TrackDetailActivity;
import cn.test.gudong.user.User;

/**
 * Created by jiahaodong on 2017/4/28-23:29.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

@ContentView(R.layout.f_my)
public class MyF extends BasicFragment {
/*
    @ViewInject(R.id.login)
    TextView login;*/

    @ViewInject(R.id.list_history)
    private RecyclerView list_history;

    private MyAdapter adapter;
    private List<Track> tracks = new ArrayList<>();
    private List<Track> tracksUnSync = new ArrayList<>();
   /* @ViewInject(R.id.test)
    Button test;*/

    //@ViewInject(R.id.db_test)
    //Button db_test;


   /* @Event(value = R.id.login, type = View.OnClickListener.class)
    private void login(View view) {
        Log.e("jhd", "click login on my");
        startActivity(new Intent(getActivity(), LoginA.class));
    }
*/
   /* @Event(value = R.id.db_test, type = View.OnClickListener.class)
    private void db_test(View view) {
        Log.e("jhd", "click dbTest on my");
    }*/
/*
   @Event(R.id.test)
    private void test(View v){
       startActivity(new Intent(getActivity(), BDTraceA.class));
   }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        try {
            tracks = DBHelper.seleteAllTrack();
            list_history.setLayoutManager(new LinearLayoutManager(getActivity()));
            list_history.setAdapter(new MyAdapter());
        } catch (DbException e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


        //----重新加载所有轨迹信息--------------------
        try {
            tracks = DBHelper.seleteAllTrack();
            // list_history.setLayoutManager(new LinearLayoutManager(getActivity()));
            list_history.setAdapter(new MyAdapter());
        } catch (DbException e) {
            e.printStackTrace();
        }

        ///------将未上传轨迹 进行上传处理-------------
        try {
            tracksUnSync = DBHelper.seleteUnSyncTrack();
            //为空，或大小为0 就不用去同步了
            if (tracksUnSync == null || tracksUnSync.size() == 0) {
                Toast.makeText(getActivity(), "暂无数据需要同步", Toast.LENGTH_SHORT).show();
                return;
            }
            Gson g = new Gson();
            String strtrack = g.toJson(tracksUnSync, new TypeToken<List<Track>>() {
            }.getType());
            RequestParams requestParams = new RequestParams(Config.IP_TRACK);
            requestParams.addQueryStringParameter("action", Config.ADD_TRACK + "");
            requestParams.addQueryStringParameter("tracks", strtrack);
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JsonObject jo = new JsonParser().parse(result).getAsJsonObject();
                    boolean isSuc1 = jo.get("isSuc").getAsBoolean();
                    Log.d("jhd", "track: json： " + isSuc1);
                    if (isSuc1) {
                        Toast.makeText(getActivity(), "同步" + tracksUnSync.size() + "条成功", Toast.LENGTH_SHORT).show();
                        for (Track temp : tracksUnSync) {
                            try {
                                DBHelper.updateTrack(temp);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(getActivity(), "无法连接服务器", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int TYPE_HEADER = 11;
        private final int TYPE_NORMAL = 22;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_NORMAL) {
                MyHolder holder = new MyHolder(LayoutInflater.from(getActivity()).inflate(R.layout.list_sport_history, parent, false));
                return holder;
            } else {
                MyHeaderHolder headerHolder = new MyHeaderHolder(LayoutInflater.from(getActivity()).inflate(R.layout.list_my_history_header, parent, false));
                return headerHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) {
                // ((MyHeaderHolder)holder).
                //判断用户是否已登录
                User u = User.getInstace();
                if (u.getUsername() != null) {
                    MyHeaderHolder headerHolder = (MyHeaderHolder) holder;
                    headerHolder.login.setText(u.getUsername());

                }

            } else {
                MyHolder myHolder = (MyHolder) holder;
                Track track = tracks.get(position - 1);
                String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(Long.parseLong(track.getTimestamp())));
                myHolder.start_time.setText(date);
                int m = (int) Double.parseDouble(track.getDistance());
                myHolder.distance.setText("" + m);
                long duration = Long.parseLong(track.getTimestamp_e()) - Long.parseLong(track.getTimestamp());
                int allSecond = (int) (duration / 1000);
                int hour = allSecond / 3600;
                int min = allSecond / 60 - hour * 60;
                int second = allSecond - 3600 * hour - min * 60;
                myHolder.duration.setText(bu0(hour) + ":" + bu0(min) + ":" + bu0(second));
                int myspeed = (int) ((double) m / (double) allSecond * 60D);
                myHolder.speed.setText(myspeed + " m/min");

            }

        }

        @Override
        public int getItemCount() {
            //这里包括了一个header
            if (tracks != null) {
                return tracks.size() + 1;
            }
            return 1;

        }

        @Override
        public int getItemViewType(int position) {
            //return super.getItemViewType(position);
            return position == 0 ? TYPE_HEADER : TYPE_NORMAL;
        }
    }

    private String bu0(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        private TextView start_time;
        private TextView duration;
        private TextView speed;
        private TextView distance;
        private ImageView image;

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),""+getPosition(),Toast.LENGTH_SHORT).show();
                Intent it=new Intent(getContext(), TrackDetailActivity.class);
                it.putExtra("points",tracks.get(getPosition()-1).getPoints());
                startActivity(it);
            }
        };

        public MyHolder(View v) {
            super(v);
            start_time = (TextView) v.findViewById(R.id.start_time);
            duration = (TextView) v.findViewById(R.id.duration);
            speed = (TextView) v.findViewById(R.id.speed);
            distance = (TextView) v.findViewById(R.id.distance);
            image= (ImageView) v.findViewById(R.id.image1);
            image.setOnClickListener(listener);
        }
    }

    public class MyHeaderHolder extends RecyclerView.ViewHolder {

        private TextView login;

        public MyHeaderHolder(View v) {
            super(v);
            login = (TextView) v.findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //User 中的 username为空证明用户没有登录过，点击会跳转到登录页面，否则不进行任何操作
                    if (User.getInstace().getUsername() == null) {
                        Log.e("jhd", "click login on my");
                        startActivity(new Intent(getActivity(), LoginA.class));
                    }
                }
            });
        }
    }
}
