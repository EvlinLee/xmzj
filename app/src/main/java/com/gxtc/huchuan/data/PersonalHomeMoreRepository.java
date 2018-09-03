package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.personalhomepage.more.PersonalHomePageMoreContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/4/10.
 */

public class PersonalHomeMoreRepository extends BaseRepository implements PersonalHomePageMoreContract.Source {

    @Override
    public void getHomePageGroupInfoList(ApiCallBack<List<CircleHomeBean>> callBack, String userCode, String token,String start) {
        addSub(MineApi.getInstance().getHomePageGroupInfoList(userCode,token,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<CircleHomeBean>>>(callBack)));
    }

    @Override
    public void dianZan(String token, int id, ApiCallBack<ThumbsupVosBean> callBack) {
        addSub(AllApi.getInstance()
                .support(token,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<ThumbsupVosBean>>(callBack)));
    }

    @Override
    public void getSelfNewsList(ApiCallBack<List<NewsBean>> callBack, String token, String start) {
        addSub(MineApi.getInstance().getSelfNews(token,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<NewsBean>>>(callBack)));
    }

    @Override
    public void getUserNewsList(ApiCallBack<List<NewsBean>> callBack, String userCode, String start) {
        addSub(MineApi.getInstance().getUserNewsList(userCode,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<NewsBean>>>(callBack)));
    }

    @Override
    public void getSelfChatInfoList(ApiCallBack<List<HomePageChatInfo>> callBack,String token, String start) {
        addSub(MineApi.getInstance().getSelfChatInfoList(token,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<HomePageChatInfo>>>(callBack)));
    }

    @Override
    public void getUserChatInfoList(ApiCallBack<List<HomePageChatInfo>> callBack, String userCode, String start) {
        addSub(MineApi.getInstance().getUserChatInfoList(userCode,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<HomePageChatInfo>>>(callBack)));
    }

    @Override
    public void getSelfDealList(ApiCallBack<List<DealListBean>> callBack, String token, String start) {
        addSub(MineApi.getInstance().getSelfDealList(token,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack)));
    }

    @Override
    public void getUserDealList(ApiCallBack<List<DealListBean>> callBack, String userCode, String start) {
        addSub(MineApi.getInstance().getUserDealList(userCode,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<DealListBean>>>(callBack)));
    }
}
