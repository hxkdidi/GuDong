package cn.test.gudong.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.view.annotation.ContentView;

/**
 * Created by jiahaodong on 2017/5/5-13:45.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

@Table(name = "track")
public class Track {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "username")
    private String username;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "points")
    private String points;

    @Column(name = "timestamp_e")
    private String timestamp_e;

    //单位米 double
    @Column(name = "distance")
    private String distance;

    //1表示同步过的，0表示还没有同步到服务器
    @Column(name = "sync")
    private int sync;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTimestamp_e() {
        return timestamp_e;
    }

    public void setTimestamp_e(String timestamp_e) {
        this.timestamp_e = timestamp_e;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", points='" + points + '\'' +
                ", timestamp_e='" + timestamp_e + '\'' +
                ", distance='" + distance + '\'' +
                ", sync=" + sync +
                '}';
    }
}
