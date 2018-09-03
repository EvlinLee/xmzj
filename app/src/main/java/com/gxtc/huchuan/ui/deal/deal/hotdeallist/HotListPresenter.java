package com.gxtc.huchuan.ui.deal.deal.hotdeallist;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.data.deal.DealHotListRepository;
import com.gxtc.huchuan.data.deal.DealListRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.deal.deal.dealList.DealListContract;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 *
 */

public class HotListPresenter implements HotListContract.Presenter {

    private HotListContract.View   mView;
    private HotListContract.Source mData;

    private int start = 0;

    public HotListPresenter (HotListContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealHotListRepository();

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
    public void getData(boolean isRefresh) {
        mView.showLoad();
        if(isRefresh) start = 0;
        mData.getData(start, new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showData(data);

            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                ErrorCodeUtil.handleErr(mView, errorCode, message);

            }
        });

    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(start, new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if(mView == null)   return;
                if (data == null || data.size() == 0) {
                    start -= 15;
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                start -= 15;
                mView.showError(message);
            }
        });

    }
}
