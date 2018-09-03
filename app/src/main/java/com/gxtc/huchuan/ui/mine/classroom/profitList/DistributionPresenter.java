package com.gxtc.huchuan.ui.mine.classroom.profitList;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.data.DistributionRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/4.
 */

public class DistributionPresenter implements DistributionContract.Presenter{
    private DistributionContract.Source mData;
    private DistributionContract.View mView;

    private int start = 0;
    private String token = UserManager.getInstance().getToken();

    public DistributionPresenter(DistributionContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DistributionRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void getDistributionList(String type,String dateType, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            mView.showLoad();
        }

        mData.getDistributionList(token,start+"",type,dateType, new ApiCallBack<List<DistributionBean>>() {

            @Override
            public void onSuccess(List<DistributionBean> data) {
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return ;
                }

                if(isRefresh){
                    mView.showRefreshFinish(data);
                }else{
                    mView.showDistributionList(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMore(final String type,String dateType) {
        start += 15;

        mData.getDistributionList(token,start+"",type,dateType, new ApiCallBack<List<DistributionBean>>() {
            @Override
            public void onSuccess(List<DistributionBean> data) {
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
                start += 15;
                mView.showLoad();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

}
