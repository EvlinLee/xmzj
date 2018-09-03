package com.gxtc.huchuan.ui.im.redPacket;

import android.content.Intent;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.Date;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/7.
 */

public class RedPacketDetailedPresenter implements RedPacketDetailedContract.Presenter {

    private RedPacketDetailedContract.View mView;
    private CircleSource                   mData;
    private String loadTime  ;
    private int start = 0;
    private String id;

    public RedPacketDetailedPresenter(RedPacketDetailedContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new CircleRepository();
    }

    @Override
    public void getData(String id,String loadTime) {
        mView.showLoad();
        this.loadTime = loadTime;
        start = 0;
        this.id = id;
        mData.getOpenRPList(id, start,loadTime, new ApiCallBack<List<RedPacketBean>>() {
            @Override
            public void onSuccess(List<RedPacketBean> data) {
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
    public void getRedInfo(String id) {
        mView.showLoad();
        String token = UserManager.getInstance().getToken();
        mData.getRedPacketInfo(token, id, new ApiCallBack<RedPacketBean>() {
            @Override
            public void onSuccess(RedPacketBean data) {
                if(mView == null)   return;

                mView.showLoadFinish();
                if(data != null){
                    mView.showRedInfo(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void loadMoreData() {
        start += 15;
        mData.getOpenRPList(id, start,this.loadTime, new ApiCallBack<List<RedPacketBean>>() {
            @Override
            public void onSuccess(List<RedPacketBean> data) {
                if(mView == null)   return;

                if(data == null || data.size() == 0){
                    mView.showNoLoadMore();
                    return;
                }
                mView.showLoadMoreData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;

                mView.showError(message);
                start -= 15;
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
