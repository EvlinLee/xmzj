package com.gxtc.huchuan.ui.deal.deal.goodsDetailed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.GoodsDetailedAdapter;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventCommentBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.customemoji.utils.GlobalOnItemClickManagerUtils;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.fastDeal.FastDealActivity;
import com.gxtc.huchuan.ui.live.series.share.SeriesShareListActivity;
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.ExpandVideoPlayer;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.photoview.log.LoggerDefault;

/**
 * 交易详情页面
 */
public class GoodsDetailedActivity extends BaseTitleActivity implements View.OnClickListener,
        GoodsDetailedContract.View {

    @BindView(R.id.activity_detailed)
    View rootView;
    @BindView(R.id.rl_goods)
    RecyclerView listView;
    @BindView(R.id.swipe_goods)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tv_kuaisu)
    TextView tvKuaisu;
    @BindView(R.id.tv_chat)
    TextView tvChat;
    @BindView(R.id.btn_danbao)
    View btnDanBao;
    @BindView(R.id.btn_kuaisu)
    View btnKuaiSu;
    @BindView(R.id.line_hl)
    View lineHl;
    @BindView(R.id.hl)
    LinearLayout layoutBottom;       //底部四个按钮布局

    //论坛相关布局
    @BindView(R.id.layout_luntan)
    LinearLayout layoutLuntan;       //论坛布局
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.img_collect)
    ImageView imgCollect;

    /*@BindView(R.id.btn_comment)    View         btnComment;
    @BindView(R.id.line_comment)   View         lineComment;
    @BindView(R.id.edit_comment)   EditText     editComment;
    @BindView(R.id.btn_send)       TextView     btnSend;
    @BindView(R.id.layout_comment) LinearLayout layoutComment;*/      //留言编辑框布局

    private ImageView imgIcon;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvTitle;
    private TextView tvMoney;
    private TextView tvMoneyLabel;
    private TextView tvDanbao;
    private TextView tvRead;
    private TextView tvCommentCount;
    private TextView tvType;
    private WebView mWebView;
    private LinearLayout layoutParame;      //商品参数布局
    private LinearLayout layoutNoComment;   //没有留言时显示的布局
    private ExpandVideoPlayer video;

    private View headview;

    private boolean isReply;        //是否是回复留言

    private EventCommentBean commentBean;
    private ChatRoomBean chatRoomBean;

    private GoodsDetailedContract.Presenter mPresenter;
    private GoodsDetailedAdapter adapter;

    private GoodsDetailedBean bean;
    private TextView tvNumber;
    private int oldCommentNum;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detailed);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_goods_detailed));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);

        refreshLayout.setColorSchemeResources(Constant.REFRESH_COLOR);

        headview = View.inflate(this, R.layout.head_goods_detailed, null);

        imgIcon = (ImageView) headview.findViewById(R.id.img_head);
        tvNumber = (TextView) headview.findViewById(R.id.tv_number);
        tvName = (TextView) headview.findViewById(R.id.tv_name);
        tvTime = (TextView) headview.findViewById(R.id.tv_time);
        tvTitle = (TextView) headview.findViewById(R.id.tv_title);
        tvMoney = (TextView) headview.findViewById(R.id.tv_money);
        tvMoneyLabel = (TextView) headview.findViewById(R.id.tv_money_label);
        tvDanbao = (TextView) headview.findViewById(R.id.tv_danbao);
        tvRead = (TextView) headview.findViewById(R.id.tv_read);
        tvCommentCount = (TextView) headview.findViewById(R.id.tv_comment);
        tvType = (TextView) headview.findViewById(R.id.tv_type);
        mWebView = (WebView) headview.findViewById(R.id.webView);
        headview.findViewById(R.id.tv_goods_protect).setOnClickListener(this);
        headview.findViewById(R.id.coustomer).setOnClickListener(this);
        layoutParame = (LinearLayout) headview.findViewById(R.id.layout_parame);
        layoutNoComment = (LinearLayout) headview.findViewById(R.id.layout_no_comment);
        video = (ExpandVideoPlayer) headview.findViewById(R.id.ep_goods_video);
        imgIcon.setOnClickListener(this);
        tvName.setOnClickListener(this);

        tvMoney.getPaint().setFakeBoldText(true);

        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST, getResources().getDimensionPixelOffset(R.dimen.px2dp), this.getResources().getColor(R.color.divide_line)));
        adapter = new GoodsDetailedAdapter(this, new ArrayList<GoodsCommentBean>(), R.layout.item_goods_comment);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.addHeadView(headview);
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient(this));
        mWebView.addJavascriptInterface(this, "ShowImg");

    }

    @Override
    public void initListener() {
        checkKeyboard(rootView);

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore(bean.getId() + "");
            }
        });
    }

    @OnClick({R.id.btn_danbao, R.id.btn_kuaisu, R.id.btn_comment, R.id.tv_comment, R.id.img_collect, R.id.fab_issue})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                goBack();
                break;

            //快速交易
            case R.id.btn_kuaisu:
                gotoFastDeal();
                break;

            //联系买／卖 家
            case R.id.btn_danbao:
                gotoChatRoom();
                break;

            //分享
            case R.id.HeadRightImageButton:
                share();
                break;

            case R.id.tv_comment:
            case R.id.btn_comment:
                isReply = false;
                showCommentPop();
                break;

            //收藏
            case R.id.img_collect:
                collect();
                break;

            case R.id.img_head:
            case R.id.tv_name:
                PersonalInfoActivity.startActivity(this, bean.getUserCode());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            goBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void goBack() {
        if (bean != null) {
            Intent intent = new Intent();
            if (oldCommentNum != bean.getCommentNum()) {
                intent.putExtra("commentCount", bean.getCommentNum());
            }
            intent.putExtra("readCount", bean.getReadNum());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    //收藏
    private void collect() {
        if (1 == bean.getIsCollect()) {
            ToastUtil.showShort(MyApplication.getInstance(), "您已经收藏过了");
        } else {
            if (UserManager.getInstance().isLogin(this)) {
                mPresenter.collect(bean.getId());
            } else {
                GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            }
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
                            mAlertDialog = DialogUtil.showDeportDialog(GoodsDetailedActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.tv_dialog_confirm) {
                                                JumpPermissionManagement.GoToSetting(GoodsDetailedActivity.this);
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
            UMShareUtils shareUtils = new UMShareUtils(GoodsDetailedActivity.this);
            String content = Jsoup.parse(bean.getContent()).body().text();

            ShareDialog.Action[] actions = null;

            //交易模式
            if (bean.getPattern() == 0) {
                actions = new ShareDialog.Action[]{
//                        new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                        new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null),
//                        new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                        new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),

                        new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null)};


                //论坛模式
            } else {
                actions = new ShareDialog.Action[]{
//                        new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
//                        new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
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
                                    IssueDynamicActivity.share(GoodsDetailedActivity.this, bean.getId() + "", "3", bean.getTitle(), bean.getPicUrl());
                                    break;

                                case ShareDialog.ACTION_COLLECT:
                                    collect();
                                    break;

                                //分享好友
                                case ShareDialog.ACTION_FRIENDS:
                                    ConversationListActivity.startActivity(GoodsDetailedActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE);
                                    break;

                                //二维码
                                case ShareDialog.ACTION_QRCODE:
                                    ErWeiCodeActivity.startActivity(GoodsDetailedActivity.this, ErWeiCodeActivity.TYPE_DEAL_GOODS, bean.getId(), "");
                                    break;
                            }

                        }
                    });
        }
    }

    private void gotoChatRoom() {
        final User user = UserManager.getInstance().getUser();
        if (user == null) {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            return;
        }
        if (bean != null) {
            startPrivateChat(bean.getUserCode(), bean.getUserName());
        }

    }

    private void gotoFastDeal() {
        if (UserManager.getInstance().isLogin(this)) {
            GotoUtil.goToActivity(this, FastDealActivity.class, 0, bean.getId());
        }
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


    // 软键盘的显示状态
    private boolean ShowKeyboard;

    //监听软键盘的弹收
    private void checkKeyboard(View v) {
        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 应用可以显示的区域。此处包括应用占用的区域，包括标题栏不包括状态栏
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                // 键盘最小高度
                int minKeyboardHeight = 150;
                // 获取状态栏高度
                int statusBarHeight = getStatusBarHeight();
                // 屏幕高度,不含虚拟按键的高度
                int screenHeight = rootView.getRootView().getHeight();
                // 在不显示软键盘时，height等于状态栏的高度
                int height = screenHeight - (r.bottom - r.top);


                if (ShowKeyboard) {
                    // 如果软键盘是弹出的状态，并且height小于等于状态栏高度，
                    // 说明这时软键盘已经收起
                    if (height - statusBarHeight < minKeyboardHeight) {
                        ShowKeyboard = false;
                    }
                } else {
                    // 如果软键盘是收起的状态，并且height大于状态栏高度，
                    // 说明这时软键盘已经弹出
                    if (height - statusBarHeight > minKeyboardHeight) {
                        ShowKeyboard = true;
                    }
                }
            }
        };
        v.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    private DynamicCommentDialog mCommentDialog;

    private void showCommentPop() {
        if (!UserManager.getInstance().isLogin(this)) {
            return;
        }
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

    private void startPrivateChat(String userCode, String title) {
        if (bean == null) return;
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath(
                "conversation").appendPath(
                Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter(
                "targetId", userCode).appendQueryParameter("title", title).appendQueryParameter(
                "goodsId", bean.getId() + "").appendQueryParameter("isFinish",
                bean.getIsfinish() + "").appendQueryParameter("flag", "1").appendQueryParameter("tradeType", bean.getTradeType() + "").build(); //0：出售，1：求购(必选)
        startActivity(new Intent("android.intent.action.VIEW", uri));
    }

    @Override
    public void initData() {
        new GoodsDetailedPresenter(this);
        String id = getIntent().getStringExtra(Constant.INTENT_DATA);
        if (!TextUtils.isEmpty(id)) {
            mPresenter.getData(id);
        } else {
            DealListBean bean = (DealListBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
            mPresenter.getData(bean.getId() + "");
        }
    }

    //显示商品详情信息 商品留言在之后获取
    @Override
    public void showData(GoodsDetailedBean bean) {
        if (bean == null) return;
        this.bean = bean;
        if (!TextUtils.isEmpty(bean.getVideoPic()) && !TextUtils.isEmpty(bean.getVideoText())) {
            video.setUp(bean.getVideoText(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, "", bean.getVideoPic());
            video.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageHelper.loadImage(this, video.thumbImageView, bean.getVideoPic());
            video.setVisibility(View.VISIBLE);
            setVideoSize(bean.getVideoPic());
        }
        //判断当前帖子是否为自己的帖子
        if (bean.getUserCode().equals(UserManager.getInstance().getUserCode())) {
            btnDanBao.setClickable(false);
            btnKuaiSu.setClickable(false);
            tvChat.setTextColor(getResources().getColor(R.color.btn_enable_false));
            tvKuaisu.setTextColor(getResources().getColor(R.color.btn_enable_false));
            tvChat.setCompoundDrawablesWithIntrinsicBounds(R.drawable.deal_chat_dd, 0, 0, 0);
            tvKuaisu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.deal_fast_dd, 0, 0, 0);
        }
        //0不匿名   1匿名
        if ("0".equals(bean.getAnonymous()) || bean.getAnonymous() == null) {
            tvName.setText(bean.getUserName());
        } else {
            if (bean.getUserName().length() > 1) {
                tvName.setText(bean.getUserName().substring(0, 1) + "**");
            } else {
                tvName.setText(bean.getUserName() + "**");
            }
        }
        tvNumber.setText(bean.getNum() + "");
        tvRead.setText(bean.getReadNum() + "");
        tvCommentCount.setText(bean.getCommentNum() + "");
        oldCommentNum = bean.getCommentNum();
        tvName.setText(bean.getUserName());
        tvTitle.setText(bean.getTitle());
        tvMoney.setText("￥" + bean.getPrice());
        ImageHelper.loadCircle(this, imgIcon, bean.getUserPic(), R.drawable.person_icon_head_120);

        String subType = TextUtils.isEmpty(bean.getTradeTypeSonName()) ? "" : bean.getTradeTypeSonName();
        tvType.setText(bean.getTradeTypeName() + " " + subType);

        long time = bean.getCreateTime();
        String s = DateUtil.showTimeAgo(time + "");
        tvTime.setText(s);

        //是否收藏
        if (bean.getIsCollect() == 0) {
            imgCollect.setImageResource(R.drawable.live_icon_collect_normal);
        } else {
            imgCollect.setImageResource(R.drawable.live_icon_collect_click);
        }

        //交易模式
        if (bean.getPattern() == 0) {
            tvMoney.setVisibility(View.VISIBLE);
            tvMoneyLabel.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            layoutLuntan.setVisibility(View.GONE);

            //论坛模式 不显示金额
        } else {
            tvMoney.setVisibility(View.INVISIBLE);
            tvMoneyLabel.setVisibility(View.INVISIBLE);
            layoutBottom.setVisibility(View.INVISIBLE);
            layoutLuntan.setVisibility(View.VISIBLE);
            lineHl.setVisibility(View.GONE);
        }


        //如果是求购  不显示交易按钮布局
        if (bean.getTradeType() == 1) {
            btnKuaiSu.setVisibility(View.GONE);
            layoutParame.setVisibility(View.GONE);
            tvChat.setText("联系买方");
        } else {
            //加载商品参数
            List<GoodsDetailedBean.Udef> list = bean.getUdefs();
            for (GoodsDetailedBean.Udef u : list) {
                String key = u.getName();
                String value = u.getTradeInfoValue();

                if (TextUtils.isEmpty(value)) continue;

                View v = View.inflate(this, R.layout.item_goods_info, null);
                TextView tvKey = (TextView) v.findViewById(R.id.tv_key);
                TextView tvValue = (TextView) v.findViewById(R.id.tv_value);

                tvKey.setText(key + "：");
                tvValue.setText(value);
                layoutParame.addView(v);

                LayoutParams parame = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                View line = new View(this);
                line.setLayoutParams(parame);
                line.setBackgroundColor(getResources().getColor(R.color.divide_line));
                layoutParame.addView(line);
            }

        }

        //String htmlHead = "<!DOCTYPE html><html><head><script type=\"text/javascript\" src=\"http://app.xinmei6.com/html/plug/jquery.min.js\"></script><script type=\"text/javascript\" src=\"http://app.xinmei6.com/html/plug/jquery.mobile.min.js\"></script><script type=\"text/javascript\" src=\"http://app.xinmei6.com/html/js/new_file.js\"></script></head><body>";
        //String htmlTail = "</body></html>";
        //加载商品描述
        //mWebView.loadData(bean.getContent(), "text/html; charset=UTF-8", null);
        String htmlData = bean.getContent().replace("<img", "<img style='max-width:100%;height:auto;'");
        mWebView.loadData(htmlData, "text/html; charset=UTF-8", null);


        if (bean.getNum() == 0) {
            tvKuaisu.setText("已售完");
            btnKuaiSu.setBackgroundColor(getResources().getColor(R.color.greyd1d1d1));
            btnKuaiSu.setOnClickListener(null);
        }

        listView.setAdapter(adapter);

        rootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showComments(List<GoodsCommentBean> beans) {
        layoutNoComment.setVisibility(View.GONE);
        listView.notifyChangeData(beans, adapter);
    }

    @Override
    public void showLoadMore(List<GoodsCommentBean> beans) {
        listView.changeData(beans, adapter);
    }

    @Override
    public void showNoLoadMore() {
        listView.loadFinish();
    }

    @Override
    public void showNoComments() {
        layoutNoComment.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSubmitSuccess() {
        mPresenter.getComments(bean.getId() + "");
        int commentNum = bean.getCommentNum() + 1;
        bean.setCommentNum(commentNum);
        tvCommentCount.setText(bean.getCommentNum() + "");
        ToastUtil.showShort(this, "发表成功");
    }

    @Override
    public void showReplySuccess() {
        isReply = false;
        mPresenter.getComments(bean.getId() + "");
        ToastUtil.showShort(this, "发表成功");
    }

    @Override
    public void showCollectSuccess() {
        if (bean != null && bean.getIsCollect() == 0) {
            imgCollect.setImageResource(R.drawable.live_icon_collect_click);
            bean.setIsCollect(1);
            ToastUtil.showShort(this, "收藏成功");
        } else {
            imgCollect.setImageResource(R.drawable.live_icon_collect_normal);
            bean.setIsCollect(0);
            ToastUtil.showShort(this, "取消收藏");
        }
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
    public void setPresenter(GoodsDetailedContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(bean.getId() + "");
            }
        });
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
                if (!UserManager.getInstance().isLogin(this)) return;
                GoodsCommentBean commentBean1 = (GoodsCommentBean) v.getTag();
                mPresenter.DZ(commentBean1.getId());
                break;
        }
    }


    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(GoodsDetailedActivity.this,
                false, null, getString(R.string.token_overdue),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            ReLoginUtil.ReloginTodo(GoodsDetailedActivity.this);
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            ToastUtil.showShort(this, "分享成功");
        }

        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            String targetId = bean.targetId;
            Conversation.ConversationType type = bean.mType;
            shareMessage(targetId, type, bean.liuyan);
        }
    }

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, GoodsDetailedActivity.class);
        intent.putExtra(Constant.INTENT_DATA, id);
        context.startActivity(intent);
    }

    /*
     *  单独显示图片
     */

    @JavascriptInterface
    public void showImg(int position, String[] imageUrl) {
        String userCode = bean == null ? "0" : bean.getUserCode();
        ArrayList<Uri> uris = new ArrayList<>();
        for (String s : imageUrl) {
            uris.add(Uri.parse(s));
        }
        CommonPhotoViewActivity.startActivity(GoodsDetailedActivity.this, uris, position);
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

    public static class MyWebViewClient extends WebViewClient {

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();

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
        GlobalOnItemClickManagerUtils.getInstance().attachToEditText(null);
    }

}
