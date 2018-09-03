package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;


import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.ArticleListBean;

import java.util.List;

public interface ArticleListContract {

    interface View extends BaseUserView<ArticleListContract.Presenter>{
        void showData(List<ArticleListBean> datas);
        void showRefreshFinish(List<ArticleListBean> datas);
        void showLoadMore(List<ArticleListBean> datas);
        void showNoMore();
    }

    interface Presenter extends BasePresenter{
        void getData(boolean isRefresh);
        void loadMrore();
    }
}
