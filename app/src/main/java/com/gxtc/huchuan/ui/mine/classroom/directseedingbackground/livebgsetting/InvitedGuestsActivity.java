package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInviteUrlBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleInviteActivity;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:邀请（管理员，主持人，普通成员）嘉宾
 * Created by ALing on 2017/3/22.
 */

public class InvitedGuestsActivity extends BaseTitleActivity implements View.OnClickListener {
    private static final String TAG = InvitedGuestsActivity.class.getSimpleName();

    @BindView(R.id.btn_send_to_webchat) Button   btnSendToWebchat;
    @BindView(R.id.text1)               TextView text1;
    @BindView(R.id.text2)               TextView text2;
    @BindView(R.id.text3)               TextView text3;
    @BindView(R.id.text4)               TextView text4;
    @BindView(R.id.text5)               TextView text5;

    private UMShareUtils shareUtils;
    private String       shareUrl;
    private String       facePic;
    private String       Type;
    private String       name;
    private String       id;
    private String       freeSign;
    private String       rolerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_guests);

    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);

        Type = getIntent().getStringExtra("Type");
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        facePic = getIntent().getStringExtra("facePic");

        if ("1".equals(Type)) {
            rolerType = CircleShareHandler.SHARE_CLASS_ADMIN + "";
            ConversationListActivity.CAN_SHARE_INVITE = 9;
            getBaseHeadView().showTitle("邀请管理员");
            setAdminInfo();

        } else if ("2".equals(Type)) {
            rolerType = CircleShareHandler.SHARE_TEACHER;
            ConversationListActivity.CAN_SHARE_INVITE = 8;
            getBaseHeadView().showTitle("邀请讲师");
            setSpeakerInfo();

        //免费邀请课堂
        } else if("3".equals(Type)){
            rolerType = CircleShareHandler.SHARE_FREE_CLASS;
            ConversationListActivity.CAN_SHARE_INVITE = 10;
            getBaseHeadView().showTitle("邀请学员");
            setStudentInfo();

        //免费邀请系列课
        }else{
            rolerType = CircleShareHandler.SHARE_FREE_SERIES;
            ConversationListActivity.CAN_SHARE_INVITE = 11;
            getBaseHeadView().showTitle("邀请学员");
            setStudentInfo();
        }

        getInviteUrl();
    }

    private void setAdminInfo() {
        text1.setText("邀请管理员说明:");
        text2.setText("管理员可以协助创建者管理课堂间");
        text3.setText("管理员权限跟创建者一致，但不能添加和删除管理员，不能操作课堂间提现功能");
        text4.setText("课堂创建者可以删除管理员");
        text5.setText("邀请管理员的链接已复制，点击下方按钮发送给好友，好友点击后即可成为管理员");
    }

    private void setSpeakerInfo() {
        text1.setText("邀请讲师说明:");
        text2.setText("讲师可以在当次课堂课程发言");
        text3.setText("创建新的课堂课程后，需要重新邀请讲师");
        text4.setText("课堂创建者可以删除讲师");
        text5.setText("点击下方按钮发送给好友，好友点击后即可成为讲师，每次发送只能邀请1位");
    }

    private void setStudentInfo() {
        text1.setText("邀请学员说明:");
        text2.setText("邀请的学员可以免费报名进入本次课程");
        text3.setText("课程创建者与管理员可以禁言、删除学员");
        text4.setText("");
        text5.setText("邀请学员免费加入课程的链接已复制，点击下方按钮发送给好友，好友点击后即可免费加入课程");
    }

    private void setSeriersInfo() {
        text1.setText("邀请学员说明:");
        text2.setText("邀请的学员可以免费报名进入本次系列课");
        text3.setText("课程创建者与管理员可以禁言、删除学员");
        text4.setText("");
        text5.setText("邀请学员免费加入系列课的链接已复制，点击下方按钮发送给好友，好友点击后即可免费加入系列课");
    }

    @OnClick({R.id.btn_send_to_webchat, R.id.btn_send_to_xinmt})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //发送给微信好友
            case R.id.btn_send_to_webchat:
                if(!TextUtils.isEmpty(shareUrl)){
                    share(SHARE_MEDIA.WEIXIN);
                }
                break;

            case R.id.btn_send_to_xinmt:
                if(!TextUtils.isEmpty(freeSign)){
                    ConversationListActivity.startActivity(InvitedGuestsActivity.this, ConversationActivity.REQUEST_SHARE_INVITE,Constant.SELECT_TYPE_SHARE,ConversationListActivity.CAN_SHARE_INVITE);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConversationActivity.REQUEST_SHARE_INVITE && resultCode == RESULT_OK) {
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            sendRongIm(bean);
        }
    }

    @Override
    protected void onDestroy() {
        RxTaskHelper.getInstance().cancelTask(this);
        super.onDestroy();
    }


    /**
     * 获取免费邀请的的 邀请url 以及 邀请码
     * 1、邀请管理员  2、邀请讲师  3、免费邀请学员进课堂  4、免费邀请学员进系列课
     */
    private void getInviteUrl() {
        getBaseLoadingView().showLoading(true);

        HashMap<String,String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("chatInfoId",id);
        map.put("joinType",Type);

        Subscription sub =
                MineApi.getInstance().getChatInviteUrl(map).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<ChatInviteUrlBean>>(new ApiCallBack<ChatInviteUrlBean>() {

                            @Override
                            public void onSuccess(ChatInviteUrlBean data) {
                                if(getBaseLoadingView() == null) return;

                                getBaseLoadingView().hideLoading();
                                shareUrl = data.getUrl();
                                freeSign = data.getFreeSign();
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if(getBaseLoadingView() == null) return;

                                getBaseLoadingView().hideLoading();
                                LoginErrorCodeUtil.showHaveTokenError(InvitedGuestsActivity.this, errorCode, message);
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private void share(final SHARE_MEDIA shareMedia) {
        String title = "";
        switch (Type){
            case "1":
                title = "管理员";
                break;
            case "2":
                title = "讲师";
                break;
            case "3":
                title = "学员";
                break;
            default:
                title = "嘉宾";
                break;
        }
        new UMShareUtils(InvitedGuestsActivity.this).shareOne(shareMedia,
                UserManager.getInstance().getUser().getHeadPic(), "新媒之家"+title+"邀请链接", "向你发出"+title+"邀请链接",
                shareUrl);
    }


    private void sendRongIm(final EventSelectFriendBean bean) {
        String title = "";

        if ("1".equals(Type)) {
            title = UserManager.getInstance().getUserName() + "邀请你成为" + name + "管理员";

        } else if ("2".equals(Type)) {
            title = UserManager.getInstance().getUserName() + "邀请你成为" + name + "讲师";

        }else if ("3".equals(Type)) {
            title = UserManager.getInstance().getUserName() + "邀请你免费加入" + name;

        }else if ("4".equals(Type)) {
            title = UserManager.getInstance().getUserName() + "邀请你免费加入" + name;
        }

        String TitleId = id + "&" + freeSign;
        ImMessageUtils.shareMessage(bean.targetId, bean.mType, TitleId, title, facePic, rolerType,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(InvitedGuestsActivity.this, "分享成功");
                        if(!TextUtils.isEmpty(bean.liuyan)){
                            RongIMTextUtil.INSTANCE.relayMessage
                                    (bean.liuyan,bean.targetId,bean.mType);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(InvitedGuestsActivity.this, "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }


    /**
     * @param Type 1、邀请管理员  2、邀请讲师  3、免费邀请学员进课堂  4、免费邀请学员进系列课
     */
    public static void startActivity(Context context, String Id, String Type, String name, String facePic) {
        Intent intent = new Intent(context, InvitedGuestsActivity.class);
        intent.putExtra("Type", Type);
        intent.putExtra("name", name);
        intent.putExtra("id", Id);
        intent.putExtra("facePic", facePic);
        context.startActivity(intent);
    }


    public static void startActivity(Context context, String shareUrl) {
        Intent intent = new Intent(context, InvitedGuestsActivity.class);
        intent.putExtra("shareUrl", shareUrl);
        context.startActivity(intent);
    }

}
