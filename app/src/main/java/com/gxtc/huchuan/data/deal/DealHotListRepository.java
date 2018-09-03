package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;
import com.gxtc.huchuan.ui.deal.deal.dealList.DealListContract;
import com.gxtc.huchuan.ui.deal.deal.hotdeallist.HotListContract;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/30 0030.
 */

public class DealHotListRepository extends BaseRepository implements HotListContract.Source {


    @Override
    public void getData( final int start,final ApiCallBack<List<DealListBean>> callBack) {
        Subscription sub = DealApi.getInstance()
                .getHotDealList(start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack));
        addSub(sub);
    }
}
