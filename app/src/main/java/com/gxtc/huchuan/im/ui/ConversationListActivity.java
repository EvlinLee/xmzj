package com.gxtc.huchuan.im.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ConfirmRelayDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.MyConversationListFragment;
import com.gxtc.huchuan.im.listener.MyConversationListListener;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;
import com.gxtc.huchuan.utils.ClickUtil;

import java.lang.ref.WeakReference;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConversationListActivity extends BaseTitleActivity {

    private int shareFlag = 0;     // 分享类型
    private MyConversationListFragment mListFragment;
    private EventShareMessage currShareMessage;
    private ConfirmRelayDialog mRelayDialog;
    public static   int CAN_SHARE_INVITE = -1;
    public int isInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist);
        shareFlag = getIntent().getIntExtra(Constant.INTENT_DATA,-1);
        isInvite = getIntent().getIntExtra("isInvite",-1);//用于区分是否是邀请操作
        new MyConversationListListenerImp(this);//设置会话列表界面操作的监听器。
        initFragment();
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("我的消息");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightImageButton(R.drawable.person_icon_group, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getInstance(), FocusActivity.class);
                intent.putExtra("focus_flag", "2");
                intent.putExtra("select_type_card", shareFlag);
                startActivityForResult(intent,ConversationActivity.REQUEST_SHARE_CONTENT);
            }
        });
    }


    private void initFragment() {
        mListFragment = new MyConversationListFragment();
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                     .appendPath("conversationlist")
                     .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")    //设置私聊会话非聚合显示
                     .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")      //设置群组会话聚合显示
                     .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false") //设置讨论组会话非聚合显示
                     .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")     //设置系统会话非聚合显示
                     .build();
        mListFragment.setUri(uri);

        Bundle bundle = new Bundle();
        bundle.putInt("select",shareFlag);
        mListFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //rong_content 为你要加载的 id
        transaction.add(R.id.rong_content, mListFragment);
        transaction.commit();
    }

    private static class MyConversationListListenerImp extends MyConversationListListener{
        WeakReference<ConversationListActivity> mWeakReference;
        ConversationListActivity mActivity;

        public MyConversationListListenerImp(ConversationListActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            RongIM.setConversationListBehaviorListener(this);//设置会话列表界面操作的监听器。
        }


        /**
         * 如果要加多选择种类还要修改好几个类的地方
         * #{@link com.gxtc.huchuan.ui.mine.focus.FocusFragment#mAdapter 中的item点击事件 }
         * #{@link com.gxtc.huchuan.ui.circle.SearchConversationActivity 中的列表item点击事件}
         */
        @Override
        public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
            if(ClickUtil.isFastClick()){
                return true;
            }
            if(mWeakReference.get() == null) return true;
            mActivity = mWeakReference.get();
            switch (mActivity.shareFlag){
                case Constant.SELECT_TYPE_SHARE:
                    String targetId = uiConversation.getConversationTargetId();
                    mActivity.setChooseType(targetId,uiConversation,Constant.SELECT_TYPE_SHARE);
                    return true;

                case Constant.SELECT_TYPE_CARD:
                    mActivity.setChooseType(uiConversation.getConversationTargetId(),uiConversation,Constant.SELECT_TYPE_CARD);
                    return true;

                case Constant.SELECT_TYPE_RELAY:
                    mActivity.setChooseType(uiConversation.getConversationTargetId(),uiConversation,Constant.SELECT_TYPE_RELAY);
                    return true;

                case Constant.SELECT_TYPE_GUARAN_DEAL:
                    mActivity.setChooseType(uiConversation.getConversationTargetId(),uiConversation,Constant.SELECT_TYPE_GUARAN_DEAL);
                    return true;

                default:
                    return super.onConversationClick(context,view,uiConversation);
            }
        }

        @Override
        public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
            if(!mActivity.mListFragment.getGatherState(uiConversation.getConversationType())){
                mActivity.mListFragment.buildMultiDialog(uiConversation);
            }else{
                mActivity.mListFragment.buildSingleDialog(uiConversation);
            }
            return true;
        }
    }

    private  void setChooseType(final String targetId, final UIConversation uiConversation,int shareFlag) {
        //-1表示不是邀请操作
        if(isInvite == -1){
            if (uiConversation.getConversationType() == Conversation.ConversationType.SYSTEM){
                ToastUtil.showShort(MyApplication.getInstance(),"非法操作,请重新选择");
            }else if(uiConversation.getConversationType() == Conversation.ConversationType.PRIVATE ){
                UserInfo userInfo = new UserInfo(uiConversation.getConversationTargetId(), uiConversation.getUIConversationTitle(), uiConversation.getIconUrl());
                showConfirmDialog(userInfo, targetId, uiConversation, shareFlag);
            }else {
                getGroupUnfo(targetId,uiConversation,shareFlag);
            }
        }else {
            if(uiConversation.getConversationType() == Conversation.ConversationType.PRIVATE ){
                    UserInfo userInfo = new UserInfo(uiConversation.getConversationTargetId(), uiConversation.getUIConversationTitle(), uiConversation.getIconUrl());
                    showConfirmDialog(userInfo, targetId, uiConversation, shareFlag);
            }else {
                String userCode = UserManager.getInstance().getUserCode();
                if(uiConversation.getConversationTargetId().equals(userCode)){
                    ToastUtil.showShort(MyApplication.getInstance(),"不能选择自己, 请重新选择");
                    return;
                }
                switch (isInvite){
                    case 5:
                        ToastUtil.showShort(MyApplication.getInstance(),"邀请圈子管理员不能分享到群聊");
                        break;
                    case 6:
                        ToastUtil.showShort(MyApplication.getInstance(),"免费邀请圈子成员不能分享到群聊");
                        break;
                    case 8:
                        ToastUtil.showShort(MyApplication.getInstance(),"邀请课程讲师不能分享到群聊");
                        break;
                    case 9:
                        ToastUtil.showShort(MyApplication.getInstance(),"邀请课程管理员不能分享到群聊");
                        break;
                    case 10:
                        ToastUtil.showShort(MyApplication.getInstance(),"免费邀请课程成员不能分享到群聊");
                        break;
                    case 11:
                        ToastUtil.showShort(MyApplication.getInstance(),"免费邀请系列课成员不能分享到群聊");
                        break;

                    case 12:
                        ToastUtil.showShort(MyApplication.getInstance(),"不能选择群聊这项, 请选择好友");
                        break;
                }
            }
        }
    }

    private  void getGroupUnfo(final String targetId, final UIConversation uiConversation,final int shareFlag){
        Subscription sub = CircleApi.getInstance()
                 .getGroupInfo(UserManager.getInstance().getToken(),targetId)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
                 .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                     @Override
                     public void onSuccess(Object data) {
                         CircleBean bean = (CircleBean) data;
                         if(bean != null){
                             UserInfo userInfo = new UserInfo(targetId,bean.getGroupName(),Uri.parse(bean.getCover()));
                             showConfirmDialog(userInfo,targetId,uiConversation,shareFlag);
                         }
                     }

                     @Override
                     public void onError(String errorCode, String message) {
                         ToastUtil.showShort(ConversationListActivity.this,message);
                     }
                 }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void showConfirmDialog(UserInfo userInfo,final String id, final UIConversation uiConversation,final int shareFlag){
        mRelayDialog = new ConfirmRelayDialog();
        mRelayDialog.setUserInfo(userInfo);
        mRelayDialog.setWidth((int) (WindowUtil.getScreenW(this) * 0.8));
        if(Constant.SELECT_TYPE_GUARAN_DEAL == shareFlag ){
            mRelayDialog.setSelectFriends(Constant.SELECT_TYPE_GUARAN_DEAL);//担保交易的把留言输入框隐藏
        }
        mRelayDialog.show(getSupportFragmentManager(),"");
        mRelayDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liuyan = mRelayDialog.getEditContent().getText().toString();
                switch (shareFlag){
                    case Constant.SELECT_TYPE_SHARE:
                        mRelayDialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra(Constant.INTENT_DATA,new EventSelectFriendBean(id, uiConversation.getConversationType(),liuyan));
                        setResult(RESULT_OK,intent);
                        finish();
                        break;

                    case Constant.SELECT_TYPE_CARD:
                        mRelayDialog.dismiss();
                        Intent intentCard = new Intent();
                        intentCard.putExtra(Constant.INTENT_DATA,new EventSelectFriendForPostCardBean(uiConversation.getConversationTargetId(),"",uiConversation.getUIConversationTitle(),"", uiConversation.getConversationType(),liuyan));
                        setResult(RESULT_OK,intentCard);
                        finish();
                        break;

                    case Constant.SELECT_TYPE_RELAY:
                        mRelayDialog.dismiss();
                        EventShareMessage message = new EventShareMessage(Constant.SELECT_TYPE_RELAY,uiConversation.getConversationTargetId(),uiConversation.getConversationType(),liuyan);
                        currShareMessage = message;
                        Intent intentRelay = new Intent();
                        intentRelay.putExtra(Constant.INTENT_DATA,currShareMessage);
                        setResult(RESULT_OK,intentRelay);
                        finish();
                        break;

                    case Constant.SELECT_TYPE_GUARAN_DEAL:
                        mRelayDialog.dismiss();
                        Intent intentDeal = new Intent();
                        intentDeal.putExtra(Constant.INTENT_DATA,new EventSelectFriendForPostCardBean(uiConversation.getConversationTargetId(),"",uiConversation.getUIConversationTitle(),"", uiConversation.getConversationType(),liuyan));
                        setResult(RESULT_OK,intentDeal);
                        finish();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择好友发送自定义的消息
        if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && data != null){
            setResult(RESULT_OK,data);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRelayDialog = null;
        RongIM.setConversationListBehaviorListener(null);
        RxTaskHelper.getInstance().cancelTask(this);
    }

    /**
     * @param shareFlag 如果是 {@link Constant#SELECT_TYPE_SHARE}
     *                  那么在onActivityResult()中   data 对象应该是 {@link EventSelectFriendBean}
     *                  {@link Constant#SELECT_TYPE_CARD} => {@link EventSelectFriendForPostCardBean}
     *                  {@link Constant#SELECT_TYPE_RELAY} => {@link EventShareMessage}
     *
     * @param requestCode {@link ConversationActivity#REQUEST_SHARE_CONTENT ...}
     */
    public static void startActivity(Activity activity, int requestCode ,int shareFlag) {
        Intent intent = new Intent(activity,ConversationListActivity.class);
        intent.putExtra(Constant.INTENT_DATA,shareFlag);
        activity.startActivityForResult(intent,requestCode);
    }

    // 用于一次性邀请的（只能分享给好友）
    public static void startActivity(Activity activity, int requestCode ,int shareFlag,int isInvite) {
        Intent intent = new Intent(activity,ConversationListActivity.class);
        intent.putExtra(Constant.INTENT_DATA,shareFlag);
        intent.putExtra("isInvite",isInvite);
        activity.startActivityForResult(intent,requestCode);
    }
}
