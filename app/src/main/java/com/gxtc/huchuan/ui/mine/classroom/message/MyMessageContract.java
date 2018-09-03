package com.gxtc.huchuan.ui.mine.classroom.message;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ClassMyMessageBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/10.
 */

public interface MyMessageContract {
    interface View extends BaseUserView<Presenter> {
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
        void getData(String token,String start, ApiCallBack<List<ClassMyMessageBean>> callBack);
    }
}
