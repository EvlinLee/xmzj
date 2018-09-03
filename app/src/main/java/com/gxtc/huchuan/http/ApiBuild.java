package com.gxtc.huchuan.http;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.cookies.ReadCookiesInterceptor;
import com.gxtc.huchuan.utils.MD5Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuild {

    private static Retrofit retrofit;
    private static Retrofit wxRetrofit;
    private static Retrofit baseRetrofit;

    private final static int CONNECT_TIMEOUT = 30;    //连接超时时间
    private final static int READ_TIMEOUT = 30;       //读取超时时间
    private final static int WRITE_TIMEOUT = 60 * 2;      //写的超时时间
    public static String Tag = "ApiBuild";

    private ApiBuild() {

    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (ApiBuild.class) {
                if (retrofit == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    if (com.gxtc.commlibrary.Constant.DEBUG) {
                        builder.addNetworkInterceptor(new StethoInterceptor());
                    }

                    OkHttpClient client = builder
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)        //设置读取超时时间
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(new TokenIntercept())
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    String t = System.currentTimeMillis() + "";
                                    String sign = MD5Util.MD5Encode(t + Constant.KEY, "");
                                    Request request = chain.request().newBuilder()
                                            .addHeader("t", t)
                                            .addHeader("sign", sign)

                                            .build();

                                    return chain.proceed(request);
                                }
                            })
                            .sslSocketFactory(HttpsUtils.getSslSocket2())
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(Constant.Url.BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static Retrofit getWxRetrofit() {

        if (wxRetrofit == null) {
            synchronized (ApiBuild.class) {
                if (wxRetrofit == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new ReadCookiesInterceptor())
                            .addNetworkInterceptor(new StethoInterceptor())
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                            .build();

                    wxRetrofit = new Retrofit.Builder()
                            .baseUrl(Constant.Url.WX_BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
                }
            }
        }
        return wxRetrofit;
    }


    public static Retrofit getBaseRetrofit() {
        if (baseRetrofit == null) {
            baseRetrofit = new Retrofit.Builder()
                    .baseUrl(Constant.Url.BASE_URL)
                    .build();
        }
        return baseRetrofit;
    }

}
