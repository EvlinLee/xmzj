package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.VisitorBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.visitor.VisitorContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/18 .
 */

public class VisitorRepository extends BaseRepository implements VisitorContract.Source{
    @Override
    public void getData(String token, String start, ApiCallBack<List<VisitorBean>> callBack) {
        addSub(MineApi.getInstance().getUserBrowseList(token,start)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<VisitorBean>>>(callBack)));
    }

    @Override
    public void getUserBrowseCount(String token, ApiCallBack<VisitorBean> callBack) {
        addSub(MineApi.getInstance().getUserBrowseCount(token)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<VisitorBean>>(callBack)));
    }
}
