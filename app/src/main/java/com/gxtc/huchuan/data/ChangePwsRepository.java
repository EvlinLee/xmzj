package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.loginandregister.changepsw.ChangePswContract;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/2/14.
 *
 */

public class ChangePwsRepository extends BaseRepository implements ChangePswContract.Source {
    @Override
    public void changePws(ApiCallBack<Object> callBack, HashMap<String, String> map) {

        addSub(MineApi.getInstance().changePws(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)
                ));
    }

    @Override
    public void getValidationCode(ApiCallBack<Object> callBack, String phone, String type) {
        addSub(MineApi.getInstance().getValidateCode(phone,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)
                ));
    }

    @Override
    public void verifyCode(String phone, String code, ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().verifyCode(phone,code)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)
                      ));
    }

    @Override
    public void changePhone(String token, String phone, String password, ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().changePhone(token,phone,password)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void verifyPassword(String token, String password, ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().verifyPassword(token,password)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void changePassword(String token, String oldPwd, String newPwd,
            ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().changePassword(token,oldPwd,newPwd)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void bindWx(String token, String name, String code,
            ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().bindWx(token,name,code)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

}
