package com.gxtc.huchuan.ui.mine.personalhomepage;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/6 .
 */

public class PersonalHomeContract {
    public interface View extends BaseUserView<PersonalHomeContract.Presenter> {
        void showHomePageSelfList(List<PersonalHomeDataBean> list);
        void showHomePageUserList(List<PersonalHomeDataBean> list);
        void showCircleByUserList(List<CircleBean> list);
        void showCircleByUserCodeList(List<CircleBean> list);
        void showRefreshFinish(List<CircleBean> datas);
        void showLoadMoreList(List<CircleBean> list);
        void showSelfData(User user);
        void showMenberData(User user);
        void showUserFocus(Object object);
        void showRecommendList(List<PersonalHomeDataBean> list);
        /**
         * 没有更多数据
         */
        void showNoMore();

    }

    public interface Presenter extends BasePresenter {
        void getUserSelfInfo(String token);

        void getUserMemberByUserCode(String userCode,String token);

        void getHomePageSelfList(boolean isRefresh);

        void getHomePageUserList(String userCode,boolean isRefresh);

        void getCircleListByUser(boolean isRefresh);

        void getCircleListByUserCode(String userCode,boolean isRefresh);

        void setUserFocus(String token,String followType,String bizId);

        void loadMrore(String userCode);

        void getUserRecommendList(String userCode);

    }

    public interface Source extends BaseSource {
        void getUserSelfInfo(ApiCallBack<User> callBack,String token);

        void getUserMemberByUserCode(ApiCallBack<User> callBack,String userCode,String token);

        void getHomePageSelfList(ApiCallBack<List<PersonalHomeDataBean>> callBack, String token, String start);

        void getHomePageUserList(ApiCallBack<List<PersonalHomeDataBean>> callBack, String userCode, String start);

        void getCircleListByUser(ApiCallBack<List<CircleBean>> callBack, String token, String type);

        void getCircleListByUserCode(ApiCallBack<List<CircleBean>> callBack,String userCode,String token,int type,int start);

        void setUserFocus(ApiCallBack<Object> callBack,String token,String followType,String bizId);

        void getUserRecommendList(ApiCallBack<List<PersonalHomeDataBean>> callBack,String userCode);
    }
}
