package cn.test.gudong.db.entity;

/**
 * Created by jiahaodong on 2017/5/12-22:23.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class Game {
    private int id;
    private String title;
    private String date;
    private String tag;
    private String image;
    private String details;
    private int peoplenum;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public int getPeoplenum() {
        return peoplenum;
    }

    public void setPeoplenum(int peoplenum) {
        this.peoplenum = peoplenum;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", tag='" + tag + '\'' +
                ", image='" + image + '\'' +
                ", details='" + details + '\'' +
                ", peoplenum=" + peoplenum +
                '}';
    }
}
