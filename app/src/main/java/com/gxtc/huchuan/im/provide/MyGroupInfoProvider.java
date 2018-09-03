package com.gxtc.huchuan.im.provide;

import android.net.Uri;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ClickUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;

import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Group;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 17/4/18.
 * 融云的群组信息提供者，这里暂时只做内存缓存
 */

public class MyGroupInfoProvider implements RongIM.GroupInfoProvider{

    private HashMap<String,Group> cache;


    public MyGroupInfoProvider() {
        cache = new HashMap<>();
    }

    @Override
    public Group getGroupInfo(String s) {

        Group info = cache.get(s);
        if(info != null){
            return info;
        }

        LogUtil.i("getUserInfo");
        String token = UserManager.getInstance().getToken();
        CircleApi.getInstance()
                 .getGroupInfo(token,s)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
                 .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                     @Override
                     public void onSuccess(Object data) {
                         CircleBean bean = (CircleBean) data;
                         if(bean != null && !TextUtils.isEmpty(bean.getCover())){
                             Group group = new Group(bean.getChatId(),bean.getGroupName(),Uri.parse(bean.getCover()));
                             cache.put(bean.getChatId(),group);
                             RongIM.getInstance().refreshGroupInfoCache(group);
                         }
                     }

                     @Override
                     public void onError(String errorCode, String message) {

                     }
                 }));

        return null;
    }
}
