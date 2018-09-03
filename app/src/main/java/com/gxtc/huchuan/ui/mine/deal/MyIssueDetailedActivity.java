package com.gxtc.huchuan.ui.mine.deal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.GoodsDetailedAdapter;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventCommentBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity;
import com.gxtc.huchuan.ui.mine.deal.issueDeal.IssueDealActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.ExpandVideoPlayer;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;

import java.text.DecimalFormat;
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

/**
 * 查看发布交易详情
 */
public class MyIssueDetailedActivity extends BaseTitleActivity implements MyIssueDetailedContract.View,
        View.OnClickListener {

    @BindView(R.id.btn_modify)
    View btnModify;
    @BindView(R.id.btn_end)
    View btnEnd;
    @BindView(R.id.rl_goods)
    RecyclerView listView;
    @BindView(R.id.swipe_goods)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tv_finish_iss)
    TextView tvFinish;

    private ImageView imgIcon;
    private TextView tvRead;
    private TextView tvCommentCount;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvTitle;
    private TextView tvMoney;
    private LinearLayout layoutParame;      //商品参数布局
    private LinearLayout layoutNoComment;   //没有留言时显示的布局
    private TextView tvMoneyLabel;
    private WebView mWebView;
    private ExpandVideoPlayer video;

    //private LinearLayout        layoutContent;  //商品图文描述布局

    private View headview;

    private int id;

    private MyIssueDetailedContract.Presenter mPresenter;
    private DealSource mData;
    private GoodsDetailedBean bean;

    private GoodsDetailedAdapter adapter;
    private int isFinish;
    private TextView tvNumber;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_issue_detailed);
        EventBusUtil.register(this);
    }


    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_goods_detailed));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);

        refreshLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        headview = View.inflate(this, R.layout.head_goods_detailed, null);

        imgIcon = (ImageView) headview.findViewById(R.id.img_head);
        tvRead = (TextView) headview.findViewById(R.id.tv_read);
        tvCommentCount = (TextView) headview.findViewById(R.id.tv_comment);
        tvName = (TextView) headview.findViewById(R.id.tv_name);
        tvTime = (TextView) headview.findViewById(R.id.tv_time);
        tvTitle = (TextView) headview.findViewById(R.id.tv_title);
        tvMoney = (TextView) headview.findViewById(R.id.tv_money);
        tvNumber = (TextView) headview.findViewById(R.id.tv_number);
        tvMoneyLabel = (TextView) headview.findViewById(R.id.tv_money_label);
        layoutParame = (LinearLayout) headview.findViewById(R.id.layout_parame);
        mWebView = (WebView) headview.findViewById(R.id.webView);
        video = (ExpandVideoPlayer) headview.findViewById(R.id.ep_goods_video);
        //layoutContent = (LinearLayout) headview.findViewById(R.id.layout_content);
        headview.findViewById(R.id.tv_goods_protect).setOnClickListener(this);
        headview.findViewById(R.id.coustomer).setOnClickListener(this);
        layoutNoComment = (LinearLayout) headview.findViewById(R.id.layout_no_comment);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient(this));
        mWebView.addJavascriptInterface(this, "ShowImg");

        adapter = new GoodsDetailedAdapter(this, new ArrayList<GoodsCommentBean>(), R.layout.item_goods_comment);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        listView.addHeadView(headview);
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        id = getIntent().getIntExtra(Constant.INTENT_DATA, -1);
        isFinish = getIntent().getIntExtra("isFinish", 0);

    }

    @Override
    public void initListener() {
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                GoodsCommentBean bean = adapter.getList().get(position);
                EventBusUtil.post(new EventCommentBean(bean.getId() + "", bean.getUserName(), bean.getIsSelf() + "", bean.getUserId() + ""));
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore(id + "");
            }
        });
    }

    @OnClick({R.id.btn_modify,
            R.id.btn_end})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.HeadRightImageButton:
                share();
                break;


            //修改交易
            case R.id.btn_modify:
                if (isFinish != 0) {
                    gotoModify();
                } else {
                    ToastUtil.showShort(this, "审核中的交易不能修改！");
                }
                break;

            //结束交易
            case R.id.btn_end:
                endDeal();
                break;

            case R.id.tv_goods_protect:
                Intent intent = new Intent(this, CommonWebViewActivity.class);
                intent.putExtra("web_url", "https://apps.xinmei6.com/publish/news.html?newsId=2775&token=&from=android");
                intent.putExtra("web_title", "新媒之家");
                startActivity(intent);
                break;
            //客服
            case R.id.coustomer:
                MallCustomersActivity.Companion.goToCustomerServicesActivity(this,
                        MallCustomersActivity.Companion.getCUSTOMERS_TYPE_OF_DEAL(),
                        MallCustomersActivity.Companion.getCUSTOMERS_STATUS_SHOW_LIST());
                break;
        }
    }


    //分享
    private void share() {
        if (bean != null && UserManager.getInstance().isLogin(this)) {
            String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,

                    new PermissionsResultListener() {
                        @Override
                        public void onPermissionGranted() {
                            openSharePanel();
                        }

                        @Override
                        public void onPermissionDenied() {
                            mAlertDialog = DialogUtil.showDeportDialog(MyIssueDetailedActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.tv_dialog_confirm) {
                                                JumpPermissionManagement.GoToSetting(MyIssueDetailedActivity.this);
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });

                        }
                    });
        }

    }

    private void openSharePanel() {
        if (bean != null) {
            UMShareUtils shareUtils = new UMShareUtils(MyIssueDetailedActivity.this);
            String content = Jsoup.parse(bean.getContent()).body().text();

            ShareDialog.Action[] actions = null;

            //交易模式
            if (bean.getPattern() == 0) {
                actions = new ShareDialog.Action[]{
                        new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                        new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null),
                        new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                        new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                        new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null)};

                //论坛模式
            } else {
                actions = new ShareDialog.Action[]{
                        new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                        new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                        new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                        new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null)};
            }
            shareUtils.shareCustom(bean.getPicUrl(), bean.getTitle(), content, bean.getShareUrl(), actions,
                    new ShareDialog.OnShareLisntener() {
                        @Override
                        public void onShare(String key, SHARE_MEDIA media) {
                            switch (key) {
                                //分享到圈子
                                case ShareDialog.ACTION_CIRCLE:
                                    IssueDynamicActivity.share(MyIssueDetailedActivity.this, bean.getId() + "", "3", bean.getTitle(), bean.getPicUrl());
                                    break;

                                case ShareDialog.ACTION_COLLECT:
                                    collect();
                                    break;

                                //分享好友
                                case ShareDialog.ACTION_FRIENDS:
                                    ConversationListActivity.startActivity(MyIssueDetailedActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE);
                                    break;

                                //二维码
                                case ShareDialog.ACTION_QRCODE:
                                    ErWeiCodeActivity.startActivity(MyIssueDetailedActivity.this, ErWeiCodeActivity.TYPE_DEAL_GOODS, bean.getId(), "");
                                    break;
                            }

                        }
                    });

        }
    }

    //收藏
    private void collect() {
        if (UserManager.getInstance().isLogin(this)) {
            String token = UserManager.getInstance().getToken();
            HashMap<String, String> map = new HashMap<>();
            map.put("token", token);
            map.put("bizType", "3");
            map.put("bizId", String.valueOf(bean.getId()));
            mData.saveCollect(map, new ApiCallBack<Object>() {
                @Override
                public void onSuccess(Object data) {
                    if (bean != null && bean.getIsCollect() == 0) {
                        ToastUtil.showShort(MyIssueDetailedActivity.this, "收藏成功");
                        bean.setIsCollect(1);
                    } else {
                        bean.setIsCollect(0);
                        ToastUtil.showShort(MyIssueDetailedActivity.this, "取消收藏");
                    }
                }

                @Override
                public void onError(String errorCode, String message) {
                    ToastUtil.showShort(MyIssueDetailedActivity.this, message);
                }
            });
        }
    }

    private AlertDialog endDialog;

    //结束交易
    private void endDeal() {
        if (bean == null) return;

        if (bean.getIsfinish() == 1) {
            ToastUtil.showShort(this, "正在交易中，不能结束交易");
            return;
        }

        if (bean.getIsfinish() == 2) {
            ToastUtil.showShort(this, "交易已完成");
            return;
        }

        endDialog = DialogUtil.showInputDialog(this, false, "", "确认结束交易?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = UserManager.getInstance().getToken();
                mData.endDeal(token, id + "", new ApiCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(MyIssueDetailedActivity.this, "操作成功");
                        setResult(Constant.ResponseCode.NORMAL_FLAG);
                        finish();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyIssueDetailedActivity.this, message);
                    }
                });
            }
        });
    }


    private void gotoModify() {
        if (bean == null) return;
        if (bean.getIsfinish() == 1) {
            ToastUtil.showShort(this, "正在交易中，不能修改交易");
            return;
        }
        Intent intent = new Intent(this, IssueDealActivity.class);
        intent.putExtra(Constant.INTENT_DATA, bean);
        startActivityForResult(intent, 2);
    }

    @Override
    public void initData() {
        new MyIssueDetailedPresenter(this);
        mData = new DealRepository();
        requestData();
    }


    private void requestData() {
        String token = UserManager.getInstance().getToken();
        mData.getGoodsDetailed(token, id + "", new ApiCallBack<GoodsDetailedBean>() {
            @Override
            public void onSuccess(GoodsDetailedBean data) {
                showData(data);
                getComment(id + "");
            }

            @Override
            public void onError(String errorCode, String message) {
                showError(message);
            }
        });
    }

    private void showData(GoodsDetailedBean bean) {
        if (bean == null) return;
        this.bean = bean;
        if (bean.getIsfinish() == 0) {
            tvFinish.setText("删除帖子");
        } else {
            tvFinish.setText("结束交易");
        }
        tvTitle.setText(bean.getTitle());
        tvName.setText(bean.getUserName());
        tvMoney.setText("￥" + bean.getPrice());
        tvNumber.setText(bean.getNum() + "");
        tvRead.setText(bean.getReadNum() + "");
        tvCommentCount.setText(bean.getCommentNum() + "");
        ImageHelper.loadCircle(this, imgIcon, bean.getUserPic(), R.drawable.person_icon_head_120);
        if (!TextUtils.isEmpty(bean.getVideoPic()) && !TextUtils.isEmpty(bean.getVideoText())) {
            video.setUp(bean.getVideoText(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, "", bean.getVideoPic());
            video.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageHelper.loadImage(this, video.thumbImageView, bean.getVideoPic());
            video.setVisibility(View.VISIBLE);
            setVideoSize(bean.getVideoPic());
        }
        long time = bean.getCreateTime();
        String s = DateUtil.diffCurTime(time);
        tvTime.setText(s);

        //交易模式
        if (bean.getPattern() == 0) {
            tvMoney.setVisibility(View.VISIBLE);
            tvMoneyLabel.setVisibility(View.VISIBLE);
            //论坛模式 不显示金额
        } else {
            tvMoney.setVisibility(View.INVISIBLE);
            tvMoneyLabel.setVisibility(View.INVISIBLE);
        }

        layoutParame.removeAllViews();
        //加载商品参数
        List<GoodsDetailedBean.Udef> list = bean.getUdefs();
        for (GoodsDetailedBean.Udef u : list) {
            String key = u.getName();
            String value = u.getTradeInfoValue();

            View v = View.inflate(this, R.layout.item_goods_info, null);
            TextView tvKey = (TextView) v.findViewById(R.id.tv_key);
            TextView tvValue = (TextView) v.findViewById(R.id.tv_value);

            tvKey.setText(key + "：");
            tvValue.setText(value);
            layoutParame.addView(v);

            LinearLayout.LayoutParams parame = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            View line = new View(this);
            line.setLayoutParams(parame);
            line.setBackgroundColor(getResources().getColor(R.color.divide_line));
            layoutParame.addView(line);
        }

        //加载商品描述
        mWebView.loadData(bean.getContent(), "text/html; charset=UTF-8", null);

        listView.setAdapter(adapter);
    }

    private int start = 0;

    //获取留言
    private void getComment(String id) {
        String token = UserManager.getInstance().getToken();
        mData.getComments(token, id, start + "", new ApiCallBack<List<GoodsCommentBean>>() {
            @Override
            public void onSuccess(List<GoodsCommentBean> data) {
                if (data == null || data.size() == 0) {
                    showNoComments();
                    return;
                }
                listView.notifyChangeData(data, adapter);
            }

            @Override
            public void onError(String errorCode, String message) {
                showNoComments();
            }
        });
    }

    private void loadMore(String id) {
        String token = UserManager.getInstance().getToken();
        start += 15;
        mData.getComments(token, id, start + "", new ApiCallBack<List<GoodsCommentBean>>() {
            @Override
            public void onSuccess(List<GoodsCommentBean> data) {
                listView.changeData(data, adapter);
            }

            @Override
            public void onError(String errorCode, String message) {
            }
        });
    }

    private void showNoComments() {
        layoutNoComment.setVisibility(View.VISIBLE);
    }

    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan) {
        String title = bean.getTitle();
        String img = bean.getPicUrl();
        String id = bean.getId() + "";
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "3", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
            }

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(MyApplication.getInstance(), "分享成功");
                if (!TextUtils.isEmpty(liuyan)) {
                    RongIMTextUtil.INSTANCE.relayMessage
                            (liuyan, targetId, type);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(MyApplication.getInstance(), "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }


    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading(true);
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        if (bean == null) {
            getBaseEmptyView().showEmptyContent(info);
        } else {
            ToastUtil.showShort(this, info);
        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.ResponseCode.NORMAL_FLAG) {
            requestData();
        }

        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            String targetId = bean.targetId;
            Conversation.ConversationType type = bean.mType;
            shareMessage(targetId, type, bean.liuyan);
        }
    }


    private DynamicCommentDialog mCommentDialog;

    private void showCommentPop() {
        mCommentDialog = new DynamicCommentDialog();
        mCommentDialog.setSendListener(new DynamicCommentDialog.OnSendListener() {
            @Override
            public void sendComment(String content) {
                submitComment(content);
                mCommentDialog.dismiss();
            }
        });

        if (commentBean != null && !TextUtils.isEmpty(commentBean.name)) {
            String content = "回复 " + commentBean.name + "：";
            mCommentDialog.setText(content);
        }
        mCommentDialog.show(getSupportFragmentManager(), DynamicCommentDialog.class.getSimpleName());
    }


    //提交评论
    private void submitComment(String content) {
        WindowUtil.closeInputMethod(this);
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(this, "请输入要留言的内容");
            return;
        }

        //回复评论
        if (isReply) {
            mPresenter.replyComments(commentBean.id + "", content, commentBean.targetUserId);

            //提交留言
        } else {
            mPresenter.submitComments(bean.getId() + "", content);
        }
    }


    private EventCommentBean commentBean;
    private boolean isReply;        //是否是回复留言

    @Subscribe
    public void onEvent(EventCommentBean bean) {
        isReply = true;
        commentBean = bean;
        showCommentPop();
    }

    @Subscribe
    public void onEvent(EventClickBean bean) {
        View v = (View) bean.bean;
        switch (v.getId()) {
            case R.id.img_delet:
                GoodsCommentBean commentBean = (GoodsCommentBean) v.getTag();
                mPresenter.deletComment(commentBean.getId());
                break;

            case R.id.tv_goods:
                GoodsCommentBean commentBean1 = (GoodsCommentBean) v.getTag();
                mPresenter.DZ(commentBean1.getId());
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.destroy();
        mAlertDialog = null;
        if (mWebView != null) {
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.removeJavascriptInterface("ShowImg");      //记得移除，避免内存泄漏

            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        EventBusUtil.unregister(this);
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showSubmitSuccess() {
        requestData();
        int commentNum = bean.getCommentNum() + 1;
        bean.setCommentNum(commentNum);
        tvCommentCount.setText(bean.getCommentNum() + "");
        ToastUtil.showShort(this, "发表成功");
    }

    @Override
    public void showReplySuccess() {
        isReply = false;
        requestData();
        ToastUtil.showShort(this, "发表成功");
    }

    @Override
    public void showDeletCommentSuccess(int id) {
        for (int i = 0; i < adapter.getList().size(); i++) {
            if (id == adapter.getList().get(i).getId()) {
                listView.removeData(adapter, i);
                break;
            }
        }
    }

    @Override
    public void showDZSuccess(int id) {
        if (adapter.getList() != null && adapter.getList().size() > 0) {
            for (int i = 0; i < adapter.getList().size(); i++) {
                if (id == adapter.getList().get(i).getId()) {
                    GoodsCommentBean commentBean = adapter.getList().get(i);
                    if (commentBean.getIsDZ() == 0) {
                        commentBean.setIsDZ(1);
                        commentBean.setDzNum(commentBean.getDzNum() + 1);
                    } else {
                        commentBean.setIsDZ(0);
                        commentBean.setDzNum(commentBean.getDzNum() - 1);
                    }
                    listView.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @Override
    public void setPresenter(MyIssueDetailedContract.Presenter presenter) {
        mPresenter = presenter;
    }

    // 设置视频的大小，宽适应屏幕，高根据比例缩放
    private void setVideoSize(String url) {
        if (!TextUtils.isEmpty(url)) {
            String temp = url.substring(url.indexOf("?") + 1, url.length());
            if (!TextUtils.isEmpty(temp) && temp.contains("*")) {
                String widthS = url.substring(url.indexOf("?") + 1, url.indexOf("*"));
                String heightS = url.substring(url.indexOf("*") + 1, url.length());
                float width = Float.parseFloat(widthS);
                float height = Float.parseFloat(heightS);
                float b = width / height;
                int c = (int) (WindowUtil.getScreenW(this) / b);
                ViewGroup.LayoutParams layoutParams = video.getLayoutParams();
                layoutParams.height = c;
                video.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @JavascriptInterface
    public void showImg(int position, String[] imageUrl) {
        String userCode = bean == null ? "0" : bean.getUserCode();
        ArrayList<Uri> uris = new ArrayList<>();
        for (String s : imageUrl) {
            uris.add(Uri.parse(s));
        }
        CommonPhotoViewActivity.startActivity(MyIssueDetailedActivity.this, uris, position);
    }

    public class MyWebViewClient extends WebViewClient {

        private Context con;

        public MyWebViewClient(Context con) {
            this.con = con;
        }

        /**
         * webview开始加载调用此方法
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        /**
         * 1-webview 加载完成调用此方法;
         * 2-查找页面中所有的<img>标签，然后动态添加onclick事件;
         * 3-事件中回调本地java的jsInvokeJava方法;
         * 注意：webtest别名和上面contentWebView.addJavascriptInterface(this, "webtest")别名要一致;
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            //动态注入js
            view.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); "
                    + "  var strings = new Array();"
                    + " for (var j = 0; j < objs.length; j++) {"
                    + "strings[j] =  objs[j].src"
                    + "}"
                    + "for(var i=0;i<objs.length;i++) { "
                    + " objs[i].index = i;"
                    + " objs[i].onclick=function()  {"
                    + "        window.ShowImg.showImg(this.index,strings);  "
                    + "    }  "
                    + "}"
                    + "})()");
        }
    }
}
