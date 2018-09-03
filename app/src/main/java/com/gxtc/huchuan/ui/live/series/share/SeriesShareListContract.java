package com.gxtc.huchuan.ui.live.series.share;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.http.ApiCallBack;

/**
 * Created by sjr on 2017/2/15.
 * 分享榜
 */

public class SeriesShareListContract {

    public interface View extends BaseUserView<SeriesShareListContract.Presenter> {

        void showData(TopicShareListBean datas);

        void showLoadMore(TopicShareListBean datas);

        void showNoMore();
    }

    public interface Presenter extends BasePresenter {

        void getData(String id);
        void loadMrore();
    }

    public interface Source extends BaseSource {
        void getData(int start, String chatRoomId, String chatInfoId,
                     ApiCallBack<TopicShareListBean> callBack);
    }
}
