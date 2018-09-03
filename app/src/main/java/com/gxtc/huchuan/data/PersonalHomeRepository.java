package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomeContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/4/6 .
 */

public class PersonalHomeRepository extends BaseRepository implements PersonalHomeContract.Source{
    //获取自己个人信息
    @Override
    public void getUserSelfInfo(ApiCallBack<User> callBack, String token) {
        addSub(MineApi.getInstance().getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));
    }
    //获取用户个人信息
    @Override
    public void getUserMemberByUserCode(ApiCallBack<User> callBack, String userCode,String token) {
        addSub(MineApi.getInstance().getUserMemberByUserCode(userCode,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));
    }
    //获取自己个人主页列表
    @Override
    public void getHomePageSelfList(ApiCallBack<List<PersonalHomeDataBean>> callBack, String token, String start) {
        addSub(MineApi.getInstance().getHomePageSelfList(token,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<PersonalHomeDataBean>>>(callBack)));
    }
    //获取用户个人主页列表
    @Override
    public void getHomePageUserList(ApiCallBack<List<PersonalHomeDataBean>> callBack, String userCode, String start) {
        addSub(MineApi.getInstance().getHomePageUserList(userCode,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<PersonalHomeDataBean>>>(callBack)));
    }

    @Override
    public void getCircleListByUser(ApiCallBack<List<CircleBean>> callBack, String token, String type) {
        addSub(MineApi.getInstance().getCircleListByUser(token,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));
    }

    @Override
    public void getCircleListByUserCode(ApiCallBack<List<CircleBean>> callBack, String userCode, String token, int type,int start) {
        addSub(MineApi.getInstance().getCircleListByUserCode(userCode,token,type,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));
    }

    @Override
    public void setUserFocus(ApiCallBack<Object> callBack, String token, String followType, String bizId) {
        addSub(AllApi.getInstance().setUserFollow(token,followType,bizId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void getUserRecommendList(ApiCallBack<List<PersonalHomeDataBean>> callBack, String userCode) {
        addSub(MineApi.getInstance().getUserRecommendList(userCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<PersonalHomeDataBean>>>(callBack)));
    }
}
