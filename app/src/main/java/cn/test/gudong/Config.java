package cn.test.gudong;

/**
 * Created by jiahaodong on 2017/5/9-9:53.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public interface Config {


    //all action
    public static final int LOGIN = 1;
    public static final int SIGN_UP = 2;
    public static final int SYNC = 3;

    public static final int GET_TRACK = 4;
    public static final int ADD_TRACK = 5;


    public static final int JOIN = 6;

    public static final int GET_GAME = 7;


    //public static String IP = "172.16.96.93";
    public static String IP = "192.168.31.171";

    public static String IP_SIGN = "http://" + IP + ":8080/GuDongS/sign";
    public static String IP_GET_GAME = "http://" + IP + ":8080/GuDongS/most";
}
