package com.gxtc.huchuan.http;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.gxtc.huchuan.Constant;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadApiBuild {

    public static volatile Retrofit retrofit;
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (DownloadApiBuild.class) {
                if (retrofit == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(Constant.Url.YZ_BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

}
