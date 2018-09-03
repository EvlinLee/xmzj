package com.gxtc.huchuan.ui.mine.personalhomepage.Deal;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/3.
 */

public class PersonDealPresenter implements PersonDealContract.Presenter {

    private PersonDealContract.View mView;
    private DealSource mData;
    private int start = 0 ;
    private String userCode;
    private String token;

    public PersonDealPresenter(PersonDealContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void getUserDealList(String token, String userCode) {
        this.userCode = userCode;
        this.token = token;
        mData.getUserDealList(token,userCode,start, new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
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
        mData.getUserDealList(token,userCode,start, new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return;
                }
                mView.showRefreshData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMoreData() {
        start += 15;
        String token = UserManager.getInstance().getToken();
        mData.getUserDealList(token,userCode,start, new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if(data == null || data.size() == 0){
                    mView.showNoLoadMore();
                    return;
                }
                mView.showLoadMoreData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }


}
