package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DiscussAdapter;
import com.gxtc.huchuan.bean.BannedOrBlackUserBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.im.bean.RemoteMessageBean;
import com.gxtc.huchuan.im.provide.RemoveMessage;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/18.
 */

public class PopDiscuss extends BasePopupWindow implements View.OnClickListener, TextWatcher,
        PopManage.PopManageListener, DiscussAdapter.ManageListener {
    private static final String TAG = "PopDiscuss";

    @BindView(R.id.voice)               ImageView                          mVoice;
    @BindView(R.id.img_return_top)      ImageView                          imgTop;
    @BindView(R.id.ll_back)             LinearLayout                       mLlBack;
    @BindView(R.id.ll_top)              LinearLayout                       mLlTop;
    @BindView(R.id.recyclerView)        RecyclerView                       mRecyclerView;
    @BindView(R.id.swipe_topic_comment) SwipeRefreshLayout                 mSwipeTopicComment;
    @BindView(R.id.line)                View                               mLine;
    @BindView(R.id.et_comment)          TextView                           mEtComment;

    private                             DiscussAdapter                     mDiscussAdapter;
    private                             AlertDialog                        mAlertDialog;
    private                             View                               mView;
    private                             CheckBox                           mCheckBox;
    private                             EditText                           mEditText;
    private                             ChatInfosBean                      mBean;
    private                             android.support.v7.app.AlertDialog dialog;
    private                             PopManage                          mBubblePopup;

    private LinearLayoutManager mLayoutManager;

    public PopDiscuss(Activity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
        mSwipeTopicComment.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
    }


    public void setChatinfosBean(ChatInfosBean bean) {
        if(bean == null )   return;
        mBean = bean;
        changeSilentModel("1".equals(mBean.getIsBanned()));
    }

    /**
     * 要在显示之前设置好。
     */
    public void setData(List<Message> messages) {
        if (mDiscussAdapter == null) {
            List<Message> messageList = new ArrayList<>();
            messageList.addAll(messages);
            mDiscussAdapter = new DiscussAdapter(getActivity(), messageList, R.layout.item_topic_comment, mBean);
            mDiscussAdapter.setPopManageListener(this);
            mRecyclerView.setAdapter(mDiscussAdapter);
        }
    }

    public void addDiscussMessage(Message message){
        if(mDiscussAdapter != null){
            List<Message> msgs = new ArrayList<>();
            msgs.add(message);
            mDiscussAdapter.changeData(msgs,0);
            mRecyclerView.notifyChangeData();
        }
    }

    public void removeDiscussMessage(String msgId){
        if(mDiscussAdapter != null){
            for (int i = 0; i < mDiscussAdapter.getList().size(); i++) {
                Message msg = mDiscussAdapter.getList().get(i);
                TextMessage content = (TextMessage) msg.getContent();
                Extra       extra   = new Extra(content.getExtra());
                String      id   = extra.getMsgId();
                if(!TextUtils.isEmpty(id) && id.equals(msgId)){
                    mRecyclerView.removeData(mDiscussAdapter, i);
                    break;
                }
            }
        }
    }

    @Override
    public void initListener() {
        mSwipeTopicComment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PopDiscuss.this.onRefresh();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });

        mRecyclerView.setMyScrollListener(new android.support.v7.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if(mLayoutManager.findFirstVisibleItemPosition() != 0){
                            imgTop.setVisibility(View.VISIBLE);
                        }else{
                            imgTop.setVisibility(View.GONE);
                        }
                        break;
                }
            }

            @Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @OnClick({R.id.ll_back, R.id.ll_top, R.id.et_comment,R.id.img_return_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.ll_top:
            case R.id.tv_cancel:
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                } else {
                    closePop();
                }
                break;

            case R.id.et_comment:
                showAlderDialog();
                break;

            case R.id.tv_finish:
                if (mEditText.getText().length() <= 0) {
                    Toast toast = Toast.makeText(getActivity(), "请输入评论内容", Toast.LENGTH_SHORT);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    //提交
                    submit();
                }
                break;

            case R.id.img_return_top:
                mRecyclerView.smoothScrollToPosition(0);
                imgTop.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void submit() {
        if(TextUtils.isEmpty(mEditText.getText().toString())){
            ToastUtil.showShort(getActivity(),"内容不能为空");
            return;
        }

        EventBusUtil.post(new EventLoadBean(true));
        WindowUtil.closeInputMethod(getActivity());

        TextMessage textMessage       = TextMessage.obtain(mEditText.getText().toString());
        long        currentTimeMillis = System.currentTimeMillis();
        long msgId = currentTimeMillis + (new Random().nextInt(99999 - 10000) + 10000);
        Extra extra = Extra.obtan("2", mCheckBox.isChecked(), mBean.getId() + msgId,"1");
        extra.setSentTime(currentTimeMillis);
        textMessage.setExtra(extra.encode());

        textMessage.setUserInfo(new UserInfo(UserManager.getInstance().getUserCode(),
                UserManager.getInstance().getUserName(),
                Uri.parse(UserManager.getInstance().getHeadPic())));
        Message message = Message.obtain(mBean.getId(), Conversation.ConversationType.CHATROOM, textMessage);
        message.setExtra(extra.encode());
        RongIMClient.getInstance().sendMessage(message, null, null,
                new IRongCallback.ISendMessageCallback() {

                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        mAlertDialog.dismiss();
                        ArrayList<Message> messages = new ArrayList<>();
                        messages.add(message);

                        mRecyclerView.notifyChangeData(messages, 0, mDiscussAdapter);
                        mEditText.setText("");
                        EventBusUtil.post(new EventLoadBean(false));
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        EventBusUtil.post(new EventLoadBean(false));
                        ToastUtil.showShort(getActivity(), "发送消息失败，请稍后重试");
                    }
                });

    }


    /**
     * 加载更多历史信息
     */
    int start = 0;
    private void loadMore() {
        start += 15;
        final Message message = mDiscussAdapter.getList().get(mDiscussAdapter.getList().size() - 1);
        Extra         extra   = new Extra(message.getExtra());
        remoteTopicHistory(start, extra.getMsgId(), "1", new CallBack() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages.size() == 0) mRecyclerView.loadFinish();
                mRecyclerView.changeData(messages, mDiscussAdapter);
            }

            @Override
            public void onError(String message) {
                mRecyclerView.loadFinish();
            }

            @Override
            public void onCancel() {

            }
        });

    }

    /**
     * 刷新有没有最新消息
     */
    private void onRefresh() {
        if (true) {
            String msgid = null;
            if (mDiscussAdapter != null && mDiscussAdapter.getItemCount() > 0) {
                Message message = mDiscussAdapter.getList().get(0);
                TextMessage tMsg = (TextMessage) message.getContent();
                Extra   extra   = new Extra(tMsg.getExtra());
                msgid = extra.getMsgId();
            }

            remoteTopicHistory(0, msgid, "2", new CallBack() {
                @Override
                public void onSuccess(List<Message> messages) {
                    if (mDiscussAdapter == null) {
                        mDiscussAdapter = new DiscussAdapter(getActivity(), messages, R.layout.item_topic_comment);
                        mRecyclerView.setAdapter(mDiscussAdapter);
                    } else {
                        mDiscussAdapter.changeData(messages, 0);
                        mRecyclerView.notifyChangeData();
                    }

                    mSwipeTopicComment.setRefreshing(false);
                }

                @Override
                public void onError(String message) {
                    mSwipeTopicComment.setRefreshing(false);
                }

                @Override
                public void onCancel() {
                    mSwipeTopicComment.setRefreshing(false);
                }
            });
        } else {
            mSwipeTopicComment.setRefreshing(false);
        }
    }

    private boolean islaodTopicHistoring = false;

    /**
     * @param start       开始数量  0
     * @param msgId       消息ID
     * @param msgRankType 上部份 还是下部份
     * @param callBack    回调
     */
    public void remoteTopicHistory(int start, String msgId, String msgRankType, final CallBack callBack) {
        if (islaodTopicHistoring || mBean == null) {
            callBack.onCancel();
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("targetType", Conversation.ConversationType.CHATROOM.getValue() + "");

        map.put("targetId", mBean.getId());
        map.put("msgRankType", msgRankType);
        if (msgId != null) map.put("msgId", msgId);
        map.put("start", String.valueOf(start));
        map.put("roleType", "2");

        LiveApi.getInstance()
               .getMessageRecordList(map)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .map(new Func1<ApiResponseBean<List<RemoteMessageBean>>, List<Message>>() {
                        @Override
                        public List<Message> call(ApiResponseBean<List<RemoteMessageBean>> listApiResponseBean) {
                            return MessageFactory.createNotInsertDao(listApiResponseBean.getResult());
                        }
                    })
               .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {
                        islaodTopicHistoring = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        callBack.onSuccess(messages);
                    }

               });

    }


    private void showAlderDialog() {
        if (mAlertDialog == null) {
            mView = View.inflate(getActivity(), R.layout.dialog_comment, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogStyle2);
            mAlertDialog = builder.create();
            mAlertDialog.setView(mView);
            View cancel = mView.findViewById(R.id.tv_cancel);
            cancel.setOnClickListener(this);
            View finish = mView.findViewById(R.id.tv_finish);
            finish.setOnClickListener(this);
            mEditText = (EditText) mView.findViewById(R.id.et_comment);
            mEditText.addTextChangedListener(this);
            mCheckBox = (CheckBox) mView.findViewById(R.id.iv_question);
        }
        mAlertDialog.show();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onAction(final Message message, @PopManage.Status int status, final int position) {
        switch (status) {
            case PopManage.REMOVE:
                dialog = DialogUtil.showInputDialog(getActivity(), true, "删除该评论?", null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mDiscussAdapter != null && mDiscussAdapter.getItemCount() >= position) {
                                    TextMessage content = (TextMessage) message.getContent();
                                    Extra       extra   = new Extra(content.getExtra());
                                    String      msgId   = extra.getMsgId();
                                    LiveApi.getInstance()
                                           .delMessage(UserManager.getInstance().getToken(), msgId)
                                           .subscribeOn(Schedulers.io())
                                           .observeOn(AndroidSchedulers.mainThread())
                                           .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                                                        @Override
                                                        public void onSuccess(Object data) {
                                                            if(mDiscussAdapter == null) return;
                                                            mDiscussAdapter.getList().remove(message);
                                                            mRecyclerView.notifyChangeData();
                                                            sendRemoveMesssage(message);
                                                            if (mBubblePopup != null) {
                                                                if (mBubblePopup.isShowing()) {
                                                                    mBubblePopup.dismiss();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(String errorCode, String message) {
                                                            if(mDiscussAdapter == null) return;
                                                            Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }));
                                }
                                dialog.dismiss();
                            }
                        });
                break;

            case PopManage.MANAGE:
                dialog = DialogUtil.showInputDialog(getActivity(), true, "确定加入黑名单？?",
                        "加入黑名单后，此用户将不能进入你的直播间，也可在课堂设置页面管理黑名单用户", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextMessage  content = (TextMessage) message.getContent();
                                final String userId  = content.getUserInfo().getUserId();
                                //  type 1拉黑/解除拉黑操作  2禁言/解除禁言操作
                                String chatRoomId = mBean.getId();
                                String chatType = "1";
                                if(mBean.getChatSeries() == null || mBean.getChatSeries().equals("")){
                                    chatRoomId = mBean.getChatSeries();
                                    chatType = "2";
                                }
                                manageUser(chatRoomId, chatType, "1", userId, "0",
                                        new ManageCallBack() {
                                            @Override
                                            public void onSuccess(List<BannedOrBlackUserBean> messages) {
                                                if(mBubblePopup == null) return;
                                                sendBlacklistMessage(userId);
                                                Toast.makeText(getActivity(), "添加黑名单成功", Toast.LENGTH_SHORT).show();
                                                if (mBubblePopup.isShowing()) {
                                                    mBubblePopup.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onError(String message) {
                                                if(mBubblePopup == null) return;
                                                Toast.makeText(getActivity(), "添加黑名单失败", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCancel() {}
                                        });
                                dialog.dismiss();
                            }
                        });
                break;

            case PopManage.SILENT:
                dialog = DialogUtil.showInputDialog(getActivity(), true, "确定禁言?", "禁言后，此用户在此直播间不能发布任何评论。\n也可在直播间设置页面管理禁言用户。",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextMessage  content = (TextMessage) message.getContent();
                                final String userId  = content.getUserInfo().getUserId();
                                String chatRoomId = mBean.getId();
                                String chatType = "1";
                                if(mBean.getChatSeries() == null || mBean.getChatSeries().equals("")){
                                    chatRoomId = mBean.getChatSeries();
                                    chatType = "2";
                                }

                                manageUser(chatRoomId, chatType, "2", userId, "0",
                                        new ManageCallBack() {
                                            @Override
                                            public void onSuccess(List<BannedOrBlackUserBean> messages) {
                                                if (mBubblePopup != null) {
                                                    if (mBubblePopup.isShowing()) {
                                                        mBubblePopup.dismiss();
                                                    }
                                                }
                                                sendMessage("true", userId);
                                                Toast.makeText(getActivity(), "禁言成功", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(String message) {
                                                Toast.makeText(getActivity(), "禁言失败", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCancel() {}

                                        });
                                dialog.dismiss();
                            }
                        });
                break;
        }
    }


    //发送删除消息
    private void sendRemoveMesssage(Message message) {
        UserInfo user = UserManager.getInstance().obtinUserInfo();
        if(user == null)    return;
        TextMessage tvMsg = (TextMessage) message.getContent();
        Extra oldExtra = new Extra(tvMsg.getExtra());
        RemoveMessage removeMessage     = RemoveMessage.obtain();
        long          currentTimeMillis = System.currentTimeMillis();
        final Extra   extra             = Extra.obtan("2", false, oldExtra.getMsgId(),"0");
        extra.setSentTime(currentTimeMillis);
        removeMessage.setUserInfo(user);
        removeMessage.setContent(extra.getMsgId());
        removeMessage.setExtra(extra.encode());
        Message temp = Message.obtain(mBean.getId(), Conversation.ConversationType.CHATROOM, removeMessage);
        message.setExtra(extra.encode());
        RongIM.getInstance().sendMessage(temp, null, null,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) { }

                    @Override
                    public void onSuccess(Message message) {
                        LogUtil.i("onSuccess  :  " + message.toString());
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        LogUtil.i("onError  :  " + errorCode);
                    }
                });

    }


    //禁言消息不要加 isClass标记
    private void sendMessage(String msg, String userCode) {
        ImMessageUtils.silentMessage(msg,userCode, mBean.getId(),new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                LogUtil.i("发送禁言消息成功");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtil.i("发送禁言消息失败");
            }
        });
    }

    //发送拉黑消息
    private void sendBlacklistMessage(String userCode){
        ImMessageUtils.blacklistMessage(userCode, mBean.getId(),new IRongCallback.ISendMessageCallback(){
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                LogUtil.i("拉黑成功");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtil.i("拉黑失败  : " + errorCode);
            }
        });
    }

    @Override
    public void onClick(View v, Message message, int position) {
        mBubblePopup = new PopManage(getActivity());
        mBubblePopup.anchorView(v)
                    .location(10, -5)
                    .gravity(Gravity.TOP)
                    .setData(message)
                    .ChatInfosBean(mBean)
                    .showAnim(new PopEnterAnim().duration(200))
                    .dismissAnim(new PopExitAnim().duration(200))
                    .dimEnabled(true)
                    .bubbleColor(Color.parseColor("#ffffff"))
                    .cornerRadius(4)
                    .setPosition(position)
                    .setPopManageListener(this)
                    .show();
    }


//    private void manageUser(String chatRoomId, String type, String userCode, String operate, final ManageCallBack callBack) {
//        AllApi.getInstance()
//              .saveChatRoomUserMng(UserManager.getInstance().getToken(), chatRoomId, type, userCode, operate)
//              .subscribeOn(Schedulers.io())
//              .observeOn(AndroidSchedulers.mainThread())
//              .subscribe(new ApiObserver<ApiResponseBean<List<BannedOrBlackUserBean>>>(new ApiCallBack() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        callBack.onSuccess(null);
//                    }
//
//                    @Override
//                    public void onError(String errorCode, String message) {
//                        callBack.onError(message);
//                    }
//                }));
//    }
    /**
     *
     *   拉黑/解除拉黑操作  禁言/解除禁言
     *  chatId 课程/系列课  id
     *  chatType 1课程 2系列课
     *  userCode 目标用户新媒号
     *  token 当前操作人token
     *  type 1拉黑/解除拉黑操作  2禁言/解除禁言操作
     *  state 0.解除 1.拉黑/禁言
     */
    private void manageUser(String chatRoomId, String chatType, final String type, String userCode, String operate, final ManageCallBack callBack) {
        HashMap<String,String> map =new HashMap<>();
        map.put("chatId", chatRoomId);
        map.put("chatType", chatType);
        map.put("userCode", userCode);
        map.put("token", UserManager.getInstance().getToken());
        map.put("type",type);
        map.put("state","1");
        LiveApi.getInstance().doJoinMemberBlacklistOrProhibitSpeaking(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new
                        ApiObserver<ApiResponseBean<Object>>
                        (new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                callBack.onSuccess(new ArrayList<BannedOrBlackUserBean>());

                            }

                            @Override
                            public void onError(String errorCode, String message) {
//                                ToastUtil.showShort(context, message);
                                callBack.onError(message);
                            }
                        }));
    }


    private interface ManageCallBack {
        void onSuccess(List<BannedOrBlackUserBean> messages);

        void onError(String message);

        void onCancel();
    }


    public interface CallBack {
        void onSuccess(List<Message> messages);

        void onError(String message);

        void onCancel();
    }

    public void changeSilentModel(boolean isSilent){
        if(isSilent){
            mEtComment.setOnClickListener(null);
            mEtComment.setText("您已被管理员禁言");
        }else{
            mEtComment.setOnClickListener(this);
            mEtComment.setText("来说点什么吧...");
        }
    }
}
