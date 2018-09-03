package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.profitList.DistributionContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/4 .
 */

public class DistributionRepository extends BaseRepository implements DistributionContract.Source{
    @Override
    public void getDistributionList(String token, String start, String type, String dateType,ApiCallBack<List<DistributionBean>> callBack) {
        addSub(MineApi.getInstance().getDistributionList(token,start,type,dateType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<DistributionBean>>>(callBack)));
    }
}
