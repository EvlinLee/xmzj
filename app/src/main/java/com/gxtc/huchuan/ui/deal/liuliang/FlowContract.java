package com.gxtc.huchuan.ui.deal.liuliang;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.FlowListBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Steven on 17/2/13.
 */

public interface FlowContract {

    interface View extends BaseUiView<FlowContract.Presenter>{
        void showData(List<FlowListBean> datas);
        void showRefreshFinish(List<FlowListBean> datas);
        void showLoadMore(List<FlowListBean> datas);
        void showNoMore();
    }

    interface Presenter extends BasePresenter{
        void getData(boolean isRefresh);
        void loadMrore();
    }

    interface Source extends BaseSource{
        void getData(int start, ApiCallBack<List<FlowListBean>> callBack);
    }
}
