package com.gxtc.huchuan.ui.circle.article;


import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 */

public class ArticleRepository extends BaseRepository implements ArticleContract.Source {


    @Override
    public void getData(int start, int groupId, String userCode,
            ApiCallBack<List<NewNewsBean.DataBean>> callBack) {
            Subscription sub = CircleApi.getInstance().getNewsInMine(userCode,start,groupId).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<List<NewNewsBean.DataBean>>>(callBack));
            addSub(sub);


    }

    @Override
    public void getSeachData(HashMap<String, String> map,
                             ApiCallBack<List<NewNewsBean.DataBean>> callBack) {
        Subscription sub = CircleApi.getInstance().searchNewsListByGroupId(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<NewNewsBean.DataBean>>>(callBack));
        addSub(sub);

    }
}
