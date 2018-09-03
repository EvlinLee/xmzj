package com.gxtc.huchuan.ui.live.intro;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.data.TopicShareListRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.data.deal.NewsCollectRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.news.NewsCollectContract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 分享榜
 */

public class TopicShareListPresenter implements TopicShareListContract.Presenter {
    public DealSource                    dealData;
    private TopicShareListContract.Source mData;
    private TopicShareListContract.View   mView;
    private int start = 0;
    private String chatRoomId;
    private String chatInfoId;

    public TopicShareListPresenter(TopicShareListContract.View mView, String chatRoomId, String chatInfoId) {
        this.mView = mView;
        this.chatRoomId = chatRoomId;
        this.chatInfoId = chatInfoId;
        this.mView.setPresenter(this);
        mData = new TopicShareListRepository();
        dealData = new DealRepository();
    }

    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        dealData.destroy();
        mView = null;
    }


    @Override
    public void getData() {
        mData.getData(start, chatRoomId, chatInfoId, new ApiCallBack<TopicShareListBean>() {

            @Override
            public void onSuccess(TopicShareListBean data) {
                if(mView == null)   return;

                mView.showLoadFinish();
                if (data == null) {
                    mView.showEmpty();
                    return;
                }
                mView.showData(data);

            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(start, chatRoomId, chatInfoId, new ApiCallBack<TopicShareListBean>() {

            @Override
            public void onSuccess(TopicShareListBean data) {
                if(mView == null)   return;
                if (data.getDatas() == null || data.getDatas().size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void collect(String classId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("bizType", "2");
        map.put("bizId", classId);

        dealData.saveCollect(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showCollectResult();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }
}
