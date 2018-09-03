package com.gxtc.huchuan.ui.mine.incomedetail;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by zzg on 2017/12/30 .
 */

public interface NewInComeDetailContract {
    interface View extends BaseUserView<Presenter> {
        void showTotalIncomeStatistics(InComeAllCountBean bean);

        void showIncomeList(List<AccountWaterBean> bean);

        void showRefreshIncomeListFinish(List<AccountWaterBean> datas);

        void showRefreshTotalIncomeStatisticsFinish(InComeAllCountBean datas);

        void showLoadMore(List<AccountWaterBean> datas);

        void showNoMore();

    }

    interface Presenter extends BasePresenter {
        void getTotalIncomeStatistics(String dateType, boolean isRefresh);

        void getIncomeList(String type, String isAdded, String dateType, boolean isRefresh);

        void loadMore(String type, String dateType);

    }
}
