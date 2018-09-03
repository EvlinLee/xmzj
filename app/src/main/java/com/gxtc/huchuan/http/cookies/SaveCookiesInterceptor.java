package com.gxtc.huchuan.http.cookies;

import com.gxtc.commlibrary.Constant;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.huchuan.MyApplication;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 用来保存Cookies
 */
public class SaveCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            SpUtil.putSet(MyApplication.getInstance(),Constant.COOKIE, cookies);
        }

        return originalResponse;
    }

}