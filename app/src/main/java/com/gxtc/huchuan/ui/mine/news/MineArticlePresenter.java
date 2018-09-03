package com.gxtc.huchuan.ui.mine.news;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.deal.MineArticleRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/3/30.
 * 自己的文章列表
 */

public class MineArticlePresenter implements MineArticleContract.Presenter {
    private MineArticleContract.Source mData;
    private MineArticleContract.View mView;
    private int start = 0;

    public MineArticlePresenter(MineArticleContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new MineArticleRepository();
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
        if (isRefresh) {
            start = 0;
        } else {
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(start, new ApiCallBack<List<NewsBean>>() {

            @Override
            public void onSuccess(List<NewsBean> data) {
                if(mView == null) return;
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
                if(mView == null) return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }
}
