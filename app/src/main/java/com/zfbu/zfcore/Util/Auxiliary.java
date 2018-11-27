package com.zfbu.zfcore.Util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/3/6.
 */

public class Auxiliary {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo ESGetInfoByID(AccessibilityNodeInfo info, String id) { //查询指定id的控件,并返回第一个
        return ESGetInfoByID(info, id, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo ESGetInfoByID(AccessibilityNodeInfo info, String id, int index) { //查询指定id的控件,并返回指定的
        if (info == null) {
            return null;
        }
        List<AccessibilityNodeInfo> listInfo = info.findAccessibilityNodeInfosByViewId(id);
        if (listInfo.size() > index) {
            return listInfo.get(index);
        } else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo ESGetInfoByText(AccessibilityNodeInfo info, String Text) { //查询指定id的控件,并返回第一个
        return ESGetInfoByText(info, Text, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo ESGetInfoByText(AccessibilityNodeInfo info, String Text, int index) { //查询指定id的控件,并返回指定的
        if (info == null) {
            return null;
        }
        List<AccessibilityNodeInfo> listInfo = info.findAccessibilityNodeInfosByText(Text);
        if (listInfo.size() > index) {
            return listInfo.get(index);
        } else {
            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean ESGetInfoByIDClick(AccessibilityNodeInfo info, String id) {
        info = ESGetInfoByID(info, id);
        if (info != null) { //找到这个控件
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String ESGetInfoByIDReadText(AccessibilityNodeInfo info, String id) {
        info = ESGetInfoByID(info, id);
        if (info != null) { //找到这个控件
            if (info.getText() != null) {
                return info.getText().toString();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean ESGetInfoByIDScrollTop(AccessibilityNodeInfo info, String id) {
        info = ESGetInfoByID(info, id);
        if (info != null) { //找到这个控件
            info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean ESGetInfoByIDScrollTop(AccessibilityNodeInfo info) {
        info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean ESGetInfoByIDScrollDown(AccessibilityNodeInfo info, String id) {
        info = ESGetInfoByID(info, id);
        if (info != null) { //找到这个控件
            info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean ESGetInfoByIDScrollDown(AccessibilityNodeInfo info) {
        info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean ESGetInfoByIDWrite(AccessibilityNodeInfo info, String id, String Str, Context context) {
        info = ESGetInfoByID(info, id);
        if (info != null) { //找到这个控件
            ESInfoWriteText(info, Str, context);
            return true;
        } else {
            return false;
        }
    }

    public static void ESInfoWriteText(AccessibilityNodeInfo info, String Str, Context context) { //向控件写入内容

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0安卓以上的
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Str);
            info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments); //输入文本
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {  //4.3以上的
            ClipData clip = ClipData.newPlainText("label", Str);
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(clip);
            info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }


    }


    public static String ESGetRect(AccessibilityNodeInfo info) { //取控件位置信息,左上角右下角座标
        Rect rect = new Rect();
        info.getBoundsInScreen(rect);
        Rect rect1 = new Rect();
        info.getBoundsInParent(rect1);
        String returnStr = "[" + (rect.centerX() - rect1.centerX()) +
                "," + (rect.centerY() - rect1.centerY()) + "]["
                + (rect.centerX() + rect1.centerX()) + ","
                + (rect.centerY() + rect1.centerY()) + "]";
        return returnStr;
    }


}
