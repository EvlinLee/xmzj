package com.gxtc.huchuan.ui.mine.circle.applyfor;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ApplyForBean;
import com.gxtc.huchuan.http.ApiCallBack;


import java.util.List;

/**
 * Created by sjr on 2017/6/10.
 */

public class ApplyForMemberListContract {
    public interface View extends BaseUserView<ApplyForMemberListContract.Presenter> {

        void showData(List<ApplyForBean> datas);

        void showRefreshFinish(List<ApplyForBean> datas);

        void showLoadMore(List<ApplyForBean> datas);


        void showNoMore();
    }


    public interface Presenter extends BasePresenter {

        void getData(boolean isRefresh);

        void loadMrore();
    }

    public interface Source extends BaseSource {
        void getData(int groupId,int start, ApiCallBack<List<ApplyForBean>> callBack);
    }
}
