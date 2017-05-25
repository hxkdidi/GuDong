package cn.test.gudong.db.entity;

/**
 * Created by jiahaodong on 2017/5/5-15:25.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class User {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static User INSTANCE =new User();

    static public User getInstace() {
        return INSTANCE;
    }
}
