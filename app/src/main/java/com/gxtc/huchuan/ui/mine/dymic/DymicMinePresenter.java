package com.gxtc.huchuan.ui.mine.dymic;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.data.DymicMineRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/17 .
 */

public class DymicMinePresenter implements DymicMineContract.Presenter{
    private DymicMineContract.Source mData;
    private DymicMineContract.View mView;

    private int start = 0;
    private String token = UserManager.getInstance().getToken();

    public DymicMinePresenter(DymicMineContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DymicMineRepository();
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

        mData.getData(token,start+"", new ApiCallBack<List<PersonalDymicBean>>() {

            @Override
            public void onSuccess(List<PersonalDymicBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return ;
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
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void delDymicList(String token) {
        if(mView == null) return;
        mView.showLoad();
        mData.delDymicList(token, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showDelResult(data);
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
    public void loadMore() {
        start += 15;

        mData.getData(token,start+"", new ApiCallBack<List<PersonalDymicBean>>() {
            @Override
            public void onSuccess(List<PersonalDymicBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                start += 15;
                mView.showLoad();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
