package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.BrowseHistoryBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.browsehistory.BrowseHistoryContract;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/16 .
 */

public class BrowseHistoryRepository extends BaseRepository implements BrowseHistoryContract.Source{
    @Override
    public void getData(String token, String start,ApiCallBack<List<BrowseHistoryBean>> callBack) {
        addSub(MineApi.getInstance().getRecordList(token,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<BrowseHistoryBean>>>(callBack)));
    }

    @Override
    public void deleteBrowseRecord(String token,ArrayList<String> list, ApiCallBack<List<Object>> callBack) {
        addSub(MineApi.getInstance().deleteUserBrowse(token,list)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<Object>>>(callBack)));
    }

}
