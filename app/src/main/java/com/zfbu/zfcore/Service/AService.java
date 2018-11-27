package com.zfbu.zfcore.Service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.ProData.QrData;
import com.zfbu.zfcore.ProData.ServiceMsgData;
import com.zfbu.zfcore.Util.Auxiliary;
import com.zfbu.zfcore.Util.ZFLog;

import java.io.Serializable;
import java.util.List;

public class AService extends AccessibilityService {


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        ZFLog.i("辅助启动成功");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Config.aserRun = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ZFLog.i("辅助的onStartCommand被触发");
        Object object = intent.getSerializableExtra("intentData");
        if (object != null) {
            if (object instanceof ServiceMsgData) {

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
