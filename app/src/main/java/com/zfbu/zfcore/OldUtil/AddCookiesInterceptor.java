package com.zfbu.zfcore.OldUtil;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
//        String preferences = this.getInstance().getApplicationContext().getSharedPreferences("config",
//                this.getInstance().getApplicationContext().MODE_PRIVATE).getString("cookie", null);
//        if (preferences != null) {
//            builder.addHeader("Cookie", preferences);
//        }
//        builder.addHeader("Cookie", String.valueOf(SPUtil.get(SPUtil.LOGIN_COOKIE,null)));
//        Log.e("AddCookiesInterceptor", "intercept: " + String.valueOf(SPUtil.get(SPUtil.LOGIN_COOKIE,null)) );
        return chain.proceed(builder.build());
    }
}
