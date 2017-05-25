package cn.test.gudong.sign;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.test.gudong.main.BasicActivity;
import cn.test.gudong.Config;
import cn.test.gudong.R;
import cn.test.gudong.db.entity.User;

/**
 * Created by jiahaodong on 2017/4/27-13:42.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class LoginA extends BasicActivity {

    @ViewInject(R.id.username)
    private EditText username;

    @ViewInject(R.id.password)
    private EditText password;

    @ViewInject(R.id.login)
    private Button login;

    @ViewInject(R.id.sign_up)
    private Button sign_up;

    @ViewInject(R.id.back)
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.a_login);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Event(R.id.login)
    private void login(View v) {
        final String usernameStr = username.getText().toString();
        final String passwordStr = password.getText().toString();
        RequestParams params = new RequestParams(Config.IP_SIGN);
        params.addQueryStringParameter("username", usernameStr);
        params.addQueryStringParameter("password", passwordStr);
        params.addQueryStringParameter("action", "" + Config.LOGIN);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("jhd", "login: " + result);
                try {
                    JsonObject jo = new JsonParser().parse(result).getAsJsonObject();
                    boolean isSuc1 = jo.get("isSuc").getAsBoolean();
                    Log.d("jhd", "login: json： " + isSuc1);
                    if(isSuc1){
                        //TODO login suc
                        User u= User.getInstace();
                        u.setUsername(usernameStr);
                        u.setPassword(passwordStr);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    @Event(R.id.sign_up)
    private void signUp(View v) {
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();

        RequestParams params = new RequestParams("http://" + Config.IP + ":8080/GuDongS/sign");
        params.addQueryStringParameter("username", usernameStr);
        params.addQueryStringParameter("password", passwordStr);
        params.addQueryStringParameter("action", "" + Config.SIGN_UP);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("jhd", "sign_up: " + result);
                try {
                    JsonObject jo = new JsonParser().parse(result).getAsJsonObject();
                    boolean isSuc1 = jo.get("isSuc").getAsBoolean();

                    if(isSuc1){
                        Toast.makeText(LoginA.this,"注册成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginA.this,"注册失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    @Event(R.id.back)
    private void back(View v){
        finish();
    }
}
