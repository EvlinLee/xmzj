package com.gxtc.huchuan.ui.mine.visitor;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.VisitorBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/18.
 */

public interface VisitorContract {
    interface View extends BaseUserView<Presenter> {
        void showData(List<VisitorBean> datas);
        void showRefreshFinish(List<VisitorBean> datas);
        void showUserBrowserCount(VisitorBean bean);
        void showLoadMore(List<VisitorBean> datas);
        void showNoMore();
    }

    interface Presenter extends BasePresenter {
        void getData(boolean isRefresh);
        void getUserBrowseCount();
        void loadMore();
    }

    interface Source extends BaseSource {
        void getData(String token,String start, ApiCallBack<List<VisitorBean>> callBack);
        //获取用户访客数量接口
        void getUserBrowseCount(String token,ApiCallBack<VisitorBean> callBack);
    }
}
