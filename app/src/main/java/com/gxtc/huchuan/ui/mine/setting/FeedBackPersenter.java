package com.gxtc.huchuan.ui.mine.setting;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.data.FeedBackRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/4/7.
 */

public class FeedBackPersenter implements FeedBackContract.Presenter {
    private FeedBackContract.View mView;
    private FeedBackContract.Source mData;

    public FeedBackPersenter(FeedBackContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new FeedBackRepository();
    }

    @Override
    public void feeBack(HashMap<String, String> map) {
        if(mView == null)return;
        mView.showLoad();
        mData.feedBack(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.showLoadFinish();
                mView.showFeedBackResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
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
