package com.zfbu.zfcore.UI.Login;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginBiz {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("login")
    Call<LoginResponseInfo> login(@Body RequestBody requestBody);
}
