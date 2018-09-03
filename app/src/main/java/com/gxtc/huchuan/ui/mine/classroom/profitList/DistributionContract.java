package com.gxtc.huchuan.ui.mine.classroom.profitList;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/4 .
 */

public class DistributionContract {
    public interface View extends BaseUserView<Presenter> {

        void showDistributionList(List<DistributionBean> datas);

        void showRefreshFinish(List<DistributionBean> datas);

        void showLoadMoreClass(List<DistributionBean> datas);

        void showLoadMoreCircle(List<DistributionBean> datas);

        void showNoMore();
    }

    public interface Presenter extends BasePresenter {
        void getDistributionList(String type,String dateType, boolean isRefresh);

        void loadMore(String type,String dateType);
    }

    public interface Source extends BaseSource {
        void getDistributionList(String token, String start, String type,String dateType, ApiCallBack<List<DistributionBean>> callBack);
    }

}
