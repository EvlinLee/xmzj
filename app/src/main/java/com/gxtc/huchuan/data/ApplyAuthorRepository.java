package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.news.applyauthor.ApplyAuthorContract;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/4/1 0001.
 */

public class ApplyAuthorRepository extends BaseRepository implements ApplyAuthorContract.Source {
    @Override
    public void applayAuthor(ApiCallBack<Object> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().saveAuthorInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }




}
