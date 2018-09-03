package com.gxtc.huchuan.ui.live.conversation;

import android.content.Context;
import android.net.Uri;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;

import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/2/20.
 */

public interface LiveConversationContract {
    interface View extends BaseUserView<Presenter> {
        void showLoadDialog(boolean isShow);

        void showDiscussIntro(List<Message> messages);

        void showDiscussLabel(String count);

        void hindDiscussLabel();

        void loadConversation(List<Message> messages);

        void addMessage(Message message);

        void showPPT(List<Uri> datas);

        void upRefreshFinish();

        void downRefreshFinish();

        void notifyChangeData();

        void showAuthor();

        void showRedpacket();

        void showKickOutRoom();

        Context getContext();

        boolean getIsAsk();

        void sendMessage();

        void addDiscussIntro(Message message);

        void setSilentModel(boolean b);

        void changePPT(List<Uri> datas);

        void removeMessage(Message message);

        void showCollectResult(boolean isSuccecc, String msg);

        void showFollowResult(boolean isSuccess, String msg);

        void showRemoveDiscussIntro(Message message);
    }

    interface Presenter extends BasePresenter {

        void remoteTopicHistory();

        void getDownRemoteHistory();

        void getUpRemoteHistory();

        void showDiscussIntro();

        void showDiscussLabel();

        void showRedpacket();

        void setCanLoadRemoteMessage(boolean flag);

        /**
         * 显示图片选择窗口
         */
        void showImageSelectView();

        void sendMessage(String string);

        void sendImageMessage(List<String> imagePath);



        /**
         * 发送图片信息
         *
         * @param imagePath
         */
        void sendImageMessage(String imagePath);


        /**
         * 发送语音信息
         */
        void sendVoiceMessage();


        void joinChatRoom(String charRoomgId);

        void sendAudienceMessage(String msg, boolean b);

        void sendRedPacketMessage(OrdersRequestBean requestBean);

        void sendPPTImageMessage(String clickImage);

        void removeMessage(Message message);

        void getChatinfosStatus();

        void sendPPTChnageMessage();

        void shareMessage(String targetId, Conversation.ConversationType type, String title, String img, String id,String liuyan);

        void sendVisitingCard(EventSelectFriendForPostCardBean bean);

        void collect(String id);

        void follow();
    }

    interface Source extends BaseSource {
        void loadConversation();

        void startConversation();


        void remoteHistory();


    }

}
