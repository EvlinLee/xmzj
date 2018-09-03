package com.gxtc.huchuan.ui.circle.groupmember;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.GroupMemberListAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.GroupRuleDialog;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleInfoActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleInviteActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.circle.homePage.InviteActivity;
import com.gxtc.huchuan.ui.mine.circle.CircleManagerActivity;
import com.gxtc.huchuan.ui.mine.circle.NoticeListActivity;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.pay.PayConstant;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.MyActionSheetDialog;
import com.gxtc.huchuan.widget.MyGridView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;



/**
 * Describe:群聊成员
 * Created by ALing on 2017/6/6.
 */

public class GroupChatMembersActivity extends BaseTitleActivity implements View.OnClickListener, GroupRuleContract.View {

    private static final String TAG = GroupChatMembersActivity.class.getSimpleName();

    @BindView(R.id.chat_menber_list)  MyGridView   mChatMenberList;
    @BindView(R.id.sw_message_free)   Switch       swMessageFree;
    @BindView(R.id.tv_all_member)     TextView     tvAllMember;
    @BindView(R.id.tv_group_rule)     TextView     tvGroupRule;
    @BindView(R.id.forbident_say)     TextView     tvForbident;
    @BindView(R.id.rv_circle_manager) TextView    tvCircleManager;
    @BindView(R.id.ll_group_rule)      LinearLayout mLLGroupRule;
    @BindView(R.id.forbident_status)  TextView     forbidentStatus;
    @BindView(R.id.forbident_say_layout)  View    forbidentSayLayout;
    @BindView(R.id.forbident_sent_dynamic_layout)  View    forbidentSentDynamicLayout;
    @BindView(R.id.forbident_sent_dynamic)          TextView    forbidentSentDynamicText;
    @BindView(R.id.forbident_sent_dynamic_status)  TextView    forbidentSentDynamicStatus;

    @BindView(R.id.line1)               View line1;
    @BindView(R.id.line2)               View line2;
    @BindView(R.id.line3)               View line3;

    private ImageView                   ivBack;
    private TextView                    tvChatCount;

    private int                         count;
    private int                         id;
    private String                      targetId;
    private String                      isMy;
    private List<CircleMemberBean>      list;

    private GroupMemberListAdapter      adapter;
    private GroupRuleContract.Presenter mPresenter;

    private GroupRuleBean               bean;
    private CircleBean roleBean;
    private CircleBean infoBean;
    private  String isShutup = "0";
    private AlertDialog mAlertDialog;
    private HashMap<String, Object> map;
    private AlertDialog exitDialog;
    public String clickType = "1";
    public String type ;
    private AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_members);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        View head = LayoutInflater.from(this).inflate(R.layout.layout_title_group_chat_info, getBaseHeadView().getParentView(), false);
        getBaseHeadView().getParentView().addView(head);
        getBaseHeadView().showHeadRightButton("进入圈子",this);
        ivBack = (ImageView) head.findViewById(R.id.iv_back);
        tvChatCount = (TextView) head.findViewById(R.id.tv_chat_count);
        ivBack.setOnClickListener(this);
        mLLGroupRule.setOnClickListener(this);
    }

    @Override
    public void initData() {
        new GroupRulePresenter(this);
        if (getIntent().getIntExtra("groupId", 0) != 0) {
            id = getIntent().getIntExtra("groupId", 0);
            isMy = getIntent().getStringExtra("isMy");
            count = getIntent().getIntExtra("count",0);

            targetId = getIntent().getStringExtra(Constant.INTENT_DATA);
            initGroupData();
            getMemberByChat(MyApplication.getInstance(),targetId);
        }

        tvChatCount.setText("聊天信息(" + count + ")");

        mPresenter.getGroupRule(id);
    }


    @Override
    public void initListener() {
        swMessageFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMessageNotif(isChecked);
            }
        });
    }

    //设置消息免打扰
    private void setMessageNotif(final boolean isChecked) {
        Conversation.ConversationNotificationStatus cns;
        if (isChecked) {
            cns = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
        } else {
            cns = Conversation.ConversationNotificationStatus.NOTIFY;
        }
        RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.GROUP,
                targetId, cns,
                new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                    @Override
                    public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {}

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(MyApplication.getInstance(),RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }

    private void initGroupData() {
        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP,
                targetId,
                new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                    @Override
                    public void onSuccess(
                            Conversation.ConversationNotificationStatus conversationNotificationStatus) {

                        if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                            swMessageFree.setChecked(true);
                        } else {
                            swMessageFree.setChecked(false);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(MyApplication.getInstance(), RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }


    private void getMenberList() {

        long loadTime= System.currentTimeMillis();
        getBaseLoadingView().showLoading();
        Subscription sub =
            CircleApi.getInstance().getListMember1(targetId, 0, 40, loadTime).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<CircleMemberBean>>>(
                        new ApiCallBack<List<CircleMemberBean>>() {
                            @Override
                            public void onSuccess(List<CircleMemberBean> data) {
                                getBaseLoadingView().hideLoading();
                                if (tvChatCount == null) return;
                                list = data;

                                CircleMemberBean addBean = new CircleMemberBean();
                                addBean.setUserCode("+");
                                if (list.size() > 30) {
                                    tvAllMember.setVisibility(View.VISIBLE);
                                    tvAllMember.setOnClickListener(GroupChatMembersActivity.this);

                                    list.add(29, addBean);
                                }else{
                                    list.add(addBean);
                                }


                                showMemberList();
                            }

                                @Override
                                public void onError(String errorCode, String message) {
                                    LoginErrorCodeUtil.showHaveTokenError(GroupChatMembersActivity.this,
                                            errorCode, message);
                                }
                            }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void showMemberList() {
        adapter = new GroupMemberListAdapter(GroupChatMembersActivity.this, list, R.layout.item_group_chat_member, false);
        mChatMenberList.setAdapter(adapter);
        mChatMenberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转个人资料
                CircleMemberBean circleMemberBean = (CircleMemberBean) adapter.getItem(position);
                if("+".equals(circleMemberBean.getUserCode())){
                    getGroupInfo();
                }else{
                    PersonalInfoActivity.startActivity(GroupChatMembersActivity.this, circleMemberBean.getUserCode());
                }
            }
        });
    }

    @OnClick({R.id.ll_notice,R.id.forbident_say,R.id.rv_circle_manager,R.id.tv_clear_conversation,R.id.tv_reported,R.id.tv_invated_friends,R.id.tv_exits_circle,R.id.forbident_say_layout,
            R.id.forbident_sent_dynamic_layout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.headRightButton:
                Intent intent = new Intent(this,CircleMainActivity.class);
                intent.putExtra("groupId",id);
                startActivity(intent);
                break;

            //退出圈子
            case R.id.tv_exits_circle:
                exitOrDelCircle();
                break;

            //显示全部成员
            case R.id.tv_all_member:
                gotoAllMember();
                break;

            //群规
            case R.id.ll_group_rule:
                //群规如果是null 的 只有群主能编辑，若不是null 的 ，可以点寄进去查看群规详情
                //只有群主才能编辑
                if(roleBean == null || roleBean.getRoleType() == null) return;

                if (roleBean.getRoleType() != null && "1".equals(roleBean.getRoleType()) || "2".equals(roleBean.getRoleType())) {
                    goToGroupRule();

                } else {
                    //公告为空，弹出对话框
                    if (bean != null && TextUtils.isEmpty(bean.getRoletext())) {
                        showTipDialog();

                    //公告不为空，进入查看公告详情
                    } else {
                        showOpenDialog(this, bean);
                    }
                }
                break;

            //公告
            case R.id.ll_notice:
                gotoNotice();
                break;

            //邀请好友
            case R.id.tv_invated_friends:
                getGroupInfo();
                break;

            //举报圈子
            case R.id.tv_reported:
                reported();
                break;

            //禁言
            case R.id.forbident_say_layout:

                if("0".equals(isShutup)){
                    showforbidentDialog(this);
                }
                 if(roleBean == null ||  roleBean.getIsShutupTiming() == null) return;
                if("1".equals(isShutup)){
                    String text = "";
                    text = "确定要取消全体禁言";
                    if("1".equals(roleBean.getIsShutupTiming())){
                         text = "确定要取消定时禁言";
                    }
                    mAlertDialog = DialogUtil.showDeportDialog(GroupChatMembersActivity.this, false, null, text,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(v.getId() == R.id.tv_dialog_confirm){
                                        shutup(isShutup);
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                }
                break;

            //禁发动态
            case R.id.forbident_sent_dynamic_layout:
                if(roleBean == null || roleBean.getIsUnRelinfo() == null) return;

                if("0".equals(roleBean.getIsUnRelinfo())){
                    showforbidentSentDynamicDialog(this);
                }

                if("1".equals(roleBean.getIsUnRelinfo())){
                    String text = "";
                    text = "确定要取消全体禁发动态";
                    if("1".equals(roleBean.getIsUnRelinfoTiming())){
                        text = "确定要取消定时禁发动态";
                    }
                    mAlertDialog = DialogUtil.showDeportDialog(GroupChatMembersActivity.this, false, null, text,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(v.getId() == R.id.tv_dialog_confirm){
                                        forbidentSentDynamic();
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                }

                break;

            //圈子管理
            case R.id.rv_circle_manager:
                if(roleBean == null || roleBean.getRoleType() == null)    return;
                Intent intent2 = new Intent(getApplicationContext(),CircleManagerActivity.class);
                intent2.putExtra("circleId", id);
                intent2.putExtra("targetId", targetId);
                intent2.putExtra("isMy", roleBean.getRoleType());
                startActivity(intent2);
                break;

            //清除聊天记录
            case R.id.tv_clear_conversation:
                clearConversation();
                break;
        }
    }

    public void showforbidentDialog(final Context context) {

        final ArrayList<String> itemList = new ArrayList<>();
        itemList.add("全体禁言");
        itemList.add("定时禁言");
        final String[] contents = new String[itemList.size()];
        final MyActionSheetDialog dialog = new MyActionSheetDialog(context,
                itemList.toArray(contents), null);
        dialog.isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginTop(
                10).cancelMarginBottom(0).cornerRadius(0f).dividerHeight(1).itemTextColor(
                getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (itemList.get(position)) {
                        case "全体禁言":
                            final String text = "确定要全体禁言";
                            mAlertDialog = DialogUtil.showDeportDialog(GroupChatMembersActivity.this, false, null, text,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(v.getId() == R.id.tv_dialog_confirm){
                                                shutup(isShutup);
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                            break;
                        case "定时禁言":
                            CustomTimeForShutUpActivity.jumpToCustomTimeForShutUpActivity(GroupChatMembersActivity.this,targetId,GroupChatMembersActivity.this.id);
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showforbidentSentDynamicDialog(final Context context) {
        final ArrayList<String> itemList = new ArrayList<>();
        itemList.add("全体禁发动态");
        itemList.add("定时禁发动态");
        final String[] contents = new String[itemList.size()];
        final MyActionSheetDialog dialog = new MyActionSheetDialog(context,
                itemList.toArray(contents), null);
        dialog.isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginTop(
                10).cancelMarginBottom(0).cornerRadius(0f).dividerHeight(1).itemTextColor(
                getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (itemList.get(position)) {
                        case "全体禁发动态":
                            final String text = "确定要全体禁发动态?";
                            mAlertDialog = DialogUtil.showDeportDialog(GroupChatMembersActivity.this, false, null, text,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(v.getId() == R.id.tv_dialog_confirm){
                                                forbidentSentDynamic();
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                            break;
                        case "定时禁发动态":
                            CustomeTimeToForbidentSentDynamicActivity.jumpToCustomeTimeToForbidentSentDynamicActivity(GroupChatMembersActivity.this,targetId,GroupChatMembersActivity.this.id);
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //禁发动态跟定时禁言是一个接口
    public void forbidentSentDynamic(){
        HashMap<String,String> map = new HashMap();
        map.put("type","3"); // 1 聊天室(定时禁言)  2 定时禁发  3 全体禁发
        map.put("clickType",clickType); //0解除  1设置
        map.put("groupChatId",targetId);
        map.put("groupId",id+"");
        map.put("token",UserManager.getInstance().getToken());
        Subscription sub = CircleApi.getInstance().setGroupTaskInTiming(map)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
                 .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        switch (clickType){
                            case "0":
                                ToastUtil.showShort(MyApplication.getInstance(),"取消禁发动态成功");
                                forbidentSentDynamicText.setText("禁发动态");
                                forbidentSentDynamicStatus.setText("");
                                roleBean.setIsUnRelinfo("0");
                                clickType = "1";
                                break;
                            case "1":
                                ToastUtil.showShort(MyApplication.getInstance(),"全体禁发动态成功");
                                forbidentSentDynamicText.setText("取消禁发动态");
                                forbidentSentDynamicStatus.setText("永久禁发动态");
                                roleBean.setIsUnRelinfo("1");
                                clickType = "0";
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


    private void showShareDialog() {
        if (infoBean != null && "1".equals(infoBean.getIsShow())) {
            if (UserManager.getInstance().isLogin(this)) {
                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                UMShareUtils utils = new UMShareUtils(GroupChatMembersActivity.this);
                                utils.shareCircle(infoBean.getName(),infoBean.getContent(),infoBean.getCover(), infoBean.getBrokerage(), infoBean.getShareUrl());
                                utils.setOnItemClickListener(
                                        new UMShareUtils.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int flag) {
                                                if (0 == flag) {
                                                    IssueDynamicActivity.share(
                                                            GroupChatMembersActivity.this,
                                                            infoBean.getId() + "", "4", infoBean.getName(),
                                                            infoBean.getCover());
                                                }
                                                if (1 == flag) {
                                                    ConversationListActivity.startActivity(
                                                            GroupChatMembersActivity.this,
                                                            ConversationActivity.REQUEST_SHARE_CONTENT,
                                                            Constant.SELECT_TYPE_SHARE);
                                                }
                                                if (2 == flag) {
                                                   collect(UserManager.getInstance().getToken(),infoBean.getId()+"");
                                                }
                                                if (3 == flag) {
                                                    Intent intent = new Intent(GroupChatMembersActivity.this, ErWeiCodeActivity.class);
                                                    intent.putExtra("id", infoBean.getId());
                                                    intent.putExtra("type", 0);
                                                    intent.putExtra(Constant.INTENT_DATA, infoBean.getQrUrl());
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                            }

                            @Override
                            public void onPermissionDenied() {
                                mAlertDialog = DialogUtil.showDeportDialog(GroupChatMembersActivity.this,
                                        false, null, getString(R.string.pre_storage_notice_msg),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == R.id.tv_dialog_confirm) {
                                                    JumpPermissionManagement.GoToSetting(
                                                            GroupChatMembersActivity.this);
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
                        switch (infoBean.getIsCollect()){
                            case 0:
                                ToastUtil.showShort(MyApplication.getInstance(),"收藏成功");
                                infoBean.setIsCollect(1);
                                break;
                            case 1:
                                ToastUtil.showShort(MyApplication.getInstance(),"取消收藏成功");
                                infoBean.setIsCollect(0);
                                break;
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subThumbsup);
    }

    private void exitOrDelCircle() {
        if(bean == null || bean.getGroupId() == null || bean.getGroupId().equals("")) return;
        map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("groupId", bean.getGroupId());
        exitCircle();
    }

    private void exitCircle() {
        exitDialog = DialogUtil.showInputDialog(this, false, null, "确认退出圈子？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      Subscription  sub = CircleApi.getInstance().quitCircle(map).observeOn(
                                AndroidSchedulers.mainThread()).subscribeOn(
                                Schedulers.io()).subscribe(
                                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        if (getBaseLoadingView() == null) return;
                                        exitDialog.dismiss();
                                        ToastUtil.showShort(GroupChatMembersActivity.this, "退出圈子成功");
                                        clearConversationList();
                                    }

                                    @Override
                                    public void onError(String errorCode, String message) {
                                        ToastUtil.showShort(GroupChatMembersActivity.this, message);
                                    }
                                }));
                        RxTaskHelper.getInstance().addTask(this, sub);
                    }
                });
    }


    //清楚会话列表中的相关圈子群聊, 如果是管理员的话一个圈子会有多个群聊的 这里注意下
    private void clearConversationList(){
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if(conversations == null) return;
                if(!TextUtils.isEmpty(targetId)){
                    List<String> targetIds = new ArrayList<String>();
                    targetIds.add(targetId);

                    for(Conversation conversation: conversations){
                        if(conversation.getTargetId().startsWith(targetId + "_")){
                            targetIds.add(conversation.getTargetId());
                        }
                    }

                    for(String id: targetIds){
                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, id, null);
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        }, Conversation.ConversationType.GROUP);

        //把相应的圈子会话删除
        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP,
                targetId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        setResult(RESULT_OK);
                        GroupChatMembersActivity.this.finish();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        RIMErrorCodeUtil.handleErrorCode(errorCode);
                    }
                });
    }

    private  void getGroupInfo(){
        getBaseLoadingView().showLoading();
        Subscription  sub = CircleApi.getInstance()
                         .getCircleInfo(UserManager.getInstance().getToken(), id)
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribeOn(Schedulers.io())
                         .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                             @Override
                             public void onSuccess(Object data) {
                                 getBaseLoadingView().hideLoading();
                                 if(data != null){
                                     infoBean = (CircleBean) data;
                                     showShareDialog();
                                 }
                             }

                             @Override
                             public void onError(String errorCode, String message) {
                                 getBaseLoadingView().hideLoading();
                                 ToastUtil.showShort(GroupChatMembersActivity.this, message);
                             }
                         }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    private void reported() {
        ReportActivity.jumptoReportActivity(this,id+"","6");
    }

    private void clearConversation() {
        mDialog =
        DialogUtil.showInputDialog(this, false, "", "确认清除聊天记录？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, targetId,
                        new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                if(aBoolean){
                                    ToastUtil.showShort(GroupChatMembersActivity.this,"清除成功");
                                }else{
                                    ToastUtil.showShort(GroupChatMembersActivity.this,"清除失败");
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                RIMErrorCodeUtil.handleErrorCode(errorCode);
                            }
                        });
                mDialog.dismiss();
            }
        });
    }

    private void shutup(String type) {
        Subscription sub =
                CircleApi.getInstance()
                        .shutup(UserManager.getInstance().getToken(),targetId,type)//0：禁言；1：解禁
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if(getBaseHeadView() == null) return;
                                if("0".equals(isShutup)){
                                    isShutup = "1";
                                    tvForbident.setText("取消禁言");
                                    forbidentStatus.setText("永久禁言");
                                    ToastUtil.showShort(MyApplication.getInstance(),"禁言成功");
                                } else {
                                    isShutup = "0";
                                    forbidentStatus.setText("");
                                    tvForbident.setText("禁言");
                                    ToastUtil.showShort(MyApplication.getInstance(),"取消成功");
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(MyApplication.getInstance(), message);
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this,sub);
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

        //从定时禁言界面返回
        if (requestCode == 10005 && resultCode == RESULT_OK) {
            if(data != null){
                String startTimeL = data.getStringExtra("startTimeL");
                String endTimeL = data.getStringExtra("endTimeL");
                forbidentStatus.setText(DateUtil.stampToDate(startTimeL,"MM-dd HH:mm") + "至" + DateUtil.stampToDate(endTimeL,"MM-dd HH:mm"));
                tvForbident.setText("取消禁言");
                isShutup = "1";
                roleBean.setIsShutupTiming("1");
            }
        }

        //从定时禁发动态界面返回
        if (requestCode == 10006 && resultCode == RESULT_OK) {
            if(data != null){
                String startTimeL = data.getStringExtra("startTimeL");
                String endTimeL = data.getStringExtra("endTimeL");
                forbidentSentDynamicStatus.setText(DateUtil.stampToDate(startTimeL,"MM-dd HH:mm") + "至" + DateUtil.stampToDate(endTimeL,"MM-dd HH:mm"));
                forbidentSentDynamicText.setText("取消禁言");
                clickType = "0";
                roleBean.setIsUnRelinfoTiming("1");
                roleBean.setIsUnRelinfo("1");
            }
        }
    }

    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan) {
        String title = infoBean.getName();
        String img   = infoBean.getCover();
        String id    = infoBean.getId() + "";

        // infotype 使用 5 表示走佣金模式
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "4",
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(GroupChatMembersActivity.this, "分享成功");
                        if(!TextUtils.isEmpty(liuyan)){
                            RongIMTextUtil.INSTANCE.relayMessage
                                    (liuyan,targetId,type);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(GroupChatMembersActivity.this,
                                "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }

    private void gotoNotice(){
        Intent intent = new Intent(MyApplication.getInstance(),NoticeListActivity.class);
        intent.putExtra(Constant.INTENT_DATA,id);
        intent.putExtra("targetId",targetId);
        if("1".equals(isMy) || "2".equals(isMy)){
            intent.putExtra("edit",true);
        }else{
            intent.putExtra("edit",false);
        }
        startActivity(intent);
    }

    private void showOpenDialog(Context context, GroupRuleBean data) {
        GroupRuleDialog mOpenDialog = new GroupRuleDialog(context, data);
        mOpenDialog.show();
    }


    private void gotoAllMember() {
        Intent intent = new Intent(GroupChatMembersActivity.this, AllGroupMemberActivity.class);
        intent.putExtra("groupId", id);
        intent.putExtra("targetId", targetId);
        intent.putExtra("count", count);

        startActivity(intent);
    }

    private void showTipDialog() {
        mAlertDialog = DialogUtil.showInputDialog(this, false, "", "只有群主才能编辑群规",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        mPresenter.destroy();
        mAlertDialog = null;
        EventBusUtil.unregister(this);
    }

    @Override
    public void showGroupRule(GroupRuleBean data) {
        bean = data;
    }

    private void goToGroupRule() {
        Intent intent = new Intent(this, GroupRuleActivity.class);
        intent.putExtra("groupId", id);
        intent.putExtra("isMy", roleBean.getRoleType());
        startActivity(intent);
    }

    //根据群聊ID获取圈子角色
    private void getMemberByChat(final Context context,String id) {
         Subscription sub =
                 CircleApi.getInstance()
                        .getMemberByChat(UserManager.getInstance().getToken(),id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if(getBaseHeadView() == null) return;
                                roleBean = (CircleBean) data;
                                isMy = roleBean.getRoleType();
                                showAllAndCircleManager();
                                //开放了成员列表才能看,否则只有管理员能看到
                                if(roleBean.getDispark() == 0 || !"0".equals(roleBean.getRoleType())){
                                    getMenberList();
                                }
                                if(roleBean.getDispark() == 1 && "0".equals(roleBean.getRoleType())){
                                    tvAllMember.setText("圈主设置不开放成员");
                                    tvAllMember.setVisibility(View.VISIBLE);
                                    mChatMenberList.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(context, message);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void showAllAndCircleManager() {
        if("1".equals(roleBean.getRoleType()) || "2".equals(roleBean.getRoleType())){
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            forbidentSayLayout.setVisibility(View.VISIBLE);
            forbidentSentDynamicLayout.setVisibility(View.VISIBLE);
            tvCircleManager.setVisibility(View.VISIBLE);

            if("0".equals(roleBean.getIsShutup())){
                isShutup = "0";
                tvForbident.setText("禁言");
                forbidentStatus.setText("");
            }else {
                isShutup = "1";
                tvForbident.setText("取消禁言");
                if("1".equals(roleBean.getIsShutupTiming())){
                    forbidentStatus.setText(DateUtil.stampToDate(String.valueOf(roleBean.getShutupStartTime()),"MM-dd HH:mm")
                            + "至" + DateUtil.stampToDate(String.valueOf(roleBean.getShutupEndTime()),"MM-dd HH:mm"));
                }else {
                    forbidentStatus.setText("永久禁言");
                }
            }

            if("0".equals(roleBean.getIsUnRelinfo())){
                clickType = "1";
                forbidentSentDynamicText.setText("禁发动态");
                forbidentSentDynamicStatus.setText("");
            }else {
                clickType = "0";
                forbidentSentDynamicText.setText("取消禁发动态");
                if("1".equals(roleBean.getIsUnRelinfoTiming())){
                    forbidentSentDynamicStatus.setText(DateUtil.stampToDate(String.valueOf(roleBean.getUnRelinfoStartTime()),"MM-dd HH:mm")
                            + "至" + DateUtil.stampToDate(String.valueOf(roleBean.getUnRelinfoEndTime()),"MM-dd HH:mm"));
                }else {
                    forbidentSentDynamicStatus.setText("永久禁发动态");
                }
            }
        }else {
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
            forbidentSayLayout.setVisibility(View.GONE);
            forbidentSentDynamicLayout.setVisibility(View.GONE);
            tvCircleManager.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSaveGroupRule(GroupRuleBean data) {}

    @Override
    public void setPresenter(GroupRuleContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {}

    @Override
    public void showLoadFinish() {}

    @Override
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(GroupChatMembersActivity.this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(GroupChatMembersActivity.this);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    //群规
    @Subscribe
    public void onEvent(GroupRuleBean bean) {}

}
