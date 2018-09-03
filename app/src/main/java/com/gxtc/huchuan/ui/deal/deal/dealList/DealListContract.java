package com.gxtc.huchuan.ui.deal.deal.dealList;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

public interface DealListContract {

    interface View extends BaseUiView<DealListContract.Presenter>{
        void showData(List<DealListBean> datas);
        void showType(List<DealTypeBean> subs, List<DealTypeBean> udefs);
        void showRefreshFinish(List<DealListBean> datas);
        void showLoadMore(List<DealListBean> datas);
        void showPlateDescripte(Object datas);
        void showNoMore();
    }

    interface Presenter extends BasePresenter{
        void getData(boolean isRefresh,int typeId,List<DealTypeBean> subsMap,List<DealTypeBean> udefs);
        void getType(int id);
        void loadMrore(int typeId,List<DealTypeBean> subsMap,List<DealTypeBean> udefs);
        void filterData(int typeId,List<DealTypeBean> subsMap,List<DealTypeBean> udefs);
        void getPlateDescripte(String tradeTypeId);
    }

    interface Source extends BaseSource{
        void getData(HashMap<String,String> map, ApiCallBack<List<DealListBean>> callBack);
        void getPlateDescripte(String tradeTypeId, ApiCallBack<Object> callBack);
    }

}
