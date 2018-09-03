package com.gxtc.huchuan.ui.deal.deal.hotdeallist;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.deal.deal.DealContract;
import com.gxtc.huchuan.ui.deal.deal.dealList.DealListContract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30 0030.
 */

public interface HotListContract {
    interface View extends BaseUiView<HotListContract.Presenter> {
        void showData(List<DealListBean> datas);
        void showLoadMore(List<DealListBean> datas);
        void showRefreshFinish(List<DealListBean> datas);
        void showNoMore();


    }

    interface Presenter extends BasePresenter {
          void getData(boolean isRefresh);
          void loadMrore();
    }

    interface Source extends BaseSource {
        void getData(int start, ApiCallBack<List<DealListBean>> callBack);

    }





}
