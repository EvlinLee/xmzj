package com.gxtc.huchuan.ui.mine.account;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.data.AccountWaterRepository;
import com.gxtc.huchuan.data.TopicShareListRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.live.intro.TopicShareListContract;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 账户流水
 */

public class AccountWaterPresenter implements AccountWaterContract.Presenter {
    private AccountWaterContract.Source mData;
    private AccountWaterContract.View mView;
    private int start = 0;


    public AccountWaterPresenter(AccountWaterContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new AccountWaterRepository();
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
    public void getData() {
        mData.getData(start, new ApiCallBack<List<AccountWaterBean>>() {

            @Override
            public void onSuccess(List<AccountWaterBean> data) {
                if(mView == null ) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }
                mView.showData(data);

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
        mData.getData(start, new ApiCallBack<List<AccountWaterBean>>() {

            @Override
            public void onSuccess(List<AccountWaterBean> data) {
                if(mView == null ) return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null ) return;
                mView.showError(message);
            }
        });
    }
}
