package com.gxtc.huchuan.ui.mine.circle.article;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.bean.CircleNewsBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.data.ArticleAuditedListRepository;
import com.gxtc.huchuan.data.NewsItemRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.circle.article.ArticleContract;
import com.gxtc.huchuan.ui.news.NewsItemContract;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 */

public class ArticleAuditedListPresenter implements ArticleAuditedListContract.Presenter {
    private ArticleAuditedListContract.Source mData;
    private ArticleAuditedListContract.View mView;
    private int start = 0;
    private String id;
    private int flag;
    private String userCode;

    public ArticleAuditedListPresenter(ArticleAuditedListContract.View mView, String id, int flag, String userCode) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ArticleAuditedListRepository();
        this.id = id;
        this.flag = flag;
        this.userCode = userCode;
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
        if (isRefresh) {
            start = 0;
        } else {
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(start, id, flag, userCode, new ApiCallBack<List<CircleNewsBean>>() {

            @Override
            public void onSuccess(List<CircleNewsBean> data) {
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
                LogUtil.printD("message:" + message);

                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(start, id, flag, userCode, new ApiCallBack<List<CircleNewsBean>>() {

            @Override
            public void onSuccess(List<CircleNewsBean> data) {
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
