package com.gxtc.huchuan.ui.live.hostpage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveHostPageAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CreateLiveBean;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.MessageBean;
import com.gxtc.huchuan.bean.event.EventCollectBean;
import com.gxtc.huchuan.bean.event.EventDelSeries;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.event.PlayEvent;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.live.conversation.LiveConversationActivity;
import com.gxtc.huchuan.ui.live.create.CreateLiveActivity;
import com.gxtc.huchuan.ui.live.hostpage.newhostpage.LiveTopicAndSeriseFragment;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.message.GroupInfoContract;
import com.gxtc.huchuan.ui.message.GroupInfoPresenter;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse.CreateSeriesCourseActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic.CreateTopicActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.LiveBgSettingActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity.REQUEST_CODE_AVATAR;

/**
 * 课堂主页
 */
public class LiveHostPageActivity extends BaseTitleActivity implements IGetChatinfos,
        View.OnClickListener, GroupInfoContract.View, PictureConfig.OnSelectResultCallback {

    @BindView(R.id.live_host_page_image)           ImageView      mLiveHostPageImage;           //背景图片
    @BindView(R.id.live_host_page_image_hit)       ImageView      imgMask;                      //遮罩背景图片
    @BindView(R.id.iv_live_host_page_portrait)     ImageView      mImageView;                   //头像
    @BindView(R.id.tv_live_host_page_owner_title)  TextView       mTvLiveHostPageOwnerTitle;    //标题
    @BindView(R.id.tv_live_host_page_peopel_count) TextView       mTvLiveHostPagePeopelCount;   //关注数
    @BindView(R.id.iv_live_host_managepage_portrait)     ImageView      mManageImageView;                   //头像
    @BindView(R.id.tv_live_host_managepage_owner_title)  TextView       mManagePageOwnerTitle;    //标题
    @BindView(R.id.tv_live_host_page_managepeopel_count) TextView       mManageTvLiveHostPagePeopelCount;   //关注数
    @BindView(R.id.cb_live_host_page_attention)    ImageView      mCheckBox;
    @BindView(R.id.relativelayout)                 RelativeLayout mRelativelayout;
    @BindView(R.id.app_bar)                        AppBarLayout   mAppBar;
    @BindView(R.id.tl_live_host_page_indicator)    TabLayout      mTlLiveHostPageIndicator;
    @BindView(R.id.vp_live_host_page_viewpager)    ViewPager      mVpLiveHostPageViewpager;
    //    WrapContentHeightViewPager mVpLiveHostPageViewpager;
    @BindView(R.id.tv_create_topic)               TextView         btnCreateTopic;
    @BindView(R.id.tv_create_series)              TextView         btnCreateSeries;
    @BindView(R.id.tv_edit)                        TextView         tvedit;
    @BindView(R.id.tv_live_host_page_owner_titleright)   View   view;

    @BindView(R.id.relativelayoutmanage)          RelativeLayout   relativelayoutmanage;





    private String[] titles = {"课程", "介绍"};
    //课程
    private LiveTopicFragment    liveTopicFragment;
    //系列课
    private SeriesCourseFragment seriesCourseFragment;
    //退款列表
    private RefundListFragment   refundListFragment;
    //介绍
    private LiveIntroFragment    liveIntroFragment;
    //课程与系列课
    private LiveTopicAndSeriseFragment  mLiveTopicAndSeriseFragment;
    //课堂id
    private String               liveId;

    //是否是课程管理
    private String              isManage = "1";

    private HashMap<String, String> map;

    LiveRoomBean bean;


    private List<Fragment> mFragments = new ArrayList<>();
    private LiveHostPageAdapter mPageAdapter;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private UMShareUtils shareUtils;
    private ImageButton  mImageButton1;


    private ImageButton mImageButton2;
    private static final int SETTING_REQUESTCODE = 1 << 3;
    private AlertDialog                 mAlertDialog;
    private ArrayList<String>           pathList;
    public  GroupInfoContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_host_page);
        EventBusUtil.register(this);

    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.mine_manage_class));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bean1 = getIntent().putExtra("bean", LiveHostPageActivity.this.bean);

                setResult(RESULT_OK, bean1);
                LiveHostPageActivity.this.finish();
            }
        });
        initHeadViewRightButton();
        mTlLiveHostPageIndicator.setupWithViewPager(mVpLiveHostPageViewpager);
    }

    private void initHeadViewRightButton() {
        View         parentView = getBaseHeadView().getParentView();
        LinearLayout layout     = (LinearLayout) parentView.findViewById(R.id.headRightLinearLayout);

        //分享
        mImageButton1 = getImageButton(R.drawable.navigation_icon_share);
        layout.addView(mImageButton1);
        mImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserManager.getInstance().isLogin(LiveHostPageActivity.this)) return;

                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                if(bean == null) return;
                                ShareDialog.Action[] actions = {new ShareDialog.Action(
                                        ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                                        new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                                        new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null),
                                        new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null),
                                        new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null)};
                                shareUtils = new UMShareUtils(LiveHostPageActivity.this);
                                shareUtils.shareCustom(bean.getBakpic(), bean.getRoomname(),
                                        bean.getIntroduce(), bean.getShareUrl(), actions,
                                        new ShareDialog.OnShareLisntener() {
                                            @Override
                                            public void onShare(String key, SHARE_MEDIA media) {
                                                switch (key) {
                                                    case ShareDialog.ACTION_QRCODE:
                                                        ErWeiCodeActivity.startActivity(
                                                                LiveHostPageActivity.this,
                                                                ErWeiCodeActivity.TYPE_CLASSROOM_PAGE,
                                                                Integer.valueOf(liveId), "");
                                                        break;
                                                    case ShareDialog.ACTION_CIRCLE:
                                                        IssueDynamicActivity.share(LiveHostPageActivity.this,bean.getId(),"8",bean.getRoomname(),bean.getBakpic());
                                                        break;
                                                    case ShareDialog.ACTION_FRIENDS:
                                                        ConversationListActivity.startActivity(LiveHostPageActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT,Constant.SELECT_TYPE_SHARE);
                                                        break;
                                                    case ShareDialog.ACTION_COLLECT:
                                                         collect(bean.getId());
                                                        break;
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onPermissionDenied() {
                                mAlertDialog = DialogUtil.showDeportDialog(
                                        LiveHostPageActivity.this, false, null,
                                        getString(R.string.pre_storage_notice_msg),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == R.id.tv_dialog_confirm) {
                                                    JumpPermissionManagement.GoToSetting(
                                                            LiveHostPageActivity.this);
                                                }
                                                mAlertDialog.dismiss();
                                            }
                                        });
                            }
                        });
            }
        });

        //设置
        mImageButton2 = getImageButton(R.drawable.person_icon_set);
        mImageButton2.setVisibility(View.GONE);
        layout.addView(mImageButton2);

        mImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoUtil.goToActivityForResult(LiveHostPageActivity.this,
                        LiveBgSettingActivity.class, SETTING_REQUESTCODE);
            }
        });
    }

    private void collect(String newsId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("bizType", "12");
        map.put("bizId", newsId);
        Subscription sub = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        switch (bean.getIsCollect()){
                            case "0":
                                ToastUtil.showShort(MyApplication.getInstance(),"收藏成功");
                                bean.setIsCollect("1");
                                break;
                            case "1":
                                ToastUtil.showShort(MyApplication.getInstance(),"取消收藏");
                                bean.setIsCollect("0");
                                break;
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    private ImageButton getImageButton(int resId) {
        ImageButton button = new ImageButton(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        button.setLayoutParams(params);
        button.setPadding(30, 0, 30, 0);
        button.setImageResource(resId);
        int[]      attrs              = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray         = this.obtainStyledAttributes(attrs);
        int        backgroundResource = typedArray.getResourceId(0, 0);
        button.setBackgroundResource(backgroundResource);

        return button;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        liveId = intent.getStringExtra("liveId");
        isManage = getIntent().getStringExtra("isManage");
        new GroupInfoPresenter(this);
        if (TextUtils.isEmpty(liveId)) return;
        getLiveRoomData();

    }

    @Override
    public void initListener() {
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserManager.getInstance().isLogin(LiveHostPageActivity.this)) {
                    return;
                }
                mCompositeSubscription.add(
                        LiveApi.getInstance().setUserFollow(UserManager.getInstance().getToken(),
                                "2", bean.getId()).subscribeOn(Schedulers.io()).observeOn(
                                AndroidSchedulers.mainThread()).subscribe(
                                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        if (mCheckBox == null) return;
                                        mCheckBox.setImageResource(
                                                bean.toggleFollow() ? R.drawable.live_topic_attention_selected : R.drawable.live_topic_attention_normal);
                                    }

                                    @Override
                                    public void onError(String errorCode, String message) {
                                        ToastUtil.showShort(LiveHostPageActivity.this, message);
                                    }
                                })));
            }
        });


    }

    public List<Fragment> getFragments(LiveRoomBean bean) {
        mFragments.clear();
//        liveTopicFragment = new LiveTopicFragment();
        mLiveTopicAndSeriseFragment = new LiveTopicAndSeriseFragment();
//        seriesCourseFragment = new SeriesCourseFragment();
        liveIntroFragment = new LiveIntroFragment();
//        refundListFragment = new RefundListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);


        Bundle                   bundleChatInfos = new Bundle();
        bundleChatInfos.putString("liveId", liveId);


        //介绍只需要传两个字段
        Bundle bundleIntro = new Bundle();
        bundleIntro.putString("introduce", bean.getIntroduce());//课堂简介
        bundleIntro.putString("create_time", bean.getCreattime());//课堂创建时间
        bundleIntro.putSerializable("bean", bean);//课堂创建时间

        //介绍课堂退款列表
//        Bundle bundleClass = new Bundle();
//        bundleClass.putString("type", "1");//课堂简介

//        liveTopicFragment.setArguments(bundleChatInfos);
        mLiveTopicAndSeriseFragment.setArguments(bundleChatInfos);
//        seriesCourseFragment.setArguments(bundle);
        liveIntroFragment.setArguments(bundleIntro);
//        refundListFragment.setArguments(bundleClass);

        mFragments.add(mLiveTopicAndSeriseFragment);
//        mFragments.add(seriesCourseFragment);
        mFragments.add(liveIntroFragment);
        //自己的课堂就是退款列表，别人的还是介绍页
       /* if(!bean.bIsSelf()){
            mFragments.add(liveIntroFragment);
        }else {
            mFragments.add(refundListFragment);
        }*/

        return mFragments;
    }


    /**
     * 获取课堂主页信息
     */
    private void getLiveRoomData() {
        hideContentView();
        getBaseLoadingView().showLoading();

        String token = null;
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
        }

        Subscription sub = LiveApi.getInstance().getLiveRoom(liveId, token).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<LiveRoomBean>>(new ApiCallBack() {

                    @Override
                    public void onSuccess(Object data) {
                        if (getBaseLoadingView() == null) return;
                        showContentView();
                        getBaseLoadingView().hideLoading();

                        bean = (LiveRoomBean) data;
//                        mImageButton2.setVisibility(bean.bIsSelf() ? View.VISIBLE : View.GONE);


                        mCheckBox.setVisibility(bean.bIsSelf() ? View.GONE : View.VISIBLE);
                        mCheckBox.setImageResource(
                                bean.isFolow() ? R.drawable.live_topic_attention_selected : R.drawable.live_topic_attention_normal);
                        //是自己的课堂
                        if (isManage.equals("0")) {

                            getBaseHeadView().showTitle(getString(R.string.mine_manage_class));
                            mRelativelayout.setVisibility(View.GONE);
                            relativelayoutmanage.setVisibility(View.VISIBLE);
                            //头像
                            ImageHelper.loadCircle(MyApplication.getInstance(), mManageImageView,
                                    bean.getHeadpic(), R.mipmap.ic_launcher);
                            //标题
                            mManagePageOwnerTitle.setText(bean.getRoomname());
                            //关注数
                            mManageTvLiveHostPagePeopelCount.setText(bean.getFs() + "人关注");

                        } else {
                            getBaseHeadView().showTitle(getString(R.string.studio_home));
                            int   height  = (int) WindowUtil.getScaleHeight(16, 7.5f,
                                    WindowUtil.getScreenWidth(LiveHostPageActivity.this),
                                    LiveHostPageActivity.this);
                            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mLiveHostPageImage.getLayoutParams();
                            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) imgMask.getLayoutParams();
                            params1.height = height;
                            params2.height = height;
                            mRelativelayout.setVisibility(View.VISIBLE);
                            relativelayoutmanage.setVisibility(View.GONE);
                            //关注数
                            mTvLiveHostPagePeopelCount.setText(bean.getFs() + "人关注");
                            //背景
                            ImageHelper.loadImage(MyApplication.getInstance(), mLiveHostPageImage,
                                    bean.getBakpic());
                            //头像
                            ImageHelper.loadHeadIcon(MyApplication.getInstance(), mImageView, R.mipmap.ic_launcher,
                                    bean.getHeadpic());
                            //课堂号
                            mTvLiveHostPageOwnerTitle.setText(bean.getRoomname());
                        }


                       /* if(bean.bIsSelf()) titles[2] = "退款列表";
                        else titles[2] = "介绍";*/
                        mPageAdapter = new LiveHostPageAdapter(getSupportFragmentManager(),
                                getFragments(bean), titles);
                        mVpLiveHostPageViewpager.setOffscreenPageLimit(mFragments.size());
                        mVpLiveHostPageViewpager.setAdapter(mPageAdapter);

                        int jumpPage = getIntent().getIntExtra("jumpPage", -1);
                        if (jumpPage != -1) {
                            mVpLiveHostPageViewpager.setCurrentItem(jumpPage);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (getBaseLoadingView() == null) return;
                        getBaseLoadingView().hideLoading();
                        LoginErrorCodeUtil.showHaveTokenError(LiveHostPageActivity.this, errorCode,
                                message);
                    }
                }));
        mCompositeSubscription.add(sub);
    }

    @Subscribe
    public void showCurrentPlayTopic(PlayEvent data) {
        switch (data.getStaus()) {
            case PlayEvent.TOGGLE_STATUS://转换
                break;
            case PlayEvent.START_STATUS://播放
                Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                break;
            case PlayEvent.PAUSE_STATUS:
                break;
        }
    }


    @OnClick({R.id.tv_create_topic,
            R.id.ll_create_topic,
            R.id.tv_create_series,
            R.id.ll_create_series,
            R.id.ll_see_order,
            R.id.tv_see_order,
            R.id.iv_live_host_page_portrait,
            R.id.relativelayout,
            R.id.live_host_page_image,
            R.id.tv_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_live_host_page_portrait:
                PersonalInfoActivity.startActivity(LiveHostPageActivity.this, bean.getUserCode());
                break;

            //新建课程
            case R.id.ll_create_topic:
            case R.id.tv_create_topic:
                GotoUtil.goToActivity(this, CreateTopicActivity.class);
                break;

            //新建系列课
            case R.id.ll_create_series:
            case R.id.tv_create_series:
                GotoUtil.goToActivityForResult(this, CreateSeriesCourseActivity.class, 777);
                break;

            //课程订单
            case R.id.ll_see_order:
            case R.id.tv_see_order:

                OrderActivity.startActivity(this, bean.getId());
                break;
            case R.id.tv_edit:
                EditLiveHostIntroduceActivity.jumpToEditLiveHostIntroduceActivity(this,bean);
                break;

//            case R.id.relativelayout:
//                //修改背景
//            case R.id.live_host_page_image:
//                if (bean.bIsSelf()) {
//                    chooseImg();
//                }
//                break;
        }
    }

    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers, REQUEST_CODE_AVATAR,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options =
                                new FunctionOptions.Builder()
                                        .setType(FunctionConfig.TYPE_IMAGE)
                                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                                        .setImageSpanCount(3)
                                        .setEnableQualityCompress(false) //是否启质量压缩
                                        .setEnablePixelCompress(false) //是否启用像素压缩
                                        .setEnablePreview(true) // 是否打开预览选项
                                        .setShowCamera(true)
                                        .setPreviewVideo(true)
                                        .setIsCrop(true)
                                        .setAspectRatio(15, 8)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(LiveHostPageActivity.this, LiveHostPageActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(LiveHostPageActivity.this, false,
                                null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(
                                                    LiveHostPageActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    @Subscribe
    public void onEvent(EventDelSeries bean) {
        if (liveId != null) {
            getLiveRoomData();
        }
    }

    @Subscribe
    public void onEvent(CreateLiveTopicBean bean) {
        if (liveId != null) {
            getLiveRoomData();
        }
    }

    @Subscribe
    public void onEvent(LiveBgSettingBean bean) {
        ImageHelper.loadImage(MyApplication.getInstance(), mLiveHostPageImage, bean.getBakpic());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        mAlertDialog = null;
        EventBusUtil.unregister(this);
    }


    public static void startActivity(Context activity, String isManage, String id) {
        if (TextUtils.isEmpty(id)) return;
        int idtemp = Integer.valueOf(id);
        if (idtemp > 0) {
            Intent intent = new Intent(activity, LiveHostPageActivity.class);
            intent.putExtra("liveId", id);
            intent.putExtra("isManage", isManage);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, CreateLiveActivity.class);
            activity.startActivity(intent);
        }

    }

    public static void startActivity(Context activity, String id, String isManage, int jumpPage) {
        if (TextUtils.isEmpty(id)) return;
        int idtemp = Integer.valueOf(id);
        if (idtemp > 0) {
            Intent intent = new Intent(activity, LiveHostPageActivity.class);
            intent.putExtra("liveId", id);
            intent.putExtra("jumpPage", jumpPage);
            intent.putExtra("isManage", isManage);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, CreateLiveActivity.class);
            activity.startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        Intent bean = getIntent().putExtra("bean", this.bean);
        setResult(RESULT_OK, bean);
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUESTCODE && resultCode == RESULT_OK) {

            String cover     = data.getStringExtra("cover");
            String roomName  = data.getStringExtra("roomName");
            String bgUri     = data.getStringExtra("bgUri");
            String introduce = data.getStringExtra("introduce");
            reSetView(cover, roomName, bgUri, introduce);
        }

        //使fragment 的 onActivityResult得以回调
        if (requestCode == 666 && (resultCode == Constant.ResponseCode.ISSUE_TONG_BU || resultCode == 667)) {
            mLiveTopicAndSeriseFragment.onActivityResult(requestCode, resultCode, data);
        }
        //使fragment 的 onActivityResult得以回调
        if (requestCode == 1000 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
//            seriesCourseFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            liveIntroFragment.onActivityResult(requestCode, resultCode, data);
            bean.setIntroduce(data.getStringExtra("data"));

        }

        if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK){
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            shareMessage(targetId,type,bean.liuyan);
        }


    }

    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan){
        String title = bean.getRoomname() + "的课堂";
        String img = bean.getBakpic();
        String id = bean.getId();
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "13", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(LiveHostPageActivity.this,"分享成功");
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage
                            (liuyan,targetId,type);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(LiveHostPageActivity.this,"分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }



    private void reSetView(String conver, String roomName, String bgUri, String introduce) {
        bean.setHeadpic(conver);
        bean.setRoomname(roomName);
        bean.setBakpic(bgUri);
        bean.setIntroduce(introduce);
        ImageHelper.loadImage(MyApplication.getInstance(), mImageView, bean.getHeadpic(),
                R.mipmap.ic_launcher);
        mTvLiveHostPageOwnerTitle.setText(bean.getRoomname() + "的课堂");
        ImageHelper.loadImage(MyApplication.getInstance(), mLiveHostPageImage, bean.getBakpic());
        if (liveIntroFragment != null) {
            liveIntroFragment.setIntroduce(introduce);
        }
    }

    @Override
    public List<ChatInfosBean> getChatinfosList() {
        if (bean != null) return bean.getChatInfos();
        return null;
    }

    @Override
    public void showPic(@NotNull String url) {
        Observable.just(url).flatMap(
                new Func1<String, Observable<ApiResponseBean<CreateLiveBean>>>() {
                    @Override
                    public Observable<ApiResponseBean<CreateLiveBean>> call(String s) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("token", UserManager.getInstance().getToken());
                        map.put("chatTypeId", bean.getChatTypeId());
                        map.put("property", bean.getProperty());
                        map.put("roomname", bean.getRoomname());
                        map.put("introduce", bean.getIntroduce());
                        map.put("bakpic", s);
                        return LiveApi.getInstance().saveChatRoom(map);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<CreateLiveBean>>(new ApiCallBack<CreateLiveBean>() {
                    @Override
                    public void onSuccess(CreateLiveBean data) {
                        getBaseLoadingView().hideLoading();
                        ImageHelper.loadImage(MyApplication.getInstance(), mLiveHostPageImage,
                                data.getBakpic());
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        getBaseLoadingView().hideLoading();
                        LoginErrorCodeUtil.showHaveTokenError(LiveHostPageActivity.this, errorCode,
                                message);
                    }
                }));
    }

    @Override
    public void showMembers(@NotNull List<MessageBean> datas) {}

    @Override
    public void showRoleType(@NotNull MessageBean bean) {}

    @Override
    public void showLoad() {}

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {}

    @Override
    public void setPresenter(GroupInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        getBaseLoadingView().showLoading();
        mPresenter.uploadPic(media.getPath());
    }
}
