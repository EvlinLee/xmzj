package com.gxtc.huchuan.ui.mine.circle;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/24.
 */

public class NoticeListPresenter implements NoticeListContract.Presenter{

    private NoticeListContract.View mView;
    private CircleSource mData;

    private int id;
    private int start = 0;

    public NoticeListPresenter(NoticeListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new CircleRepository();
    }

    @Override
    public void getData(int id) {
        this.id = id;
        if(mView == null) return;
        mView.showLoad();
        mData.getNoticeData(id, start, new ApiCallBack<List<CircleBean>>() {
            @Override
            public void onSuccess(List<CircleBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void refreshData() {
        start = 0;
        mData.getNoticeData(id, start, new ApiCallBack<List<CircleBean>>() {
            @Override
            public void onSuccess(List<CircleBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    return;
                }
                mView.showRefreshData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void loadMoreData() {
        start += 15;
        mData.getNoticeData(id, start, new ApiCallBack<List<CircleBean>>() {
            @Override
            public void onSuccess(List<CircleBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    mView.showNoLoadMore();
                    return;
                }
                mView.showLoadMoreData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                start -= 15;
                mView.showError(message);
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
