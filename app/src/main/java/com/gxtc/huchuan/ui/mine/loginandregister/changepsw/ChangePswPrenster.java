package com.gxtc.huchuan.ui.mine.loginandregister.changepsw;

import com.gxtc.huchuan.data.ChangePwsRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Created by ALing on 2017/2/14 0014.
 */

public class ChangePswPrenster implements ChangePswContract.Presenter{
    ChangePswContract.Source mData;
    ChangePswContract.View mView;

    public ChangePswPrenster(ChangePswContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ChangePwsRepository();
    }


    @Override
    public void changePws(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.changePws(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.showLoadFinish();
                mView.changePwsResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                mView.showError(message);
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
        mView = null;
    }
}
