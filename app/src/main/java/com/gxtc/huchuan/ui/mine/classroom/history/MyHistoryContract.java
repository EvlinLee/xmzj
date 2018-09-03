package com.gxtc.huchuan.ui.mine.classroom.history;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/28 .
 */

public class MyHistoryContract {
    public interface View extends BaseUserView<MyHistoryContract.Presenter> {

        void showData(List<UnifyClassBean> datas);

        void showRefreshFinish(List<UnifyClassBean> datas);

        void showLoadMore(List<UnifyClassBean> datas);

        void showNoMore();

        void showDelResult();

        void showChatInfoSuccess(ChatInfosBean infosBean);
    }

    public interface Presenter extends BasePresenter {

        void getData(boolean isRefresh);

        void loadMrore();

        void deleteChatUserRecord(String token, ArrayList<String> list);

        void getChatInfos(int id);

    }

    public interface Source extends BaseSource {

        void getData(int start,String clickType,ApiCallBack<List<UnifyClassBean>> callBack);
        //删除观看历史记录
        void deleteChatUserRecord(String token, ArrayList<String> list, ApiCallBack<List<UnifyClassBean>> callBack);
    }
}
