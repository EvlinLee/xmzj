package com.gxtc.huchuan.ui.circle.dynamic;

import android.media.projection.MediaProjection;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/6.
 */

public class DynamicPresenter implements DynamicContract.Presenter {

    private CircleSource mData;
    private DynamicContract.View mView;

    private int id;
    private int start = 0;

    public DynamicPresenter(DynamicContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new CircleRepository();

    }

    @Override
    public void getData(int id) {
        this.id = id;
        mView.showLoad();

        String token = UserManager.getInstance().getToken();
        mData.getDynamicList(token, id, start, new ApiCallBack<List<CircleHomeBean>>() {
            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null)   return;
                mView.showLoadFinish();
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
    public void dianZan(final int id) {
        String token = UserManager.getInstance().getToken();
        mData.dianZan(token, id, new ApiCallBack<ThumbsupVosBean>() {
            @Override
            public void onSuccess(ThumbsupVosBean data) {
                if(mView == null) return;
                mView.showDZSuccess(id);
            }

            @Override
            public void onError(String errorCode, String message) {

            }
        });
    }

    @Override
    public void refreshData() {
        String token = UserManager.getInstance().getToken();
        start = 0;
        mData.getDynamicList(token, id, start, new ApiCallBack<List<CircleHomeBean>>() {
            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    return;
                }
                mView.showRefreshData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void loadMoreData() {
        String token = UserManager.getInstance().getToken();
        start += 15;
        mData.getDynamicList(token, id, start, new ApiCallBack<List<CircleHomeBean>>() {
            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null) return;
                if(data == null || data.size() == 0){
                    return;
                }
                mView.showLoadMoreData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }


}
