package com.gxtc.huchuan.im;

import android.text.TextUtils;

import com.gxtc.huchuan.im.bean.RemoteMessageBean;
import com.gxtc.huchuan.im.hadler.MyVoiceMessageHandler;
import com.gxtc.huchuan.im.provide.CountDownMessage;
import com.gxtc.huchuan.im.provide.PPTMessage;
import com.gxtc.huchuan.im.provide.RedPacketMessage;
import com.gxtc.huchuan.im.provide.RemoveMessage;
import com.gxtc.huchuan.im.provide.SilentMessage;
import com.gxtc.huchuan.im.provide.VoiceMessageAdapter;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by Gubr on 2017/4/8.
 *
 */

public class MessageFactory {
    private static final String TAG = "MessageFactory";

    public static List<Message> create(List<RemoteMessageBean> beans) {
        //GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().insertOrReplaceInTx(beans);
        return createNotInsertDao(beans);
    }


    public static List<Message> createNotInsertDao(List<RemoteMessageBean> beans) {
        ArrayList<Message> messages = new ArrayList<>();
        for (RemoteMessageBean bean : beans) {
            messages.add(create(bean));
        }
        return messages;
    }

    public static Extra getExtra(String extra){
        return new Extra(extra);
    }

    public static Extra getExtrabyMessage(Message message){
        return getExtra(getExtraStr(message));
    }

    public static String getExtraStr(Message message){
        if(message == null) return "";
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
        }
        return null;
    }


    public static Message create(RemoteMessageBean map) {
        MessageContent content = null;
        String         extra   = null;
        long dataTime = 0;
        if (map != null) {
            String objectName = map.getObjectName();
            switch (objectName) {
                case "RC:VcMsg":
                    VoiceMessage voiceMessage = new VoiceMessageAdapter(map.getContent().getBytes()).getVoicMessage();
                    MyVoiceMessageHandler.decodeMessage(voiceMessage);
                    content = voiceMessage;
                    break;

                case "RC:TxtMsg":
                    TextMessage textMessage = new TextMessage(map.getContent().getBytes());
                    extra = textMessage.getExtra();
                    content = textMessage;
                    try {
                        dataTime = Long.valueOf(map.getDataTime());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case "RC:ImgMsg":
                    ImageMessage tempContent = new ImageMessage(map.getContent().getBytes());
                    tempContent.setLocalUri(tempContent.getRemoteUri());
                    tempContent.setThumUri(tempContent.getThumUri());
                    extra = tempContent.getExtra();
                    content = tempContent;
                    break;

                case "XM:CdMsg":
                    content = new CountDownMessage(map.getContent().getBytes());
                    break;

                case "XM:RpMsg":
                    RedPacketMessage redPacketMessage = new RedPacketMessage(map.getContent().getBytes());
                    extra = redPacketMessage.getExtra();
                    content = redPacketMessage;
                    break;

            }
        }
        Message message = Message.obtain(map.getTargetId() + "", Conversation.ConversationType.CHATROOM, content);
        message.setExtra(extra);
        message.setObjectName(map.getObjectName());
        message.setMessageId((int) map.getId());
        if(dataTime != 0){
            message.setSentTime(dataTime);
        }
        return message;
    }

    //安卓不知道为什么拿不到融云的发送时间 ,只能通过msgId去截取了
    public static long getMessageTime(Message message){
        if(message == null) return 0;
        String targetId = message.getTargetId();
        Extra extra = null;
        String objectName = message.getObjectName();
        switch (objectName) {
            case "RC:VcMsg":
                VoiceMessage voiceMessage = (VoiceMessage) message.getContent();
                extra = new Extra(voiceMessage.getExtra());
                break;

            case "RC:TxtMsg":
                TextMessage textMessage = (TextMessage) message.getContent();
                extra = new Extra(textMessage.getExtra());
                break;

            case "RC:ImgMsg":
                ImageMessage imgMessage = (ImageMessage) message.getContent();
                extra = new Extra(imgMessage.getExtra());
                break;

            case "XM:RpMsg":
                RedPacketMessage rpMsg = (RedPacketMessage) message.getContent();
                extra = new Extra(rpMsg.getExtra());
                break;

        }

        if(extra == null){
            return 0;
        }

        String msgId = extra.getMsgId();
        if(!TextUtils.isEmpty(targetId) && !TextUtils.isEmpty(msgId)){
            String temp = msgId.substring(targetId.length(),msgId.length());
            if(!TextUtils.isEmpty(temp)){
                return Long.valueOf(temp);
            }
        }
        return 0;
    }

}
