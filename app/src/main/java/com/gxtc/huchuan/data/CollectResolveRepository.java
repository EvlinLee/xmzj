package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.collectresolve.CollectResolveContract;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveContract;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 17/3/7.
 */

public class CollectResolveRepository extends BaseRepository implements CollectResolveContract.Source{

    @Override
    public void getArticleType(ApiCallBack<ChannelBean> callBack) {
        Subscription sub = AllApi.getInstance()
                .getNewsChannels("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<ChannelBean>>(callBack));
        addSub(sub);
    }

    @Override
    public void saveArticle(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().saveArticle(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void saveCollection(Map<String, String> map, ApiCallBack callBack) {
        AllApi.getInstance().saveCollection(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(callBack));
    }
}
