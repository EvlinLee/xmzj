package com.gxtc.huchuan.ui.circle.findCircle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CircleJoinDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.http.service.PayApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.ui.pay.PayConstant;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.ProgressWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 加入圈子页面
 */
public class CircleJoinActivity extends BaseTitleActivity implements View.OnClickListener {

    public static final int INVITE_MEMBER = 1 ;         //免费邀请成员

    @BindView(R.id.webView)      ProgressWebView mWebView;
    @BindView(R.id.tv_money)     TextView        tvMoney;
    @BindView(R.id.btn_previews) TextView        btnPre;
    @BindView(R.id.btn)          TextView        btn;

    private String      url;
    private String      name;
    private String      isAudit;
    private String      freeSign;        //免费邀请码
    private String      shareUserCode = "";   //邀请人的usercode
    private int         id;
    private int         inviteType;      //邀请成员类型
    private double      money;
    public  CircleBean  infoBean;
    public AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_join);
    }

    @Override
    public void initView() {
        freeSign = getIntent().getStringExtra("freeSign");
        id = getIntent().getIntExtra("byLiveId", 0);
        inviteType = getIntent().getIntExtra("inviteType",0);

        //现在app所有的内部分享都不得佣金 除非是微信外部分享  课程也是这样
        if(!TextUtils.isEmpty(freeSign)){
            shareUserCode = getIntent().getStringExtra("shareUserCode");
        }

        if(inviteType == INVITE_MEMBER){
            btn.setText("免费加入");
            tvMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if(id == 0){
            url = getIntent().getStringExtra("url");
            name = getIntent().getStringExtra("name");
            id = getIntent().getIntExtra("id", 0);
            money = getIntent().getDoubleExtra(Constant.INTENT_DATA, 0);
            isAudit = getIntent().getStringExtra("isAudit");
            setView();
        } else {
            getInfo();
        }

        boolean xufei = getIntent().getBooleanExtra("xuefei",false);
        if(xufei){
            btn.setText("马上续费");
        }
    }


    @OnClick({R.id.btn,R.id.btn_previews})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            //加入圈子
            case R.id.btn:
                //自己选择加入圈子
                if(TextUtils.isEmpty(shareUserCode)){
                    gotoJoin();

                //被邀请加入圈子
                }else{
                    inviteJoin();
                }
                break;

            //预览
            case R.id.btn_previews:
                gotoPre();
                break;

            case R.id.HeadRightImageButton:
                getGroupInfo();
                break;
        }
    }

    private void setData(String url, String name, double money, String isAud) {
        this.url = url;
        this.name = name;
        this.money = money;
        this.isAudit = isAud;
    }

    private void setView() {
        if(getBaseHeadView() == null) return;
        getBaseHeadView().showTitle(name);
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
        mWebView.loadUrl(url + "&from=android&usercode="+UserManager.getInstance().getUserCode());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.getSettings().setBlockNetworkImage(false);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == WebViewClient.ERROR_CONNECT) {
                    getBaseEmptyView().showEmptyView(R.drawable.load_error, "加载失败了",null);
                }
            }

        });



        String temp;
        if (money != 0) {
            temp = "¥" + String.format(Locale.CHINA, "%.2f", money);
        } else {
            temp = "免费";
        }
        tvMoney.setText(temp);
    }

    //预览圈子
    private void gotoPre() {
        Intent intent = new Intent(this, CircleMainActivity.class);
        intent.putExtra("groupId",id);
        intent.putExtra("pre",true);
        startActivity(intent);
    }

    //被邀请加入圈子
    private void inviteJoin(){
        if(money == 0){
            //是否需要提交审核资料
            if("1".equals(isAudit) && inviteType != INVITE_MEMBER){
                showJoinDialog();
            }else{
                inviteJoinFree("",freeSign);
            }
            return;
        }

        //如果是收费的圈子，判断是否是免费邀请进圈子
        if(inviteType == INVITE_MEMBER){
            inviteJoinFree("",freeSign);

        } else{
            OrdersRequestBean requestBean = new OrdersRequestBean();

            BigDecimal moneyB    = new BigDecimal(money);
            double     total     = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            String     token     = UserManager.getInstance().getToken();
            String     transType = "GJ";
            String     extra      = "{\"groupId\":\"" + id + "\",\"userCode\":\"" + shareUserCode + "\",\"joinType\":0,\"type\":1}";

            requestBean.setTotalPrice(total + "");
            requestBean.setToken(token);
            requestBean.setTransType(transType);
            requestBean.setExtra(extra);
            requestBean.setGoodsName(name);

            GotoUtil.goToActivity(this, PayActivity.class, 0, requestBean);
        }

    }

    private void gotoJoin() {
        if (TextUtils.isEmpty(url)) return;
        if (!UserManager.getInstance().isLogin(this))   return;

        if (money == 0) {
            //只有需要审核的才需要输入验证信息
            if("1".equals(isAudit) && inviteType != INVITE_MEMBER){
                showJoinDialog();
            }else{
                joinFree("");
            }
        } else {
            joinPay();
        }
    }

    public void getPrompt(){
        getBaseLoadingView().showLoading();
        Subscription sub =
                CircleApi.getInstance()
                         .getGroupData(UserManager.getInstance().getToken(), id)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                             @Override
                             public void onSuccess(Object data) {
                                 if(getBaseLoadingView() == null)   return;
                                 getBaseLoadingView().hideLoading();
                                 String prompt = (String) GsonUtil.getJsonValue(GsonUtil.objectToJson(data),"prompt");
                                 if (mDialog == null) {
                                     mDialog = new CircleJoinDialog(CircleJoinActivity.this,prompt);
                                     mDialog.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             String content = (String) v.getTag();
                                             if(TextUtils.isEmpty(content.trim())){
                                                 ToastUtil.showShort(MyApplication.getInstance(),"验证消息不能为空");
                                                 return;
                                             }
                                             if(content.trim().length() > 500){
                                                 ToastUtil.showShort(MyApplication.getInstance(),"验证消息不能超过500字");
                                                 return;
                                             }
                                             if(TextUtils.isEmpty(shareUserCode)){
                                                 joinFree((String) v.getTag());
                                             }else{
                                                 inviteJoinFree((String) v.getTag(),freeSign);
                                             }
                                         }
                                     });
                                 }
                                 mDialog.show();
                             }

                             @Override
                             public void onError(String errorCode, String message) {
                                 if(getBaseLoadingView() == null)   return;
                                 getBaseLoadingView().hideLoading();
                                 ToastUtil.showShort(MyApplication.getInstance(), message);
                             }
                         }));
        RxTaskHelper.getInstance().addTask(this,sub);

    }


    private CircleJoinDialog mDialog;

    private void showJoinDialog() {
        getPrompt();
    }

    private void inviteJoinFree(String message, String freeSign){
        String     token      = UserManager.getInstance().getToken();
        String     transType  = "GJ";
        String     payType    = "WX";
        double     total     = 0;
        //String     extra      = "{\"groupId\":\"" + id + "\",\"userCode\":\"" + shareUserCode + "\",\"joinType\":0,\"type\":1,\"message\":\"" + message + "\"}";
        String     extra = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupId",id);
            jsonObject.put("userCode",shareUserCode);
            jsonObject.put("joinType",0);
            jsonObject.put("type",1);
            if(!TextUtils.isEmpty(message)) jsonObject.put("message", message);
            if(!TextUtils.isEmpty(freeSign)) jsonObject.put("freeSign", freeSign);
            extra = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getBaseLoadingView().showLoading();

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
                              if(getBaseLoadingView() == null)  return;
                              getBaseLoadingView().hideLoading();
                              if ("0".equals(isAudit)) {
                                  joinSuccess();

                              } else if ("1".equals(isAudit)) {
                                  ToastUtil.showShort(MyApplication.getInstance(), "申请已提交，等待管理员同意");
                                  CircleJoinActivity.this.finish();
                              }
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              if(getBaseLoadingView() == null)  return;
                              getBaseLoadingView().hideLoading();

                              EventBusUtil.post(new EventLoadBean(false));
                              ToastUtil.showShort(MyApplication.getInstance(),message);
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    //加入免费的圈子
    private void joinFree(String message) {
        getBaseLoadingView().showLoading();
        String token = UserManager.getInstance().getToken();
        Subscription sub =
                CircleApi.getInstance()
                         .joinFreeCirCle(token, id, message)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if(getBaseLoadingView() == null)   return;
                                getBaseLoadingView().hideLoading();
                                if (mDialog != null)    mDialog.dismiss();
                                if ("0".equals(isAudit)) {
                                    joinSuccess();
                                } else if ("1".equals(isAudit)) {
                                    ToastUtil.showShort(MyApplication.getInstance(), "申请已提交，等待管理员同意");
                                    CircleJoinActivity.this.finish();
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if(getBaseLoadingView() == null) return;
                                getBaseLoadingView().hideLoading();

                                if (mDialog != null)    mDialog.dismiss();
                                //加入上限的错误码
                                if("10268".equals(errorCode) || "10273".equals(errorCode)){
                                    showVerifyDialog();
                                }else {
                                    ToastUtil.showShort(MyApplication.getInstance(), message);
                                }
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    public void showVerifyDialog(){
        CircleVerifyDialog verifyDialog = new CircleVerifyDialog();
        verifyDialog.show(getSupportFragmentManager(), CircleVerifyDialog.class.getSimpleName());
    }

    //加入付费的圈子
    private void joinPay() {
        OrdersRequestBean requestBean = new OrdersRequestBean();

        BigDecimal moneyB    = new BigDecimal(money);
        double     total     = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
        String     token     = UserManager.getInstance().getToken();
        String     transType = "GJ";
        String     extra     = "{\"groupId\":\"" + id + "\"}";

        requestBean.setTotalPrice(total + "");
        requestBean.setToken(token);
        requestBean.setTransType(transType);
        requestBean.setExtra(extra);
        requestBean.setGoodsName(name);

        GotoUtil.goToActivity(this, PayActivity.class, 0, requestBean);
    }


    private void getInfo() {
        getBaseLoadingView().showLoading();
        String token = UserManager.getInstance().getToken();
        Subscription sub = CircleApi.getInstance().getInfo(token, id).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack<CircleBean>() {
                    @Override
                    public void onSuccess(CircleBean data) {
                        if(getBaseLoadingView() == null)   return;
                        getBaseLoadingView().hideLoading();

                        String url   = data.getJoinUrl();
                        String name  = data.getGroupName();
                        double money = data.getFee();
                        String isAud = data.getIsAudit();
                        setData(url, name, money, isAud);
                        setView();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(getBaseLoadingView() == null)   return;
                        getBaseLoadingView().hideLoading();

                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private  void getGroupInfo(){
        if(!UserManager.getInstance().isLogin(this)) return;

        getBaseLoadingView().showLoading();
        Subscription  sub = CircleApi.getInstance()
                                     .getCircleInfo(UserManager.getInstance().getToken(), id)
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .subscribeOn(Schedulers.io())
                                     .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                                         @Override
                                         public void onSuccess(Object data) {
                                             if(getBaseLoadingView() == null)   return;
                                             getBaseLoadingView().hideLoading();
                                             if(data != null){
                                                 infoBean = (CircleBean) data;
                                                 showShareDialog();
                                             }
                                         }

                                         @Override
                                         public void onError(String errorCode, String message) {
                                             if(getBaseLoadingView() == null)   return;
                                             getBaseLoadingView().hideLoading();
                                             ToastUtil.showShort(MyApplication.getInstance(), message);
                                         }
                                     }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void showShareDialog() {
        if (infoBean != null && "1".equals(infoBean.getIsShow())) {
            if (UserManager.getInstance().isLogin(this)) {
                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                UMShareUtils utils = new UMShareUtils(CircleJoinActivity.this);
                                utils.shareCircle(infoBean.getName(),infoBean.getContent(),infoBean.getCover(), infoBean.getBrokerage(), infoBean.getShareUrl());
                                utils.setOnItemClickListener(
                                        new UMShareUtils.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int flag) {
                                                if (0 == flag) {
                                                    IssueDynamicActivity.share(
                                                            CircleJoinActivity.this,
                                                            infoBean.getId() + "", "4", infoBean.getName(),
                                                            infoBean.getCover());
                                                }
                                                if (1 == flag) {
                                                    ConversationListActivity.startActivity(
                                                            CircleJoinActivity.this,
                                                            ConversationActivity.REQUEST_SHARE_CONTENT,
                                                            Constant.SELECT_TYPE_SHARE);
                                                }
                                                if (2 == flag) {
                                                    collect(UserManager.getInstance().getToken(),infoBean.getId()+"");
                                                }
                                                if (3 == flag) {
                                                    Intent intent = new Intent(CircleJoinActivity.this, ErWeiCodeActivity.class);
                                                    intent.putExtra("id", infoBean.getId());
                                                    intent.putExtra("type", 0);
                                                    intent.putExtra(Constant.INTENT_DATA, infoBean.getQrUrl());
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                            }

                            @Override
                            public void onPermissionDenied() {
                                mAlertDialog = DialogUtil.showDeportDialog(CircleJoinActivity.this,
                                        false, null, getString(R.string.pre_storage_notice_msg),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == R.id.tv_dialog_confirm) {
                                                    JumpPermissionManagement.GoToSetting(
                                                            CircleJoinActivity.this);
                                                }
                                                mAlertDialog.dismiss();
                                            }
                                        });
                            }
                        });
            }
        } else {
            ToastUtil.showShort(this, "圈子还未通过审核，无法分享！");
        }
    }

    private void collect(String token, String circleId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "8");
        map.put("bizId", circleId);
        Subscription subThumbsup = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        switch (infoBean.getIsCollect()){
                            case 0:
                                infoBean.setIsCollect(1);
                                ToastUtil.showShort(MyApplication.getInstance(),"收藏成功");
                                break;
                            case 1:
                                infoBean.setIsCollect(0);
                                ToastUtil.showShort(MyApplication.getInstance(),"取消收藏成功");
                                break;
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subThumbsup);
    }


    private void joinSuccess(){
        ToastUtil.showShort(MyApplication.getInstance(), "加入圈子成功");
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_DATA, id);
        setResult(Constant.ResponseCode.NORMAL_FLAG, intent);
        EventBusUtil.postStickyEvent(true);
        EventBusUtil.post(new EventCircleIntro(EventCircleIntro.ENTER));

        Intent goIntent = new Intent(this, CircleMainActivity.class);
        goIntent.putExtra("groupId", id);
        goIntent.putExtra("rule",true);

        startActivity(goIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PayConstant.PAY_RESULT) {
            joinSuccess();
        }

        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            shareMessage(targetId, type,bean.liuyan);
        }
    }

    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan) {
        String title = infoBean.getName();
        String img   = infoBean.getCover();
        String id    = infoBean.getId() + "";

        // infotype 使用 5 表示走佣金模式
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "4",
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(CircleJoinActivity.this, "分享成功");
                        if(!TextUtils.isEmpty(liuyan)){
                            RongIMTextUtil.INSTANCE.relayMessage
                                    (liuyan,targetId,type);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(MyApplication.getInstance(),
                                "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWebView != null){
            mWebView .setWebViewClient(null);
            mWebView.clearAllAction();

            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;

        }
        RxTaskHelper.getInstance().cancelTask(this);
    }

    //免费邀请成员加入  freeSign 免费邀请码
    public static void freeInvite(Context context,int id, String shareUserCode,String freeSign,int inviteType){
        Intent intent = new Intent(context,CircleJoinActivity.class);
        intent.putExtra("byLiveId",id);
        intent.putExtra("shareUserCode",shareUserCode);
        intent.putExtra("inviteType",inviteType);
        intent.putExtra("freeSign",freeSign);
        context.startActivity(intent);
    }


}
