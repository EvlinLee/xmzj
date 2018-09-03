package com.gxtc.huchuan.im.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.huchuan.bean.event.EventSendRPBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.im.redPacket.RPOpenMessage;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.IExtensionClickListener;
import io.rong.imkit.IPublicServiceMenuClickListener;
import io.rong.imkit.InputMenu;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.manager.AudioPlayManager;
//import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.InternalModuleManager;
//import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.plugin.DefaultLocationPlugin;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.location.AMapRealTimeActivity;
import io.rong.imkit.plugin.location.IRealTimeLocationStateListener;
import io.rong.imkit.plugin.location.IUserInfoProvider;
import io.rong.imkit.plugin.location.LocationManager;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imkit.widget.AutoRefreshListView;
import io.rong.imkit.widget.CSEvaluateDialog;
import io.rong.imkit.widget.SingleChoiceDialog;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imkit.widget.provider.EvaluatePlugin;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.ICustomServiceListener;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.RealTimeLocationConstant;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.CSGroupItem;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.CustomServiceMode;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceMenu;
import io.rong.imlib.model.PublicServiceMenuItem;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CSPullLeaveMessage;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.PublicServiceCommandMessage;
import io.rong.message.ReadReceiptMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.push.RongPushClient;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/4/13.
 * 这个类主要是修改了融云的发送逻辑，把消息也发一份到服务器，但是现在不这么做了 弃用此类
 */
@Deprecated
public class ConVersationFragment extends UriFragment implements AbsListView.OnScrollListener, IExtensionClickListener, IUserInfoProvider, CSEvaluateDialog.EvaluateClickListener  {
    private static final String TAG = "ConVersationFragment";
    private PublicServiceProfile mPublicServiceProfile;
    private View mRealTimeBar;
    private TextView mRealTimeText;
    private RongExtension mRongExtension;
    private boolean mEnableMention;
    private float mLastTouchY;
    private boolean mUpDirection;
    private float mOffsetLimit;
    private CSCustomServiceInfo mCustomUserInfo;
    private ConversationInfo mCurrentConversationInfo;
    private String mDraft;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private static final int REQUEST_CODE_LOCATION_SHARE = 101;
    private static final int REQUEST_CS_LEAVEL_MESSAGE = 102;
    public static final int SCROLL_MODE_NORMAL = 1;
    public static final int SCROLL_MODE_TOP = 2;
    public static final int SCROLL_MODE_BOTTOM = 3;
    private static final int DEFAULT_HISTORY_MESSAGE_COUNT = 30;
    private static final int DEFAULT_REMOTE_MESSAGE_COUNT = 10;
    private static final int TIP_DEFAULT_MESSAGE_COUNT = 2;
    private String mTargetId;
    private Conversation.ConversationType mConversationType;
    private boolean mReadRec;
    private boolean mSyncReadStatus;
    private int mNewMessageCount;
    private AutoRefreshListView mList;
    private TextView mUnreadBtn;
    private ImageButton mNewMessageBtn;
    private TextView mNewMessageTextView;
    private MessageListAdapter mListAdapter;
    private View mMsgListView;
    private boolean mHasMoreLocalMessages;
    private int mLastMentionMsgId;
    private long mSyncReadStatusMsgTime;
    private boolean mCSNeedToQuit = false;
    private List<String> mLocationShareParticipants;
    private CustomServiceConfig mCustomServiceConfig;
    private CSEvaluateDialog mEvaluateDialg;
    private final int CS_HUMAN_MODE_CUSTOMER_EXPIRE = 0;
    private final int CS_HUMAN_MODE_SEAT_EXPIRE = 1;
    private boolean robotType = true;
    private long csEnterTime;
    private boolean csEvaluate = true;

    ICustomServiceListener customServiceListener = new ICustomServiceListener() {
        public void onSuccess(CustomServiceConfig config) {
            ConVersationFragment.this.mCustomServiceConfig = config;
            if(config.isBlack) {
                ConVersationFragment.this.onCustomServiceWarning(ConVersationFragment.this.getString(io.rong.imkit.R.string.rc_blacklist_prompt), false);
            }

            if(config.robotSessionNoEva) {
                ConVersationFragment.this.csEvaluate = false;
                ConVersationFragment.this.mListAdapter.setEvaluateForRobot(true);
            }

            if(ConVersationFragment.this.mRongExtension != null) {
                if(config.evaEntryPoint.equals(CustomServiceConfig.CSEvaEntryPoint.EVA_EXTENSION)) {
                    ConVersationFragment.this.mRongExtension.addPlugin(new EvaluatePlugin(ConVersationFragment.this.mCustomServiceConfig.isReportResolveStatus));
                }

                if(config.isDisableLocation) {
                    List i = ConVersationFragment.this.mRongExtension.getPluginModules();
                    IPluginModule uiMessage = null;

                    for(int i1 = 0; i1 < i.size(); ++i1) {
                        if(i.get(i1) instanceof DefaultLocationPlugin) {
                            uiMessage = (IPluginModule)i.get(i1);
                        }
                    }

                    if(uiMessage != null) {
                        ConVersationFragment.this.mRongExtension.removePlugin(uiMessage);
                    }
                }
            }

            if(config.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
                try {
                    ConVersationFragment.this.mCSNeedToQuit = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_stop_custom_service_when_quit);
                } catch (Resources.NotFoundException var5) {
                    var5.printStackTrace();
                }
            } else {
                ConVersationFragment.this.mCSNeedToQuit = config.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.SUSPEND);
            }

            for(int var6 = 0; var6 < ConVersationFragment.this.mListAdapter.getCount(); ++var6) {
                UIMessage var7 = ConVersationFragment.this.mListAdapter.getItem(var6);
                if(var7.getContent() instanceof CSPullLeaveMessage) {
                    var7.setCsConfig(config);
                }
            }

            ConVersationFragment.this.mListAdapter.notifyDataSetChanged();
        }

        public void onError(int code, String msg) {
            ConVersationFragment.this.onCustomServiceWarning(msg, false);
        }

        public void onModeChanged(CustomServiceMode mode) {
            if(ConVersationFragment.this.mRongExtension != null) {
                ConVersationFragment.this.mRongExtension.setExtensionBarMode(mode);
                if(!mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN) && !mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)) {
                    if(mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
                        ConVersationFragment.this.csEvaluate = false;
                    }
                } else {
                    if(ConVersationFragment.this.mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(ConVersationFragment.this.mCustomServiceConfig.userTipWord)) {
                        ConVersationFragment.this.startTimer(0, ConVersationFragment.this.mCustomServiceConfig.userTipTime * 60 * 1000);
                    }

                    if(ConVersationFragment.this.mCustomServiceConfig.adminTipTime > 0 && !TextUtils.isEmpty(ConVersationFragment.this.mCustomServiceConfig.adminTipWord)) {
                        ConVersationFragment.this.startTimer(1, ConVersationFragment.this.mCustomServiceConfig.adminTipTime * 60 * 1000);
                    }

                    ConVersationFragment.this.robotType = false;
                    ConVersationFragment.this.csEvaluate = true;
                }

            }
        }

        public void onQuit(String msg) {
            Log.i(TAG, "CustomService onQuit.");
            ConVersationFragment.this.stopTimer(0);
            ConVersationFragment.this.stopTimer(1);
            if(ConVersationFragment.this.mEvaluateDialg == null) {
                ConVersationFragment.this.onCustomServiceWarning(msg, ConVersationFragment.this.mCustomServiceConfig.quitSuspendType == CustomServiceConfig.CSQuitSuspendType.NONE);
            } else {
                ConVersationFragment.this.mEvaluateDialg.destroy();
            }

            if(!ConVersationFragment.this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
                RongContext.getInstance().getEventBus().post(new Event.CSTerminateEvent(ConVersationFragment.this.getActivity(), msg));
            }

        }

        public void onPullEvaluation(String dialogId) {
            if(ConVersationFragment.this.mEvaluateDialg == null) {
                ConVersationFragment.this.onCustomServiceEvaluation(true, dialogId, ConVersationFragment.this.robotType, ConVersationFragment.this.csEvaluate);

            }

        }

        public void onSelectGroup(List<CSGroupItem> groups) {
            ConVersationFragment.this.onSelectCustomerServiceGroup(groups);
        }
    };

    public ConVersationFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        InternalModuleManager.getInstance().onLoaded();

        try {
            this.mEnableMention = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_mentioned_message);
        } catch (Resources.NotFoundException var4) {
            Log.e(TAG, "rc_enable_mentioned_message not found in rc_config" +
                    ".xml");
        }

        try {
            this.mReadRec = this.getResources().getBoolean(io.rong.imkit.R.bool.rc_read_receipt);
            this.mSyncReadStatus = this.getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_sync_read_status);
        } catch (Resources.NotFoundException var3) {
            Log.e(TAG, "rc_read_receipt not found in rc_config.xml");
            var3.printStackTrace();
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(io.rong.imkit.R.layout.rc_fr_conversation, container, false);
        this.mRongExtension = (RongExtension)view.findViewById(io.rong.imkit.R.id.rc_extension);
        this.mRongExtension.setExtensionClickListener(this);
        this.mRongExtension.setFragment(this);
        this.mOffsetLimit = 70.0F * this.getActivity().getResources().getDisplayMetrics().density;
        this.mMsgListView = this.findViewById(view, io.rong.imkit.R.id.rc_layout_msg_list);
        this.mList = (AutoRefreshListView)this.findViewById(this.mMsgListView, io.rong.imkit.R.id
                .rc_list);
        this.mList.requestDisallowInterceptTouchEvent(true);
        this.mList.setMode(AutoRefreshListView.Mode.START);
        this.mList.setTranscriptMode(2);
        this.mListAdapter = this.onResolveAdapter(this.getActivity());
        this.mList.setAdapter(this.mListAdapter);
        this.mList.setOnRefreshListener(new AutoRefreshListView.OnRefreshListener() {
            public void onRefreshFromStart() {
                if(ConVersationFragment.this.mHasMoreLocalMessages) {
                    ConVersationFragment.this.getHistoryMessage(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId, 30, 1);
                } else {
                    ConVersationFragment.this.getRemoteHistoryMessages(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId, 10);
                }

            }

            public void onRefreshFromEnd() {
            }
        });
        this.mList.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == 2 && ConVersationFragment.this.mList.getCount() - ConVersationFragment.this.mList.getHeaderViewsCount() == 0) {
                    if(ConVersationFragment.this.mHasMoreLocalMessages) {
                        ConVersationFragment.this.getHistoryMessage(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId, 30, 1);
                    } else if(ConVersationFragment.this.mList.getRefreshState() != AutoRefreshListView.State.REFRESHING) {
                        ConVersationFragment.this.getRemoteHistoryMessages(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId, 10);
                    }

                    return true;
                } else {
                    if(event.getAction() == 1 && ConVersationFragment.this.mRongExtension.isExtensionExpanded()) {
                        ConVersationFragment.this.mRongExtension.collapseExtension();
                    }

                    return false;
                }
            }
        });
        if(RongContext.getInstance().getNewMessageState()) {
            this.mNewMessageTextView = (TextView)this.findViewById(view, io.rong.imkit.R.id.rc_new_message_number);
            this.mNewMessageBtn = (ImageButton)this.findViewById(view, io.rong.imkit.R.id.rc_new_message_count);
            this.mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ConVersationFragment.this.mList.smoothScrollToPosition(ConVersationFragment.this.mList.getCount() + 1);
                    ConVersationFragment.this.mNewMessageBtn.setVisibility(View.GONE);
                    ConVersationFragment.this.mNewMessageTextView.setVisibility(View.GONE);
                    ConVersationFragment.this.mNewMessageCount = 0;
                }
            });
        }

        if(RongContext.getInstance().getUnreadMessageState()) {
            this.mUnreadBtn = (TextView)this.findViewById(this.mMsgListView, io.rong.imkit.R.id.rc_unread_message_count);
        }

        this.mList.addOnScrollListener(this);
        this.mListAdapter.setOnItemHandlerListener(new MessageListAdapter.OnItemHandlerListener() {
            public boolean onWarningViewClick(final int position, final Message data, View v) {
                if(!ConVersationFragment.this.onResendItemClick(data)) {
                    RongIMClient.getInstance().deleteMessages(new int[]{data.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if(aBoolean.booleanValue()) {
                                ConVersationFragment.this.mListAdapter.remove(position);
                                data.setMessageId(0);
                                if(data.getContent() instanceof ImageMessage) {
                                    RongIM.getInstance().sendImageMessage(data, null, null, (RongIMClient.SendImageMessageCallback)null);
                                } else if(data.getContent() instanceof LocationMessage) {
                                    RongIM.getInstance().sendLocationMessage(data, null, null, (IRongCallback.ISendMessageCallback)null);
                                } else if(data.getContent() instanceof FileMessage) {
                                    RongIM.getInstance().sendMediaMessage(data, null, null, (IRongCallback.ISendMediaMessageCallback)null);
                                } else {
                                    RongIM.getInstance().sendMessage(data, null, null, (IRongCallback.ISendMessageCallback)null);
                                }
                            }

                        }




                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }

                return true;
            }

            public void onReadReceiptStateClick(Message message) {
                ConVersationFragment.this.onReadReceiptStateClick(message);
            }
        });
        return view;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == 1) {
            if(this.mRongExtension != null) {
                this.mRongExtension.collapseExtension();
            }
        } else if(scrollState == 0) {
            int last = this.mList.getLastVisiblePosition();
            if(this.mList.getCount() - last > 2) {
                this.mList.setTranscriptMode(1);
            } else {
                this.mList.setTranscriptMode(2);
            }

            if(this.mNewMessageBtn != null && last == this.mList.getCount() - 1) {
                this.mNewMessageCount = 0;
                this.mNewMessageBtn.setVisibility(View.GONE);
                this.mNewMessageTextView.setVisibility(View.GONE);
            }
        }

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onResume() {
        RongPushClient.clearAllPushNotifications(this.getActivity());
        super.onResume();
    }

    public final void getUserInfo(String userId, IUserInfoProvider.UserInfoCallback callback) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
        if(userInfo != null) {
            callback.onGotUserInfo(userInfo);
        }

    }

    public MessageListAdapter onResolveAdapter(Context context) {
        return new MessageListAdapter(context);
    }

    protected void initFragment(Uri uri) {
        Log.d(TAG, "initFragment : " + uri + ",this=" + this);
        if(uri != null) {
            String typeStr = uri.getLastPathSegment().toUpperCase();
            this.mConversationType = Conversation.ConversationType.valueOf(typeStr);
            this.mTargetId = uri.getQueryParameter("targetId");
            this.mRongExtension.setConversation(this.mConversationType, this.mTargetId);
            RongIMClient.getInstance().getTextMessageDraft(this.mConversationType, this.mTargetId, new RongIMClient.ResultCallback<String>() {
                public void onSuccess(String s) {
                    ConVersationFragment.this.mDraft = s;
                    if(ConVersationFragment.this.mRongExtension != null) {
                        EditText editText = ConVersationFragment.this.mRongExtension.getInputEditText();
                        editText.setText(s);
                        editText.setSelection(editText.length());
                    }

                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            });
            this.mCurrentConversationInfo = ConversationInfo.obtain(this.mConversationType, this.mTargetId);
            RongContext.getInstance().registerConversationInfo(this.mCurrentConversationInfo);
            this.mRealTimeBar = this.mMsgListView.findViewById(io.rong.imkit.R.id.real_time_location_bar);
            this.mRealTimeText = (TextView)this.mMsgListView.findViewById(io.rong.imkit.R.id.real_time_location_text);
            if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.getActivity() != null && this.getActivity().getIntent() != null && this.getActivity().getIntent().getData() != null) {
                this.mCustomUserInfo = (CSCustomServiceInfo)this.getActivity().getIntent().getParcelableExtra("customServiceInfo");
            }

            LocationManager.getInstance().bindConversation(this.getActivity(), this.mConversationType, this.mTargetId);
            LocationManager.getInstance().setUserInfoProvider(this);
            LocationManager.getInstance().setParticipantChangedListener(new IRealTimeLocationStateListener() {
                public void onParticipantChanged(List<String> userIdList) {
                    if(!ConVersationFragment.this.isDetached()) {
                        ConVersationFragment.this.mLocationShareParticipants = userIdList;
                        if(userIdList != null) {
                            if(userIdList.size() == 0) {
                                ConVersationFragment.this.mRealTimeBar.setVisibility(View.GONE);
                            } else if(userIdList.size() == 1 && userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                ConVersationFragment.this.mRealTimeText.setText(ConVersationFragment.this.getResources().getString(io.rong.imkit.R.string.rc_you_are_sharing_location));
                                ConVersationFragment.this.mRealTimeBar.setVisibility(View.VISIBLE);
                            } else if(userIdList.size() == 1 && !userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                ConVersationFragment.this.mRealTimeText.setText(String.format(ConVersationFragment.this.getResources().getString(io.rong.imkit.R.string.rc_other_is_sharing_location), new Object[]{ConVersationFragment.this.getNameFromCache((String)userIdList.get(0))}));
                                ConVersationFragment.this.mRealTimeBar.setVisibility(View.VISIBLE);
                            } else {
                                ConVersationFragment.this.mRealTimeText.setText(String.format(ConVersationFragment.this.getResources().getString(io.rong.imkit.R.string.rc_others_are_sharing_location), new Object[]{Integer.valueOf(userIdList.size())}));
                                ConVersationFragment.this.mRealTimeBar.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ConVersationFragment.this.mRealTimeBar.setVisibility(View.GONE);
                        }

                    }
                }

                public void onErrorException() {
                    if(!ConVersationFragment.this.isDetached()) {
                        ConVersationFragment.this.mRealTimeBar.setVisibility(View.GONE);
                        if(ConVersationFragment.this.mLocationShareParticipants != null) {
                            ConVersationFragment.this.mLocationShareParticipants.clear();
                            ConVersationFragment.this.mLocationShareParticipants = null;
                        }
                    }

                }
            });
            this.mRealTimeBar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId);
                    if(status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {
                        PromptPopupDialog intent = PromptPopupDialog.newInstance(ConVersationFragment.this.getActivity(), "", ConVersationFragment.this.getResources().getString(io.rong.imkit.R.string.rc_real_time_join_notification));
                        intent.setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                            public void onPositiveButtonClicked() {
                                LocationManager.getInstance().joinLocationSharing();
                                Intent intent = new Intent(ConVersationFragment.this.getActivity(), AMapRealTimeActivity.class);
                                if(ConVersationFragment.this.mLocationShareParticipants != null) {
                                    intent.putStringArrayListExtra("participants", (ArrayList)ConVersationFragment.this.mLocationShareParticipants);
                                }

                                ConVersationFragment.this.startActivity(intent);
                            }
                        });
                        intent.show();
                    } else {
                        Intent intent1 = new Intent(ConVersationFragment.this.getActivity(), AMapRealTimeActivity.class);
                        if(ConVersationFragment.this.mLocationShareParticipants != null) {
                            intent1.putStringArrayListExtra("participants", (ArrayList)ConVersationFragment.this.mLocationShareParticipants);
                        }

                        ConVersationFragment.this.startActivity(intent1);
                    }

                }
            });
            if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                boolean msg = this.getActivity() != null && this.getActivity().getIntent().getBooleanExtra("createIfNotExist", true);
                int message = this.getResources().getInteger(io.rong.imkit.R.integer.rc_chatroom_first_pull_message_count);
                if(msg) {
                    RongIMClient.getInstance().joinChatRoom(this.mTargetId, message, new RongIMClient.OperationCallback() {
                        public void onSuccess() {
                            Log.i(TAG, "joinChatRoom onSuccess : " + ConVersationFragment.this.mTargetId);
                        }

                        public void onError(RongIMClient.ErrorCode errorCode) {
                            Log.e(TAG, "joinChatRoom onError : " + errorCode);
                            if(ConVersationFragment.this.getActivity() != null) {
                                if(errorCode != RongIMClient.ErrorCode.RC_NET_UNAVAILABLE && errorCode != RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
                                    ConVersationFragment.this.onWarningDialog(ConVersationFragment.this.getString(io.rong.imkit.R.string.rc_join_chatroom_failure));
                                } else {
                                    ConVersationFragment.this.onWarningDialog(ConVersationFragment.this.getString(io.rong.imkit.R.string.rc_notice_network_unavailable));
                                }
                            }

                        }
                    });
                } else {
                    RongIMClient.getInstance().joinExistChatRoom(this.mTargetId, message, new RongIMClient.OperationCallback() {
                        public void onSuccess() {
                            Log.i(TAG, "joinExistChatRoom onSuccess : " + ConVersationFragment.this.mTargetId);
                        }

                        public void onError(RongIMClient.ErrorCode errorCode) {
                            Log.e(TAG, "joinExistChatRoom onError : " + errorCode);
                            if(ConVersationFragment.this.getActivity() != null) {
                                if(errorCode != RongIMClient.ErrorCode.RC_NET_UNAVAILABLE && errorCode != RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
                                    ConVersationFragment.this.onWarningDialog(ConVersationFragment.this.getString(io.rong.imkit.R.string.rc_join_chatroom_failure));
                                } else {
                                    ConVersationFragment.this.onWarningDialog(ConVersationFragment.this.getString(io.rong.imkit.R.string.rc_notice_network_unavailable));
                                }
                            }

                        }
                    });
                }
            } else if(this.mConversationType != Conversation.ConversationType.APP_PUBLIC_SERVICE
                    && this.mConversationType != Conversation.ConversationType.PUBLIC_SERVICE) {
                if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                    this.onStartCustomService(this.mTargetId);
                } else if(this.mEnableMention && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
                    RongMentionManager.getInstance().createInstance(this.mConversationType, this.mTargetId, this.mRongExtension.getInputEditText());
                }
            } else {
                PublicServiceCommandMessage msg1 = new PublicServiceCommandMessage();
                msg1.setCommand(PublicServiceMenu.PublicServiceMenuItemType.Entry.getMessage());
                Message message1 = Message.obtain(this.mTargetId, this.mConversationType, msg1);
                RongIMClient.getInstance().sendMessage(message1, null, null, new IRongCallback.ISendMessageCallback() {
                    public void onAttached(Message message) {
                    }

                    public void onSuccess(Message message) {
                    }

                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    }
                });
                Conversation.PublicServiceType publicServiceType;
                if(this.mConversationType == Conversation.ConversationType.PUBLIC_SERVICE) {
                    publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
                } else {
                    publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
                }

                RongIM.getInstance().getPublicServiceProfile(publicServiceType, this.mTargetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                    @Override
                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
                        ArrayList inputMenuList = new ArrayList();
                        PublicServiceMenu menu = publicServiceProfile.getMenu();
                        ArrayList items = menu != null?menu.getMenuItems():null;
                        if(items != null && ConVersationFragment.this.mRongExtension != null) {
                            ConVersationFragment.this.mPublicServiceProfile = publicServiceProfile;
                            Iterator i$ = items.iterator();

                            while(i$.hasNext()) {
                                PublicServiceMenuItem item = (PublicServiceMenuItem)i$.next();
                                InputMenu inputMenu = new InputMenu();
                                inputMenu.title = item.getName();
                                inputMenu.subMenuList = new ArrayList();
                                Iterator i$1 = item.getSubMenuItems().iterator();

                                while(i$1.hasNext()) {
                                    PublicServiceMenuItem i = (PublicServiceMenuItem)i$1.next();
                                    inputMenu.subMenuList.add(i.getName());
                                }

                                inputMenuList.add(inputMenu);
                            }

                            ConVersationFragment.this.mRongExtension.setInputMenu(inputMenuList, true);
                        }

                    }

                    public void onError(RongIMClient.ErrorCode e) {
                    }
                });
            }
        }

        RongIMClient.getInstance().getConversation(this.mConversationType, this.mTargetId, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if(conversation != null && ConVersationFragment.this.getActivity() != null) {
                    final int unreadCount = conversation.getUnreadMessageCount();
                    if(unreadCount > 0) {
                        ConVersationFragment.this.sendReadReceiptAndSyncUnreadStatus(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId, conversation.getSentTime());
                    }

                    if(conversation.getMentionedCount() > 0) {
                        ConVersationFragment.this.getLastMentionedMessageId(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId);
                    }

                    if(unreadCount > 10 && ConVersationFragment.this.mUnreadBtn != null) {
                        if(unreadCount > 150) {
                            ConVersationFragment.this.mUnreadBtn.setText(String.format("%s%s", new Object[]{"150+", ConVersationFragment.this.getActivity().getResources().getString(io.rong.imkit.R.string.rc_new_messages)}));
                        } else {
                            ConVersationFragment.this.mUnreadBtn.setText(String.format("%s%s", new Object[]{Integer.valueOf(unreadCount), ConVersationFragment.this.getActivity().getResources().getString(io.rong.imkit.R.string.rc_new_messages)}));
                        }

                        ConVersationFragment.this.mUnreadBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                ConVersationFragment.this.mUnreadBtn.setClickable(false);
                                TranslateAnimation animation = new TranslateAnimation(0.0F, 500.0F, 0.0F, 0.0F);
                                animation.setDuration(500L);
                                ConVersationFragment.this.mUnreadBtn.startAnimation(animation);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    public void onAnimationEnd(Animation animation) {
                                        ConVersationFragment.this.mUnreadBtn.setVisibility(View.GONE);
                                        if(unreadCount <= 30) {
                                            if(ConVersationFragment.this.mList.getCount() < 30) {
                                                ConVersationFragment.this.mList.smoothScrollToPosition(ConVersationFragment.this.mList.getCount() - unreadCount);
                                            } else {
                                                ConVersationFragment.this.mList.smoothScrollToPosition(30 - unreadCount);
                                            }
                                        } else if(unreadCount > 30) {
                                            ConVersationFragment.this.getHistoryMessage(ConVersationFragment.this.mConversationType, ConVersationFragment.this.mTargetId, unreadCount - 30 - 1, 2);
                                        }

                                    }

                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            }
                        });
                        TranslateAnimation translateAnimation = new TranslateAnimation(300.0F, 0.0F, 0.0F, 0.0F);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
                        translateAnimation.setDuration(1000L);
                        alphaAnimation.setDuration(2000L);
                        AnimationSet set = new AnimationSet(true);
                        set.addAnimation(translateAnimation);
                        set.addAnimation(alphaAnimation);
                        ConVersationFragment.this.mUnreadBtn.setVisibility(View.VISIBLE);
                        ConVersationFragment.this.mUnreadBtn.startAnimation(set);
                        set.setAnimationListener(new Animation.AnimationListener() {
                            public void onAnimationStart(Animation animation) {
                            }

                            public void onAnimationEnd(Animation animation) {
                                ConVersationFragment.this.getHandler().postDelayed(new Runnable() {
                                    public void run() {
                                        TranslateAnimation animation = new TranslateAnimation(0.0F, 700.0F, 0.0F, 0.0F);
                                        animation.setDuration(700L);
                                        animation.setFillAfter(true);
                                        ConVersationFragment.this.mUnreadBtn.startAnimation(animation);
                                    }
                                }, 4000L);
                            }

                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }
                }

            }

            public void onError(RongIMClient.ErrorCode e) {
            }
        });
        this.getHistoryMessage(this.mConversationType, this.mTargetId, 30, 3);
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public boolean onResendItemClick(Message message) {
        return false;
    }

    public void onReadReceiptStateClick(Message message) {
    }

    public void onSelectCustomerServiceGroup(final List<CSGroupItem> groupList) {
        if(this.getActivity() != null) {
            ArrayList singleDataList = new ArrayList();
            singleDataList.clear();

            for(int i = 0; i < groupList.size(); ++i) {
                if(((CSGroupItem)groupList.get(i)).getOnline()) {
                    singleDataList.add(groupList.get(i).getName());
                }
            }

            if(singleDataList.size() == 0) {
                RongIMClient.getInstance().selectCustomServiceGroup(this.mTargetId, null);
            } else {
                final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(this.getActivity(), singleDataList);
                singleChoiceDialog.setTitle(this.getActivity().getResources().getString(io.rong.imkit.R.string.rc_cs_select_group));
                singleChoiceDialog.setOnOKButtonListener(new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int selItem = singleChoiceDialog.getSelectItem();
                        RongIMClient.getInstance().selectCustomServiceGroup(ConVersationFragment.this.mTargetId, groupList.get(selItem).getId());
                    }
                });
                singleChoiceDialog.setOnCancelButtonListener(new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RongIMClient.getInstance().selectCustomServiceGroup(ConVersationFragment.this.mTargetId, null);
                    }
                });
                singleChoiceDialog.show();
            }
        }
    }

    public void onPause() {
        if(this.getActivity().isFinishing()) {
            RongIM.getInstance().clearMessagesUnreadStatus(this.mConversationType, this.mTargetId, null);
            this.stopTimer(1);
            this.stopTimer(0);
            if(this.mEnableMention && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
                RongMentionManager.getInstance().destroyInstance(this.mConversationType, this.mTargetId);
            }

            if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                SendImageManager.getInstance().cancelSendingImages(this.mConversationType, this.mTargetId);
                RongIM.getInstance().quitChatRoom(this.mTargetId, null);
            }

            if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.mCSNeedToQuit) {
                this.onStopCustomService(this.mTargetId);
            }

            if(this.mSyncReadStatus && this.mSyncReadStatusMsgTime > 0L && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
                RongIMClient.getInstance().syncConversationReadStatus(this.mConversationType, this.mTargetId, this.mSyncReadStatusMsgTime, null);
            }

            EventBus.getDefault().unregister(this);
            AudioPlayManager.getInstance().stopPlay();
            AudioRecordManager.getInstance().destroyRecord();
            RongContext.getInstance().unregisterConversationInfo(this.mCurrentConversationInfo);
            LocationManager.getInstance().quitLocationSharing();
            LocationManager.getInstance().setParticipantChangedListener(null);
            LocationManager.getInstance().setUserInfoProvider(null);
            LocationManager.getInstance().unBindConversation();
            this.destroyExtension();
        }

        super.onPause();
    }

    private void destroyExtension() {
        String text = this.mRongExtension.getInputEditText().getText().toString();
        if(TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) && !text.equals(this.mDraft)) {
            RongIMClient.getInstance().saveTextMessageDraft(this.mConversationType, this.mTargetId, text, null);
            Event.DraftEvent draft = new Event.DraftEvent(this.mConversationType, this.mTargetId, text);
            RongContext.getInstance().getEventBus().post(draft);
        }

        this.mRongExtension.onDestroy();
        this.mRongExtension = null;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isLocationSharing() {
        return LocationManager.getInstance().isSharing();
    }

    public void showQuitLocationSharingDialog(final Activity activity) {
        PromptPopupDialog.newInstance(activity, this.getString(io.rong.imkit.R.string.rc_ext_warning), this.getString(io.rong.imkit.R.string.rc_real_time_exit_notification), this.getString(io.rong.imkit.R.string.rc_action_bar_ok)).setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
            public void onPositiveButtonClicked() {
                activity.finish();
            }
        }).show();
    }

    public boolean onBackPressed() {
        if(this.mRongExtension != null && this.mRongExtension.isExtensionExpanded()) {
            this.mRongExtension.collapseExtension();
            return true;
        } else {
            return this.mConversationType != null && this.mCustomServiceConfig != null && this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)?this.onCustomServiceEvaluation(false, "", this.robotType, this.csEvaluate):false;
        }
    }

    public boolean handleMessage(android.os.Message msg) {
        InformationNotificationMessage info;
        if (this.mCustomServiceConfig == null) {
            return false;
        }
        switch(msg.what) {
            case 0:
                if(this.getActivity() == null) {
                    return true;
                }

                info = new InformationNotificationMessage(this.mCustomServiceConfig.userTipWord);
                RongIM.getInstance().insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, this.mTargetId, this.mTargetId, info, System.currentTimeMillis(), null);
                return true;
            case 1:
                if(this.getActivity() == null) {
                    return true;
                }

                if (this.customServiceListener!=null) {
                    info = new InformationNotificationMessage(this.mCustomServiceConfig.adminTipWord);
                    RongIM.getInstance().insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, this.mTargetId, this.mTargetId, info, System.currentTimeMillis(), null);
                }
//                    info = new InformationNotificationMessage(this.mCustomServiceConfig.adminTipWord);
//                    RongIM.getInstance().insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, this.mTargetId, this.mTargetId, info, System.currentTimeMillis(), null);

                return true;
            default:
                return false;
        }
    }

    public void onWarningDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_warning);
        TextView tv = (TextView)window.findViewById(io.rong.imkit.R.id.rc_cs_msg);
        tv.setText(msg);
        window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
                FragmentManager fm = ConVersationFragment.this.getChildFragmentManager();
                if(fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    ConVersationFragment.this.getActivity().finish();
                }

            }
        });
    }

    public void onCustomServiceWarning(String msg, final boolean evaluate) {
        if(this.getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_warning);
            TextView tv = (TextView)window.findViewById(io.rong.imkit.R.id.rc_cs_msg);
            tv.setText(msg);
            window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    alertDialog.dismiss();
                    if(evaluate) {
                        ConVersationFragment.this.onCustomServiceEvaluation(false, "", ConVersationFragment.this.robotType, evaluate);
                    } else {
                        FragmentManager fm = ConVersationFragment.this.getChildFragmentManager();
                        if(fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                        } else {
                            ConVersationFragment.this.getActivity().finish();
                        }
                    }

                }
            });
        }
    }

    public boolean onCustomServiceEvaluation(boolean isPullEva, String dialogId, boolean robotType, boolean evaluate) {
        if(evaluate && this.getActivity() != null) {
            long currentTime = System.currentTimeMillis();
            int interval = 60;

            try {
                interval = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_custom_service_evaluation_interval);
            } catch (Resources.NotFoundException var10) {
                var10.printStackTrace();
            }

            if(currentTime - this.csEnterTime < (long)(interval * 1000) && !isPullEva) {
                InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null && imm.isActive() && this.getActivity().getCurrentFocus() != null && this.getActivity().getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 2);
                }

                FragmentManager fm = this.getChildFragmentManager();
                if(fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    this.getActivity().finish();
                }

                return false;
            }

            this.mEvaluateDialg = new CSEvaluateDialog(this.getContext(), this.mTargetId);
            this.mEvaluateDialg.setClickListener(this);
            if(this.mCustomServiceConfig.evaluateType.equals(CustomServiceConfig.CSEvaType.EVA_UNIFIED)) {
                this.mEvaluateDialg.showStarMessage(this.mCustomServiceConfig.isReportResolveStatus);
            } else if(robotType) {
                this.mEvaluateDialg.showRobot(true);
            } else {
                this.mEvaluateDialg.showStar(dialogId);
            }
        }

        return true;
    }

    /**
     * 发送按钮被点击
     * @param v
     * @param text
     */
    public void onSendToggleClick(View v, String text) {
        if(!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
            TextMessage textMessage = TextMessage.obtain(text);
            MentionedInfo mentionedInfo = RongMentionManager.getInstance().onSendButtonClick();
            if(mentionedInfo != null) {
                textMessage.setMentionedInfo(mentionedInfo);
            }

            Message message = Message.obtain(this.mTargetId, this.mConversationType, textMessage);
            RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {


                @Override
                public void onAttached(Message message) {

                }

                @Override
                public void onSuccess(Message message) {
                    addMessage(message);
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                }
            });
        } else {
            Log.e(TAG, "text content must not be null");
        }
    }

    private void addMessage(Message message) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("objectName", message.getObjectName());
        map.put("content", new String(message.getContent().encode()));
        map.put("targetType", message.getConversationType().getValue() + "");
        Log.d(TAG, "addMessage: " + new String(message.getContent().encode()));
        map.put("targetId", message.getTargetId());


        map.put("msgId", /*new Extra(message.getExtra()).getMsgId()*/mTargetId+System.currentTimeMillis());
        Log.d(TAG, map.toString());
        addMessage(map);
    }


    private void addMessage(HashMap<String, String> map) {
        LiveApi.getInstance().addMessage(map).subscribeOn(Schedulers.io()).subscribe(new
                ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.d(TAG, "onSuccess: 文本保存成功");
            }

            @Override
            public void onError(String errorCode, String message) {
                Log.d("LiveConversationPresent", message);
                Log.d(TAG, "onError: 文本保存失败");
            }
        }));
    }

    /**
     * 发送图片
     * @param selectedImages
     * @param origin
     */
    public void onImageResult(List<Uri> selectedImages, boolean origin) {
        Log.d(TAG, "selectedImages:" + selectedImages);
        SendImageManager.getInstance().sendImages(this.mConversationType, this.mTargetId, selectedImages, origin);
        if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:ImgMsg");
        }

    }

    public void onEditTextClick(EditText editText) {
    }

    /**
     * 地址
     * @param lat
     * @param lng
     * @param poi
     * @param thumb
     */
    public void onLocationResult(double lat, double lng, String poi, Uri thumb) {
        LocationMessage locationMessage = LocationMessage.obtain(lat, lng, poi, thumb);
        Message message = Message.obtain(this.mTargetId, this.mConversationType, locationMessage);
        RongIM.getInstance().sendLocationMessage(message, null, null, null);
        if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:LBSMsg");
        }

    }

    public void onSwitchToggleClick(View v, ViewGroup inputBoard) {
        if(this.robotType) {
            RongIMClient.getInstance().switchToHumanMode(this.mTargetId);
        }

    }

    /**
     * 录音
     * @param v
     * @param event
     */
    public void onVoiceInputToggleTouch(View v, MotionEvent event) {
        String[] permissions = new String[]{"android.permission.RECORD_AUDIO"};
        if(!PermissionCheckUtil.checkPermissions(this.getActivity(), permissions)) {
            if(event.getAction() == 0) {
                PermissionCheckUtil.requestPermissions(this, permissions, 100);
            }

        } else {
            if(event.getAction() == 0) {
                AudioPlayManager.getInstance().stopPlay();
                AudioRecordManager.getInstance().startRecord(v.getRootView(), this.mConversationType, this.mTargetId);
                this.mLastTouchY = event.getY();
                this.mUpDirection = false;
                ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input_hover);
            } else if(event.getAction() == 2) {
                if(this.mLastTouchY - event.getY() > this.mOffsetLimit && !this.mUpDirection) {
                    AudioRecordManager.getInstance().willCancelRecord();
                    this.mUpDirection = true;
                    ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input);
                } else if(event.getY() - this.mLastTouchY > -this.mOffsetLimit && this.mUpDirection) {
                    AudioRecordManager.getInstance().continueRecord();
                    this.mUpDirection = false;
                    ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input_hover);
                }
            } else if(event.getAction() == 1 || event.getAction() == 3) {
                AudioRecordManager.getInstance().stopRecord();
                ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input);
            }

            if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:VcMsg");
            }

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 100 && grantResults[0] != 0) {
            Toast.makeText(this.getActivity(), this.getResources().getString(io.rong.imkit.R.string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
    }

    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int cursor;
        int offset;
        if(count == 0) {
            cursor = start + before;
            offset = -before;
        } else {
            cursor = start;
            offset = count;
        }

        if(!this.mConversationType.equals(Conversation.ConversationType.GROUP) && !this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE) && offset != 0) {
                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:TxtMsg");
            }
        } else {
            RongMentionManager.getInstance().onTextEdit(this.mConversationType, this.mTargetId, cursor, offset, s.toString());
        }

    }

    public void afterTextChanged(Editable s) {
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getKeyCode() == 67 && event.getAction() == 0) {
            EditText editText = (EditText)v;
            int cursorPos = editText.getSelectionStart();
            RongMentionManager.getInstance().onDeleteClick(this.mConversationType, this.mTargetId, editText, cursorPos);
        }

        return false;
    }

    public void onMenuClick(int root, int sub) {
        if(this.mPublicServiceProfile != null) {
            PublicServiceMenuItem item = this.mPublicServiceProfile.getMenu().getMenuItems().get(root);
            if(sub >= 0) {
                item = item.getSubMenuItems().get(sub);
            }

            if(item.getType().equals(PublicServiceMenu.PublicServiceMenuItemType.View)) {
                IPublicServiceMenuClickListener msg = RongContext.getInstance().getPublicServiceMenuClickListener();
                if(msg == null || !msg.onClick(this.mConversationType, this.mTargetId, item)) {
                    String action = "io.rong.imkit.intent.action.webview";
                    Intent intent = new Intent(action);
                    intent.setPackage(this.getActivity().getPackageName());
                    intent.addFlags(268435456);
                    intent.putExtra("url", item.getUrl());
                    this.getActivity().startActivity(intent);
                }
            }

            PublicServiceCommandMessage msg1 = PublicServiceCommandMessage.obtain(item);
            RongIMClient.getInstance().sendMessage(this.mConversationType, this.mTargetId, msg1, null, null, new IRongCallback.ISendMessageCallback() {
                public void onAttached(Message message) {
                }

                public void onSuccess(Message message) {
                }

                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                }
            });
        }

    }

    public void onPluginClicked(IPluginModule pluginModule, int position) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102) {
            this.getActivity().finish();
        } else {
            this.mRongExtension.onActivityPluginResult(requestCode, resultCode, data);
        }

    }

    private String getNameFromCache(String targetId) {
        UserInfo info = RongContext.getInstance().getUserInfoFromCache(targetId);
        return info == null?targetId:info.getName();
    }

    public final void onEventMainThread(Event.ReadReceiptRequestEvent event) {
        Log.d(TAG, "ReadReceiptRequestEvent");
        if((this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
                if(this.mListAdapter.getItem(i).getUId().equals(event.getMessageUId())) {
                    final UIMessage uiMessage = this.mListAdapter.getItem(i);
                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                    if(readReceiptInfo == null) {
                        readReceiptInfo = new ReadReceiptInfo();
                        uiMessage.setReadReceiptInfo(readReceiptInfo);
                    }

                    if(readReceiptInfo.isReadReceiptMessage() && readReceiptInfo.hasRespond()) {
                        return;
                    }

                    readReceiptInfo.setIsReadReceiptMessage(true);
                    readReceiptInfo.setHasRespond(false);
                    ArrayList messageList = new ArrayList();
                    messageList.add(this.mListAdapter.getItem(i).getMessage());
                    RongIMClient.getInstance().sendReadReceiptResponse(event.getConversationType(), event.getTargetId(), messageList, new RongIMClient.OperationCallback() {
                        public void onSuccess() {
                            uiMessage.getReadReceiptInfo().setHasRespond(true);
                        }

                        public void onError(RongIMClient.ErrorCode errorCode) {
                            Log.e(TAG, "sendReadReceiptResponse failed, errorCode = " + errorCode);
                        }
                    });
                    break;
                }
            }
        }

    }

    public final void onEventMainThread(Event.ReadReceiptResponseEvent event) {
        Log.d(TAG, "ReadReceiptResponseEvent");
        if((this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
                if(this.mListAdapter.getItem(i).getUId().equals(event.getMessageUId())) {
                    UIMessage uiMessage = this.mListAdapter.getItem(i);
                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                    if(readReceiptInfo == null) {
                        readReceiptInfo = new ReadReceiptInfo();
                        readReceiptInfo.setIsReadReceiptMessage(true);
                        uiMessage.setReadReceiptInfo(readReceiptInfo);
                    }

                    readReceiptInfo.setRespondUserIdList(event.getResponseUserIdList());
                    int first = this.mList.getFirstVisiblePosition();
                    int last = this.mList.getLastVisiblePosition();
                    int position = this.getPositionInListView(i);
                    if(position >= first && position <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                    break;
                }
            }
        }

    }

    public final void onEventMainThread(Event.MessageDeleteEvent deleteEvent) {
        Log.d(TAG, "MessageDeleteEvent");
        if(deleteEvent.getMessageIds() != null) {
            Iterator i$ = deleteEvent.getMessageIds().iterator();

            while(i$.hasNext()) {
                long messageId = (long)((Integer)i$.next()).intValue();
                int position = this.mListAdapter.findPosition(messageId);
                if(position >= 0) {
                    this.mListAdapter.remove(position);
                }
            }

            this.mListAdapter.notifyDataSetChanged();
        }

    }

    public final void onEventMainThread(Event.PublicServiceFollowableEvent event) {
        Log.d(TAG, "PublicServiceFollowableEvent");
        if(event != null && !event.isFollow()) {
            this.getActivity().finish();
        }

    }

    public final void onEventMainThread(Event.MessagesClearEvent clearEvent) {
        Log.d(TAG, "MessagesClearEvent");
        if(clearEvent.getTargetId().equals(this.mTargetId) && clearEvent.getType().equals(this.mConversationType)) {
            this.mListAdapter.clear();
            this.mListAdapter.notifyDataSetChanged();
        }

    }

    public final void onEventMainThread(Event.MessageRecallEvent event) {
        Log.d(TAG, "MessageRecallEvent");
        if(event.isRecallSuccess()) {
            RecallNotificationMessage recallNotificationMessage = event.getRecallNotificationMessage();
            int position = this.mListAdapter.findPosition((long)event.getMessageId());
            if(position != -1) {
                this.mListAdapter.getItem(position).setContent(recallNotificationMessage);
                int first = this.mList.getFirstVisiblePosition();
                int last = this.mList.getLastVisiblePosition();
                int listPos = this.getPositionInListView(position);
                if(listPos >= first && listPos <= last) {
                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                }
            }
        } else {
            Toast.makeText(this.getActivity(), io.rong.imkit.R.string.rc_recall_failed, Toast.LENGTH_SHORT).show();
        }

    }

    public final void onEventMainThread(Event.RemoteMessageRecallEvent event) {
        Log.d(TAG, "RemoteMessageRecallEvent");
        int position = this.mListAdapter.findPosition((long)event.getMessageId());
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        if(position >= 0) {
            UIMessage uiMessage = this.mListAdapter.getItem(position);
            if(uiMessage.getMessage().getContent() instanceof VoiceMessage) {
                AudioPlayManager.getInstance().stopPlay();
            }

            if(uiMessage.getMessage().getContent() instanceof FileMessage) {
                RongIM.getInstance().cancelDownloadMediaMessage(uiMessage.getMessage(), null);
            }

            uiMessage.setContent(event.getRecallNotificationMessage());
            int listPos = this.getPositionInListView(position);
            if(listPos >= first && listPos <= last) {
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            }
        }

    }

    public final void onEventMainThread(Message msg) {
        Log.d(TAG, "Event message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());
        if(this.mTargetId.equals(msg.getTargetId()) && this.mConversationType.equals(msg.getConversationType()) && msg.getMessageId() > 0) {
            int position = this.mListAdapter.findPosition((long)msg.getMessageId());
            if(position >= 0) {
                this.mListAdapter.getItem(position).setMessage(msg);
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            } else {
                UIMessage uiMessage = UIMessage.obtain(msg);
                if(msg.getContent() instanceof CSPullLeaveMessage) {
                    uiMessage.setCsConfig(this.mCustomServiceConfig);
                }

                this.mListAdapter.add(uiMessage);
                this.mListAdapter.notifyDataSetChanged();
            }

            if(msg.getSenderUserId() != null && msg.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId()) && this.mList.getLastVisiblePosition() - 1 != this.mList.getCount() && !(msg.getContent() instanceof RPOpenMessage)) {
                this.mList.smoothScrollToPosition(this.mList.getCount());
            }

            if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && msg.getMessageDirection() == Message.MessageDirection.SEND && !this.robotType && this.mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.userTipWord)) {
                this.startTimer(0, this.mCustomServiceConfig.userTipTime * 60 * 1000);
            }
        }

    }

    public final void onEventMainThread(Event.FileMessageEvent event) {
        Message msg = event.getMessage();
        Log.d(TAG, "FileMessageEvent message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());
        if(this.mTargetId.equals(msg.getTargetId()) && this.mConversationType.equals(msg.getConversationType()) && msg.getMessageId() > 0) {
            int position = this.mListAdapter.findPosition((long)msg.getMessageId());
            UIMessage uiMessage;
            if(position >= 0) {
                uiMessage = this.mListAdapter.getItem(position);
                uiMessage.setMessage(msg);
                uiMessage.setProgress(event.getProgress());
                this.mListAdapter.getItem(position).setMessage(msg);
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            } else {
                uiMessage = UIMessage.obtain(msg);
                uiMessage.setProgress(event.getProgress());
                this.mListAdapter.add(uiMessage);
                this.mListAdapter.notifyDataSetChanged();
            }

            if(msg.getSenderUserId() != null && msg.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId()) && this.mList.getLastVisiblePosition() - 1 != this.mList.getCount()) {
                this.mList.smoothScrollToPosition(this.mList.getCount());
            }

            if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && msg.getMessageDirection() == Message.MessageDirection.SEND && !this.robotType && this.mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.userTipWord)) {
                this.startTimer(0, this.mCustomServiceConfig.userTipTime * 60 * 1000);
            }
        }

    }

    public final void onEventMainThread(GroupUserInfo groupUserInfo) {
        Log.d(TAG, "GroupUserInfoEvent " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if(groupUserInfo.getNickname() != null && groupUserInfo.getGroupId() != null) {
            int count = this.mListAdapter.getCount();
            int first = this.mList.getFirstVisiblePosition();
            int last = this.mList.getLastVisiblePosition();

            for(int i = 0; i < count; ++i) {
                UIMessage uiMessage = this.mListAdapter.getItem(i);
                if(uiMessage.getSenderUserId().equals(groupUserInfo.getUserId())) {
                    uiMessage.setNickName(true);
                    UserInfo userInfo = uiMessage.getUserInfo();
                    if(userInfo != null) {
                        userInfo.setName(groupUserInfo.getNickname());
                    } else {
                        userInfo = new UserInfo(groupUserInfo.getUserId(), groupUserInfo.getNickname(), null);
                    }

                    uiMessage.setUserInfo(userInfo);
                    int pos = this.getPositionInListView(i);
                    if(pos >= first && pos <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                }
            }

        }
    }

    private View getListViewChildAt(int adapterIndex) {
        int header = this.mList.getHeaderViewsCount();
        int first = this.mList.getFirstVisiblePosition();
        return this.mList.getChildAt(adapterIndex + header - first);
    }

    private int getPositionInListView(int adapterIndex) {
        int header = this.mList.getHeaderViewsCount();
        return adapterIndex + header;
    }

    private int getPositionInAdapter(int listIndex) {
        int header = this.mList.getHeaderViewsCount();
        return listIndex <= 0?0:listIndex - header;
    }

    public final void onEventMainThread(Event.OnMessageSendErrorEvent event) {
        this.onEventMainThread(event.getMessage());
    }

    public final void onEventMainThread(Event.OnReceiveMessageEvent event) {
        Message message = event.getMessage();
        Log.i(TAG, "OnReceiveMessageEvent, " + message.getMessageId() + ", " + message.getObjectName() + ", " + message.getReceivedStatus().toString());
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if(this.mConversationType.equals(conversationType) && this.mTargetId.equals(targetId) && this.shouldUpdateMessage(message, event.getLeft())) {
            if(event.getLeft() == 0 && message.getConversationType().equals(Conversation
                    .ConversationType.PRIVATE) && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE) && message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                if(this.mReadRec) {
                    RongIMClient.getInstance().sendReadReceiptMessage(message.getConversationType(), message.getTargetId(), message.getSentTime());
                } else if(this.mSyncReadStatus) {
                    RongIMClient.getInstance().syncConversationReadStatus(message.getConversationType(), message.getTargetId(), message.getSentTime(), null);
                }
            }

            if(this.mSyncReadStatus) {
                this.mSyncReadStatusMsgTime = message.getSentTime();
            }

            if(message.getMessageId() > 0) {
                Message.ReceivedStatus status = message.getReceivedStatus();
                status.setRead();
                message.setReceivedStatus(status);
                RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), status, null);
                if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && !this.robotType && this.mCustomServiceConfig.adminTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.adminTipWord)) {
                    this.startTimer(1, this.mCustomServiceConfig.adminTipTime * 60 * 1000);
                }
            }

            if(this.mNewMessageBtn != null && this.mList.getCount() - this.mList.getLastVisiblePosition() > 2 && Message.MessageDirection.SEND != message.getMessageDirection() && message.getConversationType() != Conversation.ConversationType.CHATROOM && message.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE && message.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE && message.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) {
                ++this.mNewMessageCount;
                if(this.mNewMessageCount > 0) {
                    this.mNewMessageBtn.setVisibility(View.VISIBLE);
                    this.mNewMessageTextView.setVisibility(View.VISIBLE);
                }

                if(this.mNewMessageCount > 99) {
                    this.mNewMessageTextView.setText("99+");
                } else {
                    this.mNewMessageTextView.setText(this.mNewMessageCount + "");
                }
            }

            this.onEventMainThread(event.getMessage());
        }

    }

    public final void onEventBackgroundThread(final Event.PlayAudioEvent event) {
        this.getHandler().post(new Runnable() {
            public void run() {
                ConVersationFragment.this.handleAudioPlayEvent(event);
            }
        });
    }

    private void handleAudioPlayEvent(Event.PlayAudioEvent event) {
        Log.i(TAG, "PlayAudioEvent");
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        int position = this.mListAdapter.findPosition((long)event.messageId);
        if(event.continuously && position >= 0) {
            while(first <= last) {
                ++position;
                ++first;
                UIMessage uiMessage = this.mListAdapter.getItem(position);
                if(uiMessage != null && uiMessage.getContent() instanceof VoiceMessage && uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE) && !uiMessage.getReceivedStatus().isListened()) {
                    uiMessage.continuePlayAudio = true;
                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                    break;
                }
            }
        }

    }

    public final void onEventMainThread(Event.OnReceiveMessageProgressEvent event) {
        if(this.mList != null) {
            int first = this.mList.getFirstVisiblePosition();

            for(int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
                int position = this.getPositionInAdapter(first);
                UIMessage uiMessage = this.mListAdapter.getItem(position);
                if(uiMessage.getMessageId() == event.getMessage().getMessageId()) {
                    uiMessage.setProgress(event.getProgress());
                    if(this.isResumed()) {
                        this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                    }
                    break;
                }
            }
        }

    }

    public final void onEventMainThread(UserInfo userInfo) {
        Log.i(TAG, "userInfo " + userInfo.getUserId());
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();

        for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
            UIMessage uiMessage = this.mListAdapter.getItem(i);
            if(userInfo.getUserId().equals(uiMessage.getSenderUserId()) && !uiMessage.isNickName()) {
                if(uiMessage.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && uiMessage.getMessage() != null && uiMessage.getMessage().getContent() != null && uiMessage.getMessage().getContent().getUserInfo() != null) {
                    uiMessage.setUserInfo(uiMessage.getMessage().getContent().getUserInfo());
                } else {
                    uiMessage.setUserInfo(userInfo);
                }

                int position = this.getPositionInListView(i);
                if(position >= first && position <= last) {
                    this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                }
            }
        }

    }

    public final void onEventMainThread(PublicServiceProfile publicServiceProfile) {
        Log.i(TAG, "publicServiceProfile");
        int first = this.mList.getFirstVisiblePosition();

        for(int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
            int position = this.getPositionInAdapter(first);
            UIMessage message = this.mListAdapter.getItem(position);
            if(message != null && (TextUtils.isEmpty(message.getTargetId()) || publicServiceProfile.getTargetId().equals(message.getTargetId()))) {
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            }
        }

    }

    public final void onEventMainThread(Event.ReadReceiptEvent event) {
        Log.i(TAG, "ReadReceiptEvent");
        if(RongContext.getInstance().isReadReceiptConversationType(event.getMessage().getConversationType()) && this.mTargetId.equals(event.getMessage().getTargetId()) && this.mConversationType.equals(event.getMessage().getConversationType()) && event.getMessage().getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
            ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
            long ntfTime = content.getLastMessageSendTime();

            for(int i = this.mListAdapter.getCount() - 1; i >= 0; --i) {
                UIMessage uiMessage = this.mListAdapter.getItem(i);
                if(uiMessage.getMessageDirection().equals(Message.MessageDirection.SEND) && uiMessage.getSentStatus() == Message.SentStatus.SENT && ntfTime >= uiMessage.getSentTime()) {
                    uiMessage.setSentStatus(Message.SentStatus.READ);
                    int first = this.mList.getFirstVisiblePosition();
                    int last = this.mList.getLastVisiblePosition();
                    int position = this.getPositionInListView(i);
                    if(position >= first && position <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                }
            }
        }

    }

    public MessageListAdapter getMessageAdapter() {
        return this.mListAdapter;
    }

    public boolean shouldUpdateMessage(Message message, int left) {
        return true;
    }

    public void getHistoryMessage(Conversation.ConversationType conversationType, String targetId, int lastMessageId, int reqCount, final IHistoryDataResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, lastMessageId, reqCount, new RongIMClient.ResultCallback<List<Message>>() {
            public void onSuccess(List<Message> messages) {
                if(callback != null) {
                    callback.onResult(messages);
                }

            }

            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "getHistoryMessages " + e);
                if(callback != null) {
                    callback.onResult(null);
                }

            }
        });
    }

    private void getHistoryMessage(Conversation.ConversationType conversationType, String targetId, final int reqCount, final int scrollMode) {
        this.mList.onRefreshStart(AutoRefreshListView.Mode.START);
        if(conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            this.mList.onRefreshComplete(0, 0, false);
            Log.w(TAG, "Should not get local message in chatroom");
        } else {
            this.mList.onRefreshStart(AutoRefreshListView.Mode.START);
            int last = this.mListAdapter.getCount() == 0?-1: this.mListAdapter.getItem(0).getMessageId();
            this.getHistoryMessage(conversationType, targetId, last, reqCount, new IHistoryDataResultCallback<List<Message>>() {
                public void onResult(List<Message> messages) {
                    Log.i(TAG, "getHistoryMessage " + (messages != null?messages.size():0));
                    ConVersationFragment.this.mHasMoreLocalMessages = (messages != null?messages.size():0) == reqCount;
                    ConVersationFragment.this.mList.onRefreshComplete(reqCount, reqCount, false);
                    if(messages != null && messages.size() > 0) {
                        Iterator index = messages.iterator();

                        while(index.hasNext()) {
                            Message message = (Message)index.next();
                            boolean contains = false;

                            for(int uiMessage = 0; uiMessage < ConVersationFragment.this.mListAdapter.getCount(); ++uiMessage) {
                                contains = ConVersationFragment.this.mListAdapter.getItem(uiMessage).getMessageId() == message.getMessageId();
                                if(contains) {
                                    break;
                                }
                            }

                            if(!contains) {
                                UIMessage var7 = UIMessage.obtain(message);
                                if(message.getContent() instanceof CSPullLeaveMessage) {
                                    var7.setCsConfig(ConVersationFragment.this.mCustomServiceConfig);
                                }

                                ConVersationFragment.this.mListAdapter.add(var7, 0);
                            }
                        }

                        if(scrollMode == 3) {
                            ConVersationFragment.this.mList.setTranscriptMode(2);
                        } else {
                            ConVersationFragment.this.mList.setTranscriptMode(0);
                        }

                        ConVersationFragment.this.mListAdapter.notifyDataSetChanged();
                        if(ConVersationFragment.this.mLastMentionMsgId > 0) {
                            int var6 = ConVersationFragment.this.mListAdapter.findPosition((long)ConVersationFragment.this.mLastMentionMsgId);
                            ConVersationFragment.this.mList.smoothScrollToPosition(var6);
                            ConVersationFragment.this.mLastMentionMsgId = 0;
                        } else if(2 == scrollMode) {
                            ConVersationFragment.this.mList.setSelection(0);
                        } else if(scrollMode == 3) {
                            ConVersationFragment.this.mList.setSelection(ConVersationFragment.this.mList.getCount());
                        } else {
                            ConVersationFragment.this.mList.setSelection(messages.size() + 1);
                        }

                        ConVersationFragment.this.sendReadReceiptResponseIfNeeded(messages);
                    } else {
                        ConVersationFragment.this.mList.onRefreshComplete(reqCount, reqCount, false);
                    }

                }

                public void onError() {
                    ConVersationFragment.this.mList.onRefreshComplete(reqCount, reqCount, false);
                }
            });
        }
    }

    public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dateTime, int reqCount, final IHistoryDataResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new RongIMClient.ResultCallback<List<Message>>() {
            public void onSuccess(List<Message> messages) {
                if(callback != null) {
                    callback.onResult(messages);
                }

            }

            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "getRemoteHistoryMessages " + e);
                if(callback != null) {
                    callback.onResult(null);
                }

            }
        });
    }

    private void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, final int reqCount) {
        this.mList.onRefreshStart(AutoRefreshListView.Mode.START);
        if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
            this.mList.onRefreshComplete(0, 0, false);
            Log.w(TAG, "Should not get remote message in chatroom");
        } else {
            long dateTime = this.mListAdapter.getCount() == 0?0L: this.mListAdapter.getItem(0).getSentTime();
            this.getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new IHistoryDataResultCallback<List<Message>>() {
                public void onResult(List<Message> messages) {
                    Log.i(TAG, "getRemoteHistoryMessages " + (messages == null?0:messages.size()));
                    ConVersationFragment.this.mList.onRefreshComplete(messages == null?0:messages.size(), reqCount, false);
                    if(messages != null && messages.size() > 0) {
                        ArrayList remoteList = new ArrayList();
                        Iterator i$ = messages.iterator();

                        while(i$.hasNext()) {
                            Message uiMessage = (Message)i$.next();
                            if(uiMessage.getMessageId() > 0) {
                                UIMessage uiMessage1 = UIMessage.obtain(uiMessage);
                                if(uiMessage.getContent() instanceof CSPullLeaveMessage) {
                                    uiMessage1.setCsConfig(ConVersationFragment.this.mCustomServiceConfig);
                                }

                                remoteList.add(uiMessage1);
                            }
                        }

                        List remoteList1 = ConVersationFragment.this.filterMessage(remoteList);
                        if(remoteList1 != null && remoteList1.size() > 0) {
                            i$ = remoteList1.iterator();

                            while(i$.hasNext()) {
                                UIMessage uiMessage2 = (UIMessage)i$.next();
                                ConVersationFragment.this.mListAdapter.add(uiMessage2, 0);
                            }

                            ConVersationFragment.this.mList.setTranscriptMode(0);
                            ConVersationFragment.this.mListAdapter.notifyDataSetChanged();
                            ConVersationFragment.this.mList.setSelection(messages.size() + 1);
                            ConVersationFragment.this.sendReadReceiptResponseIfNeeded(messages);
                        }
                    } else {
                        ConVersationFragment.this.mList.onRefreshComplete(0, reqCount, false);
                    }

                }

                public void onError() {
                    ConVersationFragment.this.mList.onRefreshComplete(0, reqCount, false);
                }
            });
        }
    }

    private List<UIMessage> filterMessage(List<UIMessage> srcList) {
        Object destList;
        if(this.mListAdapter.getCount() > 0) {
            destList = new ArrayList();

            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
                Iterator i$ = srcList.iterator();

                while(i$.hasNext()) {
                    UIMessage msg = (UIMessage)i$.next();
                    if(!((List)destList).contains(msg) && msg.getMessageId() != this.mListAdapter.getItem(i).getMessageId()) {
                        ((List)destList).add(msg);
                    }
                }
            }
        } else {
            destList = srcList;
        }

        return (List)destList;
    }

    private void sendReadReceiptAndSyncUnreadStatus(Conversation.ConversationType conversationType, String targetId, long timeStamp) {
        if(conversationType == Conversation.ConversationType.PRIVATE) {
            if(this.mReadRec && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE)) {
                RongIMClient.getInstance().sendReadReceiptMessage(conversationType, targetId, timeStamp);

            } else if(this.mSyncReadStatus) {
                RongIMClient.getInstance().syncConversationReadStatus(conversationType, targetId, timeStamp, null);
            }
        } else if(conversationType.equals(Conversation.ConversationType.GROUP) || conversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            RongIMClient.getInstance().syncConversationReadStatus(conversationType, targetId, timeStamp, null);
        }

    }

    private void getLastMentionedMessageId(Conversation.ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().getUnreadMentionedMessages(conversationType, targetId, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if(messages != null && messages.size() > 0) {
                    ConVersationFragment.this.mLastMentionMsgId = messages.get(0).getMessageId();
                    int index = ConVersationFragment.this.mListAdapter.findPosition((long)ConVersationFragment.this.mLastMentionMsgId);
                    Log.i(TAG, "getLastMentionedMessageId " + ConVersationFragment.this.mLastMentionMsgId + " " + index);
                    if(ConVersationFragment.this.mLastMentionMsgId > 0 && index >= 0) {
                        ConVersationFragment.this.mList.smoothScrollToPosition(index);
                        ConVersationFragment.this.mLastMentionMsgId = 0;
                    }
                }

            }



            public void onError(RongIMClient.ErrorCode e) {
            }
        });
    }

    private void sendReadReceiptResponseIfNeeded(List<Message> messages) {
        if(this.mReadRec && (this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(this.mConversationType)) {
            ArrayList responseMessageList = new ArrayList();
            Iterator i$ = messages.iterator();

            while(i$.hasNext()) {
                Message message = (Message)i$.next();
                ReadReceiptInfo readReceiptInfo = message.getReadReceiptInfo();
                if(readReceiptInfo != null && readReceiptInfo.isReadReceiptMessage() && !readReceiptInfo.hasRespond()) {
                    responseMessageList.add(message);
                }
            }

            if(responseMessageList.size() > 0) {
                RongIMClient.getInstance().sendReadReceiptResponse(this.mConversationType, this.mTargetId, responseMessageList, null);
            }
        }

    }

    public void onExtensionCollapsed() {
    }

    public void onExtensionExpanded(int h) {
        this.mList.setTranscriptMode(2);
        this.mList.smoothScrollToPosition(this.mList.getCount());
    }

    public void onStartCustomService(String targetId) {
        this.csEnterTime = System.currentTimeMillis();
        this.mRongExtension.setExtensionBarMode(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
        RongIMClient.getInstance().startCustomService(targetId, this.customServiceListener, this.mCustomUserInfo);
    }

    public void onStopCustomService(String targetId) {
        RongIMClient.getInstance().stopCustomService(targetId);
    }

    public final void onEvaluateSubmit() {
        if(this.mEvaluateDialg != null) {
            this.mEvaluateDialg.destroy();
            this.mEvaluateDialg = null;
        }

        if(this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            this.getActivity().finish();
        }

    }

    public final void onEvaluateCanceled() {
        if(this.mEvaluateDialg != null) {
            this.mEvaluateDialg.destroy();
            this.mEvaluateDialg = null;
        }

        if(this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            this.getActivity().finish();
        }

    }

    private void startTimer(int event, int interval) {
        this.getHandler().removeMessages(event);
        this.getHandler().sendEmptyMessageDelayed(event, (long)interval);
    }

    private void stopTimer(int event) {
        this.getHandler().removeMessages(event);
    }

    public RongExtension getRongExtension() {
        return mRongExtension;
    }
}
