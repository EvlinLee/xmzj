package com.gxtc.huchuan.ui.live.list;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.LiveListBean;
import com.gxtc.huchuan.data.LiveListRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Gubr on 2017/2/27.
 */

public class LiveListPresenter implements LiveListContract.Presenter {

    private String currentType;

    private final LiveListContract.View mView;
    private final LiveListRepository mSource;

    public LiveListPresenter(LiveListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mSource = new LiveListRepository();
    }

    @Override
    public void getLiveListDatas(final int id) {
        mSource.getLiveListDatas(new ApiCallBack<List<LiveListBean>>() {
            @Override
            public void onSuccess(List<LiveListBean> data) {
                mView.showLiveListDatas(data);
                mSource.saveHistory(id, data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        }, 0, id);

    }

    @Override
    public void getLoadMore(final int id, int count) {
        mSource.getLiveListDatas(new ApiCallBack<List<LiveListBean>>() {
            @Override
            public void onSuccess(List<LiveListBean> data) {
                if (data==null||data.size()==0){
                    mView.showLoadFinish();
                }else{
                    mView.showLoadMore(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        }, count, id);

    }


//    @Override
//    public void changeListType(String temp) {
//        currentType = temp;
//        List<LiveListBean> history = mSource.getHistory(currentType);
//        mView.showLiveListDatas(history);
//
//    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mSource.destroy();
    }
}
