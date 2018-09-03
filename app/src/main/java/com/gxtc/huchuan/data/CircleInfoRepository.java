package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleInfoContract;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/8 .
 */

public class CircleInfoRepository extends BaseRepository implements CircleInfoContract.Source {

    @Override
    public void getMemberList(int groupId, int type, int start, String onlyLook,ApiCallBack<List<CircleMemberBean>> callBack) {
        addSub((CircleApi.getInstance().getListMember(groupId,type,start,20,onlyLook )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ApiObserver<ApiResponseBean<List<CircleMemberBean>>>(callBack))));
    }

    @Override
    public void getCircleInfo(String token,int groupId, ApiCallBack<CircleBean> callBack) {
        addSub(CircleApi.getInstance().getCircleMainInfo(token,groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(callBack)));
    }

    @Override
    public void editCircleInfo(HashMap<String, Object> map, ApiCallBack<Object> callBack) {
        addSub(CircleApi.getInstance().editCircle(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void removeMember(String token, int groupId, String userCode,
            ApiCallBack<Void> apiCallBack) {
        addSub((CircleApi.getInstance()
                         .clean(token,groupId,Integer.valueOf(userCode))
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<Void>>(apiCallBack))));
    }

    @Override
    public void transCircle(String token, int groupId, String userCode,
            ApiCallBack<Void> apiCallBack) {
        addSub((CircleApi.getInstance().transafer(token,groupId,Integer.valueOf(userCode))
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<Void>>(apiCallBack))));
    }

    @Override
    public void changeMemberTpye(String token, int groupId, String userCode, int i,
            ApiCallBack<Void> apiCallBack) {
        addSub((CircleApi.getInstance().changeMember(token,groupId,userCode,i)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<Void>>(apiCallBack))));
    }
}
