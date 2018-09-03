package com.gxtc.huchuan.data.deal;

import android.text.TextUtils;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.bean.AllTypeBaen;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.FastDealBean;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.OrderDetailedBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.DealApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.deal.deal.DealFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DealRepository extends BaseRepository implements DealSource{

    private Subscription mSub;

    @Override
    public void getData(HashMap<String,String> map,final ApiCallBack<DealData> callBack) {
        mSub = DealApi.getInstance()
                      .getDealData(map)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<DealData>>(callBack));

        addSub(mSub);
    }

    @Override
    public void putCache(final String key, final Serializable serializable) {
        addSub(Observable
                .create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        ACache.get(MyApplication.getInstance()).put(key,serializable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public void getGoodsDetailed(String token ,String infoId, ApiCallBack<GoodsDetailedBean> callBack) {
        Subscription sub = DealApi.getInstance().getGoodsDetailed(token,infoId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<GoodsDetailedBean>>(callBack));

        addSub(sub);
    }

    @Override
    public void getComments(String token, String infoId,String start, ApiCallBack<List<GoodsCommentBean>> callBack) {
        Subscription sub = DealApi.getInstance().getGoodsComment(token,infoId,start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<GoodsCommentBean>>>(callBack));

        addSub(sub);
    }

    @Override
    public void submitComment(String token, String infoId, String content, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().submitComment(token,infoId,content)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void replyComment(String token, String commentId, String content, String targetUserId, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().replyComment(token,commentId,content,targetUserId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void getBuyOrderList(String token, String start, final ApiCallBack<List<PurchaseListBean>> callBack) {
        Subscription sub = DealApi.getInstance().getOrderList(token,start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseListBean>>>(callBack));

        addSub(sub);
    }

    @Override
    public void getIssueList(String token, String start, final ApiCallBack<List<PurchaseListBean>> callBack) {
        Subscription sub = DealApi.getInstance().getIssueList(token,start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseListBean>>>(callBack));

        addSub(sub);
    }

    @Override
    public void getFastList(String token, String start,int isAppointTr, final ApiCallBack<List<PurchaseListBean>> callBack) {
        Subscription sub = DealApi.getInstance().getFastList(token,start,isAppointTr)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseListBean>>>(callBack));

        addSub(sub);
    }

    @Override
    public void getOrderDetailed(String id, String token, ApiCallBack<OrderDetailedBean> callBack) {
        Subscription sub = DealApi.getInstance().getOrderDetailed(id,token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<OrderDetailedBean>>(callBack));

        addSub(sub);
    }

    @Override
    public void cancelOrder(String id, String token, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().cancelOrder(id,token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void getAllType(ApiCallBack<List<AllTypeBaen>> callBack) {
        Subscription sub = DealApi.getInstance().getAllType()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<AllTypeBaen>>>(callBack));

        addSub(sub);
    }

    @Override
    public void issueDeal(Map<String, String> map, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().issueDeal(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void endDeal(String token, String id, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().endDeal(token,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void getAddress(String token, ApiCallBack<List<AddressBean>> callBack) {
        Subscription sub = DealApi.getInstance().getAddress(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<AddressBean>>>(callBack));

        addSub(sub);
    }

    @Override
    public void deletAddress(String token, String id, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().deletAddress(token,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void addAddress(HashMap<String,String> map, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().addAddress(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void getFastDetailed(String token, int id, ApiCallBack<FastDealBean> callBack) {
        Subscription sub = DealApi.getInstance().getFastDetailed(token,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<FastDealBean>>(callBack));

        addSub(sub);
    }

    @Override
    public void submitAgreeOrReject(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().agreeOrRejectDeal(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void confirmDeal(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().confirmDeal(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void getChatRoom(String token, String orderId, ApiCallBack<ChatRoomBean> callBack) {
        Subscription sub = DealApi.getInstance().getChatRoom(token,orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<ChatRoomBean>>(callBack));

        addSub(sub);
    }

    @Override
    public void saveChatRoom(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance().saveChatRoom(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));

        addSub(sub);
    }

    @Override
    public void getRefundList(String token, final int start, final ApiCallBack<List<PurchaseListBean>> callBack) {
        Subscription sub = DealApi.getInstance()
                                  .getRefundList(token,start)
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribeOn(Schedulers.io())
                                  .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseListBean>>>(callBack));
        addSub(sub);

    }

    @Override
    public void delDeal(String token, ArrayList<String> list, ApiCallBack<List<PurchaseListBean>> callBack) {
        Subscription sub = DealApi.getInstance()
                .delDeal(token,list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseListBean>>>(callBack));
        addSub(sub);
    }

    @Override
    public void cancelRequest() {
        if (mSub != null) {
            mSub.unsubscribe();
        }
    }

    @Override
    public void getAdvertise(String type, ApiCallBack<List<DealListBean>> callBack) {
        addSub(DealApi.getInstance()
                      .getSlideList("",type)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack)));
    }

    @Override
    public void saveCollect(HashMap<String,String> map, ApiCallBack<Object> callBack) {
        addSub(AllApi.getInstance()
                     .saveCollection(map)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void deleteComment(String token, int id, ApiCallBack<Object> callBack) {
        addSub(DealApi.getInstance()
                     .deleteComment(token,id)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void DzComment(String token, int id, ApiCallBack<Object> callBack) {
        addSub(DealApi.getInstance()
                      .DzComment(token,id)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void deleteDeal(String token, int id, ApiCallBack<Object> callBack) {
        addSub(DealApi.getInstance()
                      .deleteDeal(token,id)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void getUserDealList(String token, String userCode, int start, ApiCallBack<List<DealListBean>> callBack) {
        if(TextUtils.isEmpty(userCode)){
            addSub(MineApi.getInstance().getSelfDealList(token, start + "")
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack)));
        }else{
            addSub(MineApi.getInstance().getUserDealList(userCode, start + "")
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack)));
        }
    }

    @Override
    public void disagreeFastTrade(String token, String tradInfoId, ApiCallBack<Object> callBack) {
        addSub(DealApi.getInstance()
                    .disagreeFastTrade(token, tradInfoId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void issueGuaranteeInfo(Map<String, String> map, ApiCallBack<GoodsDetailedBean> callBack) {
        addSub(DealApi.getInstance()
                      .applyGuarantee(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<GoodsDetailedBean>>(callBack)));
    }

    @Override
    public void CreateGuaranteeTrade(Map<String, String> map, ApiCallBack<GoodsDetailedBean> callBack) {
        addSub(DealApi.getInstance()
                      .agreeFastTrade(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<GoodsDetailedBean>>(callBack)));
    }


}
