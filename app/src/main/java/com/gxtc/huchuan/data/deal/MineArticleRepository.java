package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.mine.news.MineArticleContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 自己写的文章
 */

public class MineArticleRepository extends BaseRepository implements MineArticleContract.Source {

    @Override
    public void getData(final int start, final ApiCallBack<List<NewsBean>> callBack) {
        String token = UserManager.getInstance().getToken();
        Subscription sub = AllApi.getInstance().getMineArticle(token, String.valueOf(start),"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<NewsBean>>>(callBack));
        addSub(sub);
    }

}
