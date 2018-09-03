package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.loginandregister.login.LoginContract;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/2/8 .
 *
 */

public class LoginRepository extends BaseRepository implements LoginContract.Source {
    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void getLogin(ApiCallBack<User> callBack, String count, String pws) {

        addSub(MineApi.getInstance().getLogin(count,pws)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));

    }

    @Override
    public void getThirdLogin(ApiCallBack<User> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().getThirdLogin(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));
    }
}
