package com.gxtc.huchuan.ui.mine.incomedetail;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.data.InComeDetailNewRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
* Created by zzg on 2017/12/30 .
 */

public class NewInComeDetailPresenter implements NewInComeDetailContract.Presenter {

    private InComeDetailNewContract.Source mData;
    private NewInComeDetailContract.View   mView;

    private int start = 0;

    public NewInComeDetailPresenter(NewInComeDetailContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new InComeDetailNewRepository();
    }

    @Override
    public void getTotalIncomeStatistics(String dateType, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
            mView.showLoad();
        }

        String token = UserManager.getInstance().getToken();
        mData.getNewTotalIncomeStatistics(token, dateType, new ApiCallBack<InComeAllCountBean>() {
            @Override
            public void onSuccess(InComeAllCountBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(isRefresh){
                    mView.showRefreshTotalIncomeStatisticsFinish(data);
                }else{
                    mView.showTotalIncomeStatistics(data);
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
    public void getIncomeList(String type,String isAdded, String dateType, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }
        String token = UserManager.getInstance().getToken();
        mData.getNewIncomeList(token,start+"",type,dateType, new ApiCallBack<List<AccountWaterBean>>() {

            @Override
            public void onSuccess(List<AccountWaterBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return ;
                }

                if(isRefresh){
                    mView.showRefreshIncomeListFinish(data);;
                }else{
                    mView.showIncomeList(data);
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
    public void loadMore(String type, String dateType) {
        start += 15;

        String token = UserManager.getInstance().getToken();
        mData.getNewIncomeList(token,start+"",type,dateType, new ApiCallBack<List<AccountWaterBean>>() {
            @Override
            public void onSuccess(List<AccountWaterBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }
}
