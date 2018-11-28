package com.zfbu.zfcore.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.Service.NCService;

public class NServiceMake {
    private String alreadyNotification = "";
    private ContentResolver contentResolver; //getContentResolver()

    public NServiceMake(ContentResolver contentResolver) {//getContentResolver()
        this.contentResolver = contentResolver;
    }

    public void start(String alreadyNotification) { //开启指定辅助..估计用不上
        startService(alreadyNotification);
    }

    public void start() { //开启插件 ,如果不传参数,就用历史的开 ,  如果历史上没有, 就不开
        if (!alreadyNotification.equals("")) { //如果不为空的话, 就开启
            startService(alreadyNotification);
            alreadyNotification = ""; //清空,防止多次运行本命令开启
        }
    }

    public boolean startSelf(Context context) { //启动自身的辅助插件   getApplicationContext()
        if (!Config.controlIsOpen) { //如果自己的辅助没有在运行
            stop();//先清空(数据库有内容才清空)
            startService(context.getPackageName() + "/" + NCService.class.getName());
            alreadyNotification = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
            //com.zfbu.zfcore/com.zfbu.zfcore.Service.NCService
            if (alreadyNotification.equals(context.getPackageName() + "/" + NCService.class.getName())) {
                alreadyNotification = ""; //清空,防止被之后执行start命令
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void startService(String alreadyAccessibility) { //开启插件,本类里面使用
        Core.execRootCmd("settings put secure enabled_notification_listeners " + alreadyAccessibility);
    }

    public void stop() { //关闭插件
        alreadyNotification = Settings.Secure.getString(contentResolver, "enabled_notification_listeners").replaceAll(" ","");
        //ZFLog.i("抓到的通知栏服务: " + alreadyNotification);
        if (!StringUtils.isEmpty(alreadyNotification)) { //如果不为空的话, 就强制置空
            Core.execRootCmd("settings put secure enabled_notification_listeners \r\n");  //全部关闭
        }
    }
}
