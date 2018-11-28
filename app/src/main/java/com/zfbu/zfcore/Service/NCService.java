package com.zfbu.zfcore.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;


import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.ProData.ServiceMsgData;
import com.zfbu.zfcore.Util.ZFLog;

import java.io.Serializable;

@SuppressLint({"OverrideAbstract", "Registered"})
public class NCService extends NotificationListenerService {

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (Config.serviceIsOpen && Config.isOpenControl) {

            Notification n = sbn.getNotification();
            if (n == null) {
                return;
            }
            if (!sbn.getPackageName().equals("com.eg.android.AlipayGphone")) {
                return;
            }
            String title = "";
            if (n.tickerText != null) {
                title = n.tickerText.toString();
            }
            long when = n.when;
        /*Bundle bundle = null;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // android 4.3
            try {
                Field field = Notification.class.getDeclaredField("extras");
                bundle = (Bundle) field.get(n);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // android 4.3之后
            bundle = n.extras;
        }
        // 内容标题、内容、副内容
        String contentTitle = bundle.getString(Notification.EXTRA_TITLE);
        if (contentTitle == null) {
            contentTitle = "";
        }
        String contentText = bundle.getString(Notification.EXTRA_TEXT);
        if (contentText == null) {
            contentText = "";
        }
        String contentSubtext = bundle.getString(Notification.EXTRA_SUB_TEXT);
        if (contentSubtext == null) {
            contentSubtext = "";
        }*/
            ZFLog.i("监控类获取到通知: " + title + " 时间:" + when);//监控类获取到通知: 你发起的AA收款，钱收齐啦 时间:1542698764139
            if (title.contains("向你付款") || title.contains("向你转了")) { //是否包含
//                try {
//                    Thread.sleep(500); //给一秒的读取时间
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                sendMsg2SS(4);



                if (sbn.isClearable()) {//删除
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cancelNotification(sbn.getKey());
                    } else {
                        cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
                    }
                }
            }
            cancelAllNotifications();//清除所有通知
        }
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        ZFLog.i("监听服务启动成功");
        Config.controlIsOpen = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Config.controlIsOpen = false;
        ZFLog.i("监听服务已关闭");
    }

    void sendMsg2SS(int msgWhat) {
        sendMsg2SS(msgWhat, null);
    }

    void sendMsg2SS(int msgWhat, String msgSend) {
        Intent serviceIntent = new Intent(NCService.this, SService.class);
        //serviceIntent.setClass()
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
}
