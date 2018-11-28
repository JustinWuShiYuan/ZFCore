package com.zfbu.zfcore.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.ProData.OrderDataVar;
import com.zfbu.zfcore.Util.HBSQL.DBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//这里的逻辑要根据用户以及绑定的appid+aliid来改
//dbOrderCreatTime 分两次保存,0以及用户id的,用来获取历史记录
public class OrderSu {
    private Context mContext;

    private String dbOldPath, dbNewPath; //文件地址
    private DBManager dbManager; //数据库

    private long dbChangTime; //原数据库文件最后修改的时间
    private String dbOrderCreatTime; //订单数据库里面最后一条订单创建时间

    private List<String> oldOrderList; //上次读取到的订单号数据list


    private int stateId = 0; //错误id

    public OrderSu(Context context) {
        this.mContext = context;
        init();
    }

    public int getStateMod() { //获取失败的原因id
        //1:文件不存在  2:初始化成功  3://初始化时候,有数据库但是没有订单数据
        //4:没有获取到新数据
        return stateId;
    }

    @SuppressLint({"UseSparseArrays", "SdCardPath"})
    private void init() {
        if (oldOrderList == null) {
            oldOrderList = new ArrayList<>();
            dbOldPath = "/data/data/com.eg.android.AlipayGphone/databases/messagebox.db";
            dbNewPath = "/data/data/" + mContext.getPackageName() + "/shared_prefs/order.db";
            dbManager = DBManager.getInstance(mContext); //数据库
            dbChangTime = 0;//原数据库文件最后修改的时间

            String lastCreate = Core.getPreferences_string(mContext, Config.userName, "lastCreate");
            if (StringUtils.isEmpty(lastCreate)) {//如果还没有数据
                dbOrderCreatTime = "-1";//-1代表初始化 未知的情况.  0代表没有数据库的情况
                Core.setPreferences_string(mContext, Config.userName, "lastCreate", dbOrderCreatTime);
                fuckIt(); //初始化读取一次
            } else {//有数据
                dbOrderCreatTime = lastCreate;
            }
            ZFLog.i("最后的订单创建时间:" + dbOrderCreatTime);
        }
    }

    public List<OrderDataVar> fuckIt() {
        boolean tmpCpState; //复制文件的判断
        //复制一份数据库
        File file = new File(dbOldPath); //支付宝的数据库是否存在
        if (!file.exists()) {
            ZFLog.i("文件不存在");
            stateId = 1;
            return null;
        } else {
            ZFLog.i("文件存在");
            //判断文件是否有变化
            if (dbChangTime != 0 && dbChangTime == file.lastModified()) {//不是第一次 并且  时间一样
                ZFLog.i("时间一样X1");
                //TODO 上面已经 取过支付宝的了 这里是？？
                try {
                    Thread.sleep(1000); //给一秒的读取时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                file = new File(dbOldPath);
                if (dbChangTime == file.lastModified()) {
                    //置错误状态码
                    return null;
                }
            } else { //不是第一次  时间不一样
                dbChangTime = file.lastModified(); //第一次赋值
            }

            tmpCpState = Core.copyFile(dbOldPath, dbNewPath); //复制一次
            if (!tmpCpState) {
                ZFLog.i("复制失败");
                Core.execRootCmd("chmod 777 " + dbOldPath); //单文件给权限
                tmpCpState = Core.copyFile(dbOldPath, dbNewPath);
                if (!tmpCpState) {
                    ZFLog.i("第二次复制失败");
                    //置错误状态码
                    return null;
                }
            }
        }

        //打开数据库
        file = new File(dbNewPath);
        if (!file.exists() || file.length() == 0) {
            ZFLog.i("新数据库文件无法读取或为空");
            return null;
        }
        SQLiteDatabase database;
        try {
            database = SQLiteDatabase.openOrCreateDatabase(file, null); //已经打开的数据库
        } catch (SQLiteException s) {
            try {
                Thread.sleep(100); //会出现少表的情况
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tmpCpState = Core.copyFile(dbOldPath, dbNewPath);
            if (!tmpCpState) {
                ZFLog.i("读取数据库不完整并且补救的复制失败");
                //置错误状态码
                return null;
            }
            file = new File(dbNewPath);
            try {
                database = SQLiteDatabase.openOrCreateDatabase(file, null); //已经打开的数据库
            } catch (SQLiteException ignored) {
                ZFLog.i("第二次复制还无法完整读取数据库");
                //置错误状态码
                return null;
            }
        }
        //进行数据操作
        Cursor cursor;
        if (dbOrderCreatTime.equals("-1")) { // -1为第一次未初始化的时候
            cursor = database.rawQuery("select * from trade_message ORDER BY gmtCreate DESC LIMIT 1", null);
            if (cursor.moveToFirst()) {
                dbOrderCreatTime = cursor.getString(cursor.getColumnIndex("gmtCreate")); //订单的最新创建时间
                Core.setPreferences_string(mContext, Config.userName, "lastCreate", dbOrderCreatTime);
                ZFLog.i("初始化读取,最新订单创建时间:" + dbOrderCreatTime);
                stateId = 2;//初始化成功
                cursor.close();
                database.close();
                return null;
            } else { //没有找到数据
                dbOrderCreatTime = "0";
                Core.setPreferences_string(mContext, Config.userName, "lastCreate", dbOrderCreatTime);
                stateId = 3;//初始化时候,有数据库但是没有订单数据
                ZFLog.i("初始化读取,订单未获取到");
                cursor.close();
                database.close();
                return null;
            }
        } else { //第二次之后的读取
            String sqlStr = "select * from trade_message WHERE `gmtCreate` >= ?";
            cursor = database.rawQuery(sqlStr, new String[]{dbOrderCreatTime});
            if (cursor.moveToFirst()) {
                ZFLog.i("数据库获取到新订单了");
                List<OrderDataVar> returnOrderList = null;
                List<String> tempOrderList = new ArrayList<>();
                do {
                    if (!cursor.getString(cursor.getColumnIndex("title")).equals("支付助手")) {
                        continue;
                    }
                    String tempStr = cursor.getString(cursor.getColumnIndex("homePageTitle")); //获取到付款标题
                    if (!tempStr.contains("支付助手") || !tempStr.contains("收到一笔转账")) { //如果没有包含这两个就跳出
                        continue;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex("extraInfo")));
                        //ZFLog.i("数据库数据: " + jsonObject.toString());
                        tempStr = jsonObject.getString("goto");//获取该笔支付订单的跳转地址
                        if (tempStr.contains("&tradeNO=")) { //是否存在该字符
                            tempStr = Core.ESSubstr(tempStr, "&tradeNO=", "&bizType="); //订单号
                        } else {
                            tempStr = Core.ESSubstr(tempStr, "%26tradeNo%3D", "%26source%3D"); //订单号
                        }
                        ZFLog.i("获取到订单号:" + tempStr);
                        if (cursor.getString(cursor.getColumnIndex("gmtCreate")).equals(dbOrderCreatTime)) { //如果订单时间跟历史记录一样
                            List<Map<String, Object>> tempSqlValue = dbManager.query_select("SELECT * FROM `hb_order` WHERE `orderid` = ?"
                                    , new String[]{ tempStr});
                            if (tempSqlValue != null) { //如果有数据
                                continue; //跳过
                            }
                        }

                        /*for (int f = 0; f < oldOrderList.size(); f++) {
                            ZFLog.i("历史记录" + f + ":" + oldOrderList.get(f));
                        }*/
                        if (!oldOrderList.contains(tempStr)) { //如果本地总的记录里面没有包含的话
                            OrderDataVar tempOrderDataVar = new OrderDataVar();
                            tempOrderDataVar.setOrderId(tempStr); //订单号..
                            tempOrderList.add(tempStr); //订单号

                            tempOrderDataVar.setMoney(jsonObject.getString("money")); //金额

                            JSONArray jsonArray = new JSONArray(jsonObject.getString("content"));
                            for (int ii = 0; ii < jsonArray.length(); ii++) {
                                JSONObject jsonObjectTemp = new JSONObject(String.valueOf(jsonArray.get(ii)));
                                switch (jsonObjectTemp.getString("title")) {
                                    case "付款人：":
                                    case "付款方：":
                                        tempOrderDataVar.setPayuser(jsonObjectTemp.getString("content")); //付款人
                                        break;
                                    case "备注：":
                                    case "转账备注：":
                                        tempOrderDataVar.setInfo(jsonObjectTemp.getString("content")); //转账说明
                                        break;
                                    case "到账时间：":
                                        tempOrderDataVar.setTime(jsonObjectTemp.getString("content"));//到帐时间
                                        break;
                                }
                            }
                            tempOrderDataVar.setGmtCreate(cursor.getString(cursor.getColumnIndex("gmtCreate")));//订单时间戳
                            String userid = cursor.getString(cursor.getColumnIndex("userId")); //用户消息的用户ID
                            /*    for (int i = 0; i < HBAppConfig.listAppidData.size(); i++) {
                                    if (HBAppConfig.listAppidData.get(i).loguserid.equals(userid)) { //如果userid一样
                                        tempOrderDataVar.setAppid(HBAppConfig.listAppidData.get(i).appid);
                                        break;
                                    }
                                }

                                测试时候关闭的!!!!!!!!!!!!!!!!!!!!!!!!!
                                */

                            tempOrderDataVar.setState(1); //状态

                            if (returnOrderList == null) {
                                returnOrderList = new ArrayList<>();
                            }
                            returnOrderList.add(tempOrderDataVar);
                            ZFLog.i("获取成功一条新订单");
                            //如果最新的数据比历史数据还大
                            if (Long.valueOf(tempOrderDataVar.getGmtCreate()) > Long.valueOf(dbOrderCreatTime)) {
                                dbOrderCreatTime = tempOrderDataVar.getGmtCreate();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
                if (returnOrderList != null) { //如果有数据
                    Core.setPreferences_string(mContext, Config.userName, "lastCreate", dbOrderCreatTime);
                    oldOrderList = tempOrderList; //历史记录赋值
                }
                cursor.close();
                database.close();
                return returnOrderList;
            } else {
                ZFLog.i("没有找到数据");
                cursor.close();
                database.close();
                stateId = 4;//没有获取到新数据
                return null;
            }
        }
    }
}
