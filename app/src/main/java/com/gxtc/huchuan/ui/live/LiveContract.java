package com.gxtc.huchuan.ui.live;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.ClassHotBean;
import com.gxtc.huchuan.bean.ClassLike;
import com.gxtc.huchuan.bean.LiveHeadPageBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gubr on 2017/2/8.
 */

public interface LiveContract {


    interface View extends BaseUserView<Presenter> {
        void showCbBanner(List<NewsAdsBean> advertise);

        /**
         * 显示 头部 图标
         */
        void showHeadIcon(List<LiveHeadTitleBean> datas);

        /**
         * 显示 直播 预告
         */
        void showLiveForeshow(List<ChatInfosBean> datas);

        /**
         * 显示正在直播
         */
        void showLiveRoom(List<UnifyClassBean> datas);

        /**
         * 显示直播类型
         */
        void showClassDatas(List<LiveHeadPageBean.TypesChatRoom> datas);

        void showloadMoreLiveRoom(List<UnifyClassBean> data);

        void showNoLoadMoreLiveRoom();

        void showData(List<ClassLike> data);

        void showHotData(List<ClassHotBean> data);
    }


    interface Presenter extends BasePresenter {

        void getAdvertise();

        void getAdvertise(String token);

        void getLiveRoom(String token, int start, boolean isRefresh, String typeId);

        void getLiveLoadMore(int start, String typeId);

        void getData(int start, int pageSize);

        void gethotData(int start, int pageSize);
    }

    interface Source extends BaseSource {

        void getDatas(String token, int start, String type, ApiCallBack<ArrayList<ChatInfosBean>> apiCallBack);

        void getAdvertise(String token, ApiCallBack<List<NewsAdsBean>> callBack);

        void getDataHistory(String key, CallBack callBack);

        void saveDataHistory(String key, ArrayList<ChatInfosBean> data);

        void getLiveRoom(String token, int start, String typeId,String clickType, ApiCallBack<List<UnifyClassBean>> apiCallBack);
    }


    interface CallBack<T> {
        void successful(T data);
    }
}
