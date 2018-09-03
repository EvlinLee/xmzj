package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.incomedetail.InComeDetailNewContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/21 .
 */

public class InComeDetailNewRepository extends BaseRepository implements InComeDetailNewContract.Source{
    @Override
    public void getTotalIncomeStatistics(String token, String dateType,
            ApiCallBack<InComeAllCountBean> callBack) {
        addSub(MineApi.getInstance().getTotalIncomeStatistics(token,dateType)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<InComeAllCountBean>>(callBack)));
    }

    @Override
    public void getNewTotalIncomeStatistics(String token, String dateType,
                                            ApiCallBack<InComeAllCountBean> callBack) {
        addSub(MineApi.getInstance().getNewTotalIncomeStatistics(token,dateType)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<InComeAllCountBean>>(callBack)));
    }

    @Override
    public void getIncomeList(String token, String start, String type,String isAdded ,String dateType,
            ApiCallBack<List<AccountWaterBean>> callBack) {
        addSub(MineApi.getInstance().userStreamList(token,start,type,isAdded,dateType)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<AccountWaterBean>>>(callBack)));
    }

    @Override
    public void getNewIncomeList(String token, String start, String type, String dateType,
                                 ApiCallBack<List<AccountWaterBean>> callBack) {
        addSub(MineApi.getInstance().listBusinessIncomeRecordByType(token,start,type,dateType)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<AccountWaterBean>>>(callBack)));
    }
}
