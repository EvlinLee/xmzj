package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.news.NewsCollectContract;
import com.gxtc.huchuan.ui.news.VideoNewsContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 视频页
 * id随意传
 */

public class VideoNewsRepository extends BaseRepository implements VideoNewsContract.Source {

    @Override
    public void getData(final int start, final ApiCallBack<List<NewsBean>> callBack) {

        String token = null;
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
        }
        Subscription sub = AllApi.getInstance().getNews(token, "", String.valueOf(start),"", "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<NewsBean>>>(callBack));
        addSub(sub);
    }

}
