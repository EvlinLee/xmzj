package com.gxtc.huchuan.ui.mine.circle.applyfor;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ApplyForBean;
import com.gxtc.huchuan.data.deal.ApplyMenberListRepository;

import com.gxtc.huchuan.http.ApiCallBack;


import java.util.List;

/**
 * Created by sjr on 2017/6/10.
 */

public class ApplyMemberListPresenter implements ApplyForMemberListContract.Presenter {
    private ApplyForMemberListContract.Source mData;
    private ApplyForMemberListContract.View mView;
    private int start = 0;
    private int groupId;

    public ApplyMemberListPresenter(int groupId, ApplyForMemberListContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ApplyMenberListRepository();
        this.groupId = groupId;
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
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
        } else {
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(groupId, start, new ApiCallBack<List<ApplyForBean>>() {

            @Override
            public void onSuccess(List<ApplyForBean> data) {
                if(mView == null) return;
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
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(groupId,start, new ApiCallBack<List<ApplyForBean>>() {

            @Override
            public void onSuccess(List<ApplyForBean> data) {
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
}

