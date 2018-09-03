package com.gxtc.huchuan.ui.mine.personalhomepage;

import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.PersonalHomeRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/6 .
 */

public class PersonalHomePresenter implements PersonalHomeContract.Presenter {
    private PersonalHomeContract.View mView;
    private PersonalHomeContract.Source mData;
    private int start = 0;
    String token = UserManager.getInstance().getToken();

    public PersonalHomePresenter(PersonalHomeContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new PersonalHomeRepository();
    }
    @Override
    public void start() {

    }

    @Override
    public void destroy() {
   mData.destroy();
        mView = null;
    }

    @Override
    public void getUserSelfInfo(String token) {
//        mView.showLoad();
        mData.getUserSelfInfo(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User data) {
//                mView.showLoadFinish();
                if(mView == null) return;
                mView.showSelfData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
//                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        }, token);
    }

    @Override
    public void getUserMemberByUserCode(String userCode,String token) {
//        mView.showLoad();
        mData.getUserMemberByUserCode(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User data) {
//                mView.showLoadFinish();
                if(mView == null) return;
                mView.showMenberData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
//                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        }, userCode,token);
    }

    @Override
    public void getHomePageSelfList(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            mView.showLoad();
        }

        mData.getHomePageSelfList(new ApiCallBack<List<PersonalHomeDataBean>>() {
            @Override
            public void onSuccess(List<PersonalHomeDataBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showHomePageSelfList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },token,start+"");
    }


    @Override
    public void getHomePageUserList(String userCode,boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getHomePageUserList(new ApiCallBack<List<PersonalHomeDataBean>>() {
            @Override
            public void onSuccess(List<PersonalHomeDataBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showHomePageUserList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },userCode,start+"");
    }

    @Override
    public void getCircleListByUser(boolean isRefresh) {
//        mView.showLoad();
        mData.getCircleListByUser(new ApiCallBack<List<CircleBean>>() {
            @Override
            public void onSuccess(List<CircleBean> data) {
//                mView.showLoadFinish();
                if(mView == null) return;
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showCircleByUserList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
//                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },token,start+"");
    }

    @Override
    public void getCircleListByUserCode(String userCode, final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else {
            if(mView == null) return;
            mView.showLoadFinish();
        }
        mData.getCircleListByUserCode(new ApiCallBack<List<CircleBean>>() {
            @Override
            public void onSuccess(List<CircleBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                if(isRefresh){
                    mView.showRefreshFinish(data);
                }else{
                    mView.showCircleByUserCodeList(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },userCode,token,0,start);
    }

    @Override
    public void setUserFocus(String token, String followType, String bizId) {
        mData.setUserFocus(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showUserFocus(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },token, followType, bizId);
    }

    @Override
    public void loadMrore(String userCode) {
        start += 15;
        if (!TextUtils.isEmpty(userCode)) {
            mData.getCircleListByUserCode(new ApiCallBack<List<CircleBean>>() {
                @Override
                public void onSuccess(List<CircleBean> data) {
                    if(mView == null) return;
                    if (data == null || data.size() == 0) {
                        mView.showNoMore();
                        return;
                    }
                    mView.showLoadMoreList(data);
                }

                @Override
                public void onError(String errorCode, String message) {
                    if(mView == null) return;
                    start += 15;
                    ErrorCodeUtil.handleErr(mView, errorCode, message);
                }
            }, userCode,token, 0,start);

        } /*else {
//            getHomePageSelfList(true);
            mData.getCircleListByUser(new ApiCallBack<List<HisCircleBean>>() {
                @Override
                public void onSuccess(List<HisCircleBean> data) {
                    if (data == null || data.size() == 0) {
                        mView.showNoMore();
                        return;
                    }
                    mView.showLoadMoreList(data);
                }

                @Override
                public void onError(String errorCode, String message) {
                    start += 15;
                    ErrorCodeUtil.handleErr(mView, errorCode, message);
                }
            }, token, start + "");
        }*/
    }

    @Override
    public void getUserRecommendList(String userCode) {
        if(mView == null) return;
        mView.showLoad();
        mData.getUserRecommendList(new ApiCallBack<List<PersonalHomeDataBean>>() {
            @Override
            public void onSuccess(List<PersonalHomeDataBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showRecommendList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },userCode);
    }
}
