package com.gxtc.huchuan.ui.mine.loginandregister.login;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.LoginRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Created by ALing on 2017/2/8
 */

public class LoginPrenster implements LoginContract.Presenter {

    LoginContract.Source mData;
    LoginContract.View mView;

    public LoginPrenster(LoginContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new LoginRepository();
    }

    @Override
    public void getLogin(String count, String password) {
        EventBusUtil.post(new EventLoadBean(true));
        mData.getLogin(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User data) {
                EventBusUtil.post(new EventLoadBean(false));
                if(mView == null) return;
                if (data != null){
                    mView.showLogin(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                EventBusUtil.post(new EventLoadBean(false));
                if(mView == null) return;
                mView.showError(message);
            }
        },count,password);
    }

    @Override
    public void getThirdLogin(final HashMap<String, String> map) {
        EventBusUtil.post(new EventLoadBean(true));
        mData.getThirdLogin(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User data) {
                EventBusUtil.post(new EventLoadBean(false));
                mView.thirdLoginResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                EventBusUtil.post(new EventLoadBean(false));
                if(mView == null) return;
                if ("10006".equals(errorCode)){
                    mView.thirdLoginBindPhone();
                }else {
                    ErrorCodeUtil.handleErr(mView,errorCode,message);
                }
            }
        },map);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;

    }
}
