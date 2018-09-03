package com.gxtc.huchuan.ui.circle.topic;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.data.deal.CircleDynamicManagerRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 动态管理
 */

public class CircleDynamicManagerPresenter implements CircleDynamicManagerContract.Presenter {
    private CircleDynamicManagerContract.Source mData;
    private CircleDynamicManagerContract.View mView;
    private int start = 0;
    private int groudId;
    private long timeStamp;
    public CircleDynamicManagerPresenter(CircleDynamicManagerContract.View mView, int groudId) {
        this.mView = mView;
        this.mView.setPresenter(this);
        this.groudId = groudId;
        timeStamp = System.currentTimeMillis();
        mData = new CircleDynamicManagerRepository();
    }

    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }


    @Override
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
            timeStamp = System.currentTimeMillis();
        } else {
            mView.showLoad();
        }

        mData.getData(groudId,start,timeStamp, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
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
        start += 15;
        mData.getData(groudId,start,timeStamp, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null)   return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }
}
