package com.zfbu.zfcore.OldUtil;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostPayInfoBiz {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("order/payOrder")
    Call<ResponseInfo> getPostPayInfo(@Body RequestBody requestBody);


}
