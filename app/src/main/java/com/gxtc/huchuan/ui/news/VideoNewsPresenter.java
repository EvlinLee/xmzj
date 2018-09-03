package com.gxtc.huchuan.ui.news;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.NewsItemRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.NewsCollectRepository;
import com.gxtc.huchuan.data.deal.VideoNewsRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 新闻收藏列表
 */

public class VideoNewsPresenter implements VideoNewsContract.Presenter {
    private VideoNewsContract.Source mData;
    private NewsItemContract.Source mDatas;
    private VideoNewsContract.View mView;
    private int start = 0;

    public VideoNewsPresenter(VideoNewsContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new VideoNewsRepository();
        mDatas = new NewsItemRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mDatas.destroy();
        mView = null;
    }


    @Override
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
        } else {
            if(mView == null)return;
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

    @Override
    public void shieldType(final String newsId, final String targetUserCode, int type, String shieldType) {
        String               token = UserManager.getInstance().getToken();
        HashMap<String, String> map   = new HashMap<>();
        map.put("token", token);
        map.put("type", type + "");
        map.put("shieldType", "1");

        //屏蔽文章
        if(type == 1){
            map.put("newsId", newsId);

            //屏蔽用户
        }else{
            map.put("targetUserCode", targetUserCode);
        }

        mDatas.shieldType(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView != null ){
                    mView.showShieldSuccess(newsId, targetUserCode);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView != null){
                    mView.showShieldError(newsId, targetUserCode, message);
                }
            }
        });
    }
}
