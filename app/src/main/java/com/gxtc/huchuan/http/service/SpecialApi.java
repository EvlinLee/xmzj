package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.data.ArticleSpecialBean;
import com.gxtc.huchuan.data.SpecialBean;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zzg on 2018/5/24
 */
public class SpecialApi {
    public static volatile SpecialApi.Service instance;

    public interface Service {
        /**
         * 307 获取专题列表
         */
        @POST("api/v%s/newsSpecial/list.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SpecialBean>>> newsSpecial(@Field("token") String token, @Field("start") int start);

        /**
         * 312.	获取已订阅的专题列表接口
         * @param token
         * @param start
         * @return
         */
        @POST("api/v%s/newsSpecialSubscribe/listBySubscription.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SpecialBean>>> listBySubscription(@Field("token") String token, @Field("start") int start);

        /**
         * 308.	获取专题详细信息
         * @param token
         * @param id
         * @return
         */
        @POST("api/v%s/newsSpecial/get.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<SpecialBean>> getSpecialDetail(@Field("token") String token, @Field("id") String id);

        /**
         * 309.	获取专题文章/视频列表接口
         */
        @POST("api/v%s/newsSpecial/listNewsByNewsSpecial.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ArticleSpecialBean>>> listNewsByNewsSpecial(@FieldMap Map<String,String> map);

        /**
         * 310.    专题订阅接口
         */
        @POST("api/v%s/newsSpecialSubscribe/subscription.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> subscription(@Field("token") String token, @Field("id") String id);
    }

    public static SpecialApi.Service getInstance() {
        if(instance == null){
            synchronized (SpecialApi.class){
                if(instance == null){
                    instance  = ApiBuild.getRetrofit().create(SpecialApi.Service.class);
                }
            }
        }
        return  instance;

    }

}
