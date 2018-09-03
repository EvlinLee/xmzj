package com.gxtc.huchuan.ui.mine.classroom.purchaserecord;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.data.MyPurchseRecordResitory;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/24 .
 */

public class PurchaseRecordPresenter implements PurchaseRecordContract.Presenter {

    private PurchaseRecordContract.Source mData;
    private PurchaseRecordContract.View mView;
    private int start;
    String token = UserManager.getInstance().getToken();
    public PurchaseRecordPresenter(PurchaseRecordContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new MyPurchseRecordResitory();
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
    public void getSeriesAndTopic(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getSeriesAndTopic(token,start+"",new ApiCallBack<List<PurchaseSeriesAndTopicBean>>() {
            @Override
            public void onSuccess(List<PurchaseSeriesAndTopicBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showSeriesAndTopic(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

   /* @Override
    public void getToicData(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            mView.showLoad();
        }
        mData.getToicData(token,start+"",new ApiCallBack<List<PurchaseTopicRecordBean>>() {
            @Override
            public void onSuccess(List<PurchaseTopicRecordBean> data) {
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showToicData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getSeriesData(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            mView.showLoad();
        }
        mData.getSeriesData(token,start+"",new ApiCallBack<List<PurchaseSeriesRecordBean>>() {
            @Override
            public void onSuccess(List<PurchaseSeriesRecordBean> data) {
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showSeriesData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
*/
    @Override
    public void getCircleData(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getCircleData(token,start+"",new ApiCallBack<List<PurchaseCircleRecordBean>>() {
            @Override
            public void onSuccess(List<PurchaseCircleRecordBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showCircleData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getDealData(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getDealData(token,start+"",new ApiCallBack<List<PurchaseListBean>>() {
            @Override
            public void onSuccess(List<PurchaseListBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showDealData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    //todo  还没有接口
    @Override
    public void getAllOrderData(boolean isRefresh, String type, final int start) {
        if(!isRefresh){
            if(mView == null) return;
            mView.showLoad();
        }
        mData.getAllOrderData(token, type,start+"",new ApiCallBack<List<AllPurchaseListBean>>() {
            @Override
            public void onSuccess(List<AllPurchaseListBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || (data.size() == 0 && start == 0)){
                    mView.showEmpty();
                    return;
                }
                mView.showAllOrderData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMoreSeriesAndTopic() {
        start += 15;
        mData.getSeriesAndTopic(token,start+"",new ApiCallBack<List<PurchaseSeriesAndTopicBean>>() {
            @Override
            public void onSuccess(List<PurchaseSeriesAndTopicBean> data) {
                if(mView == null) return;
                if (data == null || data.size() == 0){
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMoreSeriesAndTopic(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    /*@Override
    public void loadMroreToic() {
        start += 15;
        mData.getToicData(token,start+"",new ApiCallBack<List<PurchaseTopicRecordBean>>() {
            @Override
            public void onSuccess(List<PurchaseTopicRecordBean> data) {
                if (data == null || data.size() == 0){
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMoreToic(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMroreSeries() {
        start += 15;
        mData.getSeriesData(token,start+"",new ApiCallBack<List<PurchaseSeriesRecordBean>>() {
            @Override
            public void onSuccess(List<PurchaseSeriesRecordBean> data) {
                if (data == null || data.size() == 0){
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMoreSeries(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }*/

    @Override
    public void loadMoreCircle() {
        start += 15;
        mData.getCircleData(token,start+"",new ApiCallBack<List<PurchaseCircleRecordBean>>() {
            @Override
            public void onSuccess(List<PurchaseCircleRecordBean> data) {
                if(mView == null) return;
                if (data == null || data.size() == 0){
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMoreCircle(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMoreDeal() {
        start += 15;
        mData.getDealData(token,start+"",new ApiCallBack<List<PurchaseListBean>>() {
            @Override
            public void onSuccess(List<PurchaseListBean> data) {
                if(mView == null) return;
                if (data == null || data.size() == 0){
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMoreDeal(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                start += 15;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

}
