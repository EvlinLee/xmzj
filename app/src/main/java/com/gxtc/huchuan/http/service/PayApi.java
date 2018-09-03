package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.AccountSetInfoBean;
import com.gxtc.huchuan.bean.pay.AccountSet;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.util.HashMap;
import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


public class PayApi {
    private static PayApi.Service instance;


    public interface Service {

        //获取支付订单接口  文档104
        @POST("publish/pay/createOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<OrdersResultBean>> getOrder(
                @FieldMap HashMap<String, String> map);

        //157.	重新支付接口
        @POST("api/v%s/pay/repay.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<OrdersResultBean>> repay(
                @FieldMap HashMap<String, String> map);


        /**
         * 133.验证订单是否支付成功接口
         *
         * @param outTradeNo
         * @return
         */
        @POST("publish/pay/validateOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> validateOrder(@Field("out_trade_no") String outTradeNo);


        /**
         * 149.获取用户提现账户列表及收费比例接口
         *
         * @param token
         * @return
         */
        @POST("publish/expRecd/getAccountSetInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<AccountSetInfoBean>> getAccountSetInfo(
                @Field("token") String token);

        /**
         * 226.	获取账户列表接口
         * @param token
         * @return
         */
        @POST("publish/expRecd/getAccountSetList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<AccountSet>>> getAccountSet(
                @Field("token") String token);
        /**
         * 226.	获取账户列表接口
         * @param token
         * @return
         */
        @POST("publish/accountSet/delAccountSet.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteAccountSet(
                @Field("token") String token, @Field("id") String id);


        /**
         * token	是	用户token
         * money	是	提现金额
         * withdrawCashType	是	提现方式（1：微信；2：支付宝；3：银行卡）
         * userAccount	是	用户账号
         * userName	否	实名名字
         * 当withdrawCashType==3时必传
         * openingBank	否	开户行
         * 当withdrawCashType==3时必传
         * midFee	是	中间费
         *
         * @param map
         * @return
         */
        @POST("publish/expRecd/saveExpRecd.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> saveExpRecd(@FieldMap HashMap<String, String> map);


    }

    public static PayApi.Service getInstance() {
        if (instance == null) {
            synchronized (PayApi.class) {
                if (instance == null)
                    instance = ApiBuild.getRetrofit().create(PayApi.Service.class);
            }
        }
        return instance;
    }
}
