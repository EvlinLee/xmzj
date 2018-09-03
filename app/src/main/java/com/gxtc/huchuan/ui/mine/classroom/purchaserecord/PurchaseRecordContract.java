package com.gxtc.huchuan.ui.mine.classroom.purchaserecord;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/24 .
 */

public class PurchaseRecordContract {
    public interface View extends BaseUserView<PurchaseRecordContract.Presenter> {

        void showSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas);

//        void showToicData(List<PurchaseTopicRecordBean> datas);
//
//        void showSeriesData(List<PurchaseSeriesRecordBean> datas);

        void showCircleData(List<PurchaseCircleRecordBean> datas);

        void showDealData(List<PurchaseListBean> datas);

        void showAllOrderData(List<AllPurchaseListBean> datas);

        void showLoadMoreSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas);

//        void showLoadMoreToic(List<PurchaseTopicRecordBean> datas);
//
//        void showLoadMoreSeries(List<PurchaseSeriesRecordBean> datas);

        void showLoadMoreCircle(List<PurchaseCircleRecordBean> datas);

        void showLoadMoreDeal(List<PurchaseListBean> datas);

        void showNoMore();

    }

    public interface Presenter extends BasePresenter {
        void getSeriesAndTopic(boolean isRefresh);

//        void getToicData(boolean isRefresh);
//
//        void getSeriesData(boolean isRefresh);

        void getCircleData(boolean isRefresh);

        void getDealData(boolean isRefresh);

        void getAllOrderData(boolean isRefresh, String type, int start);

        void loadMoreSeriesAndTopic();

//        void loadMroreToic();
//
//        void loadMroreSeries();

        void loadMoreCircle();

        void loadMoreDeal();

    }

    public interface Source extends BaseSource {

        void getSeriesAndTopic(String token,String start,ApiCallBack<List<PurchaseSeriesAndTopicBean>> callBack);

//        void getToicData(String token,String start, ApiCallBack<List<PurchaseTopicRecordBean>> callBack);
//
//        void getSeriesData(String token,String start, ApiCallBack<List<PurchaseSeriesRecordBean>> callBack);

        void getCircleData(String token,String start,ApiCallBack<List<PurchaseCircleRecordBean>> callBack);

        void getDealData(String token,String start,ApiCallBack<List<PurchaseListBean>> callBack);

        void getAllOrderData(String token, String type,String start,ApiCallBack<List<AllPurchaseListBean>> callBack);

    }
}
