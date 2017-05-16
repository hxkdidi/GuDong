package cn.test.gudong.game;

import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.test.gudong.BasicActivity;
import cn.test.gudong.R;
import cn.test.gudong.sign.LoginA;

@ContentView(R.layout.activity_game_detail)
public class GameDetailActivity extends BasicActivity {

    @ViewInject(R.id.web_view)
    WebView webView;

    @ViewInject(R.id.join)
    Button join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game_detail);
        String url = getIntent().getStringExtra("url");
        Log.e("jhd", url);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

// 这行代码一定加上否则效果不会出现
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Event(R.id.join)
    private void join(View v){
        RequestParams params=new RequestParams("");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JsonObject jo = new JsonParser().parse(result).getAsJsonObject();
                boolean isSuc1 = jo.get("isSuc").getAsBoolean();
                if(isSuc1){
                    Toast.makeText(GameDetailActivity.this,"报名成功",Toast.LENGTH_SHORT).show();
                    //((Button)v).setText("已报名");
                    join.setText("已报名");
                    join.setEnabled(false);
                }else{
                    Toast.makeText(GameDetailActivity.this,"报名失败",Toast.LENGTH_SHORT).show();
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

}
