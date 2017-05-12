package cn.test.gudong.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

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

    @Column(name = "trackpoints")
    private String trackpoints;

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

    public String getTrackpoints() {
        return trackpoints;
    }

    public void setTrackpoints(String trackpoints) {
        this.trackpoints = trackpoints;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", trackpoints='" + trackpoints + '\'' +
                '}';
    }
}
