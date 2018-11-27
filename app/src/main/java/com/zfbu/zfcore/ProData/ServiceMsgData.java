package com.zfbu.zfcore.ProData;


import java.io.Serializable;

public class ServiceMsgData implements Serializable {
    private int what; //类似messanger的
    private String str1;

    public ServiceMsgData(int what) {
        this.what = what;
    }

    public ServiceMsgData(int what, String str1){
        this.what = what;
        this.str1 = str1;
    }


    public int getWhat() {
        return this.what;
    }

    public String getStr1(){
        return  this.str1;
    }
}
