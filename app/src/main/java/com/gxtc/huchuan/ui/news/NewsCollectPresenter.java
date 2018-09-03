package com.gxtc.huchuan.ui.news;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.deal.NewsCollectRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 新闻收藏列表
 */

public class NewsCollectPresenter implements NewsCollectContract.Presenter {
    private NewsCollectContract.Source mData;
    private NewsCollectContract.View mView;
    private int start = 0;

    public NewsCollectPresenter(NewsCollectContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new NewsCollectRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        mData.destroy();
    }


    @Override
    public void getData(final boolean isRefresh) {
        if(mView == null)return;
        if (isRefresh) {
            start = 0;
        } else {
            mView.showLoad();
        }

        mData.getData(start, new ApiCallBack<List<NewsBean>>() {

            @Override
            public void onSuccess(List<NewsBean> data) {
                if(mView == null)return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start +=15;
        mData.getData(start, new ApiCallBack<List<NewsBean>>() {

            @Override
            public void onSuccess(List<NewsBean> data) {
                if(mView == null)return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)return;
                mView.showError(message);
            }
        });
    }
}
