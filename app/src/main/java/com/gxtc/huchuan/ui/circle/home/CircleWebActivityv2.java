package com.gxtc.huchuan.ui.circle.home;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.news.NewsCommentDialog;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.ADFilterTool;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.ProgressWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 圈子详情webview界面
 * 跟资讯类似所以我在上个页面直接把圈子bean的数据转成了newsbean的数据。。
 */

public class CircleWebActivityv2 extends BaseTitleActivity implements View.OnClickListener {
    private static final String TAG = "CircleWebActivityv2";
    @BindView(R.id.webView)           ProgressWebView webView;
    @BindView(R.id.iv_like)           ImageView       ivLike;
    @BindView(R.id.tv_write_comment)  TextView        tvWriteComment;
    @BindView(R.id.ll_webview_bottom) LinearLayout    llWebviewBottom;

    CircleHomeBean mBean;
    String         mUrl;

    private boolean isLike;//是否点赞

    private boolean isDatachange = false;
    private UMShareUtils shareUtils;
    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_web_view);
    }

    Intent mIntent;

    @Override
    public void initData() {
        mBean = new CircleHomeBean();
        mIntent = getIntent();
        mBean = (CircleHomeBean) mIntent.getSerializableExtra("data");

        initHeadView(getString(R.string.title_details));
        initWebView(mBean);
        isShowBottomCount(String.valueOf(mBean.getIsDZ()));
    }

    private void initHeadView(String title) {
        getBaseHeadView().showTitle(title);
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleWebActivityv2.this.finish();
            }
        });
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
    }

    @Override
    public void initListener() {

        tvWriteComment.setOnClickListener(this);
        ivLike.setOnClickListener(this);
    }


    /**
     * @param isThum
     */
    private void isShowBottomCount(String isThum) {

        //是否已经点赞,0未点1已点
        if ("0".equals(isThum)) {
            ivLike.setImageResource(R.drawable.news_icon_like_normal);
            isLike = false;
        } else if ("1".equals(isThum)) {
            ivLike.setImageResource(R.drawable.news_icon_like_selected);
            isLike = true;
        }


    }


    private void initWebView(CircleHomeBean bean) {
        //toke传空不要紧
        if (UserManager.getInstance().isLogin())
            mUrl = bean.getUrl() + UserManager.getInstance().getToken() + "&from=android";
        else mUrl = bean.getUrl() + "" + "&from=android";

//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(mUrl);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        webView.getSettings().setDomStorageEnabled(true);// 开启 DOM storage API 功能
        webView.getSettings().setDatabaseEnabled(true);//开启 database storage API 功能
        webView.getSettings().setAppCacheEnabled(true);//开启 Application Caches 功能
        webView.getSettings().setBlockNetworkImage(true);
        webView.addJavascriptInterface(this, "Interaction");
        webView.addJavascriptInterface(this, "CommentDetails");
        webView.addJavascriptInterface(this, "ShowImg");
        webView.addJavascriptInterface(this, "recommend");
        webView.addJavascriptInterface(this, "GoToHomePage");
        webView.addJavascriptInterface(this, "GoToSearch");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                getBaseLoadingView().hideLoading();
                webView.getSettings().setBlockNetworkImage(false);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == WebViewClient.ERROR_CONNECT) {
                    showNetError();
                }
            }

            /**
             * 广告拦截
             * @param view
             * @param url
             * @return
             */
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                url = url.toLowerCase();
                if (!url.contains(mUrl)) {
                    if (!ADFilterTool.hasAd(CircleWebActivityv2.this, url)) {
                        return super.shouldInterceptRequest(view, url);
                    } else {
                        return new WebResourceResponse(null, null, null);
                    }
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

    }

    NewsCommentDialog mCOmmentDialog;


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_write_comment://填写评论
                if (UserManager.getInstance().isLogin()) {//用户已经登录
                    mCOmmentDialog = new NewsCommentDialog(this);
                    mCOmmentDialog.setOnSendListener(new NewsCommentDialog.OnSendListener() {
                        @Override
                        public void sendComment(String content) {
                            //调用js代码
//                            webView.loadUrl("javascript:commentFun('" + token + "','" + content + "')");
                            comment(content);
                        }
                    });
                    mCOmmentDialog.show();
                } else {
                    gotoLogin();
                }

                break;
            case R.id.iv_like://是否点赞
                if (isLike == true) {//已经点赞用户肯定已经在登录状态了
                    thumbsup(UserManager.getInstance().getToken(), mBean.getId());
                } else {//没有点赞
                    if (UserManager.getInstance().isLogin()) {
                        thumbsup(UserManager.getInstance().getToken(), mBean.getId());
                    } else {
                        gotoLogin();
                    }
                }
                break;

            case R.id.headBackButton:
                if (webView.canGoBack()) {
                    webView.goBack();
                    llWebviewBottom.setVisibility(View.VISIBLE);
                    return;
                }
                CircleWebActivityv2.this.finish();
                break;
            case R.id.HeadRightImageButton:
                shareNews();
                break;
        }
    }

    /**
     * 回复动态
     *
     * @param content
     */
    private void comment(final String content) {
        if (UserManager.getInstance().isLogin()) {
            if (UserManager.getInstance().isLogin()) {
                Subscription sub = AllApi.getInstance().comment(UserManager.getInstance().getToken(),
                        mBean.getId(), content, "").subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<CircleCommentBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                CircleCommentBean bean = (CircleCommentBean) data;

                                if (bean != null && webView != null && mBean.getCommentVos() != null) {
                                    //调用js代码
                                    webView.loadUrl("javascript:commentFun('" + content + "','" + bean.getCreateTime() + "','" + bean.getUserPic() + "','" + bean.getUserName() + "','" + bean.getUserCode() + "','" + "" + "','" + "" + "','" + bean.getId() + "')");
                                    mBean.getCommentVos().add(bean);
                                    Intent intent = new Intent();
                                    intent.putExtra("newData", mBean);
                                    setResult(RESULT_OK, intent);
                                    mCOmmentDialog.dismiss();
                                }

                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if(mCOmmentDialog == null)  return;
                                LoginErrorCodeUtil.showHaveTokenError(CircleWebActivityv2.this, errorCode, message);
                                mCOmmentDialog.dismiss();
                            }
                        }));

                RxTaskHelper.getInstance().addTask(this,sub);
            }
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    /**
     * 回复评论
     */
    private void commentConnent(String userCode, final String content) {
        if (UserManager.getInstance().isLogin()) {
            Subscription sub = AllApi.getInstance().comment(UserManager.getInstance().getToken(),
                    mBean.getId(), content, userCode).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<CircleCommentBean>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            CircleCommentBean bean = (CircleCommentBean) data;
                            if (bean != null && webView != null) {
                                //调用js代码
                                webView.loadUrl(
                                        "javascript:commentFun('" + content + "','" + bean.getCreateTime() + "','" + bean.getUserPic() + "','" + bean.getUserName() + "','" + bean.getUserCode() + "','" + bean.getTargetUserName() + "','" + bean.getTargetUserCode() + "','" + bean.getId() + "')");
                                mBean.getCommentVos().add(bean);
                                Log.d(TAG, "onBackPressed: 修改数据了");
                                Intent intent = new Intent();
                                intent.putExtra("newData", mBean);
                                setResult(RESULT_OK, intent);

                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LoginErrorCodeUtil.showHaveTokenError(CircleWebActivityv2.this, errorCode, message);
                        }
                    }));

            RxTaskHelper.getInstance().addTask(this,sub);
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }


    private void shareNews() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(CircleWebActivityv2.this);
                        if (mBean.getPicList().size() > 0)
                            shareUtils.shareNews(mBean.getPicList().get(0).getPicUrl(),
                                    "这条动态很有意思，快来围观吧", mBean.getContent(), mBean.getUrl());
                        else shareUtils.shareNews(R.mipmap.person_icon_head_share,"这条动态很有意思，快来围观吧",
                                mBean.getContent(), mBean.getUrl());

                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(int flag) {
                                if (flag == 0) {//复制链接
                                    ClipboardManager cmb = (ClipboardManager) CircleWebActivityv2.this.getSystemService(
                                            Context.CLIPBOARD_SERVICE);
                                    cmb.setText(mBean.getUrl().trim());
                                    ToastUtil.showShort(CircleWebActivityv2.this, "复制成功");
                                } else if (flag == 2) {//投诉
                                    if (UserManager.getInstance().isLogin()) {
                                         ReportActivity.jumptoReportActivity(CircleWebActivityv2.this,String.valueOf(mBean.getId()),"5");
                                    } else {
                                        gotoLogin();
                                    }
                                } else if (flag == 3) { //刷新
                                    showReLoad();
                                }
                            }
                        });
//                new UMShareUtils(NewsWebViewActivity.this).shareSpecific(mBean.getCover(), mBean.getTitle()
//                        , mBean.getDigest(), mBean.getRedirectUrl(), "0");
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CircleWebActivityv2.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CircleWebActivityv2.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });


                    }
                });

    }


    /**
     * 点赞或取消点赞
     */

    private void thumbsup(String token, final int id) {
        final Intent intent = new Intent();
        getBaseLoadingView().showLoading(true);
        Subscription sub = AllApi.getInstance().support(token, id).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<ThumbsupVosBean>>(
                        new ApiCallBack<ThumbsupVosBean>() {
                            @Override
                            public void onSuccess(ThumbsupVosBean data) {
                                if(webView == null) return;

                                intent.putExtra("circle_id", id);
                                if (isLike == true) {
                                    isLike = false;
                                    ivLike.setImageResource(R.drawable.news_icon_like_normal);
                                    webView.loadUrl("javascript:supportFun('" + UserManager.getInstance().getToken() + "','" + false + "')");

                                    if(mBean != null && mBean.getThumbsupVos() != null){
                                        for (int i = 0; i < mBean.getThumbsupVos().size(); i++) {
                                            ThumbsupVosBean thumbsupVosBean = mBean.getThumbsupVos().get(i);
                                            if (thumbsupVosBean.getUserCode().equals(UserManager.getInstance().getUserCode())) {
                                                mBean.getThumbsupVos().remove(i);
                                                mBean.setIsDZ(0);
                                                Intent intent = new Intent();
                                                intent.putExtra("newData", mBean);
                                                setResult(RESULT_OK, intent);

                                                break;
                                            }
                                        }
                                    }

                                } else if (isLike == false) {
                                    isLike = true;
                                    ivLike.setImageResource(R.drawable.news_icon_like_selected);
                                    webView.loadUrl("javascript:supportFun('" + UserManager.getInstance().getToken() + "','" + true + "')");
                                    mBean.setIsDZ(1);
                                    mBean.getThumbsupVos().add(data);
                                    Intent intent = new Intent();
                                    intent.putExtra("newData", mBean);
                                    setResult(RESULT_OK, intent);

                                }
                                getBaseLoadingView().hideLoading();
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                LoginErrorCodeUtil.showHaveTokenError(CircleWebActivityv2.this, errorCode, message);
                                getBaseLoadingView().hideLoading();
                            }
                        }));
            RxTaskHelper.getInstance().addTask(this,sub);
    }


    private void gotoLogin() {

        Intent intent = new Intent(CircleWebActivityv2.this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);

    }


    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    public void showError(String info) {
        webView.setVisibility(View.INVISIBLE);
    }


    public void showReLoad() {
        webView.reload();
        webView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            llWebviewBottom.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }


    /**
     * web调用原生方法
     *
     * @param value
     */
    @JavascriptInterface
    public void recommendNews(String value) {
        try {
            //由于web下标字段跟我们格式不同，所以这里单独解析这个个字段
            JSONObject jsonObject = new JSONObject(value);
            int        subscript  = jsonObject.getInt("EleIndex");
            int        status     = jsonObject.getInt("status");
            if (1 == status) {
                JSONArray array = jsonObject.getJSONArray("result");
                TypeToken<List<NewsBean>> tt = new TypeToken<List<NewsBean>>() {
                };
                List<NewsBean> datas = new Gson().fromJson(array.toString(), tt.getType());
                NewsBean       bean  = datas.get(subscript);

                Intent intent = new Intent(this, NewsWebActivity.class);
                intent.putExtra("data", bean);
                startActivity(intent);
                this.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /*
     *  单独显示图片
     */
    @JavascriptInterface
    public void showBigImg(int position, String[] imageUrl) {
        String userCode = mBean == null ? "0" : mBean.getUserCode();
        ArrayList<Uri> uris = new ArrayList<>();
        for (String s : imageUrl) {
            uris.add(Uri.parse(s));
        }
        CommonPhotoViewActivity.startActivity(CircleWebActivityv2.this,uris,position);
    }

    /**
     * 点击文章的头像跳转到个人主页
     *
     * @param userCode
     */
    @JavascriptInterface
    public void goToHomePage(String userCode) {
        PersonalInfoActivity.startActivity(this,userCode);
//        PersonalHomePageActivity.startActivity(this, userCode);
    }

    private ListDialog mListDialog;


    /**
     * 回复动态评论或删除自己的评论
     *
     * @param groupInfoId 动态id
     * @param userCode    评论对象的userCode
     * @param position    web给的下标我们没用他们那边要删除的时候返回去
     * @param id          评论的id
     */
    @JavascriptInterface
    public void commentDetail(final Integer groupInfoId, final String userCode, final int position,
            final int id) {
        LogUtil.printD("groupInfoId" + groupInfoId);
        LogUtil.printD("userCode" + userCode);
        LogUtil.printD("position" + position);
        LogUtil.printD("id" + id);
        if (UserManager.getInstance().isLogin()) {
            if (!UserManager.getInstance().getUserCode().equals(userCode)) {//评论别人
                mCOmmentDialog = new NewsCommentDialog(this);
                mCOmmentDialog.setOnSendListener(new NewsCommentDialog.OnSendListener() {
                    @Override
                    public void sendComment(String content) {
                        //调用js代码
//                            webView.loadUrl("javascript:commentFun('" + token + "','" + content + "')");
                        commentConnent(userCode, content);
                        mCOmmentDialog.dismiss();

                    }
                });
                mCOmmentDialog.show();

            } else if (UserManager.getInstance().getUserCode().equals(userCode)) {//删除自己的评论
                mListDialog = new ListDialog(this, new String[]{"删除评论"});
                mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int posiition,
                            long mid) {

                        if (posiition == 0) {
                            deleteComment(position, id);
                        }
                    }
                });
            }
            mListDialog.show();
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }

    }

    /**
     * 删除评论
     */
    private void deleteComment(final int position, final int id) {
        Subscription sub = CircleApi.getInstance().deleteComment(id,
                UserManager.getInstance().getToken()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(webView == null) return;
                        webView.loadUrl("javascript:deleteComment('" + position + "')");
                        List<CircleCommentBean> commentVos = mBean.getCommentVos();
                        for (int i = 0; i < commentVos.size(); i++) {
                            CircleCommentBean circleCommentBean = commentVos.get(i);
                            if (circleCommentBean.getId() == id) {
                                commentVos.remove(i);
                                mBean.setLiuYan(mBean.getLiuYan() - 1);
                                Intent intent = new Intent();
                                intent.putExtra("newData", mBean);
                                setResult(RESULT_OK, intent);

                                break;
                            }
                        }

                        mListDialog.dismiss();

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(mListDialog == null) return;
                        LoginErrorCodeUtil.showHaveTokenError(CircleWebActivityv2.this, errorCode, message);
                        getBaseLoadingView().hideLoading();
                        mListDialog.dismiss();
                    }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    /**
     * 失败错误码
     *
     * @param errorCode
     */
    @JavascriptInterface
    public void interaction(String errorCode) {
        LoginErrorCodeUtil.webError(this, errorCode, 666);

    }


    /**
     * 关注用户 web处理
     */
    private void focusUser(final String userCode) {

//        if (UserManager.getInstance().isLogin()) {
//            sub = AllApi.getInstance().setUserFollow(UserManager.getInstance().getToken(), "3",
//                    userCode)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
//                        @Override
//                        public void onSuccess(Object data) {
//                            webView.loadUrl("javascript:isFocus('true')");
//                        }
//
//                        @Override
//                        public void onError(String errorCode, String message) {
//                            LoginErrorCodeUtil.showHaveTokenError(CircleWebActivity.this,
//                                    errorCode, message);
//                            webView.loadUrl("javascript:isFocus('false')");
//                        }
//                    }));
//        } else {
        Intent intent = new Intent(CircleWebActivityv2.this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, 666);

//        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.requestCode.NEWS_LIKEANDCOLLECT && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
//            thumbsup(UserManager.getInstance().getToken(), mBean.getId());
        }
        if (requestCode == 666 && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            showReLoad();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.putExtra("newData", mBean);
        setResult(RESULT_OK, intent);
        if (webView != null) {
            webView.removeJavascriptInterface("Interaction");
            webView.removeJavascriptInterface("CommentDetails");
            webView.removeJavascriptInterface("ShowImg");
            webView.removeJavascriptInterface("recommend");
            webView.removeJavascriptInterface("GoToHomePage");
            webView.removeJavascriptInterface("GoToSearch");
            webView.setWebViewClient(null);
            webView.clearAllAction();

            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
            webView = null;

        }
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
    }
}

