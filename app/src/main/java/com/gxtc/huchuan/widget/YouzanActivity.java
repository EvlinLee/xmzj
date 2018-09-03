///*
// * author: lachang@youzan.com
// * Copyright (C) 2016 Youzan, Inc. All Rights Reserved.
// */
//package com.gxtc.huchuan.widget;
//
//import android.Manifest;
//import android.content.ActivityNotFoundException;
//import android.content.ClipboardUtil;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.view.Gravity;
//import android.view.View;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import com.gxtc.commlibrary.base.BaseTitleActivity;
//import com.gxtc.commlibrary.utils.GotoUtil;
//import com.gxtc.commlibrary.utils.LogUtil;
//import com.gxtc.commlibrary.utils.SpUtil;
//import com.gxtc.commlibrary.utils.ToastUtil;
//import com.gxtc.huchuan.Constant;
//import com.gxtc.huchuan.R;
//import com.gxtc.huchuan.bean.YouZanBean;
//import com.gxtc.huchuan.data.UserManager;
//import com.gxtc.huchuan.dialog.ComplaintDialog;
//import com.gxtc.huchuan.http.service.YouZanApi;
//import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
//import com.gxtc.huchuan.utils.DialogUtil;
//import com.gxtc.huchuan.utils.JumpPermissionManagement;
//import com.gxtc.huchuan.utils.UMShareUtils;
//import com.youzan.androidsdk.YouzanToken;
//import com.youzan.androidsdk.basic.YouzanBrowser;
//import com.youzan.androidsdk.event.AbsAuthEvent;
//import com.youzan.androidsdk.event.AbsChooserEvent;
//import com.youzan.androidsdk.event.AbsStateEvent;
//import com.youzan.androidsdk.model.goods.GoodsShareModel;
//
//import butterknife.BindView;
//import rx.Observer;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * zzg
// */
//public class YouzanActivity extends BaseTitleActivity implements View.OnClickListener{
//    public  static final String TAG = "YouzanActivity";
//    public  static final String CLENT_ID = "45d03972c8a1dbd682";
//    public  static final String ClENT_SECRET = "b31123dbb20346ca422c014d742e1c1f";
//    private static final int CODE_REQUEST_LOGIN = 0x101;
//    @BindView(R.id.YouzanBrowser)
//    YouzanBrowser webView;
//    private YouzanToken mYouzanToken;
//    private AlertDialog mAlertDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_youzan_layout);
//         mYouzanToken = new YouzanToken();
//        initToken();
//        initHeadView("新媒工具平台");
//        webView.getSettings().setDomStorageEnabled(true);
//        //替换成需要展示入口的链接
//        webView.loadUrl("https://h5.youzan.com/v2/home/yt7kt1zk?reft=1491479386972&spm=g328715193&kdtfrom=wsc&form=wsc");
//        setupYouzanView(webView);
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return false;
//            }
//        });
//
//    }
//    private void initHeadView(String title) {
//        getBaseHeadView().showTitle(title);
//        getBaseHeadView().showBackButton(this);
//        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        webView.onResume();
//        if(UserManager.getInstance().isLogin()){
//            if(SpUtil.getFirstAccessYouZan(getApplicationContext(),TAG,false)){
//                ShowLogin();
//            }
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        webView.onPause();
//    }
//
//    private void setupYouzanView (YouzanBrowser client) {
//        //认证事件, 回调表示: 需要需要新的认证信息传入
//        client.subscribe(new AbsAuthEvent() {
//
//            @Override
//            public void call(Context context, boolean needLogin) {
//                /**
//                 * 建议实现逻辑:
//                 *
//                 *     判断App内的用户是否登录?
//                 *       => 已登录: 请求带用户角色的认证信息(login接口);
//                 *       => 未登录: needLogin为true, 唤起App内登录界面, 请求带用户角色的认证信息(login接口);
//                 *       => 未登录: needLogin为false, 请求不带用户角色的认证信息(initToken接口).
//                 *
//                 *      服务端接入文档: https://www.youzanyun.com/docs/guide/appsdk/683
//                 */
//                if(needLogin){
//                    if(UserManager.getInstance().isLogin()){
//                        ShowLogin();
//                    }else {
//                        SpUtil.putFirstAccessYouZan(YouzanActivity.this, TAG,true);
//                        GotoUtil.goToActivity(YouzanActivity.this, LoginAndRegisteActivity.class);
//                    }
//                }
//            }
//        });
//
//        //文件选择事件, 回调表示: 发起文件选择. (如果app内使用的是系统默认的文件选择器, 该事件可以直接删除)
//        client.subscribe(new AbsChooserEvent() {
//            @Override
//            public void call(Context context, Intent intent, int requestCode) throws ActivityNotFoundException {
//                startActivityForResult(intent, requestCode);
//            }
//        });
//
//        //页面状态事件, 回调表示: 页面加载完成
//        client.subscribe(new AbsStateEvent() {
//            @Override
//            public void call(Context context) {
//
//                //停止刷新
////                mRefreshLayout.setRefreshing(false);
////                mRefreshLayout.setEnabled(true);
//            }
//        });
//
//        //分享事件, 回调表示: 获取到当前页面的分享信息数据
//        client.subscribe(new com.youzan.androidsdk.event.AbsShareEvent() {
//            @Override
//            public void call(Context context, GoodsShareModel data) {
//                /**
//                 * 在获取数据后, 可以使用其他分享SDK来提高分享体验.
//                 * 这里调用系统分享来简单演示分享的过程.*/
//                shareNews(data);
//            }
//        });
//
//
//
//    }
//
//    private  void initToken() {
//           YouZanApi.getInstance().initToken(CLENT_ID,ClENT_SECRET)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<YouZanBean>() {
//                    @Override
//                    public void onCompleted() {}
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.d(TAG,"---有赞onError--="+e);
//                    }
//
//                    @Override
//                    public void onNext(YouZanBean youzanToken) {
//                        mYouzanToken.setAccessToken(youzanToken.getData().getAccess_token());
//
//                    }
//                });
//    }
//
//    private void ShowLogin() {
//        String userId = String.valueOf(UserManager.getInstance().getUser().getId());
//        YouZanApi.getInstance().login(CLENT_ID,ClENT_SECRET,userId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<YouZanBean>() {
//            @Override
//            public void onCompleted() {
//                SpUtil.putFirstAccessYouZan(getApplicationContext(), TAG,false);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                LogUtil.d(TAG,"---有赞dlonError--="+e);
//            }
//
//            @Override
//            public void onNext(YouZanBean youZanBean) {
//                mYouzanToken.setAccessToken(youZanBean.getData().getAccess_token());
//                mYouzanToken.setCookieKey(youZanBean.getData().getCookie_key());
//                mYouzanToken.setCookieValue(youZanBean.getData().getCookie_value());
//                webView.sync(mYouzanToken);
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            /**
//             * 用户登录成功返回, 从自己的服务器上请求同步认证后组装成{@link com.youzan.sdk.YouzanToken},
//             * 调用{code view.sync(token);}同步信息.
//             */
//            if (CODE_REQUEST_LOGIN == requestCode) {
//                webView.sync(mYouzanToken);
//            }
//            else if (requestCode == Constant.requestCode.NEWS_LIKEANDCOLLECT && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
////            thumbsup(UserManager.getInstance().getToken(), mBean.getId());
//            }
//            else if (requestCode == 666 && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
//                showReLoad();
//            }
//            else {
//                //处理文件上传
//                webView.receiveFile(requestCode, data);
//            }
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (!webView.pageGoBack()) {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        webView.destroy();
//        webView = null;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.headBackButton:
//                onBackPressed();
//                break;
//            case R.id.HeadRightImageButton:
//                webView.sharePage();
//                break;
//
//        }
//    }
//    private UMShareUtils shareUtils;
//    ComplaintDialog complaintDialog;
//    private void shareNews(final GoodsShareModel mBean ) {
//    String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//    performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
//            new PermissionsResultListener() {
//                @Override
//                public void onPermissionGranted() {
//                    shareUtils = new UMShareUtils(YouzanActivity.this);
//                    if (!mBean.getImgUrl().isEmpty())
//                        shareUtils.shareNews(mBean.getImgUrl(),
//                                "这条动态很有意思，快来围观吧", mBean.getDesc(), mBean.getLink());
//                    else shareUtils.shareNews(R.mipmap.person_icon_head_share,"这条动态很有意思，快来围观吧",
//                            mBean.getDesc(), mBean.getLink());
//
//                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(int flag) {
//                            if (flag == 0) {//复制链接
//                                ClipboardUtil cmb = (ClipboardUtil) YouzanActivity.this.getSystemService(
//                                        Context.CLIPBOARD_SERVICE);
//                                cmb.setText(mBean.getLink().trim());
//                                ToastUtil.showShort(YouzanActivity.this, "复制成功");
//                            } else if (flag == 2) {//投诉
//                                if (UserManager.getInstance().isLogin()) {
//
//                                    complaintDialog = new ComplaintDialog(
//                                            YouzanActivity.this, YouzanActivity.this,
//                                            R.style.BottomDialog, String.valueOf(mBean.getTitle()),
//                                            "举报动态问题", "5");
//                                    complaintDialog.getWindow().setGravity(Gravity.BOTTOM);
//                                    complaintDialog.show();
//
//                                } else {
//                                    gotoLogin();
//                                }
//                            } else if (flag == 3) { //刷新
//                                showReLoad();
//                            }
//                        }
//                    });
////                new UMShareUtils(NewsWebViewActivity.this).shareSpecific(mBean.getCover(), mBean.getTitle()
////                        , mBean.getDigest(), mBean.getRedirectUrl(), "0");
//                }
//
//                @Override
//                public void onPermissionDenied() {
//                    mAlertDialog = DialogUtil.showDeportDialog(YouzanActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if(v.getId() == R.id.tv_dialog_confirm){
//                                        JumpPermissionManagement.GoToSetting(YouzanActivity.this);
//                                    }
//                                    mAlertDialog.dismiss();
//                                }
//                            });
//
//                }
//            });
//
//}
//    public void showReLoad() {
//        webView.reload();
//        webView.setVisibility(View.VISIBLE);
//    }
//    private void gotoLogin() {
//
//        Intent intent = new Intent(YouzanActivity.this, LoginAndRegisteActivity.class);
//        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);
//
//    }
//
//}
