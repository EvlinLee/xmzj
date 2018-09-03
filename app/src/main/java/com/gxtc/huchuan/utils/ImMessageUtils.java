package com.gxtc.huchuan.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.bean.CategoryBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CollectMallDetailBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.ConversationTextBean;
import com.gxtc.huchuan.bean.CustomCollectBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.LiveInsertBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.provide.BlacklistMessage;
import com.gxtc.huchuan.im.provide.SilentMessage;
import com.gxtc.huchuan.ui.im.merge.MergeHistoryMessage;
import com.gxtc.huchuan.ui.im.postcard.PTMessage;
import com.gxtc.huchuan.ui.im.share.ShareMessage;
import com.gxtc.huchuan.ui.im.video.VideoMessage;
import com.gxtc.huchuan.ui.live.conversation.LiveInsertChooseActivity;
import com.melink.bqmmplugin.rc.EmojiMessage;
import com.melink.bqmmplugin.rc.GifMessage;

import java.util.Random;

import im.collect.CollectMessage;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.Emoji;
import io.rong.imkit.utils.StringUtils;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/6.
 */

public class ImMessageUtils {

    public static Message obtain(String targetId, Conversation.ConversationType type, MessageContent msgContent){
        String userCode = UserManager.getInstance().getUserCode();
        String name = UserManager.getInstance().getUserName();
        String url = UserManager.getInstance().getHeadPic();
        UserInfo userInfo = new UserInfo(userCode,name, Uri.parse(url));
        msgContent.setUserInfo(userInfo);

        Message msg = Message.obtain(targetId,type,msgContent);
        return msg;
    }

    /**
     * 聊天室相关的发送方法
     */

    //分享圈内消息到聊天室
    public static void shareMessage(String targetId,Conversation.ConversationType type ,String typeId, String typeTitle, String typeCover,String infoType,IRongCallback.ISendMessageCallback callback){
        ShareMessage shareMessage = ShareMessage.obtain(typeId,typeTitle,typeCover,infoType);
        Message      message      = ImMessageUtils.obtain(targetId,type,shareMessage);
        RongIM.getInstance().sendMessage(message,typeTitle,typeTitle,callback);
    }

    //发送名片
    public static void sentPost(String targetId,Conversation.ConversationType type ,String typeId, String typeTitle, String typeCover,IRongCallback.ISendMessageCallback callback){
        PTMessage shareMessage = PTMessage.obtain(typeId,typeTitle,typeCover);
        Message      message      = ImMessageUtils.obtain(targetId,type,shareMessage);
        RongIM.getInstance().sendMessage(message,typeTitle,typeTitle,callback);
    }

    //分享收藏
    public static void shareCollect(CollectionBean collectionBean, String targetId, Conversation.ConversationType type, IRongCallback.ISendMessageCallback callback){
        String id = "";
        String t = collectionBean.getType();
        String cover = "";
        String content = "";
        String url = "";

        //分享新闻
        if(collectionBean.getData() instanceof NewsBean){
            NewsBean newsBean = collectionBean.getData();
            cover = newsBean.getCover();
            content = newsBean.getTitle();
            id = newsBean.getId();
        }

        //分享课堂
        if(collectionBean.getData() instanceof ChatInfosBean){
            ChatInfosBean chatInfosBean = collectionBean.getData();
            cover = chatInfosBean.getFacePic();
            content = chatInfosBean.getSubtitle();
            id = chatInfosBean.getId();
        }

        //分享交易
        if(collectionBean.getData() instanceof DealListBean){
            DealListBean listBean = collectionBean.getData();
            cover = listBean.getPicUrl();
            content = listBean.getTitle();
            id = listBean.getId();
        }

        //分享动态
        if(collectionBean.getData() instanceof CircleHomeBean){
            CircleHomeBean homeBean = collectionBean.getData();
            cover = homeBean.getUserPic();
            content = homeBean.getContent();
            id = homeBean.getId() + "";
            if(TextUtils.isEmpty(content)){
                if(TextUtils.isEmpty(homeBean.getTypeTitle())){
                    content = "分享收藏动态";
                }else{
                    content = homeBean.getTypeTitle();
                }
            }
        }

        //分享加入圈子
        if(collectionBean.getData() instanceof CircleBean){
            CircleBean circleBean = collectionBean.getData();
            cover = circleBean.getCover();
            id = circleBean.getId() + "";
            content = circleBean.getGroupName();
            t = "6";
        }

        if(collectionBean.getData() instanceof CustomCollectBean){
            CustomCollectBean collectBean = collectionBean.getData();
            content = collectBean.getTitle();
            url = collectBean.getUrl();

            if(TextUtils.isEmpty(content)){
                content = collectBean.getStrContent();
            }
        }

        //系列课
        if(collectionBean.getData() instanceof SeriesPageBean){
            SeriesPageBean seriesPageBean = collectionBean.getData();
            id = seriesPageBean.getId();
            content = seriesPageBean.getSeriesname();
            cover = seriesPageBean.getHeadpic();
            t = "7";
        }

        //课程
        if(collectionBean.getData() instanceof CollectMallDetailBean){
            CollectMallDetailBean mallBean = collectionBean.getData();
            id = mallBean.getStoreId() + "";
            content = mallBean.getStoreName();
            cover = mallBean.getFacePic();
            t = "8";
        }

        //直播间
        if(collectionBean.getData() instanceof LiveRoomBean){
            LiveRoomBean mLiveRoomBean = collectionBean.getData();
            id = mLiveRoomBean.getId();
            content = mLiveRoomBean.getName();
            cover = mLiveRoomBean.getBakpic();
            t = "9";
        }

        CollectMessage msg     = CollectMessage.obtain(id, t, cover, content, url);
        Message        message = ImMessageUtils.obtain(targetId,type,msg);
        RongIM.getInstance().sendMessage(message,content,content,callback);
    }

    public static String getMessagePushContent(Message message){
        if(message == null){
            return "";
        }
        return getMessagePushContent(message.getContent());
    }

    public static String getMessagePushContent(MessageContent content){
        if(content == null){
            return "";
        }

        if(content instanceof TextMessage){
            TextMessage textMessage = (TextMessage) content;
            return textMessage.getContent();
        }

        if(content instanceof ImageMessage){
            return "[图片]";
        }

        if(content instanceof VoiceMessage){
            return "[语音]";
        }

        if(content instanceof VideoMessage){
            return "[视频]";
        }

        if(content instanceof GifMessage){
            return "[表情]";
        }

        if(content instanceof EmojiMessage){
            return "[表情]";
        }

        if(content instanceof MergeHistoryMessage){
            return "[聊天记录]";
        }

        if(content instanceof ShareMessage){
            ShareMessage shareMessage = (ShareMessage) content;
            return shareMessage.getTypeTitle();
        }

        if(content instanceof CollectMessage){
            CollectMessage shareMessage = (CollectMessage) content;
            return shareMessage.getContent();
        }

        return "";
    }


    /**
     * 课堂相关
     */

    public static void silentMessage(String isSilent,String targetUserCode, String targetId, IRongCallback.ISendMessageCallback callback){
        String userCode = UserManager.getInstance().getUserCode();
        String name = UserManager.getInstance().getUserName();
        String head = UserManager.getInstance().getHeadPic();

        SilentMessage silentMessage = SilentMessage.obtain(isSilent,targetUserCode);
        UserInfo userInfo = RongImHelper.createUserInfo(userCode,name,head);
        Extra extra = Extra.obtan("4", false, targetId + System.currentTimeMillis(), "0");
        extra.setSentTime(System.currentTimeMillis());
        silentMessage.setUserInfo(userInfo);
        silentMessage.setExtra(extra.encode());

        Message message = Message.obtain(targetId, Conversation.ConversationType.CHATROOM, silentMessage);
        message.setExtra(extra.encode());

        RongIM.getInstance().sendMessage(message,null,null,callback);
    }

    public static void blacklistMessage(String targetUserCode, String targetId, IRongCallback.ISendMessageCallback callback){
        String userCode = UserManager.getInstance().getUserCode();
        String name = UserManager.getInstance().getUserName();
        String head = UserManager.getInstance().getHeadPic();

        BlacklistMessage blackMessage = BlacklistMessage.obtain(targetUserCode);
        UserInfo         userInfo      = RongImHelper.createUserInfo(userCode,name,head);
        Extra            extra         = Extra.obtan("4", false, targetId + System.currentTimeMillis(), "0");
        extra.setSentTime(System.currentTimeMillis());
        blackMessage.setUserInfo(userInfo);
        blackMessage.setExtra(extra.encode());

        Message message = Message.obtain(targetId, Conversation.ConversationType.CHATROOM, blackMessage);
        message.setExtra(extra.encode());

        RongIM.getInstance().sendMessage(message,null,null,callback);
    }


    //课堂内分享内容       sendType： 1、嘉宾  3、主持人
    public static void sendShareMessage(String sendType , String classId, LiveInsertBean bean, IRongCallback.ISendMessageCallback callback){
        String userCode = UserManager.getInstance().getUserCode();
        String userName = UserManager.getInstance().getUserName();
        Uri headPic = Uri.parse(UserManager.getInstance().getHeadPic());
        UserInfo mUserInfo = new UserInfo(userCode, userName, headPic);

        String id = "";
        String title = "";
        String cover = "";

        switch (StringUtil.toInt(bean.getInfoType())){
            case LiveInsertChooseActivity.TYPE_DEAL:
                DealListBean listBean = bean.getDealBean();
                id = listBean.getId();
                title = listBean.getTitle();
                cover = listBean.getPicUrl();
                break;

            case LiveInsertChooseActivity.TYPE_MALL:
                CategoryBean mallBean = bean.getMallBean();
                id = mallBean.getMerchandiseId();
                title = mallBean.getName();
                cover = mallBean.getFacePic();
                break;

            default:
                id = bean.getId() + "";
                title = bean.getTitle();
                cover = bean.getCover();
                break;
        }

        TextMessage myTextMessage     = TextMessage.obtain("此版本暂不支持此消息，请更新app版本");
        long        currentTimeMillis = System.currentTimeMillis();
        long        msgId             = currentTimeMillis + (new Random().nextInt(99999 - 10000) + 10000);
        final Extra extra             = Extra.obtan(sendType, false, classId + msgId, null, "1", id, bean.getInfoType(), title, cover);
        extra.setSentTime(currentTimeMillis);
        myTextMessage.setUserInfo(mUserInfo);

        myTextMessage.setExtra(extra.encode());
        Message message = Message.obtain(classId, Conversation.ConversationType.CHATROOM, myTextMessage);
        message.setExtra(extra.encode());
        RongIM.getInstance().sendMessage(message, null, null, callback);
    }

}
