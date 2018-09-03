package com.gxtc.huchuan.ui.live.conversation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PPTAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.UploadPPTFileBean;
import com.gxtc.huchuan.bean.event.EventLiveRefreshBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.bean.event.EventPPTBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.event.EventStartLiveBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.PayBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.im.Extension;
import com.gxtc.huchuan.im.itemview.AbsMessageView;
import com.gxtc.huchuan.im.itemview.CountDownMessageView;
import com.gxtc.huchuan.im.itemview.ImageMessageView;
import com.gxtc.huchuan.im.itemview.NoneMessageView;
import com.gxtc.huchuan.im.itemview.ReceivedMessageView;
import com.gxtc.huchuan.im.itemview.RedPacketMessageView;
import com.gxtc.huchuan.im.itemview.TextMessageView;
import com.gxtc.huchuan.im.itemview.VoiceMessageView;
import com.gxtc.huchuan.im.manager.AudioPlayManager;
import com.gxtc.huchuan.im.manager.AudioRecordManager;
import com.gxtc.huchuan.im.manager.ConversationManager;
import com.gxtc.huchuan.im.manager.MessageManager;
import com.gxtc.huchuan.im.provide.RedPacketMessage;
import com.gxtc.huchuan.im.provide.RemoveMessage;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.pop.PopDiscuss;
import com.gxtc.huchuan.pop.PopManage;
import com.gxtc.huchuan.pop.PopRemoveMessge;
import com.gxtc.huchuan.pop.PopReward;
import com.gxtc.huchuan.pop.PopRewardShow;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.common.HackyViewPager;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.intro.ShareImgActivity;
import com.gxtc.huchuan.ui.live.setting.normal.NormalSettingActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.LiveMemberManagerActivity;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.KeyboardUtils;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.Constant.SELECT_TYPE_CARD;


/**
 * Created by Gubr on 2017/2/20.
 *
 */
public class LiveConversationActivity extends BaseTitleActivity implements
        LiveConversationContract.View, ViewPager.OnPageChangeListener, View.OnClickListener, AbsMessageView.MessageViewListener,
        PopRemoveMessge.PopManageListener {

    private static final String TAG           = "LiveConversationActivit";
    private static final int    REQUSET_IMAGE = 1 << 3;

    public static final int REQUEST_PPT = 1 << 4;
    public static final int REQUSET_INSERT = 100;

    @BindView(R.id.conversation_ppt_show) HackyViewPager mConversationPptShow;
    @BindView(R.id.cb_isshwoPPT)          CheckBox       mCbIsshwoPPT;
    @BindView(R.id.rc_conversation)       RecyclerView   mRcConversation;

    //界面右边那排按钮
    @BindView(R.id.tv_discuss_count)      TextView       mTvDiscussCount;
    @BindView(R.id.img_head)              ImageView      imgFloatHead;
    @BindView(R.id.img_follow)            ImageView      imgFollow;

    @BindView(R.id.extension)                       Extension                   mExtension;
    @BindView(R.id.swipe_convertasion)              SwipeRefreshLayout          mSwipeConvertasion;
    @BindView(R.id.ib_shang)                        ImageButton                 mIbShang;
    @BindView(R.id.extension_area)                  LinearLayout                mExtensionArea;
    @BindView(R.id.conversation_ppt_show_indicator) TextView                    mConversationPptShowIndicator;

    //             课堂id       主讲人       创建者  课堂名称
    private String charRoomgId, mainSpearker, host, charRoomName;


    private ChatInfosBean mBean = null;

    private              boolean      isDiscussOpen     = true;
    private              boolean      isPPtModel        = false;
    private              boolean      isTouchList       = false;
    private              List<String> path              = new ArrayList<>();
    private static final int          RC_CAMERA_PERM    = 123;
    public static final  int          RECARD_AUDIO_PERM = 234;

    private ArrayList<Message>            discussMessages;
    private MultiItemTypeAdapter<Message> mMessageMultiItemTypeAdapter;
    private LinearLayoutManager           mLinearLayoutManager;

    private LiveConversationContract.Presenter mPresenter;
    private EditText                           mEditText;
    private PopReward                          mPopReward;
    private OrdersRequestBean                  mRequestBean;
    private PopRewardShow                      mPopRewardShow;
    private ConversationManager.ItemScrollBean mItemScrollBean;
    private PopRemoveMessge                    mRemoveMessagePopup;
    private PopDiscuss                         mPopDiscuss;
    private ProgressDialog                     mLoadDialog;
    private AlertDialog mAlertDialog;
    private SeriesPageBean seriesBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        AndroidBug5497Workaround.assistActivity(this);
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
        mBean = (ChatInfosBean) getIntent().getSerializableExtra("bean");
        seriesBean = (SeriesPageBean) getIntent().getSerializableExtra("seriesBean");
        if (mBean == null) {
            Toast.makeText(this, "数据出错，请稍后再试", Toast.LENGTH_SHORT).show();
        }
        mSwipeConvertasion.setColorSchemeResources(Constant.REFRESH_COLOR);


        if (UserManager.getInstance().isLogin()) {
            //如果是普通用户 或是结束直播都会显示普通的输入框
            if (!"1".equals(mBean.getRoleType()) && !"3".equals(mBean.getRoleType())) {
                mExtension.audienceInputModel();
            } else {
                mExtension.hostInputModel();
            }
        }

        mEditText = mExtension.getEditText();
    }

    @Override
    public void initListener() {
        mExtension.setExtensionListener(new MyExtendsionListener());
        mExtension.setClickInsertListener(new Extension.onClickInsertListener() {
            @Override
            public void onClick(View v, int position, String title) {
                if(mBean == null){
                    return;
                }

                switch (title){
                    case "PPT":
                        HashMap map = new HashMap();
                        map.put("chatInfoId", charRoomgId);
                        map.put("model", isPPtModel ? "true" : "false");        //通过这个来确定是否要显示发送图片
                        GotoUtil.goToActivityWithDataAndForResult(LiveConversationActivity.this, PPTSelectActivity.class, map, REQUEST_PPT);
                        break;

                    case "名片":
                        Intent intent = new Intent(LiveConversationActivity.this, FocusActivity.class);
                        intent.putExtra("focus_flag", "2");
                        intent.putExtra("select_type_card", SELECT_TYPE_CARD);
                        intent.putExtra("type", Conversation.ConversationType.CHATROOM);
                        startActivityForResult(intent, ConversationActivity.REQUEST_SHARE_CARD);
                        break;

                    case "圈子":
                        LiveInsertChooseActivity.startActivity(mBean, Conversation.ConversationType.CHATROOM,LiveInsertChooseActivity.TYPE_CIRCLE, LiveConversationActivity.this);
                        break;

                    case "文章":
                        LiveInsertChooseActivity.startActivity(mBean, Conversation.ConversationType.CHATROOM,LiveInsertChooseActivity.TYPE_ARTICLE, LiveConversationActivity.this);
                        break;

                    case "课程":
                        LiveInsertChooseActivity.startActivity(mBean, Conversation.ConversationType.CHATROOM,LiveInsertChooseActivity.TYPE_CLASS, LiveConversationActivity.this);
                        break;

                    case "交易":
                        LiveInsertChooseActivity.startActivity(mBean, Conversation.ConversationType.CHATROOM,LiveInsertChooseActivity.TYPE_DEAL, LiveConversationActivity.this);
                        break;

                    case "商品":
                        LiveInsertChooseActivity.startActivity(mBean, Conversation.ConversationType.CHATROOM,LiveInsertChooseActivity.TYPE_MALL, LiveConversationActivity.this);
                        break;

                    case "专题":
                        SpecialSelectActivity.startActivity(mBean, LiveConversationActivity.this);
                        break;
                }
            }
        });

        mCbIsshwoPPT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText(isChecked ? "隐藏PPT" : "显示PPT");
                mConversationPptShow.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mConversationPptShowIndicator.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });


        mSwipeConvertasion.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getUpRemoteHistory();
            }
        });


        mRcConversation.addOnItemTouchListener(
                new android.support.v7.widget.RecyclerView.OnItemTouchListener() {

                    @Override
                    public boolean onInterceptTouchEvent(android.support.v7.widget.RecyclerView rv, MotionEvent e) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (mExtension != null && mExtension.isVoiceShow()) {
                                    mExtension.closeInput();
                                }
                                if (mExtension != null && mExtension.isInsertLayoutShow()) {
                                    mExtension.hideInertLayout();
                                }
                                isTouchList = true;
                                break;

                            case MotionEvent.ACTION_UP:
                                isTouchList = false;
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(android.support.v7.widget.RecyclerView rv, MotionEvent e) {}

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
                });


        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChangeState(int state) {
                if(state == KeyboardUtils.KEYBOARD_CLOSE){
                    if(mExtension != null) mExtension.hidtWirte();
                }
            }
        });
    }

    @Override
    public void initData() {
        EventBusUtil.post(new EventStartLiveBean(mBean.getId()));
        EventBusUtil.register(this);

        charRoomgId = mBean.getId();
        mainSpearker = mBean.getMainSpeaker();
        host = mBean.getMainSpeaker();          //创建这个课程的人
        charRoomName = mBean.getSubtitle();

        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(charRoomName);
        getBaseHeadView().showHeadRightImageButton(R.drawable.person_icon_set, this);


        isPPtModel = true;          //设置是否是ppt模式
        mCbIsshwoPPT.setText(isPPtModel ? "隐藏PPT" : "显示PPT");

        showChatCommentCount(0);

        if(!mBean.isFolow() && !mBean.isSelff()){
            imgFollow.setImageResource(R.drawable.live_icon_attention_1);
        }else{
            imgFollow.setVisibility(View.INVISIBLE);
        }

        AudioRecordManager.getInstance().startRecord(Conversation.ConversationType.CHATROOM, charRoomgId);

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mMessageMultiItemTypeAdapter = new MultiItemTypeAdapter<>(this, MessageManager.getInstance().getMessages());
        mMessageMultiItemTypeAdapter.addItemViewDelegate(new TextMessageView(this,mMessageMultiItemTypeAdapter).setMessageViewListener(this));
        mMessageMultiItemTypeAdapter.addItemViewDelegate(new ImageMessageView(this,mMessageMultiItemTypeAdapter).setMessageViewListener(this));
        mMessageMultiItemTypeAdapter.addItemViewDelegate(new VoiceMessageView(this,mMessageMultiItemTypeAdapter).setMessageViewListener(this));
        mMessageMultiItemTypeAdapter.addItemViewDelegate(MultiItemTypeAdapter.NONE_TYPE,new NoneMessageView(this));
        mMessageMultiItemTypeAdapter.addItemViewDelegate(new CountDownMessageView(this));
        mMessageMultiItemTypeAdapter.addItemViewDelegate(new ReceivedMessageView(this));
        mMessageMultiItemTypeAdapter.addItemViewDelegate(new RedPacketMessageView(this).setOnClickListener(this));
        mRcConversation.setLayoutManager(mLinearLayoutManager);
        mRcConversation.setAdapter(mMessageMultiItemTypeAdapter);
        if (ConversationManager.getInstance().mItemScrollBean.flag) {
            mRcConversation.scrollToPosition(ConversationManager.getInstance().mItemScrollBean.getPosition());
        }

        mConversationPptShow.addOnPageChangeListener(this);
        mConversationPptShow.setOffscreenPageLimit(0);
        initFloatlable();
        initViewpager();


        new LiveConversationPresenter(this, mBean, charRoomgId, mainSpearker, host);
        // 获取课程的禁言等状态接口
        mPresenter.getChatinfosStatus();
        if (ConversationManager.getInstance().isCanLoadRemoteMessage()) {

        } else {
            mRcConversation.loadFinishNotView();
        }


        initSimpeDiscuss();    //加载讨论的列表
        joinChatRoom();
    }

    private void share() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        String title = mBean.getSubtitle();
                        String desc = TextUtils.isEmpty(mBean.getDesc()) ? title : mBean.getDesc();
                        UMShareUtils shareUtils = new UMShareUtils(LiveConversationActivity.this);
                        String uri = mBean.getChatRoomHeadPic();
                        shareUtils.shareLive(uri, title, desc, mBean.getShareUrl());
                        shareUtils.setOnItemClickListener(
                                new UMShareUtils.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int flag) {
                                        switch (flag){
                                            //这里跳去邀请卡
                                            case 0:
                                                Intent intent = new Intent(LiveConversationActivity.this, ShareImgActivity.class);
                                                intent.putExtra("chatInfoId", mBean.getId());
                                                startActivity(intent);
                                                break;

                                            //分享到动态
                                            case 1:
                                                IssueDynamicActivity.share(LiveConversationActivity.this,mBean.getId(),"2",mBean.getSubtitle(),mBean.getHeadPic());
                                                break;

                                            //分享到好友
                                            case 2:
                                                ConversationListActivity.startActivity(LiveConversationActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT,Constant.SELECT_TYPE_SHARE);
                                                break;

                                            //收藏
                                            case 3:
                                                mPresenter.collect(mBean.getId());
                                                break;

                                            //二维码
                                            case 4:
                                                ErWeiCodeActivity.startActivity(LiveConversationActivity.this, ErWeiCodeActivity.TYPE_CLASSROOM, Integer.valueOf(mBean.getId()), "");
                                                break;
                                        }

                                    }
                                });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(LiveConversationActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(LiveConversationActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    public void ShowCopyDelCollection(String userCode, String myCode, final Message message){
        final List<String> itemList = new ArrayList<>();
        if(userCode.equals(myCode) || mBean.isSelff() &&
                ((message.getContent() instanceof TextMessage) ||
                 (message.getContent() instanceof ImageMessage) ||
                        (message.getContent() instanceof VoiceMessage))){
            itemList.add("删除");

        }

        String[] s = new String[itemList.size()];

        if(itemList.size() == 0)  return;
        final MyActionSheetDialog dialog = new MyActionSheetDialog(this,
                itemList.toArray(s), null);
        dialog.isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginBottom(
                0).cornerRadius(0f).dividerHeight(1).itemTextColor(
                getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (itemList.get(position)){
                        case "删除" :
                            showRemoveMessageDialog(message);
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showChatCommentCount(int add){
        int count = StringUtil.toInt(mBean.getChatCommentCount()) + add;
        mTvDiscussCount.setText(count > 999 ? "999+" : count + "");
        mBean.setChatCommentCount(count);
    }

    public void showRemoveMessageDialog( Message message) {
        mPresenter.removeMessage(message);
    }

    public void getScollYDistance() {
        if (mLinearLayoutManager == null) {
            return;
        }
        int  position               = mLinearLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = mLinearLayoutManager.findViewByPosition(position);
        if (firstVisiableChildView == null) {
            return;
        }
        int itemHeight = firstVisiableChildView.getHeight();
        ConversationManager.getInstance().mItemScrollBean.setItemPosition(position,
                firstVisiableChildView.getTop());

    }

    @Override
    protected void onPause() {
        getScollYDistance();
        super.onPause();
    }

    /**
     * 加载讨论的列表
     */
    private void initSimpeDiscuss() {
        mPresenter.remoteTopicHistory();
    }

    /**
     * 初始化 浮窗的标签
     */
    private void initFloatlable() {
        ImageHelper.loadCircle(this, imgFloatHead, mBean.getHeadPic(), R.drawable.live_head_icom_temp);
    }


    /**
     * 设置ppt
     */
    private void initViewpager() {
        mCbIsshwoPPT.setVisibility(isPPtModel ? View.VISIBLE : View.INVISIBLE);
        mConversationPptShow.setVisibility(isPPtModel ? View.VISIBLE : View.GONE);
        PPTAdapter pptAdapter = new PPTAdapter(this, new ArrayList<Uri>());
        mConversationPptShow.setAdapter(pptAdapter);
    }

    @Override
    public void showLoadDialog(boolean isShow) {
        if(mLoadDialog == null){
            mLoadDialog = new ProgressDialog(this);
            mLoadDialog.setMessage("玩命加载课堂中...");
        }
        if(isShow){
            mLoadDialog.show();
        }else{
            mLoadDialog.dismiss();
        }
    }

    @Override
    public void showDiscussIntro(List<Message> messages) {
        if(discussMessages == null){
            discussMessages = new ArrayList<>();
        }
        if(messages != null){
            discussMessages.addAll(messages);
        }
    }


    @Override
    public void showDiscussLabel(String count) {
        mTvDiscussCount.setText(count);
        mTvDiscussCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindDiscussLabel() {
        mTvDiscussCount.setVisibility(View.GONE);
    }

    @Override
    public void loadConversation(List<Message> messages) {
        mRcConversation.addHeadView(new ImageView(this));
    }


    @Override
    public void addMessage(final Message message) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRcConversation == null) return;
                    MessageManager.getInstance().addMessage(message);
                    mRcConversation.notifyItemChanged(mRcConversation.getAdapter().getItemCount() - 1);
                    if(!isTouchList){
                        mRcConversation.smoothScrollToPosition(mRcConversation.getAdapter().getItemCount() - 1);
                    }
                    int msgCount = mBean.getMessageNum();
                    mBean.setMessageNum(++msgCount);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendPPTChnageMessage() {
        mPresenter.sendPPTChnageMessage();
    }


    @Override
    public void showPPT(List<Uri> datas) {
        if (datas.size() > 0) {
            mCbIsshwoPPT.setVisibility(View.VISIBLE);

            PPTAdapter adapter = (PPTAdapter) mConversationPptShow.getAdapter();
            adapter.setUris(datas);
            mConversationPptShow.getAdapter().notifyDataSetChanged();
        } else {
            mCbIsshwoPPT.setChecked(false);
            mCbIsshwoPPT.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 下拉刷新 加载数据后 调用
     */
    @Override
    public void upRefreshFinish() {
        if (mSwipeConvertasion != null) {
            if (mRcConversation != null) {
                mRcConversation.notifyChangeData();
                mLinearLayoutManager.scrollToPositionWithOffset(MessageManager.getInstance().getNewCount() - 1,0);
            }
            mSwipeConvertasion.setRefreshing(false);
        }
    }

    /**
     * 不再需要上拉刷新时 调用
     */
    @Override
    public void downRefreshFinish() {
        if (mRcConversation != null) {
            mRcConversation.loadFinishNotView();
        }
    }


    @Override
    public void notifyChangeData() {
        if (mRcConversation != null) {
            mRcConversation.notifyChangeData();
            if(!ConversationManager.getInstance().mItemScrollBean.flag){
                mRcConversation.smoothScrollToPosition(0);
                //刚进入课堂的时候不要一下子滑道底部
                //mRcConversation.smoothScrollToPosition(mMessageMultiItemTypeAdapter.getItemCount() - 1);
            }
        }
    }

    @Override
    public void showAuthor() {

    }

    @Override
    public void showRedpacket() {

    }

    @Override
    public void showKickOutRoom() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLong(LiveConversationActivity.this,"你已被管理员加入黑名单");
                finish();
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mSwipeConvertasion.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {

    }


    @Override
    public void setPresenter(LiveConversationContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        mConversationPptShowIndicator.setText(
                mConversationPptShow.getCurrentItem() + 1 + "/" + mConversationPptShow.getAdapter().getCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.ll_discuss_area,
        R.id.img_head,
        R.id.img_follow,
        R.id.layout_invite})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送红包
            case R.id.iv_zan:
                showPopReward((UserInfo) v.getTag());
                break;

            //返回按钮
            case R.id.headBackButton:
                finish();
                break;

            //设置按钮
            case R.id.HeadRightImageButton:
                if (mBean == null) {
                    Toast.makeText(this, "数据出错，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                NormalSettingActivity.startActivity(this, mBean,seriesBean);
                break;

            //跳到个人主页
            case R.id.img_head:
                LiveHostPageActivity.startActivity(LiveConversationActivity.this,"1", mBean.getChatRoom());
                break;

            case R.id.ll_red_packet_area:
                RedPacketMessage message = (RedPacketMessage) v.getTag();
                showRedpacketIntro(message);
                break;

            //邀请好友
            case R.id.layout_invite:
                invite();
                break;

            //关注讲师
            case R.id.img_follow:
                mPresenter.follow();
                break;

            //显示讨论组消息
            case R.id.ll_discuss_area:
                if (mPopDiscuss == null)
                    mPopDiscuss = new PopDiscuss(LiveConversationActivity.this, R.layout.pop_topic_comment);
                mPopDiscuss.setChatinfosBean(mBean);
                if (discussMessages != null) {
                    mPopDiscuss.setData(discussMessages);
                }
                mPopDiscuss.showPopOnRootView(LiveConversationActivity.this);
                break;
        }
    }


    //邀请成员
    private void invite() {
        if(mBean != null){
            if(ChatInfosBean.ROLE_STUDENT.equals(mBean.getRoleType())){
                share();
            }else{
                Intent intent1 = new Intent(this, LiveMemberManagerActivity.class);
                intent1.putExtra("bean", mBean);
                intent1.putExtra("type", "0");
                startActivity(intent1);
            }
        }
    }


    private void showRedpacketIntro(RedPacketMessage message) {
        if (mPopRewardShow == null) {
            mPopRewardShow = new PopRewardShow(this, R.layout.pop_show_reward_layout);
        }

        mPopRewardShow.setMessage(message);
        mPopRewardShow.showPopOnRootView(this);
    }


    //允许主持人跟嘉宾删除消息
    @Override
    public boolean onLongClick(View v, Message message) {
        String myCode = UserManager.getInstance().getUserCode();
        String userCode = message.getContent().getUserInfo().getUserId();

        ShowCopyDelCollection(userCode, myCode, message);
        return false;
    }


    @Override
    public void onAction(Message message, @PopManage.Status int status, int position) {
        mPresenter.removeMessage(message);
    }


    private class MyExtendsionListener implements Extension.ExtensionListener {

        String[] pers = new String[]{Manifest.permission.RECORD_AUDIO};

        @Override
        public void onTextSendClick() {
            mPresenter.sendMessage(mEditText.getText().toString());
            mEditText.setText("");
        }

        @Override
        public boolean getRecordPer() {
            requestRecordPer(new PermissionsResultListener() {
                @Override
                public void onPermissionGranted() {
                    mExtension.showRecordLayout();
                }

                @Override
                public void onPermissionDenied() {
                    JumpPermissionManagement.GoToSetting(LiveConversationActivity.this);
                }
            });
            return false;
        }

        @Override
        public void onImageClick() {
            sendImageMessage();
        }


        @Override
        public void onStartRecord(final Context context) {
            requestRecordPer(new PermissionsResultListener() {
                @Override
                public void onPermissionGranted() {
                    AudioRecordManager.getInstance().startRec(context);
                }

                @Override
                public void onPermissionDenied() {
                    JumpPermissionManagement.GoToSetting(LiveConversationActivity.this);
                }
            });
        }

        @Override
        public void onStopRecord() {
            requestRecordPer(new PermissionsResultListener() {
                @Override
                public void onPermissionGranted() {
                    AudioRecordManager.getInstance().stopRec();
                }

                @Override
                public void onPermissionDenied() {
                    JumpPermissionManagement.GoToSetting(LiveConversationActivity.this);
                }
            });
        }

        @Override
        public void onStopRecordAndSend() {
            requestRecordPer(new PermissionsResultListener() {
                @Override
                public void onPermissionGranted() {
                    AudioRecordManager.getInstance().stopRec();
                    mPresenter.sendVoiceMessage();
                }

                @Override
                public void onPermissionDenied() {
                    JumpPermissionManagement.GoToSetting(LiveConversationActivity.this);
                }
            });
        }

        @Override
        public void onCancnelRecord() {
            AudioRecordManager.getInstance().deleteAudioFile();
            AudioRecordManager.getInstance().stopRec();
        }

        @Override
        public void onSend() {
            mPresenter.sendVoiceMessage();
        }


        @Override
        public void onPPT() {
        }

        @Override
        public void onSimpleSendClick(String s, boolean checked) {
            mPresenter.sendAudienceMessage(s, checked);
        }

    }

    private void sendImageMessage() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机存储权限", pers, RC_CAMERA_PERM, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                mPresenter.showImageSelectView();
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    //获取录音权限
    private void requestRecordPer(PermissionsResultListener listener){
        String[] pers = new String[]{Manifest.permission.RECORD_AUDIO};
        performRequestPermissions("此应用需要录音权限", pers, RECARD_AUDIO_PERM, listener);
    }

    @Override
    public boolean getIsAsk() {
        return false;
    }


    @Override
    public void sendMessage() {
      ToastUtil.showShort(MyApplication.getInstance(),"分享成功");
    }


    @Override
    public void addDiscussIntro(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (discussMessages != null) {
                    discussMessages.add(0, message);
                    if(mTvDiscussCount != null && mBean != null){
                        int count = StringUtil.toInt(mBean.getChatCommentCount());
                        mTvDiscussCount.setText(count++ > 999 ? "999+" : count + "");
                        mBean.setChatCommentCount(count);
                    }

                    if(mPopDiscuss != null){
                        mPopDiscuss.addDiscussMessage(message);
                    }
                }
            }
        });

    }

    @Override
    public void setSilentModel(final boolean b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mExtension == null) {
                    return;
                }
                if (!mExtension.getInputModel()) {
                    if(mPopDiscuss != null) {
                        mPopDiscuss.setChatinfosBean(mBean);
                    }
                    if (b) {
                        mExtension.getEditText().setHint("您已被管理员禁言");
                        mExtension.getEditText().setEnabled(false);
                    } else {
                        mExtension.getEditText().setHint("输入你的问题或者讨论");
                        mExtension.getEditText().setEnabled(true);

                    }
                }
            }
        });
    }

    @Override
    public void changePPT(List<Uri> datas) {
        PPTAdapter adapter = (PPTAdapter) mConversationPptShow.getAdapter();
        adapter.setUris(datas);
        mConversationPptShow.getAdapter().notifyDataSetChanged();
        mConversationPptShow.setCurrentItem(adapter.getCount());
        if (datas.size() > 0) {
            mCbIsshwoPPT.setChecked(true);
            mCbIsshwoPPT.setVisibility(View.VISIBLE);
        } else {
            mCbIsshwoPPT.setVisibility(View.INVISIBLE);
            mCbIsshwoPPT.setChecked(false);
        }
    }

    @Override
    public void removeMessage(Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRcConversation != null) {
                    mRcConversation.notifyChangeData();
                    if (mRemoveMessagePopup != null && mRemoveMessagePopup.isShowing()) {
                        mRemoveMessagePopup.dismiss();
                        int msgCount = mBean.getMessageNum();
                        mBean.setMessageNum(--msgCount);
                    }
                }
            }
        });

    }

    @Override
    public void showCollectResult(boolean isSuccecc, String msg) {
        if(isSuccecc){
            if("0".equals(mBean.getIsCollect())){
                mBean.setIsCollect("1");
                ToastUtil.showShort(this, "收藏成功");
            }else{
                mBean.setIsCollect("0");
                ToastUtil.showShort(this, "取消收藏");
            }
        }else{
            ToastUtil.showShort(this, msg);
        }
    }


    private AnimationDrawable followAnima;

    @Override
    public void showFollowResult(boolean isSuccess, String msg) {
        if(isSuccess){
            mBean.setIsFollow("1");
            EventBusUtil.post(new EventLiveRefreshBean(mBean));
            followAnima = (AnimationDrawable) getResources().getDrawable(R.drawable.anim_follow);
            if(followAnima != null){
                imgFollow.setImageDrawable(followAnima);
                imgFollow.setVisibility(View.VISIBLE);
                followAnima.start();
                int dur = followAnima.getDuration(0);
                int totalDuration = dur * followAnima.getNumberOfFrames();
                Subscription sub =
                    Observable.timer(totalDuration, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                if(imgFollow != null){
                                    imgFollow.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                RxTaskHelper.getInstance().addTask(this, sub);
            }
        }
    }

    @Override
    public void showRemoveDiscussIntro(Message message) {
        RemoveMessage reMessage = (RemoveMessage) message.getContent();
        String msgId = reMessage.getContent();
        if(mPopDiscuss != null){
            mPopDiscuss.removeDiscussMessage(msgId);
        }
        showChatCommentCount(-1);
    }

    public static void startActivity(Context context, ChatInfosBean bean) {
        Intent intent = new Intent(context, LiveConversationActivity.class);
        intent.putExtra("bean", bean);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, ChatInfosBean bean,SeriesPageBean mData) {
        Intent intent = new Intent(context, LiveConversationActivity.class);
        intent.putExtra("bean", bean);
        intent.putExtra("seriesBean", mData);
        context.startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Constant.ResponseCode.CIRCLE_ISSUE){
            ToastUtil.showShort(this,"分享成功");
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_PPT) {
            String clickImage = data.getStringExtra("clickImage");
            if (clickImage != null) {
                mPresenter.sendPPTImageMessage(clickImage);
            }
        }

        if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK){
            EventSelectFriendBean  bean = data.getParcelableExtra(Constant.INTENT_DATA);
            String targetId = bean.targetId;
            Conversation.ConversationType type = bean.mType;
            mPresenter.shareMessage(targetId,type,mBean.getSubtitle(),mBean.getFacePic(),mBean.getId(),bean.liuyan);
        }


        if(requestCode == REQUSET_INSERT && resultCode == RESULT_OK){
            Message msg = data.getParcelableExtra(Constant.INTENT_DATA);
            addMessage(msg);
        }

        //发送名片
        if(requestCode == ConversationActivity.REQUEST_SHARE_CARD && resultCode == RESULT_OK){
            EventSelectFriendForPostCardBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            mPresenter.sendVisitingCard(bean);
        }
    }


    /**
     * 加入聊天室
     */
    private void joinChatRoom() {
        if (RongIM.getInstance().getCurrentConnectionStatus().equals(
                RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
            //-1 不从容云拉数据
            RongIMClient.getInstance().joinChatRoom(charRoomgId, -1,
                    new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            ToastUtil.showShort(LiveConversationActivity.this, "加入聊天室失败");
                        }
                    });
        } else {
            RongIM.connect(UserManager.getInstance().getImToken(),
                    new RongIMClient.ConnectCallback() {

                        @Override
                        public void onTokenIncorrect() {}

                        @Override
                        public void onSuccess(String s) {
                            //-1 不从容云拉数据
                            RongIMClient.getInstance().joinChatRoom(charRoomgId, -1,
                                    new RongIMClient.OperationCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "onSuccess joinChatRoom: 加入成功");
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                            Log.d(TAG, "errorCode:" + errorCode);
                                            Log.d(TAG, "onError joinChatRoom: 加入失败");
                                        }
                                    });
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });
        }
    }


    private UserInfo receiverUserinfo;

    /**
     * 显示红包
     * @param userinfo
     */
    private void showPopReward(UserInfo userinfo) {
        receiverUserinfo = userinfo;//
        if (mPopReward == null) {
            mPopReward = new PopReward(this, R.layout.pop_reward_layout);

            mPopReward.setPriceList(new double[]{5, 10, 20, 30, 40, 50});
            mPopReward.setPayPriceListener(new PopReward.PayPriceListener() {
                @Override
                public void onPriceSelected(double price) {

                    BigDecimal moneyB = new BigDecimal(price);

                    //计算总价
                    double total = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
                    String     id           = mBean.getId();
                    String     chatRoomName = mBean.getChatRoomName();
                    JSONObject jsonObject   = new JSONObject();
                    jsonObject.put("contId", id);
                    jsonObject.put("type", "zb");
                    jsonObject.put("userCode", receiverUserinfo.getUserId());
                    jsonObject.put("name", receiverUserinfo.getName());
                    String extra = jsonObject.toJSONString();

                    mRequestBean = new OrdersRequestBean();
                    mRequestBean.setToken(UserManager.getInstance().getToken());
                    mRequestBean.setTransType("DS");
                    mRequestBean.setTotalPrice(total + "");
                    mRequestBean.setExtra(extra);
                    mRequestBean.setGoodsName("打赏红包-" + chatRoomName);

                    GotoUtil.goToActivity(LiveConversationActivity.this, PayActivity.class, Constant.INTENT_PAY_RESULT, mRequestBean);
                }

            });
        }
        mPopReward.setUserCode(userinfo);

        mPopReward.showPopOnRootView(this);
    }
    
    @Subscribe
    public void onEvent(EventLoadBean bean){
        getBaseLoadingView().show(bean.isLoading);
    }


    @Subscribe
    public void PlayEvent(EventStartLiveBean data) {
        if (!data.getId().equals(mBean.getId())) {
            finish();
        }
    }


    @Subscribe
    public void EventPPT(EventPPTBean bean) {
        ArrayList<Uri> uris = new ArrayList<>();

        for (UploadPPTFileBean uploadPPTFileBean : bean.datas) {
            uris.add(Uri.parse(uploadPPTFileBean.getPicUrl()));
        }
        showPPT(uris);
        sendPPTChnageMessage();
    }


    @Subscribe
    public void event(PayBean event) {
        if (event.isPaySucc) {
            if(mPopReward!= null)   mPopReward.closePop();

            if (mPresenter != null && receiverUserinfo != null) {
                mPresenter.sendRedPacketMessage(mRequestBean);
                mRequestBean = null;
            }
        }
    }


    @Override
    protected void onDestroy() {
        EventBusUtil.unregister(this);
        mExtension.setExtensionListener(null);
        mPresenter.setCanLoadRemoteMessage(true);
        mPresenter.destroy();
        mAlertDialog = null;
        AudioRecordManager.getInstance().release();
        AudioPlayManager.getInstance().removeAllListener();
        quitChatRoom();
        if(followAnima != null && followAnima.isRunning()) followAnima.stop();
        RxTaskHelper.getInstance().cancelTask(this);
        super.onDestroy();
    }


    private void quitChatRoom(){
        RongIM.getInstance().quitChatRoom(charRoomgId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {}
        });
    }
    //判断RecyclerView 是否已滑倒底部
    public static boolean isVisBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE){
            return true;
        }else {
            return false;
        }
    }



}
