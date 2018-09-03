package com.gxtc.huchuan.ui.mine.incomedetail.distribute;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.bean.DistributionCountBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/5 .
 */

public interface DistributeContract {
     interface View extends BaseUserView<Presenter> {

        void showDistributList(List<DistributionBean> datas);

        void showProfitList(List<DistributionBean> datas);

        void showDistributionCount(DistributionCountBean bean);

        void showIncomeStatistics(DistributionCountBean bean);

        void showRefreshFinish(List<DistributionBean> datas);

        void showLoadMoreProfit(List<DistributionBean> datas);

        void showLoadMoreDistribution(List<DistributionBean> datas);

        void showNoMore();
    }

     interface Presenter extends BasePresenter {
        void getDistributionList(String type, String dateType, boolean isRefresh);

        void getProfitList(String type, String dateType, boolean isRefresh);

        void getDistributionCount(String token, String type, String dataTpe);

        void getDistributionIncomeSum(String token, String type, String dataTpe);

        void getIncomeStatistics(HashMap<String, String> map);

        void loadMoreProfit(String type, String dateType);

        void loadMoreDistribution(String type, String dateType);
    }

}
