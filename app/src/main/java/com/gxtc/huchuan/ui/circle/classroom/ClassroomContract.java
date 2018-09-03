package com.gxtc.huchuan.ui.circle.classroom;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.LiveHeadPageBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.live.LiveContract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/2/8.
 */

public interface ClassroomContract {


    interface View extends BaseUserView<Presenter> {


        /**
         * 显示正在直播
         *
         * @param datas
         */
        void showLiveRoom(List<ChatInfosBean> datas);

        void showSearChLiveRoom(List<SearchBean> datas);

        void showSearData(List<ClassAndSeriseBean> datas);

        void showlaodMoreLiveRoom(List<ChatInfosBean> datas);

        void showUnauditGroup(List<ClassAndSeriseBean> datas);
    }


    interface Presenter extends BasePresenter {

        void getDatas(HashMap<String, String> map);

        void getSeachData(boolean isLoadMore,String searchKey,String mCircleid);

        void loadmoreDatas(HashMap<String, String> map);

        void getUnauditGroup(HashMap<String, String> map);

        void searchClass(String searchKey,int mCircleid,boolean isRefresh);
    }

    interface Source extends BaseSource {

        void getDatas(HashMap<String, String> map, ApiCallBack<List<ChatInfosBean>> apiCallBack);

        void loadmoreDatas(HashMap<String, String> map,
                ApiCallBack<List<ChatInfosBean>> apiCallBack);

        void getDataHistory(String key, LiveContract.CallBack callBack);

        void saveDataHistory(String key, List<ChatInfosBean> data);

        void getUnauditGroup(HashMap<String, String> map, ApiCallBack<List<ClassAndSeriseBean>> apiCallBack);

        void searchClass(HashMap<String, String> map, ApiCallBack<List<ClassAndSeriseBean>> apiCallBack);
    }


    interface CallBack<T> {
        void successful(T data);
    }
}
