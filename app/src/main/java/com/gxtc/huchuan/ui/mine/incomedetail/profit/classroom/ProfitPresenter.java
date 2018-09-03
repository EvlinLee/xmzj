package com.gxtc.huchuan.ui.mine.incomedetail.profit.classroom;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.bean.DistributionCountBean;
import com.gxtc.huchuan.data.ProfitRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/5 .
 */

public class ProfitPresenter implements ProfitContract.Presenter{
    private ProfitContract.Source mData;
    private ProfitContract.View mView;

    private int start = 0;
    private String token = UserManager.getInstance().getToken();

    public ProfitPresenter(ProfitContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ProfitRepository();
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
    public void getDistributionList(String type,String dateType, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getDistributionList(token,start+"",type, dateType,new ApiCallBack<List<DistributionBean>>() {

            @Override
            public void onSuccess(List<DistributionBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return ;
                }

                if(isRefresh){
                    mView.showRefreshFinish(data);
                }else{
                    mView.showDistributList(data);
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
    public void getProfitList(String type,String dateType, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getProfitList(token,start+"",type, dateType,new ApiCallBack<List<DistributionBean>>() {

            @Override
            public void onSuccess(List<DistributionBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return ;
                }

                if(isRefresh){
                    mView.showRefreshFinish(data);
                }else{
                    mView.showProfitList(data);
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
    public void getDistributionCount(String token, String type,String dataTpe) {
        mData.getDistributionCount(token, type,dataTpe, new ApiCallBack<DistributionCountBean>() {
            @Override
            public void onSuccess(DistributionCountBean data) {
                if(mView == null) return;
                mView.showDistributionCount(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getIncomeStatistics(HashMap<String, String> map) {
        mData.getIncomeStatistics(map, new ApiCallBack<DistributionCountBean>() {
            @Override
            public void onSuccess(DistributionCountBean data) {
                if(mView == null) return;
                mView.showIncomeStatistics(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMoreProfit(final String type,String dateType) {
        start += 15;

        mData.getProfitList(token,start+"",type,dateType, new ApiCallBack<List<DistributionBean>>() {
            @Override
            public void onSuccess(List<DistributionBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMoreProfit(data);

            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                mView.showLoad();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMoreDistribution(String type,String dateType) {
        start += 15;

        mData.getDistributionList(token,start+"",type,dateType, new ApiCallBack<List<DistributionBean>>() {
            @Override
            public void onSuccess(List<DistributionBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMoreDistribution(data);

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
