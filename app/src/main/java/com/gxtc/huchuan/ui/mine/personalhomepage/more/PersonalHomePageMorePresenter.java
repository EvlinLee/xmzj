package com.gxtc.huchuan.ui.mine.personalhomepage.more;

import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.PersonalHomeMoreRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/10 .
 */

public class PersonalHomePageMorePresenter implements PersonalHomePageMoreContract.Presenter{
    private PersonalHomePageMoreContract.View mView;
    private PersonalHomePageMoreContract.Source mData;
    String token = UserManager.getInstance().getToken();
    private int start = 0;

    public PersonalHomePageMorePresenter(PersonalHomePageMoreContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new PersonalHomeMoreRepository();
    }
    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }

    @Override
    public void getHomePageGroupInfoList(String userCode,boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getHomePageGroupInfoList(new ApiCallBack<List<CircleHomeBean>>() {
            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showHomePageGroupInfoList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },userCode,token,start+"");
    }

    @Override
    public void dianZan(final int id) {
        String token = UserManager.getInstance().getToken();
        mData.dianZan(token, id, new ApiCallBack<ThumbsupVosBean>() {
            @Override
            public void onSuccess(ThumbsupVosBean data) {
                if(mView == null) return;
                mView.showDZSuccess(id);
            }

            @Override
            public void onError(String errorCode, String message) {

            }
        });
    }

    @Override
    public void getSelfNewsList(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getSelfNewsList(new ApiCallBack<List<NewsBean>>() {
            @Override
            public void onSuccess(List<NewsBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showSelfNewsList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },token,start+"");
    }

    @Override
    public void getUserNewsList(String userCode,boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getUserNewsList(new ApiCallBack<List<NewsBean>>() {
            @Override
            public void onSuccess(List<NewsBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showUserNewsList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },userCode,start+"");
    }

    @Override
    public void getSelfChatInfoList(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getSelfChatInfoList(new ApiCallBack<List<HomePageChatInfo>>() {
            @Override
            public void onSuccess(List<HomePageChatInfo> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showSelfChatInfoList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },token,start+"");
    }

    @Override
    public void getUserChatInfoList(String userCode,boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getUserChatInfoList(new ApiCallBack<List<HomePageChatInfo>>() {
            @Override
            public void onSuccess(List<HomePageChatInfo> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showUserChatInfoList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },userCode,start+"");
    }

    @Override
    public void getSelfDealList(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getSelfDealList(new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showSelfDealList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },token,start+"");
    }

    @Override
    public void getUserDealList(String userCode,boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getUserDealList(new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showUserDealList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },userCode,start+"");
    }


    @Override
    public void loadMrore(String type, String userCode) {
        start += 15;
        if(mView == null) return;
        switch (type){
            case "news":
                if (!TextUtils.isEmpty(userCode)){
//                    getUserNewsList(map);
                    mData.getUserNewsList(new ApiCallBack<List<NewsBean>>() {
                        @Override
                        public void onSuccess(List<NewsBean> data) {
                            if (data.size() == 0 || data == null ){
                                mView.showNoMore();
                                return;
                            }
                            mView.showLoadMoreNewsList(data);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            start += 15;
                            ErrorCodeUtil.handleErr(mView,errorCode,message);
                        }
                    },userCode,start+"");

                }else {
//                    getSelfNewsList(map);
                    mData.getSelfNewsList(new ApiCallBack<List<NewsBean>>() {
                        @Override
                        public void onSuccess(List<NewsBean> data) {
                            mView.showLoadFinish();
                            if (data == null || data.size() == 0){
                                mView.showNoMore();
                                return;
                            }
                            mView.showLoadMoreNewsList(data);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            start += 15;
                            ErrorCodeUtil.handleErr(mView,errorCode,message);
                        }
                    },token,start+"");
                }

                break;
            case "chatInfo":
                if (!TextUtils.isEmpty(userCode)){
//                    getUserChatInfoList(map);
                    mData.getUserChatInfoList(new ApiCallBack<List<HomePageChatInfo>>() {
                        @Override
                        public void onSuccess(List<HomePageChatInfo> data) {
                            if (data == null || data.size() == 0){
                                mView.showNoMore();
                                return;
                            }
                            mView.showLoadMoreChatInfoList(data);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            start += 15;
                            ErrorCodeUtil.handleErr(mView,errorCode,message);
                        }
                    },userCode,start+"");
                }else {
//                    getSelfChatInfoList(map);
                    mData.getSelfChatInfoList(new ApiCallBack<List<HomePageChatInfo>>() {
                        @Override
                        public void onSuccess(List<HomePageChatInfo> data) {
                            mView.showLoadFinish();
                            if (data == null || data.size() == 0){
                                mView.showNoMore();
                                return;
                            }
                            mView.showLoadMoreChatInfoList(data);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            start += 15;
                            ErrorCodeUtil.handleErr(mView,errorCode,message);
                        }
                    },token,start+"");
                }
                break;
            case "dymic":
                if (!TextUtils.isEmpty(userCode)){
                    mData.getHomePageGroupInfoList(new ApiCallBack<List<CircleHomeBean>>() {
                        @Override
                        public void onSuccess(List<CircleHomeBean> data) {
                            mView.showLoadFinish();
                            if (data == null || data.size() == 0){
                                mView.showNoMore();
                                return;
                            }
                            mView.showLoadMoreHomePageGroupInfoList(data);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            start += 15;
                            ErrorCodeUtil.handleErr(mView,errorCode,message);
                        }
                    },userCode,token,start+"");
                }else {
                    mData.getHomePageGroupInfoList(new ApiCallBack<List<CircleHomeBean>>() {
                        @Override
                        public void onSuccess(List<CircleHomeBean> data) {
                            mView.showLoadFinish();
                            if (data == null || data.size() == 0){
                                mView.showNoMore();
                                return;
                            }
                            mView.showLoadMoreHomePageGroupInfoList(data);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            start += 15;
                            ErrorCodeUtil.handleErr(mView,errorCode,message);
                        }
                    },userCode,token,start+"");
                }
                break;
        }
    }
}
