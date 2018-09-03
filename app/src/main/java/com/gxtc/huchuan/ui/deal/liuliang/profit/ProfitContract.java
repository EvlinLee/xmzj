package com.gxtc.huchuan.ui.deal.liuliang.profit;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ProfitListBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

public interface ProfitContract {

    interface View extends BaseUserView<ProfitContract.Presenter>{
        void showData(List<ProfitListBean> datas);
        void showRefreshFinish(List<ProfitListBean> datas);
        void showLoadMore(List<ProfitListBean> datas);
        void showNoMore();
    }


    interface Presenter extends BasePresenter{
        void getData(boolean isRefresh);
        void loadMrore();
    }

    interface Source extends BaseSource{
        void getData(int start, ApiCallBack<List<ProfitListBean>> callBack);
    }
}
