package cn.test.gudong.db.entity;

/**
 * Created by jiahaodong on 2017/5/21-16:14.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class Point {

    private String la;
    private String lo;
    private String timestamp;

    public Point() {
    }

    public Point(String la, String lo, String timestamp) {
        this.la = la;
        this.lo = lo;
        this.timestamp = timestamp;
    }

    public String getLa() {
        return la;
    }

    public void setLa(String la) {
        this.la = la;
    }

    public String getLo() {
        return lo;
    }

    public void setLo(String lo) {
        this.lo = lo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Point{" +
                "la='" + la + '\'' +
                ", lo='" + lo + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
