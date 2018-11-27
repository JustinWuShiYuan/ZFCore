package com.zfbu.zfcore.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.zfbu.zfcore.R;
import com.zfbu.zfcore.UI.Login.LoginActivity;
import com.zfbu.zfcore.Util.ConnectServer;
import com.zfbu.zfcore.Util.Core;
import com.zfbu.zfcore.Util.UserFunc;

import java.util.List;

public class HelloActivity extends AppCompatActivity {
    public HBHandler hbHandler = new HBHandler();  //线程

    @SuppressLint("HandlerLeak")
    private class HBHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent;
            switch (msg.what) {
                case 1: //app首页
                    intent = new Intent(HelloActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case 2://登录页面
                    intent = new Intent(HelloActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
                default:
                    intent = new Intent(HelloActivity.this, LoginActivity.class);
                    startActivity(intent);
            }
            HelloActivity.this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (UserFunc.isLogin()) {
                    hbHandler.sendEmptyMessage(1);//进入登录页面
                } else {
                    List<String> userData = Core.hasReUser(HelloActivity.this);
                    if (userData != null) {    //已经保存帐号
                        ConnectServer connectServer = new ConnectServer();
                        if (connectServer.login(userData.get(0), userData.get(1))) {//如果登录成功
                            hbHandler.sendEmptyMessage(1);//进入首页
                        } else {
                            hbHandler.sendEmptyMessage(2);//进入登录页面
                        }
                    } else {    //没有保存帐号
                        hbHandler.sendEmptyMessage(2);//进入登录页面
                    }
                }
            }
        }).start();
    }
}
