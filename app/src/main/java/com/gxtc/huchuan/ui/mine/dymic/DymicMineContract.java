package com.gxtc.huchuan.ui.mine.dymic;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/17 .
 */

public interface DymicMineContract {
    interface View extends BaseUserView<Presenter> {
        void showData(List<PersonalDymicBean> datas);
        void showDelResult(Object o);
        void showRefreshFinish(List<PersonalDymicBean> datas);
        void showLoadMore(List<PersonalDymicBean> datas);
        void showNoMore();
    }

    interface Presenter extends BasePresenter {
        void getData(boolean isRefresh);
        void delDymicList(String token);
        void loadMore();
    }

    interface Source extends BaseSource {
        void getData(String token,String start, ApiCallBack<List<PersonalDymicBean>> callBack);

        //清除动态列表
        void delDymicList(String token,ApiCallBack<Object> callBack);
    }
}
