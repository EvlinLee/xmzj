package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CopywritingBean;
import com.gxtc.huchuan.data.deal.CopywritingRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;


public class CopywritingPresenter implements CopywritingContract.Presenter{

    private CopywritingContract.View    mView;
    private CopywritingContract.Source  mData;

    public CopywritingPresenter(CopywritingContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new CopywritingRepository();
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
    public void getData() {
        mData.getData(new ApiCallBack<List<CopywritingBean>>() {
            @Override
            public void onSuccess(List<CopywritingBean> data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
