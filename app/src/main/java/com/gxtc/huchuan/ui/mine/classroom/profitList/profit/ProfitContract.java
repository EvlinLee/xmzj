package com.gxtc.huchuan.ui.mine.classroom.profitList.profit;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/5 .
 */

public class ProfitContract {
    public interface View extends BaseUserView<Presenter> {

        void showProfitList(List<DistributionBean> datas);

        void showRefreshFinish(List<DistributionBean> datas);

        void showLoadMoreClass(List<DistributionBean> datas);

        void showLoadMoreCircle(List<DistributionBean> datas);

        void showNoMore();
    }

    public interface Presenter extends BasePresenter {
        void getProfitList(String type, boolean isRefresh);

        void loadMore(String type);
    }

    public interface Source extends BaseSource {
        void getProfitList(String token, String start, String type, ApiCallBack<List<DistributionBean>> callBack);
    }

}
