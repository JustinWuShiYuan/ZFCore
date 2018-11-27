package com.zfbu.zfcore.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.Service.AService;


/**
 * 本类执行accessibilityservice的开关操作
 */

public class AServiceMake {
    private String alreadyAccessibility = "";
    private ContentResolver contentResolver; //getContentResolver()

    public AServiceMake(ContentResolver contentResolver) {//getContentResolver()
        this.contentResolver = contentResolver;
    }

    public void start(String alreadyAccessibility) { //开启指定辅助..估计用不上
        startService(alreadyAccessibility);
    }

    public void start() { //开启插件 ,如果不传参数,就用历史的开 ,  如果历史上没有, 就不开
        if (!alreadyAccessibility.equals("")) { //如果不为空的话, 就开启
            startService(alreadyAccessibility);
            alreadyAccessibility = ""; //清空,防止多次运行本命令开启
        }
    }

    public void startSelf(Context context) { //启动自身的辅助插件   getApplicationContext()
        if (!Config.aserRun) { //如果自己的辅助没有在运行
            stop();//先清空(数据库有内容才清空)
            startService(context.getPackageName() + "/" + AService.class.getName());
            alreadyAccessibility = ""; //清空,防止被之后执行start命令
        }
    }

    private void startService(String alreadyAccessibility) { //开启插件,本类里面使用
        //settings put secure enabled_accessibility_services es.Tool/es.Tool.ESService.AService
        Core.execRootCmd("settings put secure enabled_accessibility_services " + alreadyAccessibility);
        Core.execRootCmd("settings put secure accessibility_enabled 1");
    }

    public void stop() { //关闭插件
        alreadyAccessibility = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES); //把已经开启的辅助抓取出来
        if ( alreadyAccessibility ==null || !alreadyAccessibility.equals("") ) { //如果不为空的话, 就强制置空
            Core.execRootCmd("settings put secure enabled_accessibility_services \r\n");  //关闭整部手机的所有accessibility服务
        }
    }

}
