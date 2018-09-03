package com.gxtc.huchuan.im.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.ConversationTextBean;
import com.gxtc.huchuan.bean.MergeMessageBean;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.event.EventSendRPBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.bean.event.EventUploadVideoBean;
import com.gxtc.huchuan.data.CircleInfoRepository;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.MessageRepository;
import com.gxtc.huchuan.data.MessageSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleInfoContract;
import com.gxtc.huchuan.ui.im.MessageUploadCallback;
import com.gxtc.huchuan.ui.im.merge.MergeHistoryMessage;
import com.gxtc.huchuan.ui.im.postcard.PTMessage;
import com.gxtc.huchuan.ui.im.redPacket.RPMessage;
import com.gxtc.huchuan.ui.im.share.ShareMessage;
import com.gxtc.huchuan.ui.im.video.MessageProgressListener;
import com.gxtc.huchuan.ui.im.video.VideoMessage;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.Event;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/17.
 */

public class ConversationPresenter implements ConversationContract.Presenter {

    private ConversationContract.View mView;
    private CircleSource mData;
    private DealSource mDealData;
    private MessageSource mMessageData;
    private CircleInfoContract.Source mInfoData;

    private String mTargetId;
    private Conversation.ConversationType mType;
    private List<Message> uploadMessagQueen;


    public ConversationPresenter(ConversationContract.View view, String targetId, Conversation.ConversationType type) {
        mView = view;
        mView.setPresenter(this);
        mData = new CircleRepository();
        mDealData = new DealRepository();
        mInfoData = new CircleInfoRepository();
        mMessageData = new MessageRepository();
        mTargetId = targetId;
        mType = type;

        uploadMessagQueen = new ArrayList<>();

        EventBusUtil.register(this);
    }


    @Override
    public void getCircleInfo(String id) {
        String token = UserManager.getInstance().getToken();
       mData.getChatMemberCount(id, token, new ApiCallBack<CircleBean>() {
           @Override
           public void onSuccess(CircleBean data) {
               if(mView == null) return;
               mView.showCircleInfo(data);
           }

           @Override
           public void onError(String errorCode, String message) {
               if(mView == null) return;

               if(Constant.GROUPCODE_OUT.equals(errorCode) || Constant.GROUPCODE_CLEAR.equals(errorCode)){
                   mView.showOutClear(errorCode, message);
                   return;
               }
               mView.showError(message);
           }
       });
    }


    @Override
    public void getRedPackInfo(final Message message) {
        RPMessage rpMessage = (RPMessage) message.getContent();

        mView.showLoad();
        String token = UserManager.getInstance().getToken();
        mData.getRedPacketInfo(token, rpMessage.getRedId(), new ApiCallBack<RedPacketBean>() {
            @Override
            public void onSuccess(RedPacketBean data) {
                if(mView == null || data == null)   return;
                data.setTargetId(message.getTargetId());
                data.setConversationType(message.getConversationType());

                mView.showLoadFinish();
                mView.showRedPackInfo(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showError(message);
            }
        });
    }


    @Override
    public void shareMessage(Message currMessage, String targetId, Conversation.ConversationType type) {
        if(currMessage.getContent() instanceof ShareMessage){
            ShareMessage shareMessage = (ShareMessage) currMessage.getContent();
            String title = shareMessage.getTypeTitle();
            //转发邀请需要更改邀请人的名字
            if("4".equals(shareMessage.getInfoType())|| "5".equals(shareMessage.getInfoType())){
                String t = shareMessage.getTypeTitle();
                String temp [] = t.split("邀请你加入");
                String name = UserManager.getInstance().getUserName();
                if(temp.length >= 2){
                    title = t.replaceFirst(temp[0],name);
                }
            }
            String img = shareMessage.getTypeCover();
            String id = shareMessage.getTypeId();
            String infoType = shareMessage.getInfoType();
            ImMessageUtils.shareMessage(targetId, type, id, title, img, infoType, new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(Message message) {}

                @Override
                public void onSuccess(Message message) {
                    ToastUtil.showShort(MyApplication.getInstance(),"分享成功");
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    ToastUtil.showShort(MyApplication.getInstance(),"分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                }
            });
        }
    }


    @Override
    public void sendRongIm(final EventSelectFriendForPostCardBean bean) {
        PTMessage mPTMessage = PTMessage.obtain(bean.userCode, bean.name, bean.picHead);
        String    title      = mPTMessage.getName();
        String    img        = mPTMessage.getHeadPic();
        String    id         = mPTMessage.getUserCode();
        ImMessageUtils.sentPost(bean.targetId,bean.mType, id, title, img, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(MyApplication.getInstance(),"发送名片成功");
                if(!TextUtils.isEmpty(bean.liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage(bean.liuyan,bean.targetId,bean.mType);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(MyApplication.getInstance(),"发送名片失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }


    //发送小视频
    @Override
    public void sendVideoMessage(Context context, LocalMedia media) {
        final String                 cachePath = FileStorage.getImgCacheFile() + "/" + System.currentTimeMillis() + ".jpg" ;
        final WeakReference<Context> reference = new WeakReference<>(context);
        final LocalMedia tempMedia = media;
        final int [] frameSize = new int[2];

        if(media == null){
            mView.showError("获取本地视频失败");
        }else{
            Subscription sub =
                Observable.just(media.getPath())
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<String, Bitmap>() {          //获取视频的第一帧
                        @Override
                        public Bitmap call(String path) {
                            return ImageUtils.getVideoFirstFrame(path);
                        }
                    })
                    .map(new Func1<Bitmap, File>() {            //将图片保存到本地
                        @Override
                        public File call(Bitmap bitmap) {
                            if(reference.get() != null && bitmap != null){
                                frameSize[0] = bitmap.getWidth();
                                frameSize[1] = bitmap.getHeight();
                                int quality = bitmap.getByteCount() > 1024 * 1024 ? 70 : 100;
                                ImageUtils.saveImage(reference.get(), cachePath, bitmap, quality);
                            }
                            return new File(cachePath);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<File>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            mView.showError("发生未知错误，上传视频失败");
                        }

                        @Override
                        public void onNext(File file) {
                            if(file != null){
                                //插入本地占位消息
                                insertLocalMessage(tempMedia, file.getPath(), frameSize);
                            }else{
                                mView.showError("获取视频信息失败");
                            }
                        }
                    });

            RxTaskHelper.getInstance().addTask(this, sub);
        }
    }


    //发送编辑的图片
    @Override
    public void sendImageMessage(final Context context, final String path, String targetId, Conversation.ConversationType type) {
        ImageMessage imgMsg = ImageMessage.obtain(Uri.fromFile(new File(path)), Uri.fromFile(new File(path)), true);
        Message msg = ImMessageUtils.obtain(targetId, type, imgMsg);
        RongIM.getInstance().sendImageMessage(msg, null, null, new RongIMClient.SendImageMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(context, "发送失败 : " + errorCode);
            }

            @Override
            public void onSuccess(Message message) {
                //删除临时图片文件
                Observable.just(path)
                          .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                if(FileUtil.deleteFile(path)){
                                    //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri    uri    = Uri.fromFile(new File(path));
                                    intent.setData(uri);
                                    context.sendBroadcast(intent);
                                }
                            }
                        });

                ToastUtil.showShort(context, "发送成功");
            }

            @Override
            public void onProgress(Message message, int i) {

            }
        });
    }


    @Override
    public void sendCollectionMessage(EventShareMessage bean, final String targetId, final Conversation.ConversationType conversationType) {
        CollectionBean collectionBean = (CollectionBean) bean.mObject;
        if("6".equals(collectionBean.getType())){
            String      content     = ((ConversationTextBean)collectionBean.getData()).getContent();
            TextMessage textMessage = TextMessage.obtain(content);
            Message     msg         = ImMessageUtils.obtain(targetId,conversationType,textMessage);
            RongIM.getInstance().sendMessage(msg, content, content, new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(Message message) {}

                @Override
                public void onSuccess(Message message) {}

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    LogUtil.i(errorCode.toString());
                }
            });
            return;
        }

        if("7".equals(collectionBean.getType())){
            final String url = ((ConversationTextBean)collectionBean.getData()).getContent();
            Subscription sub =
                Observable.just(url)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<String, File>() {
                            @Override
                            public File call(String s) {
                                try {
                                    return Glide.with(MyApplication.getInstance())
                                                .asFile()
                                                .load(s)
                                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                                .submit()
                                                .get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File file) {
                                if(file != null){
                                    ImageMessage imgMessage = ImageMessage.obtain(Uri.fromFile(file), Uri.fromFile(file), false);
                                    Message      msg        = ImMessageUtils.obtain(targetId,conversationType,imgMessage);
                                    RongIM.getInstance().sendImageMessage(msg, "[图片]", "[图片]", new RongIMClient.SendImageMessageCallback() {
                                        @Override
                                        public void onAttached(Message message) {}

                                        @Override
                                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {}

                                        @Override
                                        public void onSuccess(Message message) {}

                                        @Override
                                        public void onProgress(Message message, int i) {}
                                    });
                                }
                            }
                        });

            RxTaskHelper.getInstance().addTask(this, sub);
            return;
        }


        if("11".equals(collectionBean.getType())){
            ConversationTextBean videoBean = collectionBean.getData();
            String content = videoBean.getContent();        //视频地址
            String temp = videoBean.getTitle();
            if(!TextUtils.isEmpty(temp) && temp.contains("?")){
                String cover = temp.substring(0, temp.indexOf("?"));
                String arr [] = temp.substring(temp.indexOf("?") + 1, temp.length()).split("\\*");
                int width = StringUtil.toInt(arr[0]);
                int height = StringUtil.toInt(arr[1]);
                int time = StringUtil.toInt(arr[2]);

                VideoMessage vMessage = new VideoMessage();
                vMessage.setUrl(content);
                vMessage.setCover(cover);
                vMessage.setDuration(time / 1000);
                vMessage.setWidth(width);
                vMessage.setHeight(height);

                RongIM.getInstance().sendMessage(ImMessageUtils.obtain(targetId, conversationType, vMessage), "[视频]", "[视频]", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        mView.showError("发送收藏失败 :  " + errorCode.getMessage());
                    }
                });
            }
            return;
        }


        ImMessageUtils.shareCollect(collectionBean, targetId,bean.mType, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                EventBusUtil.post(new EventSendRPBean());
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtil.i("send msg error : " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }


    //转发消息
    @Override
    public void relayMessage(MessageContent messageContent, final String targetId, final Conversation.ConversationType conversationType,final String liuyan) {
        String pushContent = "";
        pushContent = ImMessageUtils.getMessagePushContent(messageContent);
        Message message = ImMessageUtils.obtain(targetId, conversationType, messageContent);
        RongIM.getInstance().sendMessage(message, pushContent, pushContent, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(MyApplication.getInstance(),"转发成功");
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage(liuyan,targetId,conversationType);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {}
        });
    }

    @Override
    public void relayMessages(final List<Message> messages, final String targetId, final Conversation.ConversationType conversationType, String liuyan) {
        if(messages == null){
            return;
        }

        if(!TextUtils.isEmpty(liuyan)){
            TextMessage textMessage = TextMessage.obtain(liuyan);
            Message liuyanMessage = ImMessageUtils.obtain(targetId, conversationType, textMessage);
            messages.add(liuyanMessage);
        }

        final List<Message> result = new ArrayList<>();

        final UserInfo info = UserManager.getInstance().obtinUserInfo();
        Observable.from(messages)
                  .subscribeOn(Schedulers.io())
                  .map(new Func1<Message, Message>() {
                      @Override
                      public Message call(Message message) {
                          MessageContent messageContent = message.getContent();
                          messageContent.setUserInfo(info);

                          Message newMessage = ImMessageUtils.obtain(targetId, conversationType, message.getContent());
                          newMessage.setContent(messageContent);

                          try {
                              Thread.sleep(300);            //融云每秒只能发送5条消息
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          }

                          return newMessage;
                      }
                  })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Action1<Message>() {
                      @Override
                      public void call(Message message) {
                          String pushContent = ImMessageUtils.getMessagePushContent(message);
                          RongIM.getInstance().sendMessage(message, pushContent, pushContent, new IRongCallback.ISendMessageCallback() {
                              @Override
                              public void onAttached(Message message) {

                              }

                              @Override
                              public void onSuccess(Message message) {
                                  result.add(message);
                                  if(result.size() == messages.size()){
                                      ToastUtil.showShort(MyApplication.getInstance(), "已发送");
                                  }
                              }

                              @Override
                              public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                  LogUtil.i("sendMessage  error  :  "  + errorCode);
                              }
                          });
                      }
                  });
    }

    //多消息合并转发
    @Override
    public void mergeRelayMessages(final List<Message> messages, final String targetId, final Conversation.ConversationType conversationType, final String liuyan) {
        if(messages != null && messages.size() <= 0){
            return;
        }

        mView.showLoad();
        StringBuilder sb = new StringBuilder();
        for(Message message: messages){
            sb.append(message.getUId());
            sb.append(",");
        }
        if(sb.lastIndexOf(",") == sb.length() - 1){
            sb.deleteCharAt(sb.lastIndexOf(","));
        }

        String token = UserManager.getInstance().getToken();
        mMessageData.saveMergeMessage(token, sb.toString(), new ApiCallBack<MergeMessageBean>() {
            @Override
            public void onSuccess(MergeMessageBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if(!TextUtils.isEmpty(data.getId())){
                    sendMergeMessages(data.getId(), messages, targetId, conversationType, liuyan);
                }else{
                    mView.showMergeResult(false, MyApplication.getInstance().getResources().getString(R.string.message_relay_faild));
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showMergeResult(false, message);
            }
        });
    }

    private void sendMergeMessages(String id, List<Message> messages, final String targetId, final Conversation.ConversationType conversationType, final String liuyan){
        MergeHistoryMessage historyMessage = new MergeHistoryMessage();
        String title = "";

        Message firstMessage = messages.get(0);
        String sendId = firstMessage.getSenderUserId();

        if(firstMessage.getConversationType() == Conversation.ConversationType.GROUP){
            title = "群聊";
        }

        if(firstMessage.getConversationType() == Conversation.ConversationType.PRIVATE){
            if(firstMessage.getMessageDirection() == Message.MessageDirection.SEND){
                UserInfo reciveInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
                if(reciveInfo != null){
                    title = UserManager.getInstance().getUserName() + "与" + reciveInfo.getName();
                }else{
                    title = UserManager.getInstance().getUserName();
                    LogUtil.i("reciveInfo   ==== null");
                }

            }else{
                UserInfo sendInfo = RongUserInfoManager.getInstance().getUserInfo(sendId);
                title = sendInfo.getName() + "与" + UserManager.getInstance().getUserName();
            }
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            UserInfo info = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if(info != null){
                sb.append(info.getName())
                  .append("：")
                  .append(ImMessageUtils.getMessagePushContent(message));

                if(i != messages.size() - 1){
                    sb.append("\n");
                }
            }
        }
        historyMessage.setId(id);
        historyMessage.setTitle(title);
        historyMessage.setContent(sb.toString());

        String pushContent = ImMessageUtils.getMessagePushContent(historyMessage);
        Message newMessage = ImMessageUtils.obtain(targetId, conversationType, historyMessage);
        RongIM.getInstance().sendMessage(newMessage, pushContent, pushContent, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage(liuyan, targetId, conversationType);
                }
                mView.showMergeResult(true, "");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtil.i("sendMessage  error  :  "  + errorCode);
                mView.showMergeResult(false, MyApplication.getInstance().getResources().getString(R.string.message_relay_faild));
            }
        });
    }


    @Override
    public void collectMessage(Message message) {
        String                 bizType = "";
        String                 content = "";
        String                 token   = UserManager.getInstance().getToken();
        HashMap<String,String> map     = new HashMap<>();

        if( RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()) != null){
            map.put("userName", RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()).getName());
            map.put("userPic",RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()).getPortraitUri().toString());
        }
        map.put("token",token);

        if(message.getContent() instanceof TextMessage){
            bizType = "6";
            content = ((TextMessage)message.getContent()).getContent();
            map.put("content",content);
        }

        if(message.getContent() instanceof ImageMessage){
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            bizType = "7";
            content = imageMessage.getRemoteUri().toString();
            map.put("content",content);
            map.put("bizType",bizType);
            getBitMap(content,map);
            return;
        }

        if(message.getContent() instanceof VideoMessage){
            bizType = "11";
            VideoMessage videoMessage = (VideoMessage) message.getContent();
            content = videoMessage.getUrl();
            //收藏小视频的时候 封面图片title字段 要加宽高和时长
            //ios那边上传小视频封面图片是不带宽高的 android 带有所以要判断下
            String title = "";
            if(videoMessage.getCover().contains("?")){
                title = videoMessage.getCover() + "*" + videoMessage.getDuration();
            }else{
                title = videoMessage.getCover() + "?" + videoMessage.getWidth() + "*" + videoMessage.getHeight() + "*" + videoMessage.getDuration();
            }
            map.put("content",content);
            map.put("title",title);
        }

        if(message.getContent() instanceof ShareMessage){
            ShareMessage shareMessage = (ShareMessage) message.getContent();
            String infoType = shareMessage.getInfoType();

            //收藏分享
            if("1".equals(infoType) || "2".equals(infoType) || "3".equals(infoType)){
                bizType = infoType;
                map.put("bizId",shareMessage.getTypeId());
            }

            if("4".equals(shareMessage.getInfoType())){
                bizType = "8";
                map.put("bizId",shareMessage.getTypeId());
            }

            //收藏系列课
            if(CircleShareHandler.SHARE_SERIES.equals(shareMessage.getInfoType())){
                bizType = "9";
                map.put("bizId",shareMessage.getTypeId());
            }

        }

        map.put("bizType",bizType);
        savaCollect(map);
    }


    @Override
    public void getMemberTypeByChat(String chatId) {
        String token = UserManager.getInstance().getToken();
        mData.getMemberTypeByChat(token, chatId, new ApiCallBack<CircleBean>() {
            @Override
            public void onSuccess(CircleBean data) {
                if(mView == null)   return;
                mView.showMemberType(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                LogUtil.i("getMemberTypeByChat  获取身份失败" + message );
            }
        });
    }


    //禁言
    @Override
    public void shutup(int groupId, String userCode, final int days){
        String token = UserManager.getInstance().getToken();
        mData.shutup(token,groupId,userCode,days,new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                String msg = "";
                if(days == 0){
                    msg = "取消禁言成功";
                }else{
                    msg = "禁言成功";
                }
                mView.showShutupResult(true,msg,"","");
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showShutupResult(false,"",errorCode,message);
            }
        });
    }


    //踢出圈子
    @Override
    public void removeMember(int groupId, String userCode) {
        String token = UserManager.getInstance().getToken();
        mInfoData.removeMember(token, groupId, userCode, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null)   return;
                mView.showRemoveResult(true,"","");
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showRemoveResult(false,errorCode,message);
            }
        });
    }


    //插入本地占位消息
    private void insertLocalMessage(LocalMedia media, String path, int[] frameSize){
        VideoMessage vMessage = new VideoMessage();
        vMessage.setLocalCover(path);
        vMessage.setUrl(media.getPath());
        vMessage.setDuration(media.getDuration() / 1000);
        vMessage.setWidth(frameSize[0]);
        vMessage.setHeight(frameSize[1]);
        RongIM.getInstance().insertOutgoingMessage(mType, mTargetId, Message.SentStatus.SENT,
                vMessage, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        uploadMessagQueen.add(message);
                        uploadingVideoMessage(message);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        mView.showError("发送视频失败:  " + errorCode.getMessage());
                    }
                });
    }


    //上传视频消息里面的视频到服务器
    private void uploadingVideoMessage(Message message){
        if(message == null ){
            mView.showError("发送视频失败");
            return;
        }

        VideoMessage vMessage = (VideoMessage) message.getContent();
        if(vMessage == null){
            mView.showError("发送视频失败");
            return;
        }

        String path = vMessage.getUrl();
        if(TextUtils.isEmpty(path)){
            mView.showError("发送视频失败, 视频地址为空");
            return;
        }

        File file = new File(path);
            LoadHelper.UpyunUploadVideo(file, new MessageUploadCallback(message) {
                @Override
                public void onUploadSuccess(Object data, Message message) {
                    if (data instanceof UploadResult) {
                        List<String> datas        = ((UploadResult) data).getUrls();
                        VideoMessage videoMessage = (VideoMessage) message.getContent();
                        FileUtil.deleteFile(videoMessage.getCover());       //删除临时文件
                        videoMessage.setUrl(datas.get(0));
                        videoMessage.setCover(datas.get(1));
                        videoMessage.setLocalCover("");

                        RongIM.getInstance().sendMessage(message, "[视频]", "[视频]", new IRongCallback.ISendMessageCallback() {
                            @Override
                            public void onAttached(Message message) {}

                            @Override
                            public void onSuccess(Message message) {
                                uploadMessagQueen.remove(message);
                            }

                            @Override
                            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                mView.showError("发送视频失败 :  " + errorCode.getMessage());
                                message.setSentStatus(Message.SentStatus.FAILED);
                            }
                        });
                    }
                }

                @Override
                public void onUploadFailed(String errorCode, String errorMsg, Message message) {
                    mView.showError("上传视频失败 :  " + errorMsg);
                    Event.OnReceiveMessageProgressEvent e = new Event.OnReceiveMessageProgressEvent();
                    e.setMessage(message);
                    e.setProgress(-2);
                    RongContext.getInstance().getEventBus().post(e);
                }
            },
            new MessageProgressListener(message) {
                @Override
                public void onUIProgress(long currentBytes, long contentLength, boolean done, int position, Message message) {
                    if(position == 0){
                        VideoMessage videoMsg = (VideoMessage) message.getContent();
                        if(videoMsg != null){
                            long progress = (100 * currentBytes) / contentLength;
                            if(!ClickUtil.isFastClick()){
                                Event.OnReceiveMessageProgressEvent e = new Event.OnReceiveMessageProgressEvent();
                                e.setMessage(message);
                                e.setProgress((int) progress);
                                RongContext.getInstance().getEventBus().post(e);
                            }
                        }
                    }
                }
            });

        /*Subscription sub =
                LoadHelper.uploadVideo(file, new MessageApiCallBack<List<String>>(message) {
                            @Override
                            public void onSuccess(@Nullable Object data, @NotNull Message message) {
                                if(data instanceof List){
                                    List<String> datas = (List<String>) data;
                                    VideoMessage videoMessage = (VideoMessage) message.getContent();
                                    FileUtil.deleteFile(videoMessage.getCover());       //删除临时文件
                                    videoMessage.setUrl(datas.get(0));
                                    videoMessage.setCover(datas.get(1));
                                    videoMessage.setLocalCover("");

                                    RongIM.getInstance().sendMessage(message, "[视频]", "[视频]", new IRongCallback.ISendMessageCallback() {
                                        @Override
                                        public void onAttached(Message message) {}

                                        @Override
                                        public void onSuccess(Message message) {
                                            uploadMessagQueen.remove(message);
                                        }

                                        @Override
                                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                            mView.showError("发送视频失败 :  " + errorCode.getMessage());
                                            message.setSentStatus(Message.SentStatus.FAILED);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(@Nullable String errorCode, @Nullable String errorMsg, @NotNull Message message) {
                                mView.showError("上传视频失败 :  " + errorMsg);
                                Event.OnReceiveMessageProgressEvent e = new Event.OnReceiveMessageProgressEvent();
                                e.setMessage(message);
                                e.setProgress(-2);
                                RongContext.getInstance().getEventBus().post(e);
                            }
                        },
                        new MessageProgressListener(message){
                            @Override
                            public void onUIProgress(long currentBytes, long contentLength, boolean done, int position, Message message) {
                                if(position == 1){
                                    VideoMessage videoMsg = (VideoMessage) message.getContent();
                                    if(videoMsg != null){
                                        long progress = (100 * currentBytes) / contentLength;
                                        if(!ClickUtil.isFastClick()){
                                            Event.OnReceiveMessageProgressEvent e = new Event.OnReceiveMessageProgressEvent();
                                            e.setMessage(message);
                                            e.setProgress((int) progress);
                                            RongContext.getInstance().getEventBus().post(e);
                                        }
                                    }
                                }
                            }
                        });

        RxTaskHelper.getInstance().addTask(this, sub);*/
    }


    //保存收藏
    private void savaCollect(HashMap<String,String> map){
        mDealData.saveCollect(map, new ApiCallBack<Object>() {

            @Override
            public void onSuccess(Object data) {
                ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"收藏成功");
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"收藏失败");
            }
        });
    }


    private void getBitMap(final String url, final HashMap<String,String> map) {
        Observable.just(url)
                  .map(new Func1<String, Bitmap>() {
                      @Override
                      public Bitmap call(String s) {
                          try {
                              return Glide.with(MyApplication.getInstance())
                                           .asBitmap()
                                           .load(s)
                                           .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                           .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                           .get();
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                          return null;
                      }
                  })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(new Observer<Bitmap>() {
                      @Override
                      public void onCompleted() {}

                      @Override
                      public void onError(Throwable e) {
                          e.printStackTrace();
                          ToastUtil.showShort(MyApplication.getInstance(),"收藏失败");
                      }

                      @Override
                      public void onNext(Bitmap bitmap) {
                          if(bitmap != null){
                              map.put("title",bitmap.getWidth() + "*" + bitmap.getHeight());           //图片宽高，iOS需要
                              savaCollect(map);
                          }
                      }
                  });
    }


    //移除本地未上传完成的视频消息
    private void removeAllLocalVideoMessage(){
        int [] messageIds = new int [uploadMessagQueen.size()];
        for (int i = 0; i < uploadMessagQueen.size(); i++){
                messageIds[i] = uploadMessagQueen.get(i).getMessageId();
        }

        if(messageIds.length > 0){
            RongIM.getInstance().deleteMessages(messageIds, new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    LogUtil.i("移除消息成功");
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.i("移除消息失败:  " + errorCode);
                }
            });
        }
    }


    @Override
    public void start() {

    }


    @Subscribe
    public void onEvent(EventUploadVideoBean bean){
        if(bean.getMessage() != null){
            uploadMessagQueen.remove(bean.getMessage());
            uploadingVideoMessage(bean.getMessage());
        }
    }


    @Override
    public void destroy() {
        mData.destroy();
        mDealData.destroy();
        mInfoData.destroy();
        mMessageData.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
        removeAllLocalVideoMessage();
        EventBusUtil.unregister(this);
    }


    @Override
    public List<Message> getUploadMessagQueen() {
        return uploadMessagQueen;
    }
}