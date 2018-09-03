package com.gxtc.huchuan.ui.mine.circle.article;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleNewsBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 圈子新闻首页
 */

public class ArticleAuditedListContract {


    public interface View extends BaseUserView<ArticleAuditedListContract.Presenter> {

        void showData(List<CircleNewsBean> datas);


        void showRefreshFinish(List<CircleNewsBean> datas);


        void showLoadMore(List<CircleNewsBean> datas);

        void showNoMore();
    }


    public interface Presenter extends BasePresenter {

        void getData(boolean isRefresh);

        void loadMrore();
    }

    public interface Source extends BaseSource {
        void getData(int start, String id, int flag, String userCode, ApiCallBack<List<CircleNewsBean>> callBack);
    }
}
