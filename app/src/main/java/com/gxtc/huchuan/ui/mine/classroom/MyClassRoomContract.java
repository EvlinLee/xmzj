package com.gxtc.huchuan.ui.mine.classroom;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ClassMyMessageBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by ALing on 2017/3/9 .
 */

public interface MyClassRoomContract {
    interface View extends BaseUiView<MyClassRoomContract.Presenter> {
        void showData(List<ClassMyMessageBean> datas);
        void showRefreshFinish(List<ClassMyMessageBean> datas);
        void showLoadMore(List<ClassMyMessageBean> datas);
        void showNoMore();
    }

    interface Presenter extends BasePresenter {
        void getData(boolean isRefresh);
        void loadMore();
    }

    interface Source extends BaseSource {
        void getData(int start, ApiCallBack<List<ClassMyMessageBean>> callBack);
    }
}
