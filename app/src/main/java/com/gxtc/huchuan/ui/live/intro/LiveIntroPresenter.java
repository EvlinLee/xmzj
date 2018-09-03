package com.gxtc.huchuan.ui.live.intro;

import android.app.Activity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.ChatInFoStatusBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.PayApi;
import com.gxtc.huchuan.ui.pay.PayActivity;

import java.math.BigDecimal;
import java.util.HashMap;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/8.
 */

public class LiveIntroPresenter implements LiveIntroContract.Presenter {

    private LiveIntroContract.View mView;
    private DealSource             dealData;

    public LiveIntroPresenter(LiveIntroContract.View view) {
        mView = view;
        mView.setPresenter(this);
        dealData = new DealRepository();
    }


    @Override
    public void getData(String id) {
        mView.showLoad();
        String token = null;
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
        }

        HashMap<String, String> map = new HashMap<>();
        if(!TextUtils.isEmpty(token))   map.put("token", token);
        map.put("chatInfoId", id);

        Observable<ApiResponseBean<ChatInfosBean>>      infoObs   = LiveApi.getInstance().getChatInfosBean(token, id);      //获取课程信息
        Observable<ApiResponseBean<ChatInFoStatusBean>> statusObs = LiveApi.getInstance().getChatInfoStatus(map);           //获取用户在课程中的状态信息等

        //获取课堂的禁言状态
        Subscription sub =
                Observable.concat(infoObs,statusObs)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new ApiObserver<ApiResponseBean<?>>(new ApiCallBack() {
                              @Override
                              public void onSuccess(Object data) {
                                  if(mView == null) return;

                                  if(data instanceof ChatInfosBean){
                                      mView.showLoadFinish();
                                      mView.showChatInfoData((ChatInfosBean) data);
                                  }

                                  if(data instanceof ChatInFoStatusBean){
                                      mView.showChatInFoStatusBean((ChatInFoStatusBean) data);
                                  }
                              }

                              @Override
                              public void onError(String errorCode, String message) {
                                  if(mView == null) return;
                                  mView.showLoadFinish();
                                  if(!String.valueOf(ErrorCodeUtil.NO_TOKEN_10002).equals(errorCode)){
                                      mView.showError(message);
                                  }
                              }

                              @Override
                              public void onCompleted() {

                              }
                          }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    @Override
    public void collect(String classId) {
        mView.showLoad();
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("bizType", "2");
        map.put("bizId", classId);

        dealData.saveCollect(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showCollectResult();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showError(message);
            }
        });
    }


    @Override
    public void follow(String id) {
        mView.showLoad();
        Subscription sub =
            LiveApi.getInstance()
                  .setUserFollow(UserManager.getInstance().getToken(), "2", id)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(
                          new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                              @Override
                              public void onSuccess(Object data) {
                                  if(mView != null){
                                      mView.showLoadFinish();
                                      mView.showFollowSuccess();
                                  }
                              }

                              @Override
                              public void onError(String errorCode, String message) {
                                  if(mView != null){
                                      mView.showLoadFinish();
                                      mView.showError(message);
                                  }
                              }
                          }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    //报名系列课
    @Override
    public void enrollSeries(Activity activity, ChatInfosBean bean, String shareUserCode, String freeSign) {
        String token  = UserManager.getInstance().getToken();
        String seriesId = bean.getChatSeries();

        //圈内成员免费报名
        if("1".equals(bean.getJoinGroup())){
            mView.showLoad();
            Subscription sub =
                LiveApi.getInstance()
                       .saveFreeChatSeriesBuy(token, seriesId)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                if(mView != null){
                                    mView.showLoadFinish();
                                    mView.showEnrollSeriesSuccess();
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if(mView != null){
                                    mView.showLoadFinish();
                                    mView.showError(message);
                                }
                            }
                        }));

            RxTaskHelper.getInstance().addTask(this, sub);
            return;
        }

        //免费邀请
        if(!TextUtils.isEmpty(shareUserCode) && !TextUtils.isEmpty(freeSign)){
            freeInviteJoinSeries(seriesId, shareUserCode, freeSign);
            return;
        }


        //普通的购买
        double fee = 0;
        try {
            fee = Double.valueOf(bean.getFee());
        }catch (Exception e){
            fee = 0;
        }
        BigDecimal moneyB = new BigDecimal(fee);

        //计算总价
        double total = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chatSeriesId", seriesId);
        if (!TextUtils.isEmpty(shareUserCode)) {
            jsonObject.put("userCode", shareUserCode);
            jsonObject.put("joinType", 0);
            jsonObject.put("type", 1);
        }
        String extra = jsonObject.toJSONString();

        OrdersRequestBean requestBean = new OrdersRequestBean();
        requestBean.setToken(UserManager.getInstance().getToken());
        requestBean.setTransType("CS");
        requestBean.setTotalPrice(total + "");
        requestBean.setExtra(extra);
        requestBean.setGoodsName("系列课购买");

        if (fee == 0) {
            mView.showLoad();
            requestBean.setPayType("ALIPAY");
            HashMap<String, String> map = new HashMap<>();
            map.put("token", requestBean.getToken());
            map.put("transType", requestBean.getTransType());
            map.put("payType", requestBean.getPayType());
            map.put("totalPrice", requestBean.getTotalPrice());
            map.put("extra", requestBean.getExtra());

            Subscription sub = PayApi.getInstance().getOrder(map).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<OrdersResultBean>>(
                            new ApiCallBack<OrdersResultBean>() {
                                @Override
                                public void onSuccess(OrdersResultBean data) {
                                    if (mView != null) {
                                        mView.showLoadFinish();
                                        mView.showEnrollSeriesSuccess();
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    if (mView != null) {
                                        mView.showLoadFinish();
                                        mView.showError(message);
                                    }
                                }
                            }));

            RxTaskHelper.getInstance().addTask(this, sub);

        } else {
            GotoUtil.goToActivity(activity, PayActivity.class, Constant.INTENT_PAY_RESULT, requestBean);
        }

    }


    //报名课堂
    @Override
    public void enrollClassroom(String id, String joinGroup, String shareUserCode) {
        if (UserManager.getInstance().isLogin()) {
            //这里不走支付接口
            if(TextUtils.isEmpty(shareUserCode) || "1".equals(joinGroup)){
                freeEnroll(id);

            //这里走支付接口 要生成订单
            }else{
                freeEnrollByPay(id, shareUserCode);
            }

        } else {
            mView.tokenOverdue();
        }
    }


    @Override
    public void payEnrollClassroom(Activity activity, ChatInfosBean bean, String shareUserCode, String freeSign) {
        if (!UserManager.getInstance().isLogin()) {
            mView.tokenOverdue();
            return;
        }

        //圈内成员 免费入场
        if(bean != null && "1".equals(bean.getJoinGroup())){
            enrollClassroom(bean.getId(), bean.getJoinGroup(), null);
            return;
        }

        String fee          = bean.getFee();
        String chatRoomName = bean.getChatRoomName();

        double money = 0;
        try {
            money = Double.valueOf(bean.getFee());
        }catch (Exception e){
            e.printStackTrace();
        }

        //课程收费，跳去支付界面
        if(TextUtils.isEmpty(shareUserCode) && money != 0){
            BigDecimal moneyB = new BigDecimal(fee);
            double     total  = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chatInfoId", bean.getId());
            String            extra       = jsonObject.toJSONString();
            OrdersRequestBean requestBean = new OrdersRequestBean();
            requestBean.setToken(UserManager.getInstance().getToken());
            requestBean.setTransType("CI");
            requestBean.setTotalPrice(total + "");
            requestBean.setExtra(extra);
            requestBean.setGoodsName("课堂收费-" + chatRoomName);

            GotoUtil.goToActivity(activity, PayActivity.class, Constant.INTENT_PAY_RESULT, requestBean);

        //课程免费而且不是别人分享的
        }else if( TextUtils.isEmpty(shareUserCode) && money == 0){
            freeEnrollByPay(bean.getId(), null);

        //免费邀请进入课堂的
        } else if(!TextUtils.isEmpty(freeSign) && !TextUtils.isEmpty(shareUserCode)) {
            freeInviteJoin(bean.getId(), shareUserCode, freeSign);

        //这里是走分销流程的
        } else {
            OrdersRequestBean requestBean = new OrdersRequestBean();

            BigDecimal moneyB    = new BigDecimal(fee);
            double     total     = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            if(total == 0){
                freeEnrollByPay(bean.getId(), null);

            }else{
                String     token     = UserManager.getInstance().getToken();
                String     transType = "CI";
                String     extra      = "{\"chatInfoId\":\"" + bean.getId() + "\",\"userCode\":\"" + shareUserCode + "\",\"joinType\":0,\"type\":1}";

                requestBean.setTotalPrice(total + "");
                requestBean.setToken(token);
                requestBean.setTransType(transType);
                requestBean.setExtra(extra);
                requestBean.setGoodsName("课堂收费-" + chatRoomName);

                GotoUtil.goToActivity(activity, PayActivity.class, 0, requestBean);
            }

        }
    }


    //免费报名，不走支付接口
    @Override
    public void freeEnroll(String id) {
        mView.showLoad();
        Subscription sub =
                LiveApi.getInstance()
                       .saveChatSignup(UserManager.getInstance().getToken(), id)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack<Void>() {
                           @Override
                           public void onSuccess(Void data) {
                               if(mView == null)   return;
                               mView.showLoadFinish();
                               mView.showEnrollSuccess();
                           }

                           @Override
                           public void onError(String errorCode, String message) {
                               if(mView == null)    return;
                               mView.showLoadFinish();
                               mView.showError(message);
                           }
                       }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    //免费报名，走支付接口
    @Override
    public void freeEnrollByPay(String id, String shareUserCode) {
        mView.showLoad();

        JSONObject jObject = new JSONObject();
        jObject.put("chatInfoId", id);
        jObject.put("joinType", 0);
        jObject.put("type", 1);

        if(!TextUtils.isEmpty(shareUserCode)){
            jObject.put("userCode", shareUserCode);
        }

        String     token      = UserManager.getInstance().getToken();
        String     transType  = "CI";
        String     payType    = "WX";
        String     extra      = jObject.toJSONString();
        double     total     = 0;

        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("transType",transType);
        map.put("payType",payType);
        map.put("totalPrice",total + "");
        map.put("extra",extra);

        Subscription sub =
                PayApi.getInstance()
                      .getOrder(map)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<OrdersResultBean>>(new ApiCallBack() {
                          @Override
                          public void onSuccess(Object data) {
                              if(mView == null)   return;
                              mView.showLoadFinish();
                              mView.showEnrollSuccess();
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              if(mView == null)    return;
                              mView.showLoadFinish();
                              mView.showError(message);
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    //免费邀请加入课堂
    @Override
    public void freeInviteJoin(String id, String shareUserCode, String freeSign) {
        mView.showLoad();

        String     token      = UserManager.getInstance().getToken();
        String     transType = "CI";
        String     payType    = "WX";
        String     totalPrice = "0";

        JSONObject jobj = new JSONObject();
        jobj.put("chatInfoId", id);
        jobj.put("freeSign", freeSign);
        jobj.put("userCode", shareUserCode);
        jobj.put("joinType", 3);
        String extra = jobj.toString();

        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("transType",transType);
        map.put("payType",payType);
        map.put("totalPrice",totalPrice);
        map.put("extra",extra);

        Subscription sub =
                PayApi.getInstance()
                      .getOrder(map)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<OrdersResultBean>>(new ApiCallBack() {
                          @Override
                          public void onSuccess(Object data) {
                              if(mView == null) return;
                              mView.showLoadFinish();
                              mView.showEnrollSuccess();
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              if(mView != null){
                                  mView.showLoadFinish();
                                  mView.showError(message);
                              }
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    //免费邀请系列课
    private void freeInviteJoinSeries(String id, String shareUserCode, String freeSign){
        mView.showLoad();
        String     token      = UserManager.getInstance().getToken();
        String     transType = "CS";
        String     payType    = "WX";
        String     totalPrice = "0";

        JSONObject jobj = new JSONObject();
        jobj.put("chatSeriesId", id);
        jobj.put("freeSign", freeSign);
        jobj.put("userCode", shareUserCode);
        jobj.put("joinType", 4);
        String extra = jobj.toString();

        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("transType",transType);
        map.put("payType",payType);
        map.put("totalPrice",totalPrice);
        map.put("extra",extra);

        Subscription sub =
                PayApi.getInstance()
                      .getOrder(map)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<OrdersResultBean>>(new ApiCallBack() {
                          @Override
                          public void onSuccess(Object data) {
                              if (mView != null) {
                                  mView.showLoadFinish();
                                  mView.showEnrollSeriesSuccess();

                              }
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              if(mView != null){
                                  mView.showLoadFinish();
                                  mView.showError(message);
                              }
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        dealData.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
