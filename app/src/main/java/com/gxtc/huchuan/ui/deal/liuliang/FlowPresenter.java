package com.gxtc.huchuan.ui.deal.liuliang;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.FlowListBean;
import com.gxtc.huchuan.data.deal.FlowRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Steven on 17/2/13.
 *
 */

public class FlowPresenter implements FlowContract.Presenter{

    private FlowContract.Source mData;
    private FlowContract.View   mView;

    private int start = 0;

    public FlowPresenter(FlowContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new FlowRepository();
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
            mView.showLoad();
        }

        mData.getData(start, new ApiCallBack<List<FlowListBean>>() {

            @Override
            public void onSuccess(List<FlowListBean> data) {
                if(mView == null)   return;
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
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start ++;

        mData.getData(start, new ApiCallBack<List<FlowListBean>>() {
            @Override
            public void onSuccess(List<FlowListBean> data) {
                if(mView == null)   return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }
}
