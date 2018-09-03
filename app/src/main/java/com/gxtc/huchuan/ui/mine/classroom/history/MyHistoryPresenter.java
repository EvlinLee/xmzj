package com.gxtc.huchuan.ui.mine.classroom.history;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.data.MyHistoryResitory;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:观看过的课程历史记录
 * Created by ALing on 2017/3/28 .
 */

public class MyHistoryPresenter implements MyHistoryContract.Presenter {

    private MyHistoryContract.Source mData;
    private MyHistoryContract.View mView;
    private int start = 0;
    private String clickType ;

    public MyHistoryPresenter(MyHistoryContract.View mView,String clickType ) {
        this.mView = mView;
        this.mView.setPresenter(this);
        this.clickType = clickType;
        mData = new MyHistoryResitory();
    }


    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
            clickType = MainActivity.STATISTICS_EMTPT;
        } else {
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(start,clickType, new ApiCallBack<List<UnifyClassBean>>() {

            @Override
            public void onSuccess(List<UnifyClassBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start +=15;
        mData.getData(start,MainActivity.STATISTICS_EMTPT, new ApiCallBack<List<UnifyClassBean>>() {

            @Override
            public void onSuccess(List<UnifyClassBean> data) {
                if(mView == null) return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void deleteChatUserRecord(String token, ArrayList<String> list) {
        if(mView == null) return;
        mView.showLoad();
        mData.deleteChatUserRecord(token, list, new ApiCallBack<List<UnifyClassBean>>() {
            @Override
            public void onSuccess(List<UnifyClassBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showDelResult();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getChatInfos(int id) {
        mView.showLoad();
        String token = UserManager.getInstance().getToken();
        Subscription sub =
                LiveApi.getInstance().getChatInfosBean(token, id + "")
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<ChatInfosBean>>(new ApiCallBack<ChatInfosBean>() {
                         @Override
                         public void onSuccess(ChatInfosBean data) {
                             if(mView != null){
                                 mView.showLoadFinish();
                                 mView.showChatInfoSuccess(data);
                             }
                         }

                         @Override
                         public void onError(String errorCode, String message) {
                             if(mView != null){
                                 mView.showLoadFinish();
                                 mView.showError(message);
                             }
                         }
                     }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }
}
