package com.gxtc.huchuan.ui.mine.incomedetail;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.data.InComeDetailNewRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/21 .
 */

public class InComeDetailNewPresenter implements InComeDetailNewContract.Presenter {

    private InComeDetailNewContract.Source mData;
    private InComeDetailNewContract.View   mView;

    private int start = 0;
    private String isAdded = "";

    public InComeDetailNewPresenter(InComeDetailNewContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new InComeDetailNewRepository();
    }

    @Override
    public void getTotalIncomeStatistics(String dateType, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        String token = UserManager.getInstance().getToken();
        mData.getTotalIncomeStatistics(token, dateType, new ApiCallBack<InComeAllCountBean>() {
            @Override
            public void onSuccess(InComeAllCountBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data.getUserStreams().size() == 0 || data == null){
                    mView.showEmpty();
                    return ;
                }

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
        this.isAdded = isAdded;
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        String token = UserManager.getInstance().getToken();
        mData.getIncomeList(token,start+"",type,isAdded,dateType, new ApiCallBack<List<AccountWaterBean>>() {

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
        mData.getIncomeList(token,start+"",type,isAdded,dateType, new ApiCallBack<List<AccountWaterBean>>() {
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
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }
}
