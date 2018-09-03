package com.gxtc.huchuan.ui.mine.circle;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleSignBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/22.
 */

public class CircleStatisticPresenter implements CircleStatisticContract.Presenter{

    private CircleStatisticContract.View mView;
    private CircleSource mData;

    private int id;
    private int start = 0;

    private CircleBean bean;

    public CircleStatisticPresenter(CircleStatisticContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new CircleRepository();
    }

    @Override
    public void refreshData() {
        start = 0;
        mData.getActiveData(id, start, new ApiCallBack<List<CircleSignBean>>() {
            @Override
            public void onSuccess(List<CircleSignBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showRefreshData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showError(message);
            }
        });
    }

    @Override
    public void loadMoreData() {
        start += 15 ;
        mData.getActiveData(id, start, new ApiCallBack<List<CircleSignBean>>() {
            @Override
            public void onSuccess(List<CircleSignBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoLoadMore();
                    return;
                }
                mView.showLoadMoreData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start -= 15;
                mView.showError(message);
            }
        });

    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView =null;
    }

    @Override
    public void getData(final int id) {
        this.id = id;
        mView.showLoad();
        mData.getChartData(id, new ApiCallBack<CircleBean>() {
            @Override
            public void onSuccess(CircleBean data) {
                if(mView == null) return;
                if(data == null){
                    mView.showEmpty();
                    return;
                }
                bean = data;
                getActiveData(id,start);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });

    }

    @Override
    public void getActiveData(int id, int start) {
        mData.getActiveData(id, start, new ApiCallBack<List<CircleSignBean>>() {
            @Override
            public void onSuccess(List<CircleSignBean> data) {
                if(mView == null) return;
                if(data != null && data.size() > 10){
                    for (int i = 10; i < data.size(); i++) {
                        data.remove(i);
                    }
                }

                mView.showLoadFinish();
                mView.showData(bean,data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
