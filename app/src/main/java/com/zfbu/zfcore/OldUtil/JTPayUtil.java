package com.zfbu.zfcore.OldUtil;

import android.util.Log;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.jt.pay.jtpay.model.user.UserInfo;

public class JTPayUtil {

    private static final String USERNAME = "userName";
    private static final String USERID = "userId";
//    private static final String ZFB = "ZFB";

    public static boolean isLogin() {
        return !JTUtils.isEmpty(getUserName());
    }

//    public static void saveUserName(UserInfo userInfo) {
//        if (!JTUtils.isEmpty(userInfo.getZFB())) {
//            SPUtil.put(ZFB, userInfo.getZFB());
//        }
//    }
//    public static void saveZFB(UserInfo userInfo) {
//        if (!JTUtils.isEmpty(userInfo.getZFB())) {
//            SPUtil.put(ZFB, userInfo.getZFB());
//        }
//    }
    public static String getUserName() {
        if (SPUtil.get(SPUtil.LOGIN_NAME, "") != null) {
            return String.valueOf(SPUtil.get(SPUtil.LOGIN_NAME, ""));
        } else {
            return null;
        }
    }

    public static String getUserId() {
        if (SPUtil.get(USERID, "") != null) {
            return String.valueOf(SPUtil.get(USERID, ""));
        } else {
            return null;
        }
    }

//    public static void saveUserId(UserInfo userInfo) {
//        if (!JTUtils.isEmpty(userInfo.getUserId())) {
//            SPUtil.put(USERID, userInfo.getUserId());
//        }
//    }

    public static String getNotificationMoney(String content){ //https://blog.csdn.net/tuesdayma/article/details/76412800
        //先判断有没有整数，如果没有整数那就肯定就没有小数
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(content);
        String result = "";
        if (m.find()) {
            Map<Integer, String> map = new TreeMap<>();
            Pattern p2 = Pattern.compile("(\\d+\\.\\d+)");
            m = p2.matcher(content);
            //遍历小数部分
            while (m.find()) {
                result = m.group(1) == null ? "" : m.group(1);
                int i = content.indexOf(result);
                String s = content.substring(i, i + result.length());
                map.put(i, s);
                //排除小数的整数部分和另一个整数相同的情况下，寻找整数位置出现错误的可能，还有就是寻找重复的小数
                // 例子中是排除第二个345.56时第一个345.56产生干扰和寻找整数345的位置时，前面的小数345.56会干扰
                content = content.substring(0, i) + content.substring(i + result.length());
            }
            //遍历整数
            Pattern p3 = Pattern.compile("(\\d+)");
            m = p3.matcher(content);
            while (m.find()) {
                result = m.group(1) == null ? "" : m.group(1);
                int i = content.indexOf(result);
                //排除jia567.23.23在第一轮过滤之后留下来的jia.23对整数23产生干扰
                if (String.valueOf(content.charAt(i - 1)).equals(".")) {
                    //将这个字符串删除
                    content = content.substring(0, i - 1) + content.substring(i + result.length());
                    continue;
                }
                String s = content.substring(i, i + result.length());
                map.put(i, s);
                content = content.substring(0, i) + content.substring(i + result.length());
            }
            result = "";
            for (Map.Entry<Integer, String> e : map.entrySet()) {
                result += e.getValue() + ",";
            }
            result = result.substring(0, result.length()-1);
            String[] str = result.split(",");
            result = str[str.length - 1];
        } else {
            result = "";
        }
        Log.e("GTPayService", "getNotificationMoney: "+ result );
        return result;
    }
}
