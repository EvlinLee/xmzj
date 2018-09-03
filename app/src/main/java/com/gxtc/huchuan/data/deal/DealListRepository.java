package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;
import com.gxtc.huchuan.ui.deal.deal.dealList.DealListContract;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DealListRepository extends BaseRepository implements DealListContract.Source {


    @Override
    public void getData(HashMap<String, String> map,
            final ApiCallBack<List<DealListBean>> callBack) {
        Subscription sub = DealApi.getInstance()
                                  .filterDealData(map)
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribeOn(Schedulers.io())
                                  .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack));
        addSub(sub);
    }

    @Override
    public void getPlateDescripte(String tradeTypeId, ApiCallBack<Object> callBack) {
        Subscription sub = DealApi.getInstance()
                                  .getPlateDescripte(tradeTypeId)
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribeOn(Schedulers.io())
                                  .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));
        addSub(sub);
    }

    /*@Override
    public void getSubType(String typeId, ApiCallBack<DealTypeBean> callBack) {
        Subscription sub =
                DealApi.getInstance().getSubType(typeId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<DealTypeBean>>(callBack));
        addSub(sub);
    }*/

//    @Override
//    public void getHotlist(int start,
//                        final ApiCallBack<List<DealListBean>> callBack) {
//        Subscription sub = DealApi.getInstance()
//                .getHotDealList(start)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack));
//        addSub(sub);
//    }
}
