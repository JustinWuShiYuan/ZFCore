package com.zfbu.zfcore.Util.HBSQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.zfbu.zfcore.Util.ZFLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库管理类
 */
public class DBManager {
    private static DBManager dbManager;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private DBManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    private void getConnection() {
        db = dbHelper.getWritableDatabase();
        //Log.i("zuuuuuuuuuuuuuuu", "连接数据库");
    }

    private void closeConnection() {
        db.close();
    }

    public boolean execSQL(String sql, Object[] bindArgs) {
        ZFLog.i("执行类一次sql语句");
        getConnection();
        try {
            db.execSQL(sql, bindArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeConnection();
        }
    }

    //直接执行一个sql语句
    //创建表:"create table ssssssb (id integer PRIMARY KEY autoincrement,user_name varchar(20),user_password varchar(20))"
    public boolean execSQL(String sql) {
        getConnection();
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeConnection();
        }
    }


    //执行一次添加
    //ContentValues values = new ContentValues();
    //values.put("user_name", "千");//key为字段名，value为值
    public boolean insert(String tableName, ContentValues values) {
        ZFLog.i("执行类一次sql添加语句");
        getConnection();
        try {
            long rowid = db.insert(tableName, null, values);//返回新添记录的行号，与主键id无关
            if (rowid > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeConnection();
        }
    }

    //插入多行数据
    //表名 , 字段名(例:"orderid ,order_money")
    //例:dbManager.insert_many("hb_order",new String[]{"orderid","order_money"},new String[]{"7777","8888","9999","10","11","12"});
    public boolean insert_many(String tableName, String[] fieldArr, String[] dataArr) {
        ZFLog.i("执行类一次sql插入多行语句");
        getConnection();
        try {
            String fieldStr = ""; //字段名字符串
            String seatStrTemp = "";//占位符字符串,临时
            String seatStr = ""; //占位符
            for (int i = 0; i < fieldArr.length; i++) {
                if (i == 0) { //第一次的时候
                    fieldStr += "(";
                    seatStrTemp += "(";
                }
                fieldStr += fieldArr[i];
                seatStrTemp += "?";
                if (i != fieldArr.length - 1) { //如果没有到最后一次的时候
                    fieldStr += " ,";
                    seatStrTemp += " ,";
                } else if (i == fieldArr.length - 1) { //如果是最后一次的时候
                    fieldStr += ")";
                    seatStrTemp += ")";
                }
            }
            int s = dataArr.length / fieldArr.length; //计算要复制的次数
            Log.i("zuuuuuuuuuuuu", "要循环的次数: " + s + " 要循环的内容: " + seatStrTemp);
            for (int i = 0; i < s; i++) {
                seatStr += seatStrTemp;
                if (i != s - 1) { //如果不是最后一次
                    seatStr += " ,";
                }
            }

            String sql = "insert into " + tableName + " " + fieldStr + " values " + seatStr;
            Log.i("zuuuuuuuuu", "执行的语句" + sql);
            db.execSQL(sql, dataArr);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeConnection();
        }
    }


    //执行一次删除
    // ifStr = "user_name=?"
    //bingArgs = new String[]{"小"}
    public boolean delete(String tableName, String ifStr, String[] bindArgs) {
        ZFLog.i("执行类一次sql删除语句");
        getConnection();
        try {
            db.delete(tableName, ifStr, bindArgs);//返回新添记录的行号，与主键id无关
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeConnection();
        }
    }

    //执行一次更新
    //ContentValues values = new ContentValues();
    //values.put("user_name", "千");//key为字段名，value为值
    // ifStr = "user_name=?"
    //bingArgs = new String[]{"小"}
    public boolean update(String tableName, ContentValues values, String ifStr, String[] bindArgs) {
        ZFLog.i("执行类一次sql更新语句");
        getConnection();
        try {
            db.update(tableName, values, ifStr, bindArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeConnection();
        }
    }

    //查询并输出整个表
    public List<Map<String, Object>> query_select_all(String tableName) {
        ZFLog.i("执行类一次sql输出整个表的语句");
        getConnection();
        try {
            return cursor2Data(db.rawQuery("select * from " + tableName, null));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeConnection();
        }
    }


    //执行一个查询语句  没有数据或异常, 返回null    用到占位符
    public List<Map<String, Object>> query_select(String sql, String[] bindArgs) {
        getConnection(); //连接
        try {
            return cursor2Data(db.rawQuery(sql, bindArgs));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeConnection(); //关闭连接
        }
    }


    //将游标里面的数据全部读取出来 , 本命令在数据库没关闭前使用吧

    private List<Map<String, Object>> cursor2Data(Cursor cursor) {
        List<Map<String, Object>> resultList = new ArrayList<>(); //最终传出的数据
        Map<String, Integer> keyType = new HashMap<>(); //保存键所属的类型
        boolean isFirst = true; //是否是第一次循环
        try {
            if (cursor.getCount() == 0) { //如果没有数据
                return null;
            }
            String columnNameArr[] = cursor.getColumnNames(); //字段名数组
            while (cursor.moveToNext()) {
                Map<String, Object> tempData = new HashMap<>();//这一行的数据
                for (String columnName : columnNameArr) {
                    int nowKeyType = -1;//当前列的字段类型
                    int nowColumnIndex = cursor.getColumnIndex(columnName); //当前列的索引
                    if (isFirst) { //如果是第一次进入
                        //保存当前键名所对应的字段类型
                        nowKeyType = cursor.getType(nowColumnIndex);
                        keyType.put(columnName, nowKeyType);
                    }
                    //当前字段的类型  如果不是第一次,就从记录中获取
                    nowKeyType = nowKeyType == -1 ? keyType.get(columnName) : nowKeyType;
                    switch (nowKeyType) {
                        case 0: //FIELD_TYPE_NULL (0)
                            tempData.put(columnName, "null");
                            break;
                        case 1://FIELD_TYPE_INTEGER (1)
                            tempData.put(columnName, cursor.getInt(nowColumnIndex));
                            break;
                        case 2:   //FIELD_TYPE_FLOAT (2)
                            tempData.put(columnName, cursor.getFloat(nowColumnIndex));
                            break;
                        case 3://FIELD_TYPE_STRING (3)
                            tempData.put(columnName, cursor.getString(nowColumnIndex));
                            break;
                        case 4:  //FIELD_TYPE_BLOB (4)
                            tempData.put(columnName, cursor.getBlob(nowColumnIndex));
                            break;
                    }
                } //内循环完毕
                isFirst = false; //第一次完毕
                resultList.add(tempData);
            }

            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }
}