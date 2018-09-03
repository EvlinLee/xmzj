package com.gxtc.huchuan.ui.circle.findCircle;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/5.
 */

public class CircleListPresenter implements CircleListContract.Presenter {

    private CircleListContract.View mView;
    private CircleSource            mData;

    private int start = 0;
    private String  keyword;
    private String  isfree;
    private String  orderType = "";
    private Integer typeId = 0;

    public CircleListPresenter(CircleListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new CircleRepository();
    }

    @Override
    public void getData(String k, String infree, String orderType, Integer typeId) {
        if(this.typeId != typeId || !this.orderType.equals(orderType)){
            start = 0;
        }
        this.keyword = k;
        this.isfree = infree;
        this.typeId = typeId;
        this.orderType = orderType;
        String token = UserManager.getInstance().getToken();
        mView.showLoad();
        mData.getFindCircle(token, start, infree, orderType, k,typeId,
                new ApiCallBack<List<CircleBean>>() {
                    @Override
                    public void onSuccess(List<CircleBean> data) {
                        if (mView == null) return;
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
    public void getListType() {
        mData.getListType(new ApiCallBack<List<CircleBean>>() {
            @Override
            public void onSuccess(List<CircleBean> data) {
                if (mView == null) return;
                mView.showListType(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if (mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void refreshData() {
        String token = UserManager.getInstance().getToken();
        start = 0;
        mData.getFindCircle(token, start, isfree, orderType, keyword,typeId,
                new ApiCallBack<List<CircleBean>>() {
                    @Override
                    public void onSuccess(List<CircleBean> data) {
                        if (mView == null) return;
                        if (data == null || data.size() == 0) {
                            mView.showEmpty();
                            return;
                        }
                        mView.showRefreshData(data);

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mView == null) return;
                        mView.showError(message);
                    }
                });
    }

    @Override
    public void loadMoreData() {
        String token = UserManager.getInstance().getToken();
        start += 15;

        mData.getFindCircle(token, start, isfree, orderType, keyword,typeId,
                new ApiCallBack<List<CircleBean>>() {
                    @Override
                    public void onSuccess(List<CircleBean> data) {
                        if (mView == null) return;
                        if (data == null || data.size() == 0) {
                            mView.showNoLoadMore();
                            return;
                        }
                        mView.showLoadMoreData(data);

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mView == null) return;
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
        mView = null;
    }


}
