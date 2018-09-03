package com.gxtc.huchuan.ui.news;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.data.ShareMakeMoneyRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 */

public class ShareMakeMoneyPresenter implements ShareMakeMoneyContract.Presenter {
    private ShareMakeMoneyContract.Source mData;
    private ShareMakeMoneyContract.View mView;
    private int start = 0;


    public ShareMakeMoneyPresenter(ShareMakeMoneyContract.View mView ) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ShareMakeMoneyRepository();

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
    public void getData(final boolean isRefresh,String token, String type,String orderByType,String searchParm) {
        if (isRefresh) {
            start = 0;
        }
        if(mView == null)return;
        if (!isRefresh){
            mView.showLoad();
        }
        mData.getData(token,type,orderByType,start,searchParm, new ApiCallBack<List<ShareMakeMoneyBean>>() {

            @Override
            public void onSuccess(List<ShareMakeMoneyBean> data) {
                if(mView == null)return;
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
    public void getMMoneyList(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
        } else {
            if(mView == null)return;
            mView.showLoad();
        }

        mData.getMMoneyList(start, new ApiCallBack<List<PreProfitBean>>() {

            @Override
            public void onSuccess(List<PreProfitBean> data) {
                if(mView == null)return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showMoneyList(data);
                } else {
                    mView.showMoneyList(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {

                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore(String token, String type,String orderByType,String searchParm) {
        start += 15;
        mData.getData(token,type,orderByType,start,searchParm,new ApiCallBack<List<ShareMakeMoneyBean>>() {

            @Override
            public void onSuccess(List<ShareMakeMoneyBean> data) {
                if(mView == null)return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void loadMoreMoneyList() {
        start += 15;
        mData.getMMoneyList(start,new ApiCallBack<List<PreProfitBean>>() {

            @Override
            public void onSuccess(List<PreProfitBean> data) {
                if(mView == null)return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMoreMoneyList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)return;
                mView.showError(message);
            }
        });
    }
}
