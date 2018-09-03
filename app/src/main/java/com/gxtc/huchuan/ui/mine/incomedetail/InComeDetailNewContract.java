package com.gxtc.huchuan.ui.mine.incomedetail;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/21 .
 */

public interface InComeDetailNewContract {
    interface View extends BaseUserView<Presenter> {
        void showTotalIncomeStatistics(InComeAllCountBean bean);

        void showIncomeList(List<AccountWaterBean> bean);

        void showRefreshIncomeListFinish(List<AccountWaterBean> datas);

        void showRefreshTotalIncomeStatisticsFinish(InComeAllCountBean datas);

        void showLoadMore(List<AccountWaterBean> datas);

        void showNoMore();

    }

    interface Presenter extends BasePresenter {
        void getTotalIncomeStatistics(String dateType,boolean isRefresh);

        void getIncomeList(String type,String isAdded,String dateType,boolean isRefresh);

        void loadMore(String type,String dateType);

    }

    interface Source extends BaseSource {
        //获取总收益统计
        void getTotalIncomeStatistics(String token, String dateType,ApiCallBack<InComeAllCountBean> callBack);

        //获取总收益统计（对接新的接口）
        void getNewTotalIncomeStatistics(String token, String dateType,ApiCallBack<InComeAllCountBean> callBack);

        //交易和打赏明细 （流水）
        void getIncomeList(String token,String start,String type,String isAdded,String dateType,ApiCallBack<List<AccountWaterBean>> callBack);

        void getNewIncomeList(String token,String start,String type,String dateType,ApiCallBack<List<AccountWaterBean>> callBack);
    }
}
