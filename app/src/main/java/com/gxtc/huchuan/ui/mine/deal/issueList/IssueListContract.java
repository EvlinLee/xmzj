package com.gxtc.huchuan.ui.mine.deal.issueList;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.PurchaseListBean;

import java.util.List;


public interface IssueListContract {

    interface View extends BaseUserView<IssueListContract.Presenter>{
        void showData(List<PurchaseListBean> datas);
        void showRefreshFinish(List<PurchaseListBean> datas);
        void showLoadMore(List<PurchaseListBean> datas);
        void showNoMore();
        void showDeleteSuccess(int id);
    }

    interface Presenter extends BasePresenter{
        void getData(boolean isRefresh);
        void loadMrore();
        void deleteDeal(int id);
    }
}
