package com.gxtc.huchuan.ui.mine;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseFragment;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.bean.CoustomMerBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.bean.event.EventJPushBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventUpdataBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.EditInfoDialog;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MallApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.home.MineCircleActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.live.apply.ApplyLecturerActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity;
import com.gxtc.huchuan.ui.mine.account.UsableAccountActivity;
import com.gxtc.huchuan.ui.mine.browsehistory.BrowseHistoryActivity;
import com.gxtc.huchuan.ui.mine.classroom.history.MyHistoryActivity;
import com.gxtc.huchuan.ui.mine.classroom.message.MyMessageActivity;
import com.gxtc.huchuan.ui.mine.classroom.purchaserecord.PurchaseRecordActivity;
import com.gxtc.huchuan.ui.mine.collect.CollectActivity;
import com.gxtc.huchuan.ui.mine.dealmanagement.DealManagementActivity;
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoContract;
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoPrenster;
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceActivity;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;
import com.gxtc.huchuan.ui.mine.incomedetail.InComeDetailNewActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.news.MineArticleActivity;
import com.gxtc.huchuan.ui.mine.news.applyauthor.ApplyAuthorActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.mine.setting.MessageSettingActivity;
import com.gxtc.huchuan.ui.mine.setting.SettingActivity;
import com.gxtc.huchuan.ui.mine.visitor.VisitorActivity;
import com.gxtc.huchuan.ui.special.MySpecialListActivity;
import com.gxtc.huchuan.utils.CheaekUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ErweiCodeHandler;
import com.gxtc.huchuan.utils.JPushUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;
import static com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity.REQUEST_CODE_AVATAR;

/**
 * Describe:个人中心
 * Created by ALing on 2017/5/13 .
 */

public class MineFragmentNew extends BaseFragment implements View.OnClickListener,
        EditInfoContract.View, PictureConfig.OnSelectResultCallback {

    private static final String TAG = MineFragmentNew.class.getSimpleName();

    @BindView(R.id.iv_my_avatar)
    ImageView mIvMyAvatar;
    @BindView(R.id.iv_mediaLevel)
    ImageView mIvMediaLevel;
    @BindView(R.id.img_new_manager)
    ImageView mImgNewManager;
    @BindView(R.id.img_live_manager)
    ImageView mImgLiveManager;
    @BindView(R.id.iv_more)
    ImageView mIvMore;
    @BindView(R.id.tv_my_name)
    TextView mTvMyName;
    @BindView(R.id.tv_introduce)
    TextView mTvIntroduce;
    @BindView(R.id.tv_top_dymic)
    TextView mTvTopDymic;
    @BindView(R.id.tv_top_visitor)
    TextView mTvTopVisitor;
    @BindView(R.id.tv_top_focus)
    TextView mTvTopFocus;
    @BindView(R.id.tv_top_fans)
    TextView mTvTopFans;
    @BindView(R.id.tv_manage_article)
    TextView mTvManageArticle;
    @BindView(R.id.tv_manage_class)
    TextView mTvManageClass;
    @BindView(R.id.tv_manage_circle)
    TextView mTvManageCircle;
    @BindView(R.id.tv_manage_deal)
    TextView mTvManageDeal;
    @BindView(R.id.tv_logout)
    TextView mTvLogout;
    @BindView(R.id.img_setting)
    ImageView imgSetting;
    @BindView(R.id.img_setting_white)
    ImageView imgSettingWhite;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //@BindView(R.id.app_bar)                  AppBarLayout    appBarLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_top)
    RelativeLayout rLTop;
    @BindView(R.id.layout_middle)
    View layoutMid;
    @BindView(R.id.dragView_msg_article)
    View messgaeArticleRedDot;
    @BindView(R.id.dragView_msg_class)
    View messgaeClassRedDot;
    @BindView(R.id.dragView_msg_lass_circle)
    View messgaeCircleRedDot;
    @BindView(R.id.dragView_msg_lass_trance)
    View messgaeTranceRedDot;
    @BindView(R.id.dragView_setting)
    View messgaeSettingRedDot;


    private EditInfoContract.Presenter mPresenter;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private String token = "";
    private User user;
    private float headerBgHeight = -1;
    private String sex = "";
    private ImageView iv;
    private String picHead = "";
    private EditInfoDialog mEditInfoDialog;
    private VertifanceFlowDialog mVertifanceFlowDialog;
    private String status = "";
    private String linkType = "";
    private AlertDialog mAlertDialog;
    private Handler myHandler = new Handler();


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_mine_new, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //fragment 被隐藏后显示
        if (!hidden) {
            if (UserManager.getInstance().isLogin())
                getUserInfo(UserManager.getInstance().getToken());
        }
    }

    @Override
    public void initListener() {
        /*appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (headerBgHeight == -1)
                    headerBgHeight = rLTop.getHeight() - layoutMid.getHeight();
                float percen = Math.abs(verticalOffset / headerBgHeight);
                tvTitle.setAlpha(percen);
            }
        });*/
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);
        new EditInfoPrenster(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        isShowDialog();
    }

    public void isShowDialog() {
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getUser().getToken();
            if (TextUtils.isEmpty(UserManager.getInstance().getHeadPic())) {
                showInfoDialog();
            }
            if (TextUtils.isEmpty(
                    UserManager.getInstance().getUserName()) || UserManager.getInstance().getUserName().equals(
                    UserManager.getInstance().getUserCode())) {
                showInfoDialog();
            } else {
                getUserInfo(token);
            }
        } else {
            resetUserInfo();
        }
    }

    public void showInfoDialog() {
        picHead = UserManager.getInstance().getUser().getHeadPic();
        sex = UserManager.getInstance().getUser().getSex();
        if (mEditInfoDialog == null) mEditInfoDialog = new EditInfoDialog(getContext());
        mEditInfoDialog.show();
        mEditInfoDialog.setSex();
        mEditInfoDialog.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.head_pic:
                        iv = (ImageView) v;
                        chooseImg();
                        break;
                    case R.id.man:
                        sex = "1";
                        break;
                    case R.id.woman:
                        sex = "2";
                        break;
                    case R.id.btn_next:
                        dataSave();
                        break;
                }
            }
        });
        mEditInfoDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false; //默认返回 false
                }
            }
        });
    }

    private void dataSave() {
        if (TextUtils.isEmpty(picHead)) {
            ToastUtil.showShort(getContext(), "请选择头像");
            return;
        }
        if (TextUtils.isEmpty(mEditInfoDialog.getNiCheng().trim())) {
            ToastUtil.showShort(getContext(), getString(R.string.tusi_nikename_canot_empty));
            return;
        }
        if (mEditInfoDialog.getNiCheng().length() > 8) {
            ToastUtil.showShort(getContext(), "昵称字数不能大于8");
            return;
        }
        if (TextUtils.isEmpty(sex)) {
            ToastUtil.showShort(getContext(), "请选择性别");
            return;
        }
        mEditInfoDialog.dismiss();
        myHandler.postDelayed(mRunnable, 2000);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> map = new HashMap<>();
            map.put("name", mEditInfoDialog.getNiCheng());
            map.put("sex", sex);
            map.put("token", UserManager.getInstance().getToken());
            mPresenter.getEditInfo(map);
        }
    };


    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和存储权限", pers, REQUEST_CODE_AVATAR,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options = new FunctionOptions.Builder().setType(
                                FunctionConfig.TYPE_IMAGE).setSelectMode(
                                FunctionConfig.MODE_SINGLE).setImageSpanCount(
                                3).setEnableQualityCompress(false) //是否启质量压缩
                                .setEnablePixelCompress(
                                        false) //是否启用像素压缩
                                .setEnablePreview(
                                        true) // 是否打开预览选项
                                .setShowCamera(
                                        true).setPreviewVideo(
                                        true).setIsCrop(true).setAspectRatio(1, 1).create();
                        PictureConfig.getInstance().init(options).openPhoto(getActivity(),
                                MineFragmentNew.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null,
                                getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(getActivity());
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }


    @Override
    public void getUserSuccess(User data) {
        setUserData(data);
    }

    @Override
    public void EditInfoSuccess(Object object) {
        ToastUtil.showShort(getContext(), getString(R.string.modify_success));
        getUserInfo(UserManager.getInstance().getToken());
    }

    @Override
    public void showUploadResult(User bean) {
        picHead = bean.getHeadPic();
        ImageHelper.loadCircle(getContext(), iv, bean.getHeadPic(), R.drawable.person_icon_head);
        User user = UserManager.getInstance().getUser();
        user.setHeadPic(bean.getHeadPic());
        UserManager.getInstance().updataUser(user);
        EventBusUtil.post(new EventEditInfoBean(EventEditInfoBean.UPLOADAVATAR));
    }


    public void compression(String path) {
        //将图片进行压缩
        if (TextUtils.isEmpty(path)) return;
        final File file = new File(path);
        Subscription sub = Luban.get(MyApplication.getInstance()).load(file).putGear(
                Luban.THIRD_GEAR).asObservable().subscribeOn(Schedulers.newThread()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<File>(new ApiCallBack<File>() {
                    @Override
                    public void onSuccess(File compressFile) {
                        if (FileUtil.getSize(file) > maxLen_500k) {
                            setFile(compressFile);
                        } else {
                            setFile(file);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void setFile(File file) {
        token = UserManager.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            ToastUtil.showShort(getContext(), "请重新登录");
            return;
        }
        if (file == null || TextUtils.isEmpty(file.getName())) {
            ToastUtil.showShort(getContext(), "上传失败，文件为空");
            return;
        }
        RequestBody requestBody = new MultipartBody.Builder().setType(
                MultipartBody.FORM).addFormDataPart("token", token).addFormDataPart("file",
                file.getName(), RequestBody.create(MediaType.parse("image*//**//*"), file)).build();
        mPresenter.uploadAvatar(requestBody);
    }

    private void setVerStatus(User user) {
        if (user != null && "1".equals(user.getIsRealAudit())) {
            mIvMediaLevel.setImageResource(R.drawable.ver_blue);
            mIvMediaLevel.setVisibility(View.VISIBLE);
        } else {
            mIvMediaLevel.setImageResource(R.drawable.ver_gray);
            mIvMediaLevel.setVisibility(View.GONE);
        }
    }

    private void getUserInfo(String token) {
        mCompositeSubscription.add(
                MineApi.getInstance().getUserInfo(token).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<User>>(new ApiCallBack<User>() {
                            @Override
                            public void onSuccess(User data) {
                                if (data != null) {
                                    setUserData(data);
                                } else {
                                    setUserFromLocalData();
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if (ErrorCodeUtil.TOKEN_OVERDUE_10001 == Integer.parseInt(
                                        errorCode)) {
                                    mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false,
                                            null, getResources().getString(R.string.token_overdue),
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                                        ReLoginUtil.ReloginTodo(getActivity());
                                                    }
                                                    mAlertDialog.dismiss();
                                                }
                                            });
                                } else {
                                    ToastUtil.showShort(MyApplication.getInstance(), message);
                                }
                            }
                        })));
    }

    private void setUserData(User data) {
        Observable.just(data).subscribeOn(Schedulers.io()).subscribe(new Action1<User>() {
            @Override
            public void call(User user) {
                UserManager.getInstance().saveUser(user);
                UserManager.getInstance().updataUser(user);
            }
        });
        setVerStatus(data);
        if (!TextUtils.isEmpty(data.getSelfMediaName())) {
            mTvMyName.setText(data.getSelfMediaName());
        } else {
            mTvMyName.setText(data.getName());
        }

        ImageHelper.loadCircle(getContext(), mIvMyAvatar, data.getHeadPic(),
                R.drawable.person_icon_head_120);
        mTvTopDymic.setText(data.getUserDynamicCount());
        mTvTopVisitor.setText(data.getBrowseCount());
        mTvTopFocus.setText(data.getFollowCount());
        mTvTopFans.setText(data.getFsCount());
        mTvIntroduce.setVisibility(View.VISIBLE);
        mTvLogout.setText("退出登录");
        if (data.getIntroduction().toString().trim().length() != 0) {
            mTvIntroduce.setText("简介:" + data.getIntroduction());
        } else {
            mTvIntroduce.setText("简介:" + getString(R.string.label_introduct));
        }
        setAuthorSatus(data);
        setlecturerSatus(data);
    }

    private void setAuthorSatus(User data) {
        //这种状态主要是应对直接从后台推为作者，跳过了实名认证
        if ("1".equals(data.getIsAuthor())) {
            mTvManageArticle.setText(getString(R.string.tab_wenzhangguanli));
            mImgNewManager.setImageResource(R.drawable.person_icon_wzgl);
        } else {
            if ("1".equals(data.getIsRealAudit())) {
                //不是作者，跳申请作者页面
                if (("1".equals(data.getIsAuthor()))) {
                    mTvManageArticle.setText(getString(R.string.tab_wenzhangguanli));
                    mImgNewManager.setImageResource(R.drawable.person_icon_wzgl);

                } else if ("2".equals(data.getIsAuthor())) {
                    mTvManageArticle.setText("在审核中");
                    mImgNewManager.setImageResource(R.drawable.person_icon_sqzz);

                } else {
                    mTvManageArticle.setText(getString(R.string.mine_application_author));
                    mImgNewManager.setImageResource(R.drawable.person_icon_sqzz);

                }
            } else {
                mTvManageArticle.setText(getString(R.string.mine_application_author));
                mImgNewManager.setImageResource(R.drawable.person_icon_sqzz);
            }
        }
    }

    private void setlecturerSatus(User data) {
        //这种状态主要是应对直接从后台推为讲师，跳过了实名认证
        if ("1".equals(data.getIsAnchor())) {
            mTvManageClass.setText(getString(R.string.mine_manage_class));
            mImgLiveManager.setImageResource(R.drawable.person_icon_kcgl);
        } else {
            if ("1".equals(data.getIsRealAudit())) {
                //不是讲师，跳申请讲师页面
                if (("1".equals(data.getIsAnchor()))) {
                    mTvManageClass.setText(getString(R.string.mine_manage_class));
                    mImgLiveManager.setImageResource(R.drawable.person_icon_kcgl);

                } else if ("2".equals(data.getIsAnchor())) {
                    mTvManageClass.setText("在审核中");
                    mImgLiveManager.setImageResource(R.drawable.person_icon_sqjs);
                } else {
                    mTvManageClass.setText(getString(R.string.mine_application_lecturer));
                    mImgLiveManager.setImageResource(R.drawable.person_icon_sqjs);
                }
            } else {
                mTvManageClass.setText(getString(R.string.mine_application_lecturer));
                mImgLiveManager.setImageResource(R.drawable.person_icon_sqjs);
            }
        }
    }

    private void setBadgeView(EventJPushBean bean) {
        switch (bean.type) {
            //圈子提醒
            case 17:
                messgaeCircleRedDot.setVisibility(View.INVISIBLE); //先屏蔽小红点
                break;

            //文章提醒
            case 18:
                messgaeArticleRedDot.setVisibility(View.INVISIBLE);
                break;

            //课堂提醒
            case 19:
                messgaeClassRedDot.setVisibility(View.INVISIBLE);
                break;

            //交易提醒
            case 20:
                messgaeTranceRedDot.setVisibility(View.INVISIBLE);
                break;

            //作者审核
            case 5:
//                mPresenter.sendNotification(getContext(), "申请作者通知", "状态已发生改变");
                getUserInfo(UserManager.getInstance().getToken());
                break;

            //讲师审核
            case 6:
//                mPresenter.sendNotification(getContext(), "申请讲师通知", "状态已发生改变");
                getUserInfo(UserManager.getInstance().getToken());
                break;
        }
        //EventBus.getDefault().cancelEventDelivery(bean);
    }

    private void resetUserInfo() {
        mIvMyAvatar.setImageResource(R.drawable.person_icon_head_120);
        mTvMyName.setText("1秒登录，体验更多功能");
        mTvIntroduce.setVisibility(View.GONE);
        mTvTopDymic.setText("0");
        mTvTopVisitor.setText("0");
        mTvTopFocus.setText("0");
        mTvTopFans.setText("0");
        mTvManageArticle.setText(getString(R.string.mine_application_author));
        mTvManageClass.setText(getString(R.string.mine_application_lecturer));
        mTvLogout.setText("马上登录");
        setVerStatus(null);
    }

    private void setUserFromLocalData() {
        user = UserManager.getInstance().getUser();
        setUserData(user);
    }


    @OnClick({R.id.rl_top_dymic, R.id.rl_top_visitor, R.id.rl_top_foucs, R.id.rl_top_fans,
            R.id.tv_manage_article, R.id.tv_manage_class, R.id.tv_manage_circle,
            R.id.tv_manage_deal, R.id.tv_new_task, R.id.tv_income_detail,
            R.id.tv_my_wallet, R.id.tv_order_record, R.id.tv_my_message,
            R.id.tv_messge_set, R.id.tv_my_collection, R.id.tv_browse_history,
            R.id.rl_personal_homepage, R.id.img_setting, R.id.img_setting_white,
            R.id.iv_my_avatar, R.id.toolbar, R.id.tv_chat_msg, R.id.tv_my_name,
            R.id.tv_kefu, R.id.layout_manage_article, R.id.layout_manage_class,
            R.id.layout_manage_circle, R.id.tv_special,
            R.id.layout_manage_deal, R.id.tv_share, R.id.tv_logout})

    public void onClick(View view) {
        switch (view.getId()) {
            //已报课程
            case R.id.tv_chat_msg:
                if (UserManager.getInstance().isLogin(getActivity())) {
                    Intent intent = new Intent(getContext(), MyHistoryActivity.class);
                    intent.putExtra("title", "已报课程");
                    intent.putExtra(MainActivity.STATISTICS_STATUS, MainActivity.STATISTICS_EMTPT);
                    startActivity(intent);
                }
                break;

            //点击头像，进编辑资料
            case R.id.iv_my_avatar:
                if (UserManager.getInstance().isLogin(getActivity())) {
                    PersonalInfoActivity.startActivity(getContext(),
                            UserManager.getInstance().getUserCode());
                }
                break;

            //系统消息
            case R.id.rl_top_dymic:
                mTvTopDymic.setText("0");
                goToActivity(MyMessageActivity.class);
                break;

            //访客
            case R.id.rl_top_visitor:
                goToActivity(VisitorActivity.class);
                break;

            //关注
            case R.id.rl_top_foucs:
                if (UserManager.getInstance().isLogin(getActivity())) {
                    goToFocusActivity("1");
                }
                break;

            //粉丝
            case R.id.rl_top_fans:
                if (UserManager.getInstance().isLogin(getActivity())) {
                    goToFocusActivity("3");
                }
                break;
            //粉丝
            case R.id.layout_manage_article:
                //文章管理
            case R.id.tv_manage_article:
                messgaeArticleRedDot.setVisibility(View.INVISIBLE);
                if ("1".equals(UserManager.getInstance().getIsAuthor())) {
                    goToActivity(MineArticleActivity.class);
                } else {
                    if (UserManager.getInstance().isLogin(getActivity())) {
                        if ("1".equals(UserManager.getInstance().getIsRealAudit())) {
                            switch (UserManager.getInstance().getIsAuthor()) {
                                case "0":      //不是作者
                                    goToActivity(ApplyAuthorActivity.class);
                                    break;
                                case "1":      //作者
                                    goToActivity(MineArticleActivity.class);
                                    break;
                                case "2":       //2 审核中
                                    showVertifanceDialog(R.id.tv_manage_article);
                                    break;
                                case "3":       //2 审核不通过
                                    showVertifanceDialog(R.id.tv_manage_article);
                                    break;
                            }
                        } else {
                            showVertifanceDialog();
                        }

                    }
                }
                break;

            //课程管理
            case R.id.layout_manage_class:
            case R.id.tv_manage_class:
                messgaeClassRedDot.setVisibility(View.INVISIBLE);
                if (UserManager.getInstance().isLogin(getActivity())) {
                    if (("1".equals(UserManager.getInstance().getIsAnchor()))) {
                        String chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
                        LiveHostPageActivity.startActivity(getActivity(), "0", chatRoomId);
                    } else {
                        if ("1".equals(UserManager.getInstance().getIsRealAudit())) {
                            switch (UserManager.getInstance().getIsAnchor()) {
                                //不是讲师
                                case "0":
                                    goToActivity(ApplyLecturerActivity.class);
                                    break;

                                //是讲师者
                                case "1":
                                    String chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
                                    LiveHostPageActivity.startActivity(getActivity(), "0",
                                            chatRoomId);
                                    break;

                                //审核中
                                case "2":
                                    showVertifanceDialog(R.id.tv_manage_class);
                                    break;

                                //审核不通过
                                case "3":
                                    showVertifanceDialog(R.id.tv_manage_class);
                                    break;
                            }
                        } else {
                            showVertifanceDialog();
                        }

                    }
                }
                break;

            //圈子管理
            case R.id.layout_manage_circle:
            case R.id.tv_manage_circle:
                messgaeCircleRedDot.setVisibility(View.INVISIBLE);
                gotoManage(false);
                break;

            //交易管理
            case R.id.layout_manage_deal:
            case R.id.tv_manage_deal:
                messgaeTranceRedDot.setVisibility(View.INVISIBLE);
                goToActivity(DealManagementActivity.class);
                break;

            //我订阅的专题
            case R.id.tv_special:
                goToActivity(MySpecialListActivity.class);
                break;

            //分享赚钱
            /*case R.id.tv_share_profit:
                goToActivity(ShareMakeMoneyActivity.class);
                break;*/

            //新手任务
            case R.id.tv_new_task:
                Intent intent = new Intent(getContext(), CommonWebViewActivity.class);
                intent.putExtra("web_url",
                        Constant.Url.DEBUG + "html/tasklist.html?token=" + UserManager.getInstance().getToken());
                intent.putExtra("web_title", "新手任务");
                startActivity(intent);
                break;

            //收益明细
            case R.id.tv_income_detail:
                goToActivity(InComeDetailNewActivity.class);
                break;

            //我的钱包
            case R.id.tv_my_wallet:
                goToActivity(UsableAccountActivity.class);
                break;

            //订单列表
            case R.id.tv_order_record:
                goToActivity(PurchaseRecordActivity.class);
                break;

            //系统设置
            case R.id.tv_my_message:
//                JumpPermissionManagement.GoToSetting(getActivity());
                messgaeSettingRedDot.setVisibility(View.GONE);
                GotoUtil.goToActivity(getActivity(), SettingActivity.class);
                break;

            //我的收藏
            case R.id.tv_my_collection:
                goToActivity(CollectActivity.class);
                break;

            //浏览历史
            case R.id.tv_browse_history:
                goToActivity(BrowseHistoryActivity.class);
                break;

            case R.id.toolbar:
                if (!UserManager.getInstance().isLogin(getActivity())) {
                    GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
                }
                break;

            //跳转到个人主页
            case R.id.rl_personal_homepage:
                if (UserManager.getInstance().isLogin(getActivity())) {
                    PersonalInfoActivity.startActivity(getContext(),
                            UserManager.getInstance().getUserCode());
                }
                break;

            //消息设置
            case R.id.tv_messge_set:
                goToActivity(MessageSettingActivity.class);
                break;

            //系统设置
            case R.id.img_setting_white:
            case R.id.img_setting:
                GotoUtil.goToActivity(this, SettingActivity.class);
                break;

            //未登录点击进入登录页
            case R.id.tv_my_name:
                if (UserManager.getInstance().isLogin(getActivity())) {
                    PersonalInfoActivity.startActivity(getContext(),
                            UserManager.getInstance().getUserCode());
                }
                break;

            //联系客服
            case R.id.tv_kefu:
                if (UserManager.getInstance().isLogin()) {
                    getKefu();
                } else {
                    GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class,
                            Constant.requestCode.NEWS_AUTHOR);
                }
                break;

            //推荐给好友
            case R.id.tv_share:
                if (UserManager.getInstance().isLogin()) {
                    share();
                } else {
                    GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class,
                            Constant.requestCode.NEWS_AUTHOR);
                }
                break;

            //退出登录
            case R.id.tv_logout:
                if (UserManager.getInstance().isLogin(getActivity())) {
                    mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null, "确定退出吗",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                        LogOut();
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                }
                break;
        }
    }

    private void share() {
        ErweiCodeHandler.getInstant().getUrl(this, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    String url = (String) GsonUtil.getJsonValue(GsonUtil.objectToJson(data), "url");
                    share(url);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(), message);
            }
        });

    }

    private void share(final String url) {
        UMShareUtils mUMShareUtils = new UMShareUtils(getActivity());
        mUMShareUtils.shareDefault(R.mipmap.person_icon_head_share, "新媒之家",
                "一个汇聚百万新媒体大咖的信息交流、资源交换平台", url);
        mUMShareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
            @Override
            public void onItemClick(int flag) {
                switch (flag) {
                    case 0:
                        ClipboardManager cmb = (ClipboardManager) MyApplication.getInstance().getSystemService(
                                Context.CLIPBOARD_SERVICE);
                        cmb.setText(url);
                        ToastUtil.showShort(MyApplication.getInstance(), "链接已复制");
                        break;
                    case 1:
                        ErWeiCodeActivity.startActivity(getContext(),
                                ErWeiCodeActivity.TYPE_YINGYONGBAO, 0, "");
                        break;
                }
            }
        });
    }

    //实名认证
    private void showVertifanceDialog() {
        mVertifanceFlowDialog = new VertifanceFlowDialog(getContext());
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(UserManager.getInstance().getIsRealAudit()) || "3".equals(
                        UserManager.getInstance().getIsRealAudit())) {
                    GotoUtil.goToActivityForResult(getActivity(), VertifanceActivity.class, 100);
                }
                mVertifanceFlowDialog.dismiss();
            }
        });
        switch (UserManager.getInstance().getIsRealAudit()) {
            case "0":
                mVertifanceFlowDialog.setFlowStatus("请先实名认证");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_3);
                break;
            case "2":
                mVertifanceFlowDialog.setFlowStatus("系统正在实名认证审核中，请耐心等待");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
                break;
            case "3":
                checkRealAudit();
                break;
        }
    }

    public void checkRealAudit() {
        CheaekUtil.getInstance().getInfo(UserManager.getInstance().getToken(),
                UserManager.getInstance().getUserCode(), Constant.STATUE_LINKTYPE_REAL_NAME,
                new ApiCallBack<CheckBean>() {

                    @Override
                    public void onSuccess(CheckBean data) {
                        if (data == null) return;
                        mVertifanceFlowDialog.setFlowStatus("实名认证审核不通过 " + data.getContent());
                        mVertifanceFlowDialog.setFlowStatusPic(
                                R.drawable.applicationprocess_icon_shjg_2);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (ErrorCodeUtil.TOKEN_OVERDUE_10001 == Integer.parseInt(errorCode)) {
                            mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null,
                                    getResources().getString(R.string.token_overdue),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.tv_dialog_confirm) {
                                                ReLoginUtil.ReloginTodo(getActivity());
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                        } else {
                            ToastUtil.showShort(getContext(), message);
                        }
                    }
                }).addTask(this);
    }

    private void getKefu() {
        //0：全局客服1：商城客服 2：交易客服 3：app客服  rand  0：列表 1：随机
        Subscription sub = MallApi.getInstance().getIMServiceList(
                MallCustomersActivity.Companion.getCUSTOMERS_TYPE_OF_APP(),
                MallCustomersActivity.Companion.getCUSTOMERS_STATUS_SHOW_RAND()).observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<ArrayList<CoustomMerBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data == null) return;
                        ArrayList<CoustomMerBean> datas = (ArrayList<CoustomMerBean>) data;
                        if (datas.size() > 0) RongIM.getInstance().startPrivateChat(getActivity(),
                                datas.get(0).getUserCode(), datas.get(0).getName());
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if ("110".equals(errorCode)) {
                            ToastUtil.showShort(MyApplication.getInstance(), "当前客服不在");
                        } else if (ErrorCodeUtil.TOKEN_OVERDUE_10001 == Integer.parseInt(
                                errorCode)) {
                            mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null,
                                    getResources().getString(R.string.token_overdue),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.tv_dialog_confirm) {
                                                ReLoginUtil.ReloginTodo(getActivity());
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), message);
                        }
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void showVertifanceDialog(final int btnId) {
        String text = "";
        mVertifanceFlowDialog = new VertifanceFlowDialog(getContext());
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btnId) {
                    case R.id.tv_manage_article://申请作者不通过做跳转
                        if ("3".equals(UserManager.getInstance().getIsAuthor())) {
                            goToActivity(ApplyAuthorActivity.class);
                        }
                        break;
                    case R.id.tv_manage_class://申请讲师不通过做跳转
                        if ("3".equals(UserManager.getInstance().getIsAnchor())) {
                            goToActivity(ApplyLecturerActivity.class);
                        }
                        break;
                }
                mVertifanceFlowDialog.dismiss();
            }
        });
        if (btnId == R.id.tv_manage_article) {
            text = "申请作者";
            linkType = Constant.STATUE_LINKTYPE_AUTHOR;
            status = UserManager.getInstance().getIsAuthor();
        } else {
            text = "申请讲师";
            linkType = Constant.STATUE_LINKTYPE_ANCHOR;
            status = UserManager.getInstance().getIsAnchor();
        }
        switch (status) {
            case "2":
                mVertifanceFlowDialog.setFlowStatus(text + "系统正在审核中，请耐心等待");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
                break;
            case "3":
                checkCicle(linkType);
                break;
        }
    }

    public void checkCicle(final String linkType) {
        CheaekUtil.getInstance().getInfo(UserManager.getInstance().getToken(),
                UserManager.getInstance().getUserCode(), linkType, new ApiCallBack<CheckBean>() {

                    @Override
                    public void onSuccess(CheckBean data) {
                        if (data == null) return;
                        switch (linkType) {
                            case Constant.STATUE_LINKTYPE_AUTHOR:
                                mVertifanceFlowDialog.setFlowStatus(
                                        "申请作者审核不通过:" + data.getContent());
                                break;
                            case Constant.STATUE_LINKTYPE_ANCHOR:
                                mVertifanceFlowDialog.setFlowStatus(
                                        "申请讲师审核不通过:" + data.getContent());
                                break;
                        }
                        mVertifanceFlowDialog.setFlowStatusPic(
                                R.drawable.applicationprocess_icon_shjg_2);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (ErrorCodeUtil.TOKEN_OVERDUE_10001 == Integer.parseInt(errorCode)) {
                            mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null,
                                    getResources().getString(R.string.token_overdue),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.tv_dialog_confirm) {
                                                ReLoginUtil.ReloginTodo(getActivity());
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                        } else {
                            ToastUtil.showShort(getContext(), message);
                        }
                    }
                }).addTask(this);
    }

    private void goToFocusActivity(String focusFlag) {
        Intent intent = new Intent(getActivity(), FocusActivity.class);
        intent.putExtra("focus_flag", focusFlag);
        getActivity().startActivity(intent);
    }

    /**
     * 是否需要展示加入的圈子
     *
     * @param isShowJoin
     */
    private void gotoManage(boolean isShowJoin) {
        if (UserManager.getInstance().isLogin()) {
            Intent intent = new Intent(this.getActivity(), MineCircleActivity.class);
            if (isShowJoin) {
                intent.putExtra("flag", "1");
                startActivity(intent);
            } else {
                intent.putExtra("flag", "2");
                startActivity(intent);
            }

        } else {
            GotoUtil.goToActivity(this.getActivity(), LoginAndRegisteActivity.class);
        }
    }

    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(getActivity(), toClass);
        } else {
            GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class,
                    Constant.requestCode.NEWS_AUTHOR);
        }
    }

    //刷新头像 用户名
    @Subscribe
    public void onEvent(EventLoginBean bean) {
        if (bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.REGISTE || bean.status == EventLoginBean.THIRDLOGIN) {
            if (UserManager.getInstance().isLogin()) {
                user = UserManager.getInstance().getUser();
                setUserData(user);
            }

            //退出登录
        } else if (bean.status == EventLoginBean.EXIT || bean.status == EventLoginBean.TOKEN_OVERDUCE) {
            resetUserInfo();
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(EventJPushBean bean) {
        setBadgeView(bean);
    }


    //版本更新
    @Subscribe(sticky = true)
    public void onEvent(EventUpdataBean bean) {
        messgaeSettingRedDot.setVisibility(View.VISIBLE);
    }


    @Subscribe
    public void onEvent(EventEditInfoBean bean) {
        User user = UserManager.getInstance().getUser();
        if (bean.status == EventEditInfoBean.CHANGENAME) {
            mTvMyName.setText(user.getName());
        } else if (bean.status == EventEditInfoBean.UPLOADAVATAR) {
            ImageHelper.loadCircle(getContext(), mIvMyAvatar, user.getHeadPic(),
                    R.drawable.person_icon_head_120);
        } else if (bean.status == EventEditInfoBean.INTRO) {
            mTvIntroduce.setText(user.getIntroduction());
        }

    }

    private void setTopManageTypeAuthor() {
        //新申请 未审核
        if ("2".equals(UserManager.getInstance().getIsAuthor())) {
            mTvManageArticle.setText("正在审核");

            //审核通过
        } else if ("1".equals(UserManager.getInstance().getIsAuthor())) {
            mTvManageArticle.setText("文章管理");
            getUserInfo(UserManager.getInstance().getToken());

            //审核不通过
        } else if ("0".equals(UserManager.getInstance().getIsAuthor())) {
            mTvManageArticle.setText("申请作者");
        }
    }

    //新申请 未审核
    private void setTopManageTypeAnchor() {
        if ("2".equals(UserManager.getInstance().getIsAnchor())) {

            mTvManageClass.setText("正在审核");

            //审核通过
        } else if ("1".equals(UserManager.getInstance().getIsAnchor())) {

            mTvManageClass.setText("课程管理");
            getUserInfo(UserManager.getInstance().getToken());

            //审核不通过
        } else if ("0".equals(UserManager.getInstance().getIsAnchor())) {

            mTvManageClass.setText("申请讲师");
        }
    }


    private void LogOut() {
        mTvLogout.setText("马上登录");
        UserManager.getInstance().deleteUser();
        EventBusUtil.post(new EventLoginBean(EventLoginBean.EXIT));
        RongIM.getInstance().logout();
        //退出登录收不到推送
        JPushUtil.getInstance().closeJPush(getActivity());
    }


    //推荐关注
    @Subscribe
    public void onEvent(EventFocusBean bean) {
        mTvTopFocus.setText(
                (Integer.valueOf(UserManager.getInstance().getUser().getFollowCount()) + 1) + "");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        if ("3".equals(status)) {
            CheaekUtil.getInstance().cancelTask(this);
        }
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
        ErweiCodeHandler.Companion.getInstant().destroy(this);
        myHandler.removeCallbacks(mRunnable);
        mAlertDialog = null;
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null,
                getResources().getString(R.string.token_overdue), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            ReLoginUtil.ReloginTodo(getActivity());
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }

    @Override
    public void showLoad() {
    }

    @Override
    public void showLoadFinish() {
    }

    @Override
    public void showEmpty() {
    }

    @Override
    public void showReLoad() {
        mPresenter.getUserInfo(token);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(getContext(), info);
    }

    @Override
    public void showNetError() {
    }

    @Override
    public void setPresenter(EditInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        compression(media.getPath());
    }
}
