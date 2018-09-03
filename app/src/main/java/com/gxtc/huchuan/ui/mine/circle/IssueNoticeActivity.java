package com.gxtc.huchuan.ui.mine.circle;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.im.postcard.PTMessage;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 发布群公告页面
 */
public class IssueNoticeActivity extends BaseTitleActivity implements View.OnClickListener {

    Subscription sub;
    @BindView(R.id.et_issue_notice_title)
    EditText etTitle;
    @BindView(R.id.et_issue_notice_content)
    EditText etContent;

    private int groudId;
    private String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_notice);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(getString(R.string.title_circle_notice_issue));
        getBaseHeadView().showHeadRightButton("发布", this);
        Intent intent = getIntent();
        groudId = intent.getIntExtra("groud_id", -1);
        targetId = intent.getStringExtra("targetId");
    }

    private void issueNotice() {
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            ToastUtil.showShort(IssueNoticeActivity.this, "公告标题不能为空");
            return;
        }
        if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
            ToastUtil.showShort(IssueNoticeActivity.this, "公告内容不能为空");
            return;
        }
        if (etContent.getText().toString().trim().length() > 1000) {
            ToastUtil.showShort(IssueNoticeActivity.this, "公告内容不能超过1000字");
            return;
        }
        getBaseLoadingView().showLoading();
        sub = CircleApi.getInstance().saveGroupNotice(groudId, UserManager.getInstance().getToken(),
                etTitle.getText().toString(), etContent.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        getBaseLoadingView().hideLoading();
                        ToastUtil.showShort(IssueNoticeActivity.this, "发布成功");
                        if(targetId == null){
                            IssueNoticeActivity.this.finish();
                        }else {
                            //sendRongIm();
                        }

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(IssueNoticeActivity.this, errorCode, message);
                        getBaseLoadingView().hideLoading();
                    }
                }));
    }


    private void sendRongIm() {
        MentionedInfo mentionedInfo = new MentionedInfo(MentionedInfo.MentionedType.ALL, null, null);
        TextMessage textMessage = TextMessage.obtain(etContent.getText().toString()+"@所有人");
        textMessage.setMentionedInfo(mentionedInfo);
        Message myMessage = Message.obtain(targetId, Conversation.ConversationType.GROUP, textMessage);
        RongIM.getInstance().sendMessage(myMessage, "公告", "公告", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                setResult(RESULT_OK);
                IssueNoticeActivity.this.finish();
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(MyApplication.getInstance(), RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                issueNotice();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}
