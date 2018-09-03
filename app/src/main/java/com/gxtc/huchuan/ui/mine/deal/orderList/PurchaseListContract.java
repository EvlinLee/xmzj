package com.gxtc.huchuan.ui.mine.deal.orderList;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.PurchaseListBean;

import java.util.List;


public interface PurchaseListContract {

    interface View extends BaseUserView<PurchaseListContract.Presenter>{
        void showData(List<PurchaseListBean> datas);
        void showRefreshFinish(List<PurchaseListBean> datas);
        void showLoadMore(List<PurchaseListBean> datas);
        void showNoMore();
    }

    interface Presenter extends BasePresenter{
        void getData(boolean isRefresh);
        void loadMrore();
    }
}
