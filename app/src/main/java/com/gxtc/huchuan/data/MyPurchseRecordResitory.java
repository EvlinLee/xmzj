package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.purchaserecord.PurchaseRecordContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/4/24 .
 */

public class MyPurchseRecordResitory extends BaseRepository implements PurchaseRecordContract.Source{

    @Override
    public void getSeriesAndTopic(String token, String start,
            ApiCallBack<List<PurchaseSeriesAndTopicBean>> callBack) {
        addSub(MineApi.getInstance().getSeriedAndChatOrderList(token, String.valueOf(start))
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseSeriesAndTopicBean>>>(callBack)));
    }

//    @Override
//    public void getToicData(String token, String start, ApiCallBack<List<PurchaseTopicRecordBean>> callBack) {
//        Subscription sub = MineApi.getInstance().getChatOrderList(token, String.valueOf(start))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseTopicRecordBean>>>(callBack));
//        addSub(sub);
//    }
//
//    @Override
//    public void getSeriesData(String token, String start, ApiCallBack<List<PurchaseSeriesRecordBean>> callBack) {
//        Subscription sub = MineApi.getInstance().getChatSeriesOrderList(token, String.valueOf(start))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseSeriesRecordBean>>>(callBack));
//        addSub(sub);
//    }

    @Override
    public void getCircleData(String token, String start,
            ApiCallBack<List<PurchaseCircleRecordBean>> callBack) {
        Subscription sub = MineApi.getInstance().getGroupOrder(token, String.valueOf(start))
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseCircleRecordBean>>>(callBack));
        addSub(sub);
    }



    @Override
    public void getDealData(String token, String start,
            ApiCallBack<List<PurchaseListBean>> callBack) {
        Subscription sub = DealApi.getInstance().getOrderList(token, String.valueOf(start))
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new ApiObserver<ApiResponseBean<List<PurchaseListBean>>>(callBack));
        addSub(sub);
    }

    @Override
    public void getAllOrderData(String token, String type, String start, ApiCallBack<List<AllPurchaseListBean>> callBack) {

            Subscription sub = DealApi.getInstance().getOrderList(token, type, String.valueOf(start))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<AllPurchaseListBean>>>(callBack));
            addSub(sub);
    }
}
