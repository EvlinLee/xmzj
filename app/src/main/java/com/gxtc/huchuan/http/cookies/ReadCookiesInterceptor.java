package com.gxtc.huchuan.http.cookies;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.MyApplication;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用来加入cookie
 */
public class ReadCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Application app = MyApplication.getInstance();
        Headers headers = new Headers.Builder()
                .add("Referer", "https://mp.weixin.qq.com/")
                .add("User-Agent", getUserAgent(app))
                .build();

        HttpUrl url;

        HttpUrl.Builder builder = chain.request().url().newBuilder();
        url = builder.build();
        Request newRequest = chain.request().newBuilder().headers(headers).url(url).build();

        Response response;
        response = chain.proceed(newRequest);
        LogUtil.i("response : " + response.toString());

        return response;
    }


    private String getUserAgent(Context context) {
        if (Build.VERSION.SDK_INT < 19)
            return "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 5 Build/KVT49L) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
        return WebSettings.getDefaultUserAgent(context);
    }

}