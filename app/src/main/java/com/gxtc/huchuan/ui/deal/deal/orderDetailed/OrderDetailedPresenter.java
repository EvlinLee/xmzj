package com.gxtc.huchuan.ui.deal.deal.orderDetailed;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.OrderDetailedBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class OrderDetailedPresenter implements OrderDetailedContract.Presenter {

    private OrderDetailedContract.View mView;
    private DealSource                 mData;
    private Subscription               timeSub;
    public OrderDetailedPresenter(OrderDetailedContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealRepository();
    }


    @Override
    public void getData(String id) {
        mView.showLoad();
        String token = UserManager.getInstance().getToken();
        mData.getOrderDetailed(id + "", token, new ApiCallBack<OrderDetailedBean>() {
            @Override
            public void onSuccess(OrderDetailedBean data) {
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

    @Override
    public void submitAgreeOrReject(HashMap<String, String> map) {
        mView.showLoad();
        mData.submitAgreeOrReject(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showAgreeOrRejectSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showAgreeOrRejectFailed(message);
            }
        });
    }

    @Override
    public void confirmDeal(HashMap<String, String> map) {
        mView.showLoad();
        mData.confirmDeal(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showConfirmSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showConfirmFailed(message);
            }
        });
    }

    @Override
    public void getChatRoom(String token, String tradeInfoId) {
        mData.getChatRoom(token, tradeInfoId, new ApiCallBack<ChatRoomBean>() {
            @Override
            public void onSuccess(ChatRoomBean data) {
                if(mView == null)   return;
                mView.showChatRoom(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showGetChatRoomFailed(errorCode);
            }
        });
    }

    @Override
    public void saveChatRoom(HashMap<String, String> map) {
        mData.saveChatRoom(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showSaveChatSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showSaveChatFailed(message);
            }
        });
    }

    @Override
    public void computeEndTime(final long endTime) {
        if(System.currentTimeMillis() >= endTime){
            mView.showEndTime(true,"");

        }else{
            if(timeSub != null){
                timeSub.unsubscribe();
            }
            timeSub = Observable.interval(1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Long>() {
                                    @Override
                                    public void call(Long aLong) {
                                        if(mView == null)   return;
                                        long time = (endTime - System.currentTimeMillis());      //相差几秒
                                        String [] s = DateUtil.countDown(time);

                                        if(!"00".equals(s[0])){
                                            mView.showEndTime(false, s[0] + "天 " + s[1] + ":"+ s[2] + ":" + s[3]);
                                        }else{
                                            mView.showEndTime(false,s[1] +":"+ s[2] + ":" + s[3]);
                                        }

                                        if(System.currentTimeMillis() >= endTime){
                                            timeSub.unsubscribe();
                                            mView.showEndTime(true,"");
                                        }

                                    }
                                });
        }
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
