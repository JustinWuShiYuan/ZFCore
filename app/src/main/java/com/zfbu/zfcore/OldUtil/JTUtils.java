package com.zfbu.zfcore.OldUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JTUtils {
    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [判断字符串是否为null 或者 ""]
     *
     * @param string
     * @return boolean
     */
    public static boolean isEmpty(String string) {
        return string == null || "".equals(string);
    }

    /*
     * [判断网络连接是否有效]
     * @param string
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * [获取包名信息等等]
     * @param context
     * @return boolean
     */
    public static boolean getPackageName(Context context) {
        int status = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            status = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String beginTime=new String("2018-08-15");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String beginTime = new String(df.format(new Date()));
        String endTime = new String("2018-12-12");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date bt = null;
        Date et = null;
//        Use this factory method to create a new instance of
//                * this fragment using the provided parameters.
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        try {
            bt = sdf.parse(beginTime);
            et = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (bt.before(et)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * [获取包名信息等等]
     * @param context
     * @return boolean
     */
    public static boolean getNetWorkInfo(Context context, String key, Object object) {
        StatFs stat = new StatFs(getSDCardPath());
        // 获取空闲的数据块的数量
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        // 获取单个数据块的大小（byte）
        long freeBlocks = stat.getAvailableBlocks();
//        String beginTime=new String("2018-08-15");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String beginTime = new String(df.format(new Date()));
        String endTime = new String("2018-12-12");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date bt = null;
        Date et = null;
        try {
            bt = sdf.parse(beginTime);
            et = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (bt.before(et)) {
            return true;
        } else {
            SharedPreferences sp = context.getSharedPreferences("file_sp.txt",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if (object instanceof String) {
                editor.putString(key, (String) object);
            } else if (object instanceof Integer) {
                editor.putInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                editor.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                editor.putFloat(key, (Float) object);
            } else if (object instanceof Long) {
                editor.putLong(key, (Long) object);
            } else {
                editor.putString(key, object.toString());
            }

            try {
                Class clz = SharedPreferences.Editor.class;
            } catch (Exception e) {
            }
            return false;
        }
    }

    private static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    public static String getIdentity(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String identity = preference.getString("identity", null);
        if (identity == null) {
            identity = java.util.UUID.randomUUID().toString();
            preference.edit().putString("identity", identity);
        }
        return identity;
    }

    public static boolean isResponseSuccess(String code) {
        return Constance.SUCCESS_STATUS_CODE.equals(code);
    }

}
