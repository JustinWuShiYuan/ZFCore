package com.zfbu.zfcore.OldUtil;

public class Constance {
//    http://120.79.158.163:8040  192.168.202.7:8082 http://120.79.158.163:8040
public static final String baseUrl = "http://callback.65011688.com";
//    public static final String baseUrl = "http://192.168.23.207:8555";

//public static final String baseUrl = "http://api.godeng.com"; //正式
//    public static final String baseUrl = "http://192.168.202.16:8555"; //pie

    //    public static final String baseUrl = "http://120.79.158.163:8060"; //测试
//    public static final String baseUrl = "http://192.168.202.7:8555"; //chris 内网
//    public static final String baseUrl = "http://120.79.158.163:8060";
//    public static final String baseUrl = "http://192.168.12.209:8020/";
//    public static final String baseUrl = "http://192.168.12.155:8020/";
    public static final String SUCCESS_STATUS_CODE = "200";
    public static final int PAYTYPE_WX = 0;
    public static final int PAYTYPE_QQ = 1;
    public static final int PAYTYPE_ZFB = 2;
//    public static final String PACKAGE_NAME_WX = "com.tencent.mm";
    public static final String PACKAGE_NAME_WX = "com.eg.android.AlipayGphone";
    public static final String NOTIFICATION_TITLE_WX = "微信支付";
    public static final String NOTIFICATION_TITLE_WX_PROXY = "微信收款助手";
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String NOTIFICATION_TITLE_QQ = "QQ钱包";
    public static final String PACKAGE_NAME_ZFB = "com.eg.android.AlipayGphone";
    public static final String NOTIFICATION_TITLE_ZFB = "支付宝通知";
    public static final String[] PAYTYPE_NAME = {"WX", "QQ", "ZFB"};

    // 支付宝包名
    public static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";

    public static final String LOG_DIR = "/aliqrcode_log/";

    // 加号id = com.alipay.mobile.base.commonbiz:id/launcher_title_search_item_bg
    public static final String OPEN_MENU_ID = "com.alipay.mobile.base.commonbiz:id/launcher_title_search_item_bg";
    // 收钱id =com.alipay.mobile.ui:id/item_name
    public static final String COLLECT_MONEY = "com.alipay.mobile.ui:id/item_name";

    // 设置金额的id = payee_QRCodePayModifyMoney
    public static final String SET_MONEY_ID = "com.alipay.mobile.payee:id/payee_QRCodePayModifyMoney";

    // 添加收款理由 id = payee_QRAddBeiZhuLink
    public static final String ADD_GATHER_REASON_ID = "com.alipay.mobile.payee:id/payee_QRAddBeiZhuLink";

    // 金额的relativelayout id = payee_QRmoneySetInput
    public static final String MONEY_RELATIVELAYOUT_ID = "com.alipay.mobile.payee:id/payee_QRmoneySetInput";

    // 理由 relativelayout id = payee_QRmoneySetBeiZhuInput
    public static final String REASON_RELATIVELAYOUT_ID = "com.alipay.mobile.payee:id/payee_QRmoneySetBeiZhuInput";

    // 确定 id = payee_NextBtn
    public static final String SUBMIT_ID = "com.alipay.mobile.payee:id/payee_NextBtn";

    //保存图片
    public static final String SAVE_PICTURE = "com.alipay.mobile.payee:id/payee_save_qrcode";

    public static final int QRCODE_TOTAL_COUNT = 10;

}
