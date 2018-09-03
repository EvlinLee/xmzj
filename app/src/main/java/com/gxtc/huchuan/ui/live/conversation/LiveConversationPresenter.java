package com.gxtc.huchuan.ui.live.conversation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInFoStatusBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.LiveInsertBean;
import com.gxtc.huchuan.bean.UploadPPTFileBean;
import com.gxtc.huchuan.bean.event.EventMessageBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.im.bean.RemoteMessageBean;
import com.gxtc.huchuan.im.bean.RemoteMessageBeanDao;
import com.gxtc.huchuan.im.manager.AudioRecordManager;
import com.gxtc.huchuan.im.manager.ConversationManager;
import com.gxtc.huchuan.im.manager.MessageManager;
import com.gxtc.huchuan.im.manager.MessageUploadMyService;
import com.gxtc.huchuan.im.provide.BlacklistMessage;
import com.gxtc.huchuan.im.provide.CountDownMessage;
import com.gxtc.huchuan.im.provide.PPTMessage;
import com.gxtc.huchuan.im.provide.ReceivedMessage;
import com.gxtc.huchuan.im.provide.RedPacketMessage;
import com.gxtc.huchuan.im.provide.RemoveMessage;
import com.gxtc.huchuan.im.provide.SilentMessage;
import com.gxtc.huchuan.im.provide.VoiceMessageAdapter;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.rong.common.FileUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by Gubr on 2017/2/20.
 *
 */
public class LiveConversationPresenter extends BaseRepository implements LiveConversationContract.Presenter,
        PictureConfig.OnSelectResultCallback {
    private static final String TAG            = "LiveConversationPresent";
    private static final int    REQUSET_IMAGE  = 1 << 3;
    public static final  String THUMURI_SUFFIX = "!upyun200";

    private LiveConversationContract.View mView;
    private ChatInfosBean                 mBean;
    private       String                        chatRoomId;
    private       String                        author, host;//作者 主持人：
    private List<Message>             discuss;
    private UserInfo                  mUserInfo;
    private Handler                   mHandler;
    private Message                   message;
    private Message                   mObtain;
    private boolean                   hasCoundDown, hasInvitationLecturer, hasInVitationAudience, hasReceivedMessage;
    private String token;


    //因为  倒计时  邀请 等信息体是不会传到服务器的 但是又要显示在信息里面 为了避免错乱  在跟服务器请求更多历史消息的时候  要先减掉这个数
    private int getMinus() {
        int temp = 0;
        if (hasCoundDown) ++temp;
        if (hasInvitationLecturer) ++temp;
        if (hasInVitationAudience) ++temp;
        if (hasReceivedMessage) ++temp;
        return temp;
    }

    public LiveConversationPresenter(LiveConversationContract.View view, ChatInfosBean bean, String chatRoomId, String author, String host) {
        mBean = bean;
        token = com.gxtc.huchuan.data.UserManager.getInstance().getToken();
        this.chatRoomId = mBean.getId();
        this.author = author;
        this.host = host;

        EventBusUtil.register(this);

        ConversationManager.getInstance().init(mBean);
        mHandler = new MyHandler();

        String headPic = UserManager.getInstance().getUser().getHeadPic();
        Uri    uri     = null;
        if (headPic != null) {
            uri = Uri.parse(headPic);
        }

        mUserInfo = new UserInfo(UserManager.getInstance().getUserCode(),
                UserManager.getInstance().getUser().getName(), uri);
        mView = view;
        mView.setPresenter(this);


        //获取服务器上的课件数据
        getChatInfoSlideList();
        mView.notifyChangeData();
        mView.showLoadDialog(true);
        //这里获取聊天历史记录
        ConversationManager.getInstance().getremoteHistory(10000, "", new ConversationManager.CallBack() {
            @Override
            public void onSuccess(List<Message> messages) {
                if(mView == null)   return;
                mView.showLoadDialog(false);
                /**
                 * isBackLoadMsg
                 * 这个表示是判断是否是从后台播放语音的时候  从服务器拿数据的标志
                 * 因为后台播放语音的时候拿的数据 只拿 语音消息 有可能语音消息之前参杂有图片消息文字消息
                 * 这时候再进入课堂页面听课的话  就拿不到前面的消息了 ，这时候需要根据这个标志去后台从新请求课堂页面全部的消息回来
                 */
                MessageManager.getInstance().addMessages(messages);
                addConversationHeadViews();
                mView.notifyChangeData();
            }

            @Override
            public void onError(String message) {
                if(mView == null) return;
                mView.showLoadDialog(false);
            }

            @Override
            public void onCancel() {
                if(mView == null) return;
                mView.showLoadDialog(false);
            }
        });

    }

    private boolean islaodTopicHistoring = false;

    @Override
    public void remoteTopicHistory() {
        if (islaodTopicHistoring) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("targetType", Conversation.ConversationType.CHATROOM.getValue() + "");
        map.put("targetId", chatRoomId);
        map.put("start", "0");
        map.put("roleType", "2");
        LiveApi.getInstance()
               .getMessageRecordList(map)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .map(new Func1<ApiResponseBean<List<RemoteMessageBean>>, List<Message>>() {
                    @Override
                    public List<Message> call(ApiResponseBean<List<RemoteMessageBean>> listApiResponseBean) {
                        return MessageFactory.create(listApiResponseBean.getResult());
                    }
                })
               .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {
                        islaodTopicHistoring = false;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        if (mView != null) {
                            mView.showDiscussIntro(messages);
                        }
                    }

               });
    }


    /**
     * 如果新建的话  应该可以添加  如果是拿 历史数据的话 应该是 先看是否已经到最上面的时候再添加这些消息。
     */
    private void addConversationHeadViews() {
        if (!TextUtils.isEmpty(mBean.getStarttime()) && DateUtil.isDeadLine(mBean.getStarttime())) {
            addCoundDownView();
        }
    }


    /**
     * 获取后面的历史数据
     */
    @Override
    public void getDownRemoteHistory() {
        Message lastMessage = MessageManager.getInstance().getLastMessage();
        Extra extra = null;
        if (lastMessage != null) {
            extra = new Extra(extraStr(lastMessage));
        }
        if (extra != null) {
            ConversationManager.getInstance().downRmoteHistory(extra.getMsgId(), "1", "2",
                    new ConversationManager.CallBack() {

                        @Override
                        public void onSuccess(List<Message> messages) {
                            if(mView == null) return;
                            if (messages.size() == 0) {
                                mView.downRefreshFinish();
                            } else {
                                MessageManager.getInstance().addMessages(messages);
                                mView.notifyChangeData();
                            }
                        }

                        @Override
                        public void onError(String message) {}

                        @Override
                        public void onCancel() {}
                    });
        } else {
            if(mView == null) return;
            mView.downRefreshFinish();
        }

    }


    /**
     * 获取更前的历史数据
     */
    @Override
    public void getUpRemoteHistory() {
        Message firstMessage = MessageManager.getInstance().getFirstMessage();
        String  extraStr     = null;
        if(mView == null) return;
        if (firstMessage == null) {
            mView.upRefreshFinish();
            return;

        } else {
            extraStr = extraStr(firstMessage);
            if (extraStr == null) {
                mView.upRefreshFinish();
                return;
            }
            Extra extra = new Extra(extraStr);
            if (extra != null)
                ConversationManager.getInstance().upRemoteHistory(extra.getMsgId(), "1", "1", new ConversationManager.CallBack() {
                            @Override
                            public void onSuccess(List<Message> messages) {
                                if (messages.size() > 0) {
                                    MessageManager.getInstance().addMessagesToHead(messages);
                                } else {
                                    addConversationHeadViews();
                                }
                                if(mView == null) return;
                                mView.upRefreshFinish();
                            }

                            @Override
                            public void onError(String message) {
                                if(mView == null) return;
                                mView.upRefreshFinish();
                            }

                            @Override
                            public void onCancel() {
                                if(mView == null) return;
                                mView.upRefreshFinish();
                            }
                        });
        }

    }


    private void addCoundDownView() {
        Message message = null;
        if (MessageManager.getInstance().getMessages().size() > 0) {
            message = MessageManager.getInstance().getMessages().get(0);
        }
        if (message == null || (!"XM:CdMsg".equals(message.getObjectName()) && !hasCoundDown)) {
            CountDownMessage countDownMessage = new CountDownMessage(new byte[]{});
            countDownMessage.setTimestamp(mBean.getStarttime());
            countDownMessage.setContent(mBean.getSubtitle());
            Message message1 = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, countDownMessage);
            message1.setObjectName(CountDownMessage.class.getAnnotation(MessageTag.class).value());
            MessageManager.getInstance().addMessage(message1, 0);
        }
        hasCoundDown = true;


    }

    @Override
    public void setCanLoadRemoteMessage(boolean flag) {
        ConversationManager.getInstance().setCanLoadRemoteMessage(flag);
    }

    private void addReceivedMessage() {
        int tempPosition = 0;
        if (hasReceivedMessage) return;
        if (hasCoundDown) ++tempPosition;
        Message message = null;
        if (MessageManager.getInstance().getMessages().size() > tempPosition) {
            message = MessageManager.getInstance().getMessages().get(tempPosition);
        }
        if (message == null || (!"XM:ReMsg".equals(message.getObjectName()))) {
            ReceivedMessage receivedMessage = new ReceivedMessage(new byte[]{});
            receivedMessage.setContent(mBean.getId());
            receivedMessage.setUserInfo(mUserInfo);
            Message obtain = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, receivedMessage);
            obtain.setObjectName(ReceivedMessage.class.getAnnotation(MessageTag.class).value());
            MessageManager.getInstance().addMessage(obtain, tempPosition);
        }
        hasReceivedMessage = true;
    }


    @Override
    public void showDiscussIntro() {

    }

    @Override
    public void showDiscussLabel() {

    }

    @Override
    public void showRedpacket() {

    }


    /**
     * 获取服务器上的课件数据
     */
    private void getChatInfoSlideList() {
        addSub(LiveApi.getInstance()
                      .getChatInfoSlideList(UserManager.getInstance().getToken(), chatRoomId)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<UploadPPTFileBean>>>(
                        new ApiCallBack<List<UploadPPTFileBean>>() {
                            @Override
                            public void onSuccess(List<UploadPPTFileBean> data) {
                                if (mView != null) {
                                    ArrayList<Uri> uris = new ArrayList<>();
                                    for (UploadPPTFileBean uploadPPTFileBean : data) {
                                        uris.add(Uri.parse(uploadPPTFileBean.getPicUrl()));
                                    }
                                    mView.showPPT(uris);
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {}
                       })));
    }


    @Override
    public void showImageSelectView() {
        FunctionOptions options =
                new FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_IMAGE)
                        .setSelectMode(FunctionConfig.MODE_MULTIPLE)
                        .setMaxSelectNum(9)
                        .setImageSpanCount(3)
                        .setEnableQualityCompress(false) //是否启质量压缩
                        .setEnablePixelCompress(false) //是否启用像素压缩
                        .setEnablePreview(true) // 是否打开预览选项
                        .setShowCamera(true)
                        .setPreviewVideo(true)
                        .create();
        PictureConfig.getInstance().init(options).openPhoto((Activity) mView.getContext(), this);
    }

    @Override
    public void sendAudienceMessage(String msg, boolean b) {
        sendMessage(msg, "2", b);
    }


    private void sendMessage(String msg, String sendertype, boolean b) {
        TextMessage myTextMessage     = TextMessage.obtain(msg);
        long        currentTimeMillis = System.currentTimeMillis();
        long msgId = currentTimeMillis + (new Random().nextInt(99999 - 10000) + 10000);
        final Extra extra = Extra.obtan(sendertype, b, mBean.getId() + msgId, "1");
        extra.setSentTime(currentTimeMillis);
        myTextMessage.setUserInfo(mUserInfo);

        myTextMessage.setExtra(extra.encode());
        Message message = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, myTextMessage);
        message.setExtra(extra.encode());
        RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                        message.setExtra(((TextMessage) message.getContent()).getExtra());
                        Extra extra1 = new Extra(((TextMessage) message.getContent()).getExtra());
                        if(mView != null){
                            if ("2".equals(extra1.getSenderType())) {
                                mView.addDiscussIntro(message);
                            } else {
                                mView.addMessage(message);
                            }
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        if(mView == null) return;
                        ToastUtil.showShort(mView.getContext(),RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }


    /**
     * 发送文本信息
     *
     * @param string
     */
    @Override
    public void sendMessage(String string) {
        sendMessage(string, mBean.isSelff() ? "3" : "1", false);
    }

    @Override
    public void start() {
        RongIMClient.getInstance().joinExistChatRoom("lskdjf", 50,
                new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {}
                });
    }

    @Override
    public void destroy() {
        super.destroy();
        mView = null;
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }


    @Subscribe
    public void onEvent(EventMessageBean bean){
        if (!bean.mMessage.getTargetId().equals(chatRoomId) && mView != null) {
            return ;
        }
        String extraStr = extraStr(bean.mMessage);
        Extra extra = null;
        if (extraStr != null) {
            extra = new Extra(extraStr);
            switch (extra.getSenderType()) {
                case "1":
                case "3":
                    setCanLoadRemoteMessage(false);         //只要监听到收到消息 就不再可以去服务器请求历史消息
                    if (mView != null) {
                        mView.downRefreshFinish();
                    }
                    mView.addMessage(bean.mMessage);
                    break;

                case "2":
                    if("XM:RmMsg".equals(bean.mMessage.getObjectName())){
                        mView.showRemoveDiscussIntro(bean.mMessage);
                    }else{
                        mView.addDiscussIntro(bean.mMessage);
                    }
                    break;

                case "4":
                    if (bean.mMessage.getObjectName().equals("XM:SLMsg")) {
                        changeSilentModel(bean.mMessage);
                    }

                    if (bean.mMessage.getObjectName().equals("XM:PPTMsg")) {
                        changePPT();
                    }

                    if (bean.mMessage.getObjectName().equals("XM:RmMsg")) {
                        removeMessagebyRemote(bean.mMessage);
                    }

                    if(bean.mMessage.getObjectName().equals("XM:BLMsg")){
                        kickOutRoom(bean.mMessage);
                    }

                    break;
                case "":
                    changePPT();
            }
        }
    }


    //踢出聊天室
    private void kickOutRoom(Message message) {
        BlacklistMessage blackMsg = (BlacklistMessage) message.getContent();
        if(blackMsg.getUserCode().equals(UserManager.getInstance().getUserCode())){
            mView.showKickOutRoom();
        }
    }

    private void removeMessagebyRemote(Message message) {
        RemoveMessage removeMessage = (RemoveMessage) message.getContent();
        List<RemoteMessageBean> list = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                RemoteMessageBeanDao.Properties.MsgId.eq(removeMessage.getContent())).limit(
                1).build().list();
        if (list.size() > 0) {
            GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().deleteInTx(list);
        }
        Message message1 = MessageManager.getInstance().removeMessage(String.valueOf(removeMessage.getContent()));
        mView.removeMessage(message1);
    }

    private void changePPT() {
        addSub(LiveApi.getInstance()
                      .getChatInfoSlideList(UserManager.getInstance().getToken(), chatRoomId)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<UploadPPTFileBean>>>(
                        new ApiCallBack<List<UploadPPTFileBean>>() {
                            @Override
                            public void onSuccess(List<UploadPPTFileBean> data) {
                                if (mView != null) {
                                    ArrayList<Uri> uris = new ArrayList<>();
                                    for (UploadPPTFileBean uploadPPTFileBean : data) {
                                        uris.add(Uri.parse(uploadPPTFileBean.getPicUrl()));
                                    }
                                    mView.changePPT(uris);
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {

                            }
                        })));
    }


    private void changeSilentModel(Message message) {
        SilentMessage content = (SilentMessage) message.getContent();
        boolean       b       = Boolean.parseBoolean(content.getContent());
        String targetUserCode = content.getUserCode();
        if (content.getUserCode().equals("0") || targetUserCode.equals(UserManager.getInstance().getUserCode())) {
            mBean.setIsBanned(b ? "1" : "0");
            mView.setSilentModel(b);
        }

    }

    LinkedList<String> mImageList;


    @Override
    public void sendImageMessage(List<String> imagePath) {
        if (mImageList == null) {
            mImageList = new LinkedList<>();
        }
        mImageList.addAll(imagePath);
        sendImageMessage();
    }

    @Override
    public void sendRedPacketMessage(OrdersRequestBean requestBean) {
        String  extra1     = requestBean.getExtra();
        String  totalPrice = requestBean.getTotalPrice();
        double money       = Double.valueOf(totalPrice);
        double  price      = money / 100.0d;

        String userCode = "";
        String name     = "";
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(extra1);
            if (jsonObject.has("userCode")) {
                userCode = jsonObject.optString("userCode");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.optString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RedPacketMessage redPacketMessage = RedPacketMessage.obtain(mUserInfo.getName(), name,
                price + "");
        long currentTimeMillis = System.currentTimeMillis();
        final Extra extra = Extra.obtan("1", false, mBean.getId() + currentTimeMillis, "1");
        extra.setSentTime(currentTimeMillis);
        redPacketMessage.setUserInfo(mUserInfo);

        redPacketMessage.setExtra(extra.encode());
        Message message = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, redPacketMessage);
        message.setExtra(extra.encode());
        RongIM.getInstance().sendMessage(message, null, null,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        message.setExtra(((RedPacketMessage) message.getContent()).getExtra());
                        Extra extra1 = new Extra(((RedPacketMessage) message.getContent()).getExtra());
                        if ("2".equals(extra1.getSenderType())) {
                            mView.addDiscussIntro(message);
                        } else {
                            mView.addMessage(message);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        Toast.makeText(MyApplication.getInstance(), "信息发送失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void sendPPTChnageMessage() {
        PPTMessage pptMessage        = PPTMessage.obtain();
        long       currentTimeMillis = System.currentTimeMillis();
        final Extra extra = Extra.obtan("4", false, mBean.getId() + currentTimeMillis,"0");
        extra.setSentTime(currentTimeMillis);
        pptMessage.setUserInfo(mUserInfo);

        pptMessage.setExtra(extra.encode());
        Message message = Message.obtain(mBean.getId(), Conversation.ConversationType.CHATROOM, pptMessage);
        message.setExtra(extra.encode());
        RongIM.getInstance().sendMessage(message, null, null,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    //删除消息 不要加isClass标记
    public void sendRemoveMessage(long msgId) {
        RemoveMessage removeMessage     = RemoveMessage.obtain();
        long          currentTimeMillis = System.currentTimeMillis();
        final Extra extra = Extra.obtan("4", false, mBean.getId() + currentTimeMillis,"0");
        extra.setSentTime(currentTimeMillis);
        removeMessage.setUserInfo(mUserInfo);
        removeMessage.setContent(String.valueOf(msgId));
        removeMessage.setExtra(extra.encode());
        Message message = Message.obtain(mBean.getId(), Conversation.ConversationType.CHATROOM,
                removeMessage);
        message.setExtra(extra.encode());
        RongIM.getInstance().sendMessage(message, null, null,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    @Override
    public void sendPPTImageMessage(String clickImage) {
        ImageMessage imageMessage      = new ImageMessage();
        long         currentTimeMillis = System.currentTimeMillis();
        Extra extra = Extra.obtan(mBean.isSelff() ? "3" : "1", false, mBean.getId() + currentTimeMillis,"1");
        extra.setSentTime(currentTimeMillis);
        imageMessage.setUserInfo(mUserInfo);
        Uri imageUri = Uri.parse(clickImage);
        imageMessage.setLocalUri(imageUri);
        imageMessage.setRemoteUri(imageUri);
        imageMessage.setThumUri(imageUri);
        imageMessage.setExtra(extra.encode());
        Message message = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, imageMessage);
        RongIM.getInstance().sendMessage(message, null, null,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {

                        message.setExtra(((ImageMessage) message.getContent()).getExtra());
                        Extra extra1 = new Extra(((ImageMessage) message.getContent()).getExtra());

                        if ("2".equals(extra1.getSenderType())) {
                            mView.addDiscussIntro(message);
                        } else {
                            mView.addMessage(message);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });
    }


    public void sendImageMessage() {
        if (mImageList.size() > 0)
            compressImg(mImageList.removeLast());
    }

    public void compressImg(final String s) {

        //将图片进行压缩
        final File file = new File(s);
        final long minSize= Constant.COMPRESS_VALUE;
        if(FileUtil.getSize(file) > minSize){
            Luban.get(MyApplication.getInstance()).load(file)       //传人要压缩的图片
                    .putGear(Luban.THIRD_GEAR)                      //设定压缩档次，默认三挡
                    .setCompressListener(new OnCompressListener() {

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(File file) {
                            sendImageMessage(file);
                        }

                        //  当压缩过去出现问题时调用
                        @Override
                        public void onError(Throwable e) {
                        }
                    }).launch();
        }else {
            sendImageMessage(file);
        }

    }


    /**
     * 发送图片
     *
     * @param imagePath 图片地址
     */

    public void sendImageMessage(File imagePath) {

        final ImageMessage imgMsg = ImageMessage.obtain(Uri.fromFile(imagePath), Uri.fromFile(imagePath), false);

        imgMsg.setUserInfo(mUserInfo);
        Extra extra = Extra.obtan(mBean.isSelff() ? "3" : "1", mView.getIsAsk(),
                mBean.getId() + System.currentTimeMillis(),"1");
        imgMsg.setExtra(extra.encode());
        Message message = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM,
                imgMsg);


        RongIM.getInstance().sendImageMessage(message, null, null,
                new RongIMClient.SendImageMessageWithUploadListenerCallback() {

                    @Override
                    public void onAttached(Message message, RongIMClient.UploadImageStatusListener uploadImageStatusListener) {
                        Uri localUri = ((ImageMessage) message.getContent()).getLocalUri();
                        uploadImage(localUri, uploadImageStatusListener);
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        if(mView == null) return;
                        ImageMessage content = (ImageMessage) message.getContent();
                        String s = content.getRemoteUri().toString();
                        s = s + THUMURI_SUFFIX;
                        content.setThumUri(Uri.parse(s));
                        content.setLocalUri(content.getRemoteUri());
                        message.setExtra(content.getExtra());

                        mView.addMessage(message);
                        sendImageMessage();
                    }

                    @Override
                    public void onProgress(Message message, int i) {

                    }
                });
    }


    public void sendImageMessage(String imagePath) {
        long l               = System.currentTimeMillis();
        File imageFileSource = new File(mView.getContext().getCacheDir(), l + "source.jpg");
        File imageFileThumb  = new File(mView.getContext().getCacheDir(), l + "thumb.jpg");

        File file = new File(imagePath);
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                Bitmap          bmpSource   = BitmapFactory.decodeStream(inputStream);
                imageFileSource.createNewFile();
                FileOutputStream fosSource = new FileOutputStream(imageFileSource);
                bmpSource.compress(Bitmap.CompressFormat.JPEG, 100, fosSource);
                Matrix matrix = new Matrix();
                RectF  rectF  = new RectF();
                if (bmpSource.getWidth() > 480) {
                    double i      = bmpSource.getWidth() / 480.0;
                    int    height = (int) (bmpSource.getHeight() / i);
                    rectF.set(0, 0, 480, height);
                } else {
                    rectF.set(0, 0, bmpSource.getWidth(), bmpSource.getHeight());
                }
                matrix.setRectToRect(new RectF(0, 0, bmpSource.getWidth(), bmpSource.getHeight()),
                        rectF, Matrix.ScaleToFit.CENTER);
                Bitmap bmpThumb = Bitmap.createBitmap(bmpSource, 0, 0, bmpSource.getWidth(),
                        bmpSource.getHeight(), matrix, true);
                imageFileThumb.createNewFile();
                FileOutputStream fosThumb = new FileOutputStream(imageFileThumb);
                bmpThumb.compress(Bitmap.CompressFormat.JPEG, 80, fosThumb);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final ImageMessage imgMsg = ImageMessage.obtain(Uri.fromFile(imageFileThumb),
                    Uri.fromFile(imageFileSource), true);

            imgMsg.setUserInfo(mUserInfo);
            Extra extra = Extra.obtan(mBean.isSelff() ? "3" : "1", mView.getIsAsk(),
                    mBean.getId() + System.currentTimeMillis(),"1");
            imgMsg.setExtra(extra.encode());
            Message message = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM,
                    imgMsg);


            RongIM.getInstance().sendImageMessage(message, null, null,
                    new RongIMClient.SendImageMessageWithUploadListenerCallback() {


                        @Override
                        public void onAttached(Message message,
                                RongIMClient.UploadImageStatusListener uploadImageStatusListener) {
                            Uri localUri = ((ImageMessage) message.getContent()).getLocalUri();
                            uploadImage(localUri, uploadImageStatusListener);
                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                        }

                        @Override
                        public void onSuccess(Message message) {

                            ImageMessage content = (ImageMessage) message.getContent();
                            content.setThumUri(content.getRemoteUri());
                            content.setLocalUri(content.getRemoteUri());

                            mView.addMessage(message);
                            sendImageMessage();
                        }

                        @Override
                        public void onProgress(Message message, int i) {

                        }
                    });
        }
    }


    /**
     * 上传IM图片到自己的服务器
     */
    public void uploadImage(Uri uri,
            final RongIMClient.UploadImageStatusListener uploadImageStatusListener) {
        File file = new File(uri.getPath());
        MultipartBody build = new MultipartBody.Builder().setType(
                MultipartBody.FORM).addFormDataPart("token", token).addFormDataPart("file",
                file.getName(), RequestBody.create(MediaType.parse("image*//**//*"), file)).build();
        addSub(LiveApi.getInstance()
                      .uploadIMFile(build)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<String>>>(new ApiCallBack<List<String>>() {
                            @Override
                            public void onSuccess(List<String> data) {
                                if(uploadImageStatusListener == null) return;
                                if (data.size() > 0) {
                                    String uriString = data.get(0);
                                    Uri    iamgeUri  = Uri.parse(uriString);
                                    uploadImageStatusListener.success(iamgeUri);
                                } else {
                                    uploadImageStatusListener.error();
                                }

                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if(uploadImageStatusListener == null) return;
                                uploadImageStatusListener.error();
                            }
                        })));
    }


    /**
     * 发送语音信息
     */
    @Override
    public void sendVoiceMessage() {
        Uri audioPath = AudioRecordManager.getInstance().getAudioPath();
        int duration  = AudioRecordManager.getInstance().getDuration();
        if (audioPath == null || duration < 3) {
            //这里 取消发送
            return;
        }
        VoiceMessage vocMsg = VoiceMessage.obtain(audioPath, duration);
        vocMsg.setUserInfo(mUserInfo);
        Extra extra = Extra.obtan(mBean.isSelff() ? "3" : "1", mView.getIsAsk(), mBean.getId() + System.currentTimeMillis(),"1");
        vocMsg.setExtra(extra.encode());
        Message message = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, vocMsg);

        RongIM.getInstance().sendMessage(message, null, null,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                        VoiceMessage content = (VoiceMessage) message.getContent();
                    }

                    @Override
                    public void onSuccess(Message message) {
                        VoiceMessage content = (VoiceMessage) message.getContent();
                        uploadVoiceMessagev2(content);
                        if(mView == null) return;
                        mView.addMessage(message);
                        LogUtil.i("发送语音消息成功  :  时长" + content.getDuration());
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        LogUtil.i("发送语音消息失败  : " + errorCode );
                    }
                });
    }


    @Override
    public void joinChatRoom(String charRoomgId) {

    }


    @Override
    public void removeMessage(final Message message) {
        MessageUploadMyService.removeMessage(message, new MessageUploadMyService.CallBack() {
            @Override
            public void onSuccess() {
                Extra extra = MessageFactory.getExtrabyMessage(message);
                if (extra==null) {
                    return;
                }
                MessageManager.getInstance().removeMessage(message);
                mView.removeMessage(message);
                List<RemoteMessageBean> list = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                        RemoteMessageBeanDao.Properties.MsgId.eq(extra.getMsgId())).limit(
                        1).build().list();
                if (list.size() > 0) {
                    GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().deleteInTx(
                            list);
                }
                sendRemoveMessage(Long.valueOf(extra.getMsgId()));

            }

            @Override
            public void onError(String message) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void uploadVoiceMessagev2(final VoiceMessage message) {
        File file = new File(message.getUri().getPath());

        Subscription sub =
        Observable.just(message)
                  .subscribeOn(Schedulers.io())
                  .map(new Func1<VoiceMessage, Message>() {
                            @Override
                            public Message call(VoiceMessage voiceMessage) {
                                Uri    uri       = voiceMessage.getUri();
                                byte[] voiceData = FileUtils.getByteFromUri(uri);
                                try {
                                    String var9 = Base64.encodeToString(voiceData, 2);
                                    voiceMessage.setBase64(var9);
                                } catch (IllegalArgumentException var8) {
                                    var8.printStackTrace();
                                }
                                Message mObtain = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, voiceMessage);
                                mObtain.setObjectName(VoiceMessageAdapter.class.getAnnotation(MessageTag.class).value());
                                mObtain.setExtra(voiceMessage.getExtra());
                                return mObtain;
                            }
                        })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Action1<Message>() {
                        @Override
                        public void call(Message message) {

                        }
                  });

        addSub(sub);
    }

    private void uploadVoiceMessage(final VoiceMessage message) {
        File file = new File(message.getUri().getPath());
        final RequestBody requestBody = new MultipartBody.Builder().setType(
                MultipartBody.FORM).addFormDataPart("token",
                com.gxtc.huchuan.data.UserManager.getInstance().getToken()).addFormDataPart("file",
                file.getName(), RequestBody.create(MediaType.parse("audio*//**/*"), file)).build();

        Subscription sub =
        LiveApi.getInstance()
               .uploadIMFile(requestBody)
               .subscribeOn(Schedulers.io())
               .map(new Func1<ApiResponseBean<List<String>>, Message>() {
                        @Override
                        public Message call(ApiResponseBean<List<String>> uploadFileBeanApiResponseBean) {
                            List<String> datas = uploadFileBeanApiResponseBean.getResult();
                            VoiceMessageAdapter voiceMessageAdapter = null;
                            for (String data : datas) {
                                voiceMessageAdapter = new VoiceMessageAdapter(message, data);
                            }

                            mObtain = Message.obtain(chatRoomId, Conversation.ConversationType.CHATROOM, voiceMessageAdapter);
                            mObtain.setObjectName(VoiceMessageAdapter.class.getAnnotation(MessageTag.class).value());
                            mObtain.setExtra(message.getExtra());
                            return mObtain;
                        }
                })
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Action1<Message>() {
                    @Override
                    public void call(Message message) {

                    }
                 });

        addSub(sub);
    }

    @Override
    public void collect(String classId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("bizType", "2");
        map.put("bizId", classId);

        Subscription subThumbsup = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(mView != null){
                            mView.showCollectResult(true,"");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(mView != null){
                            mView.showCollectResult(false,message);
                        }
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subThumbsup);
    }

    @Override
    public void follow() {
        if(mBean != null){
            mView.showLoad();
            Subscription sub =
                    LiveApi.getInstance()
                           .setUserFollow(UserManager.getInstance().getToken(), "2", mBean.getChatRoom())
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(
                                   new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                                       @Override
                                       public void onSuccess(Object data) {
                                           mBean.setIsFollow("1");
                                           if(mView != null){
                                               mView.showLoadFinish();
                                               mView.showFollowResult(true, null);
                                           }
                                       }

                                       @Override
                                       public void onError(String errorCode, String message) {
                                           if(mView != null){
                                               mView.showLoadFinish();
                                               mView.showError(message);
                                           }
                                       }
                                   }));
            RxTaskHelper.getInstance().addTask(this,sub);
        }
    }

    //分享到好友
    @Override
    public void shareMessage(final String targetId, final Conversation.ConversationType type, final String title, String img, String id,final String liuyan){
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "2", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                if(mView != null){
                    mView.sendMessage();
                    if(!TextUtils.isEmpty(liuyan)){
                        RongIMTextUtil.INSTANCE.relayMessage
                                (liuyan,targetId,type);
                    }
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                if(mView != null){
                    mView.showError("分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                }
            }
        });
    }


    //发送名片
    @Override
    public void sendVisitingCard(final EventSelectFriendForPostCardBean bean) {
        // 1：圈子，2：文章，3：课程，4：交易，5：商品 6: 个人名片
        LiveInsertBean insertBean = new LiveInsertBean(StringUtil.toInt(bean.userCode));
        insertBean.setInfoType(LiveInsertChooseActivity.TYPE_VISITING_CARD + "");
        insertBean.setTitle(bean.name);
        insertBean.setCover(bean.picHead);

        ImMessageUtils.sendShareMessage(mBean.isSelff()? "3" : "1", chatRoomId,insertBean, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                if(mView != null){
                    mView.addMessage(message);
                    if(!TextUtils.isEmpty(bean.liuyan)){
                        sendMessage(bean.liuyan, mBean.isSelff()? "3" : "1", false);
                    }
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                if(mView != null){
                    mView.showError("发送失败 : " + errorCode);
                }
            }
        });

    }

    @Override
    public void getChatinfosStatus() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("chatInfoId", mBean.getId());
        addSub(LiveApi.getInstance()
                      .getChatInfoStatus(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<ChatInFoStatusBean>>(
                        new ApiCallBack<ChatInFoStatusBean>() {

                            @Override
                            public void onSuccess(ChatInFoStatusBean data) {
                                if(mBean == null)   return;
                                if("1".equals(mBean.getIsBanned()) || "1".equals(data.getIsBanned()) || "1".equals(data.getIsBlock()) || "1".equals(data.getIsBlack())){
                                    mView.setSilentModel(true);
                                }else{
                                    mView.setSilentModel(false);
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {}

                        })));
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {
        List<String> paths = new ArrayList<>();
        for(LocalMedia media: resultList){
            paths.add(media.getPath());
        }
        sendImageMessage(paths);
    }

    @Override
    public void onSelectSuccess(LocalMedia media) {

    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg != null && msg.obj instanceof Message) {

            } else {
                super.handleMessage(msg);
            }
        }
    }


    private String extraStr(Message message) {
        switch (message.getObjectName()) {

            case "RC:VcMsg":
                return ((VoiceMessage) message.getContent()).getExtra();
            case "RC:TxtMsg":
                return ((TextMessage) message.getContent()).getExtra();

            case "RC:ImgMsg":
                return ((ImageMessage) message.getContent()).getExtra();

            case "XM:CdMsg":
                return ((CountDownMessage) message.getContent()).getExtra();

            case "XM:RpMsg":
                return ((RedPacketMessage) message.getContent()).getExtra();

            case "XM:SLMsg":
                return ((SilentMessage) message.getContent()).getExtra();

            case "XM:PPTMsg":
                return ((PPTMessage) message.getContent()).getExtra();

            case "XM:RmMsg":
                return ((RemoveMessage) message.getContent()).getExtra();

            case "XM:BLMsg":
                return ((BlacklistMessage) message.getContent()).getExtra();
        }
        return null;
    }


}
