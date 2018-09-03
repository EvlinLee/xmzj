package com.gxtc.huchuan.ui.live.participation;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Gubr on 2017/3/29.
 */

public interface ParticipationContract {
    interface View extends BaseUiView<Presenter>{

        void showData(List<ChatInfosBean> datas);

        void showRefreshFinish(List<ChatInfosBean> datas);

        void showLoadMore(List<ChatInfosBean> datas);

        void showNoMore();

        void showChatInfoSuccess(ChatInfosBean infosBean);

    }

    interface Presenter extends BasePresenter{
        void getData(boolean isRefresh, int start);

        void getData(boolean isRefresh);


        void getChatInfos(int id);
    }

    interface Source extends BaseSource {
        void getData(int start, ApiCallBack<List<DealListBean>> callBack);
    }

}
