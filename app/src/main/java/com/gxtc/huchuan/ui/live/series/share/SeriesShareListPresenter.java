package com.gxtc.huchuan.ui.live.series.share;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.data.TopicShareListRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.live.intro.TopicShareListContract;

import java.util.HashMap;

/**
 * Created by sjr on 2017/2/15.
 * 分享榜
 */

public class SeriesShareListPresenter implements SeriesShareListContract.Presenter {
    public DealSource                    dealData;
    private TopicShareListContract.Source mData;
    private SeriesShareListContract.View   mView;
    private int start = 0;
    private String id ;


    public SeriesShareListPresenter(SeriesShareListContract.View mView) {
        this.mView = mView;
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
    public void getData(String id) {
        mView.showLoad();
        this.id = id;
        mData.getSeriesShareData(id, start, new ApiCallBack<TopicShareListBean>() {

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
        mData.getSeriesShareData(id, start, new ApiCallBack<TopicShareListBean>() {

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

}
