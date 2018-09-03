package com.gxtc.huchuan.ui.news;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.widget.MsgView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.NewsCommentBean;
import com.gxtc.huchuan.bean.event.EventCollectBean;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventThumbsupBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.handler.QrcodeHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.im.utilities.OptionsPopupDialog;
import com.gxtc.huchuan.qrcode.utils.QRCodeDecoder;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.LiveActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomePageActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.utils.ADFilterTool;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.DotViewBitmapUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CircleImageView;
import com.gxtc.huchuan.widget.ExpandVideoPlayer;
import com.gxtc.huchuan.widget.ProgressWebView;
import com.luck.picture.lib.ui.PictureEditActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 宋家任 on 2017/3/20.
 * 新闻详情WebView页面
 * 同js交互参看文档
 */

public class NewsWebActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.webView)
    ProgressWebView webView;
    @BindView(R.id.iv_comment)
    ImageView ivComment;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.iv_like)
    ImageView ivLike;
    @BindView(R.id.tv_write_comment)
    TextView tvWriteComment;
    @BindView(R.id.ll_webview_bottom)
    LinearLayout llWebviewBottom;
    @BindView(R.id.play_news_web_video_cover)
    ExpandVideoPlayer play;
    @BindView(R.id.iv_news_web_video_back)
    ImageView ivNewsWebVideoBack;
    @BindView(R.id.iv_news_web_video_share)
    ImageView ivNewsWebVideoShare;
    @BindView(R.id.coment_doc)
    MsgView commentDocs;
    @BindView(R.id.head_video)
    View headVideo;


    NewsBean mBean;
    String mUrl;
    String content;

    private boolean isCollect;  //是否收藏
    private boolean isLike;     //是否点赞
    private boolean isComment;  //是否是评论页
    private boolean isInit = false;

    private UMShareUtils shareUtils;


    Subscription subComment;
    Subscription subDeleteComment;
    Subscription subCommentContent;

    private String commentId;
    private AlertDialog mAlertDialog;
    private QrcodeHandler mQrcodeHandler;
    private MyHandler mMyHandler;

    public String targetUserCode;
    public String targetUserName;
    public String targetId;
    private OrdersRequestBean mRequestBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        mBean = new NewsBean();
        final Intent intent = getIntent();
        mBean = (NewsBean) intent.getSerializableExtra("data");

        if (mBean == null) return;

        //视频
        if (mBean.getIsVideo() != null && "1".equals(mBean.getIsVideo().trim())) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_web_view);//activity_news_web_view
    }

    @Override
    public void initData() {
        //视频
        if ("1".equals(mBean.getIsVideo())) {

            play.setVisibility(View.VISIBLE);
            play.setUp(mBean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, "", mBean.getCover());
            ImageHelper.loadImage(this, play.thumbImageView, mBean.getCover());

            ivNewsWebVideoBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsWebActivity.this.finish();
                }
            });
            ivNewsWebVideoShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareNews();
                }
            });
            setActionBarTopPadding(headVideo, false);
        } else {
            initHeadView(mBean.getSource());
        }
        initWebView(mBean);
        isShowBottomCount(mBean.getCommentCount(), mBean.getThumbsupCount(), mBean.getIsThumbsup(), mBean.getIsCollect());
        mQrcodeHandler = new QrcodeHandler(this);
        mMyHandler = new MyHandler(this);
    }

    private void initHeadView(String title) {
        getBaseHeadView().showBackButton(this);

        int edit = getIntent().getIntExtra("edit", 0);
        if (edit == 1) {
            if (!"1".equals(mBean.getAudit())) {
                getBaseHeadView().showHeadRightButton("编辑", this);
                llWebviewBottom.setVisibility(View.GONE);
            }
        } else {
            getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
        }

        View header = getLayoutInflater().inflate(R.layout.header_view_news, getBaseHeadView().getParentView(), false);
        CircleImageView leftImage = (CircleImageView) header.findViewById(R.id.left_image);
        TextView headtitle = (TextView) header.findViewById(R.id.title);
        headtitle.setText(title);
        ImageHelper.loadImage(this, leftImage, mBean.getSourceicon());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, R.id.headBackButton);
        params.addRule(RelativeLayout.LEFT_OF, R.id.headRightLinearLayout);
        getBaseHeadView().getParentView().addView(header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalInfoActivity.startActivity(NewsWebActivity.this, mBean.getUserCode());
            }
        });

    }


    /**
     * 是否显示点赞数评论数是否收藏
     *
     * @param commentCon 评论数
     * @param thum       点赞数
     * @param isThum     是否显示点赞数
     * @param isColle    是否显示评论数
     */
    private void isShowBottomCount(String commentCon, String thum, String isThum, String isColle) {
        //是否显示评论数
        if (commentCon != null && !TextUtils.isEmpty(commentCon)) {
            int commentCoutn = Integer.valueOf(commentCon);
            //大于0才显示评论数
            if (commentCoutn > 0) {
//                Bitmap bitmapComment = BitmapFactory.decodeResource(getResources(), R.drawable.news_icon_information);
//                ivComment.setImageBitmap(DotViewBitmapUtil.getDotNumViewBitmap(this, bitmapComment, mBean.getCommentCount()));
                showComentCount(commentCoutn);
            } else {
                commentDocs.setVisibility(View.INVISIBLE);
            }
        }

        //是否显示点赞数
        if (thum != null && !TextUtils.isEmpty(commentCon)) {
            int thumbsupCount = Integer.valueOf(thum);
            if (thumbsupCount > 0) {
                Bitmap bitmapThumbsup = BitmapFactory.decodeResource(getResources(),
                        R.drawable.news_icon_like_normal);
                ivLike.setImageBitmap(DotViewBitmapUtil.getDotNumViewBitmap(this, bitmapThumbsup,
                        mBean.getThumbsupCount()));
            }
        }

        //是否已经点赞,0未点1已点
        if ("0".equals(isThum)) {
            ivLike.setImageResource(R.drawable.news_icon_like_normal);
            isLike = false;
        } else if ("1".equals(isThum)) {
            ivLike.setImageResource(R.drawable.news_icon_like_selected);
            isLike = true;
        }

        //是否收藏
        if ("0".equals(isColle)) {
            ivCollect.setImageResource(R.drawable.news_icon_collect_normal);
            this.isCollect = false;
        } else if ("1".equals(isColle)) {
            ivCollect.setImageResource(R.drawable.news_icon_collect_selected);
            this.isCollect = true;
        }
    }

    private void showComentCount(int count) {
        commentDocs.setVisibility(View.VISIBLE);
        ivComment.setVisibility(View.VISIBLE);
        if (count <= 99) {
            commentDocs.setText(String.valueOf(count));
        } else {
            commentDocs.setText("99+");
        }
    }


    private void initWebView(NewsBean bean) {
        //toke传空不要紧
        if (UserManager.getInstance().isLogin())
            mUrl = bean.getRedirectUrl() + UserManager.getInstance().getToken() + "&from=android&userCode=" + UserManager.getInstance().getUserCode();
        else
            mUrl = bean.getRedirectUrl() + "" + "&from=android" + "";

        mUrl = mUrl.replace("http:", "https:");
        webView.loadUrl(mUrl);
        webView.addJavascriptInterface(new UMShareUtils(this), "ShareNews");
        webView.addJavascriptInterface(this, "Press");
        webView.addJavascriptInterface(this, "ShowImg");
        webView.addJavascriptInterface(this, "recommend");
        webView.addJavascriptInterface(this, "GoToHomePage");
        webView.addJavascriptInterface(this, "GoToSearch");
        webView.addJavascriptInterface(this, "PayWZ");
        webView.addJavascriptInterface(this, "Interaction");
        webView.addJavascriptInterface(this, "DeleteComment");
        webView.addJavascriptInterface(this, "GoBackNews");
        webView.addJavascriptInterface(this, "JoinTheCircle");
        webView.addJavascriptInterface(this, "dianZan");
        webView.addJavascriptInterface(this, "xmzj");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.getSettings().setBlockNetworkImage(false);
                /*if(isComment){
                    showCommentDialog();
                }*/
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == WebViewClient.ERROR_CONNECT) {
                    showNetError();
                }
            }

            /**
             * 广告拦截
             */
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                url = url.toLowerCase();
                if (!url.contains(mUrl)) {
                    if (!ADFilterTool.hasAd(NewsWebActivity.this, url)) {
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
                //打赏隐藏底部按钮
                if (url.contains("admire")) {
                    llWebviewBottom.setVisibility(View.GONE);
                }
                //评论详情页
                if (url.contains("reply")) {
                    isComment = true;
                    ivComment.setVisibility(View.GONE);
                    commentDocs.setVisibility(View.GONE);
                    commentId = url.substring(url.indexOf("=") + 1, url.indexOf("&"));

                } else {
                    isComment = false;
                    ivComment.setVisibility(View.VISIBLE);
                    //这里如果重新路由到其他web页面的话，会出现底部评论框的
                    if (!url.contains("admire") && isInit) {
                        if (url.contains("QRType")) {
                            if (UserManager.getInstance().isLogin()) {
                                if (mQrcodeHandler != null)
                                    mQrcodeHandler.resolvingCode(url, UserManager.getInstance().getUserCode());
                            } else {
                                GotoUtil.goToActivity(NewsWebActivity.this, LoginAndRegisteActivity.class);
                            }
                        } else {
                            CommonWebViewActivity.startActivity(NewsWebActivity.this, url, "");
                        }
                        return true;
                    }
                    isInit = true;
                }

                if (mQrcodeHandler != null && url.contains("QRType")) {
                    mQrcodeHandler.resolvingCode(url, "");
                    return true;
                }

                if (!url.contains("from=android")) {
                    if (url.endsWith("?")) {
                        url += "from=android";
                    } else {
                        url += "&from=android";
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    private DynamicCommentDialog mCommentDialog;

    @OnClick({R.id.tv_write_comment, R.id.iv_comment, R.id.iv_like, R.id.iv_collect})
    public void onClick(View view) {
        switch (view.getId()) {
            //填写评论
            case R.id.tv_write_comment:
                if (isComment) {
                    //评论页的输入框
                    webView.loadUrl("javascript:getMainCommentInfo()");
                } else {
                    //一级页面
                    showCommentDialog();
                }
                break;

            //查看评论
            case R.id.iv_comment:
                webView.loadUrl("javascript:goToCommment('true')");
                break;

            //是否点赞
            case R.id.iv_like:
                //已经点赞用户肯定已经在登录状态了
                if (isLike) {
                    thumbsup(UserManager.getInstance().getToken(), mBean.getId());

                    //没有点赞
                } else {
                    if (UserManager.getInstance().isLogin()) {
                        thumbsup(UserManager.getInstance().getToken(), mBean.getId());
                    } else {
                        gotoLogin();
                    }
                }
                break;

            //是否收藏
            case R.id.iv_collect:
                //已经点赞用户肯定已经在登录状态了
                if (isCollect) {
                    collect(UserManager.getInstance().getToken(), mBean.getId());
                } else {//没有点赞
                    if (UserManager.getInstance().isLogin()) {
                        collect(UserManager.getInstance().getToken(), mBean.getId());
                    } else {
                        gotoLogin();
                    }
                }
                break;

            case R.id.headBackButton:
                if (isComment) {
                    isComment = false;
                }
                if (webView.canGoBack()) {
                    llWebviewBottom.setVisibility(View.VISIBLE);
                    isShowBottomCount(mBean.getCommentCount(), mBean.getThumbsupCount(), mBean.getIsThumbsup(), mBean.getIsCollect());
                    webView.goBack();
                    return;
                }
                NewsWebActivity.this.finish();
                break;

            case R.id.HeadRightImageButton:
                shareNews();
                break;

            //编辑文章
            case R.id.headRightButton:
                Intent intent = new Intent(this, ArticleResolveActivity.class);
                intent.putExtra("bean", mBean);
                startActivityForResult(intent, ArticleResolveActivity.REQUEST_CODE_EDIT);
                break;
        }
    }

    private void showCommentDialog() {
        //用户已经登录
        if (UserManager.getInstance().isLogin()) {
            mCommentDialog = new DynamicCommentDialog();
            mCommentDialog.setSendListener(new DynamicCommentDialog.OnSendListener() {
                @Override
                public void sendComment(String content) {
                    //评论详情
                    if (isComment) {
                        commentConnent(content);
                    } else {
                        commentNews(content);
                        webView.loadUrl("javascript:goToCommment('true')");
                    }
                    if (mCommentDialog != null)
                        mCommentDialog.dismiss();
                }
            });
            if (!TextUtils.isEmpty(targetUserName)) {
                mCommentDialog.setText("回复 " + targetUserName + "：");
            }
            mCommentDialog.show(getSupportFragmentManager(), DynamicCommentDialog.class.getSimpleName());

        } else if (!isComment) {
            gotoLogin();
        }
    }

    /**
     * 评论新闻
     */
    private void commentNews(final String content) {
        if (UserManager.getInstance().isLogin()) {
            subComment = AllApi.getInstance().saveComment(UserManager.getInstance().getToken(),
                    mBean.getId(), content).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<NewsCommentBean>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            NewsCommentBean bean = (NewsCommentBean) data;
                            if (bean != null) {
                                //调用js代码

                                webView.loadUrl(
                                        "javascript:saveComment('" + content + "','" + "" + bean.getCreatetime() + "','" + "" + bean.getHeadPic() + "','" + "" + bean.getId() + "','" + "" + bean.getName() + "','" + "" + UserManager.getInstance().getUserCode() + "')");
//                                webView.loadUrl("javascript:saveComment('" + token + "','" + newsId + "','" + content + "')");
//                                webView.loadUrl("javascript:goToCommment('true')");
                               /* Bitmap bitmapComment = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.news_icon_information);*/
                                int count = Integer.valueOf(mBean.getCommentCount()) + 1;
                                mBean.setCommentCount(String.valueOf(count));
                                showComentCount(count);
                               /* ivComment.setImageBitmap(
                                        DotViewBitmapUtil.getDotNumViewBitmap(NewsWebActivity.this,
                                                bitmapComment, String.valueOf(count)));*/
                                //获取js的返回值
//                            webView.evaluateJavascript("saveComment('" + token + "','" + newsId + "','" + content + "')", new ValueCallback() {
//                                @Override
//                                public void onReceiveValue(Object value) {
//                                    ToastUtil.showShort(NewsWebViewActivity.this, "obj:" + value);
//                                }
//                            });
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LoginErrorCodeUtil.showHaveTokenError(NewsWebActivity.this, errorCode,
                                    message);
                        }
                    }));
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    /**
     * 评论回复
     */
    private void commentConnent(final String inputContent) {
        if (!TextUtils.isEmpty(targetUserName)) {
            content = "回复" + targetUserName + ":" + inputContent;
        } else {
            content = "回复顶楼" + ":" + inputContent;
        }
        if (UserManager.getInstance().isLogin()) {
            subCommentContent = AllApi.getInstance().saveCommentReply(
                    UserManager.getInstance().getToken(), commentId, content, targetUserCode, targetId).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<NewsCommentBean>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            NewsCommentBean bean = (NewsCommentBean) data;
                            if (bean != null) {
                                //调用js代码
                                webView.loadUrl("javascript:saveReply('" + content + "','" + bean.getCreatetime() + "','" + bean.getHeadPic() + "','" + bean.getName() + "','" + UserManager.getInstance().getUserCode() + "','" + bean.getId() + "')");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ToastUtil.showShort(MyApplication.getInstance(), message);
                        }
                    }));
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }


    private void shareNews() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {

                        if (!UserManager.getInstance().isLogin(NewsWebActivity.this) || mBean == null) return;
                        String content = TextUtils.isEmpty(mBean.getDigest()) ? mBean.getTitle() : mBean.getDigest();
                        shareUtils = new UMShareUtils(NewsWebActivity.this);
                        ShareDialog.Action[] actions = {
                                new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                                new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null),
                                new ShareDialog.Action(ShareDialog.ACTION_COMPLAINTS, R.drawable.share_icon_complaint, null),
                                new ShareDialog.Action(ShareDialog.ACTION_REFRESH, R.drawable.share_icon_refresh, null),
                                new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                                new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null)};

                        shareUtils.shareCustom(mBean.getCover(), mBean.getTitle(), content, mBean.getRedirectUrl(), actions, new ShareDialog.OnShareLisntener() {
                            @Override
                            public void onShare(@Nullable String key, @Nullable SHARE_MEDIA media) {
                                switch (key) {
                                    case ShareDialog.ACTION_COLLECT://收藏
                                        if (isCollect) {
                                            ToastUtil.showShort(NewsWebActivity.this, "已收藏");

                                            //没有点赞
                                        } else {
                                            collect(UserManager.getInstance().getToken(), mBean.getId());
                                        }
                                        break;
                                    case ShareDialog.ACTION_COMPLAINTS://投诉
                                        ReportActivity.jumptoReportActivity(NewsWebActivity.this, mBean.getId(), "0");
                                        break;
                                    case ShareDialog.ACTION_REFRESH://刷新
                                        showReLoad();
                                        break;
                                    case ShareDialog.ACTION_CIRCLE://分享到圈子动态
                                        if (mBean != null)
                                            IssueDynamicActivity.share(NewsWebActivity.this, mBean.getId(), "1", mBean.getTitle(), mBean.getCover());
                                        break;
                                    case ShareDialog.ACTION_FRIENDS://分享好友
                                        ConversationListActivity.startActivity(NewsWebActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE);
                                        break;
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(NewsWebActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(NewsWebActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });


                    }
                });
    }


    /**
     * 打赏
     *
     * @param price    价格，单位元
     * @param userCode 被打赏人userCode
     */
    @JavascriptInterface
    public void pay(double price, String userCode) {
        if (UserManager.getInstance().isLogin()) {
            BigDecimal moneyB = new BigDecimal(price);
            //计算总价
            double total = moneyB.setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            String id = mBean.getId();
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("contId", id);
            jsonObject.put("type", "wz");
            jsonObject.put("userCode", userCode);
            String extra = jsonObject.toJSONString();

            mRequestBean = new OrdersRequestBean();
            mRequestBean.setToken(UserManager.getInstance().getToken());
            mRequestBean.setTransType("DS");
            mRequestBean.setTotalPrice(total + "");
            mRequestBean.setExtra(extra);
            mRequestBean.setGoodsName("赞赏红包-" + mBean.getSource());


            GotoUtil.goToActivity(NewsWebActivity.this, PayActivity.class,
                    Constant.INTENT_PAY_RESULT, mRequestBean);
        } else
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

    }


    Subscription subThumbsup;

    /**
     * 点赞或取消点赞
     */

    private void thumbsup(String token, String newsId) {
        subThumbsup = AllApi.getInstance().getThumbsup(token, newsId).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (isLike) {
                            isLike = false;
                            ivLike.setImageResource(R.drawable.news_icon_like_normal);
                            webView.loadUrl("javascript:likeFun('false')");
                            EventBusUtil.post(new EventThumbsupBean(mBean.getId(), "0"));

                        } else {
                            isLike = true;
                            ivLike.setImageResource(R.drawable.news_icon_like_selected);
                            webView.loadUrl("javascript:likeFun('true')");
                            EventBusUtil.post(new EventThumbsupBean(mBean.getId(), "1"));
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(NewsWebActivity.this, errorCode,
                                message);
                    }
                }));

    }


    private void gotoLogin() {
        Intent intent = new Intent(NewsWebActivity.this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);

    }

    /**
     * 收藏或取消收藏
     */

    private void collect(String token, String newsId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "1");
        map.put("bizId", newsId);

        subThumbsup = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (isCollect) {
                            isCollect = false;
                            ivCollect.setImageResource(R.drawable.news_icon_collect_normal);
                            EventBusUtil.post(new EventCollectBean(mBean.getId(), "0"));
                        } else {
                            isCollect = true;
                            ivCollect.setImageResource(R.drawable.news_icon_collect_selected);
                            EventBusUtil.post(new EventCollectBean(mBean.getId(), "1"));
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(NewsWebActivity.this, errorCode, message);
                    }
                }));


    }

    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        String title = mBean.getTitle();
                        String img = mBean.getCover();
                        String id = mBean.getId();
                        ImMessageUtils.shareMessage(targetId, type, id, title, img, "1", new IRongCallback.ISendMessageCallback() {
                            @Override
                            public void onAttached(Message message) {
                            }

                            @Override
                            public void onSuccess(Message message) {
                                ToastUtil.showShort(NewsWebActivity.this, "分享成功");
                                if (!TextUtils.isEmpty(liuyan)) {
                                    RongIMTextUtil.INSTANCE.relayMessage(liuyan, targetId, type);
                                }
                            }

                            @Override
                            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                ToastUtil.showShort(NewsWebActivity.this, "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(NewsWebActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(NewsWebActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }


    private Bitmap downloadBitmap;
    private String qrcodeResult;

    /**
     * 下载网络图片
     *
     * @param url
     * @param action -1 代表啥都不干  0 保存图片 1编辑图片
     */
    private void downloadBitmap(String url, final int action) {
        Subscription sub =
                Observable.just(url)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<String, Bitmap>() {        //下载bitmap
                            @Override
                            public Bitmap call(String url) {
                                try {
                                    return Glide.with(NewsWebActivity.this)
                                            .asBitmap()
                                            .load(url)
                                            .submit(480, 800)
                                            .get();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        })
                        .map(new Func1<Bitmap, String>() {         //识别二维码
                            @Override
                            public String call(Bitmap bitmap) {
                                if (bitmap != null) {
                                    downloadBitmap = bitmap;
                                    return QRCodeDecoder.syncDecodeQRCode(bitmap);
                                }
                                return null;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Action1<String>() {         //这里判断是否是保存图片的操作
                            @Override
                            public void call(String s) {
                                //保存图片
                                if (action == 0 && downloadBitmap != null) {
                                    saveImageToLocal(downloadBitmap, 0);
                                }

                                //编辑图片
                                if (action == 1 && downloadBitmap != null) {
                                    saveImageToLocal(downloadBitmap, 1);
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(final String qrUrl) {
                                if (!TextUtils.isEmpty(qrUrl)) {
                                    qrcodeResult = qrUrl;
                                    android.os.Message msg = new android.os.Message();
                                    msg.what = 1;
                                    msg.obj = qrUrl;
                                    msg.arg1 = action;
                                    mMyHandler.sendMessage(msg);
                                }
                            }
                        });

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    //保存图片到本地
    private void saveImageToLocal(Bitmap bitmap, final int action) {
        if (bitmap != null) {
            Observable.just(bitmap)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<Bitmap, String>() {
                        @Override
                        public String call(Bitmap bitmap) {
                            String path = FileStorage.getImgCacheFile() + "/" + FileStorage.getImageTempName();
                            try {
                                ImageUtils.saveImageToSD(MyApplication.getInstance(), path, bitmap, 100);
                                return path;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return "";
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String path) {
                            if (!TextUtils.isEmpty(path)) {
                                //保存图片
                                if (action == 0) {
                                    ToastUtil.showLong(MyApplication.getInstance(), "图片已保存至：" + path);
                                } else {
                                    PictureEditActivity.startActivit(NewsWebActivity.this, path);
                                }

                            } else {
                                ToastUtil.showShort(MyApplication.getInstance(), "图片保存失败");
                            }
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.requestCode.NEWS_LIKEANDCOLLECT && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
        }

        if (resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            ToastUtil.showShort(this, "分享成功");
        }

        if (requestCode == ArticleResolveActivity.REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }

        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            String targetId = bean.targetId;
            Conversation.ConversationType type = bean.mType;
            shareMessage(targetId, type, bean.liuyan);
        }

        //分享视频
        if (requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK) {
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
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
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (isComment) {
            isComment = false;
            ivComment.setVisibility(View.VISIBLE);
        }
        if (JZVideoPlayer.backPress()) {
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
            llWebviewBottom.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 加关注失败错误码
     */
    @JavascriptInterface
    public void interaction(String errorCode) {
        LoginErrorCodeUtil.webError(this, errorCode, 666);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subThumbsup != null && subThumbsup.isUnsubscribed()) {
            subThumbsup.unsubscribe();
        }
        if (subComment != null && subComment.isUnsubscribed()) {
            subComment.unsubscribe();
        }
        if (subCommentContent != null && subCommentContent.isUnsubscribed()) {
            subCommentContent.unsubscribe();
        }
        if (webView != null) {
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.clearAllAction();
            removeJsInterface();
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
            webView = null;

        }
        mAlertDialog = null;
        EventBusUtil.unregister(this);
    }

    public void removeJsInterface() {
        webView.removeJavascriptInterface("ShareNews");
        webView.removeJavascriptInterface("ShowImg");
        webView.removeJavascriptInterface("recommend");
        webView.removeJavascriptInterface("GoToHomePage");
        webView.removeJavascriptInterface("GoToSearch");
        webView.removeJavascriptInterface("PayWZ");
        webView.removeJavascriptInterface("Interaction");
        webView.removeJavascriptInterface("DeleteComment");
        webView.removeJavascriptInterface("GoBackNews");
        webView.removeJavascriptInterface("JoinTheCircle");
        webView.removeJavascriptInterface("xmzj");
    }

    /**
     * web调用原生方法
     */
    @JavascriptInterface
    public void recommendNews(String value) {
        try {
            //由于web下标字段跟我们格式不同，所以这里单独解析这个个字段
            JSONObject jsonObject = new JSONObject(value);
            int subscript = jsonObject.getInt("EleIndex");
            int status = jsonObject.getInt("status");
            if (1 == status) {
                JSONArray array = jsonObject.getJSONArray("result");
                TypeToken<List<NewsBean>> tt = new TypeToken<List<NewsBean>>() {
                };
                List<NewsBean> datas = new Gson().fromJson(array.toString(), tt.getType());
                NewsBean bean = datas.get(subscript);

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
     * 点击图片广告
     */
    @JavascriptInterface
    public void goUrl(String url) {
        mQrcodeHandler.resolvingCode(url, "");
    }

    private OptionsPopupDialog mOptionsPopupDialog;

    /*
     *  图片长按
     */
    @JavascriptInterface
    public void longPress(final String imageUrl) {
        android.os.Message msg = new android.os.Message();
        msg.what = 0;
        msg.obj = imageUrl;
        mMyHandler.sendMessage(msg);
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
        CommonPhotoViewActivity.startActivity(this, uris, position);
    }

    /**
     * 点击文章的头像跳转到个人主页
     */
    @JavascriptInterface
    public void goToHomePage(String userCode, String isFollow) {
        Log.d("NewsWebActivity", "goToHomePage: ");
        PersonalInfoActivity.startActivity(this, userCode);
    }


    /**
     * 删除评论
     *
     * @param type 0是资讯详情页面的主评论，1是评论回复页面的主评论，2是二级评论
     */
    @JavascriptInterface
    public void deleteComment(final int type, final int id, final int index) {
        int delType;

        if (0 == type || 1 == type) {
            delType = 1;
        } else {
            delType = 2;
        }
        subDeleteComment = AllApi.getInstance().delComment(UserManager.getInstance().getToken(),
                delType, id).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (type == 0) {

                            webView.loadUrl("javascript:deleteWhichComment('" + String.valueOf(index) + "')");

                            mBean.setCommentCount(
                                    String.valueOf(Integer.valueOf(mBean.getCommentCount()) - 1));

                            int commentCoutn = Integer.valueOf(mBean.getCommentCount());
                            Bitmap bitmapComment = BitmapFactory.decodeResource(getResources(),
                                    R.drawable.news_icon_information);
                            if (commentCoutn > 0) {//大于0才显示评论数
                                showComentCount(commentCoutn);
                               /* ivComment.setImageBitmap(
                                        DotViewBitmapUtil.getDotNumViewBitmap(NewsWebActivity.this,
                                                bitmapComment, mBean.getCommentCount()));*/
                            } else {
                                commentDocs.setVisibility(View.INVISIBLE);
                                /*ivComment.setImageBitmap(bitmapComment);*/
                            }
                        } else if (type == 1) {
                            webView.loadUrl(
                                    "javascript:replyToNewsDelete('" + String.valueOf(id) + "')");
                            webView.loadUrl("javascript:wantDeleteFirstComment()");

                            mBean.setCommentCount(
                                    String.valueOf(Integer.valueOf(mBean.getCommentCount()) - 1));

                            int commentCoutn = Integer.valueOf(mBean.getCommentCount());
                            Bitmap bitmapComment = BitmapFactory.decodeResource(getResources(),
                                    R.drawable.news_icon_information);
                            if (commentCoutn > 0) {//大于0才显示评论数
                                showComentCount(commentCoutn);
                                /*ivComment.setImageBitmap(
                                        DotViewBitmapUtil.getDotNumViewBitmap(NewsWebActivity.this,
                                                bitmapComment, mBean.getCommentCount()));*/
                            } else {
                                commentDocs.setVisibility(View.INVISIBLE);
                                /* ivComment.setImageBitmap(bitmapComment);*/
                            }


                        } else if (type == 2) {
                            webView.loadUrl(
                                    "javascript:deleteWhichSecondComment('" + String.valueOf(
                                            index) + "')");
                        }

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(NewsWebActivity.this, errorCode,
                                message);
                    }
                }));
    }


    /**
     * 从评论页返回上级新闻页
     */
    @JavascriptInterface
    public void goBackNews() {
        llWebviewBottom.setVisibility(View.VISIBLE);
    }

    /**
     * 点赞回调
     */
    @JavascriptInterface
    public void status(String touchFlag) {
        if ("0".equals(touchFlag)) {
            ivLike.setImageResource(R.drawable.news_icon_like_normal);
            isLike = false;
        } else if ("1".equals(touchFlag)) {
            ivLike.setImageResource(R.drawable.news_icon_like_selected);
            isLike = true;
        }
    }

    /**
     * 点击底部输入框的回调（回复页面）
     *
     * @param commentsId
     * @param targetUserCode
     * @param targetUserName
     */
    @JavascriptInterface
    public void mainCommentInfo(String commentsId, String targetUserCode, String targetUserName) {
        this.targetUserCode = targetUserCode;
        this.targetUserName = targetUserName;
        this.targetId = commentsId;
        showCommentDialog();
    }

    /**
     * 点击顶部评论回复（回复页面）
     *
     * @param commentsId
     * @param targetUserCode
     * @param targetUserName
     */
    @JavascriptInterface
    public void repyMainComment(String commentsId, String targetUserCode, String targetUserName) {
        this.targetUserCode = targetUserCode;
        this.targetUserName = targetUserName;
        this.targetId = commentsId;
        showCommentDialog();//点击头部的评论进行回复跟跟点击底部的输入框效果是一样的
    }

    /**
     * 点击回复列表里的某一条评论回调（回复页面）
     *
     * @param replayId
     * @param targetUserCode
     * @param targetUserName
     */
    @JavascriptInterface
    public void repyComment(String replayId, String targetUserCode, String targetUserName) {
        this.targetId = replayId;
        this.targetUserCode = targetUserCode;
        this.targetUserName = targetUserName;
        showCommentDialog();
    }

    /**
     * -
     * 跳转圈子
     *
     * @param groupId      圈子id
     * @param groupIsFree  是否收费
     * @param groupFee     费用
     * @param groupIsAudit 是否审核
     * @param groupUrl     跳转链接
     * @param joinGroup    是否加入圈子
     * @param groupName    是否加入圈子
     */
    @JavascriptInterface
    public void joinTheCircle(String groupId, String groupIsFree, String groupFee,
                              String groupIsAudit, String groupUrl, String joinGroup, String groupName) {
        if ("0".equals(joinGroup)) {
            double money = Double.valueOf(groupFee);
            int id = Integer.valueOf(groupId);

            Intent intent = new Intent(NewsWebActivity.this, CircleJoinActivity.class);
            intent.putExtra("url", groupUrl);
            intent.putExtra("id", id);
            intent.putExtra("name", groupName);
            intent.putExtra("isAudit", groupIsAudit);
            intent.putExtra(Constant.INTENT_DATA, money);
            startActivityForResult(intent, 0);

        } else {
            CircleBean bean = new CircleBean();
            bean.setId(Integer.valueOf(groupId));
            GotoUtil.goToActivity(NewsWebActivity.this, CircleMainActivity.class, 0, bean);
        }

    }


    /**
     * type，0：圈子，1：课程，2：系列课，3：交易  4: 群聊邀请  5: 文章  6: 商品  7: 交易详情  8: 个人主页
     */
    @JavascriptInterface
    public void onJumpNative(String type, String id) {
        Log.d("NewsWebActivity", "onJumpNative: " + "????????????????????????");
        switch (type) {
            case "0":
                if (!TextUtils.isEmpty(id)) {
                    CircleMainActivity.startActivity(this, StringUtil.toInt(id));
                }
                break;

            case "1":
                if (TextUtils.isEmpty(id)) {
                    GotoUtil.goToActivity(this, LiveActivity.class);
                } else {
                    LiveIntroActivity.startActivity(this, id);
                }
                break;

            case "2":
                if (!TextUtils.isEmpty(id)) {
                    SeriesActivity.startActivity(this, id);
                }
                break;

            case "5":
                if (!TextUtils.isEmpty(id)) {
                    CircleShareHandler handler = new CircleShareHandler(this);
                    handler.getNewsData(id);
                }
                break;

            case "6":
                if (!TextUtils.isEmpty(id)) {
                    MallDetailedActivity.startActivity(this, id);
                }
                break;

            case "7":
                if (!TextUtils.isEmpty(id)) {
                    GoodsDetailedActivity.startActivity(this, id);
                }
                break;

            case "8":
                if (!TextUtils.isEmpty(id)) {
                    PersonalHomePageActivity.startActivity(this, id);
                }
                break;
        }
    }


    /**
     * 点击关键字跳转到搜索页
     */
    @JavascriptInterface
    public void goToSearch(String keyWord) {
        NewSearchActivity.startActivity(this, keyWord);
    }


    @Subscribe
    public void onEvent(EventFocusBean bean) {
        showReLoad();
    }

    public OptionsPopupDialog getOptionsPopupDialog() {
        return mOptionsPopupDialog;
    }

    public QrcodeHandler getQrcodeHandler() {
        return mQrcodeHandler;
    }

    private static class MyHandler extends Handler {
        WeakReference<NewsWebActivity> mWeakReference;

        public MyHandler(NewsWebActivity activity) {
            mWeakReference = new WeakReference<NewsWebActivity>(activity);
        }


        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            final NewsWebActivity activity = mWeakReference.get();
            if (activity != null) {
                if (msg.what == 0) {
                    final String imageUrl = (String) msg.obj;
                    String[] items = new String[]{"保存图片", "编辑图片"};
                    activity.mOptionsPopupDialog = OptionsPopupDialog.newInstance(activity, items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                        public void onOptionsItemClicked(int which) {
                            //保存图片
                            if (which == 0) {
                                if (activity.downloadBitmap == null) {
                                    activity.downloadBitmap(imageUrl, 0);
                                } else {
                                    activity.saveImageToLocal(activity.downloadBitmap, 0);
                                }
                            }

                            //编辑图片
                            if (which == 1) {
                                if (activity.downloadBitmap == null) {
                                    activity.downloadBitmap(imageUrl, 1);
                                } else {
                                    activity.saveImageToLocal(activity.downloadBitmap, 1);
                                }
                            }

                            // 识别二维码
                            if (which == 2) {
                                if (activity.downloadBitmap == null) {
                                    activity.downloadBitmap(imageUrl, 2);
                                } else {
                                    activity.mQrcodeHandler.resolvingCode(activity.qrcodeResult, "");
                                }
                            }

                        }
                    });
                    activity.mOptionsPopupDialog.show();
                    activity.downloadBitmap(imageUrl, -1);
                }

                if (msg.what == 1) {
                    String result = (String) msg.obj;
                    //Dialog在show的时候必须要有一个activity作为窗口载体 这里做个activity是否销毁的判断
                    if (!activity.isFinishing()) {
                        OptionsPopupDialog dialog = activity.getOptionsPopupDialog();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.addItem();
                        }
                    }
                    // 识别二维码
                    if (msg.arg1 == 2) {
                        activity.mQrcodeHandler.resolvingCode(result, "");
                    }
                }
            }
        }

    }

}
