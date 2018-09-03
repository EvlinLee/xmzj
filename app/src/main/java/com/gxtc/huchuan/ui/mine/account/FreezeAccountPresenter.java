package com.gxtc.huchuan.ui.mine.account;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.FreezeAccountBean;
import com.gxtc.huchuan.data.FreezeAccountRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 未结算金额
 */

public class FreezeAccountPresenter implements FreezeAccountContract.Presenter {

    private FreezeAccountContract.Source mData;
    private FreezeAccountContract.View mView;
    private int start = 0;

    public FreezeAccountPresenter(FreezeAccountContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new FreezeAccountRepository();
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
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(start, new ApiCallBack<List<FreezeAccountBean>>() {

            @Override
            public void onSuccess(List<FreezeAccountBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }
                if(isRefresh){
                    mView.showRefreshFinish(data);
                }else{
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(start, new ApiCallBack<List<FreezeAccountBean>>() {

            @Override
            public void onSuccess(List<FreezeAccountBean> data) {
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
