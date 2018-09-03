package com.gxtc.huchuan.ui.mine.deal.orderList;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;


public class PurchaseListPresenter implements PurchaseListContract.Presenter{

    private PurchaseListContract.View mView;
    private DealSource mData;

    private int start = 0;

    public PurchaseListPresenter(PurchaseListContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void getData(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        String token = UserManager.getInstance().getToken();
        mData.getBuyOrderList(token, start+"", new ApiCallBack<List<PurchaseListBean>>() {
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
    public void loadMrore() {
        start += 15;
        String token = UserManager.getInstance().getToken();
        mData.getBuyOrderList(token, start+"", new ApiCallBack<List<PurchaseListBean>>() {
            @Override
            public void onSuccess(List<PurchaseListBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                start += 15;
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
