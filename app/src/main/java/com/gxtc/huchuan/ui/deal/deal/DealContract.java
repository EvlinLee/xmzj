package com.gxtc.huchuan.ui.deal.deal;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;


public interface DealContract {

    interface View extends BaseUiView<DealContract.Presenter>{
        void showData(DealData data);
        void showloadmore(DealData data);
        void showReloadmre(DealData data);
        void showLoadMoreFinish();
        void showAdvertise(List<NewsAdsBean> data);
    }



    interface Presenter extends BasePresenter{
        void loadmore(int position);

        void getHomeData();

        void getData();

        void changeShowType(String s);
    }
}
