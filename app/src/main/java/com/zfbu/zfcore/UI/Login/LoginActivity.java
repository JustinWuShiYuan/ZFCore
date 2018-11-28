package com.zfbu.zfcore.UI.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.google.gson.Gson;
import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.OldUtil.EncipherInfo;
import com.zfbu.zfcore.OldUtil.HttpUtils;
import com.zfbu.zfcore.OldUtil.JTUtils;
import com.zfbu.zfcore.OldUtil.SPUtil;
import com.zfbu.zfcore.OldUtil.encryption.RsaUtils;
import com.zfbu.zfcore.R;
import com.zfbu.zfcore.UI.MainActivity;
import com.zfbu.zfcore.Util.ConnectServer;
import com.zfbu.zfcore.Util.Core;
import com.zfbu.zfcore.Util.ZFLog;
import com.zfbu.zfcore.lemonDialog.lemonbubble.LemonBubble;
import com.zfbu.zfcore.lemonDialog.lemonbubble.enums.LemonBubbleLayoutStyle;
import com.zfbu.zfcore.lemonDialog.lemonbubble.enums.LemonBubbleLocationStyle;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {
    boolean hasRe = false;
    ImageView savepwd;
    Button logBtn;
//    EditText userEdit;
//    EditText pwdEdit;
    public HBHandler hbHandler = new HBHandler();  //线程
    private EditText userEdit;
    private EditText pwdEdit;
    private EditText ZFB;
    private String ZFBTest;
    private String userNameText;
    private String passwordText;

    @SuppressLint("HandlerLeak")
    private class HBHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1: //登录成功
                    LemonBubble.showRight(LoginActivity.this, "登录成功", 2000);
                    if (hasRe) {//如果保存
                        Core.modResUser(LoginActivity.this, userEdit.getText().toString(), pwdEdit.getText().toString());
                    } else {
                        Core.modResUserNo(LoginActivity.this);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2500);//比提示框多500毫秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            hbHandler.sendEmptyMessage(3);
                        }
                    }).start();
                    break;
                case 2://登录失败
                    LemonBubble.showError(LoginActivity.this, "登录失败", 2000);
                    break;
                case 3://登录成功后续
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        savepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasRe) {
                    savepwd.setImageResource(R.drawable.btn_check_on);
                    hasRe = true;
                } else {
                    savepwd.setImageResource(R.drawable.btn_check_off);
                    hasRe = false;
                }
            }
        });


        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userEdit.getText().toString().equals("") || pwdEdit.getText().toString().equals("")) {
                    LemonBubble.showError(LoginActivity.this, "帐号或密码未填写", 2000);
                } else {
                    LemonBubble.getRoundProgressBubbleInfo()
                            .setLocationStyle(LemonBubbleLocationStyle.BOTTOM)
                            .setLayoutStyle(LemonBubbleLayoutStyle.ICON_LEFT_TITLE_RIGHT)
                            .setBubbleSize(200, 50)
                            .setProportionOfDeviation(0.1f)
                            .setTitle("正在请求服务器...")
                            .show(LoginActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectServer connectServer = new ConnectServer();
                            login();
                        }
                    }).start();

                }
            }
        });
    }
    private void login() {

        LoginInfo loginInfo = new LoginInfo();
        ZFBTest = ZFB.getText().toString();
        userNameText = userEdit.getText().toString();
        passwordText = pwdEdit.getText().toString();

        //保存支付宝账号到本地
        SPUtil.put(SPUtil.ZFB, ZFBTest);
        loginInfo.setZFB(ZFBTest);
        loginInfo.setPassword(passwordText);
        loginInfo.setLonginName(userNameText);
        final Gson gson = new Gson();

        String route = gson.toJson(loginInfo);
        //加密
        String encipherData= RsaUtils.encipher(route);
        if (encipherData == null){
            return;
        }
        //签名
        String signData = RsaUtils.MD5SignIn(route);
        EncipherInfo encipherInfo = new EncipherInfo();
        encipherInfo.setData(encipherData);
        encipherInfo.setData(encipherData);
        encipherInfo.setSign(signData);

        LoginBiz loginBiz = HttpUtils.retrofit.create(LoginBiz.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), gson.toJson(encipherInfo));

        Call<LoginResponseInfo> call = loginBiz.login(body);
        call.enqueue(new Callback<LoginResponseInfo>() {
            @Override
            public void onResponse(Call <LoginResponseInfo> call, Response<LoginResponseInfo> response) {
                if (response.body() == null){
                    return;
                }
                String decryptString = RsaUtils.dencipher(response.body().getData());
                LoginResponseData loginResponseData = gson.fromJson(decryptString, LoginResponseData.class);
                String remoteSign = response.body().getSign();
                String localSign = RsaUtils.MD5SignIn(decryptString);
                if (!JTUtils.isEmpty(response.body().getStatus()) && !JTUtils.isEmpty(remoteSign)) {
                    if (JTUtils.isResponseSuccess(response.body().getStatus()) && remoteSign.equals(localSign)) {
                        //收款支付宝
                        Config.proceedsZFBNum = ZFBTest;
                        //商户号
                        Config.businessNum = loginResponseData.getOrgCode();

                        //登录成功后将相应数据保存至本地
                        SPUtil.put(SPUtil.ORG_CODE, loginResponseData.getOrgCode());
                        SPUtil.put(SPUtil.LOGIN_NAME, userEdit.getText().toString());
                        SPUtil.put(SPUtil.ZFB,ZFBTest);

                        //进入首页
                        hbHandler.sendEmptyMessage(1);

                    } else {
                        hbHandler.sendEmptyMessage(2);//失败
                        ZFLog.ToastMsg(getApplicationContext(), "当前功能暂未开启");

//                        Toast.makeText(SignInActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();//根据状态码判断是请求超时还是用户名或者密码输入错误,并给出相应的提示
                    }
                }
                else {
//                    Toast.makeText(SignInActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();//根据状态码判断是请求超时还是用户名或者密码输入错误,并给出相应的提示
//                    SPUtil.put(SPUtil.ORG_CODE, loginResponseData.getOrgCode());
//                    SPUtil.put(SPUtil.LOGIN_NAME, loginName.getText().toString());
//                    gotoHomeActivity();
                    hbHandler.sendEmptyMessage(2);//失败
                    ZFLog.ToastMsg(getApplicationContext(), "当前功能暂未开启");


                }

            }

            @Override
            public void onFailure(Call <LoginResponseInfo> call, Throwable t) {

            }
        });
    }
    public void init() {
        userEdit = findViewById(R.id.login_edit_account);
        pwdEdit = findViewById(R.id.login_edit_pwd);
        ZFB = findViewById(R.id.login_edit_zfb);
        savepwd = findViewById(R.id.login_cb_savepwd);
        logBtn = findViewById(R.id.login_btn_login);
        List<String> userData = Core.hasReUser(LoginActivity.this);
        if (userData != null) {    //已经保存帐号
            savepwd.setImageResource(R.drawable.btn_check_on);
            hasRe = true;
            userEdit.setText(userData.get(0));
            pwdEdit.setText(userData.get(1));
        }
    }

}
