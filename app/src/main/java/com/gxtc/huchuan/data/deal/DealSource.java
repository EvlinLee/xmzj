package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.bean.AllTypeBaen;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.FastDealBean;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.OrderDetailedBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface DealSource extends BaseSource{

    void getData(HashMap<String, String> map, ApiCallBack<DealData> callBack);

    void putCache(String key, Serializable serializable);

    //获取商品详情信息
    void getGoodsDetailed(String token ,String infoId , ApiCallBack<GoodsDetailedBean> callBack);

    //获取商品留言信息
    void getComments(String token, String infoId,String start, ApiCallBack<List<GoodsCommentBean>> callBack);

    //提交留言
    void submitComment(String token, String infoId,String content, ApiCallBack<Object> callBack);

    //回复留言
    void replyComment(String token, String commentId,String content,String targetUserId, ApiCallBack<Object> callBack);

    //获取购买订单列表
    void getBuyOrderList(String token, String start, ApiCallBack<List<PurchaseListBean>> callBack);

    //获取发布列表
    void getIssueList(String token, String start, ApiCallBack<List<PurchaseListBean>> callBack);

    //获取快速交易列表
    void getFastList(String token, String start,int isAppointTr, ApiCallBack<List<PurchaseListBean>> callBack);

    //获取订单详情
    void getOrderDetailed(String id, String token, ApiCallBack<OrderDetailedBean> callBack);

    //取消订单
    void cancelOrder(String id, String token, ApiCallBack<Object> callBack);

    //获取所有的分类
    void getAllType(ApiCallBack<List<AllTypeBaen>> callBack);

    //发布交易
    void issueDeal(Map<String,String> map, ApiCallBack<Object> callBack);

    //结束交易订单
    void endDeal(String token, String id, ApiCallBack<Object> callBack);

    //获取收获地址
    void getAddress(String token , ApiCallBack<List<AddressBean>> callBack);

    //删除收货地址
    void deletAddress(String token, String id ,ApiCallBack<Object> callBack);

    //新建收货地址
    void addAddress(HashMap<String,String> map, ApiCallBack<Object> callBack);

    //获取快速交易详情
    void getFastDetailed(String token , int id, ApiCallBack<FastDealBean> callBack);

    //提交同意或拒绝
    void submitAgreeOrReject(HashMap<String,String> map, ApiCallBack<Object> callBack);

    //卖家确认交付／买家确认完成
    void confirmDeal(HashMap<String,String> map, ApiCallBack<Object> callBack);

    //获取聊天室接口
    void getChatRoom(String token, String tradeInfoId, ApiCallBack<ChatRoomBean> callBack);

    //保存聊天室接口
    void saveChatRoom(HashMap<String,String> map, ApiCallBack<Object> callBack);

    //获取退款列表
    void getRefundList(String token, int start, ApiCallBack<List<PurchaseListBean>> callBack);

    //删除交易
    void delDeal(String token, ArrayList<String> list, ApiCallBack<List<PurchaseListBean>> callBack);

    //取消首页数据加载请求
    void cancelRequest();

    //获取交易首面的广告
    void getAdvertise(String type, ApiCallBack<List<DealListBean>> callBack);

    void saveCollect(HashMap<String,String> map, ApiCallBack<Object> callBack);

    void deleteComment(String token, int id , ApiCallBack<Object> callBack);

    void DzComment(String token, int id , ApiCallBack<Object> callBack);

    void deleteDeal(String token, int id , ApiCallBack<Object> callBack);

    void getUserDealList(String token, String userCode, int start,ApiCallBack<List<DealListBean>> callBack);

    void disagreeFastTrade(String token, String tradInfoId, ApiCallBack<Object> callBack);

    //保存申请担保帖子
    void issueGuaranteeInfo(Map<String,String> map, ApiCallBack<GoodsDetailedBean> callBack);

    //创建担保交易订单接口
    void CreateGuaranteeTrade(Map<String,String> map, ApiCallBack<GoodsDetailedBean> callBack);
}
