package com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PersonalInfoPhotoAdapter;
import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.bean.event.EventRemarkPersonalInfo;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.data.PersonalInfoRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListBottomDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.deal.guarantee.ApplyGuaranteeActivity;
import com.gxtc.huchuan.ui.im.postcard.PTMessage;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomePageActivity;
import com.gxtc.huchuan.ui.mine.setting.SetCirclePermisionActivity;
import com.gxtc.huchuan.ui.mine.setting.accountSafe.AccountSafeActivity;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.widget.OtherGridView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/31.
 * 个人资料
 */

public class PersonalInfoActivity extends BaseTitleActivity implements View.OnClickListener,
        PersonalInfoContract.View {

    private static final String TAG = PersonalHomePageActivity.class.getSimpleName();
    private final String APPLY_TIME = "apply";

    @BindView(R.id.iv_img_head)
    ImageView mIvImgHead;
    @BindView(R.id.pic_ver_status)
    ImageView mIvImgVer;
    @BindView(R.id.tv_area)
    TextView mTvArea;
    @BindView(R.id.tv_intro)
    TextView mTvIntro;
    @BindView(R.id.tv_follow)
    TextView tvFollow;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.iv_more)
    ImageView mIvMore;
    @BindView(R.id.rl_album)
    RelativeLayout mRlAlbum;
    @BindView(R.id.grid_album)
    OtherGridView mGrideView;
    @BindView(R.id.set_remark_and_label)
    TextView mSetRemarkAndLabel;
    @BindView(R.id.tv_remark)
    TextView mTvRemark;
    @BindView(R.id.tv_nikename)
    TextView mTvNikename;
    @BindView(R.id.tv_des)
    TextView mTvDes;
    @BindView(R.id.usercode)
    TextView usercode;
    @BindView(R.id.btn_send_message)
    Button mBtn;
    @BindView(R.id.btn_apply_guarantee)
    Button btnGuarantee;
    @BindView(R.id.rl_des)
    RelativeLayout rlDes;

    private PersonalInfoContract.Presenter mPresenter;
    private PersonalInfoPhotoAdapter mAdapter;
    private HashMap<String, String> infomap;
    private String userCode;
    private Drawable mDrawable;
    private PersonInfoBean mPersonInfoBean;
    private String coverUrl;
    private int privateChat;         //主要是圈子点过来的参数，是否开启允许圈子内私聊   0、允许；1、不允许
    private int chatStatus = -1;     //0申请好友,  1通过好友  2等待验证

    private boolean isFollow;

    private String[] dialogData = new String[]{"设置备注", "设置圈子权限", "加入黑名单", "推荐给好友", "举报用户", "取消关注"};
    private int[] imgs = new int[]{
            R.drawable.icon_beizhu,
            R.drawable.icon_qzqx,
            R.drawable.icon_jrhmd,
            R.drawable.icon_tuijian,
            R.drawable.ju_bao,
            R.drawable.icon_qxgz};

    private ListBottomDialog mBottomDialog;
    private Subscription subShield;
    private AlertDialog mDialog;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_detail_info));
        getBaseHeadView().showBackButton(this);
    }


    @Override
    public void initData() {
        userCode = getIntent().getStringExtra("userCode");
        privateChat = getIntent().getIntExtra("privateChat", 0);
        infomap = new HashMap<>();
        if (UserManager.getInstance().isLogin()) {

            infomap.put("token", UserManager.getInstance().getToken());
        } else {
            GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class, 666);
        }
        infomap.put("userCode", userCode);
        new PersonalInfoPresenter(this);
        mPresenter.getUserInformation(infomap);

    }

    public void setVerStatus(PersonInfoBean bean) {
        if ("1".equals(bean.getIsRealAudit())) {
            mIvImgVer.setImageResource(R.drawable.ver_blue);
            mIvImgVer.setVisibility(View.VISIBLE);
        } else {
            mIvImgVer.setImageResource(R.drawable.ver_gray);
            mIvImgVer.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.set_remark_and_label,
            R.id.rl_album,
            R.id.btn_send_message,
            R.id.iv_img_head,
            R.id.rl_des,
            R.id.tv_follow,
            R.id.tv_edit,
            R.id.btn_apply_guarantee})
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;

        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.HeadRightImageButton:
                if (mPersonInfoBean != null) {
                    showBottomDialog();
                }
                break;

            //二维码名片
            case R.id.rl_des:
                break;
            case R.id.set_remark_and_label:
                gotoErWeiCode();
                break;

            //个人相册——> 进个人主页
            case R.id.rl_album:
                if (mPersonInfoBean != null) {
                    PersonalHomePageActivity.startActivity(this, userCode);
                }
                break;

            case R.id.btn_send_message:
                if (UserManager.getInstance().isLogin(this)) {
                    //好友可以直接发消息
                    if (canStartChat()) {
                        connect();
                    } else {
                        //申请好友
                        if (chatStatus == 0) {
                            applyFriends();
                        }
                        //通过好友
                        if (chatStatus == 1) {
                            follow();
                        }
                        //等待验证
                        if (chatStatus == 2) {

                        }
                    }
                }
                break;

            case R.id.iv_img_head:
                HeadImageShoweActivity.startActivity(this, coverUrl);
                break;

            //关注
            case R.id.tv_follow:
                follow();
                break;

            //跳转到编辑个人资料页面
            case R.id.tv_edit:
                Intent intent = new Intent(this, EditInfoActivity.class);
                startActivity(intent);
                break;

            //申请快速担保交易
            case R.id.btn_apply_guarantee:
                if (UserManager.getInstance().isLogin(this)) {
                    if (mPersonInfoBean != null) {
                        String name = TextUtils.isEmpty(mPersonInfoBean.getRemarkName()) ? mPersonInfoBean.getName() : mPersonInfoBean.getRemarkName();
                        ApplyGuaranteeActivity.startActivity(this, mPersonInfoBean.getUserCode(), name, ApplyGuaranteeActivity.CREATESOURCE_FROM_PERSON);
                    }
                }
                break;
        }
    }

    private void reported() {
        ReportActivity.jumptoReportActivity(this, mPersonInfoBean.getUserCode(), "7");
    }

    private void gotoErWeiCode() {
        if (mPersonInfoBean != null) {
            ErWeiCodeActivity.startActivity(this, ErWeiCodeActivity.TYPE_PERSONINFO, mPersonInfoBean.getId(), "");
        }
    }

    //显示底部弹窗
    private void showBottomDialog() {
        final LinkedList<String> list = new LinkedList<>();
        Collections.addAll(list, dialogData);
        if (mPersonInfoBean.getIsBlackList().equals("1")) {
            list.set(2, "取消黑名单");
        } else {
            list.set(2, "加入黑名单");
        }
        if (mPersonInfoBean.getIsFollow() == 1) {
            list.set(dialogData.length - 1, "取消关注");
        } else {
            list.remove(dialogData.length - 1);
        }
        if (mBottomDialog == null) {
            mBottomDialog = new ListBottomDialog();
        }
        mBottomDialog.setDatas(list)
                .setDrawableImgs(imgs)
                .setShowDivider(false)
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mBottomDialog.dismiss();
                        switch (position) {
                            //设置备注
                            case 0:
                                RemarkAndLabelActivity.startActivity(PersonalInfoActivity.this, userCode);
                                break;

                            //设置圈子权限
                            case 1:
                                SetCirclePermisionActivity.startActivity(PersonalInfoActivity.this, userCode, mPersonInfoBean);
                                break;

                            //加入黑名单
                            case 2:
                                //黑名单。0、未设置；1、已设置
                                if (mPersonInfoBean.getIsBlackList().equals("1")) {
                                    relieveShield();
                                } else {
                                    setUserPermision("2");//0：不看该用户动态；1：不给该用户看动态；2：黑名单
                                }
                                break;

                            //发送名片
                            case 3:
                                ConversationListActivity.startActivity(PersonalInfoActivity.this, ConversationActivity.REQUEST_SHARE_CARD, Constant.SELECT_TYPE_CARD);
                                break;

                            //举报用户
                            case 4:
                                reported();
                                break;

                            //取消关注
                            case 5:
                                isFollow = true;
                                mPresenter.folowUser(userCode);
                                break;
                        }
                    }
                });

        if (!mBottomDialog.isAdded() && !mBottomDialog.isVisible() && !mBottomDialog.isRemoving()) {
            mBottomDialog.show(getSupportFragmentManager(), ListBottomDialog.class.getName());
        }

    }

    /**
     * 解除屏蔽
     */
    private void relieveShield() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("userCode", userCode);
        map.put("type", "2");//0：不看该用户动态；1：不给该用户看动态；2：黑名单

        subShield = AllApi.getInstance().deleteByUserCode(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mPresenter == null) return;
                        mPresenter.getUserInformation(infomap);
                        ToastUtil.showShort(MyApplication.getInstance(), "解除黑名单成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, subShield);
    }

    void setUserPermision(String type) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", UserManager.getInstance().getToken());
        hashMap.put("userCode", mPersonInfoBean.getUserCode());
        hashMap.put("type", type);//0：不看该用户动态；1：不给该用户看动态；2：黑名单
        Subscription sub = MineApi.getInstance().setUserScren(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mPresenter == null) return;
                        mPresenter.getUserInformation(infomap);
                        ToastUtil.showShort(MyApplication.getInstance(), "加入黑名单成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    //加关注
    private void follow() {
        mPresenter.folowUser(userCode);
    }

    //申请好友  一分钟之内全部好友只能申请一次
    private void applyFriends() {
        long time = SpUtil.getLong(this, APPLY_TIME, 0);
        if ((System.currentTimeMillis() - time) >= 1000 * 60) {
            View dialogView = View.inflate(this, R.layout.dialog_author_layout, null);
            final EditText editAuthor = (EditText) dialogView.findViewById(R.id.et_input);
            editAuthor.setText("我是" + UserManager.getInstance().getUserName());
            mDialog = DialogUtil.showAuthorDialog(this, dialogView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WindowUtil.closeInputMethod(PersonalInfoActivity.this);
                    mDialog.dismiss();
                    mPresenter.applyFriends(userCode, editAuthor.getText().toString());
                }
            });
        } else {
            ToastUtil.showShort(this, "一分钟内只能申请一次好友");
        }
    }

    private void sendRongIm(final EventSelectFriendForPostCardBean bean) {
        bean.userCode = mPersonInfoBean.getUserCode();
        bean.name = mPersonInfoBean.getName();
        bean.picHead = mPersonInfoBean.getHeadPic();
        PTMessage mPTMessage = PTMessage.obtain(bean.userCode, bean.name, bean.picHead);
        String title = mPTMessage.getName();
        String img = mPTMessage.getHeadPic();
        String id = mPTMessage.getUserCode();
        ImMessageUtils.sentPost(bean.targetId, bean.mType, id, title, img, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
            }

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(MyApplication.getInstance(), "发送名片成功");
                if (!TextUtils.isEmpty(bean.liuyan)) {
                    RongIMTextUtil.INSTANCE.relayMessage(bean.liuyan, bean.targetId, bean.mType);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(MyApplication.getInstance(), "发送名片失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(this, false, null, getString(R.string.token_overdue),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            ReLoginUtil.ReloginTodo(PersonalInfoActivity.this);
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }

    private void setViewData(PersonInfoBean bean) {
        mPersonInfoBean = bean;
        setVerStatus(mPersonInfoBean);
        usercode.setText("新媒号：" + mPersonInfoBean.getUserCode());
        coverUrl = bean.getHeadPic();
        ImageHelper.loadRound(this, mIvImgHead, bean.getHeadPic(), 6);
        if (!TextUtils.isEmpty(bean.getRemarkDesc())) {
            rlDes.setVisibility(View.GONE);
            mTvDes.setText(bean.getRemarkDesc());
        }
        if (TextUtils.isEmpty(bean.getRemarkName())) {
            mTvRemark.setText(bean.getName());
            mTvNikename.setVisibility(View.GONE);
        } else {
            mTvRemark.setText(bean.getRemarkName());
            mTvNikename.setVisibility(View.VISIBLE);
            mTvNikename.setText("昵称:" + bean.getName());
        }
        if ("2".equals(bean.getSex())) {
            mDrawable = getResources().getDrawable(R.drawable.person_homepage_icon_nv);
            mDrawable.setBounds(0, 0, mDrawable.getBounds().width(), mDrawable.getBounds().height());
            mTvRemark.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
        } else if ("1".equals(bean.getSex())) {
            mDrawable = getResources().getDrawable(R.drawable.person_homepage_icon_nan);
            mDrawable.setBounds(0, 0, mDrawable.getBounds().width(), mDrawable.getBounds().height());
            mTvRemark.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
        }
        mTvRemark.setVisibility(View.VISIBLE);

        if (bean.getIsFollow() == 0) {
            tvFollow.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(userCode) && userCode.equals(UserManager.getInstance().getUserCode())) {
            tvFollow.setVisibility(View.INVISIBLE);
            tvEdit.setVisibility(View.VISIBLE);
        } else {
            //设置了仅好友可发消息 ，1是不可以发消息
            if (canStartChat()) {
                mBtn.setText("发消息");
            } else {
                setSendBtnUI();
            }
            getBaseHeadView().showHeadRightImageButton(R.drawable.icon_more, this);
        }
        if (TextUtils.isEmpty(bean.getCity())) {
            mTvArea.setText("未设置");
        } else {
            mTvArea.setText(bean.getCity());
        }
        if (TextUtils.isEmpty(bean.getIntroduction())) {
            mTvIntro.setText("未设置");
        } else {
            mTvIntro.setText(bean.getIntroduction());
        }
        List<String> photos = bean.getPhotos();

        mAdapter = new PersonalInfoPhotoAdapter(PersonalInfoActivity.this, photos,
                R.layout.item_personal_photo);
        mGrideView.setAdapter(mAdapter);
        mGrideView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonalHomePageActivity.startActivity(PersonalInfoActivity.this, userCode);
            }
        });

        if (!TextUtils.isEmpty(userCode) && !userCode.equals(UserManager.getInstance().getUserCode())) {
            btnGuarantee.setVisibility(View.VISIBLE);
        } else {
            btnGuarantee.setVisibility(View.GONE);
        }

        UserInfo userInfo = new UserInfo(userCode, bean.getName(), Uri.parse(bean.getHeadPic()));
        RongIM.getInstance().refreshUserInfoCache(userInfo);
    }

    @Override
    public void showUserInformation(PersonInfoBean bean) {
        if (bean == null) return;

        if (bean.getIsSaler() == 1) {
            getBaseEmptyView().showEmptyView("该用户已被平台封禁");
            return;
        }
        setViewData(bean);
    }

    //是否是好友
    private boolean isFrends() {
        return mPersonInfoBean.getIsFollow() == 1 && mPersonInfoBean.getIsFans() == 1;
    }

    //是否可以私聊
    private boolean canStartChat() {
        if (mPersonInfoBean == null) return false;

        return mPersonInfoBean.getUserCode().equals(UserManager.getInstance().getUserCode())
                || isFrends()
                || "0".equals(mPersonInfoBean.getIsChat())
                && 0 == privateChat;
    }

    private void setSendBtnUI() {
        if (mPersonInfoBean.getIsFans() == 1 && mPersonInfoBean.getIsFollow() == 1) {
            mBtn.setText("发消息");
            mBtn.setEnabled(true);
            mBtn.setBackgroundResource(R.drawable.btn_blue_selector);
        }

        if (mPersonInfoBean.getIsFans() == 0 && mPersonInfoBean.getIsFollow() == 0) {
            mBtn.setText("申请好友");
            mBtn.setEnabled(true);
            chatStatus = 0;
            mBtn.setBackgroundResource(R.drawable.btn_blue_selector);
        }


        if (mPersonInfoBean.getIsFollow() == 0 && mPersonInfoBean.getIsFans() == 1) {
            mBtn.setText("通过好友");
            mBtn.setEnabled(true);
            chatStatus = 1;
            mBtn.setBackgroundResource(R.drawable.btn_blue_selector);
        }

        if (mPersonInfoBean.getIsFollow() == 1 && mPersonInfoBean.getIsFans() == 0) {
            mBtn.setText("等待验证");
            mBtn.setBackgroundResource(R.drawable.btn_blue_selector);
            mBtn.setEnabled(false);
            chatStatus = 2;
        }
    }

    @Override
    public void showsaveLinkRemark(Object o) {

    }

    //关注成功
    @Override
    public void showFollowSuccess() {
        if (isFollow) {
            ToastUtil.showShort(MyApplication.getInstance(), "取消关注成功");
            mPersonInfoBean.setIsFollow(0);
            tvFollow.setVisibility(View.VISIBLE);
            isFollow = false;
            EventBusUtil.post(new EventFocusBean(true, userCode));
            EventBusUtil.post(new EventFocusBean(false, userCode));
            setSendBtnUI();
        } else {
            ToastUtil.showShort(MyApplication.getInstance(), "关注成功");
            mPersonInfoBean.setIsFollow(1);
            tvFollow.setVisibility(View.INVISIBLE);
            isFollow = true;
            setSendBtnUI();
        }
    }

    //申请成功
    @Override
    public void showApplySuccess() {
        SpUtil.putLong(this, APPLY_TIME, System.currentTimeMillis());
        ToastUtil.showShort(MyApplication.getInstance(), "申请好友成功");
        tvFollow.setVisibility(View.INVISIBLE);
        mPersonInfoBean.setIsFollow(1);
        isFollow = true;
        setSendBtnUI();
    }

    @Subscribe
    public void onEvent(EventFocusBean bean) {
        if (bean.isFocus()) {
            mBtn.setText("发消息");
            mBtn.setEnabled(true);
            mPersonInfoBean.setIsFollow(1);
            mPersonInfoBean.setIsFans(1);
            mBtn.setBackgroundResource(R.drawable.btn_blue_selector);
        }
    }


    @Override
    public void setPresenter(PersonalInfoContract.Presenter presenter) {
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
        mPresenter.getUserInformation(infomap);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(MyApplication.getInstance(), info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(MyApplication.getInstance(), getString(R.string.empty_net_error));
    }

    //修改备注和描述
    @Subscribe
    public void onEvent(EventRemarkPersonalInfo bean) {
        mPresenter.getUserInformation(infomap);
    }

    @Subscribe
    public void onEvent(EventEditInfoBean bean) {
        User user = UserManager.getInstance().getUser();
        if (bean.status == EventEditInfoBean.CHANGENAME) {
            mTvRemark.setText(user.getName());
        } else if (bean.status == EventEditInfoBean.UPLOADAVATAR) {
            ImageHelper.loadHeadIcon(this, mIvImgHead, R.drawable.person_icon_head,
                    user.getHeadPic());
        } else if (bean.status == EventEditInfoBean.INTRO) {
            mTvIntro.setText(user.getIntroduction());
        } else if (bean.status == EventEditInfoBean.UPDATSEXSTATUS) {
            setSexStatus(user);
        }

    }

    public void setSexStatus(User user) {
        if ("2".equals(user.getSex())) {
            mDrawable = getResources().getDrawable(R.drawable.person_homepage_icon_nv);
            mDrawable.setBounds(0, 0, mDrawable.getBounds().width(),
                    mDrawable.getBounds().height());
            mTvRemark.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
        } else {
            mDrawable = getResources().getDrawable(R.drawable.person_homepage_icon_nan);
            mDrawable.setBounds(0, 0, mDrawable.getBounds().width(),
                    mDrawable.getBounds().height());
            mTvRemark.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConversationActivity.REQUEST_SHARE_CARD && resultCode == RESULT_OK) {
            EventSelectFriendForPostCardBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            sendRongIm(bean);
        }
        if (requestCode == 666 && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }

    private void connect() {
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        if (RongIM.getInstance().getCurrentConnectionStatus().equals(
                RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
            if (mPersonInfoBean != null) {
                Uri uri = Uri.parse(
                        "rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath(
                        "conversation").appendPath(
                        Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter(
                        "targetId", mPersonInfoBean.getUserCode()).appendQueryParameter("title",
                        TextUtils.isEmpty(
                                mPersonInfoBean.getRemarkName()) ? mPersonInfoBean.getName() : mPersonInfoBean.getRemarkName()).appendQueryParameter(
                        "flag", "1").build();
                startActivity(new Intent("android.intent.action.VIEW", uri));

            }
        } else if (this.getApplicationInfo().packageName.equals(
                MyApplication.getCurProcessName(MyApplication.getInstance()))) {
            RongIM.connect(UserManager.getInstance().getImToken(),
                    new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {
                            Log.d(TAG, "onTokenIncorrect: test");
                        }

                        @Override
                        public void onSuccess(String s) {
                            if (mPersonInfoBean != null) {
                                Uri uri = Uri.parse(
                                        "rong://" + PersonalInfoActivity.this.getApplicationInfo().packageName).buildUpon().appendPath(
                                        "conversation").appendPath(
                                        Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter(
                                        "targetId",
                                        mPersonInfoBean.getUserCode()).appendQueryParameter("title",
                                        TextUtils.isEmpty(
                                                mPersonInfoBean.getRemarkName()) ? mPersonInfoBean.getName() : mPersonInfoBean.getRemarkName()).appendQueryParameter(
                                        "flag", "1").build();
                                startActivity(new Intent("android.intent.action.VIEW", uri));
                            }

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            Log.d(TAG, "errorCode:" + errorCode);
                        }
                    });
        }
    }

    //允许私聊。0、允许；1、不允许
    public static void startActivity(Context context, String userCode, int privateChat) {
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        intent.putExtra("userCode", userCode);
        intent.putExtra("privateChat", privateChat);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String userCode) {
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }

}
