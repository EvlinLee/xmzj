package com.gxtc.huchuan.im.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.MergeMessageBean;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventGroupBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.event.EventSendRPBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.bean.event.EventUnReadBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CustomDayDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.dialog.RedPacketOpenDialog;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.handler.QrcodeHandler;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.im.utilities.OptionsPopupDialog;
import com.gxtc.huchuan.ui.circle.groupmember.AllGroupMemberActivity;
import com.gxtc.huchuan.ui.circle.groupmember.GroupChatMembersActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.deal.deal.fastDeal.FastDealActivity;
import com.gxtc.huchuan.ui.im.merge.MergeHistoryMessage;
import com.gxtc.huchuan.ui.im.postcard.PTMessage;
import com.gxtc.huchuan.ui.im.redPacket.RPMessage;
import com.gxtc.huchuan.ui.im.redPacket.RedPacketDetailedActivity;
import com.gxtc.huchuan.ui.im.share.ShareMessage;
import com.gxtc.huchuan.ui.im.system.ArticleMessage;
import com.gxtc.huchuan.ui.im.system.CircleMessage;
import com.gxtc.huchuan.ui.im.system.ClassMessage;
import com.gxtc.huchuan.ui.im.system.DealMessage;
import com.gxtc.huchuan.ui.im.system.MallMessage;
import com.gxtc.huchuan.ui.im.system.MessagePresenter;
import com.gxtc.huchuan.ui.im.system.SystemMessage;
import com.gxtc.huchuan.ui.im.system.TradeInfoMessage;
import com.gxtc.huchuan.ui.im.video.VideoMessage;
import com.gxtc.huchuan.ui.message.AllGroupInfoMemberActivity;
import com.gxtc.huchuan.ui.message.GroupInfoActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.ui.PictureEditActivity;
import com.yalantis.ucrop.util.FileUtils;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayer;
import im.collect.CollectMessage;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.mention.IMentionedInputListener;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.MessageItemLongClickAction;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**  http://blog.csdn.net/qq_19986309/article/details/50402476
 * 融云会话界面
 */
public class ConversationActivity extends BaseTitleActivity implements ConversationContract.View, View.OnClickListener{

    public final static int REQUEST_SHARE_CONTENT = 0;      //分享内容给好友
    public final static int REQUEST_SHARE_RELAY = 1;        //转发内容给好友
    public final static int REQUEST_SHARE_CARD = 2;         //分享个人名片
    public final static int REQUEST_SHARE_COLLECT = 3;      //分享收藏
    public final static int REQUEST_SHARE_INVITE = 4;       //分享课堂邀请
    public final static int REQUEST_SHARE_IMAGE = 6;        //分享编辑的图片
    public final static int REQUEST_SHARE_SRC_IMAGE = 7;    //分享图片
    public final static int REQUEST_SHARE_VIDEO = 8;        //分享视频
    public final static int REQUEST_SHARE_MORE_RELAY = 9;   //多聊天消息逐条转发
    public final static int REQUEST_SHARE_MORE_MERGE_RELAY = 10;   //多聊天消息合并转发

    public static final int STATUE_INVATE_CARD = 5  ;  //邀请卡
    public static final int REQUEST_VIDEO = 101  ;     //邀请卡
    private final static int GROUP_TYPE_CIRCLE = 1;        //圈子群聊
    private final static int GROUP_TYPE_CUSTOM = 2;        //自建群聊
    private final static String CONVERSATIONACTIVITY = "ConversationActivity";        //首次进去群聊key

    private LinearLayout mUnReadLayout;
    private TextView     mTvUnreadCircle;

    private int groupType = 0;
    private int groupId = 0;
    private int groupCount = 0;
    private int unreadMsgCount = 0;     //未读圈子动态数
    private String tradeType = "0";
    private String mTargetId;
    private String mTargetIds;
    private String title;
    private String goodsId;
    private String roleType;             //角色。0:普通用户，1：管理员，2：圈主
    private String clickUserCode;
    private String editPicturePath;      //编辑图片后的地址
    private MyConversationFragment fragment;
    private AlertDialog exitDialog;

    private List<Message> selectMessage;        //选中的消息

    private Conversation.ConversationType mConversationType;

    private Message currMessage;

    private CircleBean bean;

    private CircleShareHandler mShareHandler;
    private ConversationContract.Presenter mPresenter;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        AndroidBug5497Workaround.assistActivity(this);
        EventBusUtil.register(this);
    }

    /**   http://blog.csdn.net/acerhphp/article/details/62889468
     *   JZVideoPlayer 内部的AudioManager会对Activity持有一个强引用，而AudioManager的生命周期比较长，导致这个Activity始终无法被回收
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name)){
                    return getApplicationContext().getSystemService(name);
                }
                return super.getSystemService(name);
            }
        });
    }


    @Override
    public void initView() {
        /*从 intent 携带的数据里获取 targetId 和会话类型*/
        Intent intent = getIntent();
        mTargetId = intent.getData().getQueryParameter("targetId");
        tradeType = intent.getData().getQueryParameter("tradeType");
        if(mTargetId == null) return;
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        title = intent.getData().getQueryParameter("title");

        getBaseHeadView().showTitle(title);

        goodsId = getIntent().getData().getQueryParameter("goodsId");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        initFragment();
        initHeadTitle();
        initRongIm();
    }


    @Override
    public void initData() {
        goodsId = getIntent().getData().getQueryParameter("goodsId");
        String userCode = UserManager.getInstance().getUserCode();
        if (!TextUtils.isEmpty(goodsId) && !TextUtils.isEmpty(userCode) && !userCode.equals(mTargetId) && !"1".equals(tradeType.trim())) {   //0：出售，1：求购(必选)
            getBaseHeadView().showHeadRightButton("发起交易",this);
        }
        mShareHandler = new CircleShareHandler(this);
        new ConversationPresenter(this, mTargetId, mConversationType);

        //获取群的信息
        if(mConversationType == Conversation.ConversationType.GROUP) {

            boolean isFirstComeIntoGrounp = SpUtil.getBoolean(this,CONVERSATIONACTIVITY);
            //首次进入群聊（包括圈子）
            if(isFirstComeIntoGrounp){
                mAlertDialog =  DialogUtil.showNoticeDialog(this, true, "温馨提示",
                        "为了保证用户体验，不能在群聊内发布带有第三方账号和二维码以及收徒招代理等广告，违反规则直接踢出群聊和撤回广告并封号处理!",
                        new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
            }
            //自建的群聊
            if (mTargetId.contains("user")) {
                groupType = GROUP_TYPE_CUSTOM;
            //圈子的群聊
            } else {
                groupType = GROUP_TYPE_CIRCLE;
                initUnReadMsg();
            }
            SpUtil.putBoolean(this,CONVERSATIONACTIVITY,false);
            mPresenter.getCircleInfo(mTargetId);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                WindowUtil.closeInputMethod(ConversationActivity.this);
                if(exitCheckVideoMessage()){
                    showExitDialog();
                    return;
                }
                if(fragment != null && fragment.onBackPressed()){
                    return;
                }
                finish();
                break;

            case R.id.headRightButton:
                gotoFastDeal(goodsId);
                break;

            case R.id.HeadRightImageButton:
                if(mConversationType == Conversation.ConversationType.PRIVATE){
                    PersonalInfoActivity.startActivity(this,mTargetId);

                } else if(mConversationType != Conversation.ConversationType.CUSTOMER_SERVICE) {
                    if(bean != null && groupType == GROUP_TYPE_CIRCLE){
                        //群成员列表
                        Intent intent = new Intent(ConversationActivity.this, GroupChatMembersActivity.class);
                        intent.putExtra("groupId",groupId);
                        intent.putExtra("isMy",String.valueOf(bean.getIsMy()));
                        intent.putExtra("count",groupCount);
                        intent.putExtra(Constant.INTENT_DATA,mTargetId);
                        startActivityForResult(intent,555);
                    }

                    if(groupType == GROUP_TYPE_CUSTOM){
                        GroupInfoActivity.startActivity(this,mTargetId,groupCount);
                    }
                }
                break;
        }
    }

    @Override
    public void showCircleInfo(CircleBean bean) {
        groupCount = bean.getCount();
        getBaseHeadView().showTitle(title + "(" + groupCount + ")");

        //获取在群里的身份 是否有权执行禁言删除操作
        mPresenter.getMemberTypeByChat(mTargetId);
    }

    //点击红包
    @Override
    public void showRedPackInfo(RedPacketBean data) {
        if(data != null){

            //如果是私聊 并且是自己点击红包
            String userCoder = UserManager.getInstance().getUserCode();
            if(data.getConversationType() == Conversation.ConversationType.PRIVATE && data.getUserCode().equals(userCoder)){
                Intent intent = new Intent(this, RedPacketDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA,data);
                startActivity(intent);
                return;
            }

            //没领取这个红包，打开弹窗
            if(data.getIsSnatch() == 0){
                showOpenDialog(this,data);

            //领取了红包，打开红包详情页面
            }else{
                Intent intent = new Intent(this, RedPacketDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA,data);
                startActivity(intent);
            }
        }
    }

    private void showOpenDialog(Context context, RedPacketBean data){
        RedPacketOpenDialog mOpenDialog = new RedPacketOpenDialog(context,data);
        mOpenDialog.show();
    }

    @Override
    public void showMemberType(CircleBean bean) {
        if(bean.getRoleType() == null)  return;
        bean.setIsMy(Integer.valueOf(bean.getRoleType()));
        groupId = bean.getGroupId();
        this.bean = bean;
        roleType = bean.getRoleType();
        unreadMsgCount = bean.getUnReadInfo();
        showUnReadMsg();
    }

    @Override
    public void showShutupResult(boolean isSuccessed,String msg , String errorCode, String error) {
        if(isSuccessed){
           ToastUtil.showShort(this,msg);
        }else{
            ToastUtil.showShort(this,error);
        }
    }

    @Override
    public void showRemoveResult(boolean isSuccessed, String errorCode, String error) {
        if(isSuccessed){
            ToastUtil.showShort(this, "移除成员成功");
        }else{
            ToastUtil.showShort(this,error);
        }
    }

    //显示合并消息结果
    @Override
    public void showMergeResult(boolean isSuccessed, String msg) {
        if(isSuccessed){
            ToastUtil.showShort(this, getString(R.string.message_relay_isuccess));
        }else{
            ToastUtil.showShort(this, msg);
        }
    }

    //code 10261 不在该群    10278群解散或圈子解散
    @Override
    public void showOutClear(String code, String msg) {
        String content = "";
        if(Constant.GROUPCODE_OUT.equals(code)){
            content = "你已不在此群聊";
        }

        if(Constant.GROUPCODE_CLEAR.equals(code)){
            if(groupType == GROUP_TYPE_CIRCLE){
                content = "此圈子已解散";
            }else{
                content = "此群聊已解散";
            }
        }

        mAlertDialog =  DialogUtil.showDeportDialog(this, false, null, content, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    removeMessage();
                }
                mAlertDialog.dismiss();
    }

    private void removeMessage() {
        RongIM.getInstance().removeConversation(mConversationType, mTargetId, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(aBoolean){
                    mAlertDialog.dismiss();
                    finish();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                mAlertDialog.dismiss();
                ToastUtil.showShort(MyApplication.getInstance(), RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }
    });
    }


    //发送小视频
    @Subscribe
    public void onEvent(EventClickBean bean){
        mPresenter.sendVideoMessage(this, (LocalMedia) bean.bean);
    }


    @Subscribe
    public void onEvent(EventLoadBean bean){
        getBaseLoadingView().show(bean.isLoading);
    }


    @Subscribe
    public void onEvent(EventSendRPBean bean){
        if(fragment.getRongExtension() != null)
            fragment.getRongExtension().collapseExtension();
    }


    //退出群聊结束会话
    @Subscribe(priority = 1)
    public void onEvent(EventGroupBean bean){
        if(bean.isDelete) {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        if(fragment != null && fragment.onBackPressed()){
            return;
        }
        if(exitCheckVideoMessage()){
            showExitDialog();
            return;
        }
        super.onBackPressed();
    }


    private boolean exitCheckVideoMessage() {
        return mPresenter.getUploadMessagQueen() != null && mPresenter.getUploadMessagQueen().size() > 0;
    }


    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();

        //onDestroy方法在软键盘弹起后 直接调用finish()方法 会出现延迟好几秒调用的情况，所以释放资源放到这里来
        if(isFinishing()){
            //这里只对软键盘做处理，释放资源还是放到onDestroy方法里
            RongIM.setConversationBehaviorListener(null);
            RongMentionManager.getInstance().setMentionedInputListener(null);
            LogUtil.i("onPause ");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
        mPresenter.destroy();
        mShareHandler.destroy();
        MessagePresenter.INSTANCE.destroy();
        EventBusUtil.post(new EventUnReadBean());           //更新未读消息数
        JZMediaManager.instance().releaseMediaPlayer();     //释放视频资源
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            initRongIm();
        }

        if (requestCode == 1001 && resultCode == RESULT_OK){
            groupCount = data.getIntExtra("count",0);//当群聊的人数改变，在这里会重新设置
            getBaseHeadView().showTitle(title + "(" + groupCount + ")");
        }

        //退出圈子结束会话界面
        if (requestCode == 555 && resultCode == RESULT_OK){
            finish();
        }

        if(requestCode == REQUEST_SHARE_CONTENT && resultCode == RESULT_OK){
            EventSelectFriendBean  bean = data.getParcelableExtra(Constant.INTENT_DATA);
            String targetId = bean.targetId;
            Conversation.ConversationType type = bean.mType;
            mPresenter.shareMessage(currMessage, targetId, type);
        }

        //发送收藏
        if(requestCode == REQUEST_SHARE_COLLECT && resultCode == RESULT_OK){
            EventShareMessage bean = (EventShareMessage) data.getSerializableExtra(Constant.INTENT_DATA);
            mPresenter.sendCollectionMessage(bean,mTargetId,mConversationType);
        }

        //转发消息
        if(requestCode == REQUEST_SHARE_RELAY && resultCode == RESULT_OK){
            EventShareMessage bean = (EventShareMessage) data.getSerializableExtra(Constant.INTENT_DATA);
            mPresenter.relayMessage(currMessage.getContent(), (String) bean.mObject,bean.mType,bean.Liuyan);
        }

        //发送名片
        if(requestCode == REQUEST_SHARE_CARD && resultCode == RESULT_OK){
            EventSelectFriendForPostCardBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            mPresenter.sendRongIm(bean);
        }

        //发送文件
        if(requestCode == Constant.requestCode.UPLOAD_FILE && resultCode == RESULT_OK){
            Uri    uri  = data.getData();
            sentFile(uri);
        }

        //编辑图片
        if(requestCode == PictureEditActivity.REQUEST_CODE && resultCode == RESULT_OK){
            editPicturePath = data.getStringExtra("data");
            showEditPictureDialog();
        }

        //分享编辑的图片
        if(requestCode == REQUEST_SHARE_IMAGE && resultCode == RESULT_OK){
            EventSelectFriendBean  bean = data.getParcelableExtra(Constant.INTENT_DATA);
            String targetId = bean.targetId;
            Conversation.ConversationType type = bean.mType;
            mPresenter.sendImageMessage(this, editPicturePath, targetId, type);
        }

        //分享视频
        if(requestCode == REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }

        //多消息逐条转发
        if(requestCode == REQUEST_SHARE_MORE_RELAY && resultCode == RESULT_OK){
            fragment.resetMoreActionState();
            EventShareMessage bean = (EventShareMessage) data.getSerializableExtra(Constant.INTENT_DATA);
            mPresenter.relayMessages(selectMessage, (String) bean.mObject, bean.mType, bean.Liuyan);
        }

        //多消息合并转发
        if(requestCode == REQUEST_SHARE_MORE_MERGE_RELAY && resultCode == RESULT_OK){
            fragment.resetMoreActionState();
            EventShareMessage bean = (EventShareMessage) data.getSerializableExtra(Constant.INTENT_DATA);
            mPresenter.mergeRelayMessages(selectMessage, (String) bean.mObject, bean.mType, bean.Liuyan);
        }
    }


    private void showEditPictureDialog() {
        String [] items = {"发送好友", "保存图片"};
        OptionsPopupDialog dialog = OptionsPopupDialog
                .newInstance(this, items)
                .setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                    @Override
                    public void onOptionsItemClicked(int which) {
                        if(which == 0){
                            ConversationListActivity.startActivity(ConversationActivity.this, ConversationActivity.REQUEST_SHARE_IMAGE, Constant.SELECT_TYPE_SHARE);
                        }

                        if(which == 1){
                            ToastUtil.showLong(ConversationActivity.this, "图片已保存至:  " + editPicturePath);
                        }
                    }
                });
        dialog.show();
    }


    private void initUnReadMsg() {
        Subscription sub = Observable.timer(200, TimeUnit.MILLISECONDS)
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new Action1<Long>() {
                         @Override
                         public void call(Long aLong) {
                             mUnReadLayout = (LinearLayout) View.inflate(ConversationActivity.this, R.layout.layout_circle_unread_msg, null);
                             mTvUnreadCircle = mUnReadLayout.findViewById(R.id.tv_unread);
                             if(fragment != null && fragment.getView() != null){
                                 LinearLayout floatLayout = fragment.getView().findViewById(R.id.float_layout);
                                 floatLayout.addView(mUnReadLayout,0);
                                 LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mUnReadLayout.getLayoutParams();
                                 params.gravity = Gravity.END;
                                 params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                 params.height = WindowUtil.dip2px(ConversationActivity.this, 40);
                                 params.bottomMargin = (int) getResources().getDimension(R.dimen.margin_middle);
                                 showUnReadMsg();

                                 mUnReadLayout.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         hideUnReadMsg();
                                         CircleMainActivity.startActivity(ConversationActivity.this, groupId);
                                     }
                                 });
                             }
                         }
                     });

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private void showUnReadMsg(){
        if(mTvUnreadCircle.isSelected()) return;
        if(mUnReadLayout != null && mUnReadLayout.getAnimation() == null){
            TranslateAnimation translateAnimation = new TranslateAnimation(300.0F, 0.0F, 0.0F, 0.0F);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
            translateAnimation.setDuration(1000L);
            alphaAnimation.setDuration(2000L);
            AnimationSet set = new AnimationSet(true);
            set.addAnimation(translateAnimation);
            set.addAnimation(alphaAnimation);
            mUnReadLayout.setVisibility(View.VISIBLE);
            mUnReadLayout.startAnimation(set);
            if(unreadMsgCount > 0){
                String s = String.format(Locale.CHINA, "%s条新动态", unreadMsgCount > 99 ? "99+" : unreadMsgCount);
                mTvUnreadCircle.setText(s);
            }else{
                String s = "进入圈子";
                mTvUnreadCircle.setText(s);
            }
            mTvUnreadCircle.setSelected(true);


            LinearLayout floatLayout = fragment.getView().findViewById(R.id.float_layout);
            if(floatLayout != null ){
                LinearLayout messageLayout = floatLayout.findViewById(R.id.rc_unread_message_layout);
                if(messageLayout != null){
                    int messageWidth = messageLayout.getWidth();
                    int padding = (int) (getResources().getDimension(R.dimen.margin_tiny) * 2 + WindowUtil.dip2px(this, 7));
                    int dynamicWidth = 48 + padding + StringUtil.getTextViewLength(mTvUnreadCircle, mTvUnreadCircle.getText().toString());

                    LinearLayout.LayoutParams params = null;
                    if(messageWidth > dynamicWidth){
                        params = (LinearLayout.LayoutParams) mUnReadLayout.getLayoutParams();
                        params.width = messageWidth;

                    }else if(dynamicWidth > messageWidth){
                        params = (LinearLayout.LayoutParams) messageLayout.getLayoutParams();
                        params.width = dynamicWidth;
                    }
                }
            }
        }
    }


    private void hideUnReadMsg(){
        if(mUnReadLayout != null){
            mTvUnreadCircle.setText("进入圈子");
        }
    }


    private void sentFile(Uri uri) {
        if(uri == null)  return;
        String path = FileUtils.getPath(MyApplication.getInstance().getApplicationContext(), uri);
        if(TextUtils.isEmpty(path)){
            ToastUtil.showShort(this, "文件地址无效，请重新选择");
            return;
        }

        FileMessage fileMessage = FileMessage.obtain(Uri.fromFile(FileUtil.createFile(path)));
        if(fileMessage == null){
            ToastUtil.showShort(this, "文件地址无效，请重新选择");
            return;
        }
        Message message = ImMessageUtils.obtain(mTargetId,mConversationType,fileMessage);
        RongIM.getInstance().sendMediaMessage(message, "文件", "文件", new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int i) {}

            @Override
            public void onCanceled(Message message) {}

            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(ConversationActivity.this, "发送文件成功");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(ConversationActivity.this, "发送文件失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }

    private void initHeadTitle(){
        getBaseHeadView().showBackButton(this);

        String userCode = UserManager.getInstance().getUserCode();
        if (!TextUtils.isEmpty(goodsId) && !TextUtils.isEmpty(userCode) && !userCode.equals(mTargetId)) {
            getBaseHeadView().showHeadRightButton("发起交易",this);
        }else {
            if (mConversationType == Conversation.ConversationType.PRIVATE) {
                getBaseHeadView().showTitle(title);
                getBaseHeadView().showHeadRightImageButton(R.drawable.person_friend_icon_homepage, this);

            } else if(mConversationType == Conversation.ConversationType.GROUP) {
                getBaseHeadView().showHeadRightImageButton(R.drawable.person_friend_icon_hylb, this);

            } else if(mConversationType == Conversation.ConversationType.SYSTEM){
                getBaseHeadView().showTitle(title);
            }
        }


    }

    private boolean isAdmin(){
        return "1".equals(roleType) || "2".equals(roleType);
    }


    /**
     * 会话消息点击事件
     * 自定义消息的点击操作已经移到自定义消息的provider里面处理
     * 采用静态内部类和弱引用的写法避免内存泄漏
     */
    private static class ConversationListenerImp implements RongIM.ConversationBehaviorListener{
        WeakReference<ConversationActivity> mWeakReference;
        ConversationActivity  mConversationActivity;

        public ConversationListenerImp(ConversationActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            RongIM.setConversationBehaviorListener(this);
        }

        @Override
        public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
            if(mWeakReference.get() == null) return true;
            mConversationActivity = mWeakReference.get();
            String id = userInfo.getUserId();
            //系统的小助手不然后用户点击头像
            if(!TextUtils.isEmpty(id) && id.startsWith("1000") && id.length() == 6){
                return true;
            }

//            PersonalInfoActivity.startActivity(mConversationActivity,userInfo.getUserId(),1000);
            PersonalInfoActivity.startActivity(mConversationActivity,userInfo.getUserId());
            return true;
        }

        @Override
        public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
            return false;
        }

        @Override
        public boolean onMessageLinkClick(Context context, String s) {
            if(mWeakReference.get() != null){
                QrcodeHandler handler = new QrcodeHandler(mWeakReference.get());
                handler.resolvingCode(s, "");
                return true;
            }
            return false;
        }

        @Override
        public boolean onMessageClick(Context context, View view, Message message) {
            if(mWeakReference.get() == null) return true;
            mConversationActivity = mWeakReference.get();
            MessageContent messageContent = message.getContent();

            //修改融云的图片长按点击事件
            if(messageContent instanceof ImageMessage){
                Intent intent = new Intent(context, PicturePagerActivity.class);
                intent.setPackage(view.getContext().getPackageName());
                intent.putExtra("message", message);
                if(view.getContext() instanceof Activity){
                    ((Activity) view.getContext()).startActivityForResult(intent, PictureEditActivity.REQUEST_CODE);
                }
                return true;
            }

            //打开红包
            if(messageContent instanceof RPMessage){
                if(ClickUtil.isFastClick()) return false;
                mConversationActivity.mPresenter.getRedPackInfo(message);
            }

            //分享
            if(messageContent instanceof ShareMessage){
                ShareMessage shareMessage = (ShareMessage) message.getContent();
                if(!TextUtils.isEmpty(shareMessage.getTypeId())){
                    try {
                        int type = Integer.valueOf(shareMessage.getInfoType());
                        UIMessage uiMessage = UIMessage.obtain(message);
                        mConversationActivity.mShareHandler.shareHandle(view.getContext(),shareMessage.getTypeId(),type,uiMessage);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            //收藏
            if(messageContent instanceof CollectMessage){
                CollectMessage cMessage = (CollectMessage) message.getContent();
                mConversationActivity.mShareHandler.collectHandle(cMessage.getId(),cMessage.getType(),cMessage.getUrl());
            }

            //系统小助手
            if(messageContent instanceof ClassMessage
                    || messageContent instanceof DealMessage
                    || messageContent instanceof CircleMessage
                    || messageContent instanceof ArticleMessage
                    || messageContent instanceof TradeInfoMessage
                    || messageContent instanceof SystemMessage
                    || (messageContent instanceof TextMessage && "100001".equals(message.getTargetId()))  //通过文本消息和 TargetId = 100001 判断是不是系统消息
                    || messageContent instanceof MallMessage){

                return MessagePresenter.INSTANCE.messageClick(mConversationActivity,view,message);
            }

            return false;
        }


        //这里为了给原来的长按弹窗加 收藏功能 todo
        @Override
        public boolean onMessageLongClick(final Context context, final View view, final Message message) {
            if(mWeakReference.get() == null) return true;

            mConversationActivity = mWeakReference.get();
            mConversationActivity.currMessage = message;
            mConversationActivity.clickUserCode = message.getSenderUserId();
            String userCode = UserManager.getInstance().getUserCode();

            //红包消息不给撤回
            if(message.getContent() instanceof RPMessage)   return true;

            final UIMessage uiMessage                   = UIMessage.obtain(message);
            final List      messageItemLongClickActions = RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions(uiMessage);

            Collections.sort(messageItemLongClickActions, new Comparator<MessageItemLongClickAction>() {
                public int compare(MessageItemLongClickAction lhs, MessageItemLongClickAction rhs) {
                    return rhs.priority - lhs.priority;
                }
            });

            final ArrayList<String> titles = new ArrayList<>();
            Iterator                var7   = messageItemLongClickActions.iterator();

            while(var7.hasNext()) {
                MessageItemLongClickAction action = (MessageItemLongClickAction)var7.next();
                titles.add(action.getTitle(context));
            }

            //只能收藏这几种消息
            if(message.getContent() instanceof TextMessage
                    || message.getContent() instanceof ImageMessage
                    || message.getContent() instanceof ShareMessage
                    || message.getContent() instanceof VideoMessage){
                titles.add("收藏");
            }

            //只能转发这几种消息
            if(message.getContent() instanceof TextMessage
                    || message.getContent() instanceof FileMessage
                    || message.getContent() instanceof ImageMessage
                    || message.getContent() instanceof PTMessage
                    || message.getContent() instanceof MergeHistoryMessage
                    || message.getContent() instanceof VoiceMessage
                    || message.getContent() instanceof VideoMessage){
                titles.add("转发");
            }

            //只有群主或者管理员才有权限管理成员
            if(mConversationActivity.isAdmin() && !userCode.equals(mConversationActivity.clickUserCode) && mConversationActivity.groupType == GROUP_TYPE_CIRCLE){
                titles.add("禁言");
                titles.add("拉黑");
            }

            //给管理员添加个
            if(mConversationActivity.isAdmin() && !userCode.equals(mConversationActivity.clickUserCode) && !(message.getContent() instanceof RPMessage)){
                titles.add("撤回消息");
            }


            OptionsPopupDialog.newInstance(context, titles.toArray(new String[titles.size()])).setOptionsPopupDialogListener(
                    new OptionsPopupDialog.OnOptionsItemClickedListener() {
                        @Override
                        public void onOptionsItemClicked(int flag) {
                            if(flag < messageItemLongClickActions.size()){
                                ((MessageItemLongClickAction)messageItemLongClickActions.get(flag)).listener.onMessageItemLongClick(context, uiMessage);

                            }else{
                                String title = titles.get(flag);
                                //收藏消息
                                if(title.equals("收藏")){
                                    mConversationActivity.mPresenter.collectMessage(message);
                                }

                                //转发
                                if(title.equals("转发")){
                                    ConversationListActivity.startActivity(mConversationActivity,REQUEST_SHARE_RELAY ,Constant.SELECT_TYPE_RELAY);
                                }

                                //禁言
                                if(title.equals("禁言")){
                                    mConversationActivity.showSelectDialog();
                                }

                                //拉黑
                                if(title.equals("拉黑")){
                                    mConversationActivity.removeMemberDialog();
                                }

                                //撤回消息
                                if(title.equals("撤回消息")){
                                    mConversationActivity.recallMessage(message);
                                }
                            }
                        }
                    }).show();
            return true;
        }
    }

    //撤回消息
    private void recallMessage(final Message message){
        RongIMClient.getInstance().recallMessage(message, "", new RongIMClient.ResultCallback<RecallNotificationMessage>() {
            @Override
            public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) { }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) { }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtil.i("撤回消息失败  : " + errorCode);
                ToastUtil.showShort(ConversationActivity.this, "撤回消息失败");
            }
        });
    }

    //选择禁言时间
    private ListDialog mListDialog;
    String [] datas = new String[]{"1天", "2天", "3天", "4天", "30天", "永久"};
    public void showSelectDialog() {
        if (mListDialog == null) {
            mListDialog = new ListDialog(this, datas, true, R.style.cusDialog);
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 5) {
                        int days = Integer.parseInt(datas[position] .replace("天","").trim());
                        forbidentSay(days * 24 * 60);       //后台接口按分计算
                    } else {
                        int days = -1;
                        forbidentSay(days);
                    }
                }
            });
        }
        mListDialog.show();
    }


    //time 按分钟算  0代表取消禁言
    private void forbidentSay(int time) {
         mPresenter.shutup(groupId,clickUserCode,time);
    }

    private AlertDialog dialog;
    //删除成员
    private void removeMemberDialog() {
        dialog =
            DialogUtil.showInputDialog(this, false, "", "确认将此成员移出圈子?",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.removeMember(groupId,clickUserCode);
                        } else {
                            Toast.makeText(ConversationActivity.this, "登录后才能操作", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void initFragment() {
        /* 新建 ConversationFragment 实例，通过 setUri() 设置相关属性*/
        fragment = new MyConversationFragment();
        WeakReference<MyConversationFragment> weakReference = new WeakReference<>(fragment);
        if(weakReference.get() != null){
            fragment = weakReference.get();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                         .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                         .appendQueryParameter("targetId", mTargetId)
                         .appendQueryParameter("targetIds", mTargetIds).build();

            fragment.setUri(uri);

            /* 加载 ConversationFragment */
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.rong_content, fragment);
            transaction.commitAllowingStateLoss();
        }
    }


    //去快速交易
    private void gotoFastDeal(String id) {
        String isFinish = getIntent().getData().getQueryParameter("isFinish");
        if ("1".equals(isFinish)) {
            ToastUtil.showShort(this, "当前商品正在交易中");
            return;
        }

        if ("2".equals(isFinish)) {
            ToastUtil.showShort(this, "当前商品已交易完成");
            return;
        }

        if (UserManager.getInstance().isLogin(this)) {
            GotoUtil.goToActivity(this, FastDealActivity.class, 0, Integer.valueOf(id));
        }
    }

    private void initRongIm() {
        connectRongIm();
        new ConversationListenerImp(this);
        new IMentionedInputListenerImp(this);
    }

    //设置@成员数据
    private static class IMentionedInputListenerImp implements IMentionedInputListener{
        private WeakReference<ConversationActivity> mWeakReference;
        private ConversationActivity mActivity;

        private IMentionedInputListenerImp(ConversationActivity conversationActivity){
            mWeakReference = new WeakReference<>(conversationActivity);
            RongMentionManager.getInstance().setMentionedInputListener(this);
        }

        @Override
        public boolean onMentionedInput(Conversation.ConversationType conversationType, String targetId) {
            if(mWeakReference.get() == null) return true;
            mActivity = mWeakReference.get();
            Class targetClass = null;
            switch (mActivity.groupType){
                case GROUP_TYPE_CIRCLE:
                    targetClass = AllGroupMemberActivity.class;
                    break;
                case GROUP_TYPE_CUSTOM:
                    targetClass = AllGroupInfoMemberActivity.class;
                    break;
            }
            Intent intent = new Intent(MyApplication.getInstance(), targetClass);
            intent.putExtra("count", mActivity.groupCount);
            intent.putExtra("targetId", targetId);
            intent.putExtra("isAiter", true);
            intent.putExtra("groupId", mActivity.groupId);
            mActivity.startActivity(intent);
            return true;
        }
    }


    //连接融云操作
    private void connectRongIm() {
        String token = UserManager.getInstance().getImToken();
        RongImHelper.connect(MyApplication.getInstance(), token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {}

            @Override
            public void onSuccess(String s) {
                RongImHelper.initDefaultConfig();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {}
        });
    }

    private void showExitDialog() {
        exitDialog = DialogUtil.showInputDialog(this, false, "", "尚未上传完成视频，确认退出？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });
    }

    public void setSelectMessage(List<Message> selectMessage) {
        if(this.selectMessage == null){
            this.selectMessage = new ArrayList<>();
        }
        this.selectMessage.clear();
        this.selectMessage.addAll(selectMessage);
    }

    @Override
    public void setPresenter(ConversationContract.Presenter presenter) {
        mPresenter = presenter;
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
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {}

}
