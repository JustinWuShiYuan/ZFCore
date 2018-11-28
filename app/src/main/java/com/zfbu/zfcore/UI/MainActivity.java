package com.zfbu.zfcore.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.OldUtil.SPUtil;
import com.zfbu.zfcore.ProData.ServiceMsgData;
import com.zfbu.zfcore.R;
import com.zfbu.zfcore.Service.NCService;
import com.zfbu.zfcore.Service.SService;
import com.zfbu.zfcore.UI.Login.LoginActivity;
import com.zfbu.zfcore.Util.Core;
import com.zfbu.zfcore.Util.InstallUtils;
import com.zfbu.zfcore.Util.NServiceMake;
import com.zfbu.zfcore.Util.SetPageLayoutItem;
import com.zfbu.zfcore.Util.UserFunc;
import com.zfbu.zfcore.Util.ZFLog;
import com.zfbu.zfcore.lemonDialog.lemonbubble.LemonBubble;
import com.zfbu.zfcore.lemonDialog.lemonbubble.enums.LemonBubbleLayoutStyle;
import com.zfbu.zfcore.lemonDialog.lemonbubble.enums.LemonBubbleLocationStyle;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHello;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloAction;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloInfo;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloView;
import com.zfbu.zfcore.lemonDialog.lemonhello.adapter.LemonHelloEventDelegateAdapter;
import com.zfbu.zfcore.lemonDialog.lemonhello.interfaces.LemonHelloActionDelegate;
import com.zfbu.zfcore.threadPool.ThreadPoolFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;


@SuppressLint("Registered")
public class MainActivity extends Activity {
    public HBHandler hbHandler = new HBHandler();
    private static  final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 333; //6.0文件读取授权的动态回调

//  TextView outLogText;
    TextView userText;
    Button btnOpenControl, btnBillHistoryRecord;
    TextView appText1, appText2, appText3;

    boolean isFor = false;          //是否轮询数据库
    boolean firstOpen = true;
    boolean threadSleep = false;    //是否执行
    boolean threadRun = true;       //是否循环

    private SetPageLayoutItem rootCheck;
    private SetPageLayoutItem restartZFBCheck;
    private SetPageLayoutItem protectCodeCheck;
    private Button btnSecretKey;
    private MyRunnable        myRunnable;


    //重新绑定通知获取权限
    public static void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NCService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NCService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //退出登录
//        outLogText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LemonHello.getInformationHello("您确定要注销吗？", "注销登录后您将无法接收到当前用户的所有推送消息。")
//                        .addAction(new LemonHelloAction("取消", new LemonHelloActionDelegate() {
//                            @Override
//                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
//                                helloView.hide();
//                            }
//                        }))
//                        .addAction(new LemonHelloAction("我要注销", Color.RED, new LemonHelloActionDelegate() {
//                            @Override
//                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
//                                helloView.hide();
//                                showLoad("正在请求服务器...");
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        LemonBubble.showRight(MainActivity.this, "注销成功，欢迎您下次登录", 2000);
//                                        userText.setText("账户:(当前未登陆)");
//                                        UserFunc.outLogin(MainActivity.this);//退出登录
//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    Thread.sleep(2500);
//                                                } catch (InterruptedException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                hbHandler.sendEmptyMessage(1);//退出登录
//                                            }
//                                        }).start();
//                                    }
//                                }, 1500);
//                            }
//                        }))
//                        .show(MainActivity.this);
//            }
//        });

        //开启监控
        btnOpenControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Config.isOpenControl) { //如果未开启监控
                    isFor = true;
                    showLoad("开始处理...");//  LemonBubble.hide();
                    firstStart();
                } else {//已经开启,现在关闭
                    if (Config.proState) {//保护程序
                        Core.setRunCache(MainActivity.this, null);//清空
                    }
                    isFor = false;
                    btnOpenControl.setText("开启监控");
                    sendMsg2SS(2);//关闭
                    UserFunc.setAppTime(MainActivity.this, false);
                    UserFunc.setAppKillTime(MainActivity.this, false);
                    NServiceMake nServiceMake = new NServiceMake(getContentResolver());
                    nServiceMake.stop();
                    LemonBubble.showRight(MainActivity.this, "停止监控成功", 2000);
                    Config.isOpenControl = false;
                }
            }
        });


        //历史记录
        btnBillHistoryRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZFLog.i("打开订单列表页面");
                startActivity(new Intent(MainActivity.this, OrderListActivity.class));
            }
        });

        rootCheck.setmOnLSettingItemClick(new SetPageLayoutItem.OnLSettingItemClick() {
            @Override
            public void click() {
                LemonHello.getWarningHello("检测ROOT", "即将开始检测ROOT权限")
                        .setContentFontSize(14)
                        .addAction(new LemonHelloAction("我知道啦", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                                showLoad("正在检测...");
                                firstGetRoot();
                            }
                        }))
                        .show(MainActivity.this);
            }
        });

        restartZFBCheck.setmOnLSettingItemClick(new SetPageLayoutItem.OnLSettingItemClick() {
            @Override
            public void click() {
                if (Config.rootState != 2) { //没有检测通过
                    showError("当前功能仅在root下执行");
                } else { //
                    ZFLog.i("点击重启按钮");
                    /*Config.restState = !Config.restState;
                    Core.setRestState(MainActivity.this, Config.restState);//写缓存
                    restartZFBCheck.switchThis();//按钮切换*/
                    ZFLog.ToastMsg(MainActivity.this, "当前功能暂未开启");
                }
            }
        });

        protectCodeCheck.setmOnLSettingItemClick(new SetPageLayoutItem.OnLSettingItemClick() {
            @Override
            public void click() {
                if (Config.rootState != 2) {
                    showError("当前功能仅在root下执行");
                } else {//已经给root
                   /* if (!Core.isAppInstalled(MainActivity.this, "com.a.alipaytool")) { //如果未安装
                        LemonHelloInfo bookMarkInfo = new LemonHelloInfo();
                        bookMarkInfo.setTitle("未安装保护程序")
                                .setContent("本机暂未安装保护程序,点击确定键将联网下载并自动安装," +
                                        "也可点击取消键,手动安装后再回来开启本设置.")
                                .setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.appscan));
                        bookMarkInfo.addAction(new LemonHelloAction("确定",
                                new LemonHelloActionDelegate() {
                                    @Override
                                    public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                        helloView.hide();
                                        hbHandler.sendEmptyMessage(1000);//下载
                                    }
                                }));
                        bookMarkInfo.addAction(new LemonHelloAction("取消", Color.RED, new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }));
                        bookMarkInfo.show(MainActivity.this);
                    } else {//已经安装
                        ZFLog.i("执行一次保护程序的状态修改");
                        hbHandler.sendEmptyMessage(1003);
                    }*/
                    ZFLog.ToastMsg(MainActivity.this, "当前功能暂未开启");
                }
            }
        });

        userText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showLoad("正在请求服务器..."); //这里做对接平台的网络数据初始化
                ZFLog.ToastMsg(MainActivity.this, "当前功能暂未开启");
            }
        });
        btnSecretKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(MainActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("綁定APP秘钥")
                        .setIcon(R.drawable.logo)
                        .setView(et)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                if (!input.equals("")){
                                    //发起请求,匹配后台

                                    requistMd5(input);

                                }
                            }
                        })
                        .setNegativeButton("取消",null).show();
            }
        });

    }

    public void showError(String msg) {
        Message hbMsg = new Message();
        hbMsg.what = 4; //错误消息
        hbMsg.obj = msg;
        hbHandler.sendMessage(hbMsg);
    }

    public void showLoad(String msg) {
        LemonBubble.getRoundProgressBubbleInfo()
                .setLocationStyle(LemonBubbleLocationStyle.BOTTOM)
                .setLayoutStyle(LemonBubbleLayoutStyle.ICON_LEFT_TITLE_RIGHT)
                .setBubbleSize(200, 50)
                .setProportionOfDeviation(0.1f)
                .setTitle(msg)
                .show(MainActivity.this);
    }

    boolean installApk() {
        Core.execRootCmd("chmod 777 /data/data/" + getPackageName() + "/cache/alipaytool.apk"); //单文件给权限
        String tempStr = Core.execRootCmd("pm install /data/data/" + getPackageName() + "/cache/alipaytool.apk");  //需要给777权限
        if (tempStr.equals("Success")) { //如果安装成功
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        toggleNotificationListenerService(MainActivity.this);
        userText = findViewById(R.id.fragment1_text_msg_title);//用户名称
//        outLogText = findViewById(R.id.outLog);//退出按钮
        btnOpenControl = findViewById(R.id.btnOpenControl);//开启监控按钮
        if (!Config.isOpenControl) {
            btnOpenControl.setText("开启监控");

        } else {
            btnOpenControl.setText("关闭监控");
        }
        btnBillHistoryRecord = findViewById(R.id.btnHistoryRecord);//历史订单记录按钮
        rootCheck = findViewById(R.id.rootState);//root选项
        rootCheck.setCanCheck(false);//禁止点击
        restartZFBCheck = findViewById(R.id.isOpenRestartZFB);//支付宝重启选项
        restartZFBCheck.setCanCheck(false);//禁止点击
        protectCodeCheck = findViewById(R.id.isOpenProtectCode);
        protectCodeCheck.setCanCheck(false);//禁止点击

        appText1 = findViewById(R.id.fragment1_text1);
        appText2 = findViewById(R.id.fragment1_text2);
        appText3 = findViewById(R.id.fragment1_text3);

        btnSecretKey = findViewById(R.id.btnInputSecretKey);

        Config.readState = Core.getReadState(MainActivity.this);
        Config.rootState = Core.getRootState(MainActivity.this);
        Config.restState = Core.getRestState(MainActivity.this);
        Config.proState = Core.getProState(MainActivity.this);

        UserFunc.setAppTime(MainActivity.this, false);//清空时间
        UserFunc.setAppKillTime(MainActivity.this, false);//清空时间

//        if (UserFunc.isLogin()) {//已经登录
//            userText.setText("账户:" + Config.userName);
//        } else {
//            userText.setText("账户:(当前未登陆)");
//        }

        // TODO ?  >> userText.setText("商户号:" + Config.businessNum + "\n收款支付宝:" + Config.proceedsZFBNum);
        new Thread(){
            @Override
            public void run() {
                try {
                    this.sleep(1000);
                    userText.post(new Runnable() {
                        @Override
                        public void run() {
                            userText.setText("商户号:" + Config.businessNum + "\n收款支付宝:" + Config.proceedsZFBNum);
                        }
                    });
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }.start();

        if (Config.restState) {
            restartZFBCheck.switchThis();
        }
        if (Config.proState) {
            protectCodeCheck.switchThis();
        }

        if (Config.rootState == 2) {//如果有root权限
            rootCheck.clickThis();
        } else if (Config.rootState == 1) {//如果还未检测过的,说明第一次运行
            firstShow();
        }

        ThreadPoolFactory.getExecutorService().execute(new DataLoop());//开始数据
        if (Config.isOpenControl) {//重启重开
            hbHandler.sendEmptyMessage(7);
        }
    }

    @SuppressLint("SdCardPath")
    public void firstStart() {
        if (!Core.isAppInstalled(MainActivity.this, "com.eg.android.AlipayGphone")) { //如果未安装支付宝
            LemonBubble.hide();
            showError("未安装支付宝");
        } else {//安装支付宝
            if (Config.rootState == 2) {//Root过
                String tempStr = Core.execRootCmd("ls -al /data/data |grep com.eg.android.AlipayGphone");
                if (tempStr.equals("")) {
                    LemonBubble.hide();
                    showError("未检测到支付宝文件");
                } else {//文件存在且有属性
                    if (!tempStr.substring(0, 10).equals("drwxrwxrwx")) { //如果不是777权限
                        Core.execRootCmd("chmod -R 777 /data/data/com.eg.android.AlipayGphone");//设置777
                        tempStr = Core.execRootCmd("ls -al /data/data |grep com.eg.android.AlipayGphone");  //取文件属性
                        if (!tempStr.substring(0, 10).equals("drwxrwxrwx")) { //如果还不是777权限
                            LemonBubble.hide();
                            showError("文件无法授权,停止运行,请联系管理员");
                            return;
                        }
                    }
                    Config.aliUserId =  Core.getXmlStr("/data/data/com.eg.android.AlipayGphone/shared_prefs/com.alipay.android.phone.businesscommon.xml", "string","name","AdLastLoginUser");
                    if (Config.aliUserId.equals("null")) {//异常了
                        Core.execRootCmd("chmod -R 777 /data/data/com.eg.android.AlipayGphone/shared_prefs/com.alipay.android.phone.businesscommon.xml");//设置777
                        Config.aliUserId = Core.getXmlStr("/data/data/com.eg.android.AlipayGphone/shared_prefs/com.alipay.android.phone.businesscommon.xml", "string","name","AdLastLoginUser");
                        if (Config.aliUserId.equals("null")) {//异常了
                            Config.aliUserId = "";//置空
                        }
                    }
                    if (Config.aliUserId.equals("")) {//没取到
                        LemonBubble.hide();
                        showError("当前支付宝未登录");
                    } else {//成功取到支付宝id
                        hbHandler.sendEmptyMessage(6);//打开服务
                        //开始循环抓取
                        ThreadPoolFactory.getExecutorService().execute(new MyRunnable());
                    }
                }
            } else {//未有root授权
                LemonBubble.hide();
                showError("当前APP暂未有ROOT权限");
            }

        }
    }

    public void gotoNotificationAccessSetting(Context context) {//打开通知栏状态开关页面
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ZFLog.ToastMsg(context, "对不起，您的手机暂不支持");
            e.printStackTrace();
        }
    }

    public void firstShow() {
        LemonHello.getWarningHello("权限获取", "第一次运行,即将进行文件管理权限授权和ROOT权限检测,请给予永久ROOT授权")
                .setContentFontSize(14)
                .addAction(new LemonHelloAction("我知道啦", new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                        helloView.hide();
                        showLoad("正在检测....");
                        hbHandler.sendEmptyMessage(2);//检测授权
                    }
                }))
                .show(MainActivity.this);
    }

    public void firstGetRoot() {
        final String msgStr;
        if (Core.hasRootPerssion()) {//如果有root权限
            if (Config.rootState == 1 || Config.rootState == 3) { //未检测或者检测失败
                rootCheck.clickThis();
            }
            Config.rootState = 2;
            Core.setROOTState(MainActivity.this, 2);
            msgStr = "权限检测通过";
        } else {
            if (Config.rootState == 2) {//检测通过
                rootCheck.clickThis();
            }
            Config.rootState = 3;
            Core.setROOTState(MainActivity.this, 3);
            msgStr = "ROOT权限检测失败";
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LemonBubble.hide();
                LemonHello.getWarningHello("检测完毕", msgStr)
                        .setContentFontSize(14)
                        .addAction(new LemonHelloAction("我知道啦", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .setEventDelegate(new LemonHelloEventDelegateAdapter() {
                            @Override
                            public void onMaskTouch(LemonHelloView helloView, LemonHelloInfo helloInfo) {
                                super.onMaskTouch(helloView, helloInfo);
                                helloView.hide();
                            }
                        })
                        .show(MainActivity.this);
            }
        }, 1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 333: //文件读写的动态授权
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Core.setReadState(MainActivity.this, true);
                    Config.readState = true;
                    firstGetRoot();
                } else {
                    Core.setReadState(MainActivity.this, false);
                    Config.readState = false;
                }
                break;
        }
    }

    void sendMsg2SS(int msgWhat) {
        sendMsg2SS(msgWhat, null);
    }

    void sendMsg2SS(int msgWhat, String msgSend) {
        Intent serviceIntent = new Intent(MainActivity.this, SService.class);
        Bundle bundle = new Bundle();
        Object object;
        if (msgSend == null) {
            object = new ServiceMsgData(msgWhat);
        } else {
            object = new ServiceMsgData(msgWhat, msgSend);
        }
        bundle.putSerializable("intentData", (Serializable) object);
        serviceIntent.putExtras(bundle);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ZFLog.i("页面可见");
        if (Config.serviceIsOpen && Config.controlIsOpen && Config.isOpenControl && btnOpenControl.getText().toString().equals("开启监控")) {
            //开始轮询数据库
            btnOpenControl.setText("停止监控");
        }
        threadSleep = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ZFLog.i("不可见");
        threadSleep = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadRun = false;
    }

    @SuppressLint("HandlerLeak")
    private class HBHandler extends Handler {
        @SuppressLint("ObsoleteSdkInt")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: //退出登录,返回登录页面
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;
                case 2://文件授权检测
                    if (android.os.Build.VERSION.SDK_INT >= 23
                            && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {//动态申请权限  -1 没权限  0  有权限
                        //弹出授权框
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    } else {//直接授权
                        Core.setReadState(MainActivity.this, true);
                        Config.readState = true;
                        firstGetRoot();
                    }
                    break;
                case 3://显示二维码个数
                    if (msg.arg1 == 3) {
                        LemonBubble.hide();
                    }
                    if (msg.arg2 == 3) {
                        firstStart();
                    } else if (!Config.tmpAppid.equals("")) { //重启保护
                        Config.isOpenControl = true;
                        firstStart();
                    } else if (firstOpen) { //直接启动监控
                        Config.isOpenControl = true;
                        firstStart();
                    }
                    firstOpen = false;
                    break;

                case 4://错误消息
                    LemonHello.getErrorHello("发生错误", String.valueOf(msg.obj))
                            .addAction(new LemonHelloAction("关闭", new LemonHelloActionDelegate() {
                                @Override
                                public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                    helloView.hide();
                                }
                            }))
                            .setEventDelegate(new LemonHelloEventDelegateAdapter() {
                                @Override
                                public void onMaskTouch(LemonHelloView helloView, LemonHelloInfo helloInfo) {
                                    super.onMaskTouch(helloView, helloInfo);
                                    helloView.hide();
                                }
                            })
                            .show(MainActivity.this);
                    break;
                case 5://普通提示

                    break;
                case 6://打开服务

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//4.3

                        hbHandler.sendEmptyMessage(7);

                        //判读是否打开获取通知权限
//                        if (Config.controlIsOpen) {//如果已经在运行
//                            hbHandler.sendEmptyMessage(7);
//                        } else {//还没运行
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//大于8.0
//                                /*AServiceMake aServiceMake = new AServiceMake(getContentResolver());
//                                aServiceMake.startSelf(MainActivity.this);
//                                用辅助打开通知栏监控
//                                */
//                                LemonBubble.hide();
//                                showError("因安卓8.0的特殊性,这里需要进行继续优化");
//                            } else {
//
//                                NServiceMake nServiceMake = new NServiceMake(getContentResolver()); //TODO  这里逻辑有待 确认
//                                if(nServiceMake.startSelf(MainActivity.this)){
//                                    hbHandler.sendEmptyMessage(7);
//                                }else{
//                                    LemonHello.getErrorHello("当前系统原因,通知栏权限无法自动打开,请点击确定键后手动开启", String.valueOf(msg.obj))
//                                            .addAction(new LemonHelloAction("确定", new LemonHelloActionDelegate() {
//                                                @Override
//                                                public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
//                                                    helloView.hide();
//                                                    gotoNotificationAccessSetting(MainActivity.this);
//                                                }
//                                            }))
//                                            .setEventDelegate(new LemonHelloEventDelegateAdapter() {
//                                                @Override
//                                                public void onMaskTouch(LemonHelloView helloView, LemonHelloInfo helloInfo) {
//                                                    super.onMaskTouch(helloView, helloInfo);
//                                                    helloView.hide();
//                                                }
//                                            })
//                                            .show(MainActivity.this);
//                                }
//                            }
//                        }
                    } else {//版本过低
                        LemonBubble.hide();
                        showError("当前设备版本过低");
                    }
                    break;
                case 7://最后一步的启动
                    LemonBubble.showRight(MainActivity.this, "开始监控", 2000);
                    UserFunc.setAppTime(MainActivity.this, true);
                    UserFunc.setAppKillTime(MainActivity.this, true);
                    btnOpenControl.setText("停止监控");
                    sendMsg2SS(1);
                    Config.isOpenControl = true;
                    break;
                case 10: //显示APP内容
                    String tmpStr;
                    tmpStr = UserFunc.getData1(MainActivity.this);
                    if (!appText1.getText().toString().equals(tmpStr)) {
                        appText1.setText(tmpStr);
                    }

                    tmpStr = UserFunc.getData2(MainActivity.this);
                    if (!appText2.getText().toString().equals(tmpStr)) {
                        appText2.setText(tmpStr);
                    }
                    tmpStr = UserFunc.getData3(MainActivity.this);
                    if (!appText3.getText().toString().equals(tmpStr)) {
                        appText3.setText(tmpStr);
                    }
                    break;
                case 1000://执行监控程序下载
                    showLoad("正在下载并处理...");
                    new InstallUtils(MainActivity.this, "http://www.zuuu.com/down.php", "alipaytool.apk",
                            Core.getCachePath(MainActivity.this, 1), new InstallUtils.DownloadCallBack() {
                        @Override
                        public void onStart() {
                            ZFLog.i("开始下载");
                            Message hbMsg = new Message();
                            hbMsg.what = 1002;//显示下载进度
                            hbMsg.arg1 = 0;
                            hbHandler.sendMessage(hbMsg);
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(String path) {
                            ZFLog.i("下载完毕:" + path);
                            Message hbMsg = new Message();
                            hbMsg.what = 1002;//显示下载进度
                            hbMsg.arg1 = 100;
                            hbHandler.sendMessage(hbMsg);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000); //延迟1秒, 让设备反应过来
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    hbHandler.sendEmptyMessage(1001);//下载完毕安装apk文件
                                }
                            }).start();
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onLoading(long total, long current) {
                            Message hbMsg = new Message();
                            hbMsg.what = 1002;//显示下载进度
                            hbMsg.arg1 = (int) (current * 100 / total);
                            hbHandler.sendMessage(hbMsg);
                            ZFLog.i("正在下载中,文件总长度:" + total + ",当前长度:" + current + "   当前进度:" + ((int) (current * 100 / total)) + "%");
                        }

                        @Override
                        public void onFail(Exception e) {
                            ZFLog.i("下载失败:" + e.getMessage());
                            LemonBubble.hide();
                            showError("下载失败,请联系管理员");
                            Message hbMsg = new Message();
                            hbMsg.what = 1002;//显示下载进度
                            hbMsg.arg1 = 100;
                            hbHandler.sendMessage(hbMsg);
                        }

                    }).downloadAPK();
                    break;
                case 1001://安装
                    File file = new File(Core.getCachePath(MainActivity.this, 1) + "/alipaytool.apk"); //默认的辅助插件安装包是否存在
                    if (!file.exists()) { //文件不存在
                        LemonBubble.hide();
                        showError("文件下载出现问题");
                    } else {//已经有文件了
                        boolean a = installApk();//安装程序
                        LemonBubble.hide();
                        if (a) {
                            ZFLog.i("安装成功");
                            hbHandler.sendEmptyMessage(1003);
                        } else {
                            showError("安装失败,联系管理员");
                        }
                    }
                    break;
                case 1002://显示下载进度
                    if (msg.arg1 != 100) {
                        protectCodeCheck.setLeftText("是否启用保护程序(" + msg.arg1 + "%)");
                    } else {
                        protectCodeCheck.setLeftText("是否启用保护程序");
                    }
                    break;
                case 1003://监控启动
                    Config.proState = !Config.proState;
                    Core.setProState(MainActivity.this, Config.proState);//写缓存
                    protectCodeCheck.switchThis();//按钮切换
                    break;
            }

        }
    }

    private class DataLoop implements Runnable {

        @Override
        public void run() {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (threadSleep) {
                        hbHandler.sendEmptyMessage(10);
                    }
                }
            },1000,2000);
        }
    }

    //点击绑定md5按钮点击事件
    private class MD5BtnClick implements View.OnClickListener{
        public void onClick(View view) {
            final EditText et = new EditText(MainActivity.this);
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("綁定APP秘钥")
                    .setIcon(R.drawable.logo)
                    .setView(et)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            if (!input.equals("")){
                                //发起请求,匹配后台

                                requistMd5(input);

                            }
                        }
                    })
                    .setNegativeButton("取消",null).show();
        }
    }
    //匹配MD5请求
    private void requistMd5(final String MD5Key){

//        String url = "http://192.168.23.207:8555/queryNotifyKey?mer_code=Mer1533383955740Rm3&notify_key=ABCDEFG10086";

        String url = "http://callback.65011688.com/queryNotifyKey?mer_code="+Config.businessNum +"&notify_key=" + MD5Key;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //失败
//                Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                btnSecretKey.post(new Runnable() {
                    @Override
                    public void run() {
                        ZFLog.ToastMsg(MainActivity.this, "匹配失败");
                    }
                });
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                //响应
                if (response.body() == null) {
                    return;
                }
                Gson gson=new Gson();
                LinkedHashMap linkedHashMap =  gson.fromJson(response.body().charStream(), LinkedHashMap.class);
                String status=(String) linkedHashMap.get("status");
                if (status.equals("200")){
                    //密钥校验成功
                    btnSecretKey.post(new Runnable() {
                        @Override
                        public void run() {
                            //保存本地
                            SPUtil.put(SPUtil.MD5_KEY,MD5Key);
                            String str = "App秘钥: " + (String) SPUtil.get(SPUtil.MD5_KEY,"");
                            btnSecretKey.setText(str);
                            ZFLog.ToastMsg(MainActivity.this, "匹配成功");

                        }
                    });
                }else {
                    //秘钥校验失败
                    btnSecretKey.post(new Runnable() {
                        @Override
                        public void run() {

                            ZFLog.ToastMsg(MainActivity.this, "匹配失败");

                        }
                    });
                }


            }
        });

    }

    class MyRunnable implements  Runnable{
        @Override
        public void run() {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isFor){
                        sendMsg2SS(4);
                    }
                }
            },1000,10000);
        }
    }
}
