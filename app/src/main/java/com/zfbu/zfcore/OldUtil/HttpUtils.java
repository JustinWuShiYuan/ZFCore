package com.zfbu.zfcore.OldUtil;

import com.zfbu.zfcore.Util.CustomGsonConverterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtils {

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constance.baseUrl)
            .addConverterFactory(CustomGsonConverterFactory.create())
            .client(CookieUtil.getClient())
            .build();
}
