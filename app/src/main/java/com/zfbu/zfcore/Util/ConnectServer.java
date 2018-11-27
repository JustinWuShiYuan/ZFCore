package com.zfbu.zfcore.Util;


import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.ProData.AppData;
import com.zfbu.zfcore.ProData.QrData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//需要在线程中执行
public class ConnectServer {

    public boolean login(String user, String pwd) {
        /*Map<String, String> sendMap = new HashMap<>();
        sendMap.put("email", user);
        sendMap.put("password", pwd);
        HBWebServer webUri = new HBWebServer();
        String tmpReturn = webUri.post(Config.service_url + "login", sendMap);
        //ZFLog.i(tmpReturn);
        if (tmpReturn != null && !tmpReturn.equals("")) {//如果返回空
            try {
                JSONObject jsonObject = new JSONObject(tmpReturn);
                if (jsonObject.getInt("status") == 1) { //登录成功
                    Config.userData = jsonObject.getString("data");//用户token
                    Config.userName = user;
                    ZFLog.i("用户token:" + Config.userData);
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ZFLog.i("登录失败");
        return false;*/

        //这里模拟登录成功事件
        Config.userData = "123";//用户token
        Config.userName = user;
        return true;
    }


    //int  1:status1 成功   2:status0失败  3.其他错误,可重复推送
    public int submit(String payTime, String actual) {
        Map<String, String> sendMap = new HashMap<>();
        sendMap.put("type", "set_show_order");
        //演示用
        sendMap.put("sign", "eJxlkF1PgzAYRu-5FYRbjWvLSpjJLthkfkGcA6fxpqlQtoqDlnYImP13lZmI8fqc5HnP*2GYpmnFQXRGk6TcF5roVjDLPDctYJ3*QiF4SqgmdpX*g6wRvGKEZppVPUR4ggAYKjxlheYZ-xE0U3pAVZqTfqGHcAwAxA5C9lDhmx6G-v38*lIX*WsenjQP7mp21b3Z28RbKBzUF20gRwnIfHk7dxZLt3vyuKcwjUcyjpr9OuyepSzpbtm*sAit5SOdOXeiAe9Rvc38m810OpjUfHd8BMRj5LrA*VNUs0rxsjj2ft0LIZyA72jjYHwC5bVdvg__");
        sendMap.put("paytime", payTime);
        sendMap.put("money", actual);
        //ZFLog.i("提交订单: paytime:" + payTime + "  money:" + actual);
        HBWebServer webUri = new HBWebServer();
        String tmpReturn = webUri.post(Config.service_url, sendMap);
        ZFLog.i("提交订单返回数据:" + tmpReturn);
        try {
            JSONObject jsonObject = new JSONObject(tmpReturn);
            if (jsonObject.getString("state").equals("success")) {//成功
                return 1;
            } else if (jsonObject.getString("state").equals("error")) {//失败
                return 2;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 3;
    }

}
