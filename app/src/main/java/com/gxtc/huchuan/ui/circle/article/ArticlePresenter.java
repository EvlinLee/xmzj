package com.gxtc.huchuan.ui.circle.article;

import android.util.Log;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.search.SearchContract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 */

public class ArticlePresenter implements ArticleContract.Presenter {
    private ArticleContract.Source mData;
    private ArticleContract.View   mView;
    private int start = 0;

    private String userCode;
    private int mCircleid;
    private HashMap map;
    int searchStart = 0;

    public ArticlePresenter(ArticleContract.View mView, int circleid, String userCode) {
        this.mView=mView;
        this.mView.setPresenter(this);
        mData = new ArticleRepository();
        map = new HashMap<String,String>();
        this.mCircleid=circleid;
        this.userCode=userCode;
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
            mView.showLoad();
        }
        mData.getData(start, mCircleid, userCode, new ApiCallBack<List<NewNewsBean.DataBean>>() {

            @Override
            public void onSuccess(List<NewNewsBean.DataBean> data) {
                if (mView == null) return;

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
    public void getSeachData(boolean isLoadMore,String searchKey) {
        if(!isLoadMore){
            searchStart = 0;
        }else {
            searchStart = searchStart + 15;
        }
        map.put("token", UserManager.getInstance().getToken());
        map.put("start", searchStart+"");
        map.put("searchKey", searchKey);
        map.put("groupId",String.valueOf(mCircleid));
        mData.getSeachData(map, new ApiCallBack<List<NewNewsBean.DataBean>>() {
            @Override
            public void onSuccess(List<NewNewsBean.DataBean> data) {
                if(mView == null) return;
                if (searchStart == 0 && (data == null || data.size() == 0)) {
                    mView.showEmpty();
                    return;
                }
                mView.showSeachData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showError(message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(start, mCircleid, userCode, new ApiCallBack<List<NewNewsBean.DataBean>>() {

            @Override
            public void onSuccess(List<NewNewsBean.DataBean> data) {
                if (mView == null) return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if (mView == null) return;
                mView.showError(message);
            }
        });
    }

}
