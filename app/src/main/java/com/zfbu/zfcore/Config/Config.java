package com.zfbu.zfcore.Config;


public class Config {

    public static String service_url = "http://www.zfbu.com/api.php";
    public static String userData = "";//token
    public static String userName = "";//用户登录名
    public static String nowAppId = ""; //现在在使用的appid(暂未启用
    public static String aliUserId;//aliid
    public static String tmpAppid = "";//重启防护用的(暂未启用)

    public static String proceedsZFBNum = "";     //收款支付宝账号
    public static String businessNum = "";//商户号
    public static String appKey = "";//appKey

    public static boolean readState = false; //文件读写权限
    public static int rootState = 1;//1:未检测  2:检测通过  3:检测失败
    public static boolean restState = false; //是否30分钟检测
    public static boolean proState = false; //是否保护程序
    public static int proType = 0; //保护状态id     0:未开启保护 1:等待开启保护   2:保护开启成功  3:等待关闭
    public static boolean isOpenControl = false; // 是否开启监控

    public static boolean serviceIsOpen = false; //服务是否开启
    public static boolean helpIsOpen = false; //辅助是否开启
    public static boolean controlIsOpen = false; //监控是否开启

    public static boolean order_page_list_hasopen = false; //第三页的订单列表是否打开

}
