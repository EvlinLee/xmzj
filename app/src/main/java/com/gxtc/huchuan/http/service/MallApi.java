package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.AllStatusSumsBean;
import com.gxtc.huchuan.bean.CoustomMerBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.CategoryBean;
import com.gxtc.huchuan.bean.MallDetailBean;
import com.gxtc.huchuan.bean.MallShopCartBean;
import com.gxtc.huchuan.bean.OrderBean;
import com.gxtc.huchuan.bean.OrderDetailBean;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


public class MallApi {
    private static volatile MallApi.Service instance;


    public interface Service {

        //255.	获取商城轮播图
        @POST("publish/storeCarouse/listByCarouse.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MallBean>>> listByCarouse(@Field("pageSize") int pageSize);

        //256.	获取商城菜单按钮/列表/商品展示块  默认：0、0：菜单；1：列表；2：展示块
        @POST("publish/storeMenu/show.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MallBean>>> storeMenu(@Field("start") int start,@Field("sort") int sort);

        //257.	获取商品详情
        @POST("publish/store/storeInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<MallDetailBean>> getGoodsDetailed(@Field("storeId") String id, @Field("token") String token);

        //258.	添加购物车
        @POST("publish/store/addShopcart.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> addShopcart(@FieldMap HashMap<String,String> map);

        //261.	分类获取商品列表
        @POST("publish/store/merchandiseList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<CategoryBean>>> merchandiseList(@FieldMap HashMap<String,String> map);

        //265	获取所有商城订单接口
        @POST("publish/store/getAllOrderList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<OrderBean>>> getAllOrderList(@FieldMap HashMap<String,String> map);

        //270.	删除商品订单接口
        @POST("publish/store/deleteOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteOrder(@Field("token") String token, @Field("orderNo") String orderNo);

        //266.	确认订单收货
        @POST("publish/store/confirmOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> confirmOrder(@Field("token") String token, @Field("orderNo") String orderNo);

        //267.	商城个人中心订单数显示接口
        @POST("publish/store/getAllStatusSums.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<AllStatusSumsBean>>> getAllStatusSums(@Field("token") String token);

        //262.	获取客服列表接口  0：全局客服1：商城客服 2：交易客服 3：app客服  rand  0：列表 1：随机
        @POST("publish/member/getIMServiceList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<CoustomMerBean>>> getIMServiceList(@Field("type") String type,@Field("rand") String rand);

        //268.	订单详情接口
        @POST("publish/store/getOrderDetail.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<OrderDetailBean>> getOrderDetail(@Field("token") String token,@Field("orderNo") String orderNo);

        //269.	商品搜索接口
        @POST("publish/store/searchStore.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<CategoryBean>>> searchStore(@FieldMap HashMap<String,String> map);


        //259.  获取购物车列表
        @POST("publish/store/shopcartList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MallShopCartBean>>> getShopCartList(@Field("token") String token);


        //260.  删除购物车商品
        @POST("publish/store/removeShopcart.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> removeGoods(@Field("token") String token, @Field("storePriceId") String id);



    }

    public static MallApi.Service getInstance() {
        if (instance == null) {
            synchronized (MallApi.class) {
                if (instance == null)
                    instance = ApiBuild.getRetrofit().create(MallApi.Service.class);
            }
        }
        return instance;
    }
}
