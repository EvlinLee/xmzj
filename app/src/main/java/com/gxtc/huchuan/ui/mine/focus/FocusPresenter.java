package com.gxtc.huchuan.ui.mine.focus;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 关注列表
 */

public class FocusPresenter implements FocusContract.Presenter {
    private FocusContract.Source mData;
    private FocusContract.View mView;
    private int start = 0;
    private String id;
    private String groupChatId = "";

    public FocusPresenter(FocusContract.View mView, String id) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new FocusRepository();
        this.id = id;
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        mData.destroy();
    }


    @Override
    public void getData(final boolean isRefresh,String groupChatId) {
        this.groupChatId = groupChatId;
        if (isRefresh) {
            start = 0;
        } else {
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(start, id,groupChatId, new ApiCallBack<List<FocusBean>>() {

            @Override
            public void onSuccess(List<FocusBean> data) {
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
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start +=15;
        mData.getData(start, id,groupChatId, new ApiCallBack<List<FocusBean>>() {

            @Override
            public void onSuccess(List<FocusBean> data) {
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
