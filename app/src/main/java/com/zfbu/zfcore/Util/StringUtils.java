package com.zfbu.zfcore.Util;

public class StringUtils {

    public static boolean isEmpty(String content){
        if(null == content){
            return  true;
        }else if(content.length() == 0){
            return  true;
        }

        return  false;

    }
}
