package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.AccountSetInfoBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.http.service.PayApi;
import com.gxtc.huchuan.ui.mine.loginandregister.register.RegisterContract;
import com.gxtc.huchuan.ui.mine.withdraw.WithdrawContract;

import java.util.HashMap;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/2/13 .
 */

public class WithdrawRepository extends BaseRepository implements WithdrawContract.Source {


    @Override
    public void getAccountSetInfo(ApiCallBack<AccountSetInfoBean> callBack, String token) {
        addSub(PayApi.getInstance().getAccountSetInfo(token).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<AccountSetInfoBean>>(callBack)));
    }

    @Override
    public void saveExpRecd(ApiCallBack<Void> callBack, HashMap<String, String> map) {
        addSub(PayApi.getInstance().saveExpRecd(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack)));
    }

    @Override
    public void upUser(final ApiCallBack<User> callBack, String token) {
        Subscription sub =
            MineApi.getInstance().getUserInfo(token).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<User>>(new ApiCallBack<User>() {
                    @Override
                    public void onSuccess(User data) {
                        if (data != null) {
                            UserManager.getInstance().saveUser(data);
                            callBack.onSuccess(data);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        callBack.onError(errorCode, message);
                    }
                }));

        addSub(sub);
    }
}

