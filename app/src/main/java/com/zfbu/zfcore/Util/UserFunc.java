package com.zfbu.zfcore.Util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.Util.HBSQL.DBManager;


public class UserFunc {

    //是否已经登录
    public static boolean isLogin() {
        return !Config.userData.equals("") && !Config.userName.equals("");
    }

    public static void outLogin(Context context) {
        Config.userName = "";
        Config.userData = "";
        Config.nowAppId = "";
        Config.aliUserId = "";
        Core.modResUserNo(context);
    }

    public static String getData1(Context context) {
        String tmpStr = null, tmpNum;
        String toDay = Core.getPreferences_string(context, Config.userName, "today");
        if (toDay.equals("") || !toDay.equals(Core.getToday_dd())) { //如果为空  或者 非今天
            tmpStr = "0/0";
        } else { //今日已经存在的
            tmpNum = Core.getPreferences_string(context, Config.userName, "tomoney");
            tmpStr = tmpNum.equals("") ? "0" : tmpNum;
            tmpStr += "/";

            tmpNum = Core.getPreferences_string(context, Config.userName, "totime");
            tmpStr += tmpNum.equals("") ? "0" : tmpNum;
        }

        return tmpStr;
    }

    @SuppressLint("DefaultLocale")
    public static String getData2(Context context) {
        String tmpStr;
        tmpStr = Core.getPreferences_string(context, Config.userName, "runtime");
        if (tmpStr.equals("")) {
            return "0:0";
        } else {
            return Core.stamp2time(Integer.valueOf(Core.getTenStamp(Core.getStamp())) - Integer.valueOf(tmpStr));
        }
    }

    public static String getData3(Context context) {
        String tmpStr, tmpNum;
        tmpNum = Core.getPreferences_string(context, Config.userName, "allmoney");
        tmpStr = tmpNum.equals("") ? "0" : tmpNum;
        tmpStr += "/";

        tmpNum = Core.getPreferences_string(context, Config.userName, "alltime");
        tmpStr += tmpNum.equals("") ? "0" : tmpNum;
        return tmpStr;
    }

    public static void setAppData(Context context, String money, int status,String note,String orderid) {
        DBManager dbManager = DBManager.getInstance(context); //数据库
        String toDay = Core.getPreferences_string(context, Config.userName, "today");
        String tmpStr;
        if (toDay.equals("") || !toDay.equals(Core.getToday_dd())) { //如果为空  或者 非今天
            Core.setPreferences_string(context, Config.userName, "tomoney", money);
            Core.setPreferences_string(context, Config.userName, "totime", "1");
            Core.setPreferences_string(context, Config.userName, "today", Core.getToday_dd());
            //新增group
            ContentValues values = new ContentValues();
            values.put("username", Config.userName);
            values.put("timestr", Core.getToday_yyyy_MM_dd());
            values.put("order_number", "1");
            values.put("order_money", money);
            if (status == 1 || status == 2) {
                values.put("sendState", "2");//有新增
            } else {//3
                values.put("sendState", "1");//网络错误等等
            }
            dbManager.insert("hb_order_itme", values);
        } else { //今日已经存在的
            String tmpTM = Core.doubleAdd(Core.getPreferences_string(context, Config.userName, "tomoney"), money);
            String tmpTT = String.valueOf(Integer.valueOf(Core.getPreferences_string(context, Config.userName, "totime")) + 1);
            Core.setPreferences_string(context, Config.userName, "tomoney", tmpTM);
            Core.setPreferences_string(context, Config.userName, "totime", tmpTT);
            //更新
            ContentValues tempValues = new ContentValues();
            tempValues.put("order_number", tmpTT);
            tempValues.put("order_money", tmpTM);
            if (status == 1 || status == 2) {
                tempValues.put("sendState", "2");//有新增
            } else {//3
                tempValues.put("sendState", "1");//网络错误等等
            }
            String tempIfStr = "username=? AND timestr=?";
            String[] tempBingArgs = new String[]{Config.userName, Core.getToday_yyyy_MM_dd()};
            dbManager.update("hb_order_itme", tempValues, tempIfStr, tempBingArgs); //更新数据
        }
        tmpStr = Core.getPreferences_string(context, Config.userName, "allmoney").equals("") ?
                "0" : Core.getPreferences_string(context, Config.userName, "allmoney");
        Core.setPreferences_string(context, Config.userName, "allmoney", Core.doubleAdd(tmpStr, money));
        tmpStr = Core.getPreferences_string(context, Config.userName, "alltime").equals("") ?
                "0" : Core.getPreferences_string(context, Config.userName, "alltime");
        Core.setPreferences_string(context, Config.userName, "alltime",
                String.valueOf(Integer.valueOf(tmpStr) + 1));
        //增加今日
        ContentValues values = new ContentValues();
        values.put("username", Config.userName);
        values.put("timestr", Core.getToday_yyyy_MM_dd());
        values.put("appid", Config.nowAppId);
        values.put("paytime", Core.getStamp());
        values.put("actual", money);
        values.put("paynote", note);
        values.put("orderid", orderid);
        if (status == 1) {//1:status1 成功
            values.put("sendState", "1");
            values.put("sendway", "发送并处理成功,status:1");
        } else if (status == 2) {//2:status0 失败
            values.put("sendState", "2");
            values.put("sendway", "发送但处理失败,status:0");
        } else {//3.其他错误,可重复推送
            values.put("sendState", "3");
            values.put("sendway", "发送到服务器失败");
        }
        dbManager.insert("hb_order", values);
    }


    @SuppressLint("DefaultLocale")
    public static void setAppTime(Context context, boolean setTime) {  //是否插入时间
        if (setTime) { //是否插入内容
            Core.setPreferences_string(context, Config.userName, "runtime", Core.getTenStamp(Core.getStamp()));
        } else {
            Core.setPreferences_string(context, Config.userName, "runtime", "");
        }
    }

    @SuppressLint("DefaultLocale")
    public static void setAppKillTime(Context context, boolean killTime) {  //重启时间
        if (killTime) { //是否插入内容
            Core.setPreferences_string(context, Config.userName, "killtime", Core.getTenStamp(Core.getStamp()));
        } else {
            Core.setPreferences_string(context, Config.userName, "killtime", "");
        }
    }

    public static String getNoteMoney(String str) {
        if (!Core.ESSubstr(str, "成功收款", "元").equals("")) {
            return Core.ESSubstr(str, "成功收款", "元");
        } else if (!Core.ESSubstr(str, "向你付款", "元").equals("")) {
            return Core.ESSubstr(str, "向你付款", "元");
        } else return "";
    }
}
