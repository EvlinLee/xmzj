package com.gxtc.huchuan.ui.mine.personalhomepage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.RomUtils;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PersonalHomePageTopAdapter;
import com.gxtc.huchuan.adapter.PersonalPageTabAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.bean.event.EventImgBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.im.postcard.PTMessage;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.Deal.PersonDealFragment;
import com.gxtc.huchuan.ui.mine.personalhomepage.article.ArticleFragment;
import com.gxtc.huchuan.ui.mine.personalhomepage.classroom.ClassRoomFragment;
import com.gxtc.huchuan.ui.mine.personalhomepage.dynamic.DynamicFragment;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.RelayHelper;
import com.gxtc.huchuan.utils.TextLineUtile;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CircleImageView;
import com.gxtc.huchuan.widget.OtherGridView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

import static android.os.Build.VERSION_CODES.KITKAT;
import static com.gxtc.huchuan.im.ui.ConversationActivity.REQUEST_SHARE_RELAY;

/**
 * Describe:我的 >  个人主页
 * Created by ALing on 2017/3/13.
 */

public class PersonalHomePageActivity extends BaseActivity implements
        PersonalHomeContract.View {

    private static final String TAG = PersonalHomePageActivity.class.getSimpleName();

    @BindView(R.id.iv_avatar)       CircleImageView mIvAvatar;
    @BindView(R.id.rl_show_info)    RelativeLayout  mRlShowInfo;
    @BindView(R.id.cb_focus)        ImageView       mCbFocus;
    @BindView(R.id.tabLayout_main)  TabLayout       mTabLayoutMain;
    @BindView(R.id.vp_personal)     ViewPager       mVpPersonal;
    @BindView(R.id.tv_username)     TextView        mTvUsername;
    @BindView(R.id.tv_introduce)    TextView        mTvIntroduce;
    @BindView(R.id.tv_focus)        TextView        mTvFocus;
    @BindView(R.id.tv_fans)         TextView        mTvFans;
    @BindView(R.id.otherGridView)   OtherGridView   otherGridView;
    @BindView(R.id.tv_all_circle)   TextView        mTvAllCircle;
    @BindView(R.id.ll_focus_chat)   LinearLayout    llFocusChat;
    @BindView(R.id.tv_label_circle) TextView        mTvLabelCircle;
    @BindView(R.id.iv_mediaLevel)   ImageView       mIvMediaLevel;

    @BindView(R.id.iv_share)             ImageView      ivShare;
    @BindView(R.id.iv_share_white)       ImageView      ivShareWhite;
    @BindView(R.id.toolbar)              Toolbar        toolbar;
    @BindView(R.id.app_bar)              AppBarLayout   appBarLayout;
    @BindView(R.id.tv_title)             TextView       tvTitle;
    @BindView(R.id.rl_content_top)       RelativeLayout rlContentTop;
    @BindView(R.id.iv_back)              ImageView      ivBack;
    @BindView(R.id.iv_back_white)        ImageView      ivBackWhite;
    @BindView(R.id.tv_toolbar_focus_top) TextView       mTvToolBarFocus;

    private UMShareUtils   shareUtils;
    private List<Fragment> fragments;

    private String userCode;

    private PersonalHomeContract.Presenter mPresenter;

    private String                         token;
    private String                         isFollow;
    private Intent                         intent;
    private PersonalHomePageTopAdapter     adapter;
    private User                           mUser;
    private float                          headerBgHeight;
    private AlertDialog mAlertDialog;
    private DynamicFragment dynamicFragment;

    @Override
    public boolean isLightStatusBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_personal_homepage);
        ButterKnife.bind(this);
    }

    @Override
    public void initListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percen = Math.abs(verticalOffset / headerBgHeight);
                mTvToolBarFocus.setEnabled(true);
                mTvToolBarFocus.setAlpha(percen);
                tvTitle.setAlpha(percen);
                ivBack.setAlpha(percen);
                ivShare.setAlpha(percen);
                ivBackWhite.setAlpha(1f - percen);
                ivShareWhite.setAlpha(1f - percen);
            }
        });
    }

    @Override
    public void initData() {
        switch (RomUtils.getLightStatausBarAvailableRomType()) {
            case RomUtils.AvailableRomType.MIUI:
            case RomUtils.AvailableRomType.FLYME:
            case RomUtils.AvailableRomType.ANDROID_NATIVE://6.0以上
                setActionBarTopPadding(toolbar, true);
                break;

            case RomUtils.AvailableRomType.NA://6.0以下
                //这里也要设置一下顶部标题栏的padding，否则会重叠的
                if(Build.VERSION.SDK_INT >= KITKAT){
                    setActionBarTopPadding(toolbar, true);
                }
                break;
        }


        headerBgHeight = getResources().getDimension(R.dimen.px500dp) - getResources().getDimension(
                R.dimen.head_view_hight);

        new PersonalHomePresenter(this);
        mTabLayoutMain.setupWithViewPager(mVpPersonal);
        token = UserManager.getInstance().getToken();

        userCode = getIntent().getStringExtra("userCode");
        if (userCode != null) {
            mTvToolBarFocus.setVisibility(View.VISIBLE);
            llFocusChat.setVisibility(View.VISIBLE);
            mTvLabelCircle.setText(getString(R.string.title_his_circle));
            mCbFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.setUserFocus(token, "3", userCode);
                }
            });
            mTvToolBarFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.setUserFocus(token, "3", userCode);
                }
            });
        }
        if(!TextUtils.isEmpty(userCode) && userCode.equals(UserManager.getInstance().getUserCode())){
            mTvToolBarFocus.setVisibility(View.GONE);
            llFocusChat.setVisibility(View.GONE);
        }
        getHomePageData();
        initViewPage();
        initGrideView();

    }

    private void initGrideView() {
        if (userCode != null) {
            mPresenter.getCircleListByUserCode(userCode, false);
        } else {
            mPresenter.getCircleListByUserCode(UserManager.getInstance().getUserCode(), false);
        }

    }

    private void getHomePageData() {
        if (userCode != null) {
            mPresenter.getUserMemberByUserCode(userCode, token);

        } else {
            mPresenter.getUserSelfInfo(token);
        }
    }

    private void initViewPage() {
        String[] arrTabTitles = getResources().getStringArray(R.array.personal_homepage_tab);
        fragments = new ArrayList<>();
        //主页
        //PersonalHomeFragment homePageFragment = new PersonalHomeFragment();
        //推荐
//        RecommendFragment recommendFragment = new RecommendFragment();
        dynamicFragment   = new DynamicFragment();
        ArticleFragment   articleFragment   = new ArticleFragment();
        ClassRoomFragment classRoomFragment = new ClassRoomFragment();
        PersonDealFragment mPersonDealFragment = new PersonDealFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constant.INTENT_DATA,userCode);
        mPersonDealFragment.setArguments(bundle);

        fragments.add(dynamicFragment);
        fragments.add(articleFragment);
        fragments.add(classRoomFragment);
        fragments.add(mPersonDealFragment);

        mVpPersonal.setAdapter(new PersonalPageTabAdapter(getSupportFragmentManager(), fragments, arrTabTitles,userCode));
    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.tv_chat, R.id.tv_all_circle, R.id.tv_toolbar_focus_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_share:
                share();
                break;

            case R.id.tv_all_circle:
                HisCircleActivity.startActivity(this, userCode);
                break;

            //聊天
            case R.id.tv_chat:
                finish();
                break;
        }
    }

    //分享
    private void share() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        if(mUser == null) return;
                        shareUtils = new UMShareUtils(PersonalHomePageActivity.this);
                        String uri = TextUtils.isEmpty(mUser.getHeadPic()) ? Constant.DEFUAL_SHARE_IMAGE_URI : mUser.getHeadPic();
                        shareUtils.sharePersonalHome(uri, TextUtils.isEmpty(mUser.getSelfMediaName()) ? mUser.getName() : mUser.getSelfMediaName(), mUser.getIntroduction(), mUser.getShareUrl());
                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(int flag) {
                                //发送名片
                                if(flag == 0){
                                    ConversationListActivity.startActivity(PersonalHomePageActivity.this, ConversationActivity.REQUEST_SHARE_CARD,Constant.SELECT_TYPE_CARD);
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(PersonalHomePageActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(PersonalHomePageActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(PersonalHomePageActivity.this, false, null, getString(R.string.token_overdue),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.tv_dialog_confirm){
                            ReLoginUtil.ReloginTodo(PersonalHomePageActivity.this);
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }


    @Override
    public void showHomePageSelfList(List<PersonalHomeDataBean> list) {
    }


    @Override
    public void showHomePageUserList(List<PersonalHomeDataBean> list) {

    }

    @Override
    public void showCircleByUserList(List<CircleBean> list) {
        adapter = new PersonalHomePageTopAdapter(PersonalHomePageActivity.this, list,
                R.layout.item_personalpage_top);
        otherGridView.setAdapter(adapter);
        otherGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CircleBean bean = (CircleBean) adapter.getItem(position);
                GotoUtil.goToActivity(PersonalHomePageActivity.this, CircleMainActivity.class, 0, bean);
            }
        });
    }

    @Override
    public void showCircleByUserCodeList(List<CircleBean> list) {
        adapter = new PersonalHomePageTopAdapter(PersonalHomePageActivity.this, list,
                R.layout.item_personalpage_top);
        otherGridView.setAdapter(adapter);
        otherGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CircleBean bean     = adapter.getDatas().get(position);
                String     name     = bean.getGroupName();
                String     joinUrl  = bean.getJoinUrl();
                double     money    = bean.getFee();
                int        circleId = bean.getId();
                int        isJoin   = bean.getIsJoin();   //是否已经加入    0：未加入。1：已加入

                if (0 == isJoin) {
                    Intent intent = new Intent(PersonalHomePageActivity.this, CircleJoinActivity.class);
                    intent.putExtra("url", joinUrl);
                    intent.putExtra("id", circleId);
                    intent.putExtra("name", name);
                    intent.putExtra("isAudit", bean.getIsAudit());
                    intent.putExtra(Constant.INTENT_DATA, money);
                    startActivityForResult(intent, 0);
                } else {
                    GotoUtil.goToActivity(PersonalHomePageActivity.this, CircleMainActivity.class, 0, bean);
                }
            }
        });
    }

    @Override
    public void showRefreshFinish(List<CircleBean> datas) {
    }

    @Override
    public void showLoadMoreList(List<CircleBean> list) {
    }

    @Override
    public void showSelfData(User user) {
        mUser = user;

        ImageHelper.loadHeadIcon(this, mIvAvatar, R.drawable.person_icon_head_120,
                user.getHeadPic());

        ImageHelper.loadImage(this, mIvMediaLevel, user.getSelfMediaLevelPicUrl(),
                R.drawable.person_grade_icon_lv1);

        if (!TextUtils.isEmpty(user.getSelfMediaName())) {
            mTvUsername.setText(user.getSelfMediaName());
        } else {
            mTvUsername.setText(user.getName());
        }

        if (user.getIntroduction().toString().trim().length() != 0) {
            mTvIntroduce.setText("简介:" + user.getIntroduction());
        } else {
            mTvIntroduce.setText(getString(R.string.label_introduct));
        }
        mTvFocus.setText("关注" + user.getFollowCount());
        mTvFans.setText("粉丝" + user.getFsCount());
    }

    @Override
    public void showMenberData(User user) {
        mUser = user;
        isFollow = user.getIsFollow();
        if (userCode != null) {
            Log.d(TAG, "initData: " + userCode);
            mCbFocus.setImageResource("0".equals(
                    user.getIsFollow()) ? R.drawable.live_attention_normal_white : R.drawable.live_topic_attention_selected_white);
            mTvToolBarFocus.setText("0".equals(user.getIsFollow()) ? "关注" : "已关注");
        }


        ImageHelper.loadHeadIcon(this, mIvAvatar, R.drawable.person_icon_head_120,
                user.getHeadPic());
        ImageHelper.loadImage(this, mIvMediaLevel, user.getSelfMediaLevelPicUrl(),
                R.drawable.person_grade_icon_lv1);

        if (!TextUtils.isEmpty(user.getSelfMediaName())) {
            mTvUsername.setText(user.getSelfMediaName());
        } else {
            mTvUsername.setText(user.getName());
        }
        if (user.getIntroduction().toString().trim().length() != 0) {
            mTvIntroduce.setText("简介:" + user.getIntroduction());
        } else {
            mTvIntroduce.setText(getString(R.string.label_introduct));
        }
        mTvFocus.setText("关注：" + user.getFollowCount());
        mTvFans.setText("粉丝：" + user.getFsCount());
    }

    @Override
    public void showUserFocus(Object object) {
        if ("0".equals(isFollow)) {
            isFollow = "1";
            intent = new Intent();
            setResult(Constant.ResponseCode.FOCUS_RESULT_CODE, intent);
            mCbFocus.setImageResource(R.drawable.live_topic_attention_selected_white);
            mTvToolBarFocus.setText("已关注");
            new EventBusUtil().post(new EventFocusBean(true));
        } else {
            isFollow = "0";
            intent = new Intent();
            setResult(Constant.ResponseCode.FOCUS_RESULT_CODE, intent);
            mCbFocus.setImageResource(R.drawable.live_attention_normal_white);
            mTvToolBarFocus.setText("关注");
            new EventBusUtil().post(new EventFocusBean(true));
        }
    }

    @Override
    public void showRecommendList(List<PersonalHomeDataBean> list) {
    }

    @Override
    public void showNoMore() {
    }

    @Override
    public void setPresenter(PersonalHomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {
    }

    @Override
    public void showEmpty() {
        mTvAllCircle.setVisibility(View.GONE);
        mTvLabelCircle.setText("还没加入任何圈子");
    }

    @Override
    public void showReLoad() {
        getHomePageData();
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
    }

    //修改圈子封面图
    @Subscribe(sticky = true)
    public void onEvent(EventImgBean bean) {
        initGrideView();
    }

    //分享名片
    private void sharePostCard(EventSelectFriendForPostCardBean bean){
        if(mUser == null)   return;

        PTMessage mPTMessage = PTMessage.obtain(mUser.getUserCode(), mUser.getName(), mUser.getHeadPic());
        String    title      = mPTMessage.getName();
        String    img        = mPTMessage.getHeadPic();
        String    id         = mPTMessage.getUserCode();
        ImMessageUtils.sentPost(bean.targetId,bean.mType, id, title, img, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(MyApplication.getInstance(),"发送名片成功");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(MyApplication.getInstance(),"发送名片失败: " + message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ConversationActivity.REQUEST_SHARE_CARD && resultCode == RESULT_OK){
            EventSelectFriendForPostCardBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            sharePostCard(bean);
        }

        //分享视频
        if(requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }

        //转发消息
        if(requestCode == REQUEST_SHARE_RELAY && resultCode == RESULT_OK){
            dynamicFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (!JZVideoPlayer.backPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mAlertDialog = null;
        TextLineUtile.clearTextLineCache();
        EventBusUtil.unregister(this);
    }

    public static void startActivity(Context context, String userCode) {
        Intent intent = new Intent(context, PersonalHomePageActivity.class);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }
}