package com.gxtc.huchuan.im.manager;

import android.content.Intent;
import android.net.Uri;

import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by Gubr on 2017/3/8.
 * <p>
 * <p>
 * 用来保存当前会话的  信息  到时候 自动播放 要从这里拿到 uri
 */

public class MessageManager {
    List<VoiceTimeBean> mVoiceMessages = new ArrayList<>();
    List<Message> mMessages = new ArrayList<>();

    private int newCount = 0;

    public ArrayList<Uri> getImageList(){
        ArrayList<Uri> iamgeUri = new ArrayList<>();
        for (Message message : mMessages) {
            if ("RC:ImgMsg".equals(message.getObjectName())) {
                Uri remoteUri = ((ImageMessage) message.getContent()).getRemoteUri();
                iamgeUri.add(remoteUri);
            }
        }
        return iamgeUri;
    }


    private MessageManager() {
    }


    public List<Message> getMessages() {
        return mMessages;
    }


    /**
     * 这个方法只能用来添加  倒及时 跟邀请的信息  不可以添加语音信息
     *
     * @param message
     * @param position
     */
    public void addMessage(Message message, int position) {
        if (message != null && mMessages.size() >= position) {
            mMessages.add(position, message);
            if (message.getObjectName().equals("RC:VcMsg")) {
                addVoicMessage((VoiceMessage) message.getContent());
            }
        }
    }

    public void addMessage(Message message) {
        if (message != null) {
            mMessages.add(message);
            if (message.getObjectName().equals("RC:VcMsg")) {
                addVoicMessage((VoiceMessage) message.getContent());
            }
        }
    }

    /**
     * 这里有一种情况，可能在外面点击听课 这时候只会拉去语音消息 文字消息不会拉去
     * 所以进入课堂的时候获取消息的时候 要去重复消息
     * @param messages
     */
    public void addMessages(List<Message> messages) {
        if (messages != null && messages.size() > 0) {
            this.mMessages.clear();

            for (Message message : messages) {
                this.mMessages.add(message);

                if (message.getObjectName().equals("RC:VcMsg") ) {
                    addVoicMessage((VoiceMessage) message.getContent());
                }
            }
        }
    }


    /**
     * 上拉加载消息
     * @param messages
     */
    public void addMessagesToHead(List<Message> messages){
        newCount = messages.size();
        List<VoiceTimeBean> messages1 = new ArrayList<>();
        if (messages != null && messages.size()>0){
            this.mMessages.addAll(0,messages);
        }
        for (Message message : messages) {
            if (message.getObjectName().equals("RC:VcMsg")) {
                messages1.add(new VoiceTimeBean((VoiceMessage) message.getContent()));
            }
        }
        if (messages1.size()>0){
            mVoiceMessages.addAll( 0,messages1);
        }
    }

    private void addVoicMessage(VoiceMessage voiceMessage) {
        for (int i = 0; i < mVoiceMessages.size(); i++) {
            VoiceMessage vMsg = mVoiceMessages.get(i).getVoiceMessage();
            if(vMsg.getUri().toString().equals(voiceMessage.getUri().toString())){
                return;
            }
        }
        mVoiceMessages.add(new VoiceTimeBean(voiceMessage));
    }


    public VoiceTimeBean getFirstVoiceMessage() {
        return mVoiceMessages.size() > 0 ? mVoiceMessages.get(0) : null;
    }


    public Message getLastMessage(){
        if (mMessages.size()>0){
            return mMessages.get(mMessages.size() - 1);
        }
        return null;
    }

    public Message getFirstMessage(){
        if (mMessages.size()>0){
            return mMessages.get(0);
        }
        return null;
    }

    /**
     * 获取 VoiceTimeBean
     *
     * @param uri
     * @return
     */
    public VoiceTimeBean getVoiceTimeBean(Uri uri) {
        if (mVoiceMessages != null) {
            for (int i = 0; i < mVoiceMessages.size(); i++) {
                if (mVoiceMessages.get(i).getUri().equals(uri)) {
                    return mVoiceMessages.get(i);
                }
            }
        }
        return null;
    }

    /**
     * 获取下一条 要播放的
     *
     * @param uri
     * @return
     */
    public VoiceTimeBean getNextVoicMessage(Uri uri) {
        if (mVoiceMessages != null) {
            for (int i = 0; i < mVoiceMessages.size(); i++) {
                if (mVoiceMessages.get(i).getUri().equals(uri)) {
                    if (i < mVoiceMessages.size() - 1) {
                        return mVoiceMessages.get(i + 1);
                    }
                }
            }
        }

        //如果走到这里就是没有更多语音消息 这时候发个广播
        Intent intent = new Intent(AudioPlayManager.ACTION_NOT_NEXT);
        MyApplication.getInstance().sendBroadcast(intent);
        return null;
    }


    /**
     * 这个是根据Message 拿 到对应的VoiceMessage
     *
     * @param position
     * @return
     */
    public VoiceMessage getVoiceMessage(int position) {
        if (mMessages == null && mMessages.size() > position) {
            if (mMessages.get(position).getObjectName().equals("RC:VcMsg")) {
                return (VoiceMessage) mMessages.get(position).getContent();
            } else {
                throw new RuntimeException("position 不匹配");
            }
        }
        return null;
    }


    public void removeMessage(Message message) {
        if (message != null) {
            mMessages.remove(message);
            if (message.getObjectName().equals("RC:VcMsg")) {
                removeVoiceMessage((VoiceMessage) message.getContent());
            }
        }
    }

    private void removeVoiceMessage(VoiceMessage voiceMessage) {
        for (int i = 0; i < mVoiceMessages.size(); i++) {
            if (mVoiceMessages.get(i).getVoiceMessage().equals(voiceMessage)) {
                mVoiceMessages.remove(i);
                if (AudioPlayManager.getInstance().isUriPlaying(voiceMessage.getUri())){
                    AudioPlayManager.getInstance().stopPlay();
                }
                return;
            }
        }
    }

    public void reinit() {
        if (mMessages != null) {
            mMessages.clear();
        }
        if (mVoiceMessages != null) {
            mVoiceMessages.clear();
        }
    }


    public void release(){
        if(!AudioPlayManager.getInstance().isAudioPlaying()){
            reinit();
        }
    }


    public static MessageManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public Message removeMessage(String content) {
        for (int i = 0; i < mMessages.size(); i++) {
            Extra extrabyMessage = MessageFactory.getExtrabyMessage(mMessages.get(i));
            LogUtil.i(mMessages.get(i).getObjectName() + "   i ：  " + i);
            if (extrabyMessage.getMsgId().equals(content)) {
                Message message = mMessages.get(i);
                removeMessage(message);
                return message;
            }
        }
        return null;
    }

    public int getNewCount() {
        int temp = newCount;
        newCount = 0;
        return temp;
    }

    static class SingletonHolder {
        static MessageManager sInstance = new MessageManager();
    }
}
