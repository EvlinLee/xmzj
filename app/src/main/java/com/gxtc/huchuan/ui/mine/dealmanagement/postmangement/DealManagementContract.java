package com.gxtc.huchuan.ui.mine.dealmanagement.postmangement;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.PurchaseListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/15.
 */

public class DealManagementContract {
    interface View extends BaseUserView<DealManagementContract.Presenter> {
        void showData(List<PurchaseListBean> datas);
        void showRefreshFinish(List<PurchaseListBean> datas);
        void showLoadMore(List<PurchaseListBean> datas);
        void showNoMore();
        void showDelResult(List<PurchaseListBean> datas);
    }

    interface Presenter extends BasePresenter {
        void getData(boolean isRefresh);
        void loadMrore();
        void deleteDel(String token, ArrayList<String> list);
    }
}
