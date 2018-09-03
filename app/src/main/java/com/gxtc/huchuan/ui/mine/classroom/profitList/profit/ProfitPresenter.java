package com.gxtc.huchuan.ui.mine.classroom.profitList.profit;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

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
//        mData = new ProfitRepository();
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
    public void getProfitList(String type, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getProfitList(token,start+"",type, new ApiCallBack<List<DistributionBean>>() {

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
    public void loadMore(final String type) {
        start += 15;

        mData.getProfitList(token,start+"",type, new ApiCallBack<List<DistributionBean>>() {
            @Override
            public void onSuccess(List<DistributionBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                if ("1".equals(type)){
                    mView.showLoadMoreClass(data);
                }else {
                    mView.showLoadMoreCircle(data);
                }

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
