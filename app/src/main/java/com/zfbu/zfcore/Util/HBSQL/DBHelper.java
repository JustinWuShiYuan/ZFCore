package com.zfbu.zfcore.Util.HBSQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zfbu.zfcore.Util.ZFLog;


public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "hbpay_db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {//第一次将执行
        ZFLog.i("开始创建数据库...");
        db.execSQL("CREATE TABLE `hb_order_itme` ( " +
                "`sid` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                "`username` TEXT NOT NULL, " +
                "`timestr` TEXT NOT NULL, " +
                "`order_number` INTEGER, " +
                "`order_money` INTEGER, " +
                "`sendState` INTEGER)"); //订单groupA数据 //1有错误 2新增  3隐藏
        db.execSQL("CREATE TABLE `hb_order` ( " +
                "`sid` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                "`username` TEXT NOT NULL, " +
                "`timestr` TEXT NOT NULL, " +
                "`appid` TEXT NOT NULL, " +
                "`paytime` INTEGER, " +
                "`actual` TEXT NOT NULL, " +
                "`paynote` TEXT NOT NULL, " +
                "`orderid` TEXT NOT NULL, " +
                "`sendway` TEXT NOT NULL, " +
                "`sendState` INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果有新版本诞生. 这里修改表结构
    }
}
