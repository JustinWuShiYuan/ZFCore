package com.zfbu.zfcore.OldUtil.encryption;





import com.zfbu.zfcore.OldUtil.SPUtil;

import java.security.MessageDigest;

/**
 * 加密的工具类
 */
public class RsaUtils {
        //测试环境秘钥
//    public static String PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3AdN6A9+Nqhusa3U2wbTQCYOxK/afiXDWE64GU8id5FMvBd2/X3HzRNoLAA4K8lB4TRMpMHtOfkXBLM6ePukUovkg2QKiq+rtzKsd6FeH1LT1zRIl1FaJsoUpZtZ5jzPjYABueNSa5IRJwBoHsdKvJNlalKpiOrE3M1osSifhFQIDAQAB";
//    public static String PRA_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIsFvLgEzOG9rH4eyy2MyQ/KvYSkGCpDALHrruxCxQooRNs/tIOIqtwiDYsCjDKdAmxL94TlvxwWIqqviKcZk9JVc+uOJX8/dsoBiUJCZBKvBn6G5kuEPxmxfHcMuBPh2LostBTa17crhflYi/8+Ax0WbOkkbBbL4UBBOrc9JENlAgMBAAECgYAxtChnxleV8eFk6g+MRoRwH/UoIh79pRzvf6r3j1uxKPqNRLZ+PHOR5p7e71c+lUIThAmHuzhbi1JzofSCgbOGfd7YZpD40RJfBpNLdHG4R6glIx2WOJl9z65Oen7mTMJh/xNDYU1UlOYGsEiDZfUDM/nLoOJ3m30xD+nYC0ApVQJBAL1URrvah1EG0s/EoeBsSqOrjFxyOOoMzDqVlWx4ObbVdYgiP0A6Rzx/RwTkovNf0cqZxfYkH7VKutdInVcUYIsCQQC7+mJd609QjE5Ioc5T/fUb1mXM8YHE7hbHiwyTuXUb6rtQEUcVP4g63Z7Xxqe6dTf9mMbUUWC1GaU029iSqfnPAkEAhydiVzfo4S69zxPPeyXTIoOT72qatnnsUOX3hTdN+/blsAjCnlb8A3PYfuW4bYQ5fyfG6a9f80sllgmASGmDgQJADOJgk6dpTFARnZDZn5AsmLfdSmrTRjg7f3ncZtOzXzJ7nEVS5fXw1HgURx3qqTEXoV4zYqIhSshzNmhLM+JKqQJBAK1oCZo610YcOe9TQn4stnwo+VDwCilkYktOyz6RjpsugyW6weYy4Hg3Q5VnAJhd6aQ3TJQuhXCf/nPX9C6Y93I=";

    //正式环境钥匙
    public static String PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3AdN6A9+Nqhusa3U2wbTQCYOxK/afiXDWE64GU8id5FMvBd2/X3HzRNoLAA4K8lB4TRMpMHtOfkXBLM6ePukUovkg2QKiq+rtzKsd6FeH1LT1zRIl1FaJsoUpZtZ5jzPjYABueNSa5IRJwBoHsdKvJNlalKpiOrE3M1osSifhFQIDAQAB";
    public static String PRA_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIsFvLgEzOG9rH4eyy2MyQ/KvYSkGCpDALHrruxCxQooRNs/tIOIqtwiDYsCjDKdAmxL94TlvxwWIqqviKcZk9JVc+uOJX8/dsoBiUJCZBKvBn6G5kuEPxmxfHcMuBPh2LostBTa17crhflYi/8+Ax0WbOkkbBbL4UBBOrc9JENlAgMBAAECgYAxtChnxleV8eFk6g+MRoRwH/UoIh79pRzvf6r3j1uxKPqNRLZ+PHOR5p7e71c+lUIThAmHuzhbi1JzofSCgbOGfd7YZpD40RJfBpNLdHG4R6glIx2WOJl9z65Oen7mTMJh/xNDYU1UlOYGsEiDZfUDM/nLoOJ3m30xD+nYC0ApVQJBAL1URrvah1EG0s/EoeBsSqOrjFxyOOoMzDqVlWx4ObbVdYgiP0A6Rzx/RwTkovNf0cqZxfYkH7VKutdInVcUYIsCQQC7+mJd609QjE5Ioc5T/fUb1mXM8YHE7hbHiwyTuXUb6rtQEUcVP4g63Z7Xxqe6dTf9mMbUUWC1GaU029iSqfnPAkEAhydiVzfo4S69zxPPeyXTIoOT72qatnnsUOX3hTdN+/blsAjCnlb8A3PYfuW4bYQ5fyfG6a9f80sllgmASGmDgQJADOJgk6dpTFARnZDZn5AsmLfdSmrTRjg7f3ncZtOzXzJ7nEVS5fXw1HgURx3qqTEXoV4zYqIhSshzNmhLM+JKqQJBAK1oCZo610YcOe9TQn4stnwo+VDwCilkYktOyz6RjpsugyW6weYy4Hg3Q5VnAJhd6aQ3TJQuhXCf/nPX9C6Y93I=";

//    public static String MD5_KEY = "86A47A0F207277AEB8395BC3D79EEF58";
//    public static String MD5_KEY = (String) SPUtil.get(SPUtil.MD5_KEY,"");


    public static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static String CHARSET = "utf-8";
    private static int enSegmentSize = 117;//加密长度
    private static int deSegmentSize = 128;//解密长度

    /**
     * 解密的方法
     *
     * @param data
     * @return
     */
    public static String dencipher(String data) {
        RsaHelper rsa = new RsaHelper();
        String deTxt = rsa.decipher(data, PRA_KEY, deSegmentSize);
        return deTxt;
    }

    /**
     * 加密的方法
     *
     * @param data
     * @return
     */
    public static String encipher(String data) {
        RsaHelper rsa = new RsaHelper();
        String ciphertext = rsa.encipher(data, PUB_KEY, enSegmentSize);
        return ciphertext;
    }

    /**
     * md5加密的方法
     *
     * @param s
     * @param
     * @return
     */
    public final static String MD5(String s) {
        try {
//            86A47A0F207277AEB8395BC3D79EEF58
            String msg = s + (String) SPUtil.get(SPUtil.MD5_KEY,"");//新的
//            String msg = s + "86A47A0F207277AEB8395BC3D79EEF58";//旧的
            byte[] btInput = msg.getBytes(CHARSET);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public final static String MD5SignIn(String s) {
        try {
            String msg = s + "86A47A0F207277AEB8395BC3D79EEF58";
            byte[] btInput = msg.getBytes(CHARSET);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
