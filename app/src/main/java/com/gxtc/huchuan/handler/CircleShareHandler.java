package com.gxtc.huchuan.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.PayApi;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.home.CircleWebActivityv2;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.ui.special.SpecialDetailActivity;
import com.gxtc.huchuan.utils.DialogUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.rong.imkit.model.UIMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/1.
 * 圈子转发处理类
 */

public class CircleShareHandler {

    public static final String SHARE_NEW = "1";                    //资讯分享
    public static final String SHARE_CLASS = "2";                  //课堂分享
    public static final String SHARE_DEAL = "3";                   //交易分享
    public static final String SHARE_CIRCLE = "4";                 //圈子分享
    public static final String SHARE_CIRCLE_ADMIN = "5";           //邀请圈子管理员
    public static final String SHARE_FREE_CIRCLE = "6";            //免费圈子
    public static final String SHARE_SERIES = "7";                 //分享系列课
    public static final String SHARE_TEACHER = "8";                //课堂讲师
    public static final String SHARE_CLASS_ADMIN = "9";            //课堂管理员
    public static final String SHARE_FREE_CLASS = "10";            //免费课堂
    public static final String SHARE_FREE_SERIES = "11";           //免费系列课
    public static final String SHARE_MALL = "12";                  //商品详情
    public static final String SHARE_LIVE = "13";                  //直播间
    public static final String SHARE_SPECIAL = "14";               //专题

    private WeakReference<Context> mReference;
    private Context mContext;

    public CircleShareHandler(Context context) {
        mReference = new WeakReference<>(context);
    }


    //收藏操作
    public void collectHandle(String id, String type, String url){
        Intent intent = null;
        if(getContext() == null)    return;

        //新闻
        if("1".equals(type)){
            getNewsData(id);
        }

        //课堂
        if("2".equals(type)){
            getLiveInfo(id,null);
        }

        //交易信息
        if("3".equals(type)){
            intent = new Intent(getContext(), GoodsDetailedActivity.class);
            intent.putExtra(Constant.INTENT_DATA,id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }

        //圈子动态
        if("4".equals(type)){
            getCirCleDynamicData(id);
        }

        //笔记
        if("5".equals(type)){
            CommonWebViewActivity.startActivity(getContext(), url, "收藏");
        }


        if("6".equals(type)){
            Message message = new Message();
            UIMessage uiMessage = UIMessage.obtain(message);
            getCircleInfo(id,4,uiMessage);
        }

        //系列课
        if(SHARE_SERIES.equals(type)){
            SeriesActivity.startActivity(mContext,id);
        }

        //商品
        if("8".equals(type)){
            MallDetailedActivity.startActivity(mContext,id);
        }

        //直播间
        if("9".equals(type)){
            LiveHostPageActivity.startActivity(mContext,"1",id);
        }

        //专题
        if("10".equals(type)){
            if(mContext instanceof Activity){
                SpecialDetailActivity.gotoSpecialDetailActivity((Activity) mContext, id);
            }
        }
    }


    /**
     * 分享操作
     * 这个方法主要操作分享东西到聊天里面的点击，圈子动态的点击也会复用一部分, 有一部分冲突 所有没有全部复用
     */
    public void shareHandle(Context context, final String typeId, final int infoType, final UIMessage uiMessage){
        if(getContext() == null)    return;
        String type = infoType + "";

        //新闻链接
        if(infoType == 1){
            getNewsData(typeId);
            return;
        }

        //课程
        if(infoType == 2){
            getLiveInfo(typeId,uiMessage);
            return;
        }

        //交易
        if(infoType == 3){
            Intent dealIntent = new Intent(getContext(), GoodsDetailedActivity.class);
            dealIntent.putExtra(Constant.INTENT_DATA,typeId);
            dealIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(dealIntent);
            return;
        }

        //邀请普通成员
        if(infoType == 4){
            getCircleInfo(typeId,infoType,uiMessage);
        }

        //邀请圈子管理员
        if(infoType == 5 && !uiMessage.getSenderUserId().equals(UserManager.getInstance().getUserCode())){
            if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
                String temp [] = typeId.split("&");
                if(temp.length >= 2){
                    getCircleInfo(temp[0], new ApiCallBack<CircleBean>() {
                        @Override
                        public void onSuccess(CircleBean data) {
                            if(getContext() == null)   return;
                            EventBusUtil.post(new EventLoadBean(false));
                            if(data.getIsJoin() == 0){
                                showAgreeJoinDialog(getContext(),typeId,infoType,uiMessage,"确认成为管理员?");
                            }else{
                                Intent intent = new Intent(getContext(), CircleMainActivity.class);
                                intent.putExtra("groupId",data.getId());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            EventBusUtil.post(new EventLoadBean(false));
                            ToastUtil.showShort(getContext(),message);
                        }
                    });
                }
            }

        }

        //免费邀请好友加入圈子
        if(infoType == 6){
            if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
                final String temp [] = typeId.split("&");
                if(temp.length >= 2){
                    getCircleInfo(temp[0], new ApiCallBack<CircleBean>() {

                        @Override
                        public void onSuccess(CircleBean data) {
                            if(getContext() == null)   return;
                            EventBusUtil.post(new EventLoadBean(false));

                            if(data.getIsJoin() == 0){
                                CircleJoinActivity.freeInvite(getContext(),data.getId(),uiMessage.getSenderUserId(),temp[1],CircleJoinActivity.INVITE_MEMBER);

                            }else{
                                Intent intent = new Intent(getContext(), CircleMainActivity.class);
                                intent.putExtra("groupId",data.getId());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            EventBusUtil.post(new EventLoadBean(false));
                            ToastUtil.showShort(getContext(),message);
                        }
                    });

                }
            }
        }

        //系列课主页
        if(type.equals(SHARE_SERIES)){
            if(uiMessage.getConversationType() == Conversation.ConversationType.PRIVATE){
                SeriesActivity.startActivity(mContext,typeId, uiMessage.getSenderUserId());
            }else{
                SeriesActivity.startActivity(mContext,typeId);
            }
        }

        // 8 讲师（主持人）
        if(type.equals(SHARE_TEACHER)){
            if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
                String temp [] = typeId.split("&");
                if(temp.length >= 2){
                    if(uiMessage.getSenderUserId().equals(UserManager.getInstance().getUserCode())){
                        getLiveInfo(temp[0],uiMessage);
                        return;
                    }
                    getData(temp[0],infoType,uiMessage,typeId);
                }
            }
        }

        // 9 管理员
        if(type.equals(SHARE_CLASS_ADMIN)){
            if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
                String temp [] = typeId.split("&");
                if(temp.length >= 2){
                    if(uiMessage.getSenderUserId().equals(UserManager.getInstance().getUserCode())){
                        getLiveInfo(temp[0], uiMessage);
                        return;
                    }
                    getData(temp[0], infoType, uiMessage, typeId);
                }
            }
        }

        //10 免费邀请课堂
        if(type.equals(SHARE_FREE_CLASS)){
            if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
                String temp [] = typeId.split("&");

                if(temp.length >= 2){
                    String userCode = UserManager.getInstance().getUserCode();

                    if(userCode != null && userCode.equals(uiMessage.getSenderUserId())){
                        LiveIntroActivity.startActivity(mContext, temp[0]);
                    }else{
                        LiveIntroActivity.freeInvite(mContext, temp[0], uiMessage.getSenderUserId(), temp[1]);
                    }
                }
            }
        }

        //11 免费邀请系列课
        if(type.equals(SHARE_FREE_SERIES)){
            if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
                String temp [] = typeId.split("&");

                if(temp.length >= 2){
                    String userCode = UserManager.getInstance().getUserCode();

                    if(userCode != null && userCode.equals(uiMessage.getSenderUserId())){
                        SeriesActivity.startActivity(mContext, temp[0]);
                    }else{
                        SeriesActivity.freeInvite(mContext, temp[0], uiMessage.getSenderUserId(), temp[1]);
                    }
                }
            }
        }

        //12 商品详情
        if(type.equals(SHARE_MALL)){
            MallDetailedActivity.startActivity(mContext, typeId);
        }

        //13 直播间
        if(type.equals(SHARE_LIVE)){
            Intent intent = new Intent(context, LiveHostPageActivity.class);
            intent.putExtra("liveId", typeId);
            intent.putExtra("jumpPage", 0);
            intent.putExtra("isManage", "1");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        //专题
        if(type.equals(SHARE_SPECIAL)){
            if(mContext instanceof Activity){
                SpecialDetailActivity.gotoSpecialDetailActivity((Activity) mContext, typeId);
            }
        }
    }


    private void getData(final String id, final int infoType, final UIMessage uiMessage, final String typeId) {
        if(infoType == 8)
            showAgreeJoinClassDialog(getContext(),typeId,infoType,uiMessage,"确认成为讲师?");
        else if(infoType == 9)
            showAgreeJoinClassDialog(getContext(),typeId,infoType,uiMessage,"确认成为管理员?");
    }


    private AlertDialog joinDialog;
    private void showAgreeJoinClassDialog(Context context, String typeId, final int infoType, UIMessage uiMessage, String title){
        final String                        userCode   = uiMessage.getMessage().getSenderUserId();
        final Conversation.ConversationType converType = uiMessage.getConversationType();

        if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
            String       temp []  = typeId.split("&");

            if(temp.length >= 2){
                final String Id = temp[0];
                final String freeSign = temp[1];

                joinDialog = DialogUtil.showCommonDialog(context, false, "", title, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 8 讲师
                        if(infoType == 8){

                            //只有单聊的分享可以赚取佣金
                            if(converType == Conversation.ConversationType.PRIVATE){
                                inviteJoinClass(Id,2,freeSign,userCode);
                            }else{
                                inviteJoinClass(Id,2,freeSign,"");
                            }
                        }

                        // 9 管理员
                        if(infoType == 9){
                            inviteJoinClass(Id,1,freeSign,userCode);
                        }
                        joinDialog.dismiss();
                    }
                });
            }
        }
    }


    private void showAgreeJoinDialog(Context context, String typeId, final int infoType, UIMessage uiMessage, String title){
        final String                        userCode   = uiMessage.getMessage().getSenderUserId();
        final Conversation.ConversationType converType = uiMessage.getConversationType();

        if(!TextUtils.isEmpty(typeId) && typeId.contains("&")){
            String       temp []  = typeId.split("&");

            if(temp.length >= 2){
                final String circleId = temp[0];
                final String freeSign = temp[1];

                joinDialog = DialogUtil.showCommonDialog(context, false, "", title, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //免费邀请管理员
                        if(infoType == 5){

                            //只有单聊的分享可以赚取佣金
                            if(converType == Conversation.ConversationType.PRIVATE){
                                inviteJoinCircle(circleId,1,freeSign,userCode);
                            }else{
                                inviteJoinCircle(circleId,1,freeSign,"");
                            }
                        }

                        //免费邀请好友
                        if(infoType == 6){
                            inviteJoinCircle(circleId,0,freeSign,userCode);
                        }
                        joinDialog.dismiss();
                    }
                });
            }
        }
    }


    /**
     * 邀请加入圈子 joinType  邀请类型 0普通成员，1管理员  type      是否分销。0不是，1是
     * @param id        圈子id
     * @param freeSign  免费邀请码
     */
    public void inviteJoinCircle(String id, int joinType , String freeSign,String userCode){
        EventBusUtil.post(new EventLoadBean(true));
        String     token      = UserManager.getInstance().getToken();
        String     transType  = "GJ";
        String     payType    = "WX";
        String     totalPrice = "0";
        String     extra      = "{\"groupId\":\"" + id + "\",\"userCode\":\"" + userCode + "\",\"joinType\":\"" + joinType + "\",\"type\":0,\"freeSign\":\"" + freeSign + "\"}";

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
                              EventBusUtil.post(new EventLoadBean(false));
                              ToastUtil.showShort(getContext(),"加入圈子成功");
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              EventBusUtil.post(new EventLoadBean(false));
                              if("10170".equals(errorCode)){
                                ToastUtil.showShort(getContext(),"该分享已被其他用户使用");
                              }else if("10129".equals(errorCode)){
                                  ToastUtil.showShort(getContext(),"你已经是该圈子管理员");
                              }else{
                                  ToastUtil.showShort(getContext(),message);
                              }
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    public void inviteJoinClass(String id, final int joinType , String freeSign,String userCode){
        EventBusUtil.post(new EventLoadBean(true));
        String     token      = UserManager.getInstance().getToken();
        String     transType  = "CI";
        String     payType    = "WX";
        String     totalPrice = "0";
        String     extra      = "{\"chatInfoId\":\"" + id + "\",\"userCode\":\"" + userCode + "\",\"joinType\":\"" + joinType + "\",\"type\":0,\"freeSign\":\"" + freeSign + "\"}";

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
                              EventBusUtil.post(new EventLoadBean(false));
                              if(joinType == 1){
                                  ToastUtil.showShort(getContext(),"已成为管理员");
                              }else if(joinType == 2){
                                  ToastUtil.showShort(getContext(),"已成为讲师");
                              }
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              EventBusUtil.post(new EventLoadBean(false));
                              ToastUtil.showShort(getContext(),message);
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    //获取圈子动态信息
    public void getCirCleDynamicData(String id){
        EventBusUtil.post(new EventLoadBean(true));
        String token = UserManager.getInstance().getToken();
        Subscription sub = CircleApi.getInstance()
                                    .getDynamicData(token,id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new ApiObserver<ApiResponseBean<CircleHomeBean>>(new ApiCallBack() {
                                         @Override
                                         public void onSuccess(Object data) {
                                             if(getContext() == null)   return;

                                             EventBusUtil.post(new EventLoadBean(false));

                                             Intent intent = new Intent(getContext(),DynamicDetialActivity.class);
                                             CircleHomeBean bean = (CircleHomeBean) data;
                                             intent.putExtra("id", bean.getId()+"");
                                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                             getContext().startActivity(intent);
                                         }

                                         @Override
                                         public void onError(String errorCode, String message) {
                                             EventBusUtil.post(new EventLoadBean(false));
                                             ToastUtil.showShort(getContext(),message);
                                         }
                                     }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    //获取新闻信息
    public void getNewsData(String typeId) {
        EventBusUtil.post(new EventLoadBean(true));
        String token = UserManager.getInstance().getToken();
        Subscription sub = AllApi.getInstance().getNewsInfo(token,typeId)
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .subscribeOn(Schedulers.io())
                                 .subscribe(new ApiObserver<ApiResponseBean<NewsBean>>(new ApiCallBack() {
                                     @Override
                                     public void onSuccess(Object data) {
                                         if(getContext() == null)   return;

                                         EventBusUtil.post(new EventLoadBean(false));

                                         NewsBean bean = (NewsBean) data;
                                         Intent intent = new Intent(getContext(), NewsWebActivity.class);
                                         intent.putExtra("data",bean);
                                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                         getContext().startActivity(intent);
                                     }

                                     @Override
                                     public void onError(String errorCode, String message) {
                                         EventBusUtil.post(new EventLoadBean(false));
                                         ToastUtil.showShort(getContext(),message);
                                     }
                                 }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    //获取圈子信息
    public void getCircleInfo(final String typeId, final int infoType, final UIMessage uiMessage) {
        EventBusUtil.post(new EventLoadBean(true));
        try {
            int id = Integer.valueOf(typeId);
            String token = UserManager.getInstance().getToken();
            Subscription sub = CircleApi.getInstance()
                                        .circleData(token, id)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                if(getContext() == null)   return;
                                                EventBusUtil.post(new EventLoadBean(false));
                                                CircleBean bean = (CircleBean) data;
                                                if(bean != null){

                                                    //未加入圈子
                                                    if(bean.getIsJoin() == 0 && uiMessage != null){

                                                        //邀请普通成员
                                                        if(infoType == 4){
                                                            String myCode = UserManager.getInstance().getUserCode();

//                                                            if(uiMessage.getSenderUserId() != null && uiMessage.getSenderUserId().equals(myCode)) return;

                                                            Intent joinIntent = new Intent(getContext(),CircleJoinActivity.class);
                                                            joinIntent.putExtra("byLiveId",Integer.valueOf(typeId));

                                                            //只有单聊的分享可以赚取佣金
                                                            if(uiMessage.getConversationType() == Conversation.ConversationType.PRIVATE){
                                                                joinIntent.putExtra("shareUserCode",uiMessage.getSenderUserId());
                                                            }
                                                            joinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            getContext().startActivity(joinIntent);
                                                            return;
                                                        }

                                                        Intent joinIntent = new Intent(getContext(),CircleJoinActivity.class);
                                                        joinIntent.putExtra("byLiveId",Integer.valueOf(typeId));
                                                        joinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        getContext().startActivity(joinIntent);

                                                    }else{
                                                        Intent intent = new Intent(getContext(), CircleMainActivity.class);
                                                        intent.putExtra("groupId",bean.getId());
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        getContext().startActivity(intent);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onError(String errorCode, String message) {
                                                EventBusUtil.post(new EventLoadBean(false));
                                                ToastUtil.showShort(getContext(),message);
                                            }
                                        }));

            RxTaskHelper.getInstance().addTask(this,sub);
        }catch (Exception e){
            EventBusUtil.post(new EventLoadBean(false));
            e.printStackTrace();
        }

    }


    public void getCircleInfo(String typeId,ApiCallBack<CircleBean> callBack){
        EventBusUtil.post(new EventLoadBean(true));
        try {
            int id = Integer.valueOf(typeId);
            String token = UserManager.getInstance().getToken();
            Subscription sub = CircleApi.getInstance()
                                        .circleData(token,id)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(callBack));

            RxTaskHelper.getInstance().addTask(this,sub);
        }catch (Exception e){
            EventBusUtil.post(new EventLoadBean(false));
            e.printStackTrace();
        }
    }


    //获取课堂信息  uiMessage 需要算佣金就传 不需要就传null
    public void getLiveInfo(final String typeId, final UIMessage uiMessage){
        EventBusUtil.post(new EventLoadBean(true));
        String token = UserManager.getInstance().getToken();
        Subscription sub =
            LiveApi.getInstance()
                .getChatInfosBean(token,typeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<ChatInfosBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        EventBusUtil.post(new EventLoadBean(false));
                        ChatInfosBean bean = (ChatInfosBean) data;
                        if(data == null | getContext() == null)    return;

                        //跳去系列课主页购买
                        if(!"0".equals(bean.getChatSeries()) && !bean.isSingUp()){
                            SeriesActivity.startActivity(getContext(),bean.getChatSeries(), true,Intent.FLAG_ACTIVITY_NEW_TASK);
                        }else{

                            //只有私聊的能分享佣金
                            if(uiMessage != null && uiMessage.getConversationType() == Conversation.ConversationType.PRIVATE){
                                LiveIntroActivity.startActivity(getContext(),typeId,uiMessage.getSenderUserId(),Intent.FLAG_ACTIVITY_NEW_TASK);
                            }else{
                                LiveIntroActivity.startActivity(getContext(),typeId,Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        EventBusUtil.post(new EventLoadBean(false));
                        ToastUtil.showShort(getContext(),message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    //主要是应对从app广告位点击进来的
    public void getLiveInfoFromAd(final String typeId, final UIMessage uiMessage){
        final Intent intentMain = new Intent(getContext(), MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        EventBusUtil.post(new EventLoadBean(true));
        String token = UserManager.getInstance().getToken();
        Subscription sub =
            LiveApi.getInstance()
                .getChatInfosBean(token,typeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<ChatInfosBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        EventBusUtil.post(new EventLoadBean(false));
                        ChatInfosBean bean = (ChatInfosBean) data;
                        if(data == null | getContext() == null)    return;

                        //跳去系列课主页购买
                        if(!"0".equals(bean.getChatSeries()) && !bean.isSingUp()){
                            Intent intent = new Intent(getContext(), SeriesActivity.class);
                            intent.putExtra("id", bean.getChatSeries());
                            intent.putExtra("isByLiveFragment", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivities(new Intent[]{intentMain,intent});
                        }else{

                            //只有私聊的能分享佣金
                            if(uiMessage != null && uiMessage.getConversationType() == Conversation.ConversationType.PRIVATE){
                                Intent intent = new Intent(getContext(), LiveIntroActivity.class);
                                intent.putExtra("id", typeId);
                                intent.putExtra("shareUserCode", uiMessage.getSenderUserId());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivities(new Intent[]{intentMain,intent});
                            }else{
                                Intent intent = new Intent(getContext(), LiveIntroActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("id", typeId);
                                getContext().startActivities(new Intent[]{intentMain,intent});
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        EventBusUtil.post(new EventLoadBean(false));
                        ToastUtil.showShort(getContext(),message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    private Context getContext(){
        if(mContext == null)
            mContext = mReference.get();
        return mContext;
    }


    public void destroy(){
        mReference.clear();
        RxTaskHelper.getInstance().cancelTask(this);
    }

}
