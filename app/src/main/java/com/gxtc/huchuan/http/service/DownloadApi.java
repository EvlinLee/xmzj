package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.http.DownloadApiBuild;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;


public class DownloadApi {
    private static volatile DownloadApi.Service instance;


    public interface Service {

        @Streaming
        @GET
        Observable<ResponseBody> downloadFile(@Url String url);

    }

    public static DownloadApi.Service getInstance() {
        if (instance == null) {
            synchronized (DownloadApi.class) {
                if (instance == null)
                    instance = DownloadApiBuild.getRetrofit().create(DownloadApi.Service.class);
            }
        }
        return instance;
    }
}
