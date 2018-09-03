package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.news.ShareMakeMoneyContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 */

public class ShareMakeMoneyRepository extends BaseRepository implements ShareMakeMoneyContract.Source {


    @Override
    public void getData(String token, String type,String orderByType,int start,String searchParm, ApiCallBack<List<ShareMakeMoneyBean>> callBack) {

        Subscription sub = AllApi.getInstance().shareMakeMoney(token, type,orderByType,start+"",searchParm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ShareMakeMoneyBean>>>(callBack));
        addSub(sub);
    }

    @Override
    public void getMMoneyList(int start, ApiCallBack<List<PreProfitBean>> callBack) {
        Subscription sub = AllApi.getInstance().getSaleInviteList(UserManager.getInstance().getToken(), String.valueOf(start))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<PreProfitBean>>>(callBack));
        addSub(sub);
    }

}
