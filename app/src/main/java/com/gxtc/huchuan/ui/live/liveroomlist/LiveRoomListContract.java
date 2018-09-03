package com.gxtc.huchuan.ui.live.liveroomlist;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.FollowLecturerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/31.
 */

public interface LiveRoomListContract {
    public interface View extends BaseUiView<Presenter> {
        void showDatas(List<FollowLecturerBean> datas);

        void updateFollow(String id,int position);
    }


    public interface Presenter extends BasePresenter {
        void getDatas(String start,String pageSize);

        void getFollowDatas(String start, String pageSize);

        void changeFollow(String token,String id,int position);
    }


    public interface source extends BaseSource {
        void getDats(HashMap<String,String> map, ApiCallBack<List<LiveRoomBean>> callback);

        void getFollowDats(HashMap<String, String> map, ApiCallBack<List<FollowLecturerBean>> callback);

        void changeFolow(String token,String id,ApiCallBack<Void> callBack);
    }
}
