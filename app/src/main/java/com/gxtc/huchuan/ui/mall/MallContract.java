package com.gxtc.huchuan.ui.mall;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CoustomMerBean;
import com.gxtc.huchuan.bean.LiveHeadPageBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface MallContract {


    public interface View extends BaseUserView<Presenter> {

        void showCbBanner(List<MallBean> advertise);

        /**
         * 显示 头部 图标
         *
         * @param datas
         */
        void showHeadIcon(List<MallBean> datas);

        void showLinesData(List<MallBean> datas);

        void showGridData(List<MallBean> datas);

        void showAddShopCartResult(Object datas);

        void showCustermersList(ArrayList<CoustomMerBean> datas);

        void showActivitysData(List<MallBean> datas);
    }


    interface Presenter extends BasePresenter {

        void getAdvertise();

        void getTags(String token);

        void getLinesData(String token, int start);

        void getGridData(boolean isLoadMore);

        void addShopCar(HashMap<String,String> map);

        void getCustermersList(String type,String rand);

        void getActivitysData(String token, int start);
    }
}
