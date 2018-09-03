package com.gxtc.huchuan.ui.mine.setting.dustbin;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.DustbinListBean;
import com.gxtc.huchuan.bean.event.EventDustbinRecoverBean;
import com.gxtc.huchuan.data.DustbinRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class DustbinListPersenter implements DustbinListContract.Presenter{

    private DustbinListContract.Source mData;
    private DustbinListContract.View   mView;
    private int start = 0;
    private String token = UserManager.getInstance().getToken();

    public DustbinListPersenter(DustbinListContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DustbinRepository();
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
    public void getData(final boolean isRefresh) {
//        if(isRefresh){
//            start = 0;
//        }else{
//            mView.showLoad();
//        }
        mData.getData(token,0, new ApiCallBack<List<DustbinListBean>>() {

            @Override
            public void onSuccess(List<DustbinListBean> data) {
                if(mView == null)return;
                mView.showLoadFinish();
                if(data == null || data.size() == 0){
                    mView.showEmpty();
                    return ;
                }

                if(isRefresh){
                    mView.showRefreshFinish(data);
                }else{
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
    public void HuiFuDustbin(final int id, int type) {
        if(mView == null)return;
        mView.showLoad();
        String token = UserManager.getInstance().getToken();
        mData.HuiFuDustbin(token,id,type, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)return;
                mView.showLoadFinish();
                mView.showHuifuSuccess(id);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(token,start, new ApiCallBack<List<DustbinListBean>>() {
            @Override
            public void onSuccess(List<DustbinListBean> data) {
                if(mView == null)return;
                if(data == null || data.size() == 0){
                    mView.showNoMore();
                    return ;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)return;
                start += 15;
                mView.showLoad();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }


    @Override
    public void refreshData() {

    }

    @Override
    public void loadMoreData() {

    }
}
