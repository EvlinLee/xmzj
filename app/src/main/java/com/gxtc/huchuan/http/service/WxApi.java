package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.WxResponse;
import com.gxtc.huchuan.http.ApiBuild;

import java.util.HashMap;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


public class WxApi {
    private static WxApi.Service instance;


    public interface Service {

        @FormUrlEncoded
        @POST("cgi-bin/bizlogin?action=startlogin")
        Observable<WxResponse> login(@FieldMap HashMap<String,String> map);

        @FormUrlEncoded
        @GET("cgi-bin/verifycode")
        Observable<WxResponse> getVerifcode(@Query("username") String userName, @Query("r") String currTime);

    }

    public static WxApi.Service getInstance() {
        if (instance == null) {
            synchronized (WxApi.class) {
                if (instance == null)
                    instance = ApiBuild.getWxRetrofit().create(WxApi.Service.class);
            }
        }
        return instance;
    }
}
