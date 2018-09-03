package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.live.intro.TopicShareListContract;
import com.gxtc.huchuan.ui.news.NewsCollectContract;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 分享榜
 */

public class TopicShareListRepository extends BaseRepository implements TopicShareListContract.Source {


    @Override
    public void getData(int start, String chatRoomId, String chatInfoId, ApiCallBack<TopicShareListBean> callBack) {

        Subscription sub = MineApi.getInstance().getShareNotice(UserManager.getInstance().getToken(),
                "0", chatRoomId, chatInfoId, String.valueOf(start))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<TopicShareListBean>>(callBack));
        addSub(sub);

    }

    @Override
    public void getSeriesShareData(String id, int start, ApiCallBack<TopicShareListBean> callBack) {
        Subscription sub =
             LiveApi.getInstance()
                    .getSeriesShareData(UserManager.getInstance().getToken(), start, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<TopicShareListBean>>(callBack));
        addSub(sub);

    }
}
