package com.zfbu.zfcore.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ZFLog {

    public static void i(String msg) {

        int strLength = msg.length();
        int start = 0;
        int LOG_MAXLENGTH = 2000;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.i("zuuuuuuuuuuuuuuuu" + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.i("zuuuuuuuuuuuuuuuu", msg.substring(start, strLength));
                break;
            }
        }
    }

    public static void i(int logInt) {
        i(String.valueOf(logInt));
    }

    public static void ToastMsg(Context context, String ToastStr) {
        Toast.makeText(context, ToastStr, Toast.LENGTH_SHORT).show();
    }
}
