package com.gxtc.huchuan.ui.mine.personalhomepage.more;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/10 .
 */

public class PersonalHomePageMoreContract {
    public interface View extends BaseUserView<PersonalHomePageMoreContract.Presenter> {

        void showHomePageGroupInfoList(List<CircleHomeBean> list);

        void showDZSuccess(int id);

        void showSelfNewsList(List<NewsBean> list);

        void showUserNewsList(List<NewsBean> list);

        void showSelfChatInfoList(List<HomePageChatInfo> list);

        void showUserChatInfoList(List<HomePageChatInfo> list);

        void showSelfDealList(List<DealListBean> list);

        void showUserDealList(List<DealListBean> list);

        void showLoadMoreNewsList(List<NewsBean> list);

        void showLoadMoreChatInfoList(List<HomePageChatInfo> list);

        void showLoadMoreHomePageGroupInfoList(List<CircleHomeBean> list);

        void showLoadMoreDealList(List<DealListBean> list);

        /**
         * 没有更多数据
         */
        void showNoMore();

    }

    public interface Presenter extends BasePresenter {

        void getHomePageGroupInfoList(String userCode,boolean isRefresh);

        void dianZan(int id);

        void getSelfNewsList(boolean isRefresh);

        void getUserNewsList(String userCode,boolean isRefresh);

        void getSelfChatInfoList(boolean isRefresh);

        void getUserChatInfoList(String userCode,boolean isRefresh);

        void getSelfDealList(boolean isRefresh);

        void getUserDealList(String userCode,boolean isRefresh);

        void loadMrore(String type, String userCode);

    }

    public interface Source extends BaseSource {

        //动态主页
        void getHomePageGroupInfoList(ApiCallBack<List<CircleHomeBean>> callBack, String userCode,String token, String start);

        //动态点赞
        void dianZan(String token, int id, ApiCallBack<ThumbsupVosBean> callBack);

        //获取自己个人主页的新闻列表接口
        void getSelfNewsList(ApiCallBack<List<NewsBean>> callBack, String token, String start);

        //获取用户个人主页的新闻列表接口
        void getUserNewsList(ApiCallBack<List<NewsBean>> callBack, String userCode, String start);

        //获取自己个人主页的直播课程列表接口
        void getSelfChatInfoList(ApiCallBack<List<HomePageChatInfo>> callBack, String token, String start);

        //获取用户个人主页的直播课程列表接口
        void getUserChatInfoList(ApiCallBack<List<HomePageChatInfo>> callBack, String userCode, String start);

        //获取自己个人主页的交易列表接口
        void getSelfDealList(ApiCallBack<List<DealListBean>> callBack, String token, String start);

        //获取用户个人主页的交易列表接口
        void getUserDealList(ApiCallBack<List<DealListBean>> callBack, String userCode, String start);
    }
}
