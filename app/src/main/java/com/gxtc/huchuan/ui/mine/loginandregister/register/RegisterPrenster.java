package com.gxtc.huchuan.ui.mine.loginandregister.register;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.EditInfoRepository;
import com.gxtc.huchuan.data.RegisterRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoContract;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ALing on 2017/2/13.
 */

public class RegisterPrenster implements RegisterContract.Presenter{

    RegisterContract.Source mData;
    RegisterContract.View mView;
    EditInfoContract.Source mEditInfoData;
    public RegisterPrenster(RegisterContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new RegisterRepository();
        mEditInfoData = new EditInfoRepository();
    }

    @Override
    public void getRegister(HashMap<String, String> map) {
        EventBusUtil.post(new EventLoadBean(true));
        mData.getRegister(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User data) {
                EventBusUtil.post(new EventLoadBean(false));
                if(mView == null) return;
                if (data != null){
                    mView.showRegisterSuccess(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                EventBusUtil.post(new EventLoadBean(false));
                if(mView == null) return;
                mView.showError(message);
            }
        },map);
    }

    @Override
    public void getEditInfo(Map<String, String> map) {
        mEditInfoData.getEditInfo(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.EditInfoSuccess(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);
    }


    @Override
    public void getValidationCode(String phone, String type) {
        mData.getValidationCode(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showValidationCode(data);
                mView.showCountdown();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        },phone,type);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mEditInfoData.destroy();
        mView = null;
    }
}
