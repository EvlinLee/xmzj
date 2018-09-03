package com.gxtc.huchuan.http;

import android.text.TextUtils;

import com.gxtc.huchuan.Constant;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/4.
 */
public class TokenIntercept implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String oldUrl = request.url().toString();

        if(!TextUtils.isEmpty(oldUrl) && oldUrl.contains("api/v")){
            String newUrl = String.format(Locale.CHINA, oldUrl, Constant.Url.API_VER);
            HttpUrl.Builder urlBuilder = request.url().newBuilder(newUrl);

            Request.Builder requestBuider = request.newBuilder();
            if(urlBuilder != null){
                requestBuider.url(urlBuilder.build());
            }

            Request newRequest = requestBuider.build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(request);
    }

}
