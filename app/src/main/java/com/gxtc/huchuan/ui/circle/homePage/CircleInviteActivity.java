package com.gxtc.huchuan.ui.circle.homePage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleInviteAdapter;
import com.gxtc.huchuan.adapter.PurchaseRecordAdapter;
import com.gxtc.huchuan.bean.CircleShareInviteBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.homePage.invitation.CircleInvitaFragment;
import com.gxtc.huchuan.ui.circle.homePage.invitation.CircleRankFragment;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 圈子-邀请页面\
 * 苏修伟
 */
public class CircleInviteActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.tabLayout_main) SlidingTabLayout mTabLayoutMain;
    @BindView(R.id.vp_invite)      ViewPager        mVpInvite;

    private List<Fragment> fragments;
    public  String         liuyan;
    private String         id;
    private String         sharImgUrl;
    private String         shareTitle;
    private String         shareUrl;
    private int            memberType;
    private double         money;   //邀请佣金

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_invite);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_circle_invite));
        getBaseHeadView().showBackButton(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        sharImgUrl = intent.getStringExtra("share_img_url");
        shareTitle = intent.getStringExtra("share_title");
        shareUrl = intent.getStringExtra("share_url");
        money = intent.getDoubleExtra("money", 0);
        memberType = intent.getIntExtra("memberType", 0);
        String[] arrTabTitles = getResources().getStringArray(R.array.array_circle_invite);

        fragments = new ArrayList<>();
        CircleInvitaFragment circleInvitaFragment = new CircleInvitaFragment();
        Bundle               bundle               = new Bundle();
        bundle.putString("id", id);
        bundle.putString("share_img_url", sharImgUrl);
        bundle.putString("share_title", shareTitle);
        bundle.putString("share_url", shareUrl);
        bundle.putDouble("money", money);
        bundle.putInt("memberType", memberType);
        circleInvitaFragment.setArguments(bundle);

        CircleRankFragment circleRankFragment = new CircleRankFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("id", id);
        circleRankFragment.setArguments(bundle1);

        fragments.add(circleInvitaFragment);
        fragments.add(circleRankFragment);
        mVpInvite.setAdapter(new PurchaseRecordAdapter(getSupportFragmentManager(), fragments, arrTabTitles));
        mTabLayoutMain.setViewPager(mVpInvite);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTabLayoutMain.getMsgView(2).getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = WindowUtil.dip2px(this, 10);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }


    //分享到圈子
    private void shareToCircle() {
        if (TextUtils.isEmpty(shareUrl)) return;
        String infoType = "";
        //分享普通成员
        if (shareUrl.endsWith("0")) {
            infoType = "4";
            //邀请管理员
        } else {
            infoType = "5";
        }
        String title = shareTitle;
        IssueDynamicActivity.share(CircleInviteActivity.this, id, infoType, title, sharImgUrl);
    }

    private void gotoInvite() {
        //发送邀请普通成员可以分享到群里面
        if (shareUrl.endsWith("0")) {
            ConversationListActivity.startActivity(CircleInviteActivity.this,
                    ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE);

            //发送管理员邀请只能分享给好友
        } else {
            ConversationListActivity.CAN_SHARE_INVITE = 5;
            ConversationListActivity.startActivity(CircleInviteActivity.this,
                    ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE,
                    ConversationListActivity.CAN_SHARE_INVITE);
        }
    }

    private void sendMessage(final String targetId, final Conversation.ConversationType type,
                             String id, String title, String img, String infoType) {
        ImMessageUtils.shareMessage(targetId, type, id, title, img, infoType,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(CircleInviteActivity.this, "分享成功");
                        if (!TextUtils.isEmpty(liuyan)) {
                            RongIMTextUtil.INSTANCE.relayMessage(liuyan, targetId, type);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(CircleInviteActivity.this,
                                "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }

    //获取免费邀请码
    private void getFreeSign(String targetId, Conversation.ConversationType type, String title,
                             String img, String infoType) {
        getBaseLoadingView().showLoading(true);

        final String                        mId       = targetId;
        final Conversation.ConversationType mType     = type;
        final String                        mTitle    = title;
        final String                        mImg      = img;
        final String                        mInfoType = infoType;
        String                              token     = UserManager.getInstance().getToken();

        Subscription sub = CircleApi.getInstance().getGroupFreeInviteUrl(token,
                Integer.valueOf(id)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(
                Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<CircleShareInviteBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (getBaseLoadingView() == null) return;
                        getBaseLoadingView().hideLoading();
                        CircleShareInviteBean bean = (CircleShareInviteBean) data;
                        if (bean != null) {
                            String url = bean.getFreeUrl();
                            if (!TextUtils.isEmpty(url)) {
                                String freeSign = url.substring(url.lastIndexOf("=") + 1,
                                        url.length());
                                String tempId   = id + "&" + freeSign;
                                sendMessage(mId, mType, tempId, mTitle, mImg, mInfoType);
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (getBaseLoadingView() == null) return;
                        getBaseLoadingView().hideLoading();
                        ToastUtil.showShort(CircleInviteActivity.this, message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    /**
     * 邀请好友
     * 如果邀请的是普通成员走一般的分享流程就好
     * 如果邀请的是管理员，需要一个免费邀请码  这里需要调用接口 先请求回来 然后把邀请码拼在id后面  用&符号拼接
     */
    private void shareMessage(String targetId, Conversation.ConversationType type) {
        String userName = UserManager.getInstance().getUserName();
        String img      = sharImgUrl;
        String title;
        String infoType;        //5是邀请普通成员   6是邀请管理员
        //邀请普通成员
        if (shareUrl.endsWith("0")) {
            infoType = "4";
            sendMessage(targetId, type, id, shareTitle, img, infoType);

            //邀请成为管理员
        } else {
            infoType = "5";
            title = userName + "邀请你成为" + shareTitle + "圈子管理员";
            getFreeSign(targetId, type, title, img, infoType);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            liuyan = bean.liuyan;
            shareMessage(targetId, type);
        } else {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }
    }

    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
//        mAlertDialog = null;
    }

    public static void startActivity(Context context, String id, String shareUrl, String title,
                                     String cover, double money, int memberType) {
        Intent intent = new Intent(context, CircleInviteActivity.class);
        intent.putExtra("share_img_url", cover);
        intent.putExtra("share_title", title);
        intent.putExtra("share_url", shareUrl);
        intent.putExtra("money", money);
        intent.putExtra("memberType", memberType);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

}
