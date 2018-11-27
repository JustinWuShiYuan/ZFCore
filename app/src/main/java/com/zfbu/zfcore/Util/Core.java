package com.zfbu.zfcore.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.zfbu.zfcore.Config.Config;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Core {

    //是否记住密码  是的话返回帐号密码
    @SuppressLint("ApplySharedPref")
    public static List<String> hasReUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        boolean flag = preferences.getBoolean("reUser", false); //获取到参数属性  2:缺省
        if (flag) { //如果保存
            List<String> userList = new ArrayList<>();
            userList.add(preferences.getString("userName", ""));
            userList.add(preferences.getString("userPwd", ""));
            return userList;
        }
        return null;
    }

    //不保存密码
    public static void modResUserNo(Context context) {//不保存状态
        modResUser(context, false, "", "");
    }

    //保存密码
    public static void modResUser(Context context, String userName, String userPwd) {//保存用户数据
        modResUser(context, true, userName, userPwd);
    }

    @SuppressLint("ApplySharedPref")
    private static void modResUser(Context context, boolean hasRe, String userName, String userPwd) {//修改保存状态
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        if (hasRe) {//如果是要保存帐号密码
            preferences.edit().putBoolean("reUser", true).commit();
            preferences.edit().putString("userName", userName).commit();
            preferences.edit().putString("userPwd", userPwd).commit();
        } else { //删除密码
            preferences.edit().putString("userName", "").commit();
            preferences.edit().putString("userPwd", "").commit();
        }
    }

    public static String getPreferences_string(Context context, String fileName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    @SuppressLint("ApplySharedPref")
    public static void setPreferences_string(Context context, String fileName, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }


    //是否能读取文件权限
    public static boolean getReadState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return preferences.getBoolean("readState", false);
    }

    //设置允许读取
    @SuppressLint("ApplySharedPref")
    public static void setReadState(Context context, boolean tmpState) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("readState", tmpState).commit();
    }

    //是否有ROOT权限
    public static int getRootState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return preferences.getInt("rootState", 1);//未检测
    }

    //设置ROOT状态
    @SuppressLint("ApplySharedPref")
    public static void setROOTState(Context context, int tmpState) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        preferences.edit().putInt("rootState", tmpState).commit();
    }

    //获取是否重启功能
    public static boolean getRestState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return preferences.getBoolean("restState", false);
    }

    //设置是否重启
    @SuppressLint("ApplySharedPref")
    public static void setRestState(Context context, boolean tmpState) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("restState", tmpState).commit();
    }

    //获取是否保护功能
    public static boolean getProState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return preferences.getBoolean("proState", false);
    }

    //设置是否保护
    @SuppressLint("ApplySharedPref")
    public static void setProState(Context context, boolean tmpState) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("proState", tmpState).commit();
    }

    //运行缓存,用来重启后重新拉起 ,参数为空的时候为删除
    @SuppressLint("ApplySharedPref")
    public static void setRunCache(Context context, String appId) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        if (appId == null) {
            preferences.edit().putString("tmpAppid", "").commit();
        } else {
            preferences.edit().putString("tmpAppid", appId).commit();
        }
    }

    public static boolean getRunCache(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String a = preferences.getString("tmpAppid", "");
        if (!a.equals("")) {//如果有保存
            Config.tmpAppid = a;
            return true;
        } else {
            return false;
        }
    }

    public static String stringMD5(String pw) {//取md5
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = pw.getBytes();
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    //将时间转换为时间戳
    public static String dateToStamp(String s) {
        String res;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    private static String byteArrayToHex(byte[] byteArray) { //md5转字符专用
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    public static String getCachePath(Context context, int pathMod) {
        String pathName = null;
        if (pathMod == 1) {
            pathName = context.getCacheDir().getPath(); //data/data/<application package>/cache目录   缓存
        } else if (pathMod == 2) {
            pathName = context.getFilesDir().getPath();//data/data/<application package>/files目录   数据
        }
        return pathName;
    }

    public static String HBMultiplication(String strA, String strB) {//乘法 , 字符串相乘
        BigDecimal b1 = new BigDecimal(strA);
        BigDecimal b2 = new BigDecimal(strB);
        return String.valueOf(b1.multiply(b2).intValue());
    }

    // 判断是否有root权限
    public static boolean hasRootPerssion() {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            if (value == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    //指定包名的程序是否安装,以及版本号是否正确
    //1 :未安装    2:已安装但版本不对   3:已安装且版本正确   4:已安装,未检测版本
    public static int isAppInstalled(Context context, String uri, int versionCode) {
        PackageManager pm = context.getPackageManager();
        int installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_UNINSTALLED_PACKAGES); //防止bind超过1M

            if (versionCode == 0) {//如果传递0,说明不判断版本号
                installed = 4;//已安装,未检测版本
            } else { //有传递版本号
                if (pm.getPackageInfo(uri, 0).versionCode == versionCode) {//如果传递的版本号和指定的版本号一样
                    installed = 3; //已安装且版本正确
                } else {
                    installed = 2;//已安装但版本不对
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            installed = 1;//未安装
        }
        return installed;
    }

    //复制文件. 返回复制成功或失败
    public static boolean copyFile(String oldPath, String newPath) {

        boolean isCopyState = false;
        try {
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
                isCopyState = true;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
        return isCopyState;
    }

    public static boolean isAppInstalled(Context context, String uri) {//传递的是包名
        return isAppInstalled(context, uri, 0) == 4;
    }

    public static void root_input(String str) {
        execRootCmd("input text " + str + " " + "\n");
    }

    public static void root_keyevent(String str) {
        execRootCmd("input keyevent " + str + " " + "\n");
    }

    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ZFLog.i("执行命令: " + cmd + ", result =" + result);
        return result;
    }

    public static String getXmlStr(String filePath, String element, String keyStr, String valStr) {
        String valueStr = "";
        File file = new File(filePath);
        try {
            FileInputStream in = new FileInputStream(file); //读入文件
            SAXReader saxReader = new SAXReader(); //初始化
            Document document = saxReader.read(in);//初始化
            Element root = document.getRootElement();//获取根元素
            List strList = root.elements(element);
            for (Object elementTemp : strList) {
                Element elementStr = (Element) elementTemp;
                if (elementStr.attributeValue(keyStr).equals(valStr)) {
                    valueStr = elementStr.getText();
                    break;
                }
            }
        } catch (Exception e) {
            ZFLog.i("获取xml文件失败,抛出异常");
            e.printStackTrace();
            return "null";
        }
        return valueStr;
    }

    public static String ESSubstr(String s, String pre, String suf) {  // 取出中间文本
        if (s.contains(pre) && s.contains(suf)) {
            if ((s.indexOf(pre) + pre.length()) >= 0) { //怪异的错误
                return s.substring((s.indexOf(pre) + pre.length()), s.indexOf(suf));
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String stamp2time(int sta) {
        String tmpStr = sta / 3600 > 0 ? String.valueOf(sta / 3600) + ":" : "";//有小时
        sta = sta % 3600;//取余数
        tmpStr += sta / 60 > 0 ? String.valueOf(sta / 60) : "0";//分钟
        tmpStr += ":" + sta % 60;
        return tmpStr;
    }

    public static String getToday_dd() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd");
        Date date = new Date();
        return format.format(date);
    }

    public static String getToday_yyyy_MM_dd() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return format.format(date);
    }

    //打开app  Core.ESOpenApp(esdojson.getString("package"), getApplicationContext());
    public static boolean ESOpenApp(String appName, Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public static String doubleAdd(String s1, String s2) {
        double a = Double.valueOf(s1) * 100;
        double b = Double.valueOf(s2) * 100;
        return String.valueOf(handDouble((a + b) / 100));
    }

    public static double handDouble(double f) {
        BigDecimal bg = new BigDecimal(f);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String HH_MM_SS(long times) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(times);
        return format.format(date);
    }

    public static long getStamp() {//时间戳 13位
        return new Date().getTime();
    }

    public static String getTenStamp(long stamp) {//时间戳 10位
        return String.valueOf(stamp).substring(0, 10);
    }

    public static void ImageCrop(String filePath,int x,int y,int width,int height){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        bitmap=  Bitmap.createBitmap(bitmap, x, y, width, height, null, false);
        File file = new File(filePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
