package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoContract;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/6/1 .
 */

public class PersonalInfoRepository extends BaseRepository implements PersonalInfoContract.Source{
    //194.获取个人详细资料接口
    @Override
    public void getUserInformation(ApiCallBack<PersonInfoBean> callBack, HashMap<String,String> map) {
        addSub(MineApi.getInstance().getUserInformation(map)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<PersonInfoBean>>(callBack)));
    }

    @Override
    public void saveLinkRemark(ApiCallBack<PersonInfoBean> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().saveLinkRemark(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<PersonInfoBean>>(callBack)));
    }


    @Override
    public void setUserFocus(ApiCallBack<Object> callBack, String token, String followType, String bizId) {
        addSub(AllApi.getInstance().setUserFollow(token,followType,bizId)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void applyFriends(ApiCallBack<Object> callBack, String token, String followType, String bizId,String message) {
        addSub(AllApi.getInstance().applyFriends(token,followType,bizId,message)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }
}
