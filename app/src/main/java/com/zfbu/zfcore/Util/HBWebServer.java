package com.zfbu.zfcore.Util;


import android.annotation.SuppressLint;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HBWebServer {
    //https://github.com/square/okhttp

    private OkHttpClient client = new OkHttpClient().newBuilder()
            .proxy(Proxy.NO_PROXY)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20,TimeUnit.SECONDS)
            .build();//防抓包

    public String get(String url) {
        return get(url, null);
    }

    @SuppressLint("NewApi")
    public String get(String url, Map<String, String> joinMap) {
        Request.Builder builder = new Request.Builder();
        if (joinMap != null) {
            for (Map.Entry<String, String> entry : joinMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());  //构造参数
            }
        }
        Request request = builder.url(url).build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.string();
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String post(String url, Map<String, String> joinMap) {
        return post(url, joinMap, null);
    }

    @SuppressLint("NewApi")
    public String post(String url, Map<String, String> joinMap, Map<String, String> headMap) {

        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : joinMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());  //构造参数
        }
        Request.Builder rBuilder = new Request.Builder();
        if (headMap != null) {
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                rBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = rBuilder
                .url(url)
                .post(builder.build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.string();
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String post_json(String url, String jsonStr) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    return responseBody.string();
                } else {
                    return "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
