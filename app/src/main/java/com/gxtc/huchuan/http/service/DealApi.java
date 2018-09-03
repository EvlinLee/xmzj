package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.AllTypeBaen;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.FastDealBean;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.bean.OrderDetailedBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


public class DealApi {
    private static DealApi.Service instance;


    public interface Service {

        /**
         * 获取交易首页数据
         */
        @POST("publish/tradeInfo/listAndTypes.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<DealData>> getDealData(@FieldMap HashMap<String,String> map);

        /**
         * 获取交易列表
         */
        @POST("publish/tradeInfo/list.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealListBean>>> getDealList(@FieldMap HashMap<String,String> map);

        /**
         * 卖家处理买家退款申请的接口
         */
        @POST("publish/userRefund/sellerAgr.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> sellerAgr(@FieldMap HashMap<String,String> map);

        /**
         * 获取交易详情
         */
        @POST("publish/tradeInfo/getInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<GoodsDetailedBean>> getGoodsDetailed(@Field("token") String token, @Field("infoId") String infoId);

        /**
         * 获取交易筛选类型
         */
        @POST("publish/tradeInfo/listQuery.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<DealTypeBean>> getSubType(@Field("typeId") String typeId);


        /**
         * 获取交易留言
         */
        @POST("publish/tradeComment/list.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<GoodsCommentBean>>> getGoodsComment(@Field("token") String token, @Field("tradeInfoId") String infoId, @Field("start") String start);


        /**
         * 给卖家留言
         */
        @POST("publish/tradeComment/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> submitComment(@Field("token") String token, @Field("tradeInfoId") String infoId, @Field("comment") String comment);

        /**
         * 回复别人的留言
         */
        @POST("publish/tradeComment/reply.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> replyComment(@Field("token") String token, @Field("tradeCommentId") String commentId, @Field("comment") String comment, @Field("targetUserId") String targetUserId);


        /**
         * 获取订单列表
         */
        @POST("publish/tradeOrder/listByUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseListBean>>> getOrderList(@Field("token") String token, @Field("start") String start);

        /**
         * 获取订单列表
         * 0=ALL，1=话题，2=系列课，3=圈子，4=担保交易，5=商城
         */
        @POST("api/v%s/universalOrder/listByOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<AllPurchaseListBean>>> getOrderList(@Field("token") String token, @Field("type") String type, @Field("start") String start);


        /**
         * 获取自己发布的列表
         */
        @POST("publish/tradeInfo/listBySelf.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseListBean>>> getIssueList(@Field("token") String token, @Field("start") String start);


        /**
         * 获取快速交易的列表
         */
        @POST("publish/tradeInfo/listBySelf.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseListBean>>> getFastList(@Field("token") String token, @Field("start") String start, @Field("isAppointTr")int isAppointTr);


        /**
         * 获取订单详情
         */
        @POST("publish/tradeOrder/getOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<OrderDetailedBean>> getOrderDetailed(@Field("id") String id, @Field("token") String token);

        /**
         * 取消订单
         */
        @POST("publish/tradeOrder/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> cancelOrder(@Field("id") String id, @Field("token") String token);

        /**
         * 获取所有的分类
         */
        @POST("publish/tradeType/listAllType.do")
        Observable<ApiResponseBean<List<AllTypeBaen>>> getAllType();


        /**
         * 发布交易
         */
        @POST("publish/tradeInfo/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> issueDeal(@FieldMap Map<String,String> map);

        /**
         * 结束发布交易
         */
        @POST("publish/tradeInfo/endTrade.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> endDeal(@Field("token") String token, @Field("id") String id);

        /**
         * 获取收货地址
         */
        @POST("publish/takeAddr/list.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<AddressBean>>> getAddress(@Field("token") String token);

        /**
         * 删除收货地址
         */
        @POST("publish/takeAddr/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deletAddress(@Field("token") String token, @Field("id") String id);

        /**
         * 新建收货地址
         */
        @POST("publish/takeAddr/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> addAddress(@FieldMap HashMap<String,String> map);

        /**
         * 获取快速交易详情接口       提交订单接口，见文档 99
         */
        @POST("publish/tradeOrder/placeOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<FastDealBean>> getFastDetailed(@Field("token") String token, @Field("infoId") int id);

        /**
         * 提交订单接口     见文档 99
         */
        @POST("publish/tradeOrder/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<GoodsDetailedBean>> submitOrder(@FieldMap HashMap<String,String> map);

        /**
         * 卖家同意或拒绝交易接口     见文档 127
         */
        @POST("publish/tradeOrder/sellerAgree.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> agreeOrRejectDeal(@FieldMap HashMap<String,String> map);

        /**
         * 卖家确认交付/买家确认完成接口     见文档 128
         */
        @POST("publish/tradeOrder/change.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> confirmDeal(@FieldMap HashMap<String,String> map);

        /**
         * 获取聊天室接口     见文档 135
         */
        @POST("publish/tradeChat/getTradeChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ChatRoomBean>> getChatRoom(@Field("token") String token, @Field("orderId") String orderId);

        /**
         * 保存聊天室接口     见文档 134
         */
        @POST("publish/tradeChat/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveChatRoom(@FieldMap HashMap<String,String> map);

        /**
         * 获取用户退款申请列表     见文档 159
         */
        @POST("publish/userRefund/getUserRefundList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseListBean>>> getRefundList(@Field("token") String token, @Field("start") int start);
        /**
         * 用户退款     见文档 158
         */
        @POST("publish/userRefund/saveUserRefund.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveUserRefund(@FieldMap HashMap<String,String> map);

        /**
         * 退款申请     见文档 158
         */
        @POST("publish/userRefund/saveUserRefund.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> refund(@Field("token") String token, @Field("orderId") String orderId, @Field("type") int type,@Field("reason") String reason,@Field("money") String money);
        /**
         * 退款申请     见文档 158 用于订单里边的退款
         */
        @POST("publish/userRefund/saveUserRefund.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> refund(@FieldMap Map<String,String> map);

        /**
         * 删除自己发布的交易     见文档 126
         */
        @POST("publish/tradeInfo/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseListBean>>> delDeal(@Field("token") String token,
                                                    @Field("chatInfoId") ArrayList<String> list);

        @POST("publish/advert/getSlideList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealListBean>>> getSlideList(@Field("token") String token,@Field("type")String type);


        //获取交易子分类接口  35
        @POST("publish/tradeType/listSon.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealTypeBean>>> getDealSubType(@Field("typeId") int typeId);

        //获取交易自定义筛选接口  178
        @POST("publish/tradeInfo/listUdefs.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealTypeBean>>> getUdefType(@Field("typeId") int typeId);

        //筛选交易信息集合接口  179
        @POST("publish/tradeInfo/listNew.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealListBean>>> filterDealData(@FieldMap HashMap<String,String> map);

        //筛选交易信板块说明接口
        @POST("publish/tradeType/getPlateDescripte.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getPlateDescripte(@Field("tradeTypeId") String tradeTypeId);

        //删除交易留言接口  191
        @POST("publish/tradeComment/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteComment(@Field("token") String token , @Field("tradeCommentId") int id);

        //交易留言点赞接口  192
        @POST("publish/tradeComment/support.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> DzComment(@Field("token") String token , @Field("commentId") int id);

        //删除帖子接口  192
        @POST("publish/tradeInfo/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteDeal(@Field("token") String token , @Field("id") int id);

        //获取热门交易接口214
        @POST("publish/tradeInfo/listHot.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealListBean>>> getHotDealList(@Field("start") int start);


        //同意或拒绝交易
        @POST("publish/tradeInfo/disagreeFastTrade.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> disagreeFastTrade(@Field("token") String token, @Field("tradeInfoId") String tradeInfoId);


        //同意担保交易
        @POST("publish/tradeOrder/saveFastTradeOrder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<GoodsDetailedBean>> agreeFastTrade(@FieldMap Map<String, String> map);


        //申请担保交易
        @POST("publish/tradeInfo/saveFastTradeInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<GoodsDetailedBean>> applyGuarantee(@FieldMap Map<String, String> map);
    }

    public static DealApi.Service getInstance() {
        if (instance == null) {
            synchronized (DealApi.class) {
                if (instance == null)
                    instance = ApiBuild.getRetrofit().create(DealApi.Service.class);
            }
        }
        return instance;
    }
}
