package com.gxtc.huchuan.ui.mine.deal.refund;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/9.
 */

public class RefundListPresenter implements RefundListContract.Presenter{

    private RefundListContract.View mView;
    private DealSource mData;

    private int start = 0;

    public RefundListPresenter(RefundListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void refreshData() {
        start = 0;
        String token = UserManager.getInstance().getToken();
        mData.getRefundList(token, start, new ApiCallBack<List<PurchaseListBean>>() {
            @Override
            public void onSuccess(List<PurchaseListBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    return;
                }
                mView.showRefreshData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void loadMoreData() {
        start += 15;
        String token = UserManager.getInstance().getToken();
        mData.getRefundList(token, start, new ApiCallBack<List<PurchaseListBean>>() {
            @Override
            public void onSuccess(List<PurchaseListBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    return;
                }
                mView.showLoadMoreData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                start -= 15;
                mView.showError(message);
            }
        });
    }

    @Override
    public void getData() {
        mView.showLoad();
        String token = UserManager.getInstance().getToken();
        mData.getRefundList(token, 0, new ApiCallBack<List<PurchaseListBean>>() {
            @Override
            public void onSuccess(List<PurchaseListBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }

                mView.showData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
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
