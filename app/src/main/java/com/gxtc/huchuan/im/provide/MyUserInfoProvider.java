package com.gxtc.huchuan.im.provide;

import android.net.Uri;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ClickUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;

import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 17/4/18.
 * 融云的用户信息提供者，这里暂时只做内存缓存
 */

public class MyUserInfoProvider implements RongIM.UserInfoProvider{

    private HashMap<String,UserInfo> cache;


    public MyUserInfoProvider() {
        cache = new HashMap<>();
    }

    @Override
    public UserInfo getUserInfo(String s) {
        UserInfo info = cache.get(s);
        if(info != null){
            return info;
        }
        LogUtil.i("getUserInfo");
        String token = UserManager.getInstance().getToken();
        LiveApi.getInstance().getUserInfo(token,s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        User user = (User) data;
                        if(user != null && !TextUtils.isEmpty(user.getHeadPic())){
                            UserInfo info = new UserInfo(user.getUserCode(),user.getName(), Uri.parse(user.getHeadPic()));
                            cache.put(user.getUserCode(),info);
                            RongIM.getInstance().refreshUserInfoCache(info);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {

                    }
                }));
        return null;
    }
}
