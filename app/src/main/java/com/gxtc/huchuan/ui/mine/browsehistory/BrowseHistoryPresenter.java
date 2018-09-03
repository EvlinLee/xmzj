package com.gxtc.huchuan.ui.mine.browsehistory;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.BrowseHistoryBean;
import com.gxtc.huchuan.data.BrowseHistoryRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/16 .
 */

public class BrowseHistoryPresenter implements BrowseHistoryContract.Presenter {

    private BrowseHistoryContract.Source mData;
    private BrowseHistoryContract.View mView;

    private int start = 0;
    private String token = UserManager.getInstance().getToken();

    public BrowseHistoryPresenter(BrowseHistoryContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new BrowseHistoryRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void getData(final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(token,0+"", new ApiCallBack<List<BrowseHistoryBean>>() {

            @Override
            public void onSuccess(List<BrowseHistoryBean> data) {
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
    public void deleteBrowseRecord(String token, ArrayList<String> list) {
        mView.showLoad();
        mData.deleteBrowseRecord(token,list, new ApiCallBack<List<Object>>() {
            @Override
            public void onSuccess(List<Object> data) {
                mView.showLoadFinish();
                mView.showDelResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMore() {
        start += 15;

        mData.getData(token,start+"", new ApiCallBack<List<BrowseHistoryBean>>() {
            @Override
            public void onSuccess(List<BrowseHistoryBean> data) {
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                mView.showLoad();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
