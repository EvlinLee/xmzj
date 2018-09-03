package com.gxtc.huchuan.im.ui;

import android.content.Context;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/17.
 */

public interface ConversationContract {

    interface View extends BaseUiView<ConversationContract.Presenter>{
        void showCircleInfo(CircleBean bean);

        void showRedPackInfo(RedPacketBean bean);

        void showMemberType(CircleBean bean);

        void showShutupResult(boolean isSuccessed,String msg ,String errorCode, String error);

        void showRemoveResult(boolean isSuccessed, String errorCode, String error);

        void showMergeResult(boolean isSuccessed, String msg);

        void showOutClear(String code, String msg);
    }

    interface Presenter extends BasePresenter{
        void getCircleInfo(String id);

        void getRedPackInfo(Message message);

        void shareMessage(Message currMessage, String targetId, Conversation.ConversationType type);

        void sendRongIm(EventSelectFriendForPostCardBean bean);

        void sendVideoMessage(Context context, LocalMedia media);

        void sendImageMessage(Context context, String path, String targetId, Conversation.ConversationType conversationType);

        void sendCollectionMessage(EventShareMessage bean, String targetId, Conversation.ConversationType conversationType);

        void relayMessage(MessageContent messageContent,String targetId, Conversation.ConversationType conversationType,String liuyan);

        void relayMessages(List<Message> messages, String targetId, Conversation.ConversationType conversationType,String liuyan);

        void mergeRelayMessages(List<Message> messages, String targetId, Conversation.ConversationType conversationType, String liuyan);

        void collectMessage(Message message);

        void getMemberTypeByChat(String chatId);

        void shutup(int groupId, String userCode, int days);

        void removeMember(int groupId, String userCode);

        List<Message> getUploadMessagQueen();
    }

}
