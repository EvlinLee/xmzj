package com.gxtc.huchuan.ui.circle.circleInfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.EventImgBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.TransCircleBeanEvent;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.GroupRuleDialog;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.groupmember.GroupRuleActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleInviteActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.mine.circle.CircleManagerActivity;
import com.gxtc.huchuan.ui.mine.circle.MembershipApproachActivity;
import com.gxtc.huchuan.ui.mine.deal.issueDeal.IssueDealActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.ui.pay.PayConstant;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.ShieldCircleDynamicHandler;
import com.gxtc.huchuan.utils.UMShareUtils;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
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
 * 圈子资料
 */
public class CircleInfoActivity extends BaseTitleActivity implements View.OnClickListener {

    @IdRes static int ADD_MASTER = 000;
    @IdRes static int ADD_MEMBER = 001;

    @BindView(R.id.img_icon)          ImageView    imgIcon;
    @BindView(R.id.tv_name)           TextView     tvName;
    @BindView(R.id.btn_edit)          TextView     tvEdit;
    @BindView(R.id.tv_info)           TextView     tvInfo;
    @BindView(R.id.btn_edit_detailed) TextView     tvDetailed;
    @BindView(R.id.tv_master_more)    TextView     btnMasterMore;
    @BindView(R.id.tv_member_more)    TextView     btnMemberMore;
    @BindView(R.id.layout_member)     View         layoutMember;
    @BindView(R.id.tv_exit_circle)    TextView     tvExitCircle;
    @BindView(R.id.btn_join_type)     TextView     btnJoinType;
    //@BindView(R.id.iv_qrcode)         ImageView    mIvQrcode;
    @BindView(R.id.switch_dynamic)    SwitchCompat switchDynamic;

    @BindView(R.id.layout_master1)    LinearLayout layoutMaster1;
    @BindView(R.id.layout_member2)    LinearLayout layoutMember2;
    @BindView(R.id.tv_label_qrcode)   TextView     tvLabelQrcode;
    @BindView(R.id.btn_join_manager)  TextView     btnJoinManager;
    @BindView(R.id.tv_group_id)       TextView     groupId;
    @BindView(R.id.btn_apply_refunds) TextView     btnRefunds;
    @BindView(R.id.line)              View         line;

    @BindView(R.id.tv_time)        TextView     tvTime;
    @BindView(R.id.tv_xufei)       TextView     tvRenew;
    @BindView(R.id.tv_price)       TextView     tvPrice;
    @BindView(R.id.tv_money_label) TextView     tvPriceLabel;
    @BindView(R.id.tv_surplus_day) TextView     tvSurplusDay;       //剩余天数
    @BindView(R.id.layout_master)  LinearLayout layoutMaster;
    @BindView(R.id.layout_member1) LinearLayout layoutMember1;

    private int id;

    private CircleBean              bean;
    private GroupRuleBean           mGroupRuleBean;
    private String                  token;
    private int                     isMy;            //是否是圈主，只有圈主和管理员才能修改资料，只有圈主才能删除圈子，其他成员（包括管理员）都只能退出圈子
    private int                     mMenberType;     //成员类型。0:普通用户，1：管理员，2：圈主
    private HashMap<String, Object> map;
    //    private PopQrCode               popQrCode;
    private String                  mQrCode;
    private AlertDialog             mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_circle_info));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
    }

    @Override
    public void initListener() {
        switchDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwitchCompat checkBox = (SwitchCompat) view;
                if(checkBox.isChecked()){
                    receiveDynamic(true);
                }else {
                    receiveDynamic(false);
                }
            }
        });
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);
        bean = (CircleBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        if (bean == null) return;
        mMenberType = bean.getMemberType();

        tvName.setText(bean.getGroupName());
        tvInfo.setText(bean.getContent());
        ImageHelper.loadRound(this, imgIcon, bean.getCover(), 2);

        //普通用户 ,不能编辑圈子资料
        if (mMenberType == 0) {
            tvExitCircle.setText(R.string.label_exit_circle);
            tvEdit.setText("圈子资料");
            btnJoinManager.setVisibility(View.GONE);
            btnJoinType.setVisibility(View.GONE);

            //管理员 ,可以编辑圈子资料
        } else if (mMenberType == 1) {
            tvExitCircle.setText(R.string.label_exit_circle);

            //圈主 ,可以编辑圈子资料并删除圈子
        } else {
            tvExitCircle.setText(R.string.label_del_circle);
        }

        getData();

    }

    private void getData() {
        if(TextUtils.isEmpty(token)){
            hideContentView();
        }

        token = UserManager.getInstance().getToken();
        sub =
                CircleApi.getInstance()
                         .getCircleInfo(token, bean.getId())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribeOn(Schedulers.io())
                         .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                             @Override
                             public void onSuccess(Object data) {
                                 showContentView();
                                 bean = (CircleBean) data;
                                 showData();
                             }

                             @Override
                             public void onError(String errorCode, String message) {
                                 ToastUtil.showShort(CircleInfoActivity.this, message);
                             }
                         }));
        RxTaskHelper.getInstance().addTask(this, sub);

        getRule();
    }

    //获取群规
    private void getRule() {
        sub = CircleApi.getInstance()
                       .getRole(UserManager.getInstance().getToken(),bean.getId())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribeOn(Schedulers.io()).subscribe(new ApiObserver<ApiResponseBean<GroupRuleBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                mGroupRuleBean = (GroupRuleBean) data;
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(MyApplication.getInstance(),message);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @OnClick({R.id.tv_master_more,
            R.id.tv_member_more,
            R.id.btn_edit,
            R.id.btn_edit_detailed,
            R.id.tv_exit_circle,
            R.id.layout_master1,
            R.id.layout_master,
            R.id.layout_member1,
            R.id.layout_member2,
            R.id.btn_join_manager,
            R.id.btn_join_type,
            //R.id.rl_circle_qrcode,
            R.id.rl_circle_invite,
            R.id.btn_apply_refunds,
            R.id.btn_rule,
            R.id.tv_creat,
            R.id.tv_xufei})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            //分享圈子
            case R.id.HeadRightImageButton:
                if (bean != null) {
                    shareCircle();
                }
                break;

            //管理员列表
            case R.id.tv_master_more:
            case R.id.layout_master:
            case R.id.layout_master1:
                gotoMemberList(EventCircleIntro.MANAGER);
                break;

            //成员列表
            case R.id.tv_member_more:
            case R.id.layout_member1:
            case R.id.layout_member2:
                gotoMemberList(EventCircleIntro.MEMBER);
                break;

            case R.id.btn_join_manager:
                Intent intent1 = new Intent(CircleInfoActivity.this, CircleManagerActivity.class);
                intent1.putExtra("circleId", bean.getId());
                intent1.putExtra("isMy", bean.getIsMy());
                startActivity(intent1);
                break;

            //成员加入方式
            case R.id.btn_join_type:
                Intent intent = new Intent(this, MembershipApproachActivity.class);
                intent.putExtra("circle_id", bean.getId());
                intent.putExtra("isFree", bean.getIsFee());
                intent.putExtra("fee", bean.getFee());
                intent.putExtra("pent", bean.getPent());
                startActivity(intent);
                break;

            //编辑圈子资料
            case R.id.btn_edit:
                gotoEdit();
                break;

            //圈子介绍详情页
            case R.id.btn_edit_detailed:
                gotoDetailed();
                break;

            //退出圈子/删除圈子
            case R.id.tv_exit_circle:
                exitOrDelCircle();
                break;

            //创建圈子群聊
            case R.id.tv_creat:
                mAlertDialog = DialogUtil.showDeportDialog(this, false, null,"创建群聊后将不能关闭群聊功能，确定创建？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(v.getId() == R.id.tv_dialog_confirm){
                                    createGroupChat();
                                }
                                mAlertDialog.dismiss();
                            }
                        });
                break;

            //二维码
            /*case R.id.rl_circle_qrcode:
                if (bean != null) {
                    if (UserManager.getInstance().isLogin()) {
                        intent = new Intent(CircleInfoActivity.this, ErWeiCodeActivity.class);
                        intent.putExtra("id", bean.getId());
                        startActivity(intent);
                    } else {
                        GotoUtil.goToActivity(CircleInfoActivity.this,
                                LoginAndRegisteActivity.class);
                    }
                }
                break;*/

            //邀请好友
            case R.id.rl_circle_invite:
                if (bean != null) {
                    shareCircle();
                }
                break;

            //申请退款
            case R.id.btn_apply_refunds:
                if ("2".equals(bean.getIsRefund()) || "4".equals(bean.getIsRefund())) {
                    showVertifanceDialog(bean);
                } else if ("1".equals(bean.getIsRefund())) {
                    GotoUtil.goToActivity(this, ApplyRefundsActivity.class, 0, bean);
                }
                break;

            //群规
            case R.id.btn_rule:
                if (bean == null) return;
                //群规如果是null 的 只有群主能编辑，若不是null 的 ，可以点寄进去查看群规详情
                //只有群主才能编辑
                if (1 == bean.getMemberType() || 2 == bean.getMemberType()) {
                    goToGroupRule();

                } else {
                    //公告为空，弹出对话框
                    if (bean != null && TextUtils.isEmpty(mGroupRuleBean.getRoletext())) {
                        showTipDialog();

                    //公告不为空，进入查看公告详情
                    } else {
                        showOpenDialog(this, mGroupRuleBean);
                    }
                }
                break;

            // 续费
            case R.id.tv_xufei:
                renew();
                break;
        }

        //添加管理员
        if (v.getId() == ADD_MASTER) {
            String url = bean.getShareUrl() + "1";
            CircleInviteActivity.startActivity(this, bean.getId() + "", url, bean.getName(),
                    bean.getCover(), bean.getBrokerage(), 1);
            return;
        }


        //添加成员
        if (v.getId() == ADD_MEMBER) {
            String url = bean.getShareUrl() + "0";
            CircleInviteActivity.startActivity(this, bean.getId() + "", url, bean.getName(),
                    bean.getCover(), bean.getBrokerage(), 0);
            return;
        }
    }

    private void createGroupChat() {
        Subscription subThumbsup = AllApi.getInstance().createGroupChat(token,bean.getId()).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(MyApplication.getInstance(),"创建群聊成功");
                        findViewById(R.id.tv_creat).setVisibility(View.GONE);
                        //发消息给上个页面
                        EventBusUtil.post(
                                new EventCircleIntro(EventCircleIntro.CREATE,
                                        bean.getId()));
                        CircleInfoActivity.this.finish();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subThumbsup);
    }

    //分享圈子  需要判断圈子是否已经通过审核
    private void shareCircle() {
        if (bean != null && "1".equals(bean.getIsShow())) {
            if (UserManager.getInstance().isLogin(this)) {
                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                UMShareUtils utils = new UMShareUtils(CircleInfoActivity.this);
                                utils.shareCircle(bean.getName(),bean.getContent(),bean.getCover(), bean.getBrokerage(), bean.getShareUrl());
                                utils.setOnItemClickListener(
                                        new UMShareUtils.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int flag) {
                                                if (0 == flag) {
                                                    IssueDynamicActivity.share(
                                                            CircleInfoActivity.this,
                                                            bean.getId() + "", "4", bean.getName(),
                                                            bean.getCover());
                                                }
                                                if (1 == flag) {
                                                    ConversationListActivity.startActivity(
                                                            CircleInfoActivity.this,
                                                            ConversationActivity.REQUEST_SHARE_CONTENT,
                                                            Constant.SELECT_TYPE_SHARE);
                                                }
                                                if (2 == flag) {
                                                    if(1 != bean.getIsCollect()){
                                                        collect(UserManager.getInstance().getToken(),bean.getId()+"");
                                                    }else {
                                                        ToastUtil.showShort(MyApplication.getInstance(),"你已经收藏过了");
                                                    }
                                                }
                                                if (3 == flag) {
                                                    Intent intent = new Intent(CircleInfoActivity.this, ErWeiCodeActivity.class);
                                                    intent.putExtra("id", bean.getId());
                                                    intent.putExtra("type", ErWeiCodeActivity.TYPE_CIRCLE);
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                            }

                            @Override
                            public void onPermissionDenied() {
                                mAlertDialog = DialogUtil.showDeportDialog(CircleInfoActivity.this,
                                        false, null, getString(R.string.pre_storage_notice_msg),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == R.id.tv_dialog_confirm) {
                                                    JumpPermissionManagement.GoToSetting(
                                                            CircleInfoActivity.this);
                                                }
                                                mAlertDialog.dismiss();
                                            }
                                        });
                            }
                        });
            }
        } else {
            ToastUtil.showShort(this, "您的圈子还未通过审核，无法分享！");
        }

    }

    private void collect(String token, String circleId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "8");
        map.put("bizId", circleId);
        Subscription subThumbsup = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        bean.setIsCollect(1);
                        ToastUtil.showShort(MyApplication.getInstance(),"收藏成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subThumbsup);
    }


    private void receiveDynamic(boolean isChcked) {
        int type;
        if (isChcked) {
            type = 0;
        } else {
            type = 1;
        }
        String token = UserManager.getInstance().getToken();
        ShieldCircleDynamicHandler.getInstant().receiveDynamic(token, bean.getId(), type, new ApiCallBack<Object>() {

            @Override
            public void onSuccess(Object data) {
               
            }

            @Override
            public void onError(String errorCode, String message) {
                if (switchDynamic == null) return;
                if (switchDynamic.isChecked()) {
                    switchDynamic.setChecked(false);
                } else {
                    switchDynamic.setChecked(true);
                }
                ToastUtil.showShort(CircleInfoActivity.this, message);
            }
        });
        ShieldCircleDynamicHandler.getInstant().addTask(this);
    }


    private AlertDialog dialog;
    private void showTipDialog() {
        dialog = DialogUtil.showInputDialog(this, false, "", "只有群主才能编辑公告",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
    }

    /*
     群规相关
     */
    private void showOpenDialog(Context context, GroupRuleBean data) {
        GroupRuleDialog mOpenDialog = new GroupRuleDialog(context, data);
        mOpenDialog.show();
    }

    private void goToGroupRule() {
        String isMy = "0";
        if (1 == bean.getMemberType() || 2 == bean.getMemberType()) {
            isMy = "1";
        }
        Intent intent = new Intent(this, GroupRuleActivity.class);
        intent.putExtra("groupId", bean.getId());
        intent.putExtra("isMy", isMy);
        startActivity(intent);
    }

    private void exitOrDelCircle() {
        map = new HashMap<>();
        map.put("token", token);
        map.put("groupId", bean.getId());
        //圈主 ,删除圈子
        if (bean.getIsMy() == 1) {
            delCircle();

        //不是圈主 ,退出圈子
        } else {
            exitCircle();
        }
    }


    private AlertDialog exitDialog;

    private void exitCircle() {
        exitDialog = DialogUtil.showInputDialog(this, false, null, "确认退出圈子？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sub = CircleApi.getInstance().quitCircle(map).observeOn(
                                AndroidSchedulers.mainThread()).subscribeOn(
                                Schedulers.io()).subscribe(
                                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        if (getBaseLoadingView() == null) return;
                                        exitDialog.dismiss();
                                        ToastUtil.showShort(CircleInfoActivity.this, "退出圈子成功");
                                        //发消息给上上个页面
                                        EventBusUtil.post(
                                                new EventCircleIntro(EventCircleIntro.EXIT,
                                                        bean.getId()));
                                        CircleInfoActivity.this.finish();
                                    }

                                    @Override
                                    public void onError(String errorCode, String message) {
                                        ToastUtil.showShort(CircleInfoActivity.this, message);
                                    }
                                }));
                        RxTaskHelper.getInstance().addTask(this, sub);
                    }
                });
    }

    private void delCircle() {
        DialogUtil.showInputDialog(this, false, null, "确认删除圈子？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub = CircleApi.getInstance().deleteCircle(map).observeOn(
                        AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                        new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if (getBaseLoadingView() == null) return;
                                ToastUtil.showShort(CircleInfoActivity.this, "删除圈子成功");
                                //发消息给上上个页面
                                //EventBusUtil.postStickyEvent(new EventCircleIntro(EventCircleIntro.DELETE));
                                EventBusUtil.post(new EventCircleIntro(EventCircleIntro.DELETE,
                                        bean.getId()));
                                CircleInfoActivity.this.finish();
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(CircleInfoActivity.this, message);
                            }
                        }));
                RxTaskHelper.getInstance().addTask(this, sub);
                finish();
            }
        });

    }

    private void gotoDetailed() {
        CommonWebViewActivity.startActivity(this, bean.getJoinUrl() + "&from=android", "圈子详情");
        /*if (mMenberType == 0) {
            CommonWebViewActivity.startActivity(this, bean.getJoinUrl() + "&from=android", "圈子详情");
        } else {
            Intent intent = new Intent(this, EditCircleInfoActivity.class);
            intent.putExtra("groupId", bean.getId());
            intent.putExtra("isMy", 1);
            startActivity(intent);
        }*/

    }

    private void gotoMemberList(int flag) {
//        if (bean.getDispark() == 1 ) {
//            ToastUtil.showShort(this, "仅管理员可见");
//            return;
//        }
        if(flag == EventCircleIntro.MANAGER && bean.getMemberType() == 0){
            ToastUtil.showShort(this, "仅管理员可见");
            return;
        }
        EventBusUtil.postStickyEvent(new EventCircleIntro(flag));
        GotoUtil.goToActivity(this, CircleMemberActivity.class, 0, bean);
    }

    private void gotoEdit() {
        GotoUtil.goToActivity(this, CircleEditActivity.class, 0, bean);
    }


    private Subscription sub;

    private void showData() {
        if (layoutMaster == null) return;
        layoutMaster.removeAllViews();
        layoutMember1.removeAllViews();
        mQrCode = bean.getQrUrl();
        layoutMember.setVisibility(View.VISIBLE);
        //ImageHelper.loadHeadIcon(this, mIvQrcode, bean.getQrUrl());
        ImageHelper.loadRound(this, imgIcon, bean.getCover(), 2);

        if (TextUtils.isEmpty(bean.getBackgCover())) {
            ImageHelper.loadImage(this, imgIcon, bean.getCover());
        } else {
            ImageHelper.loadImage(this, imgIcon, bean.getBackgCover());
        }

        String time = "创建时间   " + DateUtil.formatTime(bean.getCreateTime(), "yyyy-MM-dd");
        tvTime.setText(time);
        groupId.setText("ID:" + bean.getGroupCode());

        if (1 == bean.getIsFee()) {
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText("￥ " + bean.getFee());
            tvPrice.setTextColor(getResources().getColor(R.color.red));

            long diff = bean.getUserEndtime() - System.currentTimeMillis();
            if(diff > 0){
                String[] strings = DateUtil.countDownNotAddZero(diff);
                String text = "";
                if (!strings[0].equals("0")) {
                    text = strings[0] + "天后";

                } else if (!strings[1].equals("0")) {
                    text = strings[1] + "小时后";

                }else if (!strings[2].equals("0")) {
                    text = strings[2] + "分后";

                }else if (!strings[3].equals("0")) {
                    text = strings[3] + "秒后";
                }
                tvSurplusDay.setText(text);
                tvSurplusDay.setVisibility(View.VISIBLE);
            }
            tvRenew.setVisibility(bean.getUserEndtime() == 0 ? View.INVISIBLE : View.VISIBLE);
            tvPriceLabel.setVisibility(View.VISIBLE);

        } else {
            tvPrice.setText("免费");
            tvPrice.setTextColor(getResources().getColor(R.color.text_color_999));
            tvSurplusDay.setVisibility(View.INVISIBLE);
            tvRenew.setVisibility(View.INVISIBLE);
            tvPriceLabel.setVisibility(View.INVISIBLE);
        }

        String adminNum   = bean.getAdminNum() + "";
        String regularNum = bean.getRegularNum() + "";

        btnMasterMore.setText(adminNum);
        btnMemberMore.setText(regularNum);

        //管理成员列表
        List<CircleBean.Member> masters = bean.getAdminMembers();
        for (int i = 0; i < (masters.size() < 5 ? masters.size() : 5); i++) {
            CircleBean.Member member = masters.get(i);
            ImageView         img    = new ImageView(this);
            int               width  = (int) getResources().getDimension(R.dimen.px70dp);
            LayoutParams      params = new LayoutParams(width, width);
            params.rightMargin = (int) getResources().getDimension(R.dimen.margin_middle);
            img.setLayoutParams(params);
            ImageHelper.loadCircle(this, img, member.getUserPic(), R.drawable.circle_head_icon_120);

            layoutMaster.addView(img);
        }

        //是否是群主
        if (bean.getMemberType() == 2) {
            //添加管理员按钮
            ImageView    img    = new ImageView(this);
            int          width  = (int) getResources().getDimension(R.dimen.px70dp);
            LayoutParams params = new LayoutParams(width, width);
            params.rightMargin = (int) getResources().getDimension(R.dimen.margin_middle);
            img.setLayoutParams(params);
            img.setImageResource(R.drawable.circle_data_icon_add);
            img.setId(ADD_MASTER);
            img.setOnClickListener(this);
            layoutMaster.addView(img);

            //仅有圈主才有为该群创建群聊的权限
            if(bean.getCreateGroupChat() == 0){
                //已经创建群聊
                findViewById(R.id.tv_creat).setVisibility(View.GONE);
                findViewById(R.id.line1).setVisibility(View.GONE);
            }

            if(bean.getCreateGroupChat() == 1){
                //还没创建群聊
                findViewById(R.id.tv_creat).setVisibility(View.VISIBLE);
                findViewById(R.id.line1).setVisibility(View.VISIBLE);
            }

        }

        //不可以退款
        if (bean.getIsRefund().equals("0")) {
            btnRefunds.setVisibility(View.GONE);
            line.setVisibility(View.GONE);

        //可以退款
        } else if (bean.getIsRefund().equals("1")) {
            btnRefunds.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);

        //审核中
        } else if (bean.getIsRefund().equals("2")) {
            btnRefunds.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            btnRefunds.setText("审核中");
            btnRefunds.setClickable(true);

        //审核成功
        } else if (bean.getIsRefund().equals("3")) {
            btnRefunds.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            btnRefunds.setText("审核成功");
            btnRefunds.setClickable(false);

        //审核不通过
        } else if (bean.getIsRefund().equals("4")) {
            btnRefunds.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            btnRefunds.setText("审核不通过");
            btnRefunds.setClickable(true);
        }


        //普通成员列表
        List<CircleBean.Member> members = bean.getGroupMembers();
        for (int i = 0; i < (members.size() < 5 ? members.size() : 5); i++) {
            CircleBean.Member member  = members.get(i);
            ImageView         imgM    = new ImageView(this);
            int               widthM  = (int) getResources().getDimension(R.dimen.px70dp);
            LayoutParams      paramsM = new LayoutParams(widthM, widthM);
            paramsM.rightMargin = (int) getResources().getDimension(R.dimen.margin_middle);
            imgM.setLayoutParams(paramsM);
            ImageHelper.loadCircle(this, imgM, member.getUserPic(), R.drawable.circle_head_icon_120);

            layoutMember1.addView(imgM);
        }

        //添加普通成员按钮
        ImageView    img1    = new ImageView(this);
        int          width1  = (int) getResources().getDimension(R.dimen.px70dp);
        LayoutParams params1 = new LayoutParams(width1, width1);
        params1.rightMargin = (int) getResources().getDimension(R.dimen.margin_middle);
        img1.setLayoutParams(params1);
        img1.setImageResource(R.drawable.circle_data_icon_add);
        img1.setId(ADD_MEMBER);
        img1.setOnClickListener(this);
        layoutMember1.addView(img1);

        //接受圈子动态
        if (bean.getIsReceive() == 0) {
            switchDynamic.setChecked(true);
        } else {
            switchDynamic.setChecked(false);
        }

    }

    VertifanceFlowDialog mVertifanceFlowDialog;

    private void showVertifanceDialog(final CircleBean mCircleBean) {
        if (mVertifanceFlowDialog == null) {
            mVertifanceFlowDialog = new VertifanceFlowDialog(this);
        }
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVertifanceFlowDialog.dismiss();
            }
        });
        switch (mCircleBean.getIsRefund()) {
            case "2":
                mVertifanceFlowDialog.setFlowStatus("申请中...");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
                break;
            case "4":
                mVertifanceFlowDialog.setFlowStatus("申请不通过：" + mCircleBean.getRefundRemark());
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
                break;
        }
    }

    //续费
    private void renew(){
        OrdersRequestBean requestBean = new OrdersRequestBean();

        BigDecimal moneyB    = new BigDecimal(bean.getFee());
        double     total     = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
        String     token     = UserManager.getInstance().getToken();
        String     transType = "GJ";
        String     extra     = "{\"groupId\":\"" + bean.getId() + "\"}";

        requestBean.setTotalPrice(total + "");
        requestBean.setToken(token);
        requestBean.setTransType(transType);
        requestBean.setExtra(extra);
        requestBean.setGoodsName("圈子续费");

        GotoUtil.goToActivity(this, PayActivity.class, 101, requestBean);
    }

    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan) {
        String title = bean.getName();
        String img   = bean.getCover();
        String id    = bean.getId() + "";

        // infotype 使用 5 表示走佣金模式
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "4",
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(CircleInfoActivity.this, "分享成功");
                        if(!TextUtils.isEmpty(liuyan)){
                            RongIMTextUtil.INSTANCE.relayMessage
                                    (liuyan,targetId,type);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(CircleInfoActivity.this,
                                "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }


    //圈子介绍
    @Subscribe
    public void onEvent(EventCircleIntro bean) {
        bean.setIntro(bean.getIntro());
        tvInfo.setText(bean.getIntro());
    }

    //圈主被转让了
    @Subscribe
    public void onEvent(TransCircleBeanEvent bean) {
        finish();
    }

    //修改圈子封面图
    @Subscribe(sticky = true)
    public void onEvent(EventImgBean bean) {
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        ShieldCircleDynamicHandler.getInstant().cancelTask(this);
        EventBusUtil.unregister(this);
        mAlertDialog = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            shareMessage(targetId, type,bean.liuyan);
        }

        if(requestCode == 101 && resultCode == PayConstant.PAY_RESULT){
            getData();
        }
    }
}
