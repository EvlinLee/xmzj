package com.gxtc.huchuan.ui.deal.liuliang.profit;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.ProfitListBean;
import com.gxtc.huchuan.data.deal.ProfitRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Steven on 17/3/22.
 *
 */

public class ProfitPresenter implements ProfitContract.Presenter{

    private ProfitContract.View   mView;
    private ProfitContract.Source mData;

    private int start = 0;

    public ProfitPresenter(ProfitContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ProfitRepository();
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
        if(isRefresh){
            start = 0;
        }else{
            mView.showLoad();
        }

        mData.getData(start, new ApiCallBack<List<ProfitListBean>>() {
            @Override
            public void onSuccess(List<ProfitListBean> data) {
                if(mView == null)   return;

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
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMrore() {

    }
}
