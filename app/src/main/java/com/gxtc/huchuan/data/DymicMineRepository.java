package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.dymic.DymicMineContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/17 .
 */

public class DymicMineRepository extends BaseRepository implements DymicMineContract.Source {
    @Override
    public void getData(String token, String start, ApiCallBack<List<PersonalDymicBean>> callBack) {
        addSub(MineApi.getInstance().getListDynamic(token,start)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<PersonalDymicBean>>>(callBack)));
    }

    @Override
    public void delDymicList(String token, ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().deleteDymicList(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }
}
