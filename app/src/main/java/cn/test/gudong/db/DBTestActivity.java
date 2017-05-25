package cn.test.gudong.db;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xutils.ex.DbException;

import java.util.List;

import cn.test.gudong.main.BasicActivity;
import cn.test.gudong.R;
import cn.test.gudong.db.entity.Track;

/**
 * Created by jiahaodong on 2017/5/5-14:33.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class DBTestActivity extends BasicActivity implements View.OnClickListener{

    Button add;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.a_db_test);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        add= (Button) findViewById(R.id.add);
        search= (Button) findViewById(R.id.search);
    }

    @Override
    protected void initListener() {
        super.initListener();
        add.setOnClickListener(this);
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                try {
                    Log.d("db-test","add");
                    Track track=new Track();

                    DBHelper.insertTrack(track);
                    //Log.d("db-test",tracks.toString());

                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.search:
                try {
                    List<Track> tracks = DBHelper.seleteAllTrack();
                    Toast.makeText(getApplication(),tracks.toString(),Toast.LENGTH_SHORT).show();
                    Log.d("db-test",tracks.toString());
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}
