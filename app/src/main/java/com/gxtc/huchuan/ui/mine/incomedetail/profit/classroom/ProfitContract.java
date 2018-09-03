package com.gxtc.huchuan.ui.mine.incomedetail.profit.classroom;

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

public interface ProfitContract {
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
        void getDistributionList(String type,String dateType, boolean isRefresh);

        void getProfitList(String type, String dateType,boolean isRefresh);

        void getDistributionCount(String token,String type,String dataTpe);

         void getIncomeStatistics(HashMap<String,String> map);

        void loadMoreProfit(String type,String dateType);

        void loadMoreDistribution(String type,String dateType);
    }

     interface Source extends BaseSource {
        //分销收益
        void getDistributionList(String token, String start, String type,String dateType, ApiCallBack<List<DistributionBean>> callBack);
        //课程收益
        void getProfitList(String token,String start,String type,String dateType,ApiCallBack<List<DistributionBean>> callBack);

        //课程或是圈子收益
        void getToatalProfitList(String token,String start,String type,String dateType,ApiCallBack<List<DistributionBean>> callBack);
        //分销总数
        void getDistributionCount(String token,String type,String dataTpe,ApiCallBack<DistributionCountBean> callBack);
        //获取分销与收益统计（圈子和课程）接口
        void getIncomeStatistics(HashMap<String,String> map,ApiCallBack<DistributionCountBean> callBack);
        //个人分销业务列表
        void getlistByDistributionBusiness(HashMap<String,String> map,ApiCallBack<List<DistributionBean>> callBack);
        //获取个人分销业务列表佣金统计
        void getlistByDistributionBusinessSum(String token,String type,String dataTpe,ApiCallBack<DistributionCountBean> callBack);

        void getCreateUserIncome(String token,String type,String dataTpe,ApiCallBack<DistributionCountBean> callBack);
    }
}
