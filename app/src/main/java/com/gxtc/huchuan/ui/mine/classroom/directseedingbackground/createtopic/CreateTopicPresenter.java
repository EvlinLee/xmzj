package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.data.CreateLiveTopicRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/3/20 .
 */

public class CreateTopicPresenter implements CreateTopicContract.Presenter {
    private CreateTopicContract.View mView;
    private CreateTopicContract.Source mData;
    public CreateTopicPresenter(CreateTopicContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new CreateLiveTopicRepository();
    }



    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }


    @Override
    public void createLiveTopic(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.createLiveTopic(map, new ApiCallBack<CreateLiveTopicBean>() {
            @Override
            public void onSuccess(CreateLiveTopicBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.createLiveResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

}
