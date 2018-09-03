package com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.data.PersonalInfoRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/6/1.
 */

public class PersonalInfoPresenter implements PersonalInfoContract.Presenter{

    private PersonalInfoContract.View   mView;
    private PersonalInfoContract.Source mData;

    public PersonalInfoPresenter(PersonalInfoContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new PersonalInfoRepository();
    }
    @Override
    public void getUserInformation(HashMap<String,String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.getUserInformation(new ApiCallBack<PersonInfoBean>() {
            @Override
            public void onSuccess(PersonInfoBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showUserInformation(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        },map);
    }

    @Override
    public void saveLinkRemark(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.saveLinkRemark(new ApiCallBack<PersonInfoBean>() {
            @Override
            public void onSuccess(PersonInfoBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showsaveLinkRemark(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        },map);
    }

    @Override
    public void folowUser(String userCode) {
        String token = UserManager.getInstance().getToken();
        mData.setUserFocus(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showFollowSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        },token, "3", userCode);
    }

    @Override
    public void applyFriends(String userCode,String message) {
        String token = UserManager.getInstance().getToken();
        mData.applyFriends(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showApplySuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        },token, "3", userCode,message);
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
