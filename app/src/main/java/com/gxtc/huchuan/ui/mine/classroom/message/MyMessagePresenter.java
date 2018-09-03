package com.gxtc.huchuan.ui.mine.classroom.message;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.ClassMyMessageBean;
import com.gxtc.huchuan.data.MyMessageRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/10.
 */

public class MyMessagePresenter implements MyMessageContract.Presenter{
    private MyMessageContract.Source mData;
    private MyMessageContract.View mView;

    private int start = 0;
    private String token = UserManager.getInstance().getToken();

    public MyMessagePresenter(MyMessageContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new MyMessageRepository();
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
    public void getData(final boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null)return;
            mView.showLoad();
        }

        mData.getData(token,start+"", new ApiCallBack<List<ClassMyMessageBean>>() {

            @Override
            public void onSuccess(List<ClassMyMessageBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return ;
                }

                if(isRefresh){
                    mView.showRefreshFinish(data);
                }else{
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMore() {
        start += 15;

        mData.getData(token,start+"", new ApiCallBack<List<ClassMyMessageBean>>() {
            @Override
            public void onSuccess(List<ClassMyMessageBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                start += 15;
                mView.showLoad();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
