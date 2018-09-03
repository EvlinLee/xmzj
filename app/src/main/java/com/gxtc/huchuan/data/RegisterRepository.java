package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.loginandregister.register.RegisterContract;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/2/13 .
 */

public class RegisterRepository extends BaseRepository implements RegisterContract.Source {

    @Override
    public void getRegister(ApiCallBack<User> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().getRegister(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));
    }

    @Override
    public void getValidationCode(ApiCallBack<Object> callBack, String phone, String type) {
        addSub(MineApi.getInstance().getValidateCode(phone,type)
                .subscribeOn(Schedulers.io())       //io线程请求网络
                .observeOn(AndroidSchedulers.mainThread())      //主线程执行回调方法
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }
}
