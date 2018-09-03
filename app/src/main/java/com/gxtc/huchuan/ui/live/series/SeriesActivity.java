package com.gxtc.huchuan.ui.live.series;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SeriesPageAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.event.EventDelSeries;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventSeriesInviteBean;
import com.gxtc.huchuan.bean.event.EventSeriesPayBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.bean.pay.PayBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.SeriesCourseInviteDialog;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.PayApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.live.hostpage.IGetChatinfos;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.hostpage.NewLiveTopicFragment;
import com.gxtc.huchuan.ui.live.intro.ShareImgActivity;
import com.gxtc.huchuan.ui.live.series.count.SeriesSignCountActivity;
import com.gxtc.huchuan.ui.live.series.share.SeriesShareListActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse.CreateSeriesCourseActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic.CreateTopicActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.InvitedGuestsActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
 * 系列课主页
 */
public class SeriesActivity extends BaseTitleActivity implements IGetChatinfos,
        View.OnClickListener {

    public static final String AUDITION_TYPE        = "1";             //免费试听模式
    public static final String AUDITION_INVITE_TYPE = "2";      //邀请制免费试听模式

    private static final String TAG                = "SeriesActivity";
    private static final int    PLAYSERIES_REQUEST = 1 << 2;
    private static final int    SETTING_SERIES     = 1 << 3;


    //<!--headpic  系列课头像-->
    //<!--buycount 购买人数   buyUsers 参与购买人信息列表-->

    @BindView(R.id.iv_head)                     ImageView      mIvHead;//    <!--headpic  系列课头像-->
    @BindView(R.id.tv_title)                    TextView       mTvTitle;//<!--seriesname 系列课名称-->
    @BindView(R.id.tv_price)                    TextView       mTvPrice;//<!--fee  价格-->
    @BindView(R.id.tv_return_livepage)          ImageView      mTvMore;
    @BindView(R.id.layout_count)                View           mLayoutCount;
    @BindView(R.id.tv_free_invite)              TextView       tvFreeInvite;
    @BindView(R.id.tv_count)                    TextView       mTvCount;
    @BindView(R.id.ll_head_area)                LinearLayout   mLlHeadArea;
    @BindView(R.id.tl_series_page_indicator)    TabLayout      mTlSeriesPageIndicator;
    @BindView(R.id.app_bar)                     AppBarLayout   mAppBar;
    @BindView(R.id.vp_series_viewpager)         ViewPager      mVpSeriesViewpager;
    @BindView(R.id.btn_create_topic)            Button         mBtnCreateTopic;
    @BindView(R.id.btn_buy_series)              TextView       mBtnBuySeries;
    @BindView(R.id.ll_audience_area)            LinearLayout   mLlAudienceArea;
    @BindView(R.id.ll_owner_area)               LinearLayout   mLlOwnerArea;
    @BindView(R.id.rl_series_owner_intro)       RelativeLayout mRlSeriesOwnerIntro;
    @BindView(R.id.ll_owner_share_btn)          LinearLayout   mLlOwnerShareBtn;
    @BindView(R.id.ll_owner_setting_btn)        LinearLayout   mLlOwnerSettingBtn;
    @BindView(R.id.ll_share_person_area)        LinearLayout   layoutShare;
    @BindView(R.id.live_intro_follow_head)      ImageView      mLiveIntroImage;
    @BindView(R.id.live_intro_follow_ownername) TextView       tvName;
    @BindView(R.id.live_intro_follow_attention) ImageView      mImageView;
    @BindView(R.id.img_sign_more)               ImageView      imgSignMore;

    private List<Fragment> mFragments = new ArrayList<>();

    private static String[] labTitles = {"详情", "课程"};


    private SeriesIntroFragment     mSeriesIntroFragment;
    private NewLiveTopicFragment    mNewLiveTopicFragment;
    private SeriesPageAdapter       mSeriesPageAdapter;
    private SeriesPageBean          mData;
    private HashMap<String, String> map;
    private UMShareUtils            shareUtils;

    private String      mId;
    private String      shareUserCode;
    private String      freeSign;
    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
    }


    @Override
    public void initView() {
        EventBusUtil.register(this);
        getBaseHeadView().showTitle("系列课主页");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
    }


    @Override
    public void initData() {
        //这里去请求数据  如果请求成功了 才把空的view  yinchangdiao
        labTitles[1] = "课程2";
        getData();
    }


    private void getData() {
        hideContentView();
        getBaseLoadingView().showLoading();

        mId = getIntent().getStringExtra("id");
        freeSign = getIntent().getStringExtra("freeSign");
        //现在app所有的内部分享都不得佣金 除非是微信外部分享  圈子也是这样
        if (!TextUtils.isEmpty(freeSign)) {
            shareUserCode = getIntent().getStringExtra("userCode");
        }
        setdata();
    }

    private void setdata() {
        Subscription sub = LiveApi.getInstance().getChatSeriesInfo(
                UserManager.getInstance().getToken(), mId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<SeriesPageBean>>(new ApiCallBack<SeriesPageBean>() {
                    @Override
                    public void onSuccess(SeriesPageBean data) {
                        if (data != null && getBaseLoadingView() != null) {
                            getBaseLoadingView().hideLoading();
                            showContentView();
                            setdata(data);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (getBaseLoadingView() != null) {
                            getBaseLoadingView().hideLoading();
                            LoginErrorCodeUtil.showHaveTokenError(SeriesActivity.this, errorCode,
                                    message);
                        }
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private void setdata(SeriesPageBean data) {
        mData = data;
        if (mData == null || mTvTitle == null) return;
        imgSignMore.setVisibility(mData.bIsSelf() ? View.GONE : View.VISIBLE);
        ImageHelper.loadRound(this, mIvHead, mData.getHeadpic(), 4);
        mTvTitle.setText(mData.getSeriesname());
        mImageView.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.px5dp), 0);
        mImageView.setImageResource(R.drawable.person_icon_more);
        ImageHelper.loadCircle(this, mLiveIntroImage, data.getAnchorPic(),
                R.drawable.live_host_defaual_bg);
        if (!TextUtils.isEmpty(mData.getUserName())) {
            tvName.setText(mData.getUserName());
        }
        if (Double.valueOf(mData.getFee()) > 0d) {
            mTvPrice.setText(String.format("价格： ￥%.2f", Double.valueOf(mData.getFee())));
            mTvPrice.setTextColor(getResources().getColor(R.color.color_price_ec6b46));

        } else {
            mTvPrice.setText("免费");
            mTvPrice.setTextColor(getResources().getColor(R.color.color_119b1e));
        }
        mBtnCreateTopic.setVisibility(mData.bIsSelf() ? View.VISIBLE : View.GONE);
        tvFreeInvite.setVisibility(mData.bIsSelf() ? View.VISIBLE : View.GONE);
        mTlSeriesPageIndicator.setupWithViewPager(mVpSeriesViewpager);
        labTitles[1] = "课程";  //+ mData.getChatInfos().size();

        mTvCount.setText(mData.getBuyCount() + "人正在参与");
        addPersonHead();

        setModel();
        setBuySeriesBtn();

        mSeriesPageAdapter = new SeriesPageAdapter(getSupportFragmentManager(), getFragments(mData),
                labTitles);
        mVpSeriesViewpager.setAdapter(mSeriesPageAdapter);
        mVpSeriesViewpager.setCurrentItem(1);
    }


    private void addPersonHead() {
        if (mLlHeadArea != null) {
            mLlHeadArea.removeAllViews();
        }

        int cout = mData.getBuyUsers().size() > 5 ? 5 : mData.getBuyUsers().size();
        for (int i = 0; i < cout; i++) {
            mLlHeadArea.addView(getPersonHead(mData.getBuyUsers().get(i).getHeadPic()));
        }


        if (layoutShare != null) {
            layoutShare.removeAllViews();
        }

        if (mData.getUmVoList() != null) {
            cout = mData.getUmVoList().size() > 5 ? 5 : mData.getUmVoList().size();
            for (int i = 0; i < cout; i++) {
                layoutShare.addView(getPersonHead(mData.getUmVoList().get(i).getName()));
            }
        }
    }


    private void setBuySeriesBtn() {
        mBtnBuySeries.setVisibility(mData.bIsSelf() || mData.bIsBuy() ? View.GONE : View.VISIBLE);
        mLlAudienceArea.setVisibility(mData.bIsSelf() || mData.bIsBuy() ? View.GONE : View.VISIBLE);
        if (Double.valueOf(mData.getFee()) > 0d) {
            mBtnBuySeries.setText(String.format("价格： ￥%.2f", Double.valueOf(mData.getFee())));
        } else {
            mBtnBuySeries.setText("报名");
        }

        if (!mData.bIsSelf() && "1".equals(mData.getIsGroupUser()) && !mData.bIsBuy()) {
            mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvPrice.setText(String.format("价格： ￥%.2f", Double.valueOf(mData.getFee())));
            mTvPrice.setTextColor(getResources().getColor(R.color.text_color_999));
            mBtnBuySeries.setText("圈内成员，免费报名");
        }

        if (!TextUtils.isEmpty(freeSign)) {
            mBtnBuySeries.setText("免费邀请，立即报名");
            mData.setFee("0");
        }

        //显示免费邀请制按钮  没达成邀请人数都要显示
        if (!mData.bIsSelf() && AUDITION_INVITE_TYPE.equals(
                mData.getIsAuditions()) && !mData.isFinishInvite()) {
            if (!mData.bIsBuy()) {
                mBtnBuySeries.setText("报名试听");
                mBtnBuySeries.setVisibility(View.VISIBLE);
                mLlAudienceArea.setVisibility(View.VISIBLE);
            }
        }

    }

    private boolean isExamine(List<ChatInfosBean> list) {
        for (ChatInfosBean bean : list) {
            if (bean.getAudit().equals("1")) return true;
        }
        return false;
    }

    private boolean isCanShare() {
        List<ChatInfosBean> list = mNewLiveTopicFragment.mLiveTopicAdapter.getList();
        if (list == null || list.size() == 0) {
            ToastUtil.showLong(this, "没创建子课程的系列课不能分享");
            return false;
        } else if (!isExamine(list)) {
            ToastUtil.showLong(this, "子课程审核通过后才能分享");
            return false;
        }
        return true;
    }


    @Override
    public void initListener() {
        mTvMore.setOnClickListener(this);
        tvFreeInvite.setOnClickListener(this);
        mBtnCreateTopic.setOnClickListener(this);
    }


    public List<Fragment> getFragments(SeriesPageBean o) {
        mFragments.clear();
        mSeriesIntroFragment = new SeriesIntroFragment();
        mNewLiveTopicFragment = new NewLiveTopicFragment();

        Bundle bundleChatInfos = new Bundle();
        bundleChatInfos.putString("chatRoom", o.getChatRoom());
        bundleChatInfos.putBoolean("series", true);
        bundleChatInfos.putString("chatSeries", mId);
        bundleChatInfos.putString("shareUserCode", shareUserCode);
        bundleChatInfos.putString("freeSign", freeSign);
        bundleChatInfos.putString("isAuditions", mData.getIsAuditions());
        bundleChatInfos.putString("isBuy", mData.getIsBuy());
        bundleChatInfos.putDouble("seriesFree", Double.valueOf(mData.getFee()));
        bundleChatInfos.putSerializable("seriesBean", mData);
        mNewLiveTopicFragment.setArguments(bundleChatInfos);


        Bundle bundle = new Bundle();

        bundle.putString("introduce", o.getIntroduce());
        mSeriesIntroFragment.setArguments(bundle);
        mFragments.add(mSeriesIntroFragment);
        mFragments.add(mNewLiveTopicFragment);
        return mFragments;
    }

    @Override
    protected void onDestroy() {
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
        ImageHelper.onDestroy(MyApplication.getInstance());
        mAlertDialog = null;
        super.onDestroy();
    }


    @OnClick({R.id.btn_create_topic, R.id.btn_buy_series, R.id.layout_count, R.id.ll_owner_setting_btn, R.id.ll_owner_share_btn, R.id.rl_series_owner_intro, R.id.layout_follow, R.id.layout_share, R.id.tv_free_invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_series_owner_intro:
//                finish();
                break;

            case R.id.layout_count:
                //只有自己的系列课才可以点击查看报名的成员
                if (UserManager.getInstance().isLogin(this)) {
                    if (mData.bIsSelf()) {
                        SeriesSignCountActivity.jumpToSeriesSignCountActivity(this, mData.getId(),
                                mData.getJoinType() + "");
                    }
                }
                break;

            case R.id.btn_create_topic:
                if (!UserManager.getInstance().isLogin()) {
                    GotoUtil.goToActivity(SeriesActivity.this, LoginAndRegisteActivity.class);
                    return;
                }
                //新建课程
                map = new HashMap<>();
                map.put("chatSeries", mData.getId());
                map.put("chatInfoTypeId", mData.getChatInfoTypeId());
                GotoUtil.goToActivityWithData(this, CreateTopicActivity.class, map);
                break;

            case R.id.btn_buy_series:
                if (!UserManager.getInstance().isLogin()) {
                    GotoUtil.goToActivity(SeriesActivity.this, LoginAndRegisteActivity.class);
                    return;
                }

                //这里是已经成功报名，但是还未完成任务, 点击显示弹窗
                if (AUDITION_INVITE_TYPE.equals(
                        mData.getIsAuditions()) && mData.bIsBuy() && !mData.isFinishInvite()) {
                    showInviteModel();
                    return;
                }

                if ("0".equals(mData.getFee())) {
                    playSeries();
                    return;
                }

                //圈内成员免费报名
                if ("1".equals(mData.getIsGroupUser())) {
                    saveFreeChatSeries();
                    return;
                }

                playSeries();
                break;

            case R.id.headBackButton:
                finish();
                break;

            case R.id.tv_free_invite:
                if (!isCanShare()) return;
                InvitedGuestsActivity.startActivity(SeriesActivity.this, mData.getId(), "4",
                        mData.getSeriesname(), mData.getHeadpic());
                break;

            case R.id.layout_follow:
                if (mData != null) {
                    LiveHostPageActivity.startActivity(this, "1", mData.getChatRoom());
                }
                break;


            case R.id.ll_owner_setting_btn:
                //设置  把当前对象传递到下一个页面
                if (mData != null) {
                    Intent intent = new Intent(this, CreateSeriesCourseActivity.class);
                    intent.putExtra("bean", mData);
                    startActivityForResult(intent, SETTING_SERIES);
                    //GotoUtil.goToActivityWithBean(SeriesActivity.this,CreateSeriesCourseActivity.class,mData);
                }
                break;

            //分享
            case R.id.HeadRightImageButton:
            case R.id.ll_owner_share_btn:
                if (!UserManager.getInstance().isLogin(this)) return;

                if (!isCanShare()) return;
                if (mData != null) {
                    String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                            new PermissionsResultListener() {
                                @Override
                                public void onPermissionGranted() {
                                    final String uri = TextUtils.isEmpty(
                                            mData.getHeadpic()) ? Constant.DEFUAL_SHARE_IMAGE_URI : mData.getHeadpic();

                                    ShareDialog.Action[] actions = {new ShareDialog.Action(
                                            ShareDialog.ACTION_INVITE,
                                            R.drawable.share_icon_invitation,
                                            null), new ShareDialog.Action(ShareDialog.ACTION_QRCODE,
                                            R.drawable.share_icon_erweima,
                                            null), new ShareDialog.Action(
                                            ShareDialog.ACTION_COLLECT,
                                            R.drawable.share_icon_collect,
                                            null), new ShareDialog.Action(ShareDialog.ACTION_COPY,
                                            R.drawable.share_icon_copy,
                                            null), new ShareDialog.Action(ShareDialog.ACTION_CIRCLE,
                                            R.drawable.share_icon_my_dynamic,
                                            null), new ShareDialog.Action(
                                            ShareDialog.ACTION_FRIENDS,
                                            R.drawable.share_icon_friends, null)};

                                    shareUtils = new UMShareUtils(SeriesActivity.this);
                                    shareUtils.shareCustom(uri, mData.getSeriesname(),
                                            mData.getIntroduce(), mData.getShareUrl(), actions,
                                            new ShareDialog.OnShareLisntener() {
                                                @Override
                                                public void onShare(@Nullable String key,
                                                                    @Nullable SHARE_MEDIA media) {
                                                    switch (key) {
                                                        //这里跳去邀请卡
                                                        case ShareDialog.ACTION_INVITE:
                                                            ShareImgActivity.startActivity(
                                                                    SeriesActivity.this,
                                                                    mData.getId());
                                                            break;

                                                        //分享到动态
                                                        case ShareDialog.ACTION_CIRCLE:
                                                            IssueDynamicActivity.share(
                                                                    SeriesActivity.this,
                                                                    mData.getId(), "6",
                                                                    mData.getSeriesname(), uri);
                                                            break;

                                                        //分享到好友
                                                        case ShareDialog.ACTION_FRIENDS:
                                                            ConversationListActivity.startActivity(
                                                                    SeriesActivity.this,
                                                                    ConversationActivity.REQUEST_SHARE_CONTENT,
                                                                    Constant.SELECT_TYPE_SHARE);
                                                            break;

                                                        //收藏
                                                        case ShareDialog.ACTION_COLLECT:
                                                            collect(mData.getId());
                                                            break;

                                                        //二维码
                                                        case ShareDialog.ACTION_QRCODE:
                                                            ErWeiCodeActivity.startActivity(
                                                                    SeriesActivity.this,
                                                                    ErWeiCodeActivity.TYPE_SERIES,
                                                                    Integer.valueOf(mData.getId()),
                                                                    "");
                                                            break;
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onPermissionDenied() {
                                    mAlertDialog = DialogUtil.showDeportDialog(SeriesActivity.this,
                                            false, null, getString(R.string.pre_storage_notice_msg),
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                                        JumpPermissionManagement.GoToSetting(
                                                                SeriesActivity.this);
                                                    }
                                                    mAlertDialog.dismiss();
                                                }
                                            });

                                }
                            });
                }
                break;

            //分享达人榜
            case R.id.layout_share:
                if (UserManager.getInstance().isLogin()) {
                    if (!isCanShare()) return;
                    Intent intent = new Intent(SeriesActivity.this, SeriesShareListActivity.class);
                    intent.putExtra(Constant.INTENT_DATA, mData);
                    startActivity(intent);
                } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
                break;
        }
    }

    private SeriesCourseInviteDialog mInviteDialog;

    private void showInviteModel() {
        mInviteDialog = new SeriesCourseInviteDialog(mData);
        mInviteDialog.show(getSupportFragmentManager(),
                SeriesCourseInviteDialog.class.getSimpleName());
    }

    private void saveFreeChatSeries() {
        Subscription sub = LiveApi.getInstance().saveFreeChatSeriesBuy(
                UserManager.getInstance().getToken(), mId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        ToastUtil.showShort(MyApplication.getInstance(), "报名成功");
                        setdata();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void collect(String classId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("bizType", "9");
        map.put("bizId", classId);

        Subscription sub = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mData.getIsCollect() == 0) {
                            mData.setIsCollect(1);
                            ToastUtil.showShort(SeriesActivity.this, "收藏成功");
                        } else {
                            mData.setIsCollect(0);
                            ToastUtil.showShort(SeriesActivity.this, "取消收藏");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(SeriesActivity.this, errorCode,
                                message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private boolean playing = false;

    private void playSeries() {
        if (!TextUtils.isEmpty(freeSign) && !TextUtils.isEmpty(shareUserCode)) {
            freeInviteJoin();
            return;
        }

        String fee = mData.getFee();

        BigDecimal moneyB = new BigDecimal(fee);

        //计算总价
        double     total      = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chatSeriesId", mData.getId());
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

        if ("0".equals(fee)) {
            requestBean.setPayType("ALIPAY");
            HashMap<String, String> map = new HashMap<>();
            map.put("token", requestBean.getToken());
            map.put("transType", requestBean.getTransType());
            map.put("payType", requestBean.getPayType());
            map.put("totalPrice", requestBean.getTotalPrice());
            map.put("extra", requestBean.getExtra());

            getBaseLoadingView().showLoading();
            Subscription sub = PayApi.getInstance().getOrder(map).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<OrdersResultBean>>(
                            new ApiCallBack<OrdersResultBean>() {
                                @Override
                                public void onSuccess(OrdersResultBean data) {
                                    getBaseLoadingView().hideLoading();
                                    Toast.makeText(SeriesActivity.this, "报名成功",
                                            Toast.LENGTH_SHORT).show();
                                    //显示免费邀请制规则弹窗
                                    if (AUDITION_INVITE_TYPE.equals(mData.getIsAuditions())) {
                                        showInviteModel();
                                    }

                                    if (mData != null) {
                                        mData.setIsBuy("1");
                                        setdata(mData);
                                        EventBusUtil.post(new EventSeriesPayBean());
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    getBaseLoadingView().hideLoading();
                                    Toast.makeText(SeriesActivity.this, "报名失败",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }));

            RxTaskHelper.getInstance().addTask(this, sub);

        } else {
            playing = true;
            GotoUtil.goToActivity(this, PayActivity.class, Constant.INTENT_PAY_RESULT, requestBean);
        }
    }

    //免费邀请报名
    private void freeInviteJoin() {
        getBaseLoadingView().showLoading();

        String token      = UserManager.getInstance().getToken();
        String transType  = "CS";
        String payType    = "WX";
        String totalPrice = "0";

        JSONObject jobj = new JSONObject();
        jobj.put("chatSeriesId", mData.getId());
        jobj.put("freeSign", freeSign);
        jobj.put("userCode", shareUserCode);
        jobj.put("joinType", 4);
        String extra = jobj.toString();

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("transType", transType);
        map.put("payType", payType);
        map.put("totalPrice", totalPrice);
        map.put("extra", extra);

        Subscription sub = PayApi.getInstance().getOrder(map).observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<OrdersResultBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mData != null) {
                            Toast.makeText(SeriesActivity.this, "报名成功", Toast.LENGTH_SHORT).show();
                            getBaseLoadingView().hideLoading();

                            mData.setIsBuy("1");
                            setdata(mData);
                            EventBusUtil.post(new EventSeriesPayBean());
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mData != null) {
                            ToastUtil.showShort(SeriesActivity.this, message);
                            getBaseLoadingView().hideLoading();
                        }
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);

    }


    /**
     * 添加头像
     *
     * @param uri
     * @return
     */
    private ImageView getPersonHead(String uri) {
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.px60dp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimensionPixelOffset,
                dimensionPixelOffset);
        int margin = getResources().getDimensionPixelOffset(R.dimen.margin_small);
        layoutParams.setMargins(margin, margin, margin, margin);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        ImageHelper.loadCircle(this, imageView, uri, R.drawable.person_icon_head_120);
        return imageView;
    }


    private void setModel() {
        mLlOwnerArea.setVisibility(mData.bIsSelf() ? View.VISIBLE : View.GONE);
    }


    @Subscribe
    public void onEvent(EventSeriesInviteBean bean) {
        showInviteModel();
    }

    @Subscribe
    public void onEvent(CreateLiveTopicBean bean) {
        this.getData();
    }

    @Subscribe
    public void SeriesPayEvent(EventSeriesPayBean bean) {
        if (bean.flag) {
            getData();
        }
    }

    /**
     * 支付回调
     *
     * @param bean
     */
    @Subscribe
    public void onEvent(PayBean bean) {
        if (!playing) return;
        if (!bean.isPaySucc) {//支付失败
            ToastUtil.showShort(this, "支付失败");
        } else {
            getData();
        }
        playing = false;
    }

    /**
     * 更新修改系列课后的内容
     */
    @Subscribe
    public void onEvent(SeriesPageBean bean) {
        getData();
    }


    @Subscribe
    public void onEvent(EventDelSeries bean) {
        this.finish();
    }


    @Subscribe
    public void onEvent(EventLoginBean bean) {
        if (bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.REGISTE || bean.status == EventLoginBean.THIRDLOGIN) {
            getData();
        }
    }


    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type,
                              final String liuyan) {
        String title = mData.getSeriesname();
        String img = TextUtils.isEmpty(
                mData.getHeadpic()) ? Constant.DEFUAL_SHARE_IMAGE_URI : mData.getHeadpic();
        String id = mData.getId();
        ImMessageUtils.shareMessage(targetId, type, id, title, img, CircleShareHandler.SHARE_SERIES,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(SeriesActivity.this, "分享成功");
                        if (!TextUtils.isEmpty(liuyan)) {
                            RongIMTextUtil.INSTANCE.relayMessage(liuyan, targetId, type);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(SeriesActivity.this,
                                "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAYSERIES_REQUEST && resultCode == RESULT_OK) {
            if (mData != null) {
                mData.setIsBuy("1");
                setdata(mData);
                EventBusUtil.post(new EventSeriesPayBean());
            }
        } else if (requestCode == SETTING_SERIES && resultCode == RESULT_OK) {
            setdata((SeriesPageBean) data.getSerializableExtra("seriesBean"));
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

        if (requestCode == 666 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
            mNewLiveTopicFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == NewLiveTopicFragment.REQUEST_TOPIC_INTRO && resultCode == 999) {
            showInviteModel();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public List<ChatInfosBean> getChatinfosList() {
        if (mData != null) {
            return mData.getChatInfos();
        }
        return null;
    }


    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity context, String id, int recode) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra("id", id);
        context.startActivityForResult(intent, recode);
    }

    //免费邀请报名  freeSign 免费邀请码
    public static void freeInvite(Context context, String id, String shareUserCode,
                                  String freeSign) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("userCode", shareUserCode);
        intent.putExtra("freeSign", freeSign);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String id, String userCode) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String id, boolean isByLiveFragment) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isByLiveFragment", isByLiveFragment);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String id, boolean isByLiveFragment,
                                     int flag) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isByLiveFragment", isByLiveFragment);
        intent.addFlags(flag);
        context.startActivity(intent);
    }

}
