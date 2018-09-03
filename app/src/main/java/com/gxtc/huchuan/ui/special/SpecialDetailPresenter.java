package com.gxtc.huchuan.ui.special;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.data.SpeciaDetailRepository;
import com.gxtc.huchuan.data.SpecialBean;
import com.gxtc.huchuan.http.ApiCallBack;


/**
 * Created by laoshiren on 2018/5/25.
 */
public class SpecialDetailPresenter implements SpecialDetailContract.Presenter {
    private SpecialDetailContract.Source mData;
    private SpecialDetailContract.View mView;
    private String id = "";

    public SpecialDetailPresenter(SpecialDetailContract.View mView, String id) {
        this.mView = mView;
        this.id = id;
        this.mView.setPresenter(this);
        mData = new SpeciaDetailRepository();
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
    public void getData() {
        if (mView == null) return;
        mView.showLoad();

        mData.getData(id, new ApiCallBack<SpecialBean>() {

            @Override
            public void onSuccess(SpecialBean data) {
                if (mView == null) return;
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
    public void getSubscription() {
        if (mView == null) return;
        mView.showLoad();
        mData.getSubscription(id, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (mView == null) return;
                mView.showLoadFinish();
                mView.showSubscription();
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(), message);
            }
        });
    }

    @Override
    public void collectSpecia() {
        if (mView == null) return;
        mData.collectSpecia(id, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if (mView == null) return;
                mView.collectSpeciaSucc();
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(), message);
            }
        });
    }
}

