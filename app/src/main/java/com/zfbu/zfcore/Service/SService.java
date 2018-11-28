package com.zfbu.zfcore.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.OldUtil.EncipherInfo;
import com.zfbu.zfcore.OldUtil.HttpUtils;
import com.zfbu.zfcore.OldUtil.JTUtils;
import com.zfbu.zfcore.OldUtil.PostInfo;
import com.zfbu.zfcore.OldUtil.PostPayInfoBiz;
import com.zfbu.zfcore.OldUtil.ResponseInfo;
import com.zfbu.zfcore.OldUtil.encryption.RsaUtils;
import com.zfbu.zfcore.ProData.OrderDataVar;
import com.zfbu.zfcore.ProData.ServiceMsgData;
import com.zfbu.zfcore.R;
import com.zfbu.zfcore.UI.HelloActivity;
import com.zfbu.zfcore.Util.Core;
import com.zfbu.zfcore.Util.OrderSu;
import com.zfbu.zfcore.Util.UserFunc;
import com.zfbu.zfcore.Util.ZFLog;
import com.zfbu.zfcore.threadPool.ThreadPoolFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SService extends Service {
    boolean isSet = false; //是否赋值
    HBHandler hbHandler = new HBHandler();  //线程
    Messenger messenger2client = null; //全局消息管理.
    Message msg2client = null; //全局消息
    Messenger mMessenger = new Messenger(hbHandler);

    boolean threadSleep = false;//是否执行
    boolean threadRun = true;//是否循环
    PostInfo postInfo = new PostInfo();
    EncipherInfo encipherInfo = new EncipherInfo();

    OrderSu orderSu = null;

    private MyRunnable myRunnable = new MyRunnable();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ZFLog.i("有一个新的连接请求");
        //这里获取下数据
        if (String.valueOf(intent.getExtras().get("unit_pwd")).equals("look")) {
            return mMessenger.getBinder();
        } else {
            return null;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ZFLog.i("关闭了个连接");
        if (Config.proType == 2) { //开启保护的过程中断开
            Config.proType = 0;//断开
            if (isSet) {
                isSet = false;
            }
            hbHandler.sendEmptyMessage(1); //重新连接
        } else if (Config.proType == 3) { //等待关闭
            Config.proType = 0;
           /* messenger2client = null;
            msg2client = null;*/
            stopSelf();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ZFLog.i("服务启动");

        Bitmap LargeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        //1.从系统服务中获得通知管理器
        NotificationManager esManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(getPackageName(),
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            esManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(this, HelloActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pi = PendingIntent.getActivity(SService.this, 100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder myBuilder = new Notification.Builder(SService.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8.0适配
            myBuilder.setChannelId(getPackageName());
        }
        myBuilder.setContentTitle("支付系统监控端")
                .setContentText("开始运行在后台")
                .setSubText("点击呼出操作界面")
                .setTicker("开始保持后台在线")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(LargeBitmap)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setWhen(Core.getStamp())
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myBuilder.setVisibility(Notification.VISIBILITY_PRIVATE);
        }
        Notification esNotification = myBuilder.build();
        esManager.notify(1, esNotification);
        startForeground(1, esNotification);  //设置权限

        orderSu = new OrderSu(SService.this);
        ThreadPoolFactory.getExecutorService().execute(new DataLoop());//开始数据
//        new Thread(new DataLoop()).start();//开始数据
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ZFLog.i("服务的onStartCommand被触发");
        Object object = intent.getSerializableExtra("intentData");
        if (object instanceof ServiceMsgData) {
            final ServiceMsgData hbData = (ServiceMsgData) object;
            ZFLog.i("服务的onStartCommand收到的数据: " + hbData.getWhat());
            switch (hbData.getWhat()) {
                case 1: //启动
                    Config.serviceIsOpen = true;
                    threadSleep = true;
                    break;
                case 2://关闭
                    if (Config.proType != 0) {
                        Config.proType = 3;//等待关闭
                        ZFLog.i("发送一次关闭操作");
                        sendMsg2Unit(1, "key", "kill");
                        //Core.execRootCmd("am force-stop com.a.alipaytool \r\n");//强制关闭
                    } else {
                        Config.proType = 0;
                        stopSelf();
                    }
                    break;
                case 3://告知启动成功
                    ZFLog.ToastMsg(SService.this, "开始监控");
                    UserFunc.setAppTime(SService.this, true);
                    UserFunc.setAppKillTime(SService.this, true);
                    break;
                case 4://提交订单
                    List<OrderDataVar> aa; //测试用
                    aa = orderSu.fuckIt(); //测试用
                    if (aa != null && aa.size() > 0) {
                        final JSONArray rtJson = new JSONArray();
                        JSONObject jsonObject;
                        StringBuilder sss = new StringBuilder();
                        for (int i = 0; i < aa.size(); i++) {
                            ZFLog.i("金额:" + aa.get(i).getMoney());
                            ZFLog.i("备注:" + aa.get(i).getInfo());
                            ZFLog.i("订单号:" + aa.get(i).getOrderId());
                            ZFLog.i("支付人:" + aa.get(i).getPayuser());
                            ZFLog.i("支付时间:" + aa.get(i).getTime());
                            ZFLog.i("支付时间戳:" + aa.get(i).getGmtCreate());
                            sss.append("金额:").append(aa.get(i).getMoney())
                                    .append("\n备注:").append(aa.get(i).getInfo())
                                    .append("\n订单号:").append(aa.get(i).getOrderId())
                                    .append("\n支付人:").append(aa.get(i).getPayuser())
                                    .append("\n支付时间:").append(aa.get(i).getTime())
                                    .append("\n支付时间戳:").append(aa.get(i).getGmtCreate());
                            jsonObject = new JSONObject();
                            try {

                                postInfo.setAccount(Config.proceedsZFBNum);
//                                postInfo.setUuid(JTUtils.getIdentity(getApplicationContext()));
                                postInfo.setUuid(aa.get(i).getInfo());
                                postInfo.setAmount(new BigDecimal(aa.get(i).getMoney().replace(".","")));
                                postInfo.setOrganizationCode(Config.businessNum);//需要判断是否是登录,也就是是否有商户信息,如果没有则不post信息
                                postInfo.setPayType(2);//支付类型:2  支付宝
                                postInfo.setPayTypeName("WX");
                                String reqTime=stampToDate(aa.get(i).getGmtCreate());
                                postInfo.setRequestTime(reqTime);


                                Gson gson = new Gson();
                                String route = gson.toJson(postInfo);
                                //加密
                                String encipherData= RsaUtils.encipher(route);
//                                if (encipherData == null){
//                                    return;
//                                }
                                //签名
                                String signData = RsaUtils.MD5(route);
//                                EncipherInfo encipherInfo = new EncipherInfo();
                                encipherInfo.setData(encipherData);
                                encipherInfo.setSign(signData);
                                encipherInfo.setMerNo(postInfo.getOrganizationCode());
                                encipherInfo.setVersion("2.0.0");




                                jsonObject.put("money", aa.get(i).getMoney());
                                jsonObject.put("payname", aa.get(i).getPayuser());
                                jsonObject.put("paytime", aa.get(i).getGmtCreate().substring(0, 10));
                                jsonObject.put("note", aa.get(i).getInfo());
                                jsonObject.put("aliorderid", aa.get(i).getOrderId());
                                rtJson.put(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            UserFunc.setAppData(SService.this, aa.get(i).getMoney()
                                    , 1, aa.get(i).getInfo(), aa.get(i).getOrderId());//置数据
                            final String tmpA = aa.get(i).getMoney();
                            final String tmpB = aa.get(i).getGmtCreate().substring(0, 10);
                            ZFLog.i("提交订单: paytime:" + tmpB + "  money:" + tmpA+" 订单号:"+aa.get(i).getOrderId());
                            ThreadPoolFactory.getExecutorService().execute(myRunnable);
                        }
                        ZFLog.i(sss.toString());
                        ZFLog.ToastMsg(SService.this, sss.toString());
                    }
                    break;
                case 5://有新二维码
                    File path = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
                    final File[] files = path.listFiles();//列出该目录下所有文件和文件夹
                    File tmpFile = files[0];
                    for (int i = 1; i < files.length; i++) {
                        if (files[i].lastModified() > tmpFile.lastModified()) {
                            tmpFile = files[i];
                        }
                    }
                    ZFLog.i("最新文件" + tmpFile.getName());
                    final String tmpFilePath = tmpFile.getPath();

                    break;
                case 6://二维码
                    sendMsg2AS(1);
                    break;
                case 7://监控
                    //.....
                    break;
                case 8://重启支付宝
                    hbHandler.sendEmptyMessage(3);//重启支付宝
                    break;
                case 9://打开二维码页面

                    break;

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    void sendMsg2AS(int msgWhat) {
        sendMsg2AS(msgWhat, null);
    }

    void sendMsg2AS(int msgWhat, String msgSend) {
        Intent serviceIntent = new Intent(SService.this, AService.class);
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

    void sendMsg2Unit(int what, String keyStr, String valueStr) {
        if (msg2client == null) {
            return;
        }
        msg2client.what = what;
        Bundle bd = new Bundle();
        bd.putString(keyStr, valueStr);
        msg2client.setData(bd);
        try {
            if (messenger2client != null) {
                messenger2client.send(msg2client);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Config.serviceIsOpen = false;
        threadSleep = false;
        threadRun = false;
        stopForeground(true); //把通知栏关闭
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    private class HBHandler extends Handler {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ZFLog.i("服务's handler收到请求:" + msg.what);
            if (!isSet && msg.what != 1000 && msg.what != 3) { //还没赋值
                if (msg.replyTo != null) {//接收一次通讯后方可赋值
                    isSet = true;
                    ZFLog.i("赋值一次..");
                    messenger2client = msg.replyTo; //全局赋值
                    msg2client = Message.obtain(msg);//返回给客户端的消息
                }
            }
            switch (msg.what) {
                case 1: //跟保护程序连接
                    ZFLog.i("开启跟与保护程序对接");
                    Config.proType = 1; //等待开启
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName("com.a.alipaytool", "com.a.alipaytool.Su"));
                    serviceIntent.putExtra("key", "look");
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        startForegroundService(serviceIntent);
                    } else {
                        startService(serviceIntent);
                    }
                    break;
                case 2://收到通知,连接成功
                    if (Objects.equals(msg.getData().getString("key"), "sersuccess")) {
                        ZFLog.i("连接成功");
                        Config.proType = 2;//开启保护
                        if (!Config.serviceIsOpen) { //服务未开
                            if (Core.getRunCache(SService.this)) { //如果是重启的
                                Core.ESOpenApp(getPackageName(), SService.this);//启动
                            }
                        }
                    }

                    break;
                case 3://重启支付宝
                    UserFunc.setAppKillTime(SService.this, true);//修改kill时间
                    if (Config.rootState == 2) {
                        Core.execRootCmd("am force-stop com.eg.android.AlipayGphone \r\n");
                    }
                    Core.ESOpenApp("com.eg.android.AlipayGphone", SService.this);
                    sendMsg2AS(2);//点击一次返回键
                    break;

                case 100:
                    //sendMsg2Unit(2, "canuse", String.valueOf(hbKey));//发送给辅助
                    break;

                case 1000:
                    ZFLog.ToastMsg(SService.this, "新订单推送失败");
                    break;
                case 10001:

                    break;
                default:
            }
        }
    }

    private class DataLoop implements Runnable {

        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            ZFLog.i("开始服务线程");
            int a = 0;
            do {
                a++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (a % 5 == 0) {
                    //sendMsg2AS(3);//测试用,看是否存活...
                }

                if (threadSleep) {
                    if (Config.serviceIsOpen && (Config.helpIsOpen || Config.controlIsOpen) && Config.isOpenControl && Config.restState) {//运行中
                        String tmpStr = Core.getPreferences_string(SService.this, Config.userName, "killtime");
                        if (!tmpStr.equals("")) {
                            //现在时间-启动时间>30分钟
                            if ((Integer.valueOf(Core.getTenStamp(Core.getStamp())) - Integer.valueOf(tmpStr)) > 60 * 30) {
                                hbHandler.sendEmptyMessage(3);//重启支付宝
                            }
                        }
                        if (Config.proState && Config.proType == 0) { //如果开启保护,暂未开启
                            hbHandler.sendEmptyMessage(1);//k开始连接
                        }
                    }
                }
            } while (threadRun);
            ZFLog.i("关闭线程");
        }
    }
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


   private class MyRunnable implements  Runnable{
        private volatile long  retryCount = 0;

       @Override
       public void run() {
           //演示用
//         ConnectServer connectServer = new ConnectServer();
//         connectServer.submit(tmpB, tmpA);
           requestNet();
       }

       private void requestNet() {
           Gson gson = new Gson();
//           String route = gson.toJson(postInfo);
           PostPayInfoBiz postPayInfoBiz = HttpUtils.retrofit.create(PostPayInfoBiz.class);
           RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), gson.toJson(encipherInfo));
           Call<ResponseInfo> postInfoCall = postPayInfoBiz.getPostPayInfo(body);
           postInfoCall.enqueue(new Callback<ResponseInfo>() {
               @Override
               public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                   if (response.body() == null) {
                       return;
                   }
                   if (!JTUtils.isEmpty(response.body().getStatus())) {
                       if (JTUtils.isResponseSuccess(response.body().getStatus())) {
                           //成功
                       } else {
                           //重发
                           synchronized (this){
                               if(retryCount > 1000){
                                   retryCount = 0;
                               }
                               retryCount ++;
                               if(retryCount % 5 != 0){
                                   requestNet();
                               }
                           }
                       }
                   }
               }
               @Override
               public void onFailure(Call<ResponseInfo> call, Throwable t) {
                   synchronized (this){
                       if(retryCount > 1000){
                           retryCount = 0;
                       }
                       retryCount ++;
                       if(retryCount % 5 != 0){
                           requestNet();
                       }
                   }
               }
           });

       }
   }
}
