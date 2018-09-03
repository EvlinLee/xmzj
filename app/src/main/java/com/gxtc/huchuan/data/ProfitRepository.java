package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.bean.DistributionCountBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.incomedetail.profit.classroom.ProfitContract;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/5 .
 */

public class ProfitRepository extends BaseRepository implements ProfitContract.Source {
  /*  @Override
    public void getProfitList(String token, String start, String type, ApiCallBack<List<DistributionBean>> callBack) {
        addSub(MineApi.getInstance().getProfitList(token,start,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<DistributionBean>>>(callBack)));
    }
*/
    @Override
    public void getDistributionList(String token, String start, String type,String dateType, ApiCallBack<List<DistributionBean>> callBack) {
        addSub(MineApi.getInstance().getDistributionList(token,start,type,dateType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<DistributionBean>>>(callBack)));
    }


    @Override
    public void getDistributionCount(String token, String type, String dateType,ApiCallBack<DistributionCountBean> callBack) {
        addSub(MineApi.getInstance().getDistributionCount(token,type,dateType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<DistributionCountBean>>(callBack)));
    }

    @Override
    public void getIncomeStatistics(HashMap<String, String> map,
            ApiCallBack<DistributionCountBean> callBack) {
        addSub(MineApi.getInstance().getIncomeStatistics(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<DistributionCountBean>>(callBack)));
    }

    @Override
    public void getlistByDistributionBusiness(HashMap<String, String> map,
                                              ApiCallBack<List<DistributionBean>> callBack) {
        addSub(MineApi.getInstance().getlistByDistributionBusiness(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<DistributionBean>>>(callBack)));
    }

    @Override
    public void getlistByDistributionBusinessSum(String token, String type, String dateType,ApiCallBack<DistributionCountBean> callBack) {
        addSub(MineApi.getInstance().getlistByDistributionBusinessSum(token,type,dateType)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<DistributionCountBean>>(callBack)));
    }

    @Override
    public void getCreateUserIncome(String token, String type, String dataTpe,
                                    ApiCallBack<DistributionCountBean> callBack) {
        addSub(MineApi.getInstance().getCreateUserIncome(token,type,dataTpe)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<DistributionCountBean>>(callBack)));
    }

    @Override
    public void getProfitList(String token, String start, String type,String dateType, ApiCallBack<List<DistributionBean>> callBack) {
        addSub(MineApi.getInstance().getProfitList(token,start,type,dateType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<DistributionBean>>>(callBack)));
    }

    @Override
    public void getToatalProfitList(String token, String start, String type, String dateType,
                                    ApiCallBack<List<DistributionBean>> callBack) {
        addSub(MineApi.getInstance().getToatalProfitList(token,start,type,dateType)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<DistributionBean>>>(callBack)));
    }
}
