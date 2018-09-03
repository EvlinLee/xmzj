package com.gxtc.huchuan.ui.live.intro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInFoStatusBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.PersonCountBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.bean.event.EventLiveRefreshBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventSeriesPayBean;
import com.gxtc.huchuan.bean.pay.PayBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.pop.PopPayShare;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.live.conversation.LiveConversationActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.member.MemberManagerActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.ExpandVideoPlayer;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import cn.jzvd.JZVideoPlayer;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 课堂介绍
 */
public class LiveIntroActivity extends BaseTitleActivity implements LiveIntroContract.View,
        View.OnClickListener {
    private static final String TAG = "LiveIntroActivity";

    public static final int EDITE_REQUESTCODE       = 1 << 1;
    public static final int GOTO_CIRCLE_REQUESTCODE = 1 << 2;

    @BindView(R.id.toolbar)                     Toolbar        mToolbar;
    @BindView(R.id.app_bar)                     AppBarLayout   mAppBar;
    @BindView(R.id.tv_price_content)            TextView       mTvPriceContent;
    @BindView(R.id.tv_original_price)           TextView       mTvOriginalPrice;       //原价  带删除线
    @BindView(R.id.tv_starttime)                TextView       mTvStarttime;
    @BindView(R.id.tv_time_remind)              TextView       mTvTimeRemind;
    @BindView(R.id.tv_emcee_name)               TextView       mTvEmceeName;
    @BindView(R.id.tv_lecturer_name)            TextView       mTvLecturerName;
    @BindView(R.id.tv_live_desc)                TextView       mTvLiveDesc;
    @BindView(R.id.tv_topic_count)              TextView       mTvTopicCount;
    @BindView(R.id.tv_attention_count)          TextView       mTvAttentionCount;
    @BindView(R.id.tv_person_count)             TextView       mTvJoinCount;
    @BindView(R.id.tv_signup_person_count)      TextView       mTvSignupPersonCount;
    @BindView(R.id.live_intro_follow_ownername) TextView       mOwnerName;
    @BindView(R.id.tv_pay_share)                TextView       mPayShare;
    @BindView(R.id.tv_chatroomname)             TextView       mChatRoomName;
    @BindView(R.id.tv_title_bottom)             TextView       tvTitleBottom;
    @BindView(R.id.tv_spread)                   TextView       tvSpread;
    @BindView(R.id.layout_share)                View           mLayoutShareArea;
    @BindView(R.id.layout_join_modlue_space)    View           mLayoutJoinModelu_space;
    @BindView(R.id.layout_join_area)            View           mLayoutJoinArea;
    @BindView(R.id.layout_follow)               View           mLayoutFollow;
    @BindView(R.id.layout_follow_module_space)  View           mLayoutFollowModuleSpace;
    @BindView(R.id.view_line)                   View           mViewLine;
    @BindView(R.id.class_count)                 View           classCountLayout;
    @BindView(R.id.tv_price_area)               LinearLayout   mPriceArea;
    @BindView(R.id.rl_chatroom_area)            RelativeLayout mChatRoomArea;
    @BindView(R.id.live_intro_follow_head)      ImageView      mFollowHead;
    @BindView(R.id.tv_chatroomname_head)        ImageView      mFollowHead2;
    @BindView(R.id.live_intro_image)            ImageView      mLiveIntroImage;
    @BindView(R.id.tablayout)                   ImageView      mTablayout;
    @BindView(R.id.live_intro_follow_attention) ImageView      mOwnerAttention;
    @BindView(R.id.img_sign_more)               ImageView      imgSignMore;


    @BindView(R.id.ll_share_person_area)  LinearLayout mSharePersonArea;
    @BindView(R.id.ll_signup_person_area) LinearLayout mSignupPersonArea;
    @BindView(R.id.cb_attention_checkbox) ImageView    mAttentionCheckBox;
    @BindView(R.id.iv_desc_zk)            ImageView    mIvDescZk;


    //作者相关的低部
    @BindView(R.id.ll_owner_redact_area) LinearLayout mOwnerRedactArea;
    @BindView(R.id.ll_owner_editPageBtn) LinearLayout mOwnerEditPageBtn;
    @BindView(R.id.tv_owner_enter_btn)   TextView     mOwnerEnterBtn;

    //单个按钮模式
    @BindView(R.id.ll_audience_simple_area) LinearLayout mAudienceSimpeArea;
    @BindView(R.id.tv_audience_free_enter)  TextView     mAudienceFreeEnter;


    //密码模式
    @BindView(R.id.ll_audience_password_model_area) LinearLayout                mAudiencePasswordModelArea;
    @BindView(R.id.et_audience_password_edit)       EditText                    mAudiencePasswordEdit;
    @BindView(R.id.btn_audience_password_send)      Button                      mAudiencePasswordSend;
    @BindView(R.id.panel_root)                      KPSwitchFSPanelLinearLayout mPanelRoot;
    @BindView(R.id.wv_live_desc)                    WebView                     mWvLiveDesc;
    @BindView(R.id.ep_live_video)                   ExpandVideoPlayer           epLiveVideo;

    private LiveIntroContract.Presenter mPresenter;
    private UMShareUtils                shareUtils;

    private String from;
    private String mId;

    private CollapsingToolbarLayout mToolbarLayout;
    private AppBarLayout            mAppBarLayout;
    private int                     mStatusBarHeight;
    private TextView                mTvTitle;

    private String  title         = "";
    private String  shareUserCode = null;
    private boolean isFree        = false;
    private boolean isFreeTime    = false;
    private boolean isSignup      = false;
    private boolean isDataChange  = false;
    private boolean isDescZk      = false;

    private ChatInfosBean      bean;
    private ChatInFoStatusBean statusBean;
    private UMShareUtils       payshareUtils;
    private PopPayShare        mPopPayShare;

    private String         freeSign;        //免费报名的邀请码
    private SeriesPageBean seriesBean;
    private boolean        isAuditions;
    private AlertDialog    mAlertDialog;

    @Override
    public boolean isLightStatusBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_intro_old);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        setTopPadding(mToolbar, true);

        mToolbar.inflateMenu(R.menu.menu_live_intro);
        setSupportActionBar(mToolbar);
        seriesBean = (SeriesPageBean) getIntent().getSerializableExtra("seriesBean");
        isAuditions = getIntent().getBooleanExtra("isAuditions", false);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mToolbarLayout.setTitleEnabled(false);
        mToolbarLayout.setTitle("");
        mToolbar.setTitle("");
        tvTitleBottom.setText(title);
        mTvTitle.setText(title);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLiveIntroImage.getLayoutParams();
        layoutParams.height = 45 * WindowUtil.getScreenW(this) / 67;//保持宽高比 67/45
    }

    @Override
    public void initListener() {
        KeyboardUtil.attach(this, mPanelRoot, new KeyboardUtil.OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                if (isShowing) {
                    mPanelRoot.setVisibility(View.VISIBLE);
                } else {
                    mPanelRoot.setVisibility(View.GONE);
                }
            }
        });

        KPSwitchConflictUtil.attach(mPanelRoot, null, mAudiencePasswordEdit,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        if (switchToPanel) {
                            mAudiencePasswordEdit.clearFocus();
                        } else {
                            mAudiencePasswordEdit.requestFocus();
                        }
                    }
                });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int i = -(mLiveIntroImage.getHeight() - mToolbar.getHeight() - mStatusBarHeight);
                if (verticalOffset <= -(mLiveIntroImage.getHeight() - mToolbar.getHeight() - mStatusBarHeight)) {
                    mTvTitle.setVisibility(View.VISIBLE);
                    mToolbarLayout.setContentScrimColor(
                            getResources().getColor(R.color.colorPrimary));
                    mToolbar.getMenu().findItem(R.id.action_item_share).setIcon(
                            R.drawable.navigation_icon_share);
                    mToolbar.setNavigationIcon(R.drawable.navigation_icon_back);

                } else if (verticalOffset == 0) {
                    mToolbarLayout.setTitle(title);

                } else {
                    mToolbarLayout.setTitle("");
                    mTvTitle.setVisibility(View.GONE);
                    mToolbarLayout.setContentScrimColor(
                            getResources().getColor(R.color.transparent));
                    mToolbar.getMenu().findItem(R.id.action_item_share).setIcon(
                            R.drawable.navigation_icon_share_white);
                    mToolbar.setNavigationIcon(R.drawable.navigation_icon_back_white);
                }

            }
        });

        mFollowHead.setOnClickListener(this);
        mFollowHead2.setOnClickListener(this);
        mOwnerName.setOnClickListener(this);
        mChatRoomArea.setOnClickListener(this);
        mIvDescZk.setOnClickListener(this);
        mAttentionCheckBox.setOnClickListener(this);
        mOwnerAttention.setOnClickListener(this);
        classCountLayout.setOnClickListener(this);
        mLayoutFollow.setOnClickListener(this);

        mOwnerEditPageBtn.setOnClickListener(this);     //作者模式相关
        mOwnerEnterBtn.setOnClickListener(this);
        mAudienceSimpeArea.setOnClickListener(this);    //观众模式相关
        mAudiencePasswordSend.setOnClickListener(this); //密码模式
        mLayoutShareArea.setOnClickListener(this);      //分享榜
    }


    @Override
    public void initData() {
        new LiveIntroPresenter(this);
        from = getIntent().getStringExtra("from");
        freeSign = getIntent().getStringExtra("freeSign");
        //现在app所有的内部分享都不得佣金 除非是微信外部分享  圈子也是这样
        if (!TextUtils.isEmpty(freeSign)) {
            shareUserCode = getIntent().getStringExtra("shareUserCode");
        }
        mId = getIntent().getStringExtra("id");
        mStatusBarHeight = getStatusBarHeight();

        if (TextUtils.isEmpty(mId)) {
            Toast.makeText(this, "数据获取失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mPresenter.getData(mId);
    }

    protected void setTopPadding(View v, boolean changeHeight) {
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int stataHeight = getStatusBarHeight();
                if (changeHeight) {
                    v.getLayoutParams().height = v.getLayoutParams().height + stataHeight;
                }
                v.setPadding(v.getPaddingLeft(), stataHeight, v.getPaddingRight(),
                        v.getPaddingBottom());

            }
        }
    }


    @Override
    public void onClick(View v) {
        if (!UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            return;
        }
        if (bean == null) return;
        switch (v.getId()) {
            case R.id.tv_chatroomname_head:
            case R.id.live_intro_follow_head:
                PersonalInfoActivity.startActivity(this, bean.getUserCode());
                break;

            case R.id.rl_chatroom_area:
                PersonalInfoActivity.startActivity(this, bean.getUserCode());
                break;

            case R.id.iv_desc_zk:
                changeWvLiveDescZK();
                break;

            case R.id.layout_follow:
            case R.id.class_count:
            case R.id.live_intro_follow_ownername:
                LiveHostPageActivity.startActivity(LiveIntroActivity.this, "1", bean.getChatRoom());
                break;

            //关注
            case R.id.cb_attention_checkbox:
                if (UserManager.getInstance().isLogin(this)) {
                    mPresenter.follow(bean.getChatRoom());
                }
                break;

            case R.id.btn_audience_password_send:
                passwordModelSingup();
                break;

            //点击购买区域
            case R.id.ll_audience_simple_area:
                clickEnrollClassroom();
                break;

            case R.id.ll_owner_editPageBtn:
                //这里进入编辑页
                GotoEditPage();
                break;

            case R.id.tv_owner_enter_btn:
                //这里进入课堂界面
                if (!TextUtils.isEmpty(from)) {//如果是从课堂里进来的  直接finish
                    finish();
                    return;
                }
                gotoLiveRoom();
                break;

            case R.id.tv_pay_share:
                //付费分享
                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                if (mPopPayShare == null) {
                                    mPopPayShare = new PopPayShare(LiveIntroActivity.this,
                                            R.layout.pop_live_invite);
                                    mPopPayShare.setData(bean);
                                }
                                mPopPayShare.showPopOnRootView(LiveIntroActivity.this);
                            }

                            @Override
                            public void onPermissionDenied() {

                            }
                        });
                break;

            case R.id.layout_share:
                if (UserManager.getInstance().isLogin()) {
                    Intent intent = new Intent(LiveIntroActivity.this,
                            TopicShareListActivity.class);
                    intent.putExtra("chatRoomId", bean.getChatRoom());
                    intent.putExtra("chatInfoId", bean.getId());
                    intent.putExtra("chatInfosBean", bean);
                    startActivity(intent);

                } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
                break;

            case R.id.layout_join_area:
                String id = bean.getId();
                String count = bean.getJoinCount();
                String type = "1";
                String joinType = bean.getRoleType();
                if (seriesBean != null) {
                    id = seriesBean.getId();
                    count = seriesBean.getBuyCount();
                    type = "2";
                    joinType = seriesBean.getJoinType() + "";
                }
                MemberManagerActivity.startActivity(this, id, count, type, joinType);
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_live_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!UserManager.getInstance().isLogin(this)) return true;

        switch (item.getItemId()) {
            case R.id.action_item_share:
                if (bean == null) return true;

                //这里跳出去分享
                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                String title = bean.getSubtitle();
                                String desc  = Jsoup.parse(bean.getDesc()).body().text();
                                desc = TextUtils.isEmpty(desc) ? title : desc;
                                String uri = bean.getFacePic();

                                ShareDialog.Action[] actions = {new ShareDialog.Action(
                                        ShareDialog.ACTION_INVITE, R.drawable.share_icon_invitation,
                                        null), new ShareDialog.Action(ShareDialog.ACTION_QRCODE,
                                        R.drawable.share_icon_erweima,
                                        null), new ShareDialog.Action(ShareDialog.ACTION_COLLECT,
                                        R.drawable.share_icon_collect,
                                        null), new ShareDialog.Action(ShareDialog.ACTION_COPY,
                                        R.drawable.share_icon_copy, null), new ShareDialog.Action(
                                        ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic,
                                        null), new ShareDialog.Action(ShareDialog.ACTION_FRIENDS,
                                        R.drawable.share_icon_friends, null)};

                                shareUtils = new UMShareUtils(LiveIntroActivity.this);
                                shareUtils.shareCustom(uri, title, desc, bean.getShareUrl(),
                                        actions, new ShareDialog.OnShareLisntener() {
                                            @Override
                                            public void onShare(String key, SHARE_MEDIA media) {
                                                switch (key) {
                                                    //这里跳去邀请卡
                                                    case ShareDialog.ACTION_INVITE:
                                                        Intent intent = new Intent(
                                                                LiveIntroActivity.this,
                                                                ShareImgActivity.class);
                                                        intent.putExtra("chatInfoId", bean.getId());
                                                        startActivity(intent);
                                                        break;

                                                    //分享到动态
                                                    case ShareDialog.ACTION_CIRCLE:
                                                        IssueDynamicActivity.share(
                                                                LiveIntroActivity.this,
                                                                bean.getId(), "2",
                                                                bean.getSubtitle(),
                                                                bean.getHeadPic());
                                                        break;

                                                    //分享到好友
                                                    case ShareDialog.ACTION_FRIENDS:
                                                        ConversationListActivity.startActivity(
                                                                LiveIntroActivity.this,
                                                                ConversationActivity.REQUEST_SHARE_CONTENT,
                                                                Constant.SELECT_TYPE_SHARE);
                                                        break;

                                                    //收藏
                                                    case ShareDialog.ACTION_COLLECT:
                                                        mPresenter.collect(bean.getId());
                                                        break;

                                                    //二维码
                                                    case ShareDialog.ACTION_QRCODE:
                                                        ErWeiCodeActivity.startActivity(
                                                                LiveIntroActivity.this,
                                                                ErWeiCodeActivity.TYPE_CLASSROOM,
                                                                Integer.valueOf(bean.getId()), "");
                                                        break;
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onPermissionDenied() {
                                mAlertDialog = DialogUtil.showDeportDialog(LiveIntroActivity.this,
                                        false, null, getString(R.string.pre_scan_notice_msg),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == R.id.tv_dialog_confirm) {
                                                    JumpPermissionManagement.GoToSetting(
                                                            LiveIntroActivity.this);
                                                }
                                                mAlertDialog.dismiss();
                                            }
                                        });
                            }
                        });
                return true;

            case android.R.id.home:
                if (isDataChange) {
                    setResult(RESULT_OK, getIntent().putExtra("bean", bean));
                }
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showChatInfoData(ChatInfosBean data) {
        if (mOwnerName == null) return;
        bean = data;
        title = data.getSubtitle();
        ImageHelper.loadCircle(this, mFollowHead, data.getHeadPic());
        ImageHelper.loadCircle(this, mFollowHead2, data.getHeadPic());

        if (!TextUtils.isEmpty(data.getVideoPic()) && !TextUtils.isEmpty(data.getVideoText())) {
            epLiveVideo.setUp(bean.getVideoText(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, "",
                    bean.getVideoPic());
            epLiveVideo.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageHelper.loadImage(this, epLiveVideo.thumbImageView, bean.getVideoPic());
            setVideoSize(data.getVideoPic());
            epLiveVideo.setVisibility(View.VISIBLE);
        }
        mTvTitle.setText(title);
        mToolbarLayout.setTitle("");
        mToolbar.setTitle("");
        tvTitleBottom.setText(title);
        mChatRoomName.setText(data.getChatRoomName());

        if (UserManager.getInstance().isLogin()) {
            if (UserManager.getInstance().getUserCode().equals(bean.getUserCode())) {
                mAttentionCheckBox.setVisibility(View.GONE);
                mOwnerName.setText("查看我的课程");

            } else {
                mAttentionCheckBox.setVisibility(View.VISIBLE);
                mOwnerName.setText("查看他的课程");
            }
        }
        if (bean.isSelff()) {
            ownerModel();
        } else {
            //设置  观众模式的按钮
            if (bean.isSingUp()) {      // TODO: 18/5/31
                audienceModelSimple();

            //判断课程是否下架
            } else if ("2".equals(bean.getShowinfo())) {
                showOverdueModel();
            } else {
                //是否试听
                if (!isAuditions) {
                    switch (bean.getChattype()) {
                        //公开
                        case "0":
                            audienceModelSignup();
                            break;

                        //加密
                        case "1":
                            audienceModelPassworld();
                            break;

                        //收费
                        case "2":
                            audienceModelFree();
                            break;
                    }
                } else {
                    freeAuditions();
                }

                //开启了免费邀请制度 但是未达成邀请要求
                if (seriesBean != null && SeriesActivity.AUDITION_INVITE_TYPE.equals(
                        seriesBean.getIsAuditions()) && 0 == seriesBean.getJoinType()) {

                    if (isAuditions) {
                        freeAuditions();
                    } else {
                        //判断是否完成任务
                        if (seriesBean.isFinishInvite()) {
                            audienceModelSimple();
                        } else {
                            noFinishInvite();
                        }
                    }
                }

                //如果是收费课堂，课堂属于圈子，并且该成员是圈子里面的，就免费入场
                circleModel();

                //免费邀请
                freeInviteModel();
            }

        }
        payShareSetting();


        if (data.isFree()) {
            mTvPriceContent.setText("免费");
        } else {
            String temp = StringUtil.formatMoney(2, bean.getFee()) + "元";
            mTvPriceContent.setText(temp);
        }

        if (!TextUtils.isEmpty(data.getEndtime())) {
            mTvStarttime.setText("已开始");
            mTvTimeRemind.setVisibility(View.GONE);

        } else {
            Long aLong = Long.valueOf(data.getStarttime());
            mTvStarttime.setText(DateUtil.formatTime(aLong, "MM-dd HH:mm"));
            long l = aLong - System.currentTimeMillis();
            if (l > 0) {
                String[] strings = DateUtil.countDownNotAddZero(l);
                if (!strings[0].equals("0")) {
                    mTvTimeRemind.setText(strings[0] + "天");

                } else if (!strings[1].equals("0")) {
                    mTvTimeRemind.setText(strings[1] + "小时");

                } else if (!strings[2].equals("0")) {

                } else {
                    mTvTimeRemind.setText("进行中");
                }
            }
        }

        setShareUsers(data);
        setSignUpUsers(data);
        isShowShareModel(bean.isShowShare());
        isShowJoinCountModel(bean.isShowSignUp());
//        mLayoutJoinArea.setEnabled(bean.isSelff() ? true : false);
//        if (bean.isSelff())
        mLayoutJoinArea.setOnClickListener(this);
        if (TextUtils.isEmpty(data.getSpeakerIntroduce())) {
            mTvLecturerName.setVisibility(View.GONE);
        } else {
            mTvLecturerName.setVisibility(View.VISIBLE);
            mTvLecturerName.setText(data.getSpeakerIntroduce());
        }
        mTvJoinCount.setText(data.getJoinCount() + "人次");
        mTvEmceeName.setText(data.getMainSpeaker());
//        imgSignMore.setVisibility(bean.isSelff() ? View.VISIBLE : View.INVISIBLE);

        if (TextUtils.isEmpty(bean.getDesc())) {
            mWvLiveDesc.setVisibility(View.GONE);
            mIvDescZk.setVisibility(View.GONE);

        } else {
            mWvLiveDesc.setVisibility(View.VISIBLE);
            mWvLiveDesc.getSettings().setDefaultTextEncodingName("UTF-8");
            mWvLiveDesc.setWebChromeClient(new WebChromeClient());
            mWvLiveDesc.setWebViewClient(new MyWebViewClient(this));
            mWvLiveDesc.addJavascriptInterface(this, "ShowImg");
            mWvLiveDesc.setWebViewClient(new MyWebViewClient(this));
            mWvLiveDesc.loadData(data.getDesc(), "text/html; charset=UTF-8", null);
        }

        if (!TextUtils.isEmpty(data.getChatInfoCount()) && !TextUtils.isEmpty(
                data.getChatSeriesCount())) {
            //两个值都不为空
            mTvTopicCount.setText(Integer.parseInt(data.getChatInfoCount()) + Integer.parseInt(
                    data.getChatSeriesCount()) + "");
        } else {
            //两个值都其中之一为空
            if (TextUtils.isEmpty(data.getChatInfoCount())) {
                mTvTopicCount.setText(data.getChatSeriesCount());
            } else {
                mTvTopicCount.setText(data.getChatInfoCount());
            }
        }

        mTvAttentionCount.setText(data.getFollowCount());//设置关注人数

        ImageHelper.loadImage(this, mLiveIntroImage, data.getFacePic(),
                R.drawable.live_host_defaual_bg);

        mAttentionCheckBox.setImageResource(data.isFolow() ? R.drawable.live_attention_selected : R.drawable.live_attention_normal);
    }

    @Override
    public void showChatInFoStatusBean(ChatInFoStatusBean statusBean) {
        this.statusBean = statusBean;
    }

    @Override
    public void showEnrollSuccess() {
        bean.setIsSignup("1");
        showChatInfoData(bean);
    }

    @Override
    public void showEnrollSeriesSuccess() {
        bean.setIsSignup("1");
        showChatInfoData(bean);
        EventBusUtil.post(new EventSeriesPayBean());
    }

    @Override
    public void showCollectResult() {
        if ("0".equals(bean.getIsCollect())) {
            bean.setIsCollect("1");
            ToastUtil.showShort(LiveIntroActivity.this, "收藏成功");
        } else {
            bean.setIsCollect("0");
            ToastUtil.showShort(LiveIntroActivity.this, "取消收藏");
        }
    }

    @Override
    public void showFollowSuccess() {
        if (mAttentionCheckBox == null) return;
        mAttentionCheckBox.setImageResource(
                bean.toggleFollow() ? R.drawable.live_attention_selected : R.drawable.live_attention_normal);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(LiveIntroActivity.this, LoginAndRegisteActivity.class);
    }


    @Override
    public void setPresenter(LiveIntroContract.Presenter presenter) {
        this.mPresenter = presenter;
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
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }


    /**
     * 支付 回调
     */
    @Subscribe
    public void onEvent(PayBean bean) {
        //支付失败
        if (bean.isPaySucc) {
            mPresenter.getData(mId);
            EventBusUtil.post(new EventSeriesPayBean());
        } else {
            ToastUtil.showShort(this, "支付失败");
        }
    }


    @Subscribe
    public void onEvent(EventLoginBean bean) {
        if (bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.REGISTE || bean.status == EventLoginBean.THIRDLOGIN) {
            mPresenter.getData(mId);
        }
    }

    @Subscribe
    public void onEvent(EventLiveRefreshBean bean) {
        if (bean.getInfoBean() != null) {
            this.bean = bean.getInfoBean();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITE_REQUESTCODE && resultCode == RESULT_OK) {
            ChatInfosBean bean = (ChatInfosBean) data.getSerializableExtra("bean");

            isDataChange = true;    //数据  改变
            if (bean != null) showChatInfoData(bean);
        }

        if (requestCode == Constant.requestCode.NEWS_LIKEANDCOLLECT) {
            mPresenter.getData(mId);
        }
        if (resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            ToastUtil.showShort(this, "分享成功");
        }

        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            shareMessage(targetId, type, bean.liuyan);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        if (isDataChange) {
            setResult(RESULT_OK, getIntent().putExtra("bean", bean));
        }
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        mPresenter.destroy();
        EventBusUtil.unregister(this);
        ImageHelper.onDestroy(MyApplication.getInstance());
        super.onDestroy();
        if (mWvLiveDesc != null) {
            mWvLiveDesc.setWebChromeClient(null);
            mWvLiveDesc.setWebViewClient(null);
            mWvLiveDesc.removeJavascriptInterface("ShowImg");      //记得移除，避免内存泄漏

            ViewParent parent = mWvLiveDesc.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWvLiveDesc);
            }
            mWvLiveDesc.removeAllViews();
            mWvLiveDesc.destroy();
            mWvLiveDesc = null;
        }
    }


    @JavascriptInterface
    public void getContentWidth(String value) {
        if (value != null) {
        }
    }


    private void clickEnrollClassroom() {
        if (!TextUtils.isEmpty(from)) {
            finish();
            return;
        }

        if (bean == null) {
            Toast.makeText(this, "数据获取失败 请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        //试听部分
        if (isAuditions && seriesBean != null) {
            if (statusBean != null) {
                if (!"1".equals(statusBean.getIsBlack()))
                    LiveConversationActivity.startActivity(this, bean, seriesBean);
                else ToastUtil.showShort(this, "您已被加入黑名单");
            }

        } else {
            //开启了免费邀请制度 但是未达成邀请要求
            if (seriesBean != null && SeriesActivity.AUDITION_INVITE_TYPE.equals(
                    seriesBean.getIsAuditions()) && !seriesBean.isFinishInvite() && 0 == seriesBean.getJoinType()) {

                setResult(999);
                finish();
                return;
            }

            if (bean.getIsForGrop().equals("1") && bean.getJoinGroup().equals("0")) {
                Intent intent = new Intent(LiveIntroActivity.this, CircleJoinActivity.class);
                intent.putExtra("byLiveId", (int) bean.getGroupId());
                startActivityForResult(intent, GOTO_CIRCLE_REQUESTCODE);
                return;
            }

            //之前进去过 现在直接进入课堂
            if (bean.isSingUp() && statusBean != null) {
                gotoLiveRoom();

                //这里需要报名再进入
            } else if (bean.isFree()) {
                mPresenter.enrollClassroom(bean.getId(), null, shareUserCode);

                //这里需要付费才能进入
            } else if (!bean.isFree()) {

                //如果是系列课，返回去购买
                if (!"0".equals(bean.getChatSeries())) {
                    showBuySeries();
                } else {
                    mPresenter.payEnrollClassroom(this, bean, shareUserCode, freeSign);
                }
            }
        }
    }


    private void changeWvLiveDescZK() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                isDescZk ? LinearLayout.LayoutParams.WRAP_CONTENT : getResources().getDimensionPixelOffset(
                        R.dimen.px300dp));
        mWvLiveDesc.setLayoutParams(layoutParams);
        mIvDescZk.setImageResource(
                isDescZk ? R.drawable.live_particulars_icon_more_up : R.drawable.live_particulars_icon_more_downward);
        isDescZk = !isDescZk;
    }


    //免费邀请
    private void freeInviteModel() {
        if (!TextUtils.isEmpty(freeSign) && !bean.isSingUp()) {
            String temp = StringUtil.formatMoney(2, bean.getFee()) + "元";
            mTvPriceContent.setText(temp);
            mTvPriceContent.setTextColor(getResources().getColor(R.color.text_color_999));
            mTvPriceContent.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvOriginalPrice.setText("免费");
            mTvOriginalPrice.setVisibility(View.VISIBLE);
            mAudienceFreeEnter.setText("免费邀请，立即报名");
        }
    }


    /**
     * 自己的模式
     */
    private void ownerModel() {
        mOwnerRedactArea.setVisibility(View.VISIBLE);
        mAudienceSimpeArea.setVisibility(View.GONE);
        mAudiencePasswordModelArea.setVisibility(View.GONE);
        mSignupPersonArea.setEnabled(true);

        //是否设置了平台推广
        if (bean.getSpreadPent() != 0) {
            String text = String.format(Locale.CHINA, tvSpread.getText().toString(),
                    bean.getSpreadPent()) + "%";
            tvSpread.setVisibility(View.VISIBLE);
            tvSpread.setText(text);
        }
    }


    /**
     * 付费分享设置
     */
    private void payShareSetting() {
        if (Double.valueOf(bean.getFee()) > 0 && Integer.valueOf(bean.getPent()) > 0) {
            mPayShare.setVisibility(View.VISIBLE);
            Double  fee   = Double.valueOf(bean.getFee());
            Integer pent  = Integer.valueOf(bean.getPent());
            double  price = fee * pent / 100;
            if (price > 0) {
                String s = "赚￥" + StringUtil.formatMoney(2, price);
                mPayShare.setText(s);
                mPayShare.setOnClickListener(this);

            } else {
                mPayShare.setVisibility(View.GONE);
            }

        } else {
            mPayShare.setVisibility(View.GONE);
        }
    }


    /**
     * 收费
     */
    private void audienceModelFree() {
        audienceModel();
        String title = !"0".equals(bean.getChatSeries()) ? "付费参与系列课" : "付费进入课堂";
        mAudienceFreeEnter.setText(title);
    }

    /**
     * 如果是收费课堂，课堂属于圈子，并且改成员是圈子里面的，就免费入场
     */
    private void circleModel() {
        if ("1".equals(bean.getIsfree()) && "0".equals(bean.getIsSelf()) && "0".equals(
                bean.getIsSignup()) && "1".equals(bean.getJoinGroup())) {

            audienceModel();
            String temp = StringUtil.formatMoney(2, bean.getFee()) + "元";
            mTvPriceContent.setText(temp);
            mTvPriceContent.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvPriceContent.setTextColor(getResources().getColor(R.color.text_color_999));
            mTvOriginalPrice.setText("免费");
            mTvOriginalPrice.setVisibility(View.VISIBLE);
            mAudienceFreeEnter.setText("圈内成员，免费入场");
        }
    }


    /**
     * 公开  还未报名
     */
    private void audienceModelSignup() {
        audienceModel();
        String title = !"0".equals(bean.getChatSeries()) ? "报名参与系列课" : "报名进入课堂";
        mAudienceFreeEnter.setText(title);
    }

    //显示下架状态
    private void showOverdueModel() {
        mAudienceSimpeArea.setOnClickListener(null);
        mAudienceFreeEnter.setBackgroundResource(R.color.greyd1d1d1);
        mAudienceFreeEnter.setTextColor(getResources().getColor(R.color.white));
        mAudienceFreeEnter.setText("已下架");
    }

    /**
     * 免费试听
     */
    private void freeAuditions() {
        audienceModel();
        String title = "免费试听";
        mAudienceFreeEnter.setText(title);
    }

    /**
     * 未完成邀请制免费试听要求
     */
    private void noFinishInvite() {
        audienceModel();
        String title = "邀请好友，解锁课程";
        mAudienceFreeEnter.setText(title);
    }

    private void audienceModelSimple() {
        audienceModel();
        mAudienceFreeEnter.setText("进入课堂");
    }


    /**
     * 观众模式
     */
    private void audienceModel() {
        mOwnerRedactArea.setVisibility(View.GONE);
        mAudienceSimpeArea.setVisibility(View.VISIBLE);
        mAudiencePasswordModelArea.setVisibility(View.GONE);
        mSignupPersonArea.setEnabled(false);
    }


    /**
     * 观众模式  输入密码
     */
    private void audienceModelPassworld() {
        if (TextUtils.isEmpty(freeSign)) {
            mOwnerRedactArea.setVisibility(View.GONE);
            mAudienceSimpeArea.setVisibility(View.GONE);
            mSignupPersonArea.setEnabled(false);
            mAudiencePasswordModelArea.setVisibility(View.VISIBLE);

        } else {
            audienceModelSignup();
        }
    }


    private void setSignUpUsers(ChatInfosBean data) {
        if (mSignupPersonArea != null) {
            mSignupPersonArea.removeAllViews();
        }
        for (SignUpMemberBean bean : data.getSignupUsers())
            mSignupPersonArea.addView(getPersonHead(bean.getHeadPic()));
    }


    private void setShareUsers(ChatInfosBean data) {
        if (mSignupPersonArea != null) {
            mSignupPersonArea.removeAllViews();
        }
        for (PersonCountBean bean : data.getShareUsers())
            mSharePersonArea.addView(getPersonHead(bean.getHeadPic()));

    }


    private void isShowJoinCountModel(boolean isShow) {
        mLayoutJoinModelu_space.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mLayoutJoinArea.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    private void isShowShareModel(boolean isShow) {
        mLayoutShareArea.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mLayoutFollowModuleSpace.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    private AlertDialog buySeriesDialog;

    private void showBuySeries() {
        buySeriesDialog = DialogUtil.showInputDialog(this, false, "", "该课程属于系列课，需要报名系列课才可以进入课堂",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buySeriesDialog.dismiss();
                        mPresenter.enrollSeries(LiveIntroActivity.this, bean, shareUserCode,
                                freeSign);
                    }
                });
    }


    //判断是否在黑名单中
    private void gotoLiveRoom() {
        if (statusBean != null) {
            if (!"1".equals(statusBean.getIsBlack()))
                LiveConversationActivity.startActivity(this, bean, seriesBean);
            else ToastUtil.showShort(this, "您已被加入黑名单");
        }
    }


    /**
     * 密码方式报名
     */
    private void passwordModelSingup() {
        String pwStr = mAudiencePasswordEdit.getText().toString();
        if (pwStr.length() <= 0) {
            ToastUtil.showShort(this, "密码不能为空");
        } else if (bean.getPassword().equals(pwStr)) {
            mPresenter.enrollClassroom(bean.getId(), bean.getJoinGroup(), shareUserCode);
        } else {
            ToastUtil.showShort(this, "密码不正确");
        }

    }

    private void GotoEditPage() {
        Intent intent = new Intent(LiveIntroActivity.this, LiveIntroSettingActivity.class);
        intent.putExtra("bean", bean);
        startActivityForResult(intent, EDITE_REQUESTCODE);
    }


    private ImageView getPersonHead(String uri) {
        int size = getResources().getDimensionPixelOffset(R.dimen.px50dp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        int margin = getResources().getDimensionPixelOffset(R.dimen.margin_tiny);
        layoutParams.setMargins(margin, margin, margin, margin);
        ImageView imageView = new ImageView(LiveIntroActivity.this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(layoutParams);
        ImageHelper.loadCircle(LiveIntroActivity.this, imageView, uri,
                R.drawable.person_icon_head_120);
        return imageView;
    }


    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type,
                              final String liuyan) {
        String title = bean.getSubtitle();
        String img   = bean.getFacePic();
        String id    = bean.getId();
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "2",
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(LiveIntroActivity.this, "分享成功");
                        if (!TextUtils.isEmpty(liuyan)) {
                            RongIMTextUtil.INSTANCE.relayMessage(liuyan, targetId, type);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(LiveIntroActivity.this,
                                "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }


    public static void startActivity(Context activity, String id) {
        Intent intent = new Intent(activity, LiveIntroActivity.class);
        intent.putExtra("id", id);
        activity.startActivity(intent);
    }

    public static void startActivity(Context activity, String id, int flag) {
        Intent intent = new Intent(activity, LiveIntroActivity.class);
        intent.putExtra("id", id);
        intent.addFlags(flag);
        activity.startActivity(intent);
    }


    public static void startActivity(Context activity, String id, String shareUserCode) {
        Intent intent = new Intent(activity, LiveIntroActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("shareUserCode", shareUserCode);
        activity.startActivity(intent);
    }

    public static void startActivity(Context activity, String id, String shareUserCode, int flag) {
        Intent intent = new Intent(activity, LiveIntroActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("shareUserCode", shareUserCode);
        intent.addFlags(flag);
        activity.startActivity(intent);
    }


    //免费邀请报名  freeSign 免费邀请码
    public static void freeInvite(Context context, String id, String shareUserCode,
                                  String freeSign) {
        Intent intent = new Intent(context, LiveIntroActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("shareUserCode", shareUserCode);
        intent.putExtra("freeSign", freeSign);
        context.startActivity(intent);
    }

    // 设置视频的大小，宽适应屏幕，高根据比例缩放
    private void setVideoSize(String url) {
        if (!TextUtils.isEmpty(url)) {
            String temp = url.substring(url.indexOf("?") + 1, url.length());
            if (!TextUtils.isEmpty(temp) && temp.contains("*")) {
                String widthS = url.substring(url.indexOf("?") + 1, url.indexOf("*"));
                String heightS = url.substring(url.indexOf("*") + 1, url.length());
                float                  width        = Float.parseFloat(widthS);
                float                  height       = Float.parseFloat(heightS);
                float                  b            = width / height;
                int                    c            = (int) (WindowUtil.getScreenW(this) / b);
                ViewGroup.LayoutParams layoutParams = epLiveVideo.getLayoutParams();
                layoutParams.height = c;
                epLiveVideo.setLayoutParams(layoutParams);
            }
        }
    }


    @JavascriptInterface
    public void showImg(int position, String[] imageUrl) {
        String         userCode = bean == null ? "0" : bean.getUserCode();
        ArrayList<Uri> uris     = new ArrayList<>();
        for (String s : imageUrl) {
            uris.add(Uri.parse(s));
        }
        CommonPhotoViewActivity.startActivity(LiveIntroActivity.this, uris, position);
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
            view.loadUrl(
                    "javascript:(function(){" + "var objs = document.getElementsByTagName(\"img\"); " + "  var strings = new Array();" + " for (var j = 0; j < objs.length; j++) {" + "strings[j] =  objs[j].src" + "}" + "for(var i=0;i<objs.length;i++) { " + " objs[i].index = i;" + " objs[i].onclick=function()  {" + "        window.ShowImg.showImg(this.index,strings);  " + "    }  " + "}" + "})()");
            if (mWvLiveDesc.getHeight() > getResources().getDimensionPixelOffset(R.dimen.px300dp)) {
                mIvDescZk.setVisibility(View.VISIBLE);
                changeWvLiveDescZK();

            } else {
                mIvDescZk.setVisibility(View.GONE);
            }
        }
    }
}